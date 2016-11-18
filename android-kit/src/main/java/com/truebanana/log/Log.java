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

package com.truebanana.log;

/**
 * Custom logging class which you can globally turn on or off.
 */
public class Log {
    private static boolean isDebuggable = false;

    private Log() {
    }

    public static void setDebuggable(boolean debuggable) {
        isDebuggable = debuggable;
    }

    public static boolean isDebuggable() {
        return isDebuggable;
    }

    public static void v(String tag, String message) {
        if (isDebuggable) {
            android.util.Log.v("VERBOSE: " + tag, message != null ? message : "[null]");
        }
    }

    public static void d(String tag, String message) {
        if (isDebuggable) {
            android.util.Log.d("DEBUG: " + tag, message != null ? message : "[null]");
        }
    }

    public static void i(String tag, String message) {
        if (isDebuggable) {
            android.util.Log.i("INFO: " + tag, message != null ? message : "[null]");
        }
    }

    public static void w(String tag, String message) {
        if (isDebuggable) {
            android.util.Log.w("WARN: " + tag, message != null ? message : "[null]");
        }
    }

    public static void e(String tag, String message) {
        if (isDebuggable) {
            android.util.Log.e("ERROR: " + tag, message != null ? message : "[null]");
        }
    }

    public static void wtf(String tag, String message) {
        if (isDebuggable) {
            android.util.Log.wtf("WTF: " + tag, message != null ? message : "[null]");
        }
    }
}
