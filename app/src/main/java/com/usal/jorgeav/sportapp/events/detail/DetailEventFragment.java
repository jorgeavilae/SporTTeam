package com.usal.jorgeav.sportapp.events.detail;


import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.usal.jorgeav.sportapp.MainActivity;
import com.usal.jorgeav.sportapp.MainActivityContract;
import com.usal.jorgeav.sportapp.R;
import com.usal.jorgeav.sportapp.data.Event;
import com.usal.jorgeav.sportapp.data.Field;
import com.usal.jorgeav.sportapp.data.provider.SportteamContract;
import com.usal.jorgeav.sportapp.fields.detail.DetailFieldFragment;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailEventFragment extends Fragment implements DetailEventContract.View {
    private static final String TAG = DetailEventFragment.class.getSimpleName();

    private static final String ARG_EVENT = "param-event";

    private Event mEvent = null;
    private boolean isMyEvent = false;
    private DetailEventContract.Presenter mPresenter;
    private MainActivityContract.ActionBarIconManagement mActionBarIconManagementListener;
    private MainActivityContract.FragmentManagement mFragmentManagementListener;

    @BindView(R.id.event_detail_id)
    TextView textViewEventId;
    @BindView(R.id.event_detail_sport)
    TextView textViewEventSport;
    @BindView(R.id.event_detail_place)
    Button buttonEventPlace;
    @BindView(R.id.event_detail_date)
    TextView textViewEventDate;
    @BindView(R.id.event_detail_time)
    TextView textViewEventTime;
    @BindView(R.id.event_detail_total)
    TextView textViewEventTotal;
    @BindView(R.id.event_detail_empty)
    TextView textViewEventEmpty;
    @BindView(R.id.event_detail_user_requests)
    Button buttonUserRequests;
    @BindView(R.id.event_detail_send_invitation)
    Button buttonSendInvitation;
    @BindView(R.id.event_detail_unanswered_invitations)
    Button buttonUnansweredInvitations;
    @BindView(R.id.event_detail_send_request)
    Button buttonSendRequest;


    public DetailEventFragment() {
        // Required empty public constructor
    }

    public static DetailEventFragment newInstance(Event event) {
        DetailEventFragment fragment = new DetailEventFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_EVENT, event);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mEvent = getArguments().getParcelable(ARG_EVENT);
            if (mEvent != null) {
                isMyEvent = FirebaseAuth.getInstance().getCurrentUser().getUid().equals(mEvent.getmOwner());
            }
        }

        mPresenter = new DetailEventPresenter(mEvent, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_detail_event, container, false);
        ButterKnife.bind(this, root);

        if (isMyEvent) {
            buttonUserRequests.setVisibility(View.VISIBLE);
            buttonSendInvitation.setVisibility(View.VISIBLE);
            buttonUnansweredInvitations.setVisibility(View.VISIBLE);
        } else {
            buttonSendRequest.setVisibility(View.VISIBLE);
        }

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //TODO should I add event.name?
        mFragmentManagementListener.setCurrentDisplayedFragment(mEvent.getmId(), this);
        mActionBarIconManagementListener.setToolbarAsUp();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MainActivityContract.FragmentManagement)
            mFragmentManagementListener = (MainActivityContract.FragmentManagement) context;
        if (context instanceof MainActivityContract.ActionBarIconManagement)
            mActionBarIconManagementListener = (MainActivityContract.ActionBarIconManagement) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mFragmentManagementListener = null;
        mActionBarIconManagementListener = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.openEvent();
    }

    @Override
    public void showEventId(String id) {
        ((MainActivity)getActivity()).showContent();
        this.textViewEventId.setText(id);
    }

    @Override
    public void showEventSport(String sport) {
        ((MainActivity)getActivity()).showContent();
        this.textViewEventSport.setText(sport);

    }

    @Override
    public void showEventPlace(String place) {
        ((MainActivity)getActivity()).showContent();
        this.buttonEventPlace.setText(place);
        buttonEventPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Cursor c = getActivity().getContentResolver().query(
                        SportteamContract.FieldEntry.CONTENT_FIELD_URI,
                        SportteamContract.FieldEntry.FIELDS_COLUMNS,
                        SportteamContract.FieldEntry.FIELD_ID + " = ? AND " + SportteamContract.FieldEntry.SPORT +" = ? ",
                        new String[]{buttonEventPlace.getText().toString(), textViewEventSport.getText().toString()},
                        null);
                Log.d(TAG, "onClick: field query "+
                                SportteamContract.FieldEntry.FIELD_ID + " = ? AND " + SportteamContract.FieldEntry.SPORT +" = ? "+
                        buttonEventPlace.getText().toString()+" "+ textViewEventSport.getText().toString());
                //TODO error c.count == 0 es porque al cargar el event en el CP no se trae el field de Firebase
                if (c != null && c.moveToFirst()) {
                    Field field = new Field(
                            c.getString(SportteamContract.FieldEntry.COLUMN_FIELD_ID),
                            c.getString(SportteamContract.FieldEntry.COLUMN_NAME),
                            c.getString(SportteamContract.FieldEntry.COLUMN_SPORT),
                            c.getString(SportteamContract.FieldEntry.COLUMN_ADDRESS),
                            c.getString(SportteamContract.FieldEntry.COLUMN_CITY),
                            c.getFloat(SportteamContract.FieldEntry.COLUMN_PUNCTUATION),
                            c.getInt(SportteamContract.FieldEntry.COLUMN_VOTES),
                            c.getLong(SportteamContract.FieldEntry.COLUMN_OPENING_TIME),
                            c.getLong(SportteamContract.FieldEntry.COLUMN_CLOSING_TIME));
                    Fragment newFragment = DetailFieldFragment.newInstance(field);
                    mFragmentManagementListener.initFragment(newFragment, true);
                }
            }
        });

    }

    @Override
    public void showEventDate(String date) {
        ((MainActivity)getActivity()).showContent();
        this.textViewEventDate.setText(date);

    }

    @Override
    public void showEventOwner(String owner) {
        ((MainActivity)getActivity()).showContent();
        this.textViewEventTime.setText(owner);

    }

    @Override
    public void showEventTotalPlayers(int totalPlayers) {
        ((MainActivity)getActivity()).showContent();
        this.textViewEventTotal.setText(String.format(Locale.getDefault(), "%2d", totalPlayers));

    }

    @Override
    public void showEventEmptyPlayers(int emptyPlayers) {
        ((MainActivity)getActivity()).showContent();
        this.textViewEventEmpty.setText(String.format(Locale.getDefault(), "%2d", emptyPlayers));

    }
}
