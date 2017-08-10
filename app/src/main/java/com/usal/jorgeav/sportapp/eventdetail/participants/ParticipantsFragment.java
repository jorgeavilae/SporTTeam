package com.usal.jorgeav.sportapp.eventdetail.participants;

import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.usal.jorgeav.sportapp.BaseFragment;
import com.usal.jorgeav.sportapp.R;
import com.usal.jorgeav.sportapp.adapters.SimulatedUsersAdapter;
import com.usal.jorgeav.sportapp.adapters.UsersAdapter;
import com.usal.jorgeav.sportapp.eventdetail.DetailEventPresenter;
import com.usal.jorgeav.sportapp.eventdetail.simulateparticipant.SimulateParticipantFragment;
import com.usal.jorgeav.sportapp.mainactivities.EventsActivity;
import com.usal.jorgeav.sportapp.profile.ProfileFragment;
import com.usal.jorgeav.sportapp.utils.Utiles;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Jorge Avila on 29/05/2017.
 */

public class ParticipantsFragment extends BaseFragment implements ParticipantsContract.View, SimulatedUsersAdapter.OnSimulatedUserItemClickListener, UsersAdapter.OnUserItemClickListener {
    private static final String TAG = ParticipantsFragment.class.getSimpleName();
    public static final String BUNDLE_EVENT_ID = "BUNDLE_EVENT_ID";
    public static final String BUNDLE_OWNER_ID = "BUNDLE_OWNER_ID";
    public static final String BUNDLE_RELATION_TYPE = "BUNDLE_RELATION_TYPE";
    public static final String BUNDLE_IS_PAST = "BUNDLE_IS_PAST";
    public static final String BUNDLE_IS_FULL = "BUNDLE_IS_FULL";

    private static String mEventId = "";
    @DetailEventPresenter.EventRelationType
    private static int mRelation = -1;
    private static Boolean isPast = null;
    private static Boolean isFull = null;
    ParticipantsContract.Presenter mParticipantsPresenter;

    UsersAdapter mParticipantsAdapter;
    @BindView(R.id.event_participants_list)
    RecyclerView participantsList;
    @BindView(R.id.event_participants_placeholder)
    ConstraintLayout participantsPlaceholder;

    SimulatedUsersAdapter mSimulatedParticipantsAdapter;
    @BindView(R.id.event_simulated_participants_list)
    RecyclerView simulatedParticipantsList;
    @BindView(R.id.event_simulated_participants_placeholder)
    ConstraintLayout simulatedParticipantsPlaceholder;
    @BindView(R.id.event_participants_add_simulated)
    ImageView addSimulatedParticipant;

    public ParticipantsFragment() {
        // Required empty public constructor
    }

    public static ParticipantsFragment newInstance(@NonNull String eventID, @NonNull String ownerId,
                                                   @DetailEventPresenter.EventRelationType int relation,
                                                   @NonNull Boolean isPast, @NonNull Boolean isFull) {
        Bundle args = new Bundle();
        args.putString(BUNDLE_EVENT_ID, eventID);
        args.putString(BUNDLE_OWNER_ID, ownerId);
        args.putInt(BUNDLE_RELATION_TYPE, relation);
        args.putBoolean(BUNDLE_IS_PAST, isPast);
        args.putBoolean(BUNDLE_IS_FULL, isFull);
        ParticipantsFragment fragment = new ParticipantsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mParticipantsPresenter = new ParticipantsPresenter(this);
        mParticipantsAdapter = new UsersAdapter(null, this, Glide.with(this));
        mSimulatedParticipantsAdapter = new SimulatedUsersAdapter(null, this, Glide.with(this));
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_participants, container, false);
        ButterKnife.bind(this, root);

        if (getArguments() != null && getArguments().containsKey(BUNDLE_EVENT_ID))
            mEventId = getArguments().getString(BUNDLE_EVENT_ID);
        // OwnerID is accessed in the Presenter
        if (getArguments() != null && getArguments().containsKey(BUNDLE_RELATION_TYPE))
            mRelation = parseRelation(getArguments().getInt(BUNDLE_RELATION_TYPE));
        if (getArguments() != null && getArguments().containsKey(BUNDLE_IS_PAST))
            isPast = getArguments().getBoolean(BUNDLE_IS_PAST);
        if (getArguments() != null && getArguments().containsKey(BUNDLE_IS_FULL))
            isFull = getArguments().getBoolean(BUNDLE_IS_FULL);

        // Set participants list
        participantsList.setAdapter(mParticipantsAdapter);
        participantsList.setHasFixedSize(true);
        participantsList.setLayoutManager(new GridLayoutManager(getActivityContext(),
                Utiles.calculateNoOfColumns(getActivityContext())));

        // Set simulated participants list
        simulatedParticipantsList.setAdapter(mSimulatedParticipantsAdapter);
        simulatedParticipantsList.setHasFixedSize(true);
        simulatedParticipantsList.setLayoutManager(new LinearLayoutManager(getActivityContext(),
                LinearLayoutManager.VERTICAL, false));

        //Allow add simulated participant if isn't Past and isn't Full and if I am owner or participant
        setLayoutToAllowAddSimulatedParticipants(isPast != null && !isPast && isFull != null && !isFull
                &&(mRelation == DetailEventPresenter.RELATION_TYPE_ASSISTANT
                || mRelation == DetailEventPresenter.RELATION_TYPE_OWNER));

        return root;
    }

    private void setLayoutToAllowAddSimulatedParticipants(boolean allow) {
        if(allow) {
            addSimulatedParticipant.setVisibility(View.VISIBLE);
            addSimulatedParticipant.setEnabled(true);
            addSimulatedParticipant.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mEventId != null && getActivity() instanceof EventsActivity) { //Necessary for photo picker
                        Fragment fragment = SimulateParticipantFragment.newInstance(mEventId);
                        mFragmentManagementListener.initFragment(fragment, true);
                    } else
                        Log.e(TAG, "addSimulateParticipant: onClick: eventId " + mEventId
                                + "\ngetActivity is EventsActivity? " + (getActivity() instanceof EventsActivity));
                }
            });
        } else {
            addSimulatedParticipant.setVisibility(View.INVISIBLE);
            addSimulatedParticipant.setEnabled(false);
            addSimulatedParticipant.setOnClickListener(null);
        }
    }

    private
    @DetailEventPresenter.EventRelationType
    int parseRelation(int relation) {
        switch (relation) {
            default: case -1: return DetailEventPresenter.RELATION_TYPE_ERROR;
            case 0: return DetailEventPresenter.RELATION_TYPE_NONE;
            case 1: return DetailEventPresenter.RELATION_TYPE_OWNER;
            case 2: return DetailEventPresenter.RELATION_TYPE_I_SEND_REQUEST;
            case 3: return DetailEventPresenter.RELATION_TYPE_I_RECEIVE_INVITATION;
            case 4: return DetailEventPresenter.RELATION_TYPE_ASSISTANT;
            case 5: return DetailEventPresenter.RELATION_TYPE_BLOCKED;
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mFragmentManagementListener.setCurrentDisplayedFragment(getString(R.string.event_participants), this);
        mActionBarIconManagementListener.setToolbarAsUp();
    }

    @Override
    public void onStart() {
        super.onStart();
        mParticipantsPresenter.loadParticipants(getLoaderManager(), getArguments());
        mParticipantsPresenter.loadSimulatedParticipants(getLoaderManager(), getArguments());
    }

    @Override
    public void onPause() {
        super.onPause();
        mParticipantsAdapter.replaceData(null);
        mSimulatedParticipantsAdapter.replaceData(null);
    }

    @Override
    public void showParticipants(Cursor cursor) {
        mParticipantsAdapter.replaceData(cursor);
        if (cursor != null && cursor.getCount() > 0) {
            participantsList.setVisibility(View.VISIBLE);
            participantsPlaceholder.setVisibility(View.INVISIBLE);
        } else {
            participantsList.setVisibility(View.INVISIBLE);
            participantsPlaceholder.setVisibility(View.VISIBLE);
        }
        mFragmentManagementListener.showContent();
    }

    @Override
    public void showSimulatedParticipants(Cursor cursor) {
        mSimulatedParticipantsAdapter.replaceData(cursor);
        if (cursor != null && cursor.getCount() > 0) {
            simulatedParticipantsList.setVisibility(View.VISIBLE);
            simulatedParticipantsPlaceholder.setVisibility(View.INVISIBLE);
        } else {
            simulatedParticipantsList.setVisibility(View.INVISIBLE);
            simulatedParticipantsPlaceholder.setVisibility(View.VISIBLE);
        }
        mFragmentManagementListener.showContent();
    }

    @Override
    public void onUserClick(String uid) {
        String myUid = Utiles.getCurrentUserId();
        if (TextUtils.isEmpty(myUid)) return;

        if (!myUid.equals(uid)) {
            Fragment newFragment = ProfileFragment.newInstance(uid);
            mFragmentManagementListener.initFragment(newFragment, true);
        }
    }

    @Override
    public boolean onUserLongClick(final String uid) {
        String myUid = Utiles.getCurrentUserId();
        if (TextUtils.isEmpty(myUid)) return false;

        if (!myUid.equals(uid) && mRelation == DetailEventPresenter.RELATION_TYPE_OWNER && !isPast) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivityContext())
                    .setMessage(R.string.dialog_msg_quit_user_from_event)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivityContext())
                                    .setMessage(R.string.dialog_msg_quit_user_from_event_sim_user)
                                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            mParticipantsPresenter.quitEvent(uid, mEventId, true);
                                        }
                                    })
                                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            mParticipantsPresenter.quitEvent(uid, mEventId, false);
                                        }
                                    });
                            builder.create().show();
                        }
                    })
                    .setNegativeButton(android.R.string.no, null)
                    .setNeutralButton(R.string.see_details, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Fragment newFragment = ProfileFragment.newInstance(uid);
                            mFragmentManagementListener.initFragment(newFragment, true);
                        }
                    });
            builder.create().show();
            return true;
        }
        return false;
    }

    @Override
    public void onSimulatedUserClick(String simulatedUserCreator, final String simulatedUserId) {
        String myUid = Utiles.getCurrentUserId();
        if (TextUtils.isEmpty(myUid)) return;

        if ((myUid.equals(simulatedUserCreator) || mRelation == DetailEventPresenter.RELATION_TYPE_OWNER) && !isPast) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivityContext())
                    .setMessage(R.string.dialog_msg_quit_sim_user_from_event)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            mParticipantsPresenter.deleteSimulatedUser(simulatedUserId, mEventId);
                        }
                    })
                    .setNegativeButton(android.R.string.no, null);
            builder.create().show();
        }
    }
}
