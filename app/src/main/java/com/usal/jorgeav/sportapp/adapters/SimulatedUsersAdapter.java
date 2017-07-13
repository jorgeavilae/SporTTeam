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
 * Created by Jorge Avila on 13/07/2017.
 */

public class SimulatedUsersAdapter extends RecyclerView.Adapter<SimulatedUsersAdapter.ViewHolder> {
    private static final String TAG = SimulatedUsersAdapter.class.getSimpleName();

    private Cursor mDataset;
    private OnSimulatedUserItemClickListener mClickListener;

    public SimulatedUsersAdapter(Cursor mDataset, OnSimulatedUserItemClickListener listener) {
        this.mDataset = mDataset;
        this.mClickListener = listener;
    }

    @Override
    public SimulatedUsersAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.simulated_user_item_list, parent, false);

        return new SimulatedUsersAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SimulatedUsersAdapter.ViewHolder holder, int position) {
        if (mDataset.moveToPosition(position)) {
            int age = mDataset.getInt(SportteamContract.SimulatedParticipantEntry.COLUMN_AGE);
            holder.textViewSimulatedUserId.setText(mDataset.getString(SportteamContract.SimulatedParticipantEntry.COLUMN_SIMULATED_USER_ID));
            holder.textViewSimulatedUserName.setText(mDataset.getString(SportteamContract.SimulatedParticipantEntry.COLUMN_ALIAS));
            holder.textViewSimulatedUserPhoto.setText(mDataset.getString(SportteamContract.SimulatedParticipantEntry.COLUMN_PROFILE_PICTURE));
            holder.textViewSimulatedUserAge.setText(String.format(Locale.getDefault(), "%2d", age));
            holder.textViewSimulatedUserOwner.setText(mDataset.getString(SportteamContract.SimulatedParticipantEntry.COLUMN_OWNER));
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
        @BindView(R.id.simulated_user_item_id)
        TextView textViewSimulatedUserId;
        @BindView(R.id.simulated_user_item_name)
        TextView textViewSimulatedUserName;
        @BindView(R.id.simulated_user_item_age)
        TextView textViewSimulatedUserAge;
        @BindView(R.id.simulated_user_item_owner)
        TextView textViewSimulatedUserOwner;
        @BindView(R.id.simulated_user_item_photo)
        TextView textViewSimulatedUserPhoto;

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
