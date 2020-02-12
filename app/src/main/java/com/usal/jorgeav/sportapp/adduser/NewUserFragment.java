package com.usal.jorgeav.sportapp.adduser;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FetchPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.usal.jorgeav.sportapp.BaseFragment;
import com.usal.jorgeav.sportapp.MyApplication;
import com.usal.jorgeav.sportapp.R;
import com.usal.jorgeav.sportapp.adapters.PlaceAutocompleteAdapter;
import com.usal.jorgeav.sportapp.mainactivities.NewUserActivity;
import com.usal.jorgeav.sportapp.sportselection.SportsListFragment;
import com.usal.jorgeav.sportapp.utils.Utiles;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import pl.aprilapps.easyphotopicker.EasyImage;

/**
 * Fragmento utilizado en la creación de usuarios. Aquí se establecen todos los atributos de
 * un usuario por medio de {@link EditText EditTexts}. Se encarga de inicializar todos esos
 * componentes de la interfaz y validarlos con la ayuda de un {@link NewUserContract.Presenter}.
 * Se encarga de mostrar los errores en la pantalla en caso de que los datos sean incorrectos.
 * Implementa la interfaz {@link NewUserContract.View} para la comunicación con esta clase.
 */
public class NewUserFragment extends BaseFragment implements NewUserContract.View {
    /**
     * Nombre de la clase
     */
    private static final String TAG = NewUserFragment.class.getSimpleName();

    /**
     * Identificador para la ejecución de EasyImage
     *
     * @see <a href= "https://github.com/jkwiecien/EasyImage">EasyImage (Github)</a>
     */
    public static final int RC_PHOTO_PICKER = 2;

    /**
     * Presentador correspondiente a esta Vista
     */
    private NewUserContract.Presenter mPresenter;

    /**
     * Ruta del archivo de imagen resultante después del proceso de recorte con uCrop
     *
     * @see <a href= "https://github.com/Yalantis/uCrop">uCrop (Github)</a>
     */
    Uri croppedImageUri;

    /**
     * Objeto PlacesClient necesario para utilizar Google Places API
     */
    private static PlacesClient mPlacesClient;

    /**
     * Adaptador para el {@link AutoCompleteTextView} de ciudades
     */
    PlaceAutocompleteAdapter mAdapter;
    /**
     * Nombre de la ciudad escogida por el usuario
     */
    String newUserCitySelectedName;
    /**
     * Coordenadas del centro de la ciudad escogida por el usuario
     */
    LatLng newUserCitySelectedCoord;

    /**
     * Referencia al elemento de la interfaz para dirección de correo del usuario
     */
    @BindView(R.id.new_user_email)
    EditText newUserEmail;
    /**
     * Referencia al elemento de la interfaz para contraseña del usuario
     */
    @BindView(R.id.new_user_password)
    EditText newUserPassword;
    /**
     * Referencia al elemento de la interfaz para que al pulsar sobre esta imagen se
     * muestre la contraseña escrita
     */
    @BindView(R.id.new_user_visible_pass)
    ImageView newUserVisiblePass;
    /**
     * Referencia al elemento de la interfaz para nombre del usuario
     */
    @BindView(R.id.new_user_name)
    EditText newUserName;
    /**
     * Referencia al elemento de la interfaz para edad del usuario
     */
    @BindView(R.id.new_user_age)
    EditText newUserAge;
    /**
     * Referencia al elemento de la interfaz para ciudad del usuario
     */
    @BindView(R.id.new_user_city)
    AutoCompleteTextView newUserAutocompleteCity;
    /**
     * Referencia al elemento de la interfaz para mostrar la imagen de perfil
     */
    @BindView(R.id.new_user_photo)
    ImageView newUserPhoto;
    /**
     * Referencia al botón de la interfaz para iniciar el proceso de selección de foto de perfil
     */
    @BindView(R.id.new_user_photo_button)
    ImageView newUserPhotoButton;

    /**
     * Constructor sin parámetros
     */
    public NewUserFragment() {
    }

    /**
     * Método de instanciación del Fragmento
     *
     * @return una nueva instancia de NewUserFragment
     */
    public static NewUserFragment newInstance() {
        return new NewUserFragment();
    }

    /**
     * En este método se inicializan la variable que permite utilizar la API de Google y el
     * Presentador correspondiente a esta Vista.
     *
     * @param savedInstanceState estado del Fragmento guardado en una posible rotación de
     *                           la pantalla, o null.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        Context context = MyApplication.getAppContext();
        Places.initialize(getActivity(), getString(R.string.google_maps_and_geocoding_key));
        mPlacesClient = Places.createClient(context);

        mPresenter = new NewUserPresenter(this);
    }

    /**
     * Inicializa el contenido del menú de opciones de la esquina superior derecha de la pantalla
     *
     * @param menu menú de opciones donde se van a emplazar los elementos.
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        getActivity().getMenuInflater().inflate(R.menu.menu_ok, menu);
    }

    /**
     * Invocado cuando un elemento del menú es pulsado. En este caso se encarga de validar los
     * datos del proceso de creación e iniciar la selección de deportes.
     *
     * @param item elemento del menú pulsado
     * @return true si se aceptó la pulsación, false en otro caso
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if (item.getItemId() == R.id.action_ok) {
            hideSoftKeyboard();

            if (TextUtils.isEmpty(newUserEmail.getError())
                    && TextUtils.isEmpty(newUserPassword.getError())
                    && TextUtils.isEmpty(newUserName.getError())
                    && TextUtils.isEmpty(newUserAge.getError())
                    && !TextUtils.isEmpty(newUserEmail.getText())
                    && !TextUtils.isEmpty(newUserPassword.getText())
                    && !TextUtils.isEmpty(newUserName.getText())
                    && !TextUtils.isEmpty(newUserAge.getText())
                    && newUserCitySelectedName != null && newUserCitySelectedCoord != null) {

                SportsListFragment slf = SportsListFragment.newInstance("",
                        ((NewUserActivity) getActivity()).sports, null);
                mFragmentManagementListener.initFragment(slf, true);

            } else {
                Toast.makeText(getActivity(), R.string.toast_invalid_arg, Toast.LENGTH_SHORT).show();
            }
            return true;
        }
        return false;
    }

    /**
     * Inicializa y obtiene una referencia a los elementos de la interfaz con la ayuda de
     * ButterKnife.
     *
     * @param inflater           utilizado para inflar el archivo de layout
     * @param container          contenedor donde se va a incluir la interfaz o null
     * @param savedInstanceState estado del Fragmento guardado en una posible rotación de
     *                           la pantalla, o null.
     * @return la vista de la interfaz inicializada
     * @see <a href= "http://jakewharton.github.io/butterknife/">ButterKnife</a>
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_new_user, container, false);
        ButterKnife.bind(this, root);

        return root;
    }

    /**
     * Establece los controles para regular el comportamiento del {@link EditText} donde se
     * escribe la dirección de correo del usuario
     */
    private void setEmailEditText() {
        newUserEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus)
                    mPresenter.checkUserEmailExists(newUserEmail.getText().toString());
            }
        });
    }

    /**
     * Establece los controles para regular el comportamiento del {@link EditText} donde se
     * escribe la contraseña del usuario
     */
    @SuppressLint("ClickableViewAccessibility")
    private void setPasswordEditText() {
        newUserPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus)
                    if (newUserPassword.getText().toString().length() > 0
                            && newUserPassword.getText().toString().length() < 6)
                        newUserPassword.setError(getString(R.string.error_invalid_password));
            }
        });

        newUserVisiblePass.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN)
                    newUserPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                else if (event.getAction() == MotionEvent.ACTION_UP)
                    newUserPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                return true;
            }
        });
    }


    /**
     * Establece los controles para regular el comportamiento del {@link ImageView} donde se
     * pulsa para iniciar el proceso de selección de foto de perfil
     */
    private void setPhotoButton() {
        if (croppedImageUri != null)
            Glide.with(this).load(croppedImageUri).into(newUserPhoto);

        newUserPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideSoftKeyboard();
                EasyImage.configuration(getActivity())
                        .setImagesFolderName(Environment.DIRECTORY_PICTURES)
                        .saveInAppExternalFilesDir();
                if (Utiles.isStorageCameraPermissionGranted(getActivity()))
                    EasyImage.openChooserWithGallery(getActivity(), getString(R.string.pick_photo_from), RC_PHOTO_PICKER);
            }
        });
    }


    /**
     * Establece los controles para regular el comportamiento del {@link EditText} donde se
     * escribe el nombre del usuario
     */
    private void setNameEditText() {
        newUserName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus) {
                    if (newUserName.getText().toString().length() > 20)
                        newUserName.setError(getString(R.string.error_incorrect_name));
                    mPresenter.checkUserNameExists(newUserName.getText().toString());
                }
            }
        });
    }


    /**
     * Establece los controles para regular el comportamiento del {@link EditText} donde se
     * escribe la edad del usuario
     */
    private void setAgeEditText() {
        newUserAge.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus)
                    if (!TextUtils.isEmpty(newUserAge.getText())) {
                        Long age = Long.parseLong(newUserAge.getText().toString());
                        if (age <= 12 || age >= 100)
                            newUserAge.setError(getString(R.string.error_invalid_age));
                    }
            }
        });
    }

    /**
     * Inicializa el cliente PlacesClient para poder utilizar Google Places API. Establece los
     * controles para regular el comportamiento del {@link #newUserAutocompleteCity} donde se
     * escribe la ciudad. Crea un {@link TextWatcher} para reaccionar a los cambios en el texto y
     * así realizar nuevas búsquedas con el {@link PlaceAutocompleteAdapter}.
     * Cuando se selecciona una de las ciudades sugeridas, se realiza una consulta a la Google
     * Places API para obtener la coordenadas de dicha ciudad.
     *
     * @see <a href= "https://developers.google.com/places/android-sdk/intro">
     * Places SDK for Android</a>
     */
    private void setAutocompleteTextView() {
        final PlaceAutocompleteAdapter adapter = new PlaceAutocompleteAdapter(getContext(), mPlacesClient, null);
        newUserAutocompleteCity.setAdapter(adapter);

        newUserAutocompleteCity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                newUserCitySelectedName = null;
                newUserCitySelectedCoord = null;
                newUserAutocompleteCity.setError(getString(R.string.error_invalid_city));
            }
        });

        newUserAutocompleteCity.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                /*
                 Retrieve the place ID of the selected item from the Adapter.
                 The adapter stores each Place suggestion in a AutocompletePrediction from which we
                 read the place ID and title.
                  */
                AutocompletePrediction item = adapter.getItem(position);
                if (item != null) {
                    Log.i(TAG, "Autocomplete item selected: " + item.getPlaceId());
                    final ProgressDialog progressDialog = new ProgressDialog(getContext());
                    progressDialog.show();

                    // Fetch Place Details from Places API
                    String placeId = item.getPlaceId();
                    List<Place.Field> placeFields = Arrays.asList(Place.Field.NAME, Place.Field.LAT_LNG);
                    FetchPlaceRequest request = FetchPlaceRequest.newInstance(placeId, placeFields);

                    mPlacesClient.fetchPlace(request).addOnSuccessListener(
                            new OnSuccessListener<FetchPlaceResponse>() {
                                @Override
                                public void onSuccess(FetchPlaceResponse response) {
                                    // Stop UI until finish callback
                                    progressDialog.dismiss();

                                    Place myPlace = response.getPlace();
                                    newUserCitySelectedName = myPlace.getName();
                                    newUserCitySelectedCoord = myPlace.getLatLng();
                                    newUserAutocompleteCity.setError(null);
                                    Log.i(TAG, "Place found: Name - " + myPlace.getName()
                                            + " LatLng - " + myPlace.getLatLng());
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Stop UI until finish callback
                            progressDialog.dismiss();

                            Toast.makeText(getContext(),
                                    R.string.error_check_conn, Toast.LENGTH_SHORT).show();

                            if (exception instanceof ApiException) {
                                ApiException apiException = (ApiException) exception;
                                Log.e(TAG, "Place not found: " + apiException.getMessage());
                            }
                        }
                    });
                }
            }
        });
    }

    /**
     * Al finalizar el proceso de creación de la Actividad contenedora, se invoca este método que
     * establece un título para la barra superior.
     *
     * @param savedInstanceState estado del Fragmento guardado en una posible rotación de
     *                           la pantalla, o null.
     */
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mFragmentManagementListener.setCurrentDisplayedFragment(getString(R.string.new_user_title), this);
    }

    /**
     * Establece los comportamientos de los elementos de la interfaz. Si todos los datos son válidos
     * y los deportes practicados están introducidos, invoca el proceso de creación de usuario con
     * el Presentador.
     */
    @Override
    public void onStart() {
        super.onStart();

        setEmailEditText();
        setPasswordEditText();
        setPhotoButton();
        setNameEditText();
        setAgeEditText();
        setAutocompleteTextView();

        if (((NewUserActivity) getActivity()).sports != null && ((NewUserActivity) getActivity()).sportsInitialize) {
            hideContent();

            if (TextUtils.isEmpty(newUserEmail.getError())
                    && TextUtils.isEmpty(newUserPassword.getError())
                    && TextUtils.isEmpty(newUserName.getError())
                    && TextUtils.isEmpty(newUserAge.getError())) {

                ((NewUserActivity) getActivity()).sportsInitialize = mPresenter.createAuthUser(
                        newUserEmail.getText().toString(),
                        newUserPassword.getText().toString(),
                        newUserName.getText().toString(),
                        croppedImageUri,
                        newUserAge.getText().toString(),
                        newUserCitySelectedName,
                        newUserCitySelectedCoord,
                        ((NewUserActivity) getActivity()).sports);
            } else {
                showContent();
                Toast.makeText(getActivity(), R.string.toast_invalid_arg, Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Muestra el contenido del Fragmento
     */
    @Override
    public void onResume() {
        super.onResume();
        mFragmentManagementListener.showContent();
    }

    /**
     * Invocado al finalizar el proceso de recortar la foto de perfil con uCrop
     * para indicar la ruta del archivo de imagen resultante.
     *
     * @param photoCroppedUri ruta del archivo de imagen de la foto de perfil
     * @see <a href= "https://github.com/Yalantis/uCrop">uCrop (Github)</a>
     */
    @Override
    public void croppedResult(Uri photoCroppedUri) {
        // Uri from cropped photo as result of UCrop
        croppedImageUri = photoCroppedUri;
        if (croppedImageUri != null)
            Glide.with(this)
                    .load(croppedImageUri)
                    .placeholder(R.drawable.profile_picture_placeholder)
                    .centerCrop()
                    .into(newUserPhoto);
    }

    /**
     * Invocado para obtener una referencia a la {@link Activity} contenedora
     *
     * @return Actividad contenedora
     */
    @Override
    public Activity getHostActivity() {
        return getActivity();
    }

    /**
     * Invocado para establecer un error en la dirección de email introducida
     *
     * @param stringRes identificador del recurso de texto utilizado para indicar el error
     */
    @Override
    public void setEmailError(int stringRes) {
        this.newUserEmail.setError(getString(stringRes));
    }

    /**
     * Invocado para establecer un error en el nombre introducido
     *
     * @param stringRes identificador del recurso de texto utilizado para indicar el error
     */
    @Override
    public void setNameError(int stringRes) {
        this.newUserName.setError(getString(stringRes));
    }
}
