package com.usal.jorgeav.sportapp.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Jorge Avila on 15/07/2017.
 */

public class MyPlace implements Parcelable {
    String status;
    String placeId;
    String address;
    String shortNameLocality;
    String longNameLocality;
    LatLng coordinates;

    public MyPlace(String status, String placeId, String address, String shortNameLocality, String longNameLocality, LatLng coordinates) {
        this.status = status;
        this.placeId = placeId;
        this.address = address;
        this.shortNameLocality = shortNameLocality;
        this.longNameLocality = longNameLocality;
        this.coordinates = coordinates;
    }

    public MyPlace(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public boolean isSucceed(){
        return this.status.equals("OK");
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
                "status='" + status + '\'' +
                " placeId='" + placeId + '\'' +
                ", address='" + address + '\'' +
                ", shortNameLocality='" + shortNameLocality + '\'' +
                ", longNameLocality='" + longNameLocality + '\'' +
                ", coordinates=" + coordinates +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.placeId);
        dest.writeString(this.address);
        dest.writeString(this.shortNameLocality);
        dest.writeString(this.longNameLocality);
        dest.writeParcelable(this.coordinates, flags);
    }

    protected MyPlace(Parcel in) {
        this.placeId = in.readString();
        this.address = in.readString();
        this.shortNameLocality = in.readString();
        this.longNameLocality = in.readString();
        this.coordinates = in.readParcelable(LatLng.class.getClassLoader());
    }

    public static final Parcelable.Creator<MyPlace> CREATOR = new Parcelable.Creator<MyPlace>() {
        @Override
        public MyPlace createFromParcel(Parcel source) {
            return new MyPlace(source);
        }

        @Override
        public MyPlace[] newArray(int size) {
            return new MyPlace[size];
        }
    };
}
