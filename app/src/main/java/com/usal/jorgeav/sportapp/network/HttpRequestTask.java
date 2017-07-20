package com.usal.jorgeav.sportapp.network;

import com.google.android.gms.maps.model.LatLng;
import com.usal.jorgeav.sportapp.data.MyPlace;
import com.usal.jorgeav.sportapp.utils.UtilesNetwork;

import java.net.URL;

/**
 * Created by Jorge Avila on 15/07/2017.
 */

public class HttpRequestTask {

    /* Performs the network request for Google Places near by a given LatLng Object
     * and parses the JSON from that request
     */
    synchronized public static MyPlace placeInLatLngLocation(String apiKey, LatLng point) {

        try {
            /* The getUrl method will return the URL that we need to get the Places in JSON. */
            URL requestUrl = UtilesNetwork.getUrl(apiKey, point.latitude, point.longitude);

            /* Use the URL to retrieve the JSON */
            String jsonResponse = UtilesNetwork.getResponseFromHttpUrl(requestUrl);

            /* Parse the JSON into a MyPlaces object */
            return UtilesNetwork
                    .getMyPlaceFromJson(jsonResponse);

        } catch (Exception e) {
            /* Server probably invalid */
            e.printStackTrace();
            return null;
        }
    }
}
