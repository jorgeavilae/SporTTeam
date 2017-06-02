package com.usal.jorgeav.sportapp.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.usal.jorgeav.sportapp.R;
import com.usal.jorgeav.sportapp.Utiles;
import com.usal.jorgeav.sportapp.data.provider.SportteamContract;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Jorge Avila on 23/04/2017.
 */

public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.ViewHolder> {
    private static final String TAG = EventsAdapter.class.getSimpleName();

    private Cursor mDataset;
    private OnEventItemClickListener mClickListener;

    public EventsAdapter(Cursor mDataset, OnEventItemClickListener clickListener) {
        this.mDataset = mDataset;
        this.mClickListener = clickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View eventView = inflater.inflate(R.layout.events_item_list, parent, false);

        return new ViewHolder(eventView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (mDataset.moveToPosition(position)) {
            long date = mDataset.getLong(SportteamContract.EventEntry.COLUMN_DATE);
            int totalPl = mDataset.getInt(SportteamContract.EventEntry.COLUMN_TOTAL_PLAYERS);
            int emptyPl = mDataset.getInt(SportteamContract.EventEntry.COLUMN_EMPTY_PLAYERS);
            holder.textViewEventId.setText(mDataset.getString(SportteamContract.EventEntry.COLUMN_EVENT_ID));
            holder.textViewEventSport.setText(mDataset.getString(SportteamContract.EventEntry.COLUMN_SPORT));
            holder.textViewEventPlace.setText(mDataset.getString(SportteamContract.EventEntry.COLUMN_FIELD));
            holder.textViewEventDate.setText(Utiles.millisToDateTimeString(date));
            holder.textViewEventTime.setText(mDataset.getString(SportteamContract.EventEntry.COLUMN_OWNER));
            holder.textViewEventTotal.setText(String.format(Locale.getDefault(), "%2d",totalPl));
            holder.textViewEventEmpty.setText(String.format(Locale.getDefault(), "%2d",emptyPl));
        }
    }

    public void replaceData(Cursor events) {
        setDataset(events);
        notifyDataSetChanged();
    }

    public void setDataset(Cursor mDataset) {
        this.mDataset = mDataset;
    }

    @Override
    public int getItemCount() {
        if (mDataset != null) return mDataset.getCount();
        else return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements AdapterView.OnClickListener {
        @BindView(R.id.events_item_id)
        TextView textViewEventId;
        @BindView(R.id.events_item_sport)
        TextView textViewEventSport;
        @BindView(R.id.events_item_place)
        TextView textViewEventPlace;
        @BindView(R.id.events_item_date)
        TextView textViewEventDate;
        @BindView(R.id.events_item_time)
        TextView textViewEventTime;
        @BindView(R.id.events_item_total)
        TextView textViewEventTotal;
        @BindView(R.id.events_item_empty)
        TextView textViewEventEmpty;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Log.d(TAG, "onClick");
            int position = getAdapterPosition();
            mDataset.moveToPosition(position);
            mClickListener.onEventClick(mDataset.getString(SportteamContract.EventEntry.COLUMN_EVENT_ID));
        }
    }

    public interface OnEventItemClickListener {
        void onEventClick(String eventId);
    }
}
