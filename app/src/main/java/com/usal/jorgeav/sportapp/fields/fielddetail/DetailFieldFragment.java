package com.usal.jorgeav.sportapp.fields.fielddetail;


import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.usal.jorgeav.sportapp.BaseFragment;
import com.usal.jorgeav.sportapp.R;
import com.usal.jorgeav.sportapp.adapters.ProfileSportsAdapter;
import com.usal.jorgeav.sportapp.adduser.sportpractice.SportsListFragment;
import com.usal.jorgeav.sportapp.data.Sport;
import com.usal.jorgeav.sportapp.fields.addfield.NewFieldFragment;
import com.usal.jorgeav.sportapp.mainactivities.FieldsActivity;
import com.usal.jorgeav.sportapp.utils.Utiles;
import com.usal.jorgeav.sportapp.utils.UtilesContentProvider;
import com.usal.jorgeav.sportapp.utils.UtilesTime;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailFieldFragment extends BaseFragment implements DetailFieldContract.View {
    @SuppressWarnings("unused")
    private static final String TAG = DetailFieldFragment.class.getSimpleName();
    public static final String BUNDLE_FIELD_ID = "BUNDLE_FIELD_ID";
    public static final String BUNDLE_IS_INFO = "BUNDLE_IS_INFO";

    private String mFieldId = "";

    private DetailFieldContract.Presenter mPresenter;

    private Menu mMenu = null;

    @BindView(R.id.field_detail_map)
    MapView detailFieldMap;
    private GoogleMap mMap;
    @BindView(R.id.field_detail_address)
    TextView detailFieldAddress;
    @BindView(R.id.field_detail_opening)
    TextView detailFieldOpening;
    @BindView(R.id.field_detail_closing)
    TextView detailFieldClosing;
    @BindView(R.id.field_detail_creator)
    TextView detailFieldCreator;

    @BindView(R.id.field_detail_sport_list)
    RecyclerView detailFieldSportList;
    ProfileSportsAdapter sportsAdapter;
    @BindView(R.id.field_detail_sport_placeholder)
    ConstraintLayout detailFieldSportPlaceholder;


    public DetailFieldFragment() {
        // Required empty public constructor
    }

    public static DetailFieldFragment newInstance(@NonNull String fieldId, boolean isInfo) {
        DetailFieldFragment fragment = new DetailFieldFragment();
        Bundle args = new Bundle();
        args.putString(BUNDLE_FIELD_ID, fieldId);
        args.putBoolean(BUNDLE_IS_INFO, isInfo);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        mPresenter = new DetailFieldPresenter(this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        mMenu = menu;
        super.onCreateOptionsMenu(mMenu, inflater);
        mMenu.clear();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        if (item.getItemId() == R.id.action_edit) {
            Fragment fragment = NewFieldFragment.newInstance(mFieldId);
            mFragmentManagementListener.initFragment(fragment, true);
            return true;
        } else if (item.getItemId() == R.id.action_delete) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivityContext())
                    .setTitle(R.string.dialog_msg_are_you_sure)
                    .setMessage(R.string.dialog_msg_delete_field)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            mPresenter.deleteField(mFieldId);
                        }
                    })
                    .setNegativeButton(android.R.string.no, null);
            builder.create().show();
            return true;
        } else if (item.getItemId() == R.id.action_add) {
            Fragment fragment = SportsListFragment.newInstance(mFieldId, sportsAdapter.getDataAsArrayList());
            mFragmentManagementListener.initFragment(fragment, true);
            return true;
        }
        return false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_detail_field, container, false);
        ButterKnife.bind(this, root);

        if (getArguments() != null && getArguments().containsKey(BUNDLE_FIELD_ID))
            mFieldId = getArguments().getString(BUNDLE_FIELD_ID);

        //Need to be MapView, not SupportMapFragment https://stackoverflow.com/a/19354359/4235666
        detailFieldMap.onCreate(savedInstanceState);
        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        detailFieldMap.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;

                Utiles.setCoordinatesInMap(getActivityContext(), mMap, null);
            }
        });

        sportsAdapter = new ProfileSportsAdapter(null, new ProfileSportsAdapter.OnProfileSportClickListener() {
            @Override
            public void onProfileSportClick(final Sport s) {
                displayVoteCourtDialog(s);
            }
        }, Glide.with(this));
        detailFieldSportList.setAdapter(sportsAdapter);
        detailFieldSportList.setHasFixedSize(true);
        detailFieldSportList.setLayoutManager(new GridLayoutManager(getActivityContext(), 2, LinearLayoutManager.VERTICAL, false));

        return root;
    }

    private void displayVoteCourtDialog(final Sport s) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View view = inflater.inflate(R.layout.vote_dialog, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivityContext());
        builder.setTitle(R.string.dialog_title_vote)
                .setView(view)
                .setPositiveButton(R.string.action_vote, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        RatingBar ratingBar = (RatingBar) view.findViewById(R.id.rating_for_vote);
                        String sportId = s.getSportID();
                        mPresenter.voteSportInField(mFieldId, sportId, ratingBar.getRating());
                    }
                })
                .setNegativeButton(android.R.string.cancel, null);
        builder.create().show();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mFragmentManagementListener.setCurrentDisplayedFragment(getString(R.string.field_detail_title), this);
        mActionBarIconManagementListener.setToolbarAsUp();
    }

    @Override
    public void onStart() {
        super.onStart();
        detailFieldMap.onStart();
        mPresenter.openField(getLoaderManager(), getArguments());
    }

    @Override
    public void showFieldName(String name) {
        showContent();
        mActionBarIconManagementListener.setActionBarTitle(name);
    }

    @Override
    public void showFieldPlace(String address, String city, LatLng coordinates) {
        showContent();
        this.detailFieldAddress.setText(address);

        if (getActivity() instanceof FieldsActivity) {
            ((FieldsActivity) getActivity()).mAddress = address;
            ((FieldsActivity) getActivity()).mCity = city;
            ((FieldsActivity) getActivity()).mCoord = coordinates;
        }

        Utiles.setCoordinatesInMap(getActivityContext(), mMap, coordinates);
    }

    @Override
    public void showFieldTimes(long openTime, long closeTime) {
        showContent();
        if (openTime == closeTime) {
            this.detailFieldOpening.setText(getString(R.string.open_24h));
            this.detailFieldClosing.setText("");
        } else {
            this.detailFieldOpening.setText(UtilesTime.millisToTimeString(openTime));
            this.detailFieldClosing.setText(UtilesTime.millisToTimeString(closeTime));
        }
    }

    @Override
    public void showSportCourts(Cursor cursor) {
        sportsAdapter.replaceData(cursor);
        if (cursor != null && cursor.getCount() > 0) {
            detailFieldSportList.setVisibility(View.VISIBLE);
            detailFieldSportPlaceholder.setVisibility(View.INVISIBLE);
        } else {
            detailFieldSportList.setVisibility(View.INVISIBLE);
            detailFieldSportPlaceholder.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void showFieldCreator(String userId) {
        String name = UtilesContentProvider.getUserNameFromContentProvider(userId);
        if (name != null && !TextUtils.isEmpty(name)) {
            String created = getString(R.string.created_by);
            this.detailFieldCreator.setText(String.format(created, name));
        }

        String myUid = Utiles.getCurrentUserId();
        if (TextUtils.isEmpty(myUid)) return;

        // If current user is creator and is not info detail fragment: allow edit/delete
        if (myUid.equals(userId)
                && getArguments() != null && getArguments().containsKey(BUNDLE_IS_INFO)
                && !getArguments().getBoolean(BUNDLE_IS_INFO) && mMenu != null) {
            mMenu.clear();
            getActivity().getMenuInflater().inflate(R.menu.menu_edit_delete, mMenu);
            getActivity().getMenuInflater().inflate(R.menu.menu_add, mMenu);
        }
    }

    @Override
    public void clearUI() {
        this.mActionBarIconManagementListener.setActionBarTitle(getString(R.string.field_detail_title));
        this.detailFieldAddress.setText("");
        this.detailFieldOpening.setText("");
        this.detailFieldClosing.setText("");
        this.detailFieldCreator.setText("");
    }

    @Override
    public void onPause() {
        super.onPause();
        detailFieldMap.onPause();
        sportsAdapter.replaceData(null);
    }

    @Override
    public void onDetach() {
        super.onDetach();

        if (getActivity() instanceof FieldsActivity) {
            ((FieldsActivity) getActivity()).mAddress = null;
            ((FieldsActivity) getActivity()).mCity = null;
            ((FieldsActivity) getActivity()).mCoord = null;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        detailFieldMap.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        detailFieldMap.onDestroy();
    }

    @Override
    public void onStop() {
        super.onStop();
        detailFieldMap.onStop();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        detailFieldMap.onLowMemory();
    }
}
