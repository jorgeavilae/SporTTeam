package com.usal.jorgeav.sportapp.adapters;

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

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Adaptador para la lista de selección multiple de deportes mediante {@link RatingBar}.
 */
public class AddSportsAdapter extends RecyclerView.Adapter<AddSportsAdapter.ViewHolder> {
    /**
     * Nombre de la clase
     */
    @SuppressWarnings("unused")
    private static final String TAG = AddSportsAdapter.class.getSimpleName();

    /**
     * Almacena la lista de {@link Sport} que maneja este adapter.
     */
    private List<Sport> mDataset;
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
     * Constructor con argumentos
     *
     * @param mDataset Conjunto de deportes a mostrar
     * @param glide    Referencia a RequestManager para cargar el icono correspondiente a cada item de
     *                 la lista.
     * @see <a href= "https://bumptech.github.io/glide/javadocs/380/com/bumptech/glide/RequestManager.html">
     * RequestManager</a>
     */
    public AddSportsAdapter(List<Sport> mDataset, RequestManager glide) {
        this.mDataset = mDataset;
        this.mGlide = glide;
    }

    /**
     * Invocado cuando se necesita una nueva celda. Instancia la vista inflando el layout
     * correspondiente.
     *
     * @param parent   el ViewGroup al que se añadirá la vista al crearse.
     * @param viewType tipo de la vista
     * @return una nueva instancia de {@link AddSportsAdapter.ViewHolder}
     */
    @NonNull
    @Override
    public AddSportsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.sport_add_item_list, parent, false);

        return new AddSportsAdapter.ViewHolder(view);
    }

    /**
     * Invocado cuando es necesario emplazar los datos de un elemento de la colección en una celda.
     * Extrae los datos del {@link #mDataset} en la posición indicada y los coloca en los elementos
     * de {@link AddSportsAdapter.ViewHolder}
     *
     * @param holder   vista de la celda donde colocar los datos
     * @param position posición de los datos que se van a mostrar
     */
    @Override
    public void onBindViewHolder(@NonNull AddSportsAdapter.ViewHolder holder, int position) {
        Sport s = mDataset.get(position);
        if (s != null) {
            mGlide.load(s.getIconDrawableId()).into(holder.imageViewSportIcon);
            int nameResource = MyApplication.getAppContext().getResources()
                    .getIdentifier(s.getSportID(), "string", MyApplication.getAppContext().getPackageName());
            holder.textViewSportName.setText(nameResource);
            holder.ratingBarSportLevel.setRating(s.getPunctuation().floatValue());
        }
    }

    /**
     * Devuelve el número de elementos de la colección de datos de este Adaptador
     *
     * @return número de elementos de {@link #mDataset}
     */
    @Override
    public int getItemCount() {
        if (mDataset != null) return mDataset.size();
        else return 0;
    }

    /**
     * Setter para actualizar la colección de deportes que maneja este adapter.
     *
     * @param sports Colección de deportes
     */
    public void replaceData(List<Sport> sports) {
        this.mDataset = sports;
        notifyDataSetChanged();
    }

    /**
     * Getter para la colección de deportes que maneja este adapter.
     *
     * @return Lista de {@link Sport} que maneja este adapter.
     */
    public List<Sport> getDataAsArrayList() {
        if (mDataset == null) return null;
        ArrayList<Sport> result = new ArrayList<>();
        for (Sport s : mDataset)
            if (s.getPunctuation() > 0)
                result.add(s);
        return result;
    }

    /**
     * Representa cada una de las celdas que el adaptador mantiene para la colección
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        /**
         * Referencia a la imagen de la celda que representa el deporte
         */
        @BindView(R.id.sport_add_item_icon)
        ImageView imageViewSportIcon;
        /**
         * Referencia al elemento de la celda para escribir el nombre del deporte
         */
        @BindView(R.id.sport_add_item_name)
        TextView textViewSportName;
        /**
         * Referencia a la barra de puntuación de la celda para poner la puntuación del deporte
         */
        @BindView(R.id.sport_add_item_level)
        RatingBar ratingBarSportLevel;

        /**
         * Inicializa y obtiene una referencia a los elementos de la celda con la ayuda de ButterKnife.
         * Establece un {@link RatingBar.OnRatingBarChangeListener} para actualizar la puntuación
         * de los deportes de la colección
         *
         * @param itemView View de una celda de la lista
         * @see <a href= "http://jakewharton.github.io/butterknife/">ButterKnife</a>
         */
        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            ratingBarSportLevel.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                @Override
                public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                    mDataset.get(getAdapterPosition()).setPunctuation((double) v);
                }
            });
        }
    }
}