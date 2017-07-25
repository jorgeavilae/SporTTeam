package com.usal.jorgeav.sportapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.usal.jorgeav.sportapp.MyApplication;
import com.usal.jorgeav.sportapp.R;
import com.usal.jorgeav.sportapp.data.Field;
import com.usal.jorgeav.sportapp.data.SportCourt;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jorge Avila on 21/07/2017.
 */

public class MapMarkerInfoAdapter implements GoogleMap.InfoWindowAdapter {

    private View mContent;
    private List<Field> mDataset;

    public MapMarkerInfoAdapter(LayoutInflater layoutInflater, ArrayList<Field> fieldList) {
        this.mContent = layoutInflater.inflate(R.layout.field_marker, null);
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
        TextView textViewFieldName = (TextView) view.findViewById(R.id.field_marker_name);
        textViewFieldName.setText(field.getName());

        TextView textViewFieldAddress = (TextView) view.findViewById(R.id.field_marker_address);
        textViewFieldAddress.setText(field.getAddress());

        TextView textViewSportMore = (TextView) view.findViewById(R.id.field_marker_sport_more);
        textViewSportMore.setVisibility(View.GONE);
        ImageView imageViewFieldSport1 = (ImageView) view.findViewById(R.id.field_marker_sport1);
        imageViewFieldSport1.setVisibility(View.GONE);
        ImageView imageViewFieldSport2 = (ImageView) view.findViewById(R.id.field_marker_sport2);
        imageViewFieldSport1.setVisibility(View.GONE);

        if (field.getSport() != null && field.getSport().size() > 0) {
            ArrayList<SportCourt> sports = new ArrayList<>(field.getSport().values());

            int sport1Icon = MyApplication.getAppContext().getResources().getIdentifier(
                    sports.get(0).getSport_id() , "drawable", MyApplication.getAppContext().getPackageName());
            imageViewFieldSport1.setImageResource(sport1Icon);
            imageViewFieldSport1.setVisibility(View.VISIBLE);

            if (sports.size() > 1) {
                int sport2Icon = MyApplication.getAppContext().getResources().getIdentifier(
                        sports.get(1).getSport_id() , "drawable", MyApplication.getAppContext().getPackageName());
                imageViewFieldSport2.setImageResource(sport2Icon);
                imageViewFieldSport2.setVisibility(View.VISIBLE);
            }

            if (sports.size() > 2) textViewSportMore.setVisibility(View.VISIBLE);
        }

    }
}
