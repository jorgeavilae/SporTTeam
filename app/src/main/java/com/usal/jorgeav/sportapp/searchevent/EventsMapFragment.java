package com.usal.jorgeav.sportapp.searchevent;

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
import com.usal.jorgeav.sportapp.adapters.MapEventMarkerInfoAdapter;
import com.usal.jorgeav.sportapp.data.Event;
import com.usal.jorgeav.sportapp.eventdetail.DetailEventFragment;
import com.usal.jorgeav.sportapp.mainactivities.ActivityContracts;
import com.usal.jorgeav.sportapp.mainactivities.EventsActivity;
import com.usal.jorgeav.sportapp.mainactivities.SearchEventsActivity;
import com.usal.jorgeav.sportapp.searchevent.advancedsearch.SearchEventsFragment;
import com.usal.jorgeav.sportapp.utils.Utiles;
import com.usal.jorgeav.sportapp.utils.UtilesContentProvider;
import com.usal.jorgeav.sportapp.utils.UtilesPreferences;

import java.util.ArrayList;

/**
 * Fragmento utilizado para mostrar la colección de partidos a los que el usuario actual puede
 * unirse por no mantener ningún tipo de relación con ellos (no participa, no está invitado, no ha
 * enviado una petición de participación...).
 * <p>
 * Este Fragmento hereda de SupportMapFragment para mostrar un mapa en el que emplazar cada
 * uno de los partidos. Se encarga de inicializar el mapa de la interfaz. También permite
 * viajar al Fragmento en el que se muestran los detalles del partido y al Fragmento utilizado
 * para crear un partido nuevo. Además, también incluye un botón para mostrar un Fragmento en el que
 * introducir filtros que refinen la búsqueda.
 * <p>
 * Para mostrar los partidos en el mapa, almacena una colección de marcas que se colocan sobre
 * él gracias a las coordenadas que pertenecen a cada partido. Al pulsar sobre las marcas,
 * muestran algunos detalles del partido, a modo de celda de lista, y si se pulsa sobre esos
 * detalles muestra el Fragmento con la vista de detalles del partido.
 * <p>
 * Como no hereda de {@link com.usal.jorgeav.sportapp.BaseFragment}, esta clase necesita implementar
 * algunos métodos de esa clase que serán usados por el Presentador. Además, implementa la interfaz
 * {@link EventsMapContract.View} para la comunicación con esta clase, la interfaz
 * {@link OnMapReadyCallback} para manejar los eventos invocados por el ciclo de vida del mapa, la
 * interfaz {@link GoogleMap.OnMarkerClickListener} para manejar las pulsaciones sobre las marcas
 * del mapa y la interfaz {@link GoogleMap.OnInfoWindowClickListener} para manejar las pulsaciones
 * sobre los detalles del partido mostrados a modo de celda.
 *
 * @see <a href= "https://developers.google.com/android/reference/com/google/android/gms/maps/SupportMapFragment">
 * SupportMapFragment</a>
 */
public class EventsMapFragment extends SupportMapFragment implements
        EventsMapContract.View,
        OnMapReadyCallback,
        GoogleMap.OnMarkerClickListener,
        GoogleMap.OnInfoWindowClickListener {
    /**
     * Nombre de la clase
     */
    @SuppressWarnings("unused")
    private static final String TAG = EventsMapFragment.class.getSimpleName();

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
    EventsMapContract.Presenter mEventsMapPresenter;

    /**
     * Referencia al mapa de Google cargado en SupportMapFragment
     *
     * @see <a href= "https://developers.google.com/android/reference/com/google/android/gms/maps/GoogleMap">
     * GoogleMap</a>
     */
    private GoogleMap mMap;

    /**
     * Colección de partidos encontrados en la base de datos correspondientes a la ciudad del
     * usuario actual.
     */
    ArrayList<Event> mEventsList;
    /**
     * Colección de marcas asociadas a cada una de los partidos encontradas en la base de datos.
     */
    ArrayList<Marker> mMarkersList;

    /**
     * Constructor sin argumentos
     */
    public EventsMapFragment() {
        // Required empty public constructor
    }

    /**
     * Método de instanciación del Fragmento
     *
     * @return una nueva instancia de EventsMapFragment
     */
    public static EventsMapFragment newInstance() {
        return new EventsMapFragment();
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

        mEventsMapPresenter = new EventsMapPresenter(this);
    }

    /**
     * Inicializa el contenido del menú de opciones de la esquina superior derecha de la pantalla
     *
     * @param menu menú de opciones donde se van a emplazar los elementos.
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.menu_search_events, menu);
    }

    /**
     * Invocado cuando un elemento del menú es pulsado. En este caso se encarga de instanciar y
     * mostrar el Fragmento para introducir filtros que refinen los partidos del mapa, o de iniciar
     * el proceso de creación de un nueva partido.
     *
     * @param item elemento del menú pulsado
     * @return true si se aceptó la pulsación, false en otro caso
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if (item.getItemId() == R.id.action_advanced_search) {
            mFragmentManagementListener.initFragment(SearchEventsFragment.newInstance(), true);
            return true;
        } else if (item.getItemId() == R.id.action_add_event) {
            mNavigationDrawerManagementListener.simulateNavigationItemSelected(R.id.nav_events,
                    EventsActivity.CREATE_NEW_EVENT_INTENT_EXTRA, "dummy");
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
        mFragmentManagementListener.setCurrentDisplayedFragment(getString(R.string.search_events), null);
        mNavigationDrawerManagementListener.setToolbarAsNav();
    }

    /**
     * Ordena al Presentador que inicie el proceso de consulta a la base de datos de los partidos,
     * con algún filtro si es necesario. También se establece a sí misma como la clase que actúa
     * de listener para los eventos del ciclo de vida del mapa
     */
    @Override
    public void onStart() {
        super.onStart();

        getMapAsync(this);

        Bundle args = createBundleWithParams();
        mEventsMapPresenter.loadNearbyEvents(getLoaderManager(), args);
    }

    /**
     * Crea un {@link Bundle} donde alojar los filtros que hayan podido establecerse en
     * {@link SearchEventsFragment}. Estos parámetros estarán en variables de la Actividad
     * contenedora {@link SearchEventsActivity}.
     *
     * @return un {@link Bundle} con los parámetros que serán extraídos al realizar la consulta
     */
    private Bundle createBundleWithParams() {
        Bundle args = new Bundle();
        if (getActivity() instanceof SearchEventsActivity) {
            SearchEventsActivity searchEventsActivity = (SearchEventsActivity) getActivity();
            if (searchEventsActivity.mSportId != null)
                args.putString(SearchEventsActivity.INSTANCE_SPORT_SELECTED, searchEventsActivity.mSportId);

            if (searchEventsActivity.mDateFrom != null)
                args.putLong(SearchEventsActivity.INSTANCE_DATE_FROM_SELECTED, searchEventsActivity.mDateFrom);
            if (searchEventsActivity.mDateTo != null)
                args.putLong(SearchEventsActivity.INSTANCE_DATE_TO_SELECTED, searchEventsActivity.mDateTo);

            if (searchEventsActivity.mTotalFrom != null)
                args.putInt(SearchEventsActivity.INSTANCE_TOTAL_FROM_SELECTED, searchEventsActivity.mTotalFrom);
            if (searchEventsActivity.mTotalTo != null)
                args.putInt(SearchEventsActivity.INSTANCE_TOTAL_TO_SELECTED, searchEventsActivity.mTotalTo);

            if (searchEventsActivity.mEmptyFrom != null)
                args.putInt(SearchEventsActivity.INSTANCE_EMPTY_FROM_SELECTED, searchEventsActivity.mEmptyFrom);
            if (searchEventsActivity.mEmptyTo != null)
                args.putInt(SearchEventsActivity.INSTANCE_EMPTY_TO_SELECTED, searchEventsActivity.mEmptyTo);
        }
        return args;
    }

    /**
     * Transforma los partidos contenidas en el {@link Cursor} en partidos con el formato
     * {@link Event} con la ayuda de {@link UtilesContentProvider#cursorToMultipleEvent(Cursor)}.
     * A continuación, recrea la lista de marcas del mapa e inicia el emplazamiento de los partidos
     * sobre él.
     *
     * @param cursor partidos obtenidos en la consulta a la base de datos
     */
    @Override
    public void showEvents(Cursor cursor) {
        mEventsList = UtilesContentProvider.cursorToMultipleEvent(cursor);

        // Remove markers from map with remove() and clear marker list with a new ArrayList
        if (mMarkersList != null) for (Marker m : mMarkersList) m.remove();
        mMarkersList = new ArrayList<>();

        //Populate map with Events. Events's list not empty. Check if map is ready
        if (mMap != null) populateMap();
    }

    /**
     * Emplaza los partidos de {@link #mEventsList} sobre el mapa. Utiliza {@link Marker}s que
     * va creando y añadiendo al mapa y a la lista {@link #mMarkersList}.
     * <p>
     * Si varias marcas van a establecerse sobre las mismas coordenadas, se introduce un pequeño
     * desfase en la componente longitud, para que las marcas aparezcan una al lado de otra en el
     * mapa.
     * <p>
     * Asocia cada marca a su partido estableciendo en la marca como tag el índice del partido
     * dentro de {@link #mEventsList}.
     * <p>
     * Establece un {@link MapEventMarkerInfoAdapter} como adaptador utilizado para mostrar algunos
     * detalles de la instalación cuando se pulsa sobre la marca.
     */
    private void populateMap() {
        mMap.setInfoWindowAdapter(new MapEventMarkerInfoAdapter(getActivity().getLayoutInflater(), mEventsList));
        for (int i = 0; i < mEventsList.size(); i++) {
            Event event = mEventsList.get(i);
            LatLng latLong = new LatLng(event.getCoord_latitude(), event.getCoord_longitude());

            // Add marker to map
            float hue = Utiles.getFloatFromResources(getResources(), R.dimen.hue_of_colorSportteam_logo);
            Marker m = mMap.addMarker(new MarkerOptions()
                    .position(latLong)
                    .title(event.getName())
                    .icon(BitmapDescriptorFactory.defaultMarker(hue)));

            // If there was already a marker in that position,
            // the new one is offset so that it appears next to it.
            // While loop: when there are multiple events in same position,
            // should apply multiple offsets.
            while (lookForMarkerInSamePosition(m, mMarkersList) > -1) {
                LatLng newLatLong = new LatLng(m.getPosition().latitude, m.getPosition().longitude + 0.0005);
                m.setPosition(newLatLong);
            }

            // Store marker
            m.setTag(i);
            mMarkersList.add(m);
        }
    }

    /**
     * Comprueba si existe una marca de la lista que tenga las mismas coordenadas que la marca
     * indicada.
     *
     * @param m           marca indicada, cuyas coordenadas se van a buscar en la lista
     * @param markersList lista de marcas contra las que comprobar las coordenadas
     * @return el índice de la primera marca dentro de la lista que tenga las mismas coordenadas
     * que la marca indicada, o -1 si no se encuentra coincidencia.
     */
    private int lookForMarkerInSamePosition(Marker m, ArrayList<Marker> markersList) {
        for (int i = 0; i < markersList.size(); i++)
            if (markersList.get(i).getPosition().equals(m.getPosition()))
                return i;
        return -1;
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

        //Populate map with Events. Map is ready. Check if Events's list are empty
        if (mEventsList != null && mEventsList.size() > 0) populateMap();
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

            /* Error on login and current user city coordinates aren't in Content Provider */
            if (myCityLatLong == null || (myCityLatLong.latitude == 0 && myCityLatLong.longitude == 0))
                mFragmentManagementListener.signOut();
            else {
                mMap.moveCamera(CameraUpdateFactory.newLatLng(myCityLatLong));
                mMap.moveCamera(CameraUpdateFactory.zoomTo(15));
            }
        }
        showContent();
    }

    /**
     * Invocado cuando se produce una pulsación sobre alguna de las marcas del mapa. Se extrae el
     * tag de esa marca mediante el cual se obtiene el partido correspondiente, y se centra la
     * cámara en las coordenadas de ese partido. A continuación, se muestran algunos detalles
     * en un cuadro de información.
     *
     * @param marker marca del mapa pulsada
     * @return true si se aceptó la pulsación, false en otro caso
     * @see <a href= "https://developers.google.com/android/reference/com/google/android/gms/maps/model/Marker">
     * Marker</a>
     */
    @Override
    public boolean onMarkerClick(Marker marker) {
        Integer position = (Integer) marker.getTag();
        if (position != null) {
            Event event = mEventsList.get(position);

            // Move camera
            LatLng southwest = new LatLng(event.getCoord_latitude() - 0.00135, event.getCoord_longitude() - 0.00135);
            LatLng northeast = new LatLng(event.getCoord_latitude() + 0.00135, event.getCoord_longitude() + 0.00135);
            LatLngBounds llb = new LatLngBounds(southwest, northeast);
            mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(llb, 0));
            marker.showInfoWindow();

            return true;
        }
        return false;
    }

    /**
     * Invocado cuando se produce una pulsación sobre el cuadro de información de alguna de las
     * marcas del mapa. Se extrae el tag de esa marca mediante el cual se obtiene el partido
     * correspondiente, y se instancia y muestra el Fragmento de detalles de dicho partido.
     *
     * @param marker marcador correspondiente al cuadro de información pulsado
     */
    @Override
    public void onInfoWindowClick(Marker marker) {
        Integer position = (Integer) marker.getTag();
        if (position != null) {
            Event event = mEventsList.get(position);

            // Open event's details
            Fragment newFragment = DetailEventFragment.newInstance(event.getEvent_id());
            mFragmentManagementListener.initFragment(newFragment, true);

            marker.hideInfoWindow();
        }
    }

    /**
     * Devuelve el Context de la Actividad contenedora
     *
     * @return {@link Context} de la Actividad contenedora
     */
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
