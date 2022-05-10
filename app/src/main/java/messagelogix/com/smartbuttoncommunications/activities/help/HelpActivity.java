package messagelogix.com.smartbuttoncommunications.activities.help;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import messagelogix.com.smartbuttoncommunications.R;
import messagelogix.com.smartbuttoncommunications.model.HelpModel;
import messagelogix.com.smartbuttoncommunications.utils.ApiHelper;
import messagelogix.com.smartbuttoncommunications.utils.Config;
import messagelogix.com.smartbuttoncommunications.utils.CustomProgressDialog;
import messagelogix.com.smartbuttoncommunications.utils.FunctionHelper;
import messagelogix.com.smartbuttoncommunications.utils.LogUtils;
import messagelogix.com.smartbuttoncommunications.utils.NetworkCheck;
import messagelogix.com.smartbuttoncommunications.utils.Preferences;
import messagelogix.com.smartbuttoncommunications.utils.RetryCounter;

/**
 * Created by Vahid
 * This is the activity that makes the help section
 */
public class HelpActivity extends AppCompatActivity {

    //    0 = Text
    //    1 = Video
    //    2 = Weblink
    //    3 = Youtube
    //    4 = Image
    //    5 = PhoneNumber
    //    6 = EmergencyProcedure
    public static final int TEXT = 0;

    public static final int VIDEO = 1;

    public static final int WEBLINK = 2;

    public static final int YOUTUBE = 3;

    public static final int PHONE = 5;

    public static final int EMERGENCY = 6;

    public static final int PLANS = 8;

    public static final int SCHOOL_TYPE = 7;

    public static final int FULL_GUIDES = 9;

    public static final int POLICY = 10;

    private ExpandableListView expandableListView;

    private Context context = this;

    private String LOG_TAG = HelpActivity.class.getSimpleName();

    private boolean callForHR = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle(R.string.help);
        setContentView(R.layout.activity_help);
        expandableListView = (ExpandableListView) findViewById(R.id.expandable_list);
        Preferences.init(this);
        //Check the network connection
        NetworkCheck networkCheck = new NetworkCheck(context);
        if (!networkCheck.isNetworkConnected()) {
            Toast.makeText(this, getString(R.string.No_Internet), Toast.LENGTH_LONG).show();
        }
        //new GetAllResourcesTask().execute();
    }

    @Override
    protected void onResume() {
        super.onResume();

//        if(callForHR) {
//            getHelpAndResources();
//            callForHR = false;
//        }
        if(callForHR) {
            getHelpResources();
            callForHR = false;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e("HelpActivity","just ran through onPause()");
    }

    @Override
    protected void onStop() {
        super.onStop();

        callForHR = true;
        Log.e("HelpActivity","just ran through onStop()");
    }

    private void getHelpResources() {
        if(Preferences.isAvailable(Config.HELPRESOURCES_LIST_LOADDATE)) {
            try {
                boolean haveXDaysElapsed = Config.calcDaysSinceListLoad(Preferences.getString(Config.HELPRESOURCES_LIST_LOADDATE),2);
                if (haveXDaysElapsed) {
                    LogUtils.debug("gethelpresources", "calling for helpresources");
                    callForHelpResources();
                }
                else {
                    LogUtils.debug("gethelpresources", "loading stored helpresources");
                    loadStoredHelpResources();
                }
            } catch(Exception e) {
                LogUtils.debug("gethelpresources", "a parse exception was thrown while determining if the load date for the title list was X days ago -> "+e);
                callForHelpResources();
            }
        } else {
            LogUtils.debug("gethelpresources", "calling for helpresources");
            callForHelpResources();
        }
    }

    private void callForHelpResources() {
        HashMap<String, String> params = new HashMap<>();
        params.put("controller", "RedHorse");
        params.put("action", "GetHelpResourcesGroupedSBv5");
        params.put("contact_id", Preferences.getString(Config.CONTACT_ID));
        params.put("accountId", Preferences.getString(Config.ACCOUNT_ID));
        params.put("language", Preferences.getString(Config.LANGUAGE));

        final ApiHelper apiHelper = new ApiHelper();

        final CustomProgressDialog progressDialog = new CustomProgressDialog(this);
        progressDialog.showDialog(getString(R.string.Loading_Resources));

        final RetryCounter retryCounter = new RetryCounter();

        apiHelper.setOnSuccessListener(
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        try {
                            LogUtils.debug("gethelpandresources","responsedata is: "+response);
                            HelpModel helpModel = new Gson().fromJson(response, HelpModel.class);
                            if (helpModel.getSuccess() != null) {
                                if (helpModel.getSuccess()) {

                                    Calendar cal = Calendar.getInstance();
                                    SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH);

                                    LogUtils.debug("gettitlesfromdefault","callfortitlelist --> storing date: "+dateFormat.format(cal.getTime()));
                                    LogUtils.debug("gettitlesfromdefault","callfortitlelist --> storing the list: "+response);
                                    Preferences.putString(Config.HELPRESOURCES_LIST_LOADDATE,dateFormat.format(cal.getTime()));
                                    Preferences.putString(Config.HELPRESOURCES_LIST,response);

                                    loadStoredHelpResources();
                                    LogUtils.debug("gethelpresources","callforhelpresources --> responsedata has a data key and success is true");
                                } else {
                                    LogUtils.debug("gethelpresources","callforhelpresources --> responsedata has a data key and success is false");
                                    showHRErrorPrompt();
                                }
                            } else {
                                LogUtils.debug("gethelpresources","callforhelpresources --> responsedata has a data key and success is null?");
                                showHRErrorPrompt();
                            }

                        } catch(Exception e) {
                            LogUtils.debug("gethelpresources","callforhelpresources --> an exception was thrown while processing the responsedata");
                            showHRErrorPrompt();
                        }
                    }
                }
        );

        apiHelper.setOnErrorListener(
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        int currRC = retryCounter.getRetryCount();//ApiHelper.getInstance(HomeActivity.this).getRetryCount();//ApiHelper.getInstance(HomeActivity.this).getRetryCount();
                        LogUtils.debug("gethelpresources","retried: " + apiHelper.getUrl() +  " backend call\n" + currRC + " times. Received an error code from the server -> "+error);

                        if(currRC == 0) {
                            progressDialog.dismiss();
                            showHRErrorPrompt();
                        }
                        else {
                            retryCounter.decrementRetryCount();
                            //LogUtils.debug("covid19surveytask","retryPolicy of temp obj: "+temp.getRetryPolicy().toString());
                            ApiHelper.getInstance(HelpActivity.this).startRequest(apiHelper);
                        }
                        LogUtils.debug("gethelpresources","an error was encountered while trying to establish a connection");
                    }
                }
        );

        apiHelper.prepareRequest(params,false);
        ApiHelper.getInstance(this).startRequest(apiHelper);
    }

    private void loadStoredHelpResources() {
        String storedHelpResources = Preferences.getString(Config.HELPRESOURCES_LIST);
        LogUtils.debug("gethelpresources","loadstoredhelpresource --> stored helpr is: "+storedHelpResources);
        try {
            HelpModel helpModel = new Gson().fromJson(storedHelpResources, HelpModel.class);

            final List<List<HelpModel.HelpItem>> data = helpModel.getData();
            expandableListView.setAdapter(new HelpExpandableAdapter(context, data));
            expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

                @Override
                public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {

                    HelpModel.HelpItem item = data.get(groupPosition).get(childPosition);
                    Integer typeId = Integer.valueOf(item.getTypeId());
                    String typeIdString = item.getTypeId();
                    String title = item.getTitle();
                    Log.d("Monke","title = " + title);
                    Log.d("typeID",typeIdString);
                    String timestamp = "";

                    if (typeId != PLANS) {
                        logHelpEvent(typeIdString, title, timestamp);
                    }

                    switch (typeId) {
                        case TEXT: {

                            Intent intent = new Intent(context, TextActivity.class);
                            intent.putExtra("value", item.getValue());
                            intent.putExtra("title", item.getTitle());
                            startActivity(intent);
                        }
                        break;
                        case VIDEO: {
                            Intent intent = new Intent(getApplicationContext(), VideoActivity.class);
                            intent.putExtra("url", item.getValue());
                            intent.putExtra("title", item.getTitle());
                            startActivity(intent);
                        }
                        break;
                        case WEBLINK: {
                            Intent intent = new Intent(getApplicationContext(), WebViewActivity.class);
                            intent.putExtra("url", item.getValue());
                            intent.putExtra("title", item.getTitle());
                            startActivity(intent);
                        }
                        break;
                        case YOUTUBE: {
                            Intent intent = new Intent(getApplicationContext(), YoutubeActivity.class);
                            intent.putExtra("url", item.getValue());
                            intent.putExtra("title", item.getTitle());
                            startActivity(intent);
                        }
                        break;
                        case POLICY: {

                            Intent intent = new Intent(getApplicationContext(), WebViewActivity.class);
                            intent.putExtra("url", item.getValue());
                            intent.putExtra("title", item.getTitle());
                            startActivity(intent);

                        }
                        break;

                        case FULL_GUIDES: {

                            Intent intent = new Intent(getApplicationContext(), WebViewActivity.class);
                            Log.d("Monke",getString(R.string.test_string));
                            intent.putExtra("url", item.getValue());
                            intent.putExtra("title", item.getTitle());
                            startActivity(intent);

                        }
                        break;
                        case PHONE: {

                            Intent intent = new Intent(context, TextActivity.class);
                            intent.putExtra("value", item.getValue());
                            intent.putExtra("title", item.getTitle());
                            startActivity(intent);

                        }
                        break;
                        case PLANS:
                        case EMERGENCY: {
                            Integer subTypeId = Integer.valueOf(item.getSubTypeId());
                            switch (subTypeId) {
                                case TEXT: {
                                    Intent intent = new Intent(context, TextActivity.class);
                                    intent.putExtra("value", item.getValue());
                                    intent.putExtra("title", item.getTitle());
                                    startActivity(intent);
                                }
                                break;
                                case VIDEO: {
                                    Intent intent = new Intent(getApplicationContext(), VideoActivity.class);
                                    intent.putExtra("url", item.getValue());
                                    intent.putExtra("title", item.getTitle());
                                    startActivity(intent);
                                }
                                break;
                                case WEBLINK: {
                                    Intent intent = new Intent(getApplicationContext(), WebViewActivity.class);
                                    intent.putExtra("url", item.getValue());
                                    intent.putExtra("title", item.getTitle());
                                    startActivity(intent);
                                }
                                break;
                                case YOUTUBE: {
                                    Intent intent = new Intent(getApplicationContext(), YoutubeActivity.class);
                                    intent.putExtra("url", item.getValue());
                                    intent.putExtra("title", item.getTitle());
                                    startActivity(intent);
                                }
                                break;
                                case SCHOOL_TYPE: {
                                    Intent intent = new Intent(getApplicationContext(), BuildingPlansActivity.class);
                                    intent.putExtra("schoolTypeId", item.getSchoolTypeId());
                                    intent.putExtra("title", item.getTitle());
                                    startActivity(intent);
                                }
                                default:
                                    break;
                            }
                        }
                        default:
                            break;
                    }
                    return true;
                }
            });
            LogUtils.debug("gethelpresources","loadstoredhr --> storedhr was processed correctly");
        } catch (Exception e) {
            LogUtils.debug("gethelpresources","loadstoredhr --> an exception was thrown while processing the storedhr");
            showHRErrorPrompt();
        }
    }

    private void showHRErrorPrompt() {
        AlertDialog.Builder builder = new AlertDialog.Builder(HelpActivity.this);
        builder.setTitle("ERROR");
        builder.setIcon(R.drawable.warning);
        builder.setMessage(getString(R.string.error_message));
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        Dialog dialog = builder.create();
        dialog.show();
    }

    private class GetAllResourcesTask extends AsyncTask<Void, Void, String> {

        private ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
            // show progress
            pDialog = new ProgressDialog(context);
            pDialog.setMessage(getString(R.string.Please_Wait));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(Void... voidParams) {

            NetworkCheck networkCheck = new NetworkCheck(context);
            if (!networkCheck.isNetworkConnected()) {
                if (Preferences.getString(Config.HELP_CACHE) != null) {
                    return Preferences.getString(Config.HELP_CACHE);
                } else {
                    return "";
                }
            } else {
                HashMap<String, String> params = new HashMap<>();
                params.put("controller", "RedHorse");
                params.put("action", "GetHelpResourcesGroupedSBv4");
                params.put("contact_id", Preferences.getString(Config.CONTACT_ID));
                params.put("accountId", Preferences.getString(Config.ACCOUNT_ID));
                String cache = FunctionHelper.apiCaller(context, params);
                assert cache != null;
                Preferences.putString(Config.HELP_CACHE, cache);
                return cache;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            LogUtils.debug("testing","getallresource");

            if (result != null) {
                HelpModel helpModel = new Gson().fromJson(result, HelpModel.class);
                if (helpModel.getSuccess() != null) {
                    if (helpModel.getSuccess()) {
                        Log.d(LOG_TAG, result);
                        final List<List<HelpModel.HelpItem>> data = helpModel.getData();
                        expandableListView.setAdapter(new HelpExpandableAdapter(context, data));
                        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

                            @Override
                            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {

                                HelpModel.HelpItem item = data.get(groupPosition).get(childPosition);
                            //    Log.d(LOG_TAG, "type id = " + item.getSubTypeId());
                                Integer typeId = Integer.valueOf(item.getTypeId());
                                String typeIdString = item.getTypeId();
                                String title = item.getTitle();
                                Log.d("Monke","Title is: " + title);
                                String timestamp = "";

                                if(typeId!= PLANS )
                                {
                                    new LogHelpEventTask(typeIdString, title, timestamp).execute();
                                }

                                switch (typeId) {
                                    case TEXT: {
                                        Intent intent = new Intent(context, TextActivity.class);
                                        intent.putExtra("value", item.getValue());
                                        intent.putExtra("title", item.getTitle());
                                        startActivity(intent);
                                    }
                                    break;
                                    case VIDEO: {
                                        Intent intent = new Intent(getApplicationContext(), VideoActivity.class);
                                        intent.putExtra("url", item.getValue());
                                        intent.putExtra("title", item.getTitle());
                                        startActivity(intent);
                                    }
                                    break;
                                    case WEBLINK: {
                                        Intent intent = new Intent(getApplicationContext(), WebViewActivity.class);
                                        intent.putExtra("url", item.getValue());
                                        intent.putExtra("title", item.getTitle());
                                        startActivity(intent);
                                    }
                                    break;
                                    case YOUTUBE: {
                                        Intent intent = new Intent(getApplicationContext(), YoutubeActivity.class);
                                        intent.putExtra("url", item.getValue());
                                        intent.putExtra("title", item.getTitle());
                                        startActivity(intent);
                                    }
                                    break;
                                    case POLICY:{

                                        Intent intent = new Intent(getApplicationContext(), WebViewActivity.class);
                                        intent.putExtra("url", item.getValue());
                                        intent.putExtra("title", item.getTitle());
                                        startActivity(intent);

                                    }
                                    break;

                                    case FULL_GUIDES:{

                                        Intent intent = new Intent(getApplicationContext(), WebViewActivity.class);
                                        intent.putExtra("url", item.getValue());
                                        intent.putExtra("title", item.getTitle());
                                        startActivity(intent);

                                    }
                                    break;
                                    case PHONE:{

                                        Intent intent = new Intent(context, TextActivity.class);
                                        intent.putExtra("value", item.getValue());
                                        intent.putExtra("title", item.getTitle());
                                        startActivity(intent);

                                    }
                                    break;
                                    case PLANS:
                                    case EMERGENCY: {
                                        Integer subTypeId = Integer.valueOf(item.getSubTypeId());
                                        switch (subTypeId) {
                                            case TEXT: {
                                                Intent intent = new Intent(context, TextActivity.class);
                                                intent.putExtra("value", item.getValue());
                                                intent.putExtra("title", item.getTitle());
                                                startActivity(intent);
                                            }
                                            break;
                                            case VIDEO: {
                                                Intent intent = new Intent(getApplicationContext(), VideoActivity.class);
                                                intent.putExtra("url", item.getValue());
                                                intent.putExtra("title", item.getTitle());
                                                startActivity(intent);
                                            }
                                            break;
                                            case WEBLINK: {
                                                Intent intent = new Intent(getApplicationContext(), WebViewActivity.class);
                                                intent.putExtra("url", item.getValue());
                                                intent.putExtra("title", item.getTitle());
                                                startActivity(intent);
                                            }
                                            break;
                                            case YOUTUBE: {
                                                Intent intent = new Intent(getApplicationContext(), YoutubeActivity.class);
                                                intent.putExtra("url", item.getValue());
                                                intent.putExtra("title", item.getTitle());
                                                startActivity(intent);
                                            }
                                            break;
                                            case SCHOOL_TYPE: {
                                                Intent intent = new Intent(getApplicationContext(), BuildingPlansActivity.class);
                                                intent.putExtra("schoolTypeId", item.getSchoolTypeId());
                                                intent.putExtra("title", item.getTitle());
                                                startActivity(intent);
                                            }
                                            default:
                                                break;
                                        }
                                    }
                                    default:
                                        break;
                                }
                                return true;
                            }
                        });
                    }
                }
            }
            pDialog.cancel();
        }
    }

    private void logHelpEvent(String typeId, String itemTitle, String timestamp) {
        String type = typeId;
        String title = itemTitle;
        String time = timestamp;

        final HashMap<String, String> params = new HashMap<>();
        params.put("controller", "RedHorse");
        params.put("action", "LogHelpEvent");
        params.put("contact_id", Preferences.getString(Config.CONTACT_ID));
        params.put("accountId", Preferences.getString(Config.ACCOUNT_ID));
        params.put("help_type", type);
        params.put("help_title", title);
        params.put("timestamp", time);

        final ApiHelper apiHelper = new ApiHelper();

        apiHelper.setOnSuccessListener(
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject responseJsonObject = new JSONObject(response);
                            LogUtils.debug("loghelpeventinHR","responsedata is: "+response);

                            boolean success = responseJsonObject.getBoolean(Config.SUCCESS);
                            if (success) {
                                LogUtils.debug("loghelpeventinHR","responsedata has a data key and success is true");
                            } else {
                                LogUtils.debug("loghelpeventinHR","responsedata has a data key and success is false");
                            }

                        } catch (Exception e) {
                            LogUtils.debug("loghelpeventinHR","an exception was thrown while processing the response data");
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
                        LogUtils.debug("loghelpeventinHR","an error was encountered while trying to establish a connection");
                    }
                }
        );

        apiHelper.prepareRequest(params,false);
        ApiHelper.getInstance(this).startRequest(apiHelper);
    }

    private class LogHelpEventTask extends AsyncTask<Void, Void, String> {

        private ProgressDialog pDialog;
        private String type;
        private String title;
        private String time;

        private LogHelpEventTask(String typeId, String item_title, String timestamp){
            type = typeId;
            title = item_title;
            time = timestamp;
        }

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
            // show progress
//            pDialog = new ProgressDialog(context);
//            pDialog.setMessage("Please wait...");
//            pDialog.setIndeterminate(false);
//            pDialog.setCancelable(false);
//            pDialog.show();
        }

        @Override
        protected String doInBackground(Void... voidParams) {


                HashMap<String, String> params = new HashMap<>();
                params.put("controller", "RedHorse");
                params.put("action", "LogHelpEvent");
                params.put("contact_id", Preferences.getString(Config.CONTACT_ID));
                params.put("accountId", Preferences.getString(Config.ACCOUNT_ID));
                params.put("help_type", type);
                params.put("help_title", title);
                params.put("timestamp", time);

                return FunctionHelper.apiCaller(context, params);

        }

        @Override
        protected void onPostExecute(final String responseData) {
           // Log.d(LOG_TAG,"!!!!!!!" + responseData);
            if (responseData != null) {
                try {
                    JSONObject responseJsonObject = new JSONObject(responseData);
                    boolean success = responseJsonObject.getBoolean(Config.SUCCESS);
                    if (success) {
                        Log.d(LOG_TAG, "successful");
                    } else {
                        Log.d(LOG_TAG, "not successful");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Log.d(LOG_TAG, "No JSON received ! :(");
            }
        }
    }

    @Override
    public void onBackPressed() {
        LogUtils.debug("backPressed","Help&Resources Activity - back button pressed, method overridden so does nothing");;
    }
}
