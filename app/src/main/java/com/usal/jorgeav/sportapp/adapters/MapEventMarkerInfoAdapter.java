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
import com.usal.jorgeav.sportapp.utils.UtilesTime;

import java.util.ArrayList;
import java.util.List;

public class MapEventMarkerInfoAdapter implements GoogleMap.InfoWindowAdapter {

    private View mContent;
    private List<Event> mDataset;

    public MapEventMarkerInfoAdapter(LayoutInflater layoutInflater, ArrayList<Event> eventList) {
        this.mContent = layoutInflater.inflate(R.layout.event_marker, null);
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
        ImageView imageViewEventSport = (ImageView) view.findViewById(R.id.event_marker_sport);
        imageViewEventSport.setVisibility(View.VISIBLE);
        imageViewEventSport.setImageResource(Utiles.getSportIconFromResource(event.getSport_id()));

        TextView textViewEventName = (TextView) view.findViewById(R.id.event_marker_name);
        textViewEventName.setText(event.getName());

        TextView textViewEventAddress = (TextView) view.findViewById(R.id.event_marker_address);
        textViewEventAddress.setText(event.getAddress());

        TextView textViewEventDate = (TextView) view.findViewById(R.id.events_marker_date);
        textViewEventDate.setText(UtilesTime.millisToDateTimeString(event.getDate()));
    }
}