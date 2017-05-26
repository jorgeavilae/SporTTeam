package com.usal.jorgeav.sportapp.adapters;

import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.usal.jorgeav.sportapp.data.User;

import butterknife.ButterKnife;

/**
 * Created by Jorge Avila on 26/05/2017.
 */

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.ViewHolder> {
    private Cursor mDataset;
    private OnUserItemClickListener mClickListener;

    public UsersAdapter(Cursor mDataset, OnUserItemClickListener mClickListener) {
        this.mDataset = mDataset;
        this.mClickListener = mClickListener;
    }

    @Override
    public UsersAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(UsersAdapter.ViewHolder holder, int position) {

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
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements AdapterView.OnClickListener {

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
        }
    }

    public interface OnUserItemClickListener {
        void onUserClick(User user);
    }
}
