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
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.usal.jorgeav.sportapp.BaseFragment;
import com.usal.jorgeav.sportapp.R;
import com.usal.jorgeav.sportapp.data.Field;
import com.usal.jorgeav.sportapp.eventdetail.invitationsent.InvitationsSentFragment;
import com.usal.jorgeav.sportapp.eventdetail.inviteuser.InviteUserFragment;
import com.usal.jorgeav.sportapp.eventdetail.participants.ParticipantsFragment;
import com.usal.jorgeav.sportapp.eventdetail.userrequests.UsersRequestsFragment;
import com.usal.jorgeav.sportapp.events.addevent.NewEventFragment;
import com.usal.jorgeav.sportapp.fields.fielddetail.DetailFieldFragment;
import com.usal.jorgeav.sportapp.mainactivities.BaseActivity;
import com.usal.jorgeav.sportapp.utils.Utiles;
import com.usal.jorgeav.sportapp.utils.UtilesContentProvider;
import com.usal.jorgeav.sportapp.utils.UtilesTime;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Fragmento utilizado para mostrar los detalles de un partido. Se encarga de inicializar
 * los componentes de la interfaz y utilizarlos para mostrar los parámetros del partido recuperados
 * de la base de datos.
 *
 * <p>Adapta la interfaz, mostrando y ocultando elementos, según la relación del usuario con el
 * partido mostrado. Además, también es relevante para ello, determinar si el partido está completo
 * de participantes o si pertenece al pasado.
 *
 * <p>Implementa la interfaz {@link DetailEventContract.View} para la comunicación con esta clase.
 */
public class DetailEventFragment extends BaseFragment implements
        DetailEventContract.View {
    /**
     * Nombre de la clase
     */
    @SuppressWarnings("unused")
    private static final String TAG = DetailEventFragment.class.getSimpleName();

    /**
     * Etiqueta para establecer el identificador de partido que debe mostrarse en la instanciación
     * del Fragmento
     */
    public static final String BUNDLE_EVENT_ID = "BUNDLE_EVENT_ID";

    /**
     * Identificador del partido que se está mostrando
     */
    private String mEventId = "";

    /**
     * Identificador del deporte asociado al partido
     */
    private String mSportId = "";

    /**
     * Identificador del usuario creador del partido
     */
    private String mOwnerId = "";
    /**
     * Booleano que es true si el partido ya no acepta más participantes y false en caso contrario
     */
    private Boolean isFull = null;
    /**
     * Booleano que es true si la fecha del partido corresponde al pasado y false en caso contrario
     */
    private Boolean isPast = null;
    /**
     * Tipo de relación del usuario actual con el partido que se está mostrando
     */
    @DetailEventPresenter.EventRelationType int mRelation = DetailEventPresenter.RELATION_TYPE_ERROR;

    /**
     * Presentador correspondiente a esta Vista
     */
    private DetailEventContract.Presenter mPresenter;

    /**
     * Menú del Fragmento que cambiará según el tipo de relación del usuario con el partido
     */
    Menu mMenu;

    /**
     * Referencia al mapa de la interfaz donde se emplaza el lugar del partido
     */
    @BindView(R.id.event_detail_map)
    MapView detailEventMap;
    /**
     * Objeto principal de Google Maps API. Hace referencia al mapa que provee esta API.
     *
     * @see
     * <a href= "https://developers.google.com/android/reference/com/google/android/gms/maps/package-summary">
     *     Google Maps API
     * </a>
     */
    private GoogleMap mMap;
    /**
     * Referencia a la imagen de la interfaz donde se muestra el deporte del partido
     */
    @BindView(R.id.event_detail_sport)
    ImageView detailEventSport;
    /**
     * Referencia al elemento de la interfaz donde se especifica el lugar del partido
     */
    @BindView(R.id.event_detail_place)
    TextView detailEventPlace;
    /**
     * Referencia a la imagen de la interfaz que sirve de indicador para mostrar los detalles de la
     * instalación donde se juega el partido
     */
    @BindView(R.id.event_detail_place_icon)
    ImageView detailEventPlaceIcon;
    /**
     * Referencia al elemento de la interfaz donde se especifica la hora y la fecha del partido
     */
    @BindView(R.id.event_detail_date)
    TextView detailEventDate;

    /**
     * Referencia al contenedor de la interfaz donde se acumula la información relativa a los
     * puestos del partido
     */
    @BindView(R.id.event_detail_participants_container)
    CardView detailEventParticipantsContainer;
    /**
     * Referencia a la imagen de la interfaz que sirve para conocer la proporción entre los
     * puestos libres y totales
     */
    @BindView(R.id.event_detail_players_proportion)
    ImageView detailEventProportion;
    /**
     * Referencia al elemento de la interfaz donde se especifican los puestos totales del partido
     */
    @BindView(R.id.event_detail_total)
    TextView detailEventTotal;
    /**
     * Referencia al elemento de la interfaz donde se especifican los puestos vacantes del partido
     */
    @BindView(R.id.event_detail_empty)
    TextView detailEventEmpty;
    /**
     * Referencia al botón de la interfaz que permitirá una y otra acción dependiendo de la
     * relación del usuario actual con el partido
     */
    @BindView(R.id.event_detail_state)
    Button detailEventStateButton;

    /**
     * Referencia al elemento de la interfaz donde se especifica el usuario creador del partido
     */
    @BindView(R.id.event_detail_owner)
    TextView detailEventOwner;

    /**
     * Referencia al contenedor de la interfaz donde se pulsa para mostrar las peticiones de
     * participación al partido
     */
    @BindView(R.id.event_detail_user_requests)
    LinearLayout detailEventRequests;
    /**
     * Referencia al contenedor de la interfaz donde se pulsa para mostrar los usuarios a los que
     * se puede enviar una invitación
     */
    @BindView(R.id.event_detail_send_invitation)
    LinearLayout detailEventInvitation;

    /**
     * Constructor sin argumentos
     */
    public DetailEventFragment() {
    }

    /**
     * Método de instanciación del Fragmento.
     *
     * @param eventId identificador del partido que se muestra
     *
     * @return una nueva instancia de DetailEventFragment
     */
    public static DetailEventFragment newInstance(@NonNull String eventId) {
        DetailEventFragment fragment = new DetailEventFragment();
        Bundle args = new Bundle();
        args.putString(BUNDLE_EVENT_ID, eventId);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Inicialización del Presentador correspondiente a esta Vista
     *
     * @param savedInstanceState estado del Fragmento guardado en una posible rotación de
     *                           la pantalla, o null.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        mPresenter = new DetailEventPresenter(this);
    }

    /**
     * Obtiene una referencia al menú y lo limpia para establecer su contenido una vez se conozca la
     * relación del usuario con el partido
     *
     * @param menu menú de opciones donde se van a emplazar los elementos.
     * @param inflater se utiliza para cargar las opciones de menú
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        mMenu = menu;
        super.onCreateOptionsMenu(mMenu, inflater);
        mMenu.clear();
    }

    /**
     * Invocado cuando un elemento del menú es pulsado. En este caso se encarga de iniciar el
     * proceso de edición del partido instanciando y mostrando el Fragmento correspondiente, o de
     * mostrar las invitaciones enviadas que no han sido respondidas, o se encarga de iniciar el
     * proceso de borrado del partido con la ayuda del Presentador.
     *
     * @param item elemento del menú pulsado
     *
     * @return true si se aceptó la pulsación, false en otro caso
     */
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
                            if(mEventId != null) {
                                mPresenter.deleteEvent(mEventId);
                                ((BaseActivity) getActivity()).hideContent();
                            }
                        }
                    })
                    .setNegativeButton(android.R.string.no, null);
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

    /**
     * Inicializa y obtiene una referencia a los elementos de la interfaz con la ayuda de
     * ButterKnife. Además centra el mapa en la ciudad del usuario y ordena al Presentador iniciar
     * la consulta del tipo de relación.
     *
     * @param inflater utilizado para inflar el archivo de layout
     * @param container contenedor donde se va a incluir la interfaz o null
     * @param savedInstanceState estado del Fragmento guardado en una posible rotación de
     *                           la pantalla, o null.
     *
     * @return la vista de la interfaz inicializada
     *
     * @see
     * <a href= "http://jakewharton.github.io/butterknife/">
     *     ButterKnife
     * </a>
     */
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

    /**
     * Modifica el aspecto de la interfaz dependiendo del tipo de relación especificado. Especifica
     * el contenido del menú, el comportamiento del botón del contenedor de información sobre los
     * puestos y muestra o esconde algunos botones.
     *
     * @param relation tipo de relación entre el usuario actual y el partido que se está mostrando
     */
    @Override
    public void uiSetupForEventRelation(@DetailEventPresenter.EventRelationType int relation) {
        if (getActivity() == null) return;
        MenuInflater menuInflater = getActivity().getMenuInflater();

        mRelation = relation;

        // If event is in the past no action is allowed
        if (isPast == null || isPast) {
            detailEventStateButton.setVisibility(View.INVISIBLE);
            detailEventStateButton.setClickable(false);
            detailEventRequests.setVisibility(View.GONE);
            detailEventInvitation.setVisibility(View.GONE);

            // Set menu actions
            if (mMenu != null) {
                mMenu.clear();
                menuInflater.inflate(R.menu.menu_edit_delete, mMenu);
                mMenu.findItem(R.id.action_delete).setVisible(false);
            }
            return;
        }

        switch (relation) {
            case DetailEventPresenter.RELATION_TYPE_NONE:
                setupForNone();
                break;
            case DetailEventPresenter.RELATION_TYPE_OWNER:
                setupForOwner(menuInflater);
                break;
            case DetailEventPresenter.RELATION_TYPE_ASSISTANT:
                setupForParticipant(menuInflater);
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
            default:
            case DetailEventPresenter.RELATION_TYPE_ERROR:
                setupForError();
                break;
        }
    }

    /**
     * Establece el aspecto de la interfaz cuando el usuario no tiene relación con el partido. Le
     * permite enviar una petición de participación.
     */
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
        } else {
            detailEventStateButton.setVisibility(View.INVISIBLE);
            detailEventStateButton.setEnabled(false);
        }

        // Hide other buttons
        detailEventRequests.setVisibility(View.GONE);
        detailEventInvitation.setVisibility(View.GONE);
    }

    /**
     * Establece el aspecto de la interfaz para cuando el usuario es el creador del partido. Le
     * permite borrarlo o editarlo, entre otras cosas.
     *
     * @param menuInflater se utiliza para cargar las opciones del menú
     */
    private void setupForOwner(MenuInflater menuInflater) {
        // Set menu actions
        if (mMenu != null) {
            mMenu.clear();
            menuInflater.inflate(R.menu.menu_edit_delete, mMenu);
            menuInflater.inflate(R.menu.menu_detail_event, mMenu);
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
        detailEventInvitation.setEnabled(true);
        View.OnClickListener sendInvitationClickListener;
        if (isFull == null || !isFull) {
            sendInvitationClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Fragment fragment = InviteUserFragment.newInstance(mEventId);
                    mFragmentManagementListener.initFragment(fragment, true);
                }
            };
        } else {
            sendInvitationClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(getContext(), R.string.no_empty_players_for_user,
                            Toast.LENGTH_SHORT).show();
                }
            };
        }
        detailEventInvitation.setOnClickListener(sendInvitationClickListener);

        // Hide other buttons
        detailEventStateButton.setVisibility(View.GONE);
    }

    /**
     * Establece el aspecto de la interfaz para cuando el usuario es participante del partido. Le
     * permite salir del evento o enviar invitaciones, entre otras cosas.
     *
     * @param menuInflater se utiliza para cargar las opciones del menú
     */
    private void setupForParticipant(MenuInflater menuInflater) {
        // Set menu actions
        if (mMenu != null) {
            mMenu.clear();
            menuInflater.inflate(R.menu.menu_detail_event, mMenu);
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

    /**
     * Establece el aspecto de la interfaz para cuando el usuario ha enviado ya una petición de
     * participación.
     */
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

    /**
     * Establece el aspecto de la interfaz para cuando el usuario ha recibido una invitación a este
     * partido. Le permite aceptar o declinar la invitación.
     */
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
        } else {
            detailEventStateButton.setVisibility(View.INVISIBLE);
            detailEventStateButton.setEnabled(false);
        }

        // Hide other buttons
        detailEventRequests.setVisibility(View.GONE);
        detailEventInvitation.setVisibility(View.GONE);
    }

    /**
     * Establece el aspecto de la interfaz para cuando al usuario le han rechazado su petición de
     * participación. No se le permite realizar ninguna acción.
     */
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

    /**
     * Establece el aspecto de la interfaz para cuando se produce algún error en la consulta del
     * tipo de relación.
     */
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

    /**
     * Al finalizar el proceso de creación de la Actividad contenedora, se invoca este método que
     * establece un título para la barra superior y la acción que debe realizar: navegar hacia
     * atrás.
     *
     * @param savedInstanceState estado del Fragmento guardado en una posible rotación de
     *                           la pantalla, o null.
     */
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mFragmentManagementListener.setCurrentDisplayedFragment(getString(R.string.event_details), this);
        mNavigationDrawerManagementListener.setToolbarAsUp();
    }

    /**
     * Avisa al mapa de este método del ciclo de vida del Fragmento, y pide al Presentador que
     * recupere los parámetros del partido que se va a mostrar.
     */
    @Override
    public void onStart() {
        super.onStart();
        detailEventMap.onStart();
        mPresenter.openEvent(getLoaderManager(), getArguments());
    }

    /**
     * Muestra en la interfaz el deporte del partido
     *
     * @param sport identificador del deporte
     */
    @Override
    public void showEventSport(String sport) {
        showContent();
        if (sport != null && !TextUtils.isEmpty(sport))
            Glide.with(this).load(Utiles.getSportIconFromResource(sport)).into(this.detailEventSport);
        mSportId = sport;
    }

    /**
     * Muestra en la interfaz el lugar del partido. Escribe la dirección y sitúa el mapa sobre sus
     * coordenadas. También establece el botón para mostrar el Fragmento con los detalles de la
     * instalación en la pantalla
     *
     * @param field instalación con sus parámetros. Puede ser null, si el deporte no requiere
     *              instalación
     * @param address dirección postal donde se juega. Coincide con la dirección de la
     *                instalación si la hay.
     * @param coord coordenadas sobre el mapa del lugar del partido. Coincide con las coordenadas
     */
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

    /**
     * Muestra en la interfaz el nombre del partido
     *
     * @param name nombre
     */
    @Override
    public void showEventName(String name) {
        showContent();
        if (name != null && !TextUtils.isEmpty(name))
            mNavigationDrawerManagementListener.setActionBarTitle(name);
    }

    /**
     * Muestra en la interfaz el día y la fecha del partido. Si pertenece al pasado, se cambia el
     * aspecto por completo.
     *
     * @param date fecha y hora en milisegundos
     */
    @Override
    public void showEventDate(long date) {
        showContent();
        this.detailEventDate.setText(UtilesTime.millisToDateTimeString(date));

        //Change UI if event it's already happen.
        isPast = System.currentTimeMillis() > date;
        uiSetupForEventRelation(mRelation);
    }

    /**
     * Muestra en la interfaz el nombre del usuario creador del partido
     *
     * @param owner identificador del usuario creador del partido
     */
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

    /**
     * Muestra en la interfaz el número de puestos vacantes y totales del partido y establece la
     * imagen que representa esa proporción. Si el partido está completo, se cambia el aspecto de
     * la interfaz por completo.
     *
     * @param emptyPlayers número de puestos vacantes
     * @param totalPlayers número de puestos totales
     */
    @Override
    public void showEventPlayers(int emptyPlayers, int totalPlayers) {
        showContent();
        if(emptyPlayers > -1 && totalPlayers > -1) {
            this.detailEventEmpty.setText(String.format(Locale.getDefault(), "%d", emptyPlayers));
            this.detailEventTotal.setText(String.format(Locale.getDefault(), "%d", totalPlayers));
            this.detailEventProportion.setImageResource(Utiles.getPlayerIconFromResource(emptyPlayers, totalPlayers));

            //Change UI if emptyPlayer is 0 and doesn't need teams.
            isFull = emptyPlayers == 0 && Utiles.sportNeedsTeams(mSportId);
            uiSetupForEventRelation(mRelation);
        }
    }

    /**
     * Muestra el mensaje especificado en la interfaz mediante un {@link Toast}. Aunque la llamada
     * se produzca desde otro hilo, la operación sobre la interfaz para mostrar  el mensaje debe
     * ejecutarse desde el hilo principal.
     *
     * @param msgResource identificador del recurso de texto correspondiente al mensaje que se
     *                    quiere mostrar
     *
     * @see
     * <a href="https://developer.android.com/reference/android/app/Activity.html#runOnUiThread(java.lang.Runnable)">
     *     runOnUiThread(java.lang.Runnable)
     * </a>
     */
    @Override
    public void showMsgFromBackgroundThread(final int msgResource) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getActivity(), msgResource, Toast.LENGTH_SHORT).show();
                mPresenter.openEvent(getLoaderManager(), getArguments());
            }
        });
    }

    /**
     * Limpia la interfaz de los datos del partido
     */
    @Override
    public void clearUI() {
        this.detailEventSport.setVisibility(View.INVISIBLE);
        this.detailEventPlace.setText("");
        this.detailEventPlaceIcon.setVisibility(View.INVISIBLE);
        this.detailEventPlace.setOnClickListener(null);
        this.mNavigationDrawerManagementListener.setActionBarTitle(getString(R.string.event_details));
        this.detailEventDate.setText("");
        this.detailEventOwner.setText("");
        this.detailEventTotal.setText("");
        this.detailEventEmpty.setText("");
    }

    /**
     * Devuelve el identificador del partido
     *
     * @return identificador del partido
     */
    @Override
    public String getEventID() {
        return mEventId;
    }

    /**
     * Avisa al mapa de este método del ciclo de vida del Fragmento, y ordena al Presentador que se
     * añada como Observer de la relación entre el usuario y el partido
     */
    @Override
    public void onResume() {
        super.onResume();
        detailEventMap.onResume();
        mPresenter.registerUserRelationObserver();
        /* https://stackoverflow.com/a/17063800/4235666 */
        setMenuVisibility(true);
    }

    /**
     * Avisa al mapa de este método del ciclo de vida del Fragmento, y ordena al Presentador que se
     * borre como Observer de la relación entre el usuario y el partido
     */
    @Override
    public void onPause() {
        super.onPause();
        detailEventMap.onPause();
        mPresenter.unregisterUserRelationObserver();
    }

    /**
     * Avisa al mapa de este método del ciclo de vida del Fragmento.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        detailEventMap.onDestroy();
    }

    /**
     * Avisa al mapa de este método del ciclo de vida del Fragmento.
     */
    @Override
    public void onStop() {
        super.onStop();
        detailEventMap.onStop();
    }

    /**
     * Avisa al mapa de este método del ciclo de vida del Fragmento.
     */
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        detailEventMap.onLowMemory();
    }
}
