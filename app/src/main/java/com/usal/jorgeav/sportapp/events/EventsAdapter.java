package com.usal.jorgeav.sportapp.events;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.usal.jorgeav.sportapp.R;
import com.usal.jorgeav.sportapp.data.Event;

import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Jorge Avila on 23/04/2017.
 */

class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.ViewHolder> {
    private static final String TAG = EventsAdapter.class.getSimpleName();

    private List<Event> mDataset;
    private OnEventItemClickListener mClickListener;

    public EventsAdapter(List<Event> mDataset, OnEventItemClickListener clickListener) {
        Log.d(TAG, "EventsAdapter");
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
        Event event = mDataset.get(position);
        if (event != null) {
            holder.textViewEventId.setText(event.getmId());
            holder.textViewEventSport.setText(event.getmSport());
            holder.textViewEventPlace.setText(event.getmPlace());
            holder.textViewEventDate.setText(event.getmDate());
            holder.textViewEventTime.setText(event.getmTime());
            holder.textViewEventTotal.setText(String.format(Locale.getDefault(), "%2d",event.getmTotalPlayers()));
            holder.textViewEventEmpty.setText(String.format(Locale.getDefault(), "%2d",event.getmEmptyPlayers()));
        }
    }

    public void replaceData(List<Event> events) {
        setDataset(events);
        notifyDataSetChanged();
    }

    public void setDataset(List<Event> mDataset) {
        this.mDataset = mDataset;
    }

    @Override
    public int getItemCount() {
        if (mDataset != null) return mDataset.size();
        else return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements AdapterView.OnClickListener{
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
            mClickListener.onEventClick(mDataset.get(position));
        }
    }

    public interface OnEventItemClickListener {
        void onEventClick(Event event);
    }
}
