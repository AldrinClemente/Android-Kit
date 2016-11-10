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

package com.truebanana.cache;

import android.content.Context;

import com.truebanana.crypto.Crypto;
import com.truebanana.log.Log;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Extend this class and implement {@link AbstractDiskLruCache#get(String)} and {@link AbstractDiskLruCache#put(String, Object)}
 * which should respectively call {@link AbstractDiskLruCache#getData(String)} to retrieve raw data from the cache for processing and
 * {@link AbstractDiskLruCache#putData(String, byte[])} to store your data.
 */
public abstract class AbstractDiskLruCache<T> {
    private DiskLruCache cache;

    public AbstractDiskLruCache(Context context) {
        this(context, 10 * 1024 * 1024);
    }

    public AbstractDiskLruCache(Context context, long maxSize) {
        this(context.getCacheDir(), maxSize);
    }

    public AbstractDiskLruCache(File directory) {
        this(directory, 10 * 1024 * 1024);
    }

    public AbstractDiskLruCache(File directory, long maxSize) {
        try {
            cache = DiskLruCache.open(directory, 1, 1, maxSize);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean writeToFile(byte[] data, OutputStream os) {
        try {
            IOUtils.write(data, os);
            os.flush();
            os.close();
            Log.d("AbstractDiskLruCache", "Write file to disk successful");
            return true;
        } catch (IOException e) {
            Log.d("AbstractDiskLruCache", "Write file to disk failed");
            return false;
        }
    }

    public File getDirectory() {
        return cache.getDirectory();
    }

    public long getSize() {
        return cache.size();
    }

    public long getMaxSize() {
        return cache.maxSize();
    }

    public boolean contains(String key) {
        key = Crypto.SHA1(key);
        boolean contains = false;
        DiskLruCache.Snapshot snapshot = null;
        try {
            snapshot = cache.get(key);
            contains = snapshot != null;
        } catch (IOException e) {
            Log.d("AbstractDiskLruCache", "Cache entry check failed");
        } finally {
            if (snapshot != null) {
                snapshot.close();
            }
        }
        return contains;
    }

    public void clear() {
        try {
            cache.delete();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected byte[] getData(String key) {
        key = Crypto.SHA1(key);
        byte[] data = null;
        DiskLruCache.Snapshot snapshot = null;
        try {
            snapshot = cache.get(key);
            if (snapshot != null) {
                InputStream is = snapshot.getInputStream(0);
                data = IOUtils.toByteArray(is);
            }
        } catch (IOException e) {
            Log.d("AbstractDiskLruCache", "Get data from cache failed");
        } finally {
            if (snapshot != null) {
                snapshot.close();
            }
        }
        return data;
    }

    protected void putData(String key, byte[] data) {
        key = Crypto.SHA1(key);
        DiskLruCache.Editor editor = null;
        boolean success = false;
        try {
            editor = cache.edit(key);
            if (writeToFile(data, editor.newOutputStream(0))) {
                cache.flush();
                editor.commit();
                success = true;
                cache.close(); // Close cache so everything is written to disk
                cache = DiskLruCache.open(getDirectory(), 1, 1, getMaxSize()); // Reopen for other transactions
            }
        } catch (IOException e) {
            Log.d("AbstractDiskLruCache", "Put data in cache failed");
        } finally {
            if (!success && editor != null) {
                try {
                    editor.abort();
                } catch (IOException e1) {
                }
            }
        }
    }

    public abstract T get(String key);

    public abstract void put(String key, T item);
}