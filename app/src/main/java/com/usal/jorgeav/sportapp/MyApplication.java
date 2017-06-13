package com.usal.jorgeav.sportapp;

import android.app.Application;
import android.content.Context;

/**
 * Created by Jorge Avila on 13/06/2017.
 */

public class MyApplication extends Application {
    private static Context context;

    public void onCreate() {
        super.onCreate();
        MyApplication.context = getApplicationContext();
    }

    public static Context getAppContext() {
        return MyApplication.context;
    }
}
