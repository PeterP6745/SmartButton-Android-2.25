package messagelogix.com.smartbuttoncommunications.geofence;


import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;
import com.google.gson.Gson;

import java.util.List;

public class GeofenceTransitionsIntentService extends IntentService {

    // region Properties«

    private final String TAG = GeofenceTransitionsIntentService.class.getName();

    private SharedPreferences prefs;
    private Gson gson;

    // endregion

    // region Constructors

    public GeofenceTransitionsIntentService() {
        super("GeofenceTransitionsIntentService");
        //init the geofence active zone toggle to false whenever geofence has been updated

        Log.e(TAG, "GEOFENCE Intent service...");
    }

    // endregion

    // region Overrides

    protected void onHandleIntent(Intent intent) {
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()) {
            String errorMessage = "Something went wrong";//GeofenceErrorMessages.getErrorString(this,
                   // geofencingEvent.getErrorCode());
            Log.e(TAG, errorMessage);
            return;
        }

        // Get the transition type.
        int geofenceTransition = geofencingEvent.getGeofenceTransition();
        Log.e("GEOFENCE TRANSITION:", "\nType = " + geofencingEvent.getGeofenceTransition());
        // Test that the reported transition was of interest.
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER ||
                geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {



            if(geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER){
                //Toast.makeText(getApplicationContext(), "You have entered a geofence", Toast.LENGTH_LONG).show();
                Log.e(TAG, "GEOFENCE ENTER: ENABLE THE SMART BUTTON");

                Intent intentEnter = new Intent("geofenceEvent");
                //send broadcast
                String event = "enter";
                intentEnter.putExtra("event", event);
                getApplicationContext().sendBroadcast(intentEnter);
            }
            else{
                //Toast.makeText(getApplicationContext(), "You have left a geofence. SB is now deactivated", Toast.LENGTH_LONG).show();
                Log.e(TAG, "GEOFENCE EXIT: DISABLE THE SMART BUTTON");

                Intent intentExit = new Intent("geofenceEvent");
                //send broadcast
                String event = "exit";
                intentExit.putExtra("event", event);
                getApplicationContext().sendBroadcast(intentExit);
            }

            // Get the geofences that were triggered. A single event can trigger
            // multiple geofences.
            List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();

            // Get the transition details as a String.
          /*  String geofenceTransitionDetails = getGeofenceTransitionDetails(
                    this,
                    geofenceTransition,
                    triggeringGeofences
            );*/

            // Send notification and log the transition details.
            //sendNotification(geofenceTransitionDetails);
           // Log.i(TAG, geofenceTransitionDetails);
        } else {
            // Log the error.
            Log.e(TAG, "Something went wrong");
//            Log.e(TAG, getString(R.string.geofence_transition_invalid_type,
//                    geofenceTransition));
        }
    }
}


