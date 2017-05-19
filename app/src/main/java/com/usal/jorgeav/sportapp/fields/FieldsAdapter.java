package com.usal.jorgeav.sportapp.fields;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.usal.jorgeav.sportapp.R;
import com.usal.jorgeav.sportapp.Utiles;
import com.usal.jorgeav.sportapp.data.Field;
import com.usal.jorgeav.sportapp.data.provider.SportteamContract;

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
        View eventView = inflater.inflate(R.layout.fields_item_list, parent, false);

        return new FieldsAdapter.ViewHolder(eventView);
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
            holder.textViewFieldOpening.setText(Utiles.millisToTimeString(opening));
            holder.textViewFieldClosing.setText(Utiles.millisToTimeString(closing));
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
            Field field = new Field(
                    mDataset.getString(SportteamContract.FieldEntry.COLUMN_FIELD_ID),
                    mDataset.getString(SportteamContract.FieldEntry.COLUMN_NAME),
                    mDataset.getString(SportteamContract.FieldEntry.COLUMN_SPORT),
                    mDataset.getString(SportteamContract.FieldEntry.COLUMN_ADDRESS),
                    mDataset.getString(SportteamContract.FieldEntry.COLUMN_CITY),
                    mDataset.getFloat(SportteamContract.FieldEntry.COLUMN_PUNCTUATION),
                    mDataset.getInt(SportteamContract.FieldEntry.COLUMN_VOTES),
                    mDataset.getLong(SportteamContract.FieldEntry.COLUMN_OPENING_TIME),
                    mDataset.getLong(SportteamContract.FieldEntry.COLUMN_CLOSING_TIME));
            mClickListener.onFieldClick(field);
        }
    }

    public interface OnFieldItemClickListener {
        void onFieldClick(Field field);
    }
}