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

package com.truebanana.system;

import android.content.Context;
import android.content.res.Resources;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;

import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;

/**
 * Includes system and device-related utility methods.
 */
public class SystemUtils {

    // Device
    // ************************************************************************

    /**
     * Returns the SDK version of the device
     *
     * @return The SDK version of the device
     */
    public static int getSDKVersion() {
        return Build.VERSION.SDK_INT;
    }

    /**
     * Returns the {@link Settings.Secure#ANDROID_ID} of the device
     *
     * @param context
     * @return The {@link Settings.Secure#ANDROID_ID}
     */
    public static String getDeviceID(Context context) {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    // Metrics
    // ************************************************************************

    /**
     * Converts device-independent pixels (DiP/DP) to pixels (PX)
     *
     * @param dp The DP to convert
     * @return The equivalent pixels
     */
    public static float convertDPToPX(float dp) {
        return dp * Resources.getSystem().getDisplayMetrics().density;
    }

    /**
     * Converts pixels (PX) to device-independent pixels (DiP/DP)
     *
     * @param px The pixels to convert
     * @return The equivalent DP
     */
    public static float convertPXToDP(float px) {
        return px / Resources.getSystem().getDisplayMetrics().density;
    }

    // Network
    // ************************************************************************

    /**
     * Checks whether network connectivity exists
     *
     * @param context
     * @return <b>true</b> if the device is connected to the internet, <b>false</b> otherwise
     */
    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni != null && ni.isConnected()) {
            return true;
        }
        return false;
    }

    /**
     * Returns the network connection type
     *
     * @param context
     * @return One of {@link ConnectivityManager#TYPE_MOBILE}, {@link ConnectivityManager#TYPE_WIFI} or other types defined by {@link ConnectivityManager}
     */
    public static int getNetworkConnectionType(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        int networkConnectionType = -1;
        if (ni != null) {
            if (ni.isConnected()) {
                networkConnectionType = ni.getType();
            }
        }
        return networkConnectionType;
    }

    /**
     * Returns a human-readable name of the network connection type (e.g. "WIFI" or "MOBILE")
     *
     * @param context
     * @return The name of the network connection type
     */
    public static String getNetworkConnectionTypeName(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        String networkConnectionType = "";
        if (ni != null) {
            if (ni.isConnected()) {
                networkConnectionType = ni.getTypeName();
            }
        }
        return networkConnectionType;
    }

    /**
     * Returns MAC address of the given interface name
     *
     * @param interfaceName "<b>eth0</b>", "<b>wlan0</b>" or <b>null</b> to use first interface
     * @return The MAC address or <b>null</b> if it fails
     */
    public static String getMACAddress(String interfaceName) {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface networkInterface : interfaces) {
                if (interfaceName != null) {
                    if (!networkInterface.getName().equalsIgnoreCase(interfaceName)) {
                        continue;
                    }
                }

                byte[] mac = networkInterface.getHardwareAddress();

                if (mac == null)
                    return "";
                StringBuilder buf = new StringBuilder();
                for (int i = 0; i < mac.length; i++) {
                    buf.append(String.format("%02X:", mac[i]));
                }
                if (buf.length() > 0) {
                    buf.deleteCharAt(buf.length() - 1);
                }
                return buf.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * Returns the network operator name
     *
     * @param context
     * @return The network operator name
     */
    public static String getNetworkOperatorName(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return tm.getNetworkOperatorName();
    }

    /**
     * Returns the service provider name
     *
     * @param context
     * @return The service provider name if the SIM state is {@link TelephonyManager#SIM_STATE_READY}
     */
    public static String getSIMOperatorName(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return tm.getSimOperatorName();
    }

    /**
     * Returns a human-readable name of the phone type (e.g. "GSM")
     *
     * @param phoneType The phone type code from {@link TelephonyManager} such as {@link TelephonyManager#PHONE_TYPE_GSM}
     * @return The phone type name
     */
    public static String getPhoneTypeName(int phoneType) {
        String phoneTypeName = "NONE";
        switch (phoneType) {
            case TelephonyManager.PHONE_TYPE_CDMA:
                phoneTypeName = "CDMA";
                break;
            case TelephonyManager.PHONE_TYPE_GSM:
                phoneTypeName = "GSM";
                break;
            case TelephonyManager.PHONE_TYPE_NONE:
                phoneTypeName = "NONE";
                break;
            case TelephonyManager.PHONE_TYPE_SIP:
                phoneTypeName = "SIP";
                break;
        }
        return phoneTypeName;
    }

    /**
     * Returns a human-readable name of the network type (e.g. "HSPA")
     *
     * @param networkType The network type code from {@link TelephonyManager} such as {@link TelephonyManager#NETWORK_TYPE_HSPA}
     * @return The network type name
     */
    public static String getNetworkTypeName(int networkType) {
        String networkTypeName = "UNKNOWN";
        switch (networkType) {
            case TelephonyManager.NETWORK_TYPE_1xRTT:
                networkTypeName = "1xRTT";
                break;
            case TelephonyManager.NETWORK_TYPE_CDMA:
                networkTypeName = "CDMA";
                break;
            case TelephonyManager.NETWORK_TYPE_EDGE:
                networkTypeName = "EDGE";
                break;
            case TelephonyManager.NETWORK_TYPE_EHRPD:
                networkTypeName = "EHRPD";
                break;
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
                networkTypeName = "EVDO_0";
                break;
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
                networkTypeName = "EVDO_A";
                break;
            case TelephonyManager.NETWORK_TYPE_EVDO_B:
                networkTypeName = "EVDO_B";
                break;
            case TelephonyManager.NETWORK_TYPE_GPRS:
                networkTypeName = "GPRS";
                break;
            case TelephonyManager.NETWORK_TYPE_HSDPA:
                networkTypeName = "HSDPA";
                break;
            case TelephonyManager.NETWORK_TYPE_HSPA:
                networkTypeName = "HSPA";
                break;
            case TelephonyManager.NETWORK_TYPE_HSPAP:
                networkTypeName = "HSPAP";
                break;
            case TelephonyManager.NETWORK_TYPE_HSUPA:
                networkTypeName = "HSUPA";
                break;
            case TelephonyManager.NETWORK_TYPE_IDEN:
                networkTypeName = "IDEN";
                break;
            case TelephonyManager.NETWORK_TYPE_LTE:
                networkTypeName = "LTE";
                break;
            case TelephonyManager.NETWORK_TYPE_UMTS:
                networkTypeName = "UMTS";
                break;
            case TelephonyManager.NETWORK_TYPE_UNKNOWN:
                networkTypeName = "UNKNOWN";
                break;
        }
        return networkTypeName;
    }

    // Location Service
    // ************************************************************************

    /**
     * Checks if the specified location provider is enabled
     *
     * @param context
     * @param provider The location provider
     * @return <b>true</b> if enabled, <b>false</b> otherwise
     */
    public static boolean isProviderEnabled(Context context, String provider) {
        LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return lm.isProviderEnabled(provider);
    }

    /**
     * Checks if the GPS location provider is enabled
     *
     * @param context
     * @return <b>true</b> if enabled, <b>false</b> otherwise
     */
    public static boolean isGPSEnabled(Context context) {
        return isProviderEnabled(context, LocationManager.GPS_PROVIDER);
    }

    /**
     * Checks if the network location provider is enabled
     *
     * @param context
     * @return <b>true</b> if enabled, <b>false</b> otherwise
     */
    public static boolean isNetworkLocationProviderEnabled(Context context) {
        return isProviderEnabled(context, LocationManager.NETWORK_PROVIDER);
    }

    /**
     * Checks if the passive location provider is enabled
     *
     * @param context
     * @return <b>true</b> if enabled, <b>false</b> otherwise
     */
    public static boolean isPassiveLocationProviderEnabled(Context context) {
        return isProviderEnabled(context, LocationManager.PASSIVE_PROVIDER);
    }
}