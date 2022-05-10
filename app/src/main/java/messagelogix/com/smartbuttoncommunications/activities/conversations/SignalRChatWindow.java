package messagelogix.com.smartbuttoncommunications.activities.conversations;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;

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
import messagelogix.com.smartbuttoncommunications.utils.ApiHelper;
import messagelogix.com.smartbuttoncommunications.utils.Config;
import messagelogix.com.smartbuttoncommunications.utils.LogUtils;
import messagelogix.com.smartbuttoncommunications.utils.Preferences;
import messagelogix.com.smartbuttoncommunications.utils.TitleTracker;
import microsoft.aspnet.signalr.client.ConnectionState;
import microsoft.aspnet.signalr.client.Platform;
import microsoft.aspnet.signalr.client.SignalRFuture;
import microsoft.aspnet.signalr.client.http.android.AndroidPlatformComponent;
import microsoft.aspnet.signalr.client.hubs.HubConnection;
import microsoft.aspnet.signalr.client.hubs.HubProxy;

public class SignalRChatWindow extends Fragment {
    TitleTracker titleTrackerInterface;

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
    public void onAttach(Context context) {
        super.onAttach(context);
        titleTrackerInterface = (TitleTracker) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getArguments() != null) {
            chatLocationId = getArguments().getString("locationId");
            getActivity().setTitle(getArguments().getString("locationName"));
            titleTrackerInterface.addToTitleStack(getArguments().getString("locationName"));
        }

        //Load the Signal R Platform Component
        Platform.loadPlatformComponent( new AndroidPlatformComponent() );

        String host = "https://alertnotifications.com/signalr";
        connection = new HubConnection( host );
        hub = connection.createHubProxy( "ChatHub" );
        attemptHubConnection();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_signalr_chatwindow, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        LogUtils.debug("RebuildSignalR","SignalRChatWindow - onViewCreated()");

        messageContainer = view.findViewById(R.id.messagesContainerSigR);
        messageET = view.findViewById((R.id.messageEditSigR));
        sendBtn = view.findViewById(R.id.chatSendButtonSigR);

        RelativeLayout container = view.findViewById(R.id.containerSigR);

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(connection.getState() == ConnectionState.Connected) {
                    String message = messageET.getText().toString();
                    if(message.trim().length() > 0)
                        sendChatMessage(message);
                } else {
                    showToast("Your message could not be delivered because you are not connected to the Smart Button® chat server. Please close this chat window and reopen it to reconnect.", Toast.LENGTH_LONG);
                }
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        LogUtils.debug("RebuildSignalR","SignalRChatWindow - onStart()");
    }

    @Override
    public void onResume() {
        super.onResume();
        LogUtils.debug("RebuildSignalR","SignalRChatWindow - onResume()");
    }

    @Override
    public void onPause() {
        super.onPause();
        LogUtils.debug("RebuildSignalR","SignalRChatWindow - onPause()");
    }

    @Override
    public void onStop() {
        super.onStop();
        //disconnect from the hub
        connection.stop();
        LogUtils.debug("RebuildSignalR","SignalRChatWindow - onStop()");
    }

    private void attemptHubConnection(){
        //showToast("Attempting to connect to chat server", Toast.LENGTH_SHORT);
        SignalRFuture<Void> awaitConnection = connection.start();
        try {
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
        } catch (InterruptedException e) {
            showToast(e.getMessage(), Toast.LENGTH_SHORT);
        }
        catch (ExecutionException e) {
            showToast(e.getMessage(), Toast.LENGTH_SHORT);
        }
    }

    private void registerToChatRoom() {
        String name = Preferences.getString(Config.USER_FULL_NAME);
        String fromId = Preferences.getString(Config.CONTACT_ID);
        String fromIp = "Android App";

        try {
            hub.invoke( "Connect", name, chatLocationId, fromId, fromIp ).get();

            showToast(getActivity().getResources().getString(R.string.signalrchat_toastmess1), Toast.LENGTH_SHORT);
        }
        catch (InterruptedException e) {
            showToast(e.getMessage(), Toast.LENGTH_SHORT);
        }
        catch (ExecutionException e) {
            showToast(e.getMessage(), Toast.LENGTH_SHORT);
        }
    }

    public void onConnected() {
        isRegistered = true;
        Log.e("SignalR", "onConnected = success");
        LogUtils.debug("RebuildSignalR", "onConnected = success");
    }

    public void onGetCurrentChatId(String currentChatId) {
        LogUtils.debug("RebuildSignalR", "onGetCurrentChatId");
        if(!chatId.equals(currentChatId)) {
            chatId = currentChatId;
            getConversationHistory();
        }
    }

    public void addChatMessage(final String name, final String message, String fromId, String fromIp, String locationId) {
        LogUtils.debug("RebuildSignalR", "Chat Message recieved: \nName = " + name + "\nmessage = " + message + "\nfromId = " + fromId);
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

    private void sendChatMessage(String message) {
        String accountId = Preferences.getString(Config.ACCOUNT_ID);
        String name = Preferences.getString(Config.USER_FULL_NAME);
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss aa");
        String currentDateandTime = sdf.format(new Date());
        message = currentDateandTime + " > " + message;
        String fromId = Preferences.getString(Config.CONTACT_ID);
        String fromIp = "Android App";

        try {
            hub.invoke( "SendChatMessage", accountId, chatLocationId, name, message, fromId, fromIp ).get();
            messageET.setText("");
        } catch (InterruptedException e) {
            LogUtils.debug("RebuildSignalR","sendChatMessage - inside catch-block, exception encounted - "+e.getMessage());
            showToast(e.getMessage(), Toast.LENGTH_SHORT);
        } catch (ExecutionException e) {
            LogUtils.debug("RebuildSignalR","sendChatMessage - inside catch-block, exception encounted - "+e.getMessage());
            showToast(e.getMessage(), Toast.LENGTH_SHORT);
        }
    }

    private void showToast (String tMessage, int tLength) {
        Toast.makeText(getActivity(), tMessage, tLength).show();
    }

    private void getConversationHistory() {
        LogUtils.debug("RebuildSignalR","inside getConversationsHistory()");
        LogUtils.debug("SignalRChat","inside getConversationsHistory()");

        final Context that = getActivity();

        HashMap<String, String> params = new HashMap<>();
        params.put("controller", "GrayBoar");
        params.put("action", "GetMessageDetailsDesktop");
        params.put("acct_id",Preferences.getString(Config.ACCOUNT_ID));
        params.put("chat_id",chatId);

        ApiHelper apiHelper = new ApiHelper();

        apiHelper.setOnSuccessListener(new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    LogUtils.debug("SignalRChat","getConversationHistory() --> response: "+response);
                    JSONObject jsonobject = new JSONObject(response);

                    boolean success = jsonobject.getBoolean("success");
                    if(success) {
                        // Locate the NodeList name
                        JSONArray jsonarray = jsonobject.getJSONArray("data");
                        adapter = new ChatAdapter (getActivity(), new ArrayList<ChatMessage>());
                        messageContainer.setAdapter(adapter);

                        for(int i = 0; i < jsonarray.length(); i++) {
                            jsonobject = jsonarray.getJSONObject(i);

                            String messageId = jsonobject.getString("id");
                            String fName = jsonobject.getString("name");
                            String message = jsonobject.getString("message");
                            String senderId = jsonobject.getString("fromId");

                            //put json objects into "ChatMessage" object
                            ChatMessage chatItems = new ChatMessage();
                            chatItems.setId(Long.parseLong(messageId));
                            chatItems.setMessage(message);
                            chatItems.setDate(fName);
                            if(senderId.equals(Preferences.getString(Config.CONTACT_ID))) {
                                chatItems.setMe(false); //actually true in nature
                            } else {
                                chatItems.setMe(true);
                            }

                            displayMessage(chatItems);
                        }
                    } else {
                        showToast("Failed to Load Chat History - Your chat history could not be loaded at this time. Please go back to the previous screen and try again.",Toast.LENGTH_LONG);
                    }
                } catch (JSONException e) {
                    Log.e("Error", e.getMessage());
                    e.printStackTrace();
                    showToast("Failed to Load Chat History - Your chat history could not be loaded at this time. Please go back to the previous screen and try again.",Toast.LENGTH_LONG);
                }
            }
        });

        apiHelper.setOnErrorListener(new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                LogUtils.debug("SignalRChat","getConversationHistory() --> onErrorListener");
                showToast("Failed to Load Chat History - Your chat history could not be loaded at this time. Please go back to the previous screen and try again.",Toast.LENGTH_LONG);
            }
        });

        apiHelper.prepareRequest(params, false);
        ApiHelper.getInstance(that).startRequest(apiHelper);
    }

    public void displayMessage(final ChatMessage message) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(adapter != null) {
                    adapter.add(message);
                    adapter.notifyDataSetChanged();
                    scroll();
                } else {
                    adapter = new ChatAdapter(getActivity(), new ArrayList<ChatMessage>());
                    messageContainer.setAdapter(adapter);
                    adapter.add(message);
                    adapter.notifyDataSetChanged();
                    scroll();
                }
            }
        });
    }

    private void scroll() {
        messageContainer.setSelection(messageContainer.getCount()-1);
    }
}
