package com.usal.jorgeav.sportapp.friends.searchuser;

import android.database.Cursor;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.usal.jorgeav.sportapp.BaseFragment;
import com.usal.jorgeav.sportapp.R;
import com.usal.jorgeav.sportapp.adapters.UsersAdapter;
import com.usal.jorgeav.sportapp.profile.ProfileFragment;
import com.usal.jorgeav.sportapp.utils.Utiles;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Fragmento utilizado para mostrar la colección de usuarios desconocidos. Se encarga de
 * inicializar los componentes de la interfaz para mostrar la colección con la ayuda de
 * {@link UsersAdapter}.
 * <p>
 * Inicialmente muestra los usuario cercanos, los que pertenecen a la misma ciudad que el usuario
 * actual y que son desconocidos para él. Si se escribe algún nombre en el elemento destinado a
 * ello, se realiza una nueva búsqueda estableciendo el parámetro "nombre de usuario", esta vez
 * sin acotar los resultados a la ciudad actual.
 * <p>
 * Implementa la interfaz {@link SearchUsersContract.View} para la comunicación con esta clase y la
 * interfaz {@link UsersAdapter.OnUserItemClickListener} para manejar la pulsación sobre un
 * usuario de la colección.
 */
public class SearchUsersFragment extends BaseFragment implements
        SearchUsersContract.View,
        UsersAdapter.OnUserItemClickListener {
    /**
     * Nombre de la clase
     */
    @SuppressWarnings("unused")
    private static final String TAG = SearchUsersFragment.class.getSimpleName();

    /**
     * Etiqueta utilizada para enviar al Presentador el nombre que debe usar como parámetro en la
     * búsqueda de usuarios.
     */
    public static final String BUNDLE_USERNAME = "BUNDLE_USERNAME";

    /**
     * Presentador correspondiente a esta Vista
     */
    private SearchUsersContract.Presenter mSearchUsersPresenter;

    /**
     * Adaptador para la colección de usuarios que se muestra
     */
    UsersAdapter mUsersRecyclerAdapter;

    /**
     * Referencia a la lista de la interfaz donde se muestran los usuarios encontrados
     */
    @BindView(R.id.search_users_list)
    RecyclerView searchUsersList;
    /**
     * Referencia al contenedor de la interfaz mostrado en caso de que no se encuentre ningún usuario
     */
    @BindView(R.id.search_users_placeholder)
    ConstraintLayout searchUsersPlaceholder;
    /**
     * Referencia al elemento de la interfaz donde se escribe el nombre del usuario que se pretende
     * buscar.
     */
    @BindView(R.id.search_users_edit)
    EditText searchUsersEditText;

    /**
     * Constructor sin argumentos
     */
    public SearchUsersFragment() {
        // Required empty public constructor
    }

    /**
     * Método de instanciación del Fragmento.
     *
     * @return una nueva instancia de SearchUsersFragment
     */
    public static SearchUsersFragment newInstance() {
        return new SearchUsersFragment();
    }

    /**
     * Inicializa el Presentador correspondiente a esta vista, y el Adaptador para la colección de
     * usuarios.
     *
     * @param savedInstanceState estado del Fragmento guardado en una posible rotación de
     *                           la pantalla, o null.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        mSearchUsersPresenter = new SearchUsersPresenter(this);
        mUsersRecyclerAdapter = new UsersAdapter(null, this, Glide.with(this));
    }

    /**
     * Inicializa el contenido del menú de opciones de la esquina superior derecha de la pantalla:
     * en este caso lo limpia para no mostrar ninguna opción
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
     * ButterKnife. Establece el adaptador creado como adaptador de la lista de la interfaz recién
     * inflada.
     * <p>
     * Además, establece la acción a realizar cuando el usuario pulsa el botón ENTER del
     * teclado mientras escribe en {@link #searchUsersEditText}: iniciar el proceso de búsqueda,
     * mediante un {@link TextView.OnEditorActionListener}.
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
        View root = inflater.inflate(R.layout.fragment_search_users, container, false);
        ButterKnife.bind(this, root);

        searchUsersList.setAdapter(mUsersRecyclerAdapter);
        searchUsersList.setHasFixedSize(true);
        searchUsersList.setLayoutManager(new GridLayoutManager(getActivityContext(),
                Utiles.calculateNoOfColumns(getActivityContext())));

        // Search users when click on GO_Button in keyboard
        searchUsersEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.search_users_ime || id == EditorInfo.IME_ACTION_GO) {
                    mUsersRecyclerAdapter.replaceData(null);
                    Bundle b = new Bundle();
                    b.putString(BUNDLE_USERNAME, searchUsersEditText.getText().toString());
                    mSearchUsersPresenter.loadUsersWithName(getLoaderManager(), b);
                    hideSoftKeyboard();
                    return true;
                }
                return false;
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
        mFragmentManagementListener.setCurrentDisplayedFragment(getString(R.string.action_search_users), this);
        mNavigationDrawerManagementListener.setToolbarAsUp();
    }

    /**
     * Ordena al Presentador que inicie el proceso de consulta a la base de datos de los usuarios
     * desconocidos de la misma ciudad que el usuario actual.
     */
    @Override
    public void onStart() {
        super.onStart();
        mSearchUsersPresenter.loadNearbyUsers(getLoaderManager(), getArguments());
    }

    /**
     * Borra los usuarios almacenados en el Adaptador para que no se guarden en el estado del
     * Fragmento. Son recuperadas inmediatamente al volver a mostrar el Fragmento por estar
     * usando el mismo Loader.
     */
    @Override
    public void onPause() {
        super.onPause();
        mUsersRecyclerAdapter.replaceData(null);
    }

    /**
     * Establece en el Adaptador los usuarios contenidos en el {@link Cursor} y, si no está vacío,
     * muestra la lista; si está vacío, muestra una imagen indicándolo
     *
     * @param cursor usuarios obtenidos en la consulta
     */
    @Override
    public void showUsers(Cursor cursor) {
        mUsersRecyclerAdapter.replaceData(cursor);
        if (cursor != null && cursor.getCount() > 0) {
            searchUsersList.setVisibility(View.VISIBLE);
            searchUsersPlaceholder.setVisibility(View.INVISIBLE);
        } else {
            searchUsersList.setVisibility(View.INVISIBLE);
            searchUsersPlaceholder.setVisibility(View.VISIBLE);
        }
        mFragmentManagementListener.showContent();
    }

    /**
     * Inicia la transición hacia el Fragmento utilizado para mostrar los detalles del usuario
     * pulsado.
     *
     * @param uid Identificador del usuario pulsado
     */
    @Override
    public void onUserClick(String uid) {
        Fragment newFragment = ProfileFragment.newInstance(uid);
        mFragmentManagementListener.initFragment(newFragment, true);
    }

    /**
     * Innecesario. No implementado.
     *
     * @param uid Identificador del usuario pulsado
     * @return false
     */
    @Override
    public boolean onUserLongClick(String uid) {
        return false;
    }
}
