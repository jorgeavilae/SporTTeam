package com.usal.jorgeav.sportapp.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.RequestManager;
import com.usal.jorgeav.sportapp.MyApplication;
import com.usal.jorgeav.sportapp.R;
import com.usal.jorgeav.sportapp.data.Sport;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AddSportsAdapter extends RecyclerView.Adapter<AddSportsAdapter.ViewHolder> {
    @SuppressWarnings("unused")
    private static final String TAG = AddSportsAdapter.class.getSimpleName();

    private List<Sport> mDataset;
    private RequestManager mGlide;

    public AddSportsAdapter(List<Sport> mDataset, RequestManager glide) {
        this.mDataset = mDataset;
        this.mGlide = glide;
    }

    @Override
    public AddSportsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.sport_add_item_list, parent, false);
        return new AddSportsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AddSportsAdapter.ViewHolder holder, int position) {
        Sport s = mDataset.get(position);
        if (s != null) {
            mGlide.load(s.getIconDrawableId()).into(holder.imageViewSportIcon);
            int nameResource = MyApplication.getAppContext().getResources()
                    .getIdentifier(s.getSportID() , "string", MyApplication.getAppContext().getPackageName());
            holder.textViewSportName.setText(nameResource);
            holder.ratingBarSportLevel.setRating(s.getPunctuation());
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

    private void setDataset(List<Sport> mDataset) {
        this.mDataset = mDataset;
    }

    public List<Sport> getDataAsArrayList() {
        if (mDataset == null) return null;
        ArrayList<Sport> result = new ArrayList<>();
        for (Sport s : mDataset)
            if (s.getPunctuation() > 0)
                result.add(s);
        return result;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.sport_add_item_icon)
        ImageView imageViewSportIcon;
        @BindView(R.id.sport_add_item_name)
        TextView textViewSportName;
        @BindView(R.id.sport_add_item_level)
        RatingBar ratingBarSportLevel;

        public ViewHolder(final View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            ratingBarSportLevel.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                @Override
                public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                    mDataset.get(getAdapterPosition()).setPunctuation(v);
                }
            });
        }
    }
}