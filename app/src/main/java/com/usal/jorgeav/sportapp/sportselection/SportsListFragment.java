package com.usal.jorgeav.sportapp.sportselection;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.usal.jorgeav.sportapp.BaseFragment;
import com.usal.jorgeav.sportapp.R;
import com.usal.jorgeav.sportapp.adapters.AddSportsAdapter;
import com.usal.jorgeav.sportapp.data.Sport;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Fragmento utilizado para mostrar la colección de deportes de la aplicación junto con una barra de
 * puntuación con el fin de especificar un nivel o una puntuación para cada uno. Se aplica a los
 * deportes practicados por un usuario {@link Sport} o a las pistas de una instalación
 * {@link com.usal.jorgeav.sportapp.data.SportCourt}. Se encarga de inicializar los componentes de
 * la interfaz para mostrar la lista de deportes cargados desde el archivo de recursos
 * correspondiente (arrays.xml). Declara la interfaz {@link OnSportsSelected} para ser implementada
 * por la Actividad contenedora a la que comunicar la puntuación asignada a cada deporte.
 */
public class SportsListFragment extends BaseFragment {
    /**
     * Nombre de a clase
     */
    private final static String TAG = SportsListFragment.class.getSimpleName();
    /**
     * Etiqueta para indicar, en la instanciación del Fragmento, el identificador del objeto al que
     * van a aplicarse los deportes y las puntuaciones escogidas en la ejecución de este Fragmento.
     */
    public static final String BUNDLE_INSTANCE_OBJECT_ID = "BUNDLE_INSTANCE_OBJECT_ID";
    /**
     * Etiqueta para indicar, en la instanciación del Fragmento, la lista de deportes y puntuaciones
     * ya establecidas en una ejecución anterior y almacenadas en la base de datos.
     */
    public static final String BUNDLE_INSTANCE_SPORT_LIST = "BUNDLE_INSTANCE_SPORT_LIST";
    /**
     * Etiqueta para indicar, en la instanciación del Fragmento, la lista de votos para que no se
     * pierdan en la edición, sólo en caso de que se trate de editar las puntuaciones de pistas de
     * una instalación.
     */
    public static final String BUNDLE_INSTANCE_VOTES_LIST = "BUNDLE_INSTANCE_VOTES_LIST";

    /**
     * Referencia a la lista de la interfaz donde se van a colocar los deportes
     */
    @BindView(R.id.recycler_list)
    RecyclerView sportsRecyclerViewList;

    /**
     * Adaptador para manejar la colección de deportes y mostrar sus datos y su barra de puntuación
     * en cada una de las celdas de la lista.
     */
    private AddSportsAdapter mSportAdapter;

    /**
     * Constructor
     */
    public SportsListFragment() {
        // Required empty public constructor
    }

    /**
     * Método de instanciación del Fragmento
     *
     * @param id         identificador del objeto al que se van a asociar estos deportes y puntuaciones
     * @param sportsList lista de deportes y puntuaciones de una posible ejecución anterior
     * @param votesList  lista de votos para cada deporte, en el caso de que los haya
     * @return una nueva instancia de SportsListFragment
     */
    public static SportsListFragment newInstance(@NonNull String id,
                                                 ArrayList<Sport> sportsList,
                                                 HashMap<String, Long> votesList) {
        SportsListFragment fragment = new SportsListFragment();
        Bundle args = new Bundle();

        args.putString(BUNDLE_INSTANCE_OBJECT_ID, id);

        if (sportsList != null)
            args.putParcelableArrayList(BUNDLE_INSTANCE_SPORT_LIST, sportsList);

        if (votesList != null)
            args.putSerializable(BUNDLE_INSTANCE_VOTES_LIST, votesList);

        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Instancia el Adaptador usado en la lista de deportes. Este Fragmento no inicializa el
     * Presentador porque no se rige bajo el patrón MVP.
     *
     * @param savedInstanceState estado del Fragmento guardado en una posible rotación de
     *                           la pantalla, o null.
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mSportAdapter = new AddSportsAdapter(null, Glide.with(this));
    }

    /**
     * Inicializa el contenido del menú de opciones de la esquina superior derecha de la pantalla
     *
     * @param menu menú de opciones donde se van a emplazar los elementos.
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_ok, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    /**
     * Invocado cuando un elemento del menú es pulsado. En este caso se encarga de aceptar y enviar
     * las puntuaciones establecidas, y los votos si los hubiera, a la Actividad contenedora que
     * debió implementar la interfaz {@link OnSportsSelected}
     *
     * @param item elemento del menú pulsado
     * @return true si se aceptó la pulsación, false en otro caso
     * @see #extractHashMapVotesFromBundle()
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if (item.getItemId() == R.id.action_ok) {
            if (getActivity() instanceof OnSportsSelected) {
                String id = null;
                if (getArguments() != null && getArguments().containsKey(BUNDLE_INSTANCE_OBJECT_ID))
                    id = getArguments().getString(BUNDLE_INSTANCE_OBJECT_ID);

                ((OnSportsSelected) getActivity()).retrieveSportsSelected(id,
                        mSportAdapter.getDataAsArrayList(), extractHashMapVotesFromBundle());
            } else {
                Log.e(TAG, "onOptionsItemSelected: Activity does not implement OnSportsSelected");
            }
            return true;
        }
        return false;
    }

    /**
     * Extrae, si los hubiera, el número de votos y el deporte al que van destinados del
     * {@link Bundle} de argumentos especificado en la instanciación del Fragmento.
     *
     * @return {@link HashMap} con el identificador del deporte como clave y el número de votos
     * como valor.
     */
    @SuppressWarnings("unchecked")
    private HashMap<String, Long> extractHashMapVotesFromBundle() {
        HashMap<String, Long> votesListFromActivity = null;
        if (getArguments() != null && getArguments().containsKey(BUNDLE_INSTANCE_VOTES_LIST)) {
            Serializable serializable = getArguments().getSerializable(BUNDLE_INSTANCE_VOTES_LIST);
            if (serializable instanceof HashMap)
                votesListFromActivity = (HashMap) serializable;
        }
        return votesListFromActivity;
    }


    /**
     * Inicializa y obtiene una referencia a los elementos de la interfaz con la ayuda de
     * ButterKnife. Establece {@link #mSportAdapter} como adaptador de la lista de la interfaz
     * recién inflada.
     * <p>
     * Además, comprueba que la Actividad contenedora implementa la interfaz
     * {@link OnSportsSelected} para comunicar el resultado de la ejecución de este Fragmento.
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

        if (!(getActivity() instanceof OnSportsSelected)) {
            Log.e(TAG, "onCreateView: getActivity() should implement OnSportsSelected");
            getActivity().onBackPressed();
        }
        sportsRecyclerViewList.setAdapter(mSportAdapter);
        sportsRecyclerViewList.setHasFixedSize(true);
        sportsRecyclerViewList.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

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
        mFragmentManagementListener.setCurrentDisplayedFragment(getString(R.string.pick_sports), this);
        if (mNavigationDrawerManagementListener != null) mNavigationDrawerManagementListener.setToolbarAsUp();
    }

    /**
     * Establece los deportes que debe mostrar la lista en el Adaptador, por medio de
     * {@link #loadSports()}
     */
    @Override
    public void onStart() {
        super.onStart();
        hideSoftKeyboard();
        mSportAdapter.replaceData(loadSports());
        showContent();
    }

    /**
     * Carga los identificadores de deportes desde el array declarado en el archivo de recursos
     * arrays.xml, y crea para cada uno un {@link Sport} que será añadido al Adaptador de la lista.
     * También especifica una puntuación de inicio si ese deporte ya la tenía de una ejecución
     * anterior.
     *
     * @return lista de {@link Sport}s que serán añadidos al Adaptador de la lista para que los
     * muestre en la interfaz
     */
    private List<Sport> loadSports() {
        ArrayList<Sport> result = new ArrayList<>();

        String[] sportsNameArray = getResources().getStringArray(R.array.sport_id_values);
        for (String aSportsNameArray : sportsNameArray) {
            result.add(new Sport(aSportsNameArray, (double) 0f));
        }

        if (getArguments() != null && getArguments().containsKey(BUNDLE_INSTANCE_SPORT_LIST)) {
            ArrayList<Sport> sportsListFromActivity = getArguments()
                    .getParcelableArrayList(BUNDLE_INSTANCE_SPORT_LIST);
            if (sportsListFromActivity != null) {
                for (Sport sportFromActivity : sportsListFromActivity)
                    for (Sport sportFromResources : result)
                        if (sportFromActivity.getSportID().equals(sportFromResources.getSportID())) {
                            sportFromResources.setPunctuation(sportFromActivity.getPunctuation());
                            break;
                        }
            }
        }

        return result;
    }

    /**
     * Borra el contenido del Adaptador para que no se guarde en el estado del Fragmento.
     */
    @Override
    public void onPause() {
        super.onPause();
        mSportAdapter.replaceData(null);
    }

    /**
     * Interfaz que debe ser implementada por la Actividad que contenga este Fragmento
     * {@link SportsListFragment} para recibir la lista de deportes con la puntuación de cada uno.
     */
    public interface OnSportsSelected {
        /**
         * Invocado para comunicar el resultado de la ejecución de este Fragmento
         *
         * @param id             identificador del objeto al que se van a asociar estos deportes y
         *                       puntuaciones
         * @param sportsSelected lista de deportes y puntuaciones resultado de la ejecución
         * @param votesList      lista de votos para cada deporte, en el caso de que los haya
         */
        void retrieveSportsSelected(String id,
                                    List<Sport> sportsSelected,
                                    HashMap<String, Long> votesList);
    }
}
