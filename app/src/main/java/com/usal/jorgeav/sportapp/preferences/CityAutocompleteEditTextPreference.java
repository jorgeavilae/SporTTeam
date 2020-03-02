package com.usal.jorgeav.sportapp.preferences;

import android.app.ProgressDialog;
import android.content.Context;
import android.preference.EditTextPreference;
import androidx.annotation.NonNull;
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
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FetchPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.usal.jorgeav.sportapp.MyApplication;
import com.usal.jorgeav.sportapp.R;
import com.usal.jorgeav.sportapp.adapters.PlaceAutocompleteAdapter;

import java.util.Arrays;
import java.util.List;

/**
 * Clase derivada de {@link EditTextPreference} con el objetivo de usarla como tal pero añadiendo
 * la funcionalidad proporcionada por el AutoCompletado de sitios de Google GeoDataApi. Aplicando
 * un {@link PlaceAutocompleteAdapter} a este {@link EditTextPreference}, se construye un EditText
 * para la pantalla de preferencias que sugiera las ciudades que puede elegir el usuario
 *
 * @see <a href= "https://developers.google.com/android/reference/com/google/android/gms/location/places/GeoDataApi">
 * Google Places API</a>
 */
public class CityAutocompleteEditTextPreference extends EditTextPreference {
    /**
     * Nombre de la clase
     */
    private final static String TAG = CityAutocompleteEditTextPreference.class.getSimpleName();

    /**
     * Referencia al elemento de la interfaz donde se va a escribir el texto que servirá para
     * buscar las ciudades.
     */
    private AutoCompleteTextView mEditText;
    /**
     * Nombre de la ciudad escogida
     */
    String citySelectedName = null;
    /**
     * Coordenadas de la ciudad escogida
     */
    LatLng citySelectedCoord = null;

    /**
     * Objeto PlacesClient necesario para utilizar Google Places API
     */
    private static PlacesClient mPlacesClient;

    /**
     * Constructor
     *
     * @param context contexto de la Actividad
     * @param attrs   atributos necesarios para {@link #mEditText}
     * @see #setAutocompleteTextView()
     */
    public CityAutocompleteEditTextPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        mEditText = new AutoCompleteTextView(context, attrs);
        mEditText.setThreshold(0);
        setAutocompleteTextView();
    }

    /**
     * Inicializa el cliente PlacesClient para poder utilizar Google Places API. Establece los
     * controles para regular el comportamiento del {@link #mEditText} donde se
     * escribe la ciudad. Crea un {@link TextWatcher} para reaccionar a los cambios en el texto y
     * así realizar nuevas búsquedas con el {@link PlaceAutocompleteAdapter}.
     * Cuando se selecciona una de las ciudades sugeridas, se realiza una consulta a la Google
     * Places API para obtener la coordenadas de dicha ciudad.
     *
     * @see <a href= "https://developers.google.com/places/android-sdk/intro">
     * Places SDK for Android</a>
     */
    private void setAutocompleteTextView() {
        Context context = MyApplication.getAppContext();
        Places.initialize(context, context.getString(R.string.google_maps_and_geocoding_key));
        mPlacesClient = Places.createClient(context);

        final PlaceAutocompleteAdapter adapter = new PlaceAutocompleteAdapter(getContext(), mPlacesClient, null);
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

                    // Fetch Place Details from Places API
                    String placeId = item.getPlaceId();
                    List<Place.Field> placeFields = Arrays.asList(Place.Field.NAME, Place.Field.LAT_LNG);
                    FetchPlaceRequest request = FetchPlaceRequest.builder(placeId, placeFields)
                            .setSessionToken(adapter.getToken()).build();

                    mPlacesClient.fetchPlace(request).addOnSuccessListener(
                            new OnSuccessListener<FetchPlaceResponse>() {
                                @Override
                                public void onSuccess(FetchPlaceResponse response) {
                                    // Stop UI until finish callback
                                    progressDialog.dismiss();

                                    Place myPlace = response.getPlace();
                                    citySelectedName = myPlace.getName();
                                    citySelectedCoord = myPlace.getLatLng();
                                    Log.i(TAG, "Place found: Name - " + myPlace.getName()
                                            + " LatLng - " + myPlace.getLatLng());
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    // Stop UI until finish callback
                                    progressDialog.dismiss();

                                    Toast.makeText(getContext(),
                                            R.string.error_check_conn, Toast.LENGTH_SHORT).show();

                                    if (exception instanceof ApiException) {
                                        ApiException apiException = (ApiException) exception;
                                        Log.e(TAG, "Place not found: " + apiException.getMessage());
                                    }
                                }
                    });
                }
            }
        });
    }

    /**
     * Vincula la interfaz del cuadro de diálogo en el que aparece {@link #mEditText} a los datos
     * que debe mostrar este elemento.
     *
     * @param view interfaz del cuadro de diálogo
     */
    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);
        EditText oldEditText = view.findViewById(android.R.id.edit);
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

    /**
     * Invocado al cerrar el cuadro de diálogo, aplica los cambios sobre el valor de esta
     * preferencia.
     *
     * @param positiveResult true si se aceptan los cambios, false en caso contrario.
     */
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
