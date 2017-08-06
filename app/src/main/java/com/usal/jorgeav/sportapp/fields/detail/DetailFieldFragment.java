package com.usal.jorgeav.sportapp.fields.detail;


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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.model.LatLng;
import com.usal.jorgeav.sportapp.BaseFragment;
import com.usal.jorgeav.sportapp.R;
import com.usal.jorgeav.sportapp.adapters.ProfileSportsAdapter;
import com.usal.jorgeav.sportapp.adduser.sportpractice.SportsListFragment;
import com.usal.jorgeav.sportapp.data.Sport;
import com.usal.jorgeav.sportapp.fields.addfield.NewFieldFragment;
import com.usal.jorgeav.sportapp.mainactivities.BaseActivity;
import com.usal.jorgeav.sportapp.mainactivities.FieldsActivity;
import com.usal.jorgeav.sportapp.utils.Utiles;
import com.usal.jorgeav.sportapp.utils.UtilesTime;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailFieldFragment extends BaseFragment implements DetailFieldContract.View{
    private static final String TAG = DetailFieldFragment.class.getSimpleName();
    public static final String BUNDLE_FIELD_ID = "BUNDLE_FIELD_ID";
    public static final String BUNDLE_IS_INFO = "BUNDLE_IS_INFO";

    private DetailFieldContract.Presenter mPresenter;
    private String mFieldId = "";
    private Menu mMenu = null;

    @BindView(R.id.field_detail_creator)
    TextView textViewFieldCreator;
    @BindView(R.id.field_detail_name)
    TextView textViewFieldName;
    @BindView(R.id.field_detail_address)
    TextView textViewFieldAddress;
    @BindView(R.id.field_detail_opening)
    TextView textViewFieldOpening;
    @BindView(R.id.field_detail_closing)
    TextView textViewFieldClosing;

    @BindView(R.id.field_detail_sport_list)
    RecyclerView fieldSportList;
    ProfileSportsAdapter sportsAdapter;
    @BindView(R.id.field_detail_sport_placeholder)
    ConstraintLayout fieldSportPlaceholder;
    @BindView(R.id.field_detail_edit_sport)
    Button fieldEditSportListButton;


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
            Log.d(TAG, "onOptionsItemSelected: Edit");
            Fragment fragment = NewFieldFragment.newInstance(mFieldId);
            mFragmentManagementListener.initFragment(fragment, true);
            return true;
        } else if (item.getItemId() == R.id.action_delete) {
            Log.d(TAG, "onOptionsItemSelected: Delete");
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivityContext());
            builder.setTitle("Borrar campo")
                    .setMessage("Seguro que desea borrarlo?")
                    .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            mPresenter.deleteField(mFieldId);
                        }})
                    .setNegativeButton("No", null);
            builder.create().show();
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

        sportsAdapter = new ProfileSportsAdapter(null, new ProfileSportsAdapter.OnProfileSportClickListener() {
            @Override
            public void onProfileSportClick(final Sport s) {
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
        }, Glide.with(this));
        fieldSportList.setAdapter(sportsAdapter);
        fieldSportList.setHasFixedSize(true);
        fieldSportList.setLayoutManager(new GridLayoutManager(getActivityContext(), 2, LinearLayoutManager.VERTICAL, false));

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mFragmentManagementListener.setCurrentDisplayedFragment("Detalles de pista", this);
        mActionBarIconManagementListener.setToolbarAsUp();
    }

    @Override
    public void onStart() {
        super.onStart();
        mPresenter.openField(getLoaderManager(), getArguments());
    }

    @Override
    public void showFieldId(String id) {
        ((BaseActivity)getActivity()).showContent();
    }

    @Override
    public void showFieldName(String name) {
        ((BaseActivity)getActivity()).showContent();
        this.textViewFieldName.setText(name);
        mActionBarIconManagementListener.setActionBarTitle(name);
    }

    @Override
    public void showFieldPlace(String address, String city, LatLng coordinates) {
        ((BaseActivity)getActivity()).showContent();
        this.textViewFieldAddress.setText(address);

        if (getActivity() instanceof FieldsActivity) {
            ((FieldsActivity) getActivity()).mAddress = address;
            ((FieldsActivity) getActivity()).mCity = city;
            ((FieldsActivity) getActivity()).mCoord = coordinates;
        }
    }

    @Override
    public void showFieldTimes(long openTime, long closeTime) {
        ((BaseActivity)getActivity()).showContent();
        this.textViewFieldOpening.setText(UtilesTime.millisToTimeString(openTime));
        this.textViewFieldClosing.setText(UtilesTime.millisToTimeString(closeTime));
    }

    @Override
    public void showSportCourts(Cursor cursor) {
        sportsAdapter.replaceData(cursor);
        if (cursor != null && cursor.getCount() > 0) {
            fieldSportList.setVisibility(View.VISIBLE);
            fieldSportPlaceholder.setVisibility(View.INVISIBLE);
        } else {
            fieldSportList.setVisibility(View.INVISIBLE);
            fieldSportPlaceholder.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void showFieldCreator(String userId) {
        this.textViewFieldCreator.setText(userId);

        String myUid = Utiles.getCurrentUserId();
        if (TextUtils.isEmpty(myUid)) return;
        if (myUid.equals(userId)) {
            //Update menu
            if (getArguments() != null && getArguments().containsKey(BUNDLE_IS_INFO))
                if (!getArguments().getBoolean(BUNDLE_IS_INFO) && mMenu != null) {
                    mMenu.clear();
                    getActivity().getMenuInflater().inflate(R.menu.menu_edit_delete, mMenu);
                }

            //Update UI
            fieldEditSportListButton.setVisibility(View.VISIBLE);
            fieldEditSportListButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Fragment fragment = SportsListFragment.newInstance(mFieldId, sportsAdapter.getDataAsArrayList());
                    mFragmentManagementListener.initFragment(fragment, true);
                }
            });
        }
    }

    @Override
    public void clearUI() {
        this.textViewFieldCreator.setText("");
        this.textViewFieldName.setText("");
        this.mActionBarIconManagementListener.setActionBarTitle("");
        this.textViewFieldAddress.setText("");
        this.textViewFieldOpening.setText("");
        this.textViewFieldClosing.setText("");
    }

    @Override
    public void onPause() {
        super.onPause();
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
}
