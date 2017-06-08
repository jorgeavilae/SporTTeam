package com.usal.jorgeav.sportapp.profile;


import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.usal.jorgeav.sportapp.ActivityContracts;
import com.usal.jorgeav.sportapp.R;
import com.usal.jorgeav.sportapp.adapters.ProfileSportsAdapter;
import com.usal.jorgeav.sportapp.adduser.sportpractice.SportsListFragment;
import com.usal.jorgeav.sportapp.data.Sport;
import com.usal.jorgeav.sportapp.profile.eventinvitations.EventInvitationsFragment;
import com.usal.jorgeav.sportapp.profile.friendrequests.FriendRequestsFragment;
import com.usal.jorgeav.sportapp.profile.sendinvitation.SendInvitationFragment;

import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProfileFragment extends Fragment implements ProfileContract.View, SportsListFragment.OnSportsSelected {
    private final static String TAG = ProfileFragment.class.getSimpleName();

    public static final String BUNDLE_INSTANCE_UID = "BUNDLE_INSTANCE_UID";
    public static final int LOADER_MYPROFILE_ID = 1001;
    public static final int LOADER_MYPROFILE_SPORTS_ID = 1002;
    private static String mUserUid = "";
    private ProfileContract.Presenter mProfilePresenter;
    private ActivityContracts.FragmentManagement mFragmentManagementListener;
    private ActivityContracts.ActionBarIconManagement mActionBarIconManagementListener;

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
    @BindView(R.id.user_edit_sport)
    Button userEditSportListButton;
    @BindView(R.id.user_event_invitations)
    Button userEventInvitationsButton;
    @BindView(R.id.user_friend_requests)
    Button userFriendRequestsButton;

    public ProfileFragment() {
        // Required empty public constructor
    }

    public static ProfileFragment newInstance(String uid) {
        Log.d(TAG, "newInstance: uid "+uid);
        Bundle b = new Bundle();
        b.putString(BUNDLE_INSTANCE_UID, uid);
        ProfileFragment pf = new ProfileFragment();
        pf.setArguments(b);
        return pf;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null && getArguments().containsKey(BUNDLE_INSTANCE_UID))
            mUserUid = getArguments().getString(BUNDLE_INSTANCE_UID);

        mProfilePresenter = new ProfilePresenter(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_profile, container, false);
        ButterKnife.bind(this, root);

        sportsAdapter = new ProfileSportsAdapter(null);
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
            final ProfileFragment profileFragment = this;
            userEditSportListButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Fragment fragment = SportsListFragment.newInstance(sportsAdapter.getDataAsArrayList(), profileFragment);
                    mFragmentManagementListener.initFragment(fragment, true);
                }
            });
        } else {
            userSendInvitationButton.setVisibility(View.VISIBLE);
            userSendInvitationButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Fragment fragment = new SendInvitationFragment();
                    mFragmentManagementListener.initFragment(fragment, true);
                }
            });
            userAddFriendButton.setVisibility(View.VISIBLE);
            addFriendButtonSetup();
        }


        return root;
    }

    private void addFriendButtonSetup() {
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
        switch (mProfilePresenter.getRelationTypeBetweenThisUserAndI()) {
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
    public void onResume() {
        super.onResume();
//        mProfilePresenter.loadUser();
        Bundle b = new Bundle();
        b.putString(BUNDLE_INSTANCE_UID, mUserUid);
        getLoaderManager().initLoader(LOADER_MYPROFILE_ID, b, mProfilePresenter.getLoaderInstance());
        getLoaderManager().initLoader(LOADER_MYPROFILE_SPORTS_ID, b, mProfilePresenter.getLoaderInstance());
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
    public void showUserImage(String image) {
//        mFragmentManagementListener.showContent();
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.android_cheer);
        userImage.setImageDrawable(new BitmapDrawable(getResources(), bitmap));
    }

    @Override
    public void showUserName(String name) {
        userName.setText(name);
        mFragmentManagementListener.showContent();
    }

    @Override
    public void showUserCity(String city) {
        userCity.setText(city);
        mFragmentManagementListener.showContent();

    }

    @Override
    public void showUserAge(int age) {
        if (age > -1) {
            userAge.setText(String.format(Locale.getDefault(), "%2d", age));
            mFragmentManagementListener.showContent();
        }

    }

    @Override
    public void showSports(Cursor cursor) {
        sportsAdapter.replaceData(cursor);
    }

    @Override
    public Context getActivityContext() {
        return getActivity();
    }

    @Override
    public String getUserID() {
        return mUserUid;
    }

    @Override
    public void retrieveSportsSelected(List<Sport> sportsSelected) {
        //TODO update sports in firebase
    }
}
