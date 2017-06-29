package com.usal.jorgeav.sportapp.friends.searchuser;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import android.widget.Button;

import com.usal.jorgeav.sportapp.BaseFragment;
import com.usal.jorgeav.sportapp.R;
import com.usal.jorgeav.sportapp.adapters.UsersAdapter;
import com.usal.jorgeav.sportapp.profile.ProfileFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Jorge Avila on 04/06/2017.
 */

public class SearchUsersFragment extends BaseFragment implements SearchUsersContract.View, UsersAdapter.OnUserItemClickListener, UsernameDialog.UsernameDialogListener  {
    private static final String TAG = SearchUsersFragment.class.getSimpleName();
    public static final String BUNDLE_USERNAME = "BUNDLE_USERNAME";

    private SearchUsersFragment mThis;
    SearchUsersContract.Presenter mSearchUsersPresenter;
    UsersAdapter mUsersRecyclerAdapter;

    @BindView(R.id.search_users_list)
    RecyclerView searchUsersList;
    @BindView(R.id.search_users_button)
    Button searchUsersButton;

    public SearchUsersFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        mSearchUsersPresenter = new SearchUsersPresenter(this);
        mUsersRecyclerAdapter = new UsersAdapter(null, this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.menu_filters, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if (item.getItemId() == R.id.action_clear_filter) {
            Log.d(TAG, "onOptionsItemSelected: Clear Filters");
            // TODO: 27/06/2017
            return true;
        }
        return false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_search_users, container, false);
        ButterKnife.bind(this, root);

        searchUsersList.setAdapter(mUsersRecyclerAdapter);
        searchUsersList.setHasFixedSize(true);
        searchUsersList.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        searchUsersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UsernameDialog dialog = new UsernameDialog();
                dialog.setTargetFragment(mThis, 1);
                dialog.show(getActivity().getSupportFragmentManager(), null);
            }
        });
        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mFragmentManagementListener.setCurrentDisplayedFragment(SearchUsersFragment.class.getSimpleName(), this);
        mActionBarIconManagementListener.setToolbarAsUp();
        mSearchUsersPresenter.loadNearbyUsers(getLoaderManager(), getArguments());
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mThis = this;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mThis = null;
    }

    @Override
    public void onPause() {
        super.onPause();
        mUsersRecyclerAdapter.replaceData(null);
    }

    @Override
    public void showUsers(Cursor cursor) {
        mUsersRecyclerAdapter.replaceData(cursor);
        mFragmentManagementListener.showContent();
    }

    @Override
    public void onUserClick(String uid) {
        Fragment newFragment = ProfileFragment.newInstance(uid);
        mFragmentManagementListener.initFragment(newFragment, true);
    }

    @Override
    public void onDialogPositiveClick(String username) {
        mUsersRecyclerAdapter.replaceData(null);
        Bundle b = new Bundle();
        b.putString(BUNDLE_USERNAME, username);
        mSearchUsersPresenter.loadNearbyUsersWithName(getLoaderManager(), b);
    }
}
