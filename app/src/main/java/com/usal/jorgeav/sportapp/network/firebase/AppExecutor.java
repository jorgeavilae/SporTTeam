package com.usal.jorgeav.sportapp.network.firebase;

import android.os.Process;
import android.support.annotation.NonNull;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/* ChildEventListener & ValueEventListener callbacks are invoked in the UI main thread so for
 * heavy operations, such as store data in Content Provider, it recommended to use another thread
 * https://stackoverflow.com/a/39060380/4235666
 *
 * Source:
 * https://github.com/CodingDoug/white-label-event-app/commit/917ff279febce1977635226fe9181cc1ff099656
 * https://github.com/CodingDoug/white-label-event-app/blob/3adbbb62e2c94feb14fb709af02da1b4742915c1/app/src/main/java/com/hyperaware/conference/android/dagger/AppExecutorsModule.java
 */
class AppExecutor {
    private static final int COMPUTE_THREAD_PRIORITY = Process.THREAD_PRIORITY_BACKGROUND;

    // A pool of background threads
    private final Executor mExecutor;
    private static AppExecutor sInstance = null;

    static  {
        // Creates a single static instance of AppExecutor
        sInstance = new AppExecutor();
    }

    public static AppExecutor getInstance() { return sInstance; }
    Executor getExecutor() { return mExecutor; }

    // Constructs the thread pool used to decode dataSnapshot and insert in Content Provider.
    private AppExecutor() {
        mExecutor = provideAppExecutor();
    }
    private Executor provideAppExecutor() {
        final int fixed = Math.max(1, Runtime.getRuntime().availableProcessors() - 1);
        return Executors.newFixedThreadPool(fixed, new ThreadFactory() {
            @Override
            public Thread newThread(@NonNull final Runnable runnable) {
                return new PrioritizedThread(COMPUTE_THREAD_PRIORITY, runnable);
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