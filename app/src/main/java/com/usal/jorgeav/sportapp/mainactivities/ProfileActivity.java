package com.usal.jorgeav.sportapp.mainactivities;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import androidx.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.usal.jorgeav.sportapp.R;
import com.usal.jorgeav.sportapp.data.Sport;
import com.usal.jorgeav.sportapp.network.firebase.actions.UserFirebaseActions;
import com.usal.jorgeav.sportapp.profile.ProfileContract;
import com.usal.jorgeav.sportapp.profile.ProfileFragment;
import com.usal.jorgeav.sportapp.sportselection.SportsListFragment;
import com.usal.jorgeav.sportapp.utils.Utiles;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

/**
 * Actividad principal que hereda de {@link BaseActivity} y que aloja todos los Fragmentos
 * relacionados con el Usuario actual. Actúa como puente entre los Fragmentos, para sus
 * comunicaciones, con los deportes escogidos en {@link SportsListFragment} o comprobando
 * los permisos para acceder a las imágenes y así de utilizar las librerías que se encargan
 * de cambiar la foto de perfil uCrop y EasyImage.
 *
 * @see <a href= "https://github.com/Yalantis/uCrop">uCrop (Github)</a>
 * @see <a href= "https://github.com/jkwiecien/EasyImage">EasyImage (Github)</a>
 */
public class ProfileActivity extends BaseActivity
        implements SportsListFragment.OnSportsSelected {
    /**
     * Nombre de la clase
     */
    public static final String TAG = ProfileActivity.class.getSimpleName();

    /**
     * Crea el Fragmento principal que debe mostrar los datos del usuario que tiene la sesión
     * iniciada en ese momento.
     */
    @Override
    public void startMainFragment() {
        String myUserID = Utiles.getCurrentUserId();
        if (TextUtils.isEmpty(myUserID)) return;

        initFragment(ProfileFragment.newInstance(myUserID), false);
        mNavigationView.setCheckedItem(R.id.nav_profile);
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
        return item.getItemId() != R.id.nav_profile && super.onNavigationItemSelected(item);
    }

    /**
     * Método invocado para recuperar los deportes escogidos como colección de deportes que
     * practica el usuario. Pertenece a la interfaz {@link SportsListFragment.OnSportsSelected}
     *
     * @param userID         identificador del usuario al que pertenecen estos deportes
     * @param sportsSelected lista de deportes seleccionados
     * @param votesList      no se utiliza este parámetro cuando se refiere a deportes practicados por
     *                       el usuario
     */
    @Override
    public void retrieveSportsSelected(String userID,
                                       List<Sport> sportsSelected,
                                       HashMap<String, Long> votesList) {
        if (userID != null && !TextUtils.isEmpty(userID)) {
            HashMap<String, Double> sportsMap = new HashMap<>();
            if (sportsSelected != null)
                for (Sport sport : sportsSelected)
                    sportsMap.put(sport.getSportID(), sport.getPunctuation());
            UserFirebaseActions.updateSports(userID, sportsMap);
        }
        onBackPressed();
    }

    /**
     * Dependiendo del código de la consulta: <br>
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

        // Results of select image and crop Activity when add a simulated User
        EasyImage.handleActivityResult(requestCode, resultCode, data, this, new DefaultCallback() {
            @Override
            public void onImagePickerError(Exception e, EasyImage.ImageSource source, int type) {
                //Some error handling
            }

            @Override
            public void onImagePicked(File imageFile, EasyImage.ImageSource source, int type) {
                if (mDisplayedFragment instanceof ProfileContract.View)
                    Utiles.startCropActivity(Uri.fromFile(imageFile), ProfileActivity.this);
            }
        });

        if (requestCode == UCrop.REQUEST_CROP) {
            if (resultCode == RESULT_OK) {
                if (mDisplayedFragment instanceof ProfileContract.View)
                    ((ProfileContract.View) mDisplayedFragment).croppedResult(UCrop.getOutput(data));
            } else {
                // Cancel after pick image and before crop
                if (mDisplayedFragment instanceof ProfileContract.View)
                    ((ProfileContract.View) mDisplayedFragment).croppedResult(null);
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
                EasyImage.openGallery(this, ProfileFragment.RC_PHOTO_PICKER);
            else if (grantResults[1] == PackageManager.PERMISSION_GRANTED)
                //The user can take pictures or pick an image
                EasyImage.openChooserWithGallery(this, getString(R.string.pick_photo_from), ProfileFragment.RC_PHOTO_PICKER);
        }
    }
}
