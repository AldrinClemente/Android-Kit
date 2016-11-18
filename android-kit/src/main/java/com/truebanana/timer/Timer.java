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

package com.truebanana.timer;

import java.util.HashMap;
import java.util.Map;

/**
 * Includes utility methods for timing.
 */
public class Timer {
    private static final String DEFAULT_TIMER_KEY = "default";
    private static Map<String, Long> timers = new HashMap<>();

    public static void start() {
        start(DEFAULT_TIMER_KEY);
    }

    public static void start(String timerKey) {
        timers.put(timerKey, System.nanoTime());
    }

    public static long getElapsedTime() {
        return getElapsedTime(DEFAULT_TIMER_KEY);
    }

    public static long getElapsedTime(String timerKey) {
        long now = System.nanoTime();
        if (timers.containsKey(timerKey)) {
            long before = timers.get(timerKey);
            return now - before;
        }
        return 0;
    }

    public static void reset() {
        reset(DEFAULT_TIMER_KEY);
    }

    public static void reset(String timerKey) {
        timers.put(timerKey, System.nanoTime());
    }

    public static long stop() {
        return stop(DEFAULT_TIMER_KEY);
    }

    public static long stop(String timerKey) {
        long elapsedTime = getElapsedTime(timerKey);
        timers.remove(timerKey);
        return elapsedTime;
    }
}