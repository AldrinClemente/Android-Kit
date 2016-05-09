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

package com.truebanana.toast;

import android.content.Context;

/**
 * Includes convenience methods for showing and queueing toast messages.
 */
public class Toast {
    private static android.widget.Toast toast;

    /**
     * Convenience method to show a non-queuing toast
     *
     * @param context
     * @param resourceID The string resource ID of the message to display
     */
    public static void show(Context context, int resourceID) {
        show(context, resourceID, android.widget.Toast.LENGTH_LONG);
    }

    /**
     * Convenience method to show a non-queuing toast
     *
     * @param context
     * @param message The message to display
     */
    public static void show(Context context, String message) {
        show(context, message, android.widget.Toast.LENGTH_LONG);
    }

    /**
     * Convenience method to show a non-queuing toast
     *
     * @param context
     * @param resourceID The string resource ID of the message to display
     * @param duration   The display duration, which can either be {@link android.widget.Toast#LENGTH_SHORT} or {@link android.widget.Toast#LENGTH_LONG}
     */
    public static void show(Context context, int resourceID, int duration) {
        show(context, context.getResources().getString(resourceID), duration);
    }

    /**
     * Convenience method to show a non-queuing toast
     *
     * @param context
     * @param message  The message to display
     * @param duration The display duration, which can either be {@link android.widget.Toast#LENGTH_SHORT} or {@link android.widget.Toast#LENGTH_LONG}
     */
    public static void show(Context context, String message, int duration) {
        if (toast == null) {
            toast = android.widget.Toast.makeText(context, "", android.widget.Toast.LENGTH_LONG);
        }
        toast.setText(message);
        toast.setDuration(duration);
        toast.show();
    }

    /**
     * Convenience method to show a queuing toast
     *
     * @param context
     * @param resourceID The string resource ID of the message to display
     */
    public static void queue(Context context, int resourceID) {
        queue(context, resourceID, android.widget.Toast.LENGTH_LONG);
    }

    /**
     * Convenience method to show a queuing toast
     *
     * @param context
     * @param message The message to display
     */
    public static void queue(Context context, String message) {
        queue(context, message, android.widget.Toast.LENGTH_LONG);
    }

    /**
     * Convenience method to show a queuing toast
     *
     * @param context
     * @param resourceID The string resource ID of the message to display
     * @param duration   The display duration, which can either be {@link android.widget.Toast#LENGTH_SHORT} or {@link android.widget.Toast#LENGTH_LONG}
     */
    public static void queue(Context context, int resourceID, int duration) {
        queue(context, context.getResources().getString(resourceID), duration);
    }

    /**
     * Convenience method to show a queuing toast
     *
     * @param context
     * @param message  The message to display
     * @param duration The display duration, which can either be {@link android.widget.Toast#LENGTH_SHORT} or {@link android.widget.Toast#LENGTH_LONG}
     */
    public static void queue(Context context, String message, int duration) {
        android.widget.Toast toast = android.widget.Toast.makeText(context, "", android.widget.Toast.LENGTH_LONG);
        toast.setText(message);
        toast.setDuration(duration);
        toast.show();
    }
}