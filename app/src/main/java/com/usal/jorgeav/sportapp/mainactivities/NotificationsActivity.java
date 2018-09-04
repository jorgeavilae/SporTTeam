package com.usal.jorgeav.sportapp.mainactivities;

import android.support.annotation.NonNull;
import android.view.MenuItem;

import com.usal.jorgeav.sportapp.R;
import com.usal.jorgeav.sportapp.notifications.NotificationsFragment;

/**
 * Actividad principal que hereda de {@link BaseActivity} y que aloja todos el Fragmento que
 * muestra la colección de notificaciones recibidas.
 */
public class NotificationsActivity extends BaseActivity {

    /**
     * Crea el Fragmento principal que debe mostrar en la pantalla {@link NotificationsFragment}.
     */
    @Override
    public void startMainFragment() {
        initFragment(NotificationsFragment.newInstance(), false);
        mNavigationView.setCheckedItem(R.id.nav_notifications);
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
        return item.getItemId() != R.id.nav_notifications && super.onNavigationItemSelected(item);
    }
}
