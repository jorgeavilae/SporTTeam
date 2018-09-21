package com.usal.jorgeav.sportapp.eventdetail.simulateparticipant;

import android.net.Uri;
import android.text.TextUtils;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.UploadTask;
import com.usal.jorgeav.sportapp.R;
import com.usal.jorgeav.sportapp.data.SimulatedUser;
import com.usal.jorgeav.sportapp.network.firebase.actions.EventsFirebaseActions;
import com.usal.jorgeav.sportapp.network.firebase.actions.UserFirebaseActions;
import com.usal.jorgeav.sportapp.utils.Utiles;

/**
 * Presentador utilizado para añadir usuarios simulados. Esta clase funciona como puente entre la
 * Vista y la base de datos del servidor. Se encarga de comprobar los datos introducidos por el
 * usuario en la interfaz. Implementa la interfaz {@link SimulateParticipantContract.Presenter}
 * para la comunicación con esta clase.
 */
class SimulateParticipantPresenter implements
        SimulateParticipantContract.Presenter {
    /**
     * Nombre de la clase
     */
    public static final String TAG = SimulateParticipantPresenter.class.getSimpleName();

    /**
     * Vista correspondiente a este Presentador
     */
    private SimulateParticipantContract.View mView;

    /**
     * Constructor
     *
     * @param view Vista correspondiente a este Presentador
     */
    SimulateParticipantPresenter(SimulateParticipantContract.View view) {
        this.mView = view;
    }

    /**
     * Añade a la base de datos del servidor los datos proporcionados como un usuario simulado del
     * partido indicado. Primero realiza una serie de comprobaciones sobre los datos: si surge
     * algún error lo manda a la Vista, si no surge ninguno continúa con el proceso.
     * <p>
     * <p>Hace una distinción si se ha especificado una imagen para el usuario simulado, primero
     * la guarda en el almacenamiento en la nube (Firebase Storage) y luego almacena el usuario
     * simulado, en la base de datos del servidor, con la foto como una URL hacia el archivo de
     * imagen en Firebase Storage.
     *
     * @param eventId identificador del partido al que asistirá el usuario simulado
     * @param name    nombre del usuario simulado
     * @param photo   imagen del usuario simulado
     * @param ageStr  edad del usuario simulado en formato texto
     * @link <a href= "https://firebase.google.com/docs/reference/android/com/google/firebase/storage/FirebaseStorage">
     * FirebaseStorage
     * </a>
     */
    @Override
    public void addSimulatedParticipant(String eventId, String name, Uri photo, String ageStr) {
        if (eventId != null && !TextUtils.isEmpty(eventId) && !TextUtils.isEmpty(name)
                && !TextUtils.isEmpty(ageStr)) { //Validate arguments

            Long age;
            try {
                age = Long.parseLong(ageStr);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                mView.showMsgFromUIThread(R.string.new_simulated_user_invalid_arg);
                return;
            }

            if (age <= 12 || age >= 100) {
                mView.showMsgFromUIThread(R.string.error_incorrect_age);
                return;
            }

            if (photo != null)
                storePhotoOnFirebaseAndAddSimulatedParticipant(photo, eventId, name, age);
            else {
                String myUserID = Utiles.getCurrentUserId();
                if (TextUtils.isEmpty(myUserID)) return;
                SimulatedUser su = new SimulatedUser(name, null, age, myUserID);
                EventsFirebaseActions.addSimulatedParticipant(mView.getThis(), eventId, su);
            }
            mView.hideContent();
        } else
            mView.showMsgFromUIThread(R.string.new_simulated_user_invalid_arg);
    }

    /**
     * En este método se implementa el Listener que se ejecutará cuando finalice la subida del
     * archivo de imagen a Firebase Storage. Si esta subida tiene éxito, se obtiene la URL y, junto
     * con el resto de datos, se crea el usuario simulado que se añade a la base de datos.
     *
     * @param photo   ruta del archivo de imagen del usuario simulado dentro del almacenamiento del
     *                teléfono
     * @param eventId identificador del evento al que se va a añadir el usuario simulado
     * @param name    nombre del usuario simulado
     * @param age     edad del usuario simulado
     */
    private void storePhotoOnFirebaseAndAddSimulatedParticipant(Uri photo,
                                                                String eventId,
                                                                String name,
                                                                Long age) {
        final String fEventId = eventId;
        final String fName = name;
        final Long fAge = age;
        UserFirebaseActions.storePhotoOnFirebase(photo,
                new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Handle successful uploads on complete
                        Uri downloadUrl = taskSnapshot.getDownloadUrl();

                        String myUserID = Utiles.getCurrentUserId();
                        if (TextUtils.isEmpty(myUserID)) return;
                        String photo = downloadUrl != null ? downloadUrl.toString() : null;
                        SimulatedUser su = new SimulatedUser(fName, photo, fAge, myUserID);
                        EventsFirebaseActions.addSimulatedParticipant(mView.getThis(), fEventId, su);
                    }
                });
    }
}
