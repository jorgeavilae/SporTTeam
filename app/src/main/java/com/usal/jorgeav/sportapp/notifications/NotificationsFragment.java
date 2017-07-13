package com.usal.jorgeav.sportapp.notifications;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
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
import com.usal.jorgeav.sportapp.adapters.MyNotificationsAdapter;
import com.usal.jorgeav.sportapp.data.MyNotification;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NotificationsFragment extends BaseFragment implements NotificationsContract.View,
        MyNotificationsAdapter.OnMyNotificationItemClickListener {
    private static final String TAG = NotificationsFragment.class.getSimpleName();

    NotificationsContract.Presenter mPresenter;
    MyNotificationsAdapter myNotificationsAdapter;

    @BindView(R.id.recycler_list)
    RecyclerView notificationsRecyclerList;
    @BindView(R.id.list_placeholder)
    ConstraintLayout notificationsPlaceholder;

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
        myNotificationsAdapter = new MyNotificationsAdapter(null, this);
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
        View root = inflater.inflate(R.layout.fragment_list, container, false);
        ButterKnife.bind(this, root);

        notificationsRecyclerList.setAdapter(myNotificationsAdapter);
        notificationsRecyclerList.setHasFixedSize(true);
        notificationsRecyclerList.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
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
        myNotificationsAdapter.replaceData(null);
    }

    @Override
    public void showNotifications(HashMap<String, MyNotification> notifications) {
        Log.d(TAG, "showNotifications: "+notifications);
        myNotificationsAdapter.replaceData(notifications);
        if (notifications != null && notifications.size() > 0) {
            Log.d(TAG, "showNotifications: show");
            notificationsRecyclerList.setVisibility(View.VISIBLE);
            notificationsPlaceholder.setVisibility(View.INVISIBLE);
        } else {
            Log.d(TAG, "showNotifications: placeholder");
            notificationsRecyclerList.setVisibility(View.INVISIBLE);
            notificationsPlaceholder.setVisibility(View.VISIBLE);
        }
        mFragmentManagementListener.showContent();
    }

    @Override
    public void onMyNotificationClick(MyNotification notification) {
        Log.d(TAG, "onMyNotificationClick: "+notification);
    }
}
