package com.usal.jorgeav.sportapp.mainactivities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

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
import com.usal.jorgeav.sportapp.data.MyPlace;
import com.usal.jorgeav.sportapp.network.GeocodingTask;
import com.usal.jorgeav.sportapp.utils.Utiles;
import com.usal.jorgeav.sportapp.utils.UtilesPreferences;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * Actividad para seleccionar direcciones o marcas de un mapa. Carga un
 * {@link
 * <a href= "https://developers.google.com/android/reference/com/google/android/gms/maps/SupportMapFragment">
 *     SupportMapFragment
 * </a>}
 * donde emplaza las instalaciones que recibe en la creación.
 *
 * <p></p>
 * Implementa varios callbacks para
 * recibir eventos sobre el mapa, como por ejemplo pulsaciones sobre coordendas concretas con las
 * que se utilizará
 * {@link
 * <a href= "https://developers.google.com/android/reference/com/google/android/gms/location/places/GeoDataApi">
 *     Google Places API
 * </a>}
 * para obtener la dirección seleccionada.
 *
 * <p></p>
 * También es capaz de iniciar el proceso para crear una instalación nueva, en el caso de que no
 * se encuentre en el mapa la instalación deseada por el usuario
 */
public class MapsActivity extends AppCompatActivity
        implements OnMapReadyCallback,
        GoogleMap.OnMapLongClickListener,
        GoogleMap.OnMarkerClickListener {
    /**
     * Nombre de la clase
     */
    public static final String TAG = MapsActivity.class.getSimpleName();

    /**
     * Clave para identificar el dato añadido al {@link Intent}, cuando se inicia esta Actividad.
     * El dato añadido es la colección de instalaciones que debe mostrarse sobre el mapa.
     */
    public static final String INTENT_EXTRA_FIELD_LIST = "INTENT_EXTRA_FIELD_LIST";
    /**
     * Clave para identificar el dato añadido al {@link Intent}, cuando se inicia esta Actividad.
     * El dato añadido es un booleano que indica si se posibilita o no la selección de direcciones
     * sobre el mapa, o sólo se pueden seleccionar instalaciones.
     */
    public static final String INTENT_EXTRA_ONLY_FIELDS = "INTENT_EXTRA_ONLY_FIELDS";
    /**
     * Clave para identificar el dato añadido al {@link Intent}, cuando se inicia esta Actividad.
     * El dato añadido es un booleano que indica si esta Actividad fue iniciada por
     * {@link FieldsActivity} y en ese caso cargar un menú direferente en la Toolbar
     */
    public static final String INTENT_EXTRA_PARENT_FIELDS_ACTIVITY = "INTENT_EXTRA_PARENT_FIELDS_ACTIVITY";
    /**
     * Clave para identificar el dato que se utiliza como resultado, cuando finaliza esta Actividad.
     * El dato añadido es una dirección sobre el mapa {@link MyPlace}
     */
    public static final String PLACE_SELECTED_EXTRA = "PLACE_SELECTED_EXTRA";
    /**
     * Clave para identificar el dato que se utiliza como resultado, cuando finaliza esta Actividad.
     * El dato añadido es una instalación {@link Field}
     */
    public static final String FIELD_SELECTED_EXTRA = "FIELD_SELECTED_EXTRA";
    /**
     * Clave para identificar el resultado, cuando finaliza esta Actividad. Añadirlo significa
     * que el usuario seleccionó la opción de crear una instalción nueva porque no encontraba
     * la que quería.
     */
    public static final String ADD_FIELD_SELECTED_EXTRA = "ADD_FIELD_SELECTED_EXTRA";

    /**
     * Referencia al mapa de Google cargado en
     * {@link
     * <a href= "https://developers.google.com/android/reference/com/google/android/gms/maps/SupportMapFragment">
     *     SupportMapFragment
     * </a>}
     */
    private GoogleMap mMap;
    /**
     * Colección de instalaciones que deben mostrarse sobre el mapa
     */
    ArrayList<Field> mFieldsList;
    /**
     * Conlección de marcas sobre el mapa, cada una indicando la posición de una instalación
     */
    ArrayList<Marker> mMarkersList;
    /**
     * True si sólo se pueden seleccionar instalaciones o false si tambíen direcciones
     */
    boolean mOnlyField;
    /**
     * Referencia a la Toolbar de la Actividad
     */
    Toolbar mToolbar;
    /**
     * Dirección seleccionada. Ver {@link #PLACE_SELECTED_EXTRA}
     */
    MyPlace mPlaceSelected;
    /**
     * Instalación seleccionada. Ver {@link #FIELD_SELECTED_EXTRA}
     */
    Field mFieldSelected;
    /**
     * Marca del mapa seleccionada que se está mostrando actualmente
     */
    Marker mMarkerSelectedPlace;

    /**
     * En este método se carga la interfaz y se inicializan las variables
     *
     * @param savedInstanceState estado de la Actividad guardado en una posible rotación de
     *                           la pantalla, o null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onBackPressed();
                }
            });
        }

        startMapFragment();
    }

    /**
     * Inicializa el contenido del menú de la Toolbar, dependiendo de si la Actividad la inició
     * {@link FieldsActivity} u otra diferente
     *
     * @param menu menú en el que se emplazan las opciones
     *
     * @return true para mostrar el menú, false para no mostrarlo.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (getIntent().getBooleanExtra(INTENT_EXTRA_PARENT_FIELDS_ACTIVITY, false))
            getMenuInflater().inflate(R.menu.menu_ok, menu);
        else
            getMenuInflater().inflate(R.menu.menu_map, menu);
        return true;
    }

    /**
     * Invocado cuando el usuario pulsa sobre una entrada del menú. Actúa aceptando el lugar
     * seleccionado o indicando la necesidad de crear una instalación nueva. Luego finaliza
     * esta Actividad.
     *
     * @param item el ítem que fue seleccionado
     *
     * @return false para seguir invocando esta llamada a Actividades superiores,
     *          true para parar e indicar que la puslación se procesó aquí.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if (item.getItemId() == R.id.action_ok) {
            //Field first in case MyPlace isn't succeeded
            if (mFieldSelected != null) {
                Intent resultIntent = new Intent();
                resultIntent.putExtra(FIELD_SELECTED_EXTRA, mFieldSelected);
                setResult(RESULT_OK, resultIntent);
                finish();
            } else if (mPlaceSelected != null && mPlaceSelected.isSucceed()) {
                Intent resultIntent = new Intent();
                resultIntent.putExtra(PLACE_SELECTED_EXTRA, mPlaceSelected);
                setResult(RESULT_OK, resultIntent);
                finish();
            } else {
                Toast.makeText(this, R.string.toast_should_select_place, Toast.LENGTH_SHORT).show();
            }
            return true;
        } else if (item.getItemId() == R.id.action_new_field) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this)
                    .setMessage(R.string.dialog_msg_create_new_field)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Intent resultIntent = new Intent();
                            resultIntent.putExtra(ADD_FIELD_SELECTED_EXTRA, "dummy");
                            setResult(RESULT_OK, resultIntent);
                            finish();
                        }
                    })
                    .setNegativeButton(android.R.string.no, null);
            builder.create().show();
        }
        return false;
    }

    /**
     * Inicializa las variables relacionadas con la carga del mapa: las instalaciones, la
     * colección de marcas y el propio mapa.
     */
    private void startMapFragment() {
        mMarkersList = new ArrayList<>();
        mFieldsList = getIntent().getParcelableArrayListExtra(INTENT_EXTRA_FIELD_LIST);
        if (mFieldsList == null) mFieldsList = new ArrayList<>();

        //False by default to select anywhere
        mOnlyField = getIntent().getBooleanExtra(INTENT_EXTRA_ONLY_FIELDS, false);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle(getString(R.string.title_activity_maps));
    }

    /**
     * Invocado cuando finaliza la carga asíncrona del mapa. Se establecen las características
     * de este como la posición de la cámara o las capas y el tipo de mapa que será (híbrido).
     * Se emplazan las instalaciones en él {@link #populateMap()} y se centra la cámara
     * {@link #centerCameraOnInit()}
     *
     * @param googleMap referencia al objeto mapa de la interfaz
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

        mMap.setOnMapLongClickListener(this);
        mMap.setOnMarkerClickListener(this);

        populateMap();

        // Move Camera
        centerCameraOnInit();
    }

    /**
     * Crea y emplaza sobre el mapa marcas a partir de las instalaciones pasadas en la
     * creación de la Actividad.
     */
    private void populateMap() {
        mMap.setInfoWindowAdapter(new MapFieldMarkerInfoAdapter(getLayoutInflater(), mFieldsList));
        //Populate map with Fields
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
     * Centra la cámara sobre el mapa en su inicialización.
     */
    private void centerCameraOnInit() {
        String myUserId = Utiles.getCurrentUserId();
        if (myUserId != null) {
            LatLng myCityLatLong = UtilesPreferences.getCurrentUserCityCoords(this);

            if (myCityLatLong.latitude == 0 && myCityLatLong.longitude == 0)
                myCityLatLong = new LatLng(UtilesPreferences.CACERES_LATITUDE, UtilesPreferences.CACERES_LONGITUDE);

            mMap.moveCamera(CameraUpdateFactory.newLatLng(myCityLatLong));
            mMap.moveCamera(CameraUpdateFactory.zoomTo(15));
        }
    }

    /**
     * Invocado cuando se pulsa sobre una de las marcas del mapa que representa a una instalación.
     * Mueve la cámara hacia ella y muestra la información de la instalación que representa.
     *
     * @param marker marca pulsada
     * @return true si se procesa la marca pulsada, false en otro caso.
     */
    @Override
    public boolean onMarkerClick(Marker marker) {
        Integer position = (Integer) marker.getTag();
        if (position != null) {
            //Field selected: invalid MyPlace selected
            if (mMarkerSelectedPlace != null) mMarkerSelectedPlace.remove();
            mMarkerSelectedPlace = null;
            mPlaceSelected = null;

            mFieldSelected = mFieldsList.get(position);

            // Move camera
            LatLng southwest = new LatLng(mFieldSelected.getCoord_latitude()-0.00135, mFieldSelected.getCoord_longitude()-0.00135);
            LatLng northeast = new LatLng(mFieldSelected.getCoord_latitude()+0.00135, mFieldSelected.getCoord_longitude()+0.00135);
            LatLngBounds llb = new LatLngBounds(southwest, northeast);
            mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(llb, 0));
            marker.showInfoWindow();
            return true;
        }
        return false;
    }

    /**
     * Invocado cuando se produce una pulsación larga sobre un punto del mapa. Utilizado para
     * seleccinar direcciones en lugar de instalaciones. Busca instalaciones cercanas a las que
     * podría haberse referido el usuario y, si no las encuentra, inicia una tarea en segundo
     * plano para usar
     * {@link
     * <a href= "https://developers.google.com/android/reference/com/google/android/gms/location/places/GeoDataApi">
     *     Google Places API
     * </a>}
     * y obtener la dirección a partir de las coordenadas.
     *
     * @param latLng coordenadas de la pulsación larga sobre el mapa.
     */
    @Override
    public void onMapLongClick(LatLng latLng) {
        int position = Utiles.searchClosestFieldInList(mFieldsList, latLng);
        if (position > -1) {
            // latLng are too near from a Field
            onMarkerClick(mMarkersList.get(position));
        } else if (!mOnlyField) { /* Only allow MyPlace when mOnlyFields it set false */
            new MyAsyncTask(this).execute(latLng);
        }
    }

    /**
     * Clase interna derivada de {@link AsyncTask} para realizar la tarea en segundo plano de
     * geocodificación inversa a través de una petición HTTP.
     *
     * <p>Esta clase es estática para evitar pérdidas de memoria en el caso de que la Activiad
     * sufra un proceso de recreación.
     */
    private static class MyAsyncTask extends AsyncTask<LatLng, Void, MyPlace> {

        /**
         * Referencia a la Actividad contenedora de esta clase a la que debe pasar los
         * resultados de la consulta. La referncia es débil para poder ser recolectada por el
         * Garbage Collector
         *
         * <p><b>Mas información: </b>
         * {@link
         * <a href= "https://developer.android.com/reference/java/lang/ref/WeakReference">
         *     WeakReference
         * </a>}
         */
        private WeakReference<MapsActivity> mActivity;

        /**
         * Contructor con la referencia a la Actividad
         *
         * @param mapsActivity referencia a la Actividad contenedora de la clase
         */
        MyAsyncTask(MapsActivity mapsActivity) {
            mActivity = new WeakReference<>(mapsActivity);
        }

        /**
         * Realiza la consulta de la dirección. Método ejecutado en segndo plano.
         *
         * @param latLng coordenadas de la consulta
         * @return {@link MyPlace} con el resultado de la consulta
         */
        @Override
        protected MyPlace doInBackground(LatLng... latLng) {
            // Check if MapsActivity still exists
            MapsActivity mapsActivity = mActivity.get();
            if (mapsActivity == null || mapsActivity.isFinishing()) return null;

            String apiKey = mapsActivity.getResources().getString(R.string.google_maps_and_geocoding_key);
            return GeocodingTask.getMyPlaceObjectFromLatLngLocation(apiKey, latLng[0]);
        }

        /**
         * Devuelve los resultados de la tarea en segundo plano
         *
         * @param place {@link MyPlace} resultado de la consulta con una dirección o un error
         */
        @Override
        protected void onPostExecute(MyPlace place) {
            mActivity.get().updateSelectedPlace(place);
        }
    }

    /**
     * Recibe y muestra la dirección seleccionada con una pulsación larga del usuario.
     *
     * <p>Comprueba que no hay errores (si los hay los muestra) y crea una marca para la dirección
     * seleccionada que muestre el nombre de la dirección escrita.
     *
     * @param selectedPlace {@link MyPlace} resultado de la consulta
     */
    private void updateSelectedPlace(MyPlace selectedPlace) {
        if (selectedPlace.isSucceed()) {
            // If the closest address to onMapLongClick's coordinates was already in the Field list
            // means the distance is greater that the distance allowed but the address is the same
            // so it is like the user click on that Marker.
            int position = Utiles.searchCoordinatesInFieldList(mFieldsList, selectedPlace.getCoordinates());
            if (position > -1) {
                onMarkerClick(mMarkersList.get(position));
                return;
            }

            //MyPlace selected: invalid Field selected
            mFieldSelected = null;
            mPlaceSelected = selectedPlace;

            if (mMarkerSelectedPlace != null) mMarkerSelectedPlace.remove();
            mMarkerSelectedPlace = null;

            float hue = Utiles.getFloatFromResources(getResources(), R.dimen.hue_of_colorSportteam_logo);
            mMarkerSelectedPlace = mMap.addMarker(new MarkerOptions()
                    .position(mPlaceSelected.getCoordinates())
                    .title(mPlaceSelected.getAddress())
                    .icon(BitmapDescriptorFactory.defaultMarker(hue)));
            LatLngBounds llb = new LatLngBounds(mPlaceSelected.getViewPortSouthwest(),
                    mPlaceSelected.getViewPortNortheast());
            mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(llb, 0));
        } else {
            mPlaceSelected = selectedPlace;
            switch (mPlaceSelected.getStatus()) {
                case "OK":
                    break;
                case "ZERO_RESULTS":
                    /* Maybe latlng in a remote location */
                    Toast.makeText(this, R.string.toast_place_error_zero_results, Toast.LENGTH_SHORT).show();
                    break;
                case "OVER_QUERY_LIMIT":
                    /* Over your quota. */
                    Toast.makeText(this, R.string.toast_place_error_zero_results, Toast.LENGTH_SHORT).show();
                    break;
                case "REQUEST_DENIED":
                    /* API key invalid */
                    Toast.makeText(this, R.string.toast_place_error_bad_connection, Toast.LENGTH_SHORT).show();
                    break;
                case "INVALID_REQUEST":
                    /* Missing latlng or error in result_type */
                    Toast.makeText(this, R.string.toast_place_error_bad_connection, Toast.LENGTH_SHORT).show();
                    break;
                case "OUT_OF_BOUNDS":
                    Toast.makeText(this, R.string.toast_place_error_out_of_bounds, Toast.LENGTH_SHORT).show();
                    break;
                case "UNKNOWN_ERROR":
                    /* Probably a bad connection */
                    Toast.makeText(this, R.string.toast_place_error_bad_connection, Toast.LENGTH_SHORT).show();
                    break;
                default:
                    Toast.makeText(this, R.string.toast_place_error_bad_connection, Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }
}
