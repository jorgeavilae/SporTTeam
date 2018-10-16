package com.usal.jorgeav.sportapp.fields.fielddetail;


import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.usal.jorgeav.sportapp.BaseFragment;
import com.usal.jorgeav.sportapp.R;
import com.usal.jorgeav.sportapp.adapters.ProfileSportsAdapter;
import com.usal.jorgeav.sportapp.fields.addfield.NewFieldFragment;
import com.usal.jorgeav.sportapp.mainactivities.FieldsActivity;
import com.usal.jorgeav.sportapp.sportselection.SportsListFragment;
import com.usal.jorgeav.sportapp.utils.Utiles;
import com.usal.jorgeav.sportapp.utils.UtilesContentProvider;
import com.usal.jorgeav.sportapp.utils.UtilesTime;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Fragmento utilizado para mostrar los detalles de una instalación. Se encarga de inicializar
 * los componentes de la interfaz y utilizarlos para mostrar los parámetros de la instalación
 * recuperados de la base de datos.
 * <p>
 * Desde este Fragmento se puede añadir una instalación nueva o votar por alguna de las pistas de
 * la instalación que se está mostrando. Además, si el usuario actual es el creador de la
 * instalación, puede editarla o borrarla.
 * <p>
 * Este Fragmento puede ser mostrado a modo informativo para ver los detalles de la instalación
 * dónde se sitúa una alarma o un partido. También puede ser mostrado desde el mapa de instalaciones
 * lo que permitiría además, editarla o borrarla (sólo si el usuario actual es su creador).
 * <p>
 * Implementa la interfaz {@link DetailFieldContract.View} para la comunicación con esta clase.
 */
public class DetailFieldFragment extends BaseFragment implements
        DetailFieldContract.View {
    /**
     * Nombre de la clase
     */
    @SuppressWarnings("unused")
    private static final String TAG = DetailFieldFragment.class.getSimpleName();

    /**
     * Etiqueta para establecer, en la instanciación del Fragmento, el identificador de la
     * instalación que debe mostrarse
     */
    public static final String BUNDLE_FIELD_ID = "BUNDLE_FIELD_ID";

    /**
     * Etiqueta para establecer, en la instanciación del Fragmento, si la instalación debe mostrarse
     * a modo informativo o no
     */
    public static final String BUNDLE_IS_INFO = "BUNDLE_IS_INFO";

    /**
     * Identificador de la instalación que se está mostrando
     */
    private String mFieldId = "";

    /**
     * Presentador correspondiente a esta Vista
     */
    private DetailFieldContract.Presenter mPresenter;

    /**
     * Menú del Fragmento que cambiará si el usuario actual es el creador de la instalación
     */
    private Menu mMenu = null;

    /**
     * Referencia al mapa de la interfaz donde se emplaza la dirección de la instalación
     */
    @BindView(R.id.field_detail_map)
    MapView detailFieldMap;
    /**
     * Objeto principal de Google Maps API. Hace referencia al mapa que provee esta API.
     *
     * @see <a href= "https://developers.google.com/android/reference/com/google/android/gms/maps/GoogleMap">
     * GoogleMap</a>
     */
    private GoogleMap mMap;
    /**
     * Referencia al elemento de la interfaz usado para escribir la dirección postal de la
     * instalación
     */
    @BindView(R.id.field_detail_address)
    TextView detailFieldAddress;
    /**
     * Referencia al elemento de la interfaz usado para escribir la hora de apertura de la
     * instalación
     */
    @BindView(R.id.field_detail_opening)
    TextView detailFieldOpening;
    /**
     * Referencia al elemento de la interfaz usado para escribir la hora de cierre de la
     * instalación
     */
    @BindView(R.id.field_detail_closing)
    TextView detailFieldClosing;
    /**
     * Referencia al elemento de la interfaz usado para indicar el usuario creador de la
     * instalación
     */
    @BindView(R.id.field_detail_creator)
    TextView detailFieldCreator;

    /**
     * Referencia a la lista de la interfaz donde se colocan las pistas de la instalación y sus
     * puntuaciones
     */
    @BindView(R.id.field_detail_sport_list)
    RecyclerView detailFieldSportList;
    /**
     * Adaptador usado para manejar la lista de pistas de la instalación
     */
    ProfileSportsAdapter sportsAdapter;
    /**
     * Referencia al elemento de la interfaz utilizado para indicar que no existen pistas para
     * esta instalación
     */
    @BindView(R.id.field_detail_sport_placeholder)
    ConstraintLayout detailFieldSportPlaceholder;

    /**
     * Constructor sin argumentos
     */
    public DetailFieldFragment() {
        // Required empty public constructor
    }

    /**
     * Método de instanciación del Fragmento
     *
     * @param fieldId identificador de la instalación
     * @param isInfo  true si es sólo para mostrar la información de la instalación, false en caso
     *                contrario
     * @return una nueva instancia de DetailFieldFragment
     */
    public static DetailFieldFragment newInstance(@NonNull String fieldId, boolean isInfo) {
        DetailFieldFragment fragment = new DetailFieldFragment();
        Bundle args = new Bundle();
        args.putString(BUNDLE_FIELD_ID, fieldId);
        args.putBoolean(BUNDLE_IS_INFO, isInfo);
        fragment.setArguments(args);
        return fragment;
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

        mPresenter = new DetailFieldPresenter(this);
    }

    /**
     * Obtiene una referencia al menú y lo limpia para establecer su contenido una vez se conozca si
     * el usuario es el creador de la instalación
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
     * proceso de edición de la instalación instanciando y mostrando el Fragmento correspondiente, o
     * se encarga de iniciar el proceso de borrado del partido con la ayuda del Presentador, o de
     * mostrar una lista de deportes para añadir una pista nueva.
     *
     * @param item elemento del menú pulsado
     * @return true si se aceptó la pulsación, false en otro caso
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        if (item.getItemId() == R.id.action_edit) {
            Fragment fragment = NewFieldFragment.newInstance(mFieldId);
            mFragmentManagementListener.initFragment(fragment, true);
            return true;
        } else if (item.getItemId() == R.id.action_delete) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivityContext())
                    .setTitle(R.string.dialog_msg_are_you_sure)
                    .setMessage(R.string.dialog_msg_delete_field)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            mPresenter.deleteField(mFieldId);
                        }
                    })
                    .setNegativeButton(android.R.string.no, null);
            builder.create().show();
            return true;
        } else if (item.getItemId() == R.id.action_add) {
            Fragment fragment = SportsListFragment.newInstance(mFieldId,
                    sportsAdapter.getDataAsSportArrayList(), sportsAdapter.getVotesAsHashMap());
            mFragmentManagementListener.initFragment(fragment, true);
            return true;
        }
        return false;
    }

    /**
     * Inicializa y obtiene una referencia a los elementos de la interfaz con la ayuda de
     * ButterKnife. Además centra el mapa en la ciudad del usuario y establece el Adaptador para la
     * lista de pistas.
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
        View root = inflater.inflate(R.layout.fragment_detail_field, container, false);
        ButterKnife.bind(this, root);

        if (getArguments() != null && getArguments().containsKey(BUNDLE_FIELD_ID))
            mFieldId = getArguments().getString(BUNDLE_FIELD_ID);

        //Need to be MapView, not SupportMapFragment https://stackoverflow.com/a/19354359/4235666
        detailFieldMap.onCreate(savedInstanceState);
        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        detailFieldMap.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;

                Utiles.setCoordinatesInMap(getActivityContext(), mMap, null);
            }
        });

        sportsAdapter = new ProfileSportsAdapter(null,
                new ProfileSportsAdapter.OnProfileSportClickListener() {
                    @Override
                    public void onProfileSportClick(String sportId) {
                        displayVoteCourtDialog(sportId);
                    }
                }, Glide.with(this), true);
        detailFieldSportList.setAdapter(sportsAdapter);
        detailFieldSportList.setHasFixedSize(true);
        detailFieldSportList.setLayoutManager(new GridLayoutManager(getActivityContext(), 2, LinearLayoutManager.VERTICAL, false));

        return root;
    }

    /**
     * Crea y muestra un cuadro de diálogo para votar por una pista. Se muestra al pulsar sobre
     * alguna de las pistas de la lista y muestra una barra para escoger una puntuación que se
     * envía a la base de datos a través del Presentador.
     *
     * @param sportId identificador del deporte correspondiente a la pista pulsada
     */
    @SuppressLint("InflateParams")
    private void displayVoteCourtDialog(final String sportId) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View view = inflater.inflate(R.layout.vote_dialog, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivityContext());
        builder.setTitle(R.string.dialog_title_vote)
                .setView(view)
                .setPositiveButton(R.string.action_vote, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        RatingBar ratingBar = (RatingBar) view.findViewById(R.id.rating_for_vote);
                        if (!mPresenter.voteSportInField(mFieldId, sportId, ratingBar.getRating()))
                            Toast.makeText(getActivityContext(), R.string.dialog_vote_error, Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton(android.R.string.cancel, null);
        builder.create().show();
    }

    /**
     * Al finalizar el proceso de creación de la Actividad contenedora, se invoca este método que
     * establece un título para la barra superior y la acción que debe realizar: navegar hacia
     * atrás.
     *
     * @param savedInstanceState estado del Fragmento guardado en una posible rotación de
     *                           la pantalla, o null.
     */
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mFragmentManagementListener.setCurrentDisplayedFragment(getString(R.string.field_detail_title), this);
        mNavigationDrawerManagementListener.setToolbarAsUp();
    }

    /**
     * Avisa al mapa de este método del ciclo de vida del Fragmento, y pide al Presentador que
     * recupere los parámetros de la instalación que se va a mostrar.
     */
    @Override
    public void onStart() {
        super.onStart();
        detailFieldMap.onStart();
        mPresenter.openField(getLoaderManager(), getArguments());
    }

    /**
     * Muestra en la interfaz el nombre de la instalación
     *
     * @param name nombre de la instalación
     */
    @Override
    public void showFieldName(String name) {
        showContent();
        mNavigationDrawerManagementListener.setActionBarTitle(name);
    }

    /**
     * Muestra en la interfaz la dirección establecida para la instalación. Centra el mapa en esas
     * coordenadas.
     *
     * @param address dirección postal de la instalación
     * @param city    ciudad de la instalación
     * @param coords  coordenadas de la instalación
     */
    @Override
    public void showFieldPlace(String address, String city, LatLng coords) {
        showContent();
        this.detailFieldAddress.setText(address);

        if (getActivity() instanceof FieldsActivity) {
            ((FieldsActivity) getActivity()).mAddress = address;
            ((FieldsActivity) getActivity()).mCity = city;
            ((FieldsActivity) getActivity()).mCoord = coords;
        }

        Utiles.setCoordinatesInMap(getActivityContext(), mMap, coords);
    }

    /**
     * Muestra en la interfaz las horas de apertura y cierre de la instalación
     *
     * @param openTime  hora de apertura de la instalación en milisegundos
     * @param closeTime hora de cierre de la instalación en milisegundos
     */
    @Override
    public void showFieldTimes(long openTime, long closeTime) {
        showContent();
        if (openTime == closeTime) {
            this.detailFieldOpening.setText(getString(R.string.open_24h));
            this.detailFieldClosing.setText("");
        } else {
            this.detailFieldOpening.setText(UtilesTime.millisToTimeString(openTime));
            this.detailFieldClosing.setText(UtilesTime.millisToTimeString(closeTime));
        }
    }

    /**
     * Muestra en la interfaz el usuario creador de la instalación. Obtiene el nombre correspondiente
     * al identificador de usuario proporcionado para mostrarlo. También compara este identificador
     * con el del usuario actual y, si coinciden, carga en el menú de la barra superior las opciones
     * de edición y borrado de la instalación.
     *
     * @param creator identificador del usuario creador de la instalación
     */
    @Override
    public void showFieldCreator(String creator) {
        String name = UtilesContentProvider.getUserNameFromContentProvider(creator);
        if (name != null && !TextUtils.isEmpty(name)) {
            String created = getString(R.string.created_by);
            this.detailFieldCreator.setText(String.format(created, name));
        }

        String myUid = Utiles.getCurrentUserId();
        if (TextUtils.isEmpty(myUid)) return;

        // If current user is creator and is not info detail fragment: allow edit/delete
        if (myUid.equals(creator)
                && getArguments() != null && getArguments().containsKey(BUNDLE_IS_INFO)
                && !getArguments().getBoolean(BUNDLE_IS_INFO) && mMenu != null) {
            mMenu.clear();
            getActivity().getMenuInflater().inflate(R.menu.menu_edit_delete, mMenu);
            getActivity().getMenuInflater().inflate(R.menu.menu_add, mMenu);
        }
    }

    /**
     * Pasa al Adaptador el conjunto de pistas encontradas en la base de datos para esta instalación.
     * Si no se encontró ninguna, muestra una imagen explicando que la lista está vacía.
     *
     * @param cursor contiene la lista de pistas con sus puntuaciones y cantidad de votos
     */
    @Override
    public void showSportCourts(Cursor cursor) {
        sportsAdapter.replaceData(cursor);
        if (cursor != null && cursor.getCount() > 0) {
            detailFieldSportList.setVisibility(View.VISIBLE);
            detailFieldSportPlaceholder.setVisibility(View.INVISIBLE);
        } else {
            detailFieldSportList.setVisibility(View.INVISIBLE);
            detailFieldSportPlaceholder.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Limpia la interfaz de los datos de la instalación
     */
    @Override
    public void clearUI() {
        this.mNavigationDrawerManagementListener.setActionBarTitle(getString(R.string.field_detail_title));
        this.detailFieldAddress.setText("");
        this.detailFieldOpening.setText("");
        this.detailFieldClosing.setText("");
        this.detailFieldCreator.setText("");
    }

    /**
     * Avisa al mapa de este método del ciclo de vida del Fragmento, y borra las pistas del Adaptador
     * para evitar que se almacenen en el estado del Fragmento
     */
    @Override
    public void onPause() {
        super.onPause();
        detailFieldMap.onPause();
        sportsAdapter.replaceData(null);
    }

    //todo esto porque???
    @Override
    public void onDetach() {
        super.onDetach();

        if (getActivity() instanceof FieldsActivity) {
            ((FieldsActivity) getActivity()).mAddress = null;
            ((FieldsActivity) getActivity()).mCity = null;
            ((FieldsActivity) getActivity()).mCoord = null;
        }
    }

    /**
     * Avisa al mapa de este método del ciclo de vida del Fragmento.
     */
    @Override
    public void onResume() {
        super.onResume();
        detailFieldMap.onResume();
    }

    /**
     * Avisa al mapa de este método del ciclo de vida del Fragmento.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        detailFieldMap.onDestroy();
    }

    /**
     * Avisa al mapa de este método del ciclo de vida del Fragmento.
     */
    @Override
    public void onStop() {
        super.onStop();
        detailFieldMap.onStop();
    }

    /**
     * Avisa al mapa de este método del ciclo de vida del Fragmento.
     */
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        detailFieldMap.onLowMemory();
    }
}
