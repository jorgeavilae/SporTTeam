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

public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.ViewHolder> {
    @SuppressWarnings("unused")
    private static final String TAG = EventsAdapter.class.getSimpleName();

    private Cursor mDataset;
    private OnEventItemClickListener mClickListener;
    // To use Glide with hosted Fragment Context (https://stackoverflow.com/a/32887693/4235666)
    private final RequestManager mGlide;

    public EventsAdapter(Cursor mDataset, OnEventItemClickListener clickListener, RequestManager glide) {
        this.mDataset = mDataset;
        this.mClickListener = clickListener;
        this.mGlide = glide;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View eventView = inflater.inflate(R.layout.events_item_list, parent, false);

        return new ViewHolder(eventView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
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
                holder.textViewEventPlace.setText(fieldName + city);
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

    public void replaceData(Cursor events) {
        setDataset(events);
        notifyDataSetChanged();
    }

    private void setDataset(Cursor mDataset) {
        this.mDataset = mDataset;
    }

    @Override
    public int getItemCount() {
        if (mDataset != null) return mDataset.getCount();
        else return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements AdapterView.OnClickListener {
        @BindView(R.id.events_item_sport)
        ImageView imageViewEventSport;
        @BindView(R.id.events_item_name)
        TextView textViewEventName;
        @BindView(R.id.events_item_place)
        TextView textViewEventPlace;
        @BindView(R.id.events_item_date)
        TextView textViewEventDate;
        @BindView(R.id.events_item_player)
        ImageView imageViewEventPlayer;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            mDataset.moveToPosition(position);
            mClickListener.onEventClick(mDataset.getString(SportteamContract.EventEntry.COLUMN_EVENT_ID));
        }
    }

    public interface OnEventItemClickListener {
        void onEventClick(String eventId);
    }
}
