package com.usal.jorgeav.sportapp.mainactivities;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.usal.jorgeav.sportapp.R;
import com.usal.jorgeav.sportapp.data.Field;
import com.usal.jorgeav.sportapp.data.MyPlace;
import com.usal.jorgeav.sportapp.eventdetail.DetailEventFragment;
import com.usal.jorgeav.sportapp.eventdetail.simulateparticipant.SimulateParticipantContract;
import com.usal.jorgeav.sportapp.eventdetail.simulateparticipant.SimulateParticipantFragment;
import com.usal.jorgeav.sportapp.events.EventsFragment;
import com.usal.jorgeav.sportapp.events.addevent.NewEventContract;
import com.usal.jorgeav.sportapp.events.addevent.NewEventFragment;
import com.usal.jorgeav.sportapp.sportselection.SelectSportFragment;
import com.usal.jorgeav.sportapp.utils.Utiles;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.util.ArrayList;

import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

/**
 * Actividad principal que hereda de {@link BaseActivity} y que aloja todos los Fragmentos
 * relacionados con los Partidos. Actúa como puente entre los Fragmentos, para sus comunicaciones,
 * con el deporte escogido en {@link SelectSportFragment} o recibiendo el resultado de la
 * Actividad de selección de lugares {@link MapsActivity} para utilizarlo en la creación
 * de partidos. <br>
 * También se encarga de comprobar los permisos para acceder a las imágenes, y de controlar
 * las librerías que se encargan de ellas uCrop y EasyImage.
 *
 * @see <a href= "https://github.com/Yalantis/uCrop">uCrop (Github)</a>
 * @see <a href= "https://github.com/jkwiecien/EasyImage">EasyImage (Github)</a>
 */
public class EventsActivity extends BaseActivity
        implements SelectSportFragment.OnSportSelectedListener {
    /**
     * Nombre de la clase
     */
    private final static String TAG = EventsActivity.class.getSimpleName();

    /**
     * Clave para identificar un dato añadido al {@link android.app.PendingIntent}, cuando
     * se inicia esta Actividad desde una notificación. El dato añadido es el identificador
     * del partido que debe mostrar esta Actividad al iniciarse, en lugar del calendario.
     */
    public static final String EVENT_ID_PENDING_INTENT_EXTRA = "EVENT_ID_PENDING_INTENT_EXTRA";
    /**
     * Clave para identificar un dato añadido al {@link android.app.PendingIntent}, cuando
     * se inicia esta Actividad. El dato añadido es nulo, y este identificador se utiliza para
     * iniciar la creación de un nuevo partido en lugar de mostrar el calendario.
     */
    public static final String CREATE_NEW_EVENT_INTENT_EXTRA = "CREATE_NEW_EVENT_INTENT_EXTRA";
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
        String eventId = getIntent().getStringExtra(EVENT_ID_PENDING_INTENT_EXTRA);

        initFragment(EventsFragment.newInstance(), false);
        if (eventId != null)
            initFragment(DetailEventFragment.newInstance(eventId), true);
        else if (getIntent().hasExtra(CREATE_NEW_EVENT_INTENT_EXTRA))
            initFragment(SelectSportFragment.newInstance(), true);

        mNavigationView.setCheckedItem(R.id.nav_events);
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
        return item.getItemId() != R.id.nav_events && super.onNavigationItemSelected(item);
    }

    /**
     * Se invoca este método al destruir la Actividad. Guarda la instalación, la dirección,
     * la ciudad y las coordenadas si las hubiera. Estas variables toman un valor sólo
     * durante la creación de partidos.
     *
     * @param outState Bundle para guardar el estado de la Actividad
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mFieldId != null)
            outState.putString(INSTANCE_FIELD_ID_SELECTED, mFieldId);
        if (mAddress != null)
            outState.putString(INSTANCE_ADDRESS_SELECTED, mAddress);
        if (mCity != null)
            outState.putString(INSTANCE_CITY_SELECTED, mCity);
        if (mCoord != null)
            outState.putParcelable(INSTANCE_COORD_SELECTED, mCoord);
    }

    /**
     * Se ejecuta en la recreación de la Actividad, después de {@link #onStart()}. Sólo se
     * ejecuta si <code>savedInstanceState != null</code>. Es el mismo {@link Bundle} de
     * {@link #onCreate(Bundle)}. Usado para extraer la instalación, la dirección, la ciudad
     * y las coordenadas si las hubiera.
     *
     * @param savedInstanceState Bundle para extraer el estado de la Actividad
     */
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState.containsKey(INSTANCE_FIELD_ID_SELECTED))
            mFieldId = savedInstanceState.getString(INSTANCE_FIELD_ID_SELECTED);
        if (savedInstanceState.containsKey(INSTANCE_ADDRESS_SELECTED))
            mAddress = savedInstanceState.getString(INSTANCE_ADDRESS_SELECTED);
        if (savedInstanceState.containsKey(INSTANCE_CITY_SELECTED))
            mCity = savedInstanceState.getString(INSTANCE_CITY_SELECTED);
        if (savedInstanceState.containsKey(INSTANCE_COORD_SELECTED))
            mCoord = savedInstanceState.getParcelable(INSTANCE_COORD_SELECTED);

        if (mFieldId != null || mAddress != null || mCity != null || mCoord != null)
            if (mDisplayedFragment instanceof NewEventContract.View)
                ((NewEventContract.View) mDisplayedFragment).showEventField(mFieldId, mAddress, mCity, mCoord);
    }

    /**
     * Método invocado para recuperar el deporte escogido para el partido que se va a crear.
     * Pertenece a la interfaz {@link SelectSportFragment.OnSportSelectedListener}
     *
     * @param sportId identificador único del deporte escogido
     */
    @Override
    public void onSportSelected(String sportId) {
        Fragment fragment = NewEventFragment.newInstance(null, sportId);
        initFragment(fragment, true);
    }

    /**
     * Inicia la transición hacia la Actividad de selección de lugar {@link MapsActivity}
     *
     * @param dataList  colección de instalaciones que debe mostrar como opción {@link MapsActivity}
     * @param onlyField true si sólo puede escogerse instalaciones, false si también pueden
     *                  seleccionarse direcciones.
     */
    public void startMapActivityForResult(ArrayList<Field> dataList, boolean onlyField) {
        Intent intent = new Intent(this, MapsActivity.class);
        intent.putExtra(MapsActivity.INTENT_EXTRA_FIELD_LIST, dataList);
        intent.putExtra(MapsActivity.INTENT_EXTRA_ONLY_FIELDS, onlyField);
        startActivityForResult(intent, REQUEST_CODE_ADDRESS);
    }

    /**
     * Dependiendo del código de la consulta: <br>
     * - Recupera el lugar seleccionado para la alarma en {@link MapsActivity} <br>
     * - Recupera la imagen seleccionada en {@link EasyImage} <br>
     * - Recupera la imagen recortada y almacenada por {@link UCrop} para enviarla al servidor.
     * <p>
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

        if (requestCode == REQUEST_CODE_ADDRESS) {
            if (resultCode == RESULT_OK) {
                // Expect a Field where play a new Event,
                // or an address (MyPlace) to meet for non-field sports
                if (data.hasExtra(MapsActivity.FIELD_SELECTED_EXTRA)) {
                    Field field = data.getParcelableExtra(MapsActivity.FIELD_SELECTED_EXTRA);
                    mFieldId = field.getId();
                    mAddress = field.getAddress();
                    mCity = field.getCity();
                    mCoord = new LatLng(field.getCoord_latitude(), field.getCoord_longitude());
                } else if (data.hasExtra(MapsActivity.PLACE_SELECTED_EXTRA)) {
                    MyPlace myPlace = data.getParcelableExtra(MapsActivity.PLACE_SELECTED_EXTRA);
                    mFieldId = null;
                    mAddress = myPlace.getAddress();
                    mCity = myPlace.getCity();
                    mCoord = myPlace.getCoordinates();
                } else if (data.hasExtra(MapsActivity.ADD_FIELD_SELECTED_EXTRA)) {
                    Utiles.startFieldsActivityAndNewField(this);
                }
                if (mDisplayedFragment instanceof NewEventContract.View)
                    ((NewEventContract.View) mDisplayedFragment).showEventField(mFieldId, mAddress, mCity, mCoord);
            } else {
                Toast.makeText(this, R.string.toast_should_select_place, Toast.LENGTH_SHORT).show();
            }
            return;
        }

        // Results of select image and crop Activity when add a simulated User
        EasyImage.handleActivityResult(requestCode, resultCode, data, this, new DefaultCallback() {
            @Override
            public void onImagePickerError(Exception e, EasyImage.ImageSource source, int type) {
                //Some error handling
            }

            @Override
            public void onImagePicked(File imageFile, EasyImage.ImageSource source, int type) {
                Utiles.startCropActivity(Uri.fromFile(imageFile), EventsActivity.this);
            }
        });

        if (requestCode == UCrop.REQUEST_CROP) {
            if (resultCode == RESULT_OK) {
                if (mDisplayedFragment instanceof SimulateParticipantContract.View)
                    ((SimulateParticipantContract.View) mDisplayedFragment).croppedResult(UCrop.getOutput(data));
            } else {
                // Cancel after pick image and before crop
                if (mDisplayedFragment instanceof SimulateParticipantContract.View)
                    ((SimulateParticipantContract.View) mDisplayedFragment).croppedResult(null);
            }
        } else if (resultCode == UCrop.RESULT_ERROR)
            Log.e(TAG, "onActivityResult: error ", UCrop.getError(data));
    }

    /**
     * Comprueba que los permisos fueron concedidos e inicia el proceso de selección de imágenes
     * que necesita esa concesión. <br>
     * Método invocado después de iniciar el proceso de petición de permisos de
     * {@link Utiles#isStorageCameraPermissionGranted(Activity)}.
     *
     * @param requestCode  código con el que se identifica la petición
     * @param permissions  permisos requeridos. Nunca es null.
     * @param grantResults Resultado de la petición, que puede ser
     *                     {@link android.content.pm.PackageManager#PERMISSION_GRANTED} o
     *                     {@link android.content.pm.PackageManager#PERMISSION_DENIED}. Nunca es null.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == Utiles.RC_GALLERY_CAMERA_PERMISSIONS &&
                permissions[0].equals(WRITE_EXTERNAL_STORAGE) && permissions[1].equals(CAMERA)) {

            if (grantResults[0] == PackageManager.PERMISSION_DENIED)
                //Without WRITE_EXTERNAL_STORAGE it can't save cropped photo
                Toast.makeText(this, R.string.toast_need_write_permission, Toast.LENGTH_SHORT).show();
            else if (grantResults[1] == PackageManager.PERMISSION_DENIED)
                //The user can't take pictures
                EasyImage.openGallery(this, SimulateParticipantFragment.RC_PHOTO_PICKER);
            else if (grantResults[1] == PackageManager.PERMISSION_GRANTED)
                //The user can take pictures or pick an image
                EasyImage.openChooserWithGallery(this, getString(R.string.pick_photo_from), SimulateParticipantFragment.RC_PHOTO_PICKER);
        }
    }
}
