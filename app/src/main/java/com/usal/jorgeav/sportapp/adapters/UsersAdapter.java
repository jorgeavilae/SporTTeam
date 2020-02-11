package com.usal.jorgeav.sportapp.adapters;

import android.database.Cursor;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.RequestManager;
import com.usal.jorgeav.sportapp.R;
import com.usal.jorgeav.sportapp.data.provider.SportteamContract;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Adaptador para la lista de usuario
 */
public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.ViewHolder> {
    /**
     * Nombre de la clase
     */
    @SuppressWarnings("unused")
    private static final String TAG = UsersAdapter.class.getSimpleName();

    /**
     * Almacena la colección de usuarios que maneja este adapter.
     */
    private Cursor mDataset;
    /**
     * Referencia al objeto que implementa {@link OnUserItemClickListener}
     */
    private OnUserItemClickListener mClickListener;
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
     * @param mDataset       Conjunto de usuarios
     * @param mClickListener Referencia al Listener que implementa esta interfaz
     * @param glide          Referencia a RequestManager para cargar el icono correspondiente a
     *                       cada item de la lista.
     * @see <a href= "https://bumptech.github.io/glide/javadocs/380/com/bumptech/glide/RequestManager.html">
     * RequestManager
     * </a>
     */
    public UsersAdapter(Cursor mDataset, OnUserItemClickListener mClickListener, RequestManager glide) {
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
     * @return una nueva instancia de {@link UsersAdapter.ViewHolder}
     */
    @NonNull
    @Override
    public UsersAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.user_item_grid, parent, false);

        return new UsersAdapter.ViewHolder(view);
    }

    /**
     * Invocado cuando es necesario emplazar los datos de un elemento de la colección en una celda.
     * Extrae los datos del {@link #mDataset} en la posición indicada y los coloca en los elementos
     * de {@link UsersAdapter.ViewHolder}
     *
     * @param holder   vista de la celda donde colocar los datos
     * @param position posición de los datos que se van a mostrar
     */
    @Override
    public void onBindViewHolder(@NonNull final UsersAdapter.ViewHolder holder, int position) {
        if (mDataset.moveToPosition(position)) {
            // Set icon
            String userPicture = mDataset.getString(SportteamContract.UserEntry.COLUMN_PHOTO);
            mGlide.asBitmap().load(Uri.parse(userPicture))
                    .placeholder(R.drawable.profile_picture_placeholder)
                    .into(holder.imageViewUserPhoto);

            // Set title
            holder.textViewUserName.setText(mDataset.getString(SportteamContract.UserEntry.COLUMN_NAME));

            // Set subtitle
            holder.textViewUserCity.setText(mDataset.getString(SportteamContract.UserEntry.COLUMN_CITY));
        }
    }

    /**
     * Setter para actualizar la colección de usuarios que maneja este adapter
     *
     * @param users Colección de usuarios
     */
    public void replaceData(Cursor users) {
        this.mDataset = users;
        notifyDataSetChanged();
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
     * Representa cada una de las celdas que el adaptador mantiene para la colección
     */
    public class ViewHolder extends RecyclerView.ViewHolder implements AdapterView.OnClickListener, AdapterView.OnLongClickListener {
        /**
         * Referencia al elemento de la celda donde se especifica la imagen del usuario
         */
        @BindView(R.id.user_item_photo)
        ImageView imageViewUserPhoto;
        /**
         * Referencia al elemento de la celda donde se especifica el nombre del usuario
         */
        @BindView(R.id.user_item_name)
        TextView textViewUserName;
        /**
         * Referencia al elemento de la celda donde se especifica la ciudad del usuario
         */
        @BindView(R.id.user_item_city)
        TextView textViewUserCity;

        /**
         * Inicializa y obtiene una referencia a los elementos de la celda con la ayuda de ButterKnife.
         * Se establece como {@link android.view.View.OnClickListener} y como
         * {@link android.view.View.OnLongClickListener} a sí mismo para la View
         *
         * @param itemView View de una celda de la lista
         * @see <a href= "http://jakewharton.github.io/butterknife/">ButterKnife</a>
         */
        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        /**
         * Obtiene el usuario de la posición de la celda pulsada y lo envía a
         * {@link OnUserItemClickListener}
         *
         * @param view View pulsada
         */
        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            if (mDataset.moveToPosition(position))
                if (mClickListener != null)
                    mClickListener.onUserClick(
                            mDataset.getString(SportteamContract.UserEntry.COLUMN_USER_ID));
        }

        /**
         * Obtiene el usuario de la posición de la celda pulsada y lo envía a
         * {@link OnUserItemClickListener}
         *
         * @param view View pulsada
         */
        @Override
        public boolean onLongClick(View view) {
            int position = getAdapterPosition();
            return mDataset.moveToPosition(position)
                    && mClickListener != null
                    && mClickListener.onUserLongClick(mDataset.getString(SportteamContract.UserEntry.COLUMN_USER_ID));
        }
    }

    /**
     * Interfaz para las pulsaciones sobre los usuarios simulados
     */
    public interface OnUserItemClickListener {
        /**
         * Avisa al Listener de que se produjo una pulsación sobre un usuario
         *
         * @param uid Identificador del usuario pulsado
         */
        void onUserClick(String uid);

        /**
         * Avisa al Listener de que se produjo una pulsación larga sobre un usuario
         *
         * @param uid Identificador del usuario pulsado
         * @return true si este método es utilizado
         */
        boolean onUserLongClick(String uid);
    }
}
