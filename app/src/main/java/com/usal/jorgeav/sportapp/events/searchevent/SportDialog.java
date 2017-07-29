package com.usal.jorgeav.sportapp.events.searchevent;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.util.Log;

import com.usal.jorgeav.sportapp.R;

/**
 * Created by Jorge Avila on 06/06/2017.
 */

public class SportDialog extends DialogFragment {
    private static final String TAG = SportDialog.class.getSimpleName();

    private SportDialog.SportDialogListener mListener;

    public interface SportDialogListener {
        void onDialogSportClick(String sportId);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if (getTargetFragment() instanceof SportDialog.SportDialogListener)
            mListener = (SportDialog.SportDialogListener) getTargetFragment();
        else
            Log.e(TAG, "onCreateDialog: SportDialog needs a listener", new ClassCastException());

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Pick a Sport");
        builder.setItems(R.array.sport_id_values, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (mListener != null)
                    mListener.onDialogSportClick(getResources().getStringArray(R.array.sport_id_values)[i]);
            }
        });
        return builder.create();
    }
}
