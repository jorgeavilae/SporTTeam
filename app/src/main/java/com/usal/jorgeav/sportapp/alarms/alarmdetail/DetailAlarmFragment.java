package com.usal.jorgeav.sportapp.alarms.alarmdetail;


import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.usal.jorgeav.sportapp.BaseFragment;
import com.usal.jorgeav.sportapp.R;
import com.usal.jorgeav.sportapp.adapters.EventsAdapter;
import com.usal.jorgeav.sportapp.alarms.addalarm.NewAlarmFragment;
import com.usal.jorgeav.sportapp.data.Field;
import com.usal.jorgeav.sportapp.eventdetail.DetailEventFragment;
import com.usal.jorgeav.sportapp.fields.fielddetail.DetailFieldFragment;
import com.usal.jorgeav.sportapp.mainactivities.BaseActivity;
import com.usal.jorgeav.sportapp.network.firebase.FirebaseDBContract;
import com.usal.jorgeav.sportapp.network.firebase.actions.NotificationsFirebaseActions;
import com.usal.jorgeav.sportapp.utils.Utiles;
import com.usal.jorgeav.sportapp.utils.UtilesTime;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Fragmento utilizado para mostrar los detalles de una alarma. Se encarga de inicializar
 * los componentes de la interfaz y utilizarlos para mostrar los parámetros de la alarma recuperados
 * de la base de datos.
 * Implementa la interfaz {@link DetailAlarmContract.View} para la comunicación con esta clase y la
 * interfaz {@link EventsAdapter.OnEventItemClickListener} para manejar las pulsaciones sobre los
 * eventos encontrados que coincidan con la alarma mostrada.
 */
public class DetailAlarmFragment extends BaseFragment implements
        DetailAlarmContract.View,
        EventsAdapter.OnEventItemClickListener {

    /**
     * Nombre de la clase
     */
    @SuppressWarnings("unused")
    private static final String TAG = DetailAlarmFragment.class.getSimpleName();
    /**
     * Etiqueta para establecer el identificador de alarma que debe mostrarse en la instanciación
     * del Fragmento
     */
    public static final String BUNDLE_ALARM_ID = "BUNDLE_ALARM_ID";

    /**
     * Identificador de la alarma que debe mostrarse
     */
    private static String mAlarmId = "";
    /**
     * Identificador del deporte de la alarma que se muestra
     */
    private static String mSportId = "";

    /**
     * Presentador correspondiente a esta Vista
     */
    private DetailAlarmContract.Presenter mPresenter;

    /**
     * Etiqueta utilizada para guardar, en el estado del Fragmento, las coordenadas que debe mostrar
     * el mapa de la interfaz.
     */
    public static final String INSTANCE_COORDS = "INSTANCE_COORDS";
    /**
     * Coordenadas que se muestran en el mapa de la interfaz, correspondientes a la instalación o
     * la ciudad en la que se establece la alarma
     */
    LatLng mCoords;

    /**
     * Referencia al mapa de la interfaz para mostrar la instalación o ciudad escogida
     */
    @BindView(R.id.alarm_detail_map)
    MapView alarmMap;
    /**
     * Objeto principal de {@link
     * <a href= "https://developers.google.com/android/reference/com/google/android/gms/maps/package-summary">
     *     Google Maps API
     * </a>}. Hace referencia al mapa que provee esta API.
     */
    private GoogleMap mMap;
    /**
     * Referencia al elemento de la interfaz para indicar el deporte
     */
    @BindView(R.id.alarm_detail_sport)
    ImageView imageViewAlarmSport;
    /**
     * Referencia al texto de la interfaz para indicar el lugar de la alarma
     */
    @BindView(R.id.alarm_detail_place)
    TextView textViewAlarmPlace;
    /**
     * Referencia al botón de la interfaz para mostrar los detalles de la instalación
     */
    @BindView(R.id.alarm_detail_place_icon)
    ImageView textViewAlarmPlaceIcon;
    /**
     * Referencia al elemento de la interfaz para indicar el limite inferior del rango de fechas
     */
    @BindView(R.id.alarm_detail_date_from)
    TextView textViewAlarmDateFrom;
    /**
     * Referencia al elemento de la interfaz para indicar el limite superior del rango de fechas
     */
    @BindView(R.id.alarm_detail_date_to)
    TextView textViewAlarmDateTo;
    /**
     * Referencia al elemento de la interfaz para indicar el limite inferior del rango de puestos
     * totales
     */
    @BindView(R.id.alarm_detail_total_from)
    TextView textViewAlarmTotalFrom;
    /**
     * Referencia al elemento de la interfaz para indicar el limite superior del rango de puestos
     * totales
     */
    @BindView(R.id.alarm_detail_total_to)
    TextView textViewAlarmTotalTo;
    /**
     * Referencia al elemento de la interfaz para indicar el limite inferior del rango de puestos
     * vacantes
     */
    @BindView(R.id.alarm_detail_empty_from)
    TextView textViewAlarmEmptyFrom;
    /**
     * Referencia al elemento de la interfaz para indicar el limite superior del rango de puestos
     * vacantes
     */
    @BindView(R.id.alarm_detail_empty_to)
    TextView textViewAlarmEmptyTo;
    /**
     * Referencia a la lista de la interfaz para mostrar la colección de partidos que coinciden
     * con esta alarma
     */
    @BindView(R.id.alarm_detail_events_coincidence_list)
    RecyclerView eventsCoincidenceList;
    /**
     * Adaptador para manejar y mostrar en cada celda los partidos que coinciden con esta alarma
     */
    EventsAdapter eventsAdapter;
    /**
     * Referencia al contenedor de la interfaz que se muestra en caso de que no se encuentren
     * partidos que coincidan con esta alarma
     */
    @BindView(R.id.alarm_detail_events_placeholder)
    ConstraintLayout eventsCoincidencePlaceholder;

    /**
     * Constructor sin argumentos
     */
    public DetailAlarmFragment() {
        // Required empty public constructor
    }

    /**
     * Método de instanciación del Fragmento.
     *
     * @param alarmId identificador de la alarma que se muestra
     *
     * @return una nueva instancia de DetailAlarmFragment
     */
    public static DetailAlarmFragment newInstance(@NonNull String alarmId) {
        DetailAlarmFragment fragment = new DetailAlarmFragment();
        Bundle args = new Bundle();
        args.putString(BUNDLE_ALARM_ID, alarmId);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Inicialización del Presentador correspondiente a esta Vista
     *
     * @param savedInstanceState estado del Fragmento guardado en una posible rotación de
     *                           la pantalla, o null.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        mPresenter = new DetailAlarmPresenter(this);
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
        inflater.inflate(R.menu.menu_edit_delete, menu);
    }

    /**
     * Invocado cuando un elemento del menú es pulsado. En este caso se encarga de iniciar el
     * proceso de edición de la alarma instanciando y mostrando el Fragmento correspondiente,
     * o se encarga de iniciar el proceso de borrado de la alarma con la ayuda del Presentador.
     *
     * @param item elemento del menú pulsado
     *
     * @return true si se aceptó la pulsación, false en otro caso
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if (item.getItemId() == R.id.action_edit) {
            if (mAlarmId != null && !TextUtils.isEmpty(mAlarmId)
                    && mSportId != null && !TextUtils.isEmpty(mSportId)) {
                Fragment fragment = NewAlarmFragment.newInstance(mAlarmId, mSportId);
                mFragmentManagementListener.initFragment(fragment, true);
            }
            return true;
        } else if (item.getItemId() == R.id.action_delete) {
            mPresenter.deleteAlarm(getArguments());
            resetBackStack();
            return true;
        }
        return false;
    }

    /**
     * Inicializa y obtiene una referencia a los elementos de la interfaz. Además centra el mapa en
     * la ciudad del usuario, recupera posibles datos del estado anterior del Fragmento e inicializa
     * el Adaptador de la colección de partidos.
     *
     * @param inflater utilizado para inflar el archivo de layout
     * @param container contenedor donde se va a incluir la interfaz o null
     * @param savedInstanceState estado del Fragmento guardado en una posible rotación de
     *                           la pantalla, o null.
     *
     * @return la vista de la interfaz inicializada
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_detail_alarm, container, false);
        ButterKnife.bind(this, root);

        mCoords = null;
        if (savedInstanceState != null && savedInstanceState.containsKey(INSTANCE_COORDS))
            mCoords = savedInstanceState.getParcelable(INSTANCE_COORDS);

        if (getArguments() != null && getArguments().containsKey(BUNDLE_ALARM_ID))
            mAlarmId = getArguments().getString(BUNDLE_ALARM_ID);

        //Need to be MapView, not SupportMapFragment https://stackoverflow.com/a/19354359/4235666
        if (alarmMap != null) alarmMap.onCreate(savedInstanceState);
        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (alarmMap != null) alarmMap.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;

                Utiles.setCoordinatesInMap(getActivityContext(), mMap, mCoords);
            }
        });

        eventsAdapter = new EventsAdapter(null, this, Glide.with(this));
        eventsCoincidenceList.setAdapter(eventsAdapter);
        eventsCoincidenceList.setHasFixedSize(true);
        eventsCoincidenceList.setLayoutManager(new LinearLayoutManager(
                getActivityContext(), LinearLayoutManager.VERTICAL, false));

        return root;
    }

    /**
     * Al finalizar el proceso de creación de la Actividad contenedora, se invoca este método que
     * establece un título para la barra superior y la acción que debe realizar: navegar hacia
     * atrás.
     *
     * @param savedInstanceState estado del Fragmento guardado en una posible rotación de
     *                           la pantalla, o null.
     */
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mFragmentManagementListener.setCurrentDisplayedFragment(getString(R.string.alarm_detail_title), this);
        mNavigationDrawerManagementListener.setToolbarAsUp();
    }

    /**
     * Avisa al mapa de este método del ciclo de vida del Fragmento, y pide al Presentador que
     * recupere los parámetros de la alarma que se va a mostrar.
     */
    @Override
    public void onStart() {
        super.onStart();
        if (alarmMap != null) alarmMap.onStart();
        mPresenter.openAlarm(getLoaderManager(), getArguments());
    }

    /**
     * Avisa al mapa de este método del ciclo de vida del Fragmento
     */
    @Override
    public void onResume() {
        super.onResume();
        if (alarmMap != null) alarmMap.onResume();
    }

    /**
     * Avisa al mapa de este método del ciclo de vida del Fragmento, y borra del Adaptador los
     * posibles partidos que aloje para que no se almacenen en la variable de estado del Fragmento
     */
    @Override
    public void onPause() {
        super.onPause();
        if (alarmMap != null) alarmMap.onPause();
        eventsAdapter.replaceData(null);
    }

    /**
     * Muestra en la interfaz el deporte de la alarma
     *
     * @param sport identificador del deporte
     */
    @Override
    public void showAlarmSport(String sport) {
        if (sport != null) {
            Glide.with(this)
                    .load(Utiles.getSportIconFromResource(sport))
                    .into(imageViewAlarmSport);
            mSportId = sport;
        }

    }

    /**
     * Muestra la dirección de la alarma o la ciudad y centra el mapa sobre ella
     *
     * @param field instalación
     * @param city ciudad
     */
    @Override
    public void showAlarmPlace(Field field, String city) {
        if (field != null) {
            final String fieldId = field.getId();
            this.textViewAlarmPlace.setText(field.getName() + ", " + field.getCity());
            this.textViewAlarmPlace.setClickable(true);
            this.textViewAlarmPlace.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Fragment newFragment = DetailFieldFragment.newInstance(fieldId, true);
                    mFragmentManagementListener.initFragment(newFragment, true);
                }
            });
            mCoords = new LatLng(field.getCoord_latitude(), field.getCoord_longitude());
            Utiles.setCoordinatesInMap(getActivityContext(), mMap, mCoords);
        } else if (city != null && !TextUtils.isEmpty(city)) {
            this.textViewAlarmPlace.setText(city);
            this.textViewAlarmPlaceIcon.setVisibility(View.INVISIBLE);
            mCoords = null;
        }
    }

    /**
     * Muestra en la interfaz el rango de fechas establecido para la alarma. Si la alarma ya no
     * está activa porque la fecha límite pertenece al pasado, muestra un aviso para que el usuario
     * la modifique.
     *
     * @param dateFrom limite inferior del rango de fechas
     * @param dateTo limite superior del rango de fechas
     */
    @Override
    public void showAlarmDate(Long dateFrom, Long dateTo) {
        ((BaseActivity) getActivity()).showContent();

        this.textViewAlarmDateFrom.setText(UtilesTime.millisToDateStringShort(dateFrom));

        if (dateTo != null && dateTo > -1) {
            this.textViewAlarmDateTo.setText(UtilesTime.millisToDateStringShort(dateTo));
            if (dateTo < System.currentTimeMillis()) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                        .setMessage(R.string.old_alarm)
                        .setPositiveButton(android.R.string.ok, null);
                builder.create().show();
            }
        } else
            this.textViewAlarmDateTo.setText(R.string.forever);
    }

    /**
     * Muestra en la interfaz el rango de puesto totales buscados por la alarma
     *
     * @param totalPlayersFrom limite inferior del rango de puestos totales
     * @param totalPlayersTo limite superior del rango de puestos totales
     */
    @Override
    public void showAlarmTotalPlayers(Long totalPlayersFrom, Long totalPlayersTo) {
        ((BaseActivity) getActivity()).showContent();

        if (totalPlayersFrom != null && totalPlayersFrom > -1)
            this.textViewAlarmTotalFrom.setText(String.format(Locale.getDefault(), "%d", totalPlayersFrom));
        else
            this.textViewAlarmTotalFrom.setText(R.string.unspecified);

        if (totalPlayersTo != null && totalPlayersTo > -1)
            this.textViewAlarmTotalTo.setText(String.format(Locale.getDefault(), "%d", totalPlayersTo));
        else
            this.textViewAlarmTotalTo.setText(R.string.unspecified);
    }

    /**
     * Muestra en la interfaz el rango de puestos vacantes buscados por la alarma
     *
     * @param emptyPlayersFrom limite inferior del rango de puestos vacantes
     * @param emptyPlayersTo limite superior del rango de puestos vacantes
     */
    @Override
    public void showAlarmEmptyPlayers(Long emptyPlayersFrom, Long emptyPlayersTo) {
        ((BaseActivity) getActivity()).showContent();

        if (emptyPlayersFrom != null && emptyPlayersFrom > -1)
            this.textViewAlarmEmptyFrom.setText(String.format(Locale.getDefault(), "%d", emptyPlayersFrom));
        else
            this.textViewAlarmEmptyFrom.setText(R.string.unspecified);

        if (emptyPlayersTo != null && emptyPlayersTo > -1)
            this.textViewAlarmEmptyTo.setText(String.format(Locale.getDefault(), "%d", emptyPlayersTo));
        else
            this.textViewAlarmEmptyTo.setText(R.string.unspecified);
    }

    /**
     * Pasa al Adaptador el conjunto de partidos coincidentes con esta alarma encontrados en la
     * base de datos. Si no se encontró ninguno, muestra una imagen explicando que la lista está
     * vacía.
     *
     * @param data conjunto de partidos encontrados en la base de datos
     */
    @Override
    public void showEvents(Cursor data) {
        eventsAdapter.replaceData(data);
        if (data != null && data.getCount() > 0) {
            eventsCoincidenceList.setVisibility(View.VISIBLE);
            eventsCoincidencePlaceholder.setVisibility(View.INVISIBLE);
        } else {
            String myUserId = Utiles.getCurrentUserId();
            if (TextUtils.isEmpty(myUserId))
                NotificationsFirebaseActions.deleteNotification(myUserId, mAlarmId + FirebaseDBContract.User.ALARMS);
            eventsCoincidenceList.setVisibility(View.INVISIBLE);
            eventsCoincidencePlaceholder.setVisibility(View.VISIBLE);
        }
        mFragmentManagementListener.showContent();
    }

    /**
     * Limpia los elementos de la interfaz utilizados para mostrar los datos de la alarma
     */
    @Override
    public void clearUI() {
        this.imageViewAlarmSport.setVisibility(View.INVISIBLE);
        this.textViewAlarmPlace.setText("");
        this.textViewAlarmPlace.setClickable(false);
        this.textViewAlarmPlace.setOnClickListener(null);
        this.mCoords = null;
        this.textViewAlarmDateFrom.setText("");
        this.textViewAlarmDateTo.setText("");
        this.textViewAlarmTotalFrom.setText("");
        this.textViewAlarmTotalTo.setText("");
        this.textViewAlarmEmptyFrom.setText("");
        this.textViewAlarmEmptyTo.setText("");
    }

    /**
     * Instancia y muestra el Fragmento encargado de mostrar los detalles del partido seleccionado
     * de la lista por el usuario.
     *
     * @param eventId Identificador del Partido seleccionado
     */
    @Override
    public void onEventClick(String eventId) {
        Fragment newFragment = DetailEventFragment.newInstance(eventId);
        mFragmentManagementListener.initFragment(newFragment, true);
    }

    /**
     * Alamacena en la variable de estado del Fragmento las coordenadas correspondientes al lugar
     * sobre el que está establecida la alarma.
     *
     * @param outState donde se guarda estado del Fragmento en una posible rotación de la pantalla.
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mCoords != null)
            outState.putParcelable(INSTANCE_COORDS, mCoords);
    }

    /**
     * Avisa al mapa de este método del ciclo de vida del Fragmento
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (alarmMap != null) alarmMap.onDestroy();
    }

    /**
     * Avisa al mapa de este método del ciclo de vida del Fragmento
     */
    @Override
    public void onStop() {
        super.onStop();
        if (alarmMap != null) alarmMap.onStop();
    }

    /**
     * Avisa al mapa de este método del ciclo de vida del Fragmento
     */
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if (alarmMap != null) alarmMap.onLowMemory();
    }
}
