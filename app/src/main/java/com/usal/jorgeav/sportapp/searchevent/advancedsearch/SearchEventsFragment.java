package com.usal.jorgeav.sportapp.searchevent.advancedsearch;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.usal.jorgeav.sportapp.BaseFragment;
import com.usal.jorgeav.sportapp.R;
import com.usal.jorgeav.sportapp.adapters.SportSpinnerAdapter;
import com.usal.jorgeav.sportapp.utils.Utiles;

import java.util.ArrayList;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;


public class SearchEventsFragment extends BaseFragment {

    @SuppressWarnings("unused")
    private static final String TAG = SearchEventsFragment.class.getSimpleName();

    public static final String BUNDLE_SPORT = "BUNDLE_SPORT";
    String mSportIdSelected = "";

    @BindView(R.id.search_events_button)
    Button searchEventsButton;
    @BindView(R.id.search_events_icon)
    ImageView searchEventsIcon;
    @BindView(R.id.search_events_sport)
    TextView searchEventsSportName;

    public SearchEventsFragment() {
        // Required empty public constructor
    }

    public static Fragment newInstance() {
        return new SearchEventsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.menu_ok, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if (item.getItemId() == R.id.action_ok) {
            return true;
        }
        return false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_search_events, container, false);
        ButterKnife.bind(this, root);

        searchEventsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createPickSportDialog();
            }
        });

        if (savedInstanceState != null && savedInstanceState.containsKey(BUNDLE_SPORT))
            setSportSearched(savedInstanceState.getString(BUNDLE_SPORT));

        return root;
    }

    private void createPickSportDialog() {
        ArrayList<String> sportsResources = new ArrayList<>(
                Arrays.asList(getResources().getStringArray(R.array.sport_id_values)));
        final SportSpinnerAdapter listAdapter = new SportSpinnerAdapter(getActivityContext(),
                R.layout.sport_spinner_item, sportsResources);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.pick_sport)
                .setAdapter(listAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String sportId = (String) listAdapter.getItem(i);
                setSportSearched(sportId);
            }
        })
                .setCancelable(true)
                .setNegativeButton(R.string.delete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                unsetSportSearched();
            }
        });
        builder.create().show();
    }

    private void setSportSearched(String sportId) {
        mSportIdSelected = sportId;

        int sportStringResource = getResources()
                .getIdentifier(sportId, "string", getActivityContext().getPackageName());
        searchEventsSportName.setText(getString(sportStringResource));

        int sportDrawableResource = Utiles.getSportIconFromResource(sportId);
        searchEventsIcon.setVisibility(View.VISIBLE);
        Glide.with(this).load(sportDrawableResource).into(searchEventsIcon);
    }

    private void unsetSportSearched() {
        mSportIdSelected = "";

        searchEventsSportName.setText(null);
        searchEventsIcon.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mFragmentManagementListener.setCurrentDisplayedFragment(getString(R.string.search_events), this);
        mActionBarIconManagementListener.setToolbarAsUp();
        mFragmentManagementListener.showContent();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (!TextUtils.isEmpty(mSportIdSelected))
            outState.putString(BUNDLE_SPORT, mSportIdSelected);
    }
}