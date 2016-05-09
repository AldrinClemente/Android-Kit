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

package com.truebanana.app;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;

import com.truebanana.system.SystemUtils;

/**
 * Includes app-related utility methods.
 */
public class AppUtils {

    /**
     * Returns the version code of the calling app
     *
     * @param context
     * @return The version code of the app or 0 if it fails
     */
    public static int getVersionCode(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            return 0;
        }
    }

    /**
     * Returns the version name of the calling app
     *
     * @param context
     * @return The version name of the app or <b>null</b> if it fails
     */
    public static String getVersionName(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }
    }

    /**
     * Launches an app with the specified package name if it exists
     *
     * @param context
     * @param packageName The package name of the app to launch
     * @return <b>true</b> if the app exists and successfully launched, <b>false</b> otherwise
     */
    public static boolean launchApp(Context context, String packageName) {
        final PackageManager packageManager = context.getPackageManager();
        final Intent intent = packageManager.getLaunchIntentForPackage(packageName);
        if (intent != null) {
            context.startActivity(intent);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Checks if an app with the specified package name is installed
     *
     * @param context
     * @param packageName The package name of the app to check
     * @return <b>true</b> if the app exists and successfully launched, <b>false</b> otherwise
     */
    public static boolean isAppInstalled(Context context, String packageName) {
        final PackageManager packageManager = context.getPackageManager();
        try {
            packageManager.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    /**
     * Checks if an app with the specified package name is installed via Google Play Store
     *
     * @param context
     * @param packageName The package name of the app to check
     * @return <b>true</b> if the app exists and installed from Play Store, <b>false</b> otherwise
     */
    public static boolean isInstalledFromPlayStore(Context context, String packageName) {
        String installer = getInstallerPackageName(context, packageName);

        if (installer != null && installer.equals("com.android.vending")) {
            return true;
        }
        return false;
    }

    /**
     * Checks if the calling app is installed via Google Play Store
     *
     * @param context
     * @return <b>true</b> if the app exists and installed from Play Store, <b>false</b> otherwise
     */
    public static boolean isInstalledFromPlayStore(Context context) {
        return AppUtils.isInstalledFromPlayStore(context, context.getPackageName());
    }

    /**
     * Returns the package name of the installer of the app
     *
     * @param context
     * @return The package name of the installer
     */
    public static String getInstallerPackageName(Context context) {
        String packageName = context.getPackageName();
        return getInstallerPackageName(context, packageName);
    }

    /**
     * Returns the package name of the installer of the app with the specified package name
     *
     * @param context
     * @param packageName The package name of the app to check
     * @return The package name of the installer
     */
    public static String getInstallerPackageName(Context context, String packageName) {
        PackageManager packageManager = context.getPackageManager();

        try {
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(packageName, 0);
            return packageManager.getInstallerPackageName(applicationInfo.packageName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Launches the Play Store product page for the specified package name
     *
     * @param context
     * @param packageName The package name of the app
     */
    public static void launchPlayStoreProductPage(Context context, String packageName) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        try {
            intent.setData(Uri.parse("market://details?id=" + packageName));
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            intent.setData(Uri.parse("http://play.google.com/store/apps/details?id=" + packageName));
            context.startActivity(intent);
        }
    }

    /**
     * Returns the Play Store product page URL for the specified package name
     *
     * @param packageName The package name of the app
     */
    public static String getPlayStoreProductPageURL(String packageName) {
        return "http://play.google.com/store/apps/details?id=" + packageName;
    }

    /**
     * Since Android apps usually just have one signature, this convenience method returns the key
     * hash of the first signature of the calling app which may be needed when integrating with 3rd
     * party services such as Facebook.
     *
     * @param context
     * @return The key hash
     */
    public static String getKeyHash(Context context) {
        return SystemUtils.getKeyHash(context, context.getPackageName());
    }
}