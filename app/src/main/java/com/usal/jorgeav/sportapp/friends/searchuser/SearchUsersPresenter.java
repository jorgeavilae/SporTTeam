package com.usal.jorgeav.sportapp.friends.searchuser;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.text.TextUtils;

import com.usal.jorgeav.sportapp.data.provider.SportteamLoader;
import com.usal.jorgeav.sportapp.network.firebase.sync.UsersFirebaseSync;
import com.usal.jorgeav.sportapp.utils.Utiles;
import com.usal.jorgeav.sportapp.utils.UtilesPreferences;


/**
 * Presentador utilizado para mostrar la colección de usuarios desconocidos. Aquí se inicia la
 * consulta al Proveedor de Contenido para obtener los usuarios desconocidos y de la misma ciudad
 * que el usuario actual, y que serán enviados a la Vista {@link SearchUsersContract.View}.
 * <p>
 * Implementa la interfaz {@link SearchUsersContract.Presenter} para la comunicación con esta clase
 * y la interfaz {@link LoaderManager.LoaderCallbacks} para ser notificado por los callbacks de la
 * consulta.
 */
class SearchUsersPresenter implements
        SearchUsersContract.Presenter,
        LoaderManager.LoaderCallbacks<Cursor> {
    /**
     * Nombre de la clase
     */
    @SuppressWarnings("unused")
    private static final String TAG = SearchUsersPresenter.class.getSimpleName();

    /**
     * Vista correspondiente a este Presentador
     */
    private SearchUsersContract.View mSearchUsersView;

    /**
     * Constructor
     *
     * @param mEventInvitationsView Vista correspondiente a este Presentador
     */
    SearchUsersPresenter(SearchUsersContract.View mEventInvitationsView) {
        this.mSearchUsersView = mEventInvitationsView;
    }

    /**
     * Inicia el proceso de consulta a la base de datos de los usuarios desconocidos que estén en la
     * misma ciudad que el usuario actual.
     *
     * @param loaderManager objeto {@link LoaderManager} utilizado para consultar el Proveedor
     *                      de Contenido
     * @param b             contenedor de posibles parámetros utilizados en la consulta
     */
    @Override
    public void loadNearbyUsers(LoaderManager loaderManager, Bundle b) {
        loaderManager.destroyLoader(SportteamLoader.LOADER_USERS_FROM_CITY);
        loaderManager.destroyLoader(SportteamLoader.LOADER_USERS_WITH_NAME);

        String city = UtilesPreferences.getCurrentUserCity(mSearchUsersView.getActivityContext());
        UsersFirebaseSync.loadUsersFromCity(city);
        loaderManager.initLoader(SportteamLoader.LOADER_USERS_FROM_CITY, b, this);
    }

    /**
     * Inicia el proceso de consulta a la base de datos de los usuarios desconocidos cuyo nombre
     * coincida con uno dado.
     *
     * @param loaderManager objeto {@link LoaderManager} utilizado para consultar el Proveedor
     *                      de Contenido
     * @param b             contenedor de posibles parámetros utilizados en la consulta
     */
    @Override
    public void loadUsersWithName(LoaderManager loaderManager, Bundle b) {
        loaderManager.destroyLoader(SportteamLoader.LOADER_USERS_FROM_CITY);
        loaderManager.destroyLoader(SportteamLoader.LOADER_USERS_WITH_NAME);

        String username = b.getString(SearchUsersFragment.BUNDLE_USERNAME);
        UsersFirebaseSync.loadUsersWithName(username);
        loaderManager.restartLoader(SportteamLoader.LOADER_USERS_WITH_NAME, b, this);
    }

    /**
     * Invocado por {@link LoaderManager} para crear el Loader usado para la consulta
     *
     * @param id   identificador del Loader
     * @param args contenedor de posibles parámetros utilizados en la consulta
     * @return Loader que realiza la consulta.
     * @see SportteamLoader#cursorLoaderUsersFromCity(Context, String, String)
     * @see SportteamLoader#cursorLoaderUsersWithName(Context, String, String)
     */
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String currentUserID = Utiles.getCurrentUserId();
        if (TextUtils.isEmpty(currentUserID)) return null;
        switch (id) {
            case SportteamLoader.LOADER_USERS_FROM_CITY:
                String city = UtilesPreferences.getCurrentUserCity(mSearchUsersView.getActivityContext());
                return SportteamLoader
                        .cursorLoaderUsersFromCity(mSearchUsersView.getActivityContext(), currentUserID, city);
            case SportteamLoader.LOADER_USERS_WITH_NAME:
                if (args.containsKey(SearchUsersFragment.BUNDLE_USERNAME)) {
                    String username = args.getString(SearchUsersFragment.BUNDLE_USERNAME);
                    return SportteamLoader
                            .cursorLoaderUsersWithName(mSearchUsersView.getActivityContext(), currentUserID, username);
                }
        }
        return null;
    }

    /**
     * Invocado cuando finaliza la consulta del Loader, entrega los resultados obtenidos en
     * forma de {@link Cursor} a la Vista.
     *
     * @param loader Loader utilizado para la consulta
     * @param data   resultado de la consulta
     */
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mSearchUsersView.showUsers(data);
    }

    /**
     * Invocado cuando el {@link LoaderManager} exige un reinicio del Loader indicado. Se utiliza
     * este método para borrar los resultados de la consulta anterior.
     *
     * @param loader Loader que va a reiniciarse.
     */
    @Override
    public void onLoaderReset(Loader loader) {
        mSearchUsersView.showUsers(null);
    }
}
