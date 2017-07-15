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

        /* Is there an error? */
        if (json.has(STATUS_CODE)) {
            String statusCode = json.getString(STATUS_CODE);

            // https://developers.google.com/maps/documentation/geocoding/intro?hl=es-419#reverse-response
            switch (statusCode) {
                case "OK":
                    break;
                case "ZERO_RESULTS":
                    /* Maybe latlng in a remote location */
                    return null;
                case "OVER_QUERY_LIMIT":
                    /* Over your quota. */
                    return null;
                case "REQUEST_DENIED":
                    /* API key invalid */
                    return null;
                case "INVALID_REQUEST":
                    /* Missing latlng or error in result_type */
                    return null;
                case "UNKNOWN_ERROR":
                    /* Probably a bad connection */
                    return null;
                default:
                    return null;
            }
        }


        JSONArray jsonWeatherArray = json.getJSONArray(OWM_LIST);

        JSONObject cityJson = forecastJson.getJSONObject(OWM_CITY);

        JSONObject cityCoord = cityJson.getJSONObject(OWM_COORD);
        double cityLatitude = cityCoord.getDouble(OWM_LATITUDE);
        double cityLongitude = cityCoord.getDouble(OWM_LONGITUDE);

        JSONObject jsonFirstResult = json.getJSONArray("results").getJSONObject(0);
        ;

        String placeId = jsonFirstResult.getString("place_id");
        String address = jsonFirstResult.getString("formatted_address");
        String shortNameLocality;
        String longNameLocality;
        LatLng coordinates;

        return new MyPlace(placeId, address, shortNameLocality, longNameLocality, coordinates);
    }

}