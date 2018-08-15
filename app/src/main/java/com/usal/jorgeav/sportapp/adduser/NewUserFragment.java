package com.usal.jorgeav.sportapp.adduser;

import android.app.Activity;
import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.usal.jorgeav.sportapp.BaseFragment;
import com.usal.jorgeav.sportapp.R;
import com.usal.jorgeav.sportapp.adapters.PlaceAutocompleteAdapter;
import com.usal.jorgeav.sportapp.adduser.sportpractice.SportsListFragment;
import com.usal.jorgeav.sportapp.mainactivities.NewUserActivity;
import com.usal.jorgeav.sportapp.utils.Utiles;

import butterknife.BindView;
import butterknife.ButterKnife;
import pl.aprilapps.easyphotopicker.EasyImage;

public class NewUserFragment extends BaseFragment implements NewUserContract.View {
    private static final String TAG = NewUserFragment.class.getSimpleName();
    public static final int RC_PHOTO_PICKER = 2;

    private NewUserContract.Presenter mPresenter;

    Uri croppedImageUri;
    String newUserCitySelectedName;
    LatLng newUserCitySelectedCoord;

    // Static prevent double initialization with same ID
    static GoogleApiClient mGoogleApiClient;
    PlaceAutocompleteAdapter mAdapter;

    @BindView(R.id.new_user_email)
    EditText newUserEmail;
    @BindView(R.id.new_user_password)
    EditText newUserPassword;
    @BindView(R.id.new_user_visible_pass)
    ImageView newUserVisiblePass;
    @BindView(R.id.new_user_name)
    EditText newUserName;
    @BindView(R.id.new_user_age)
    EditText newUserAge;
    @BindView(R.id.new_user_city)
    AutoCompleteTextView newUserAutocompleteCity;
    @BindView(R.id.new_user_photo)
    ImageView newUserPhoto;
    @BindView(R.id.new_user_photo_button)
    ImageView newUserPhotoButton;

    public NewUserFragment() {
        // Required empty public constructor
    }

    public static NewUserFragment newInstance() {
        return new NewUserFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        if (mGoogleApiClient == null)
            mGoogleApiClient = new GoogleApiClient
                    .Builder(getActivity())
                    .addApi(Places.GEO_DATA_API)
                    .enableAutoManage(getActivity(), new GoogleApiClient.OnConnectionFailedListener() {
                        @Override
                        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                            Log.e(TAG, "onConnectionFailed: Google Api Client is not connected");
                        }
                    })
                    .build();
        else mGoogleApiClient.connect();

        mPresenter = new NewUserPresenter(this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        getActivity().getMenuInflater().inflate(R.menu.menu_ok, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if (item.getItemId() == R.id.action_ok) {
            hideSoftKeyboard();

            if (TextUtils.isEmpty(newUserEmail.getError())
                    && TextUtils.isEmpty(newUserPassword.getError())
                    && TextUtils.isEmpty(newUserName.getError())
                    && TextUtils.isEmpty(newUserAge.getError())
                    && !TextUtils.isEmpty(newUserEmail.getText())
                    && !TextUtils.isEmpty(newUserPassword.getText())
                    && !TextUtils.isEmpty(newUserName.getText())
                    && !TextUtils.isEmpty(newUserAge.getText())
                    && newUserCitySelectedName != null && newUserCitySelectedCoord != null) {

                SportsListFragment slf = SportsListFragment.newInstance("", ((NewUserActivity) getActivity()).sports);
                mFragmentManagementListener.initFragment(slf, true);

            } else {
                Toast.makeText(getActivity(), R.string.toast_invalid_arg, Toast.LENGTH_SHORT).show();
            }
            return true;
        }
        return false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_new_user, container, false);
        ButterKnife.bind(this, root);

        return root;
    }

    private void setEmailEditText() {
        newUserEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus)
                    mPresenter.checkUserEmailExists(newUserEmail.getText().toString());
            }
        });
    }

    private void setPasswordEditText() {
        newUserPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus)
                    if (newUserPassword.getText().toString().length() > 0
                            && newUserPassword.getText().toString().length() < 6)
                        newUserPassword.setError(getString(R.string.error_invalid_password));
            }
        });

        newUserVisiblePass.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN)
                    newUserPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                else if (event.getAction() == MotionEvent.ACTION_UP)
                    newUserPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                return true;
            }
        });
    }

    private void setPhotoButton() {
        if (croppedImageUri != null)
            Glide.with(this).load(croppedImageUri).into(newUserPhoto);

        newUserPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideSoftKeyboard();
                EasyImage.configuration(getActivity())
                        .setImagesFolderName(Environment.DIRECTORY_PICTURES)
                        .saveInAppExternalFilesDir();
                if (Utiles.isStorageCameraPermissionGranted(getActivity()))
                    EasyImage.openChooserWithGallery(getActivity(), getString(R.string.pick_photo_from), RC_PHOTO_PICKER);
            }
        });
    }

    private void setNameEditText() {
        newUserName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus) {
                    if (newUserName.getText().toString().length() > 20)
                        newUserName.setError(getString(R.string.error_incorrect_name));
                    mPresenter.checkUserNameExists(newUserName.getText().toString());
                }
            }
        });
    }

    private void setAgeEditText() {
        newUserAge.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus)
                    if (!TextUtils.isEmpty(newUserAge.getText())) {
                        Long age = Long.parseLong(newUserAge.getText().toString());
                        if (age <= 12 || age >= 100)
                            newUserAge.setError(getString(R.string.error_invalid_age));
                    }
            }
        });
    }

    private void setAutocompleteTextView() {
        // Set up the adapter that will retrieve suggestions from
        // the Places Geo Data API that cover Spain
        AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                .setTypeFilter(AutocompleteFilter.TYPE_FILTER_CITIES)
                /*https://developers.google.com/android/reference/com/google/android/gms/location/places/AutocompleteFilter.Builder.html#setCountry(java.lang.String)*/
                .setCountry("ES"/*https://en.wikipedia.org/wiki/ISO_3166-1_alpha-2#ES*/)
                .build();

        mAdapter = new PlaceAutocompleteAdapter(getActivityContext(), mGoogleApiClient, null, typeFilter);
        newUserAutocompleteCity.setAdapter(mAdapter);

        TextWatcher tw = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                    newUserCitySelectedName = null;
                    newUserCitySelectedCoord = null;
                    newUserAutocompleteCity.setError(getString(R.string.error_invalid_city));
            }
        };
        newUserAutocompleteCity.addTextChangedListener(tw);

        newUserAutocompleteCity.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                /*
                 Retrieve the place ID of the selected item from the Adapter.
                 The adapter stores each Place suggestion in a AutocompletePrediction from which we
                 read the place ID and title.
                  */
                AutocompletePrediction item = mAdapter.getItem(position);
                if (item != null) {
                    Log.i(TAG, "Autocomplete item selected: " + item.getPlaceId());
                    final ProgressDialog progressDialog = new ProgressDialog(getContext());
                    progressDialog.show();
                    Places.GeoDataApi.getPlaceById(mGoogleApiClient, item.getPlaceId())
                            .setResultCallback(new ResultCallback<PlaceBuffer>() {
                                @Override
                                public void onResult(@NonNull PlaceBuffer places) {
                                    // Stop UI until finish callback
                                    progressDialog.dismiss();
                                    if (places.getStatus().isSuccess() && places.getCount() > 0) {
                                        Place myPlace = places.get(0);
                                        newUserCitySelectedName = myPlace.getName().toString();
                                        newUserCitySelectedCoord = myPlace.getLatLng();
                                        newUserAutocompleteCity.setError(null);
                                        Log.i(TAG, "Place found: Name - " + myPlace.getName()
                                                + " LatLng - " + myPlace.getLatLng());
                                    } else {
                                        Log.e(TAG, "Place not found");
                                        Toast.makeText(getContext(),
                                                R.string.error_check_conn, Toast.LENGTH_SHORT).show();
                                    }
                                    places.release();
                                }
                            });
                }
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mFragmentManagementListener.setCurrentDisplayedFragment(getString(R.string.new_user_title), this);
    }

    @Override
    public void onStart() {
        super.onStart();

        setEmailEditText();
        setPasswordEditText();
        setPhotoButton();
        setNameEditText();
        setAgeEditText();
        setAutocompleteTextView();

        if (((NewUserActivity)getActivity()).sports != null && ((NewUserActivity)getActivity()).sportsInitialize) {
            hideContent();

            if (TextUtils.isEmpty(newUserEmail.getError())
                    && TextUtils.isEmpty(newUserPassword.getError())
                    && TextUtils.isEmpty(newUserName.getError())
                    && TextUtils.isEmpty(newUserAge.getError())) {

                ((NewUserActivity)getActivity()).sportsInitialize = mPresenter.createAuthUser(
                        newUserEmail.getText().toString(),
                        newUserPassword.getText().toString(),
                        newUserName.getText().toString(),
                        croppedImageUri,
                        newUserAge.getText().toString(),
                        newUserCitySelectedName,
                        newUserCitySelectedCoord,
                        ((NewUserActivity) getActivity()).sports);
            } else {
                showContent();
                Toast.makeText(getActivity(), R.string.toast_invalid_arg, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mFragmentManagementListener.showContent();
    }

    @Override
    public void croppedResult(Uri photoCroppedUri) {
        // Uri from cropped photo as result of UCrop
        croppedImageUri = photoCroppedUri;
        if (croppedImageUri != null)
            Glide.with(this)
                    .load(croppedImageUri)
                    .placeholder(R.drawable.profile_picture_placeholder)
                    .centerCrop()
                    .into(newUserPhoto);
    }

    @Override
    public Activity getHostActivity() {
        return getActivity();
    }

    @Override
    public void setEmailError(int stringRes) {
        this.newUserEmail.setError(getString(stringRes));
    }

    @Override
    public void setNameError(int stringRes) {
        this.newUserName.setError(getString(stringRes));
    }
}
