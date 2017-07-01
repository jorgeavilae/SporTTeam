package com.usal.jorgeav.sportapp.events.addevent.selectfield;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.usal.jorgeav.sportapp.BaseFragment;
import com.usal.jorgeav.sportapp.R;
import com.usal.jorgeav.sportapp.adapters.FieldsAdapter;
import com.usal.jorgeav.sportapp.fields.detail.DetailFieldFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Jorge Avila on 06/06/2017.
 */

public class SelectFieldFragment extends BaseFragment implements SelectFieldContract.View, FieldsAdapter.OnFieldItemClickListener {
    private static final String TAG = SelectFieldFragment.class.getSimpleName();
    public static final String BUNDLE_SPORT_ID = "BUNDLE_SPORT_ID";

    SelectFieldContract.Presenter mSelectFieldPresenter;
    FieldsAdapter mFieldsAdapter;

    @BindView(R.id.recycler_list)
    RecyclerView fieldsSelectionList;

    public SelectFieldFragment() {
        // Required empty public constructor
    }

    public static SelectFieldFragment newInstance(String sportId) {
        Bundle args = new Bundle();
        args.putString(BUNDLE_SPORT_ID, sportId);
        SelectFieldFragment fragment = new SelectFieldFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        mSelectFieldPresenter = new SelectFieldPresenter(this);
        mFieldsAdapter = new FieldsAdapter(null, this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_list, container, false);
        ButterKnife.bind(this, root);

        fieldsSelectionList.setAdapter(mFieldsAdapter);
        fieldsSelectionList.setHasFixedSize(true);
        fieldsSelectionList.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mFragmentManagementListener.setCurrentDisplayedFragment("Selecciona un campo", this);
        mActionBarIconManagementListener.setToolbarAsUp();
        mSelectFieldPresenter.loadFieldsWithSport(getLoaderManager(), getArguments());
    }

    @Override
    public void onPause() {
        super.onPause();
        mFieldsAdapter.replaceData(null);
    }

    @Override
    public void showFields(Cursor cursor) {
        mFieldsAdapter.replaceData(cursor);
        mFragmentManagementListener.showContent();
    }

    @Override
    public void onFieldClick(String fieldId, String sportId) {
        if (getActivity() instanceof OnFieldSelected)
            ((OnFieldSelected) getActivity()).retrieveFieldSelected(fieldId);
        Fragment newFragment = DetailFieldFragment.newInstance(fieldId, sportId);
        mFragmentManagementListener.initFragment(newFragment, true);
    }

    public interface OnFieldSelected {
        void retrieveFieldSelected(String fieldId);
    }
}
