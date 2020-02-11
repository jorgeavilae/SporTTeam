package com.usal.jorgeav.sportapp.sportselection;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.usal.jorgeav.sportapp.BaseFragment;
import com.usal.jorgeav.sportapp.R;
import com.usal.jorgeav.sportapp.adapters.SelectSportsAdapter;
import com.usal.jorgeav.sportapp.data.Sport;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Fragmento utilizado para mostrar los deportes admitidos en la aplicación con el fin de que el
 * usuario seleccione uno. Se encarga de inicializar los componentes de la interfaz para mostrar la
 * lista de deportes cargados desde el archivo de recursos correspondiente (arrays.xml). Implementa
 * la interfaz {@link SelectSportsAdapter.OnSelectSportClickListener} para manejar la
 * pulsación sobre uno de los deportes de la colección. También declara la interfaz
 * {@link OnSportSelectedListener} para ser implementada por la Actividad contenedora a la que
 * comunicar el deporte seleccionado.
 */
public class SelectSportFragment extends BaseFragment implements
        SelectSportsAdapter.OnSelectSportClickListener {
    /**
     * Nombre de la clase
     */
    private final static String TAG = SelectSportFragment.class.getSimpleName();

    /**
     * Referencia a la lista de la interfaz donde se van a colocar los deportes
     */
    @BindView(R.id.recycler_list)
    RecyclerView sportsRecyclerViewList;

    /**
     * Adaptador para manejar la colección de deportes y mostrar sus datos en cada una de las
     * celdas de la lista.
     */
    private SelectSportsAdapter mSportAdapter;

    /**
     * Constructor sin argumentos.
     */
    public SelectSportFragment() {
        // Required empty public constructor
    }

    /**
     * Método de instanciación del Fragmento
     *
     * @return una nueva instancia de SelectSportFragment
     */
    public static SelectSportFragment newInstance() {
        return new SelectSportFragment();
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
        mSportAdapter = new SelectSportsAdapter(null, this, Glide.with(this));
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
     * ButterKnife. Establece {@link #mSportAdapter} como adaptador de la lista de la interfaz
     * recién inflada.
     * <p>
     * Además, comprueba que la Actividad contenedora implementa la interfaz
     * {@link OnSportSelectedListener} para comunicar el resultado de la ejecución de este Fragmento.
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

        if (!(getActivity() instanceof OnSportSelectedListener)) {
            Log.e(TAG, "onCreateView: getActivity() should implement OnSportSelectedListener");
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
        mFragmentManagementListener.setCurrentDisplayedFragment(getString(R.string.pick_sport), this);
        mNavigationDrawerManagementListener.setToolbarAsUp();
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
     * Invocado al pulsar sobre un deporte de la lista, envía el identificador del deporte
     * seleccionado a la Actividad contenedora por medio de {@link OnSportSelectedListener}
     *
     * @param sportId Identificador del Deporte seleccionado
     */
    @Override
    public void onSportClick(String sportId) {
        if (getActivity() instanceof OnSportSelectedListener)
            ((OnSportSelectedListener) getActivity()).onSportSelected(sportId);
    }

    /**
     * Interfaz que debe ser implementada por la Actividad que contenga este Fragmento
     * {@link SelectSportFragment} para recibir el identificador del deporte seleccionado.
     */
    public interface OnSportSelectedListener {
        /**
         * Invocado para comunicar el deporte seleccionado
         *
         * @param sportId identificador del deporte seleccionado
         */
        void onSportSelected(String sportId);
    }
}
