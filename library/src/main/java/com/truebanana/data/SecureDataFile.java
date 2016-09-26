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
import com.truebanana.log.Log;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SecureDataFile extends SecureData {
    private File file;

    private static Map<String, SecureDataFile> loadedDataFiles = new HashMap<>();

    private SecureDataFile(byte[] data, File file, String password) {
        super(data, password);
        this.file = file;
    }

    public static SecureDataFile getDefault(Context context, String password) {
        return getDataFile(context, "data", password);
    }

    public static SecureDataFile getDataFile(Context context, String fileName, String password) {
        return loadFromFile(new File(context.getFilesDir().getPath(), fileName), password);
    }

    public static SecureDataFile loadFromFile(File file, String password) {
        String filePath = file.getAbsolutePath();
        if (loadedDataFiles.containsKey(filePath)) {
            Log.d("SecureDataFile", "Loading cached data file");
            return loadedDataFiles.get(filePath);
        } else {
            Log.d("SecureDataFile", "Loading data file from disk");
            byte[] data = new byte[0];
            try {
                data = FileUtils.readFileToByteArray(file);
                Log.d("SecureDataFile", "Successfully read data file");
            } catch (IOException e) {
                Log.d("SecureDataFile", "Could not read data file");
            }

            SecureDataFile dataFile = new SecureDataFile(data, file, password);

            loadedDataFiles.put(filePath, dataFile);
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
            FileUtils.writeByteArrayToFile(file, toByteArray());
            Log.d("SecureDataFile", "Successfully written data file");
        } catch (IOException e) {
            Log.d("SecureDataFile", "Could not write data file");
        }
    }
}