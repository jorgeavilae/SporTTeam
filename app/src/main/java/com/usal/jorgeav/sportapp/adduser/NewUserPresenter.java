package com.usal.jorgeav.sportapp.adduser;

import android.content.ContentValues;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.usal.jorgeav.sportapp.R;
import com.usal.jorgeav.sportapp.data.Sport;
import com.usal.jorgeav.sportapp.data.User;
import com.usal.jorgeav.sportapp.data.provider.SportteamContract;
import com.usal.jorgeav.sportapp.network.firebase.FirebaseDBContract;
import com.usal.jorgeav.sportapp.network.firebase.actions.UserFirebaseActions;
import com.usal.jorgeav.sportapp.network.firebase.sync.UsersFirebaseSync;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.app.Activity.RESULT_OK;

/**
 * Presentador utilizado en la creación de usuarios. Aquí se validan todos los atributos de
 * un usuario introducidos en la Vista {@link NewUserContract.View}. También se encarga del
 * proceso de creación del usuario en los servidores de la aplicación.
 * Implementa la interfaz {@link NewUserContract.Presenter} para la comunicación con esta clase.
 */
class NewUserPresenter implements NewUserContract.Presenter {
    /**
     * Nombre de la clase
     */
    private static final String TAG = NewUserPresenter.class.getSimpleName();

    /**
     * Vista correspondiente a este Presentador
     */
    private NewUserContract.View mView;

    /**
     * Indica si ya fue comprobado y aceptado que el email introducido por el usuario es único
     */
    private Boolean isEmailUnique = null;
    /**
     * Indica si ya fue comprobado y aceptado que el nombre introducido por el usuario es único
     */
    private Boolean isNameUnique = null;

    /**
     * Constructor
     *
     * @param mView referencia a la Vista correspondiente a este Presentador
     */
    NewUserPresenter(NewUserContract.View mView) {
        this.mView = mView;
    }

    /**
     * Comprueba la existencia del email en la base de datos del servidor de Firebase.
     *
     * @param email dirección de email introducida
     */
    @Override
    public void checkUserEmailExists(String email) {
        isEmailUnique = false;
        if (email != null && !TextUtils.isEmpty(email)) {
            if (isEmailValid(email))
                UsersFirebaseSync.queryUserEmail(email, new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    if (mView.getThis().isAdded()) //check if Fragment its attached to Activity
                                        mView.setEmailError(R.string.error_not_unique_email);
                                } else isEmailUnique = true;
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Toast.makeText(mView.getActivityContext(),
                                        R.string.error_check_conn, Toast.LENGTH_SHORT).show();
                            }
                        });
            else
                mView.setEmailError(R.string.error_invalid_email);
        }
    }

    /**
     * Asegura que la cadena de texto utilizada como email concuerda con el patrón de una dirección
     * de email.
     *
     * @param email cadena de texto usada como email
     * @return true si concuerda, false en caso contrario
     */
    private boolean isEmailValid(@NonNull String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    /**
     * Comprueba la existencia del nombre en la base de datos del servidor de Firebase.
     *
     * @param name nombre introducido
     */
    @Override
    public void checkUserNameExists(String name) {
        isNameUnique = false;
        if (name != null && !TextUtils.isEmpty(name))
            UsersFirebaseSync.queryUserName(name, new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                if (mView.getThis().isAdded()) //check if Fragment its attached to Activity
                                    mView.setNameError(R.string.error_not_unique_name);
                            } else isNameUnique = true;
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Toast.makeText(mView.getActivityContext(),
                                    R.string.error_check_conn, Toast.LENGTH_SHORT).show();
                        }
                    });

    }

    /**
     * Crea el usuario con los parámetros dados en FirebaseAuth. Luego utiliza FirebaseStorage para
     * almacenar la foto de perfil del usuario en el servidor.
     * <p>
     * Si el proceso finaliza con éxito, se invoca
     * {@link #storeUserDataAndFinish(Uri, String, String, LatLng, Long, ArrayList)}
     * para almacenar el usuario en la base de datos.
     *
     * @param email                     dirección de email
     * @param pass                      contraseña
     * @param name                      nombre
     * @param croppedImageFileSystemUri ruta del archivo de imagen utilizado como foto de perfil
     * @param age                       edad
     * @param city                      ciudad
     * @param coords                    coordenadas de la ciudad
     * @param sportsList                lista de {@link Sport} que practica el usuario
     * @return true si los argumentos son válidos, false en caso contrario.
     * @see <a href= "https://firebase.google.com/docs/reference/admin/java/reference/com/google/firebase/auth/FirebaseAuth">
     * FirebaseAuth</a>
     * @see <a href= "https://firebase.google.com/docs/reference/android/com/google/firebase/storage/FirebaseStorage">
     * FirebaseStorage</a>
     */
    @Override
    public boolean createAuthUser(final String email, String pass, final String name,
                                  final Uri croppedImageFileSystemUri, final String age,
                                  final String city, final LatLng coords, final ArrayList<Sport> sportsList) {
        if (validateArguments(email, pass, name, croppedImageFileSystemUri, age, city, coords, sportsList)) {
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, pass)
                    .addOnCompleteListener(mView.getHostActivity(), new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(mView.getActivityContext(), R.string.toast_login_success, Toast.LENGTH_SHORT).show();

                                //Add email to emails logged table
                                ContentValues cv = new ContentValues();
                                cv.put(SportteamContract.EmailLoggedEntry.EMAIL, email);
                                mView.getActivityContext().getContentResolver()
                                        .insert(SportteamContract.EmailLoggedEntry.CONTENT_EMAIL_LOGGED_URI, cv);

                                if (croppedImageFileSystemUri != null
                                        && croppedImageFileSystemUri.getLastPathSegment() != null) {
                                    // Get a reference to store file at chat_photos/<FILENAME>
                                    StorageReference mChatPhotosStorageReference = FirebaseStorage.getInstance().getReference()
                                            .child(FirebaseDBContract.Storage.PROFILE_PICTURES);
                                    StorageReference photoRef = mChatPhotosStorageReference
                                            .child(croppedImageFileSystemUri.getLastPathSegment());

                                    // Create the file metadata
                                    StorageMetadata metadata = new StorageMetadata.Builder()
                                            .setContentType("image/jpeg")
                                            .build();

                                    // Upload file and metadata to the path 'images/name.jpg' into Firebase Storage
                                    UploadTask uploadTask = photoRef.putFile(croppedImageFileSystemUri, metadata);

                                    // Listen for errors, and completion of the upload.
                                    uploadTask.addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception exception) {
                                            // Handle unsuccessful uploads
                                            Log.e(TAG, "createAuthUser:putFile:onFailure: ", exception);
                                        }
                                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                            // Handle successful uploads on complete
                                            /* https://stackoverflow.com/a/42616488/4235666 */
                                            @SuppressWarnings("VisibleForTests")
                                            StorageMetadata metadata = taskSnapshot.getMetadata();
                                            if (metadata != null) {
                                                final Uri downloadUrl = metadata.getDownloadUrl();
                                                if (downloadUrl != null)
                                                    storeUserDataAndFinish(downloadUrl, name,
                                                            city, coords, Long.parseLong(age), sportsList);
                                                else
                                                    storeUserDataAndFinish(null, name,
                                                            city, coords, Long.parseLong(age), sportsList);
                                            } else
                                                storeUserDataAndFinish(null, name,
                                                        city, coords, Long.parseLong(age), sportsList);
                                        }
                                    });
                                } else {
                                    storeUserDataAndFinish(null, name,
                                            city, coords, Long.parseLong(age), sportsList);
                                }
                            } else {
                                mView.showContent();
                                Toast.makeText(mView.getActivityContext(),
                                        R.string.toast_login_failure, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }).addOnFailureListener(mView.getHostActivity(), new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(mView.getActivityContext(),
                            R.string.toast_create_user_fail, Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            });
            return true;
        } else return false;
    }

    /**
     * Método utilizado en la creación de usuario para validar los datos antes de la creación.
     *
     * @param email                     dirección de email
     * @param pass                      contraseña
     * @param name                      nombre
     * @param croppedImageFileSystemUri ruta del archivo de imagen utilizado como foto de perfil
     * @param age                       edad
     * @param city                      ciudad
     * @param coords                    coordenadas de la ciudad
     * @param sportsList                lista de {@link Sport} que practica el usuario
     * @return true si los argumentos son válidos, false en caso contrario.
     */
    private boolean validateArguments(String email, String pass, String name,
                                      Uri croppedImageFileSystemUri, String age,
                                      String city, LatLng coords, ArrayList<Sport> sportsList) {

        if (!isEmailUnique || email == null || TextUtils.isEmpty(email)) {
            Toast.makeText(mView.getActivityContext(), R.string.error_invalid_email, Toast.LENGTH_SHORT).show();
            Log.e(TAG, "validateArguments: Email error " + email);
            return false;
        }

        if (pass == null || TextUtils.isEmpty(pass) || pass.length() < 6) {
            Toast.makeText(mView.getActivityContext(), R.string.error_invalid_password, Toast.LENGTH_SHORT).show();
            Log.e(TAG, "validateArguments: Password error " + pass);
            return false;
        }

        if (!isNameUnique || name == null || TextUtils.isEmpty(name)) {
            Toast.makeText(mView.getActivityContext(), R.string.error_invalid_name, Toast.LENGTH_SHORT).show();
            Log.e(TAG, "validateArguments: Name error " + name);
            return false;
        }

        if (croppedImageFileSystemUri != null) {
            String path = croppedImageFileSystemUri.getPath();
            if (path != null) {
                File file = new File(path);
                if (!file.exists()) {
                    Toast.makeText(mView.getActivityContext(), R.string.error_invalid_photo, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "validateArguments: Photo error " + croppedImageFileSystemUri);
                    return false;
                }
            }
        }

        if (city == null || TextUtils.isEmpty(city)) {
            Toast.makeText(mView.getActivityContext(), R.string.error_invalid_city, Toast.LENGTH_SHORT).show();
            Log.e(TAG, "validateArguments: City error " + city);
            return false;
        }

        if (coords == null || (coords.latitude == 0 && coords.longitude == 0)) {
            Toast.makeText(mView.getActivityContext(), R.string.error_invalid_city, Toast.LENGTH_SHORT).show();
            Log.e(TAG, "validateArguments: Coordinates error " + coords);
            return false;
        }

        if (age == null || Long.parseLong(age) <= 12 || Long.parseLong(age) >= 100) {
            Toast.makeText(mView.getActivityContext(), R.string.error_incorrect_age, Toast.LENGTH_SHORT).show();
            Log.e(TAG, "validateArguments: Age error " + age);
            return false;
        }

        if (sportsList == null || sportsList.size() <= 0) {
            Toast.makeText(mView.getActivityContext(), R.string.error_invalid_sport_list, Toast.LENGTH_SHORT).show();
            Log.e(TAG, "validateArguments: Sport list error " + sportsList);
            return false;
        }

        return true;
    }

    /**
     * Utiliza {@link UserFirebaseActions#addUser(User)} para añadir un usuario a FirebaseDatabase.
     * También incluye los datos más relevantes en el objeto FirebaseUser. Por último, finaliza la
     * ejecución de la Actividad contenedora dado que el proceso de creación de usuario finaliza
     * correctamente.
     *
     * @param photoUri   ruta del archivo de imagen utilizado como foto de perfil
     * @param name       nombre
     * @param city       ciudad
     * @param coords     coordenadas de la ciudad
     * @param age        edad
     * @param sportsList lista de {@link Sport} que practica el usuario
     * @see <a href= "https://firebase.google.com/docs/reference/android/com/google/firebase/database/FirebaseDatabase">
     * FirebaseDatabase</a>
     * @see <a href= "https://firebase.google.com/docs/reference/android/com/google/firebase/auth/FirebaseUser">
     * FirebaseUser</a>
     */
    private void storeUserDataAndFinish(Uri photoUri, String name, String city, LatLng coords,
                                        Long age, ArrayList<Sport> sportsList) {
        FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
        if (fUser == null) return;

        UserProfileChangeRequest.Builder profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(name);
        if (photoUri != null) profileUpdates = profileUpdates.setPhotoUri(photoUri);
        fUser.updateProfile(profileUpdates.build());

        fUser.sendEmailVerification()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.i(TAG, "Verification email sent.");
                        }
                    }
                });

        String photoUriStr = "";
        if (photoUri != null) photoUriStr = photoUri.toString();
        User user = new User(fUser.getUid(), fUser.getEmail(),
                name, city, coords.latitude, coords.longitude, age, photoUriStr,
                sportsArrayToHashMap(sportsList));
        UserFirebaseActions.addUser(user);

        // Return to LoginActivity
        mView.getHostActivity().setResult(RESULT_OK);
        mView.getHostActivity().finish();
    }

    /**
     * Transforma un array de {@link Sport} en un {@link Map} utilizado para la creación del usuario
     *
     * @param sports lista de deportes practicados
     * @return lista de deportes practicados en un objeto Map
     */
    private Map<String, Double> sportsArrayToHashMap(List<Sport> sports) {
        HashMap<String, Double> result = new HashMap<>();
        for (Sport s : sports)
            result.put(s.getSportID(), s.getPunctuation());
        return result;
    }
}
