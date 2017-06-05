package com.usal.jorgeav.sportapp.friends.searchuser;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.usal.jorgeav.sportapp.R;

/**
 * Created by Jorge Avila on 04/06/2017.
 */

public class UsernameDialog extends DialogFragment {

    private UsernameDialogListener mListener;
    private EditText mUsernameEditText;

    public interface UsernameDialogListener {
        void onDialogPositiveClick(String username);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if (getTargetFragment() instanceof UsernameDialogListener)
            mListener = (UsernameDialogListener) getTargetFragment();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Username?");
        builder.setPositiveButton("Search", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (mListener != null)
                    mListener.onDialogPositiveClick(mUsernameEditText.getText().toString());
            }
        });
        builder.setNegativeButton("Cancel", null);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.search_dialog, null);
        mUsernameEditText = (EditText) view.findViewById(R.id.dialog_username);
        builder.setView(view);

        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof UsernameDialogListener)
            mListener = (UsernameDialogListener) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}
