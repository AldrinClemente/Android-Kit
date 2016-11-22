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

package com.truebanana.random;

import java.util.Random;

/**
 * Includes utility methods for generating random values. All methods use the same instance of
 * {@link Random}.
 * <p>
 * Do not use this for security and cryptography.
 */
public class RandomUtils {
    private static Random random = new Random();

    /**
     * Returns a random boolean.
     *
     * @return
     */
    public static boolean randomBoolean() {
        return random.nextBoolean();
    }

    /**
     * Returns a random integer.
     *
     * @return
     */
    public static int randomInt() {
        return random.nextInt();
    }

    /**
     * Returns a random integer within the specified range.
     *
     * @param min The minimum value (inclusive)
     * @param max The maximum value (inclusive)
     * @return
     */
    public static int randomInt(int min, int max) {
        return (int) randomLong(min, max);
    }

    /**
     * Returns a random long.
     *
     * @return
     */
    public static long randomLong() {
        return random.nextLong();
    }

    /**
     * Returns a random long within the specified range.
     *
     * @param min The minimum value (inclusive)
     * @param max The maximum value (inclusive)
     * @return
     */
    public static long randomLong(long min, long max) {
        return (long) (random.nextDouble() * ((max - min) + 1)) + min;
    }

    /**
     * Returns a random float between 0 (inclusive) and 1 (exclusive).
     *
     * @return
     */
    public static float randomFloat() {
        return random.nextFloat();
    }

    /**
     * Returns a random float within the specified range.
     *
     * @param min The minimum value (inclusive)
     * @param max The maximum value (exclusive)
     * @return
     */
    public static float randomFloat(float min, float max) {
        return (float) (random.nextDouble() * ((max - min) + 1)) + min;
    }

    /**
     * Returns a random double between 0 (inclusive) and 1 (exclusive).
     *
     * @return
     */
    public static double randomDouble() {
        return random.nextDouble();
    }

    /**
     * Returns a random double within the specified range.
     *
     * @param min The minimum value (inclusive)
     * @param max The maximum value (exclusive)
     * @return
     */
    public static double randomDouble(double min, double max) {
        return (random.nextDouble() * ((max - min) + 1)) + min;
    }

    /**
     * Returns random bytes.
     *
     * @param length The number of bytes to return
     * @return
     */
    public static byte[] randomBytes(int length) {
        byte[] bytes = new byte[length];
        random.nextBytes(bytes);
        return bytes;
    }
}