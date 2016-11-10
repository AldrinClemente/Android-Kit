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
import android.widget.ImageView;

import com.truebanana.async.Async;
import com.truebanana.async.BackgroundTask;
import com.truebanana.http.BitmapResponseListener;
import com.truebanana.http.HTTPRequest;
import com.truebanana.log.Log;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * A simple helper class for loading and displaying {@link Bitmap}s.
 * This class also uses {@link BitmapMemCache} to cache previously loaded {@link Bitmap}s in memory.
 */
public class BitmapLoader {
    private static BitmapMemCache memCache;
    private static BitmapDiskCache diskCache;
    private static RequestOptions defaultRequestOptions = new RequestOptions();

    private static void initializeCacheAsNeeded() {
        if (memCache == null) {
            memCache = new BitmapMemCache();
        }
    }

    public static void enableDiskCache(Context context) {
        diskCache = new BitmapDiskCache(new File(context.getCacheDir(), "images"));
        Log.d("BitmapLoader", "Disk cache enabled");
    }

    /**
     * Sets the default {@link RequestOptions} to use for all requests.
     *
     * @param options
     */
    public static void setDefaultRequestOptions(RequestOptions options) {
        BitmapLoader.defaultRequestOptions = options;
    }

    /**
     * Asynchronously decodes a {@link Bitmap} from a URL and displays it in an {@link ImageView}.
     *
     * @param url       The URL of the {@link Bitmap} to display
     * @param imageView The {@link ImageView} where the {@link Bitmap} will be displayed
     */
    public static void displayBitmap(final String url, final ImageView imageView) {
        displayBitmap(url, imageView, defaultRequestOptions);
    }

    /**
     * Asynchronously decodes a {@link Bitmap} from a URL and displays it in an {@link ImageView}.
     *
     * @param url       The URL of the {@link Bitmap} to display
     * @param imageView The {@link ImageView} where the {@link Bitmap} will be displayed
     * @param options   The {@link RequestOptions} to use for this request
     */
    public static void displayBitmap(final String url, final ImageView imageView, RequestOptions options) {
        displayBitmap(url, 0, 0, imageView, options);
    }

    /**
     * Asynchronously decodes a {@link Bitmap} from a URL, resizes it first then displays it in an {@link ImageView}.
     * Use this to save memory when you have to display unnecessarily large {@link Bitmap}s.
     *
     * @param url       The URL of the {@link Bitmap} to display
     * @param width     The desired width, in pixels
     * @param height    The desired height, in pixels
     * @param imageView The {@link ImageView} where the {@link Bitmap} will be displayed
     */
    public static void displayBitmap(final String url, int width, int height, final ImageView imageView) {
        displayBitmap(url, width, height, imageView, defaultRequestOptions);
    }

    /**
     * Asynchronously decodes a {@link Bitmap} from a URL, resizes it first then displays it in an {@link ImageView}.
     * Use this to save memory when you have to display unnecessarily large {@link Bitmap}s.
     *
     * @param url       The URL of the {@link Bitmap} to display
     * @param width     The desired width, in pixels
     * @param height    The desired height, in pixels
     * @param imageView The {@link ImageView} where the {@link Bitmap} will be displayed
     * @param options   The {@link RequestOptions} to use for this request
     */
    public static void displayBitmap(final String url, int width, int height, final ImageView imageView, RequestOptions options) {
        initializeCacheAsNeeded();
        if (memCache.contains(url)) {
            Log.d("BitmapLoader", "Image in mem cache, loading from memory...");
            imageView.setImageBitmap(memCache.get(url));
        } else if (diskCache != null && diskCache.contains(url)) {
            Log.d("BitmapLoader", "Image in disk cache, loading from disk...");
            Async.executeAsync(new BackgroundTask<Bitmap>() {
                @Override
                public Bitmap doInBackground() {
                    return diskCache.get(url);
                }

                @Override
                public void onPostExecute(Bitmap result) {
                    imageView.setImageBitmap(result);
                    memCache.put(url, result);
                }
            }, true);
        } else {
            Log.d("BitmapLoader", "Image not cached, retrieving...");
            HTTPRequest.create(url)
                    .addHeaders(options.headers)
                    .setConnectTimeout(options.connectTimeout)
                    .setReadTimeout(options.readTimeout)
                    .setSSLVerificationEnabled(options.sslVerification)
                    .setLogTag("BitmapLoader")
                    .setHTTPResponseListener(new BitmapResponseListener(width, height) {
                        @Override
                        public void onDecodingSuccessful(Bitmap bitmap) {
                            imageView.setImageBitmap(bitmap);
                            memCache.put(url, bitmap);
                            if (diskCache != null) {
                                diskCache.put(url, bitmap);
                            }
                        }

                        @Override
                        public void onDecodingFailed() {
                        }
                    })
                    .executeAsync();
        }
    }

    public static class RequestOptions {
        Map<String, String> headers = new HashMap<>();
        boolean sslVerification = false;
        private int readTimeout = 10000;
        private int connectTimeout = 10000;

        public Map<String, String> getHeaders() {
            return headers;
        }

        public RequestOptions setHeaders(Map<String, String> headers) {
            this.headers = headers;
            return this;
        }

        public RequestOptions addHeader(String key, String value) {
            headers.put(key, value);
            return this;
        }

        public RequestOptions addHeaders(Map<String, String> headers) {
            Set<Map.Entry<String, String>> entries = headers.entrySet();
            for (Map.Entry<String, String> entry : entries) {
                addHeader(entry.getKey(), entry.getValue());
            }
            return this;
        }

        public boolean isSSLVerificationEnabled() {
            return sslVerification;
        }

        public RequestOptions setSSLVerificationEnabled(boolean enabled) {
            this.sslVerification = enabled;
            return this;
        }

        public int getReadTimeout() {
            return readTimeout;
        }

        public RequestOptions setReadTimeout(int readTimeout) {
            this.readTimeout = readTimeout;
            return this;
        }

        public int getConnectTimeout() {
            return connectTimeout;
        }

        public RequestOptions setConnectTimeout(int connectTimeout) {
            this.connectTimeout = connectTimeout;
            return this;
        }
    }
}