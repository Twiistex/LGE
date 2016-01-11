/**
 * Copyright 2015 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.lumi_dos.lge;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.gcm.GcmPubSub;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import java.io.IOException;

public class RegistrationIntentService extends IntentService {

    private static final String TAG = "RegIntentService";
    public String token;

    public RegistrationIntentService() {
        super(TAG);
    }

    public SharedPreferences sharedPreferences;

    @Override
    protected void onHandleIntent(Intent intent) {

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        try {
            synchronized (TAG) {

                InstanceID instanceID = InstanceID.getInstance(this);
                token = instanceID.getToken(getString(R.string.sender_id),
                        GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
                Log.i(TAG, "GCM Registration Token: " + token);

                //sharedPreferences.edit().putString(Preferences.GCM_TOKEN, token).apply();

                sharedPreferences.edit().putBoolean(Preferences.DEVICE_REGISTERED, true).apply();
            }
        } catch (Exception e) {
            Log.d(TAG, "Failed to complete token refresh", e);
            sharedPreferences.edit().putBoolean(Preferences.DEVICE_REGISTERED, false).apply();
        }

        if(sharedPreferences.getBoolean("getGlobalNotifications", true)) {
            String topic = getString(R.string.topic_global);
            try {
                subscribeTopic(topic);
                Log.i("GCM", "Subscribed " + topic);
            } catch (IOException e) {
                e.printStackTrace();
                Log.i("GCM", "Error subscribing " + topic);
            }
        } else {
            String topic = getString(R.string.topic_global);
            try {
                unsubscribeTopic(topic);
                Log.i("GCM", "Unubscribed " + topic);
            } catch (IOException e) {
                e.printStackTrace();
                Log.i("GCM", "Error unsubscribing " + topic);
            }
        }

        if(sharedPreferences.getBoolean("getServerBootReport", false)) {
            String topic = getString(R.string.topic_serverbootreport);
            try {
                subscribeTopic(topic);
                Log.i("GCM", "Subscribed " + topic);
            } catch (IOException e) {
                e.printStackTrace();
                Log.i("GCM", "Error subscribing " + topic);
            }
        } else {
            String topic = getString(R.string.topic_serverbootreport);
            try {
                unsubscribeTopic(topic);
                Log.i("GCM", "Unubscribed " + topic);
            } catch (IOException e) {
                e.printStackTrace();
                Log.i("GCM", "Error unsubscribing " + topic);
            }
        }

        if(sharedPreferences.getBoolean("getHolidayCountdown", true)) {
            String topic = getString(R.string.topic_holidayCountdown);
            try {
                subscribeTopic(topic);
                Log.i("GCM", "Subscribed " + topic);
            } catch (IOException e) {
                e.printStackTrace();
                Log.i("GCM", "Error subscribing " + topic);
            }
        } else {
            String topic = getString(R.string.topic_holidayCountdown);
            try {
                unsubscribeTopic(topic);
                Log.i("GCM", "Unubscribed " + topic);
            } catch (IOException e) {
                e.printStackTrace();
                Log.i("GCM", "Error unubscribing " + topic);
            }
        }
        Intent registrationComplete = new Intent(Preferences.REGISTRATION_COMPLETE);
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }




    /**
     * Persist registration to third-party servers.
     *
     * Modify this method to associate the user's GCM registration token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */

    /**
     * Subscribe to any GCM topics of interest, as defined by the TOPICS constant.
     *
     * (@)param token GCM token
     * @throws IOException if unable to reach the GCM PubSub service
     */
    // [START subscribe_topics]
    private void subscribeTopic(String topic) throws IOException {
        GcmPubSub pubSub = GcmPubSub.getInstance(this);
        InstanceID instanceID = InstanceID.getInstance(this);
        token = instanceID.getToken(getString(R.string.sender_id),
                GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
        pubSub.subscribe(token, topic, null);
    }

    private void unsubscribeTopic(String topic) throws IOException {
        GcmPubSub pubSub = GcmPubSub.getInstance(this);
        InstanceID instanceID = InstanceID.getInstance(this);
        token = instanceID.getToken(getString(R.string.sender_id),
                GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
        pubSub.unsubscribe(token, topic);
    }
    // [END subscribe_topics]

}