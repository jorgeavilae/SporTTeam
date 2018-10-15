package com.usal.jorgeav.sportapp.events.addevent;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.usal.jorgeav.sportapp.BaseFragment;
import com.usal.jorgeav.sportapp.R;
import com.usal.jorgeav.sportapp.data.Field;
import com.usal.jorgeav.sportapp.data.SimulatedUser;
import com.usal.jorgeav.sportapp.mainactivities.EventsActivity;
import com.usal.jorgeav.sportapp.utils.Utiles;
import com.usal.jorgeav.sportapp.utils.UtilesTime;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Fragmento utilizado para crear o editar partidos.
 * <p>
 * La instancia de este Fragmento debe crearse una vez escogido el deporte. Lo primero que hace es
 * obtener la lista de instalaciones para ese deporte y mostrar la pantalla para especificar en qué
 * instalación o dirección se va a jugar el partido. Esto es así para que, si no hay instalaciones
 * para ese deporte, se muestre un cuadro de diálogo que impida la creación del partido hasta que
 * no se haya creado primero una instalación donde jugar ese deporte.
 * <p>
 * Además de la comprobación y mostrar el cuadro de diálogo, este Fragmento se encarga de
 * inicializar el resto de los componentes de edición de la interfaz para que el usuario pueda
 * introducir los parámetros del partido, entre ellos se encuentran un {@link DatePickerDialog}, un
 * {@link TimePickerDialog} o un {@link GoogleMap} para indicar la dirección seleccionada, entre
 * otros.
 * <p>
 * Implementa la interfaz {@link NewEventContract.View} para la comunicación con esta clase.
 */
public class NewEventFragment extends BaseFragment implements
        NewEventContract.View {
    /**
     * Nombre de la clase
     */
    public static final String TAG = NewEventFragment.class.getSimpleName();

    /**
     * Etiqueta utilizada en la instanciación del Fragmento para indicar el identificador en el
     * caso de que se esté editando un partido
     */
    public static final String BUNDLE_EVENT_ID = "BUNDLE_EVENT_ID";
    /**
     * Etiqueta utilizada en la instanciación del Fragmento para indicar el deporte seleccionado en
     * el paso previo
     */
    public static final String BUNDLE_SPORT_SELECTED_ID = "BUNDLE_SPORT_SELECTED_ID";

    /**
     * Presentador correspondiente a esta Vista
     */
    NewEventContract.Presenter mNewEventPresenter;
    /**
     * Variable estática que es true para indicar que ya se mostraron en la interfaz los parámetros
     * del partido que se está editando, y así evitar sobreescribir los valores nuevos introducidos
     * en el caso de que se produzca una recreación de la pantalla y la consecuente entrega del
     * resultado de la consulta de dichos parámetros del partido.
     */
    private static boolean sInitialize;

    // todo en vez de consultar borrar el loader y guardar fields en instanceState; pq no consultar y recuperar la consulta del loader sin borrarlo? newfieldfragment no lo tiene
    /**
     * Etiqueta utilizada para guardar, en el estado del Fragmento, las instalaciones encontradas
     * en la consulta a la base de datos.
     */
    public static final String INSTANCE_FIELD_LIST_ID = "INSTANCE_FIELD_LIST_ID";
    /**
     * Almacena las instalaciones encontradas en la consulta, que serán sobre las que se pueda
     * establecer el partido.
     */
    ArrayList<Field> mFieldList;
    // todo en vez de consultar borrar el loader y guardar fields en instanceState; pq no consultar y recuperar la consulta del loader sin borrarlo?
    /**
     * Etiqueta utilizada para guardar, en el estado del Fragmento, los amigos encontrados en la
     * consulta a la base de datos.
     */
    public static final String INSTANCE_FRIENDS_LIST_ID = "INSTANCE_FRIENDS_LIST_ID";
    /**
     * Almacena los amigos encontrados en la consulta, que serán a los que se deba notificar de la
     * creación del partido
     */
    ArrayList<String> mFriendsList;
    /**
     * Almacena el deporte seleccionado para el partido
     */
    String mSportId = "";

    /**
     * Referencia al mapa de la interfaz para mostrar la instalación o dirección escogida
     */
    @BindView(R.id.new_event_map)
    MapView newEventMap;
    /**
     * Objeto principal de Google Maps API. Hace referencia al mapa que provee esta API.
     *
     * @see <a href= "https://developers.google.com/android/reference/com/google/android/gms/maps/GoogleMap">
     * GoogleMap</a>
     */
    private GoogleMap mMap;
    /**
     * Referencia a la imagen de la interfaz utilizada para mostrar el icono referente al deporte
     * del partido
     */
    @BindView(R.id.new_event_sport)
    ImageView newEventSport;
    /**
     * Referencia al elemento de la interfaz donde se escribe la dirección del partido
     */
    @BindView(R.id.new_event_address)
    TextView newEventAddress;
    /**
     * Referencia al botón de la interfaz utilizado para viajar al Fragmento donde se muestren los
     * detalles de la instalación si la hay.
     */
    @BindView(R.id.new_event_field_button)
    Button newEventFieldButton;
    /**
     * Referencia al elemento de la interfaz utilizado para especificar el nombre del partido
     */
    @BindView(R.id.new_event_name)
    EditText newEventName;
    /**
     * Referencia al elemento de la interfaz utilizado para especificar el día del partido
     */
    @BindView(R.id.new_event_date)
    EditText newEventDate;
    /**
     * Referencia al elemento de la interfaz utilizado para especificar la fecha del partido
     */
    @BindView(R.id.new_event_time)
    EditText newEventTime;
    /**
     * Referencia al elemento de la interfaz utilizado para especificar la cantidad de puestos
     * totales para el partido
     */
    @BindView(R.id.new_event_total)
    EditText newEventTotal;
    /**
     * Referencia al elemento de la interfaz utilizado para especificar la cantidad de puestos
     * vacantes en el partido
     */
    @BindView(R.id.new_event_empty)
    EditText newEventEmpty;
    /**
     * Referencia al botón de la interfaz utilizado para indicar que no es necesario un número de
     * jugadores, es decir que no hay límite de puestos vacantes. Sólo se muestra en algunos deportes
     */
    @BindView(R.id.new_event_infinite_players)
    CheckBox newEventInfinitePlayers;
    /**
     * Mapa con los participantes en caso de editar el partido. La clave es el identificador de
     * usuario, el valor es true si participa y false si está bloqueado.
     */
    HashMap<String, Boolean> mParticipants;
    /**
     * Mapa con los participantes simulados en caso de editar el partido. La clave es el
     * identificador del usuario simulado, el valor es el objeto que representa un usuario simulado
     */
    HashMap<String, SimulatedUser> mSimulatedParticipants;

    /**
     * Objeto para establecer la fecha y hora preseleccionadas y el límite del calendario mostrados
     * en los diálogos que se utilizan en la selección dela fecha y hora del partido
     */
    Calendar myCalendar;
    /**
     * Diálogo de selección de fecha para el partido
     */
    DatePickerDialog datePickerDialog;
    /**
     * Diálogo de selección de hora para el partido
     */
    TimePickerDialog timePickerDialog;

    /**
     * Constructor sin argumentos
     */
    public NewEventFragment() {
        // Required empty public constructor
    }

    /**
     * Método de instanciación del Fragmento
     *
     * @param eventId identificador del partido en el caso de una edición, null en creaciones
     * @param sportId identificador del deportes escogido para el partido
     * @return una nueva instancia de NewEventFragment
     */
    public static NewEventFragment newInstance(@Nullable String eventId, @Nullable String sportId) {
        NewEventFragment nef = new NewEventFragment();
        Bundle b = new Bundle();
        if (eventId != null)
            b.putString(BUNDLE_EVENT_ID, eventId);
        if (sportId != null)
            b.putString(BUNDLE_SPORT_SELECTED_ID, sportId);
        nef.setArguments(b);
        sInitialize = false;
        return nef;
    }

    /**
     * En este método se inicializa el Presentador correspondiente a esta Vista.
     *
     * @param savedInstanceState estado del Fragmento guardado en una posible rotación de
     *                           la pantalla, o null.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        mNewEventPresenter = new NewEventPresenter(this);
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
        inflater.inflate(R.menu.menu_ok, menu);
    }

    /**
     * Invocado cuando un elemento del menú es pulsado. En este caso se encarga de enviar los
     * datos del proceso de creación o edición al Presentador para que los almacene en la base de
     * datos.
     *
     * @param item elemento del menú pulsado
     * @return true si se aceptó la pulsación, false en otro caso
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        hideSoftKeyboard();
        if (item.getItemId() == R.id.action_ok) {
            String eventId = "";
            if (getArguments() != null && getArguments().containsKey(BUNDLE_EVENT_ID))
                eventId = getArguments().getString(BUNDLE_EVENT_ID);

            mNewEventPresenter.addEvent(
                    eventId,
                    mSportId,
                    ((EventsActivity) getActivity()).mFieldId,
                    ((EventsActivity) getActivity()).mAddress,
                    ((EventsActivity) getActivity()).mCoord,
                    newEventName.getText().toString(),
                    ((EventsActivity) getActivity()).mCity,
                    newEventDate.getText().toString(),
                    newEventTime.getText().toString(),
                    newEventTotal.getText().toString(),
                    newEventEmpty.getText().toString(),
                    mParticipants, mSimulatedParticipants, mFriendsList);
            return true;
        }
        return false;
    }

    /**
     * Inicializa y obtiene una referencia a los elementos de la interfaz con la ayuda de
     * ButterKnife. Además centra el mapa en la ciudad del usuario, recupera posibles datos del
     * estado anterior del Fragmento y establece Listeners para las pulsaciones sobre los diálogos
     * de fecha y hora.
     *
     * @param inflater           utilizado para inflar el archivo de layout
     * @param container          contenedor donde se va a incluir la interfaz o null
     * @param savedInstanceState estado del Fragmento guardado en una posible rotación de
     *                           la pantalla, o null.
     * @return la vista de la interfaz inicializada
     * @see <a href= "http://jakewharton.github.io/butterknife/">ButterKnife</a>
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_new_event, container, false);
        ButterKnife.bind(this, root);

        //Need to be MapView, not SupportMapFragment https://stackoverflow.com/a/19354359/4235666
        newEventMap.onCreate(savedInstanceState);
        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        newEventMap.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;

                // Coordinates selected previously
                Utiles.setCoordinatesInMap(getActivityContext(), mMap, ((EventsActivity) getActivity()).mCoord);
            }
        });

        if (getArguments() != null && getArguments().containsKey(BUNDLE_SPORT_SELECTED_ID))
            setSportLayout(getArguments().getString(BUNDLE_SPORT_SELECTED_ID));

        myCalendar = Calendar.getInstance();

        newEventFieldButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean onlyFields = Utiles.sportNeedsField(mSportId);
                ((EventsActivity) getActivity()).startMapActivityForResult(mFieldList, onlyFields);
            }
        });

        newEventDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePickerDialog = new DatePickerDialog(getActivityContext(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                myCalendar.set(Calendar.YEAR, year);
                                myCalendar.set(Calendar.MONTH, monthOfYear);
                                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                                newEventDate.setText(UtilesTime.millisToDateString(myCalendar.getTimeInMillis()));
                            }
                        },
                        myCalendar.get(Calendar.YEAR),
                        myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
            }
        });

        newEventTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timePickerDialog = new TimePickerDialog(getActivityContext(),
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                                myCalendar.set(Calendar.HOUR_OF_DAY, hour);
                                myCalendar.set(Calendar.MINUTE, minute);
                                newEventTime.setText(String.format(Locale.getDefault(), "%02d:%02d", hour, minute));
                            }
                        },
                        myCalendar
                                .get(Calendar.HOUR_OF_DAY), myCalendar.get(Calendar.MINUTE), true);
                timePickerDialog.show();
            }
        });

        newEventInfinitePlayers.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    newEventEmpty.setEnabled(false);
                    newEventEmpty.setText(R.string.infinite);
                } else {
                    newEventEmpty.setEnabled(true);
                    newEventEmpty.setText("");
                }
            }
        });

        hideContent();

        if (savedInstanceState != null && savedInstanceState.containsKey(INSTANCE_FIELD_LIST_ID))
            mFieldList = savedInstanceState.getParcelableArrayList(INSTANCE_FIELD_LIST_ID);
        if (savedInstanceState != null && savedInstanceState.containsKey(INSTANCE_FRIENDS_LIST_ID))
            mFriendsList = savedInstanceState.getStringArrayList(INSTANCE_FRIENDS_LIST_ID);

        //Show newField dialog on rotation if needed, after retrieveFields are called
        if (mFieldList != null && mFieldList.size() == 0 && Utiles.sportNeedsField(mSportId))
            startNewFieldDialog();

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
        mFragmentManagementListener.setCurrentDisplayedFragment(getString(R.string.action_create_event), this);
        mNavigationDrawerManagementListener.setToolbarAsUp();
    }

    /**
     * Pide al Presentador que recupere los parámetros del partido si es una edición y que recupere
     * las instalaciones y los amigos del usuario actual si no estaban todos esos datos cargados ya,
     * en cuyo caso simplemente muestra la interfaz.
     */
    @Override
    public void onStart() {
        super.onStart();
        newEventMap.onStart();
        if (!sInitialize) {
            mNewEventPresenter.openEvent(getLoaderManager(), getArguments());

            // Only need to show MapActivity on init once, not on rotation
            // Load Fields from ContentProvider and start MapActivity in retrieveFields()
            mNewEventPresenter.loadFields(getLoaderManager(), getArguments());

            // Just load friends once
            mNewEventPresenter.loadFriends(getLoaderManager(), getArguments());

            sInitialize = true;
        } else {
            showContent();
        }
    }

    /**
     * Establece el tipo de interfaz dependiendo de si el deporte acepta un número infinito de
     * jugadores y de si requiere de una pista para ser practicado.
     *
     * @param sportId identificador del deporte
     */
    private void setSportLayout(String sportId) {
        // Show sport
        showEventSport(sportId);

        if (Utiles.sportNeedsField(sportId))
            newEventInfinitePlayers.setVisibility(View.INVISIBLE);
        else
            newEventInfinitePlayers.setVisibility(View.VISIBLE);
    }

    /**
     * Avisa al mapa de este método del ciclo de vida del Fragmento y cancela los diálogos de
     * selección de fecha y hora en caso de que se estuvieran mostrando
     */
    @Override
    public void onPause() {
        super.onPause();
        newEventMap.onPause();
        if (datePickerDialog != null && datePickerDialog.isShowing()) datePickerDialog.dismiss();
        if (timePickerDialog != null && timePickerDialog.isShowing()) timePickerDialog.dismiss();
    }

    /**
     * Muestra en la interfaz el deporte escogido
     *
     * @param sport identificador del deporte
     */
    @Override
    public void showEventSport(String sport) {
        mSportId = sport;
        if (sport != null && !TextUtils.isEmpty(sport))
            Glide.with(this).load(Utiles.getSportIconFromResource(sport)).into(newEventSport);
    }

    /**
     * Muestra en el mapa la dirección escogida, y establece en el cuadro de texto y en el botón
     * la instalación escogida en el caso de que la hubiera.
     *
     * @param fieldId     identificador de la instalación o null
     * @param address     dirección del partido
     * @param city        ciudad del partido
     * @param coordinates coordenadas del partido, correspondientes a la dirección anterior
     */
    @Override
    public void showEventField(String fieldId, String address, String city, LatLng coordinates) {
        if (address != null && !TextUtils.isEmpty(address))
            newEventAddress.setText(address);

        Utiles.setCoordinatesInMap(getActivityContext(), mMap, coordinates);

        ((EventsActivity) getActivity()).mFieldId = fieldId;
        ((EventsActivity) getActivity()).mAddress = address;
        ((EventsActivity) getActivity()).mCity = city;
        ((EventsActivity) getActivity()).mCoord = coordinates;
    }

    /**
     * Muestra en la interfaz el nombre del partido
     *
     * @param name nombre del partido
     */
    @Override
    public void showEventName(String name) {
        if (name != null && !TextUtils.isEmpty(name))
            newEventName.setText(name);
    }

    /**
     * Muestra en la interfaz la fecha y la hora del partido
     *
     * @param date fecha y hora del partido en milisegundos
     */
    @Override
    public void showEventDate(long date) {
        if (date > -1) {
            newEventDate.setText(UtilesTime.millisToDateString(date));
            newEventTime.setText(UtilesTime.millisToTimeString(date));
        }
    }

    /**
     * Muestra en la interfaz el número total de jugadores para el partido
     *
     * @param totalPlayers número de puestos totales en el partido
     */
    @Override
    public void showEventTotalPlayers(int totalPlayers) {
        if (totalPlayers > -1)
            newEventTotal.setText(String.format(Locale.getDefault(), "%d", totalPlayers));
    }

    /**
     * Muestra en la interfaz el número de puestos vacantes para el partido
     *
     * @param emptyPlayers número de puestos vacantes en el partido
     */
    @Override
    public void showEventEmptyPlayers(int emptyPlayers) {
        if (emptyPlayers > -1)
            newEventEmpty.setText(String.format(Locale.getDefault(), "%d", emptyPlayers));
    }

    /**
     * Establece el listado de participantes al partido
     *
     * @param participants mapa de participantes. La clave es el identificador de usuario, el
     *                     valor es un booleano que indica si asiste al partido o está bloqueado
     */
    @Override
    public void setParticipants(HashMap<String, Boolean> participants) {
        mParticipants = participants;
    }

    /**
     * Establece el listado de participantes simulados al partido
     *
     * @param simulatedParticipants mapa de participantes simulados. La clave es el
     *                              identificador del usuario simulado, el valor es el usuario
     *                              simulado.
     */
    @Override
    public void setSimulatedParticipants(HashMap<String, SimulatedUser> simulatedParticipants) {
        mSimulatedParticipants = simulatedParticipants;
    }

    /**
     * Almacena la lista de instalaciones encontradas para el deporte seleccionado. Si la lista
     * está vacía, muestra un diálogo crear una nueva.
     *
     * @param fieldList lista de instalaciones
     */
    @Override
    public void retrieveFields(ArrayList<Field> fieldList) {
        mFieldList = fieldList;
        showContent();
        if (mFieldList != null) {
            if (!getArguments().containsKey(BUNDLE_EVENT_ID)) {// If not an edit
                if (Utiles.sportNeedsField(mSportId)) { // If new Event need a Field
                    if (mFieldList.size() == 0)
                        // Sport needs a Field so create a new one or cancel Event creation
                        startNewFieldDialog();
                    else
                        ((EventsActivity) getActivity()).startMapActivityForResult(mFieldList, true);
                } else
                    ((EventsActivity) getActivity()).startMapActivityForResult(mFieldList, false);
            }
        }

        //Since mFieldList are going to be retained in savedInstance there isn't need to be loaded again
        mNewEventPresenter.stopLoadFields(getLoaderManager());
    }

    /**
     * Almacena la lista de amigos encontrados.
     *
     * @param friendsIdList lista de amigos
     */
    @Override
    public void retrieveFriendsID(ArrayList<String> friendsIdList) {
        mFriendsList = friendsIdList;

        //Since mFieldList are going to be retained in savedInstance there isn't need to be loaded again
        mNewEventPresenter.stopLoadFriends(getLoaderManager());
    }

    /**
     * Muestra en pantalla un diálogo para ofrecer al usuario crear una pista nueva. El diálogo
     * puede cancelarse pero mostrará la pantalla anterior. En caso de aceptar, se cancela el
     * proceso de creación de partido y se inicia un proceso de creación de instalación en otro
     * Fragmento
     */
    private void startNewFieldDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivityContext());
        builder.setTitle(R.string.dialog_title_create_new_field)
                .setMessage(R.string.dialog_msg_create_new_field_for_event)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Utiles.startFieldsActivityAndNewField(getActivity());
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        getActivity().onBackPressed();
                    }
                })
                .setCancelable(false);
        builder.create().show();
    }

    /**
     * Limpia los elementos de la interfaz utilizados para mostrar los datos del partido
     */
    @Override
    public void clearUI() {
        newEventSport.setVisibility(View.INVISIBLE);
        ((EventsActivity) getActivity()).mFieldId = null;
        ((EventsActivity) getActivity()).mAddress = null;
        ((EventsActivity) getActivity()).mCity = null;
        ((EventsActivity) getActivity()).mCoord = null;
        newEventName.setText("");
        newEventDate.setText("");
        newEventTime.setText("");
        newEventAddress.setText("");
        newEventTotal.setText("");
        newEventEmpty.setText("");
    }

    /**
     * Guarda en el estado del Fragmento las instalaciones y los amigos consultados anteriormente
     *
     * @param outState donde se guarda estado del Fragmento en una posible rotación de la pantalla.
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mFieldList != null)
            outState.putParcelableArrayList(INSTANCE_FIELD_LIST_ID, mFieldList);
        if (mFriendsList != null)
            outState.putStringArrayList(INSTANCE_FRIENDS_LIST_ID, mFriendsList);
    }

    /**
     * Restablece a null la dirección previamente seleccionada puesto que se va a desvincular el
     * Fragmento
     */
    @Override
    public void onDetach() {
        super.onDetach();
        // If user go back and pick other sport, need to clear this variables
        ((EventsActivity) getActivity()).mFieldId = null;
        ((EventsActivity) getActivity()).mAddress = null;
        ((EventsActivity) getActivity()).mCity = null;
        ((EventsActivity) getActivity()).mCoord = null;
    }

    /**
     * Avisa al mapa de este método del ciclo de vida del Fragmento
     */
    @Override
    public void onResume() {
        super.onResume();
        newEventMap.onResume();
    }

    /**
     * Avisa al mapa de este método del ciclo de vida del Fragmento
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        newEventMap.onDestroy();
    }

    /**
     * Avisa al mapa de este método del ciclo de vida del Fragmento
     */
    @Override
    public void onStop() {
        super.onStop();
        newEventMap.onStop();
    }

    /**
     * Avisa al mapa de este método del ciclo de vida del Fragmento
     */
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        newEventMap.onLowMemory();
    }
}
