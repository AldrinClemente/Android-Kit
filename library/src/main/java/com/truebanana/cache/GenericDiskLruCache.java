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

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Aldrin on 09/11/2016.
 */

public class GenericDiskLruCache {
    private DiskLruCache cache;

    public GenericDiskLruCache(File directory) {
        this(directory, 10 * 1024 * 1024);
    }

    public GenericDiskLruCache(File directory, long maxSize) {
        try {
            cache = DiskLruCache.open(directory, 1, 1, maxSize);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean writeToFile(byte[] data, OutputStream os) {
        try {
            IOUtils.write(data, os);
            return true;
        } catch (IOException e) {
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

    public void put(String key, byte[] data) {
        DiskLruCache.Editor editor = null;
        try {
            editor = cache.edit(key);
            if (writeToFile(data, editor.newOutputStream(0))) {
                cache.flush();
                editor.commit();
            } else {
                editor.abort();
            }
        } catch (IOException e) {
            if (editor != null) {
                try {
                    editor.abort();
                } catch (IOException e1) {
                }
            }
        }
    }

    public byte[] get(String key) {
        byte[] data = null;
        DiskLruCache.Snapshot snapshot = null;
        try {
            snapshot = cache.get(key);
            if (snapshot != null) {
                InputStream is = snapshot.getInputStream(0);
                data = IOUtils.toByteArray(is);
            }
        } catch (IOException e) {
            if (snapshot != null) {
                snapshot.close();
            }
        }
        return data;
    }

    public boolean contains(String key) {
        boolean contains = false;
        try {
            DiskLruCache.Snapshot snapshot = cache.get(key);
            contains = snapshot != null;
        } catch (IOException e) {
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
}