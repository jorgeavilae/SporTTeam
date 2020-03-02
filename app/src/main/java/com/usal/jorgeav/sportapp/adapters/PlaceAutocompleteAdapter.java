/*
 * Copyright (C) 2015 Google Inc. All Rights Reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.usal.jorgeav.sportapp.adapters;

import android.content.Context;
import android.graphics.Typeface;
import androidx.annotation.NonNull;
import android.text.style.CharacterStyle;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.usal.jorgeav.sportapp.R;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Adaptador para la lista de sugerencias de ciudades. Utiliza la interfaz {@link Filterable}
 * para filtrar las búsquedas. Para las búsquedas utiliza Google Places API
 *
 * @see <a href= "https://developers.google.com/places/android-sdk/intro">
 * Places SDK for Android
 * </a>
 */
public class PlaceAutocompleteAdapter extends ArrayAdapter<AutocompletePrediction> implements
        Filterable {
    /**
     * Nombre de la clase
     */
    private static final String TAG = PlaceAutocompleteAdapter.class.getSimpleName();
    /**
     * Estilo de texto para la celda
     */
    private static final CharacterStyle STYLE_BOLD = new StyleSpan(Typeface.BOLD);

    /**
     * Colección de resultados una vez efectuada la búsqueda
     */
    private ArrayList<AutocompletePrediction> mResultList;

    /**
     * Objeto PlacesClient necesario para utilizar Google Places API
     */
    private PlacesClient mPlacesClient;

    /**
     * Limites en coordenadas sobre los que efectuar la búsqueda de lugares
     */
    private RectangularBounds mBounds;

    /**
     * Token para identificar una única sesión de Autocomplete API
     */
    private AutocompleteSessionToken mToken;

    /**
     * Constructor
     *
     * @param context         Contexto de la Actividad que aloja el adaptador
     * @param placesClient    necesario para acceder a las funciones de la API
     * @param bounds          limites sobre los que efectuar la búsqueda
     */
    public PlaceAutocompleteAdapter(Context context, PlacesClient placesClient, LatLngBounds bounds) {
        super(context, android.R.layout.simple_expandable_list_item_2, android.R.id.text1);
        this.mPlacesClient = placesClient;
        if (bounds == null) this.mBounds = null;
        else this.mBounds = RectangularBounds.newInstance(bounds.northeast, bounds.southwest);

        mToken = AutocompleteSessionToken.newInstance();
    }

    /**
     * @return el token de la sesión de Autocomplete API
     */
    public AutocompleteSessionToken getToken() {
        return mToken;
    }

    /**
     * @return el tamaño de la lista de ciudades
     */
    @Override
    public int getCount() {
        return mResultList.size();
    }

    /**
     * @param position posición dentro de la lista
     * @return el elemento situado en esa posición
     */
    @Override
    public AutocompletePrediction getItem(int position) {
        return mResultList.get(position);
    }

    /**
     * Rellena los elementos de la celda con los datos de la ciudad de la posición indicada
     *
     * @param position    posición de la celda
     * @param convertView vista de la celda
     * @param parent      vista del padre donde se aloja la celda
     * @return la celda con los datos emplazados
     */
    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View row = super.getView(position, convertView, parent);
        AutocompletePrediction item = getItem(position);

        if (item != null) {
            TextView textView1 = row.findViewById(android.R.id.text1);
            TextView textView2 = row.findViewById(android.R.id.text2);
            textView1.setText(item.getPrimaryText(STYLE_BOLD));
            textView2.setText(item.getSecondaryText(STYLE_BOLD));
        }

        return row;
    }

    /**
     * Crea el {@link Filter} que realiza las búsquedas. Para el {@link Filter} se implementan tres
     * métodos:
     * <p>
     * - Filter#performFiltering(CharSequence):
     * Invocado para realizar el filtrado. Dada la cadena de texto con la que se realiza la
     * búsqueda, utiliza FilterResults clase interna de {@link Filter} para almacenar los resultados
     * de la consulta a Google proporcionados por
     * PlaceAutocompleteAdapter#getAutocomplete(CharSequence) y los devuelve.
     * <p>
     * - Filter#publishResults(CharSequence, Filter.FilterResults):
     * Invocado cuando finaliza el filtrado. Dada la cadena de texto con la que se realiza la
     * búsqueda y los resultado, se encarga de notificar el éxito o el fracaso (si los resultados
     * están vacíos) de la búsqueda. Almacena el resultado en {@link #mResultList}.
     * <p>
     * - {@link Filter#convertResultToString(Object)}:
     * Invocado para mostrar un texto legible que represente cada uno de los elementos del resultado.
     *
     * @return el filtro creado
     */
    @NonNull
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();

                // We need a separate list to store the results, since
                // this run asynchronously.
                ArrayList<AutocompletePrediction> filterData = new ArrayList<>();

                // Skip the autocomplete query if no constraints are given.
                if (constraint != null) {
                    // Query the autocomplete API for the (constraint) search string.
                    filterData.addAll(getAutocomplete(constraint.toString()));
                }

                results.values = filterData;
                results.count = filterData.size();

                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && results.count > 0) {
                    // The API returned at least one result, update the data.
                    //noinspection unchecked
                    mResultList = (ArrayList<AutocompletePrediction>) results.values;
                    notifyDataSetChanged();
                } else {
                    // The API did not return any results, invalidate the data set.
                    notifyDataSetInvalidated();
                }
            }

            @Override
            public CharSequence convertResultToString(Object resultValue) {
                // Override this method to display a readable result in the AutocompleteTextView
                // when clicked.
                if (resultValue instanceof AutocompletePrediction) {
                    return ((AutocompletePrediction) resultValue).getFullText(null);
                } else {
                    return super.convertResultToString(resultValue);
                }
            }
        };
    }

    /**
     * Método encargado de realizar la consulta asíncrona a Google Places API con la cadena de
     * texto introducida por el usuario
     * <p>
     * Utiliza el método PlacesClient.findAutocompletePredictions() para hacer las consultas.
     * <p>
     * A continuación, espera por el resultado y comprueba el código de error.
     * <p>
     * Si el código es correcto devuelve una lista con los resultados que coinciden con la
     * búsqueda
     *
     * @param constraint cadena de texto con la que se realiza la búsqueda
     * @return List con los resultados de la búsqueda
     */
    private List<AutocompletePrediction> getAutocomplete(String constraint) {
        // Use the builder to create a FindAutocompletePredictionsRequest.
        FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder()
                .setLocationRestriction(mBounds)
                /*https://developers.google.com/android/reference/com/google/android/gms/location/places/AutocompleteFilter.Builder.html#setCountry(java.lang.String)*/
                .setCountry("ES"/*https://en.wikipedia.org/wiki/ISO_3166-1_alpha-2#ES*/)
                .setTypeFilter(TypeFilter.CITIES)
                .setSessionToken(mToken)
                .setQuery(constraint)
                .build();

        Task<FindAutocompletePredictionsResponse> autocompletePredictions =
                mPlacesClient.findAutocompletePredictions(request);
        try {
            Tasks.await(autocompletePredictions, 15, TimeUnit.SECONDS);
        } catch (ExecutionException | InterruptedException | TimeoutException e) {
            e.printStackTrace();
        }

        if (!autocompletePredictions.isSuccessful()) {
            Toast.makeText(getContext(), R.string.toast_error_autocomplete_city, Toast.LENGTH_SHORT).show();
            if (autocompletePredictions.getException() instanceof ApiException) {
                ApiException apiException = (ApiException) autocompletePredictions.getException();
                Log.e(TAG, "Place not found: " + apiException.getMessage());
            }
        } else {
            FindAutocompletePredictionsResponse response = autocompletePredictions.getResult();
            if (response != null) {
                for (AutocompletePrediction prediction : response.getAutocompletePredictions()) {
                    Log.i(TAG, prediction.getPlaceId());
                    Log.i(TAG, prediction.getPrimaryText(null).toString());
                }
                return response.getAutocompletePredictions();
            }
        }
        return null;
    }
}
