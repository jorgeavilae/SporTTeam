package com.usal.jorgeav.sportapp.fields;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.usal.jorgeav.sportapp.R;
import com.usal.jorgeav.sportapp.data.Field;

import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Jorge Avila on 25/04/2017.
 */

public class FieldsAdapter extends RecyclerView.Adapter<FieldsAdapter.ViewHolder> {
    private List<Field> mDataset;
    private OnFieldItemClickListener mClickListener;

    public FieldsAdapter(List<Field> mDataset, OnFieldItemClickListener listener) {
        this.mDataset = mDataset;
        this.mClickListener = listener;
    }

    @Override
    public FieldsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View eventView = inflater.inflate(R.layout.fields_item_list, parent, false);

        return new FieldsAdapter.ViewHolder(eventView);
    }

    @Override
    public void onBindViewHolder(FieldsAdapter.ViewHolder holder, int position) {
        Field field = mDataset.get(position);
        if (field != null) {
            holder.textViewFieldId.setText(field.getmId());
            holder.textViewFieldName.setText(field.getmName());
            holder.textViewFieldAddress.setText(field.getmAddress());
            holder.textViewFieldRating.setText(String.format(Locale.getDefault(), "%2.2f", field.getmRating()));
            holder.textViewFieldSport.setText(field.getmSport());
            holder.textViewFieldOpening.setText(field.getmOpeningTime());
            holder.textViewFieldClosing.setText(field.getmClosingTime());
        }
    }

    public void replaceData(List<Field> fields) {
        setDataset(fields);
        notifyDataSetChanged();
    }

    public void setDataset(List<Field> mDataset) {
        this.mDataset = mDataset;
    }

    @Override
    public int getItemCount() {
        if (mDataset != null) return mDataset.size();
        else return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements AdapterView.OnClickListener {
        @BindView(R.id.fields_item_id)
        TextView textViewFieldId;
        @BindView(R.id.fields_item_name)
        TextView textViewFieldName;
        @BindView(R.id.fields_item_address)
        TextView textViewFieldAddress;
        @BindView(R.id.fields_item_rating)
        TextView textViewFieldRating;
        @BindView(R.id.fields_item_sport)
        TextView textViewFieldSport;
        @BindView(R.id.fields_item_opening)
        TextView textViewFieldOpening;
        @BindView(R.id.fields_item_closing)
        TextView textViewFieldClosing;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            mClickListener.onFieldClick(mDataset.get(position));
        }
    }

    public interface OnFieldItemClickListener {
        void onFieldClick(Field field);
    }
}