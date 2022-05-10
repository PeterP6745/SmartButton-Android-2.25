package messagelogix.com.smartbuttoncommunications.activities.message;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

//import kotlinx.coroutines.Dispatchers;
import messagelogix.com.smartbuttoncommunications.R;
import messagelogix.com.smartbuttoncommunications.activities.core.TabBarActivity;
import messagelogix.com.smartbuttoncommunications.utils.Config;
import messagelogix.com.smartbuttoncommunications.utils.FunctionHelper;
import messagelogix.com.smartbuttoncommunications.utils.LogUtils;
import messagelogix.com.smartbuttoncommunications.utils.Preferences;

//import static kotlinx.coroutines.CoroutineScopeKt.CoroutineScope;

/**
 * Created by Vahid
 * An activity representing a single PushNotifications detail screen. This
 * activity is only used narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link PushNotificationsListActivity}.
 */
public class PushNotificationsDetailActivity extends AppCompatActivity {

    private static final String LOG_TAG = PushNotificationsDetailActivity.class.getSimpleName();
    boolean isSuperUser = Preferences.getBoolean(Config.IS_SUPER_USER);


    //private final String campaignId;

    //private ProgressBar pb;
    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pushnotifications_detail);

        //pb = new ProgressBar.findViewById(R.id.notifdetail_progressbar);
        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);
        LogUtils.debug(LOG_TAG, "(onCreate)");
        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        GetNotifDetails();
        // savedInstanceState is non-null when there is fragment state
        // saved from previous configurations of this activity
        // (e.g. when rotating the screen from portrait to landscape).
        // In this case, the fragment will automatically be re-added
        // to its container so we don't need to manually add it.
        // For more information, see the Fragments API guide at:
        //
        // http://developer.android.com/guide/components/fragments.html
        //
        if (savedInstanceState == null) {
            LogUtils.debug(LOG_TAG, "(savedInstanceState == null)");
        } else {
            LogUtils.debug(LOG_TAG, "(savedInstanceState != null)");
        }
        // Create the detail fragment and add it to the activity
        // using a fragment transaction.
//        Bundle arguments = new Bundle();
//        arguments.putString(PushNotificationsDetailFragment.ARG_ITEM_ID, getIntent().getStringExtra(PushNotificationsDetailFragment.ARG_ITEM_ID));
        LogUtils.debug("insideoncreate","arg_id: "+getIntent().getStringExtra(PushNotificationsDetailFragment.ARG_ITEM_ID));

//        Log.d("insideoncreate","arg_message: "+getIntent().getStringExtra(PushNotificationsDetailFragment.ARG_MESSAGE));
//        Log.d("insideoncreate","arg_date: "+getIntent().getStringExtra(PushNotificationsDetailFragment.ARG_DATE));
//        arguments.putString(PushNotificationsDetailFragment.ARG_SUBJECT, getIntent().getStringExtra(PushNotificationsDetailFragment.ARG_SUBJECT));
//        arguments.putString(PushNotificationsDetailFragment.ARG_MESSAGE, getIntent().getStringExtra(PushNotificationsDetailFragment.ARG_MESSAGE));
//        arguments.putString(PushNotificationsDetailFragment.ARG_DATE, getIntent().getStringExtra(PushNotificationsDetailFragment.ARG_DATE));
//        //arguments.putString(PushNotificationsDetailFragment.ARG_FIRST_VIEWED, getIntent().getStringExtra(PushNotificationsDetailFragment.ARG_FIRST_VIEWED));
//        PushNotificationsDetailFragment fragment = new PushNotificationsDetailFragment();
//        fragment.setArguments(arguments);
//        getSupportFragmentManager().beginTransaction()
//                .add(R.id.pushnotifications_detail_container, fragment)
//                .commit();


    }

    public void GetNotifDetails() {
        //ProgressBar pb = (ProgressBar) findViewById(R.id.notifdetail_progressbar);
        //pb.setVisibility(View.VISIBLE);
//        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(100,100);
//        params.addRule(RelativeLayout.CENTER_IN_PARENT);
//        layout.addView(pb, params);
        pd = new ProgressDialog(PushNotificationsDetailActivity.this);
        pd.setMessage(getString(R.string.Getting_Messages));
        pd.setIndeterminate(false);
        pd.setCancelable(false);
        pd.show();
        new GetNotifDetailsTask().execute();
        //LogUtils.debug("notifDetails",getIntent().getStringExtra(PushNotificationsDetailFragment.ARG_ITEM_ID));
    }

    public class GetNotifDetailsTask extends AsyncTask<String, Void, String> {

        private Boolean fromNotifFlag = false;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //pb = (ProgressBar) findViewById(R.id.notifdetail_progressbar);
        }

        @Override
        protected String doInBackground(String... strings) {

            HashMap<String, String> params = new HashMap<>();
            //params.put("controller", "Push");
            params.put("controller", "GreenCow");
            params.put("action", "GetPushAlertDetails2");
            LogUtils.debug("PUSHNOTIFINTENT","ARG_ID: "+getIntent().getStringExtra(PushNotificationsDetailFragment.ARG_ITEM_ID));
            if(getIntent().getStringExtra(PushNotificationsDetailFragment.ARG_ITEM_ID) != null) {
                fromNotifFlag = false;
                params.put("campaignId", getIntent().getStringExtra(PushNotificationsDetailFragment.ARG_ITEM_ID));

            }
            else {
                fromNotifFlag = true;
                params.put("campaignId",getIntent().getExtras().getString("campaign_id"));
            }

            LogUtils.debug("PUSHNOTIFINTENT","CONTACT_ID: "+Preferences.getString(Config.CONTACT_ID));
            params.put("contactid", Preferences.getString(Config.CONTACT_ID));//"deviceId", Preferences.getString(Config.DEVICE_TOKEN));
            return FunctionHelper.apiCaller(PushNotificationsDetailActivity.this, params);
        }

        @Override
        protected void onPostExecute(final String responseData) {
            super.onPostExecute(responseData);

            pd.dismiss();

            if(responseData != null) {
                //LogUtils.debug("notifDetails", "responseData" + responseData);
                try {
                    JSONObject responseJsonObject = new JSONObject(responseData);
                    boolean success = responseJsonObject.getBoolean(Config.SUCCESS);
                    if (success) {
                        JSONObject data = responseJsonObject.getJSONObject(Config.DATA);
                        LogUtils.debug("notifDetails",data.toString());
                        LogUtils.debug("notifDetails",data.getString("first_viewed"));

                        Bundle arguments = new Bundle();
                        if(!fromNotifFlag) {
                            arguments.putString(PushNotificationsDetailFragment.ARG_ITEM_ID, getIntent().getStringExtra(PushNotificationsDetailFragment.ARG_ITEM_ID));
//                            if(isSuperUser)
                            TabBarActivity.decrementNotifTabBadgeCount();
                        }
                        else
                            arguments.putString(PushNotificationsDetailFragment.ARG_ITEM_ID,getIntent().getExtras().getString("campaign_id"));

                        arguments.putString(PushNotificationsDetailFragment.ARG_SUBJECT, data.getString("subject"));//getIntent().getStringExtra(PushNotificationsDetailFragment.ARG_SUBJECT));
                        arguments.putString(PushNotificationsDetailFragment.ARG_MESSAGE, data.getString("message"));//getIntent().getStringExtra(PushNotificationsDetailFragment.ARG_MESSAGE));
                        arguments.putString(PushNotificationsDetailFragment.ARG_DATE, data.getString("schedule_datetime"));//getIntent().getStringExtra(PushNotificationsDetailFragment.ARG_DATE));
                        arguments.putString(PushNotificationsDetailFragment.ARG_FIRST_VIEWED, data.getString("first_viewed"));
                        PushNotificationsDetailFragment fragment = new PushNotificationsDetailFragment();
                        fragment.setArguments(arguments);
                        getSupportFragmentManager().beginTransaction()
                                .add(R.id.pushnotifications_detail_container, fragment)
                                .commit();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        protected void onCancelled() {
//            super.onCancelled();
        }
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
            //navigateUpTo(new Intent(this, PushNotificationsListActivity.class));
//            NavUtils.navigateUpFromSameTask(this);
            goBack();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
