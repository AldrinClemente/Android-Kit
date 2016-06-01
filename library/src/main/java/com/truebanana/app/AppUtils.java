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

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.Uri;
import android.os.Build;
import android.os.Process;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Base64;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

/**
 * Includes app-related utility methods.
 */
public class AppUtils {

    // Version
    // ************************************************************************

    /**
     * Returns the version code of the app with the specified package name
     *
     * @param context
     * @return The version code of the app or 0 if it fails
     */
    public static int getVersionCode(Context context, String packageName) {
        try {
            return context.getPackageManager().getPackageInfo(packageName, 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            return 0;
        }
    }

    /**
     * Returns the version code of the calling app
     *
     * @param context
     * @return The version code of the app or 0 if it fails
     */
    public static int getVersionCode(Context context) {
        return getVersionCode(context, context.getPackageName());
    }

    /**
     * Returns the version name of the app with the specified package name
     *
     * @param context
     * @return The version name of the app or <b>null</b> if it fails
     */
    public static String getVersionName(Context context, String packageName) {
        try {
            return context.getPackageManager().getPackageInfo(packageName, 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }
    }

    /**
     * Returns the version name of the calling app
     *
     * @param context
     * @return The version name of the app or <b>null</b> if it fails
     */
    public static String getVersionName(Context context) {
        return getVersionName(context, context.getPackageName());
    }

    // Launching
    // ************************************************************************

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

    // Process
    // ************************************************************************

    /**
     * Checks if an app or process with the specified package name is running
     *
     * @param context
     * @param packageName The package name of the app or process to check
     * @return <b>true</b> if running, <b>false</b> otherwise
     */
    public static Boolean isRunning(Context context, String packageName) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> processInfo = activityManager.getRunningAppProcesses();
        for (int i = 0; i < processInfo.size(); i++) {
            if (processInfo.get(i).processName.equals(packageName)) {
                return true;
            }
        }
        return false;
    }

    // Installation
    // ************************************************************************

    /**
     * Checks if an app with the specified package name is installed
     *
     * @param context
     * @param packageName The package name of the app to check
     * @return <b>true</b> if the app exists and successfully launched, <b>false</b> otherwise
     */
    public static boolean isInstalled(Context context, String packageName) {
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
        return installer != null && installer.equals("com.android.vending");
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
     * Returns the package name of the installer of the calling app
     *
     * @param context
     * @return The package name of the installer
     */
    public static String getInstallerPackageName(Context context) {
        return getInstallerPackageName(context, context.getPackageName());
    }

    // Play Store
    // ************************************************************************

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
     * Launches the Play Store product page for calling app
     *
     * @param context
     */
    public static void launchPlayStoreProductPage(Context context) {
        launchPlayStoreProductPage(context, context.getPackageName());
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
     * Returns the Play Store product page URL for calling app
     *
     * @param context
     */
    public static String getPlayStoreProductPageURL(Context context) {
        return getPlayStoreProductPageURL(context.getPackageName());
    }

    // Permissions
    // ************************************************************************

    /**
     * Checks if the calling app has been granted with the specified permission
     *
     * @param context
     * @param permission The permission to check (see {@link android.Manifest.permission})
     * @return <b>true</b> if the permission has been granted, <b>false</b> otherwise
     */
    public static boolean isPermissionGranted(Context context, String permission) {
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
    }

    public static boolean shouldShowRequestPermissionRationale(Activity activity, String permission) {
        return ActivityCompat.shouldShowRequestPermissionRationale(activity, permission);
    }

    public static void requestPermissions(Activity activity, String[] permissions, int requestCode) {
        ActivityCompat.requestPermissions(activity, permissions, requestCode);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static boolean isAppOpsAllowed(Context context, String appOpsPermission) {
        AppOpsManager aom = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        return aom.checkOpNoThrow(appOpsPermission, Process.myUid(), context.getPackageName()) == AppOpsManager.MODE_ALLOWED;
    }

    // Key Hash
    // ************************************************************************

    /**
     * Returns the key hashes of the signatures of the app with the specified package name
     * which may be needed when integrating with 3rd party services such as Facebook. Note that
     * Android apps usually only have one signature.
     *
     * @param context
     * @param packageName The package name of the app
     * @return The key hashes
     */
    public static List<String> getKeyHashes(Context context, String packageName) {
        try {
            List<String> hashes = new ArrayList<>();
            PackageInfo info = context.getPackageManager().getPackageInfo(packageName, PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                hashes.add(Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
            return hashes;
        } catch (PackageManager.NameNotFoundException e) {
        } catch (NoSuchAlgorithmException e) {
        }
        return null;
    }

    /**
     * Since Android apps usually just have one signature, this convenience method returns the key
     * hash of the first signature of the app with the specified package name which may be needed
     * when integrating with 3rd party services such as Facebook.
     *
     * @param context
     * @param packageName The package name of the app
     * @return The key hash
     */
    public static String getKeyHash(Context context, String packageName) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(packageName, PackageManager.GET_SIGNATURES);
            MessageDigest md = MessageDigest.getInstance("SHA");
            md.update(info.signatures[0].toByteArray());
            return Base64.encodeToString(md.digest(), Base64.DEFAULT);
        } catch (PackageManager.NameNotFoundException e) {
        } catch (NoSuchAlgorithmException e) {
        }
        return null;
    }

    /**
     * Returns the key hashes of the signatures of the calling app which may be needed when
     * integrating with 3rd party services such as Facebook. Note that Android apps usually only
     * have one signature.
     *
     * @param context
     * @return The key hashes
     */
    public static List<String> getKeyHashes(Context context) {
        return getKeyHashes(context, context.getPackageName());
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
        return AppUtils.getKeyHash(context, context.getPackageName());
    }
}