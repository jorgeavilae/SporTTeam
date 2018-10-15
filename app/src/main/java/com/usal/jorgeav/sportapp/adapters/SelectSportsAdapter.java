package com.usal.jorgeav.sportapp.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.RequestManager;
import com.usal.jorgeav.sportapp.MyApplication;
import com.usal.jorgeav.sportapp.R;
import com.usal.jorgeav.sportapp.data.Sport;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Adaptador para la lista de selección de deporte
 */
public class SelectSportsAdapter extends RecyclerView.Adapter<SelectSportsAdapter.ViewHolder> {
    /**
     * Nombre de la clase
     */
    @SuppressWarnings("unused")
    private static final String TAG = SelectSportsAdapter.class.getSimpleName();

    /**
     * Almacena la colección de deportes que maneja este adapter.
     */
    private List<Sport> mDataset;
    /**
     * Referencia al objeto que implementa {@link OnSelectSportClickListener}
     */
    private OnSelectSportClickListener mClickListener;
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
     * @param mDataset       Conjunto de deportes
     * @param mClickListener Referencia al Listener que implementa esta interfaz
     * @param glide          Referencia a RequestManager para cargar el icono correspondiente a
     *                       cada item de la lista.
     * @see <a href= "https://bumptech.github.io/glide/javadocs/380/com/bumptech/glide/RequestManager.html">
     * RequestManager</a>
     */
    public SelectSportsAdapter(List<Sport> mDataset, OnSelectSportClickListener mClickListener, RequestManager glide) {
        this.mDataset = mDataset;
        this.mClickListener = mClickListener;
        this.mGlide = glide;
    }

    /**
     * Invocado cuando se necesita una nueva celda. Instancia la vista inflando el layout
     * correspondiente.
     *
     * @param parent   el ViewGroup al que se añadirá la vista al crearse.
     * @param viewType tipo de la vista
     * @return una nueva instancia de {@link SelectSportsAdapter.ViewHolder}
     */
    @Override
    public SelectSportsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.sport_select_item_list, parent, false);

        return new SelectSportsAdapter.ViewHolder(view);
    }

    /**
     * Invocado cuando es necesario emplazar los datos de un elemento de la colección en una celda.
     * Extrae los datos del {@link #mDataset} en la posición indicada y los coloca en los elementos
     * de {@link SelectSportsAdapter.ViewHolder}
     *
     * @param holder   vista de la celda donde colocar los datos
     * @param position posición de los datos que se van a mostrar
     */
    @Override
    public void onBindViewHolder(SelectSportsAdapter.ViewHolder holder, int position) {
        Sport s = mDataset.get(position);
        if (s != null) {
            // Set icon
            mGlide.load(s.getIconDrawableId()).asBitmap().into(holder.imageViewSportIcon);

            // Set title
            int nameResource = MyApplication.getAppContext().getResources()
                    .getIdentifier(s.getSportID(), "string", MyApplication.getAppContext().getPackageName());
            holder.textViewSportName.setText(nameResource);
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
     * Setter para actualizar la colección de deportes que maneja este adapter
     *
     * @param sports Colección de deportes
     */
    public void replaceData(List<Sport> sports) {
        this.mDataset = sports;
        notifyDataSetChanged();
    }

    /**
     * Representa cada una de las celdas que el adaptador mantiene para la colección
     */
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        /**
         * Referencia al elemento de la celda donde se especifica la imagen del deporte
         */
        @BindView(R.id.sport_select_item_icon)
        ImageView imageViewSportIcon;
        /**
         * Referencia al elemento de la celda donde se especifica el nombre del deporte
         */
        @BindView(R.id.sport_select_item_name)
        TextView textViewSportName;

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
            itemView.setClickable(true);
            itemView.setOnClickListener(this);
        }

        /**
         * Obtiene el deporte de la posición de la celda pulsada y la envía a
         * {@link OnSelectSportClickListener}
         *
         * @param view View pulsada
         */
        @Override
        public void onClick(View view) {
            if (mClickListener != null)
                mClickListener.onSportClick(mDataset.get(getAdapterPosition()).getSportID());
        }
    }

    /**
     * Interfaz para las pulsaciones sobre los deportes
     */
    public interface OnSelectSportClickListener {
        /**
         * Avisa al Listener de que un deporte fue pulsado
         *
         * @param sportId Identificador del Deporte seleccionado
         */
        void onSportClick(String sportId);
    }
}
