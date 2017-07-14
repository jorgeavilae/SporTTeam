package com.usal.jorgeav.sportapp.notifications;


import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.usal.jorgeav.sportapp.BaseFragment;
import com.usal.jorgeav.sportapp.R;
import com.usal.jorgeav.sportapp.adapters.MyNotificationsAdapter;
import com.usal.jorgeav.sportapp.alarms.alarmdetail.DetailAlarmFragment;
import com.usal.jorgeav.sportapp.data.MyNotification;
import com.usal.jorgeav.sportapp.eventdetail.DetailEventFragment;
import com.usal.jorgeav.sportapp.network.firebase.FirebaseDBContract;
import com.usal.jorgeav.sportapp.profile.ProfileFragment;

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
            mPresenter.deleteAllNotifications();
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
        myNotificationsAdapter.replaceData(notifications);
        if (notifications != null && notifications.size() > 0) {
            notificationsRecyclerList.setVisibility(View.VISIBLE);
            notificationsPlaceholder.setVisibility(View.INVISIBLE);
        } else {
            notificationsRecyclerList.setVisibility(View.INVISIBLE);
            notificationsPlaceholder.setVisibility(View.VISIBLE);
        }
        mFragmentManagementListener.showContent();
    }

    @Override
    public void onMyNotificationClick(final String key, final MyNotification notification) {
        FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
        String myUid = ""; if (fUser != null) myUid = fUser.getUid();
        if (TextUtils.isEmpty(myUid)) return;

            final AlertDialog.Builder builder = new AlertDialog.Builder(getActivityContext());
            builder.setMessage("Borrar esta notification?")
                    .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            mPresenter.deleteNotification(key);
                            mPresenter.loadNotifications();
                        }
                    })
                    .setNegativeButton("No", null)
                    .setNeutralButton("See details", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            @FirebaseDBContract.NotificationDataTypes int type = notification.getData_type();
                            switch (type) {
                                case FirebaseDBContract.NOTIFICATION_TYPE_NONE:
                                    break;
                                case FirebaseDBContract.NOTIFICATION_TYPE_USER:
                                    mFragmentManagementListener.initFragment(
                                            ProfileFragment.newInstance(notification.getExtra_data_one()), true);
                                    break;
                                case FirebaseDBContract.NOTIFICATION_TYPE_EVENT:
                                    mFragmentManagementListener.initFragment(
                                            DetailEventFragment.newInstance(notification.getExtra_data_one()), true);
                                    break;
                                case FirebaseDBContract.NOTIFICATION_TYPE_ALARM:
                                    mFragmentManagementListener.initFragment(
                                            DetailAlarmFragment.newInstance(notification.getExtra_data_one()), true);
                                    break;
                                case FirebaseDBContract.NOTIFICATION_TYPE_ERROR:
                                    break;
                            }
                        }
                    });
            builder.create().show();
    }
}
