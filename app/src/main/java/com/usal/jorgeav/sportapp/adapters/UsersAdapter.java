package com.usal.jorgeav.sportapp.adapters;

import android.database.Cursor;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.RequestManager;
import com.usal.jorgeav.sportapp.R;
import com.usal.jorgeav.sportapp.data.provider.SportteamContract;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.ViewHolder> {
    @SuppressWarnings("unused")
    private static final String TAG = UsersAdapter.class.getSimpleName();

    private Cursor mDataset;
    private OnUserItemClickListener mClickListener;
    private RequestManager mGlide;

    public UsersAdapter(Cursor mDataset, OnUserItemClickListener mClickListener, RequestManager glide) {
        this.mDataset = mDataset;
        this.mClickListener = mClickListener;
        this.mGlide = glide;
    }

    @Override
    public UsersAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.user_item_grid, parent, false);

        return new UsersAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final UsersAdapter.ViewHolder holder, int position) {
        if (mDataset.moveToPosition(position)) {
            // Set icon
            String userPicture = mDataset.getString(SportteamContract.UserEntry.COLUMN_PHOTO);
            mGlide.load(Uri.parse(userPicture)).asBitmap()
                    .placeholder(R.drawable.profile_picture_placeholder)
                    .into(holder.imageViewUserPhoto);

            // Set title
            holder.textViewUserName.setText(mDataset.getString(SportteamContract.UserEntry.COLUMN_NAME));

            // Set subtitle
            holder.textViewUserCity.setText(mDataset.getString(SportteamContract.UserEntry.COLUMN_CITY));
        }
    }

    public void replaceData(Cursor users) {
        setDataset(users);
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

    public class ViewHolder extends RecyclerView.ViewHolder implements AdapterView.OnClickListener, AdapterView.OnLongClickListener {
        @BindView(R.id.user_item_photo)
        ImageView imageViewUserPhoto;
        @BindView(R.id.user_item_name)
        TextView textViewUserName;
        @BindView(R.id.user_item_city)
        TextView textViewUserCity;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            if (mDataset.moveToPosition(position))
                mClickListener.onUserClick(mDataset.getString(SportteamContract.UserEntry.COLUMN_USER_ID));
        }

        @Override
        public boolean onLongClick(View view) {
            int position = getAdapterPosition();
            return mDataset.moveToPosition(position)
                    && mClickListener.onUserLongClick(mDataset.getString(SportteamContract.UserEntry.COLUMN_USER_ID));
        }
    }

    public interface OnUserItemClickListener {
        void onUserClick(String uid);
        boolean onUserLongClick(String uid);
    }
}
