package com.usal.jorgeav.sportapp.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.RequestManager;
import com.usal.jorgeav.sportapp.MyApplication;
import com.usal.jorgeav.sportapp.R;
import com.usal.jorgeav.sportapp.data.Sport;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SelectSportsAdapter  extends RecyclerView.Adapter<SelectSportsAdapter.ViewHolder> {
    @SuppressWarnings("unused")
    private static final String TAG = SelectSportsAdapter.class.getSimpleName();

    private List<Sport> mDataset;
    private OnSelectSportClickListener mClickListener;
    private RequestManager mGlide;

    public SelectSportsAdapter(List<Sport> mDataset, OnSelectSportClickListener mClickListener, RequestManager glide) {
        this.mDataset = mDataset;
        this.mClickListener = mClickListener;
        this.mGlide = glide;
    }

    @Override
    public SelectSportsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.sport_select_item_list, parent, false);
        return new SelectSportsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SelectSportsAdapter.ViewHolder holder, int position) {
        Sport s = mDataset.get(position);
        if (s != null) {
            // Set icon
            mGlide.load(s.getIconDrawableId()).asBitmap().into(holder.imageViewSportIcon);

            // Set title
            int nameResource = MyApplication.getAppContext().getResources()
                    .getIdentifier(s.getSportID() , "string", MyApplication.getAppContext().getPackageName());
            holder.textViewSportName.setText(nameResource);
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
