package com.usal.jorgeav.sportapp.alarms;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.usal.jorgeav.sportapp.BaseFragment;
import com.usal.jorgeav.sportapp.R;
import com.usal.jorgeav.sportapp.adapters.AlarmAdapter;
import com.usal.jorgeav.sportapp.alarms.alarmdetail.DetailAlarmFragment;
import com.usal.jorgeav.sportapp.sportselection.SelectSportFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Fragmento utilizado para mostrar la colección de alarmas guardadas. Se encarga de inicializar
 * los componentes de la interfaz para mostrar la colección con la ayuda de {@link AlarmAdapter}.
 * Implementa la interfaz {@link AlarmsContract.View} para la comunicación con esta clase y la
 * interfaz {@link AlarmAdapter.OnAlarmItemClickListener} para manejar la pulsación sobre una
 * alarma de la colección.
 */
public class AlarmsFragment extends BaseFragment implements
        AlarmsContract.View,
        AlarmAdapter.OnAlarmItemClickListener {
    /**
     * Nombre de la clase
     */
    @SuppressWarnings("unused")
    private static final String TAG = AlarmsFragment.class.getSimpleName();

    /**
     * Presentador correspondiente a esta Vista
     */
    AlarmsContract.Presenter mAlarmsPresenter;

    /**
     * Adaptador para la colección de alarmas que se pretende mostrar
     */
    AlarmAdapter mAlarmsRecyclerAdapter;

    /**
     * Referencia a la lista de la interfaz donde se muestran las alarmas
     */
    @BindView(R.id.alarms_list)
    RecyclerView alarmsRecyclerList;
    /**
     * Referencia al contenedor de la interfaz mostrado en caso de que no exista ninguna alarma
     */
    @BindView(R.id.alarms_placeholder)
    ConstraintLayout alarmsPlaceholder;

    /**
     * Constructor sin parámetros
     */
    public AlarmsFragment() {
    }

    /**
     * Método de instanciación del Fragmento
     *
     * @return una nueva instancia de AlarmsFragment
     */
    public static AlarmsFragment newInstance() {
        return new AlarmsFragment();
    }

    /**
     * Inicializa el Presentador correspondiente a esta vista, y el Adaptador para la colección de
     * alarmas.
     *
     * @param savedInstanceState estado del Fragmento guardado en una posible rotación de
     *                           la pantalla, o null.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        mAlarmsPresenter = new AlarmsPresenter(this);
        mAlarmsRecyclerAdapter = new AlarmAdapter(null, this, Glide.with(this));
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
        inflater.inflate(R.menu.menu_add, menu);
    }

    /**
     * Invocado cuando un elemento del menú es pulsado. En este caso se encarga de iniciar el
     * proceso de creación de una nueva alarma.
     *
     * @param item elemento del menú pulsado
     * @return true si se aceptó la pulsación, false en otro caso
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if (item.getItemId() == R.id.action_add) {
            Fragment fragment = SelectSportFragment.newInstance();
            mFragmentManagementListener.initFragment(fragment, true);
            return true;
        }
        return false;
    }

    /**
     * Inicializa y obtiene una referencia a los elementos de la interfaz. Establece el adaptador
     * creado como adaptador de la lista de la interfaz recién inflada.
     *
     * @param inflater utilizado para inflar el archivo de layout
     * @param container contenedor donde se va a incluir la interfaz o null
     * @param savedInstanceState estado del Fragmento guardado en una posible rotación de
     *                           la pantalla, o null.
     *
     * @return la vista de la interfaz inicializada
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_alarms, container, false);
        ButterKnife.bind(this, root);

        alarmsRecyclerList.setAdapter(mAlarmsRecyclerAdapter);
        alarmsRecyclerList.setHasFixedSize(true);
        alarmsRecyclerList.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

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
        mFragmentManagementListener.setCurrentDisplayedFragment(getString(R.string.alarms), this);
        mNavigationDrawerManagementListener.setToolbarAsNav();
    }

    /**
     * Ordena al Presentador que inicie el proceso de carga de las alarmas en la base de datos.
     */
    @Override
    public void onStart() {
        super.onStart();
        mAlarmsPresenter.loadAlarms(getLoaderManager(), getArguments());
    }

    /**
     * Borra las alarmas almacenadas en el Adaptador para que no se guarden en el estado del
     * Fragmento. Son recuperadas inmediatamente al volver a mostrar el Fragmento por estar
     * haciendo la misma consulta al Loader.
     */
    @Override
    public void onPause() {
        super.onPause();
        mAlarmsRecyclerAdapter.replaceData(null);
    }

    /**
     * Establece en el Adaptador las alarmas contenidas en el {@link Cursor} y, si no está vacío,
     * muestra la lista; si está vacío, muestra una imagen indicándolo
     *
     * @param cursor alarmas obtenidas en la consulta
     */
    @Override
    public void showAlarms(Cursor cursor) {
        mAlarmsRecyclerAdapter.replaceData(cursor);
        if (cursor != null && cursor.getCount() > 0) {
            alarmsRecyclerList.setVisibility(View.VISIBLE);
            alarmsPlaceholder.setVisibility(View.INVISIBLE);
        } else {
            alarmsRecyclerList.setVisibility(View.INVISIBLE);
            alarmsPlaceholder.setVisibility(View.VISIBLE);
        }
        mFragmentManagementListener.showContent();
    }

    /**
     * Inicia el Fragmento para mostrar los detalles de la alarma pulsada
     *
     * @param alarmId Identificador de la Alarma seleccionada
     */
    @Override
    public void onAlarmClick(String alarmId) {
        Fragment newFragment = DetailAlarmFragment.newInstance(alarmId);
        mFragmentManagementListener.initFragment(newFragment, true);
    }
}
