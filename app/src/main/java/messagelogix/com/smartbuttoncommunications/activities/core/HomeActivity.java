package messagelogix.com.smartbuttoncommunications.activities.core;

import android.Manifest;
import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.hardware.Camera;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.VolleyError;

/*
 * SAMPLE CODE
 * -------------
 * -------------
 */
import android.os.Looper;
import android.content.IntentSender;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
/* --------------
 * --------------
 */

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import messagelogix.com.smartbuttoncommunications.Covid19SurveyClasses.Covid19SurveyActivity;
import messagelogix.com.smartbuttoncommunications.Covid19SurveyClasses.IncidentTypeItem;
import messagelogix.com.smartbuttoncommunications.R;
import messagelogix.com.smartbuttoncommunications.activities.login.EmailConfirmActivity;
import messagelogix.com.smartbuttoncommunications.activities.login.LoginActivity;
import messagelogix.com.smartbuttoncommunications.gcm.RegistrationIntentService;
import messagelogix.com.smartbuttoncommunications.model.Contact;
import messagelogix.com.smartbuttoncommunications.model.CustomStringArrayAdapter;
import messagelogix.com.smartbuttoncommunications.model.LocatorMessage;
import messagelogix.com.smartbuttoncommunications.model.Response;
import messagelogix.com.smartbuttoncommunications.model.SpinnerItem;
import messagelogix.com.smartbuttoncommunications.utils.ApiHelper;
import messagelogix.com.smartbuttoncommunications.utils.Config;
import messagelogix.com.smartbuttoncommunications.utils.CustomProgressDialog;
import messagelogix.com.smartbuttoncommunications.utils.DatabaseHandler;
import messagelogix.com.smartbuttoncommunications.utils.DeviceManager;
import messagelogix.com.smartbuttoncommunications.utils.DialConfirmation;
import messagelogix.com.smartbuttoncommunications.utils.EULA;
import messagelogix.com.smartbuttoncommunications.utils.FunctionHelper;
import messagelogix.com.smartbuttoncommunications.utils.LanguageManager;
import messagelogix.com.smartbuttoncommunications.utils.LogUtils;
import messagelogix.com.smartbuttoncommunications.utils.MarshMallowPermission;
import messagelogix.com.smartbuttoncommunications.utils.NetworkCheck;
import messagelogix.com.smartbuttoncommunications.utils.OperatingSystem;
import messagelogix.com.smartbuttoncommunications.utils.Preferences;
import messagelogix.com.smartbuttoncommunications.utils.RetryCounter;
import microsoft.aspnet.signalr.client.android.BuildConfig;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;
import uk.co.deanwild.materialshowcaseview.shape.CircleShape;
import uk.co.deanwild.materialshowcaseview.shape.RectangleShape;
import uk.co.deanwild.materialshowcaseview.shape.Shape;

/**
 * Created by Vahid
 * This is the home activity of the app. The page that allows user to press the smart button (red emergency button).
 */
public class HomeActivity extends AppCompatActivity
        implements MaterialShowcaseSequence.OnSequenceItemDismissedListener, MaterialShowcaseSequence.OnSequenceItemShownListener, LocationListener {

    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;
    private static final int REQUEST_CHECK_SETTINGS = 0x1;
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;

    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    private final static String KEY_REQUESTING_LOCATION_UPDATES = "requesting-location-updates";
    private final static String KEY_LOCATION = "location";
    private final static String KEY_LAST_UPDATED_TIME_STRING = "last-updated-time-string";
    private FusedLocationProviderClient mFusedLocationClient;
    private SettingsClient mSettingsClient;
    private LocationRequest mLocationRequest;
    private LocationSettingsRequest mLocationSettingsRequest;
    private LocationCallback mLocationCallback;
    private Location mCurrentLocation;
    private Boolean mRequestingLocationUpdates;
    private String mLastUpdateTime;
    private Boolean locationErrorEncountered;

    final String TAG = "GPS";
    private long UPDATE_INTERVAL = 2 * 1000;  /* 10 secs */
    private long FASTEST_INTERVAL = 2000; /* 2 sec */
    static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    final String PREFS_NAME = "MyPrefsFile";

    LocationRequest locationRequest;
    LocationCallback locationCallback;

    //BranCode Variables

    private static final String LOG_TAG = HomeActivity.class.getSimpleName();

    //This is the showcase ID
    private static final String SHOWCASE_ID = "1";

    private static final int GPS_REFRESH_GAP_TIME = 1000 * 60;

    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE = 0; // meters

    // The minimum time between updates in milliseconds
    private static final long MIN_TIME = 0; // milliseconds

    private static int TIMER_SETTING;

    MarshMallowPermission marshMallowPermission = new MarshMallowPermission(this);

    private Location currentBestLocation = null;

    private int demoCounter = 0;

    private ImageButton locatorButton;

    private double geofenceLong, geofenceLat;
    private float geofenceRad;
    //private double mLongitude, mLatitude;

    private String mStreetAddress = "";

    private Context context = this;

    private String messageId;

    private boolean timerIsElapsed;

    private String time;
    private String countDownTimer;

    private AlertDialog.Builder builder;

    private AlertDialog dialog;

    private CountDownTimer timer;

    private Spinner buildingSpinner;

    private Spinner messageSpinner;

    private Spinner languageSpinner;

    private FloatingActionButton fab;

    private FloatingActionButton flashLightFAB;

    private LocationManager locationManager;

    private LocationListener locationListener;

    //Added code 6/25/2020
    private Location location;

    private int FLASH_STATE = 0;

    private boolean covid19RelatedIncidentType = false;
    private String incidentTypeSCVParam;

    private boolean errorLoadingProfileInfo = false;
    private AlertDialog profileErrorDialog;
    //private ProgressDialog activityProgressDialog;
    private CustomProgressDialog activityProgressDialog;
    private CustomProgressDialog sbEmergencyAlertProgressDialog;
    private boolean callForProfileInfo = true;
    private boolean returningFromConfirmationActivity = false;
    //private EULA termsAndConditionsDialog;

    private AlertDialog.Builder covidDialogbuilder;
    private AlertDialog covidDialog;
    private boolean [] covid19Arrangement;
    private String concatSelections;
    private ArrayList<IncidentTypeItem> selectedCovid19SurveySelections = new ArrayList<>();
    private Boolean showQuestionnaire = null;

    //Broadcast receiver for handling geofence events
    //Listen for incoming notifications and execute method when notifications are received
    private BroadcastReceiver geoFenceReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String event = intent.getStringExtra("event");

            if (event.equals("enter")) {
                Log.e("[HOME RECEIVER]", "SB Enabled");
                //enable the smart button
                Preferences.putBoolean(Config.GEOFENCE_IN_ACTIVE_ZONE, true);
                checkTimeOut();
                //Toast.makeText(getApplicationContext(), "You have entered a geofence. Smart Button is now enabled", Toast.LENGTH_LONG).show();

            } else if (event.equals("exit")) {
                Log.e("[HOME RECEIVER]", "Smart Button Disabled");
                Preferences.putBoolean(Config.GEOFENCE_IN_ACTIVE_ZONE, false);
                checkTimeOut();
            }

        }
    };

    private String selectIncidentLangChange() {
        String selectIncidentText = "";
        if ("es".equals(Preferences.getString(Config.LANGUAGE))){
            selectIncidentText="Selecciona un Incidente";
        } else if ("en".equals(Preferences.getString(Config.LANGUAGE))){
            selectIncidentText="Select Incident";
        }

        return selectIncidentText;
    }

    private String selectSchoolSpinnerChange() {
        String selectSchoolText ="";
        if ("es".equals(Preferences.getString(Config.LANGUAGE))) {
            selectSchoolText="-Selecciona un Edificio-";
        } else if ("en".equals(Preferences.getString(Config.LANGUAGE))) {
            selectSchoolText="-Select School-";
        }

        return selectSchoolText;
    }

    private static boolean isCallingSupported(Context context, Intent intent) {
        boolean result = true;
        PackageManager manager = context.getPackageManager();
        List<ResolveInfo> infos = manager.queryIntentActivities(intent, 0);
        if(infos.size() <= 0) {
            result = false;
        }

        return result;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Preferences.init(context);
        //LanguageManager.setDefaultLocale(this, Preferences.getString(Config.LANGUAGE));
        setContentView(R.layout.activity_locate);
        LanguageManager.setLocale(this, Preferences.getString(Config.LANGUAGE)); //being called to change the language of the application. Needs to be called before the set content view or else the changes will be made but not shown.




        String patentString = this.getResources().getString(R.string.patent_notice);

        Log.e("Home LC", "onCreate -- locale code before is --> "+this.getResources().getConfiguration().locale);
        //Log.e("Home LC", "onCreate -- language code is --> "+Preferences.getString(Config.LANGUAGE));
        Log.e("Home LC", "onCreate -- patent string before is --> "+this.getResources().getString(R.string.patent_notice));

        //setContentView(R.layout.activity_locate);
        Log.e("Home LC", "onCreate -- locale code after is --> "+this.getResources().getConfiguration().locale);
        Log.e("Home LC", "onCreate -- patent string after is --> "+this.getResources().getString(R.string.patent_notice));

        TextView patentNotice = (TextView) findViewById(R.id.textView);
        patentNotice.setText(patentString);

        if("2".equals(Preferences.getString(Config.LOGIN_TYPE))) {
            /*
             * FUSEDLOCATIONPROVIDERCLIENT
             * START LOCATION UPDATES
             * -------------------------
             */
            mRequestingLocationUpdates = true;
            mLastUpdateTime = "";

            // Update values using data stored in the Bundle.
            updateValuesFromBundle(savedInstanceState);

            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
            mSettingsClient = LocationServices.getSettingsClient(this);

            // Kick off the process of building the LocationCallback, LocationRequest, and
            // LocationSettingsRequest objects.
            createLocationCallback();
            createLocationRequest();
            buildLocationSettingsRequest();
            /* -------------------------
             * -------------------------
             * -------------------------
             */
        }

        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
            @Override
            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                if (!task.isSuccessful()) {
                    //To do//
                    return;
                }

                // Get the Instance ID token//
                String token = task.getResult().getToken();
                Log.d("languageToken",token);
                String msg = getString(R.string.fcm_token, token);
                LogUtils.debug(LOG_TAG, msg);
                new SaveDeviceInfoTask(token).execute();
            }
        });

        TIMER_SETTING = Preferences.getInteger(Config.LOCATOR_TIMEOUT, 180) * 1000;

        initView();
        //if(enableOfflineMode()) return;

        if (/*Preferences.getString(Config.LOGIN_TYPE).equals("1") ||*/ "3".equals(Preferences.getString(Config.LOGIN_TYPE))) {
            //if user type is 3 (Law Enforcement), disable the smart button
            locatorButton.setBackgroundResource(R.drawable.sb_graphic_gray);

        }

        setTouchListenerForKeyboardDismissal();
        setupLongPress();

        if(Preferences.isAvailable(Config.COVIDSCREENING_ACCOUNT) && !Preferences.getBoolean(Config.COVIDSCREENING_ACCOUNT)) {
            set911Dialer();
        }
    }

    private void updateValuesFromBundle(Bundle savedInstanceState) {
        LogUtils.debug("myTag", "Outside of if statement in updateValuesFromBundle");
        if (savedInstanceState != null) {

            // Update the value of mRequestingLocationUpdates from the Bundle, and make sure that
            // the Start Updates and Stop Updates buttons are correctly enabled or disabled.
            LogUtils.debug("myTag", "SavedInstanceState not Null - updateValuesFromBundle");
            if (savedInstanceState.keySet().contains(KEY_REQUESTING_LOCATION_UPDATES)) {
                LogUtils.debug("myTag", "This is before the key");
                mRequestingLocationUpdates = savedInstanceState.getBoolean(KEY_REQUESTING_LOCATION_UPDATES);
                LogUtils.debug("myTag", "This is after, this is the key: " + mRequestingLocationUpdates);
            }

            // Update the value of mCurrentLocation from the Bundle and update the UI to show the
            // correct latitude and longitude.
            if (savedInstanceState.keySet().contains(KEY_LOCATION)) {
                // Since KEY_LOCATION was found in the Bundle, we can be sure that mCurrentLocation
                // is not null.
                mCurrentLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            }

            // Update the value of mLastUpdateTime from the Bundle and update the UI.
            if (savedInstanceState.keySet().contains(KEY_LAST_UPDATED_TIME_STRING)) {
                mLastUpdateTime = savedInstanceState.getString(KEY_LAST_UPDATED_TIME_STRING);
            }

            //updateUI();
        }

        return;
    }

    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();

        // Sets the desired interval for active location updates. This interval is
        // inexact. You may not receive updates at all if no location sources are available, or
        // you may receive them slower than requested. You may also receive updates faster than
        // requested if other applications are requesting location at a faster interval.
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);

        // Sets the fastest rate for active location updates. This interval is exact, and your
        // application will never receive updates faster than this value.
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);

        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    /**
     * Creates a callback for receiving location events.
     */
    private void
    createLocationCallback() {
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);

                if(locationResult == null) {
                    locationErrorEncountered = true;
                    return;
                }

                Location currLocation = locationResult.getLastLocation();
                LogUtils.debug("lastReadLoca", "" + currLocation);
                if(mCurrentLocation == null) {
                    LogUtils.debug("Updating currLoca","currentLocation is nil, so saving first read location");
                    mCurrentLocation = currLocation;
//                    mLatitude = currLocation.getLatitude();
//                    mLongitude = currLocation.getLongitude();
                    mLastUpdateTime = String.valueOf(currLocation.getTime());
                    locationErrorEncountered = false;
                }
                else {
                    Date currentTime = new Date(System.currentTimeMillis());
                    long timeElapsed = (currentTime.getTime() - mCurrentLocation.getTime());

                    float lastLocationAccuracy = mCurrentLocation.getAccuracy();
                    float currLocationAccuracy = currLocation.getAccuracy();

                    if(timeElapsed > 20000) {
                        LogUtils.debug("Updating currLoca","timeElapsed greater than 20 seconds, so updating location");
                        mCurrentLocation = currLocation;
//                        mLatitude = currLocation.getLatitude();
//                        mLongitude = currLocation.getLongitude();
                        mLastUpdateTime = String.valueOf(currLocation.getTime());

                        locationErrorEncountered = false;
                    }
                    else if(currLocationAccuracy <= lastLocationAccuracy) {
                        LogUtils.debug("Updating currLoca","last read location more accurate than older location, so updating location");
                        mCurrentLocation = currLocation;
//                        mLatitude = currLocation.getLatitude();
//                        mLongitude = currLocation.getLongitude();
                        mLastUpdateTime = String.valueOf(currLocation.getTime());

                        locationErrorEncountered = false;
                    }
                }

                //updateLocationUI();
            }
        };
    }

    private void buildLocationSettingsRequest() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        mLocationSettingsRequest = builder.build();
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        switch (requestCode) {
//            // Check for the integer request code originally supplied to startResolutionForResult().
//            case REQUEST_CHECK_SETTINGS:
//                switch (resultCode) {
//                    case Activity.RESULT_OK:
//                        Log.i(TAG, "User agreed to make required location settings changes.");
//                        // Nothing to do. startLocationupdates() gets called in onResume again.
//                        break;
//                    case Activity.RESULT_CANCELED:
//                        Log.i(TAG, "User chose not to make required location settings changes.");
//                        mRequestingLocationUpdates = false;
//                        //updateUI();
//                        break;
//                }
//                break;
//        }
//    }

//    public void startUpdatesButtonHandler(View view) {
//        if (!mRequestingLocationUpdates) {
//            mRequestingLocationUpdates = true;
//            //setButtonsEnabledState();
//            startLocationUpdates();
//        }
//    }

    public void stopUpdatesButtonHandler(View view) {
        // It is a good practice to remove location requests when the activity is in a paused or
        // stopped state. Doing so helps battery performance and is especially
        // recommended in applications that request frequent location updates.
        stopLocationUpdates();
    }

    /**
     * Requests location updates from the FusedLocationApi. Note: we don't call this unless location
     * runtime permission has been granted.
     */
    private void startLocationUpdates() {
        // Begin by checking if the device has the necessary location settings.
        mSettingsClient.checkLocationSettings(mLocationSettingsRequest).addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                Log.i(TAG, "All location settings are satisfied.");
                mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());

            }
        })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        int statusCode = ((ApiException) e).getStatusCode();
                        switch (statusCode) {
                            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                Log.i("fusedlocation", "Location settings are not satisfied. Attempting to upgrade " + "location settings ");
                                try {
                                    // Show the dialog by calling startResolutionForResult(), and check the
                                    // result in onActivityResult().
                                    ResolvableApiException rae = (ResolvableApiException) e;
                                    rae.startResolutionForResult(HomeActivity.this, REQUEST_CHECK_SETTINGS);
                                } catch (IntentSender.SendIntentException sie) {
                                    Log.i("fusedlocation", "PendingIntent unable to execute request.");
                                }
                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:



                                Log.e(TAG, getString(R.string.error_message));
                                Toast.makeText(HomeActivity.this, getString(R.string.error_message), Toast.LENGTH_LONG).show();
                                mRequestingLocationUpdates = false;
                            default:

                        }
                    }
                });
    }

    /**
     * Removes location updates from the FusedLocationApi.
     */
    private void stopLocationUpdates() {
        LogUtils.debug("mRequestingLU","mRequestingLU inside stopLocationUpdates():"+mRequestingLocationUpdates);
        if (!mRequestingLocationUpdates) {
            LogUtils.debug(TAG, "stopLocationUpdates: updates never requested, no-op.");
            return;
        }

        // It is a good practice to remove location requests when the activity is in a paused or
        // stopped state. Doing so helps battery performance and is especially
        // recommended in applications that request frequent location updates.
        mFusedLocationClient.removeLocationUpdates(mLocationCallback)
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        //mRequestingLocationUpdates = false;
                        //setButtonsEnabledState();
                    }
                });
    }

    private void refreshAct(){ //this will close and reopen the tab bar and the home activity
        finish(); //this will close the current activities opened
        startActivity(getIntent());
        Intent intent = new Intent(HomeActivity.this,TabBarActivity.class);
        startActivity(intent);
    }

    @SuppressLint("MissingPermission")
//    private void startLocationUpdates() {
//        String[] providers = new String[]{
//                LocationManager.NETWORK_PROVIDER,
////                    LocationManager.PASSIVE_PROVIDER,
//                LocationManager.GPS_PROVIDER
//        };
//        for (String provider : providers) {
//
//                locationManager.requestLocationUpdates(provider, MIN_TIME, MIN_DISTANCE, this);
//
//        }
//    }

//    private void stoptLocationUpdates() {
//        locationManager.removeUpdates(this);
//    }

    protected void onDestroy(){
        super.onDestroy();
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkTimeOut();
    }

    @Override
    protected void onResume() {

        // gac.connect();
        super.onResume();
        Log.e("Home LC", "onResume");
        LogUtils.debug("OnboardingTag", "onResume called");
        if(returningFromConfirmationActivity) {
            returningFromConfirmationActivity = false;
            callForProfileInfo = false;
        } else if(callForProfileInfo) {
            getUserProfileInfo();
            resetQuestionnaireButton();
            callForProfileInfo = false;
        }

        Boolean userGrantedLocPermission = checkPermissions();
        if (/*mRequestingLocationUpdates &&*/ userGrantedLocPermission == true && "2".equals(Preferences.getString(Config.LOGIN_TYPE))) {
            startLocationUpdates();
        }
        checkTimeOut();
        preSelectSchoolSpinner();
        //context.registerReceiver(geoFenceReceiver, new IntentFilter("geofenceEvent"));

        if(Preferences.getBoolean(Config.USER_INFO_WAS_UPDATED)){
            //new GetUserInfo().execute();
            if(Preferences.getBoolean(Config.GEOFENCE_TOGGLE)){
                //new GetUserGeoInfo().execute();
            }
            Preferences.putBoolean(Config.USER_INFO_WAS_UPDATED, false);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e("Home LC", "onPause");

        //termsAndConditionsDialog.dismiss();

        if("2".equals(Preferences.getString(Config.LOGIN_TYPE)))
            stopLocationUpdates();

        stopTimer();
        //context.unregisterReceiver(geoFenceReceiver);
    }

    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        if("2".equals(Preferences.getString(Config.LOGIN_TYPE))) {
            savedInstanceState.putBoolean(KEY_REQUESTING_LOCATION_UPDATES, mRequestingLocationUpdates);
            savedInstanceState.putParcelable(KEY_LOCATION, mCurrentLocation);
            savedInstanceState.putString(KEY_LAST_UPDATED_TIME_STRING, mLastUpdateTime);
        }
    }

    private void showSnackbar(final int mainTextStringId, final int actionStringId,
                              View.OnClickListener listener) {
        Snackbar.make(
                findViewById(android.R.id.content),
                getString(mainTextStringId),
                Snackbar.LENGTH_INDEFINITE)
                .setAction(getString(actionStringId), listener).show();
    }

    /**
     * Return the current state of the permissions needed.
     */

    //Maybe change this method to use the code from the sample page
    private boolean checkPermissions() {
        boolean isAvailable = false;
        int locationMode =0;
        String locationProviders="";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            try {
                locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);
            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
            }
            isAvailable = (locationMode != Settings.Secure.LOCATION_MODE_OFF);
        } else {
            isAvailable = !TextUtils.isEmpty(locationProviders);
        }
        boolean finePermissionCheck = (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED);
//        LogUtils.debug("tiger","Location services turned on for the entire phone: "+ isAvailable);
//        LogUtils.debug("tiger","Location services turned on for just the app: "+ finePermissionCheck);

        return isAvailable && finePermissionCheck;


//        int permissionState = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
//        LogUtils.debug(" LOCA PERM SET?", String.valueOf(permissionState==PackageManager.PERMISSION_GRANTED));
//
//        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_FINE_LOCATION);

        // Provide an additional rationale to the user. This would happen if the user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale) {
            Log.i(TAG, "Displaying permission rationale to provide additional context.");
            showSnackbar(R.string.permission_rationale,
                    android.R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Request permission
                            ActivityCompat.requestPermissions(HomeActivity.this,
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                    REQUEST_PERMISSIONS_REQUEST_CODE);
                        }
                    }
            );
        } else {
            Log.i(TAG, "Requesting permission");
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the user denied the permission
            // previously and checked "Never ask again".
            ActivityCompat.requestPermissions(HomeActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }
    /**
     * Callback received when a permissions request has been completed.
     */
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
//                                           @NonNull int[] grantResults) {
//        Log.i(TAG, "onRequestPermissionResult");
//        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
//            if (grantResults.length <= 0) {
//                // If user interaction was interrupted, the permission request is cancelled and you
//                // receive empty arrays.
//                Log.i(TAG, "User interaction was cancelled.");
//            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                //if (mRequestingLocationUpdates) {
//                    Log.d("permission granted", "Permission granted, updates requested, starting location updates");
//                    mRequestingLocationUpdates = true;
//                    startLocationUpdates();
//                //}
//            } else {
//                // Permission denied.
//                mRequestingLocationUpdates = false;
//                // Notify the user via a SnackBar that they have rejected a core permission for the
//                // app, which makes the Activity useless. In a real app, core permissions would
//                // typically be best requested during a welcome-screen flow.
//
//                // Additionally, it is important to remember that a permission might have been
//                // rejected without asking the user for permission (device policy or "Never ask
//                // again" prompts). Therefore, a user interface affordance is typically implemented
//                // when permissions are denied. Otherwise, your app could appear unresponsive to
//                // touches or interactions which have required permissions.
//                showSnackbar(R.string.permission_denied_explanation,
//                        R.string.settings, new View.OnClickListener() {
//                            @Override
//                            public void onClick(View view) {
//                                // Build intent that displays the App settings screen.
//                                Intent intent = new Intent();
//                                intent.setAction(
//                                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
//                                Uri uri = Uri.fromParts("package","APPLICATION_ID", null);
//                                intent.setData(uri);
//                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                                startActivity(intent);
//                            }
//                        });
//            }
//        }
//    }


    @Override
    protected void onStop() {
        super.onStop();
        callForProfileInfo = true;
        //termsAndConditionsDialog.dismiss();

        //Cancel backend requests that are in queue
        //Consider, onStop method is called as soon as emulator initiates
        ApiHelper.getInstance(this).getRequestQueue().cancelAll(this);

        if("2".equals(Preferences.getString(Config.LOGIN_TYPE)))
            stopLocationUpdates();

        Log.e("Home LC", "onStop");
        stopTimer();
    }


    //BranCode

    @Override
    public void onLocationChanged(Location location) {
        LogUtils.debug(TAG," CHHHHAAANNGEES");
        if (location != null) {
            //updateUI(location);
        }
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {}

    @Override
    public void onProviderEnabled(String s) {}

    @Override
    public void onProviderDisabled(String s) {}

    private void registerUniqueId() {
        String uniqueId = Preferences.getString(Config.UNIQUE_ID);
        if (uniqueId.isEmpty()) {
            DeviceManager.init(context);
            uniqueId = DeviceManager.generateID();

           /* FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener( HomeActivity.this,  new OnSuccessListener<InstanceIdResult>() {
                @Override
                public void onSuccess(InstanceIdResult instanceIdResult) {
                    String newToken = instanceIdResult.getToken();
                    Preferences.putString(Config.DEVICE_TOKEN, newToken);
                    Log.e("newToken",newToken);

                }
            });*/
        }
        RegisterUniqueIdTask registerUniqueIdTask = new RegisterUniqueIdTask(uniqueId);
        registerUniqueIdTask.execute();
    }

    private void dialCaller() {
        new DialConfirmation(this).show();
    }

    private void set911Dialer() {
        LogUtils.debug("set911Dailer","setting the 911Dialer");
        fab = (FloatingActionButton) findViewById(R.id.fab);
        assert fab != null;
        fab.setBackgroundColor(ContextCompat.getColor(context, R.color.red));
        fab.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                Uri number = Uri.parse("tel:911");
                Intent callIntent = new Intent(Intent.ACTION_DIAL, number);
                if (isCallingSupported(context, callIntent)) {
                    //Send to dial pad
                    startActivity(callIntent);
                } else {
                    Snackbar.make(view, getString(R.string.home_snackbar_phone), Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
                if(!Preferences.getBoolean(Config.NO_LOC_TOGGLE))
                {
                    //track
                    new Track911Task().execute();
                }
                else LogUtils.debug(LOG_TAG, "no location tracked");
            }
        });
        fab.setVisibility(View.VISIBLE);
    }

    public int checkFlashState(){
        return FLASH_STATE;
    }

    public void flashOn() {
        Camera camera = Camera.open();
        Camera.Parameters parameters = camera.getParameters();
        parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
        camera.setParameters(parameters);
        camera.startPreview();

    }

    public void flashOff() {

        Camera camera2 = Camera.open();
        Camera.Parameters parameters2 = camera2.getParameters();
        parameters2.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
        camera2.setParameters(parameters2);
        camera2.stopPreview();
    }

    private void initView() {
        buildingSpinner = (Spinner) findViewById(R.id.school_spinner);
        messageSpinner = (Spinner) findViewById(R.id.message_spinner);
        //languageSpinner = (Spinner) findViewById(R.id.language);
        Button flashBtn = (Button) findViewById(R.id.my_identity_button);
        Button attachBtn = (Button) findViewById(R.id.attach_home_button);
        locatorButton = (ImageButton) findViewById(R.id.locate_me_button);
        assert flashBtn != null;

        flashBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                int flashMode = checkFlashState();
                switch (flashMode){
                    case 0:
                        flashOn();
                        FLASH_STATE = 1;
                        break;
                    case 1:
                        flashOff();
                        FLASH_STATE = 0;
                        break;
                }
            }
        });

        attachBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, UploadMedia.class);
                startActivity(intent);
            }
        });
    }

    private boolean enableOfflineMode() {
        //Check the network connection
        NetworkCheck networkCheck = new NetworkCheck(context);
        if (!networkCheck.isNetworkConnected()) {
            Toast.makeText(this, getString(R.string.Not_Connected_To_Internet), Toast.LENGTH_LONG).show();
            //Hide the spinners in offline mode
            if (buildingSpinner != null) {
                buildingSpinner.setVisibility(View.INVISIBLE);
            }
            assert messageSpinner != null;
            messageSpinner.setVisibility(View.INVISIBLE);
            return true;
        }
        return false;
    }

    private void setupLongPress() {
        //Long press animation
        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        int color = Color.rgb(255, 0, 0);
        assert progressBar != null;
        progressBar.getIndeterminateDrawable().setColorFilter(color, PorterDuff.Mode.SRC_IN);
        progressBar.getProgressDrawable().setColorFilter(color, PorterDuff.Mode.SRC_IN);
        final ObjectAnimator animation = ObjectAnimator.ofInt(progressBar, "progress", 0, 100);

        animation.setDuration(Preferences.getInteger(Config.SB_PRESS_LENGTH) * 1000);//3200);
        animation.setInterpolator(new DecelerateInterpolator());
        animation.addListener(new Animator.AnimatorListener() {

            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                //do something when the countdown is complete
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        //Long press
        final Runnable run = new Runnable() {

            @Override
            public void run() {
                //if (timerIsElapsed) {
//                    testCovidScreen(); //There is a check for location toggle in the method
//                validateAlert();
                validateUserAndUserType();
                animation.cancel();
                progressBar.setVisibility(View.INVISIBLE);

                /*} else {
                    showTimerAlert();
                }*/
            }
        };

        final Handler handler = new Handler();
        locatorButton.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View arg0, MotionEvent arg1) {

                switch (arg1.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        LogUtils.debug("onTouchDown","SBPressLength - " + Preferences.getInteger(Config.SB_PRESS_LENGTH));
                        //handler.postDelayed(run,Preferences.getInteger(Config.SB_PRESS_LENGTH) * 1000);//, 3000/* OR the amount of time you want */);
                        //testButton.startAnimation(animRotate);
                        if (timerIsElapsed) {
                            handler.postDelayed(run,Preferences.getInteger(Config.SB_PRESS_LENGTH) * 1000);
                            locatorButton.setBackgroundResource(R.drawable.sb_graphic_dark_red);
                            progressBar.setVisibility(View.VISIBLE);
                            //Progressbar
                            animation.start();
                        } else {
                            showTimerAlert();
                        }
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        if(timerIsElapsed){
                            locatorButton.setBackgroundResource(R.drawable.sb_graphic_red);
                        }
                        else{
                            locatorButton.setBackgroundResource(R.drawable.sb_graphic_gray);
                        }
                        handler.removeCallbacks(run);
                        break;
                    case MotionEvent.ACTION_UP:
                        if(timerIsElapsed){
                            locatorButton.setBackgroundResource(R.drawable.sb_graphic_red);
                        }
                        else{
                            locatorButton.setBackgroundResource(R.drawable.sb_graphic_gray);
                        }

                        handler.removeCallbacks(run);
                        //testButton.clearAnimation();
                        progressBar.setVisibility(View.INVISIBLE);
                        animation.cancel();
                        break;
                }
                return true;
            }
        });
    }

    public void showCovid19SelectionList(){
        LinearLayout titleView = new LinearLayout(this);
        titleView.setOrientation(LinearLayout.VERTICAL);

        TextView titleP1 = new TextView(this);
        titleP1.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        // You Can Customise your Title here
        titleP1.setText(R.string.question_title1);
        titleP1.setBackgroundColor(Color.DKGRAY);
        titleP1.setPadding(10, 10, 10, 5);
        titleP1.setGravity(Gravity.CENTER);
        titleP1.setTextColor(Color.WHITE);
        titleP1.setTextSize(20);

        TextView titleP2 = new TextView(this);
        titleP2.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        titleP2.setText(R.string.question_title2);
        titleP2.setBackgroundColor(Color.DKGRAY);
        titleP2.setPadding(10, 0, 10, 10);
        titleP2.setGravity(Gravity.CENTER);
        titleP2.setTextColor(this.getResources().getColor(R.color.blue_light));
        titleP2.setTypeface(null, Typeface.BOLD);
        titleP2.setTextSize(20);

        titleView.addView(titleP1);
        titleView.addView(titleP2);

        covidDialog.setCanceledOnTouchOutside(true);
        covidDialog.setCancelable(true);
        covidDialog.setCustomTitle(titleView);
        covidDialog.show();
    }

    private void createCovid19SelectionList(final ArrayList<IncidentTypeItem> locatorItems, ArrayList<String> messageList) {
        RelativeLayout dialogLayout = new RelativeLayout(this);
        dialogLayout.setId(RelativeLayout.generateViewId());
        LinearLayout contentLayout = new LinearLayout(this);

        LinearLayout layout1 = new LinearLayout(this);
        layout1.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        LinearLayout layout2 = new LinearLayout(this);
        layout2.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        final ArrayList<CheckBox> checkBoxes = new ArrayList<>();
        contentLayout.setOrientation(LinearLayout.VERTICAL);
        layout1.setOrientation(LinearLayout.VERTICAL);
        layout2.setOrientation(LinearLayout.VERTICAL);

        for(int i=0; i<messageList.size(); i++) {
            CheckBox cb = new CheckBox(this);
            cb.setTag(i);
            cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    Log.d("doneButton","checked " + isChecked +" "+ buttonView.getTag());

                    if(isChecked) { //setting item to checked and adding the item to the list
                        Log.d("done - checked","checked");
                        IncidentTypeItem selection = locatorItems.get((int)buttonView.getTag());
                        selectedCovid19SurveySelections.add(selection); //need to add the item to this array to track store the items the user selected
                        if(locatorItems.get((int)buttonView.getTag()).getSCVParam().equals("0") && "1".equals(locatorItems.get((int)buttonView.getTag()).getSpecialCaseValue()) ){ //this will clear the other selections if one of these options was selected
                            for (int i = 0; i<locatorItems.size();i++){//this is looping through the arrangement list and setting them all to false if it is not the currently selected item
                                if(i!=(int)buttonView.getTag()){
                                    checkBoxes.get(i).setChecked(false);
    //                            Log.d("done","removing "+checkBoxes.get(i).getText());
                                }
                            }

                            selectedCovid19SurveySelections.clear();//clearing all previously selected selections
                            selectedCovid19SurveySelections.add(selection);//adding the selected item back to the now empty list

                        } else {
                            for(int i=0; i<selectedCovid19SurveySelections.size();i++){//removing nosymptoms from the list of selected incidents
                                if(selectedCovid19SurveySelections.get(i).getSCVParam().equals("0")&& "1".equals(selectedCovid19SurveySelections.get(i).getSpecialCaseValue())){
                                    selectedCovid19SurveySelections.remove(i);
                                    Log.d("done","removingselected");
                                }
                            }
                            for(int i=0; i<checkBoxes.size();i++){
                                if (locatorItems.get(i).getSCVParam().equals("0")&& "1".equals(locatorItems.get((int)buttonView.getTag()).getSpecialCaseValue())){
                                    if (checkBoxes.get(i).isChecked()==true){
                                        checkBoxes.get(i).setChecked(false);
                                    }
                                }
                            }
                        }

                    } else {//if the user in unchecking the box then this will remove the selection
                        selectedCovid19SurveySelections.clear();
                        for (int i = 0; i<checkBoxes.size();i++){
                            if (checkBoxes.get(i).isChecked()){
                                selectedCovid19SurveySelections.add(locatorItems.get(i));
                            }
                        }
                    }

//                    Button covidSelectionButton = findViewById(R.id.covidSelectionButton);
//                    if(selectedCovid19SurveySelections.size() == 0) {
//                        covidSelectionButton.setText(R.string.question_button_text1);
//                    } else if(selectedCovid19SurveySelections.size() == 1) {
//                        covidSelectionButton.setText(selectedCovid19SurveySelections.get(0).getIncidentMessage());
//                    } else {
//                        covidSelectionButton.setText(R.string.question_button_text2);
//                    }
                }
            });

            cb.setText(messageList.get(i));
            cb.setTextSize(20);
            cb.setPadding(10,10,10,10);

            if(locatorItems.get(i).getSCVParam().equals("0")&& "1".equals(locatorItems.get(i).getSpecialCaseValue())) {
                //cb.setBackgroundColor(Color.rgb(71, 176, 63));
                cb.setTypeface(null,Typeface.BOLD);
                cb.setTextColor(Color.rgb(71, 176, 63));
            } else {
                cb.setBackgroundColor(Color.WHITE);
            }

            checkBoxes.add(cb);

            View borderView = new View(this);
            borderView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,1));
            borderView.setBackgroundColor(Color.BLACK);

            layout1.addView(cb);
            layout1.addView(borderView);
        }

        covidDialogbuilder = new AlertDialog.Builder(this);

        contentLayout.addView(layout1,new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        contentLayout.addView(layout2,new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        View separator1 = new View(this);
        separator1.setId(View.generateViewId());
        View separator2 = new View(this);
        separator2.setId(View.generateViewId());
        View separator3 = new View(this);
        separator3.setId(View.generateViewId());
        separator1.setBackgroundColor(Color.BLACK);
        separator2.setBackgroundColor(Color.BLACK);
        separator3.setBackgroundColor(Color.BLACK);

        ScrollView scroll = new ScrollView(this);
        scroll.addView(contentLayout,new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        RelativeLayout.LayoutParams layParamsSep1 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,5);
        layParamsSep1.addRule(RelativeLayout.ALIGN_PARENT_TOP,dialogLayout.getId());

        RelativeLayout.LayoutParams layParamsSep2 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, 5);
        layParamsSep2.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,dialogLayout.getId());

        RelativeLayout.LayoutParams layParamsScroll = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        layParamsScroll.addRule(RelativeLayout.BELOW,separator1.getId());
        layParamsScroll.addRule(RelativeLayout.ABOVE,separator2.getId());

        dialogLayout.addView(separator1,layParamsSep1);
        dialogLayout.addView(scroll,layParamsScroll);
        dialogLayout.addView(separator2,layParamsSep2);

        covidDialogbuilder.setPositiveButton(R.string.question_button_text3, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                concatSelections="";
                Button covidSelectionButton = findViewById(R.id.covidSelectionButton);
                if(selectedCovid19SurveySelections.size() > 0) {
                    for(int i=0; i<selectedCovid19SurveySelections.size();i++){
                        concatSelections += selectedCovid19SurveySelections.get(i).getId() + ",";
                    }

                    if (concatSelections.length() > 0) {
                        concatSelections = concatSelections.substring(0, concatSelections.length() - 1);
                    }
                    LogUtils.debug("Questionnaire","concat string --> "+concatSelections);

                    LogUtils.debug("Questionnaire","selectedOptions -->"+selectedCovid19SurveySelections.toString());
                    if(selectedCovid19SurveySelections.size() == 1) {
                        covidSelectionButton.setText(selectedCovid19SurveySelections.get(0).getIncidentMessage());
                    } else {
                        covidSelectionButton.setText(R.string.question_button_text2);
                    }

                } else {
                    covidSelectionButton.setText(R.string.question_button_text1);
                }

                Log.d("done Button" , concatSelections);
            }
        });

        covidDialogbuilder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                LogUtils.debug("Questionnaire","Cancelling dialog builder");
                resetQuestionnaireButton();
            }
        });

        covidDialogbuilder.setNeutralButton(R.string.clear_selections,null);
        covidDialog = covidDialogbuilder.create();
        covidDialog.setView(dialogLayout);

        covidDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                LogUtils.debug("Questionnaire","SHOWING COVID DIALOG!!!");
                Button b = ((AlertDialog)dialogInterface).getButton(AlertDialog.BUTTON_NEUTRAL);
                b.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        Log.d("getincident","clicked2");
                        for(int i =0; i<checkBoxes.size();i++){
                            checkBoxes.get(i).setChecked(false);
                        }
                        selectedCovid19SurveySelections.clear();
                    }
                });
            }
        });

        Button covidSelectionButton = findViewById(R.id.covidSelectionButton);
        covidSelectionButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showCovid19SelectionList();

                selectedCovid19SurveySelections.clear();

                for(int i =0; i<checkBoxes.size();i++){
                    checkBoxes.get(i).setChecked(false);
                }
            }
        });
    }

    private void covid19SurveyError() {
        AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
        builder.setTitle("ERROR");
        builder.setIcon(R.drawable.warning);
        builder.setMessage(getString(R.string.Failed_Covid_Survey));
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        Dialog dialog = builder.create();
        dialog.show();
    }

    private void covid19SurveyConnectionError() {
        AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
        builder.setTitle("ERROR");
        builder.setIcon(R.drawable.warning);
        builder.setMessage(getString(R.string.Failed_Survey_Network_Issue));
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        Dialog dialog = builder.create();
        dialog.show();
    }

    //Get the list of custom emergency contacts [Data is stored on local database]
    private String getContactsJson() {

        DatabaseHandler dbHandler = new DatabaseHandler(context);
        List<Contact> contacts = dbHandler.getAllContacts();
        JSONArray contactsArray = new JSONArray();
        try {
            for (Contact contact : contacts) {
                JSONObject contactJSON = new JSONObject();
                contactJSON.put("id", contact.getId());
                contactJSON.put("name", contact.getName());
                contactJSON.put("phone", getRawPhoneFromContact(contact));
                contactJSON.put("email", contact.getEmail());
                contactsArray.put(contactJSON);
            }
            //stringify the json data
        } catch (JSONException ex) {
            //damn no reason to this should happen
            ex.printStackTrace();
            //return "[]";
        }
        return contactsArray.toString();
    }

    private String getRawPhoneFromContact(Contact contact) {

        String phone = contact.getPhoneNumber().replaceAll("[^\\d]", "");
        if (phone.length() == 1 && "1".equals(String.valueOf(phone.charAt(0)))) {
            phone = phone.substring(1);
        }
        return phone;
    }

    private void requestDialPermission() {

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.CALL_PHONE},
                2);
    }

    private void stopTimer() {

        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    private void checkTimeOut() {

        long currentTime = System.currentTimeMillis();
        long nextAllowedTime = Preferences.getLong(Config.NEXT_TIME_ALLOWED);
        boolean isOnCD = nextAllowedTime >= currentTime;
        //This checks if the usertype is Law Enforcement. If yes, the button should remain on cool-down

        if(/*Preferences.getString(Config.LOGIN_TYPE).equals("1") ||*/ "3".equals(Preferences.getString(Config.LOGIN_TYPE))){
            //simply force the next check to be true to set the button on cooldown
            isOnCD = true;
            timerIsElapsed = false;
        }

//        //check if you have the geofence toggle AND are not in the active zone. If so, lock the smart button
//        if(Preferences.getBoolean(Config.GEOFENCE_TOGGLE)){
//
//            //If the user is in the active zone OR is a super user, enable the smart button
//            if(Preferences.getBoolean(Config.GEOFENCE_IN_ACTIVE_ZONE) || Preferences.getBoolean(Config.IS_SUPER_USER)){
//                //User is inside Active Zone, enable the smart button....if the timeout checks out as well
//                isOnCD = nextAllowedTime >= currentTime;
//                //timerIsElapsed = false;
//            }
//
//            else{
//                //simply force the next check to be true to set the button on cooldown
//                isOnCD = true;
//                timerIsElapsed = false;
//            }
//
//        }


        if (isOnCD) {
            locatorButton.setBackgroundResource(R.drawable.sb_graphic_gray);
            long remainingMillis = nextAllowedTime - currentTime;
            if (remainingMillis > 0) {
                startTimer(remainingMillis);
            }
        } else {
            locatorButton.setBackgroundResource(R.drawable.sb_graphic_red);
            timerIsElapsed = true;
        }
    }

    /**
     * send User Information
     */
//    private void sendUserInformation() {
//
//        if(!Preferences.getBoolean(Config.NO_LOC_TOGGLE)) {
//                if (Preferences.getBoolean(Config.DEC_ONLY)) {
//                    new SendReportTask().execute();
//                }
//                else {
//                    new LocatorTask().execute();
//                }
//        }
//        else{
//            new LocatorTaskNoLoc().execute();
//            Log.d(LOG_TAG, "no location sent");
//        }
//    }

    /**
     * shows Identity Alert
     * prompt if contact missing: Would you like to fill out more information about yourself
     */
    private void showIdentityAlert() {

        final TabBarActivity activity = (TabBarActivity) this.getParent();
        // Redirect the app to enable location services
        AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
        builder.setTitle(getString(R.string.Profile_Info_Missing)); //Identity missing
        builder.setMessage(getString(R.string.Make_Sure_Info_Completed));
        builder.setPositiveButton(R.string.home_dialogbutton_updateidentity, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialogInterface, int i) {

                locatorButton.setBackgroundResource(R.drawable.sb_graphic_red);
                // Show the location services to the user
                TabHost host = activity.getTabHost();
                host.setCurrentTab(1);
            }
        });
        Dialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    /***
     * shows Success Alert
     */
    private void showSuccessAlert() {
        // Redirect the app to enable location services
        AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
        Toast.makeText(HomeActivity.this, R.string.success_alert, Toast.LENGTH_LONG).show();
        builder.setTitle(R.string.success_alert_title);
        builder.setIcon(R.drawable.tick_green);
        builder.setMessage(R.string.success_alert);



        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialogInterface, int i) {
                // Show the location services to the user
                Intent intent = new Intent(HomeActivity.this, MainActivity.class);

                //startActivity(intent);

            }
        });
        Dialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    private void showGeocodingError() {
        AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
        builder.setTitle(getString(R.string.Failed_Locate));
        builder.setIcon(R.drawable.warning);
        builder.setMessage(getString(R.string.Failed_to_find_location));
        builder.setNegativeButton(R.string.generic_close, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        builder.setPositiveButton(R.string.action_settings, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int i) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", HomeActivity.this.getPackageName(), null);
                intent.setData(uri);
                context.startActivity(intent);
            }
        });
        Dialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    /**
     * Show the error alerts
     */
    private void showErrorAlert() {
        // Redirect the app to enable location services
        AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
        builder.setTitle(getString(R.string.Error_Title));
        builder.setIcon(R.drawable.warning);
        builder.setMessage(getString(R.string.Failed_to_Send_Alert));
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialogInterface, int i) {
                // Show the location services for the user
                Intent intent = new Intent(HomeActivity.this, MainActivity.class);
                //startActivity(intent);
            }
        });
        Dialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    /**
     * Shows the GPS Alerts
     */
    private void showGpsAlert() {
        // Redirect the app to enable location services
        AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
        builder.setTitle(getString(R.string.Location_Services_Not_Available));
        builder.setMessage(getString(R.string.Enable_Location_Services));
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialogInterface, int i) {
                // Show the location services to the user
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                //startActivity(intent);
            }
        });
        Dialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    /**
     * Shows timer is disabled alert
     */
    private void showTimerAlert() {

        builder = new AlertDialog.Builder(HomeActivity.this);

        /*if (Preferences.getString(Config.LOGIN_TYPE).equals("1")) {
            builder.setTitle("Smart Button is disabled!");
            builder.setMessage("The Smart Button is disabled for all Student users.");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.cancel();
                }
            });
            dialog = builder.create();
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        }
        else*/ if ("3".equals(Preferences.getString(Config.LOGIN_TYPE))) {
            //if user type is 3 (Law Enforcement), change the alert message

            builder.setTitle(R.string.smartbutton_disabled);
            builder.setMessage( R.string.smartbutton_disabled_law);


            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.cancel();
                }
            });
            dialog = builder.create();
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
            //return;
        } else {
            final AlertDialog customDialog;// = new AlertDialog();

            builder.setIcon(R.drawable.warning);

            builder.setTitle(R.string.disabled);
            builder.setMessage(R.string.disabled_message);


            //builder.setMessage("You must wait " + ((Long.parseLong(countDownTimer)/1000)) + " builder just instantiated");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.cancel();
                }
            });
            //builder.create();
            customDialog = builder.create();
//            dialog.setCanceledOnTouchOutside(false);
//            dialog.show();

            customDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialog) {
                    final Button defaultButton = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                    final CharSequence positiveButtonText = defaultButton.getText();
                    new CountDownTimer(Long.parseLong(countDownTimer), 1000) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                            defaultButton.setText(String.format(Locale.getDefault(), "%s (%d)", positiveButtonText, TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished)+1));
                            //dialog.setMessage("You must wait " + ((millisUntilFinished/1000)+1) + " builder already instantiated"); //add one so it never displays zero
                        }
                        @Override
                        public void onFinish() {
                            if (((AlertDialog) customDialog).isShowing()) {
                                customDialog.dismiss();
                            }
                        }
                    }.start();
                }
            });

            customDialog.show();
        }
    }

    /**
     * Starts the timer
     *
     * @param milliseconds the time is from the settings
     */
    public void startTimer(long milliseconds) {

        timer = new CountDownTimer(milliseconds, 1000) {

            public void onTick(long millisUntilFinished) {
                timerIsElapsed = false;
                //long currentTime = System.currentTimeMillis();
                //user.setNextAllowedTime(currentTime + millisUntilFinished);
                long secondsUntilFinished = millisUntilFinished / 1000;
                long minutes = secondsUntilFinished / 60;
                long seconds = secondsUntilFinished % 60;
                time = String.format(Locale.US, "%02d:%02d", minutes, seconds);
                countDownTimer = String.valueOf(millisUntilFinished);
                if (builder != null && dialog != null) {
                    if (dialog.isShowing())
                        builder.setMessage(getString(R.string.Remaining_Time) + " " + time);
                }
//                Toast.makeText(HomeActivity.this, "TIMER = " + time, Toast.LENGTH_SHORT).show();
            }

            public void onFinish() {
                timerIsElapsed = true;
                locatorButton.setBackgroundResource(R.drawable.sb_graphic_red);
                Preferences.putLong(Config.NEXT_TIME_ALLOWED, (long) 0);
            }
        }.start();
    }

    /**
     * set Touch Listener For Keyboard Dismissal
     */
    public void setTouchListenerForKeyboardDismissal() {

        RelativeLayout layout = (RelativeLayout) findViewById(R.id.LocateMeLayout);
        assert layout != null;
        layout.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View view, MotionEvent motion) {

                hideKeyboard();
                return false;
            }
        });
    }

    protected void hideKeyboard() {
        // Check if no view has focus:
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private boolean checkGeofenceStatus(Location lastRecordedLocation) {
        Double currLat = lastRecordedLocation.getLatitude(), currLong = lastRecordedLocation.getLongitude();

        if(lastRecordedLocation == null || (currLat == 0 && currLong == 0))
            return false;

        LogUtils.debug("geofenceLong",Double.toString(geofenceLong));
        LogUtils.debug("geofenceLat",Double.toString(geofenceLat));
        LogUtils.debug("geofenceRadius", Double.toString(geofenceRad));

        Location centerOfGeofence = new Location("");
        centerOfGeofence.setLatitude(geofenceLat);
        centerOfGeofence.setLongitude(geofenceLong);

        Location usersLocation = new Location("");
        usersLocation.setLatitude(currLat);
        usersLocation.setLongitude(currLong);

        float distanceFromGeofenceCenter = centerOfGeofence.distanceTo(usersLocation);
        LogUtils.debug("distanceFromGeoCenter", Float.toString(distanceFromGeofenceCenter));

        if(distanceFromGeofenceCenter > geofenceRad) {
            //isInsideGeofence = false;
            return false;
        } else {
            //isInsideGeofence = true;
            return true;
        }
    }

//    private boolean validateUserLocation(Location lastRecordedLocation, String lastRecordedTime ) {
//        Log.d("qjesmd","mCurrentLocation inside validateUserLocation() is: " + mCurrentLocation);
//        if(lastRecordedLocation != null) {
//            Date currentTime = new Date(System.currentTimeMillis());
//            Date locationLastUpdated = new Date(Long.parseLong(lastRecordedTime));
//            long timeElapsed = (currentTime.getTime() - locationLastUpdated.getTime());
//            Log.d("ajwdwhdn","time elapsed since location was last updated: " + timeElapsed);
//            if(timeElapsed <= 20000) {
//                mLatitude = lastRecordedLocation.getLatitude();
//                mLongitude = lastRecordedLocation.getLongitude();
//                return true;
//            }
//        }
//
//        return false;
//    }

    private Boolean isItemSelectedGreater(){
        boolean item = false;

        if(messageSpinner.getSelectedItemPosition() > 0){
            item = true;
        }

        return item;
    }

    private void validateUserAndUserType() {
        if(showQuestionnaire == null) {
            showProfileInfoError();
        } else {
            if (errorLoadingProfileInfo == true) {
                showSBReportError();
            } else if (!Preferences.isAvailable(Config.USER_BUILDING_ID) || (!Preferences.isAvailable(Config.USER_FULL_NAME) || Preferences.getString(Config.USER_FULL_NAME).trim().isEmpty())) {
                showIdentityAlert();
            } else {
                if (showQuestionnaire)
                    manageQuestionnaire();
                else {
                    if(messageSpinner.getSelectedItem() == null)
                        showSBReportError();
                    else
                        validateAlert();
                }
            }
        }
    }

    private void manageQuestionnaire() {
        if(selectedCovid19SurveySelections.size() > 0 && concatSelections.length() > 0) {
            startCovid19QuestionnaireProcedure();
        } else {
            showIncidentNotSelectedError(true);
        }
    }

    /**
     * Validates and call the class
     */
    private void validateAlert() {

        //If the key 'hasScreening' was not set due to connection issues or data corruption issues,
        //assume that the user has access to the COVID-19 Screening, and thus, must select an incident before proceeding.
        Boolean hasScreening = Preferences.isAvailable(Config.COVIDSCREENING_ACCOUNT) ? Preferences.getBoolean(Config.COVIDSCREENING_ACCOUNT) : true;

        //If the "isIncidentMandatory" key is not NIL, then consider if the user MUST select an incident before sending a report.
        //Otherwise, the incident type is irrelevant because the user should be able to send an SB Alert without having to select an incident type.
        Boolean isIncidentMandatory = Preferences.isAvailable(Config.INCIDENT_REQUIRED) ? Preferences.getBoolean(Config.INCIDENT_REQUIRED) : true;

        Boolean incidentWasSelected = isItemSelectedGreater();//!((String) messageSpinner.getSelectedItem().toString()).equals(selectIncidentLangChange());

        Boolean isStudent = Preferences.isAvailable(Config.LOGIN_TYPE) ? "1".equals(Preferences.getString(Config.LOGIN_TYPE)) : false;

        if(hasScreening) {
            if (!incidentWasSelected)
                showIncidentNotSelectedError(false);
            else
                verifyIncidentType();
        } else {
            if (isStudent) {
                showIncidentTypeError();
            } else {
                if (isIncidentMandatory && !incidentWasSelected) {
                    showIncidentNotSelectedError(false);
                } else {
                    startSmartButtonAlertProcedure();
                }
            }
        }
    }

    private void verifyIncidentType() {
        Boolean isStudent = Preferences.isAvailable(Config.LOGIN_TYPE) ? "1".equals(Preferences.getString(Config.LOGIN_TYPE)) : false;

        if(isStudent) {
            if(covid19RelatedIncidentType) {
                startCovid19SurveyProcedure();
            }
            else {
                showIncidentTypeError();
            }
        } else {
            if(covid19RelatedIncidentType) {
                startCovid19SurveyProcedure();
            } else
                startSmartButtonAlertProcedure();
        }
    }

    private void startCovid19QuestionnaireProcedure(){
        final HashMap<String, String> params = new HashMap<>();
        params.put("controller", "WhiteZebra");
        params.put("action", "SendSBSurveyMulti");
        params.put("accountId", Preferences.getString(Config.ACCOUNT_ID));
        params.put("unique_Id", Preferences.getString(Config.CONTACT_ID));
        params.put("message_id", concatSelections);

        final ApiHelper apiHelper = new ApiHelper(true);

        final CustomProgressDialog progressDialog = new CustomProgressDialog(this);

        progressDialog.showDialog(getString(R.string.processing_covid_survey));

        apiHelper.setOnSuccessListener(new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();

                try {
                    displayQuestionnaireConfirmationPage();
                    LogUtils.debug("Questionnaire:","params sent to response--> "+params.toString());
                    LogUtils.debug("Questionnaire:","response from new multichoice procedure --> "+response);
                } catch (Exception e) {
                    e.printStackTrace();
                    LogUtils.debug("Questionnaire:","an exception was thrown while processing the responsedata");
                    covid19SurveyError();
                }
            }
        });

        apiHelper.setOnErrorListener(new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();

                if(error instanceof NoConnectionError || error instanceof NetworkError)
                    covid19SurveyConnectionError();
                else
                    displayQuestionnaireConfirmationPage();
            }
        });

        apiHelper.prepareRequest(params,true);
        ApiHelper.getInstance(this).startRequest(apiHelper);
    }

    private void startCovid19SurveyProcedure(){

        final HashMap<String, String> params = new HashMap<>();
        params.put("controller", "WhiteZebra");
        params.put("action", "SendSBSurvey3");
        params.put("accountId", Preferences.getString(Config.ACCOUNT_ID));
        params.put("unique_Id", Preferences.getString(Config.CONTACT_ID));
        params.put("message_id", messageId);

        final ApiHelper apiHelper = new ApiHelper(true);

        final CustomProgressDialog progressDialog = new CustomProgressDialog(this);

        progressDialog.showDialog(getString(R.string.processing_covid_survey));


        //final RetryCounter retryCounter = new RetryCounter();

        apiHelper.setOnSuccessListener(new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();

                try {
                    displayConfirmationPage();
                } catch (Exception e) {
                    e.printStackTrace();
                    LogUtils.debug("covid19surveytask","an exception was thrown while processing the responsedata");
                    covid19SurveyError();
                }
            }
        });

        apiHelper.setOnErrorListener(new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                LogUtils.debug("covid19surveytask","Received an error code from the server -> "+error);
                progressDialog.dismiss();

                if(error instanceof NoConnectionError || error instanceof NetworkError)
                    covid19SurveyConnectionError();
                else
                    displayConfirmationPage();
            }
        });

        apiHelper.prepareRequest(params, true);
        ApiHelper.getInstance(this).startRequest(apiHelper);
    }

    private void displayQuestionnaireConfirmationPage() {
        long currentTime = System.currentTimeMillis();
        Preferences.putLong(Config.NEXT_TIME_ALLOWED, TIMER_SETTING + currentTime);
        startTimer(TIMER_SETTING);

        Intent confirmationPageIntent;

        LogUtils.debug("scvParam","in display is: "+incidentTypeSCVParam);

        String firstChoice = selectedCovid19SurveySelections.get(0).getId();
        LogUtils.debug("Questionnaire","firstChoice in list of selected options --> "+firstChoice);
        String covid19ScreenChoose = String.valueOf("585".equals(firstChoice));
        confirmationPageIntent = new Intent(HomeActivity.this, Covid19SurveyActivity.class);
        confirmationPageIntent.putExtra("confirmationPageType", "1");
        confirmationPageIntent.putExtra("clearScreenVar", covid19ScreenChoose);

        resetQuestionnaireButton();
        returningFromConfirmationActivity = true;

        startActivity(confirmationPageIntent);
    }

    private void displayConfirmationPage() {
        long currentTime = System.currentTimeMillis();
        Preferences.putLong(Config.NEXT_TIME_ALLOWED, TIMER_SETTING + currentTime);
        startTimer(TIMER_SETTING);

        Intent confirmationPageIntent;

        LogUtils.debug("scvParam","in display is: "+incidentTypeSCVParam);
        if("5".equals(incidentTypeSCVParam)) {
            confirmationPageIntent = new Intent(HomeActivity.this, Covid19SurveyActivity.class);
            confirmationPageIntent.putExtra("confirmationPageType", "0");
        } else {
            String thumbsUpOrThumbsDown = String.valueOf("0".equals(incidentTypeSCVParam));
            confirmationPageIntent = new Intent(HomeActivity.this, Covid19SurveyActivity.class);
            confirmationPageIntent.putExtra("confirmationPageType", "1");
            confirmationPageIntent.putExtra("clearScreenVar", thumbsUpOrThumbsDown);
        }

        resetIncidentButton();
        returningFromConfirmationActivity = true;

        startActivity(confirmationPageIntent);
    }

    private void resetQuestionnaireButton() {
        Button covidSelectionButton = findViewById(R.id.covidSelectionButton);
        selectedCovid19SurveySelections.clear();
        concatSelections = "";
        covidSelectionButton.setText(R.string.question_button_text1);
    }

    private void resetIncidentButton() {
        messageSpinner.setSelection(0);
        messageId = "0";
        incidentTypeSCVParam = "-1";
        covid19RelatedIncidentType = false;
    }

    private void startSmartButtonAlertProcedure(){
        Boolean userGrantedLocPermission = checkPermissions();
        LogUtils.debug("tiger", "Is locations on: " + userGrantedLocPermission);
        if (userGrantedLocPermission) {
            if(sbEmergencyAlertProgressDialog == null) {
                sbEmergencyAlertProgressDialog = new CustomProgressDialog(this);
            }

            sbEmergencyAlertProgressDialog.showDialog(getString(R.string.processing_smartbutton_alert));

            Location lastRecordedLocation = mCurrentLocation;
            if(!locationErrorEncountered && lastRecordedLocation != null) {
                LogUtils.debug("validateAlert","GEOFENCE TOGGLE is " + Preferences.getBoolean(Config.GEOFENCE_TOGGLE));
                if(Preferences.getBoolean(Config.GEOFENCE_TOGGLE)) {
                    boolean isInsideGeofence = checkGeofenceStatus(lastRecordedLocation);
                    LogUtils.debug("validateAlert;","isInsideGeofence - true or false? "+isInsideGeofence);
                    if(!isInsideGeofence) {
                        sbEmergencyAlertProgressDialog.dismiss();
                        showGeofenceError();
                        return;
                    }
                }

                new GeocodingTask(lastRecordedLocation).execute();
            } else {
                LogUtils.debug("validatealert",getString(R.string.location_tracking_error));
                sbEmergencyAlertProgressDialog.dismiss();
                showTrackingError();
            }
        } else {
            /**
             * send user information with no location
             */
            showLocationSettingsPrompt();
        }
    }

    private void showIncidentNotSelectedError(Boolean isQuestionnaireEnabled) {
        AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);

        if(isQuestionnaireEnabled) {
            builder.setTitle(R.string.question_error_title1);
            builder.setMessage(R.string.question_error_mess1);
        } else {
            builder.setTitle(R.string.incident_not_selected);
            builder.setMessage(R.string.incident_not_selected_message);
        }

        builder.setIcon(R.drawable.warning);

        builder.setCancelable(true);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {}
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showIncidentTypeError() {
        AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);

        builder.setTitle(R.string.important);
        builder.setMessage(R.string.smart_button_disabled);


        builder.setIcon(R.drawable.warning);

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        Dialog dialog = builder.create();
        //       dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    private void showSBAlertError() {
        AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);

        builder.setTitle("ERROR");
        builder.setMessage(R.string.unstable_internet_error);



        builder.setIcon(R.drawable.warning);
        builder.setCancelable(true);
        builder.setPositiveButton(R.string.understand, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {}
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showProfileInfoError() {
        AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
        builder.setTitle("ERROR");
        builder.setMessage(R.string.error_loadingprofile);
        builder.setIcon(R.drawable.warning);

        builder.setCancelable(true);
        builder.setPositiveButton(R.string.understand, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {}
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showSBReportError() {
        AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
        builder.setTitle("ERROR");
        builder.setMessage(R.string.failed_connection);
        builder.setIcon(R.drawable.warning);

        builder.setCancelable(true);
        builder.setPositiveButton(R.string.understand, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {}
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void setSBProfileInfoError(boolean errorEncountered) {
        errorLoadingProfileInfo = errorEncountered;
//        if (profileErrorDialog !=null) {
//            //if(profileErrorDialog.isShowing())
//                profileErrorDialog.dismiss();
//                profileErrorDialog = null;
//            //else {
//                AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
//                builder.setTitle("ERROR");
//                builder.setIcon(R.drawable.warning);
//                builder.setMessage("The Smart Button app was unable to load all of your profile information at this time. Note: Signing out of your account and signing back in may fix this error.");
//                builder.setCancelable(true);
//                builder.setPositiveButton("I Understand", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {}
//                });
//                profileErrorDialog = builder.create();
//                profileErrorDialog.show();
//            //}
//
//        } else {
//            AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
//            builder.setTitle("ERROR");
//            builder.setIcon(R.drawable.warning);
//            builder.setMessage("The Smart Button app was unable to load all of your profile information at this time. Note: Signing out of your account and signing back in may fix this error.");
//            builder.setCancelable(true);
//            builder.setPositiveButton("I Understand", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {}
//            });
//            profileErrorDialog = builder.create();
//            profileErrorDialog.show();
//        }
    }

    private void showTrackingError() {
        AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
        builder.setTitle(R.string.generic_caution);
        builder.setMessage(R.string.locationtracker_indeterminant);

        builder.setIcon(R.drawable.warning);

        builder.setCancelable(true);

        builder.setNegativeButton(R.string.generic_no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {}
        });

        builder.setPositiveButton(R.string.yes_dialog, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                sendPanicAlert(false);
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showLocationSettingsPrompt() {
        AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
        builder.setTitle(R.string.location_services_disabled);
        builder.setMessage(R.string.location_services_disabled_message);

        builder.setIcon(R.drawable.warning);

        builder.setCancelable(true);

        builder.setNegativeButton(R.string.settings_dialog, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", HomeActivity.this.getPackageName(), null);
                intent.setData(uri);
                context.startActivity(intent);
            }
        });

        builder.setPositiveButton(R.string.yes_dialog, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                sendPanicAlert(false);
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showGeofenceError() {
        // Redirect the app to enable location services
        AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);

        builder.setTitle(R.string.geo_alert_title);
        builder.setMessage(R.string.geo_alert_message);

        builder.setIcon(R.drawable.warning);

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        Dialog dialog = builder.create();
        //       dialog.setCanceledOnTouchOutside(false);
        dialog.show();
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

    /**
     * on Options Item Selected
     *
     * @param item is the menu item
     * @return true or false
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; go home
                Intent intent = new Intent(this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;
            /*case R.id.action_change_language:
                Log.d("Zebra","Before showChangeLangDialog is Called");
                //MainActivity.showChangeLangDialog();
                return true;*/

            case R.id.action_logout:
                LogUtils.debug("SigningOut","CURRENTLY LOGGING OUT THROUGH THE LOGOUT METHOD IN HOMEACTIVITY");
                if("2".equals(Preferences.getString(Config.LOGIN_TYPE)))
                    stopLocationUpdates();

                wipeStoredDataLists();
                Preferences.clearProfilePicture();
                Preferences.putBoolean(Config.IS_LOGGED_IN, false);

                intent = new Intent(this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

//    /**
//     * on Create Options Menu
//     *
//     * @param menu object
//     * @return true or false
//     */
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds options to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }

    public void showChangeLangDialog() { //Brings up dialog with the options to change languages
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
                        Preferences.putString("langCode", "en"); //adding the language choice to the preferences so the app sets the prefered language on login
//                        setLangRecreate("en");
                        changeLang("en");
                        return;
                    case 1: //Spanish
                        PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("LANG", "es").commit();
                        Preferences.putString("langCode", "es"); //adding the language choice to the preferences so the app sets the prefered language on login
                        changeLang("es");

//                        setLangRecreate("es");
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
                        Log.d("language",spinner1.getSelectedItemPosition()+"");
                        return;
                }
            }
        });
        dialogBuilder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                if ("0".equals(Preferences.getString(Config.CONTACT_POLICY).trim())) {
                    LogUtils.debug("OnboardingTag","showing terms and conditions");
                    getTermsAndConditions();
                } else if(Preferences.getString(Config.PASSWORD).equals(Config.DEFAULT_PASSWORD)) {
                    LogUtils.debug("OnboardingTag","showing email/password reset screens");
                    Preferences.putBoolean(Config.PASSWORD_RESET_FLAG, false);
                    Intent emailConfirmIntent = new Intent(context, EmailConfirmActivity.class);
                    startActivity(emailConfirmIntent);
                } else {
                    getUserProfileInfo();
                }
            }
        });
        AlertDialog b = dialogBuilder.create();
        b.setCancelable(false);
        b.setCanceledOnTouchOutside(false);
        b.show();
    }

    public void changeLang(String langval){ //this will change the users language of choice throughout the app
        Preferences.putString(Config.LANGUAGE,langval);
        wipeStoredDataLists(); //wiping cached/stored data that is affected by language change
//        Config.LANGUAGE=langval;
        Log.d("languagecChange",langval);

        Resources resources = getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        Configuration config = getBaseContext().getResources().getConfiguration();
        resources.updateConfiguration(config,dm);
        Locale locale = new Locale(langval);
        Locale.setDefault(locale);
        Log.d("languagecChange", String.valueOf(locale));
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
        refreshAct(); //have to use this instead of recreate. recreate doesnt work due to structure of the tab bar and the activities contained.
        //recreate();
    }

    private void showCaseDemoHome() {
        ShowcaseConfig config = new ShowcaseConfig();
        config.setDelay(500); // half second between each showcase view
        config.setShapePadding((int) getResources().getDimension(R.dimen.demo_shape_padding));
        config.setRenderOverNavigationBar(true);
        config.setDismissTextColor(Color.RED);
        MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(this, SHOWCASE_ID);
//        MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(this);
        sequence.setOnItemDismissedListener(this);
        sequence.setOnItemShownListener(this);
        sequence.setConfig(config);
        //1 smart button
        sequence.addSequenceItem(locatorButton,
                getResources().getString(R.string.showcase_smart_button), getResources().getString(R.string.got_it));
        demoCounter++;
        //Building Spinner
        Shape shape = new RectangleShape((int) getResources().getDimension(R.dimen.demo_rectangle_width), (int) getResources().getDimension(R.dimen.demo_rectangle_height));
        config.setShape(shape);
        sequence.addSequenceItem(buildingSpinner,
                getResources().getString(R.string.select_school), getResources().getString(R.string.got_it));
        demoCounter++;
        //message Spinner
        sequence.addSequenceItem(messageSpinner,
                getResources().getString(R.string.select_message), getResources().getString(R.string.got_it));
        demoCounter++;
        //911 button
        Shape cShape = new CircleShape((int) getResources().getDimension(R.dimen.demo_circle_size));
        config.setShape(cShape);
        sequence.addSequenceItem(fab,
                getResources().getString(R.string.call_911), getResources().getString(R.string.got_it));
        demoCounter++;
        sequence.start();
    }

    @Override
    public void onDismiss(MaterialShowcaseView materialShowcaseView, int i) {

        if (demoCounter == 1) {
            final TabBarActivity activity = (TabBarActivity) this.getParent();
            TabHost host = activity.getTabHost();
            host.setCurrentTab(1);
        } else {
            demoCounter--;
        }
    }

    @Override
    public void onShow(MaterialShowcaseView materialShowcaseView, int i) {
//        final TabBarActivity activity = (TabBarActivity) this.getParent();
//        TabHost host = activity.getTabHost();
//        host.getTabWidget().setVisibility(View.GONE);
    }

    public class Track911Task extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... url) {

            HashMap<String, String> params = new HashMap<>();
//            params.put("controller", "User");
            params.put("controller", "RedBear");
            params.put("action", "Track911Dial");
            params.put("contactId", Preferences.getString(Config.CONTACT_ID));
            params.put("accountId", Preferences.getString(Config.ACCOUNT_ID));
            params.put("longitude", Preferences.getString(Config.LONGITUDE));
            params.put("latitude", Preferences.getString(Config.LATITUDE));
            params.put("address", Preferences.getString(Config.ADDRESS));
            //Log.d(LOG_TAG,"Tracking ...");
            return FunctionHelper.apiCaller(context, params);
        }

        @Override
        protected void onPostExecute(final String responseData) {

            if (responseData != null) {
                try {
                    JSONObject responseJsonObject = new JSONObject(responseData);
                    boolean success = responseJsonObject.getBoolean(Config.SUCCESS);
                    if (success) {
                        //Track successful
                    } else {
                        LogUtils.debug(LOG_TAG, "Track not successful");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                LogUtils.debug(LOG_TAG, "No JSON received ! :(");
            }
        }
    }

    private void sendPanicAlert(final boolean trackingLocation) {
        final CustomProgressDialog progressDialog;
        if(trackingLocation) {
            progressDialog = null;
        } else {
            progressDialog = new CustomProgressDialog(this);
            progressDialog.showDialog(getString(R.string.sending_smartbutton_alert));
        }

        String accountId = Preferences.getString(Config.ACCOUNT_ID);
        String uniqueId = Preferences.getString(Config.UNIQUE_ID);
        String name = Preferences.getString(Config.USER_FULL_NAME);

        String title = Preferences.getString(Config.USER_TITLE_ID);

        String building = Preferences.getString(Config.USER_BUILDING_ID);
        String building_ids = building + ",";

        String message = messageId;
        String imageName = new File(Preferences.getString(Config.USER_PROFILE_PICTURE)).getName();
        String room = Preferences.getString(Config.USER_ROOM);
        String floor = Preferences.getString(Config.USER_FLOOR);
        String description = Preferences.getString(Config.USER_DESCRIPTION);

        Boolean isSelectedGreater = isItemSelectedGreater();
        String messageString = isSelectedGreater ? messageSpinner.getSelectedItem().toString() : "";
        String messageBuilt = "Emergency- " + Preferences.getString(Config.USER_BUILDING_NAME) + "\n" + messageString;

        final HashMap<String, String> params = new HashMap<>();

        String userAddress = "";
        String userLat = "";
        String userLong = "";
        if(trackingLocation) {
            userAddress = mStreetAddress;
            userLat = Preferences.getString(Config.LATITUDE);
            userLong = Preferences.getString(Config.LONGITUDE);
        }

        /**BUILD THE API CALL*/
        params.put("controller", "WhiteZebra");
        params.put("action", "SendSmartButtonMaster");

        params.put("accountId", accountId);
        params.put("ip_address", "");
        params.put("image", imageName);
        params.put("message", messageBuilt);
        params.put("message_id", message);
        params.put("building_id", building);
        params.put("uniqueId", uniqueId);
        params.put("fullAddress", userAddress);
        params.put("login_type", Preferences.getString(Config.LOGIN_TYPE));
        params.put("room", room);
        params.put("floor", floor);
        params.put("roomDesc", description);
        params.put("alertType", "0");
        params.put("building_ids", building_ids);
        params.put("broadcast_id", "0");
        params.put("full_name", name);
        params.put("longitude", userLong);
        params.put("latitude", userLat);
        params.put("title", title);
        params.put("submission_type", "2");

        //TODO:Change key for "Contacts" --> "Custom_Contacts"
        //Confirm if this value is being used to send report to custom contacts
        //params.put("contacts", cellPhones);

        LogUtils.debug("MasterPanicAlertProc","sendPanicAlert() - request body: "+params.toString());

        final ApiHelper apiHelper = new ApiHelper(true);

        apiHelper.setOnSuccessListener(
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        LogUtils.debug("MasterPanicAlertProc","sendPanicAlert() - response: "+response);
                        if(trackingLocation)
                            sbEmergencyAlertProgressDialog.dismiss();
                        else
                            progressDialog.dismiss();

                        locatorButton.setBackgroundResource(R.drawable.sb_graphic_gray);
                        long currentTime = System.currentTimeMillis();
                        Preferences.putLong(Config.NEXT_TIME_ALLOWED, TIMER_SETTING + currentTime);
                        startTimer(TIMER_SETTING);
                        showSuccessAlert();
                        messageSpinner.setSelection(0);
                        LogUtils.debug("MasterPanicAlertProv","responsedata has a data key and success is true");
                    }
                }
        );

        apiHelper.setOnErrorListener(
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if(trackingLocation)
                            sbEmergencyAlertProgressDialog.dismiss();
                        else
                            progressDialog.dismiss();

                        if(error instanceof NoConnectionError || error instanceof NetworkError) {
                            showSBAlertError();
                        }
                        else {
                            locatorButton.setBackgroundResource(R.drawable.sb_graphic_gray);
                            long currentTime = System.currentTimeMillis();
                            Preferences.putLong(Config.NEXT_TIME_ALLOWED, TIMER_SETTING + currentTime);
                            startTimer(TIMER_SETTING);
                            showSuccessAlert();
                            messageSpinner.setSelection(0);
                        }
                    }
                }
        );

        apiHelper.prepareRequest(params,true);
        ApiHelper.getInstance(this).startRequest(apiHelper);
    }

    private class RegisterUniqueIdTask extends AsyncTask<String, Void, String> {

        private String uniqueId;

        RegisterUniqueIdTask(String uniqueId) {

            this.uniqueId = uniqueId;
        }

        @Override
        protected String doInBackground(String... url) {

            HashMap<String, String> params = new HashMap<>();
            params.put("controller", "User");
            params.put("controller", "RedBear");
            params.put("action", "SaveUniqueIdSbV2");
            params.put("accountId", Preferences.getString(Config.ACCOUNT_ID));
            params.put("contact_id", Preferences.getString(Config.CONTACT_ID));
            params.put("unique_id", this.uniqueId);//unique id
            params.put("os", getOS());//Get the operating system of the connected device
            params.put("deviceId", Preferences.getString(Config.DEVICE_TOKEN));
            Log.d("REGISTER","deviceId is --> "+Preferences.getString(Config.DEVICE_TOKEN));
            params.put("deviceType", getDeviceType());//Get the connected device type (cellphone or tablet)
            params.put("app_type", RegistrationIntentService.SMART_BUTTON_APP_TYPE);
            params.put("push_device_type", RegistrationIntentService.ANDROID_DEVICE_TYPE_VALUE);
            params.put("production", BuildConfig.DEBUG ? "0" : "1");
            return FunctionHelper.apiCaller(context, params);
        }

        @Override
        protected void onPostExecute(final String responseData) {

            if (responseData != null) {
                try {
                    JSONObject json = new JSONObject(responseData);
                    if (json.getBoolean(Config.SUCCESS)) {
                        Preferences.putString(Config.UNIQUE_ID, this.uniqueId);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        protected void onCancelled() {

        }

        private String getOS() {

            Context context = getApplicationContext();
            OperatingSystem oSGetter = new OperatingSystem(context);
            return oSGetter.getAndroidVersion();
        }

        private String getDeviceType() {

            Context context = getApplicationContext();
            //Get the operating system of the connected device
            OperatingSystem oSGetter = new OperatingSystem(context);
            //Get the connected device type (cellphone or tablet)
            return (oSGetter.isTablet()) ? "Tablet" : "Cellphone";
        }
    }

    private void getIncidentTypeList() {
        if(Preferences.isAvailable(Config.INCIDENTTYPE_LIST_LOADDATE)) {
            try {
                boolean haveXDaysElapsed = Config.calcDaysSinceListLoad(Preferences.getString(Config.INCIDENTTYPE_LIST_LOADDATE),3);
                if (haveXDaysElapsed) {
                    LogUtils.debug("getincidenttypelist","getincidenttypelist --> calling for incidenttypelist");
                    callForIncidentTypeList();
                }
                else {
                    LogUtils.debug("getincidenttypelist", "getincidenttypelist --> loading stored incident type list");
                    loadStoredIncidentTypeList();
//                    callForIncidentTypeList();

                }
            } catch(Exception e) {
                LogUtils.debug("getincidenttypelist", "getincidenttypelist --> a parse exception was thrown while determining if the load date for the incidenttypelist was X days ago -> "+e);
                callForIncidentTypeList();
            }
        } else {
            LogUtils.debug("getincidenttypelist","getincidenttypelist --> calling for incidenttypelist");
            callForIncidentTypeList();
        }
    }

    private void callForIncidentTypeList() {
        final HashMap<String, String> params = new HashMap<>();
//        params.put("controller", "Locator");
        params.put("controller", "WhiteZebra");
        params.put("action", "GetLocatorMessagesSBV2");
        params.put("accountId", Preferences.getString(Config.ACCOUNT_ID));
//        Log.d("testingincident",Preferences.getString(Config.LANGUAGE));
        params.put("language",Preferences.getString(Config.LANGUAGE));
        final ApiHelper apiHelper = new ApiHelper();

        apiHelper.setOnSuccessListener(
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        LogUtils.debug("getincidenttypelipst","responsedata is: "+response);
                        try {
                            final Gson gson = new GsonBuilder().create();
                            LocatorMessage messages = gson.fromJson(response, LocatorMessage.class);

                            if (messages.getSuccess()) {
                                Calendar cal = Calendar.getInstance();
                                SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy",Locale.ENGLISH);

                                LogUtils.debug("cachingIncidentList","storing date: "+dateFormat.format(cal.getTime()));
                                LogUtils.debug("cachingIncidentList","storing the list: "+response);
                                Preferences.putString(Config.INCIDENTTYPE_LIST_LOADDATE,dateFormat.format(cal.getTime()));
                                Preferences.putString(Config.INCIDENTTYPE_LIST,response);

                                loadStoredIncidentTypeList();
                            } else {
                                dismissActivityProgressDialog();
                                setSBProfileInfoError(true);
                                LogUtils.debug("getincidenttypelist","responsedata has a data key and success is false");
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                            LogUtils.debug("getincidenttypelist","an exception was thrown while processing the responsedata");
                            dismissActivityProgressDialog();
                            //progressDialog.dismiss();
                            setSBProfileInfoError(true);
                        }
                    }
                }
        );

        apiHelper.setOnErrorListener(
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Do not retry connection because the current layout of the app (as of 9/24/20) causes the app
                        // to call the GetUserProfile system to run twice when the app first runs through its onCreate() method.
                        dismissActivityProgressDialog();
                        setSBProfileInfoError(true);
                        LogUtils.debug("getincidenttypelist",getString(R.string.connection_error)+error);
                    }
                }
        );

        apiHelper.prepareRequest(params,false);
        ApiHelper.getInstance(this).startRequest(apiHelper);
    }

    public void dismissActivityProgressDialog() {
        if ((activityProgressDialog != null) && activityProgressDialog.isShowing()) {
            activityProgressDialog.dismiss();
            activityProgressDialog = null;
        }
    }

    private void getSchoolBuildingList() {

        if(Preferences.isAvailable(Config.SCHOOLBUILDING_LIST_LOADDATE)) {
            try {
                boolean haveXDaysElapsed = Config.calcDaysSinceListLoad(Preferences.getString(Config.SCHOOLBUILDING_LIST_LOADDATE),3);
                if (haveXDaysElapsed) {
                    LogUtils.debug("getschoolbuildinglist","getschoolbuildinglist --> calling for schoolbuildinglist");
                    callForSchoolBuildingList();
                }
                else {
                    LogUtils.debug("getschoolbuildinglist", "getschoolbuildinglist --> loading stored schoolbuilding list");
                    loadStoredSchoolBuildingList();
                }
            } catch(Exception e) {
                LogUtils.debug("getschoolbuildinglist", "getschoolbuildinglist --> a parse exception was thrown while determining if the load date for the schoolbuildinglist was X days ago -> "+e);
                callForSchoolBuildingList();
            }
        } else {
            LogUtils.debug("getschoolbuildinglist","getschoolbuildinglist --> calling for schoolbuildinglist");
            callForSchoolBuildingList();
        }
    }

    private void callForSchoolBuildingList() {
        final HashMap<String, String> params = new HashMap<>();
        params.put("controller", "BlueDove");
        params.put("action", "GetBuildingsSB");
        params.put("accountId", Preferences.getString(Config.ACCOUNT_ID));
        params.put("typeId", "0");

        final ApiHelper apiHelper = new ApiHelper();

        apiHelper.setOnSuccessListener(
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        LogUtils.debug("getschoolbuildinglist","callforschoolbuildinglist --> responsedata is: "+response);
                        try {
                            JSONObject jsonobject = new JSONObject(response);
                            boolean success = jsonobject.getBoolean("success");
                            if (success) {
                                Calendar cal = Calendar.getInstance();
                                SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy",Locale.ENGLISH);

                                LogUtils.debug("getschoolbuildinglist","callforschoolbuildinglist --> storing date: "+dateFormat.format(cal.getTime()));
                                LogUtils.debug("getschoolbuildinglist","callforschoolbuildinglist --> storing the list: "+response);
                                Preferences.putString(Config.SCHOOLBUILDING_LIST_LOADDATE,dateFormat.format(cal.getTime()));
                                Preferences.putString(Config.SCHOOLBUILDING_LIST,response);

                                loadStoredSchoolBuildingList();

                            } else {
                                dismissActivityProgressDialog();
                                setSBProfileInfoError(true);
                                LogUtils.debug("getschoolbuildinglist","callforschoolbuildinglist --> responsedata has a data key and success is false");
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                            LogUtils.debug("getschoolbuildinglist","callforschoolbuildinglist --> an exception was thrown while processing the responsedata -> " + e);
                            dismissActivityProgressDialog();
                            setSBProfileInfoError(true);
                        }
                    }
                }
        );

        apiHelper.setOnErrorListener(
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        //Do not retry connection because the current layout of the app (as of 9/24/20) causes the app
                        // to call the GetUserProfile system to run twice when the app first runs through its onCreate() method.
                        dismissActivityProgressDialog();
                        setSBProfileInfoError(true);
                        LogUtils.debug("getschoolbuildinglist",getString(R.string.connection_error)+error);
                    }
                }
        );

        apiHelper.prepareRequest(params, false);
        ApiHelper.getInstance(this).startRequest(apiHelper);
    }

    private void preSelectSchoolSpinner() {
        String buildingName = Preferences.getString(Config.USER_BUILDING_NAME);
        String buildingId = Preferences.getString(Config.USER_BUILDING_ID);

        Log.e("PreSelect", "Pre-Selecting School Spinner:\n" + buildingName);

        if (!buildingId.isEmpty() && !buildingName.isEmpty() && buildingSpinner != null) {
            @SuppressWarnings("unchecked")
            ArrayAdapter arrayAdapter = (ArrayAdapter) buildingSpinner.getAdapter();
            if (arrayAdapter != null) {
                buildingSpinner.setSelection(arrayAdapter.getPosition(buildingName));
            }
        }
    }

    private void loadStoredIncidentTypeList() {
        String storedIncidentTypeList = Preferences.getString(Config.INCIDENTTYPE_LIST);
        LogUtils.debug("getincidenttypelist","loadstoredincidenttypelist --> the stored incident list is --> "+storedIncidentTypeList);
        try {
            final Gson gson = new GsonBuilder().create();
            LocatorMessage messages = gson.fromJson(storedIncidentTypeList, LocatorMessage.class);

            final ArrayList<IncidentTypeItem> locatorItems;
            ArrayList<String> messageList;

            setSBProfileInfoError(false);
            // Locate the WorldPopulation Class
            locatorItems = new ArrayList<>();
            // Create an array to populate the spinner
            messageList = new ArrayList<>();

            for (LocatorMessage.Data message : messages.getData()) {
                locatorItems.add(new IncidentTypeItem(message.getId(), message.getMessage(), message.getSpecialCaseValue(), message.getSCVParam()));
                messageList.add(message.getMessage());
            }

            // Locate the spinner in activity_main.xml
            LogUtils.debug("showincident", showQuestionnaire +"");

            if(showQuestionnaire != null) {
                if(showQuestionnaire) {
                    createCovid19SelectionList(locatorItems, messageList);
                } else {
                    locatorItems.add(0, new IncidentTypeItem("0", selectIncidentLangChange(), "0", "0"));
                    messageList.add(0, selectIncidentLangChange());

                    Spinner mySpinner = (Spinner) findViewById(R.id.message_spinner);
                    // Spinner adapter
                    assert mySpinner != null;
                    final CustomStringArrayAdapter spinnerAdapter = new CustomStringArrayAdapter(this,
                            R.layout.spinner_layout, R.id.spinner_textview, messageList, HomeActivity.this) {
                        @Override
                        public View getDropDownView(int position, View convertView, ViewGroup parent) {
                            View v = super.getDropDownView(position, convertView, parent);
                            //                    //((TextView) v).setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
                            //((TextView) v).setTypeface(vrFont);
                            String spinnerText = ((TextView) (v.findViewById(R.id.spinner_textview))).getText().toString();

                            if ("1".equals(locatorItems.get(position).getSpecialCaseValue()) && "0".equals(locatorItems.get(position).getSCVParam())) {
                                //if("No symptoms.  Feeling well.".equals(spinnerText)||"No hay síntomas. Sintiendose bien.".equals(spinnerText)) {
                                ((TextView) (v.findViewById(R.id.spinner_textview))).setTypeface(Typeface.DEFAULT_BOLD);
                                ((TextView) (v.findViewById(R.id.spinner_textview))).setTextColor(Color.rgb(71, 176, 63));
                            } else {
                                ((TextView) (v.findViewById(R.id.spinner_textview))).setTypeface(Typeface.DEFAULT);
                                ((TextView) (v.findViewById(R.id.spinner_textview))).setTextColor(Color.BLACK);
                            }

                            return v;
                        }
                    };

                    mySpinner.setAdapter(spinnerAdapter);
                    /*mySpinner.setAdapter(new ArrayAdapter<>(HomeActivity.this,
                            R.layout.spinner_center_item,
                            messageList));*/
                    // Spinner on item click listener
                    mySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> arg0,
                                                   View arg1, int position, long arg3) {
                            //((TextView) mySpinner.getChildAt(0)).setTextColor(Color.rgb());

                            //((TextView) arg1.getChildAt(position)).setTextColor(Color.rgb(71,176,63));
                            messageId = locatorItems.get(position).getId();

                            LogUtils.debug("SCVPARAM", "is --> " + locatorItems.get(position).getSCVParam());
                            incidentTypeSCVParam = locatorItems.get(position).getSCVParam();
                            covid19RelatedIncidentType = "1".equals(locatorItems.get(position).getSpecialCaseValue());
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> arg0) {
                        }
                    });
                }

                renderIncidentButtonOrQuestionnaire();
            }

            dismissActivityProgressDialog();

            LogUtils.debug("getincidenttypelist", "loadstoredincidenttypelist --> loadstoredincidenttypelist was processed correctly");
        } catch (Exception e) {
            LogUtils.debug("getincidenttypelist","loadstoredincidenttypelist --> an exception was thrown while processing the incidenttype list in loadstoredincidenttypelist");
            dismissActivityProgressDialog();
            setSBProfileInfoError(true);
        }
    }

    private class GetLatestVersion extends AsyncTask<String, String, String> {

        String packageName;

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
            this.packageName = getApplicationContext().getPackageName();
        }

        @Override
        protected String doInBackground(String... params) {

            try {
                //It retrieves the latest version by scraping the content of current version from play store at runtime
                String url = "https://play.google.com/store/apps/details?id=" + this.packageName;
                Document doc = Jsoup.connect(url).get();
                return doc.getElementsByAttributeValue("itemprop", "softwareVersion").first().text();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String latestVersion) {
            //Initial version is will get null
            if (latestVersion == null) {
                return;
            }
            String currentVersion = getCurrentVersion();
            long remindMeLaterTime = Preferences.getLong(Config.REMIND_ME_LATER);
            String update, notNow;
            if (currentVersion != null && !currentVersion.equals(latestVersion)) {
                if (shouldBeReminded(remindMeLaterTime)) {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);

                    builder.setTitle(R.string.update_available);
                    builder.setMessage(R.string.new_version);



                    builder.setPositiveButton(R.string.update, new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //Click button action
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + packageName)));
                            dialog.dismiss();
                        }
                    });
                    if (!Preferences.getBoolean(Config.FORCE_UPDATE)) {
                        builder.setNegativeButton(R.string.not_now, new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //Cancel button action
                                Preferences.putLong(Config.REMIND_ME_LATER, System.currentTimeMillis());
                            }
                        });
                    } else {
                        builder.setCancelable(false);
                    }
                    builder.show();
                }
            }
        }

        private boolean shouldBeReminded(long startTimeInput) {

            long millis = System.currentTimeMillis() - startTimeInput;
            int seconds = (int) (millis / 1000);
            int minutes = seconds / 60;
            return (minutes >= Config.REMINDER_INTERVAL) || (startTimeInput == 0);
        }

        private String getCurrentVersion() {

            PackageManager pm = context.getPackageManager();
            PackageInfo pInfo = null;
            try {
                pInfo = pm.getPackageInfo(context.getPackageName(), 0);
            } catch (PackageManager.NameNotFoundException e1) {
                e1.printStackTrace();
            }
            return pInfo != null ? pInfo.versionName : null;
        }
    }

    private void getUserProfileInfo() {
        final HashMap<String, String> params = new HashMap<>();
        params.put("controller", "RedBear");
        params.put("action", "GetUserInfo");
        params.put("contact_id", Preferences.getString(Config.CONTACT_ID));

        if(activityProgressDialog == null) {
            activityProgressDialog = new CustomProgressDialog(this);
        } else if(activityProgressDialog.isShowing()) {
            activityProgressDialog.dismiss();
        }

        activityProgressDialog.showDialog(getString(R.string.loading_user_profile));

        final ApiHelper apiHelper = new ApiHelper();

        apiHelper.setOnSuccessListener(
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        LogUtils.debug("getuserprofileinfo","responsedata is: "+response);

                        try {
                            JSONObject responseJsonObject = new JSONObject(response);

                            boolean success = responseJsonObject.getBoolean(Config.SUCCESS);

                            if (success) {
                                setSBProfileInfoError(false);
                                JSONObject data = responseJsonObject.getJSONObject(Config.DATA);
//                                Log.e("Account Info", "\nContact Info:\n" + data);
//                                LogUtils.debug("GetUserInfo", data.toString());
                                Preferences.putString(Config.CONTACT_TYPE, data.getString(Config.CONTACT_TYPE));
                                Preferences.putString(Config.CONTACT_ID, data.getString(Config.CONTACT_ID));
                                Preferences.putString(Config.USER_FULL_NAME, data.getString(Config.CONTACT_FIRST_NAME));
                                Preferences.putString(Config.CONTACT_TITLE, data.getString(Config.CONTACT_TITLE));
                                Preferences.putString(Config.USER_TITLE_ID, data.getString(Config.CONTACT_TITLE));
                                Preferences.putString(Config.USER_TITLE_NAME, data.getString("title_value"));
                                Preferences.putString(Config.CONTACT_EMAIL, data.getString(Config.EMAIL));
                                Preferences.putString(Config.CONTACT_CELLPHONE, data.getString(Config.CONTACT_CELLPHONE));
                                Preferences.putString(Config.CONTACT_CARRIER, data.getString(Config.CONTACT_CARRIER));
                                Preferences.putString(Config.CONTACT_CARRIER_ID, data.getString(Config.CONTACT_CARRIER_ID));
                                Preferences.putString(Config.CONTACT_USER_NAME, data.getString(Config.CONTACT_USER_NAME));
                                Preferences.putString(Config.CONTACT_PRIVILEGE, data.getString(Config.CONTACT_PRIVILEGE));
                                Preferences.putString(Config.CONTACT_SCHOOLS, data.getString(Config.CONTACT_SCHOOLS));
                                Preferences.putString(Config.CONTACT_SCHOOL_VALUE, data.getString(Config.CONTACT_SCHOOL_VALUE));
                                Preferences.putString(Config.USER_BUILDING_ID, data.getString(Config.CONTACT_SCHOOLS));
                                Preferences.putString(Config.USER_BUILDING_NAME, data.getString(Config.CONTACT_SCHOOL_VALUE));
                                Preferences.putString(Config.CONTACT_POLICY, data.getString(Config.CONTACT_POLICY));
                                Preferences.putString(Config.CONTACT_ACTIVE, data.getString(Config.CONTACT_ACTIVE));
                                Preferences.putBoolean(Config.GEOFENCE_TOGGLE, "1".equals(data.getString(Config.GEOFENCE_TOGGLE)));
                                Preferences.putBoolean(Config.INCIDENT_REQUIRED, "1".equals(data.getString(Config.INCIDENT_REQUIRED)));

                                Preferences.putBoolean(Config.HAS_TRANSLATIONS, data.getInt(Config.HAS_TRANSLATIONS) == 1);
                                //Setting the variable to display the screen
                                Preferences.putBoolean(Config.QUESTIONNAIRE_ACCOUNT, data.getInt(Config.QUESTIONNAIRE_ACCOUNT) == 1);
                                showQuestionnaire = Preferences.isAvailable(Config.QUESTIONNAIRE_ACCOUNT) ? Preferences.getBoolean(Config.QUESTIONNAIRE_ACCOUNT) : null;

                                LogUtils.debug("incident required", "json incident toggle: " + data.getString(Config.INCIDENT_REQUIRED));
                                LogUtils.debug("incident required", "incident toggle: " + Preferences.getBoolean(Config.INCIDENT_REQUIRED));
                                Config.setPushBadgeCount(data.getString(Config.FIELD_NOTIF_BADGECOUNT));
                                Config.setChatBadgeCount(data.getString(Config.FIELD_CHAT_BADGECOUNT));
                                TabBarActivity.updateTabBadgeCount(Config.PUSH_BADGE_COUNT, 1, false);
                                TabBarActivity.updateTabBadgeCount(Config.CHAT_BADGE_COUNT, 2, false);

                                if (Preferences.getBoolean(Config.ROOM_TOGGLE)) {
                                    Preferences.putString(Config.USER_ROOM, data.getString("room_number"));
                                    Preferences.putString(Config.USER_FLOOR, data.getString("floor"));
                                    Preferences.putString(Config.USER_DESCRIPTION, data.getString("description"));
                                } else {
                                    Preferences.putString(Config.USER_ROOM, "");
                                    Preferences.putString(Config.USER_FLOOR, "");
                                    Preferences.putString(Config.USER_DESCRIPTION, "");
                                }

                                //Kick out feature
                                if ("0".equals(Preferences.getString(Config.CONTACT_ACTIVE))) {
                                    LogUtils.debug("myTag", "Inside Kickout statement");
                                    dismissActivityProgressDialog();
                                    //new KickOutTask().execute();
                                    runKickOutTask();
                                } else {
                                    //Set the order for the language and terms&conditions dialogues
                                    if (!Preferences.isAvailable(Config.IS_LANGUAGE_SET) || Preferences.getBoolean(Config.IS_LANGUAGE_SET) == false) {
                                        determineLanguageSettings();
                                    }
                                    else if("0".equals(Preferences.getString(Config.CONTACT_POLICY).trim())) {
                                        dismissActivityProgressDialog();
                                        LogUtils.debug("OnboardingTag","showing terms and conditions");
                                        getTermsAndConditions();
                                    }
                                    else if(Preferences.getString(Config.PASSWORD).equals(Config.DEFAULT_PASSWORD)){
                                        dismissActivityProgressDialog();
                                        LogUtils.debug("OnboardingTag","showing email/password reset screens");
                                        Preferences.putBoolean(Config.PASSWORD_RESET_FLAG, false);
                                        Intent emailConfirmIntent = new Intent(context, EmailConfirmActivity.class);
                                        startActivity(emailConfirmIntent);
                                    }
                                    else {
                                        LogUtils.debug("OnboardingTag","passing through and calling for the school building list");
                                        if (Preferences.getBoolean(Config.GEOFENCE_TOGGLE)) {
                                            LogUtils.debug("getusergeo", "calling GetUserGeoInfo()");
                                            //new GetUserGeoInfo().execute();
                                            getUserGeoInfo();
                                        }
                                        getSchoolBuildingList();
                                    }
                                }
                                LogUtils.debug("getuserprofileinfo","responsedata has a data key and success is true");
                            } else {
                                dismissActivityProgressDialog();
                                setSBProfileInfoError(true);
                                LogUtils.debug("getuserprofileinfo","responsedata has a data key and success is false");
                            }

                        } catch(final Exception e) {
                            e.printStackTrace();
                            dismissActivityProgressDialog();
                            setSBProfileInfoError(true);
                            LogUtils.debug("getuserprofileinfo","an exception was thrown while processing the responsedata");
                        }
                    }
                }
        );

        apiHelper.setOnErrorListener(
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        //Do not retry connection because the current layout of the app (as of 9/24/20) causes the app
                        // to call the GetUserProfile system to run twice when the app first runs through its onCreate() method.
//                        int currRC = retryCounter.getRetryCount();//ApiHelper.getInstance(HomeActivity.this).getRetryCount();//ApiHelper.getInstance(HomeActivity.this).getRetryCount();
//                        LogUtils.debug("incidenttypelist","retried backend call: " + currRC + " times. Received an error code from the server -> "+error);
                        dismissActivityProgressDialog();
                        setSBProfileInfoError(true);
                        LogUtils.debug("getuserprofileinfo","an error was encountered while trying to establish a connection -> "+error);
                    }
                }
        );

        apiHelper.prepareRequest(params, false);
        ApiHelper.getInstance(this).startRequest(apiHelper);
    }

    private void determineLanguageSettings() {
        LogUtils.debug("OnboardingTag2","Value of spInstance.contains --> "+Preferences.getSPInstance(this).contains(Config.HAS_TRANSLATIONS));
        LogUtils.debug("OnboardingTag2","value of getBoolean --> "+Preferences.getBoolean(Config.HAS_TRANSLATIONS));
        if(Preferences.getSPInstance(this).contains(Config.HAS_TRANSLATIONS) && Preferences.getBoolean(Config.HAS_TRANSLATIONS)) {
            LogUtils.debug("OnboardingTag2", "showing language dialog");
            dismissActivityProgressDialog();
            showChangeLangDialog();
            Preferences.putBoolean(Config.IS_LANGUAGE_SET, true);
        } else {
            //PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("LANG", "en").commit();
            Preferences.putString("langCode", "en"); //adding the language choice to the preferences so the app sets the prefered language
            Preferences.putBoolean(Config.IS_LANGUAGE_SET, true);

            if("0".equals(Preferences.getString(Config.CONTACT_POLICY).trim())) {
                dismissActivityProgressDialog();
                LogUtils.debug("OnboardingTag2","showing terms and conditions");
                getTermsAndConditions();
            }
            else if(Preferences.getString(Config.PASSWORD).equals(Config.DEFAULT_PASSWORD)){
                dismissActivityProgressDialog();
                LogUtils.debug("OnboardingTag2","showing email/password reset screens");
                Preferences.putBoolean(Config.PASSWORD_RESET_FLAG, false);
                Intent emailConfirmIntent = new Intent(context, EmailConfirmActivity.class);
                startActivity(emailConfirmIntent);
            }
            else {
                LogUtils.debug("OnboardingTag2","passing through and calling for the school building list");
                if (Preferences.getBoolean(Config.GEOFENCE_TOGGLE)) {
                    LogUtils.debug("getusergeo", "calling GetUserGeoInfo()");
                    //new GetUserGeoInfo().execute();
                    getUserGeoInfo();
                }
                getSchoolBuildingList();
            }
        }
    }

    private void renderIncidentButtonOrQuestionnaire(){ //shows and hides covid button with boolean
        Button covidSelectionButton = (Button) findViewById(R.id.covidSelectionButton);
        Spinner messageSpinner = findViewById(R.id.message_spinner);
        if(showQuestionnaire != null) {
            if(showQuestionnaire) {
                LogUtils.debug("showincident","covidspinner");
                LogUtils.debug("Questionnaire","String is --> "+ getResources().getString(R.string.question_button_text1));
                covidSelectionButton.setText(R.string.question_button_text1);
                covidSelectionButton.setVisibility(View.VISIBLE);
                messageSpinner.setVisibility(View.GONE);
            } else {
                LogUtils.debug("showincident","messageSpinner");
                covidSelectionButton.setVisibility(View.GONE);
                messageSpinner.setVisibility(View.VISIBLE);
            }
        } else {
            covidSelectionButton.setVisibility(View.GONE);
            messageSpinner.setVisibility(View.GONE);
        }
    }

    private void loadStoredSchoolBuildingList() {
        String storedSchoolBuildingList = Preferences.getString(Config.SCHOOLBUILDING_LIST);
        LogUtils.debug("getschoolbuildinglist","loadstoredschoolbuildinglist --> the stored schoolbuildinglist is --> "+storedSchoolBuildingList);
        try {
            JSONObject jsonobject = new JSONObject(storedSchoolBuildingList);
            final ArrayList<SpinnerItem> spinnerItems = new ArrayList<>();
            final ArrayList<String> buildingList = new ArrayList<>();

            setSBProfileInfoError(false);
            // Locate the NodeList name
            JSONArray jsonarray = jsonobject.getJSONArray("data");
            for (int i = 0; i < jsonarray.length(); i++) {
                jsonobject = jsonarray.getJSONObject(i);
                String id = jsonobject.getString("id");
                String values = jsonobject.getString("value");
                spinnerItems.add(new SpinnerItem(id, values));
                // Populate spinner
                buildingList.add(jsonobject.optString("value"));
            }
            buildingList.add(0, selectSchoolSpinnerChange());
            spinnerItems.add(0, new SpinnerItem("0", selectSchoolSpinnerChange()));

            // Locate the spinner in activity_main.xml
            Spinner BuildingSpinner = (Spinner) findViewById(R.id.school_spinner);
            // Spinner adapter
            assert BuildingSpinner != null;
            BuildingSpinner.setAdapter(new ArrayAdapter<String>(HomeActivity.this,
                    R.layout.spinner_center_item,
                    buildingList) {
                @Override
                public boolean isEnabled(int position) {
                    if (position == 0) {
                        return false;
                    }
                    return true;
                }
            });
            // Spinner on item click listener
            BuildingSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                @Override
                public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {
                    LogUtils.debug("POSITION","what is the position of this option --> "+position);
                    if (position != 0) {
                        String selectedBuildingId = spinnerItems.get(position).getId();
                        Log.e("SELECTED BID", "HOME STARTING BUILDING ID: " + Preferences.getString(Config.USER_BUILDING_ID));
                        Log.e("SELECTED BID", "HOME SELECTED BUILDING ID: " + selectedBuildingId);
                        Log.e("SELECTED BID", "BOOLEAN IS: " + Preferences.getString(Config.USER_BUILDING_ID).equals(selectedBuildingId));

                        if(!Preferences.getString(Config.USER_BUILDING_ID).equals(selectedBuildingId)){
                            //silently store the user info to update quick selected bid
                            //new SetUserInfo(true).execute();
                            Log.e("SELECTED BID", "running setuserinfo");
                            Preferences.putString(Config.USER_BUILDING_ID, spinnerItems.get(position).getId());
                            Preferences.putString(Config.USER_BUILDING_NAME, spinnerItems.get(position).getValue());
                            setUserInfo(true);

                            //TODO: Finish up geofencing code
                            if(Preferences.getBoolean(Config.GEOFENCE_TOGGLE) && !Preferences.getBoolean(Config.IS_SUPER_USER)){
                                //new GetUserGeoInfo().execute();
                            }
                        }

//                        Preferences.putString(Config.USER_BUILDING_ID, spinnerItems.get(position).getId());
//                        Preferences.putString(Config.USER_BUILDING_NAME, spinnerItems.get(position).getValue());
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> arg0) {

                }
            });

            LogUtils.debug("getschoolbuildinglist","loadstoredschoolbuildinglist --> schoolbuildinglist was processed correctly");

            preSelectSchoolSpinner();

            getIncidentTypeList();

        } catch (Exception e) {
            LogUtils.debug("getschoolbuildinglist","loadstoredschoolbuildinglist --> an exception was thrown while processing the schoolbuidling list");
            // Locate the spinner in activity_main.xml
            dismissActivityProgressDialog();
            setSBProfileInfoError(true);
        }
    }

//    public void getRemainingProfileInfo() {
//        if(Preferences.getBoolean(Config.GEOFENCE_TOGGLE)) {
//            LogUtils.debug("calling getusergeo","calling GetUserGeoInfo()");
//            new GetUserGeoInfo().execute();
//        }
//
//        new GetSchoolBuildingsTask().execute();
//    }

    private void getUserGeoInfo() {
        final HashMap<String, String> params = new HashMap<>();
        params.put("controller", "RedBear");
        params.put("action", "GetUserGeoInfo");
        params.put("contact_id", Preferences.getString(Config.CONTACT_ID));

        final ApiHelper apiHelper = new ApiHelper();

        apiHelper.setOnSuccessListener(
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject responseJsonObject = new JSONObject(response);
                            boolean success = responseJsonObject.getBoolean(Config.SUCCESS);
                            if (success) {
                                JSONObject data = responseJsonObject.getJSONObject(Config.DATA);
                                Log.e("GeoFence Info", "\n" + data);
                                /**[START] GEOFENCING CODE**/
                                /// Home: Lat: 40.8828593, long: -73.8339451
                                // Work: 41.033004, -73.766049
                                // Android Emulator location: 37.422532, -122.085627
                                double latitude = Double.parseDouble(data.getString("latitude"));
                                double longitude = Double.parseDouble(data.getString("longitude"));
                                float radius = Float.parseFloat(data.getString("radius"));
                                String geoName = data.getString("schools");

                                geofenceLat = latitude;
                                geofenceLong = longitude;
                                geofenceRad = radius;

                                //this starts the geofence monitoring
                                //TabBarActivity.SetGeoFence("DummyFence", latitude,longitude, radius);

                                // Instead of using geofencing, calculate the distance from current location to the given coordinates.
                                // If the distance is greater than the given radius, the user is outside the "Geofence"
                                Intent intentSetGeoFence = new Intent("geofenceEvent");
                                //send broadcast
                                String event = "set";
                                intentSetGeoFence.putExtra("event", event);
                                intentSetGeoFence.putExtra("lat", latitude);
                                intentSetGeoFence.putExtra("long", longitude);
                                intentSetGeoFence.putExtra("radius", radius);
                                //intentSetGeoFence.putExtra("name", geoName);
                                context.sendBroadcast(intentSetGeoFence);

                                /**[END] GEOFENCING CODE **/
                                LogUtils.debug("getUserGeoInfo","responsedata has a data key and success is true");
                            }
                            else {
                                LogUtils.debug("getUserGeoInfo","responsedata has a data key and success is false");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            LogUtils.debug("getUserGeoInfo","an exception was thrown while processing the response data");
                        }
                    }
                }
        );

        apiHelper.setOnErrorListener(
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        LogUtils.debug("getUserGeoInfo","an error was encountered while trying to establish a connection");
                    }
                }
        );

        apiHelper.prepareRequest(params,false);
        ApiHelper.getInstance(this).startRequest(apiHelper);
    }

    private void runKickOutTask() {
        final HashMap<String, String> params = new HashMap<>();
        params.put("controller", "RedBear");
        params.put("action", "KickUserOut");
        params.put("contactId", Preferences.getString(Config.CONTACT_ID));

        ApiHelper apiHelper = new ApiHelper();

        apiHelper.setOnSuccessListener(
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject responseJsonObject = new JSONObject(response);
                            boolean success = responseJsonObject.getBoolean(Config.SUCCESS);
                            if (success) {
                                //Kick out successful
                                //Log.d(LOG_TAG,"Kick out successful");
                                Preferences.putBoolean(Config.IS_LOGGED_IN, false);
                                Intent intent = new Intent(context, LoginActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                Toast.makeText(context, getString(R.string.home_inactive), Toast.LENGTH_LONG).show();
                                LogUtils.debug("runKickOutTask","responsedata has a data key and success is true");
                            } else {
                                LogUtils.debug("runKickOutTask","responsedata has a data key and success is false, kickout was unsuccessful");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            LogUtils.debug("runKickOutTask","an exception was thrown while processing the responsedata");
                        }
                    }
                }
        );

        apiHelper.setOnErrorListener(
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        LogUtils.debug("runKickOutTask","an error was encountered while trying to establish a connection");
                    }
                }
        );

        apiHelper.prepareRequest(params,true);
        ApiHelper.getInstance(this).startRequest(apiHelper);
    }

    private void getTermsAndConditions() {
        final HashMap<String, String> params = new HashMap<>();
        params.put("controller", "RedBear");
        params.put("action", "GetTerms");
        params.put("accountId", Preferences.getString(Config.ACCOUNT_ID));

        final ApiHelper apiHelper = new ApiHelper();

        apiHelper.setOnSuccessListener(
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            LogUtils.debug("getTermsAndConditions","responsedata is: "+response);
                            JSONObject responseJsonObject = new JSONObject(response);
                            boolean success = responseJsonObject.getBoolean(Config.SUCCESS);
                            if (success) {
                                JSONObject data = responseJsonObject.getJSONObject(Config.DATA);
                                Preferences.putString(Config.CONTACT_POLICY_CONTENT, data.getString(Config.CONTACT_POLICY));
                                new EULA(HomeActivity.this).show();
                                LogUtils.debug("getTermsAndConditions","responsedata has a data key and success is true");
                            } else {
                                LogUtils.debug("getTermsAndConditions","responsedata has a data key and success is false");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            LogUtils.debug("getTermsAndConditions","an exception was thrown while processing the response data");
                        }
                    }
                }
        );

        apiHelper.setOnErrorListener(
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        LogUtils.debug("getTermsAndConditions","an error was encountered while trying to establish a connection");
                    }
                }
        );

        apiHelper.prepareRequest(params,false);
        ApiHelper.getInstance(this).startRequest(apiHelper);
    }

    private class GeocodingTask extends AsyncTask<Void, Void, String> {

        private Location location;

        GeocodingTask(Location location) {
            this.location = location;
        }

        //Brancode Constructor
//        GeocodingTask (double latit, double longi){
//            location.setLatitude(latit);
//            location.setLongitude(longi);
//        }

        @Override
        protected String doInBackground(Void... voids) {

            return getAddressFromLocation(this.location.getLatitude(), this.location.getLongitude());//this.location.getLatitude(), this.location.getLongitude());
        }

        private String getAddressFromLocation(double latitude, double longitude) {

            try {
                Geocoder geoCoder = new Geocoder(context);
                List<Address> matches = geoCoder.getFromLocation(latitude, longitude, 1);
                Address bestMatch = (matches.isEmpty() ? null : matches.get(0));
                String streetAddress = (bestMatch != null ? bestMatch.getAddressLine(0) : "N/A") + ", ";
                streetAddress += (bestMatch.getAddressLine(1) != null ? bestMatch.getAddressLine(1) : "N/A") + ", ";
                streetAddress += bestMatch.getAddressLine(2) != null ? bestMatch.getAddressLine(2) : "N/A";
                if (bestMatch == null) {
                    streetAddress = "Unavailable";
                }
                return streetAddress;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(final String streetAddress) {
            if (streetAddress != null) {
                mStreetAddress = streetAddress;

                Preferences.putString(Config.ADDRESS, mStreetAddress);
                Preferences.putString(Config.LATITUDE, String.valueOf(location.getLatitude()));
                Preferences.putString(Config.LONGITUDE, String.valueOf(location.getLongitude()));
                LogUtils.debug("validateAlert", " address = " + mStreetAddress);

                sendPanicAlert(true);
            } else {
                LogUtils.debug("validateAlert","geocoding error finding address");
                sbEmergencyAlertProgressDialog.dismiss();
                showGeocodingError();
            }
        }
    }

//TODO: Fix performance issue when too many plans to cache and encrypt
//    private class GetAllResourcesTask extends AsyncTask<Void, Void, String> {
//
//        @Override
//        protected void onPreExecute() {
//
//            super.onPreExecute();
//        }
//
//        @Override
//        protected String doInBackground(Void... voidParams) {
//
//            HashMap<String, String> params = new HashMap<>();
//            params.put("controller", "Help");
//            params.put("action", "GetHelpResourcesGroupedSBv4");
//            params.put("contact_id", Preferences.getString(Config.CONTACT_ID));
//            params.put("accountId", Preferences.getString(Config.ACCOUNT_ID));
//            return FunctionHelper.apiCaller(context, params);
//        }
//
//        @Override
//        protected void onPostExecute(String result) {
//
//            if (result != null) {
//                //Log.d(LOG_TAG,result);
//                Preferences.putString(Config.HELP_CACHE, result);
//                HelpModel helpModel = new Gson().fromJson(result, HelpModel.class);
//                if (helpModel.getSuccess()) {
//                    //Log.d(LOG_TAG,"resources success");
//                    for (List<HelpModel.HelpItem> parent : helpModel.getData()) {
//                        //Log.d(LOG_TAG,"type id: "+parent.get(0).getTypeId());
//                        int typeId = Integer.valueOf(parent.get(0).getTypeId());
//                        switch (typeId) {
//                            case HelpActivity.PLANS:
//                                for (HelpModel.HelpItem child : parent) {
//                                    //Log.d(LOG_TAG,child.toString());
//                                    new FileDownloader.DownloadTask(context).execute(child.getValue());
//                                }
//                                break;
//                            case HelpActivity.WEBLINK:
//                                for (HelpModel.HelpItem child : parent) {
//                                    new WebCacher(context, child.getValue());
//                                }
//                                break;
//                        }
//                    }
//                }
//            }
//        }
//    }

    private void setUserInfo(final boolean isSilent) {
        LogUtils.debug("setuserinfo","inside setuserinfo method");
        HashMap<String, String> params = new HashMap<>();
        params.put("controller", "RedBear");
        params.put("action", "SaveIdentity");
        params.put("accountId", Preferences.getString(Config.ACCOUNT_ID));
        params.put("unique_id", Preferences.getString(Config.UNIQUE_ID));
        params.put("contact_id", Preferences.getString(Config.CONTACT_ID));
        params.put("fname", Preferences.getString(Config.USER_FULL_NAME));
        params.put("title", Preferences.getString(Config.USER_TITLE_ID));
        params.put("school", Preferences.getString(Config.USER_BUILDING_ID));
        params.put("cellphone", Preferences.getString(Config.CONTACT_CELLPHONE));
        params.put("email", Preferences.getString(Config.CONTACT_EMAIL));
        params.put("image", new File(Preferences.getString(Config.USER_PROFILE_PICTURE)).getName());
        params.put("app_type", "3");
        //params.put("room", hasRoom ? Preferences.getString(Config.USER_ROOM) : "");
        params.put("room", Preferences.getString(Config.USER_ROOM));
        params.put("floor", Preferences.getString(Config.USER_FLOOR));
        params.put("description", Preferences.getString(Config.USER_DESCRIPTION));
        params.put("language",Preferences.getString(Config.LANGUAGE));

        final ApiHelper apiHelper = new ApiHelper();

        final RetryCounter retryCounter = new RetryCounter();

        apiHelper.setOnSuccessListener(new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    Response defaults = new Gson().fromJson(response, Response.class);
                    if (defaults.getSuccess()) {

                        //Save successful
                        if (!isSilent) {
                            Toast.makeText(getApplicationContext(), getString(R.string.Information_Saved), Toast.LENGTH_SHORT).show();
                        }

                        LogUtils.debug("setuserinfoHomeActivity","responsedata returned with a data key and success key of true");

                    } else {
                        //error
                        if (!isSilent) {
                            Toast.makeText(getApplicationContext(), getString(R.string.Information_Error), Toast.LENGTH_SHORT).show();
                        }
                        LogUtils.debug("setuserinfoHomeActivity","responsedata returned with a data key and success key of false");
                    }

                } catch (Exception e) {
                    LogUtils.debug("setuserinfoHomeActivity","an exception was thrown while processing the responsedata");
                    if (!isSilent) {
                        Toast.makeText(getApplicationContext(), getString(R.string.Information_Error), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        apiHelper.setOnErrorListener(new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                int currRC = retryCounter.getRetryCount();//ApiHelper.getInstance(HomeActivity.this).getRetryCount();//ApiHelper.getInstance(HomeActivity.this).getRetryCount();
                LogUtils.debug("setuserinfoHomeActivity","retried: " + apiHelper.getUrl() +  " backend call\n" + currRC + " times. Received an error code from the server -> "+error);

                if(currRC == 0) {
                    if (!isSilent) {
                        Toast.makeText(getApplicationContext(), getString(R.string.Information_Error), Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    retryCounter.decrementRetryCount();
                    ApiHelper.getInstance(HomeActivity.this).startRequest(apiHelper);
                }
            }
        });

        apiHelper.prepareRequest(params, true);
        ApiHelper.getInstance(this).startRequest(apiHelper);
    }

    public class SetUserInfo extends AsyncTask<Void, Void, String> {
        boolean isSilent = false;
        SetUserInfo(Boolean silent){
            isSilent = silent;
        }

        @Override
        protected String doInBackground(Void... voidParams) {

            HashMap<String, String> params = new HashMap<>();
//            params.put("controller", "User");
            params.put("controller", "RedBear");
            params.put("action", "SaveIdentity");
            params.put("accountId", Preferences.getString(Config.ACCOUNT_ID));
            params.put("unique_id", Preferences.getString(Config.UNIQUE_ID));
            params.put("contact_id", Preferences.getString(Config.CONTACT_ID));
            params.put("fname", Preferences.getString(Config.USER_FULL_NAME));
            params.put("title", Preferences.getString(Config.USER_TITLE_ID));
            params.put("school", Preferences.getString(Config.USER_BUILDING_ID));
            params.put("cellphone", Preferences.getString(Config.CONTACT_CELLPHONE));
            params.put("email", Preferences.getString(Config.CONTACT_EMAIL));
            params.put("image", new File(Preferences.getString(Config.USER_PROFILE_PICTURE)).getName());
            params.put("app_type", "3");
            //params.put("room", hasRoom ? Preferences.getString(Config.USER_ROOM) : "");
            params.put("room", Preferences.getString(Config.USER_ROOM));
            params.put("floor", Preferences.getString(Config.USER_FLOOR));
            params.put("description", Preferences.getString(Config.USER_DESCRIPTION));

            return FunctionHelper.apiCaller(context, params);
        }

        @Override
        protected void onPostExecute(String responseData) {

            if (responseData != null) {
                //Log.d(LOG_TAG, "json = " + responseData);
                Response defaults = new Gson().fromJson(responseData, Response.class);
                if (defaults.getSuccess()) {

                    //Save successful
                    if(!isSilent){
                        Toast.makeText(getApplicationContext(), getString(R.string.Information_Saved), Toast.LENGTH_SHORT).show();
                    }


                } else {
                    //error
                    if(!isSilent){
                        Toast.makeText(getApplicationContext(), getString(R.string.Information_Error), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }

    /**
     * Represents an asynchronous task to save the token to the server
     */
    public class SaveDeviceInfoTask extends AsyncTask<Void, Void, String> {

        private String deviceToken;

        SaveDeviceInfoTask(String token) {

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
            postDataParams.put("deviceType", "2");
            postDataParams.put("app_type", "3");
            postDataParams.put("production", BuildConfig.DEBUG ? "1" : "1");
            return FunctionHelper.apiCaller(context, postDataParams);
        }

        @Override
        protected void onPostExecute(final String responseData) {

            if (responseData != null) {
                //Log.e("SAVEDEVICEINFO", "responseData = " + responseData);
                Preferences.putString(Config.DEVICE_TOKEN, deviceToken);
                Log.e("SAVEDEVICEINFO", "[Token Stored]\n" + Preferences.getString(Config.DEVICE_TOKEN));
                registerUniqueId();

            } else {
                Log.e("SAVEDEVICEINFO", "No JSON received ! :(");
            }
        }

        @Override
        protected void onCancelled() {

        }
    }

    @Override
    public void onBackPressed() {
        //LogUtils.debug("backPressed","SmartButton Activity - back button pressed, method overridden, so does nothing");
        if(getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().popBackStack();
            LogUtils.debug("backPressed","NotifMenuActivity - notiflist fragment is visible, so closing fragment");
        } else {
            LogUtils.debug("backPressed", "NotifMenuActivity - back button pressed - method overriden, so does nothing");
        }
    }

}
