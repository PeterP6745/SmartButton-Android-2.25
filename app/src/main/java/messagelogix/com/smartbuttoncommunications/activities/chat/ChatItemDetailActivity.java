package messagelogix.com.smartbuttoncommunications.activities.chat;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;

import android.widget.Toast;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.util.ArrayList;

import java.util.HashMap;


import messagelogix.com.smartbuttoncommunications.R;

import messagelogix.com.smartbuttoncommunications.utils.Config;
import messagelogix.com.smartbuttoncommunications.utils.FunctionHelper;
import messagelogix.com.smartbuttoncommunications.utils.LanguageManager;
import messagelogix.com.smartbuttoncommunications.utils.LogUtils;
import messagelogix.com.smartbuttoncommunications.utils.Preferences;
import messagelogix.com.smartbuttoncommunications.model.ChatMessage;
import messagelogix.com.smartbuttoncommunications.utils.Strings;

/**
 * Created by Vahid
 * An activity representing a single ChatItem detail screen. This
 * activity is only used narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link ChatItemListActivity}.
 */
public class ChatItemDetailActivity extends AppCompatActivity {
    private Context context = this;
    private EditText messageET;
    private ListView messageContainer;
    private Button sendBtn;
    private ChatAdapter adapter;
   // private ArrayList<messagelogix.com.smartbutton.model.ChatMessage> chatHistory;

    private String chatId;
    private String chatName;
    private String receiverId;
    private String receiverName;
    private String messageText;
    private String navTitle;
    private static final String LOG_TAG = ChatItemDetailActivity.class.getSimpleName();

    private ProgressDialog progress;

    private JSONArray listofChatMessages;
    private Boolean chatViewFirstOpened;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatitem_detail);

        navTitle="";
        Preferences.init(this);
        LanguageManager.setLocale(this,Preferences.getString(Config.LANGUAGE));

        //Loading Indicator
        progress = new ProgressDialog(this);
//        progress.setMessage("Getting messages...");
//        progress.setIndeterminate(false);
//        progress.setCancelable(false);
//        progress.show();

//        Bundle mExtras = getIntent().getExtras();
//        if (mExtras != null) {
//            chatName = mExtras.getString(Config.MESSAGE_RECEIVER_NAME);
//            receiverId = mExtras.getString(Config.MESSAGE_RECEIVER_ID);
//        }
        chatId=Config.MUTABLE_CHAT_ID;
        receiverName= Config.MUTABLE_RECEIVER_NAME;
        LogUtils.debug("CHATDETAILSCREEN","receiverName in chatdetailactivitys: "+receiverName);
       // Toast.makeText(getApplicationContext(), "First Conversation Id: "+chatId, Toast.LENGTH_SHORT).show();

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null)
            {
                if(Config.MUTABLE_CHAT_ID.equals("0")) {
                    chatId = "0";
                    chatName = "";
                    setTitle("New Conversation");
                    LogUtils.debug("chatroomtest", "error=null nested if ");
                    //receiverId = "";
                }

                else {
                    chatId = Config.MUTABLE_CHAT_ID;
                    LogUtils.debug("chatroomtest", "error=null nested else statement");
                    navTitle=setTitleReceiver();
                    setTitle(navTitle);
                }
            }

            else {
                chatId= extras.getString(Config.CONVERSATION_ID);
                chatName = extras.getString(Config.CONVERSATION_FNAME);
                receiverName = extras.getString(Config.CONVERSATION_RECEIVER_NAME);
                navTitle = setTitleReceiver();
                setTitle(navTitle);
        //        receiverId = extras.getString(Config.MESSAGE_RECEIVER_ID);

            }
        } else {
            chatId= (String) savedInstanceState.getSerializable(Config.CONVERSATION_ID);
            chatName= (String) savedInstanceState.getSerializable(Config.CONVERSATION_FNAME);
            receiverName = (String) savedInstanceState.getSerializable(Config.CONVERSATION_RECEIVER_NAME);
            LogUtils.debug("chatroomtest", "Inside else with serializable " + receiverName);
            setTitle(receiverName);
            //chatName= (String) savedInstanceState.getSerializable(Config.MESSAGE_RECEIVER_ID);
        }

        initControls();

        //chatViewFirstOpened = true;
        new GetConversationDetails().execute();

    }

    //sets the nav title to the receiver name or the chat id
    public String setTitleReceiver (){
        if(receiverName == null || receiverName.equals("null"))
            navTitle = getString(R.string.chatlist_chattitlenull) + chatId;
        else
            navTitle = receiverName;

        LogUtils.debug("ChatItemDetailActivity","setTitleReceiver() - receiverName is: "+receiverName);
        LogUtils.debug("ChatItemDetailActivity","setTitleReceiver() - navTitle is: "+navTitle);

        return navTitle;
    }

    //register your activity onResume()
    @Override
    public void onResume() {
        super.onResume();
        //chatViewFirstOpened = false;
        context.registerReceiver(mMessageReceiver, new IntentFilter("reloadChatList"));
    }

    //Must unregister onPause()
    @Override
    protected void onPause() {
        super.onPause();
        context.unregisterReceiver(mMessageReceiver);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds options to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_info, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        menu.clear();
        getMenuInflater().inflate(R.menu.menu_info, menu);
        return super.onPrepareOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_show_info:

                Intent intent = new Intent(this, ChatMembersActivity.class);
                intent.putExtra(Config.CONVERSATION_ID,chatId);
           //     intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
        // Extract data included in the Intent
        String message = intent.getStringExtra("message");
        progress.setMessage(context.getResources().getString(R.string.chatitemdetail_progressdialog_mess2));
        progress.show();
        new GetConversationDetails().execute();
        }
    };

    private void initControls(){
        messageContainer = (ListView) findViewById(R.id.messagesContainer);
        messageET = (EditText) findViewById((R.id.messageEdit));
        sendBtn = (Button) findViewById(R.id.chatSendButton);

     //   TextView meLabel = (TextView) findViewById(R.id.meLbl);
     //   TextView companionLabel = (TextView) findViewById(R.id.friendLabel);
        RelativeLayout container = (RelativeLayout) findViewById(R.id.container);
     //   companionLabel.setText("My Buddy");
     //   loadDummyHistory();

        sendBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
               messageText = messageET.getText().toString();
                if (TextUtils.isEmpty(messageText)){
                    return;
                }
                progress.setMessage(context.getResources().getString(R.string.chatitemdetail_progressdialog_mess1));
                progress.show();
                new SendChatMessage().execute();
                setTitle(navTitle);
            //    new PushMessage().execute();

 //              messagelogix.com.smartbutton.activities.chat.ChatMessage chatMessage = new messagelogix.com.smartbutton.activities.chat.ChatMessage();
//                chatMessage.setId(122);//dummy id
//                chatMessage.setMessage(messageText);
//                chatMessage.setDate(DateFormat.getDateTimeInstance().format(new Date()));
//                chatMessage.setMe(true);
//                messageET.setText("");

//                displayMessage(chatMessage);

            }
        });
    }

    public void displayMessage(messagelogix.com.smartbuttoncommunications.model.ChatMessage message){
        adapter.add(message);
        adapter.notifyDataSetChanged();
        scroll();
    }

    private void scroll(){
        messageContainer.setSelection(messageContainer.getCount()-1);
    }

//    void updateBadgeCount(JSONArray prevListOfChatMessages, JSONArray currListOfChatMessages, Boolean chatViewFirstOpened) {
//
//    }

    //Load Dummy Data into list

    //Asynchronous tasks
    private class GetConversationDetails extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voidParams) {

            HashMap<String, String> params = new HashMap<>();
//            params.put("controller", "Message");
            params.put("controller", "GrayBoar");
            params.put("action", "GetMessageDetails");
            params.put("chat_id",chatId);
            params.put("receiver_id", Preferences.getString(Config.CONTACT_ID));
//            Log.d(LOG_TAG,"controller: GrayBoar, action: GetMessageSummary, and account id: "+Preferences.getString(Config.CONTACT_ID));
            return FunctionHelper.apiCaller(context, params);
        }

        @Override
        protected void onPostExecute(String responseData) {

            if (responseData != null) {
                //Log.d(LOG_TAG, "json = " + responseData);
                boolean success = false;
                try {
                    JSONObject jsonobject = new JSONObject(responseData);
//                    Log.d(LOG_TAG,"JSON data: "+responseData);
                    success = jsonobject.getBoolean("success");
//                    Spinner titleSpinner = (Spinner) findViewById(R.id.TitleSpinner);
                    if (success) {
                        // Locate the NodeList name
                        JSONArray jsonarray = jsonobject.getJSONArray("data");
//                        Log.d(LOG_TAG, "Json Array: "+jsonarray);
                        //updateBadgeCount(listofChatMessages, jsonarray, chatViewFirstOpened);

                        adapter = new ChatAdapter (ChatItemDetailActivity.this, new ArrayList<messagelogix.com.smartbuttoncommunications.model.ChatMessage>());
                        messageContainer.setAdapter(adapter);

                        for (int i = 0; i < jsonarray.length(); i++) {
                            jsonobject = jsonarray.getJSONObject(i);
   //                         String id = jsonobject.getString("attatchment");
                            String timeStamp = jsonobject.getString("timestamp");
                            String messageId = jsonobject.getString("id");
                            String fName = jsonobject.getString("fname");
                            String message = jsonobject.getString("message");
                            String senderId = jsonobject.getString("from_id");
                            String viewed = jsonobject.getString("viewed");
                            receiverId = jsonobject.getString("to_id");

                            //put json objects into "ChatMessage" object
                            ChatMessage chatItems = new ChatMessage();
                            chatItems.setId(Long.parseLong(messageId));
                            chatItems.setMessage(message);
                            chatItems.setDate(fName);
                           if(senderId.equals(Preferences.getString(Config.CONTACT_ID)))
                            {
                                navTitle = setTitleReceiver();
                                setTitle(navTitle);
                                chatItems.setMe(false); //actually true in nature
                            }
                            else {

                               setTitle(fName);
                               chatItems.setMe(true);
                           }

                            displayMessage(chatItems);
                            progress.dismiss();

                        }
                    }
                } catch (JSONException e) {
                    Log.e("Error", e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }

    private class SendChatMessage extends AsyncTask<Void,Void,String> {

        @Override
        protected String doInBackground(Void... voidParams) {

            HashMap<String, String> params = new HashMap<>();
//            params.put("controller", "Message");
            params.put("controller", "GrayBoar");
            params.put("action", "SendMessage");
            params.put("acct_id", Preferences.getString(Config.ACCOUNT_ID));
            params.put("chat_id", chatId);
            params.put("contact_id", receiverId);
            params.put("message", messageText);
            params.put("sender_id", Preferences.getString(Config.CONTACT_ID));
//            Log.d(LOG_TAG,"controller: GrayBoar, action: GetMessageSummary, and account id: "+Preferences.getString(Config.CONTACT_ID));
            return FunctionHelper.apiCaller(context, params);
        }

        @Override
        protected void onPostExecute(final String responseData) {

            if (!Strings.isNullOrEmpty(responseData)) {
                try {
                    JSONObject responseJsonObject = new JSONObject(responseData);
                    boolean success = responseJsonObject.getBoolean("success");
                    if (success) {
                        ChatMessage mMessage = new ChatMessage();
                        mMessage.setMessage(messageText);
                        mMessage.setMe(false);

                        displayMessage(mMessage);

                        messageET.setText("");
                        new PushMessage().execute();
                    } else {

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private class PushMessage extends AsyncTask<Void,Void,String> {

        @Override
        protected String doInBackground(Void... voidParams) {

            HashMap<String, String> params = new HashMap<>();
//            params.put("controller", "Message");
            params.put("controller", "BlackTiger");
            params.put("action", "SendPushAlertNew");
            params.put("acct_id", Preferences.getString(Config.ACCOUNT_ID));
            params.put("chat_id", chatId);
            params.put("contact_id",Preferences.getString(Config.CONTACT_ID));
            params.put("message", messageText);
//            Log.d(LOG_TAG,"controller: GrayBoar, action: GetMessageSummary, and account id: "+Preferences.getString(Config.CONTACT_ID));
            return FunctionHelper.apiCaller(context, params);
        }

        @Override
        protected void onPostExecute(final String responseData) {

            LogUtils.debug(LOG_TAG, "onPostExecute response data: " + responseData);

            if (responseData != null)
            {
                if(responseData.contains("{\"data\":true,\"success\":true}")){
                    progress.dismiss();
                    Toast.makeText(getApplicationContext(), context.getResources().getString(R.string.chatitemdetail_successmess1), Toast.LENGTH_SHORT).show();
                }

                else {
                    progress.dismiss();
                    Toast.makeText(getApplicationContext(), context.getResources().getString(R.string.generic_errormess1), Toast.LENGTH_SHORT).show();
                }

            }


        }
    }

//    @Override
//    public void onBackPressed() {
//        LogUtils.debug("onBackPressed","closing ChatItemDetailActivity");
//    }
}
