package com.usal.jorgeav.sportapp.adapters;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.RecyclerView;
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
     * Alamacena la coleccion de usuarios simulados que maneja este adapter.
     */
    private Cursor mDataset;
    /**
     * Referencia al objeto que implementa {@link OnSimulatedUserItemClickListener}
     */
    private OnSimulatedUserItemClickListener mClickListener;
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
     * @param mDataset Conjunto de usuario simulados
     * @param mClickListener Referencia al Listener que implementa esta interfaz
     * @param glide Referencia a
     * {@link
     * <a href= "https://bumptech.github.io/glide/javadocs/380/com/bumptech/glide/RequestManager.html">
     *     RequestManager
     * </a>}
     * para cargar el icono correspondiente a cada item de la lista
     */
    public SimulatedUsersAdapter(Cursor mDataset, OnSimulatedUserItemClickListener mClickListener, RequestManager glide) {
        this.mDataset = mDataset;
        this.mClickListener = mClickListener;
        this.mGlide = glide;
    }

    @Override
    public SimulatedUsersAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.simulated_user_item_list, parent, false);

        return new SimulatedUsersAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final SimulatedUsersAdapter.ViewHolder holder, int position) {
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
     * Setter para actualizar la coleccion de usuarios simulados que maneja este adapter
     * @param users Colección de usuarios simulados
     */
    public void replaceData(Cursor users) {
        this.mDataset = users;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (mDataset != null) return mDataset.getCount();
        else return 0;
    }

    /**
     * Representa cada una de las celdas que el adaptador mantiene para la coleccion
     */
    public class ViewHolder extends RecyclerView.ViewHolder implements AdapterView.OnClickListener {
        /**
         * Imagen del usuario simulado
         */
        @BindView(R.id.simulated_user_item_photo)
        ImageView imageViewSimulatedUserPhoto;
        /**
         * Nombre del usuario simulados
         */
        @BindView(R.id.simulated_user_item_name)
        TextView textViewSimulatedUserName;
        /**
         * Creador del usuario simulado
         */
        @BindView(R.id.simulated_user_item_owner)
        TextView textViewSimulatedUserOwner;

        /**
         * Se establece como {@link android.view.View.OnClickListener} a sí mismo para la View
         *
         * @param itemView View de una celda de la lista
         */
        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        /**
         * Obtiene el usuario simulado de la posicion de la celda pulsada y
         * lo envia a {@link OnSimulatedUserItemClickListener}
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
         * @param ownerId Identificador del usuario creador
         * @param simulatedUserId Identificador del usuario simulado
         */
        void onSimulatedUserClick(String ownerId, String simulatedUserId);
    }
}
