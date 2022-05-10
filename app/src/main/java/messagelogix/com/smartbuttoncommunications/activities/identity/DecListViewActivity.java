package messagelogix.com.smartbuttoncommunications.activities.identity;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.List;

import messagelogix.com.smartbuttoncommunications.R;
import messagelogix.com.smartbuttoncommunications.adaptors.DecListAdaptor;
import messagelogix.com.smartbuttoncommunications.model.Contact;
import messagelogix.com.smartbuttoncommunications.model.ContactDefault;
import messagelogix.com.smartbuttoncommunications.utils.Config;
import messagelogix.com.smartbuttoncommunications.utils.FunctionHelper;
import messagelogix.com.smartbuttoncommunications.utils.Preferences;


public class DecListViewActivity extends AppCompatActivity {

    //Dummy Data

   List<Contact> defaultContacts;

    DecListAdaptor adapter;
    ListView decListView;

    Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dec_list);
//        Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);


        //Initiate Listview
        //Give Listview an Adapter
        //Give Adapter Dummy Data

//        defaultContacts = new ArrayList<DecModel>();
//        defaultContacts.add(new DecModel("Peter", "Spiderman", "Marvel"));
//        defaultContacts.add(new DecModel("Clark", "Superman", "DC"));
//        defaultContacts.add(new DecModel("Tony", "Ironman", "Marvel"));
//        defaultContacts.add(new DecModel("Steve", "Captain America", "Marvel"));

        new GetDefaultContacts().execute();
        decListView = findViewById(R.id.dec_listview);

    }




    private class GetDefaultContacts extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voidParams) {

            HashMap<String, String> params = new HashMap<>();
//            params.put("controller", "Locator");
            params.put("controller", "WhiteZebra");
            params.put("action", "GetDefaultContacts");
            params.put("accountId", Preferences.getString(Config.ACCOUNT_ID));
            params.put("schoolId", Preferences.getString(Config.USER_BUILDING_ID));
            return FunctionHelper.apiCaller(context, params);
        }

        @Override
        protected void onPostExecute(String responseData) {

            if (responseData != null) {
                //Log.d(LOG_TAG, "json = " + responseData);
                ContactDefault defaults = new Gson().fromJson(responseData, ContactDefault.class);
                if (defaults.getSuccess()) {
                    Log.d("", responseData);
                    defaultContacts = defaults.getData();
                    for (Contact c : defaultContacts) {
                        Log.d("", "c.title = " + c.getTitle());
                    }
                    //Log.d(LOG_TAG, "Default contacts: " + defaultContacts.toString());
                    DecListAdaptor defaultAdapter = new DecListAdaptor(context, defaultContacts);
                    //Log.d(LOG_TAG, "count = " + defaultAdapter.getCount());
                    decListView.setAdapter(defaultAdapter);
                    //Set default contacts list size, size of one is 70 dp
                    ViewGroup.LayoutParams defaultParams = decListView.getLayoutParams();
                    defaultParams.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 71 * defaultAdapter.getCount(), getResources().getDisplayMetrics());
                    decListView.setLayoutParams(defaultParams);
                    decListView.requestLayout();

                    adapter = new DecListAdaptor(context, defaultContacts);
                    decListView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();


                }
            }
            
        }
    }


}

