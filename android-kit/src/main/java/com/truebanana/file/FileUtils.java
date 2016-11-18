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

package com.truebanana.file;

import android.net.Uri;
import android.webkit.MimeTypeMap;

import java.io.File;
import java.text.DecimalFormat;

/**
 * Includes {@link File}-related utility methods.
 */
public class FileUtils {
    private static String[] decimalByteUnits;
    private static String[] binaryByteUnits;
    private static DecimalFormat fileSizeFormat;

    public static String getMimeType(File file) {
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(getExtension(file));
    }

    public static String getExtensionFromMimeType(String mimeType) {
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType);
    }

    public static String getExtension(File file) {
        return getExtension(Uri.fromFile(file));
    }

    public static String getExtension(Uri uri) {
        return MimeTypeMap.getFileExtensionFromUrl(uri.toString());
    }

    public static boolean isImage(File file) {
        String mimeType = getMimeType(file);
        return mimeType != null && mimeType.startsWith("image");
    }

    public static boolean isVideo(File file) {
        String mimeType = getMimeType(file);
        return mimeType != null && mimeType.startsWith("video");
    }

    public static String getFormattedFileSize(long bytes) {
        return getFormattedFileSize(bytes, false);
    }

    public static String getFormattedFileSize(long bytes, boolean binary) {
        return getFormattedFileSize(bytes, binary, binary);
    }

    public static String getFormattedFileSize(long bytes, boolean binaryCalculation, boolean binaryUnits) {
        if (binaryByteUnits == null) {
            decimalByteUnits = new String[]{"B", "kB", "MB", "GB", "TB", "PB", "EB", "ZB", "YB"};
            binaryByteUnits = new String[]{"B", "KiB", "MiB", "GiB", "TiB", "PiB", "EiB", "ZiB", "YiB"};
            fileSizeFormat = new DecimalFormat("0.00");
        }
        double b = bytes;
        int loops = 0;
        String[] units = binaryUnits ? binaryByteUnits : decimalByteUnits;
        int divisor = binaryCalculation ? 1024 : 1000;
        while (b > divisor & loops < units.length - 1) {
            b /= divisor;
            loops++;
        }
        return fileSizeFormat.format(b) + " " + units[loops];
    }
}