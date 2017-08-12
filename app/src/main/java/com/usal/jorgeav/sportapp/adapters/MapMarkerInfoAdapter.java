package com.usal.jorgeav.sportapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.RequestManager;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.usal.jorgeav.sportapp.R;
import com.usal.jorgeav.sportapp.data.Field;
import com.usal.jorgeav.sportapp.data.SportCourt;
import com.usal.jorgeav.sportapp.utils.Utiles;

import java.util.ArrayList;
import java.util.List;

public class MapMarkerInfoAdapter implements GoogleMap.InfoWindowAdapter {

    private View mContent;
    private List<Field> mDataset;
    private RequestManager mGlide;

    public MapMarkerInfoAdapter(LayoutInflater layoutInflater, ArrayList<Field> fieldList, RequestManager glide) {
        this.mContent = layoutInflater.inflate(R.layout.field_marker, null);
        this.mDataset = fieldList;
        this.mGlide = glide;
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
        imageViewFieldSport2.setVisibility(View.GONE);
        ImageView imageViewFieldSport3 = (ImageView) view.findViewById(R.id.field_marker_sport3);
        imageViewFieldSport3.setVisibility(View.GONE);

        // Set first sport icon
        if (field.getSport() != null && field.getSport().size() > 0) {
            ArrayList<SportCourt> sports = new ArrayList<>(field.getSport().values());

            imageViewFieldSport1.setVisibility(View.VISIBLE);
            mGlide.load(Utiles.getSportIconFromResource(sports.get(0).getSport_id())).into(imageViewFieldSport1);

            // Set second sport icon
            if (sports.size() > 1) {
                imageViewFieldSport2.setVisibility(View.VISIBLE);
                mGlide.load(Utiles.getSportIconFromResource(sports.get(1).getSport_id())).into(imageViewFieldSport2);

                // Set third sport icon
                if (sports.size() > 2) {
                    imageViewFieldSport3.setVisibility(View.VISIBLE);
                    mGlide.load(Utiles.getSportIconFromResource(sports.get(2).getSport_id())).into(imageViewFieldSport3);

                    // Set dots if there are more than 3
                    if (sports.size() > 3) textViewSportMore.setVisibility(View.VISIBLE);
                }
            }
        }
    }
}
