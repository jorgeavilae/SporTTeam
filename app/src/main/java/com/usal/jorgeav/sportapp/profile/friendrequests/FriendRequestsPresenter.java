package com.usal.jorgeav.sportapp.profile.friendrequests;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.text.TextUtils;

import com.usal.jorgeav.sportapp.data.provider.SportteamLoader;
import com.usal.jorgeav.sportapp.network.firebase.sync.FirebaseSync;
import com.usal.jorgeav.sportapp.utils.Utiles;

/**
 * Presentador utilizado para mostrar la colección de usuarios que han enviado una petición de amistad
 * al usuario actual que está pendiente de respuesta. Aquí se inicia la consulta al Proveedor
 * de Contenido para obtener los usuarios que enviaron la petición y que serán enviados a la Vista
 * {@link FriendRequestsContract.View}.
 * <p>
 * Implementa la interfaz {@link FriendRequestsContract.Presenter} para la comunicación con
 * esta clase y la interfaz {@link LoaderManager.LoaderCallbacks} para ser notificado por los
 * callbacks de la consulta.
 */
class FriendRequestsPresenter implements
        FriendRequestsContract.Presenter,
        LoaderManager.LoaderCallbacks<Cursor> {
    /**
     * Nombre de la clase
     */
    @SuppressWarnings("unused")
    private static final String TAG = FriendRequestsPresenter.class.getSimpleName();

    /**
     * Vista correspondiente a este Presentador
     */
    private FriendRequestsContract.View mFriendRequestsView;

    /**
     * Constructor
     *
     * @param friendRequestsView Vista correspondiente a este Presentador
     */
    FriendRequestsPresenter(FriendRequestsContract.View friendRequestsView) {
        this.mFriendRequestsView = friendRequestsView;
    }

    /**
     * Inicia el proceso de consulta a la base de datos sobre las peticiones de amistad recibidas
     * por el usuario actual.
     *
     * @param loaderManager objeto {@link LoaderManager} utilizado para consultar el Proveedor
     *                      de Contenido
     * @param b             contenedor de posibles parámetros utilizados en la consulta
     */
    @Override
    public void loadFriendRequests(LoaderManager loaderManager, Bundle b) {
        FirebaseSync.loadUsersFromFriendsRequestsReceived();
        loaderManager.initLoader(SportteamLoader.LOADER_FRIENDS_REQUESTS_ID, b, this);
    }

    /**
     * Invocado por {@link LoaderManager} para crear el Loader usado para la consulta
     *
     * @param id   identificador del Loader
     * @param args contenedor de posibles parámetros utilizados en la consulta
     * @return Loader que realiza la consulta.
     * @see SportteamLoader#cursorLoaderFriendRequests(Context, String)
     */
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String currentUserID = Utiles.getCurrentUserId();
        if (TextUtils.isEmpty(currentUserID)) return null;

        switch (id) {
            case SportteamLoader.LOADER_FRIENDS_REQUESTS_ID:
                return SportteamLoader
                        .cursorLoaderFriendRequests(mFriendRequestsView.getActivityContext(), currentUserID);
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
        mFriendRequestsView.showFriendRequests(data);
    }

    /**
     * Invocado cuando el {@link LoaderManager} exige un reinicio del Loader indicado. Se utiliza
     * este método para borrar los resultados de la consulta anterior.
     *
     * @param loader Loader que va a reiniciarse.
     */
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mFriendRequestsView.showFriendRequests(null);
    }
}
