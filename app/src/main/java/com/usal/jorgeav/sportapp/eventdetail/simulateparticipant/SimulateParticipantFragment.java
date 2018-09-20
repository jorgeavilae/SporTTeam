package com.usal.jorgeav.sportapp.eventdetail.simulateparticipant;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.usal.jorgeav.sportapp.BaseFragment;
import com.usal.jorgeav.sportapp.R;
import com.usal.jorgeav.sportapp.utils.Utiles;

import butterknife.BindView;
import butterknife.ButterKnife;
import pl.aprilapps.easyphotopicker.EasyImage;

/**
 * Fragmento utilizado para añadir usuarios simulados. Este Fragmento se encarga de inicializar los
 * componentes de la interfaz necesarios para introducir los datos de los usuarios simulados. Se
 * ayuda de la Actividad contenedora {@link com.usal.jorgeav.sportapp.mainactivities.EventsActivity}
 * para incluir una foto para el usuario simulado. Esta Actividad es la que implementa los métodos
 * necesarios para utilizar las librerías uCrop y EasyImage.
 *
 * <p>Implementa la interfaz {@link SimulateParticipantContract.View} para la comunicación con esta
 * clase.
 *
 * @see
 * <a href= "https://github.com/Yalantis/uCrop">
 *      uCrop (Github)
 * </a>
 * @see
 * <a href= "https://github.com/jkwiecien/EasyImage">
 *      EasyImage (Github)
 * </a>
 */
public class SimulateParticipantFragment extends BaseFragment implements
        SimulateParticipantContract.View {
    /**
     * Nombre de la clase
     */
    public static final String TAG = SimulateParticipantFragment.class.getSimpleName();
    /**
     * Etiqueta para establecer, en la instanciación del Fragmento, el identificador del partido
     * para el que se va a crear el usuario simulado
     */
    public static final String BUNDLE_EVENT_ID = "BUNDLE_EVENT_ID";

    /**
     * Identificador necesario para el proceso de selección de imagen de la librería EasyImage
     *
     * @see
     * <a href= "https://github.com/jkwiecien/EasyImage">
     *      EasyImage (Github)
     * </a>
     */
    public static final int RC_PHOTO_PICKER = 2;

    /**
     * Presentador correspondiente a esta Vista
     */
    SimulateParticipantContract.Presenter mPresenter;

    /**
     * Referencia a la imagen de la interfaz donde se muestra la foto del usuario simulado
     */
    @BindView(R.id.new_simulated_user_photo)
    ImageView simulatedUserPhoto;
    /**
     * Referencia a la imagen de la interfaz que se usa a modo de botón que inicia el proceso de
     * selección de foto para el usuario simulado
     */
    @BindView(R.id.new_simulated_user_photo_button)
    ImageView simulatedUserPhotoButton;
    /**
     * Ruta del almacenamiento interno donde se encuentra la foto que se va a usar para el usuario
     * simulado
     */
    Uri photoUri = null;
    /**
     * Referencia al elemento de la interfaz utilizado para establecer el nombre del usuario simulado
     */
    @BindView(R.id.new_simulated_user_name)
    EditText simulatedUserName;
    /**
     * Referencia al elemento de la interfaz utilizado para establecer la edad del usuario simulado
     */
    @BindView(R.id.new_simulated_user_age)
    EditText simulatedUserAge;

    /**
     * Constructor sin argumentos
     */
    public SimulateParticipantFragment() {
    }


    /**
     * Método de instanciación del Fragmento
     *
     * @param eventId identificador del partido al que se añade el usuario simulado
     *
     * @return una nueva instancia de SimulateParticipantFragment
     */
    public static SimulateParticipantFragment newInstance(@NonNull String eventId) {
        SimulateParticipantFragment fragment = new SimulateParticipantFragment();
        Bundle b = new Bundle();
        b.putString(BUNDLE_EVENT_ID, eventId);
        fragment.setArguments(b);
        return fragment;
    }

    /**
     * Inicializa el Presentador correspondiente a esta Vista.
     *
     * @param savedInstanceState estado del Fragmento guardado en una posible rotación de
     *                           la pantalla, o null.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        mPresenter = new SimulateParticipantPresenter(this);
    }

    /**
     * Inicializa el contenido del menú de opciones de la esquina superior derecha de la pantalla
     * añadiendo un botón para finalizar el proceso.
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
     * Invocado cuando un elemento del menú es pulsado. En este caso se encarga de iniciar el
     * proceso de creación enviando los datos introducidos al Presentador.
     *
     * @param item elemento del menú pulsado
     *
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

            mPresenter.addSimulatedParticipant(
                    eventId,
                    simulatedUserName.getText().toString(),
                    photoUri,
                    simulatedUserAge.getText().toString());
            return true;
        }
        return false;
    }

    /**
     * Inicializa y obtiene una referencia a los elementos de la interfaz con la ayuda de
     * ButterKnife. Establece que al pulsar {@link #simulatedUserPhotoButton} se inicie el proceso
     * por el que se elige una foto con la ayuda de EasyImage.*
     *
     * @param inflater utilizado para inflar el archivo de layout
     * @param container contenedor donde se va a incluir la interfaz o null
     * @param savedInstanceState estado del Fragmento guardado en una posible rotación de
     *                           la pantalla, o null.
     *
     * @return la vista de la interfaz inicializada
     *
     * @see
     * <a href= "http://jakewharton.github.io/butterknife/">
     *     ButterKnife
     * </a>
     * @see
     * <a href= "https://github.com/jkwiecien/EasyImage">
     *     EasyImage (Github)
     * </a>
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_new_simulated_user, container, false);
        ButterKnife.bind(this, root);

        simulatedUserPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EasyImage.configuration(getActivity())
                        .setImagesFolderName(Environment.DIRECTORY_PICTURES)
                        .saveInAppExternalFilesDir();
                if (Utiles.isStorageCameraPermissionGranted(getActivity()))
                    EasyImage.openChooserWithGallery(getActivity(),
                            getString(R.string.pick_photo_from), RC_PHOTO_PICKER);
            }
        });
        return root;
    }

    /**
     * Al finalizar el proceso de creación de la Actividad contenedora, se invoca este método que
     * establece un título para la barra superior y la acción que debe realizar: navegar hacia atrás.
     *
     * @param savedInstanceState estado del Fragmento guardado en una posible rotación de
     *                           la pantalla, o null.
     */
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mFragmentManagementListener.setCurrentDisplayedFragment(getString(R.string.new_simulated_user), this);
        mNavigationDrawerManagementListener.setToolbarAsUp();
    }

    /**
     * Invoca el método que muestra el contenido de la interfaz
     */
    @Override
    public void onResume() {
        super.onResume();
        showContent();
    }

    /**
     * Recibe la ruta hacia el archivo que se utilizará como imagen del usuario simulado y la carga
     * en {@link #simulatedUserPhoto} con ayuda de Glide.
     *
     * @param photoCroppedUri ruta del archivo
     *
     * @see
     * <a href= "https://bumptech.github.io/glide/javadocs/380/index.html">
     *     Glide
     * </a>
     */
    @Override
    public void croppedResult(Uri photoCroppedUri) {
        // Uri from cropped photo as result of UCrop
        photoUri = photoCroppedUri;
        if (photoUri != null)
            Glide.with(this)
                    .load(photoUri)
                    .placeholder(R.drawable.profile_picture_placeholder)
                    .centerCrop()
                    .into(simulatedUserPhoto);
    }

    /**
     * Muestra el mensaje especificado en la interfaz mediante un {@link Toast}. Aunque la llamada
     * se produzca desde otro hilo, la operación sobre la interfaz para mostrar  el mensaje debe
     * ejecutarse desde el hilo principal.
     *
     * @param msgResource identificador del recurso de texto correspondiente al mensaje que se
     *                    quiere mostrar
     *
     * @see
     * <a href="https://developer.android.com/reference/android/app/Activity.html#runOnUiThread(java.lang.Runnable)">
     *     runOnUiThread(java.lang.Runnable)
     * </a>
     */
    @Override
    public void showMsgFromBackgroundThread(final int msgResource) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showMsgFromUIThread(msgResource);
                getActivity().onBackPressed();
            }
        });
    }

    /**
     * Muestra el mensaje especificado en la interfaz mediante un {@link Toast}.
     *
     * @param msgResource identificador del recurso de texto correspondiente al mensaje que se
     *                    quiere mostrar
     */
    @Override
    public void showMsgFromUIThread(int msgResource) {
        if (msgResource != -1)
            Toast.makeText(getActivity(), msgResource, Toast.LENGTH_SHORT).show();
    }
}
