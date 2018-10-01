package com.usal.jorgeav.sportapp.friends;

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
 * Presentador utilizado para mostrar la colección de amigos del usuario actual. Aquí se inicia la
 * consulta al Proveedor de Contenido para obtener los amigos de este usuario que serán enviados a
 * la Vista {@link FriendsContract.View}.
 * <p>
 * Implementa la interfaz {@link FriendsContract.Presenter} para la comunicación con esta clase y la
 * interfaz {@link LoaderManager.LoaderCallbacks} para ser notificado por los callbacks de la
 * consulta.
 */
class FriendsPresenter implements
        FriendsContract.Presenter,
        LoaderManager.LoaderCallbacks<Cursor> {
    /**
     * Nombre de la clase
     */
    @SuppressWarnings("unused")
    private static final String TAG = FriendsPresenter.class.getSimpleName();

    /**
     * Vista correspondiente a este Presentador
     */
    private FriendsContract.View mFriendsView;

    /**
     * Constructor
     *
     * @param friendsView Vista correspondiente a este Presentador
     */
    FriendsPresenter(FriendsContract.View friendsView) {
        this.mFriendsView = friendsView;
    }

    /**
     * Inicia el proceso de consulta a la base de datos sobre los amigos del usuario actual.
     *
     * @param loaderManager objeto {@link LoaderManager} utilizado para consultar el Proveedor
     *                      de Contenido
     * @param b             contenedor de posibles parámetros utilizados en la consulta
     */
    @Override
    public void loadFriend(LoaderManager loaderManager, Bundle b) {
        FirebaseSync.loadUsersFromFriends();
        loaderManager.initLoader(SportteamLoader.LOADER_FRIENDS_ID, b, this);
    }

    /**
     * Invocado por {@link LoaderManager} para crear el Loader usado para la consulta
     *
     * @param id   identificador del Loader
     * @param args contenedor de posibles parámetros utilizados en la consulta
     * @return Loader que realiza la consulta.
     * @see SportteamLoader#cursorLoaderFriends(Context, String)
     */
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String currentUserID = Utiles.getCurrentUserId();
        if (TextUtils.isEmpty(currentUserID)) return null;
        switch (id) {
            case SportteamLoader.LOADER_FRIENDS_ID:
                return SportteamLoader
                        .cursorLoaderFriends(mFriendsView.getActivityContext(), currentUserID);
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
        mFriendsView.showFriends(data);
    }

    /**
     * Invocado cuando el {@link LoaderManager} exige un reinicio del Loader indicado. Se utiliza
     * este método para borrar los resultados de la consulta anterior.
     *
     * @param loader Loader que va a reiniciarse.
     */
    @Override
    public void onLoaderReset(Loader loader) {
        mFriendsView.showFriends(null);
    }

}
