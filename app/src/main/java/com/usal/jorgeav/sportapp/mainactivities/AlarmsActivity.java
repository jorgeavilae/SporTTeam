package com.usal.jorgeav.sportapp.mainactivities;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.usal.jorgeav.sportapp.R;
import com.usal.jorgeav.sportapp.alarms.AlarmsFragment;
import com.usal.jorgeav.sportapp.alarms.addalarm.NewAlarmContract;
import com.usal.jorgeav.sportapp.alarms.addalarm.NewAlarmFragment;
import com.usal.jorgeav.sportapp.alarms.alarmdetail.DetailAlarmFragment;
import com.usal.jorgeav.sportapp.data.Field;
import com.usal.jorgeav.sportapp.sportselection.SelectSportFragment;
import com.usal.jorgeav.sportapp.utils.Utiles;

import java.util.ArrayList;

/**
 * Actividad principal que hereda de {@link BaseActivity} y que aloja todos los Fragmentos
 * relacionados con las Alarmas. Actúa como puente entre los Fragmentos, para sus comunicaciones,
 * con el deporte escogido en {@link SelectSportFragment} o recibiendo el resultado de la
 * Actividad de selección de lugares {@link MapsActivity} para utilizarlo en la creación
 * de alarmas.
 */
public class AlarmsActivity extends BaseActivity
        implements SelectSportFragment.OnSportSelectedListener {
    /**
     * Nombre de la clase
     */
    @SuppressWarnings("unused")
    private final static String TAG = AlarmsActivity.class.getSimpleName();

    /**
     * Clave para identificar un dato añadido al {@link android.app.PendingIntent}, cuando
     * se inicia esta Actividad desde una notificación. El dato añadido es el identificador
     * de la alarma que debe mostrar esta Actividad al iniciarse, en lugar de la colección de
     * alarmas.
     */
    public static final String ALARM_ID_PENDING_INTENT_EXTRA = "ALARM_ID_PENDING_INTENT_EXTRA";
    /**
     * Código con el que identificar el resultado de la Actividad de selección de lugar
     * {@link MapsActivity} en {@link #onActivityResult(int, int, Intent)}
     */
    public static final int REQUEST_CODE_ADDRESS = 23;

    /**
     * Clave para mantener la instalación seleccionada en rotaciones del dispositivo en
     * el {@link Bundle} de estado.
     */
    private static final String INSTANCE_FIELD_ID_SELECTED = "INSTANCE_FIELD_ID_SELECTED";
    /**
     * Valor de la instalación seleccionada
     */
    public String mFieldId;
    /**
     * Clave para mantener la ciudad seleccionada en rotaciones del dispositivo en
     * el {@link Bundle} de estado.
     */
    private static final String INSTANCE_CITY_SELECTED = "INSTANCE_CITY_SELECTED";
    /**
     * Valor de la ciudad seleccionada
     */
    public String mCity;
    /**
     * Clave para mantener las coordenadas seleccionadas en rotaciones del dispositivo en
     * el {@link Bundle} de estado.
     */
    private static final String INSTANCE_COORD_SELECTED = "INSTANCE_COORD_SELECTED";
    /**
     * Valor de las coordenadas seleccionadas
     */
    public LatLng mCoord;

    /**
     * Crea el Fragmento principal que debe mostrar en la pantalla. Comprueba, también, si
     * hay algún dato extra en el {@link Intent} que inicia la Actividad para cargar un
     * Fragmento diferente
     */
    @Override
    public void startMainFragment() {
        String alarmId = getIntent().getStringExtra(ALARM_ID_PENDING_INTENT_EXTRA);

        initFragment(AlarmsFragment.newInstance(), false);

        // Open an alarm detail right after alarm list because
        // this Activity is open because of a notification
        if (alarmId != null)
            initFragment(DetailAlarmFragment.newInstance(alarmId), true);

        mNavigationView.setCheckedItem(R.id.nav_alarms);
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
        return item.getItemId() != R.id.nav_alarms && super.onNavigationItemSelected(item);
    }

    /**
     * Se invoca este método al destruir la Actividad. Guarda la instalación, la ciudad
     * y las coordenadas si las hubiera. Estas variables toman un valor sólo durante la
     * creación de alarmas.
     *
     * @param outState Bundle para guardar el estado de la Actividad
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mFieldId != null)
            outState.putString(INSTANCE_FIELD_ID_SELECTED, mFieldId);
        if (mCity != null)
            outState.putString(INSTANCE_CITY_SELECTED, mCity);
        if (mCoord != null)
            outState.putParcelable(INSTANCE_COORD_SELECTED, mCoord);
    }

    /**
     * Se ejecuta en la recreación de la Actividad, después de {@link #onStart()}. Sólo se
     * ejecuta si <code>savedInstanceState != null</code>. Es el mismo {@link Bundle} de
     * {@link #onCreate(Bundle)}. Usado para extraer la instalación, la ciudad y las coordenadas
     * si las hubiera.
     *
     * @param savedInstanceState Bundle para extraer el estado de la Actividad
     */
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState.containsKey(INSTANCE_FIELD_ID_SELECTED))
            mFieldId = savedInstanceState.getString(INSTANCE_FIELD_ID_SELECTED);
        if (savedInstanceState.containsKey(INSTANCE_CITY_SELECTED))
            mCity = savedInstanceState.getString(INSTANCE_CITY_SELECTED);
        if (savedInstanceState.containsKey(INSTANCE_COORD_SELECTED))
            mCoord = savedInstanceState.getParcelable(INSTANCE_COORD_SELECTED);

        if (mFieldId != null || mCity != null || mCoord != null)
            if (mDisplayedFragment instanceof NewAlarmContract.View)
                ((NewAlarmContract.View) mDisplayedFragment).showAlarmField(mFieldId, mCity, mCoord);
    }

    /**
     * Método invocado para recuperar el deporte escogido para la alarma que se va a crear.
     * Pertenece a la interfaz {@link SelectSportFragment.OnSportSelectedListener}
     *
     * @param sportId identificador único del deporte escogido
     */
    @Override
    public void onSportSelected(String sportId) {
        Fragment fragment = NewAlarmFragment.newInstance(null, sportId);
        initFragment(fragment, true);
    }

    /**
     * Inicia la transición hacia la Actividad de selección de lugar {@link MapsActivity}
     *
     * @param dataList colección de instalaciones que debe mostrar como opción {@link MapsActivity}
     */
    public void startMapActivityForResult(ArrayList<Field> dataList) {
        Intent intent = new Intent(this, MapsActivity.class);
        intent.putExtra(MapsActivity.INTENT_EXTRA_FIELD_LIST, dataList);
        intent.putExtra(MapsActivity.INTENT_EXTRA_ONLY_FIELDS, true);
        startActivityForResult(intent, REQUEST_CODE_ADDRESS);
    }

    /**
     * Recupera el lugar seleccionado para la alarma en {@link MapsActivity} <br>
     * Método invocado cuando se vuelve a esta Actividad desde otra que fue iniciada con
     * {@link android.app.Activity#startActivityForResult(Intent, int)}.
     *
     * @param requestCode código con el que se inicia e identifica la Actividad
     * @param resultCode  código representativo del resultado de la ejecución de la Actividad
     * @param data        datos extras incluidos como resultado de la ejecución de la Actividad
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_ADDRESS)
            if (resultCode == RESULT_OK) {
                // Expect a Field where set a new Alarm
                if (data.hasExtra(MapsActivity.FIELD_SELECTED_EXTRA)) {
                    Field field = data.getParcelableExtra(MapsActivity.FIELD_SELECTED_EXTRA);
                    mFieldId = field.getId();
                    mCity = field.getCity();
                    mCoord = new LatLng(field.getCoord_latitude(), field.getCoord_longitude());
                } else if (data.hasExtra(MapsActivity.PLACE_SELECTED_EXTRA)) {
                    new AlertDialog.Builder(this)
                            .setTitle(R.string.dialog_title_you_must_select_place)
                            .setMessage(R.string.dialog_msg_you_must_select_place)
                            .create().show();
                } else if (data.hasExtra(MapsActivity.ADD_FIELD_SELECTED_EXTRA)) {
                    Utiles.startFieldsActivityAndNewField(this);
                }

                if (mDisplayedFragment instanceof NewAlarmContract.View)
                    ((NewAlarmContract.View) mDisplayedFragment).showAlarmField(mFieldId, mCity, mCoord);
            } else
                Toast.makeText(this, R.string.no_field_selection, Toast.LENGTH_SHORT).show();
    }
}
