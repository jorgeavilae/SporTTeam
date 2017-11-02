package com.usal.jorgeav.sportapp.preferences;

import android.app.ProgressDialog;
import android.content.Context;
import android.preference.EditTextPreference;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.usal.jorgeav.sportapp.adapters.PlaceAutocompleteAdapter;

public class CityAutocompleteEditTextPreference extends EditTextPreference {
    private final static String TAG = CityAutocompleteEditTextPreference.class.getSimpleName();

    private AutoCompleteTextView mEditText = null;
    String citySelectedName = null;
    LatLng citySelectedCoord = null;
    // Static prevent double initialization with same ID
    private static GoogleApiClient mGoogleApiClient;

    public CityAutocompleteEditTextPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        mEditText = new AutoCompleteTextView(context, attrs);
        mEditText.setThreshold(0);
        setAutocompleteTextView();
    }

    private void setAutocompleteTextView() {
        if (mGoogleApiClient == null)
            mGoogleApiClient = new GoogleApiClient
                        .Builder(getContext())
                        .addApi(Places.GEO_DATA_API)
                        .enableAutoManage(((SettingsActivity)getContext()), new GoogleApiClient.OnConnectionFailedListener() {
                            @Override
                            public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                                Log.e(TAG, "onConnectionFailed: Google Api Client is not connected");
                            }
                        })
                        .build();
        else mGoogleApiClient.connect();

        // Set up the adapter that will retrieve suggestions from
        // the Places Geo Data API that cover Spain
        AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                .setTypeFilter(AutocompleteFilter.TYPE_FILTER_CITIES)
                /*https://developers.google.com/android/reference/com/google/android/gms/location/places/AutocompleteFilter.Builder.html#setCountry(java.lang.String)*/
                .setCountry("ES"/*https://en.wikipedia.org/wiki/ISO_3166-1_alpha-2#ES*/)
                .build();

        final PlaceAutocompleteAdapter adapter = new PlaceAutocompleteAdapter(getContext(), mGoogleApiClient, null, typeFilter);
        mEditText.setAdapter(adapter);

        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                citySelectedName = null;
                citySelectedCoord = null;
            }
        });

        mEditText.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                /*
                 Retrieve the place ID of the selected item from the Adapter.
                 The adapter stores each Place suggestion in a AutocompletePrediction from which we
                 read the place ID and title.
                  */
                AutocompletePrediction item = adapter.getItem(position);
                if (item != null) {
                    Log.i(TAG, "Autocomplete item selected: " + item.getPlaceId());
                    final ProgressDialog progressDialog = new ProgressDialog(getContext());
                    progressDialog.show();
                    Places.GeoDataApi.getPlaceById(mGoogleApiClient, item.getPlaceId())
                            .setResultCallback(new ResultCallback<PlaceBuffer>() {
                                @Override
                                public void onResult(@NonNull PlaceBuffer places) {
                                    // Stop UI until finish callback
                                    progressDialog.dismiss();
                                    if (places.getStatus().isSuccess() && places.getCount() > 0) {
                                        Place myPlace = places.get(0);
                                        citySelectedName = myPlace.getName().toString();
                                        citySelectedCoord = myPlace.getLatLng();
                                        Log.i(TAG, "Place found: Name - " + myPlace.getName()
                                                + " LatLng - " + myPlace.getLatLng());
                                    } else {
                                        Log.e(TAG, "Place not found");
                                    }
                                    places.release();
                                }
                            });
                }
            }
        });
    }

    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);
        EditText oldEditText = (EditText) view.findViewById(android.R.id.edit);
        ViewParent oldParent = oldEditText.getParent();
        String currentValue = oldEditText.getText().toString();

        // remove it from the existing layout hierarchy
        if (oldParent != view) {
            if (oldParent != null)
                ((ViewGroup) oldParent).removeView(oldEditText);
            if (mEditText.getParent() != null)
                ((ViewGroup) mEditText.getParent()).removeView(mEditText);
            mEditText.setText(currentValue);
            mEditText.setId(android.R.id.edit);
            onAddEditTextToDialogView(view, mEditText);
        }
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        if (positiveResult) {
            String value = mEditText.getText().toString();
            if (callChangeListener(value)) {
                setText(value);
            }
        }
    }
}
