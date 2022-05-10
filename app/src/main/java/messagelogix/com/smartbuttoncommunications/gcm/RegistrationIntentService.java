package messagelogix.com.smartbuttoncommunications.gcm;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

//import com.google.android.gms.gcm.GcmPubSub;
//import com.google.android.gms.gcm.GoogleCloudMessaging;
//import com.google.android.gms.iid.InstanceID;

import java.io.IOException;
import java.util.HashMap;

import messagelogix.com.smartbuttoncommunications.BuildConfig;
import messagelogix.com.smartbuttoncommunications.utils.Config;
import messagelogix.com.smartbuttoncommunications.utils.FunctionHelper;
import messagelogix.com.smartbuttoncommunications.utils.Preferences;

/**
 * Copyright 2015 Google Inc. All Rights Reserved.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
public class RegistrationIntentService extends IntentService {

    public static final String SMART_BUTTON_APP_TYPE = "3";

    private Context context = this;

    private static final String LOG_TAG = RegistrationIntentService.class.getSimpleName();

    private static final String[] TOPICS = {"global"};

    public static final String ANDROID_DEVICE_TYPE_VALUE = "2";

    public RegistrationIntentService() {

        super(LOG_TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        Preferences.init(this);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        try {
            // [START register_for_gcm]
            // Initially this call goes out to the network to retrieve the token, subsequent calls
            // are local.
            // [START get_token]
            /*InstanceID instanceID = InstanceID.getInstance(this);
            String token = instanceID.getToken(getString(R.string.gcm_defaultSenderId),
                    GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
            Preferences.putString(Config.DEVICE_TOKEN, token);*/
            // [END get_token]
            //Log.i(LOG_TAG, "GCM Registration Token: " + token);
           // sendRegistrationToServer(token);
            // Subscribe to topic channels
            //subscribeTopics(token);
            // You should store a boolean that indicates whether the generated token has been
            // sent to your server. If the boolean is false, send the token to your server,
            // otherwise your server should have already received the token.
           // sharedPreferences.edit().putBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, true).apply();
            // [END register_for_gcm]
        } catch (Exception e) {
            Log.d(LOG_TAG, "Failed to complete token refresh", e);
            // If an exception happens while fetching the new token or updating our registration data
            // on a third-party server, this ensures that we'll attempt the update at a later time.
            sharedPreferences.edit().putBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, false).apply();
        }
        // Notify UI that registration has completed, so the progress indicator can be hidden.
        Intent registrationComplete = new Intent(QuickstartPreferences.REGISTRATION_COMPLETE);
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }

    /**
     * Persist registration to third-party servers.
     * <p/>
     * Modify this method to associate the user's GCM registration token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(String token) {
        // Add custom implementation, as needed.
      //  SaveDeviceInfoTask task = new SaveDeviceInfoTask(token);
     //   task.execute();
    }

    /**
     * Subscribe to any GCM topics of interest, as defined by the TOPICS constant.
     *
     * @param token GCM token
     * @throws IOException if unable to reach the GCM PubSub service
     */
    // [START subscribe_topics]
    private void subscribeTopics(String token) throws IOException {

        for (String topic : TOPICS) {
          //  GcmPubSub pubSub = GcmPubSub.getInstance(this);
           // pubSub.subscribe(token, "/topics/" + topic, null);
        }
    }
    // [END subscribe_topics]

    /**
     * Represents an asynchronous task to save the token to the server
     */
    public class SaveDeviceInfoTask2 extends AsyncTask<Void, Void, String> {

        private String deviceToken;

        SaveDeviceInfoTask2(String token) {

            this.deviceToken = token;
        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected String doInBackground(Void... voids) {

            HashMap<String, String> postDataParams = new HashMap<String, String>();
//            postDataParams.put("controller", "Push");
            postDataParams.put("controller", "GreenCow");
            postDataParams.put("action", "SaveDeviceTokenSB");
            postDataParams.put("accountId", Preferences.getString(Config.ACCOUNT_ID));
            postDataParams.put("deviceId", this.deviceToken);
            postDataParams.put("deviceType", ANDROID_DEVICE_TYPE_VALUE);
            postDataParams.put("app_type", SMART_BUTTON_APP_TYPE);
            postDataParams.put("production", BuildConfig.DEBUG ? "0" : "1");
            return FunctionHelper.apiCaller(context, postDataParams);
        }

        @Override
        protected void onPostExecute(final String responseData) {

            if (responseData != null) {
                Log.d(LOG_TAG, "responseData = " + responseData);
            } else {
                Log.d(LOG_TAG, "No JSON received ! :(");
            }
        }

        @Override
        protected void onCancelled() {

        }
    }
}
