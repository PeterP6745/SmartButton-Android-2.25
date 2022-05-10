package messagelogix.com.smartbuttoncommunications.activities.conversations;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import messagelogix.com.smartbuttoncommunications.R;
import messagelogix.com.smartbuttoncommunications.activities.chat.ChatItemDetailActivity;
import messagelogix.com.smartbuttoncommunications.activities.chat.ChatItemListActivity;
import messagelogix.com.smartbuttoncommunications.activities.chat.ChooseReceiverActivity;
import messagelogix.com.smartbuttoncommunications.activities.core.TabBarActivity;
import messagelogix.com.smartbuttoncommunications.model.ListItemContent;
import messagelogix.com.smartbuttoncommunications.utils.ApiHelper;
import messagelogix.com.smartbuttoncommunications.utils.Config;
import messagelogix.com.smartbuttoncommunications.utils.CustomProgressDialog;
import messagelogix.com.smartbuttoncommunications.utils.LogUtils;
import messagelogix.com.smartbuttoncommunications.utils.Preferences;
import messagelogix.com.smartbuttoncommunications.utils.RetryCounter;

import static messagelogix.com.smartbuttoncommunications.model.ListItemContent.addItem;

public class StandardChatList extends Fragment {

    private boolean mTwoPane = false;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private StandardChatList.SimpleItemRecyclerViewAdapter adapter;

    private Integer totalUnreadMessages = 0;

    private static final String LOG_TAG = ChatItemListActivity.class.getSimpleName();

    @Override
    public void onAttach(Context context) { super.onAttach(context); }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(!Preferences.getBoolean(Config.HAS_DESKTOPS)) {
            android.support.v7.app.ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
            if (actionBar != null) {
                actionBar.setHomeButtonEnabled(false); // disable the button
                actionBar.setDisplayHomeAsUpEnabled(false); // remove the left caret
                //actionBar.setDisplayShowHomeEnabled(false); // remove the icon
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chatlist, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        LogUtils.debug("IncidentReportsList","onViewCreated()");

        final FloatingActionButton fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            //
            @Override
            public void onClick(View view) {
                //Go to create conversation activity
                Intent intent = new Intent(getActivity(), ChooseReceiverActivity.class);
                startActivity(intent);
            }
        });

        mRecyclerView = view.findViewById(R.id.chatitem_list);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0 && fab.getVisibility() == View.VISIBLE) {
                    fab.hide();
                } else if (dy < 0 && fab.getVisibility() != View.VISIBLE) {
                    fab.show();
                }
            }
        });

        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        assert mRecyclerView != null;
        setupRecyclerView(mRecyclerView);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        LogUtils.debug("IncidentReportsList","onActivityCreated()");
    }

    @Override
    public void onResume() {
        super.onResume();

        getActivity().registerReceiver(mMessageReceiver, new IntentFilter("reloadChatList"));

        Config.setChatId("0");
        getConvoList();
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(mMessageReceiver);
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    private void getConvoList() {
        HashMap<String, String> params = new HashMap<>();
        params.put("controller", "GrayBoar");
        params.put("action", "GetMessageSummary");
        params.put("contact_id", Preferences.getString(Config.CONTACT_ID));

        final ApiHelper apiHelper = new ApiHelper();

        final CustomProgressDialog progressDialog = new CustomProgressDialog(getActivity());
        progressDialog.showDialog(getActivity().getResources().getString(R.string.chatlist_progressdialog1));

        final RetryCounter retryCounter = new RetryCounter();

        apiHelper.setOnSuccessListener(new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
                adapter.clearData(); //Clear Data so there are no Duplicate Items

                try {
                    JSONObject jsonobject = new JSONObject(response);
                    LogUtils.debug(LOG_TAG, "JSON data: " + response);
                    boolean success = jsonobject.getBoolean("success");
                    if(success) {
                        // Locate the NodeList name
                        JSONArray jsonarray = jsonobject.getJSONArray("data");
                        LogUtils.debug(LOG_TAG, "Json Array: " + jsonarray);
                        for (int i = 0; i < jsonarray.length(); i++) {
                            jsonobject = jsonarray.getJSONObject(i);
                            String id = jsonobject.getString("chat_id");
                            String timeStamp = jsonobject.getString("timestamp");
                            String messageId = jsonobject.getString("message_id");
                            String fName = jsonobject.getString("fname");
                            String message = jsonobject.getString("message");
//                            String groupTotal = jsonobject.getString("group_total");

                            //Used to display the number of non-viewed messages from a given conversation
                            String newMessages = jsonobject.getString("total_notviewed");

                            String receiverId = jsonobject.getString("receiver_id");
                            String receiverName = jsonobject.getString("receiver_name");
                            String lastUserInChat = jsonobject.getString("last_chat_message_sender_id");

                            calcTotalUnreadMessages(newMessages);
//                            Log.d(LOG_TAG,"Conversations test- "+ id+", "+timeStamp+", "+messageId+", "+fName+", "+message+", "+groupTotal+", "+receiverId);
                            ListItemContent.ConversationItem tempItem = new ListItemContent.ConversationItem(
                                    id,
                                    timeStamp,
                                    messageId,
                                    fName,
                                    message,
                                    newMessages,
                                    receiverId,
                                    receiverName,
                                    lastUserInChat
                            );

                            addConversationItem(tempItem);
                            //clearBadges();
                        }

                        setTotalUnreadMessages();
                        LogUtils.debug("getconvolist", "responsedata has a data key and success is true");

                    } else {
                        LogUtils.debug("getconvolist", "responsedata has a data key and success is false");
                        showErrorPrompt();
                    }

                } catch (Exception e) {
                    LogUtils.debug("getconvoList", "an exception was thrown while processing responsedata");
                    showErrorPrompt();
                }
            }
        });

        apiHelper.setOnErrorListener(new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                int currRC = retryCounter.getRetryCount();
                LogUtils.debug("getconvolist","retried: " + apiHelper.getUrl() +  " backend call\n" + currRC + " times. Received an error code from the server -> "+error);

                if(currRC == 0) {
                    progressDialog.dismiss();
                    showErrorPrompt();
                }
                else {
                    retryCounter.decrementRetryCount();
                    //LogUtils.debug("covid19surveytask","retryPolicy of temp obj: "+temp.getRetryPolicy().toString());
                    ApiHelper.getInstance(getActivity()).startRequest(apiHelper);
                }
            }
        });

        apiHelper.prepareRequest(params, false);
        ApiHelper.getInstance(getActivity()).startRequest(apiHelper);
    }

    private void showErrorPrompt() {
        Toast.makeText(getActivity(), R.string.chatlist_errormess1, Toast.LENGTH_SHORT).show();
    }

    private void clearBadges(){
        TabBarActivity.updateConversationsTab(/*5,*/"0");
        Config.setChatBadgeCount("0");
    }

    private void addConversationItem(ListItemContent.ConversationItem item) {
        //Add an item to the list
        addItem(item);
        adapter.notifyDataSetChanged();
    }

    //This is the handler that will update tables when a notification is received
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Extract data included in the Intent (not necessary)
            adapter.clearData(); //Clear Data so there are no Duplicate Items
            getConvoList();
            LogUtils.debug("reloading","Table Was Reloaded");
        }
    };

    public String getDateString(Date dateObj) {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM d, yyyy - h:mm");
        String timeString = sdf.format(dateObj);

        Calendar cal = Calendar.getInstance();
        cal.setTime(dateObj);

        if(cal.get(Calendar.AM_PM) == Calendar.PM) {
            timeString += " p.m.";
        } else {
            timeString += " a.m.";
        }

        return timeString;
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        adapter = new StandardChatList.SimpleItemRecyclerViewAdapter(ListItemContent.ITEMS);
        recyclerView.setAdapter(adapter);
    }

    public class SimpleItemRecyclerViewAdapter extends RecyclerView.Adapter<StandardChatList.SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final List<ListItemContent.ConversationItem> mValues;

        public SimpleItemRecyclerViewAdapter(List<ListItemContent.ConversationItem> items) {
            mValues = items;
        }

        @Override
        public StandardChatList.SimpleItemRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chatitem_list_content_experimental, parent, false);
            return new StandardChatList.SimpleItemRecyclerViewAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final StandardChatList.SimpleItemRecyclerViewAdapter.ViewHolder holder, int position) {
            holder.mItem = mValues.get(position);

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", getResources().getConfiguration().locale);//Locale.ENGLISH);
            String convoTimestamp = mValues.get(position).timestamp;
            try {
                LogUtils.debug("NotifListFrag","notif timestamp is: "+convoTimestamp);
                Date dateObj = sdf.parse(convoTimestamp);
                convoTimestamp = getDateString(dateObj);
            } catch (Exception e) {
                LogUtils.debug("NotifListFrag","Exception thrown while reformatting timestamp for a notification");
            }

            holder.mtimeStampView.setText(convoTimestamp);

            //setting the 'title' of the message to the name of the recipient or the id of the chat room
            if(mValues.get(position).receiverName.equals("null")) {
                holder.mFName.setText((getActivity().getResources().getString(R.string.chatlist_chattitlenull)+ mValues.get(position).id));
            } else {
                holder.mFName.setText(mValues.get(position).receiverName);
            }

            //setting the chat to show who sent the last message
            if(Preferences.getString(Config.CONTACT_ID).equals(mValues.get(position).lastUserInChat)/*mValues.get(position).lastUserInChat.equals("1")*/) {
//                Log.d("conttsa",Preferences.getString(mValues.get(position).lastUserInChat));
                holder.mMessage.setText((getActivity().getResources().getString(R.string.chatlist_chatsender1)+mValues.get(position).message));
            } else {
                String firstName = mValues.get(position).fname;
                if(firstName.contains(" ")) {
                    firstName= firstName.substring(0,firstName.indexOf(" "));
                }
                holder.mMessage.setText( firstName+": "+mValues .get(position).message);
            }

            //Show "New Message" Value on Badge View
            // If value is 0 show nothing
            if(mValues.get(position).newMessage.equals("0")) {
                holder.mBadgeView.setVisibility(View.GONE);
            } else {
                holder.mBadgeView.setVisibility(View.VISIBLE);
                holder.mBadgeView.setText(mValues.get(position).newMessage);
            }

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mTwoPane) {}
                    else {
                        LogUtils.debug(LOG_TAG, "id: " + holder.mItem.id + " fname: " + holder.mItem.fname);
                        Context context = v.getContext();
                        Intent intent = new Intent(context, ChatItemDetailActivity.class);
                        intent.putExtra(Config.CONVERSATION_ID, holder.mItem.id);
                        intent.putExtra(Config.CONVERSATION_FNAME, holder.mItem.fname);
                        intent.putExtra(Config.CONVERSATION_RECEIVER_NAME, holder.mItem.receiverName);
                        Config.setChatId(holder.mItem.id);
                        Config.MUTABLE_RECEIVER_NAME = holder.mItem.receiverName;
                        LogUtils.debug("CHATDETAILSCREEN","receiverName in chatlistfrag: "+Config.MUTABLE_RECEIVER_NAME);
                        context.startActivity(intent);
                    }
                }
            });
        }

        public void removeAt(int position) {
            mValues.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, mValues.size());
        }

        public void clearData() {
            int size = this.mValues.size();
            if (size > 0) {
                for(int i = 0; i < size; i++) {
                    this.mValues.remove(0);
                }

                this.notifyItemRangeRemoved(0, size);
            }
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            public final View mView;

            public final TextView mIdView;

            public final TextView mtimeStampView;

            public final TextView mFName;

            public final TextView mMessage;

            public ListItemContent.ConversationItem mItem;

            public TextView mBadgeView;

            public ViewHolder(View view) {
                super(view);

                mView = view;
                mIdView = (TextView) view.findViewById(R.id.id);
                mtimeStampView = (TextView) view.findViewById(R.id.timestamp);
                mFName = (TextView) view.findViewById(R.id.fname);
                mMessage = (TextView) view.findViewById(R.id.message);
                mBadgeView = (TextView) view.findViewById(R.id.badge_item); //badge image
            }
        }
    }

    void calcTotalUnreadMessages(String unreadCount) {
        if(!unreadCount.equals("0"))
            totalUnreadMessages += Integer.valueOf(unreadCount);
    }

    void setTotalUnreadMessages() {
        TabBarActivity.updateTabBadgeCount(totalUnreadMessages.toString(),2,false);
        totalUnreadMessages = 0;
    }
}
