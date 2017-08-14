package com.usal.jorgeav.sportapp.network.firebase;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.Executor;

/* ChildEventListener & ValueEventListener callbacks are invoked in the UI main thread so for
 * heavy operations, such as store data in Content Provider, it recommended to use another thread
 * https://stackoverflow.com/a/39060380/4235666
 *
 * Source:
 * https://github.com/CodingDoug/white-label-event-app/commit/917ff279febce1977635226fe9181cc1ff099656
 * https://github.com/CodingDoug/white-label-event-app/blob/3adbbb62e2c94feb14fb709af02da1b4742915c1/app/src/main/java/com/hyperaware/conference/android/dagger/AppExecutorsModule.java
 */
public abstract class ExecutorValueEventListener implements ValueEventListener {
    private final Executor executor;
    protected ExecutorValueEventListener(final Executor executor) {
        this.executor = executor;
    }

    @Override
    public void onDataChange(final DataSnapshot dataSnapshot) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                onDataChangeExecutor(dataSnapshot);
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

    protected abstract void onDataChangeExecutor(DataSnapshot dataSnapshot);
    protected abstract void onCancelledExecutor(DatabaseError databaseError);
}
