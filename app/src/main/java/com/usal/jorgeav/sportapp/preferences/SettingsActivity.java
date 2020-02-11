package com.usal.jorgeav.sportapp.preferences;

import android.content.Context;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.usal.jorgeav.sportapp.R;

/**
 * Esta Actividad sólo instancia, muestra y aloja el Fragmento encargado de la funcionalidad
 * relativa a las preferencias de la aplicación {@link SettingsFragment}.
 */
public class SettingsActivity extends AppCompatActivity {
    /**
     * Nombre de la clase
     */
    @SuppressWarnings("unused")
    private final static String TAG = SettingsActivity.class.getSimpleName();

    /**
     * Crea e inicia la transición hacia el Fragmento de preferencias {@link SettingsFragment}.
     * Además, carga el archivo de la interfaz en la pantalla, inicializa la barra superior
     * {@link Toolbar} y la configura para navegar hacia atrás.
     *
     * @param savedInstanceState estado de la Actividad guardado en una posible rotación de
     *                           la pantalla, o null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Toolbar mToolbar = (Toolbar) findViewById(R.id.settings_toolbar);
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onBackPressed();
                }
            });
        }

        SettingsFragment settingsFragment = SettingsFragment.newInstance();

        getFragmentManager().beginTransaction()
                .replace(R.id.contentFrame, settingsFragment)
                .addToBackStack(null)
                .commitAllowingStateLoss();
    }

    /**
     * Finaliza la Actividad cuando se invoca esta acción desde {@link SettingsFragment}
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    /**
     * Obtiene una referencia a la {@link View} que tiene el foco de la interfaz y, si esta
     * mostrando el teclado flotante en la pantalla, lo esconde
     *
     * @see <a href= "https://stackoverflow.com/a/17789187/4235666">
     * (StackOverflow) Close/hide the Android Soft Keyboard</a>
     */
    public void hideSoftKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            InputMethodManager imm =
                    (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null)
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
