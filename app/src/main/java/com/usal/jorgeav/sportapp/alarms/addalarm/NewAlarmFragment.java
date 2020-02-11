package com.usal.jorgeav.sportapp.alarms.addalarm;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.usal.jorgeav.sportapp.BaseFragment;
import com.usal.jorgeav.sportapp.R;
import com.usal.jorgeav.sportapp.data.Field;
import com.usal.jorgeav.sportapp.mainactivities.AlarmsActivity;
import com.usal.jorgeav.sportapp.utils.Utiles;
import com.usal.jorgeav.sportapp.utils.UtilesContentProvider;
import com.usal.jorgeav.sportapp.utils.UtilesPreferences;
import com.usal.jorgeav.sportapp.utils.UtilesTime;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Fragmento utilizado para crear o editar alarmas. Se encarga de inicializar los componentes de
 * edición de la interfaz para que el usuario pueda introducir los parámetros de la alarma, entre
 * los que se encuentran dos {@link DatePickerDialog}, un {@link GoogleMap} para indicar la
 * instalación o la ciudad seleccionada y un {@link AutoCompleteTextView} para escribir la ciudad.
 * Implementa la interfaz {@link NewAlarmContract.View} para la comunicación con esta clase.
 */
public class NewAlarmFragment extends BaseFragment implements
        NewAlarmContract.View {
    /**
     * Nombre de la clase
     */
    private static final String TAG = NewAlarmFragment.class.getSimpleName();

    /**
     * Etiqueta utilizada en la instanciación del Fragmento para indicar el identificador en el
     * caso de que se esté editando una alarma
     */
    public static final String BUNDLE_ALARM_ID = "BUNDLE_ALARM_ID";
    /**
     * Etiqueta utilizada en la instanciación del Fragmento para indicar el deporte seleccionado
     * para la alarma en un Fragmento previo.
     */
    public static final String BUNDLE_SPORT_SELECTED_ID = "BUNDLE_SPORT_SELECTED_ID";

    /**
     * Etiqueta utilizada para guardar, en el estado del Fragmento, las instalaciones encontradas
     * en la consulta a la base de datos.
     */
    public static final String INSTANCE_FIELD_LIST_ID = "INSTANCE_FIELD_LIST_ID";

    /**
     * Almacena las instalaciones encontradas en la consulta, que serán sobre las que se pueda
     * establecer la alarma
     */
    ArrayList<Field> mFieldList;
    /**
     * Almacena el deporte seleccionado para la alarma
     */
    String mSportId = "";

    /**
     * Presentador correspondiente a esta Vista
     */
    NewAlarmContract.Presenter mNewAlarmPresenter;
    /**
     * Variable booleana para indicar que los datos de la alarma, en una edición, ya han sido
     * consultados al Proveedor de Contenido
     */
    private static boolean sInitialize;

    /**
     * Referencia al mapa de la interfaz para mostrar la instalación o ciudad escogida
     */
    @BindView(R.id.new_alarm_map)
    MapView newAlarmMap;
    /**
     * Objeto principal de Google Maps API. Hace referencia al mapa que provee esta API.
     *
     * @see <a href= "https://developers.google.com/android/reference/com/google/android/gms/maps/GoogleMap">
     * GoogleMap</a>
     */
    private GoogleMap mMap;
    /**
     * Marca situada sobre el mapa para indicar la dirección exacta de una instalación
     */
    private Marker mMarker;
    /**
     * Referencia al elemento de la interfaz para indicar el deporte
     */
    @BindView(R.id.new_alarm_sport)
    ImageView newAlarmSport;
    /**
     * Referencia al texto de la interfaz para indicar la instalación
     */
    @BindView(R.id.new_alarm_field)
    TextView newAlarmField;
    /**
     * Referencia al botón de la interfaz para modificar la instalación
     */
    @BindView(R.id.new_alarm_field_button)
    Button newAlarmFieldButton;
    /**
     * Referencia al elemento de la interfaz para indicar el limite inferior del rango de fechas
     */
    @BindView(R.id.new_alarm_date_from)
    EditText newAlarmDateFrom;
    /**
     * Referencia al elemento de la interfaz para indicar el limite superior del rango de fechas
     */
    @BindView(R.id.new_alarm_date_to)
    EditText newAlarmDateTo;
    /**
     * Referencia al elemento de la interfaz para indicar el limite inferior del rango de puestos
     * totales
     */
    @BindView(R.id.new_alarm_total_from)
    EditText newAlarmTotalFrom;
    /**
     * Referencia al elemento de la interfaz para indicar el limite superior del rango de puestos
     * totales
     */
    @BindView(R.id.new_alarm_total_to)
    EditText newAlarmTotalTo;
    /**
     * Referencia al elemento de la interfaz para indicar el limite inferior del rango de puestos
     * vacantes
     */
    @BindView(R.id.new_alarm_empty_from)
    EditText newAlarmEmptyFrom;
    /**
     * Referencia al elemento de la interfaz para indicar el limite superior del rango de puestos
     * vacantes
     */
    @BindView(R.id.new_alarm_empty_to)
    EditText newAlarmEmptyTo;
    /**
     * Referencia al elemento de la interfaz para indicar un número indeterminado de puestos vacantes
     */
    @BindView(R.id.new_alarm_infinite_players)
    CheckBox newAlarmInfinitePlayers;

    /**
     * Objeto para establecer las fechas preseleccionadas y los límites de los calendarios mostrados
     * en los diálogos que se utilizan en la selección del rango de fechas de la alarma
     */
    Calendar myCalendar;
    /**
     * Diálogo de selección de fecha utilizado en la selección de la rango límite inferior
     */
    DatePickerDialog datePickerDialogFrom;
    /**
     * Diálogo de selección de fecha utilizado en la selección de la rango límite superior
     */
    DatePickerDialog datePickerDialogTo;

    /**
     * Constructor sin argumentos
     */
    public NewAlarmFragment() {
        // Required empty public constructor
    }

    /**
     * Método de instanciación del Fragmento. Puede incluirse un identificador de alarma en el
     * caso de la edición, o un deporte.
     *
     * @param alarmId identificador de alarma
     * @param sportId identificador de deporte
     * @return una nueva instancia de NewAlarmFragment
     */
    public static NewAlarmFragment newInstance(@Nullable String alarmId, @Nullable String sportId) {
        NewAlarmFragment naf = new NewAlarmFragment();
        Bundle b = new Bundle();
        if (alarmId != null)
            b.putString(BUNDLE_ALARM_ID, alarmId);
        if (sportId != null)
            b.putString(BUNDLE_SPORT_SELECTED_ID, sportId);
        naf.setArguments(b);
        sInitialize = false;
        return naf;
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

        mNewAlarmPresenter = new NewAlarmPresenter(this);
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
     * datos del proceso de creación al Presentador para que los almacene en la base de datos.
     *
     * @param item elemento del menú pulsado
     * @return true si se aceptó la pulsación, false en otro caso
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        hideSoftKeyboard();
        if (item.getItemId() == R.id.action_ok) {
            String alarmId = "";
            if (getArguments() != null && getArguments().containsKey(BUNDLE_ALARM_ID))
                alarmId = getArguments().getString(BUNDLE_ALARM_ID);

            mNewAlarmPresenter.addAlarm(
                    alarmId,
                    mSportId,
                    ((AlarmsActivity) getActivity()).mFieldId,
                    ((AlarmsActivity) getActivity()).mCity,
                    ((AlarmsActivity) getActivity()).mCoord,
                    newAlarmDateFrom.getText().toString(),
                    newAlarmDateTo.getText().toString(),
                    newAlarmTotalFrom.getText().toString(),
                    newAlarmTotalTo.getText().toString(),
                    newAlarmEmptyFrom.getText().toString(),
                    newAlarmEmptyTo.getText().toString());
            return true;
        }
        return false;
    }

    /**
     * Inicializa y obtiene una referencia a los elementos de la interfaz con la ayuda de
     * ButterKnife. Además centra el mapa en la ciudad del usuario, recupera posibles datos del
     * estado anterior del Fragmento, establece Listeners para las pulsaciones sobre los elementos
     * de la interfaz y establece los limites de fechas en los {@link DatePickerDialog}
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
        View root = inflater.inflate(R.layout.fragment_new_alarm, container, false);
        ButterKnife.bind(this, root);

        //Need to be MapView, not SupportMapFragment https://stackoverflow.com/a/19354359/4235666
        newAlarmMap.onCreate(savedInstanceState);
        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        newAlarmMap.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;

                // Coordinates selected previously
                boolean coordsAreCity = ((AlarmsActivity) getActivity()).mFieldId == null;
                if (mMarker != null) mMarker.remove();
                mMarker = Utiles.setCoordinatesInMap(getActivityContext(), mMap, ((AlarmsActivity) getActivity()).mCoord, coordsAreCity);
            }
        });

        if (savedInstanceState != null && savedInstanceState.containsKey(INSTANCE_FIELD_LIST_ID))
            mFieldList = savedInstanceState.getParcelableArrayList(INSTANCE_FIELD_LIST_ID);
        if (getArguments() != null && getArguments().containsKey(BUNDLE_SPORT_SELECTED_ID))
            setSportLayout(getArguments().getString(BUNDLE_SPORT_SELECTED_ID));
        if (getArguments() != null && getArguments().containsKey(BUNDLE_ALARM_ID))
            newAlarmSport.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle(R.string.pick_sport)
                            .setItems(R.array.sport_id_entries, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int position) {
                                    String[] sportIDs = getResources().getStringArray(R.array.sport_id_values);
                                    setSportLayout(sportIDs[position]);
                                    ((AlarmsActivity) getActivity()).mFieldId = null;
                                    ((AlarmsActivity) getActivity()).mCity = null;
                                    ((AlarmsActivity) getActivity()).mCoord = null;
                                    showAlarmField(null, null, null);
                                }
                            });
                    builder.create().show();
                }
            });

        newAlarmFieldButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mFieldList != null)
                    ((AlarmsActivity) getActivity()).startMapActivityForResult(mFieldList);
            }
        });

        myCalendar = Calendar.getInstance();
        newAlarmDateFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePickerDialogFrom = new DatePickerDialog(
                        getActivityContext(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                myCalendar.set(Calendar.YEAR, year);
                                myCalendar.set(Calendar.MONTH, monthOfYear);
                                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                                newAlarmDateFrom.setText(UtilesTime.millisToDateString(myCalendar.getTimeInMillis()));
                                newAlarmDateTo.setText("");
                            }
                        },
                        myCalendar.get(Calendar.YEAR),
                        myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH));

                datePickerDialogFrom.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                datePickerDialogFrom.setCanceledOnTouchOutside(true);
                datePickerDialogFrom.setButton(DialogInterface.BUTTON_NEUTRAL, getString(R.string.delete), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        myCalendar.setTimeInMillis(System.currentTimeMillis());
                        newAlarmDateFrom.setText("");
                        newAlarmDateTo.setText("");
                    }
                });
                datePickerDialogFrom.show();
            }
        });

        newAlarmDateTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePickerDialogTo = new DatePickerDialog(
                        getActivityContext(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                Calendar c = Calendar.getInstance();
                                c.set(Calendar.YEAR, year);
                                c.set(Calendar.MONTH, monthOfYear);
                                c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                                newAlarmDateTo.setText(UtilesTime.millisToDateString(c.getTimeInMillis()));
                            }
                        },
                        myCalendar.get(Calendar.YEAR),
                        myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH));

                datePickerDialogTo.getDatePicker().setMinDate(myCalendar.getTimeInMillis() + 1000 * 60 * 60 * 24);
                datePickerDialogTo.setCanceledOnTouchOutside(true);
                datePickerDialogTo.setButton(DialogInterface.BUTTON_NEUTRAL, getString(R.string.delete), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (!TextUtils.isEmpty(newAlarmDateTo.getText())) // myCalendar has been set
                            myCalendar.setTimeInMillis(datePickerDialogTo.getDatePicker().getMinDate() - 1000 * 60 * 60 * 24);
                        newAlarmDateTo.setText("");
                    }
                });
                datePickerDialogTo.show();
            }
        });

        newAlarmInfinitePlayers.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    newAlarmEmptyFrom.setEnabled(false);
                    newAlarmEmptyFrom.setText(R.string.infinite);
                    newAlarmEmptyTo.setEnabled(false);
                    newAlarmEmptyTo.setText(R.string.infinite);
                } else {
                    newAlarmEmptyFrom.setEnabled(true);
                    newAlarmEmptyFrom.setText("");
                    newAlarmEmptyTo.setEnabled(true);
                    newAlarmEmptyTo.setText("");
                }
            }
        });

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
        mFragmentManagementListener.setCurrentDisplayedFragment(getString(R.string.new_alarm_title), this);
        mNavigationDrawerManagementListener.setToolbarAsUp();
    }

    /**
     * Muestra el contenido de la interfaz y sitúa el mapa. Además, si no están cargados ya, pide al
     * Presentador que recupere los parámetros de la alarma que se va a modificar.
     */
    @Override
    public void onStart() {
        super.onStart();
        newAlarmMap.onStart();

        if (!sInitialize) {
            if (getArguments() != null && getArguments().containsKey(BUNDLE_SPORT_SELECTED_ID))
                setSportLayout(getArguments().getString(BUNDLE_SPORT_SELECTED_ID));

            mNewAlarmPresenter.openAlarm(getLoaderManager(), getArguments());

            sInitialize = true;
        } else {
            mNewAlarmPresenter.destroyOpenAlarmLoader(getLoaderManager());
            showContent();
        }
    }

    /**
     * Establece el tipo de interfaz dependiendo de si el deporte acepta un número infinito de
     * jugadores e inicia la carga de instalaciones.
     *
     * @param sportId identificador del deporte
     */
    private void setSportLayout(String sportId) {
        //Set sport selected
        showAlarmSport(sportId);

        // Check if the sport doesn't need a field
        if (Utiles.sportNeedsField(sportId))
            newAlarmInfinitePlayers.setVisibility(View.INVISIBLE);
        else
            newAlarmInfinitePlayers.setVisibility(View.VISIBLE);

        // Sport needs a Field so load from ContentProvider and store it in retrieveFields()
        Bundle b = new Bundle();
        b.putString(BUNDLE_SPORT_SELECTED_ID, sportId);
        mNewAlarmPresenter.loadFields(getLoaderManager(), b);
    }

    /**
     * Almacena la lista de instalaciones encontradas para el deporte seleccionado. Si la lista
     * está vacía y el deporte la necesita, muestra un diálogo para crear una nueva.
     *
     * @param dataList lista de instalaciones
     */
    @Override
    public void retrieveFields(ArrayList<Field> dataList) {
        mFieldList = dataList;
        showContent();
        if (mFieldList != null && mFieldList.size() == 0)
            if (getArguments() != null && getArguments().containsKey(BUNDLE_SPORT_SELECTED_ID)) {
                String sportId = getArguments().getString(BUNDLE_SPORT_SELECTED_ID);
                if (Utiles.sportNeedsField(sportId))
                    startNewFieldDialog();
            }

        //Since mFieldList are retained in savedInstance no need to load again
        mNewAlarmPresenter.stopLoadFields(getLoaderManager());
    }

    /**
     * Muestra en pantalla un diálogo para ofrecer al usuario crear una pista nueva. El diálogo
     * puede cancelarse, pero en caso de aceptar, se cancela el proceso de creación de alarma y se
     * inicia un proceso de creación de instalación en otro Fragmento
     */
    private void startNewFieldDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivityContext());
        builder.setTitle(R.string.dialog_title_create_new_field)
                .setMessage(R.string.dialog_msg_create_new_field_for_alarm)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Utiles.startFieldsActivityAndNewField(getActivity());
                    }
                })
                //No need to go back since an alarm can be created without a field
                .setNegativeButton(android.R.string.no, null);
        builder.create().show();
    }

    /**
     * Avisa al mapa de este método del ciclo de vida del Fragmento y ordena mostrar el contenido
     * de la interfaz, en lugar de una barra de carga
     */
    @Override
    public void onResume() {
        super.onResume();
        newAlarmMap.onResume();
        mFragmentManagementListener.showContent();
    }

    /**
     * Avisa al mapa de este método del ciclo de vida del Fragmento y cancela los diálogos de
     * selección de fecha en caso de que se estuvieran mostrando
     */
    @Override
    public void onPause() {
        super.onPause();
        newAlarmMap.onPause();
        if (datePickerDialogFrom != null && datePickerDialogFrom.isShowing())
            datePickerDialogFrom.dismiss();
        if (datePickerDialogTo != null && datePickerDialogTo.isShowing())
            datePickerDialogTo.dismiss();
    }

    /**
     * Muestra el deporte escogido en la interfaz
     *
     * @param sport identificador del deporte
     */
    @Override
    public void showAlarmSport(String sport) {
        if (sport != null && !TextUtils.isEmpty(sport)) {
            mSportId = sport;
            int sportResource = Utiles.getSportIconFromResource(mSportId);
            Glide.with(this).load(sportResource).into(newAlarmSport);
        }
    }

    /**
     * Muestra la instalación o la ciudad escogida en el mapa
     *
     * @param fieldId identificador de la instalación
     * @param city    ciudad
     * @param coords  coordenadas del lugar
     */
    @Override
    public void showAlarmField(String fieldId, String city, LatLng coords) {
        if (fieldId != null && !TextUtils.isEmpty(fieldId) && getActivity() instanceof AlarmsActivity) {
            // Set as field
            Field f = UtilesContentProvider.getFieldFromContentProvider(fieldId);
            if (f != null) {
                newAlarmField.setText(String.format("%s, %s", f.getName(), f.getCity()));

                LatLng fCoords = new LatLng(f.getCoord_latitude(), f.getCoord_longitude());
                if (mMarker != null) mMarker.remove();
                mMarker = Utiles.setCoordinatesInMap(getActivityContext(), mMap, fCoords, false);

                ((AlarmsActivity) getActivityContext()).mFieldId = fieldId;
                ((AlarmsActivity) getActivityContext()).mCity = f.getCity();
                ((AlarmsActivity) getActivityContext()).mCoord = fCoords;
            }
        } else if (city != null && !TextUtils.isEmpty(city) && getActivity() instanceof AlarmsActivity) {
            // Set as city
            newAlarmField.setText(city);

            if (mMarker != null) mMarker.remove();
            mMarker = Utiles.setCoordinatesInMap(getActivityContext(), mMap, coords, true);
            if (mMarker != null) mMarker.remove();

            ((AlarmsActivity) getActivityContext()).mFieldId = null;
            ((AlarmsActivity) getActivityContext()).mCity = city;
            ((AlarmsActivity) getActivityContext()).mCoord = coords;
        } else {
            String prefCity = UtilesPreferences.getCurrentUserCity(getActivityContext());
            LatLng prefCoords = UtilesPreferences.getCurrentUserCityCoords(getActivityContext());
            newAlarmField.setText(prefCity);

            if (mMarker != null) mMarker.remove();
            mMarker = Utiles.setCoordinatesInMap(getActivityContext(), mMap, prefCoords, true);
            if (mMarker != null) mMarker.remove();

            ((AlarmsActivity) getActivityContext()).mFieldId = null;
            ((AlarmsActivity) getActivityContext()).mCity = prefCity;
            ((AlarmsActivity) getActivityContext()).mCoord = prefCoords;
        }
    }

    /**
     * Muestra en la interfaz las fechas escogidas para la alarma
     *
     * @param dateFrom limite inferior del rango de fechas
     * @param dateTo   limite superior del rango de fechas
     */
    @Override
    public void showAlarmDate(Long dateFrom, Long dateTo) {
        if (dateFrom != null && dateFrom > 0)
            newAlarmDateFrom.setText(UtilesTime.millisToDateString(dateFrom));

        if (dateTo != null && dateTo > 0)
            newAlarmDateTo.setText(UtilesTime.millisToDateString(dateTo));
    }

    /**
     * Muestra en la interfaz el rango de puestos totales buscados en la alarma
     *
     * @param totalPlayersFrom limite inferior del rango de puestos totales
     * @param totalPlayersTo   limite superior del rango de puestos totales
     */
    @Override
    public void showAlarmTotalPlayers(Long totalPlayersFrom, Long totalPlayersTo) {
        if (totalPlayersFrom != null && totalPlayersFrom > -1)
            newAlarmTotalFrom.setText(String.format(Locale.getDefault(), "%d", totalPlayersFrom));

        if (totalPlayersTo != null && totalPlayersTo > -1)
            newAlarmTotalTo.setText(String.format(Locale.getDefault(), "%d", totalPlayersTo));
    }

    /**
     * Muestra en la interfaz el rango de puestos vacantes buscados en la alarma
     *
     * @param emptyPlayersFrom limite inferior del rango de puestos vacantes
     * @param emptyPlayersTo   limite superior del rango de puestos vacantes
     */
    @Override
    public void showAlarmEmptyPlayers(Long emptyPlayersFrom, Long emptyPlayersTo) {
        if (emptyPlayersFrom != null && emptyPlayersFrom > -1)
            newAlarmEmptyFrom.setText(String.format(Locale.getDefault(), "%d", emptyPlayersFrom));

        if (emptyPlayersTo != null && emptyPlayersTo > -1)
            newAlarmEmptyTo.setText(String.format(Locale.getDefault(), "%d", emptyPlayersTo));
    }

    /**
     * Limpia los elementos de la interfaz utilizados para mostrar los datos de la alarma
     */
    @Override
    public void clearUI() {
        mSportId = "";
        ((AlarmsActivity) getActivity()).mFieldId = null;
        ((AlarmsActivity) getActivity()).mCity = null;
        ((AlarmsActivity) getActivity()).mCoord = null;
        newAlarmField.setText("");
        newAlarmDateFrom.setText("");
        newAlarmDateTo.setText("");
        newAlarmTotalFrom.setText("");
        newAlarmTotalTo.setText("");
        newAlarmEmptyFrom.setText("");
        newAlarmEmptyTo.setText("");
    }

    /**
     * Indica a la Actividad contenedora que elimine el lugar seleccionado porque este Fragmento va
     * a desvincularse de ella. Posiblemente porque el usuario navega hacia atrás para seleccionar
     * otro deporte.
     */
    @Override
    public void onDetach() {
        super.onDetach();
        // If user go back and pick other sport, we need to clear this variables
        ((AlarmsActivity) getActivity()).mFieldId = null;
        ((AlarmsActivity) getActivity()).mCity = null;
        ((AlarmsActivity) getActivity()).mCoord = null;
    }

    /**
     * Guarda el deporte seleccionado y las instalaciones para ese deporte en el estado del Fragmento
     *
     * @param outState donde se guarda estado del Fragmento en una posible rotación de la pantalla.
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mFieldList != null)
            outState.putParcelableArrayList(INSTANCE_FIELD_LIST_ID, mFieldList);
    }

    /**
     * Avisa al mapa de este método del ciclo de vida del Fragmento
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        newAlarmMap.onDestroy();
    }

    /**
     * Avisa al mapa de este método del ciclo de vida del Fragmento
     */
    @Override
    public void onStop() {
        super.onStop();
        newAlarmMap.onStop();
    }

    /**
     * Avisa al mapa de este método del ciclo de vida del Fragmento
     */
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        newAlarmMap.onLowMemory();
    }
}
