package com.usal.jorgeav.sportapp.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.usal.jorgeav.sportapp.R;
import com.usal.jorgeav.sportapp.data.Sport;

import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Jorge Avila on 03/06/2017.
 */

public class AddSportsAdapter extends RecyclerView.Adapter<AddSportsAdapter.ViewHolder> {

    private List<Sport> mDataset;

    public AddSportsAdapter(List<Sport> mDataset) {
        this.mDataset = mDataset;
    }

    @Override
    public AddSportsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout., parent, false);
        return new AddSportsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AddSportsAdapter.ViewHolder holder, int position) {
        Sport s = mDataset.get(position);
        if (s != null) {
            String name = s.getmName();
            float level = s.getmLevel();
            holder.textViewSportName.setText(name);
            holder.textViewSportLevel.setText(String.format(Locale.getDefault(), "%.2f", level));
        }
    }

    @Override
    public int getItemCount() {
        if (mDataset != null) return mDataset.size();
        else return 0;
    }

    public void replaceData(List<Sport> sports) {
        setDataset(sports);
        notifyDataSetChanged();
    }

    public void setDataset(List<Sport> mDataset) {
        this.mDataset = mDataset;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.sport_item_icon)
        ImageView imageViewSportIcon;
        @BindView(R.id.sport_item_name)
        TextView textViewSportName;
        @BindView(R.id.sport_item_level)
        TextView textViewSportLevel;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}