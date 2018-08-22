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

/**
 * Adaptador para la coleccion de marcas que se posicionan sobre el mapa representando
 * partidos.
 * <p>
 *
 * Mas informacion en
 * {@link
 * <a href= "https://developers.google.com/android/reference/com/google/android/gms/maps/GoogleMap.InfoWindowAdapter">
 *     GoogleMap.InfoWindowAdapter
 * </a>}
 */
public class MapEventMarkerInfoAdapter implements GoogleMap.InfoWindowAdapter {

    /**
     * Representa el cuadro que se abre al pulsar sobre cada marca
     */
    private View mContent;
    /**
     * Almacena la coleccion de partidos que maneja este adapter
     */
    private List<Event> mDataset;

    /**
     *
     * @param layoutInflater Necesario para crear la View del cuadro de cada marca
     * @param eventList Conjunto de partidos
     */
    public MapEventMarkerInfoAdapter(LayoutInflater layoutInflater, ArrayList<Event> eventList) {
        this.mContent = layoutInflater.inflate(R.layout.event_marker, null);
        this.mDataset = eventList;
    }

    /**
     * Mas informacion en
     * {@link
     * <a href= "https://developers.google.com/android/reference/com/google/android/gms/maps/GoogleMap.InfoWindowAdapter.html#getInfoWindow(com.google.android.gms.maps.model.Marker)">
     *     GoogleMap.InfoWindowAdapter.getInfoWindow(Marker)
     * </a>}
     */
    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    /**
     * Obtiene el partido correspondiente a la marca seleccionada y lo emplaza en la View.
     * <p>
     *
     * Mas informacion en
     * {@link
     * <a href= "https://developers.google.com/android/reference/com/google/android/gms/maps/GoogleMap.InfoWindowAdapter.html#getInfoContents(com.google.android.gms.maps.model.Marker)">
     *     GoogleMap.InfoWindowAdapter.getInfoContents(Marker)
     * </a>}
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
     * Emplaza los atributos de cada partido dentro de la View
     * @param view View del cuadro que aparece sobre cada marca
     * @param event Partido del que extraer los datos
     */
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