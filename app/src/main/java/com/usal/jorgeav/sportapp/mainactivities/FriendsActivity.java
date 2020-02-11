package com.usal.jorgeav.sportapp.mainactivities;

import androidx.annotation.NonNull;
import android.view.MenuItem;

import com.usal.jorgeav.sportapp.R;
import com.usal.jorgeav.sportapp.friends.FriendsFragment;
import com.usal.jorgeav.sportapp.profile.ProfileFragment;

/**
 * Actividad principal que hereda de {@link BaseActivity} y que aloja todos los Fragmentos
 * relacionados con los amigos y la búsqueda de nuevos.
 */
public class FriendsActivity extends BaseActivity {
    /**
     * Nombre de la clase
     */
    public static final String TAG = FriendsActivity.class.getSimpleName();
    /**
     * Clave para identificar un dato añadido al {@link android.app.PendingIntent}, cuando se
     * inicia esta Actividad desde una notificación. El dato añadido es el identificador del
     * usuario que debe mostrar esta Actividad al iniciarse, en lugar de la colección de amigos.
     */
    public static final String USER_ID_PENDING_INTENT_EXTRA = "USER_ID_PENDING_INTENT_EXTRA";

    /**
     * Crea el Fragmento principal que debe mostrar en la pantalla. Comprueba, también, si
     * hay algún dato extra en el {@link android.content.Intent} que inicia la Actividad para
     * cargar un Fragmento diferente
     */
    @Override
    public void startMainFragment() {
        String userId = getIntent().getStringExtra(USER_ID_PENDING_INTENT_EXTRA);

        initFragment(FriendsFragment.newInstance(), false);
        if (userId != null) {
            initFragment(ProfileFragment.newInstance(userId), true);
        }
        mNavigationView.setCheckedItem(R.id.nav_friends);
    }

    /**
     * Comprueba que la entrada pulsada del menú lateral de navegación no es la
     * correspondiente a esta Actividad, en cuyo caso ignora la pulsación. Si no lo es,
     * invoca el mismo método de la superclase {@link BaseActivity#onNavigationItemSelected(MenuItem)}
     *
     * @param item elemento del menú pulsado
     * @return valor de {@link BaseActivity#onNavigationItemSelected(MenuItem)} o false si es
     * la misma entrada
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return item.getItemId() != R.id.nav_friends && super.onNavigationItemSelected(item);
    }
}
