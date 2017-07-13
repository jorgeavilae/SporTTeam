package com.usal.jorgeav.sportapp.notifications;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.usal.jorgeav.sportapp.BaseFragment;
import com.usal.jorgeav.sportapp.R;
import com.usal.jorgeav.sportapp.adapters.FieldsAdapter;
import com.usal.jorgeav.sportapp.data.MyNotification;
import com.usal.jorgeav.sportapp.fields.detail.DetailFieldFragment;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NotificationsFragment extends BaseFragment implements NotificationsContract.View, FieldsAdapter.OnFieldItemClickListener {
    private static final String TAG = NotificationsFragment.class.getSimpleName();

    NotificationsContract.Presenter mPresenter;
    FieldsAdapter mFieldsRecyclerAdapter;

    @BindView(R.id.fields_list)
    RecyclerView fieldsRecyclerList;
    @BindView(R.id.fields_placeholder)
    ConstraintLayout fieldsPlaceholder;

    public NotificationsFragment() {
        // Required empty public constructor
    }

    public static NotificationsFragment newInstance() {
        return new NotificationsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        mPresenter = new NotificationsPresenter(this);
//        mFieldsRecyclerAdapter = new FieldsAdapter(null, this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.menu_notifications, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if (item.getItemId() == R.id.action_clear_notifications) {
            Log.d(TAG, "onOptionsItemSelected: Clear Notifications");
//
            return true;
        }
        return false;
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
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mFragmentManagementListener.setCurrentDisplayedFragment(getString(R.string.notifications), this);
        mActionBarIconManagementListener.setToolbarAsNav();
    }

    @Override
    public void onStart() {
        super.onStart();
        mPresenter.loadNotifications();
    }

    @Override
    public void onPause() {
        super.onPause();
        mFieldsRecyclerAdapter.replaceData(null);
    }

    @Override
    public void showNotifications(ArrayList<MyNotification> notifications) {
        Log.d(TAG, "showNotifications: "+notifications);
//        mFieldsRecyclerAdapter.replaceData(cursor);
//        if (cursor != null && cursor.getCount() > 0) {
//            fieldsRecyclerList.setVisibility(View.VISIBLE);
//            fieldsPlaceholder.setVisibility(View.INVISIBLE);
//        } else {
//            fieldsRecyclerList.setVisibility(View.INVISIBLE);
//            fieldsPlaceholder.setVisibility(View.VISIBLE);
//        }
        mFragmentManagementListener.showContent();
    }

    @Override
    public void onFieldClick(String fieldId, String sportId) {
        Fragment newFragment = DetailFieldFragment.newInstance(fieldId, sportId);
        mFragmentManagementListener.initFragment(newFragment, true);
    }
}
