package com.usal.jorgeav.sportapp.fields.addfield;

import android.annotation.SuppressLint;
import android.app.TimePickerDialog;
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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.usal.jorgeav.sportapp.BaseFragment;
import com.usal.jorgeav.sportapp.R;
import com.usal.jorgeav.sportapp.data.Field;
import com.usal.jorgeav.sportapp.data.SportCourt;
import com.usal.jorgeav.sportapp.mainactivities.FieldsActivity;
import com.usal.jorgeav.sportapp.utils.Utiles;
import com.usal.jorgeav.sportapp.utils.UtilesTime;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Fragmento utilizado para crear o editar instalaciones.
 * <p>
 * La instancia de este Fragmento se crea una vez escogidos los deportes (almacenados en la
 * Actividad contenedora). Este Fragmento se encarga de inicializar los componentes de edición de
 * la interfaz para que el usuario pueda introducir los parámetros de la instalación, entre ellos
 * se encuentran dos {@link TimePickerDialog}s y un {@link GoogleMap} para indicar la dirección
 * seleccionada, entre otros.
 * <p>
 * Implementa la interfaz {@link NewFieldContract.View} para la comunicación con esta clase.
 */
public class NewFieldFragment extends BaseFragment implements
        NewFieldContract.View {
    /**
     * Nombre de la clase
     */
    public static final String TAG = NewFieldFragment.class.getSimpleName();

    /**
     * Etiqueta utilizada en la instanciación del Fragmento para indicar el identificador en el
     * caso de que se esté editando una instalación
     */
    public static final String BUNDLE_FIELD_ID = "BUNDLE_FIELD_ID";

    /**
     * Presentador correspondiente a esta Vista
     */
    NewFieldContract.Presenter mNewFieldPresenter;
    /**
     * Variable estática que es true para indicar que ya se mostraron en la interfaz los parámetros
     * de la instalación que se está editando, y así evitar sobreescribir los valores nuevos
     * introducidos en el caso de que se produzca una recreación de la pantalla y la consecuente
     * entrega del resultado de la consulta otra vez.
     */
    private static boolean sInitialize;

    /**
     * Almacena las instalaciones encontradas en la consulta, necesarias para mostrarlas en el mapa
     * de selección de sitio en el caso de que el usuario desee cambiar la dirección.
     */
    private ArrayList<Field> mFieldList;

    /**
     * Referencia al mapa de la interfaz para mostrar la dirección escogida
     */
    @BindView(R.id.new_field_map)
    MapView newFieldMap;
    /**
     * Objeto principal de Google Maps API. Hace referencia al mapa que provee esta API.
     *
     * @see <a href= "https://developers.google.com/android/reference/com/google/android/gms/maps/GoogleMap">
     * GoogleMap</a>
     */
    private GoogleMap mMap;
    /**
     * Referencia al elemento de la interfaz utilizado para mostrar la dirección postal escogida para
     * la instalación
     */
    @BindView(R.id.new_field_address)
    TextView newFieldAddress;
    /**
     * Referencia al botón de la interfaz utilizado para escoger otra dirección diferente para la
     * instalación
     */
    @BindView(R.id.new_field_map_button)
    Button newFieldMapButton;
    /**
     * Referencia al elemento de la interfaz utilizado para escribir el nombre de la instalación
     */
    @BindView(R.id.new_field_name)
    EditText newFieldName;
    /**
     * Referencia al elemento de la interfaz utilizado para establecer la hora de apertura de la
     * instalación
     */
    @BindView(R.id.new_field_open_time)
    EditText newFieldOpenTime;
    /**
     * Referencia al elemento de la interfaz utilizado para establecer la hora de cierre de la
     * instalación
     */
    @BindView(R.id.new_field_close_time)
    EditText newFieldCloseTime;
    /**
     * Referencia a la casilla de la interfaz que al marcarse indica que la instalación permanece
     * abierta las 24 horas del dia
     */
    @BindView(R.id.new_field_all_day_time)
    CheckBox newFieldAllDayTime;
    /**
     * Lista de {@link SportCourt} representando las pistas que la instalación pueda tener en el
     * momento de la edición, incluyendo sus puntuaciones y su cantidad de votos
     */
    List<SportCourt> mSports;

    /**
     * Objeto para establecer las horas preseleccionadas en los diálogos que se utilizan en la
     * selección de horas de apertura y cierre de la instalación
     */
    Calendar myCalendar;
    /**
     * Diálogo de selección de hora de apertura de la instalación
     */
    TimePickerDialog openTimePickerDialog;
    /**
     * Diálogo de selección de hora de cierre de la instalación
     */
    TimePickerDialog closeTimePickerDialog;

    /**
     * Constructor sin argumentos
     */
    public NewFieldFragment() {
        // Required empty public constructor
    }

    /**
     * Método de instanciación del Fragmento
     *
     * @param fieldId identificador de la instalación que se va a editar o null si es una creación
     * @return una nueva instancia de NewFieldFragment
     */
    public static NewFieldFragment newInstance(@Nullable String fieldId) {
        NewFieldFragment nff = new NewFieldFragment();
        Bundle b = new Bundle();
        if (fieldId != null)
            b.putString(BUNDLE_FIELD_ID, fieldId);
        nff.setArguments(b);
        sInitialize = false;
        return nff;
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

        mNewFieldPresenter = new NewFieldPresenter(this);
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
    @SuppressLint("SetTextI18n")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        hideSoftKeyboard();
        if (item.getItemId() == R.id.action_ok) {
            String fieldId = "";
            if (getArguments() != null && getArguments().containsKey(BUNDLE_FIELD_ID))
                fieldId = getArguments().getString(BUNDLE_FIELD_ID);

            if (newFieldAllDayTime.isChecked()) {
                newFieldOpenTime.setText("00:00");
                newFieldCloseTime.setText("00:00");
            }
            mNewFieldPresenter.addField(
                    fieldId,
                    newFieldName.getText().toString(),
                    ((FieldsActivity) getActivity()).mAddress,
                    ((FieldsActivity) getActivity()).mCoord,
                    ((FieldsActivity) getActivity()).mCity,
                    newFieldOpenTime.getText().toString(),
                    newFieldCloseTime.getText().toString(),
                    mSports);
            return true;
        }
        return false;
    }

    /**
     * Inicializa y obtiene una referencia a los elementos de la interfaz con la ayuda de
     * ButterKnife. Además centra el mapa en la ciudad del usuario y establece Listeners para
     * mostrar los diálogos de horas de apertura y cierre.
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
        View root = inflater.inflate(R.layout.fragment_new_field, container, false);
        ButterKnife.bind(this, root);

        //Need to be MapView, not SupportMapFragment https://stackoverflow.com/a/19354359/4235666
        newFieldMap.onCreate(savedInstanceState);
        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        newFieldMap.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;

                // Coordinates selected previously
                Utiles.setCoordinatesInMap(getActivityContext(), mMap, ((FieldsActivity) getActivity()).mCoord);
            }
        });

        newFieldMapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mFieldList != null) // Only if all Fields from city are loaded
                    ((FieldsActivity) getActivity()).startMapActivityForResult(mFieldList, false);
            }
        });

        myCalendar = Calendar.getInstance();

        newFieldOpenTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openTimePickerDialog = new TimePickerDialog(getActivityContext(),
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                                myCalendar.set(Calendar.HOUR_OF_DAY, hour);
                                myCalendar.set(Calendar.MINUTE, minute);
                                newFieldOpenTime.setText(String.format(Locale.getDefault(), "%02d:%02d", hour, minute));
                            }
                        },
                        myCalendar.get(Calendar.HOUR_OF_DAY), myCalendar.get(Calendar.MINUTE), true);
                openTimePickerDialog.show();
            }
        });

        newFieldCloseTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeTimePickerDialog = new TimePickerDialog(getActivityContext(),
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                                myCalendar.set(Calendar.HOUR_OF_DAY, hour);
                                myCalendar.set(Calendar.MINUTE, minute);
                                newFieldCloseTime.setText(String.format(Locale.getDefault(), "%02d:%02d", hour, minute));
                            }
                        },
                        myCalendar.get(Calendar.HOUR_OF_DAY), myCalendar.get(Calendar.MINUTE), true);
                closeTimePickerDialog.show();
            }
        });

        newFieldAllDayTime.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    newFieldOpenTime.setEnabled(false);
                    newFieldOpenTime.setText(" ");
                    newFieldCloseTime.setEnabled(false);
                    newFieldCloseTime.setText(" ");
                } else {
                    newFieldOpenTime.setEnabled(true);
                    newFieldOpenTime.setText("");
                    newFieldCloseTime.setEnabled(true);
                    newFieldCloseTime.setText("");
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
        mFragmentManagementListener.setCurrentDisplayedFragment(getString(R.string.new_field_title), this);
        mNavigationDrawerManagementListener.setToolbarAsUp();
    }

    /**
     * Pide al Presentador que recupere las instalaciones de la ciudad actual en caso de que el
     * usuario quiera cambiar la dirección. Si es una edición, también pide al Presentador los
     * datos de la instalación que se va a editar.
     */
    @Override
    public void onStart() {
        super.onStart();
        newFieldMap.onStart();
        //Do always to have Fields to populate Map
        mNewFieldPresenter.loadNearbyFields(getLoaderManager(), getArguments());

        if (!sInitialize) {
            mNewFieldPresenter.openField(getLoaderManager(), getArguments());
            sInitialize = true;
        } else {
            setSportCourts(((FieldsActivity) getActivity()).mSports);
            showContent();
        }
    }

    /**
     * Almacena la lista de instalaciones encontradas.
     *
     * @param fieldList lista de instalaciones
     */
    @Override
    public void retrieveFields(ArrayList<Field> fieldList) {
        showContent();
        mFieldList = fieldList;
    }

    /**
     * Muestra el contenido del Fragmento en la pantalla y avisa al mapa de este método del ciclo
     * de vida del Fragmento.
     */
    @Override
    public void onResume() {
        super.onResume();
        newFieldMap.onResume();
    }

    /**
     * Avisa al mapa de este método del ciclo de vida del Fragmento y cancela los diálogos de
     * selección de horas en caso de que se estuvieran mostrando
     */
    @Override
    public void onPause() {
        super.onPause();
        newFieldMap.onPause();
        if (openTimePickerDialog != null && openTimePickerDialog.isShowing())
            openTimePickerDialog.dismiss();
        if (closeTimePickerDialog != null && closeTimePickerDialog.isShowing())
            closeTimePickerDialog.dismiss();
    }

    /**
     * Muestra en la interfaz el nombre del partido
     *
     * @param name nombre del partido
     */
    @Override
    public void showFieldName(String name) {
        if (name != null && !TextUtils.isEmpty(name))
            newFieldName.setText(name);
    }

    /**
     * Muestra en la interfaz la dirección escogida para la instalación
     *
     * @param address dirección postal de la instalación
     * @param city    ciudad correspondiente a esa dirección
     * @param coords  coordenadas correspondientes a esa dirección
     */
    @Override
    public void showFieldPlace(String address, String city, LatLng coords) {
        if (address != null && !TextUtils.isEmpty(address))
            newFieldAddress.setText(address);

        Utiles.setCoordinatesInMap(getActivityContext(), mMap, coords);

        ((FieldsActivity) getActivity()).mAddress = address;
        ((FieldsActivity) getActivity()).mCity = city;
        ((FieldsActivity) getActivity()).mCoord = coords;
    }

    /**
     * Muestra en la interfaz las horas de apertura y cierre
     *
     * @param openTime   hora de apertura en milisegundos
     * @param closeTimes hora de cierre en milisegundos
     */
    @Override
    public void showFieldTimes(long openTime, long closeTimes) {
        if (openTime <= closeTimes) {
            newFieldOpenTime.setText(UtilesTime.millisToTimeString(openTime));
            newFieldCloseTime.setText(UtilesTime.millisToTimeString(closeTimes));
        }
    }

    /**
     * Almacena las pistas de la instalación que se está editando
     *
     * @param sports lista de {@link SportCourt} representando una pista, su puntuación, y  sus
     *               votos
     */
    @Override
    public void setSportCourts(List<SportCourt> sports) {
        mSports = sports;
    }

    /**
     * Limpia los elementos de la interfaz utilizados para mostrar los datos de la instalación
     */
    @Override
    public void clearUI() {
        newFieldName.setText("");
        newFieldAddress.setText("");
        newFieldOpenTime.setText("");
        newFieldCloseTime.setText("");
        mSports = null;
    }

    /**
     * Avisa al mapa de este método del ciclo de vida del Fragmento
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        newFieldMap.onDestroy();
    }

    /**
     * Avisa al mapa de este método del ciclo de vida del Fragmento
     */
    @Override
    public void onStop() {
        super.onStop();
        newFieldMap.onStop();
    }

    /**
     * Avisa al mapa de este método del ciclo de vida del Fragmento
     */
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        newFieldMap.onLowMemory();
    }
}
