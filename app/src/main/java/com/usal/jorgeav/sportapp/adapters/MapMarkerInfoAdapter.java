package com.usal.jorgeav.sportapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.usal.jorgeav.sportapp.R;
import com.usal.jorgeav.sportapp.data.Field;
import com.usal.jorgeav.sportapp.utils.UtilesTime;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jorge Avila on 21/07/2017.
 */

public class MapMarkerInfoAdapter implements GoogleMap.InfoWindowAdapter {

    private View mContent;
    private List<Field> mDataset;

    public MapMarkerInfoAdapter(LayoutInflater layoutInflater, ArrayList<Field> fieldList) {
        //TODO make new marker layout
        this.mContent = layoutInflater.inflate(R.layout.fields_item_list, null);
        this.mDataset = fieldList;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        Integer position = (Integer) marker.getTag();
        if (position != null) {
            populate(mContent, mDataset.get(position));
            return mContent;
        }
        return null;
    }

    private void populate(View view, Field field) {
        TextView textViewFieldId = (TextView) view.findViewById(R.id.fields_item_id);
        textViewFieldId.setText(field.getId());

        TextView textViewFieldName = (TextView) view.findViewById(R.id.fields_item_name);
        textViewFieldName.setText(field.getName());

        TextView textViewFieldAddress = (TextView) view.findViewById(R.id.fields_item_address);
        textViewFieldAddress.setText(field.getAddress());

        TextView textViewFieldOpening = (TextView) view.findViewById(R.id.fields_item_opening);
        textViewFieldOpening.setText(UtilesTime.millisToTimeString(field.getOpening_time()));

        TextView textViewFieldClosing = (TextView) view.findViewById(R.id.fields_item_closing);
        textViewFieldClosing.setText(UtilesTime.millisToTimeString(field.getClosing_time()));

    }
}
