package com.usal.jorgeav.sportapp.adapters;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.usal.jorgeav.sportapp.R;
import com.usal.jorgeav.sportapp.data.Sport;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Jorge Avila on 20/07/2017.
 */

public class SelectSportsAdapter  extends RecyclerView.Adapter<SelectSportsAdapter.ViewHolder> {
    private static final String TAG = SelectSportsAdapter.class.getSimpleName();

    private List<Sport> mDataset;
    private OnSelectSportClickListener mClickListener;

    public SelectSportsAdapter(List<Sport> mDataset, OnSelectSportClickListener mClickListener) {
        this.mDataset = mDataset;
        this.mClickListener = mClickListener;
    }

    @Override
    public SelectSportsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder: ");
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.sport_select_item_list, parent, false);
        return new SelectSportsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SelectSportsAdapter.ViewHolder holder, int position) {
        Sport s = mDataset.get(position);
        if (s != null) {
            holder.textViewSportName.setText(s.getmName());
            holder.imageViewSportIcon.setImageResource(s.getIconDrawableId());
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

    public String getItemNameAtPosition(int position) {
        Sport s = mDataset.get(position);
        if (s != null)
            return s.getmName();
        return null;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.sport_select_item_icon)
        ImageView imageViewSportIcon;
        @BindView(R.id.sport_select_item_name)
        TextView textViewSportName;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setClickable(true);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            mClickListener.onSportClick(mDataset.get(getAdapterPosition()));
        }
    }

    public interface OnSelectSportClickListener {
        void onSportClick(Sport s);
    }
}
