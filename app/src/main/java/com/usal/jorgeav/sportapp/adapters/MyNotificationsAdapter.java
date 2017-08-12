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

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MyNotificationsAdapter extends RecyclerView.Adapter<MyNotificationsAdapter.ViewHolder> {
    @SuppressWarnings("unused")
    private static final String TAG = MyNotificationsAdapter.class.getSimpleName();

    /* LinkedHasMap to reference a notification by position instead of by key */
    private LinkedHashMap<String, MyNotification> mDataset;
    private OnMyNotificationItemClickListener mClickListener;
    private RequestManager mGlide;

    public MyNotificationsAdapter(Map<String, MyNotification> dataset, OnMyNotificationItemClickListener listener, RequestManager glide) {
        if (dataset != null) this.mDataset = new LinkedHashMap<>(dataset);
        else this.mDataset = null;
        this.mClickListener = listener;
        this.mGlide = glide;
    }

    @Override
    public MyNotificationsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.notification_item_list, parent, false);

        return new MyNotificationsAdapter.ViewHolder(view);
    }

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
        }
    }

    /* Look for value in position. Value is always the same since
     * mDataset is a LinkedHasMap (ordered) not a simple HashMap */
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
    }

    private void setDataset(Map<String, MyNotification> mDataset) {
        if (mDataset != null) this.mDataset = new LinkedHashMap<>(mDataset);
        else this.mDataset = null;
    }

    @Override
    public int getItemCount() {
        if (mDataset != null) return mDataset.size();
        else return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements AdapterView.OnClickListener {
        @BindView(R.id.notification_item_icon)
        ImageView imageViewNotificationIcon;
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
                mClickListener.onMyNotificationClick(entry.getKey(), entry.getValue());
        }
    }

    public interface OnMyNotificationItemClickListener {
        void onMyNotificationClick(String key, MyNotification notification);
    }
}