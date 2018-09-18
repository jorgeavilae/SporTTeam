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
import com.usal.jorgeav.sportapp.MyApplication;
import com.usal.jorgeav.sportapp.R;
import com.usal.jorgeav.sportapp.data.Alarm;
import com.usal.jorgeav.sportapp.data.provider.SportteamContract;
import com.usal.jorgeav.sportapp.utils.Utiles;
import com.usal.jorgeav.sportapp.utils.UtilesContentProvider;
import com.usal.jorgeav.sportapp.utils.UtilesTime;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Adaptador para la lista de alarmas
 */
public class AlarmAdapter extends RecyclerView.Adapter<AlarmAdapter.ViewHolder> {
    /**
     * Nombre de la clase
     */
    @SuppressWarnings("unused")
    private static final String TAG = AlarmAdapter.class.getSimpleName();

    /**
     * Alamacena la coleccion de {@link Alarm} que maneja este adapter
     */
    private Cursor mDataset;
    /**
     * Referencia al objeto que implementa {@link OnAlarmItemClickListener}
     */
    private OnAlarmItemClickListener mClickListener;
    /**
     * Referencia a la libreria Glide, concretamente al objeto RequestManager para cargar el icono
     * correspondiente a cada item de la lista.
     *
     * @see
     * <a href= "https://bumptech.github.io/glide/javadocs/380/index.html">
     *     Glide
     * </a>
     * @see
     * <a href= "https://bumptech.github.io/glide/javadocs/380/com/bumptech/glide/RequestManager.html">
     *     RequestManager
     * </a>
     */
    private final RequestManager mGlide;

    /**
     * Constructor con argumentos
     *
     * @param mDataset Conjunto de alarmas
     * @param clickListener Referencia al Listener que implementa esta interfaz
     * @param glide Referencia a RequestManager para cargar el icono correspondiente a cada item de
     *             la lista.
     *
     * @see
     * <a href= "https://bumptech.github.io/glide/javadocs/380/com/bumptech/glide/RequestManager.html">
     *     RequestManager
     * </a>
     */
    public AlarmAdapter(Cursor mDataset, OnAlarmItemClickListener clickListener, RequestManager glide) {
        this.mDataset = mDataset;
        this.mClickListener = clickListener;
        this.mGlide = glide;
    }

    @Override
    public AlarmAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View alarmView = inflater.inflate(R.layout.alarm_item_list, parent, false);

        return new AlarmAdapter.ViewHolder(alarmView);
    }

    @Override
    public void onBindViewHolder(AlarmAdapter.ViewHolder holder, int position) {
        if (mDataset.moveToPosition(position)) {
            // Set icon
            String sportId = mDataset.getString(SportteamContract.AlarmEntry.COLUMN_SPORT);
            mGlide.load(Utiles.getSportIconFromResource(sportId)).into(holder.imageViewAlarmSport);

            // Set title
            String fieldName = UtilesContentProvider.getFieldNameFromContentProvider(
                    mDataset.getString(SportteamContract.AlarmEntry.COLUMN_FIELD));
            String city = mDataset.getString(SportteamContract.AlarmEntry.COLUMN_CITY);
            if (fieldName == null) fieldName = "";
            if (!TextUtils.isEmpty(fieldName)) fieldName = fieldName + ", ";
            holder.textViewAlarmPlace.setText(fieldName + city);

            // Set subtitle
            Long dateFrom = mDataset.getLong(SportteamContract.AlarmEntry.COLUMN_DATE_FROM);
            holder.textViewAlarmDateFrom.setText(UtilesTime.millisToDateStringShort(dateFrom));
            String dateToStr = UtilesTime.millisToDateStringShort(mDataset.getLong(SportteamContract.AlarmEntry.COLUMN_DATE_TO));
            if (TextUtils.isEmpty(dateToStr)) dateToStr = MyApplication.getAppContext().getString(R.string.forever);
            holder.textViewAlarmDateTo.setText(dateToStr);
        }
    }

    /**
     * Setter para actualizar la coleccion de alarmas que maneja este adapter
     * @param alarms Colección de alarmas
     */
    public void replaceData(Cursor alarms) {
        this.mDataset = alarms;
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
         * Imagen del deporte de la alarma
         */
        @BindView(R.id.alarm_item_sport)
        ImageView imageViewAlarmSport;
        /**
         * Lugar de la alarma
         */
        @BindView(R.id.alarm_item_place)
        TextView textViewAlarmPlace;
        /**
         * Limite de fecha superior
         */
        @BindView(R.id.alarm_item_date_from)
        TextView textViewAlarmDateFrom;
        /**
         * Limite de fecha inferior
         */
        @BindView(R.id.alarm_item_date_to)
        TextView textViewAlarmDateTo;

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
         * Obtiene la Alarma de la posicion de la celda pulsada y
         * la envia a {@link OnAlarmItemClickListener}
         *
         * @param view View pulsada
         */
        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            mDataset.moveToPosition(position);
            if (mClickListener != null)
                mClickListener.onAlarmClick(
                        mDataset.getString(SportteamContract.AlarmEntry.COLUMN_ALARM_ID));
        }
    }

    /**
     * Interfaz para las pulsaciones sobre la alarmas
     */
    public interface OnAlarmItemClickListener {
        /**
         * Avisa al Listener de que una Alarma fue pulsada
         *
         * @param alarmId Identificador de la Alarma seleccionada
         */
        void onAlarmClick(String alarmId);
    }
}
