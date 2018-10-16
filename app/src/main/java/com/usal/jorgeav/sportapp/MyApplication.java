package com.usal.jorgeav.sportapp;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;

/**
 * Clase heredera de {@link Application} que representa toda la aplicación.
 * <p>
 * Usada para guardar una referencia estática al {@link Context} de Aplicación
 */
public class MyApplication extends Application {
    /**
     * Referencia estática a {@link Context} de Aplicación
     */
    @SuppressLint("StaticFieldLeak")
    private static Context context;

    /**
     * Invocado cuando la aplicación esta iniciándose, guarda la referencia a Context
     */
    @Override
    public void onCreate() {
        super.onCreate();
        MyApplication.context = getApplicationContext();
    }

    /**
     * Método de acceso a la variable estática {@link Context} de Aplicación
     *
     * @return {@link MyApplication#context}
     */
    public static Context getAppContext() {
        return MyApplication.context;
    }
}
