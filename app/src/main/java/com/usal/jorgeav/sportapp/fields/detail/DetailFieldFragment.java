package com.usal.jorgeav.sportapp.fields.detail;


import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
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
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.usal.jorgeav.sportapp.BaseFragment;
import com.usal.jorgeav.sportapp.R;
import com.usal.jorgeav.sportapp.adapters.ProfileSportsAdapter;
import com.usal.jorgeav.sportapp.adduser.sportpractice.SportsListFragment;
import com.usal.jorgeav.sportapp.mainactivities.BaseActivity;
import com.usal.jorgeav.sportapp.utils.Utiles;
import com.usal.jorgeav.sportapp.utils.UtilesTime;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailFieldFragment extends BaseFragment implements DetailFieldContract.View{
    private static final String TAG = DetailFieldFragment.class.getSimpleName();
    public static final String BUNDLE_FIELD_ID = "BUNDLE_FIELD_ID";

    private DetailFieldContract.Presenter mPresenter;

    @BindView(R.id.field_detail_id)
    TextView textViewFieldId;
    @BindView(R.id.field_detail_name)
    TextView textViewFieldName;
    @BindView(R.id.field_detail_address)
    TextView textViewFieldAddress;
    @BindView(R.id.field_detail_rating)
    TextView textViewFieldRating;
    @BindView(R.id.field_detail_sport)
    TextView textViewFieldSport;
    @BindView(R.id.field_detail_opening)
    TextView textViewFieldOpening;
    @BindView(R.id.field_detail_closing)
    TextView textViewFieldClosing;

    @BindView(R.id.field_detail_sport_list)
    RecyclerView fieldSportList;
    ProfileSportsAdapter sportsAdapter;
    @BindView(R.id.field_detail_sport_placeholder)
    ConstraintLayout fieldSportPlaceholder;
    @BindView(R.id.field_detail_edit_sport)
    Button fieldEditSportListButton;


    public DetailFieldFragment() {
        // Required empty public constructor
    }

    public static DetailFieldFragment newInstance(@NonNull String fieldId) {
        DetailFieldFragment fragment = new DetailFieldFragment();
        Bundle args = new Bundle();
        args.putString(BUNDLE_FIELD_ID, fieldId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        mPresenter = new DetailFieldPresenter(this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.menu_field, menu); //TODO quitar
        inflater.inflate(R.menu.menu_edit_delete, menu); // TODO implementar
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if (item.getItemId() == R.id.action_vote) { //TODO mover
            Log.d(TAG, "onOptionsItemSelected: Vote");
            LayoutInflater inflater = getActivity().getLayoutInflater();
            final View view = inflater.inflate(R.layout.vote_dialog, null);
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivityContext());
            builder.setMessage("Selecciona puntuación")
                    .setView(view)
                    .setPositiveButton("Votar", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            RatingBar ratingBar = (RatingBar) view.findViewById(R.id.rating_for_vote);
                            String fieldId = getArguments().getString(DetailFieldFragment.BUNDLE_FIELD_ID);
                            String sportId = ""; //TODO cambiar
                            mPresenter.voteField(fieldId, sportId, ratingBar.getRating());
                        }
                    })
                    .setNegativeButton("Cancelar", null);
            builder.create().show();
            return true;
        } else
            return true;
//        return false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_detail_field, container, false);
        ButterKnife.bind(this, root);

        sportsAdapter = new ProfileSportsAdapter(null);
        fieldSportList.setAdapter(sportsAdapter);
        fieldSportList.setHasFixedSize(true);
        fieldSportList.setLayoutManager(new LinearLayoutManager(getActivityContext(), LinearLayoutManager.HORIZONTAL, false));

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mFragmentManagementListener.setCurrentDisplayedFragment("Detalles de pista", this);
        mActionBarIconManagementListener.setToolbarAsUp();
    }

    @Override
    public void onStart() {
        super.onStart();
        mPresenter.openField(getLoaderManager(), getArguments());
    }

    @Override
    public void showFieldId(String id) {
        ((BaseActivity)getActivity()).showContent();
        this.textViewFieldId.setText(id);
    }

    @Override
    public void showFieldName(String name) {
        ((BaseActivity)getActivity()).showContent();
        this.textViewFieldName.setText(name);
        mFragmentManagementListener.setActionBarTitle(name);
    }

    @Override
    public void showFieldPlace(String address, String city, LatLng coordinates) {
        ((BaseActivity)getActivity()).showContent();
        this.textViewFieldAddress.setText(address);
    }

    @Override
    public void showFieldTimes(long openTime, long closeTime) {
        ((BaseActivity)getActivity()).showContent();
        //TODO time = -36000000 ???
        this.textViewFieldOpening.setText(UtilesTime.millisToTimeString(openTime));
        this.textViewFieldClosing.setText(UtilesTime.millisToTimeString(closeTime));
    }

    @Override
    public void showSportCourts(Cursor cursor) {
        sportsAdapter.replaceData(cursor);
        if (cursor != null && cursor.getCount() > 0) {
            fieldSportList.setVisibility(View.VISIBLE);
            fieldSportPlaceholder.setVisibility(View.INVISIBLE);
        } else {
            fieldSportList.setVisibility(View.INVISIBLE);
            fieldSportPlaceholder.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void showFieldCreator(String userId) {
        //TODO show in UI
        String myUid = Utiles.getCurrentUserId();
        if (TextUtils.isEmpty(myUid)) return;
        if (myUid.equals(userId)) {
            fieldEditSportListButton.setVisibility(View.VISIBLE);
            fieldEditSportListButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Fragment fragment = SportsListFragment.newInstance(textViewFieldId.getText().toString(), sportsAdapter.getDataAsArrayList());
                    mFragmentManagementListener.initFragment(fragment, true);
                }
            });
        }
    }

    @Override
    public void clearUI() {
        this.textViewFieldId.setText("");
        this.textViewFieldName.setText("");
        this.mFragmentManagementListener.setActionBarTitle("");
        this.textViewFieldAddress.setText("");
        this.textViewFieldRating.setText("");
        this.textViewFieldSport.setText("");
        this.textViewFieldOpening.setText("");
        this.textViewFieldClosing.setText("");
    }

    @Override
    public void onPause() {
        super.onPause();
        sportsAdapter.replaceData(null);
    }
}
