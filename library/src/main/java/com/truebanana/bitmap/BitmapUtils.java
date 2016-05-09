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
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;

import com.truebanana.http.BitmapResponseListener;
import com.truebanana.http.HTTPRequest;

import org.apache.commons.io.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Includes {@link Bitmap}-related utility methods.
 */
public class BitmapUtils {
    // Bounds
    // *********************************************************************************************

    public static BitmapFactory.Options getBounds(File file) {
        return getBounds(file, new BitmapFactory.Options());
    }

    public static BitmapFactory.Options getBounds(File file, BitmapFactory.Options options) {
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(file.getPath(), options);
        return options;
    }

    public static BitmapFactory.Options getBounds(InputStream inputStream) {
        return getBounds(inputStream, new BitmapFactory.Options());
    }

    public static BitmapFactory.Options getBounds(InputStream inputStream, BitmapFactory.Options options) {
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(inputStream, new Rect(), options);
        return options;
    }

    public static BitmapFactory.Options getBounds(byte[] bytes) {
        return getBounds(bytes, new BitmapFactory.Options());
    }

    public static BitmapFactory.Options getBounds(byte[] bytes, BitmapFactory.Options options) {
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
        return options;
    }

    // InSampleSize
    // *********************************************************************************************

    public static int getInSampleSize(BitmapFactory.Options optionsWithBounds, int targetWidth, int targetHeight) {
        return getInSampleSize(optionsWithBounds.outWidth, optionsWithBounds.outHeight, targetWidth, targetHeight);
    }

    public static int getInSampleSize(int width, int height, int targetWidth, int targetHeight) {
        int inSampleSize = 1;
        if (width > targetWidth || height > targetHeight) {
            final int halfWidth = width / 2;
            final int halfHeight = height / 2;
            while ((halfWidth / inSampleSize) > targetWidth && (halfHeight / inSampleSize) > targetHeight) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    // Decode File
    // *********************************************************************************************

    public static Bitmap decodeFile(File file, int targetWidth, int targetHeight) {
        BitmapFactory.Options options = getBounds(file);
        options.inJustDecodeBounds = false;
        options.inSampleSize = getInSampleSize(options, targetWidth, targetHeight);
        return BitmapFactory.decodeFile(file.getPath(), options);
    }

    public static DecodeFileTask decodeFileAsync(File file, int targetWidth, int targetHeight, DecodeFileListener listener) {
        DecodeFileTask task = new DecodeFileTask(file, targetWidth, targetHeight, listener);
        task.execute();
        return task;
    }

    // Decode Stream
    // *********************************************************************************************

    public static Bitmap decodeStream(InputStream inputStream, int targetWidth, int targetHeight) {
        try {
            byte[] bytes = IOUtils.toByteArray(inputStream);
            InputStream is1 = new ByteArrayInputStream(bytes);
            InputStream is2 = new ByteArrayInputStream(bytes);
            BitmapFactory.Options options = getBounds(is1);
            options.inJustDecodeBounds = false;
            options.inSampleSize = getInSampleSize(options, targetWidth, targetHeight);
            return BitmapFactory.decodeStream(is2, new Rect(), options);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Decode Byte Array
    // *********************************************************************************************

    public static Bitmap decodeBytes(byte[] bytes, int targetWidth, int targetHeight) {
        BitmapFactory.Options options = getBounds(bytes);
        options.inJustDecodeBounds = false;
        options.inSampleSize = getInSampleSize(options, targetWidth, targetHeight);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
    }

    // Decode From URL
    // *********************************************************************************************

    public static void decodeFromURL(String url, BitmapResponseListener listener) {
        HTTPRequest.create(url)
                .setHTTPResponseListener(listener)
                .executeAsync();
    }

    // Cropping
    // *********************************************************************************************

    public static Bitmap crop(Bitmap bitmap, int targetWidth, int targetHeight, int x, int y) {
        return Bitmap.createBitmap(bitmap, x, y, targetWidth, targetHeight);
    }

    // Orientation
    // *********************************************************************************************

    public static Bitmap correctOrientation(Bitmap bitmap, int orientation) {
        Matrix matrix = new Matrix();
        matrix.postRotate(orientation);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    public static int getOrientation(Context context, Uri bitmapUri) {
        Cursor cursor = context.getContentResolver().query(bitmapUri, new String[]{MediaStore.Images.ImageColumns.ORIENTATION}, null, null, null);

        if (cursor == null || cursor.getCount() != 1) {
            return -1;
        }

        cursor.moveToFirst();
        return cursor.getInt(0);
    }

    public static int getOrientation(File file) {
        return getOrientation(file.getPath());
    }

    public static int getOrientation(String path) {
        try {
            ExifInterface exif = new ExifInterface(path);

            return exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }
}