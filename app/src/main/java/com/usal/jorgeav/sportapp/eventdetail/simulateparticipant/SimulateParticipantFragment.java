package com.usal.jorgeav.sportapp.eventdetail.simulateparticipant;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.usal.jorgeav.sportapp.BaseFragment;
import com.usal.jorgeav.sportapp.R;
import com.usal.jorgeav.sportapp.utils.Utiles;

import butterknife.BindView;
import butterknife.ButterKnife;
import pl.aprilapps.easyphotopicker.EasyImage;

public class SimulateParticipantFragment extends BaseFragment implements SimulateParticipantContract.View {
    public static final String TAG = SimulateParticipantFragment.class.getSimpleName();
    public static final String BUNDLE_EVENT_ID = "BUNDLE_EVENT_ID";

    public static final int RC_PHOTO_PICKER = 2;

    SimulateParticipantContract.Presenter mPresenter;

    @BindView(R.id.new_simulated_user_photo)
    ImageView simulatedUserPhoto;
    @BindView(R.id.new_simulated_user_photo_button)
    ImageView simulatedUserPhotoButton;
    Uri photoUri = null;
    @BindView(R.id.new_simulated_user_name)
    EditText simulatedUserName;
    @BindView(R.id.new_simulated_user_age)
    EditText simulatedUserAge;

    public SimulateParticipantFragment() {
    }

    public static SimulateParticipantFragment newInstance(@NonNull String eventId) {
        SimulateParticipantFragment fragment = new SimulateParticipantFragment();
        Bundle b = new Bundle();
        b.putString(BUNDLE_EVENT_ID, eventId);
        fragment.setArguments(b);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        mPresenter = new SimulateParticipantPresenter(this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.menu_ok, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        hideSoftKeyboard();
        if (item.getItemId() == R.id.action_ok) {
            String eventId = "";
            if (getArguments() != null && getArguments().containsKey(BUNDLE_EVENT_ID))
                eventId = getArguments().getString(BUNDLE_EVENT_ID);

            mPresenter.addSimulatedParticipant(
                    eventId,
                    simulatedUserName.getText().toString(),
                    photoUri,
                    simulatedUserAge.getText().toString());
            return true;
        }
        return false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_new_simulated_user, container, false);
        ButterKnife.bind(this, root);

        simulatedUserPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EasyImage.configuration(getActivity())
                        .setImagesFolderName(Environment.DIRECTORY_PICTURES)
                        .saveInAppExternalFilesDir();
                if (Utiles.isStorageCameraPermissionGranted(getActivity()))
                    EasyImage.openChooserWithGallery(getActivity(),
                            getString(R.string.pick_photo_from), RC_PHOTO_PICKER);
            }
        });
        return root;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mFragmentManagementListener.setCurrentDisplayedFragment(getString(R.string.new_simulated_user), this);
        mActionBarIconManagementListener.setToolbarAsUp();
    }

    @Override
    public void onResume() {
        super.onResume();
        mFragmentManagementListener.showContent();
    }

    @Override
    public void croppedResult(Uri photoCroppedUri) {
        // Uri from cropped photo as result of UCrop
        photoUri = photoCroppedUri;
        if (photoUri != null)
            Glide.with(this)
                    .load(photoUri)
                    .placeholder(R.drawable.profile_picture_placeholder)
                    .centerCrop()
                    .into(simulatedUserPhoto);
    }

    @Override
    public void showResult(final int msgResource) {
        /* Perform UI actions (like display a Toast or press back) need to happen in UI thread
         * https://stackoverflow.com/a/3875204/4235666
         * https://developer.android.com/reference/android/app/Activity.html#runOnUiThread(java.lang.Runnable)
         */
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (msgResource != -1)
                    Toast.makeText(getActivity(), msgResource, Toast.LENGTH_SHORT).show();
                getActivity().onBackPressed();
            }
        });
    }

    @Override
    public void showError(int msgResource) {
        Toast.makeText(getActivity(), msgResource, Toast.LENGTH_SHORT).show();
    }
}
