package com.usal.jorgeav.sportapp.profile;


import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.usal.jorgeav.sportapp.BaseFragment;
import com.usal.jorgeav.sportapp.R;
import com.usal.jorgeav.sportapp.adapters.ProfileSportsAdapter;
import com.usal.jorgeav.sportapp.adduser.sportpractice.SportsListFragment;
import com.usal.jorgeav.sportapp.profile.eventinvitations.EventInvitationsFragment;
import com.usal.jorgeav.sportapp.profile.friendrequests.FriendRequestsFragment;
import com.usal.jorgeav.sportapp.profile.sendinvitation.SendInvitationFragment;
import com.usal.jorgeav.sportapp.utils.Utiles;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import pl.aprilapps.easyphotopicker.EasyImage;

public class ProfileFragment extends BaseFragment implements ProfileContract.View {
    private final static String TAG = ProfileFragment.class.getSimpleName();
    public static final int RC_PHOTO_PICKER = 2;

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

        sportsAdapter = new ProfileSportsAdapter(null, null, Glide.with(this));
        userSportList.setAdapter(sportsAdapter);
        userSportList.setHasFixedSize(true);
        userSportList.setLayoutManager(new GridLayoutManager(getActivityContext(), 2, LinearLayoutManager.VERTICAL, false));

        String currentUserId = Utiles.getCurrentUserId();
        if (TextUtils.isEmpty(currentUserId)) throw new NullPointerException();
        if (mUserUid.equals(currentUserId)) {
            setLayoutAsMyUser();
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

    private void setLayoutAsMyUser() {
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

        userName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogForEditName();
            }
        });
        userAge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogForEditAge();
            }
        });
        userImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EasyImage.configuration(getActivity())
                        .setImagesFolderName(Environment.DIRECTORY_PICTURES)
                        .saveInAppExternalFilesDir();
                if (Utiles.isStorageCameraPermissionGranted(getActivity()))
                    EasyImage.openChooserWithGallery(getActivity(), "Elegir foto de...", RC_PHOTO_PICKER);
            }
        });

    }

    private void showDialogForEditName() {
        // Prepare View
        final View dialogView = getActivity().getLayoutInflater().inflate(R.layout.edit_text_change_dialog, null);
        final EditText editText = (EditText) dialogView.findViewById(R.id.change_dialog_text);
        editText.setText(userName.getText());
        editText.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME|InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        editText.setHint(R.string.prompt_name);

        // Create dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivityContext());
        builder.setView(dialogView);
        builder.setTitle(getString(R.string.dialog_title_change_name))
                .setPositiveButton(android.R.string.ok, null)
                .setNegativeButton(android.R.string.cancel, null);
        final AlertDialog alertDialog = builder.create();
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button button = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);

                // Setting clickListener like this prevent from automatic dismiss Dialog
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mProfilePresenter.checkUserName(editText.getText().toString(), new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    editText.setError(getString(R.string.error_not_unique_name));
                                } else {
                                    mProfilePresenter.updateUserName(editText.getText().toString());
                                    alertDialog.dismiss();
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                            }
                        });
                    }
                });
            }
        });
        alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                hideSoftKeyboard();
            }
        });
        alertDialog.show();
    }
    private void showDialogForEditAge() {
        // Prepare View
        final View dialogView = getActivity().getLayoutInflater().inflate(R.layout.edit_text_change_dialog, null);
        final EditText editText = (EditText) dialogView.findViewById(R.id.change_dialog_text);
        editText.setText(userAge.getText());
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        editText.setHint(R.string.prompt_age);

        // Create dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivityContext());
        builder.setView(dialogView);
        builder.setTitle(getString(R.string.dialog_title_change_age))
                .setPositiveButton(android.R.string.ok, null)
                .setNegativeButton(android.R.string.cancel, null);
        final AlertDialog alertDialog = builder.create();
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button button = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);

                // Setting clickListener like this prevent from automatic dismiss Dialog
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (Utiles.isNumeric(editText.getText().toString())) {
                            Integer age = Integer.parseInt(editText.getText().toString());
                            if (age > 12 && age < 100) {
                                mProfilePresenter.updateUserAge(age);
                                alertDialog.dismiss();
                            } else
                                editText.setError(getString(R.string.error_incorrect_age));
                        } else
                            editText.setError(getString(R.string.error_invalid_age));
                    }
                });
            }
        });
        alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                hideSoftKeyboard();
            }
        });
        alertDialog.show();
    }

    @Override
    public void croppedResult(Uri photoCroppedUri) {
        // Uri from cropped photo as result of UCrop
        if (photoCroppedUri != null)
            mProfilePresenter.updateUserPhoto(photoCroppedUri);
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
                    userSendInvitationButton.setVisibility(View.VISIBLE);
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
                    userSendInvitationButton.setVisibility(View.INVISIBLE);
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
                    userSendInvitationButton.setVisibility(View.INVISIBLE);
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
                    userSendInvitationButton.setVisibility(View.INVISIBLE);
                    break;
                case ProfilePresenter.RELATION_TYPE_ERROR:
                    userAddFriendButton.setText("Error");
                    userSendInvitationButton.setVisibility(View.INVISIBLE);
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
    public void showUserImage(String imageUrl) {
        userImage.setVisibility(View.VISIBLE);
        Glide.with(this.getActivity())
                .load(imageUrl)
                .error(R.drawable.profile_picture_placeholder)
                .placeholder(R.drawable.profile_picture_placeholder)
                .centerCrop()
                .into(userImage);
    }

    @Override
    public void showUserName(String name) {
        userName.setText(name);
        mActionBarIconManagementListener.setActionBarTitle(name);
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
        this.mActionBarIconManagementListener.setActionBarTitle("");
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
