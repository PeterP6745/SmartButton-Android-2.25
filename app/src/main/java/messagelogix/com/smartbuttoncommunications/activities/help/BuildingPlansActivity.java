package messagelogix.com.smartbuttoncommunications.activities.help;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

import messagelogix.com.smartbuttoncommunications.R;
import messagelogix.com.smartbuttoncommunications.model.BuildingPlans;
import messagelogix.com.smartbuttoncommunications.utils.Config;
import messagelogix.com.smartbuttoncommunications.utils.FunctionHelper;
import messagelogix.com.smartbuttoncommunications.utils.LogUtils;
import messagelogix.com.smartbuttoncommunications.utils.Preferences;

/**
 * Created by Vahid
 * This activity is for showing the building plans
 */
public class BuildingPlansActivity extends AppCompatActivity {

    private static final String LOG_TAG = BuildingPlansActivity.class.getSimpleName();

    private boolean mTwoPane;

    private Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_building_plans);
        buildActionBar();
        String schoolTypeId = getIntent().getExtras().getString("schoolTypeId");
        String title = getIntent().getExtras().getString("title");
        if (title != null) {
            setTitle(title);
        }
        if (schoolTypeId != null) {
            new GetBuildingPlansTask(schoolTypeId).execute();
        }
    }

    public void buildActionBar() {

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }
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
                super.onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView, List<BuildingPlans.HelpItem> items) {

        recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(items));
    }

    public class GetBuildingPlansTask extends AsyncTask<String, Void, String> {

        private String schoolTypeId;

        public GetBuildingPlansTask(String schoolTypeId) {

            this.schoolTypeId = schoolTypeId;
        }

        @Override
        protected void onPreExecute() {

            Preferences.init(context);
        }

        @Override
        protected String doInBackground(String... url) {

            HashMap<String, String> params = new HashMap<>();
            params.put("controller", "RedHorse");
            params.put("action", "GetBuildingPlans");
            params.put("accountId", Preferences.getString(Config.ACCOUNT_ID));
            params.put("contact_id", Preferences.getString(Config.CONTACT_ID));
            params.put("school_type", this.schoolTypeId);
            return FunctionHelper.apiCaller(context, params);
        }

        @Override
        protected void onPostExecute(final String responseData) {
            LogUtils.debug("BuildingPLansActivity", "responseData" + responseData);
            if (responseData != null) {
                Log.d(LOG_TAG, "responseData" + responseData);
                Gson gson = new GsonBuilder().create();
                BuildingPlans buildingPlans = gson.fromJson(responseData, BuildingPlans.class);
                if (buildingPlans.getSuccess()) {
                    View recyclerView = findViewById(R.id.pushnotifications_list);
                    assert recyclerView != null;
                    setupRecyclerView((RecyclerView) recyclerView, buildingPlans.getData());
                }
            } else {
                Log.d(LOG_TAG, "No JSON received ! :(");
            }
        }

        @Override
        protected void onCancelled() {

        }
    }

    public class SimpleItemRecyclerViewAdapter extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final List<BuildingPlans.HelpItem> mValues;

        public SimpleItemRecyclerViewAdapter(List<BuildingPlans.HelpItem> items) {

            mValues = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.pushnotifications_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {

            holder.mItem = mValues.get(position);
            holder.mIdView.setText(mValues.get(position).getTitle());

            holder.mView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    String type = holder.mItem.getTypeId();
                    String title = holder.mItem.getTitle();
                    String timestamp = "";
                    new LogHelpEventTask(type, title, timestamp).execute();

                    Intent intent = new Intent(v.getContext(), WebViewActivity.class);
                    intent.putExtra("url", holder.mItem.getValue());
                    intent.putExtra("title", holder.mItem.getTitle());
                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {

            return mValues.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            public final View mView;

            public final TextView mIdView;

            public final TextView mContentView;

            public BuildingPlans.HelpItem mItem;

            public ViewHolder(View view) {

                super(view);
                mView = view;
                mIdView = (TextView) view.findViewById(R.id.id);
                mContentView = (TextView) view.findViewById(R.id.content);
            }

            @Override
            public String toString() {

                return super.toString() + " '" + mContentView.getText() + "'";
            }
        }
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

}
