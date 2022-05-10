package messagelogix.com.smartbuttoncommunications.activities.core;

import android.Manifest;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Rect;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import messagelogix.com.smartbuttoncommunications.R;
import messagelogix.com.smartbuttoncommunications.activities.conversations.ChatListActivity;
import messagelogix.com.smartbuttoncommunications.activities.conversations.ConversationsMenuActivity;
import messagelogix.com.smartbuttoncommunications.activities.help.HelpActivity;
import messagelogix.com.smartbuttoncommunications.activities.identity.IdentityActivity;
import messagelogix.com.smartbuttoncommunications.gcm.QuickstartPreferences;
import messagelogix.com.smartbuttoncommunications.gcm.RegistrationIntentService;
import messagelogix.com.smartbuttoncommunications.geofence.GeofenceTransitionsIntentService;
import messagelogix.com.smartbuttoncommunications.notifications.NotificationsMenuActivity;
import messagelogix.com.smartbuttoncommunications.notifications.NotificationsListActivity;
import messagelogix.com.smartbuttoncommunications.utils.Config;
import messagelogix.com.smartbuttoncommunications.utils.LanguageManager;
import messagelogix.com.smartbuttoncommunications.utils.LogUtils;
import messagelogix.com.smartbuttoncommunications.utils.MarshMallowPermission;
import messagelogix.com.smartbuttoncommunications.utils.Preferences;

/**
 * Created by Vahid
 * This is the part that takes care of the tabs in the bottom of our app. This activity is very important, since all of the activities are being called from inside this activity.
 */
public class TabBarActivity extends android.app.TabActivity implements TabHost.OnTabChangeListener {

    //private static String CHANNEL_ID = "emergency_broadcasts";

    private static final String LOG_TAG = TabBarActivity.class.getSimpleName();

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    //Tab test start
    public static Button btnRed; // Works as a badge

    //tab test end
    //Declared static; so it can be accessed from all other Activities
    public static TabHost tabHost;

    private Context context = this;

    private BroadcastReceiver mRegistrationBroadcastReceiver;

    private MarshMallowPermission marshMallowPermission = new MarshMallowPermission(this);

    private String badgeCount;

    private int tabCount;

    public static boolean isLoaded;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("[Tab LC]", "TabBarActivity onCreate()");
        Preferences.init(context);
        LogUtils.debug("NewLanguageManager","Preferences -> Config.Language value before passing to setLocale() --> "+Preferences.getString(Config.LANGUAGE));
        LanguageManager.setLocale(this,Preferences.getString(Config.LANGUAGE));
        //LanguageManager.setDefaultLocale(this,Preferences.getString(Config.LANGUAGE));
        setContentView(R.layout.activity_tab);

        Resources resources = this.getResources();
        Configuration config = resources.getConfiguration();
        LogUtils.debug("NewLanguageManager","setLocale() - config.locale after new is --> "+config.locale);

        isLoaded =true;
        createDefaultNotificationChannel();
        createChatNotificationChannel();
       // createGeoNotificationChannel();

        tabHost = getTabHost();
        LogUtils.debug("LanguageManager","Config.Language value for tabHost object --> "+tabHost.getResources().getConfiguration().locale);

        tabHost.setOnTabChangedListener(this);
        //setTabHostListener();

        if (!marshMallowPermission.checkPermissionForNetworkState() ||
                !marshMallowPermission.checkPermissionForWifiState()) {
            marshMallowPermission.requestPermissionForNetwork();
        }
//        //Ge the extras
//        Bundle extras = getIntent().getExtras();
//
//        if (extras != null) {
//            String curernt_tab = extras.getString(Config.CURRENT_TAB);
//            Log.d(LOG_TAG,"Current tab: "+curernt_tab);
//            // and get whatever type user account id is
//        }
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                //mRegistrationProgressBar.setVisibility(ProgressBar.GONE);
                SharedPreferences sharedPreferences =
                        PreferenceManager.getDefaultSharedPreferences(context);
                boolean sentToken = sharedPreferences
                        .getBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, false);
                if (sentToken) {
                    CharSequence text = getString(R.string.gcm_send_message);
                    //Toast.makeText(context,text,Toast.LENGTH_SHORT).show();
                } else {
                    CharSequence text = getString(R.string.token_error_message);
                    //Toast.makeText(context,text,Toast.LENGTH_SHORT).show();
                }
            }
        };

        if(checkPlayServices()) {
            // Start IntentService to register this application with GCM.
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        }

        boolean hasDesktops = Preferences.getBoolean(Config.HAS_DESKTOPS);
        boolean isSuperUser = Preferences.getBoolean(Config.IS_SUPER_USER);
        boolean hasPushToAppOnly = Preferences.getBoolean(Config.PUSH_TO_APP_ONLY);

        String tabIdentity = this.getResources().getString(R.string.identity);
        String tabHR = this.getResources().getString(R.string.help);
        String tabNotif = this.getString(R.string.notifications);
        String tabConvo = this.getString(R.string.conversations_action_bar);

        LogUtils.debug("NewLanguageManager","Tab Names:\nIdentity --> "+tabIdentity+"\nHelp&Resources --> "+tabHR+"\nNotifications --> "+tabNotif+"\nConversations --> "+tabConvo);

        //This is the section that adds the tabs to the bottom of the page.
        //Check user type for value "1". If true, hide Smart Button Home Page
        addTab("Smart Button", R.drawable.ic_touch_app_black_24dp, HomeActivity.class);
        addTab(tabIdentity, R.drawable.ic_person_black_24dp, IdentityActivity.class);
        addTab(tabHR, R.drawable.ic_help_black_24dp, HelpActivity.class);

        Log.d("isSuperUSER","answer: "+String.valueOf(isSuperUser));

        if(hasDesktops){
            if(isSuperUser)
                addTab(tabNotif, R.drawable.ic_message_black_24dp, NotificationsMenuActivity.class);
            else
                addTab(tabNotif, R.drawable.ic_message_black_24dp, NotificationsListActivity.class);
        }
        else {
            if(hasPushToAppOnly){
                if(isSuperUser)
                    addTab(tabNotif, R.drawable.ic_message_black_24dp, NotificationsMenuActivity.class);
                else
                    addTab(tabNotif, R.drawable.ic_message_black_24dp, NotificationsListActivity.class);
            } else
                addTab(tabNotif, R.drawable.ic_message_black_24dp, NotificationsListActivity.class);
        }

        Log.d(LOG_TAG,"TOGGLE: " + Preferences.getBoolean(Config.TWO_WAY_TOGGLE));
        //Check toggle for 2-way chat. If true, then add the tab
        if(Preferences.getBoolean(Config.TWO_WAY_TOGGLE))
        {
            //User Type 1 will not have chat tab
            if(!Preferences.getString(Config.LOGIN_TYPE).equals("1")){
                //some accounts will not have chat tab regardless of user type
                if(hasDesktops)
                    addTab(tabConvo, R.drawable.ic_forum_black_24dp, ConversationsMenuActivity.class);
                else
                    addTab(tabConvo, R.drawable.ic_forum_black_24dp, ChatListActivity.class);
            }
        }

        tabCount = tabHost.getTabWidget().getTabCount();
        Log.d(LOG_TAG, "TabCount: " + tabCount);

        //Update Badges on Tabs
        if(tabCount == 5) {
            badgeCount = Config.CHAT_BADGE_COUNT;
            //updateConversationsTab(/*tabCount,*/badgeCount);
        }

        badgeCount = Config.PUSH_BADGE_COUNT;
        //updateMessagesTab(badgeCount);
        //tabHost.setCurrentTab(0);

        //Init the preferences
        //Preferences.init(context);

        //Not sure what this does so commenting it out until I figure it out
//        if (Preferences.isAvailable(Config.CURRENT_TAB) && Preferences.isAvailable(Config.FLAG)) {
//            Log.d(LOG_TAG, "on create: " + Preferences.getString(Config.CURRENT_TAB));
//            int tempValue = Integer.valueOf(Preferences.getString(Config.CURRENT_TAB));
//            if (tempValue >= 0 && tempValue <= 4 && Preferences.getBoolean(Config.FLAG)) {
//                tabHost.setCurrentTab(tempValue);
//                Preferences.putBoolean(Config.FLAG, false);
//            }
//        }
    }

    /**
     * GEOFENCING START
     */

    public void SetGeoFenceTracker(final String geoName, Double latitude, Double longitude, Float radius) {
        Log.e("TabGeo", "Setting geofence:\n"  + "\nLatitude:" + latitude + "\nLongitude:" + longitude + "\nRadius:" + radius);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
    }

    private GeofencingClient geofencingClient;

    public void SetGeoFence(final String geoName, Double latitude, Double longitude, Float radius) {
        Log.e("TabGeo", "Setting geofence:\nName/ID:" + geoName + "\nLatitude:" + latitude + "\nLongitude:" + longitude + "\nRadius:" + radius);
        geofencingClient = LocationServices.getGeofencingClient(context);
        Geofence geofence = new Geofence.Builder()
                .setRequestId(UUID.randomUUID().toString())
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                .setCircularRegion(latitude, longitude, radius)
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .build();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        //Remove the geofence before adding it
        List<String> geofencesToRemove = new ArrayList<>();
        geofencesToRemove.add(geofence.getRequestId());
        geofencingClient.removeGeofences(geofencesToRemove);
        //Call the API to add the geofence
        geofencingClient.addGeofences(getGeofencingRequest(geofence), getGeofencePendingIntent())
                .addOnSuccessListener(this, new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Geofences added
                        // ...
                        Log.e("TabGeo", "Added geofence:\n" + geoName);

                        //re-init the active zone toggle
                        //simply use the "Exit" event to do the job
                        Intent intentExit = new Intent("geofenceEvent");
                        //send broadcast
                        String event = "exit";
                        intentExit.putExtra("event", event);
                        getApplicationContext().sendBroadcast(intentExit);
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Failed to add geofences
                        // ...
                        Log.e("TabGeo", "Something went wrong");
                    }
                });
    }

    private GeofencingRequest getGeofencingRequest(Geofence geofence) {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofence(geofence);
        return builder.build();
    }

    private PendingIntent geofencePendingIntent;

    private PendingIntent getGeofencePendingIntent() {
        // Reuse the PendingIntent if we already have it.
        if(geofencePendingIntent != null)
            return geofencePendingIntent;

        Intent intent = new Intent(this, GeofenceTransitionsIntentService.class);
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when
        // calling addGeofences() and removeGeofences().
        geofencePendingIntent = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return geofencePendingIntent;
    }

    //Broadcast receiver for handling geofence events
    //Listen for incoming notifications and execute method when notifications are received
    private BroadcastReceiver geoFenceReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String event = intent.getStringExtra("event");

            if(event.equals("enter")){
                Log.e("[TABGEO RECEIVER]","Smart Button Enabled");
                //enable the smart button
                Preferences.putBoolean(Config.GEOFENCE_IN_ACTIVE_ZONE, true);
                Toast.makeText(getApplicationContext(), "You have entered a geofence. Smart Button is now enabled", Toast.LENGTH_LONG).show();

            } else if(event.equals("exit")) {
                Log.e("[TABGEO RECEIVER]","Smart Button Disabled");
                Preferences.putBoolean(Config.GEOFENCE_IN_ACTIVE_ZONE, false);
                //Don't display this message for Super Users.
                if(!Preferences.getBoolean(Config.IS_SUPER_USER)){
                    Toast.makeText(getApplicationContext(), "You are not currently inside the Active Zone set for your account. Please enter the zone to enable the Smart Button®", Toast.LENGTH_LONG).show();
                }
            } else if(event.equals("set")) {
                double latitude = intent.getDoubleExtra("lat", 0);
                double longitude = intent.getDoubleExtra("long", 0);
                float radius = intent.getFloatExtra("radius", 0);
                String geoName = intent.getStringExtra("name");
                SetGeoFenceTracker(geoName, latitude,longitude,radius);
            }
        }
    };

    private void createDefaultNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.default_channel_name); //Visible name for users
            String description = getString(R.string.default_channel_description); //Visible description for users
            int importance = NotificationManager.IMPORTANCE_HIGH; //Notification Behavior

            //Declare custom sound URI
            Uri customSoundUri = Uri.parse("android.resource://"+context.getPackageName()+"/"+R.raw.sb_alert_sound);
            //Declare Audio Behavior
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .build();

            //Create the channel
            //Channel ID
            NotificationChannel channel = new NotificationChannel(getString(R.string.default_channel_id), name, importance);
            channel.setDescription(description);
            channel.setSound(customSoundUri,audioAttributes);

            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void createChatNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.chat_channel_name); //Visible name for users
            String description = getString(R.string.chat_channel_description); //Visible description for users
            int importance = NotificationManager.IMPORTANCE_HIGH; //Notification Behavior
            //Declare custom sound URI
            Uri customSoundUri = Uri.parse("android.resource://"+context.getPackageName()+"/"+R.raw.sb_alert_sound);
            //Declare Audio Behavior
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build();

            //Create the channel
            //Channel ID
            NotificationChannel channel = new NotificationChannel(getString(R.string.chat_channel_id), name, importance);
            channel.setDescription(description);
            channel.setSound(customSoundUri,audioAttributes);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    /**
     * GEOFENCING END
     */
//    private void createGeoNotificationChannel() {
//        // Create the NotificationChannel, but only on API 26+ because
//        // the NotificationChannel class is new and not in the support library
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            CharSequence name = getString(R.string.geo_channel_name); //Visible name for users
//            String description = getString(R.string.geo_channel_description); //Visible description for users
//            int importance = NotificationManager.IMPORTANCE_HIGH; //Notification Behavior
//            //Declare custom sound URI
//            Uri customSoundUri = Uri.parse("android.resource://"+context.getPackageName()+"/"+R.raw.sb_alert_sound);
//            //Declare Audio Behavior
//            AudioAttributes audioAttributes = new AudioAttributes.Builder()
//                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
//                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
//                    .build();
//
//            //Create the channel
//            NotificationChannel channel = new NotificationChannel("geofences", name, importance);//Channel ID
//            channel.setDescription(description);
//            channel.setSound(customSoundUri,audioAttributes);
//            // Register the channel with the system; you can't change the importance
//            // or other notification behaviors after this
//            NotificationManager notificationManager = getSystemService(NotificationManager.class);
//            notificationManager.createNotificationChannel(channel);
//            Log.e("TAB","CREATED CHANNEL ID: " + "geofences");
//        }
//    }

    private void checkPermissions(int currentTab) {

        switch (currentTab) {
            case 0:
                /**
                 * first check for no location toggle and handle accordingly
                 * noLoc = false e.g check location permissions as well
                 * noLoc = true e.g do not check for location permissions
                 */
                LogUtils.debug("[TabBarActivity]","What is the NoLocToggle --> "+Preferences.getBoolean(Config.NO_LOC_TOGGLE));
                if(!Preferences.getBoolean(Config.NO_LOC_TOGGLE)){
                    Boolean locPerm = marshMallowPermission.checkPermissionForFineLocation();
                    Boolean cameraPerm = marshMallowPermission.checkPermissionForCamera();
                    Boolean extStoragePerm = marshMallowPermission.checkPermissionForExternalStorage();
                    Boolean contactsPerm = marshMallowPermission.checkPermissionForContact();
                    LogUtils.debug("[TabBarActivity","Permission status:\nfinelocation --> "+locPerm+"\ncamera --> "+cameraPerm+"\nexternalstorage --> "+extStoragePerm+"\ncontacts --> "+contactsPerm);
                    if (!locPerm || !cameraPerm || !extStoragePerm || !contactsPerm) {
                        marshMallowPermission.requestPermissionsForApp();
                    }

                } else {
                    Boolean cameraPerm = marshMallowPermission.checkPermissionForCamera();
                    Boolean extStoragePerm = marshMallowPermission.checkPermissionForExternalStorage();
                    Boolean contactsPerm = marshMallowPermission.checkPermissionForContact();
                    if (!cameraPerm || !extStoragePerm || !contactsPerm) {
                        marshMallowPermission.requestPermissionsForAppNoLoc();
                    }
                }

                break;
            case 1:
//                if (
//                        !marshMallowPermission.checkPermissionForCamera() ||
//                        !marshMallowPermission.checkPermissionForExternalStorage() ||
//                        !marshMallowPermission.checkPermissionForContact()
//                        ) {
//                    marshMallowPermission.requestPermissionsForApp();
//                }
                break;
            case 2:
                break;
            case 3:
                break;
            default:
                break;
        }
    }

    private void addTab(String labelId, int drawableId, Class<?> c) {
        //TabHost tabHost = getTabHost();
        LogUtils.debug("TabBarActivity","addTab() --> label id is: "+labelId);
        Intent intent = new Intent(this, c);
        TabHost.TabSpec spec = tabHost.newTabSpec("tab" + labelId);
        View tabIndicator = LayoutInflater.from(this).inflate(R.layout.tab_indicator, getTabWidget(), false);
     //   View tabIndicator = LayoutInflater.from(this).inflate(R.layout.badged_tab, getTabWidget(), false);
        TextView title = (TextView) tabIndicator.findViewById(R.id.title);
        title.setText(labelId);
        ImageView icon = (ImageView) tabIndicator.findViewById(R.id.icon);
        icon.setImageResource(drawableId);
        TextView badgeIcon = (TextView) tabIndicator.findViewById(R.id.tab_badge);
        badgeIcon.setVisibility(View.GONE);
        spec.setIndicator(tabIndicator);
        spec.setContent(intent);
        tabHost.addTab(spec);
    }

    private void wipeStoredDataLists() {
        Preferences.remove(Config.INCIDENTTYPE_LIST_LOADDATE);
        Preferences.remove(Config.INCIDENTTYPE_LIST);

        Preferences.remove(Config.SCHOOLBUILDING_LIST_LOADDATE);
        Preferences.remove(Config.SCHOOLBUILDING_LIST);

        Preferences.remove(Config.TITLE_LIST_LOADDATE);
        Preferences.remove(Config.TITLE_LIST);

        Preferences.remove(Config.HELPRESOURCES_LIST_LOADDATE);
        Preferences.remove(Config.HELPRESOURCES_LIST);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_logout:
                LogUtils.debug("SigningOut","CURRENTLY LOGGING OUT THROUGH THE LOGOUT METHOD IN TABBARACTIVITY");
                wipeStoredDataLists();
                Preferences.clearProfilePicture();
                Preferences.putBoolean(Config.IS_LOGGED_IN, false);

                Intent intent = new Intent(this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;
            case R.id.action_change_language:
                Log.d("Zebra","Before showChangeLangDialog is Called");
                showChangeLangDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void showChangeLangDialog() {   //This will bring up the dialog for the user to change languages
        Log.d("languageChange","showChangeLangDialog is Working!");
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.activity_show_language_settings, null);
        dialogBuilder.setView(dialogView);

        final Spinner spinner1 = (Spinner) dialogView.findViewById(R.id.spinner1);

        dialogBuilder.setTitle(R.string.select_language);

        dialogBuilder.setPositiveButton(R.string.change, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                int langpos = spinner1.getSelectedItemPosition();
                switch(langpos) {
                    case 0: //English
                        PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("LANG", "en").commit();
//                        Preferences.putString("langCode", "en");
//                        setLangRecreate("en");
                        changeLang("en");
                        return;
                    case 1: //Spanish
                        PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("LANG", "es").commit();
//                        Preferences.putString("langCode", "es");
//                        setLangRecreate("es");
                        changeLang("es");
//                        setLanguageForApp("es");
                        return;
                    /*case 2: //Vietnamese
                        PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("LANG", "vi").commit();
                        Preferences.putString("langCode", "vi");
                        setLangRecreate("vi");
                        return;
                    case 3: //Arabic
                        PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("LANG", "ar").commit();
                        setLangRecreate("ar");
                        return;*/
                    default: //By default set to english
                        PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("LANG", "en").commit();
//                        setLangRecreate("en");
                        changeLang("en");
                        Log.d("language",spinner1.getSelectedItemPosition()+"");
                        return;
                }
            }
        });
        dialogBuilder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });
        AlertDialog b = dialogBuilder.create();
        b.show();
    }

    public void setLangRecreate(String langval) {
        Log.d("languagecChange",langval);

        Configuration config = getBaseContext().getResources().getConfiguration();

        Locale locale = new Locale(langval);
        Locale.setDefault(locale);
        Log.d("languagecChange", String.valueOf(locale));
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
        recreate();
    }

    //changeLang is used in the language
    public void changeLang(String langval) {
        Preferences.putString(Config.LANGUAGE,langval);
        wipeStoredDataLists(); // wiping the stored values so the spinners populate with the correct language
        Log.d("languagecChange",langval);
        Resources resources = getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        Configuration config = getBaseContext().getResources().getConfiguration();
        resources.updateConfiguration(config,dm);
        Locale locale = new Locale(langval);
        Locale.setDefault(locale);
        config.locale = locale; //setting the locale of config to the given value
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
        recreate(); //recreating the activity to set languagechanges
    }

    private void setLanguageForApp(String language) {
        String languageToLoad  = language; //pass the language code as param
        Locale locale;
        if(languageToLoad.equals("not-set")){
            locale = Locale.getDefault();
        }
        else {
            locale = new Locale(languageToLoad);
        }
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());
        recreate();
    }

    /**
     * on Create Options Menu
     *
     * @param menu object
     * @return true or false
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds options to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem langItem = menu.findItem(R.id.action_change_language);

        Boolean hasTranslation = Preferences.getSPInstance(this).contains(Config.HAS_TRANSLATIONS) ? Preferences.getBoolean(Config.HAS_TRANSLATIONS) : true;
        if(hasTranslation)
            langItem.setVisible(true);

        return true;
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i("LanguageManager", "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("[Tab LC]", "TabBarActivity onResume()");
        //Init the preferences
        //Preferences.init(context);

        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(QuickstartPreferences.REGISTRATION_COMPLETE));

        //REGISTER BROADCASTED EVENTS
        context.registerReceiver(mMessageReceiver, new IntentFilter("updateTabs"));
        context.registerReceiver(geoFenceReceiver, new IntentFilter("geofenceEvent"));

       // Log.d(LOG_TAG, "on resume: " + Preferences.getString(Config.CURRENT_TAB));
//        //Ge the extras
//        Bundle extras = getIntent().getExtras();
//        if (extras != null) {
//            String curernt_tab = extras.getString(Config.CURRENT_TAB);
//            Log.d(LOG_TAG,"Current tab: "+curernt_tab);
//            // and get whatever type user account id is
//        }
    }

    //Listen for incoming notifications and execute method when notifications are received
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String type = intent.getStringExtra("type");

            if(type.equals("2")) {
                int bInt = Integer.parseInt(Config.CHAT_BADGE_COUNT);
                bInt++;
                badgeCount = Integer.toString(bInt);
                Config.setChatBadgeCount(badgeCount);

                //updateConversationsTab(/*tabCount,*/badgeCount);
                updateTabBadgeCount(badgeCount,2,true);
                Toast.makeText(getApplicationContext(), context.getResources().getString(R.string.tabbar_chatreceived1), Toast.LENGTH_LONG).show();
            } else  {
                int bInt = Integer.parseInt(Config.PUSH_BADGE_COUNT);
                bInt++;
                badgeCount = Integer.toString(bInt);
                Config.setPushBadgeCount(badgeCount);
                //updateMessagesTab(badgeCount);
                updateTabBadgeCount(badgeCount,1, true);
                Toast.makeText(getApplicationContext(), context.getResources().getString(R.string.tabbar_notifreceived1), Toast.LENGTH_LONG).show();
            }
            Log.d(LOG_TAG,"Tabs were updated");
        }
    };

    //Update the tabs with badges
    public static void updateConversationsTab(String badgeCount){
        if(Preferences.getString(Config.LOGIN_TYPE).equals("1")) {
            //if user type is 1, stop here
            return;
        }

        int tabIndex = 4;
        if(Preferences.getBoolean(Config.TWO_WAY_TOGGLE)) {

            TextView tabBadge = (TextView) tabHost.getTabWidget().getChildAt(tabIndex).findViewById(R.id.tab_badge);

            if (badgeCount.equals("") || badgeCount.equals("0")) {
                tabBadge.setVisibility(View.GONE);
            } else {
                tabBadge.setText(badgeCount);
                tabBadge.setVisibility(View.VISIBLE);
            }
        }
        else Log.d(LOG_TAG, "updateConversationsTab: no available tab at index 4");
    }

    public static void updateMessagesTab(String badgeCount){
        int tabIndex = 3;
//        if(Preferences.getString(Config.LOGIN_TYPE).equals("1")){
//            tabIndex = 2;//1;
//        }
        TextView tabBadge = (TextView) tabHost.getTabWidget().getChildAt(tabIndex).findViewById(R.id.tab_badge);
        if(badgeCount.equals("") | badgeCount.equals("0")) {
            tabBadge.setVisibility(View.GONE);
        } else {
            tabBadge.setText(badgeCount);
            tabBadge.setVisibility(View.VISIBLE);
        }
    }

    public static void updateTabBadgeCount(String badgeCount, Integer pushType, Boolean fromNotif){

        Boolean isStudentUser = Preferences.getString(Config.LOGIN_TYPE).equals("1");

        int tabIndex = 0;
        if(isStudentUser) {
            if(pushType == 1)
                tabIndex = 3;
            else
                return;
        } else {
            if(pushType == 1)
                tabIndex = 3;
            else if(pushType == 2 && Preferences.getBoolean(Config.TWO_WAY_TOGGLE))
                tabIndex = 4;
            else
                return;
        }

        TextView tabBadge = (TextView) tabHost.getTabWidget().getChildAt(tabIndex).findViewById(R.id.tab_badge);
        if(badgeCount.equals("0")){
            tabBadge.setVisibility(View.GONE);
        } else if(tabBadge.getText() == "" || fromNotif == false) {
            tabBadge.setText(badgeCount);
            tabBadge.setVisibility(View.VISIBLE);
        }
        else {
            Integer currBadgeCount = Integer.valueOf(tabBadge.getText().toString());

            currBadgeCount += 1;
            tabBadge.setText(currBadgeCount.toString());
            tabBadge.setVisibility(View.VISIBLE);
        }
    }

    public static void decrementNotifTabBadgeCount() {
        Boolean isStudentUser = Preferences.getString(Config.LOGIN_TYPE).equals("1");
        LogUtils.debug("decrement","decrementing notif badge count");
        int tabIndex = 3;
        TextView tabBadge = (TextView) tabHost.getTabWidget().getChildAt(tabIndex).findViewById(R.id.tab_badge);

        if(tabBadge.getText().toString() == "" || tabBadge.getText().toString() == "0") {}
        else {

            Integer currBadgeCount = Integer.valueOf(tabBadge.getText().toString());
            //Log.d("decrement","badge count is NOT EMPTY - currbadgecount -> "+currBadgeCount);

            currBadgeCount -= 1;
            tabBadge.setText(currBadgeCount.toString());
            tabBadge.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e("[Tab LC]", "TabBarActivity onPause()");
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        context.unregisterReceiver(mMessageReceiver);
        context.unregisterReceiver(geoFenceReceiver);
    }

    @Override
    public void onTabChanged(String tabId) {
        int currentTab = tabHost.getCurrentTab();
        checkPermissions(currentTab);
        highlightTab(currentTab);
    }

    private void highlightTab(int index) {
        for (int i = 0; i < getTabHost().getTabWidget().getTabCount(); i++) {
            if (i == index) {
                //make blue
                View tabView = getTabHost().getTabWidget().getChildAt(i);
                if (tabView != null) {
                    ImageView icon = (ImageView) tabView.findViewById(R.id.icon);
                    icon.setColorFilter(getResources().getColor(R.color.colorPrimary));
                    TextView title = (TextView) tabView.findViewById(R.id.title);
                    title.setTextColor(getResources().getColor(R.color.colorPrimary));
                }
            } else {
                //make black
                View tabView = getTabHost().getTabWidget().getChildAt(i);
                if (tabView != null) {
                    ImageView icon = (ImageView) tabView.findViewById(R.id.icon);
                    icon.setColorFilter(getResources().getColor(R.color.gray));
                    TextView title = (TextView) tabView.findViewById(R.id.title);
                    title.setTextColor(getResources().getColor(R.color.gray));
                }
            }
        }
    }

    public static boolean isVisible(final View view) {
        if(view == null) {
            return false;
        }

        if(!view.isShown()) {
            return false;
        }

        final Rect actualPosition = new Rect();
        view.getGlobalVisibleRect(actualPosition);
        final Rect screen = new Rect(0,0, Resources.getSystem().getDisplayMetrics().widthPixels,Resources.getSystem().getDisplayMetrics().heightPixels);
        return actualPosition.intersect(screen);
    }
}
