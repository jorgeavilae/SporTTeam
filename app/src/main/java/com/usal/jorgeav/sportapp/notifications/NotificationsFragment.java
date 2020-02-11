package com.usal.jorgeav.sportapp.notifications;

import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.usal.jorgeav.sportapp.BaseFragment;
import com.usal.jorgeav.sportapp.R;
import com.usal.jorgeav.sportapp.adapters.MyNotificationsAdapter;
import com.usal.jorgeav.sportapp.alarms.alarmdetail.DetailAlarmFragment;
import com.usal.jorgeav.sportapp.data.MyNotification;
import com.usal.jorgeav.sportapp.eventdetail.DetailEventFragment;
import com.usal.jorgeav.sportapp.network.firebase.FirebaseDBContract;
import com.usal.jorgeav.sportapp.profile.ProfileFragment;
import com.usal.jorgeav.sportapp.utils.Utiles;

import java.util.LinkedHashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Fragmento utilizado para mostrar la colección de notificaciones recibidas por el usuario actual.
 * Se encarga de inicializar los componentes de la interfaz para mostrar la colección con la ayuda
 * de {@link MyNotificationsAdapter}. También permite borrar una o todas las notificaciones.
 * <p>
 * Implementa la interfaz {@link NotificationsContract.View} para la comunicación con esta clase y la
 * interfaz {@link MyNotificationsAdapter.OnMyNotificationItemClickListener} para manejar la
 * pulsación sobre una notificación de la colección.
 */
public class NotificationsFragment extends BaseFragment implements
        NotificationsContract.View,
        MyNotificationsAdapter.OnMyNotificationItemClickListener {
    /**
     * Nombre de la clase
     */
    @SuppressWarnings("unused")
    private static final String TAG = NotificationsFragment.class.getSimpleName();

    /**
     * Presentador correspondiente a esta Vista
     */
    NotificationsContract.Presenter mPresenter;

    /**
     * Adaptador para la colección de notificaciones que se muestra
     */
    MyNotificationsAdapter myNotificationsAdapter;

    /**
     * Referencia a la lista de la interfaz donde se muestran las notificaciones encontradas
     */
    @BindView(R.id.recycler_list)
    RecyclerView notificationsRecyclerList;
    /**
     * Referencia al contenedor de la interfaz mostrado en caso de que no exista ninguna notificación
     */
    @BindView(R.id.list_placeholder)
    ConstraintLayout notificationsPlaceholder;

    /**
     * Constructor sin argumentos
     */
    public NotificationsFragment() {
        // Required empty public constructor
    }

    /**
     * Método de instanciación del Fragmento
     *
     * @return una nueva instancia de NotificationsFragment
     */
    public static NotificationsFragment newInstance() {
        return new NotificationsFragment();
    }

    /**
     * Inicializa el Presentador correspondiente a esta vista, y el Adaptador para la colección de
     * notificaciones.
     *
     * @param savedInstanceState estado del Fragmento guardado en una posible rotación de
     *                           la pantalla, o null.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        mPresenter = new NotificationsPresenter(this);
        myNotificationsAdapter = new MyNotificationsAdapter(null, this, Glide.with(this));
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
        inflater.inflate(R.menu.menu_notifications, menu);
    }

    /**
     * Invocado cuando un elemento del menú es pulsado. En este caso se encarga de crear y mostrar
     * el diálogo que permite borrar todas las notificaciones.
     *
     * @param item elemento del menú pulsado
     * @return true si se aceptó la pulsación, false en otro caso
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if (item.getItemId() == R.id.action_clear_notifications) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivityContext())
                    .setTitle(R.string.dialog_msg_are_you_sure)
                    .setMessage(R.string.dialog_msg_delete_all_notifications)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            mPresenter.deleteAllNotifications();
                        }
                    })
                    .setNegativeButton(android.R.string.no, null);
            builder.create().show();
            return true;
        }
        return false;
    }

    /**
     * Inicializa y obtiene una referencia a los elementos de la interfaz con la ayuda de
     * ButterKnife. Establece el adaptador creado como adaptador de la lista de la interfaz recién
     * inflada.
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
        View root = inflater.inflate(R.layout.fragment_list, container, false);
        ButterKnife.bind(this, root);

        notificationsRecyclerList.setAdapter(myNotificationsAdapter);
        notificationsRecyclerList.setHasFixedSize(true);
        notificationsRecyclerList.setLayoutManager(new LinearLayoutManager(getActivityContext(),
                LinearLayoutManager.VERTICAL, false));
        return root;
    }

    /**
     * Al finalizar el proceso de creación de la Actividad contenedora, se invoca este método que
     * establece un título para la barra superior y la acción que debe realizar: mostrar el menú
     * lateral de navegación.
     *
     * @param savedInstanceState estado del Fragmento guardado en una posible rotación de
     *                           la pantalla, o null.
     */
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mFragmentManagementListener.setCurrentDisplayedFragment(getString(R.string.notifications), this);
        mNavigationDrawerManagementListener.setToolbarAsNav();
    }

    /**
     * Ordena al Presentador que inicie el proceso de consulta a la base de datos de las
     * notificaciones del usuario actual.
     */
    @Override
    public void onStart() {
        super.onStart();
        mPresenter.loadNotifications();
    }

    /**
     * Borra las notificaciones almacenadas en el Adaptador para que no se guarden en el estado del
     * Fragmento. Son recuperadas inmediatamente al volver a mostrar el Fragmento por estar
     * usando el mismo Loader.
     */
    @Override
    public void onPause() {
        super.onPause();
        myNotificationsAdapter.replaceData(null);
    }

    /**
     * Establece en el Adaptador las notificaciones contenidas en el {@link LinkedHashMap} y, si no
     * está vacío, muestra la lista; si está vacío, muestra una imagen indicándolo
     *
     * @param notifications notificaciones obtenidas en la consulta. La clave es el identificador
     *                      de la notificación, el valor es el objeto notificación {@link MyNotification}
     */
    @Override
    public void showNotifications(LinkedHashMap<String, MyNotification> notifications) {
        myNotificationsAdapter.replaceData(notifications);
        if (notifications != null && notifications.size() > 0) {
            notificationsRecyclerList.setVisibility(View.VISIBLE);
            notificationsPlaceholder.setVisibility(View.INVISIBLE);
        } else {
            notificationsRecyclerList.setVisibility(View.INVISIBLE);
            notificationsPlaceholder.setVisibility(View.VISIBLE);
        }
        mFragmentManagementListener.showContent();
    }

    /**
     * Inicia la transición hacia el Fragmento utilizado para mostrar los detalles del objeto al
     * que hace referencia la notificación, puede ser un usuario, un partido o una alarma.
     *
     * @param key          Identificador de la notificación
     * @param notification notificación seleccionada
     */
    @Override
    public void onMyNotificationClick(String key, MyNotification notification) {
        @FirebaseDBContract.NotificationDataTypes int type = notification.getData_type();
        switch (type) {
            case FirebaseDBContract.NOTIFICATION_TYPE_NONE:
                break;
            case FirebaseDBContract.NOTIFICATION_TYPE_USER:
                mFragmentManagementListener.initFragment(
                        ProfileFragment.newInstance(notification.getExtra_data_one()), true);
                break;
            case FirebaseDBContract.NOTIFICATION_TYPE_EVENT:
                mFragmentManagementListener.initFragment(
                        DetailEventFragment.newInstance(notification.getExtra_data_one()), true);
                break;
            case FirebaseDBContract.NOTIFICATION_TYPE_ALARM:
                mFragmentManagementListener.initFragment(
                        DetailAlarmFragment.newInstance(notification.getExtra_data_one()), true);
                break;
            case FirebaseDBContract.NOTIFICATION_TYPE_ERROR:
                break;
        }
    }

    /**
     * Crea y muestra un cuadro de diálogo que permite borrar la notificación con ayuda del
     * Presentador de esta Vista.
     *
     * @param key          Identificador de la notificación
     * @param notification notificación seleccionada
     * @return true
     */
    @Override
    public boolean onMyNotificationLongClick(final String key, MyNotification notification) {
        String myUid = Utiles.getCurrentUserId();
        if (TextUtils.isEmpty(myUid)) return true;

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivityContext())
                .setTitle(R.string.dialog_msg_are_you_sure)
                .setMessage(R.string.dialog_msg_delete_one_notification)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mPresenter.deleteNotification(key);
                        mPresenter.loadNotifications();
                    }
                })
                .setNegativeButton(android.R.string.no, null);
        builder.create().show();

        return true;
    }
}
