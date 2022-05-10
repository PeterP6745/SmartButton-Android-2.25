package messagelogix.com.smartbuttoncommunications.activities.chat;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

import messagelogix.com.smartbuttoncommunications.R;
import messagelogix.com.smartbuttoncommunications.activities.core.TabBarActivity;
import messagelogix.com.smartbuttoncommunications.model.ListItemContent;
import messagelogix.com.smartbuttoncommunications.model.ListItemContent.ConversationItem;
import messagelogix.com.smartbuttoncommunications.utils.Config;
import messagelogix.com.smartbuttoncommunications.utils.FunctionHelper;
import messagelogix.com.smartbuttoncommunications.utils.LogUtils;
import messagelogix.com.smartbuttoncommunications.utils.Preferences;

import static messagelogix.com.smartbuttoncommunications.model.ListItemContent.addItem;

/**
 * Created by Vahid
 * An activity representing a list of ChatItems. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link ChatItemDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class ChatItemListActivity extends AppCompatActivity {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane = false;

    public boolean backButtonDisabled = false;

  //  private View recyclerView;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private ProgressDialog progress;

    private SimpleItemRecyclerViewAdapter adapter;

    private Context context = this;

    private static final String LOG_TAG = ChatItemListActivity.class.getSimpleName();



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatitem_list);

        boolean isSuperUser = Preferences.getBoolean(Config.IS_SUPER_USER);
        if(!isSuperUser)
            backButtonDisabled = true;

        progress = new ProgressDialog(this);





//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        toolbar.setTitle(getTitle());
        //Initialize preferences
        Preferences.init(this);

        if(!Preferences.getBoolean(Config.HAS_DESKTOPS)){
            android.support.v7.app.ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setHomeButtonEnabled(false); // disable the button
                actionBar.setDisplayHomeAsUpEnabled(false); // remove the left caret
                //actionBar.setDisplayShowHomeEnabled(false); // remove the icon
            }
        }


     //   adapter.clearData();
        //Asynchronous tasks
     //   new GetConversations().execute();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
//
            @Override
            public void onClick(View view) {
//
////                Snackbar.make(view, "Create new conversation!", Snackbar.LENGTH_LONG)
////                        .setAction("Action", null).show();
//
//                //Go to create conversation activity
                Intent intent = new Intent(ChatItemListActivity.this, ChooseReceiverActivity.class);
                startActivity(intent);
//
//                //Show the crete conversation dialog
////                new CreateConversation(ChatItemListActivity.this).show();
////                ConversationItem tempItem = new ConversationItem("1","This is a test item!","This is the item details.","Vahid","Ahmed");
////                addConversationItem(tempItem);
            }
        });

     //   recyclerView = findViewById(R.id.chatitem_list);
        mRecyclerView = (RecyclerView) findViewById(R.id.chatitem_list);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

   //     assert recyclerView != null;
        assert mRecyclerView!=null;

        setupRecyclerView(mRecyclerView);

//        if (findViewById(R.id.chatitem_detail_container) != null) {
//            // The detail container view will be present only in the
//            // large-screen layouts (res/values-w900dp).
//            // If this view is present, then the
//            // activity should be in two-pane mode.
//            mTwoPane = true;
//        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        context.registerReceiver(mMessageReceiver, new IntentFilter("reloadChatList"));
        progress.setMessage("Getting conversations...");
        progress.show();
       // adapter.clearData(); //Clear Data so there are no Duplicate Items
        Config.setChatId("0");
        new GetConversations().execute();
    }

    @Override
    protected void onPause() {
        super.onPause();
        context.unregisterReceiver(mMessageReceiver);
    }

    private void clearBadges(){

        TabBarActivity.updateConversationsTab(/*5,*/"0");
        Config.setChatBadgeCount("0");

    }

    private void addConversationItem(ConversationItem item) {
        //Add an item to the list
        addItem(item);
        adapter.notifyDataSetChanged();
    }


    //This is the handler that will update tables when a notification is received
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            // Extract data included in the Intent (not necessary)
           // String message = intent.getStringExtra("message");
            adapter.clearData(); //Clear Data so there are no Duplicate Items
            progress.setMessage("Incoming Message");
            progress.show();
            new GetConversations().execute();
            Log.d(LOG_TAG,"Table Was Reloaded");
        }
    };

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {

        adapter = new SimpleItemRecyclerViewAdapter(ListItemContent.ITEMS);
        recyclerView.setAdapter(adapter);
    }

    public class SimpleItemRecyclerViewAdapter extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {



        private final List<ListItemContent.ConversationItem> mValues;

        public SimpleItemRecyclerViewAdapter(List<ListItemContent.ConversationItem> items) {

            mValues = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.chatitem_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {

            holder.mItem = mValues.get(position);
            //holder.mIdView.setText(mValues.get(position).id);
            holder.mtimeStampView.setText(mValues.get(position).timestamp);
            //holder.mMessageId.setText(mValues.get(position).message_id);
            holder.mFName.setText(mValues.get(position).fname);
            holder.mMessage.setText(mValues.get(position).message);

            //Show "New Message" Value on Badge View
           // If value is 0 show nothing
            if(mValues.get(position).newMessage.equals("0")){
                holder.mBadgeView.setVisibility(View.GONE);
            }
            else{
                holder.mBadgeView.setVisibility(View.VISIBLE);
                holder.mBadgeView.setText(mValues.get(position).newMessage);}

            //holder.mGroupTotal.setText(mValues.get(position).group_total);
            //holder.mReceiverId.setText(mValues.get(position).receiver_id);

            holder.mView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    if (mTwoPane) {
//                        Bundle arguments = new Bundle();
//                        arguments.putString(ChatItemDetailFragment.ARG_ITEM_ID, holder.mItem.id);
//                        ChatItemDetailFragment fragment = new ChatItemDetailFragment();
//                        fragment.setArguments(arguments);
//                        getSupportFragmentManager().beginTransaction()
//                                .replace(R.id.chatitem_detail_container, fragment)
//                                .commit();
                    } else {
                        Log.d(LOG_TAG, "id: " + holder.mItem.id + " fname: " + holder.mItem.fname);
                        Context context = v.getContext();
                        Intent intent = new Intent(context, ChatItemDetailActivity.class);
                        intent.putExtra(Config.CONVERSATION_ID, holder.mItem.id);
                        intent.putExtra(Config.CONVERSATION_FNAME, holder.mItem.fname);
                        Config.setChatId(holder.mItem.id);
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
                for (int i = 0; i < size; i++) {
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

            //public final TextView mMessageId;
            public final TextView mFName;

            public final TextView mMessage;
            //public final TextView mGroupTotal;
            //public final TextView mReceiverId;


            public ListItemContent.ConversationItem mItem;

            public TextView mBadgeView;

            public ViewHolder(View view) {

                super(view);
                mView = view;
                mIdView = (TextView) view.findViewById(R.id.id);
                mtimeStampView = (TextView) view.findViewById(R.id.timestamp);
                //mMessageId = (TextView) view.findViewById(R.id.message_id);
                mFName = (TextView) view.findViewById(R.id.fname);
                mMessage = (TextView) view.findViewById(R.id.message);
                //mGroupTotal = (TextView) view.findViewById(R.id.group_total);
                //mReceiverId = (TextView) view.findViewById(R.id.receiver_id);

                mBadgeView = (TextView) view.findViewById(R.id.badge_item); //badge image
            }
//            @Override
//            public String toString() {
//
//                return super.toString() + " '" + mContentView.getText() + "'";
//            }
        }
    }

    //Asynchronous tasks
    private class GetConversations extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voidParams) {

            HashMap<String, String> params = new HashMap<>();
//            params.put("controller", "Message");
            params.put("controller", "GrayBoar");
            params.put("action", "GetMessageSummary");
            params.put("contact_id", Preferences.getString(Config.CONTACT_ID));
            Log.d(LOG_TAG,"controller: GrayBoar, action: GetMessageSummary, and contact id: "+Preferences.getString(Config.CONTACT_ID));
            return FunctionHelper.apiCaller(context, params);
        }

        @Override
        protected void onPostExecute(String responseData) {

            LogUtils.debug("getConvo","onPostExecute: " + responseData);
            //progress.dismiss();
            adapter.clearData(); //Clear Data so there are no Duplicate Items
            if (responseData != null) {
                //Log.d(LOG_TAG, "json = " + responseData);
                boolean success = false;
                try {
                    JSONObject jsonobject = new JSONObject(responseData);
                    Log.d(LOG_TAG,"JSON data: "+responseData);
                    success = jsonobject.getBoolean("success");
//                    Spinner titleSpinner = (Spinner) findViewById(R.id.TitleSpinner);
                    if (success) {
                        // Locate the NodeList name
                        JSONArray jsonarray = jsonobject.getJSONArray("data");
                        Log.d(LOG_TAG, "Json Array: "+jsonarray);
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
//                            Log.d(LOG_TAG,"Conversations test- "+ id+", "+timeStamp+", "+messageId+", "+fName+", "+message+", "+groupTotal+", "+receiverId);
//                            ConversationItem tempItem = new ConversationItem(
//                                    id,
//                                    timeStamp,
//                                    messageId,
//                                    fName,
//                                    message,
//                                    newMessages,
//                                    receiverId
//
//
//                            );
//
//                            addConversationItem(tempItem);

                            clearBadges();
                        }

                    }
                    progress.dismiss();
                } catch (JSONException e) {
                    Log.e("Error", e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        if(backButtonDisabled == true){
            Log.d("backPressed:","ChatItemListActivity - backButtonDisabled is - " + backButtonDisabled);
        }
        else {
            Log.d("backPressed","ChatItemListActivity - backButton works as normal");
            super.onBackPressed();
        }
    }
}
