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

import android.graphics.Bitmap;
import android.widget.ImageView;

import com.truebanana.http.BitmapResponseListener;

/**
 * A simple helper class for loading and displaying {@link Bitmap}s.
 * This class also uses {@link BitmapMemCache} to cache previously loaded {@link Bitmap}s in memory.
 */
public class BitmapLoader {
    private static BitmapMemCache cache;

    private static void initializeCacheAsNeeded() {
        if (cache == null) {
            cache = new BitmapMemCache();
        }
    }

    public static void displayBitmap(final String url, int width, int height, final ImageView imageView) {
        initializeCacheAsNeeded();
        Bitmap bitmap = cache.get(url);
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
        } else {
            BitmapUtils.decodeFromURL(url, new BitmapResponseListener(width, height) {
                @Override
                public void onDecodingSuccessful(Bitmap bitmap) {
                    imageView.setImageBitmap(bitmap);
                    cache.put(url, bitmap);
                }

                @Override
                public void onDecodingFailed() {
                }
            });
        }
    }
}