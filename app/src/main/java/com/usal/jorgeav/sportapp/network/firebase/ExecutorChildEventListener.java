package com.usal.jorgeav.sportapp.network.firebase;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.util.concurrent.Executor;

/* ChildEventListener & ValueEventListener callbacks are invoked in the UI main thread so for
 * heavy operations, such as store data in Content Provider, it recommended to use another thread
 * https://stackoverflow.com/a/39060380/4235666
 *
 * Source:
 * https://github.com/CodingDoug/white-label-event-app/commit/917ff279febce1977635226fe9181cc1ff099656
 * https://github.com/CodingDoug/white-label-event-app/blob/3adbbb62e2c94feb14fb709af02da1b4742915c1/app/src/main/java/com/hyperaware/conference/android/dagger/AppExecutorsModule.java
 */
public abstract class ExecutorChildEventListener implements ChildEventListener {
    private final Executor executor;
    protected ExecutorChildEventListener(final Executor executor) {
        this.executor = executor;
    }

    @Override
    public void onChildAdded(final DataSnapshot dataSnapshot, final String s) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                onChildAddedExecutor(dataSnapshot, s);
            }
        });
    }

    @Override
    public void onChildChanged(final DataSnapshot dataSnapshot, final String s) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                onChildChangedExecutor(dataSnapshot, s);
            }
        });
    }

    @Override
    public void onChildRemoved(final DataSnapshot dataSnapshot) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                onChildRemovedExecutor(dataSnapshot);
            }
        });
    }

    @Override
    public void onChildMoved(final DataSnapshot dataSnapshot, final String s) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                onChildMovedExecutor(dataSnapshot, s);
            }
        });
    }

    @Override
    public void onCancelled(final DatabaseError databaseError) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                onCancelledExecutor(databaseError);
            }
        });
    }

    protected abstract void onChildAddedExecutor(DataSnapshot dataSnapshot, String s);
    protected abstract void onChildChangedExecutor(DataSnapshot dataSnapshot, String s);
    protected abstract void onChildRemovedExecutor(DataSnapshot dataSnapshot);
    protected abstract void onChildMovedExecutor(DataSnapshot dataSnapshot, String s);
    protected abstract void onCancelledExecutor(DatabaseError databaseError);
}
