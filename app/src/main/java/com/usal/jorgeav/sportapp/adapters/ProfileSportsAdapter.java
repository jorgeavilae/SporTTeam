package com.usal.jorgeav.sportapp.adapters;

import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.RequestManager;
import com.usal.jorgeav.sportapp.MyApplication;
import com.usal.jorgeav.sportapp.R;
import com.usal.jorgeav.sportapp.data.Sport;
import com.usal.jorgeav.sportapp.data.SportCourt;
import com.usal.jorgeav.sportapp.data.provider.SportteamContract;
import com.usal.jorgeav.sportapp.utils.Utiles;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Adaptador para mostrar la lista indicadora de deportes mediante {@link RatingBar}.
 * Principalmente usado para mostrar los deportes practicados por el usuario, pero también
 * puede usarse para representar pistas de instalaciones.
 */
public class ProfileSportsAdapter extends RecyclerView.Adapter<ProfileSportsAdapter.ViewHolder> {
    /**
     * Nombre de la clase
     */
    @SuppressWarnings("unused")
    private static final String TAG = ProfileSportsAdapter.class.getSimpleName();

    /**
     * Almacena la colección de deportes que muestra este adapter. Puede ser un {@link Cursor}
     * de deportes que practica el usuario ({@link java.util.Map Map&lt;String, Double&gt;}) o
     * de deportes que representen pistas de una instalación y que lleven un número de votos
     * asociado ({@link SportCourt})
     */
    private Cursor mDataset;
    /**
     * Referencia al objeto que implementa {@link OnProfileSportClickListener}
     */
    private OnProfileSportClickListener mClickListener;
    /**
     * Referencia a la librería Glide, concretamente al objeto RequestManager para cargar el icono
     * correspondiente a cada item de la lista.
     *
     * @see <a href= "https://bumptech.github.io/glide/javadocs/380/index.html">Glide</a>
     * @see <a href= "https://bumptech.github.io/glide/javadocs/380/com/bumptech/glide/RequestManager.html">
     * RequestManager</a>
     */
    private RequestManager mGlide;
    /**
     * True si el adapter se esta usando para una lista de pistas de una instalación, false si se
     * esta usando para otra cosa, como para los deportes practicados por un usuario
     */
    private boolean isSportCourtsAdapter;

    /**
     * Constructor con argumentos
     *
     * @param mDataset             Conjunto de deportes
     * @param mClickListener       Referencia al Listener que implementa esta interfaz
     * @param glide                Referencia a RequestManager para cargar el icono correspondiente
     *                             a cada item de la lista.
     * @param isSportCourtsAdapter indicador de si los deportes son pistas de una instalación
     * @see <a href= "https://bumptech.github.io/glide/javadocs/380/com/bumptech/glide/RequestManager.html">
     * RequestManager</a>
     */
    public ProfileSportsAdapter(Cursor mDataset,
                                OnProfileSportClickListener mClickListener,
                                RequestManager glide,
                                boolean isSportCourtsAdapter) {
        this.mDataset = mDataset;
        this.mClickListener = mClickListener;
        this.mGlide = glide;
        this.isSportCourtsAdapter = isSportCourtsAdapter;
    }

    /**
     * Invocado cuando se necesita una nueva celda. Instancia la vista inflando el layout
     * correspondiente.
     *
     * @param parent   el ViewGroup al que se añadirá la vista al crearse.
     * @param viewType tipo de la vista
     * @return una nueva instancia de {@link ProfileSportsAdapter.ViewHolder}
     */
    @NonNull
    @Override
    public ProfileSportsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.sport_profile_item_list, parent, false);

        return new ProfileSportsAdapter.ViewHolder(view);
    }

    /**
     * Invocado cuando es necesario emplazar los datos de un elemento de la colección en una celda.
     * Extrae los datos del {@link #mDataset} en la posición indicada y los coloca en los elementos
     * de {@link ProfileSportsAdapter.ViewHolder}
     *
     * @param holder   vista de la celda donde colocar los datos
     * @param position posición de los datos que se van a mostrar
     */
    @Override
    public void onBindViewHolder(@NonNull ProfileSportsAdapter.ViewHolder holder, int position) {
        if (mDataset.moveToPosition(position)) {
            String sportId = mDataset.getString(SportteamContract.UserSportEntry.COLUMN_SPORT);

            // Set icon
            mGlide.load(Utiles.getSportIconFromResource(sportId)).asBitmap().into(holder.imageViewSportIcon);

            // Set title
            int nameResource = MyApplication.getAppContext().getResources()
                    .getIdentifier(sportId, "string", MyApplication.getAppContext().getPackageName());
            holder.textViewSportName.setText(nameResource);

            // Set stars
            float level = mDataset.getFloat(SportteamContract.UserSportEntry.COLUMN_LEVEL);
            holder.ratingBarSportLevel.setRating(level);
        }
    }

    /**
     * Devuelve el número de elementos de la colección de datos de este Adaptador
     *
     * @return número de elementos de {@link #mDataset}
     */
    @Override
    public int getItemCount() {
        if (mDataset != null) return mDataset.getCount();
        else return 0;
    }

    /**
     * Setter para actualizar la colección de deportes que maneja este adapter
     *
     * @param sports Colección de deportes
     */
    public void replaceData(Cursor sports) {
        this.mDataset = sports;
        notifyDataSetChanged();
    }

    /**
     * Getter para la colección de deportes que maneja este adapter.
     * Se utiliza este para obtener los deportes que muestra el adapter cuando es usado para
     * un usuario
     *
     * @return Lista de {@link Sport}
     */
    public ArrayList<Sport> getDataAsSportArrayList() {
        if (mDataset == null) return null;
        ArrayList<Sport> result = new ArrayList<>();
        for (mDataset.moveToFirst(); !mDataset.isAfterLast(); mDataset.moveToNext()) {
            String name = mDataset.getString(SportteamContract.UserSportEntry.COLUMN_SPORT);
            Double level = mDataset.getDouble(SportteamContract.UserSportEntry.COLUMN_LEVEL);

            result.add(new Sport(name, level));
        }
        return result;
    }

    /**
     * Getter para la colección de votos sobre las pistas de deportes que maneja este adapter.
     *
     * @return Map de pista (representada por identificador de deporte) con sus respectivos votos
     */
    public HashMap<String, Long> getVotesAsHashMap() {
        if (mDataset == null || !isSportCourtsAdapter) return null;
        HashMap<String, Long> result = new HashMap<>();
        for (mDataset.moveToFirst(); !mDataset.isAfterLast(); mDataset.moveToNext()) {
            if (mDataset.getColumnCount() == SportteamContract.FieldSportEntry.COLUMN_VOTES + 1) {
                String name = mDataset.getString(SportteamContract.FieldSportEntry.COLUMN_SPORT);
                Long votes = mDataset.getLong(SportteamContract.FieldSportEntry.COLUMN_VOTES);
                result.put(name, votes);
            }
        }
        return result;
    }

    /**
     * Representa cada una de las celdas que el adaptador mantiene para la colección
     */
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        /**
         * Referencia al elemento de la celda donde se especifica la imagen del deporte
         */
        @BindView(R.id.sport_profile_item_icon)
        ImageView imageViewSportIcon;
        /**
         * Referencia al elemento de la celda donde se especifica el nombre del deporte
         */
        @BindView(R.id.sport_profile_item_name)
        TextView textViewSportName;
        /**
         * Referencia al elemento de la celda donde se especifica la puntuación del deporte
         */
        @BindView(R.id.sport_profile_item_level)
        RatingBar ratingBarSportLevel;

        /**
         * Inicializa y obtiene una referencia a los elementos de la celda con la ayuda de ButterKnife.
         * Se establece como {@link android.view.View.OnClickListener} a sí mismo para la View
         *
         * @param itemView View de una celda de la lista
         * @see <a href= "http://jakewharton.github.io/butterknife/">ButterKnife</a>
         */
        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        /**
         * Obtiene el deporte de la posición de la celda pulsada y la envía a
         * {@link OnProfileSportClickListener}
         *
         * @param view View pulsada
         */
        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            mDataset.moveToPosition(position);
            if (mClickListener != null)
                mClickListener.onProfileSportClick(
                        mDataset.getString(SportteamContract.UserSportEntry.COLUMN_SPORT));
        }
    }

    /**
     * Interfaz para las pulsaciones sobre los deportes
     */
    public interface OnProfileSportClickListener {
        /**
         * Avisa al Listener de que un deporte fue pulsado
         *
         * @param sportId Identificador del Deporte seleccionado
         */
        void onProfileSportClick(String sportId);
    }
}

