package com.usal.jorgeav.sportapp.network;

import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.usal.jorgeav.sportapp.MyApplication;
import com.usal.jorgeav.sportapp.data.MyPlace;
import com.usal.jorgeav.sportapp.utils.UtilesPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Scanner;

// https://developers.google.com/maps/documentation/geocoding/intro?hl=es-419#ReverseGeocoding
public class GeocodingTask {
    public static final String TAG = GeocodingTask.class.getSimpleName();

    // https://maps.googleapis.com/maps/api/geocode/
    private static final String BASE_URL = "https://maps.googleapis.com/maps/api/geocode/";
    private static final String RESPONSE_TYPE = "json";
    private static final String PARAM_LATLNG = "latlng";
    private static final String PARAM_KEY = "key";
    /* https://developers.google.com/maps/documentation/geocoding/intro?hl=es-419#Types */
    private static final String PARAM_RESULT_TYPE = "result_type";
    private static final String RESULT_TYPE = "street_address"+"|"+"route"+"|"+"intersection";

    // Performs the network request for Google Places near by a given LatLng Object
    // and parses the JSON from that request
    synchronized public static MyPlace getMyPlaceObjectFromLatLngLocation(String apiKey, LatLng point) {
        try {
            /* The getUrl method will return the URL that we need to get the Places in JSON. */
            URL requestUrl = buildGeocodingUrl(apiKey, point.latitude, point.longitude);
            if (requestUrl == null) return null;

            /* Use the URL to retrieve the JSON */
            String jsonResponse = getResponseFromHttpUrl(requestUrl);

            /* Parse the JSON into a MyPlaces object */
            return jsonStringToMyPlace(jsonResponse);

        } catch (Exception e) {
            /* Server probably invalid */
            e.printStackTrace();
            return null;
        }
    }

    private static URL buildGeocodingUrl(String apiKey, Double latitude, Double longitude) {
        Uri queryUri = Uri.parse(BASE_URL).buildUpon()
                .appendPath(RESPONSE_TYPE)
                .appendQueryParameter(PARAM_LATLNG, latitude+","+longitude)
                .appendQueryParameter(PARAM_RESULT_TYPE, RESULT_TYPE)
                .appendQueryParameter(PARAM_KEY, apiKey)
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

    private static String getResponseFromHttpUrl(URL url) throws IOException {
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
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return null;
        } finally {
            urlConnection.disconnect();
        }
    }

    private static MyPlace jsonStringToMyPlace(String jsonStr)
            throws JSONException {

        JSONObject json = new JSONObject(jsonStr);

        /* Is there an error? */
        if (!json.has("status")) return new MyPlace("UNKNOWN_ERROR");
        String statusCode = json.getString("status");

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
        String city = "";
        JSONArray jsonArrayAddresses = jsonFirstResult.getJSONArray("address_components");
        for (int i = 0; i < jsonArrayAddresses.length(); i++) {
            JSONObject addressComponent = jsonArrayAddresses.getJSONObject(i);
            JSONArray addressTypes = addressComponent.getJSONArray("types");
            for (int j = 0; j < addressTypes.length(); j++) {
                if (addressTypes.getString(j).equals("locality")) //Ciudad - Pueblo
                    shortNameLocality = addressComponent.getString("long_name");
                if (addressTypes.getString(j).equals("administrative_area_level_2")) //Provincia
                    city = addressComponent.getString("long_name");
            }
        }
        if (TextUtils.isEmpty(city)) city = shortNameLocality;

        // Check if address belongs to current user's city
        String currentUserCity = UtilesPreferences.getCurrentUserCity(MyApplication.getAppContext());
        if (!TextUtils.equals(shortNameLocality, currentUserCity)
                && !TextUtils.equals(city, currentUserCity)) {
            Log.e(TAG, "getMyPlaceFromJson: Point out of current city ("+currentUserCity+") bounds."
                    + " Point in " + shortNameLocality + ", " + city);
            return new MyPlace("OUT_OF_BOUNDS");
        }

        double lat = jsonFirstResult.getJSONObject("geometry").getJSONObject("location").getDouble("lat");
        double lng = jsonFirstResult.getJSONObject("geometry").getJSONObject("location").getDouble("lng");
        LatLng coordinates = new LatLng(lat, lng);

        double viewPortNortheastLat = jsonFirstResult.getJSONObject("geometry").getJSONObject("viewport")
                .getJSONObject("northeast").getDouble("lat");
        double viewPortNortheastLng = jsonFirstResult.getJSONObject("geometry").getJSONObject("viewport")
                .getJSONObject("northeast").getDouble("lng");
        LatLng viewPortNortheast = new LatLng(viewPortNortheastLat, viewPortNortheastLng);

        double viewPortSouthwestLat = jsonFirstResult.getJSONObject("geometry").getJSONObject("viewport")
                .getJSONObject("southwest").getDouble("lat");
        double viewPortSouthwestLng = jsonFirstResult.getJSONObject("geometry").getJSONObject("viewport")
                .getJSONObject("southwest").getDouble("lng");
        LatLng viewPortSouthwest = new LatLng(viewPortSouthwestLat, viewPortSouthwestLng);

//        "geometry" : {
//            "location" : {
//                "lat" : 37.4224764,
//                "lng" : -122.0842499
//            },
//            "location_type" : "ROOFTOP",
//            "viewport" : {
//                "northeast" : {
//                    "lat" : 37.4238253802915,
//                    "lng" : -122.0829009197085
//                },
//                "southwest" : {
//                    "lat" : 37.4211274197085,
//                    "lng" : -122.0855988802915
//                }
//            }
//        },

        return new MyPlace(statusCode, placeId, address, shortNameLocality,
                city, coordinates, viewPortNortheast, viewPortSouthwest);
    }

}
