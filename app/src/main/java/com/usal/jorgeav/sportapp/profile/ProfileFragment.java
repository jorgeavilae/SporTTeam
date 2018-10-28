package com.usal.jorgeav.sportapp.profile;


import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.usal.jorgeav.sportapp.BaseFragment;
import com.usal.jorgeav.sportapp.MyApplication;
import com.usal.jorgeav.sportapp.R;
import com.usal.jorgeav.sportapp.adapters.ProfileSportsAdapter;
import com.usal.jorgeav.sportapp.data.Sport;
import com.usal.jorgeav.sportapp.profile.friendrequests.FriendRequestsFragment;
import com.usal.jorgeav.sportapp.profile.invitationreceived.InvitationsReceivedFragment;
import com.usal.jorgeav.sportapp.profile.sendinvitation.SendInvitationFragment;
import com.usal.jorgeav.sportapp.sportselection.SportsListFragment;
import com.usal.jorgeav.sportapp.utils.Utiles;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import pl.aprilapps.easyphotopicker.EasyImage;

/**
 * Fragmento utilizado para mostrar los detalles del usuario actual o cualquier otro. Se encarga de
 * inicializar los componentes de la interfaz y utilizarlos para mostrar los parámetros del usuario
 * recuperados de la base de datos.
 * <p>
 * Adapta la interfaz, mostrando y ocultando elementos, según la relación del usuario actual con el
 * usuario mostrado. Mantiene, a través del Presentador, un Observer sobre la relación de estos dos
 * usuarios para ser notificado y cambiar la interfaz en caso de que la relación cambie.
 * <p>
 * Si son el mismo usuario, el Fragmento permite editar los parámetros nombre,
 * edad y foto de perfil; también inicia la transición hacia {@link SportsListFragment} para
 * cambiar la lista de deportes practicados. Además, mantiene dos botones que muestran la lista de
 * invitaciones recibidas ({@link InvitationsReceivedFragment}) y la lista de peticiones de amistad
 * recibidas ({@link FriendRequestsFragment}); y otro botón más para mostrar el calendario de
 * partidos ({@link com.usal.jorgeav.sportapp.events.EventsFragment}).
 * <p>
 * Si son usuarios diferentes, existe un botón que realiza acciones diferentes según la relación
 * entre ellos: enviar petición de amistad, cancelarla, contestarla o borrar la amistad dependiendo
 * de si son desconocidos, si el usuario actual envió una petición, de si el usuario actual recibió
 * una petición o de si ya son amigos, respectivamente. Además, se muestran los datos del usuario a
 * modo informativo.
 * <p>
 * Implementa la interfaz {@link ProfileContract.View} para la comunicación con esta clase.
 */
public class ProfileFragment extends BaseFragment implements
        ProfileContract.View {
    /**
     * Nombre de la clase
     */
    @SuppressWarnings("unused")
    private final static String TAG = ProfileFragment.class.getSimpleName();

    /**
     * Identificador para la ejecución de EasyImage
     *
     * @see <a href= "https://github.com/jkwiecien/EasyImage">EasyImage (Github)</a>
     */
    public static final int RC_PHOTO_PICKER = 2;

    /**
     * Etiqueta para establecer el identificador del usuario que debe mostrarse en la instanciación
     * del Fragmento
     */
    public static final String BUNDLE_INSTANCE_UID = "BUNDLE_INSTANCE_UID";

    /**
     * Identificador del usuario que se está mostrando
     */
    private static String mUserUid = "";

    /**
     * Presentador correspondiente a esta Vista
     */
    private ProfileContract.Presenter mProfilePresenter;

    /**
     * Menú del Fragmento que se mostrará sólo en el caso de que se esté mostrando el perfil del
     * usuario actual
     */
    Menu mMenu;

    /**
     * Referencia a la imagen de la interfaz donde se va a mostrar la foto de perfil del usuario
     */
    @BindView(R.id.user_image)
    ImageView userImage;
    /**
     * Referencia al elemento de la interfaz donde se va a mostrar el nombre del usuario
     */
    @BindView(R.id.user_name)
    TextView userName;
    /**
     * Referencia al elemento de la interfaz donde se va a mostrar la edad del usuario
     */
    @BindView(R.id.user_age)
    TextView userAge;
    /**
     * Referencia al elemento de la interfaz donde se va a mostrar la ciudad del usuario
     */
    @BindView(R.id.user_city)
    TextView userCity;
    /**
     * Referencia al botón de la interfaz que muestra el calendario de partidos del usuario actual.
     */
    @BindView(R.id.user_calendar)
    CardView userCalendarButton;

    /**
     * Referencia al cuadro de la interfaz que servirá de botón para mostrar las invitaciones a
     * partidos recibidas
     */
    @BindView(R.id.user_event_invitations)
    CardView userEventInvitationsButton;
    /**
     * Referencia a la imagen que se muestra en {@link #userEventInvitationsButton}
     */
    @BindView(R.id.user_event_invitations_image)
    ImageView userEventInvitationsButtonImage;
    /**
     * Referencia al título que se muestra en {@link #userEventInvitationsButton}
     */
    @BindView(R.id.user_event_invitations_text)
    TextView userEventInvitationsButtonText;

    /**
     * Referencia al cuadro de la interfaz que servirá de botón para mostrar las peticiones de
     * amistad recibidas o para modificar la relación del usuario actual con el usuario mostrado
     * enviando peticiones, contestándolas o borrando la amistad.
     */
    @BindView(R.id.user_friend_requests)
    CardView userFriendRequestsButton;
    /**
     * Referencia a la imagen que se muestra en {@link #userFriendRequestsButton}
     */
    @BindView(R.id.user_friend_requests_image)
    ImageView userFriendRequestsButtonImage;
    /**
     * Referencia al título que se muestra en {@link #userFriendRequestsButton}
     */
    @BindView(R.id.user_friend_requests_text)
    TextView userFriendRequestsButtonText;

    /**
     * Adaptador correspondiente a la colección de deportes practicados por el usuario
     */
    ProfileSportsAdapter sportsAdapter;

    /**
     * Referencia a la lista de la interfaz donde se muestran los deportes practicado por el
     * usuario que se muestra.
     */
    @BindView(R.id.user_sport_list)
    RecyclerView userSportList;
    /**
     * Referencia al elemento de la interfaz que debe mostrarse en caso de que la consulta de
     * deportes no arroje ningún resultado.
     */
    @BindView(R.id.user_sport_placeholder)
    ConstraintLayout userSportPlaceholder;

    /**
     * Constructor sin argumentos
     */
    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Método de instanciación del Fragmento
     *
     * @param uid identificador del usuario que debe mostrar el Fragmento
     * @return una nueva instancia de ProfileFragment
     */
    public static ProfileFragment newInstance(@NonNull String uid) {
        Bundle b = new Bundle();
        b.putString(BUNDLE_INSTANCE_UID, uid);
        ProfileFragment pf = new ProfileFragment();
        pf.setArguments(b);
        return pf;
    }

    /**
     * Inicialización del Presentador correspondiente a esta Vista
     *
     * @param savedInstanceState estado del Fragmento guardado en una posible rotación de
     *                           la pantalla, o null.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        mProfilePresenter = new ProfilePresenter(this);
    }

    /**
     * Obtiene una referencia al menú y lo limpia para establecer su contenido una vez se conozca la
     * relación del usuario actual con el usuario mostrado
     *
     * @param menu     menú de opciones donde se van a emplazar los elementos.
     * @param inflater se utiliza para cargar las opciones de menú
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        mMenu = menu;
        super.onCreateOptionsMenu(mMenu, inflater);
        mMenu.clear();
    }

    /**
     * Invocado cuando un elemento del menú es pulsado. En este caso se encarga de iniciar el
     * proceso de edición de deportes practicados instanciando y mostrando el Fragmento
     * correspondiente, o de iniciar los procesos de edición de imagen de perfil, nombre o edad del
     * usuario actual.
     *
     * @param item elemento del menú pulsado
     * @return true si se aceptó la pulsación, false en otro caso
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if (item.getItemId() == R.id.action_edit_sports) {
            Fragment fragment = SportsListFragment.newInstance(mUserUid,
                    sportsAdapter.getDataAsSportArrayList(), null);
            mFragmentManagementListener.initFragment(fragment, true);
            return true;
        } else if (item.getItemId() == R.id.action_change_image) {
            EasyImage.configuration(getActivity())
                    .setImagesFolderName(Environment.DIRECTORY_PICTURES)
                    .saveInAppExternalFilesDir();
            if (Utiles.isStorageCameraPermissionGranted(getActivity()))
                EasyImage.openChooserWithGallery(getActivity(), getString(R.string.pick_photo_from), RC_PHOTO_PICKER);
            return true;
        } else if (item.getItemId() == R.id.action_change_name) {
            showDialogForEditName();
            return true;
        } else if (item.getItemId() == R.id.action_change_age) {
            showDialogForEditAge();
            return true;
        }
        return false;
    }

    /**
     * Inicializa y obtiene una referencia a los elementos de la interfaz con la ayuda de
     * ButterKnife. Además, establece el Adaptador para la lista de deportes, extrae el identificador
     * del usuario mostrado e inicia el proceso para determinar el aspecto de la interfaz.
     *
     * @param inflater           utilizado para inflar el archivo de layout
     * @param container          contenedor donde se va a incluir la interfaz o null
     * @param savedInstanceState estado del Fragmento guardado en una posible rotación de
     *                           la pantalla, o null.
     * @return la vista de la interfaz inicializada
     * @see <a href= "http://jakewharton.github.io/butterknife/">ButterKnife</a>
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_profile, container, false);
        ButterKnife.bind(this, root);

        if (getArguments() != null && getArguments().containsKey(BUNDLE_INSTANCE_UID))
            mUserUid = getArguments().getString(BUNDLE_INSTANCE_UID);

        sportsAdapter = new ProfileSportsAdapter(null, null,
                Glide.with(this), false);
        userSportList.setAdapter(sportsAdapter);
        userSportList.setHasFixedSize(true);
        userSportList.setLayoutManager(new GridLayoutManager(getActivityContext(), 2, LinearLayoutManager.VERTICAL, false));

        initDefaultLayout();

        return root;
    }

    /**
     * Inicializa el aspecto de la interfaz antes de determinar el tipo de relación gracias a la
     * base de datos.
     * Comprueba si el identificador del usuario que se muestra es el mismo que el del usuario
     * actual, para mostrar la interfaz con los detalles del propio usuario.
     * Si no, se establece una interfaz por defecto esperando por el resultado de la consulta a la
     * base de datos sobre el tipo de relación, que se inicia al final.
     *
     * @see ProfilePresenter#getRelationTypeBetweenThisUserAndI()
     */
    private void initDefaultLayout() {
        String currentUserId = Utiles.getCurrentUserId();
        if (TextUtils.isEmpty(currentUserId)) throw new NullPointerException();

        if (mUserUid.equals(currentUserId)) {
            userCalendarButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mNavigationDrawerManagementListener.simulateNavigationItemSelected(R.id.nav_events,
                            null, null);
                }
            });

            userEventInvitationsButtonText.setText(R.string.event_invitations);
            userEventInvitationsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Fragment fragment = InvitationsReceivedFragment.newInstance();
                    mFragmentManagementListener.initFragment(fragment, true);
                }
            });

            userFriendRequestsButtonText.setText(R.string.friend_requests);
            userFriendRequestsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Fragment fragment = FriendRequestsFragment.newInstance();
                    mFragmentManagementListener.initFragment(fragment, true);
                }
            });
        } else {
            userCalendarButton.setVisibility(View.GONE);
            userEventInvitationsButton.setVisibility(View.INVISIBLE);
            userEventInvitationsButtonText.setText(R.string.send_invitation);
            userEventInvitationsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Fragment fragment = SendInvitationFragment.newInstance(mUserUid);
                    mFragmentManagementListener.initFragment(fragment, true);
                }
            });

            userFriendRequestsButton.setVisibility(View.INVISIBLE);
        }

        mProfilePresenter.getRelationTypeBetweenThisUserAndI();
    }

    /**
     * Crea y muestra un cuadro de diálogo en el que escribir el nuevo nombre del usuario actual.
     * Una vez escrito, con ayuda del Presentador, se comprueba que ese nombre no exista ya en la
     * base de datos y se establece como nuevo nombre del usuario.
     */
    @SuppressLint("InflateParams")
    private void showDialogForEditName() {
        // Prepare View
        final View dialogView = getActivity().getLayoutInflater().inflate(R.layout.edit_text_change_dialog, null);
        final EditText editText = (EditText) dialogView.findViewById(R.id.change_dialog_text);
        editText.setText(userName.getText());
        editText.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        editText.setHint(R.string.prompt_name);

        // Create dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivityContext());
        builder.setView(dialogView);
        builder.setTitle(getString(R.string.dialog_title_change_name))
                .setPositiveButton(android.R.string.ok, null)
                .setNegativeButton(android.R.string.cancel, null);
        final AlertDialog alertDialog = builder.create();
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button button = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);

                // Setting clickListener like this prevent from automatic dismiss Dialog
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (editText.getText().toString().length() > 20) {
                            editText.setError(getString(R.string.error_incorrect_name));
                            return;
                        }

                        mProfilePresenter.checkUserName(editText.getText().toString(), new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    editText.setError(getString(R.string.error_not_unique_name));
                                } else {
                                    mProfilePresenter.updateUserName(editText.getText().toString());
                                    alertDialog.dismiss();
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                            }
                        });
                    }
                });
            }
        });
        alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                hideSoftKeyboard();
            }
        });
        alertDialog.show();
    }

    /**
     * Crea y muestra un cuadro de diálogo en el que escribir la nueva edad del usuario actual.
     * Una vez escrita, con ayuda del Presentador, se establece como nueva edad del usuario.
     */
    @SuppressLint("InflateParams")
    private void showDialogForEditAge() {
        // Prepare View
        final View dialogView = getActivity().getLayoutInflater().inflate(R.layout.edit_text_change_dialog, null);
        final EditText editText = (EditText) dialogView.findViewById(R.id.change_dialog_text);
        editText.setText(userAge.getText());
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        editText.setHint(R.string.prompt_age);

        // Create dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivityContext());
        builder.setView(dialogView);
        builder.setTitle(getString(R.string.dialog_title_change_age))
                .setPositiveButton(android.R.string.ok, null)
                .setNegativeButton(android.R.string.cancel, null);
        final AlertDialog alertDialog = builder.create();
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button button = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);

                // Setting clickListener like this prevent from automatic dismiss Dialog
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (Utiles.isNumeric(editText.getText().toString())) {
                            Integer age = Integer.parseInt(editText.getText().toString());
                            if (age > 12 && age < 100) {
                                mProfilePresenter.updateUserAge(age);
                                alertDialog.dismiss();
                            } else
                                editText.setError(getString(R.string.error_incorrect_age));
                        } else
                            editText.setError(getString(R.string.error_invalid_age));
                    }
                });
            }
        });
        alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                hideSoftKeyboard();
            }
        });
        alertDialog.show();
    }

    /**
     * Invocado al finalizar el proceso de recortar la foto de perfil con uCrop para indicar la
     * ruta del archivo de imagen resultante. Con ayuda del Presentador, establece esta imagen
     * como nueva foto de perfil del usuario en los servidores de la aplicación.
     *
     * @param photoCroppedUri ruta del archivo de imagen de la foto de perfil
     * @see <a href= "https://github.com/Yalantis/uCrop">uCrop (Github)</a>
     */
    @Override
    public void croppedResult(Uri photoCroppedUri) {
        // Uri from cropped photo in filesystem as result of UCrop
        if (photoCroppedUri != null)
            mProfilePresenter.updateUserPhoto(photoCroppedUri);
    }

    /**
     * Modifica el aspecto de la interfaz dependiendo del tipo de relación especificado. Especifica
     * el contenido del menú, el comportamiento del botón {@link #userFriendRequestsButton} y
     * muestra o esconde otros botones.
     *
     * @param relation tipo de relación entre el usuario actual y el usuario mostrado
     */
    @SuppressLint("SwitchIntDef")
    @Override
    public void uiSetupForUserRelation(@ProfilePresenter.UserRelationType int relation) {
        if (relation == ProfilePresenter.RELATION_TYPE_ME) {
            if (mMenu != null && getActivity() != null) {
                mMenu.clear();
                getActivity().getMenuInflater().inflate(R.menu.menu_my_profile, mMenu);
            }
        } else {
            if (mMenu != null) mMenu.clear();

            switch (relation) {
                case ProfilePresenter.RELATION_TYPE_FRIENDS:
                    userEventInvitationsButton.setVisibility(View.VISIBLE);
                    userFriendRequestsButton.setVisibility(View.VISIBLE);
                    userFriendRequestsButtonText.setText(R.string.delete_friend);
                    userFriendRequestsButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivityContext())
                                    .setTitle(R.string.dialog_msg_are_you_sure)
                                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            mProfilePresenter.deleteFriend(mUserUid);
                                        }
                                    })
                                    .setNegativeButton(android.R.string.no, null);
                            builder.create().show();
                        }
                    });
                    break;
                case ProfilePresenter.RELATION_TYPE_I_RECEIVE_REQUEST:
                    userEventInvitationsButton.setVisibility(View.INVISIBLE);
                    userFriendRequestsButton.setVisibility(View.VISIBLE);
                    userFriendRequestsButtonText.setText(R.string.answer_request);
                    userFriendRequestsButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivityContext());
                            builder.setMessage(R.string.dialog_msg_accept_friend)
                                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            mProfilePresenter.acceptFriendRequest(mUserUid);
                                        }
                                    })
                                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            mProfilePresenter.declineFriendRequest(mUserUid);
                                        }
                                    });
                            builder.create().show();
                        }
                    });
                    break;
                case ProfilePresenter.RELATION_TYPE_I_SEND_REQUEST:
                    userEventInvitationsButton.setVisibility(View.INVISIBLE);
                    userFriendRequestsButton.setVisibility(View.VISIBLE);
                    userFriendRequestsButtonText.setText(R.string.friend_request_sent);
                    userFriendRequestsButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mProfilePresenter.cancelFriendRequest(mUserUid);
                        }
                    });
                    break;
                case ProfilePresenter.RELATION_TYPE_NONE:
                    userEventInvitationsButton.setVisibility(View.INVISIBLE);
                    userFriendRequestsButton.setVisibility(View.VISIBLE);
                    userFriendRequestsButtonText.setText(R.string.send_user_request);
                    userFriendRequestsButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mProfilePresenter.sendFriendRequest(mUserUid);
                        }
                    });
                    break;
                case ProfilePresenter.RELATION_TYPE_ERROR:
                    userEventInvitationsButton.setVisibility(View.INVISIBLE);
                    userFriendRequestsButton.setVisibility(View.VISIBLE);
                    userFriendRequestsButton.setOnClickListener(null);
                    userFriendRequestsButtonText.setText(R.string.error);
                    break;
            }
        }
    }

    /**
     * Al finalizar el proceso de creación de la Actividad contenedora, se invoca este método que
     * establece un título para la barra superior y la acción que debe realizar: mostrar el menú de
     * navegación en caso de que se muestre el perfil del usuario actual, o navegar hacia atrás en
     * caso de que se muestre el perfil de otro usuario.
     *
     * @param savedInstanceState estado del Fragmento guardado en una posible rotación de
     *                           la pantalla, o null.
     */
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mFragmentManagementListener.setCurrentDisplayedFragment(getString(R.string.profile), this);
        if (mUserUid.equals(Utiles.getCurrentUserId()))
            mNavigationDrawerManagementListener.setToolbarAsNav();
        else
            mNavigationDrawerManagementListener.setToolbarAsUp();
    }

    /**
     * Pide al Presentador que recupere los parámetros del usuario que se va a mostrar.
     */
    @Override
    public void onStart() {
        super.onStart();
        mProfilePresenter.openUser(getLoaderManager(), getArguments());
    }

    /**
     * Muestra en la interfaz la imagen de perfil del usuario actual.
     * <p>
     * Para cargar la imagen desde una URL de Firebase Storage, utiliza Glide. Además, mediante esta
     * librería, poniendo la imagen sobre un nuevo {@link BitmapImageViewTarget} puede establecerse
     * un comportamiento a la hora de cargarla. En este caso, se crea un {@link RoundedBitmapDrawable}
     * para contener la imagen, de tal forma que la foto de perfil aparece recortada en un círculo,
     * en lugar de aparecer completa y cuadrada.
     * <p>
     * Además, se establece un {@link View.OnClickListener} para mostrar un cuadro de diálogo
     * compuesto únicamente por un {@link ImageView} que contenga la foto de perfil a tamaño
     * completo, para que se pueda ver ampliada.
     *
     * @param image ruta hacia la imagen de perfil dentro de Firebase Storage
     * @see <a href= "https://firebase.google.com/docs/reference/android/com/google/firebase/storage/FirebaseStorage">
     * FirebaseStorage</a>
     * @see <a href= "https://bumptech.github.io/glide/javadocs/380/index.html">Glide</a>
     */
    @SuppressLint("InflateParams")
    @Override
    public void showUserImage(String image) {
        userImage.setVisibility(View.VISIBLE);
        Glide.with(this).load(image).asBitmap()
                .error(R.drawable.profile_picture_placeholder)
                .placeholder(R.drawable.profile_picture_placeholder)
                .into(new BitmapImageViewTarget(userImage) {
                    @Override
                    protected void setResource(Bitmap resource) {
                        // Set a rounded profile image
                        RoundedBitmapDrawable circularBitmapDrawable =
                                RoundedBitmapDrawableFactory.create(MyApplication.getAppContext().getResources(), resource);
                        circularBitmapDrawable.setCircular(true);
                        userImage.setImageDrawable(circularBitmapDrawable);
                        // Bigger profile image on clicks
                        userImage.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                View dialogView = getActivity().getLayoutInflater().inflate(R.layout.image_dialog, null);
                                ImageView imageView = (ImageView) dialogView.findViewById(R.id.image_dialog_image);

                                if (userImage.getDrawable().getCurrent() instanceof RoundedBitmapDrawable) {
                                    Bitmap bmp = ((RoundedBitmapDrawable) userImage.getDrawable().getCurrent()).getBitmap();
                                    imageView.setImageBitmap(bmp);

                                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity()).setView(dialogView);
                                    builder.create().show();
                                }
                            }
                        });
                    }
                });
    }

    /**
     * Muestra en la interfaz el nombre del usuario. También establece su nombre en la barra
     * superior.
     *
     * @param name nombre del usuario
     */
    @Override
    public void showUserName(String name) {
        userName.setText(name);
        mNavigationDrawerManagementListener.setActionBarTitle(name);
    }

    /**
     * Muestra en la interfaz la ciudad del usuario
     *
     * @param city ciudad el usuario
     */
    @Override
    public void showUserCity(String city) {
        userCity.setText(city);
    }

    /**
     * Muestra en la interfaz la edad del usuario
     *
     * @param age edad del usuario
     */
    @Override
    public void showUserAge(int age) {
        if (age > -1) {
            userAge.setText(String.format(Locale.getDefault(), "%2d", age));
        }
    }

    /**
     * Establece en el Adaptador los deportes contenidas en el {@link Cursor} y, si no está vacío,
     * muestra la lista; si está vacío, muestra una imagen indicándolo
     *
     * @param cursor colección de {@link Sport} del usuario
     */
    @Override
    public void showSports(Cursor cursor) {
        sportsAdapter.replaceData(cursor);
        if (cursor != null && cursor.getCount() > 0) {
            userSportList.setVisibility(View.VISIBLE);
            userSportPlaceholder.setVisibility(View.GONE);
        } else {
            userSportList.setVisibility(View.INVISIBLE);
            userSportPlaceholder.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Muestra el contenido del Fragmento pero, además, vuelve a realizar la consulta del tipo de
     * relación entre el usuario mostrado y el usuario actual.
     */
    @Override
    public void showContent() {
        super.showContent();
        mProfilePresenter.getRelationTypeBetweenThisUserAndI();
    }

    /**
     * Limpia la interfaz de los datos del usuario
     */
    @Override
    public void clearUI() {
        this.userImage.setVisibility(View.INVISIBLE);
        this.userName.setText("");
        this.mNavigationDrawerManagementListener.setActionBarTitle("");
        this.userCity.setText("");
        this.userAge.setText("");
    }

    /**
     * Devuelve el identificador del usuario que se está mostrando
     *
     * @return identificador del usuario
     */
    @Override
    public String getUserID() {
        return mUserUid;
    }

    /**
     * Ordena al Presentador que se añada como Observer de la relación entre el usuario y el
     * partido. Provoca que se muestre el menú al girar la pantalla.
     *
     * @see <a href="https://stackoverflow.com/a/17063800/4235666">
     * Call onCreateOptionsMenu() on orientation changes (StackOverflow)</a>
     */
    @Override
    public void onResume() {
        super.onResume();
        mProfilePresenter.registerUserRelationObserver();
        setMenuVisibility(true);
    }

    /**
     * Ordena al Presentador que se borre como Observer de la relación entre el usuario y el partido.
     * Borra los deportes almacenados en el Adaptador para que no se guarden en el estado del
     * Fragmento. Son recuperadas inmediatamente al volver a mostrar el Fragmento por estar
     * usando el mismo Loader.
     */
    @Override
    public void onPause() {
        super.onPause();
        mProfilePresenter.unregisterUserRelationObserver();
        sportsAdapter.replaceData(null);
    }
}
