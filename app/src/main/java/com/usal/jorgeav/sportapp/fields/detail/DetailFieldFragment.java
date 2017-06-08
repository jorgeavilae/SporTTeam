package com.usal.jorgeav.sportapp.fields.detail;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.usal.jorgeav.sportapp.ActivityContracts;
import com.usal.jorgeav.sportapp.MainActivity;
import com.usal.jorgeav.sportapp.R;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailFieldFragment extends Fragment implements DetailFieldContract.View{
    private static final String TAG = DetailFieldFragment.class.getSimpleName();
    public static final String BUNDLE_FIELD_ID = "BUNDLE_FIELD_ID";
    public static final String BUNDLE_SPORT_ID = "BUNDLE_SPORT_ID";
    public static final int LOADER_FIELD_ID = 10000;

    private static String mFieldId = "";
    private static String mSportId = "";
    private DetailFieldContract.Presenter mPresenter;
    private ActivityContracts.ActionBarIconManagement mActionBarIconManagementListener;
    private ActivityContracts.FragmentManagement mFragmentManagementListener;

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

    public static DetailFieldFragment newInstance(String fieldId, String sportId) {
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

        mPresenter = new DetailFieldPresenter(this);
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
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ActivityContracts.FragmentManagement)
            mFragmentManagementListener = (ActivityContracts.FragmentManagement) context;
        if (context instanceof ActivityContracts.ActionBarIconManagement)
            mActionBarIconManagementListener = (ActivityContracts.ActionBarIconManagement) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mFragmentManagementListener = null;
        mActionBarIconManagementListener = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        Bundle b = new Bundle();
        b.putString(BUNDLE_FIELD_ID, mFieldId);
        b.putString(BUNDLE_SPORT_ID, mSportId);
        getLoaderManager().initLoader(LOADER_FIELD_ID, b, mPresenter.getLoaderInstance());
    }

    @Override
    public void showFieldId(String id) {
        ((MainActivity)getActivity()).showContent();
        this.textViewFieldId.setText(id);
    }

    @Override
    public void showFieldName(String name) {
        ((MainActivity)getActivity()).showContent();
        this.textViewFieldName.setText(name);
    }

    @Override
    public void showFieldAddress(String address) {
        ((MainActivity)getActivity()).showContent();
        this.textViewFieldAddress.setText(address);
    }

    @Override
    public void showFieldRating(Float rating) {
        if (rating > -1) {
            ((MainActivity) getActivity()).showContent();
            this.textViewFieldRating.setText(String.format(Locale.getDefault(), "%2.2f", rating));
        }
    }

    @Override
    public void showFieldSport(String sport) {
        ((MainActivity)getActivity()).showContent();
        this.textViewFieldSport.setText(sport);
    }

    @Override
    public void showFieldOpeningTime(String opening) {
        ((MainActivity)getActivity()).showContent();
        this.textViewFieldOpening.setText(opening);
    }

    @Override
    public void showFieldClosingTime(String closing) {
        ((MainActivity)getActivity()).showContent();
        this.textViewFieldClosing.setText(closing);
    }

    @Override
    public Context getActivityContext() {
        return getActivity();
    }
}
