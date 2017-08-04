package com.usal.jorgeav.sportapp.adapters;

import android.content.Context;
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
import com.usal.jorgeav.sportapp.data.provider.SportteamContract;
import com.usal.jorgeav.sportapp.utils.UtilesContentProvider;
import com.usal.jorgeav.sportapp.utils.UtilesTime;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Jorge Avila on 15/06/2017.
 */

public class AlarmAdapter extends RecyclerView.Adapter<AlarmAdapter.ViewHolder> {
    private static final String TAG = AlarmAdapter.class.getSimpleName();

    private Cursor mDataset;
    private OnAlarmitemClickListener mClickListener;
    // To use Glide with hosted Fragment Context (https://stackoverflow.com/a/32887693/4235666)
    private final RequestManager mGlide;

    public AlarmAdapter(Cursor mDataset, OnAlarmitemClickListener clickListener, RequestManager glide) {
        this.mDataset = mDataset;
        this.mClickListener = clickListener;
        this.mGlide = glide;
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
            // Set icon
            String sportId = mDataset.getString(SportteamContract.AlarmEntry.COLUMN_SPORT);
            int sportDrawableResource = MyApplication.getAppContext().getResources()
                    .getIdentifier(sportId , "drawable", MyApplication.getAppContext().getPackageName());
            mGlide.load(sportDrawableResource).into(holder.imageViewAlarmSport);

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
        @BindView(R.id.alarm_item_sport)
        ImageView imageViewAlarmSport;
        @BindView(R.id.alarm_item_place)
        TextView textViewAlarmPlace;
        @BindView(R.id.alarm_item_date_from)
        TextView textViewAlarmDateFrom;
        @BindView(R.id.alarm_item_date_to)
        TextView textViewAlarmDateTo;

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
