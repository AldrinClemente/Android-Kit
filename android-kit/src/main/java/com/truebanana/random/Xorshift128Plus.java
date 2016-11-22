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

public class Xorshift128Plus implements RandomNumberGenerator {
    private long state0, state1;

    public Xorshift128Plus() {
        state0 = Double.doubleToLongBits(Math.random());
        state1 = Double.doubleToLongBits(Math.random());
    }

    public long nextLong() {
        long a = state0;
        long b = state1;

        state0 = b;
        a ^= a << 23;
        a ^= a >> 18;
        a ^= b;
        a ^= b >> 5;
        state1 = a;

        return a + b;
    }

    @Override
    public int nextInt() {
        return (int) nextLong();
    }

    @Override
    public double nextDouble() {
        return Double.longBitsToDouble(nextLong());
    }

    @Override
    public float nextFloat() {
        return Float.intBitsToFloat(nextInt());
    }

    @Override
    public boolean nextBoolean() {
        return Long.signum(nextLong()) >= 0;
    }

    @Override
    public byte nextByte() {
        return (byte) nextLong();
    }

    @Override
    public short nextShort() {
        return (short) nextLong();
    }
}