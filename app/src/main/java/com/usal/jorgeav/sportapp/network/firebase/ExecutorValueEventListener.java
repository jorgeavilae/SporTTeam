package com.usal.jorgeav.sportapp.network.firebase;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.Executor;

/**
 * Esta clase implementa los métodos del Listener de Firebase {@link ValueEventListener} por
 * heredar de él y delega su ejecución a un {@link Executor} que invocará el método abstracto
 * correspondiente en un hilo en segundo plano, diferente del hilo de ejecución principal.
 * <p>
 * De este modo, al cambiar los datos sobre los que escuche una instancia de este Listener, se
 * invocará {@link #onDataChange(DataSnapshot)} que usará el {@link Executor} para derivar la
 * ejecución del método correspondiente {@link #onDataChangeExecutor(DataSnapshot)} a otro hilo de
 * ejecución.
 *
 * @see <a href= "https://firebase.google.com/docs/reference/android/com/google/firebase/database/ValueEventListener">
 * ValueEventListener</a>
 */
public abstract class ExecutorValueEventListener implements ValueEventListener {
    /**
     * {@link Executor} utilizado para realizar las acciones en otro hilo
     */
    private final Executor executor;

    /**
     * Constructor
     *
     * @param executor instancia de {@link Executor} en la que se ejecutarán los métodos.
     */
    protected ExecutorValueEventListener(final Executor executor) {
        this.executor = executor;
    }

    /**
     * Invocado cuando cambian los datos sobre los que escucha este Listener. Utiliza
     * {@link #executor} para invocar {@link #onDataChangeExecutor(DataSnapshot)}
     *
     * @param dataSnapshot los datos nuevos que han provocado la invocación de este callback
     */
    @Override
    public void onDataChange(final DataSnapshot dataSnapshot) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                onDataChangeExecutor(dataSnapshot);
            }
        });
    }

    /**
     * Invocado cuando falla el Listener. Utiliza {@link #executor} para invocar
     * {@link #onCancelledExecutor(DatabaseError)}
     *
     * @param databaseError error que ha causado el fallo del Listener
     */
    @Override
    public void onCancelled(final DatabaseError databaseError) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                onCancelledExecutor(databaseError);
            }
        });
    }

    /**
     * Invocado cuando cambian los datos sobre los que escucha este Listener.
     *
     * @param dataSnapshot los datos nuevos que han provocado la invocación de este callback
     */
    protected abstract void onDataChangeExecutor(DataSnapshot dataSnapshot);

    /**
     * Invocado cuando falla el Listener.
     *
     * @param databaseError error que ha causado el fallo del Listener
     */
    protected abstract void onCancelledExecutor(DatabaseError databaseError);
}
