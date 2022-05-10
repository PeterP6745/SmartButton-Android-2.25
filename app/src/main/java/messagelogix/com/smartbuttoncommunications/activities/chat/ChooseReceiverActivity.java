package messagelogix.com.smartbuttoncommunications.activities.chat;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import messagelogix.com.smartbuttoncommunications.R;
import messagelogix.com.smartbuttoncommunications.model.Contact;
import messagelogix.com.smartbuttoncommunications.model.ContactDefault;
import messagelogix.com.smartbuttoncommunications.utils.Config;
import messagelogix.com.smartbuttoncommunications.utils.FunctionHelper;
import messagelogix.com.smartbuttoncommunications.utils.Preferences;

/**
 * Created by Vahid
 * This is the activity for showing received messages
 */
public class ChooseReceiverActivity extends AppCompatActivity {

    private static final String LOG_TAG = ChooseReceiverActivity.class.getSimpleName();

    //The context
    private Context context;

    //The spinner HashMap
    private HashMap<String, String> hm = null;

    //The controls
    Spinner default_spinner = null;

    Button choose_default = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_receiver);
        setTitle(this.getResources().getString(R.string.chooserec_title));
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //Initialize the context
        context = this;
        //Initialize the HashMap
        hm = new HashMap<>();
        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        //Initialize the controls
        default_spinner = (Spinner) findViewById(R.id.default_contact_spinner);
        choose_default = (Button) findViewById(R.id.btn_choose_default);
//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
        //Set click events
        assert choose_default != null;
        choose_default.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                final int ID = 0;
                final int NAME = 1;

                Log.d(LOG_TAG, default_spinner.getSelectedItem().toString() + " contact id: " + hm.get(default_spinner.getSelectedItem().toString()));
                //Creat the intent
                Intent intent = new Intent(context, CreateConversationActivity.class);
             //  Intent intent = new Intent(context, ChatItemDetailActivity.class);
             //   String receiver_id =tr imString(hm.get(default_spinner.getSelectedItem().toString()),ID);
                String receiver_title_for_id = trimString(default_spinner.getSelectedItem().toString(),ID);
                String receiver_name = trimString(default_spinner.getSelectedItem().toString(),NAME);
                intent.putExtra(Config.MESSAGE_RECEIVER_NAME, receiver_name);
                intent.putExtra(Config.MESSAGE_RECEIVER_ID, hm.get(receiver_title_for_id));
                context.startActivity(intent);
            }
        });
        //Async tasks
        new GetDefaultContacts().execute();
    }

    private String trimString(String dataToTrim, int type){
        StringBuilder trimData = new StringBuilder(dataToTrim);
        Log.d(LOG_TAG, "Before Trim: " +dataToTrim);
        if(type == 0){
            int charsToDeleteStart = trimData.indexOf(":");
            int charsToDeleteEnd = trimData.length();
            trimData.delete(charsToDeleteStart,charsToDeleteEnd);

        }

        else if(type == 1){
            int charsToDeleteStart = 0;
            int charsToDeleteEnd = trimData.indexOf("- ") + 2;
            trimData.delete(charsToDeleteStart,charsToDeleteEnd);

        }


        dataToTrim= trimData.toString();

        Log.d(LOG_TAG, "Trimmed String: " +dataToTrim);

        return dataToTrim;
    }

    //Get default contacts
    private class GetDefaultContacts extends AsyncTask<Void, Void, String> {

        private List<Contact> defaultContacts = new ArrayList<>();

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
                Log.d(LOG_TAG, "json = " + responseData);
                ContactDefault defaults = new Gson().fromJson(responseData, ContactDefault.class);
                if (defaults.getSuccess()) {
                    Log.d(LOG_TAG, responseData);
                    defaultContacts = defaults.getData();
                    String[] spinnerArray = new String[defaultContacts.size()];
                    int i = 0;
                    for (Contact c : defaultContacts) {
                        Log.d(LOG_TAG, "Title = " + c.getTitle() + " contact id: " + c.get_contactId());
                        hm.put(c.getTitle(), c.get_contactId());

                        final SpannableStringBuilder strTitle = new SpannableStringBuilder(c.getTitle());
                        strTitle.setSpan(new StyleSpan(Typeface.BOLD), 0, strTitle.length(), 0);
                        spinnerArray[i] = strTitle + ":\n" + "- " + c.getName();
                        i++;
                    }
                    ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, spinnerArray); //selected item will look like a spinner set from XML
                    spinnerArrayAdapter.setDropDownViewResource(R.layout.contact_spinne_dropdownr_custom);
                    default_spinner.setAdapter(spinnerArrayAdapter);
                } else {
                    //no default
                }
            }
        }
    }
}
