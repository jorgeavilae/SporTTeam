package com.usal.jorgeav.sportapp.adapters;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.usal.jorgeav.sportapp.MyApplication;
import com.usal.jorgeav.sportapp.R;
import com.usal.jorgeav.sportapp.data.provider.SportteamContract;
import com.usal.jorgeav.sportapp.utils.UtilesContentProvider;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Adaptador para la lista de usuario simulados
 */
public class SimulatedUsersAdapter extends RecyclerView.Adapter<SimulatedUsersAdapter.ViewHolder> {
    /**
     * Nombre de la clase
     */
    @SuppressWarnings("unused")
    private static final String TAG = SimulatedUsersAdapter.class.getSimpleName();

    /**
     * Almacena la colección de usuarios simulados que maneja este adapter.
     */
    private Cursor mDataset;
    /**
     * Referencia al objeto que implementa {@link OnSimulatedUserItemClickListener}
     */
    private OnSimulatedUserItemClickListener mClickListener;
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
     * @param mDataset       Conjunto de usuario simulados
     * @param mClickListener Referencia al Listener que implementa esta interfaz
     * @param glide          Referencia a RequestManager para cargar el icono correspondiente a
     *                       cada item de la lista.
     * @see <a href= "https://bumptech.github.io/glide/javadocs/380/com/bumptech/glide/RequestManager.html">
     * RequestManager</a>
     */
    public SimulatedUsersAdapter(Cursor mDataset, OnSimulatedUserItemClickListener mClickListener, RequestManager glide) {
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
     * @return una nueva instancia de {@link SimulatedUsersAdapter.ViewHolder}
     */
    @NonNull
    @Override
    public SimulatedUsersAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.simulated_user_item_list, parent, false);

        return new SimulatedUsersAdapter.ViewHolder(view);
    }

    /**
     * Invocado cuando es necesario emplazar los datos de un elemento de la colección en una celda.
     * Extrae los datos del {@link #mDataset} en la posición indicada y los coloca en los elementos
     * de {@link SimulatedUsersAdapter.ViewHolder}
     *
     * @param holder   vista de la celda donde colocar los datos
     * @param position posición de los datos que se van a mostrar
     */
    @Override
    public void onBindViewHolder(@NonNull final SimulatedUsersAdapter.ViewHolder holder, int position) {
        if (mDataset.moveToPosition(position)) {
            // Set icon
            String userPicture = mDataset.getString(SportteamContract.SimulatedParticipantEntry.COLUMN_PROFILE_PICTURE);
            if (userPicture != null && !TextUtils.isEmpty(userPicture))
                mGlide.load(Uri.parse(userPicture)).asBitmap().into(new BitmapImageViewTarget(holder.imageViewSimulatedUserPhoto) {
                    @Override
                    protected void setResource(Bitmap resource) {
                        RoundedBitmapDrawable circularBitmapDrawable =
                                RoundedBitmapDrawableFactory.create(MyApplication.getAppContext().getResources(), resource);
                        circularBitmapDrawable.setCircular(true);
                        holder.imageViewSimulatedUserPhoto.setImageDrawable(circularBitmapDrawable);
                    }
                });
            else
                mGlide.load(R.drawable.profile_picture_placeholder)
                        .placeholder(R.drawable.profile_picture_placeholder)
                        .into(holder.imageViewSimulatedUserPhoto);

            // Set title
            holder.textViewSimulatedUserName.setText(mDataset.getString(SportteamContract.SimulatedParticipantEntry.COLUMN_ALIAS));

            // Set subtitle
            String ownerId = mDataset.getString(SportteamContract.SimulatedParticipantEntry.COLUMN_OWNER);
            String ownerName = UtilesContentProvider.getUserNameFromContentProvider(ownerId);
            String unformattedString = MyApplication.getAppContext().getString(R.string.created_by);
            holder.textViewSimulatedUserOwner.setText(String.format(unformattedString, ownerName));
        }
    }

    /**
     * Setter para actualizar la colección de usuarios simulados que maneja este adapter
     *
     * @param users Colección de usuarios simulados
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
    public class ViewHolder extends RecyclerView.ViewHolder implements AdapterView.OnClickListener {
        /**
         * Referencia al elemento de la celda donde se especifica la imagen del usuario simulado
         */
        @BindView(R.id.simulated_user_item_photo)
        ImageView imageViewSimulatedUserPhoto;
        /**
         * Referencia al elemento de la celda donde se especifica el nombre del usuario simulados
         */
        @BindView(R.id.simulated_user_item_name)
        TextView textViewSimulatedUserName;
        /**
         * Referencia al elemento de la celda donde se especifica el creador del usuario simulado
         */
        @BindView(R.id.simulated_user_item_owner)
        TextView textViewSimulatedUserOwner;

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
         * Obtiene el usuario simulado de la posición de la celda pulsada y
         * lo envía a {@link OnSimulatedUserItemClickListener}
         *
         * @param view View pulsada
         */
        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            if (mDataset.moveToPosition(position)) {
                String id = mDataset.getString(SportteamContract.SimulatedParticipantEntry.COLUMN_SIMULATED_USER_ID);
                String owner = mDataset.getString(SportteamContract.SimulatedParticipantEntry.COLUMN_OWNER);
                mClickListener.onSimulatedUserClick(owner, id);
            }
        }
    }

    /**
     * Interfaz para las pulsaciones sobre los usuarios simulados
     */
    public interface OnSimulatedUserItemClickListener {
        /**
         * Avisa al Listener de que un usuario fue pulsado
         *
         * @param ownerId         Identificador del usuario creador
         * @param simulatedUserId Identificador del usuario simulado
         */
        void onSimulatedUserClick(String ownerId, String simulatedUserId);
    }
}
