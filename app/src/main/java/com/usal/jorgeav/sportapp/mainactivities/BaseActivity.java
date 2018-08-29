package com.usal.jorgeav.sportapp.mainactivities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.usal.jorgeav.sportapp.BaseFragment;
import com.usal.jorgeav.sportapp.R;
import com.usal.jorgeav.sportapp.network.SportteamSyncInitialization;
import com.usal.jorgeav.sportapp.network.firebase.actions.UserFirebaseActions;
import com.usal.jorgeav.sportapp.network.firebase.sync.FirebaseSync;
import com.usal.jorgeav.sportapp.preferences.SettingsActivity;
import com.usal.jorgeav.sportapp.utils.Utiles;
import com.usal.jorgeav.sportapp.utils.UtilesNotification;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Actividad principal dónde se desarrolla la mayoría de la funcionalidad de la aplicación.
 * Casi todas las Actividades derivan de esta para, de esta forma, alojar en BaseActivity
 * el código común a todas.
 * <p></p><p></p>
 * Inicializa las referencias a la interfaz común a la mayoría de las Actividades. Configura la
 * barra superior y el menú lateral de navegación. Implementa la interfaz de
 * {@link ActivityContracts.NavigationDrawerManagement} para controlar estos elementos desde
 * los Fragmentos.
 * <p></p>
 * Implementa también, {@link ActivityContracts.FragmentManagement} para resolver transiciones
 * entre Fragmentos y establecer cuál de ellos está siendo mostrado, almacenando ese estado.
 * <p></p>
 * Inicializa los listeners de
 * {@link
 * <a href= "https://firebase.google.com/docs/reference/admin/java/reference/com/google/firebase/auth/FirebaseAuth">
 *     FirebaseAuth
 * </a>}
 * . Invoca la incialización y la finalización, según corresponda, de la carga de datos desde
 * {@link
 * <a href= "https://firebase.google.com/docs/reference/android/com/google/firebase/database/FirebaseDatabase">
 *     FirebaseDatabase
 * </a>}
 */
public abstract class BaseActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        ActivityContracts.NavigationDrawerManagement,
        ActivityContracts.FragmentManagement {
    /**
     * Método abstracto que deben implementar las Actividades que hereden de esta para
     * establecer cúal será el Fragmento principal que mostrarán al iniciarse.
     */
    public abstract void startMainFragment();

    /**
     * Nombre de la clase
     */
    private final static String TAG = BaseActivity.class.getSimpleName();
    /**
     * Nombre de la clave usada para almacenar el Fragmento actual durante rotaciones.
     */
    private final static String BUNDLE_SAVE_FRAGMENT_INSTANCE = "BUNDLE_SAVE_FRAGMENT_INSTANCE";
    /**
     * Nombre del tag usado para el Fragmento de mapa de Instalaciones. Al ser un mapa no
     * puede almacenarse como {@link BaseFragment} por lo que no se almacena durante rotaciones.
     * Se usa esta constante para indicar que ese era el Fragmento mostrado antes de la rotación.
     */
    public static final String FRAGMENT_TAG_IS_FIELDS_MAP = "FRAGMENT_TAG_IS_FIELDS_MAP";

    /**
     * Barra superior de la interfaz
     */
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    /**
     * Contenedor del menú lateral de navegación de la interfaz
     */
    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawer;
    /**
     * Menú lateral de navegación
     */
    @BindView(R.id.nav_view)
    NavigationView mNavigationView;
    /**
     * Contenedor de la interfaz donde se muestra el Fragmento
     */
    @BindView(R.id.contentFrame)
    FrameLayout mContentFrame;
    /**
     * Barra de progreso mostrada mientras el contenedor del Fragmento está oculto
     */
    @BindView(R.id.main_activity_progressbar)
    ProgressBar mProgressbar;
    /**
     * Referencia al Fragmento que se está mostrando actualmente, o null si es un Fragmento
     * no derivado de {@link BaseFragment} como
     * {@link
     * <a href= "https://developers.google.com/android/reference/com/google/android/gms/maps/SupportMapFragment">
     *     SupportMapFragment
     * </a>}
     */
    BaseFragment mDisplayedFragment;
    /**
     * Establece la unión entre el menú lateral de navegación y la barra superior. Se usa para
     * modificar la apariencia y el comportamiento de estos elementos
     */
    ActionBarDrawerToggle mToggle;

    /**
     * Referencia a
     * {@link
     * <a href= "https://firebase.google.com/docs/reference/admin/java/reference/com/google/firebase/auth/FirebaseAuth">
     *     FirebaseAuth
     * </a>}
     * para establecer el listener del inicio y cierre de sesión
     */
    private FirebaseAuth mAuth;
    /**
     * Listener del inicio y cierre de sesión.
     */
    private FirebaseAuth.AuthStateListener mAuthListener;
    /**
     * Es posible que se inicie la carga de un nuevo Fragmento principal desde el Listener
     * {@link #mAuthListener}, en {@link #onStart()}, antes de que se obtenga el posible Fragmento
     * guardado en la rotación de la pantalla en {@link #onRestoreInstanceState(Bundle)}.
     * <code>mIsFragmentOnSavedInstance</code> se inicia con este mismo {@link Bundle} obtenido en
     * {@link #onCreate(Bundle)}, y es true para indicar que hay un Fragmento guardado esperando
     * a ser cargado y false en caso contrario.
     *
     * <p><b>Más información:</b><br>
     * {@link
     * <a href= "https://developer.android.com/guide/components/activities/activity-lifecycle#restore-activity-ui-state-using-saved-instance-state">
     *     Restore activity UI state using saved instance state
     * </a>}</p>
     */
    private boolean mIsFragmentOnSavedInstance;

    /**
     * True cuando la Actividad está finalizando para cambiar a otra del menú lateral de
     * navegación, false en otro caso. Esta variable es necesaria para iniciar el proceso de
     * cierre de la aplicación y, por tanto, desvincular los Listener de la base de datos de Firebase.
     */
    private Boolean shouldDetachFirebaseListener;


    /**
     * En este método se carga la interfaz y se inicializan todas las variables
     *
     * @param savedInstanceState estado de la Actividad guardado en una posible rotación de
     *                           la pantalla, o null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);

        mToggle = new ActionBarDrawerToggle(
                this, mDrawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        mNavigationView.setNavigationItemSelectedListener(this);

        hideContent();

        // To know if onRestoreInstance() are going to init a Fragment before
        // onStart() method attach the FirebaseAuth listener and start a main Fragment
        // Repeat this onRestoreInstance() when onCreate() are not 
        mIsFragmentOnSavedInstance = (savedInstanceState != null &&
                (savedInstanceState.containsKey(BUNDLE_SAVE_FRAGMENT_INSTANCE)
                        || savedInstanceState.containsKey(FRAGMENT_TAG_IS_FIELDS_MAP)));

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser fuser = firebaseAuth.getCurrentUser();
                if (fuser != null) {
                    // User is signed in
                    Log.i(TAG, "FirebaseUser logged ID: "+fuser.getUid());
                    setUserInfoInNavigationDrawer();

                    // Initialization for populate Content Provider and init Service if needed
                    SportteamSyncInitialization.initialize(BaseActivity.this);

                    // Prevent from create main Fragment on configuration changes
                    // or if there was a fragment already
                    if (!mIsFragmentOnSavedInstance && mDisplayedFragment == null)
                        startMainFragment();
                } else {
                    // User is signed out
                    Log.i(TAG, "FirebaseUser logged ID: null");

                    // Finalize service
                    SportteamSyncInitialization.finalize(BaseActivity.this);

                    UtilesNotification.clearAllNotifications(BaseActivity.this);

                    Intent intent = new Intent(BaseActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                }
            }
        };

        shouldDetachFirebaseListener = true;
    }

    /**
     * Extrae la información del usuario almacenada en su perfil de
     * {@link
     * <a href= "https://firebase.google.com/docs/reference/android/com/google/firebase/auth/FirebaseUser">
     *     FirebaseUser
     * </a>}
     * y la coloca sobre la cabecera del menú lateral de navegación
     */
    @Override
    public void setUserInfoInNavigationDrawer() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser == null) return;
        ImageView image = (ImageView) mNavigationView.getHeaderView(0).findViewById(R.id.nav_header_image);
        TextView title = (TextView) mNavigationView.getHeaderView(0).findViewById(R.id.nav_header_title);
        TextView subtitle = (TextView) mNavigationView.getHeaderView(0).findViewById(R.id.nav_header_subtitle);

        title.setText(firebaseUser.getDisplayName());
        subtitle.setText(firebaseUser.getEmail());
        Glide.with(this)
                .load(firebaseUser.getPhotoUrl())
                .error(R.drawable.profile_picture_placeholder)
                .placeholder(R.drawable.profile_picture_placeholder)
                .into(image);
    }

    /**
     * Método usado para mostra más detalles de algunos errores que pueden suceder durante el
     * desarrollo. TODO Decidir si borrar este método
     *
     * @see
     * <a href= "https://developer.android.com/reference/android/os/StrictMode">
     *     Enable StrictMode for Debug Version
     * </a>
     */
    @SuppressWarnings("unused")
    private void debugTrickyErrors() {
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectDiskReads()
                .detectDiskWrites()
                .detectAll()
                .penaltyLog()
                .build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                .detectLeakedSqlLiteObjects()
                .detectLeakedClosableObjects()
                .penaltyLog()
                .penaltyDeath()
                .build());
    }

    /**
     * Se invoca este método al destruir la Actividad. Guarda el {@link BaseFragment} que se
     * esté mostrando en ese momento para mostrarlo en la recreación de la Actividad. Si el
     * que se está mostrando es {@link com.usal.jorgeav.sportapp.fields.FieldsMapFragment},
     * se guarda un TAG que lo indica. Si el que se está mostrando es
     * {@link com.usal.jorgeav.sportapp.searchevent.EventsMapFragment} o cualquier otro no
     * mencionado, no guarda nada y en la recreación muestra
     * {@link com.usal.jorgeav.sportapp.searchevent.EventsMapFragment}.
     *
     * @param outState Bundle para guardar el estado de la Actividad
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (getSupportFragmentManager() != null) {
            if (mDisplayedFragment != null)
                getSupportFragmentManager().putFragment(outState,
                    BUNDLE_SAVE_FRAGMENT_INSTANCE, mDisplayedFragment);
            else if (getSupportFragmentManager()
                    .findFragmentByTag(FRAGMENT_TAG_IS_FIELDS_MAP) != null)
                outState.putInt(FRAGMENT_TAG_IS_FIELDS_MAP, 1);
        }
        super.onSaveInstanceState(outState);
    }

    /**
     * Se ejecuta en la recreación de la Actividad, después de {@link #onStart()}. Sólo se
     * ejecuta si <code>savedInstanceState != null</code>. Es el mismo {@link Bundle} de
     * {@link #onCreate(Bundle)}. Usado para extraer el Fragmento guardado, si lo hubiese,
     * o para iniciar {@link com.usal.jorgeav.sportapp.fields.FieldsMapFragment} si la
     * etiqueta lo indica, o para iniciar
     * {@link com.usal.jorgeav.sportapp.searchevent.EventsMapFragment} en cualquier otro caso.
     *
     * @param savedInstanceState Bundle para extraer el estado de la Actividad
     */
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        mDisplayedFragment = null;
        if (savedInstanceState.containsKey(BUNDLE_SAVE_FRAGMENT_INSTANCE)) {
            try {
                mDisplayedFragment = (BaseFragment) getSupportFragmentManager()
                        .getFragment(savedInstanceState, BUNDLE_SAVE_FRAGMENT_INSTANCE);
            } catch (IllegalStateException e) { e.printStackTrace(); }
        } else if(savedInstanceState.containsKey(FRAGMENT_TAG_IS_FIELDS_MAP)) {
            onNavigationItemSelected(mNavigationView.getMenu().findItem(R.id.nav_fields));
        } else {
            onNavigationItemSelected(mNavigationView.getMenu().findItem(R.id.nav_search_events));
        }
    }

    /**
     * Método sobreescrito para poder introducir la llamada a
     * {@link ActionBarDrawerToggle#syncState()} que {@link #mToggle} necesita.
     *
     * @param savedInstanceState Bundle de estado
     */
    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mToggle.syncState();
    }

    /**
     * Método sobreescrito para cerrar el menú lateral de navegación al invocar la navegación
     * hacia atrás, en caso de que estuviese abierto.
     */
    @Override
    public void onBackPressed() {
        if (mDrawer.isDrawerOpen(GravityCompat.START)) {
            mDrawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    /**
     * Método sobreescrito para poder introducir la llamada a
     * {@link ActionBarDrawerToggle#onConfigurationChanged(Configuration)}
     * que {@link #mToggle} necesita.
     *
     * @param newConfig nueva configuración
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        hideSoftKeyboard();
        mToggle.onConfigurationChanged(newConfig);
    }

    /**
     * Se invoca cuando se pulsa sobre una de las entradas del menú lateral de navegación.
     * Se distingue entre el inicio de la Actividad de preferencias {@link SettingsActivity},
     * el cierre de sesión, y el cambio de Actividad por otra de las principales.<br>
     *     En este último caso se invoca {@link #simulateNavigationItemSelected(int, String, String)}
     *
     * @param item elemento del menú pulsado
     * @return true para mostrar el ítem del parámetro como seleccionado
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.nav_sign_out) {
            signOut();
        } else if (item.getItemId() == R.id.nav_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        } else {
            simulateNavigationItemSelected(item.getItemId(), null, null);
        }

        mNavigationView.setCheckedItem(item.getItemId());
        mDrawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Invocado cuando se pulsa (o se simula la pulsación) sobre una entrada de las principales
     * del menú lateral de navegación. Implementa el cambio de una Actividad principal por otra.
     * Comienza una Actividad con un {@link Intent} e inicia el proceso de finalización
     * de la Actividad actual.
     *
     * @param menuItemId identificador de la entrada del menú que simula ser pulsada
     * @param intentExtraKey clave del valor pasado a la nueva Actividad mediante el Intent.
     *                      También puede ser null.
     * @param intentExtraValue valor pasado a la nueva Actividad mediante el Intent. También
     *                         puede ser null.
     */
    @Override
    public void simulateNavigationItemSelected(int menuItemId,
                                               String intentExtraKey,
                                               String intentExtraValue) {
        Intent intent;
        switch (menuItemId) {
            default: case R.id.nav_profile:
                intent = new Intent(this, ProfileActivity.class); break;
            case R.id.nav_events:
                intent = new Intent(this, EventsActivity.class); break;
            case R.id.nav_search_events:
                intent = new Intent(this, SearchEventsActivity.class); break;
            case R.id.nav_notifications:
                intent = new Intent(this, NotificationsActivity.class); break;
            case R.id.nav_friends:
                intent = new Intent(this, FriendsActivity.class); break;
            case R.id.nav_alarms:
                intent = new Intent(this, AlarmsActivity.class); break;
            case R.id.nav_fields:
                intent = new Intent(this, FieldsActivity.class); break;
        }
        // Do not invoke detachListeners in onPause if it's a
        // navigation between activities
        shouldDetachFirebaseListener = false;

        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        if (intentExtraKey != null && !TextUtils.isEmpty(intentExtraKey))
            intent.putExtra(intentExtraKey, intentExtraValue);
        startActivity(intent);
        finish();
    }

    /**
     * Cuando una notificación inicia una Actividad que estaba parada en segundo plano, el
     * {@link Intent} con el que esa Actividad dice que fue iniciada ({@link Activity#getIntent()})
     * no es el mismo que el que se establece en la notificación. Para cambiarlo un Intent por otro
     * y así pasar datos en él, se sobreescribe este método para establecer el Intent nuevo de la
     * notificación.
     *
     * @see
     * <a href= "https://stackoverflow.com/a/6357330/4235666">
     *     StackOverflow: getIntent() Extras always NULL
     * </a>
     *
     * @param intent el Intent nuevo que abre la Actividad
     */
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        /* https://stackoverflow.com/a/6357330/4235666 */
        setIntent(intent);
    }

    /**
     * Borra el Fragmento mostrado y cierra la sesión del usuario actual, lo que provoca
     * que finalice la Actividad y se inicie {@link LoginActivity}
     */
    @Override
    public void signOut() {
        if (mDisplayedFragment != null) {
            getSupportFragmentManager().beginTransaction().remove(mDisplayedFragment).commit();
            mDisplayedFragment = null;
        }

        // Delete token to stop receiving new notifications
        UserFirebaseActions.deleteUserToken(Utiles.getCurrentUserId());
        mAuth.signOut();
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
     * @param fragment Fragmento que va a mostrarse
     * @param addToBackStack true si debe almacenarse en la pila de Fragmentos
     * @param tag etiqueta asociada al Fragmento en la transición
     */
    @Override
    public void initFragment(@NonNull Fragment fragment, boolean addToBackStack, String tag) {
        hideContent();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.contentFrame, fragment, tag);
        if (addToBackStack) transaction.addToBackStack(null);
        transaction.commitAllowingStateLoss();
    }

    /**
     * Establece el titulo en la Toolbar y almacena la referencia al Fragmento mostrado actualmente
     *
     * @param title título del Fragmento
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
    @Override
    public void setActionBarTitle(String title) {
        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle(title);
    }

    /**
     * Establece como comportamiento de la Toolbar abrir el menú lateral de navegación y
     * establece la animación del icono de la Toolbar a este estado. Esto ocurre cuando
     * se muestra el Fragmento principal de las opciones del menú lateral de navegación.
     */
    @Override
    public void setToolbarAsNav() {
        if (mToolbar != null) {
            mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mDrawer.openDrawer(GravityCompat.START);
                }
            });
        }
        toolbarIconTransition.postDelayed(transitionToNav, 0);
    }

    /**
     * Establece como comportamiento de la Toolbar la navegación hacia atrás y
     * establece la animación del icono de la Toolbar a este estado. Esto ocurre cuando
     * se muestran los Fragmentos secundario de las opciones del menú lateral de navegación.
     */
    @Override
    public void setToolbarAsUp() {
        if (mToolbar != null) {
            mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onBackPressed();
                }
            });
        }
        toolbarIconTransition.postDelayed(transitionToUp, 0);
    }

    /**
     * Handler para ejecutar la animación del icono de la Toolbar. Se va a simular la
     * apertura o cierre del menú mediante el aumento o disminición del desfase en la posición
     * del menú. Para que se produzca este cambio del desfase de manera continua e incremental,
     * se establece esa tarea recursivamente sobre este Handler, cambiando sucesivamente el
     * desfase hasta la apertura (1) o cierre (0) total.
     */
    Handler toolbarIconTransition = new Handler();
    /**
     * Decimal para indicar el desfase en el estado del proceso de animación del icono. Puede
     * variar entre 1 abierto y 0 cerrado.
     */
    float currentOffset = 0;
    /**
     * Código para transformar el icono en el correspondiente para cuando se establece
     * la navegación hacia atrás
     */
    Runnable transitionToUp = new Runnable() {
        @Override
        public void run() {
            float offset = currentOffset + 0.1f;
            if (offset > 1f) {
                mToggle.onDrawerOpened(mDrawer);
                currentOffset = 1f;
                toolbarIconTransition.removeCallbacks(transitionToUp);
            } else {
                mToggle.onDrawerSlide(mDrawer, offset);
                currentOffset = offset;
                toolbarIconTransition.postDelayed(this, 10);
            }
        }
    };
    /**
     * Código para transformar el icono en el correspondiente para cuando se establece
     * la apertura del menú lateral de navegación
     */
    Runnable transitionToNav = new Runnable() {
        @Override
        public void run() {
            float offset = currentOffset - 0.1f;
            if (offset < 0f) {
                mToggle.onDrawerClosed(mDrawer);
                currentOffset = 0f;
                toolbarIconTransition.removeCallbacks(transitionToNav);
            } else {
                mToggle.onDrawerSlide(mDrawer, offset);
                currentOffset = offset;
                toolbarIconTransition.postDelayed(this, 10);
            }
        }
    };

    /**
     * Establece el Listener para los inicios y cierres de sesión de
     * {@link
     * <a href= "https://firebase.google.com/docs/reference/admin/java/reference/com/google/firebase/auth/FirebaseAuth">
     *     FirebaseAuth
     * </a>}
     */
    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    /**
     * Inicia el proceso de finalización de los Listener de
     * {@link
     * <a href= "https://firebase.google.com/docs/reference/android/com/google/firebase/database/FirebaseDatabase">
     *     FirebaseDatabase
     * </a>}
     * si corresponde. También borra el Listener sobre el inicio y cierre de sesión. Y elimina las
     * animaciones que pudieran estar en proceso sobre el icono de la Toolbar.
     */
    @Override
    protected void onPause() {
        super.onPause();
        toolbarIconTransition.removeCallbacks(transitionToNav);
        toolbarIconTransition.removeCallbacks(transitionToUp);

        // This prevent from detach listeners on orientation changes and activity transitions
        if (isFinishing() && shouldDetachFirebaseListener) FirebaseSync.detachListeners();

        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    /**
     * Oculta la barra de progreso de la interfaz y muestra el contenido del Fragemento
     */
    @Override
    public void showContent() {
        mContentFrame.setVisibility(View.VISIBLE);
        mProgressbar.setVisibility(View.INVISIBLE);
    }

    /**
     * Muestra la barra de progreso de la interfaz y oculta el contenido del Fragmento
     */
    @Override
    public void hideContent() {
        mContentFrame.setVisibility(View.INVISIBLE);
        mProgressbar.setVisibility(View.VISIBLE);
    }

    /**
     * Obtiene una referencia a la {@link View} que tiene el foco de la interfaz y, si esta
     * mostrando el teclado flotante en la pantalla, lo esconde
     *
     * @see
     * <a href= "https://stackoverflow.com/a/17789187/4235666">
     *     StackOverflow: Close/hide the Android Soft Keyboard
     * </a>
     */
    public void hideSoftKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    /**
     * Setter para {@link #shouldDetachFirebaseListener}. Establece cuando se debe
     * iniciar el proceso de finalización al ejecutar {@link #onPause()}
     *
     * @param shouldI nuevo valor de {@link #shouldDetachFirebaseListener}
     */
    protected void shouldDetachFirebaseListener(boolean shouldI) {
        shouldDetachFirebaseListener = shouldI;
    }
}
