package com.usal.jorgeav.sportapp.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.usal.jorgeav.sportapp.R;
import com.usal.jorgeav.sportapp.data.provider.SportteamContract;
import com.usal.jorgeav.sportapp.utils.UtilesTime;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Jorge Avila on 25/04/2017.
 */

public class FieldsAdapter extends RecyclerView.Adapter<FieldsAdapter.ViewHolder> {
    private Cursor mDataset;
    private OnFieldItemClickListener mClickListener;

    public FieldsAdapter(Cursor mDataset, OnFieldItemClickListener listener) {
        this.mDataset = mDataset;
        this.mClickListener = listener;
    }

    @Override
    public FieldsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.fields_item_list, parent, false);

        return new FieldsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FieldsAdapter.ViewHolder holder, int position) {
        if (mDataset.moveToPosition(position)) {
            float rate = mDataset.getFloat(SportteamContract.FieldEntry.COLUMN_PUNCTUATION);
            long opening = mDataset.getLong(SportteamContract.FieldEntry.COLUMN_OPENING_TIME);
            long closing = mDataset.getLong(SportteamContract.FieldEntry.COLUMN_CLOSING_TIME);
            holder.textViewFieldId.setText(mDataset.getString(SportteamContract.FieldEntry.COLUMN_FIELD_ID));
            holder.textViewFieldName.setText(mDataset.getString(SportteamContract.FieldEntry.COLUMN_NAME));
            holder.textViewFieldAddress.setText(mDataset.getString(SportteamContract.FieldEntry.COLUMN_ADDRESS));
            holder.textViewFieldRating.setText(String.format(Locale.getDefault(), "%2.2f", rate));
            holder.textViewFieldSport.setText(mDataset.getString(SportteamContract.FieldEntry.COLUMN_SPORT));
            holder.textViewFieldOpening.setText(UtilesTime.millisToTimeString(opening));
            holder.textViewFieldClosing.setText(UtilesTime.millisToTimeString(closing));
        }
    }

    public void replaceData(Cursor fields) {
        setDataset(fields);
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
            mDataset.moveToPosition(position);
            String fieldId = mDataset.getString(SportteamContract.FieldEntry.COLUMN_FIELD_ID);
            String city = mDataset.getString(SportteamContract.FieldEntry.COLUMN_CITY);
            String sportId = mDataset.getString(SportteamContract.FieldEntry.COLUMN_SPORT);
            double latitude = mDataset.getDouble(SportteamContract.FieldEntry.COLUMN_ADDRESS_LATITUDE);
            double longitude = mDataset.getDouble(SportteamContract.FieldEntry.COLUMN_ADDRESS_LONGITUDE);
            LatLng coord = null; if (latitude != 0 && longitude != 0) coord = new LatLng(latitude, longitude);
            mClickListener.onFieldClick(fieldId, city, sportId, coord);
        }
    }

    public interface OnFieldItemClickListener {
        void onFieldClick(String fieldId, String city, String sportId, LatLng coordinates);
    }
}