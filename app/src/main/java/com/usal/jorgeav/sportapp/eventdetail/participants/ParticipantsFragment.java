package com.usal.jorgeav.sportapp.eventdetail.participants;

import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.usal.jorgeav.sportapp.BaseFragment;
import com.usal.jorgeav.sportapp.R;
import com.usal.jorgeav.sportapp.adapters.SimulatedUsersAdapter;
import com.usal.jorgeav.sportapp.adapters.UsersAdapter;
import com.usal.jorgeav.sportapp.eventdetail.DetailEventPresenter;
import com.usal.jorgeav.sportapp.eventdetail.simulateparticipant.SimulateParticipantFragment;
import com.usal.jorgeav.sportapp.mainactivities.EventsActivity;
import com.usal.jorgeav.sportapp.profile.ProfileFragment;
import com.usal.jorgeav.sportapp.utils.Utiles;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Fragmento utilizado para mostrar la colección de participantes del partido, tanto usuarios
 * de la aplicación, como usuarios simulados añadidos por los primeros.
 * <p>
 * Este Fragmento se encarga de inicializar los componentes de la interfaz para mostrar esa
 * colección en dos listas, con la ayuda de {@link UsersAdapter} y {@link SimulatedUsersAdapter}.
 * En la instanciación, se le pasa el tipo de relación del partido con el usuario actual, si el
 * partido está lleno y si el partido pertenece al pasado; es necesario para permitir o no añadir
 * más usuarios simulados.
 * <p>
 * Desde este Fragmento, el creador del partido puede expulsar usuarios y usuarios simulados.
 * Además, cualquier participante puede añadir usuarios simulados, así como eliminarlos, pero sólo
 * a los usuarios simulados que añadió.
 * <p>
 * Implementa la interfaz {@link ParticipantsContract.View} para la comunicación con esta clase,
 * la interfaz {@link UsersAdapter.OnUserItemClickListener} para manejar la pulsación sobre cada
 * uno de los usuarios participantes y la interfaz
 * {@link SimulatedUsersAdapter.OnSimulatedUserItemClickListener}
 * para manejar la pulsación sobre cada uno de los usuarios simulados.
 */
public class ParticipantsFragment extends BaseFragment implements
        ParticipantsContract.View,
        UsersAdapter.OnUserItemClickListener,
        SimulatedUsersAdapter.OnSimulatedUserItemClickListener {
    /**
     * Nombre de la clase
     */
    private static final String TAG = ParticipantsFragment.class.getSimpleName();
    /**
     * Etiqueta para establecer, en la instanciación del Fragmento, el identificador del partido en
     * el que participan los usuarios
     */
    public static final String BUNDLE_EVENT_ID = "BUNDLE_EVENT_ID";
    /**
     * Etiqueta para establecer, en la instanciación del Fragmento, el identificador del usuario
     * creador del partido en el que participan los usuarios
     */
    public static final String BUNDLE_OWNER_ID = "BUNDLE_OWNER_ID";
    /**
     * Etiqueta para establecer, en la instanciación del Fragmento, el tipo de relación del usuario
     * actual con el partido en el que participan los usuarios
     */
    public static final String BUNDLE_RELATION_TYPE = "BUNDLE_RELATION_TYPE";
    /**
     * Etiqueta para establecer, en la instanciación del Fragmento, si el partido en el que
     * participan los usuarios pertenece a una fecha ya pasada
     */
    public static final String BUNDLE_IS_PAST = "BUNDLE_IS_PAST";
    /**
     * Etiqueta para establecer, en la instanciación del Fragmento, si el partido en el que
     * participan los usuarios está completo y no admite más participantes
     */
    public static final String BUNDLE_IS_FULL = "BUNDLE_IS_FULL";

    /**
     * Presentador correspondiente a esta Vista
     */
    ParticipantsContract.Presenter mParticipantsPresenter;

    /**
     * Identificador del partido
     */
    private static String mEventId = "";
    /**
     * Tipo de relación del usuario actual con el partido
     */
    @DetailEventPresenter.EventRelationType
    private static int mRelation = DetailEventPresenter.RELATION_TYPE_ERROR;
    /**
     * True si el partido pertenece al pasado, false en caso contrario
     */
    private static Boolean isPast = null;
    /**
     * True si el partido está completo, false en caso contrario
     */
    private static Boolean isFull = null;

    /**
     * Adaptador para manejar y emplazar los datos de los usuarios participantes en cada una de las
     * celdas de la lista
     */
    UsersAdapter mParticipantsAdapter;
    /**
     * Referencia al elemento de la interfaz donde se listan los usuarios participantes
     */
    @BindView(R.id.event_participants_list)
    RecyclerView participantsList;
    /**
     * Referencia al elemento de la interfaz que debe mostrarse en caso de que la consulta de
     * participantes no arroje ningún resultado.
     */
    @BindView(R.id.event_participants_placeholder)
    ConstraintLayout participantsPlaceholder;

    /**
     * Adaptador para manejar y emplazar los datos de los usuarios simulados en cada una de las
     * celdas de la lista
     */
    SimulatedUsersAdapter mSimulatedParticipantsAdapter;
    /**
     * Referencia al elemento de la interfaz donde se listan los usuarios simulados
     */
    @BindView(R.id.event_simulated_participants_list)
    RecyclerView simulatedParticipantsList;
    /**
     * Referencia al elemento de la interfaz que debe mostrarse en caso de que la consulta de
     * usuarios simulados no arroje ningún resultado.
     */
    @BindView(R.id.event_simulated_participants_placeholder)
    ConstraintLayout simulatedParticipantsPlaceholder;
    /**
     * Referencia a la imagen de la interfaz que actúa como botón para añadir usuarios simulados
     */
    @BindView(R.id.event_participants_add_simulated)
    ImageView addSimulatedParticipant;

    /**
     * Constructor sin argumentos
     */
    public ParticipantsFragment() {
        // Required empty public constructor
    }

    /**
     * Método de instanciación del Fragmento
     *
     * @param eventID  identificador del partido al que asisten los participantes
     * @param ownerId  identificador del usuario creador del partido
     * @param relation tipo de relación del usuario actual con el partido
     * @param isPast   true si la fecha del partido pertenece al pasado, false en otro caso
     * @param isFull   true si no quedan puestos vacantes en el partido, false en otro caso
     * @return una nueva instancia de ParticipantsFragment
     */
    public static ParticipantsFragment newInstance(@NonNull String eventID, @NonNull String ownerId,
                                                   @DetailEventPresenter.EventRelationType int relation,
                                                   @NonNull Boolean isPast, @NonNull Boolean isFull) {
        Bundle args = new Bundle();

        if (!TextUtils.isEmpty(eventID))
            args.putString(BUNDLE_EVENT_ID, eventID);
        if (!TextUtils.isEmpty(ownerId))
            args.putString(BUNDLE_OWNER_ID, ownerId);
        if (relation != DetailEventPresenter.RELATION_TYPE_ERROR)
            args.putInt(BUNDLE_RELATION_TYPE, relation);

        args.putBoolean(BUNDLE_IS_PAST, isPast);
        args.putBoolean(BUNDLE_IS_FULL, isFull);

        ParticipantsFragment fragment = new ParticipantsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Inicializa el Presentador correspondiente a esta Vista, y los Adaptadores para las
     * colecciones de usuarios normales y usuarios simulados.
     *
     * @param savedInstanceState estado del Fragmento guardado en una posible rotación de
     *                           la pantalla, o null.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mParticipantsPresenter = new ParticipantsPresenter(this);
        mParticipantsAdapter = new UsersAdapter(null, this, Glide.with(this));
        mSimulatedParticipantsAdapter = new SimulatedUsersAdapter(null, this, Glide.with(this));
    }

    /**
     * Inicializa el contenido del menú de opciones de la esquina superior derecha de la pantalla:
     * lo limpia para no mostrar ninguna opción.
     *
     * @param menu menú de opciones donde se van a emplazar los elementos.
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
    }

    /**
     * Inicializa y obtiene una referencia a los elementos de la interfaz con la ayuda de
     * ButterKnife. Establece los adaptadores creados como adaptador de cada lista. Extrae los
     * parámetros incluidos en el Fragmento en su instanciación, para determinar si se permite
     * añadir usuarios simulados y mostrar o no el botón correspondiente en consecuencia.
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
        View root = inflater.inflate(R.layout.fragment_participants, container, false);
        ButterKnife.bind(this, root);

        if (getArguments() != null && getArguments().containsKey(BUNDLE_EVENT_ID))
            mEventId = getArguments().getString(BUNDLE_EVENT_ID);
        // OwnerID is accessed in the Presenter
        if (getArguments() != null && getArguments().containsKey(BUNDLE_RELATION_TYPE))
            mRelation = parseRelation(getArguments().getInt(BUNDLE_RELATION_TYPE));
        if (getArguments() != null && getArguments().containsKey(BUNDLE_IS_PAST))
            isPast = getArguments().getBoolean(BUNDLE_IS_PAST);
        if (getArguments() != null && getArguments().containsKey(BUNDLE_IS_FULL))
            isFull = getArguments().getBoolean(BUNDLE_IS_FULL);

        // Set participants list
        participantsList.setAdapter(mParticipantsAdapter);
        participantsList.setHasFixedSize(true);
        participantsList.setLayoutManager(new GridLayoutManager(getActivityContext(),
                Utiles.calculateNoOfColumns(getActivityContext())));

        // Set simulated participants list
        simulatedParticipantsList.setAdapter(mSimulatedParticipantsAdapter);
        simulatedParticipantsList.setHasFixedSize(true);
        simulatedParticipantsList.setLayoutManager(new LinearLayoutManager(getActivityContext(),
                LinearLayoutManager.VERTICAL, false));

        //Allow add simulated participant if isn't Past and isn't Full and if I am owner or participant
        setLayoutToAllowAddSimulatedParticipants(isPast != null && !isPast && isFull != null && !isFull
                && (mRelation == DetailEventPresenter.RELATION_TYPE_ASSISTANT
                || mRelation == DetailEventPresenter.RELATION_TYPE_OWNER));

        return root;
    }

    /**
     * Oculta o muestra la imagen que sirve como botón para iniciar el proceso de añadir un usuario
     * simulado como participante del partido.
     *
     * @param allow true si se permite añadir usuarios simulados, false en caso contrario
     */
    private void setLayoutToAllowAddSimulatedParticipants(boolean allow) {
        if (allow) {
            addSimulatedParticipant.setVisibility(View.VISIBLE);
            addSimulatedParticipant.setEnabled(true);
            addSimulatedParticipant.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mEventId != null && getActivity() instanceof EventsActivity) { //Necessary for photo picker
                        Fragment fragment = SimulateParticipantFragment.newInstance(mEventId);
                        mFragmentManagementListener.initFragment(fragment, true);
                    } else
                        Log.e(TAG, "addSimulateParticipant: onClick: eventId " + mEventId
                                + "\ngetActivity is EventsActivity? " + (getActivity() instanceof EventsActivity));
                }
            });
        } else {
            addSimulatedParticipant.setVisibility(View.INVISIBLE);
            addSimulatedParticipant.setEnabled(false);
            addSimulatedParticipant.setOnClickListener(null);
        }
    }

    /**
     * Transforma una variable entera en una del tipo {@link DetailEventPresenter.EventRelationType}
     * asegurando que ese número entero representa a algún tipo de relación o error en otro caso.
     *
     * @param relation variable entera que se va a transformar
     * @return <var>relation</var> transformada a variable de tipo {@link DetailEventPresenter.EventRelationType}
     */
    private
    @DetailEventPresenter.EventRelationType
    int parseRelation(int relation) {
        switch (relation) {
            default:
            case -1:
                return DetailEventPresenter.RELATION_TYPE_ERROR;
            case 0:
                return DetailEventPresenter.RELATION_TYPE_NONE;
            case 1:
                return DetailEventPresenter.RELATION_TYPE_OWNER;
            case 2:
                return DetailEventPresenter.RELATION_TYPE_I_SEND_REQUEST;
            case 3:
                return DetailEventPresenter.RELATION_TYPE_I_RECEIVE_INVITATION;
            case 4:
                return DetailEventPresenter.RELATION_TYPE_ASSISTANT;
            case 5:
                return DetailEventPresenter.RELATION_TYPE_BLOCKED;
        }
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
        mFragmentManagementListener.setCurrentDisplayedFragment(getString(R.string.event_participants), this);
        mNavigationDrawerManagementListener.setToolbarAsUp();
    }

    /**
     * Ordena al Presentador que inicie el proceso de carga de los participantes, los usuarios
     * normales y los usuarios simulados, que se encuentren en la base de datos.
     */
    @Override
    public void onStart() {
        super.onStart();
        mParticipantsPresenter.loadParticipants(getLoaderManager(), getArguments());
        mParticipantsPresenter.loadSimulatedParticipants(getLoaderManager(), getArguments());
    }

    /**
     * Borra los usuarios almacenados en los Adaptadores para que no se guarden en el estado del
     * Fragmento. Son recuperados inmediatamente al volver a mostrar el Fragmento por estar
     * usando el mismo Loader.
     */
    @Override
    public void onPause() {
        super.onPause();
        mParticipantsAdapter.replaceData(null);
        mSimulatedParticipantsAdapter.replaceData(null);
    }

    /**
     * Establece en el Adaptador de la lista de participantes los usuarios contenidos en el
     * {@link Cursor} y, si no está vacío, muestra la lista; si está vacío, muestra una imagen
     * que lo indica
     *
     * @param cursor usuarios participantes obtenidos en la consulta
     */
    @Override
    public void showParticipants(Cursor cursor) {
        mParticipantsAdapter.replaceData(cursor);
        if (cursor != null && cursor.getCount() > 0) {
            participantsList.setVisibility(View.VISIBLE);
            participantsPlaceholder.setVisibility(View.INVISIBLE);
        } else {
            participantsList.setVisibility(View.INVISIBLE);
            participantsPlaceholder.setVisibility(View.VISIBLE);
        }
        mFragmentManagementListener.showContent();
    }

    /**
     * Establece en el Adaptador de la lista de usuarios simulados los usuarios contenidos en el
     * {@link Cursor} y, si no está vacío, muestra la lista; si está vacío, muestra una imagen
     * que lo indica
     *
     * @param cursor usuarios simulados obtenidos en la consulta
     */
    @Override
    public void showSimulatedParticipants(Cursor cursor) {
        mSimulatedParticipantsAdapter.replaceData(cursor);
        if (cursor != null && cursor.getCount() > 0) {
            simulatedParticipantsList.setVisibility(View.VISIBLE);
            simulatedParticipantsPlaceholder.setVisibility(View.INVISIBLE);
        } else {
            simulatedParticipantsList.setVisibility(View.INVISIBLE);
            simulatedParticipantsPlaceholder.setVisibility(View.VISIBLE);
        }
        mFragmentManagementListener.showContent();
    }

    /**
     * Al realizar una pulsación normal sobre un usuario: Inicia la transición a otro Fragmento con
     * la intención de mostrar el perfil del usuario.
     *
     * @param uid Identificador del usuario pulsado
     */
    @Override
    public void onUserClick(String uid) {
        String myUid = Utiles.getCurrentUserId();
        if (TextUtils.isEmpty(myUid)) return;

        if (!myUid.equals(uid)) {
            Fragment newFragment = ProfileFragment.newInstance(uid);
            mFragmentManagementListener.initFragment(newFragment, true);
        }
    }

    /**
     * Al realizar una pulsación larga sobre un usuario: se crea y muestra un cuadro de diálogo
     * preguntando si se le quiere expulsar. También ofrece la opción de mostrar el perfil del
     * usuario pulsado.
     * <p>
     * Si se expulsa al usuario, se crea y muestra otro cuadro de diálogo preguntando si también
     * se desea borrar a los usuarios simulados que fueron añadidos por el usuario recién expulsado.
     *
     * @param uid Identificador del usuario pulsado
     */
    @Override
    public boolean onUserLongClick(final String uid) {
        String myUid = Utiles.getCurrentUserId();
        if (TextUtils.isEmpty(myUid)) return true;

        if (!myUid.equals(uid) && mRelation == DetailEventPresenter.RELATION_TYPE_OWNER && !isPast) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivityContext())
                    .setMessage(R.string.dialog_msg_quit_user_from_event)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivityContext())
                                    .setMessage(R.string.dialog_msg_quit_user_from_event_sim_user)
                                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            mParticipantsPresenter.quitEvent(uid, mEventId, true);
                                        }
                                    })
                                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            mParticipantsPresenter.quitEvent(uid, mEventId, false);
                                        }
                                    });
                            builder.create().show();
                        }
                    })
                    .setNegativeButton(android.R.string.no, null)
                    .setNeutralButton(R.string.see_details, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Fragment newFragment = ProfileFragment.newInstance(uid);
                            mFragmentManagementListener.initFragment(newFragment, true);
                        }
                    });
            builder.create().show();
        }
        return true;
    }

    /**
     * Al realizar una pulsación normal sobre un usuario simulado: se crea y muestra un cuadro de
     * diálogo preguntando si se le quiere expulsar, sólo en el caso de que el usuario actual sea
     * el creador del usuario simulado o el creador del partido.
     *
     * @param simulatedUserCreator Identificador del usuario creador del usuario simulado
     * @param simulatedUserId      Identificador del usuario simulado pulsado
     */
    @Override
    public void onSimulatedUserClick(String simulatedUserCreator, final String simulatedUserId) {
        String myUid = Utiles.getCurrentUserId();
        if (TextUtils.isEmpty(myUid)) return;

        if ((myUid.equals(simulatedUserCreator) || mRelation == DetailEventPresenter.RELATION_TYPE_OWNER) && !isPast) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivityContext())
                    .setMessage(R.string.dialog_msg_quit_sim_user_from_event)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            mParticipantsPresenter.deleteSimulatedUser(simulatedUserId, mEventId);
                        }
                    })
                    .setNegativeButton(android.R.string.no, null);
            builder.create().show();
        }
    }
}
