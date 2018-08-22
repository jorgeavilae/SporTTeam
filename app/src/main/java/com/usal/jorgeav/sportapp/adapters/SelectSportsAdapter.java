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
 * Adaptador para la lista de seleccion de deporte
 */
public class SelectSportsAdapter  extends RecyclerView.Adapter<SelectSportsAdapter.ViewHolder> {
    /**
     * Nombre de la clase
     */
    @SuppressWarnings("unused")
    private static final String TAG = SelectSportsAdapter.class.getSimpleName();

    /**
     * Alamacena la coleccion de deportes que maneja este adapter.
     */
    private List<Sport> mDataset;
    /**
     * Referencia al objeto que implementa {@link OnSelectSportClickListener}
     */
    private OnSelectSportClickListener mClickListener;
    /**
     * Referencia a la libreria
     * {@link
     * <a href= "https://bumptech.github.io/glide/javadocs/380/index.html">
     *     Glide
     * </a>}
     * , concretamente al objeto
     * {@link
     * <a href= "https://bumptech.github.io/glide/javadocs/380/com/bumptech/glide/RequestManager.html">
     *     RequestManager
     * </a>}
     * para cargar el icono correspondiente a cada item de la lista.
     */
    private RequestManager mGlide;

    /**
     *
     * @param mDataset Conjunto de deportes
     * @param mClickListener Referencia al Listener que implementa esta interfaz
     * @param glide Referencia a
     * {@link
     * <a href= "https://bumptech.github.io/glide/javadocs/380/com/bumptech/glide/RequestManager.html">
     *     RequestManager
     * </a>}
     * para cargar el icono correspondiente a cada item de la lista
     */
    public SelectSportsAdapter(List<Sport> mDataset, OnSelectSportClickListener mClickListener, RequestManager glide) {
        this.mDataset = mDataset;
        this.mClickListener = mClickListener;
        this.mGlide = glide;
    }

    @Override
    public SelectSportsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.sport_select_item_list, parent, false);
        return new SelectSportsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SelectSportsAdapter.ViewHolder holder, int position) {
        Sport s = mDataset.get(position);
        if (s != null) {
            // Set icon
            mGlide.load(s.getIconDrawableId()).asBitmap().into(holder.imageViewSportIcon);

            // Set title
            int nameResource = MyApplication.getAppContext().getResources()
                    .getIdentifier(s.getSportID() , "string", MyApplication.getAppContext().getPackageName());
            holder.textViewSportName.setText(nameResource);
        }
    }

    @Override
    public int getItemCount() {
        if (mDataset != null) return mDataset.size();
        else return 0;
    }

    /**
     * Setter para actualizar la coleccion de deportes que maneja este adapter
     * @param sports Colección de deportes
     */
    public void replaceData(List<Sport> sports) {
        this.mDataset = sports;
        notifyDataSetChanged();
    }

    /**
     * Representa cada una de las celdas que el adaptador mantiene para la coleccion
     */
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        /**
         * Imagen del deporte
         */
        @BindView(R.id.sport_select_item_icon)
        ImageView imageViewSportIcon;
        /**
         * Nombre del deporte
         */
        @BindView(R.id.sport_select_item_name)
        TextView textViewSportName;

        /**
         * Se establece como {@link android.view.View.OnClickListener} a sí mismo para la View
         *
         * @param itemView View de una celda de la lista
         */
        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setClickable(true);
            itemView.setOnClickListener(this);
        }

        /**
         * Obtiene el deporte de la posicion de la celda pulsada y
         * la envia a {@link OnSelectSportClickListener}
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
