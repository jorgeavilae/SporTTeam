package com.usal.jorgeav.sportapp.fields;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.usal.jorgeav.sportapp.R;
import com.usal.jorgeav.sportapp.data.Field;
import com.usal.jorgeav.sportapp.data.FieldRepository;
import com.usal.jorgeav.sportapp.fields.detail.DetailFieldFragment;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FieldsFragment extends Fragment implements FieldsContract.View, FieldsAdapter.OnFieldItemClickListener {
    FieldsContract.Presenter mFieldsPresenter;
    FieldsAdapter mFieldsRecyclerAdapter;

    @BindView(R.id.fields_list)
    RecyclerView fieldsRecyclerList;

    public FieldsFragment() {
        // Required empty public constructor
    }

    public static FieldsFragment newInstance() {
        return new FieldsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mFieldsPresenter = new FieldsPresenter(new FieldRepository(), this);
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
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        mFieldsPresenter.loadFields();
    }

    @Override
    public void showFields(List<Field> fields) {
        mFieldsRecyclerAdapter.replaceData(fields);
    }

    @Override
    public void onFieldClick(Field field) {
        Fragment newFragment = DetailFieldFragment.newInstance(field);

        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.contentFrame, newFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
