/**
 * Copyright (C) 2017 - 2021 The Project-Xtended
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.xtended.preferences;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.UserHandle;
import android.provider.Settings;
import android.util.Log;

import com.xtended.fragments.XAmbientService;

public final class DozeUtils {

    private static final String TAG = "DozeUtils";
    private static final boolean DEBUG = false;
    private static boolean mServiceEnabled = false;

    public static final String DOZE_INTENT = "com.android.systemui.doze.pulse";

    private static void startService(Context context) {
        if (DEBUG) Log.d(TAG, "Starting service");
        context.startServiceAsUser(new Intent(context, XAmbientService.class),
                UserHandle.CURRENT);
        mServiceEnabled = true;
    }

    private static void stopService(Context context) {
        if (DEBUG) Log.d(TAG, "Stopping service");
        mServiceEnabled = false;
        context.stopServiceAsUser(new Intent(context, XAmbientService.class),
                UserHandle.CURRENT);
    }

    public static boolean getTiltSensor(Context context) {
        return context.getResources().getBoolean(
                com.android.internal.R.bool.config_dozePulseTilt);
    }

    public static boolean getPickupSensor(Context context) {
        return context.getResources().getBoolean(
                com.android.internal.R.bool.config_dozePulsePickup);
    }

    public static boolean getProximitySensor(Context context) {
        return getProxCheckBeforePulse(context) && context.getResources().getBoolean(
                com.android.internal.R.bool.config_dozePulseProximity);
    }

    private static boolean getProxCheckBeforePulse(Context context) {
        try {
            Context con = context.createPackageContext("com.android.systemui", 0);
            int id = con.getResources().getIdentifier("doze_proximity_check_before_pulse",
                    "bool", "com.android.systemui");
            return con.getResources().getBoolean(id);
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    public static boolean isDozeEnabled(Context context) {
        return Settings.Secure.getIntForUser(context.getContentResolver(),
                Settings.Secure.DOZE_ENABLED, 1, UserHandle.USER_CURRENT) != 0;
    }

    public static boolean isDozeAlwaysOnEnabled(Context context) {
        return Settings.Secure.getIntForUser(context.getContentResolver(),
                Settings.Secure.DOZE_ALWAYS_ON, 0, UserHandle.USER_CURRENT) != 0;
    }

    public static void enableService(Context context) {
        if (!getTiltSensor(context) && !getPickupSensor(context) && !getProximitySensor(context)) return;
        if (sensorsEnabled(context) && !mServiceEnabled) {
            startService(context);
        } else if (!sensorsEnabled(context) && mServiceEnabled) {
            stopService(context);
        }
    }

    public static void launchDozePulse(Context context) {
        if (DEBUG) Log.d(TAG, "Launch doze pulse");
        context.sendBroadcastAsUser(new Intent(DOZE_INTENT),
                UserHandle.CURRENT);
    }

    public static boolean tiltEnabled(Context context) {
        return Settings.Secure.getIntForUser(context.getContentResolver(),
                Settings.Secure.DOZE_TILT_GESTURE, 0, UserHandle.USER_CURRENT) == 1;
    }

    public static boolean pickUpEnabled(Context context) {
        return Settings.Secure.getIntForUser(context.getContentResolver(),
                Settings.Secure.DOZE_PICK_UP_GESTURE, 0, UserHandle.USER_CURRENT) == 1;
    }

    public static boolean handwaveGestureEnabled(Context context) {
        return Settings.Secure.getIntForUser(context.getContentResolver(),
                Settings.Secure.DOZE_HANDWAVE_GESTURE, 0, UserHandle.USER_CURRENT) == 1;
    }

    public static boolean pocketGestureEnabled(Context context) {
        return Settings.Secure.getIntForUser(context.getContentResolver(),
                Settings.Secure.DOZE_POCKET_GESTURE, 0, UserHandle.USER_CURRENT) == 1;
    }

    public static boolean sensorsEnabled(Context context) {
        return tiltEnabled(context) || pickUpEnabled(context) || handwaveGestureEnabled(context)
                || pocketGestureEnabled(context);
    }

    public static Sensor getSensor(SensorManager sm, String type) {
        for (Sensor sensor : sm.getSensorList(Sensor.TYPE_ALL)) {
            if (type.equals(sensor.getStringType())) {
                return sensor;
            }
        }
        return null;
    }
}

