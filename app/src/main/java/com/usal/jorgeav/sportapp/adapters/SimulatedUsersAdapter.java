package com.usal.jorgeav.sportapp.adapters;

import android.database.Cursor;
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
import com.usal.jorgeav.sportapp.data.provider.SportteamContract;
import com.usal.jorgeav.sportapp.utils.UtilesContentProvider;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Jorge Avila on 13/07/2017.
 */

public class SimulatedUsersAdapter extends RecyclerView.Adapter<SimulatedUsersAdapter.ViewHolder> {
    private static final String TAG = SimulatedUsersAdapter.class.getSimpleName();

    private Cursor mDataset;
    private OnSimulatedUserItemClickListener mClickListener;
    private RequestManager mGlide;

    public SimulatedUsersAdapter(Cursor mDataset, OnSimulatedUserItemClickListener listener, RequestManager glide) {
        this.mDataset = mDataset;
        this.mClickListener = listener;
        this.mGlide = glide;
    }

    @Override
    public SimulatedUsersAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.simulated_user_item_list, parent, false);

        return new SimulatedUsersAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final SimulatedUsersAdapter.ViewHolder holder, int position) {
        if (mDataset.moveToPosition(position)) {
            // Set icon
            String userPicture = mDataset.getString(SportteamContract.SimulatedParticipantEntry.COLUMN_PROFILE_PICTURE);
            if (userPicture != null && !TextUtils.isEmpty(userPicture))
                mGlide.load(Uri.parse(userPicture)).asBitmap().into(new BitmapImageViewTarget(holder.imageViewSimulatedUserPhoto) {
                    @Override
                    protected void setResource(Bitmap resource) {
                        RoundedBitmapDrawable circularBitmapDrawable =
                                RoundedBitmapDrawableFactory.create(MyApplication.getAppContext().getResources(), resource);
                        circularBitmapDrawable.setCircular(true);
                        holder.imageViewSimulatedUserPhoto.setImageDrawable(circularBitmapDrawable);
                    }
                });
            else
                mGlide.load(R.drawable.profile_picture_placeholder)
                        .placeholder(R.drawable.profile_picture_placeholder)
                        .into(holder.imageViewSimulatedUserPhoto);

            // Set title
            holder.textViewSimulatedUserName.setText(mDataset.getString(SportteamContract.SimulatedParticipantEntry.COLUMN_ALIAS));

            // Set subtitle
            String ownerId = mDataset.getString(SportteamContract.SimulatedParticipantEntry.COLUMN_OWNER);
            String ownerName = UtilesContentProvider.getUserNameFromContentProvider(ownerId);
            String unformattedString = MyApplication.getAppContext().getString(R.string.created_by);
            holder.textViewSimulatedUserOwner.setText(String.format(unformattedString, ownerName));
        }
    }

    public void replaceData(Cursor users) {
        setDataset(users);
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
        @BindView(R.id.simulated_user_item_photo)
        ImageView imageViewSimulatedUserPhoto;
        @BindView(R.id.simulated_user_item_name)
        TextView textViewSimulatedUserName;
        @BindView(R.id.simulated_user_item_owner)
        TextView textViewSimulatedUserOwner;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            if (mDataset.moveToPosition(position)) {
                String id = mDataset.getString(SportteamContract.SimulatedParticipantEntry.COLUMN_SIMULATED_USER_ID);
                String owner = mDataset.getString(SportteamContract.SimulatedParticipantEntry.COLUMN_OWNER);
                mClickListener.onSimulatedUserClick(owner, id);
            }
        }
    }

    public interface OnSimulatedUserItemClickListener {
        void onSimulatedUserClick(String ownerId, String simulatedUserId);
    }
}
