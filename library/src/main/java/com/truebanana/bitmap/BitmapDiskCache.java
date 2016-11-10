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

package com.truebanana.bitmap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.truebanana.cache.AbstractDiskLruCache;

import java.io.ByteArrayOutputStream;
import java.io.File;

/**
 * A {@link AbstractDiskLruCache} for {@link Bitmap}s with a default max size of 10MB if not specified.
 */
public class BitmapDiskCache extends AbstractDiskLruCache<Bitmap> {
    public BitmapDiskCache(Context context) {
        super(context);
    }

    public BitmapDiskCache(Context context, long maxSize) {
        super(context, maxSize);
    }

    public BitmapDiskCache(File directory) {
        super(directory);
    }

    public BitmapDiskCache(File directory, long maxSize) {
        super(directory, maxSize);
    }

    @Override
    public Bitmap get(String key) {
        byte[] data = getData(key);
        return BitmapFactory.decodeByteArray(data, 0, data.length);
    }

    @Override
    public void put(String key, Bitmap item) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        item.compress(Bitmap.CompressFormat.PNG, 100, os);
        putData(key, os.toByteArray());
    }
}