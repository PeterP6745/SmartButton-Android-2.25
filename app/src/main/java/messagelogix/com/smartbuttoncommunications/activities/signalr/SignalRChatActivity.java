package messagelogix.com.smartbuttoncommunications.activities.signalr;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import messagelogix.com.smartbuttoncommunications.R;
import messagelogix.com.smartbuttoncommunications.activities.chat.ChatAdapter;
import messagelogix.com.smartbuttoncommunications.model.ChatMessage;
import messagelogix.com.smartbuttoncommunications.utils.Config;
import messagelogix.com.smartbuttoncommunications.utils.FunctionHelper;
import messagelogix.com.smartbuttoncommunications.utils.Preferences;
import microsoft.aspnet.signalr.client.Platform;
import microsoft.aspnet.signalr.client.SignalRFuture;
import microsoft.aspnet.signalr.client.http.android.AndroidPlatformComponent;
import microsoft.aspnet.signalr.client.hubs.HubConnection;
import microsoft.aspnet.signalr.client.hubs.HubProxy;

/**
 * Created by Richard on 8/21/2018.
 */
public class SignalRChatActivity extends AppCompatActivity{

    private Context context = this;
    private EditText messageET;
    private ListView messageContainer;
    private Button sendBtn;
    private ChatAdapter adapter;

    private HubConnection connection;
    private HubProxy hub;

    private boolean isRegistered = false;

    private String chatId = "";
    private String chatLocationId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_signalr_chatwindow);

        Intent intent = getIntent();
        chatLocationId = intent.getStringExtra("locationId");
        setTitle(intent.getStringExtra("locationName"));
        //Load the Signal R Platform Component
        Platform.loadPlatformComponent( new AndroidPlatformComponent() );

        String host = "https://alertnotifications.com/signalr";
        connection = new HubConnection( host );
        hub = connection.createHubProxy( "ChatHub" );
        attemptHubConnection();
        initControls();
    }

    @Override
    protected void onStop() {
        super.onStop();
        //disconnect from the hub
        connection.stop();
    }


    private void initControls(){
        messageContainer = (ListView) findViewById(R.id.messagesContainerSigR);
        messageET = (EditText) findViewById((R.id.messageEditSigR));
        sendBtn = (Button) findViewById(R.id.chatSendButtonSigR);

        //   TextView meLabel = (TextView) findViewById(R.id.meLbl);
        //   TextView companionLabel = (TextView) findViewById(R.id.friendLabel);
        RelativeLayout container = (RelativeLayout) findViewById(R.id.containerSigR);

        sendBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                String message = messageET.getText().toString();
                if(message.trim().length()>0){
                    sendChatMessage(message);
                }

//                if(!isRegistered){
//                    attemptHubConnection();
//                }
//                else{
//                    showToast("Is Registered! || Chat ID = " + chatId, Toast.LENGTH_SHORT);
//
//                }
            }
        });

    }


    private void attemptHubConnection(){
        //showToast("Attempting to connect to chat server", Toast.LENGTH_SHORT);
        SignalRFuture<Void> awaitConnection = connection.start();
        try
        {
            //try connecting
            awaitConnection.get();
            //subscribe to hub callback methods (listen for hub callback methods)
            hub.subscribe( this );

            //the below lines of code provide the same functionality as the line above (hub.subscribe). However, it eats up a lot of memory and hinders performance if not used properly.
//            hub.on("onConnected", new SubscriptionHandler() {
//                @Override
//                public void run() {
//                    onConnected();
//                }
//            });
//            hub.on("onGetCurrentChatId", new SubscriptionHandler1<String>() {
//                @Override
//                public void run(String currentChatId) {
//                    chatId = currentChatId;
//                }
//            }, String.class);
//            hub.on("addChatMessage", new SubscriptionHandler5<String, String, String, String, String>() {
//
//                @Override
//                public void run(String cName, String cMessage, String cFromId, String cFromIp, String cLocationId) {
//                    addChatMessage(cName, cMessage, cFromId);
//                }
//            }, String.class, String.class, String.class, String.class, String.class);


            //showToast("Successfully connected and subscribed to hub", Toast.LENGTH_SHORT);
            //attempt to register
            registerToChatRoom();
        }
        catch (InterruptedException e)
        {
            showToast(e.getMessage(), Toast.LENGTH_SHORT);
        }
        catch (ExecutionException e)
        {
            showToast(e.getMessage(), Toast.LENGTH_SHORT);
        }
    }

    private void registerToChatRoom(){
       // showToast("Attempting to register to chat room", Toast.LENGTH_SHORT);

        //String accountId = Preferences.getString(Config.ACCOUNT_ID);
        String name = Preferences.getString(Config.USER_FULL_NAME);
        String fromId = Preferences.getString(Config.CONTACT_ID);
        String fromIp = "Android App";

        try
        {
            hub.invoke( "Connect", name, chatLocationId, fromId, fromIp ).get();

            showToast(context.getResources().getString(R.string.signalrchat_toastmess1), Toast.LENGTH_SHORT);
        }
        catch (InterruptedException e)
        {
            showToast(e.getMessage(), Toast.LENGTH_SHORT);
        }
        catch (ExecutionException e)
        {
            showToast(e.getMessage(), Toast.LENGTH_SHORT);
        }
    }

    public void onConnected(){

        isRegistered = true;
        Log.e("SignalR", "onConnected = success");

    }

    public void onGetCurrentChatId(String currentChatId){

        if(!chatId.equals(currentChatId)){
            chatId = currentChatId;
            new GetConversationHistory().execute();
        }

    }

    public void addChatMessage(final String name, final String message, String fromId, String fromIp, String locationId){
        Log.e("SignalR: ", "Chat Message recieved: \nName = " + name + "\nmessage = " + message + "\nfromId = " + fromId);
        ChatMessage mMessage = new ChatMessage();
        mMessage.setDate(name);//name will appear where date label is
        mMessage.setMessage(message);
        if(fromId.equals(Preferences.getString(Config.CONTACT_ID))) {
            mMessage.setMe(false);
        } else {
            mMessage.setMe(true);
        }

        displayMessage(mMessage);
    }

    private void sendChatMessage(String message){

        String accountId = Preferences.getString(Config.ACCOUNT_ID);
        String name = Preferences.getString(Config.USER_FULL_NAME);
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss aa");
        String currentDateandTime = sdf.format(new Date());
        message = currentDateandTime + " > " + message;
        String fromId = Preferences.getString(Config.CONTACT_ID);
        String fromIp = "Android App";

        try
        {
            hub.invoke( "SendChatMessage", accountId, chatLocationId, name, message, fromId, fromIp ).get();
            messageET.setText("");

        }
        catch (InterruptedException e)
        {
            showToast(e.getMessage(), Toast.LENGTH_SHORT);
        }
        catch (ExecutionException e)
        {
            showToast(e.getMessage(), Toast.LENGTH_SHORT);
        }
    }

    private void showToast (String tMessage, int tLength){
        Toast.makeText(context, tMessage, tLength).show();
    }

    private class GetConversationHistory extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voidParams) {

            HashMap<String, String> params = new HashMap<>();
            params.put("controller", "GrayBoar");
            params.put("action", "GetMessageDetailsDesktop");
            params.put("acct_id",Preferences.getString(Config.ACCOUNT_ID));
            params.put("chat_id",chatId);

            return FunctionHelper.apiCaller(context, params);
        }

        @Override
        protected void onPostExecute(String responseData) {

            if (responseData != null) {
                Log.e("Chat History Response", "\n" + responseData);
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
                        adapter = new ChatAdapter (SignalRChatActivity.this, new ArrayList<ChatMessage>());
                        messageContainer.setAdapter(adapter);

                        for (int i = 0; i < jsonarray.length(); i++) {
                            jsonobject = jsonarray.getJSONObject(i);
                            //                         String id = jsonobject.getString("attatchment");
                            String timeStamp = jsonobject.getString("datetime");
                            String messageId = jsonobject.getString("id");
                            String fName = jsonobject.getString("name");
                            String message = jsonobject.getString("message");
                            String senderId = jsonobject.getString("fromId");
                            //String viewed = jsonobject.getString("viewed");
                            //String receiverId = jsonobject.getString("to_id");



                            //put json objects into "ChatMessage" object
                            ChatMessage chatItems = new ChatMessage();
                            chatItems.setId(Long.parseLong(messageId));
                            chatItems.setMessage(message);
                            chatItems.setDate(fName);
                            if(senderId.equals(Preferences.getString(Config.CONTACT_ID)))
                            {
                                //navTitle = fName;
                                //setTitle(navTitle);
                                chatItems.setMe(false); //actually true in nature
                            }
                            else {
//                                setTitle(fName);
                                chatItems.setMe(true);
                            }


                            displayMessage(chatItems);
                            //progress.dismiss();

                        }
                    }
                } catch (JSONException e) {
                    Log.e("Error", e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }

    public void displayMessage(final ChatMessage message){

        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                if(adapter!=null){
                    adapter.add(message);
                    adapter.notifyDataSetChanged();
                    scroll();
                }
                else{
                    adapter = new ChatAdapter (SignalRChatActivity.this, new ArrayList<ChatMessage>());
                    messageContainer.setAdapter(adapter);
                    adapter.add(message);
                    adapter.notifyDataSetChanged();
                    scroll();
                }

            }
        });

    }

    private void scroll(){

        messageContainer.setSelection(messageContainer.getCount()-1);



    }
}
