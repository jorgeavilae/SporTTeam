package com.usal.jorgeav.sportapp.eventdetail;


import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.usal.jorgeav.sportapp.ActivityContracts;
import com.usal.jorgeav.sportapp.MainActivity;
import com.usal.jorgeav.sportapp.R;
import com.usal.jorgeav.sportapp.adapters.UsersAdapter;
import com.usal.jorgeav.sportapp.data.provider.SportteamContract;
import com.usal.jorgeav.sportapp.eventdetail.sendinvitation.SendInvitationFragment;
import com.usal.jorgeav.sportapp.eventdetail.unansweredinvitation.InvitationsSentFragment;
import com.usal.jorgeav.sportapp.eventdetail.userrequests.UsersRequestsFragment;
import com.usal.jorgeav.sportapp.fields.detail.DetailFieldFragment;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailEventFragment extends Fragment implements DetailEventContract.View {
    private static final String TAG = DetailEventFragment.class.getSimpleName();
    public static final String BUNDLE_EVENT_ID = "BUNDLE_EVENT_ID";
    public static final int LOADER_EVENT_ID = 11000;
    public static final int LOADER_EVENTS_PARTICIPANTS_ID = 11001;
    public static final int LOADER_USER_DATA_FROM_PARTICIPANTS_ID = 11002;

    private static String mEventId = "";
    private boolean isMyEvent = false;
    private DetailEventContract.Presenter mPresenter;
    private ActivityContracts.ActionBarIconManagement mActionBarIconManagementListener;
    private ActivityContracts.FragmentManagement mFragmentManagementListener;

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
    @BindView(R.id.event_detail_participants_list)
    RecyclerView eventParticipantsList;
    UsersAdapter usersAdapter;

    public DetailEventFragment() {
        // Required empty public constructor
    }

    public static DetailEventFragment newInstance(String eventId) {
        DetailEventFragment fragment = new DetailEventFragment();
        Bundle args = new Bundle();
        args.putString(BUNDLE_EVENT_ID, eventId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPresenter = new DetailEventPresenter(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_detail_event, container, false);
        ButterKnife.bind(this, root);

        if (getArguments() != null && getArguments().containsKey(BUNDLE_EVENT_ID)) {
            mEventId = getArguments().getString(BUNDLE_EVENT_ID);
            if (mEventId != null) {
                Cursor c = getActivity().getContentResolver().query(
                        SportteamContract.EventEntry.CONTENT_EVENT_URI,
                        SportteamContract.EventEntry.EVENT_COLUMNS,
                        SportteamContract.EventEntry.EVENT_ID + " = ?",
                        new String[]{mEventId},
                        null);
                if (c != null && c.moveToFirst()) {
                    String ownerId = c.getString(SportteamContract.EventEntry.COLUMN_OWNER);
                    isMyEvent = FirebaseAuth.getInstance().getCurrentUser().getUid().equals(ownerId);
                    c.close();
                } else
                    isMyEvent = false;
            }
        }

        usersAdapter = new UsersAdapter(null, null);
        eventParticipantsList.setAdapter(usersAdapter);
        eventParticipantsList.setHasFixedSize(true);
        eventParticipantsList.setLayoutManager(new LinearLayoutManager(getActivityContext(), LinearLayoutManager.VERTICAL, false));

        if (isMyEvent) {
            buttonUserRequests.setVisibility(View.VISIBLE);
            buttonUserRequests.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //DONE ver peticiones para entrar en este evento
                    if(mEventId != null) {
                        Fragment fragment = UsersRequestsFragment.newInstance(mEventId);
                        mFragmentManagementListener.initFragment(fragment, true);
                    }
                }
            });
            buttonSendInvitation.setVisibility(View.VISIBLE);
            buttonSendInvitation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //DONE ver lista de amigos para enviarles invitaciones
                    Fragment fragment = SendInvitationFragment.newInstance();
                    mFragmentManagementListener.initFragment(fragment, true);
                }
            });
            buttonUnansweredInvitations.setVisibility(View.VISIBLE);
            buttonUnansweredInvitations.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //DONE ver invitaciones enviadas y no contestadas
                    if(mEventId != null) {
                        Fragment fragment = InvitationsSentFragment.newInstance(mEventId);
                        mFragmentManagementListener.initFragment(fragment, true);
                    }
                }
            });
        } else {
            buttonSendRequest.setVisibility(View.VISIBLE);
            buttonSendRequest.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //TODO enviar peticion para participar
                }
            });
        }

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //TODO should I add event.name?
        mFragmentManagementListener.setCurrentDisplayedFragment(mEventId, this);
        mActionBarIconManagementListener.setToolbarAsUp();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ActivityContracts.FragmentManagement)
            mFragmentManagementListener = (ActivityContracts.FragmentManagement) context;
        if (context instanceof ActivityContracts.ActionBarIconManagement)
            mActionBarIconManagementListener = (ActivityContracts.ActionBarIconManagement) context;
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
        Bundle b = new Bundle();
        b.putString(BUNDLE_EVENT_ID, mEventId);
        getLoaderManager().initLoader(LOADER_EVENT_ID, b, mPresenter.getLoaderInstance());
        getLoaderManager().initLoader(LOADER_EVENTS_PARTICIPANTS_ID, b, mPresenter.getLoaderInstance());
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
                if (c != null && c.moveToFirst()) {
                    String fieldId = c.getString(SportteamContract.FieldEntry.COLUMN_FIELD_ID);
                    String sportId = c.getString(SportteamContract.FieldEntry.COLUMN_SPORT);
                    Fragment newFragment = DetailFieldFragment.newInstance(fieldId, sportId);
                    mFragmentManagementListener.initFragment(newFragment, true);
                    c.close();
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
        if(totalPlayers > -1) {
            ((MainActivity) getActivity()).showContent();
            this.textViewEventTotal.setText(String.format(Locale.getDefault(), "%2d", totalPlayers));
        }

    }

    @Override
    public void showEventEmptyPlayers(int emptyPlayers) {
        if(emptyPlayers > -1) {
            ((MainActivity) getActivity()).showContent();
            this.textViewEventEmpty.setText(String.format(Locale.getDefault(), "%2d", emptyPlayers));
        }

    }

    @Override
    public void showParticipants(Cursor cursor) {
        usersAdapter.replaceData(cursor);
    }

    @Override
    public Context getActivityContext() {
        return getActivity();
    }

    @Override
    public Fragment getThis() {
        return this;
    }
}
