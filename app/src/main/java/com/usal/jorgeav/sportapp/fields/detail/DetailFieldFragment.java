package com.usal.jorgeav.sportapp.fields.detail;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.usal.jorgeav.sportapp.MainActivity;
import com.usal.jorgeav.sportapp.MainActivityContract;
import com.usal.jorgeav.sportapp.R;
import com.usal.jorgeav.sportapp.data.Field;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailFieldFragment extends Fragment implements DetailFieldContract.View{
    private static final String TAG = DetailFieldFragment.class.getSimpleName();
    private static final String ARG_FIELD = "param-field";

    private Field mField = null;
    private DetailFieldContract.Presenter mPresenter;
    private MainActivityContract.ActionBarIconManagement mActionBarIconManagementListener;
    private MainActivityContract.FragmentManagement mFragmentManagementListener;

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

    public static DetailFieldFragment newInstance(Field field) {
        DetailFieldFragment fragment = new DetailFieldFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_FIELD, field);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mField = getArguments().getParcelable(ARG_FIELD);
        }

        mPresenter = new DetailFieldPresenter(mField, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_detail_field, container, false);
        ButterKnife.bind(this, root);

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mFragmentManagementListener.setCurrentDisplayedFragment(mField.getmName(), this);
        mActionBarIconManagementListener.setToolbarAsUp();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mActionBarIconManagementListener.setToolbarAsNav();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MainActivityContract.ActionBarIconManagement) {
            mActionBarIconManagementListener = (MainActivityContract.ActionBarIconManagement) context;
            mFragmentManagementListener = (MainActivityContract.FragmentManagement) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mActionBarIconManagementListener = null;
        mFragmentManagementListener = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.openField();
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
        ((MainActivity)getActivity()).showContent();
        this.textViewFieldRating.setText(String.format(Locale.getDefault(), "%2.2f", rating));
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
}
