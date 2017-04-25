package com.usal.jorgeav.sportapp.data;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.usal.jorgeav.sportapp.R;

/**
 * Created by Jorge Avila on 23/04/2017.
 */

public class UsuarioManager {
    private Usuario mCurrentUser;

    public void loadNewUser(Context context) {
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.android_cheer);
        mCurrentUser = new Usuario(bitmap, "Nombre Apellidos", "Ciudad, País", "30");
    }

    public Usuario User() {
        return mCurrentUser;
    }
}
