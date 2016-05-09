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

package com.truebanana.data;

import android.content.Context;

import com.truebanana.async.Async;
import com.truebanana.crypto.Crypto;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SecureDataFile extends SecureData {
    private String fileName;
    private Context context;

    private static Map<String, SecureDataFile> loadedDataFiles = new HashMap<>();

    private SecureDataFile(Context context, byte[] data, String fileName, String password) {
        super(data, password);
        this.context = context;
        this.fileName = fileName;
    }

    public static SecureDataFile getDataFile(Context context, String fileName, String password) {
        if (loadedDataFiles.containsKey(fileName)) {
            return loadedDataFiles.get(fileName);
        } else {
            byte[] data = new byte[0];
            try {
                data = FileUtils.readFileToByteArray(new File(context.getFilesDir().getPath(), fileName));
            } catch (IOException e) {
            }

            SecureDataFile dataFile = new SecureDataFile(context, data, fileName, password);

            loadedDataFiles.put(fileName, dataFile);
            return dataFile;
        }
    }

    public void saveAsync() {
        Async.executeAsync(new Runnable() {
            @Override
            public void run() {
                save();
            }
        });
    }

    public synchronized void save() {
        try {
            FileUtils.writeByteArrayToFile(new File(context.getFilesDir().getPath(), fileName), Crypto.encrypt(toByteArray(), password));
        } catch (IOException e) {
        }
    }
}