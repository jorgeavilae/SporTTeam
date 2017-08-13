package com.usal.jorgeav.sportapp.eventdetail;


import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.usal.jorgeav.sportapp.BaseFragment;
import com.usal.jorgeav.sportapp.R;
import com.usal.jorgeav.sportapp.data.Field;
import com.usal.jorgeav.sportapp.eventdetail.inviteuser.InviteUserFragment;
import com.usal.jorgeav.sportapp.eventdetail.participants.ParticipantsFragment;
import com.usal.jorgeav.sportapp.eventdetail.unansweredinvitation.InvitationsSentFragment;
import com.usal.jorgeav.sportapp.eventdetail.userrequests.UsersRequestsFragment;
import com.usal.jorgeav.sportapp.events.addevent.NewEventFragment;
import com.usal.jorgeav.sportapp.fields.detail.DetailFieldFragment;
import com.usal.jorgeav.sportapp.mainactivities.BaseActivity;
import com.usal.jorgeav.sportapp.utils.Utiles;
import com.usal.jorgeav.sportapp.utils.UtilesContentProvider;
import com.usal.jorgeav.sportapp.utils.UtilesTime;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailEventFragment extends BaseFragment implements DetailEventContract.View {
    @SuppressWarnings("unused")
    private static final String TAG = DetailEventFragment.class.getSimpleName();
    public static final String BUNDLE_EVENT_ID = "BUNDLE_EVENT_ID";

    private String mEventId = "";
    private String mSportId = "";
    private String mOwnerId = "";
    private Boolean isFull = null;
    private Boolean isPast = null;
    @DetailEventPresenter.EventRelationType int mRelation = -1;

    private DetailEventContract.Presenter mPresenter;

    Menu mMenu;

    @BindView(R.id.event_detail_map)
    MapView detailEventMap;
    private GoogleMap mMap;
    @BindView(R.id.event_detail_sport)
    ImageView detailEventSport;
    @BindView(R.id.event_detail_place)
    TextView detailEventPlace;
    @BindView(R.id.event_detail_place_icon)
    ImageView detailEventPlaceIcon;
    @BindView(R.id.event_detail_date)
    TextView detailEventDate;

    @BindView(R.id.event_detail_participants_container)
    CardView detailEventParticipantsContainer;
    @BindView(R.id.event_detail_players_proportion)
    ImageView detailEventProportion;
    @BindView(R.id.event_detail_total)
    TextView detailEventTotal;
    @BindView(R.id.event_detail_empty)
    TextView detailEventEmpty;
    @BindView(R.id.event_detail_state)
    Button detailEventStateButton;

    @BindView(R.id.event_detail_owner)
    TextView detailEventOwner;

    @BindView(R.id.event_detail_user_requests)
    LinearLayout detailEventRequests;
    @BindView(R.id.event_detail_send_invitation)
    LinearLayout detailEventInvitation;

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
            Fragment fragment = NewEventFragment.newInstance(mEventId, mSportId);
            mFragmentManagementListener.initFragment(fragment, true);
            return true;
        } else if (item.getItemId() == R.id.action_delete) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivityContext())
                    .setTitle(R.string.dialog_msg_are_you_sure)
                    .setMessage(R.string.dialog_msg_delete_event)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            mPresenter.deleteEvent(getArguments());
                            ((BaseActivity)getActivity()).hideContent();
                        }
                    })
                    .setNegativeButton(android.R.string.yes, null);
            builder.create().show();
            return true;
        } else if (item.getItemId() == R.id.action_unanswered_invitations) {
            if(mEventId != null) {
                Fragment fragment = InvitationsSentFragment.newInstance(mEventId);
                mFragmentManagementListener.initFragment(fragment, true);
            }
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

        //Need to be MapView, not SupportMapFragment https://stackoverflow.com/a/19354359/4235666
        detailEventMap.onCreate(savedInstanceState);
        try { MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) { e.printStackTrace(); }
        detailEventMap.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;

                Utiles.setCoordinatesInMap(getActivityContext(), mMap, null);
            }
        });

        mPresenter.getRelationTypeBetweenThisEventAndI();

        detailEventParticipantsContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(mEventId) && !TextUtils.isEmpty(mOwnerId)
                        && mRelation != DetailEventPresenter.RELATION_TYPE_ERROR
                        && isPast != null && isFull != null) {
                    Fragment fragment = ParticipantsFragment.newInstance(mEventId, mOwnerId, mRelation, isPast, isFull);
                    mFragmentManagementListener.initFragment(fragment, true);
                }
            }
        });

        return root;
    }

    @Override
    public void uiSetupForEventRelation(@DetailEventPresenter.EventRelationType int relation) {
        mRelation = relation;

        // If event is in the past no action is allowed
        if (isPast == null || isPast) {
            detailEventStateButton.setVisibility(View.INVISIBLE);
            detailEventStateButton.setClickable(false);
            detailEventRequests.setVisibility(View.GONE);
            detailEventInvitation.setVisibility(View.GONE);
            return;
        }
        switch (relation) {
            case DetailEventPresenter.RELATION_TYPE_NONE:
                setupForNone();
                break;
            case DetailEventPresenter.RELATION_TYPE_OWNER:
                setupForOwner();
                break;
            case DetailEventPresenter.RELATION_TYPE_ASSISTANT:
                setupForParticipant();
                break;
            case DetailEventPresenter.RELATION_TYPE_I_SEND_REQUEST:
                setupForRequestSent();
                break;
            case DetailEventPresenter.RELATION_TYPE_I_RECEIVE_INVITATION:
                setupForInvitationReceived();
                break;
            case DetailEventPresenter.RELATION_TYPE_BLOCKED:
                setupForBlocked();
                break;
            default: case DetailEventPresenter.RELATION_TYPE_ERROR:
                setupForError();
                break;
        }
    }

    private void setupForNone() {
        // Set menu actions
        if (mMenu != null) mMenu.clear();

        // Set state action
        detailEventStateButton.setVisibility(View.VISIBLE);
        detailEventStateButton.setText(R.string.event_state_none);
        if (isFull == null || !isFull) {
            detailEventStateButton.setEnabled(true);
            detailEventStateButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mPresenter.sendEventRequest(mEventId);
                }
            });
        } else
            detailEventStateButton.setEnabled(false);

        // Hide other buttons
        detailEventRequests.setVisibility(View.GONE);
        detailEventInvitation.setVisibility(View.GONE);
    }

    private void setupForOwner() {
        // Set menu actions
        if (mMenu != null) {
            mMenu.clear();
            getActivity().getMenuInflater().inflate(R.menu.menu_edit_delete, mMenu);
            getActivity().getMenuInflater().inflate(R.menu.menu_detail_event, mMenu);
        }

        // Set user requests button
        detailEventRequests.setVisibility(View.VISIBLE);
        detailEventRequests.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mEventId != null) {
                    Fragment fragment = UsersRequestsFragment.newInstance(mEventId);
                    mFragmentManagementListener.initFragment(fragment, true);
                }
            }
        });

        // Set send invitation button
        detailEventInvitation.setVisibility(View.VISIBLE);
        if (isFull == null || !isFull) {
            detailEventInvitation.setEnabled(true);
            detailEventInvitation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Fragment fragment = InviteUserFragment.newInstance(mEventId);
                    mFragmentManagementListener.initFragment(fragment, true);
                }
            });
        } else
            detailEventInvitation.setEnabled(false);

        // Hide other buttons
        detailEventStateButton.setVisibility(View.GONE);
    }

    private void setupForParticipant() {
        // Set menu actions
        if (mMenu != null) {
            mMenu.clear();
            getActivity().getMenuInflater().inflate(R.menu.menu_detail_event, mMenu);
        }

        // Set state action
        detailEventStateButton.setVisibility(View.VISIBLE);
        detailEventStateButton.setText(R.string.event_state_participant);
        detailEventStateButton.setEnabled(true);
        detailEventStateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivityContext())
                        .setMessage(R.string.dialog_msg_quit_myself_from_event)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(getActivityContext())
                                        .setMessage(R.string.dialog_msg_quit_my_sim_user_from_event)
                                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                mPresenter.quitEvent(mEventId, true);
                                            }
                                        })
                                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                mPresenter.quitEvent(mEventId, false);
                                            }
                                        });
                                builder.create().show();
                            }
                        })
                        .setNegativeButton(android.R.string.no, null);
                builder.create().show();
            }
        });

        // Set send invitation button
        detailEventInvitation.setVisibility(View.VISIBLE);
        if (isFull == null || !isFull) {
            detailEventInvitation.setEnabled(true);
            detailEventInvitation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Fragment fragment = InviteUserFragment.newInstance(mEventId);
                    mFragmentManagementListener.initFragment(fragment, true);
                }
            });
        } else
            detailEventInvitation.setEnabled(false);

        // Hide other buttons
        detailEventRequests.setVisibility(View.GONE);
    }

    private void setupForRequestSent() {
        // Set menu actions
        if (mMenu != null) mMenu.clear();

        // Set state action
        detailEventStateButton.setVisibility(View.VISIBLE);
        detailEventStateButton.setText(R.string.event_state_request_sent);
        detailEventStateButton.setEnabled(true);
        detailEventStateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivityContext())
                        .setMessage(R.string.dialog_msg_are_you_sure)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                mPresenter.cancelEventRequest(mEventId);
                            }
                        })
                        .setNegativeButton(android.R.string.no, null);
                builder.create().show();
            }
        });

        // Hide other buttons
        detailEventRequests.setVisibility(View.GONE);
        detailEventInvitation.setVisibility(View.GONE);
    }

    private void setupForInvitationReceived() {
        // Set menu actions
        if (mMenu != null) mMenu.clear();

        // Set state action
        detailEventStateButton.setVisibility(View.VISIBLE);
        detailEventStateButton.setText(R.string.event_state_invitation_received);
        if (isFull == null || !isFull) {
            detailEventStateButton.setEnabled(true);
            detailEventStateButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivityContext())
                            .setMessage(R.string.dialog_msg_answer_invitation)
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    mPresenter.acceptEventInvitation(mEventId, mPresenter.getEventInvitation().getSender());
                                }
                            })
                            .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    mPresenter.declineEventInvitation(mEventId, mPresenter.getEventInvitation().getSender());
                                }
                            });
                    builder.create().show();
                }
            });
        } else
            detailEventStateButton.setEnabled(false);

        // Hide other buttons
        detailEventRequests.setVisibility(View.GONE);
        detailEventInvitation.setVisibility(View.GONE);
    }

    private void setupForBlocked() {
        // Set menu actions
        if (mMenu != null) mMenu.clear();

        // Set state action
        detailEventStateButton.setVisibility(View.VISIBLE);
        detailEventStateButton.setText(R.string.event_state_request_rejected);
        detailEventStateButton.setEnabled(false);

        // Hide other buttons
        detailEventRequests.setVisibility(View.GONE);
        detailEventInvitation.setVisibility(View.GONE);
    }

    private void setupForError() {
        // Set menu actions
        if (mMenu != null) mMenu.clear();

        // Set state action
        detailEventStateButton.setVisibility(View.VISIBLE);
        detailEventStateButton.setText(R.string.error);
        detailEventStateButton.setEnabled(false);

        // Hide other buttons
        detailEventRequests.setVisibility(View.GONE);
        detailEventInvitation.setVisibility(View.GONE);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mFragmentManagementListener.setCurrentDisplayedFragment(getString(R.string.event_details), this);
        mActionBarIconManagementListener.setToolbarAsUp();
    }

    @Override
    public void onStart() {
        super.onStart();
        detailEventMap.onStart();
        mPresenter.openEvent(getLoaderManager(), getArguments());
    }

    @Override
    public void showEventSport(String sport) {
        showContent();
        if (sport != null && !TextUtils.isEmpty(sport))
            Glide.with(this).load(Utiles.getSportIconFromResource(sport)).into(this.detailEventSport);
        mSportId = sport;
    }

    @Override
    public void showEventField(Field field, String address, LatLng coord) {
        showContent();
        if (field != null) {
            this.detailEventPlace.setText(field.getName() + ", " + field.getCity());
            this.detailEventPlaceIcon.setVisibility(View.VISIBLE);
            final String fieldId = field.getId();
            this.detailEventPlace.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Fragment newFragment = DetailFieldFragment.newInstance(fieldId, true);
                    mFragmentManagementListener.initFragment(newFragment, true);
                }
            });
        } else if (address != null && !TextUtils.isEmpty(address)) {
            this.detailEventPlace.setText(address);
            this.detailEventPlaceIcon.setVisibility(View.INVISIBLE);
            this.detailEventPlace.setOnClickListener(null);
        }

        Utiles.setCoordinatesInMap(getActivityContext(), mMap, coord);
    }

    @Override
    public void showEventName(String name) {
        showContent();
        if (name != null && !TextUtils.isEmpty(name))
            mActionBarIconManagementListener.setActionBarTitle(name);
    }

    @Override
    public void showEventDate(long date) {
        showContent();
        this.detailEventDate.setText(UtilesTime.millisToDateTimeString(date));

        //Change UI if event it's already happen.
        isPast = System.currentTimeMillis() > date;
        uiSetupForEventRelation(mRelation);
    }

    @Override
    public void showEventOwner(String owner) {
        showContent();
        if (owner != null && !TextUtils.isEmpty(owner)) {
            mOwnerId = owner;

            String ownerName = UtilesContentProvider.getUserNameFromContentProvider(owner);
            String unformattedString = getString(R.string.created_by);
            this.detailEventOwner.setText(String.format(unformattedString, ownerName));
        }

    }

    @Override
    public void showEventPlayers(int emptyPlayers, int totalPlayers) {
        showContent();
        if(emptyPlayers > -1 && totalPlayers > -1) {
            this.detailEventEmpty.setText(String.format(Locale.getDefault(), "%d", emptyPlayers));
            this.detailEventTotal.setText(String.format(Locale.getDefault(), "%d", totalPlayers));

            //Change UI if emptyPlayer is 0 and doesn't need teams.
            isFull = emptyPlayers == 0 && Utiles.sportNeedsTeams(mSportId);
            uiSetupForEventRelation(mRelation);
        }
    }

    @Override
    public void clearUI() {
        this.detailEventSport.setVisibility(View.INVISIBLE);
        this.detailEventPlace.setText("");
        this.detailEventPlaceIcon.setVisibility(View.INVISIBLE);
        this.detailEventPlace.setOnClickListener(null);
        this.mActionBarIconManagementListener.setActionBarTitle(getString(R.string.event_details));
        this.detailEventDate.setText("");
        this.detailEventOwner.setText("");
        this.detailEventTotal.setText("");
        this.detailEventEmpty.setText("");
    }

    @Override
    public String getEventID() {
        return mEventId;
    }

    @Override
    public void onResume() {
        super.onResume();
        detailEventMap.onResume();
        mPresenter.registerUserRelationObserver();
        /* https://stackoverflow.com/a/17063800/4235666 */
        setMenuVisibility(true);
    }

    @Override
    public void onPause() {
        super.onPause();
        detailEventMap.onPause();
        mPresenter.unregisterUserRelationObserver();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        detailEventMap.onDestroy();
    }

    @Override
    public void onStop() {
        super.onStop();
        detailEventMap.onStop();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        detailEventMap.onLowMemory();
    }
}
