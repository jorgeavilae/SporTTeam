package com.usal.jorgeav.sportapp.adapters;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.usal.jorgeav.sportapp.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jorge Avila on 03/08/2017.
 */

public class SportSpinnerAdapter extends ArrayAdapter {
    ArrayList<String> mDataset;

    public SportSpinnerAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<String> objects) {
        super(context, resource, objects);
        this.mDataset = (ArrayList<String>) objects;
    }

    private View getCustomView(int position, @Nullable View convertView, @NonNull ViewGroup parent){
        String sportId = mDataset.get(position);

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            convertView = inflater.inflate(R.layout.sport_spinner_item, parent, false);
        }

        int sportStringResource = parent.getContext().getResources()
                .getIdentifier(sportId , "string", parent.getContext().getPackageName());
        TextView text = (TextView) convertView.findViewById(R.id.sport_spinner_item_text);
        text.setText(parent.getContext().getString(sportStringResource));

        int sportDrawableResource = parent.getContext().getResources()
                .getIdentifier(sportId , "drawable", parent.getContext().getPackageName());
        ImageView icon = (ImageView) convertView.findViewById(R.id.sport_spinner_item_icon);
        icon.setImageResource(sportDrawableResource);

        return convertView;

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }
}
