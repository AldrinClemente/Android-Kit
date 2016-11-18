/*
 * MIT License
 *
 * Copyright (c) 2016 Aldrin Clemente
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.truebanana.async;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.Executor;


/**
 * Includes static methods to simplify execution of {@link Runnable}s and {@link BackgroundTask}s
 * asynchronously instead of using {@link AsyncTask}s. If your code is running on a different thread,
 * this class can also help you post {@link Runnable}s on the main thread.
 */
public class Async {
    private static Handler mainHandler = new Handler(Looper.getMainLooper());
    private static Executor parallelExecutor = AsyncTask.THREAD_POOL_EXECUTOR;
    private static Executor serialExecutor = AsyncTask.SERIAL_EXECUTOR;

    /**
     * Executes a {@link BackgroundTask} asynchronously.
     *
     * @param task              The {@link BackgroundTask} to execute
     * @param executeInParallel <strong>true</strong> to execute the task in parallel with other
     *                          tasks or <strong>false</strong> to execute serially.
     */
    public static void executeAsync(final BackgroundTask task, boolean executeInParallel) {
        AsyncTask<Void, Void, Object> asyncTask = new AsyncTask<Void, Void, Object>() {
            @Override
            protected Object doInBackground(Void... params) {
                return task.doInBackground();
            }

            @Override
            protected void onPostExecute(Object result) {
                task.onPostExecute(result);
            }
        };

        asyncTask.executeOnExecutor(executeInParallel ? parallelExecutor : serialExecutor);
    }

    /**
     * Executes a {@link BackgroundTask} asynchronously in parallel with other tasks.
     *
     * @param task The {@link BackgroundTask} to execute
     */
    public static void executeAsync(final BackgroundTask task) {
        executeAsync(task, true);
    }

    // Runnable
    // ************************************************************************

    /**
     * Runs a {@link Runnable} asynchronously.
     *
     * @param runnable          The {@link Runnable} to run
     * @param executeInParallel <strong>true</strong> to run in parallel with other
     *                          runnables or <strong>false</strong> to run serially.
     */
    public static void executeAsync(Runnable runnable, boolean executeInParallel) {
        if (executeInParallel) {
            parallelExecutor.execute(runnable);
        } else {
            serialExecutor.execute(runnable);
        }
    }

    /**
     * Runs a {@link Runnable} asynchronously in parallel with other runnables.
     *
     * @param runnable The {@link Runnable} to run
     */
    public static void executeAsync(Runnable runnable) {
        executeAsync(runnable, true);
    }

    /**
     * Posts a {@link Runnable} in the main thread.
     *
     * @param runnable The {@link Runnable} to run
     */
    public static void executeInMainThread(Runnable runnable) {
        mainHandler.post(runnable);
    }

    /**
     * Posts a {@link Runnable} in the main thread with a set delay.
     *
     * @param runnable    The {@link Runnable} to run
     * @param delayMillis The delay in milliseconds
     */
    public static void executeInMainThread(Runnable runnable, long delayMillis) {
        mainHandler.postDelayed(runnable, delayMillis);
    }
}