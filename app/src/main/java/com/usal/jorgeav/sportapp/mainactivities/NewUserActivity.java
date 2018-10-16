package com.usal.jorgeav.sportapp.mainactivities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.usal.jorgeav.sportapp.BaseFragment;
import com.usal.jorgeav.sportapp.R;
import com.usal.jorgeav.sportapp.adduser.NewUserContract;
import com.usal.jorgeav.sportapp.adduser.NewUserFragment;
import com.usal.jorgeav.sportapp.data.Sport;
import com.usal.jorgeav.sportapp.sportselection.SportsListFragment;
import com.usal.jorgeav.sportapp.utils.Utiles;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

/**
 * Implementa la funcionalidad relativa a la creación de usuarios. Aloja el Fragmento de
 * introducción de datos de usuario {@link NewUserFragment} y el de la selección de deportes
 * {@link SportsListFragment}.
 * <p>
 * Implementa también, la interfaz {@link ActivityContracts.FragmentManagement} para que pueda
 * ser utilizada por los Fragmentos en sus comunicaciones.
 * <p>
 * Además, se encarga de comprobar los permisos para acceder a las imágenes y así utilizar
 * las librerías que se encargan de establecer la foto de perfil uCrop y EasyImage.
 *
 * @see <a href= "https://github.com/Yalantis/uCrop">uCrop (Github)</a>
 * @see <a href= "https://github.com/jkwiecien/EasyImage">EasyImage (Github)</a>
 */
public class NewUserActivity extends AppCompatActivity implements
        ActivityContracts.FragmentManagement,
        SportsListFragment.OnSportsSelected {
    /**
     * Nombre de la clase
     */
    private final static String TAG = NewUserActivity.class.getSimpleName();
    /**
     * Clave para guardar el estado del Fragmento mostrado en recreaciones de la Actividad
     */
    private final static String BUNDLE_SAVE_FRAGMENT_INSTANCE = "BUNDLE_SAVE_FRAGMENT_INSTANCE";

    /**
     * Referencia a la barra superior de la interfaz
     */
    @BindView(R.id.new_user_toolbar)
    Toolbar newUserToolbar;
    /**
     * Barra de progreso mostrada durante cargas
     */
    @BindView(R.id.new_user_progressbar)
    ProgressBar newUserProgressbar;
    /**
     * Contenedor del Fragmento
     */
    @BindView(R.id.new_user_content)
    FrameLayout newUserContent;

    /**
     * Referencia al Fragmento que se está mostrando en este momento
     */
    private Fragment mDisplayedFragment;
    /**
     * Lista de deportes establecida durante la ejecución de {@link SportsListFragment}
     */
    public ArrayList<Sport> sports;
    /**
     * true para indicar que los deportes ya han sido establecidos, false en otro caso
     */
    public boolean sportsInitialize;

    /**
     * Inicializa y obtiene una referencia a los elementos de la interfaz, con la ayuda de
     * ButterKnife, y se inicializan todas las variables. Inicia el Fragmento {@link NewUserFragment}
     *
     * @param savedInstanceState estado de la Actividad guardado en una posible rotación de
     *                           la pantalla, o null.
     * @see <a href= "http://jakewharton.github.io/butterknife/">ButterKnife</a>
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_user);
        ButterKnife.bind(this);

        setSupportActionBar(newUserToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getString(R.string.new_user_title));
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            newUserToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onBackPressed();
                }
            });
        }

        sportsInitialize = false;
        sports = new ArrayList<>();

        // Start Main Fragment
        initFragment(NewUserFragment.newInstance(), false);
    }

    /**
     * Se invoca este método al destruir la Actividad. Guarda el estado del Fragmento mostrado
     *
     * @param outState Bundle para guardar el Fragmento
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mDisplayedFragment != null && getSupportFragmentManager() != null)
            getSupportFragmentManager().putFragment(outState, BUNDLE_SAVE_FRAGMENT_INSTANCE, mDisplayedFragment);
    }

    /**
     * Se ejecuta en la recreación de la Actividad, después de {@link #onStart()}. Sólo se
     * ejecuta si <code>savedInstanceState != null</code>. Es el mismo {@link Bundle} de
     * {@link #onCreate(Bundle)}. Usado para extraer el Fragmento
     *
     * @param savedInstanceState Bundle para extraer el Fragmento
     */
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mDisplayedFragment = null;
        if (savedInstanceState != null && savedInstanceState.containsKey(BUNDLE_SAVE_FRAGMENT_INSTANCE)) {
            try {
                mDisplayedFragment = getSupportFragmentManager().getFragment(savedInstanceState, BUNDLE_SAVE_FRAGMENT_INSTANCE);
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Método invocado para recuperar los deportes escogidos como colección de deportes que
     * practica el usuario. Pertenece a la interfaz {@link SportsListFragment.OnSportsSelected}
     *
     * @param id             no se utiliza en este caso
     * @param sportsSelected lista de deportes seleccionados
     * @param votesList      no se utiliza este parámetro cuando se refiere a deportes practicados por
     *                       el usuario
     */
    @Override
    public void retrieveSportsSelected(String id, List<Sport> sportsSelected,
                                       HashMap<String, Long> votesList) {
        this.sports.clear();
        this.sports.addAll(sportsSelected);
        sportsInitialize = true;
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

        EasyImage.handleActivityResult(requestCode, resultCode, data, this, new DefaultCallback() {
            @Override
            public void onImagePickerError(Exception e, EasyImage.ImageSource source, int type) {
                //Some error handling
            }

            @Override
            public void onImagePicked(File imageFile, EasyImage.ImageSource source, int type) {
                Utiles.startCropActivity(Uri.fromFile(imageFile), NewUserActivity.this);
            }
        });

        if (requestCode == UCrop.REQUEST_CROP) {
            if (resultCode == RESULT_OK) {
                if (mDisplayedFragment instanceof NewUserContract.View)
                    ((NewUserContract.View) mDisplayedFragment).croppedResult(UCrop.getOutput(data));
            } else {
                // Cancel after pick image and before crop
                if (mDisplayedFragment instanceof NewUserContract.View)
                    ((NewUserContract.View) mDisplayedFragment).croppedResult(null);
            }
        } else if (resultCode == UCrop.RESULT_ERROR)
            Log.e(TAG, "onActivityResult: error ", UCrop.getError(data));
    }

    /**
     * Comprueba que los permisos fueron concedidos e inicia el proceso de selección de imágenes
     * que necesita esa concesión. <br>
     * Método invocado después de iniciar el proceso de petición de permisos de
     * {@link Utiles#isStorageCameraPermissionGranted(Activity)} en {@link NewUserFragment}.
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
                EasyImage.openGallery(this, NewUserFragment.RC_PHOTO_PICKER);
            else if (grantResults[1] == PackageManager.PERMISSION_GRANTED)
                //The user can take pictures or pick an image
                EasyImage.openChooserWithGallery(this, getString(R.string.pick_photo_from), NewUserFragment.RC_PHOTO_PICKER);
        }
    }

    /**
     * Invoca {@link #initFragment(Fragment, boolean, String)} con un tag nulo.
     */
    @Override
    public void initFragment(@NonNull Fragment fragment, boolean addToBackStack) {
        initFragment(fragment, addToBackStack, null);
    }

    /**
     * Inicia la transición hacia el Fragmento especificado y lo almacena en
     * la pila de de Fragmentos si corresponde. Asocia una etiqueta al Fragmento para
     * poder ser encontrado posteriormente con
     * {@link android.support.v4.app.FragmentManager#findFragmentByTag(String)}
     *
     * @param fragment       Fragmento que va a mostrarse
     * @param addToBackStack true si debe almacenarse en la pila de Fragmentos
     * @param tag            etiqueta asociada al Fragmento en la transición
     */
    @Override
    public void initFragment(@NonNull Fragment fragment, boolean addToBackStack, String tag) {
        hideContent();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.new_user_content, fragment, tag);
        if (addToBackStack) transaction.addToBackStack(null);
        transaction.commit();
    }

    /**
     * Establece el titulo en la Toolbar y almacena la referencia al Fragmento mostrado actualmente
     *
     * @param title    título del Fragmento
     * @param fragment referencia al Fragmento
     */
    @Override
    public void setCurrentDisplayedFragment(String title, BaseFragment fragment) {
        setActionBarTitle(title);
        mDisplayedFragment = fragment;
    }

    /**
     * Establece el titulo en la Toolbar
     *
     * @param title título del Fragmento
     */
    private void setActionBarTitle(String title) {
        if (getSupportActionBar() != null && title != null)
            getSupportActionBar().setTitle(title);
    }

    /**
     * Oculta la barra de progreso de la interfaz y muestra el contenido del Fragmento
     */
    @Override
    public void showContent() {
        newUserToolbar.setVisibility(View.VISIBLE);
        newUserContent.setVisibility(View.VISIBLE);
        newUserProgressbar.setVisibility(View.INVISIBLE);
    }


    /**
     * Muestra la barra de progreso de la interfaz y oculta el contenido del Fragmento
     */
    @Override
    public void hideContent() {
        newUserToolbar.setVisibility(View.INVISIBLE);
        newUserContent.setVisibility(View.INVISIBLE);
        newUserProgressbar.setVisibility(View.VISIBLE);
    }

    /**
     * Pertenece a la interfaz {@link ActivityContracts.FragmentManagement} pero no se utiliza.
     */
    @Override
    public void signOut() {
        Log.e(TAG, "signOut: User is not logged yet.");
    }

    /**
     * Obtiene una referencia a la {@link View} que tiene el foco de la interfaz y, si esta
     * mostrando el teclado flotante en la pantalla, lo esconde
     *
     * @see <a href= "https://stackoverflow.com/a/17789187/4235666">
     * (StackOverflow) Close/hide the Android Soft Keyboard</a>
     */
    public void hideSoftKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            InputMethodManager imm =
                    (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null)
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
