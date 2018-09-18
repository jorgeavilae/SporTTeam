package com.usal.jorgeav.sportapp.mainactivities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.usal.jorgeav.sportapp.R;
import com.usal.jorgeav.sportapp.adapters.SportSpinnerAdapter;
import com.usal.jorgeav.sportapp.data.Field;
import com.usal.jorgeav.sportapp.data.MyPlace;
import com.usal.jorgeav.sportapp.data.Sport;
import com.usal.jorgeav.sportapp.data.SportCourt;
import com.usal.jorgeav.sportapp.fields.FieldsMapFragment;
import com.usal.jorgeav.sportapp.fields.addfield.NewFieldContract;
import com.usal.jorgeav.sportapp.fields.addfield.NewFieldFragment;
import com.usal.jorgeav.sportapp.network.firebase.actions.FieldsFirebaseActions;
import com.usal.jorgeav.sportapp.sportselection.SportsListFragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Actividad principal que hereda de {@link BaseActivity} y que aloja todos los Fragmentos
 * relacionados con los Partidos. Actúa como puente entre los Fragmentos, para sus comunicaciones,
 * con los deportes escogidos en {@link SportsListFragment} o recibiendo el resultado de la
 * Actividad de selección de lugares {@link MapsActivity} para utilizarlo en la creación
 * de instalaciones, o para añadir una pista si se selecciona una instalación.
 */
public class FieldsActivity extends BaseActivity
        implements SportsListFragment.OnSportsSelected {
    /**
     * Nombre de la clase
     */
    public static final String TAG = FieldsActivity.class.getSimpleName();

    /**
     * Clave para identificar un dato añadido al {@link android.app.PendingIntent}, cuando
     * se inicia esta Actividad. El dato añadido es nulo, y este identificador se utiliza para
     * iniciar la creación de una nueva instalación en lugar de mostrar el mapa de instalaciones.
     */
    public static final String INTENT_EXTRA_CREATE_NEW_FIELD = "INTENT_EXTRA_CREATE_NEW_FIELD";
    /**
     * Código con el que identificar el resultado de la Actividad de selección de lugar
     * {@link MapsActivity} en {@link #onActivityResult(int, int, Intent)}. Utilizado para
     * iniciar el proceso de creación de nueva instalación o de creación de pista para
     * instalación existente
     */
    public static final int REQUEST_CODE_ADDRESS_TO_START_NEW_FIELD_PROCESS = 23;
    /**
     * Código con el que identificar el resultado de la Actividad de selección de lugar
     * {@link MapsActivity} en {@link #onActivityResult(int, int, Intent)}. Utilizado para
     * aceptar sólo direcciones con las que actualizar la dirección actual de la instalción
     */
    public static final int REQUEST_CODE_JUST_ADDRESS = 24;

    //todo mFieldId innecesario???
//    private static final String INSTANCE_FIELD_ID_SELECTED = "INSTANCE_FIELD_ID_SELECTED";
//    public String mFieldId;
    /**
     * Clave para mantener la dirección seleccionada en rotaciones del dispositivo en
     * el {@link Bundle} de estado.
     */
    private static final String INSTANCE_ADDRESS_SELECTED = "INSTANCE_ADDRESS_SELECTED";
    /**
     * Valor de la dirección seleccionada
     */
    public String mAddress;
    /**
     * Clave para mantener la ciudad seleccionada en rotaciones del dispositivo en
     * el {@link Bundle} de estado.
     */
    private static final String INSTANCE_CITY_SELECTED = "INSTANCE_CITY_SELECTED";
    /**
     * Valor para la ciudad seleccionada
     */
    public String mCity;
    /**
     * Clave para mantener las coordenadas seleccionadas en rotaciones del dispositivo en
     * el {@link Bundle} de estado.
     */
    private static final String INSTANCE_COORD_SELECTED = "INSTANCE_COORD_SELECTED";
    /**
     * Valor para las coordenadas seleccionadas
     */
    public LatLng mCoord;
    /**
     * Clave para mantener las pistas de una instalación en rotaciones del dispositivo en
     * el {@link Bundle} de estado. Utilizado durante las ediciones de una instalación
     */
    private static final String INSTANCE_SPORTS_COURT_SELECTED = "INSTANCE_SPORTS_COURT_SELECTED";
    /**
     * Valor de las pistas de una instalación que está siendo editada
     */
    public ArrayList<SportCourt> mSports;

    /**
     * Crea el Fragmento principal que debe mostrar en la pantalla. Comprueba, también, si
     * hay algún dato extra en el {@link Intent} que inicia la Actividad para cargar un
     * Fragmento diferente
     */
    @Override
    public void startMainFragment() {
        // If needed, start createNewField from fragment instead of from here
        boolean createNewField = false;
        if (getIntent().hasExtra(INTENT_EXTRA_CREATE_NEW_FIELD))
            createNewField = true;

        initFragment(FieldsMapFragment.newInstance(createNewField), false,
                BaseActivity.FRAGMENT_TAG_IS_FIELDS_MAP);

        mNavigationView.setCheckedItem(R.id.nav_fields);
    }

    /**
     * Comprueba que la entrada pulsada del menú lateral de navegación no es la
     * correspondiente a esta Actividad, en cuyo caso ignora la pulsación. Si no lo es,
     * invoca el mismo método de la superclase {@link BaseActivity#onNavigationItemSelected(MenuItem)}
     *
     * @param item elemento del menú pulsado
     * @return valor de {@link BaseActivity#onNavigationItemSelected(MenuItem)} o false si es
     * la misma entrada
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return item.getItemId() != R.id.nav_fields && super.onNavigationItemSelected(item);
    }

    /**
     * Se invoca este método al destruir la Actividad. Guarda la instalación, la dirección,
     * la ciudad y las coordenadas si las hubiera. Estas variables toman un valor sólo
     * durante la creación o edición de instalaciones.
     *
     * @param outState Bundle para guardar el estado de la Actividad
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
//        if (mFieldId != null)
//            outState.putString(INSTANCE_FIELD_ID_SELECTED, mFieldId);
        if (mAddress != null)
            outState.putString(INSTANCE_ADDRESS_SELECTED, mAddress);
        if (mCity != null)
            outState.putString(INSTANCE_CITY_SELECTED, mCity);
        if (mCoord != null)
            outState.putParcelable(INSTANCE_COORD_SELECTED, mCoord);
        if (mSports != null && mSports.size() > 0)
            outState.putParcelableArrayList(INSTANCE_SPORTS_COURT_SELECTED, mSports);
    }

    /**
     * Se ejecuta en la recreación de la Actividad, después de {@link #onStart()}. Sólo se
     * ejecuta si <code>savedInstanceState != null</code>. Es el mismo {@link Bundle} de
     * {@link #onCreate(Bundle)}. Usado para extraer la dirección, la ciudad,
     * las coordenadas o los deportes si los hubiera.
     *
     * @param savedInstanceState Bundle para extraer el estado de la Actividad
     */
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
//        if (savedInstanceState.containsKey(INSTANCE_FIELD_ID_SELECTED))
//            mFieldId = savedInstanceState.getString(INSTANCE_FIELD_ID_SELECTED);
        if (savedInstanceState.containsKey(INSTANCE_ADDRESS_SELECTED))
            mAddress = savedInstanceState.getString(INSTANCE_ADDRESS_SELECTED);
        if (savedInstanceState.containsKey(INSTANCE_CITY_SELECTED))
            mCity = savedInstanceState.getString(INSTANCE_CITY_SELECTED);
        if (savedInstanceState.containsKey(INSTANCE_COORD_SELECTED))
            mCoord = savedInstanceState.getParcelable(INSTANCE_COORD_SELECTED);

        if (mDisplayedFragment instanceof NewFieldContract.View)
            ((NewFieldContract.View) mDisplayedFragment).showFieldPlace(mAddress, mCity, mCoord);


        if (savedInstanceState.containsKey(INSTANCE_SPORTS_COURT_SELECTED)) {
            mSports = savedInstanceState.getParcelableArrayList(INSTANCE_SPORTS_COURT_SELECTED);

            if (mDisplayedFragment instanceof NewFieldContract.View)
                ((NewFieldContract.View) mDisplayedFragment).setSportCourts(mSports);
        }
    }

    /**
     * Método invocado para recuperar los deportes escogidos para las pistas de la instalación
     * que se va a crear o se está editando. Pertenece a la interfaz
     * {@link SportsListFragment.OnSportsSelected}
     *
     * <p>Cuando se está editando, <code>fieldId != null</code> por lo que se actualizan las pistas
     * en el servidor y se vuelve al Fragmento anterior. Cuando <code>fieldId == null</code>
     * es un proceso de creación de instalación y ya se tienen la dirección y la lista de deportes
     * por lo que se inicia {@link NewFieldFragment}
     *
     * @param fieldId identificador de la instalación a la que pertenecen estos deportes
     * @param sportsSelected lista de deportes seleccionados
     * @param votesList lista del número de votos de cada pista. Se utiliza en el caso de que
     *                  se esté modificando una instalación, para no perder los votos en la edición.
     */
    @Override
    public void retrieveSportsSelected(String fieldId,
                                       List<Sport> sportsSelected,
                                       HashMap<String, Long> votesList) {
        HashMap<String, Object> sportsMap = new HashMap<>();
        Long votes;
        mSports = new ArrayList<>();

        if (sportsSelected != null) {
            for (Sport s : sportsSelected) {

                /* Extract votes cached in SportListFragment from DetailFieldFragment when editing*/
                if (votesList == null || !votesList.containsKey(s.getSportID())) votes = 1L;
                else votes = votesList.get(s.getSportID());

                SportCourt sc = new SportCourt(s.getSportID(), s.getPunctuation(), votes);
                sportsMap.put(s.getSportID(), sc.toMap());
                mSports.add(sc);
            }

            if (fieldId != null && !TextUtils.isEmpty(fieldId)) {
                //Update sports (DetailFieldFragment update sports)
                FieldsFirebaseActions.updateFieldSports(fieldId, sportsMap);
                onBackPressed();
            } else {
                //Start NewFieldFragment with sport already selected
                Fragment fragment = NewFieldFragment.newInstance(null);
                initFragment(fragment, true);
            }
        }
    }

    /**
     * Inicia la transición hacia la Actividad de selección de lugar {@link MapsActivity}
     *
     * @param dataList colección de instalaciones que debe mostrar como opción {@link MapsActivity}
     * @param startNewField false si sólo pueden escogerse direcciones para actualizar la que
     *                      ya fue escogida, true si pueden escogerse direcciones para iniciar el
     *                      proceso de creación de una nueva instalación o pueden escogerse
     *                      instalaciones para iniciar el proceso de creación de una nueva pista
     *                      para esa instalación.
     */
    public void startMapActivityForResult(ArrayList<Field> dataList, boolean startNewField) {
        Intent intent = new Intent(this, MapsActivity.class);
        intent.putExtra(MapsActivity.INTENT_EXTRA_FIELD_LIST, dataList);
        intent.putExtra(MapsActivity.INTENT_EXTRA_PARENT_FIELDS_ACTIVITY, true);
        if (startNewField)
            startActivityForResult(intent, REQUEST_CODE_ADDRESS_TO_START_NEW_FIELD_PROCESS);
        else
            startActivityForResult(intent, REQUEST_CODE_JUST_ADDRESS);
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == REQUEST_CODE_ADDRESS_TO_RETRIEVE || requestCode == REQUEST_CODE_ADDRESS_TO_START_NEW_FRAGMENT) {
//            if (resultCode == RESULT_OK) {
//                // Expect a Field where to add a new Sport,
//                // or an address (MyPlace) where to add a new Field
//                if (data.hasExtra(MapsActivity.FIELD_SELECTED_EXTRA)) {
//                    Field field = data.getParcelableExtra(MapsActivity.FIELD_SELECTED_EXTRA);
//                    mFieldId = field.getId();
//                    mAddress = field.getAddress();
//                    mCity = field.getCity();
//                    mCoord = new LatLng(field.getCoord_latitude(), field.getCoord_longitude());
//
//                    //Start dialog to add sport to this Field
//                    startDialogToAddSport(field);
//                } else if (data.hasExtra(MapsActivity.PLACE_SELECTED_EXTRA)) {
//                    MyPlace myPlace = data.getParcelableExtra(MapsActivity.PLACE_SELECTED_EXTRA);
//                    mFieldId = null;
//                    mAddress = myPlace.getAddress();
//                    mCity = myPlace.getCity();
//                    mCoord = myPlace.getCoordinates();
//
//                    //When select a place from FieldsFragment's addFieldButton
//                    if (requestCode == REQUEST_CODE_ADDRESS_TO_START_NEW_FRAGMENT) {
//                        //Select sports and later, in retrieveFields(), start new Field fragment flow
//                        Fragment fragment = SportsListFragment.newInstance("", null, null);
//                        initFragment(fragment, true);
//                    } else { //When edit Field's place from NewFieldFragment's button
//                        if (mDisplayedFragment instanceof NewFieldContract.View)
//                            ((NewFieldContract.View) mDisplayedFragment).showFieldPlace(mAddress, mCity, mCoord);
//                    }
//                }
//            } else {
//                Toast.makeText(this, getString(R.string.toast_should_select_place), Toast.LENGTH_SHORT).show();
//            }
//        }
//    }
    /**
     * Recupera el lugar seleccionado para la instalación en {@link MapsActivity} o la
     * instalación seleccionada para incluir una nueva pista. <br>
     * Método invocado cuando se vuelve a esta Actividad desde otra que fue iniciada con
     * {@link android.app.Activity#startActivityForResult(Intent, int)}.
     *
     * @param requestCode código con el que se inicia e identifica la Actividad
     * @param resultCode código representativo del resultado de la ejecución de la Actividad
     * @param data datos extras incluidos como resultado de la ejecución de la Actividad
     */
    //todo testear
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE_ADDRESS_TO_START_NEW_FIELD_PROCESS) {
                // Expect a Field where to add a new Sport,
                // or an address (MyPlace) where to add a new Field
                if (data.hasExtra(MapsActivity.FIELD_SELECTED_EXTRA)) {
                    Field field = data.getParcelableExtra(MapsActivity.FIELD_SELECTED_EXTRA);
                    startDialogToAddSport(field);
                } else if (data.hasExtra(MapsActivity.PLACE_SELECTED_EXTRA)) {
                    MyPlace myPlace = data.getParcelableExtra(MapsActivity.PLACE_SELECTED_EXTRA);
//                    mFieldId = null;
                    mAddress = myPlace.getAddress();
                    mCity = myPlace.getCity();
                    mCoord = myPlace.getCoordinates();

                    //Need sports before another data to the new Field
                    Fragment fragment = SportsListFragment.newInstance("", null, null);
                    initFragment(fragment, true);
                } else {
                    Toast.makeText(this,
                            getString(R.string.toast_should_select_place),
                            Toast.LENGTH_SHORT).show();
                }
            } else if (requestCode == REQUEST_CODE_JUST_ADDRESS) {
                //Address requested by NewFieldFragment to get a new one
                if (mDisplayedFragment instanceof NewFieldContract.View)
                    ((NewFieldContract.View) mDisplayedFragment).showFieldPlace(mAddress, mCity, mCoord);
            }
        }
    }

    /**
     * Crea y muestra un diálogo para escoger la pista que se va a añadir a la instalación
     * seleccionada
     *
     * @param field Instalación a la que se va a añadir la pista
     */
    private void startDialogToAddSport(final Field field) {
        // Prepare list
        List<String> sportsResources = Arrays.asList(getResources().getStringArray(R.array.sport_id_values));
        ArrayList<String> sportsLeft = new ArrayList<>();
        for (String sportId : sportsResources)
            if (!field.getSport().containsKey(sportId))
                sportsLeft.add(sportId);

        // Prepare view
        View view = getLayoutInflater().inflate(R.layout.add_sport_dialog, null);
        final Spinner sportSpinner = (Spinner) view.findViewById(R.id.add_sport_dialog_sport);
        sportSpinner.setAdapter(new SportSpinnerAdapter(this, R.layout.sport_spinner_item, sportsLeft));
        final RatingBar ratingBar = (RatingBar) view.findViewById(R.id.add_sport_dialog_rate);

        // Prepare dialog
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle(getString(R.string.dialog_title_add_sport_to_field))
                .setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String sportId = sportSpinner.getSelectedItem().toString();
                        float punctuation = ratingBar.getRating();
                        FieldsFirebaseActions.addFieldSport(field.getId(), new SportCourt(sportId, (double) punctuation, 1L));
                        Toast.makeText(FieldsActivity.this, getString(R.string.toast_add_sport_success), Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton(android.R.string.cancel, null)
                .setView(view);
        dialog.create().show();
    }
}
