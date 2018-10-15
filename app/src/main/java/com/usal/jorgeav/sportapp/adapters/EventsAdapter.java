package com.usal.jorgeav.sportapp.adapters;

import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.RequestManager;
import com.usal.jorgeav.sportapp.R;
import com.usal.jorgeav.sportapp.data.provider.SportteamContract;
import com.usal.jorgeav.sportapp.utils.Utiles;
import com.usal.jorgeav.sportapp.utils.UtilesContentProvider;
import com.usal.jorgeav.sportapp.utils.UtilesTime;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Adaptador para la lista de partidos
 */
public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.ViewHolder> {
    /**
     * Nombre de la clase
     */
    @SuppressWarnings("unused")
    private static final String TAG = EventsAdapter.class.getSimpleName();

    /**
     * Almacena la colección de {@link com.usal.jorgeav.sportapp.data.Event} que maneja este adapter
     */
    private Cursor mDataset;
    /**
     * Referencia al objeto que implementa {@link OnEventItemClickListener}
     */
    private OnEventItemClickListener mClickListener;
    /**
     * Referencia a la librería Glide, concretamente al objeto RequestManager para cargar el icono
     * correspondiente a cada item de la lista.
     *
     * @see <a href= "https://bumptech.github.io/glide/javadocs/380/index.html">Glide</a>
     * @see <a href= "https://bumptech.github.io/glide/javadocs/380/com/bumptech/glide/RequestManager.html">
     * RequestManager</a>
     */
    private final RequestManager mGlide;

    /**
     * Constructor con argumentos
     *
     * @param mDataset      Conjunto de partidos
     * @param clickListener Referencia al Listener que implementa esta interfaz
     * @param glide         Referencia a RequestManager para cargar el icono correspondiente a
     *                      cada item de la lista.
     * @see <a href= "https://bumptech.github.io/glide/javadocs/380/com/bumptech/glide/RequestManager.html">
     * RequestManager</a>
     */
    public EventsAdapter(Cursor mDataset, OnEventItemClickListener clickListener, RequestManager glide) {
        this.mDataset = mDataset;
        this.mClickListener = clickListener;
        this.mGlide = glide;
    }

    /**
     * Invocado cuando se necesita una nueva celda. Instancia la vista inflando el layout
     * correspondiente.
     *
     * @param parent   el ViewGroup al que se añadirá la vista al crearse.
     * @param viewType tipo de la vista
     * @return una nueva instancia de {@link EventsAdapter.ViewHolder}
     */
    @Override
    public EventsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View eventView = inflater.inflate(R.layout.events_item_list, parent, false);

        return new ViewHolder(eventView);
    }

    /**
     * Invocado cuando es necesario emplazar los datos de un elemento de la colección en una celda.
     * Extrae los datos del {@link #mDataset} en la posición indicada y los coloca en los elementos
     * de {@link EventsAdapter.ViewHolder}
     *
     * @param holder   vista de la celda donde colocar los datos
     * @param position posición de los datos que se van a mostrar
     */
    @Override
    public void onBindViewHolder(EventsAdapter.ViewHolder holder, int position) {
        if (mDataset.moveToPosition(position)) {
            // Set icon
            String sportId = mDataset.getString(SportteamContract.EventEntry.COLUMN_SPORT);
            mGlide.load(Utiles.getSportIconFromResource(sportId)).into(holder.imageViewEventSport);

            // Set title
            holder.textViewEventName.setText(mDataset.getString(SportteamContract.EventEntry.COLUMN_NAME));

            // Set subtitle
            String fieldName = UtilesContentProvider.getFieldNameFromContentProvider(
                    mDataset.getString(SportteamContract.EventEntry.COLUMN_FIELD));
            String city = mDataset.getString(SportteamContract.EventEntry.COLUMN_CITY);
            String address = mDataset.getString(SportteamContract.EventEntry.COLUMN_ADDRESS);
            if (fieldName == null)
                holder.textViewEventPlace.setText(address);
            else {
                if (!TextUtils.isEmpty(fieldName)) fieldName = fieldName + ", ";
                holder.textViewEventPlace.setText(String.format("%s%s", fieldName, city));
            }
            long date = mDataset.getLong(SportteamContract.EventEntry.COLUMN_DATE);
            holder.textViewEventDate.setText(UtilesTime.millisToDateTimeString(date));

            // Set icon two
            int totalPl = mDataset.getInt(SportteamContract.EventEntry.COLUMN_TOTAL_PLAYERS);
            int emptyPl = mDataset.getInt(SportteamContract.EventEntry.COLUMN_EMPTY_PLAYERS);
            int playerIcon = Utiles.getPlayerIconFromResource(emptyPl, totalPl);
            if (playerIcon != -1) mGlide.load(playerIcon).into(holder.imageViewEventPlayer);
        }
    }

    /**
     * Setter para actualizar la colección de partidos que maneja este adapter
     *
     * @param events Colección de partidos
     */
    public void replaceData(Cursor events) {
        this.mDataset = events;
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
         * Referencia al elemento de la celda que representa la imagen del deporte del partido
         */
        @BindView(R.id.events_item_sport)
        ImageView imageViewEventSport;
        /**
         * Referencia al elemento de la celda donde se especifica el nombre del partido
         */
        @BindView(R.id.events_item_name)
        TextView textViewEventName;
        /**
         * Referencia al elemento de la celda donde se especifica el nombre del lugar del partido
         */
        @BindView(R.id.events_item_place)
        TextView textViewEventPlace;
        /**
         * Referencia al elemento de la celda donde se especifica la fecha y hora del partido
         */
        @BindView(R.id.events_item_date)
        TextView textViewEventDate;
        /**
         * Referencia al elemento de la celda que muestra el icono de los puestos vacantes
         */
        @BindView(R.id.events_item_player)
        ImageView imageViewEventPlayer;

        /**
         * Inicializa y obtiene una referencia a los elementos de la celda con la ayuda de ButterKnife.
         * Se establece como {@link View.OnClickListener} a sí mismo para la View
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
         * Obtiene la Alarma de la posición de la celda pulsada y la envía a
         * {@link OnEventItemClickListener}
         *
         * @param view View pulsada
         */
        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            mDataset.moveToPosition(position);
            if (mClickListener != null)
                mClickListener.onEventClick(
                        mDataset.getString(SportteamContract.EventEntry.COLUMN_EVENT_ID));
        }
    }

    /**
     * Interfaz para las pulsaciones sobre los partidos
     */
    public interface OnEventItemClickListener {
        /**
         * Avisa al Listener de que un partido fue pulsado
         *
         * @param eventId Identificador del Partido seleccionado
         */
        void onEventClick(String eventId);
    }
}
