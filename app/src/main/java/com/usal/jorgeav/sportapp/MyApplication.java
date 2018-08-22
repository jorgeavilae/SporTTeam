package com.usal.jorgeav.sportapp;

import android.app.Application;
import android.content.Context;

/**
 * Clase heredera de {@link Application} que representa toda la aplicacion.
 * <p>
 * Usada para guardar una referencia estatica al {@link Context} de Aplicacion
 */
public class MyApplication extends Application {
    /**
     * Referncia estatica a {@link Context} de Aplicacion
     */
    private static Context context;

    /**
     * Invocado cuando la aplicacion esta iniciandose, guarda la referencia a Context
     */
    @Override
    public void onCreate() {
        super.onCreate();
        MyApplication.context = getApplicationContext();
    }

    /**
     * Metodo de acceso a la variable estatica {@link Context} de Aplicacion
     * @return {@link MyApplication#context}
     */
    public static Context getAppContext() {
        return MyApplication.context;
    }
}
