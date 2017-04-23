package com.usal.jorgeav.sportapp;

import android.graphics.Bitmap;

/**
 * Created by Jorge Avila on 23/04/2017.
 */

public class Usuario {
    Bitmap imageProfile;
    String name;
    String city;
    String age;

    public Usuario(Bitmap imageProfile, String name, String city, String age) {
        this.imageProfile = imageProfile;
        this.name = name;
        this.city = city;
        this.age = age;
    }
}
