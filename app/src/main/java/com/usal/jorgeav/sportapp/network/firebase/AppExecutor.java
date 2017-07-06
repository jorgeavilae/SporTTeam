package com.usal.jorgeav.sportapp.network.firebase;

import android.os.Process;
import android.support.annotation.NonNull;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import javax.inject.Singleton;

/**
 * Created by Jorge Avila on 06/07/2017.
 */

public class AppExecutor {
    private static final int COMPUTE_THREAD_PRIORITY = Process.THREAD_PRIORITY_BACKGROUND;

    @Singleton
    public Executor provideAppExecutor() {
        final int fixed = Math.max(1, Runtime.getRuntime().availableProcessors() - 1);
        final Executor executor = Executors.newFixedThreadPool(fixed, new ThreadFactory() {
            @Override
            public Thread newThread(@NonNull final Runnable runnable) {
                return new PrioritizedThread(COMPUTE_THREAD_PRIORITY, runnable);
            }
        });
        return executor;
    }

    private static class PrioritizedThread extends Thread {
        private final int prio;
        private final Runnable runnable;

        public PrioritizedThread(int prio, Runnable runnable) {
            this.prio = prio;
            this.runnable = runnable;
        }

        @Override
        public void run() {
            Process.setThreadPriority(prio);
            super.run();
            runnable.run();
        }
    }

}