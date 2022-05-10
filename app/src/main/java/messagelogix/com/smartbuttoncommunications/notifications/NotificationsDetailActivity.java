package messagelogix.com.smartbuttoncommunications.notifications;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import messagelogix.com.smartbuttoncommunications.R;
import messagelogix.com.smartbuttoncommunications.activities.core.TabBarActivity;
import messagelogix.com.smartbuttoncommunications.utils.ApiHelper;
import messagelogix.com.smartbuttoncommunications.utils.Config;
import messagelogix.com.smartbuttoncommunications.utils.CustomProgressDialog;
import messagelogix.com.smartbuttoncommunications.utils.LogUtils;
import messagelogix.com.smartbuttoncommunications.utils.Preferences;

/**
 * Created by Vahid
 * An activity representing a single PushNotifications detail screen. This
 * activity is only used narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 */
public class NotificationsDetailActivity extends AppCompatActivity {

    private static final String LOG_TAG = NotificationsDetailActivity.class.getSimpleName();

    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_pushnotifications_detail);

        Toolbar toolbar = findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        getNotifDetails();

        // savedInstanceState is non-null when there is fragment state
        // saved from previous configurations of this activity
        // (e.g. when rotating the screen from portrait to landscape).
        // In this case, the fragment will automatically be re-added
        // to its container so we don't need to manually add it.
        // For more information, see the Fragments API guide at:
        //
        // http://developer.android.com/guide/components/fragments.html
        //
//        if (savedInstanceState == null) {
//            LogUtils.debug(LOG_TAG, "(savedInstanceState == null)");
//        } else {
//            LogUtils.debug(LOG_TAG, "(savedInstanceState != null)");
//        }
        // Create the detail fragment and add it to the activity
        // using a fragment transaction.
//        Bundle arguments = new Bundle();
//        arguments.putString(NotificationsDetailFragment.ARG_ITEM_ID, getIntent().getStringExtra(NotificationsDetailFragment.ARG_ITEM_ID));
        LogUtils.debug("insideoncreate","arg_id: "+getIntent().getStringExtra(NotificationsDetailFragment.ARG_ITEM_ID));
    }

    private void getNotifDetails() {
        final CustomProgressDialog progressDialog = new CustomProgressDialog(this);
        progressDialog.showDialog(getString(R.string.Getting_Messages));

        HashMap<String, String> params = new HashMap<>();
        params.put("controller", "GreenCow");
        params.put("action", "GetPushAlertDetails2");
        LogUtils.debug("PUSHNOTIFINTENT","ARG_ID: "+getIntent().getStringExtra(NotificationsDetailFragment.ARG_ITEM_ID));

        boolean fromNotifFlag = false;
        if(getIntent().getStringExtra(NotificationsDetailFragment.ARG_ITEM_ID) != null) {
            params.put("campaignId", getIntent().getStringExtra(NotificationsDetailFragment.ARG_ITEM_ID));
        } else {
            fromNotifFlag = true;
            params.put("campaignId",getIntent().getStringExtra("campaign_id"));
        }
        final boolean finalFromNotifFlag = fromNotifFlag;

        LogUtils.debug("PUSHNOTIFINTENT","CONTACT_ID: "+Preferences.getString(Config.CONTACT_ID));
        params.put("contactid", Preferences.getString(Config.CONTACT_ID));//"deviceId", Preferences.getString(Config.DEVICE_TOKEN));

        ApiHelper apiHelper = new ApiHelper();
        apiHelper.setOnSuccessListener(new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
                try {
                    LogUtils.debug("notifDetails", "responseData:" + response);
                    JSONObject responseJsonObject = new JSONObject(response);
                    boolean success = responseJsonObject.getBoolean(Config.SUCCESS);
                    if(success) {
                        JSONObject data = responseJsonObject.getJSONObject(Config.DATA);
                        LogUtils.debug("notifDetails",data.toString());
                        LogUtils.debug("notifDetails",data.getString("first_viewed"));
                        LogUtils.debug("NotificationsDetailFrag","data: "+data.toString());

                        Bundle arguments = new Bundle();
                        if(!finalFromNotifFlag) {
                            arguments.putString(NotificationsDetailFragment.ARG_ITEM_ID, getIntent().getStringExtra(NotificationsDetailFragment.ARG_ITEM_ID));
//                            if(isSuperUser)
                            TabBarActivity.decrementNotifTabBadgeCount();
                        }
                        else
                            arguments.putString(NotificationsDetailFragment.ARG_ITEM_ID,getIntent().getStringExtra(NotificationsDetailFragment.ARG_ITEM_ID));//("campaign_id"));

                        arguments.putString(NotificationsDetailFragment.ARG_SUBJECT, data.getString("subject"));//getIntent().getStringExtra(NotificationsDetailFragment.ARG_SUBJECT));
                        arguments.putString(NotificationsDetailFragment.ARG_MESSAGE, data.getString("message"));//getIntent().getStringExtra(NotificationsDetailFragment.ARG_MESSAGE));
                        arguments.putString(NotificationsDetailFragment.ARG_DATE, data.getString("schedule_datetime"));//getIntent().getStringExtra(NotificationsDetailFragment.ARG_DATE));
                        arguments.putString(NotificationsDetailFragment.ARG_FIRST_VIEWED, data.getString("first_viewed"));
                        NotificationsDetailFragment fragment = new NotificationsDetailFragment();
                        fragment.setArguments(arguments);
                        getSupportFragmentManager().beginTransaction().add(R.id.pushnotifications_detail_container, fragment).commit();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        apiHelper.setOnErrorListener(new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
            }
        });

        apiHelper.prepareRequest(params,false);
        ApiHelper.getInstance(this).startRequest(apiHelper);
    }

    private void goBack() {
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
//            NavUtils.navigateUpFromSameTask(this);
            goBack();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
