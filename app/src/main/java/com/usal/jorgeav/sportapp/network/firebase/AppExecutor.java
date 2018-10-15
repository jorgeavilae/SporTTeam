package com.usal.jorgeav.sportapp.network.firebase;

import android.os.Process;
import android.support.annotation.NonNull;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * Esta clase provee una cola de hilos sobre los que ejecutar tareas en segundo plano. Opera bajo
 * el patrón Singleton a lo largo de toda la ejecución para asegurar que sólo exista una cola de
 * hilos.
 * <p>
 * Esta pensada para encolar tareas largas que no puedan realizarse en el hilo principal
 * de ejecución. En la práctica, se usa para almacenar en el Proveedor de Contenido los resultados
 * de los Listeners vinculados a los datos de Firebase Realtime Database.
 * <p>
 * Ofrece acceso a un {@link Executor} en el que ejecutar las tareas. Este Executor está compuesto
 * por una cola de {@link PrioritizedThread} que se ejecutan en segundo plano.
 */
public class AppExecutor {

    /**
     * Cola de hilos
     */
    private final Executor mExecutor;

    /**
     * Devuelve la referencia a la cola de hilos.
     *
     * @return referencia a {@link #mExecutor}
     */
    public Executor getExecutor() {
        return mExecutor;
    }

    /**
     * Instancia estática de esta clase, necesaria para implementar el patrón Singleton.
     */
    private static AppExecutor sInstance = new AppExecutor();

    /**
     * Devuelve la referencia a la instancia única de esta clase.
     *
     * @return referencia a {@link #sInstance}
     */
    public static AppExecutor getInstance() {
        return sInstance;
    }

    /**
     * Constructor de esta clase. Crea una nueva cola de hilos para {@link #mExecutor}. Para aplicar
     * la prioridad de background a los hilos de la cola y que se ejecuten en segundo plano, utiliza
     * {@link PrioritizedThread}.
     */
    private AppExecutor() {
        final int fixed = Math.max(1, Runtime.getRuntime().availableProcessors() - 1);
        mExecutor = Executors.newFixedThreadPool(fixed, new ThreadFactory() {
            @Override
            public Thread newThread(@NonNull final Runnable runnable) {
                return new PrioritizedThread(Process.THREAD_PRIORITY_BACKGROUND, runnable);
            }
        });
    }

    /**
     * Clase derivada de {@link Thread} para describir el tipo de hilo que se crea para la cola.
     * <p>
     * El hilo debe ejecutarse en segundo plano, pero las prioridades proporcionadas por la clase
     * {@link Thread} no son suficientes para especificar esto. Sin embargo,
     * {@link Process#THREAD_PRIORITY_BACKGROUND} si cumple esta función. El problema es que sólo
     * puede aplicarse esta prioridad desde dentro del propio hilo. Por ello, se crea esta clase,
     * se sobrescribe el método {@link #run()} y se aplica dicha prioridad.
     */
    private static class PrioritizedThread extends Thread {
        /**
         * Prioridad de este hilo
         */
        private final int priority;
        /**
         * Código que debe ejecutarse sobre este hilo
         */
        private final Runnable runnable;

        /**
         * Constructor
         *
         * @param priority prioridad de este hilo
         * @param runnable código que debe ejecutarse sobre este hilo
         */
        PrioritizedThread(int priority, Runnable runnable) {
            this.priority = priority;
            this.runnable = runnable;
        }

        /**
         * Invocado para comenzar la ejecución del hilo. Primero aplica la prioridad y luego
         * inicia la ejecución con {@link Runnable#run()}
         */
        @Override
        public void run() {
            Process.setThreadPriority(priority);
            super.run();
            runnable.run();
        }
    }
}




