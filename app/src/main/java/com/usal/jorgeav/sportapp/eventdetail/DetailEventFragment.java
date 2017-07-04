package com.usal.jorgeav.sportapp.eventdetail;


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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.usal.jorgeav.sportapp.BaseFragment;
import com.usal.jorgeav.sportapp.R;
import com.usal.jorgeav.sportapp.adapters.UsersAdapter;
import com.usal.jorgeav.sportapp.eventdetail.inviteuser.InviteUserFragment;
import com.usal.jorgeav.sportapp.eventdetail.unansweredinvitation.InvitationsSentFragment;
import com.usal.jorgeav.sportapp.eventdetail.userrequests.UsersRequestsFragment;
import com.usal.jorgeav.sportapp.events.addevent.NewEventFragment;
import com.usal.jorgeav.sportapp.fields.detail.DetailFieldFragment;
import com.usal.jorgeav.sportapp.mainactivities.BaseActivity;
import com.usal.jorgeav.sportapp.profile.ProfileFragment;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailEventFragment extends BaseFragment implements DetailEventContract.View, UsersAdapter.OnUserItemClickListener {
    private static final String TAG = DetailEventFragment.class.getSimpleName();
    public static final String BUNDLE_EVENT_ID = "BUNDLE_EVENT_ID";

    private static String mEventId = "";
    @DetailEventPresenter.EventRelationType int mRelation;
    private DetailEventContract.Presenter mPresenter;

    Menu mMenu;
    @BindView(R.id.event_detail_id)
    TextView textViewEventId;
    @BindView(R.id.event_detail_sport)
    TextView textViewEventSport;
    @BindView(R.id.event_detail_place)
    Button buttonEventPlace;
    @BindView(R.id.event_detail_name)
    TextView textViewEventName;
    @BindView(R.id.event_detail_date)
    TextView textViewEventDate;
    @BindView(R.id.event_detail_owner)
    TextView textViewEventOwner;
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
    @BindView(R.id.event_detail_participants_placeholder)
    ConstraintLayout eventParticipantsPlaceholder;

    public DetailEventFragment() {
        // Required empty public constructor
    }

    public static DetailEventFragment newInstance(@NonNull String eventId) {
        DetailEventFragment fragment = new DetailEventFragment();
        Bundle args = new Bundle();
        args.putString(BUNDLE_EVENT_ID, eventId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        mPresenter = new DetailEventPresenter(this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        mMenu = menu;
        super.onCreateOptionsMenu(mMenu, inflater);
        mMenu.clear();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if (item.getItemId() == R.id.action_edit) {
            Log.d(TAG, "onOptionsItemSelected: Edit");
            Fragment fragment = NewEventFragment.newInstance(mEventId);
            mFragmentManagementListener.initFragment(fragment, true);
            return true;
        } else if (item.getItemId() == R.id.action_delete) {
            Log.d(TAG, "onOptionsItemSelected: Delete");
            mPresenter.deleteEvent(getArguments());
            return true;
        }
        return false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_detail_event, container, false);
        ButterKnife.bind(this, root);

        if (getArguments() != null && getArguments().containsKey(BUNDLE_EVENT_ID))
            mEventId = getArguments().getString(BUNDLE_EVENT_ID);

        usersAdapter = new UsersAdapter(null, this);
        eventParticipantsList.setAdapter(usersAdapter);
        eventParticipantsList.setHasFixedSize(true);
        eventParticipantsList.setLayoutManager(new LinearLayoutManager(getActivityContext(), LinearLayoutManager.VERTICAL, false));

        mPresenter.getRelationTypeBetweenThisEventAndI();

        return root;
    }

    @Override
    public void uiSetupForEventRelation(@DetailEventPresenter.EventRelationType int relation) {
        mRelation = relation;
        switch (relation) {
            case DetailEventPresenter.RELATION_TYPE_OWNER:
                if (mMenu != null) getActivity().getMenuInflater().inflate(R.menu.menu_edit_delete, mMenu);
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
                        Fragment fragment = InviteUserFragment.newInstance(mEventId);
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
                break;
            case DetailEventPresenter.RELATION_TYPE_NONE:
                if (mMenu != null) mMenu.clear();
                buttonSendRequest.setVisibility(View.VISIBLE);
                buttonSendRequest.setText("Enviar peticion de entrada");
                buttonSendRequest.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        /*Enviar peticion de entrada*/
                        mPresenter.sendEventRequest(mEventId);
                    }
                });
                break;
            case DetailEventPresenter.RELATION_TYPE_I_SEND_REQUEST:
                if (mMenu != null) mMenu.clear();
                buttonSendRequest.setVisibility(View.VISIBLE);
                buttonSendRequest.setText("Cancelar peticion de entrada");
                buttonSendRequest.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        /*Canncelar peticion de entrada*/
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivityContext());
                        builder.setMessage("Seguro de que quieres eliminar la peticion?")
                                .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        mPresenter.cancelEventRequest(mEventId);
                                    }
                                })
                                .setNegativeButton("No", null);
                        builder.create().show();
                    }
                });
                break;
            case DetailEventPresenter.RELATION_TYPE_I_RECEIVE_INVITATION:
                if (mMenu != null) mMenu.clear();
                buttonSendRequest.setVisibility(View.VISIBLE);
                buttonSendRequest.setText("Contestar invitacion");
                buttonSendRequest.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        /*Contestar invitacion*/
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivityContext());
                        builder.setMessage("Quieres asistir a este evento?")
                                .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        mPresenter.acceptEventInvitation(mEventId);
                                    }
                                })
                                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        mPresenter.declineEventInvitation(mEventId);
                                    }
                                });
                        builder.create().show();
                    }
                });
                break;
            case DetailEventPresenter.RELATION_TYPE_ASSISTANT:
                if (mMenu != null) mMenu.clear();
                buttonSendRequest.setVisibility(View.VISIBLE);
                buttonSendRequest.setText("Abandonar partido");
                buttonSendRequest.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        /*Abandonar partido*/
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivityContext());
                        builder.setMessage("Quieres abandonar este partido?")
                                .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        mPresenter.quitEvent(mEventId);
                                    }
                                })
                                .setNegativeButton("No", null);
                        builder.create().show();
                    }
                });
                break;
            case DetailEventPresenter.RELATION_TYPE_BLOCKED:
                if (mMenu != null) mMenu.clear();
                buttonSendRequest.setVisibility(View.VISIBLE);
                buttonSendRequest.setText("No puedes asistir");
                buttonSendRequest.setEnabled(false);
                break;
            case DetailEventPresenter.RELATION_TYPE_ERROR:
                if (mMenu != null) mMenu.clear();
                buttonSendRequest.setVisibility(View.VISIBLE);
                buttonSendRequest.setText("Error");
                break;
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mFragmentManagementListener.setCurrentDisplayedFragment("Detalles de evento", this);
        mActionBarIconManagementListener.setToolbarAsUp();
        mPresenter.openEvent(getLoaderManager(), getArguments());
    }

    @Override
    public void showEventId(String id) {
        ((BaseActivity)getActivity()).showContent();
        this.textViewEventId.setText(id);
    }

    @Override
    public void showEventSport(String sport) {
        ((BaseActivity)getActivity()).showContent();
        this.textViewEventSport.setText(sport);

    }

    @Override
    public void showEventPlace(final String place, final String sport) {
        ((BaseActivity)getActivity()).showContent();
        this.buttonEventPlace.setText(place);
        buttonEventPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment newFragment = DetailFieldFragment.newInstance(place, sport);
                mFragmentManagementListener.initFragment(newFragment, true);
            }
        });
    }

    @Override
    public void showEventName(String name) {
        ((BaseActivity)getActivity()).showContent();
        this.textViewEventName.setText(name);
        mFragmentManagementListener.setActionBarTitle(name);
    }

    @Override
    public void showEventDate(String date) {
        ((BaseActivity)getActivity()).showContent();
        this.textViewEventDate.setText(date);
    }

    @Override
    public void showEventOwner(String owner) {
        ((BaseActivity)getActivity()).showContent();
        this.textViewEventOwner.setText(owner);

    }

    @Override
    public void showEventTotalPlayers(int totalPlayers) {
        if(totalPlayers > -1) {
            ((BaseActivity) getActivity()).showContent();
            this.textViewEventTotal.setText(String.format(Locale.getDefault(), "%2d", totalPlayers));
        }

    }

    @Override
    public void showEventEmptyPlayers(int emptyPlayers) {
        if(emptyPlayers > -1) {
            if (mRelation == DetailEventPresenter.RELATION_TYPE_NONE && emptyPlayers == 0)
                buttonSendRequest.setEnabled(false);
            ((BaseActivity) getActivity()).showContent();
            this.textViewEventEmpty.setText(String.format(Locale.getDefault(), "%2d", emptyPlayers));
        }
    }

    @Override
    public void showParticipants(Cursor cursor) {
        usersAdapter.replaceData(cursor);
        if (cursor != null && cursor.getCount() > 0) {
            eventParticipantsList.setVisibility(View.VISIBLE);
            eventParticipantsPlaceholder.setVisibility(View.INVISIBLE);
        } else {
            eventParticipantsList.setVisibility(View.INVISIBLE);
            eventParticipantsPlaceholder.setVisibility(View.VISIBLE);
        }
        mFragmentManagementListener.showContent();
    }

    @Override
    public void clearUI() {
        this.textViewEventId.setText("");
        this.textViewEventSport.setText("");
        this.buttonEventPlace.setText("");
        this.buttonEventPlace.setOnClickListener(null);
        this.textViewEventName.setText("");
        this.mFragmentManagementListener.setActionBarTitle("");
        this.textViewEventDate.setText("");
        this.textViewEventOwner.setText("");
        this.textViewEventTotal.setText("");
        this.textViewEventEmpty.setText("");
    }

    @Override
    public String getEventID() {
        return mEventId;
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.registerUserRelationObserver();
        /* https://stackoverflow.com/a/17063800/4235666 */
        setMenuVisibility(true);
    }

    @Override
    public void onPause() {
        super.onPause();
        mPresenter.unregisterUserRelationObserver();
        usersAdapter.replaceData(null);
    }

    @Override
    public void onUserClick(final String uid) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivityContext());
        builder.setMessage("Quieres expulsarlo del evento?")
                .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mPresenter.quitEvent(uid, mEventId);
                    }
                })
                .setNegativeButton("No", null)
                .setNeutralButton("See details", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Fragment newFragment = ProfileFragment.newInstance(uid);
                        mFragmentManagementListener.initFragment(newFragment, true);
                    }
                });
        builder.create().show();
    }
}
