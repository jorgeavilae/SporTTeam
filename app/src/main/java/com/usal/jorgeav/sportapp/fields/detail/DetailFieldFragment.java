package com.usal.jorgeav.sportapp.fields.detail;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.usal.jorgeav.sportapp.BaseFragment;
import com.usal.jorgeav.sportapp.R;
import com.usal.jorgeav.sportapp.mainactivities.BaseActivity;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailFieldFragment extends BaseFragment implements DetailFieldContract.View{
    private static final String TAG = DetailFieldFragment.class.getSimpleName();
    public static final String BUNDLE_FIELD_ID = "BUNDLE_FIELD_ID";
    public static final String BUNDLE_SPORT_ID = "BUNDLE_SPORT_ID";

    private static String mFieldId = "";
    private static String mSportId = "";
    private DetailFieldContract.Presenter mPresenter;

    @BindView(R.id.field_detail_id)
    TextView textViewFieldId;
    @BindView(R.id.field_detail_name)
    TextView textViewFieldName;
    @BindView(R.id.field_detail_address)
    TextView textViewFieldAddress;
    @BindView(R.id.field_detail_rating)
    TextView textViewFieldRating;
    @BindView(R.id.field_detail_sport)
    TextView textViewFieldSport;
    @BindView(R.id.field_detail_opening)
    TextView textViewFieldOpening;
    @BindView(R.id.field_detail_closing)
    TextView textViewFieldClosing;


    public DetailFieldFragment() {
        // Required empty public constructor
    }

    public static DetailFieldFragment newInstance(@NonNull String fieldId, @NonNull String sportId) {
        DetailFieldFragment fragment = new DetailFieldFragment();
        Bundle args = new Bundle();
        args.putString(BUNDLE_FIELD_ID, fieldId);
        args.putString(BUNDLE_SPORT_ID, sportId);
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
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.menu_field, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if (item.getItemId() == R.id.action_vote) {
            Log.d(TAG, "onOptionsItemSelected: Vote");
            // TODO: 27/06/2017
            return true;
        }
        return false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_detail_field, container, false);
        ButterKnife.bind(this, root);

        if (getArguments() != null && getArguments().containsKey(BUNDLE_FIELD_ID) && getArguments().containsKey(BUNDLE_SPORT_ID)) {
            mFieldId = getArguments().getString(BUNDLE_FIELD_ID);
            mSportId = getArguments().getString(BUNDLE_SPORT_ID);
        }

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mFragmentManagementListener.setCurrentDisplayedFragment(mFieldId, this);
        mActionBarIconManagementListener.setToolbarAsUp();
        mPresenter.openField(getLoaderManager(), getArguments());
    }

    @Override
    public void showFieldId(String id) {
        ((BaseActivity)getActivity()).showContent();
        this.textViewFieldId.setText(id);
    }

    @Override
    public void showFieldName(String name) {
        ((BaseActivity)getActivity()).showContent();
        this.textViewFieldName.setText(name);
    }

    @Override
    public void showFieldAddress(String address) {
        ((BaseActivity)getActivity()).showContent();
        this.textViewFieldAddress.setText(address);
    }

    @Override
    public void showFieldRating(Float rating) {
        if (rating > -1) {
            ((BaseActivity) getActivity()).showContent();
            this.textViewFieldRating.setText(String.format(Locale.getDefault(), "%2.2f", rating));
        }
    }

    @Override
    public void showFieldSport(String sport) {
        ((BaseActivity)getActivity()).showContent();
        this.textViewFieldSport.setText(sport);
    }

    @Override
    public void showFieldOpeningTime(String opening) {
        ((BaseActivity)getActivity()).showContent();
        this.textViewFieldOpening.setText(opening);
    }

    @Override
    public void showFieldClosingTime(String closing) {
        ((BaseActivity)getActivity()).showContent();
        this.textViewFieldClosing.setText(closing);
    }
}
