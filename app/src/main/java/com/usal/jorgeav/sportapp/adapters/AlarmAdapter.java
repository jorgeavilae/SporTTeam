package com.usal.jorgeav.sportapp.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.usal.jorgeav.sportapp.R;
import com.usal.jorgeav.sportapp.data.provider.SportteamContract;
import com.usal.jorgeav.sportapp.utils.UtilesTime;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Jorge Avila on 15/06/2017.
 */

public class AlarmAdapter extends RecyclerView.Adapter<AlarmAdapter.ViewHolder> {
    private static final String TAG = AlarmAdapter.class.getSimpleName();

    private Cursor mDataset;
    private OnAlarmitemClickListener mClickListener;

    public AlarmAdapter(Cursor mDataset, OnAlarmitemClickListener clickListener) {
        this.mDataset = mDataset;
        this.mClickListener = clickListener;
    }

    @Override
    public AlarmAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View alarmView = inflater.inflate(R.layout.alarm_item_list, parent, false);

        return new AlarmAdapter.ViewHolder(alarmView);
    }

    @Override
    public void onBindViewHolder(AlarmAdapter.ViewHolder holder, int position) {
        if (mDataset.moveToPosition(position)) {
            long dateFrom = mDataset.getLong(SportteamContract.AlarmEntry.COLUMN_DATE_FROM);
            long dateTo = mDataset.getLong(SportteamContract.AlarmEntry.COLUMN_DATE_TO);
            int totalPlFrom = mDataset.getInt(SportteamContract.AlarmEntry.COLUMN_TOTAL_PLAYERS_FROM);
            int totalPlTo = mDataset.getInt(SportteamContract.AlarmEntry.COLUMN_TOTAL_PLAYERS_TO);
            int emptyPlFrom = mDataset.getInt(SportteamContract.AlarmEntry.COLUMN_EMPTY_PLAYERS_FROM);
            int emptyPlTo = mDataset.getInt(SportteamContract.AlarmEntry.COLUMN_EMPTY_PLAYERS_TO);
            holder.textViewAlarmId.setText(mDataset.getString(SportteamContract.AlarmEntry.COLUMN_ALARM_ID));
            holder.textViewAlarmSport.setText(mDataset.getString(SportteamContract.AlarmEntry.COLUMN_SPORT));
            holder.textViewAlarmPlace.setText(mDataset.getString(SportteamContract.AlarmEntry.COLUMN_FIELD));
            holder.textViewAlarmDateFrom.setText(UtilesTime.millisToDateString(dateFrom));
            holder.textViewAlarmDateTo.setText(UtilesTime.millisToDateString(dateTo));
            holder.textViewAlarmTotal.setText(String.format(Locale.getDefault(), "%2d/%2d",totalPlFrom,totalPlTo));
            holder.textViewAlarmEmpty.setText(String.format(Locale.getDefault(), "%2d/%2d",emptyPlFrom,emptyPlTo));
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
        @BindView(R.id.alarm_item_id)
        TextView textViewAlarmId;
        @BindView(R.id.alarm_item_sport)
        TextView textViewAlarmSport;
        @BindView(R.id.alarm_item_place)
        TextView textViewAlarmPlace;
        @BindView(R.id.alarm_item_date_from)
        TextView textViewAlarmDateFrom;
        @BindView(R.id.alarm_item_date_to)
        TextView textViewAlarmDateTo;
        @BindView(R.id.alarm_item_total)
        TextView textViewAlarmTotal;
        @BindView(R.id.alarm_item_empty)
        TextView textViewAlarmEmpty;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            mDataset.moveToPosition(position);
            mClickListener.onAlarmClick(mDataset.getString(SportteamContract.AlarmEntry.COLUMN_ALARM_ID));
        }
    }

    public interface OnAlarmitemClickListener {
        void onAlarmClick(String alarmId);
    }
}
