package com.usal.jorgeav.sportapp.network.firebase;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.util.concurrent.Executor;

/**
 * Esta clase implementa los métodos del Listener de Firebase {@link ChildEventListener} por
 * heredar de él y delega su ejecución a un {@link Executor} que invocará el método abstracto
 * correspondiente en un hilo en segundo plano, diferente del hilo de ejecución principal.
 * <p>
 * De este modo, al añadir datos sobre la rama que escucha una instancia de este Listener, se
 * invocará {@link #onChildAdded(DataSnapshot, String)} que usará el {@link Executor} para derivar
 * la ejecución del método correspondiente {@link #onChildAddedExecutor(DataSnapshot, String)} a
 * otro hilo de ejecución.
 *
 * @see <a href= "https://firebase.google.com/docs/reference/android/com/google/firebase/database/ChildEventListener">
 * ChildEventListener</a>
 */
public abstract class ExecutorChildEventListener implements ChildEventListener {
    /**
     * {@link Executor} utilizado para realizar las acciones en otro hilo
     */
    private final Executor executor;

    /**
     * Constructor
     *
     * @param executor instancia de {@link Executor} en la que se ejecutarán los métodos.
     */
    protected ExecutorChildEventListener(final Executor executor) {
        this.executor = executor;
    }

    /**
     * Invocado cuando se añade un objeto de datos sobre la rama que escucha este Listener. Utiliza
     * {@link #executor} para invocar {@link #onChildAddedExecutor(DataSnapshot, String)}
     *
     * @param dataSnapshot los datos nuevos que han provocado la invocación de este callback
     * @param s            clave del objeto previo de datos al que se hermana el objeto nuevo
     */
    @Override
    public void onChildAdded(final DataSnapshot dataSnapshot, final String s) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                onChildAddedExecutor(dataSnapshot, s);
            }
        });
    }

    /**
     * Invocado cuando cambia un objeto de datos de la rama que escucha este Listener. Utiliza
     * {@link #executor} para invocar {@link #onChildChangedExecutor(DataSnapshot, String)}
     *
     * @param dataSnapshot los datos nuevos que han provocado la invocación de este callback
     * @param s            clave del objeto previo de datos al que se hermana el objeto que ha cambiado
     */
    @Override
    public void onChildChanged(final DataSnapshot dataSnapshot, final String s) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                onChildChangedExecutor(dataSnapshot, s);
            }
        });
    }

    /**
     * Invocado cuando se borra un objeto de datos sobre la rama que escucha este Listener. Utiliza
     * {@link #executor} para invocar {@link #onChildRemovedExecutor(DataSnapshot)}
     *
     * @param dataSnapshot los datos borrados que han provocado la invocación de este callback
     */
    @Override
    public void onChildRemoved(final DataSnapshot dataSnapshot) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                onChildRemovedExecutor(dataSnapshot);
            }
        });
    }

    /**
     * Invocado cuando se mueve un objeto de datos sobre la rama que escucha este Listener. Utiliza
     * {@link #executor} para invocar {@link #onChildMovedExecutor(DataSnapshot, String)}
     *
     * @param dataSnapshot los datos movidos que han provocado la invocación de este callback
     * @param s            clave del objeto previo de datos al que se hermana el objeto movido
     */
    @Override
    public void onChildMoved(final DataSnapshot dataSnapshot, final String s) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                onChildMovedExecutor(dataSnapshot, s);
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
     * Invocado cuando se añade un objeto de datos sobre la rama que escucha este Listener.
     *
     * @param dataSnapshot los datos nuevos que han provocado la invocación de este callback
     * @param s            clave del objeto previo de datos al que se hermana el objeto nuevo
     */
    protected abstract void onChildAddedExecutor(DataSnapshot dataSnapshot, String s);

    /**
     * Invocado cuando cambia un objeto de datos de la rama que escucha este Listener.
     *
     * @param dataSnapshot los datos nuevos que han provocado la invocación de este callback
     * @param s            clave del objeto previo de datos al que se hermana el objeto que ha cambiado
     */
    protected abstract void onChildChangedExecutor(DataSnapshot dataSnapshot, String s);

    /**
     * Invocado cuando se borra un objeto de datos sobre la rama que escucha este Listener.
     *
     * @param dataSnapshot los datos borrados que han provocado la invocación de este callback
     */
    protected abstract void onChildRemovedExecutor(DataSnapshot dataSnapshot);

    /**
     * Invocado cuando se mueve un objeto de datos sobre la rama que escucha este Listener.
     *
     * @param dataSnapshot los datos movidos que han provocado la invocación de este callback
     * @param s            clave del objeto previo de datos al que se hermana el objeto movido
     */
    protected abstract void onChildMovedExecutor(DataSnapshot dataSnapshot, String s);

    /**
     * Invocado cuando falla el Listener.
     *
     * @param databaseError error que ha causado el fallo del Listener
     */
    protected abstract void onCancelledExecutor(DatabaseError databaseError);
}
