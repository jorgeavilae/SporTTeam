package com.usal.jorgeav.sportapp.fields;


import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.usal.jorgeav.sportapp.BaseFragment;
import com.usal.jorgeav.sportapp.R;
import com.usal.jorgeav.sportapp.adapters.FieldsAdapter;
import com.usal.jorgeav.sportapp.fields.addfield.NewFieldFragment;
import com.usal.jorgeav.sportapp.fields.detail.DetailFieldFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FieldsFragment extends BaseFragment implements FieldsContract.View, FieldsAdapter.OnFieldItemClickListener {
    private static final String TAG = FieldsFragment.class.getSimpleName();

    FieldsContract.Presenter mFieldsPresenter;
    FieldsAdapter mFieldsRecyclerAdapter;

    @BindView(R.id.fields_new_field)
    Button fieldsNewField;
    @BindView(R.id.fields_list)
    RecyclerView fieldsRecyclerList;
    @BindView(R.id.fields_placeholder)
    ConstraintLayout fieldsPlaceholder;

    public FieldsFragment() {
        // Required empty public constructor
    }

    public static FieldsFragment newInstance() {
        return new FieldsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mFieldsPresenter = new FieldsPresenter(this);
        mFieldsRecyclerAdapter = new FieldsAdapter(null, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_fields, container, false);
        ButterKnife.bind(this, root);

        fieldsRecyclerList.setAdapter(mFieldsRecyclerAdapter);
        fieldsRecyclerList.setHasFixedSize(true);
        fieldsRecyclerList.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        fieldsNewField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = NewFieldFragment.newInstance(null, null);
                mFragmentManagementListener.initFragment(fragment, true);
            }
        });
        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mFragmentManagementListener.setCurrentDisplayedFragment(getString(R.string.fields), this);
        mActionBarIconManagementListener.setToolbarAsNav();
    }

    @Override
    public void onStart() {
        super.onStart();
        mFieldsPresenter.loadNearbyFields(getLoaderManager(), getArguments());
    }

    @Override
    public void onPause() {
        super.onPause();
        mFieldsRecyclerAdapter.replaceData(null);
    }

    @Override
    public void showFields(Cursor cursor) {
        mFieldsRecyclerAdapter.replaceData(cursor);
        if (cursor != null && cursor.getCount() > 0) {
            fieldsRecyclerList.setVisibility(View.VISIBLE);
            fieldsPlaceholder.setVisibility(View.INVISIBLE);
        } else {
            fieldsRecyclerList.setVisibility(View.INVISIBLE);
            fieldsPlaceholder.setVisibility(View.VISIBLE);
        }

        mFragmentManagementListener.showContent();
    }

    @Override
    public void onFieldClick(String fieldId, String sportId) {
        Fragment newFragment = DetailFieldFragment.newInstance(fieldId, sportId);
        mFragmentManagementListener.initFragment(newFragment, true);
    }
}
