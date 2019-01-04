package com.usal.jorgeav.sportapp.adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.usal.jorgeav.sportapp.R;
import com.usal.jorgeav.sportapp.data.Field;
import com.usal.jorgeav.sportapp.data.SportCourt;
import com.usal.jorgeav.sportapp.utils.Utiles;

import java.util.ArrayList;
import java.util.List;

/**
 * Adaptador para la colección de marcas que se posicionan sobre el mapa representando
 * instalaciones.
 *
 * @see <a href= "https://developers.google.com/android/reference/com/google/android/gms/maps/GoogleMap.InfoWindowAdapter">
 * GoogleMap.InfoWindowAdapter</a>
 */
public class MapFieldMarkerInfoAdapter implements GoogleMap.InfoWindowAdapter {

    /**
     * Representa el cuadro que se abre al pulsar sobre cada marca
     */
    private View mContent;
    /**
     * Almacena la colección de instalaciones que maneja este adapter
     */
    private List<Field> mDataset;

    /**
     * @param layoutInflater Necesario para crear la View del cuadro de cada marca
     * @param fieldList      Conjunto de instalaciones
     */
    @SuppressLint("InflateParams")
    public MapFieldMarkerInfoAdapter(LayoutInflater layoutInflater, ArrayList<Field> fieldList) {
        this.mContent = layoutInflater.inflate(R.layout.field_marker, null);
        this.mDataset = fieldList;
    }

    /**
     * @see <a href= "https://developers.google.com/android/reference/com/google/android/gms/maps/GoogleMap.InfoWindowAdapter.html#getInfoWindow(com.google.android.gms.maps.model.Marker)">
     * GoogleMap.InfoWindowAdapter.getInfoWindow(Marker)</a>
     */
    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    /**
     * Obtiene la instalación correspondiente a la marca seleccionada y la emplaza en la View.
     *
     * @see <a href= "https://developers.google.com/android/reference/com/google/android/gms/maps/GoogleMap.InfoWindowAdapter.html#getInfoContents(com.google.android.gms.maps.model.Marker)">
     * GoogleMap.InfoWindowAdapter.getInfoContents(Marker)</a>
     */
    @Override
    public View getInfoContents(Marker marker) {
        Integer position = (Integer) marker.getTag();
        if (position != null && position > -1 && mDataset != null && position < mDataset.size()) {
            populate(mContent, mDataset.get(position));
            return mContent;
        }
        return null;
    }

    /**
     * Emplaza los atributos de cada instalación dentro de la View
     *
     * @param view  View del cuadro que aparece sobre cada marca
     * @param field instalación de la que extraer los datos
     */
    private void populate(View view, Field field) {
        TextView textViewFieldName = view.findViewById(R.id.field_marker_name);
        textViewFieldName.setText(field.getName());

        TextView textViewFieldAddress = view.findViewById(R.id.field_marker_address);
        textViewFieldAddress.setText(field.getAddress());

        TextView textViewSportMore = view.findViewById(R.id.field_marker_sport_more);
        textViewSportMore.setVisibility(View.GONE);
        ImageView imageViewFieldSport1 = view.findViewById(R.id.field_marker_sport1);
        imageViewFieldSport1.setVisibility(View.GONE);
        ImageView imageViewFieldSport2 = view.findViewById(R.id.field_marker_sport2);
        imageViewFieldSport2.setVisibility(View.GONE);
        ImageView imageViewFieldSport3 = view.findViewById(R.id.field_marker_sport3);
        imageViewFieldSport3.setVisibility(View.GONE);

        // Set first sport icon
        if (field.getSport() != null && field.getSport().size() > 0) {
            ArrayList<SportCourt> sports = new ArrayList<>(field.getSport().values());

            imageViewFieldSport1.setVisibility(View.VISIBLE);
            imageViewFieldSport1.setImageResource(Utiles.getSportIconFromResource(sports.get(0).getSport_id()));

            // Set second sport icon
            if (sports.size() > 1) {
                imageViewFieldSport2.setVisibility(View.VISIBLE);
                imageViewFieldSport2.setImageResource(Utiles.getSportIconFromResource(sports.get(1).getSport_id()));

                // Set third sport icon
                if (sports.size() > 2) {
                    imageViewFieldSport3.setVisibility(View.VISIBLE);
                    imageViewFieldSport3.setImageResource(Utiles.getSportIconFromResource(sports.get(2).getSport_id()));

                    // Set dots if there are more than 3
                    if (sports.size() > 3) textViewSportMore.setVisibility(View.VISIBLE);
                }
            }
        }
    }
}
