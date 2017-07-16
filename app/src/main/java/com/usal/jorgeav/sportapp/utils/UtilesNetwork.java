package com.usal.jorgeav.sportapp.utils;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.usal.jorgeav.sportapp.R;
import com.usal.jorgeav.sportapp.data.MyPlace;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by Jorge Avila on 15/07/2017.
 */

// https://developers.google.com/maps/documentation/geocoding/intro?hl=es-419#ReverseGeocoding
public class UtilesNetwork {
    public static final String TAG = UtilesNetwork.class.getSimpleName();

    // https://maps.googleapis.com/maps/api/geocode/
    private static final String BASE_URL = "https://maps.googleapis.com/maps/api/geocode/";
    private static final String RESPONSE_TYPE = "json";

    // latlng 0.0,0.0
    private static final String LATLNG_PARAM = "latlng";

    // result_type street_address https://developers.google.com/maps/documentation/geocoding/intro?hl=es-419#Types
    private static final String RESULT_TYPE_PARAM = "result_type";
    private static final String RESULT_TYPE = "street_address";

    // key asdasdasd6asd5as54d55a
    private static final String KEY_PARAM = "key";

    // Status field from Json response
    private static final String STATUS_CODE = "status";

    public static URL getUrl(Context context,Double latitude, Double longitude) {
        String apiKey = context.getResources().getString(R.string.google_maps_key);
        Uri queryUri = Uri.parse(BASE_URL).buildUpon()
                .appendPath(RESPONSE_TYPE)
                .appendQueryParameter(LATLNG_PARAM, latitude+","+longitude)
                .appendQueryParameter(RESULT_TYPE_PARAM, RESULT_TYPE)
                .appendQueryParameter(KEY_PARAM, apiKey)
                .build();

        try {
            URL queryUrl = new URL(queryUri.toString());
            Log.v(TAG, "URL: " + queryUrl);
            Log.d(TAG, "getUrl: latlng "+latitude+" ~ "+longitude);
            return queryUrl;
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            String response = null;
            if (hasInput) {
                response = scanner.next();
            }
            scanner.close();
            return response;
        } finally {
            urlConnection.disconnect();
        }
    }

    public static MyPlace getMyPlaceFromJson(Context context, String jsonStr)
            throws JSONException {

        JSONObject json = new JSONObject(jsonStr);
        Log.d(TAG, "getMyPlaceFromJson: "+json);

        /* Is there an error? */
        if (!json.has(STATUS_CODE)) return new MyPlace("UNKNOWN_ERROR");
        String statusCode = json.getString(STATUS_CODE);

        // https://developers.google.com/maps/documentation/geocoding/intro?hl=es-419#reverse-response
        switch (statusCode) {
            case "OK":
                break;
            case "ZERO_RESULTS":
                /* Maybe latlng in a remote location */
                Log.e(TAG, "getMyPlaceFromJson: Maybe latlng in a remote location "+statusCode);
                return new MyPlace("ZERO_RESULTS");
            case "OVER_QUERY_LIMIT":
                /* Over your quota. */
                Log.e(TAG, "getMyPlaceFromJson: Over quota "+statusCode);
                return new MyPlace("OVER_QUERY_LIMIT");
            case "REQUEST_DENIED":
                /* API key invalid */
                Log.e(TAG, "getMyPlaceFromJson: API key invalid "+statusCode);
                return new MyPlace("REQUEST_DENIED");
            case "INVALID_REQUEST":
                /* Missing latlng or error in result_type */
                Log.e(TAG, "getMyPlaceFromJson: Missing latlng or error in result_type "+statusCode);
                return new MyPlace("INVALID_REQUEST");
            case "UNKNOWN_ERROR":
                /* Probably a bad connection */
                Log.e(TAG, "getMyPlaceFromJson: Probably a bad connection "+statusCode);
                return new MyPlace("UNKNOWN_ERROR");
            default:
                return new MyPlace("UNKNOWN_ERROR");
        }

        JSONObject jsonFirstResult = json.getJSONArray("results").getJSONObject(0);

        String placeId = jsonFirstResult.getString("place_id");
        String address = jsonFirstResult.getString("formatted_address");
        String shortNameLocality = "";
        String longNameLocality = "";
        JSONArray jsonArrayAddresses = jsonFirstResult.getJSONArray("address_components");
        for (int i = 0; i < jsonArrayAddresses.length(); i++) {
            JSONObject addressComponent = jsonArrayAddresses.getJSONObject(i);
            JSONArray addressTypes = addressComponent.getJSONArray("types");
            for (int j = 0; j < addressTypes.length(); j++) {
                if (addressTypes.getString(j).equals("locality")) {
                    shortNameLocality = addressComponent.getString("short_name");
                    longNameLocality = addressComponent.getString("long_name");
                }
            }
        }

        double lat = jsonFirstResult.getJSONObject("geometry").getJSONObject("location").getDouble("lat");
        double lng = jsonFirstResult.getJSONObject("geometry").getJSONObject("location").getDouble("lng");
        LatLng coordinates = new LatLng(lat, lng);

//        "geometry" : {
//            "location" : {
//                "lat" : 37.4224764,
//                        "lng" : -122.0842499
//            },
//            "location_type" : "ROOFTOP",
//                    "viewport" : { // TODO: 16/07/2017 añadir viewport para centrar el mapa
//                "northeast" : {
//                    "lat" : 37.4238253802915,
//                            "lng" : -122.0829009197085
//                },
//                "southwest" : {
//                    "lat" : 37.4211274197085,
//                            "lng" : -122.0855988802915
//                }
//            }
//        },

        return new MyPlace(statusCode, placeId, address, shortNameLocality, longNameLocality, coordinates);
    }

}