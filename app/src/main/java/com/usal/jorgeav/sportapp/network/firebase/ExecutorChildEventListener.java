package com.usal.jorgeav.sportapp.network.firebase;

import android.os.AsyncTask;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

/**
 * Created by Jorge Avila on 27/06/2017.
 */

/* ChildEventListener callbacks are invoked in the UI main thread so for heavy operations,
 * such as store data in Content Provider, it recommended to use another thread
 * https://stackoverflow.com/a/39060380/4235666
 *
 * Source:
 * https://github.com/CodingDoug/white-label-event-app/commit/917ff279febce1977635226fe9181cc1ff099656
 * From:
 * https://stackoverflow.com/a/35777599/4235666
 */
// TODO: 27/06/2017 memory leak
public abstract class ExecutorChildEventListener implements ChildEventListener {
//    protected final AsyncTask<Void, Void, Void> executor;
    public ExecutorChildEventListener() {
//        this.executor = executor;
    }

    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
        new AsyncTask<Object, Void, Void>() {
            @Override
            protected Void doInBackground(Object... objects) {
                onChildAddedExecutor((DataSnapshot) objects[0], (String) objects[1]);
                return null;
            }
        }.execute(dataSnapshot, s);
    }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
        new AsyncTask<Object, Void, Void>() {
            @Override
            protected Void doInBackground(Object... objects) {
                onChildChangedExecutor((DataSnapshot) objects[0], (String) objects[1]);
                return null;
            }
        }.execute(dataSnapshot, s);

    }

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {
        new AsyncTask<Object, Void, Void>() {
            @Override
            protected Void doInBackground(Object... objects) {
                onChildRemovedExecutor((DataSnapshot) objects[0]);
                return null;
            }
        }.execute(dataSnapshot);
    }

    @Override
    public void onChildMoved(DataSnapshot dataSnapshot, String s) {
        new AsyncTask<Object, Void, Void>() {
            @Override
            protected Void doInBackground(Object... objects) {
                onChildMovedExecutor((DataSnapshot) objects[0], (String) objects[1]);
                return null;
            }
        }.execute(dataSnapshot, s);

    }

    @Override
    public void onCancelled(DatabaseError databaseError) {
        new AsyncTask<Object, Void, Void>() {
            @Override
            protected Void doInBackground(Object... objects) {
                onCancelledExecutor((DatabaseError) objects[0]);
                return null;
            }
        }.execute(databaseError);

    }

    protected abstract void onChildAddedExecutor(DataSnapshot dataSnapshot, String s);
    protected abstract void onChildChangedExecutor(DataSnapshot dataSnapshot, String s);
    protected abstract void onChildRemovedExecutor(DataSnapshot dataSnapshot);
    protected abstract void onChildMovedExecutor(DataSnapshot dataSnapshot, String s);
    protected abstract void onCancelledExecutor(DatabaseError databaseError);
}
