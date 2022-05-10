package messagelogix.com.smartbuttoncommunications.activities.chat;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import messagelogix.com.smartbuttoncommunications.R;
import messagelogix.com.smartbuttoncommunications.activities.core.TabBarActivity;
import messagelogix.com.smartbuttoncommunications.utils.Config;
import messagelogix.com.smartbuttoncommunications.utils.FunctionHelper;
import messagelogix.com.smartbuttoncommunications.utils.Preferences;

/**
 * Created by Vahid
 * This activity is related to creating a new conversation
 */
public class CreateConversationActivity extends AppCompatActivity {

    private ProgressDialog progress;

    //Se the log tag
    private static final String LOG_TAG = CreateConversationActivity.class.getSimpleName();

    //Set the context
    private Context context;

    //Edit text
    EditText messageEditText;

    //Receiver of the message
    String id = null;
    String chatId;
    String name = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setTitle(this.getResources().getString(R.string.title_activity_create_conversation));
        progress = new ProgressDialog(this);

        setContentView(R.layout.activity_create_conversation);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //Init the preferences
        Preferences.init(this);
        //Set keyboard dismissal
        setTouchListenerForKeyboardDismissal();
        //Ge the extras
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            name = extras.getString(Config.MESSAGE_RECEIVER_NAME);
            id = extras.getString(Config.MESSAGE_RECEIVER_ID);
            Log.d(LOG_TAG, "name: " + name + " id: " + id);
            // and get whatever type user account id is
        }
        //Initialize the context
        context = this;
        //Setup the controls
        messageEditText = (EditText) findViewById(R.id.editText);
        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        final String convoMess1 = this.getResources().getString(R.string.createconvo_successmess1);
        final String convoMess2 = this.getResources().getString(R.string.createconvo_errormess1);
        //Setup the button
        Button sendButton = (Button) findViewById(R.id.btn_send);
        assert sendButton != null;
        sendButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(!messageEditText.getText().toString().equals("") && messageEditText!=null){
                    progress.setMessage(convoMess1);
                    progress.setIndeterminate(false);
                    progress.setCancelable(false);
                    progress.show();
                    new SetMessage().execute();
                }
                else{
                    Toast.makeText(getApplicationContext(), convoMess2, Toast.LENGTH_SHORT).show();
                }
            }
        });
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fabCreate);
//        fab.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View view) {
//
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
    }

    private String trimResponseData(String dataToTrim){
        StringBuilder trimData = new StringBuilder(dataToTrim);
        trimData.delete(1,19);
        trimData.deleteCharAt(0);

        int charsToDeleteStart = trimData.indexOf(",");
        int charsToDeleteEnd = trimData.lastIndexOf("\"success\":true");

        trimData.delete(charsToDeleteStart,charsToDeleteEnd);
        dataToTrim = trimData.toString();

        int stringCount = dataToTrim.length();
        trimData.delete(stringCount - 15, stringCount);
        trimData.deleteCharAt(0);
        dataToTrim = trimData.toString();
        stringCount = dataToTrim.length();
        trimData.deleteCharAt(stringCount-1);

        dataToTrim= trimData.toString();

        Log.d(LOG_TAG, "chat id: " +dataToTrim);

        return dataToTrim;
    }


    //Go to the main conversations
    private void goBack() {
        //Create the intent
        Intent intent = new Intent(context, TabBarActivity.class);
       // intent.putExtra("chatId",chatId);
        Preferences.putString(Config.CURRENT_TAB, "4");
        Preferences.putBoolean(Config.FLAG, true);
//        intent.putExtra(Config.CURRENT_TAB, 4);
        context.startActivity(intent);
    }

    private void setTouchListenerForKeyboardDismissal() {

        RelativeLayout layout = (RelativeLayout) findViewById(R.id.content_create_conversation);
        layout.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View view, MotionEvent motion) {
                hideKeyboard();
                return false;
            }
        });
    }

    private void hideKeyboard() {
        // Check if no view has focus:
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void pushTheMessage(){

        progress.setMessage(this.getResources().getString(R.string.createconvo_progressdialog_mess1));
        new PushMessage().execute();
    }

    //Async task
    /*
    Send message:
    controller=Message
    action=SendMessage
    -------
    params:
    acct_id
    chat_id
    contact_id
    message
    sender_id
     */
    private class SetMessage extends AsyncTask<Void, Void, String> {

        String message = "";

        @Override
        protected void onPreExecute() {

            super.onPreExecute();


            //Load the message
            message = messageEditText.getText().toString();
        }

        @Override
        protected String doInBackground(Void... voidParams) {

            HashMap<String, String> params = new HashMap<>();
//            params.put("controller", "Message");
            params.put("controller", "GrayBoar");
            params.put("action", "SendMessage");
            params.put("acct_id", Preferences.getString(Config.ACCOUNT_ID));
            params.put("chat_id", "0");
            //Receiver
            params.put("contact_id", id);
            //The message
            params.put("message", message);
            //Me as sender
            params.put("sender_id", Preferences.getString(Config.CONTACT_ID));
            return FunctionHelper.apiCaller(context, params);
        }

        @Override
        protected void onPostExecute(String responseData) {

            Log.d(LOG_TAG, "onPostExecute: " + responseData);
            //progress.dismiss();
            //       adapter.clearData(); //Clear Data so there are no Duplicate Items
            if (responseData != null) {
                //Log.d(LOG_TAG, "json = " + responseData);
                boolean success;
                try {
                    JSONObject jsonobject = new JSONObject(responseData);
                //    Log.d(LOG_TAG,"JSON data: "+responseData);
                    success = jsonobject.getBoolean("success");
//                    Spinner titleSpinner = (Spinner) findViewById(R.id.TitleSpinner);
                    if (success) {
                //Trim Response Data to obtain correct chat_id value!
                        responseData= trimResponseData(responseData);
                        chatId = responseData;
                      //  Log.d(LOG_TAG, "chat id: " +chatId);

                       // progress.dismiss();
                        pushTheMessage();
                        //goBack();

                    }
                } catch (JSONException e) {
                    Log.e("Error", e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }



    private class PushMessage extends AsyncTask<Void,Void,String> {
        String message = messageEditText.getText().toString();
        @Override
        protected String doInBackground(Void... voidParams) {
            HashMap<String, String> params = new HashMap<>();
//            params.put("controller", "Message");
            params.put("controller", "BlackTiger");
            params.put("action", "SendPushAlertNew");
            params.put("acct_id", Preferences.getString(Config.ACCOUNT_ID));
            params.put("chat_id", chatId);
            params.put("contact_id",Preferences.getString(Config.CONTACT_ID));
            params.put("message", message);
//            Log.d(LOG_TAG,"controller: GrayBoar, action: GetMessageSummary, and account id: "+Preferences.getString(Config.CONTACT_ID));
            return FunctionHelper.apiCaller(context, params);
        }

        @Override
        protected void onPostExecute(final String responseData) {

            Log.d(LOG_TAG, "onPostExecute response data: " + responseData);

            if (responseData != null)
            {
                if(responseData.contains("{\"data\":true,\"success\":true}")){

                    progress.dismiss();

                    Toast.makeText(getApplicationContext(), context.getResources().getString(R.string.createconvo_successmess2), Toast.LENGTH_SHORT).show();

                    goBack();

                }

                else {
                    progress.dismiss();
                    Toast.makeText(getApplicationContext(), context.getResources().getString(R.string.generic_errormess1), Toast.LENGTH_SHORT).show();
                }

            }


        }
    }
}
