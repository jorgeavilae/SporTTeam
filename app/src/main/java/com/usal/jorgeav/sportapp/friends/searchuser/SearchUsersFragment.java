package com.usal.jorgeav.sportapp.friends.searchuser;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.usal.jorgeav.sportapp.BaseFragment;
import com.usal.jorgeav.sportapp.R;
import com.usal.jorgeav.sportapp.adapters.UsersAdapter;
import com.usal.jorgeav.sportapp.profile.ProfileFragment;
import com.usal.jorgeav.sportapp.utils.Utiles;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SearchUsersFragment extends BaseFragment implements SearchUsersContract.View,
        UsersAdapter.OnUserItemClickListener {
    @SuppressWarnings("unused")
    private static final String TAG = SearchUsersFragment.class.getSimpleName();

    public static final String BUNDLE_USERNAME = "BUNDLE_USERNAME";

    private SearchUsersContract.Presenter mSearchUsersPresenter;
    UsersAdapter mUsersRecyclerAdapter;

    @BindView(R.id.search_users_list)
    RecyclerView searchUsersList;
    @BindView(R.id.search_users_placeholder)
    ConstraintLayout searchUsersPlaceholder;
    @BindView(R.id.search_users_edit)
    EditText searchUsersEditText;

    public SearchUsersFragment() {
        // Required empty public constructor
    }

    public static SearchUsersFragment newInstance() {
        return new SearchUsersFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        mSearchUsersPresenter = new SearchUsersPresenter(this);
        mUsersRecyclerAdapter = new UsersAdapter(null, this, Glide.with(this));
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        return false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_search_users, container, false);
        ButterKnife.bind(this, root);

        searchUsersList.setAdapter(mUsersRecyclerAdapter);
        searchUsersList.setHasFixedSize(true);
        searchUsersList.setLayoutManager(new GridLayoutManager(getActivityContext(),
                Utiles.calculateNoOfColumns(getActivityContext())));

        // Search users when click on GO_Button in keyboard
        searchUsersEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.search_users_ime || id == EditorInfo.IME_ACTION_GO) {
                    mUsersRecyclerAdapter.replaceData(null);
                    Bundle b = new Bundle();
                    b.putString(BUNDLE_USERNAME, searchUsersEditText.getText().toString());
                    mSearchUsersPresenter.loadUsersWithName(getLoaderManager(), b);
                    hideSoftKeyboard();
                    return true;
                }
                return false;
            }
        });
        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mFragmentManagementListener.setCurrentDisplayedFragment(getString(R.string.action_search_users), this);
        mNavigationDrawerManagementListener.setToolbarAsUp();
    }

    @Override
    public void onStart() {
        super.onStart();
        mSearchUsersPresenter.loadNearbyUsers(getLoaderManager(), getArguments());
    }

    @Override
    public void onPause() {
        super.onPause();
        mUsersRecyclerAdapter.replaceData(null);
    }

    @Override
    public void showUsers(Cursor cursor) {
        mUsersRecyclerAdapter.replaceData(cursor);
        if (cursor != null && cursor.getCount() > 0) {
            searchUsersList.setVisibility(View.VISIBLE);
            searchUsersPlaceholder.setVisibility(View.INVISIBLE);
        } else {
            searchUsersList.setVisibility(View.INVISIBLE);
            searchUsersPlaceholder.setVisibility(View.VISIBLE);
        }
        mFragmentManagementListener.showContent();
    }

    @Override
    public void onUserClick(String uid) {
        Fragment newFragment = ProfileFragment.newInstance(uid);
        mFragmentManagementListener.initFragment(newFragment, true);
    }

    @Override
    public boolean onUserLongClick(String uid) {
        return false;
    }
}
