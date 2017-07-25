package com.usal.jorgeav.sportapp.profile;


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
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.usal.jorgeav.sportapp.BaseFragment;
import com.usal.jorgeav.sportapp.R;
import com.usal.jorgeav.sportapp.adapters.ProfileSportsAdapter;
import com.usal.jorgeav.sportapp.adduser.sportpractice.SportsListFragment;
import com.usal.jorgeav.sportapp.profile.eventinvitations.EventInvitationsFragment;
import com.usal.jorgeav.sportapp.profile.friendrequests.FriendRequestsFragment;
import com.usal.jorgeav.sportapp.profile.sendinvitation.SendInvitationFragment;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProfileFragment extends BaseFragment implements ProfileContract.View {
    private final static String TAG = ProfileFragment.class.getSimpleName();

    public static final String BUNDLE_INSTANCE_UID = "BUNDLE_INSTANCE_UID";
    private static String mUserUid = "";
    private ProfileContract.Presenter mProfilePresenter;

    Menu mMenu;
    @BindView(R.id.user_send_invitation)
    Button userSendInvitationButton;
    @BindView(R.id.user_add_friend)
    Button userAddFriendButton;
    @BindView(R.id.user_image)
    ImageView userImage;
    @BindView(R.id.user_name)
    TextView userName;
    @BindView(R.id.user_city)
    TextView userCity;
    @BindView(R.id.user_age)
    TextView userAge;
    @BindView(R.id.user_sport_list)
    RecyclerView userSportList;
    ProfileSportsAdapter sportsAdapter;
    @BindView(R.id.user_sport_placeholder)
    ConstraintLayout userSportPlaceholder;
    @BindView(R.id.user_edit_sport)
    Button userEditSportListButton;
    @BindView(R.id.user_event_invitations)
    Button userEventInvitationsButton;
    @BindView(R.id.user_friend_requests)
    Button userFriendRequestsButton;

    public ProfileFragment() {
        // Required empty public constructor
    }

    public static ProfileFragment newInstance(@NonNull String uid) {
        Bundle b = new Bundle();
        b.putString(BUNDLE_INSTANCE_UID, uid);
        ProfileFragment pf = new ProfileFragment();
        pf.setArguments(b);
        return pf;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        mProfilePresenter = new ProfilePresenter(this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        mMenu = menu;
        super.onCreateOptionsMenu(mMenu, inflater);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_profile, container, false);
        ButterKnife.bind(this, root);

        //Es importante hacerlo aqui ya que el valor de mUserId gobierna el aspecto de la UI y
        // en onCreate no se ejecutaria al volver de otro fragment
        if (getArguments() != null && getArguments().containsKey(BUNDLE_INSTANCE_UID))
            mUserUid = getArguments().getString(BUNDLE_INSTANCE_UID);

        sportsAdapter = new ProfileSportsAdapter(null, null);
        userSportList.setAdapter(sportsAdapter);
        userSportList.setHasFixedSize(true);
        userSportList.setLayoutManager(new LinearLayoutManager(getActivityContext(), LinearLayoutManager.HORIZONTAL, false));


        if (mUserUid.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
            userEventInvitationsButton.setVisibility(View.VISIBLE);
            userEventInvitationsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Fragment fragment = new EventInvitationsFragment();
                    mFragmentManagementListener.initFragment(fragment, true);
                }
            });
            userFriendRequestsButton.setVisibility(View.VISIBLE);
            userFriendRequestsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Fragment fragment = new FriendRequestsFragment();
                    mFragmentManagementListener.initFragment(fragment, true);
                }
            });
            userEditSportListButton.setVisibility(View.VISIBLE);
            userEditSportListButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Fragment fragment = SportsListFragment.newInstance(mUserUid, sportsAdapter.getDataAsArrayList());
                    mFragmentManagementListener.initFragment(fragment, true);
                }
            });
        } else {
            userSendInvitationButton.setVisibility(View.VISIBLE);
            userSendInvitationButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Fragment fragment = SendInvitationFragment.newInstance(mUserUid);
                    mFragmentManagementListener.initFragment(fragment, true);
                }
            });
            userAddFriendButton.setVisibility(View.VISIBLE);
            mProfilePresenter.getRelationTypeBetweenThisUserAndI();
        }


        return root;
    }

    @Override
    public void uiSetupForUserRelation(@ProfilePresenter.UserRelationType int relation) {
        /* AMISTAD
         * Si this y yo somos amigos: Icono de amigos -> onClick: borrar amigo
         * Si this ha pedido ser amigo de mi: Icono de responder peticion -> onClick: aceptar o rechazar
         * Si yo he pedido ser amigo de this: Icono de peticion enviada -> onClick: borrar peticion
         * En otro caso: Icono de enviar peticion -> onClick: enviar peticion
         *
         * SEGUIR
         * Si this y yo nos seguimos: Icono de siguiendo -> onClick: dejar de seguir
         * Si this sigue a mi: Icono de seguir? -> onClick: seguir
         * Si yo sigo a this: Icono de siguiendo -> onClick: dejar de seguir
         * En otro caso: Icono de seguir? -> onClick: seguir
         */
        if (relation != ProfilePresenter.RELATION_TYPE_ME) {
            if (mMenu != null) mMenu.clear();
            Log.d(TAG, "uiSetupForUserRelation: mMenu " + mMenu);
            Log.d(TAG, "uiSetupForUserRelation: relation " + relation);
            switch (relation) {
                case ProfilePresenter.RELATION_TYPE_FRIENDS:
                    userAddFriendButton.setText("Borrar Amigo");
                    userAddFriendButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                        /*borrar amigos*/
                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivityContext());
                            builder.setMessage("Estas seguro de que quieres borrar amigo?")
                                    .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            mProfilePresenter.deleteFriend(mUserUid);
                                        }
                                    })
                                    .setNegativeButton("No", null);
                            builder.create().show();
                        }
                    });
                    break;
                case ProfilePresenter.RELATION_TYPE_I_RECEIVE_REQUEST:
                    userAddFriendButton.setText("Responder peticion");
                    userAddFriendButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                        /*aceptar o rechazar*/
                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivityContext());
                            builder.setMessage("Aceptar como amigo?")
                                    .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            mProfilePresenter.acceptFriendRequest(mUserUid);
                                        }
                                    })
                                    .setNegativeButton("Rechazar", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            mProfilePresenter.declineFriendRequest(mUserUid);
                                        }
                                    });
                            builder.create().show();
                        }
                    });
                    break;
                case ProfilePresenter.RELATION_TYPE_I_SEND_REQUEST:
                    userAddFriendButton.setText("Peticion Enviada");
                    userAddFriendButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                        /*borrar peticion*/
                            mProfilePresenter.cancelFriendRequest(mUserUid);
                        }
                    });
                    break;
                case ProfilePresenter.RELATION_TYPE_NONE:
                    userAddFriendButton.setText("Enviar peticion");
                    userAddFriendButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                        /*enviar peticion*/
                            mProfilePresenter.sendFriendRequest(mUserUid);
                        }
                    });
                    break;
                case ProfilePresenter.RELATION_TYPE_ERROR:
                    userAddFriendButton.setText("Error");
                    break;
            }
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mFragmentManagementListener.setCurrentDisplayedFragment(getString(R.string.profile), this);
        if (mUserUid.equals(FirebaseAuth.getInstance().getCurrentUser().getUid()))
            mActionBarIconManagementListener.setToolbarAsNav();
        else
            mActionBarIconManagementListener.setToolbarAsUp();
    }

    @Override
    public void onStart() {
        super.onStart();
        mProfilePresenter.openUser(getLoaderManager(), getArguments());
    }

    @Override
    public void showUserImage(String image) {
        userImage.setVisibility(View.VISIBLE);
        Glide.with(this.getActivity())
                .load(image)
                .error(R.drawable.profile_picture_placeholder)
                .placeholder(R.drawable.profile_picture_placeholder)
                .centerCrop()
                .into(userImage);
    }

    @Override
    public void showUserName(String name) {
        userName.setText(name);
        mFragmentManagementListener.setActionBarTitle(name);
    }

    @Override
    public void showUserCity(String city) {
        userCity.setText(city);
    }

    @Override
    public void showUserAge(int age) {
        if (age > -1) {
            userAge.setText(String.format(Locale.getDefault(), "%2d", age));
        }
    }

    @Override
    public void showSports(Cursor cursor) {
        sportsAdapter.replaceData(cursor);
        if (cursor != null && cursor.getCount() > 0) {
            userSportList.setVisibility(View.VISIBLE);
            userSportPlaceholder.setVisibility(View.INVISIBLE);
        } else {
            userSportList.setVisibility(View.INVISIBLE);
            userSportPlaceholder.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void clearUI() {
        this.userImage.setVisibility(View.INVISIBLE);
        this.userName.setText("");
        this.mFragmentManagementListener.setActionBarTitle("");
        this.userCity.setText("");
        this.userAge.setText("");
    }

    @Override
    public String getUserID() {
        return mUserUid;
    }

    @Override
    public void onResume() {
        super.onResume();
        mProfilePresenter.registerUserRelationObserver();
        /* https://stackoverflow.com/a/17063800/4235666 */
        setMenuVisibility(true);
    }

    @Override
    public void onPause() {
        super.onPause();
        mProfilePresenter.unregisterUserRelationObserver();
        sportsAdapter.replaceData(null);
    }
}
