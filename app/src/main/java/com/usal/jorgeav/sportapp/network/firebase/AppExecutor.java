package com.usal.jorgeav.sportapp.network.firebase;

import android.os.Process;
import android.support.annotation.NonNull;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
// This class acts as Singleton AppExecutor to provide an Executor thread pool.
// In that Executor pool are attached heavy operations started in Firebase listeners events,
// such as Content Provider store operations
public class AppExecutor {
    // A pool of background threads
    private final Executor mExecutor;
    public Executor getExecutor() { return mExecutor; }

    // Creates a single static instance of AppExecutor. Every execution uses the instance.
    private static AppExecutor sInstance = new AppExecutor();
    public static AppExecutor getInstance() { return sInstance; }

    // Constructs the thread pool used to decode dataSnapshot and insert in Content Provider.
    private AppExecutor() {
        final int fixed = Math.max(1, Runtime.getRuntime().availableProcessors() - 1);
        mExecutor = Executors.newFixedThreadPool(fixed, new ThreadFactory() {
            @Override
            public Thread newThread(@NonNull final Runnable runnable) {
                return new PrioritizedThread(Process.THREAD_PRIORITY_BACKGROUND, runnable);
            }
        });
    }

    private static class PrioritizedThread extends Thread {
        private final int priority;
        private final Runnable runnable;

        PrioritizedThread(int priority, Runnable runnable) {
            this.priority = priority;
            this.runnable = runnable;
        }

        @Override
        public void run() {
            Process.setThreadPriority(priority);
            super.run();
            runnable.run();
        }
    }
}




