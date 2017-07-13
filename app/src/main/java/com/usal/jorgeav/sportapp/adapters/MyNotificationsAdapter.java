package com.usal.jorgeav.sportapp.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.usal.jorgeav.sportapp.R;
import com.usal.jorgeav.sportapp.data.MyNotification;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Jorge Avila on 13/07/2017.
 */

public class MyNotificationsAdapter extends RecyclerView.Adapter<MyNotificationsAdapter.ViewHolder> {
    private static final String TAG = MyNotificationsAdapter.class.getSimpleName();

    private LinkedHashMap<String, MyNotification> mDataset;
    private OnMyNotificationItemClickListener mClickListener;

    public MyNotificationsAdapter(Map<String, MyNotification> mDataset, OnMyNotificationItemClickListener listener) {
        if (mDataset != null) this.mDataset = new LinkedHashMap<>(mDataset);
        else this.mDataset = null;
        this.mClickListener = listener;
    }

    @Override
    public MyNotificationsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.notification_item_list, parent, false);

        return new MyNotificationsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyNotificationsAdapter.ViewHolder holder, int position) {
        Map.Entry<String, MyNotification> entry = getEntry(position);
        if (entry != null) {
            MyNotification n = entry.getValue();
            holder.textViewNotificationId.setText(entry.getKey());
            holder.textViewNotificationTitle.setText(n.getTitle());
            holder.textViewNotificationMessage.setText(n.getMessage());
        }
    }

    private Map.Entry<String, MyNotification> getEntry(int position) {
        if (position > -1 && position < mDataset.size()) {
            int i = 0;
            for (Map.Entry<String, MyNotification> entry : mDataset.entrySet())
                if (i++ == position) return entry;
        }
        return null;
    }

    public void replaceData(HashMap<String, MyNotification> notifications) {
        setDataset(notifications);
        notifyDataSetChanged();
        Log.d(TAG, "replaceData: size "+(mDataset!=null?mDataset.size():"null"));
    }

    public void setDataset(Map<String, MyNotification> mDataset) {
        if (mDataset != null) this.mDataset = new LinkedHashMap<>(mDataset);
        else this.mDataset = null;
    }

    @Override
    public int getItemCount() {
        if (mDataset != null) return mDataset.size();
        else return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements AdapterView.OnClickListener {
        @BindView(R.id.notification_item_id)
        TextView textViewNotificationId;
        @BindView(R.id.notification_item_title)
        TextView textViewNotificationTitle;
        @BindView(R.id.notification_item_message)
        TextView textViewNotificationMessage;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            Map.Entry<String, MyNotification> entry = getEntry(position);
            if (entry != null)
                mClickListener.onMyNotificationClick(entry.getValue());
        }
    }

    public interface OnMyNotificationItemClickListener {
        void onMyNotificationClick(MyNotification notification);
    }
}