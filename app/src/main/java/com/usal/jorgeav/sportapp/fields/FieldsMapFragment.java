package com.usal.jorgeav.sportapp.fields;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.usal.jorgeav.sportapp.R;
import com.usal.jorgeav.sportapp.adapters.MapFieldMarkerInfoAdapter;
import com.usal.jorgeav.sportapp.data.Field;
import com.usal.jorgeav.sportapp.fields.fielddetail.DetailFieldFragment;
import com.usal.jorgeav.sportapp.mainactivities.ActivityContracts;
import com.usal.jorgeav.sportapp.mainactivities.FieldsActivity;
import com.usal.jorgeav.sportapp.utils.Utiles;
import com.usal.jorgeav.sportapp.utils.UtilesContentProvider;
import com.usal.jorgeav.sportapp.utils.UtilesPreferences;

import java.util.ArrayList;

/**
 * Fragmento utilizado para mostrar la colección de instalaciones de la ciudad del usuario actual.
 * <p>
 * Este Fragmento hereda de SupportMapFragment para mostrar un mapa en el que emplazar cada
 * una de las instalaciones. Se encarga de inicializar el mapa de la interfaz. También permite
 * viajar al Fragmento en el que se muestran los detalles de la instalación y al Fragmento utilizado
 * para crear una instalación nueva (incluso desde el inicio).
 * <p>
 * Para mostrar las instalaciones en el mapa, almacena una colección de marcas que se colocan sobre
 * él gracias a las coordenadas que pertenecen a cada instalación. Al pulsar sobre las marcas,
 * muestran algunos detalles de la instalación, a modo de celda de lista, y si se pulsa sobre esos
 * detalles muestra la vista de detalles de la instalación.
 * <p>
 * Cuando se va a crear una instalación, primero es necesario cargar todas las instalaciones que
 * ya existen en la base de datos. Por ello, en ese proceso de creación, primero se instancia
 * este Fragmento y, al finalizar la consulta de las instalaciones, se muestra un mapa con las
 * instalaciones encontradas en la base de datos si este Fragmento ha sido creado para ese fin. Esto
 * se indica en la instanciación de este Fragmento, mediante una etiqueta almacenada como argumento.
 * <p>
 * Como no hereda de {@link com.usal.jorgeav.sportapp.BaseFragment}, esta clase necesita implementar
 * algunos métodos de esa clase que serán usados por el Presentador. Además, implementa la interfaz
 * {@link FieldsContract.View} para la comunicación con esta clase, la interfaz
 * {@link OnMapReadyCallback} para manejar los eventos invocados por el ciclo de vida del mapa, la
 * interfaz {@link GoogleMap.OnMarkerClickListener} para manejar las pulsaciones sobre las marcas
 * del mapa y la interfaz {@link GoogleMap.OnInfoWindowClickListener} para manejar las pulsaciones
 * sobre los detalles de la instalación mostrados a modo de celda.
 *
 * @see <a href= "https://developers.google.com/android/reference/com/google/android/gms/maps/SupportMapFragment">
 * SupportMapFragment</a>
 */
public class FieldsMapFragment extends SupportMapFragment implements
        FieldsContract.View,
        OnMapReadyCallback,
        GoogleMap.OnMarkerClickListener,
        GoogleMap.OnInfoWindowClickListener {
    /**
     * Nombre de la clase
     */
    @SuppressWarnings("unused")
    private static final String TAG = FieldsMapFragment.class.getSimpleName();

    /**
     * Referencia a la interfaz {@link ActivityContracts.FragmentManagement} posiblemente
     * implementada por Actividad contenedora del Fragmento. Usada para controlar las
     * transacciones entre Fragmentos.
     */
    protected ActivityContracts.FragmentManagement mFragmentManagementListener;

    /**
     * Referencia a la interfaz {@link ActivityContracts.NavigationDrawerManagement} posiblemente
     * implementada por Actividad contenedora del Fragmento. Usada para controlar el aspecto
     * y comportamiento del menú lateral de navegación y la Toolbar.
     */
    protected ActivityContracts.NavigationDrawerManagement mNavigationDrawerManagementListener;

    /**
     * Presentador correspondiente a esta Vista
     */
    FieldsContract.Presenter mFieldsPresenter;

    /**
     * Variable estática utilizada para comprobar sólo una vez si este Fragmento fue instanciado
     * para crear una instalación.
     * <p>
     * Es false hasta que se entregan por primera vez los resultados de
     * la consulta a la base de datos, en ese momento se comprueba si se debe iniciar el proceso
     * de creación de instalación y, si no, se pone a true para no volver a realizar dicha
     * comprobación. Si no existiera esta variable, se volvería a realizar la comprobación en el
     * caso de que se produzca una recreación de la pantalla y la consecuente entrega del resultado
     * de la consulta.
     */
    private static boolean sInitialize;

    /**
     * Referencia al mapa de Google cargado en SupportMapFragment
     *
     * @see <a href= "https://developers.google.com/android/reference/com/google/android/gms/maps/GoogleMap">
     * GoogleMap</a>
     */
    private GoogleMap mMap;

    /**
     * Colección de instalaciones encontradas en la base de datos correspondientes a la ciudad del
     * usuario actual.
     */
    ArrayList<Field> mFieldsList;
    /**
     * Colección de marcas asociadas a cada una de las instalaciones encontradas en la base de datos.
     */
    ArrayList<Marker> mMarkersList;

    /**
     * Constructor
     */
    public FieldsMapFragment() {
        // Required empty public constructor
    }

    /**
     * Método de instanciación del Fragmento
     *
     * @param createNewField true si es necesario iniciar el proceso de creación de un nuevo
     * @return una nueva instancia de FieldsMapFragment
     */
    public static FieldsMapFragment newInstance(boolean createNewField) {
        Bundle b = new Bundle();
        //Is necessary a NewFieldFragment initialization programmatically?
        if (createNewField)
            b.putString(FieldsActivity.INTENT_EXTRA_CREATE_NEW_FIELD, "");
        FieldsMapFragment fragment = new FieldsMapFragment();
        fragment.setArguments(b);
        sInitialize = false;
        return fragment;
    }

    /**
     * Inicializa el Presentador correspondiente a esta Vista
     *
     * @param savedInstanceState estado del Fragmento guardado en una posible rotación de
     *                           la pantalla, o null.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        mFieldsPresenter = new FieldsPresenter(this);
    }

    /**
     * Inicializa el contenido del menú de opciones de la esquina superior derecha de la pantalla
     *
     * @param menu menú de opciones donde se van a emplazar los elementos.
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_add, menu);
    }

    /**
     * Invocado cuando un elemento del menú es pulsado. En este caso se encarga de iniciar el
     * proceso de creación de una nueva instalación.
     *
     * @param item elemento del menú pulsado
     * @return true si se aceptó la pulsación, false en otro caso
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        hideSoftKeyboard();
        if (item.getItemId() == R.id.action_add) {
            if (mFieldsList != null)
                ((FieldsActivity) getActivity()).startMapActivityForResult(mFieldsList, true);
            return true;
        }
        return false;
    }

    /**
     * Al finalizar el proceso de creación de la Actividad contenedora, se invoca este método que
     * establece un título para la barra superior y la acción que debe realizar: mostrar el menú
     * lateral de navegación.
     *
     * @param savedInstanceState estado del Fragmento guardado en una posible rotación de
     *                           la pantalla, o null.
     */
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mFragmentManagementListener.setCurrentDisplayedFragment(getString(R.string.fields), null);
        mNavigationDrawerManagementListener.setToolbarAsNav();
    }

    /**
     * Ordena al Presentador que inicie el proceso de consulta a la base de datos de las
     * instalaciones. También se establece a sí misma como la clase que actúa de listener para los
     * eventos del ciclo de vida del mapa
     */
    @Override
    public void onStart() {
        super.onStart();

        getMapAsync(this);

        mFieldsPresenter.loadNearbyFields(getLoaderManager(), getArguments());
    }

    /**
     * Transforma las instalaciones contenidas en el {@link Cursor} en instalaciones con el formato
     * {@link Field} con la ayuda de {@link UtilesContentProvider#cursorToMultipleField(Cursor)}.
     * A continuación, comprueba si debe iniciar el proceso de creación de instalación utilizando
     * esas instalaciones recibidas. Si no es así, recrea la lista de marcas del mapa e inicia el
     * emplazamiento de las instalaciones sobre él.
     *
     * @param cursor instalaciones obtenidas en la consulta a la base de datos
     */
    @Override
    public void showFields(Cursor cursor) {
        mFieldsList = UtilesContentProvider.cursorToMultipleField(cursor);

        //Is necessary a NewFieldFragment initialization programmatically?
        if (!sInitialize
                && getArguments() != null && getArguments().containsKey(FieldsActivity.INTENT_EXTRA_CREATE_NEW_FIELD)) {
            if (mFieldsList != null)
                ((FieldsActivity) getActivity()).startMapActivityForResult(mFieldsList, true);
            sInitialize = true;
            return;
        }

        // Remove markers from map with remove() and clear marker list with a new ArrayList
        if (mMarkersList != null) for (Marker m : mMarkersList) m.remove();
        mMarkersList = new ArrayList<>();

        //Populate map with Fields. Field's list not empty. Check if map is ready
        if (mMap != null) populateMap();
    }

    /**
     * Emplaza las instalaciones de {@link #mFieldsList} sobre el mapa. Utiliza {@link Marker}s que
     * va creando y añadiendo al mapa y a la lista {@link #mMarkersList}.
     * <p>
     * Asocia cada marca a su instalación estableciendo en la marca como tag el índice de la
     * instalación dentro de {@link #mFieldsList}.
     * <p>
     * Establece un {@link MapFieldMarkerInfoAdapter} como adaptador utilizado para mostrar algunos
     * detalles de la instalación cuando se pulsa sobre la marca.
     */
    private void populateMap() {
        mMap.setInfoWindowAdapter(new MapFieldMarkerInfoAdapter(getActivity().getLayoutInflater(), mFieldsList));
        for (int i = 0; i < mFieldsList.size(); i++) {
            Field f = mFieldsList.get(i);
            LatLng latLong = new LatLng(f.getCoord_latitude(), f.getCoord_longitude());

            float hue = Utiles.getFloatFromResources(getResources(), R.dimen.hue_of_colorSportteam_logo);
            Marker m = mMap.addMarker(new MarkerOptions()
                    .position(latLong)
                    .title(f.getName())
                    .icon(BitmapDescriptorFactory.defaultMarker(hue)));
            m.setTag(i);
            mMarkersList.add(m);
        }
    }

    /**
     * Invocado cuando el mapa esta listo para ser mostrado en la pantalla. Aquí se establecen las
     * características del mapa y se centra la cámara.
     *
     * @param googleMap referencia al mapa de Google cargado en SupportMapFragment
     * @see <a href= "https://developers.google.com/android/reference/com/google/android/gms/maps/SupportMapFragment">
     * SupportMapFragment</a>
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        mMap.setIndoorEnabled(false);
        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.getUiSettings().setCompassEnabled(false);
        mMap.getUiSettings().setTiltGesturesEnabled(false);
        mMap.getUiSettings().setZoomControlsEnabled(false);
        mMap.setMaxZoomPreference(19);
        mMap.setMinZoomPreference(10);

        mMap.setOnMarkerClickListener(this);
        mMap.setOnInfoWindowClickListener(this);

        // Move Camera
        centerCameraOnInit();

        //Populate map with Fields. Map is ready. Check if Field's list are empty
        if (mFieldsList != null && mFieldsList.size() > 0) populateMap();
    }

    /**
     * Centra la cámara sobre el mapa mostrado. Obtiene las coordenadas de la ciudad actual del
     * usuario almacenadas en {@link android.content.SharedPreferences}
     *
     * @see UtilesPreferences#getCurrentUserCityCoords(Context)
     */
    private void centerCameraOnInit() {
        String myUserId = Utiles.getCurrentUserId();
        if (myUserId != null) {
            LatLng myCityLatLong = UtilesPreferences.getCurrentUserCityCoords(getActivityContext());

            if (myCityLatLong.latitude == 0 && myCityLatLong.longitude == 0)
                myCityLatLong = new LatLng(UtilesPreferences.CACERES_LATITUDE, UtilesPreferences.CACERES_LONGITUDE);

            mMap.moveCamera(CameraUpdateFactory.newLatLng(myCityLatLong));
            mMap.moveCamera(CameraUpdateFactory.zoomTo(15));
        }
        showContent();
    }

    /**
     * Invocado cuando se produce una pulsación sobre alguna de las marcas del mapa. Se extrae el
     * tag de esa marca mediante el cual se obtiene la instalación correspondiente, y se centra la
     * cámara en las coordenadas de esa instalación. A continuación, se muestran algunos detalles
     * en un cuadro de información.
     *
     * @param marker marca del mapa pulsada
     * @return true si se aceptó la pulsación, false en otro caso
     * @see <a href= "https://developers.google.com/android/reference/com/google/android/gms/maps/model/Marker">
     * Marker </a>
     */
    @Override
    public boolean onMarkerClick(Marker marker) {
        Integer position = (Integer) marker.getTag();
        if (position != null) {
            Field f = mFieldsList.get(position);

            // Move camera
            LatLng southwest = new LatLng(f.getCoord_latitude() - 0.00135, f.getCoord_longitude() - 0.00135);
            LatLng northeast = new LatLng(f.getCoord_latitude() + 0.00135, f.getCoord_longitude() + 0.00135);
            LatLngBounds llb = new LatLngBounds(southwest, northeast);
            mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(llb, 0));
            marker.showInfoWindow();

            return true;
        }
        return false;
    }

    /**
     * Invocado cuando se produce una pulsación sobre el cuadro de información de alguna de las
     * marcas del mapa. Se extrae el tag de esa marca mediante el cual se obtiene la instalación
     * correspondiente, y se instancia y muestra el Fragmento de detalles de dicha instalación.
     *
     * @param marker marcador correspondiente al cuadro de información pulsado
     */
    @Override
    public void onInfoWindowClick(Marker marker) {
        Integer position = (Integer) marker.getTag();
        if (position != null) {
            Field f = mFieldsList.get(position);

            // Open Detail Field
            Fragment newFragment = DetailFieldFragment.newInstance(f.getId(), false);
            mFragmentManagementListener.initFragment(newFragment, true);

            marker.hideInfoWindow();
        }
    }

    /**
     * @return {@link Context} de la Actividad contenedora
     */
    @Override
    public Context getActivityContext() {
        return getActivity();
    }

    /**
     * En este método del ciclo de vida se asocian las variables a la Actividad, en el caso de que
     * haya implementado las interfaces de {@link ActivityContracts}
     *
     * @param context {@link Context} de Actividad
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ActivityContracts.FragmentManagement)
            mFragmentManagementListener = (ActivityContracts.FragmentManagement) context;
        if (context instanceof ActivityContracts.NavigationDrawerManagement)
            mNavigationDrawerManagementListener = (ActivityContracts.NavigationDrawerManagement) context;
    }

    /**
     * Proceso inverso a {@link #onAttach(Context)}: anula las variables de las interfaces
     */
    @Override
    public void onDetach() {
        super.onDetach();
        hideSoftKeyboard();
        mFragmentManagementListener = null;
        mNavigationDrawerManagementListener = null;
    }

    /**
     * Utiliza {@link #mFragmentManagementListener} para mostrar el Fragmento
     */
    public void showContent() {
        if (mFragmentManagementListener != null)
            mFragmentManagementListener.showContent();
    }

    /**
     * Utiliza {@link #mFragmentManagementListener} para ocultar el Fragmento
     */
    public void hideContent() {
        if (mFragmentManagementListener != null)
            mFragmentManagementListener.hideContent();
    }

    /**
     * Obtiene una referencia a la {@link View} que tiene el foco de la interfaz y, si esta
     * mostrando el teclado flotante en la pantalla, lo esconde
     *
     * @see <a href= "https://stackoverflow.com/a/17789187/4235666">
     * (StackOverflow) Close/hide the Android Soft Keyboard</a>
     */
    public void hideSoftKeyboard() {
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm =
                    (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null)
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
