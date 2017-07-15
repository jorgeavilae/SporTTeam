package com.usal.jorgeav.sportapp.data;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Jorge Avila on 15/07/2017.
 */

public class MyPlace {
    String placeId;
    String address;
    String shortNameLocality;
    String longNameLocality;
    LatLng coordinates;

    public MyPlace(String placeId, String address, String shortNameLocality, String longNameLocality, LatLng coordinates) {
        this.placeId = placeId;
        this.address = address;
        this.shortNameLocality = shortNameLocality;
        this.longNameLocality = longNameLocality;
        this.coordinates = coordinates;
    }

    public String getPlaceId() {
        return placeId;
    }

    public String getAddress() {
        return address;
    }

    public String getShortNameLocality() {
        return shortNameLocality;
    }

    public String getLongNameLocality() {
        return longNameLocality;
    }

    public LatLng getCoordinates() {
        return coordinates;
    }

    @Override
    public String toString() {
        return "MyPlace{" +
                "placeId='" + placeId + '\'' +
                ", address='" + address + '\'' +
                ", shortNameLocality='" + shortNameLocality + '\'' +
                ", longNameLocality='" + longNameLocality + '\'' +
                ", coordinates=" + coordinates +
                '}';
    }
}
