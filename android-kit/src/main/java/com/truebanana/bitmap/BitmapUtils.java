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

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;

import com.truebanana.http.BitmapResponseListener;
import com.truebanana.http.HTTPRequest;
import com.truebanana.system.SystemUtils;

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

    public static Bitmap decodeFile(File file) {
        return decodeFile(file, 0, 0);
    }

    public static Bitmap decodeFile(File file, int targetWidth, int targetHeight) {
        if (targetWidth > 1 && targetHeight > 1) {
            BitmapFactory.Options options = getBounds(file);
            options.inJustDecodeBounds = false;
            options.inSampleSize = getInSampleSize(options, targetWidth, targetHeight);
            return BitmapFactory.decodeFile(file.getPath(), options);
        } else {
            return BitmapFactory.decodeFile(file.getPath());
        }
    }

    public static DecodeFileTask decodeFileAsync(File file, int targetWidth, int targetHeight, DecodeFileListener listener) {
        DecodeFileTask task = new DecodeFileTask(file, targetWidth, targetHeight, listener);
        task.execute();
        return task;
    }

    // Decode Stream
    // *********************************************************************************************

    public static Bitmap decodeStream(InputStream inputStream) {
        return decodeStream(inputStream, 0, 0);
    }

    public static Bitmap decodeStream(InputStream inputStream, int targetWidth, int targetHeight) {
        try {
            byte[] bytes = IOUtils.toByteArray(inputStream);
            InputStream is1 = new ByteArrayInputStream(bytes);
            InputStream is2 = new ByteArrayInputStream(bytes);
            if (targetWidth > 1 && targetHeight > 1) {
                BitmapFactory.Options options = getBounds(is1);
                options.inJustDecodeBounds = false;
                options.inSampleSize = getInSampleSize(options, targetWidth, targetHeight);
                return BitmapFactory.decodeStream(is2, new Rect(), options);
            } else {
                return BitmapFactory.decodeStream(is2);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Decode Byte Array
    // *********************************************************************************************

    public static Bitmap decodeBytes(byte[] bytes) {
        return decodeBytes(bytes, 0, 0);
    }

    public static Bitmap decodeBytes(byte[] bytes, int targetWidth, int targetHeight) {
        if (targetWidth > 1 && targetHeight > 1) {
            BitmapFactory.Options options = getBounds(bytes);
            options.inJustDecodeBounds = false;
            options.inSampleSize = getInSampleSize(options, targetWidth, targetHeight);
            return BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
        } else {
            return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        }
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

    // Blur
    // *********************************************************************************************

    @SuppressLint("NewApi")
    public static Bitmap blur(Context context, Bitmap bitmap, float scale, int radius) {
        if (SystemUtils.getSDKVersion() >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            int width = Math.round(bitmap.getWidth() * scale);
            int height = Math.round(bitmap.getHeight() * scale);

            bitmap = Bitmap.createScaledBitmap(bitmap, width, height, false);
            Bitmap processedBitmap = Bitmap.createBitmap(bitmap);

            RenderScript rs = RenderScript.create(context);
            ScriptIntrinsicBlur sib = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
            Allocation tmpIn = Allocation.createFromBitmap(rs, bitmap);
            Allocation tmpOut = Allocation.createFromBitmap(rs, processedBitmap);
            sib.setRadius(radius);
            sib.setInput(tmpIn);
            sib.forEach(tmpOut);
            tmpOut.copyTo(processedBitmap);

            return processedBitmap;
        } else {
            int width = Math.round(bitmap.getWidth() * scale);
            int height = Math.round(bitmap.getHeight() * scale);
            bitmap = Bitmap.createScaledBitmap(bitmap, width, height, false);

            Bitmap processedBitmap = bitmap.copy(bitmap.getConfig(), true);

            if (radius < 1) {
                return null;
            }

            int w = processedBitmap.getWidth();
            int h = processedBitmap.getHeight();

            int[] pix = new int[w * h];
            processedBitmap.getPixels(pix, 0, w, 0, 0, w, h);

            int wm = w - 1;
            int hm = h - 1;
            int wh = w * h;
            int div = radius + radius + 1;

            int r[] = new int[wh];
            int g[] = new int[wh];
            int b[] = new int[wh];
            int rsum, gsum, bsum, x, y, i, p, yp, yi, yw;
            int vmin[] = new int[Math.max(w, h)];

            int divsum = (div + 1) >> 1;
            divsum *= divsum;
            int dv[] = new int[256 * divsum];
            for (i = 0; i < 256 * divsum; i++) {
                dv[i] = (i / divsum);
            }

            yw = yi = 0;

            int[][] stack = new int[div][3];
            int stackpointer;
            int stackstart;
            int[] sir;
            int rbs;
            int r1 = radius + 1;
            int routsum, goutsum, boutsum;
            int rinsum, ginsum, binsum;

            for (y = 0; y < h; y++) {
                rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
                for (i = -radius; i <= radius; i++) {
                    p = pix[yi + Math.min(wm, Math.max(i, 0))];
                    sir = stack[i + radius];
                    sir[0] = (p & 0xff0000) >> 16;
                    sir[1] = (p & 0x00ff00) >> 8;
                    sir[2] = (p & 0x0000ff);
                    rbs = r1 - Math.abs(i);
                    rsum += sir[0] * rbs;
                    gsum += sir[1] * rbs;
                    bsum += sir[2] * rbs;
                    if (i > 0) {
                        rinsum += sir[0];
                        ginsum += sir[1];
                        binsum += sir[2];
                    } else {
                        routsum += sir[0];
                        goutsum += sir[1];
                        boutsum += sir[2];
                    }
                }
                stackpointer = radius;

                for (x = 0; x < w; x++) {
                    r[yi] = dv[rsum];
                    g[yi] = dv[gsum];
                    b[yi] = dv[bsum];

                    rsum -= routsum;
                    gsum -= goutsum;
                    bsum -= boutsum;

                    stackstart = stackpointer - radius + div;
                    sir = stack[stackstart % div];

                    routsum -= sir[0];
                    goutsum -= sir[1];
                    boutsum -= sir[2];

                    if (y == 0) {
                        vmin[x] = Math.min(x + radius + 1, wm);
                    }
                    p = pix[yw + vmin[x]];

                    sir[0] = (p & 0xff0000) >> 16;
                    sir[1] = (p & 0x00ff00) >> 8;
                    sir[2] = (p & 0x0000ff);

                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];

                    rsum += rinsum;
                    gsum += ginsum;
                    bsum += binsum;

                    stackpointer = (stackpointer + 1) % div;
                    sir = stack[(stackpointer) % div];

                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];

                    rinsum -= sir[0];
                    ginsum -= sir[1];
                    binsum -= sir[2];

                    yi++;
                }
                yw += w;
            }
            for (x = 0; x < w; x++) {
                rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
                yp = -radius * w;
                for (i = -radius; i <= radius; i++) {
                    yi = Math.max(0, yp) + x;

                    sir = stack[i + radius];

                    sir[0] = r[yi];
                    sir[1] = g[yi];
                    sir[2] = b[yi];

                    rbs = r1 - Math.abs(i);

                    rsum += r[yi] * rbs;
                    gsum += g[yi] * rbs;
                    bsum += b[yi] * rbs;

                    if (i > 0) {
                        rinsum += sir[0];
                        ginsum += sir[1];
                        binsum += sir[2];
                    } else {
                        routsum += sir[0];
                        goutsum += sir[1];
                        boutsum += sir[2];
                    }

                    if (i < hm) {
                        yp += w;
                    }
                }
                yi = x;
                stackpointer = radius;
                for (y = 0; y < h; y++) {
                    // Preserve alpha channel: ( 0xff000000 & pix[yi] )
                    pix[yi] = (0xff000000 & pix[yi]) | (dv[rsum] << 16) | (dv[gsum] << 8) | dv[bsum];

                    rsum -= routsum;
                    gsum -= goutsum;
                    bsum -= boutsum;

                    stackstart = stackpointer - radius + div;
                    sir = stack[stackstart % div];

                    routsum -= sir[0];
                    goutsum -= sir[1];
                    boutsum -= sir[2];

                    if (x == 0) {
                        vmin[y] = Math.min(y + r1, hm) * w;
                    }
                    p = x + vmin[y];

                    sir[0] = r[p];
                    sir[1] = g[p];
                    sir[2] = b[p];

                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];

                    rsum += rinsum;
                    gsum += ginsum;
                    bsum += binsum;

                    stackpointer = (stackpointer + 1) % div;
                    sir = stack[stackpointer];

                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];

                    rinsum -= sir[0];
                    ginsum -= sir[1];
                    binsum -= sir[2];

                    yi += w;
                }
            }
            processedBitmap.setPixels(pix, 0, w, 0, 0, w, h);

            return processedBitmap;
        }
    }
}