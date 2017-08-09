package com.usal.jorgeav.sportapp.eventdetail;


import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
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
    @BindView(R.id.event_detail_sport)
    ImageView detailEventSport;
    @BindView(R.id.event_detail_place)
    TextView detailEventPlace;
    @BindView(R.id.event_detail_place_icon)
    ImageView detailEventPlaceIcon;
    @BindView(R.id.event_detail_date)
    TextView detailEventDate;
    @BindView(R.id.event_detail_players_proportion)
    ImageView detailEventProportion;
    @BindView(R.id.event_detail_total)
    TextView detailEventTotal;
    @BindView(R.id.event_detail_empty)
    TextView detailEventEmpty;
    @BindView(R.id.event_detail_owner)
    TextView detailEventOwner;

    @BindView(R.id.event_detail_user_requests)
    Button buttonUserRequests;
    @BindView(R.id.event_detail_send_invitation)
    Button buttonSendInvitation;
    @BindView(R.id.event_detail_send_request)
    Button buttonSendRequest;
    @BindView(R.id.event_detail_simulate_participant)
    Button buttonSimulateParticipant;

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
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivityContext());
            builder.setTitle("Borrar evento")
                    .setMessage("Seguro que desea borrarlo?")
                    .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    mPresenter.deleteEvent(getArguments());
                    ((BaseActivity)getActivity()).hideContent();
                }})
                    .setNegativeButton("No", null);
            builder.create().show();
            return true;
        } else if (item.getItemId() == R.id.action_unanswered_invitations) {
            if(mEventId != null) {
                Fragment fragment = InvitationsSentFragment.newInstance(mEventId);
                mFragmentManagementListener.initFragment(fragment, true);
            }
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

        mPresenter.getRelationTypeBetweenThisEventAndI();

        buttonSimulateParticipant.setOnClickListener(new View.OnClickListener() {
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
            buttonSendRequest.setVisibility(View.GONE);
            buttonUserRequests.setVisibility(View.GONE);
            buttonSendInvitation.setVisibility(View.GONE);
            return;
        }
        switch (relation) {
            case DetailEventPresenter.RELATION_TYPE_OWNER:
                setupForOwner();
                break;
            case DetailEventPresenter.RELATION_TYPE_NONE:
                setupForNone();
                break;
            case DetailEventPresenter.RELATION_TYPE_I_SEND_REQUEST:
                setupForRequestSent();
                break;
            case DetailEventPresenter.RELATION_TYPE_I_RECEIVE_INVITATION:
                setupForInvitationReceived();
                break;
            case DetailEventPresenter.RELATION_TYPE_ASSISTANT:
                setupForParticipant();
                break;
            case DetailEventPresenter.RELATION_TYPE_BLOCKED:
                setupForBlocked();
                break;
            default: case DetailEventPresenter.RELATION_TYPE_ERROR:
                setupForError();
                break;
        }
    }

    private void setupForError() {
        if (mMenu != null) mMenu.clear();
        buttonSendRequest.setVisibility(View.VISIBLE);
        buttonSendRequest.setText("Error");
        buttonSendRequest.setEnabled(false);
        buttonUserRequests.setVisibility(View.INVISIBLE);
        buttonSendInvitation.setVisibility(View.INVISIBLE);
    }

    private void setupForBlocked() {
        if (mMenu != null) mMenu.clear();
        buttonSendRequest.setVisibility(View.VISIBLE);
        buttonSendRequest.setText("No puedes asistir");
        buttonSendRequest.setEnabled(false);
        buttonUserRequests.setVisibility(View.INVISIBLE);
        buttonSendInvitation.setVisibility(View.INVISIBLE);
    }

    private void setupForParticipant() {
        if (mMenu != null) {
            mMenu.clear();
            getActivity().getMenuInflater().inflate(R.menu.menu_detail_event, mMenu);
        }
        buttonSendRequest.setVisibility(View.VISIBLE);
        buttonSendRequest.setText("Abandonar partido");
        buttonSendRequest.setEnabled(true);
        buttonSendRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                        /*Abandonar partido*/
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivityContext());
                builder.setMessage("Quieres abandonar este partido?")
                        .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(getActivityContext());
                                builder.setMessage("Quieres que se borren los usuarios simulados por ti?")
                                        .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                mPresenter.quitEvent(mEventId, true);
                                            }
                                        })
                                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                mPresenter.quitEvent(mEventId, false);
                                            }
                                        });
                                builder.create().show();
                            }
                        })
                        .setNegativeButton("No", null);
                builder.create().show();
            }
        });
        buttonSendInvitation.setVisibility(View.VISIBLE);
        if (!isFull) {
            buttonSendInvitation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //DONE ver lista de amigos para enviarles invitaciones
                    Fragment fragment = InviteUserFragment.newInstance(mEventId);
                    mFragmentManagementListener.initFragment(fragment, true);
                }
            });
        } else {
            buttonSendInvitation.setEnabled(false);
        }
    }

    private void setupForInvitationReceived() {
        if (mMenu != null) mMenu.clear();
        buttonSendRequest.setVisibility(View.VISIBLE);
        buttonSendRequest.setText("Contestar invitacion");
        if (isFull == null || !isFull) {
            buttonSendRequest.setEnabled(true);
            buttonSendRequest.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                        /*Contestar invitacion*/
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivityContext());
                    builder.setMessage("Quieres asistir a este evento?")
                            .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    mPresenter.acceptEventInvitation(mEventId, mPresenter.getEventInvitation().getSender());
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    mPresenter.declineEventInvitation(mEventId, mPresenter.getEventInvitation().getSender());
                                }
                            });
                    builder.create().show();
                }
            });
        } else {
            buttonSendRequest.setEnabled(false);
        }
        buttonUserRequests.setVisibility(View.INVISIBLE);
        buttonSendInvitation.setVisibility(View.INVISIBLE);
    }

    private void setupForRequestSent() {
        if (mMenu != null) mMenu.clear();
        buttonSendRequest.setVisibility(View.VISIBLE);
        buttonSendRequest.setText("Cancelar peticion de entrada");
        buttonSendRequest.setEnabled(true);
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
        buttonUserRequests.setVisibility(View.INVISIBLE);
        buttonSendInvitation.setVisibility(View.INVISIBLE);
    }

    private void setupForNone() {
        if (mMenu != null) mMenu.clear();
        buttonSendRequest.setVisibility(View.VISIBLE);
        buttonSendRequest.setText("Enviar peticion de entrada");
        if (isFull == null || !isFull) {
            buttonSendRequest.setEnabled(true);
            buttonSendRequest.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                        /*Enviar peticion de entrada*/
                    mPresenter.sendEventRequest(mEventId);
                }
            });
        } else {
            buttonSendRequest.setEnabled(false);
        }
        buttonUserRequests.setVisibility(View.INVISIBLE);
        buttonSendInvitation.setVisibility(View.INVISIBLE);
    }

    private void setupForOwner() {
        if (mMenu != null) {
            mMenu.clear();
            getActivity().getMenuInflater().inflate(R.menu.menu_edit_delete, mMenu);
            getActivity().getMenuInflater().inflate(R.menu.menu_detail_event, mMenu);
        }
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
        buttonSimulateParticipant.setVisibility(View.VISIBLE);
        if (isFull == null || !isFull) {
            buttonSendInvitation.setEnabled(true);
            buttonSendInvitation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //DONE ver lista de amigos para enviarles invitaciones
                    Fragment fragment = InviteUserFragment.newInstance(mEventId);
                    mFragmentManagementListener.initFragment(fragment, true);
                }
            });
        } else {
            buttonSendInvitation.setEnabled(false);
        }
        buttonSendRequest.setVisibility(View.INVISIBLE);
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
        mPresenter.openEvent(getLoaderManager(), getArguments());
    }

    @Override
    public void showEventSport(String sport) {
        ((BaseActivity)getActivity()).showContent();
        if (sport != null && !TextUtils.isEmpty(sport))
            Glide.with(this).load(Utiles.getSportIconFromResource(sport)).into(this.detailEventSport);
        mSportId = sport;
    }

    @Override
    public void showEventField(Field field, String address, LatLng coord) {
        //TODO mostrar datos mejor
        ((BaseActivity)getActivity()).showContent();
        if (field != null) {
            this.detailEventPlace.setText(field.getName() + ", " + field.getCity());
            this.detailEventPlaceIcon.setVisibility(View.VISIBLE);
            final String fieldId = field.getId();
            detailEventPlace.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Fragment newFragment = DetailFieldFragment.newInstance(fieldId, true);
                    mFragmentManagementListener.initFragment(newFragment, true);
                }
            });
        } else if (address != null && !TextUtils.isEmpty(address)) {
            this.detailEventPlace.setText(address);
            this.detailEventPlaceIcon.setVisibility(View.INVISIBLE);
            detailEventPlace.setOnClickListener(null);
        }
    }

    @Override
    public void showEventName(String name) {
        ((BaseActivity)getActivity()).showContent();
        if (name != null && !TextUtils.isEmpty(name))
            mActionBarIconManagementListener.setActionBarTitle(name);
    }

    @Override
    public void showEventDate(long date) {
        ((BaseActivity)getActivity()).showContent();
        this.detailEventDate.setText(UtilesTime.millisToDateTimeString(date));

        //Change UI if event it's already happen.
        isPast = System.currentTimeMillis() > date;
        uiSetupForEventRelation(mRelation);
    }

    @Override
    public void showEventOwner(String owner) {
        ((BaseActivity)getActivity()).showContent();
        if (owner != null && !TextUtils.isEmpty(owner)) {
            mOwnerId = owner;

            String ownerName = UtilesContentProvider.getUserNameFromContentProvider(owner);
            String unformattedString = getString(R.string.created_by);
            this.detailEventOwner.setText(String.format(unformattedString, ownerName));
        }

    }

    @Override
    public void showEventPlayers(int emptyPlayers, int totalPlayers) {
        if(emptyPlayers > -1 && totalPlayers > -1) {
            ((BaseActivity) getActivity()).showContent();
            this.detailEventEmpty.setText(String.format(Locale.getDefault(), "%2d", emptyPlayers));
            this.detailEventTotal.setText(String.format(Locale.getDefault(), "%2d", totalPlayers));

            //Change UI if emptyPlayer is 0 and doesn't need teams.
            isFull = emptyPlayers == 0 && Utiles.sportNeedsTeams(mSportId);
            uiSetupForEventRelation(mRelation);
        }
    }

    @Override
    public void clearUI() {
        this.detailEventSport.setVisibility(View.INVISIBLE);
        this.detailEventPlace.setText("");
        this.detailEventPlace.setOnClickListener(null);
        this.mActionBarIconManagementListener.setActionBarTitle("");
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
        mPresenter.registerUserRelationObserver();
        /* https://stackoverflow.com/a/17063800/4235666 */
        setMenuVisibility(true);
    }

    @Override
    public void onPause() {
        super.onPause();
        mPresenter.unregisterUserRelationObserver();
    }
}
