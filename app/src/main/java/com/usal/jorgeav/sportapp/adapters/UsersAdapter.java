package com.usal.jorgeav.sportapp.adapters;

import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.usal.jorgeav.sportapp.R;
import com.usal.jorgeav.sportapp.data.provider.SportteamContract;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Jorge Avila on 26/05/2017.
 */

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.ViewHolder> {
    private static final String TAG = UsersAdapter.class.getSimpleName();

    private Cursor mDataset;
    private OnUserItemClickListener mClickListener;

    public UsersAdapter(Cursor mDataset, OnUserItemClickListener mClickListener) {
        this.mDataset = mDataset;
        this.mClickListener = mClickListener;
    }

    @Override
    public UsersAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.user_item_list, parent, false);

        return new UsersAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(UsersAdapter.ViewHolder holder, int position) {
        if (mDataset.moveToPosition(position)) {
            int age = mDataset.getInt(SportteamContract.UserEntry.COLUMN_AGE);
            holder.textViewUserId.setText(mDataset.getString(SportteamContract.UserEntry.COLUMN_USER_ID));
            holder.textViewUserEmail.setText(mDataset.getString(SportteamContract.UserEntry.COLUMN_EMAIL));
            holder.textViewUserName.setText(mDataset.getString(SportteamContract.UserEntry.COLUMN_NAME));
            holder.textViewUserAge.setText(String.format(Locale.getDefault(), "%2d", age));
            holder.textViewUserCity.setText(mDataset.getString(SportteamContract.UserEntry.COLUMN_CITY));
            holder.textViewUserPhoto.setText(mDataset.getString(SportteamContract.UserEntry.COLUMN_PHOTO));
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
        @BindView(R.id.user_item_id)
        TextView textViewUserId;
        @BindView(R.id.user_item_email)
        TextView textViewUserEmail;
        @BindView(R.id.user_item_name)
        TextView textViewUserName;
        @BindView(R.id.user_item_age)
        TextView textViewUserAge;
        @BindView(R.id.user_item_city)
        TextView textViewUserCity;
        @BindView(R.id.user_item_photo)
        TextView textViewUserPhoto;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            if (mDataset.moveToPosition(position))
                mClickListener.onUserClick(mDataset.getString(SportteamContract.UserEntry.COLUMN_USER_ID));
        }
    }

    public interface OnUserItemClickListener {
        void onUserClick(String uid);
    }
}
