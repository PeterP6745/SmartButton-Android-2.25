package messagelogix.com.smartbuttoncommunications.gcm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

//import com.google.android.gms.gcm.GcmListenerService;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

import messagelogix.com.smartbuttoncommunications.R;
import messagelogix.com.smartbuttoncommunications.activities.core.TabBarActivity;
import messagelogix.com.smartbuttoncommunications.activities.login.LoginActivity;
import messagelogix.com.smartbuttoncommunications.notifications.NotificationsDetailActivity;
import messagelogix.com.smartbuttoncommunications.notifications.NotificationsDetailFragment;
import messagelogix.com.smartbuttoncommunications.utils.Config;
import messagelogix.com.smartbuttoncommunications.utils.LogUtils;
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
public class MyFcmListenerService extends FirebaseMessagingService {

    private Context context = this;

    private static final String TAG = "MyGcmListenerService";

    public static final String REPORT_TYPE = "reportType";

    private static final String SB_TITLE = "Smart Button";

    public static final String SHOW_PUSH_TAB_ACTION = "SHOW_PUSH_TAB";

    public static final String SHOW_PUSH_DETAIL_ACTION = "SHOW_PUSH_DETAIL";

    public static final String SHOW_CHAT_TAB_ACTION = "SHOW_CHAT_TAB";

    public static final String PUSH_NOTIFICATION = "1";

    public static final String TWO_WAY = "2";


    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        Log.e("NEW_TOKEN",s);

    }

    /**
     * Called when message is received.

     */
    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage rMessage) {

       // String from = rMessage.getFrom();
        Map data = rMessage.getData();
        Log.i("PushNotifNotice","Raw Data: \n" + data);

        String id = "";
        if(data.get("campaign_id") != null){
            Log.d("PushNotifNotice","raw data - campaignid - " + data.get("campaign_id"));
            id = data.get("campaign_id").toString();
            //Log.d("PushNotifNotice","extracted data- id - " + id);
        }
        String subject = "";//data.get("subject").toString();
        if(data.get("subject") !=null){
            subject = data.get("subject").toString();
        }
        String msg = "";
        if(data.get("message") != null){
            msg = data.get("message").toString();
        }
        String date = "";//data.get("schedule_datetime").toString();
        if(data.get("schedule_datetime") != null){
            Log.d("PushNotifNotice","raw data - campaignid - " + data.get("campaign_id"));
            date = data.get("schedule_datetime").toString();
        }
        String type = "";//(String) data.get("type");
        if(data.get("type") != null){
            type = data.get("type").toString();
        }

        String clickAction = rMessage.getNotification().getClickAction();
        if(clickAction == null){
            clickAction = "nothing";
        }
        else{
            clickAction = rMessage.getNotification().getClickAction();
        }

        if(type!=null) {
            if(type.equals("2")) {
                showChatNotification(msg);
                updateTabs(context,type);

                reloadChatList(context);
            } else {
                /*******/
                showPushNotification(id, subject, msg, date, "1", clickAction);
                updateTabs(context,"1");

                reloadNotifList(context);
            }
        }
    }
    // [END receive_message]

    static void reloadNotifList(Context context) {
        Log.d("reloading","calling reloadNotifList");
        Intent intent = new Intent("reloadNotifList");
        context.sendBroadcast(intent);
    }
    
    // This function will create an intent. This intent must take as parameter the "unique_name" that you registered your activity with
    static void reloadChatList(Context context) {
        Log.d("reloading","calling reloadChatList");
        Intent intent = new Intent("reloadChatList");
        //send broadcast
        context.sendBroadcast(intent);
    }

    static void updateTabs(Context context,String type) {
        Intent intent = new Intent("updateTabs");
        //put whatever data you want to send, if any
         intent.putExtra("type", type);
        //send broadcast
        context.sendBroadcast(intent);
    }

    /**
     * Create and show a simple notification containing the received GCM message.
     */
    private void showPushNotification(String id, String subject, String message, String date, String type, String clickAction) {
        Log.i("PushNotif","Attempting to show Push Notification");
        //Intent resultIntent = new Intent(clickAction);
        Intent resultIntent = new Intent(context, NotificationsDetailActivity.class);
        resultIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
        resultIntent.putExtra(NotificationsDetailFragment.ARG_ITEM_ID, id);
        //resultIntent.putExtra(NotificationsDetailFragment.ARG_SUBJECT, subject);
        //resultIntent.putExtra(NotificationsDetailFragment.ARG_MESSAGE, message);
        //resultIntent.putExtra(NotificationsDetailFragment.ARG_DATE, date);

        LogUtils.debug("PUSHNOTICE","id - "+id);
        LogUtils.debug("PUSHNOTICE","date - "+date);
        LogUtils.debug("PUSHNOTICE","message - "+message);
        LogUtils.debug("PUSHNOTICE","clickAction - "+clickAction);

        resultIntent.putExtra(REPORT_TYPE, type);
        //resultIntent.setAction(SHOW_PUSH_DETAIL_ACTION);
        //resultIntent.setAction(SHOW_PUSH_TAB_ACTION);

        // Because clicking the notification opens a new ("special") activity, there's
        // no need to create an artificial back stack.
        PendingIntent pendingIntent = PendingIntent.getActivity(
                        this,
                        0,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        // Sets the uri for custom sound - sb_alert_sound.mp3
        Uri customSoundUri = Uri.parse("android.resource://"+context.getPackageName()+"/"+R.raw.sb_alert_sound);

//        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
//                .setSmallIcon(R.drawable.ic_stat_ic_sb_notifications_white_24dp)
//                .setContentTitle(SB_TITLE)
//                .setContentText(message)
//                .setAutoCancel(true)
//                .setContentIntent(pendingIntent);
//
//        NotificationManager notificationManager =
//                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//        notificationManager.notify(Integer.valueOf(id) /* ID of notification */,
//                notificationBuilder.build()
//        );

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, getString(R.string.default_channel_id))
                .setContentTitle("SmartButton® New Emergency Broadcast")
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setStyle(new NotificationCompat.BigTextStyle())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setAutoCancel(true)
                .setSound(customSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
       /* if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

            if(customSoundUri != null){
                // Changing Default mode of notification
                notificationBuilder.setDefaults(Notification.DEFAULT_VIBRATE);
                // Creating an Audio Attribute
                AudioAttributes audioAttributes = new AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .build();

                // Creating Channel
                NotificationChannel notificationChannel = new NotificationChannel("push_notification_channel","Emergency Broadcast",NotificationManager.IMPORTANCE_HIGH);
                notificationChannel.setSound(customSoundUri,audioAttributes);
                mNotificationManager.createNotificationChannel(notificationChannel);
            }
        }*/

//        NotificationsMenuActivity nma = new NotificationsMenuActivity();
//        nma.getPushNotifCell().performClick();

        mNotificationManager.notify(0, notificationBuilder.build());

        //notificationManager.notify(0, notificationBuilder.build());
        Log.i("PushNotif","FINISHED Attempting to show Push Notification");
    }

    public void showChatNotification(String message){
        Log.i("PushNotif","Attempting to show Chat Notification");
        boolean showNotification = true;
        Intent resultIntent;

        if(Preferences.getBoolean(Config.IS_LOGGED_IN)) {
            resultIntent = new Intent(this, TabBarActivity.class);
            resultIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |Intent.FLAG_ACTIVITY_NEW_TASK);
            resultIntent.setAction(SHOW_CHAT_TAB_ACTION);
            //set to chat tab
            Preferences.putBoolean(Config.FLAG, true);
        } else {
            resultIntent = new Intent(this,LoginActivity.class);
            resultIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |Intent.FLAG_ACTIVITY_NEW_TASK);//(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        }

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, getString(R.string.chat_channel_id))
                .setContentTitle("SmartButton® New Conversation Message")
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setStyle(new NotificationCompat.BigTextStyle())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        if(TabBarActivity.isLoaded){
            int currentTabIndx = TabBarActivity.tabHost.getCurrentTab();
            //check if user is at chat tab
            if(currentTabIndx == 4){
                showNotification = false;
            }
        }

        if(showNotification){
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(2, notificationBuilder.build());
        }
    }

//    public void showCNotification(String message) {
//        boolean showNotification = true;
//
//        //Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//        Intent resultIntent;
//
//        if(Preferences.getBoolean(Config.IS_LOGGED_IN))
//        {
//            resultIntent = new Intent(this, TabBarActivity.class);
//            resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            //set to chat tab
//            Preferences.putBoolean(Config.FLAG, true);
//        }
//        else{
//            resultIntent = new Intent(this,LoginActivity.class);
//            resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);//(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
//
//        }
//
//        PendingIntent pi = PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_CANCEL_CURRENT);
//
//        Notification notification = new NotificationCompat.Builder(this)
//              //.setTicker(r.getString(R.string.notification_title))
//                .setSmallIcon(R.mipmap.ic_launcher)
//                .setContentTitle("New conversation message")
//                .setContentText(message)
//                .setContentIntent(pi)
//                .setAutoCancel(true)
//                .setDefaults(Notification.DEFAULT_ALL)
//                .setPriority(Notification.PRIORITY_HIGH)
//
//                .build();
//        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//
//        if(TabBarActivity.isLoaded){
//            int currentTabIndx = TabBarActivity.tabHost.getCurrentTab();
//            //check if user is at chat tab
//            if(currentTabIndx == 4){
//                showNotification = false;
//            }
//        }
//
//        if(showNotification){
//            try{
//
//                String channelId = "default_channel_id";
//                String channelDescription = "Default Channel";
//                // Since android Oreo notification channel is needed.
//                //Check if notification channel exists and if not create one
//                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
//                    NotificationChannel notificationChannel = notificationManager.getNotificationChannel(channelId);
//                    if (notificationChannel == null) {
//                        int importance = NotificationManager.IMPORTANCE_HIGH; //Set the importance level
//                        notificationChannel = new NotificationChannel(channelId, channelDescription, importance);
//                        notificationChannel.setLightColor(Color.RED); //Set if it is necesssary
//                        notificationChannel.enableVibration(true); //Set if it is necesssary
//                        notificationManager.createNotificationChannel(notificationChannel);
//                    }
//                }
//                //Then notify
//                notificationManager.notify(0, notification);
//                //PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
//                //PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK|PowerManager.ACQUIRE_CAUSES_WAKEUP, TAG); //deprecated but only available method to achieve this
//                //wl.acquire(15000);
//            }
//            catch (Exception e){
//                Log.e("push notif", "\nException: " + e.getMessage());
//            }
//
//        }
//
//    }

    //override onMessageReceived(bundle data, string id){
    //        String id =  data.get("campaign_id").toString();
    //        String subject = data.get("subject").toString();
    //        String message = data.get("message").toString();
    //        String date = data.get("schedule_datetime").toString();
    //        String type = data.get("type").toString();//TODO: Add check for type
    //
    //        //Strip Characters or your message will look like this: {"message":"Sample Message"}
    //        assert message != null;
    //        int stringCount = message.length();
    //        StringBuilder message2 = new StringBuilder(message);
    //        message2.delete(stringCount-2,stringCount);
    //        //{"message":"Sample Message"} --> {"message":"Sample Message
    //        message2.delete(1,12);
    //        //{"message":"Sample Message --> {Sample Message
    //        message2.deleteCharAt(0);
    //        //{Sample Message --> Sample Message
    //        message = message2.toString();
    //
    //
    //        Log.d("PUSHNOTIF", "\nPush Notification Data:\nID: " + id + "\nSubject: " + subject + "\nMessage: " + message + "\ndate: " + date + "\nType: " + type);
    //        /**
    //         * Production applications would usually process the message here.
    //         * Eg: - Syncing with server.
    //         *     - Store message in local database.
    //         *     - Update UI.
    //         */
    //        /**
    //         * In some cases it may be useful to show a notification indicating to the user
    //         * that a message was received.
    //         */
    //
    //
    //        if(type!=null && type.equals("2")) {
    //            showChatNotification(message);
    //            updateTabs(context,type);
    //        }
    //
    //        else if(type!=null){
    //            sendNotification(id, subject, message, date, "1");
    //            updateTabs(context,"1");
    //        }
    //        else{
    //            sendNotification(id, subject, message, date, "1");
    //            updateTabs(context,"1");
    //        }
    //
    //        updateLists(context);
    // }
}
