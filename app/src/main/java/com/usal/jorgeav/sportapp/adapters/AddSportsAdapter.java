package com.usal.jorgeav.sportapp.adapters;

import android.support.v7.widget.RecyclerView;
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
 * Adaptador para la lista de seleccion multiple de deportes mediante {@link RatingBar}.
 */
public class AddSportsAdapter extends RecyclerView.Adapter<AddSportsAdapter.ViewHolder> {
    /**
     * Nombre de la clase
     */
    @SuppressWarnings("unused")
    private static final String TAG = AddSportsAdapter.class.getSimpleName();

    /**
     * Alamacena la lista de {@link Sport} que maneja este adapter.
     */
    private List<Sport> mDataset;
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
     * @param mDataset Conjunto de deportes a mostrar
     * @param glide Referencia a
     * {@link
     * <a href= "https://bumptech.github.io/glide/javadocs/380/com/bumptech/glide/RequestManager.html">
     *     RequestManager
     * </a>}
     * para cargar el icono correspondiente a cada item de la lista.
     */
    public AddSportsAdapter(List<Sport> mDataset, RequestManager glide) {
        this.mDataset = mDataset;
        this.mGlide = glide;
    }

    @Override
    public AddSportsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.sport_add_item_list, parent, false);
        return new AddSportsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AddSportsAdapter.ViewHolder holder, int position) {
        Sport s = mDataset.get(position);
        if (s != null) {
            mGlide.load(s.getIconDrawableId()).into(holder.imageViewSportIcon);
            int nameResource = MyApplication.getAppContext().getResources()
                    .getIdentifier(s.getSportID() , "string", MyApplication.getAppContext().getPackageName());
            holder.textViewSportName.setText(nameResource);
            holder.ratingBarSportLevel.setRating(s.getPunctuation());
        }
    }

    @Override
    public int getItemCount() {
        if (mDataset != null) return mDataset.size();
        else return 0;
    }

    /**
     * Setter para actualizar la coleccion de deportes que maneja este adapter.
     * @param sports Colección de deportes
     */
    public void replaceData(List<Sport> sports) {
        this.mDataset = sports;
        notifyDataSetChanged();
    }

    /**
     * Getter para la coleccion de deportes que maneja este adapter.
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
     * Representa cada una de las celdas que el adaptador mantiene para la coleccion
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        /**
         * Imagen del deporte
         */
        @BindView(R.id.sport_add_item_icon)
        ImageView imageViewSportIcon;
        /**
         * Nombre del deporte
         */
        @BindView(R.id.sport_add_item_name)
        TextView textViewSportName;
        /**
         * Puntuacion del deporte
         */
        @BindView(R.id.sport_add_item_level)
        RatingBar ratingBarSportLevel;

        /**
         * Establece un {@link android.widget.RatingBar.OnRatingBarChangeListener} para actualizar
         * la puntuacion de los deportes de la coleccion
         *
         * @param itemView View de una celda de la lista
         */
        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            ratingBarSportLevel.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                @Override
                public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                    mDataset.get(getAdapterPosition()).setPunctuation(v);
                }
            });
        }
    }
}