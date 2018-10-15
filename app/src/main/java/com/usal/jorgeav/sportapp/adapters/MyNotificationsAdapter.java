package com.usal.jorgeav.sportapp.adapters;

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
import com.usal.jorgeav.sportapp.data.MyNotification;
import com.usal.jorgeav.sportapp.network.firebase.FirebaseDBContract;
import com.usal.jorgeav.sportapp.utils.Utiles;
import com.usal.jorgeav.sportapp.utils.UtilesContentProvider;
import com.usal.jorgeav.sportapp.utils.UtilesTime;

import java.util.LinkedHashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Adaptador para la lista de notificaciones
 */
public class MyNotificationsAdapter extends RecyclerView.Adapter<MyNotificationsAdapter.ViewHolder> {
    /**
     * Nombre de la clase
     */
    @SuppressWarnings("unused")
    private static final String TAG = MyNotificationsAdapter.class.getSimpleName();

    /**
     * Almacena la colección de {@link MyNotification} que maneja este adapter mediante
     * {@link LinkedHashMap} para mantener el orden a lo largo de la ejecución
     */
    private LinkedHashMap<String, MyNotification> mDataset;
    /**
     * Referencia al objeto que implementa {@link OnMyNotificationItemClickListener}
     */
    private OnMyNotificationItemClickListener mClickListener;
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
     * @param dataset        Conjunto de notificaciones almacenadas en un {@link Map}
     * @param mClickListener Referencia al Listener que implementa esta interfaz
     * @param glide          Referencia a RequestManager para cargar el icono correspondiente a cada item de
     *                       la lista.
     * @see <a href= "https://bumptech.github.io/glide/javadocs/380/com/bumptech/glide/RequestManager.html">
     * RequestManager</a>
     */
    public MyNotificationsAdapter(Map<String, MyNotification> dataset,
                                  OnMyNotificationItemClickListener mClickListener,
                                  RequestManager glide) {
        if (dataset != null) this.mDataset = new LinkedHashMap<>(dataset);
        else this.mDataset = null;
        this.mClickListener = mClickListener;
        this.mGlide = glide;
    }

    /**
     * Invocado cuando se necesita una nueva celda. Instancia la vista inflando el layout
     * correspondiente.
     *
     * @param parent   el ViewGroup al que se añadirá la vista al crearse.
     * @param viewType tipo de la vista
     * @return una nueva instancia de {@link MyNotificationsAdapter.ViewHolder}
     */
    @Override
    public MyNotificationsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.notification_item_list, parent, false);

        return new MyNotificationsAdapter.ViewHolder(view);
    }

    /**
     * Invocado cuando es necesario emplazar los datos de un elemento de la colección en una celda.
     * Extrae los datos del {@link #mDataset} en la posición indicada y los coloca en los elementos
     * de {@link MyNotificationsAdapter.ViewHolder}
     *
     * @param holder   vista de la celda donde colocar los datos
     * @param position posición de los datos que se van a mostrar
     */
    @Override
    public void onBindViewHolder(final MyNotificationsAdapter.ViewHolder holder, int position) {
        Map.Entry<String, MyNotification> entry = getEntry(position);
        if (entry != null) {
            MyNotification n = entry.getValue();

            // Set icon
            switch (n.getData_type()) {
                case FirebaseDBContract.NOTIFICATION_TYPE_ALARM:
                    String alarmSportId = UtilesContentProvider.getAlarmSportFromContentProvider(n.getExtra_data_one());
                    if (alarmSportId != null && !TextUtils.isEmpty(alarmSportId)) {
                        mGlide.load(Utiles.getSportIconFromResource(alarmSportId)).into(holder.imageViewNotificationIcon);
                    } else
                        mGlide.load(R.drawable.ic_logo)
                                .placeholder(R.drawable.ic_logo)
                                .into(holder.imageViewNotificationIcon);
                    break;
                case FirebaseDBContract.NOTIFICATION_TYPE_ERROR:
                    mGlide.load(R.drawable.ic_logo)
                            .placeholder(R.drawable.ic_logo)
                            .into(holder.imageViewNotificationIcon);
                    break;
                case FirebaseDBContract.NOTIFICATION_TYPE_EVENT:
                    String eventSportId = UtilesContentProvider.getEventSportFromContentProvider(n.getExtra_data_one());
                    if (eventSportId != null && !TextUtils.isEmpty(eventSportId)) {
                        mGlide.load(Utiles.getSportIconFromResource(eventSportId)).into(holder.imageViewNotificationIcon);
                    } else
                        mGlide.load(R.drawable.ic_logo)
                                .placeholder(R.drawable.ic_logo)
                                .into(holder.imageViewNotificationIcon);
                    break;
                case FirebaseDBContract.NOTIFICATION_TYPE_NONE:
                    mGlide.load(R.drawable.ic_logo)
                            .placeholder(R.drawable.ic_logo)
                            .into(holder.imageViewNotificationIcon);
                    break;
                case FirebaseDBContract.NOTIFICATION_TYPE_USER:
                    String userPicture = UtilesContentProvider.getUserPictureFromContentProvider(n.getExtra_data_one());
                    if (userPicture != null && !TextUtils.isEmpty(userPicture))
                        mGlide.load(Uri.parse(userPicture)).asBitmap().into(new BitmapImageViewTarget(holder.imageViewNotificationIcon) {
                            @Override
                            protected void setResource(Bitmap resource) {
                                RoundedBitmapDrawable circularBitmapDrawable =
                                        RoundedBitmapDrawableFactory.create(MyApplication.getAppContext().getResources(), resource);
                                circularBitmapDrawable.setCircular(true);
                                holder.imageViewNotificationIcon.setImageDrawable(circularBitmapDrawable);
                            }
                        });
                    else
                        mGlide.load(R.drawable.profile_picture_placeholder)
                                .placeholder(R.drawable.profile_picture_placeholder)
                                .into(holder.imageViewNotificationIcon);
                    break;
            }

            // Set title and subtitle
            holder.textViewNotificationTitle.setText(n.getTitle());
            holder.textViewNotificationMessage.setText(n.getMessage());
            holder.textViewNotificationDate.setText(UtilesTime.millisToDateTimeString(n.getDate()));
        }
    }

    /**
     * Devuelve la entrada del Map para la posición indicada
     *
     * @param position posición de la entrada buscada
     * @return Entrada de Map en la posición indicada
     */
    private Map.Entry<String, MyNotification> getEntry(int position) {
        if (position > -1 && position < mDataset.size()) {
            int i = 0;
            for (Map.Entry<String, MyNotification> entry : mDataset.entrySet())
                if (i++ == position) return entry;
        }
        return null;
    }

    /**
     * Setter para actualizar la colección de notificaciones que maneja este adapter
     *
     * @param notifications Colección de notificaciones
     */
    public void replaceData(LinkedHashMap<String, MyNotification> notifications) {
        if (notifications != null) this.mDataset = new LinkedHashMap<>(notifications);
        else this.mDataset = null;
        notifyDataSetChanged();
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
     * Representa cada una de las celdas que el adaptador mantiene para la colección
     */
    public class ViewHolder extends RecyclerView.ViewHolder implements AdapterView.OnClickListener, AdapterView.OnLongClickListener {
        /**
         * Referencia al elemento de la celda donde se especifica la imagen de la notificación
         */
        @BindView(R.id.notification_item_icon)
        ImageView imageViewNotificationIcon;
        /**
         * Referencia al elemento de la celda donde se especifica el titulo de la notificación
         */
        @BindView(R.id.notification_item_title)
        TextView textViewNotificationTitle;
        /**
         * Referencia al elemento de la celda donde se especifica el mensaje de la notificación
         */
        @BindView(R.id.notification_item_message)
        TextView textViewNotificationMessage;
        /**
         * Referencia al elemento de la celda donde se especifica la fecha de la notificación
         */
        @BindView(R.id.notification_item_date)
        TextView textViewNotificationDate;

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
         * Obtiene la notificación de la posición de la celda pulsada y lo envía a
         * {@link OnMyNotificationItemClickListener}
         *
         * @param view View pulsada
         */
        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            Map.Entry<String, MyNotification> entry = getEntry(position);
            if (entry != null)
                mClickListener.onMyNotificationClick(entry.getKey(), entry.getValue());
        }

        /**
         * Obtiene la notificación de la posición de la celda pulsada y lo envía a
         * {@link OnMyNotificationItemClickListener}
         *
         * @param view View pulsada
         * @return true si se utiliza la pulsación, false en caso contrario
         */
        @Override
        public boolean onLongClick(View view) {
            int position = getAdapterPosition();
            Map.Entry<String, MyNotification> entry = getEntry(position);
            return entry != null
                    && mClickListener.onMyNotificationLongClick(entry.getKey(), entry.getValue());
        }
    }

    /**
     * Interfaz para las pulsaciones sobre las notificaciones
     */
    public interface OnMyNotificationItemClickListener {
        /**
         * Avisa al Listener de que se produjo una pulsación sobre una notificación
         *
         * @param key          Identificador de la notificación
         * @param notification notificación seleccionada
         */
        void onMyNotificationClick(String key, MyNotification notification);

        /**
         * Avisa al Listener de que se produjo una pulsación larga sobre una notificación
         *
         * @param key          Identificador de la notificación
         * @param notification notificación seleccionada
         * @return true si se utiliza la pulsación, false en caso contrario
         */
        boolean onMyNotificationLongClick(String key, MyNotification notification);
    }
}