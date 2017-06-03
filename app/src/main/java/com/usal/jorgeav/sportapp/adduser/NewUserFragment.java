package com.usal.jorgeav.sportapp.adduser;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.usal.jorgeav.sportapp.R;
import com.usal.jorgeav.sportapp.data.Sport;
import com.usal.jorgeav.sportapp.data.User;
import com.usal.jorgeav.sportapp.network.FirebaseDBContract;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Jorge Avila on 03/06/2017.
 */

public class NewUserFragment extends DialogFragment {

    @BindView(R.id.new_user_toolbar)
    Toolbar newUseerToolbar;
    @BindView(R.id.new_user_email)
    EditText newUserEmail;
    @BindView(R.id.new_user_password)
    EditText newUserPassword;
//    @BindView(R.id.new_user_auth_button)
//    Button newUserAuthButton;
    @BindView(R.id.new_user_name)
    EditText newUserName;
    @BindView(R.id.new_user_age)
    EditText newUserAge;
    @BindView(R.id.new_user_photo)
    EditText newUserPhoto;
    @BindView(R.id.new_user_city)
    EditText newUserCity;
    @BindView(R.id.new_user_spinner)
    Spinner newUserSpinner;
    @BindView(R.id.new_user_sport_rating)
    RatingBar newUserSportRating;
    @BindView(R.id.new_user_add_sport_button)
    Button newUserAddSportButton;
    @BindView(R.id.new_user_create_button)
    Button newUserCreateButton;
    @BindView(R.id.new_user_content)
    NestedScrollView newUserContent;
    @BindView(R.id.new_user_progressbar)
    ProgressBar newUserProgressbar;

    ArrayList<Sport> sports;

    public NewUserFragment() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    public static NewUserFragment newInstance() {
        NewUserFragment frag = new NewUserFragment();
        return frag;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_user, container, false);
        ButterKnife.bind(this, view);

        newUseerToolbar.setTitle("Add User");
        newUseerToolbar.setNavigationIcon(R.drawable.ic_action_close);
        newUseerToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();    }
        });

        sports = new ArrayList<Sport>();
        final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.sport_id,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        newUserSpinner.setAdapter(adapter);
        newUserAddSportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Sport s = new Sport(newUserSpinner.getSelectedItem().toString(), newUserSportRating.getRating(), 0);
                sports.add(s);
                Toast.makeText(getContext(), "Sport añadido", Toast.LENGTH_SHORT).show();
            }
        });

        newUserEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus)
                    checkEmailExists(newUserEmail.getText().toString());
            }
        });
        newUserPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus)
                    if (newUserPassword.getText().toString().length() < 6)
                        newUserPassword.setError("Necesita al menos 6 caracteres");
            }
        });
        newUserName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus)
                    checkNameExists(newUserName.getText().toString());
            }
        });

        newUserCreateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Check emailEditText and PassEditText
                if (!TextUtils.isEmpty(newUserEmail.getText())
                        && TextUtils.isEmpty(newUserEmail.getError())
                        && !TextUtils.isEmpty(newUserPassword.getText())
                        && TextUtils.isEmpty(newUserPassword.getError())
                        && !TextUtils.isEmpty(newUserName.getText())
                        && TextUtils.isEmpty(newUserName.getError())
                        && !TextUtils.isEmpty(newUserAge.getText())
                        && !TextUtils.isEmpty(newUserPhoto.getText())
                        && !TextUtils.isEmpty(newUserCity.getText())
                        && sports.size() > 0) {
                    hideContent();
                    createAuthUser(newUserEmail.getText().toString(), newUserPassword.getText().toString());
                } else
                    Toast.makeText(getContext(), "Error: algun campo vacio", Toast.LENGTH_SHORT).show();
            }
        });
        showContent();

        return view;
    }

    private void checkEmailExists(String email) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference(FirebaseDBContract.TABLE_USERS);
        String userEmailPath = FirebaseDBContract.DATA + "/" + FirebaseDBContract.User.EMAIL;

        myRef.orderByChild(userEmailPath).equalTo(email)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            newUserEmail.setError("Email already exist");
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) { }
                });
    }

    private void checkNameExists(String name) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference(FirebaseDBContract.TABLE_USERS);
        String userNamePath = FirebaseDBContract.DATA + "/" + FirebaseDBContract.User.ALIAS;

        myRef.orderByChild(userNamePath).equalTo(name)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            newUserName.setError("Name already exist");
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) { }
                });
    }

    private void createAuthUser(String email, String pass) {
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, pass)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Toast.makeText(getContext(), "Log in is Successful", Toast.LENGTH_SHORT).show();
                        if (task.isSuccessful()) {
                            User user = new User(FirebaseAuth.getInstance().getCurrentUser().getUid(),
                                    newUserEmail.getText().toString(),
                                    newUserName.getText().toString(),
                                    newUserCity.getText().toString(),
                                    Integer.parseInt(newUserAge.getText().toString()),
                                    newUserPhoto.getText().toString(),
                                    sports);
                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            DatabaseReference myRef = database.getReference(FirebaseDBContract.TABLE_USERS);
                            myRef.child(user.getmId()).setValue(user.toMap());
                            //TODO ahora que?
                            showContent();
                        } else {
                            //TODO error show content
                        }
                    }
                });
    }

    private void closeDialogApplyingChanges(boolean applyChanges) {
        if(applyChanges) {

        }
        dismiss();
    }

    private void showContent() {
        newUserContent.setVisibility(View.VISIBLE);
        newUserProgressbar.setVisibility(View.INVISIBLE);
    }

    private void hideContent() {
        newUserContent.setVisibility(View.INVISIBLE);
        newUserProgressbar.setVisibility(View.VISIBLE);
    }
}
