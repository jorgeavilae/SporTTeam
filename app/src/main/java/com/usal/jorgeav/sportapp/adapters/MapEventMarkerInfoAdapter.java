package com.usal.jorgeav.sportapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.usal.jorgeav.sportapp.R;
import com.usal.jorgeav.sportapp.data.Event;
import com.usal.jorgeav.sportapp.utils.Utiles;

import java.util.ArrayList;
import java.util.List;

public class MapEventMarkerInfoAdapter implements GoogleMap.InfoWindowAdapter {

    private View mContent;
    private List<Event> mDataset;

    public MapEventMarkerInfoAdapter(LayoutInflater layoutInflater, ArrayList<Event> eventList) {
        this.mContent = layoutInflater.inflate(R.layout.field_marker, null);
        this.mDataset = eventList;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        Integer position = (Integer) marker.getTag();
        if (position != null && position > -1 && mDataset != null && position < mDataset.size()) {
            populate(mContent, mDataset.get(position));
            return mContent;
        }
        return null;
    }

    private void populate(View view, Event event) {
        TextView textViewFieldName = (TextView) view.findViewById(R.id.field_marker_name);
        textViewFieldName.setText(event.getName());

        TextView textViewFieldAddress = (TextView) view.findViewById(R.id.field_marker_address);
        textViewFieldAddress.setText(event.getAddress());

        ImageView imageViewFieldSport1 = (ImageView) view.findViewById(R.id.field_marker_sport1);
        imageViewFieldSport1.setVisibility(View.VISIBLE);
        imageViewFieldSport1.setImageResource(Utiles.getSportIconFromResource(event.getSport_id()));
    }
}