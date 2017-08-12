package com.usal.jorgeav.sportapp.adapters;

import android.database.Cursor;
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
import com.usal.jorgeav.sportapp.data.provider.SportteamContract;
import com.usal.jorgeav.sportapp.utils.Utiles;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProfileSportsAdapter extends RecyclerView.Adapter<ProfileSportsAdapter.ViewHolder> {
    @SuppressWarnings("unused")
    private static final String TAG = ProfileSportsAdapter.class.getSimpleName();

    private Cursor mDataset;
    private OnProfileSportClickListener mListener;
    private RequestManager mGlide;

    public ProfileSportsAdapter(Cursor mDataset, OnProfileSportClickListener listener, RequestManager glide) {
        this.mDataset = mDataset;
        this.mListener = listener;
        this.mGlide = glide;
    }

    @Override
    public ProfileSportsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.sport_profile_item_list, parent, false);
        return new ProfileSportsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ProfileSportsAdapter.ViewHolder holder, int position) {
        if (mDataset.moveToPosition(position)) {
            String sportId = mDataset.getString(SportteamContract.UserSportEntry.COLUMN_SPORT);

            // Set icon
            mGlide.load(Utiles.getSportIconFromResource(sportId)).asBitmap().into(holder.imageViewSportIcon);

            // Set title
            int nameResource = MyApplication.getAppContext().getResources()
                    .getIdentifier(sportId, "string", MyApplication.getAppContext().getPackageName());
            holder.textViewSportName.setText(nameResource);

            // Set stars
            float level = mDataset.getFloat(SportteamContract.UserSportEntry.COLUMN_LEVEL);
            holder.ratingBarSportLevel.setRating(level);
        }
    }

    @Override
    public int getItemCount() {
        if (mDataset != null) return mDataset.getCount();
        else return 0;
    }

    public void replaceData(Cursor sports) {
        setDataset(sports);
        notifyDataSetChanged();
    }

    private void setDataset(Cursor mDataset) {
        this.mDataset = mDataset;
    }

    public ArrayList<Sport> getDataAsArrayList() {
        if (mDataset == null) return null;
        ArrayList<Sport> result = new ArrayList<>();
        for(mDataset.moveToFirst(); !mDataset.isAfterLast(); mDataset.moveToNext()) {
            String name = mDataset.getString(SportteamContract.UserSportEntry.COLUMN_SPORT);
            float level = mDataset.getFloat(SportteamContract.UserSportEntry.COLUMN_LEVEL);
            result.add(new Sport(name, level, 0));
        }
        return result;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        @BindView(R.id.sport_profile_item_icon)
        ImageView imageViewSportIcon;
        @BindView(R.id.sport_profile_item_name)
        TextView textViewSportName;
        @BindView(R.id.sport_profile_item_level)
        RatingBar ratingBarSportLevel;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            mDataset.moveToPosition(position);
            String name = mDataset.getString(SportteamContract.UserSportEntry.COLUMN_SPORT);
            float punctuation = mDataset.getFloat(SportteamContract.UserSportEntry.COLUMN_LEVEL);

            if (mListener != null)
                mListener.onProfileSportClick(new Sport(name, punctuation, 1));
        }
    }

    public interface OnProfileSportClickListener {
        void onProfileSportClick(Sport s);
    }
}

