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

package com.truebanana.sms;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;

import java.util.ArrayList;
import java.util.WeakHashMap;

/**
 * Includes SMS-related utility methods.
 */
public class SMSUtils {
    private static int requestCode = 10000;
    private static WeakHashMap<Integer, SMSStatusListener> listeners = new WeakHashMap<>();
    private static BroadcastReceiver sentReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context c, Intent intent) {
            int requestCode = intent.getIntExtra("requestCode", 0);

            if (requestCode > 0) {
                int resultCode = getResultCode();
                SMSStatusListener listener = listeners.get(requestCode);

                if (listener != null) {
                    switch (resultCode) {
                        case Activity.RESULT_OK:
                            listener.onSMSSent();
                            break;
                        case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        case SmsManager.RESULT_ERROR_NO_SERVICE:
                        case SmsManager.RESULT_ERROR_NULL_PDU:
                        case SmsManager.RESULT_ERROR_RADIO_OFF:
                        default:
                            listener.onSMSSendingFailed(resultCode);
                            break;
                    }
                    listeners.remove(requestCode);
                }
            }
        }
    };
    private static BroadcastReceiver deliveredReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context c, Intent intent) {
            int requestCode = intent.getIntExtra("requestCode", 0);

            if (requestCode > 0) {
                int resultCode = getResultCode();
                SMSStatusListener listener = listeners.get(requestCode);

                if (listener != null) {
                    switch (resultCode) {
                        case Activity.RESULT_OK:
                            listener.onSMSDelivered();
                            break;
                        default:
                            listener.onSMSDeliveryFailed();
                            break;
                    }
                    listeners.remove(requestCode);
                }
            }
        }
    };


    /**
     * Call this during your {@link Activity#onResume()} to automatically handle registering of receivers used by {@link SMSUtils}.
     *
     * @param context
     */
    public static void onResume(Context context) {
        IntentFilter sentFilter = new IntentFilter("SMS_SENT");
        IntentFilter deliveredFilter = new IntentFilter("SMS_DELIVERED");

        context.registerReceiver(sentReceiver, sentFilter);
        context.registerReceiver(deliveredReceiver, deliveredFilter);
    }

    /**
     * Call this during your {@link Activity#onPause()} to automatically handle unregistering of receivers used by {@link SMSUtils}.
     *
     * @param context
     */
    public static void onPause(Context context) {
        context.unregisterReceiver(sentReceiver);
        context.unregisterReceiver(deliveredReceiver);
    }


    /**
     * Sends an SMS to the specified phone number
     *
     * @param phoneNumber The phone number to send the message to
     * @param message     The text message
     */
    public static void sendSMS(String phoneNumber, String message) {
        sendSMS(phoneNumber, null, message);
    }

    /**
     * Sends an SMS to the specified phone number
     *
     * @param phoneNumber The phone number to send the message to
     * @param sc          The service center number (SMSC) or <b>null</b> to use the default
     * @param message     The text message
     */
    public static void sendSMS(String phoneNumber, String sc, String message) {
        sendSMS(null, phoneNumber, sc, message, null);
    }

    /**
     * Sends an SMS to the specified phone number
     *
     * @param context
     * @param phoneNumber The phone number to send the message to
     * @param message     The text message
     * @param listener    The {@link SMSStatusListener} to handle the SMS events
     */
    public static void sendSMS(Context context, String phoneNumber, String message, SMSStatusListener listener) {
        sendSMS(context, phoneNumber, null, message, listener);
    }

    /**
     * Sends an SMS to the specified phone number
     *
     * @param context
     * @param phoneNumber The phone number to send the message to
     * @param sc          The service center number (SMSC) or <b>null</b> to use the default
     * @param message     The text message
     * @param listener    The {@link SMSStatusListener} to handle the SMS events
     */
    public static void sendSMS(final Context context, String phoneNumber, String sc, String message, final SMSStatusListener listener) {
        SmsManager smsManager = SmsManager.getDefault();
        PendingIntent sentIntent = null, deliveryIntent = null;
        if ((context != null) && (listener != null)) {
            final String sentAction = "SMS_SENT", deliveryAction = "SMS_DELIVERED";

            Intent si = new Intent(sentAction);
            si.putExtra("requestCode", requestCode);
            sentIntent = PendingIntent.getBroadcast(context, requestCode, si, 0);
            listeners.put(requestCode, listener);
            requestCode++;

            Intent di = new Intent(deliveryAction);
            di.putExtra("requestCode", requestCode);
            deliveryIntent = PendingIntent.getBroadcast(context, requestCode, di, 0);
            listeners.put(requestCode, listener);
            requestCode++;
        }
        if (message.length() > 160) {
            ArrayList<String> messageParts = smsManager.divideMessage(message);

            ArrayList<PendingIntent> sentIntents = new ArrayList<>();
            ArrayList<PendingIntent> deliveryIntents = new ArrayList<>();

            sentIntents.add(sentIntent);
            deliveryIntents.add(deliveryIntent);

            smsManager.sendMultipartTextMessage(phoneNumber, sc, messageParts, sentIntents, deliveryIntents);
        } else {
            smsManager.sendTextMessage(phoneNumber, sc, message, sentIntent, deliveryIntent);
        }
    }

    /**
     * Concatenates a multi-part SMS into a single String
     *
     * @param context
     * @param intent  The SMS receive intent
     * @return The SMS
     */
    public static String combineMultipartSMS(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            Object[] pdus = (Object[]) bundle.get("pdus");
            if (pdus != null) {
                final SmsMessage[] messages = new SmsMessage[pdus.length];
                for (int i = 0; i < pdus.length; i++) {
                    messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                }
                StringBuilder content = new StringBuilder();
                if (messages.length > 0) {
                    for (SmsMessage message : messages) {
                        content.append(message.getMessageBody());
                    }
                }
                return content.toString();
            }
        }
        return null;
    }
}