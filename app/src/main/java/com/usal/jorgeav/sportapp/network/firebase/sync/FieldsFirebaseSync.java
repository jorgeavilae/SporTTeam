package com.usal.jorgeav.sportapp.network.firebase.sync;

import android.content.ContentValues;
import android.text.TextUtils;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.usal.jorgeav.sportapp.MyApplication;
import com.usal.jorgeav.sportapp.data.Field;
import com.usal.jorgeav.sportapp.data.provider.SportteamContract;
import com.usal.jorgeav.sportapp.network.firebase.AppExecutor;
import com.usal.jorgeav.sportapp.network.firebase.ExecutorChildEventListener;
import com.usal.jorgeav.sportapp.network.firebase.ExecutorValueEventListener;
import com.usal.jorgeav.sportapp.network.firebase.FirebaseDBContract;
import com.usal.jorgeav.sportapp.utils.UtilesContentValues;

import java.util.List;

@SuppressWarnings("WeakerAccess")
public class FieldsFirebaseSync {
    private static final String TAG = FieldsFirebaseSync.class.getSimpleName();

    public static void loadAField(String fieldId) {
        if (fieldId == null || TextUtils.isEmpty(fieldId)) return;
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference(FirebaseDBContract.TABLE_FIELDS).child(fieldId);

        ref.addListenerForSingleValueEvent(new ExecutorValueEventListener(AppExecutor.getInstance().getExecutor()) {
            @Override
            public void onDataChangeExecutor(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Field field = dataSnapshot.child(FirebaseDBContract.DATA).getValue(Field.class);
                    if (field == null) {
                        Log.e(TAG, "loadAField: onDataChangeExecutor: Error parsing Field");
                        return;
                    }
                    field.setId(dataSnapshot.getKey());

                    // Store Field in Content Provider
                    ContentValues cvData = UtilesContentValues.fieldToContentValues(field);
                    MyApplication.getAppContext().getContentResolver()
                            .insert(SportteamContract.FieldEntry.CONTENT_FIELD_URI, cvData);

                    // Delete Field's courts in case some of them was deleted
                    MyApplication.getAppContext().getContentResolver()
                            .delete(SportteamContract.FieldSportEntry.CONTENT_FIELD_SPORT_URI,
                                    SportteamContract.FieldSportEntry.FIELD_ID + " = ? ",
                                    new String[]{field.getId()});

                    // Store Field's courts in ContentProvider
                    List<ContentValues> cvSports = UtilesContentValues.fieldSportToContentValues(field);
                    MyApplication.getAppContext().getContentResolver()
                            .bulkInsert(SportteamContract.FieldSportEntry.CONTENT_FIELD_SPORT_URI,
                                    cvSports.toArray(new ContentValues[cvSports.size()]));
                }
            }

            @Override
            public void onCancelledExecutor(DatabaseError databaseError) {

            }
        });
    }

    public static ExecutorChildEventListener getListenerToLoadFieldsFromCity() {
        return new ExecutorChildEventListener(AppExecutor.getInstance().getExecutor()) {
            @Override
            public void onChildAddedExecutor(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.exists()) {
                    Field field = dataSnapshot.child(FirebaseDBContract.DATA).getValue(Field.class);
                    if (field == null) {
                        Log.e(TAG, "loadFieldsFromCity: onDataChangeExecutor: Error parsing Field from "
                                + dataSnapshot.child(FirebaseDBContract.DATA).getRef());
                        return;
                    }
                    field.setId(dataSnapshot.getKey());

                    ContentValues cvData = UtilesContentValues.fieldToContentValues(field);
                    MyApplication.getAppContext().getContentResolver()
                            .insert(SportteamContract.FieldEntry.CONTENT_FIELD_URI, cvData);

                    MyApplication.getAppContext().getContentResolver()
                            .delete(SportteamContract.FieldSportEntry.CONTENT_FIELD_SPORT_URI,
                                    SportteamContract.FieldSportEntry.FIELD_ID + " = ? ",
                                    new String[]{field.getId()});

                    List<ContentValues> cvSports = UtilesContentValues.fieldSportToContentValues(field);
                    MyApplication.getAppContext().getContentResolver()
                            .bulkInsert(SportteamContract.FieldSportEntry.CONTENT_FIELD_SPORT_URI,
                                    cvSports.toArray(new ContentValues[cvSports.size()]));
                }
            }

            @Override
            public void onChildChangedExecutor(DataSnapshot dataSnapshot, String s) {
                onChildAdded(dataSnapshot, s);
            }

            @Override
            public void onChildRemovedExecutor(DataSnapshot dataSnapshot) {
                String fieldId = dataSnapshot.getKey();
                MyApplication.getAppContext().getContentResolver()
                        .delete(SportteamContract.FieldEntry.CONTENT_FIELD_URI,
                                SportteamContract.FieldEntry.FIELD_ID + " = ? ",
                                new String[]{fieldId});
                MyApplication.getAppContext().getContentResolver()
                        .delete(SportteamContract.FieldSportEntry.CONTENT_FIELD_SPORT_URI,
                                SportteamContract.FieldSportEntry.FIELD_ID + " = ? ",
                                new String[]{fieldId});
            }

            @Override
            public void onChildMovedExecutor(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelledExecutor(DatabaseError databaseError) {

            }
        };
    }
}
