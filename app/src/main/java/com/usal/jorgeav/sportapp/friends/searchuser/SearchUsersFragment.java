package com.usal.jorgeav.sportapp.friends.searchuser;

import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.usal.jorgeav.sportapp.BaseFragment;
import com.usal.jorgeav.sportapp.R;
import com.usal.jorgeav.sportapp.adapters.UsersAdapter;
import com.usal.jorgeav.sportapp.profile.ProfileFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Jorge Avila on 04/06/2017.
 */

public class SearchUsersFragment extends BaseFragment implements SearchUsersContract.View,
        UsersAdapter.OnUserItemClickListener {
    private static final String TAG = SearchUsersFragment.class.getSimpleName();
    public static final String BUNDLE_USERNAME = "BUNDLE_USERNAME";

    private SearchUsersFragment mThis;
    SearchUsersContract.Presenter mSearchUsersPresenter;
    UsersAdapter mUsersRecyclerAdapter;

    @BindView(R.id.search_users_list)
    RecyclerView searchUsersList;
    @BindView(R.id.search_users_placeholder)
    ConstraintLayout searchUsersPlaceholder;
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
            mSearchUsersPresenter.loadNearbyUsers(getLoaderManager(), getArguments());
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
                showDialogForSearchName();
            }
        });
        return root;
    }

    private void showDialogForSearchName() {
        // Prepare View
        final View dialogView = getActivity().getLayoutInflater().inflate(R.layout.edit_text_change_dialog, null);
        final EditText editText = (EditText) dialogView.findViewById(R.id.change_dialog_text);
        editText.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME|InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        editText.setHint(R.string.prompt_name);

        // Create dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivityContext());
        builder.setView(dialogView);
        builder.setTitle(getString(R.string.dialog_title_search_user))
                .setPositiveButton(android.R.string.search_go, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mUsersRecyclerAdapter.replaceData(null);
                        Bundle b = new Bundle();
                        b.putString(BUNDLE_USERNAME, editText.getText().toString());
                        mSearchUsersPresenter.loadUsersWithName(getLoaderManager(), b);
                    }
                })
                .setNegativeButton(android.R.string.cancel, null)
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        hideSoftKeyboard();
                    }
                });
        builder.create().show();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mFragmentManagementListener.setCurrentDisplayedFragment("Buscar usuarios", this);
        mActionBarIconManagementListener.setToolbarAsUp();
    }

    @Override
    public void onStart() {
        super.onStart();
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
}
