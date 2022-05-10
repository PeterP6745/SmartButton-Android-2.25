package messagelogix.com.smartbuttoncommunications.activities.chat;


import android.app.ProgressDialog;

import android.content.Context;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import android.util.Log;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;


import messagelogix.com.smartbuttoncommunications.R;

import messagelogix.com.smartbuttoncommunications.utils.Config;
import messagelogix.com.smartbuttoncommunications.utils.FunctionHelper;
import messagelogix.com.smartbuttoncommunications.utils.Preferences;



/**
 * Created by Vahid
 * An activity representing a single ChatItem detail screen. This
 * activity is only used narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link ChatItemListActivity}.
 */
public class ChatMembersActivity extends AppCompatActivity {

    private Context context = this;

   // private ListView messageContainer;

   // private MemberAdapter adapter;
  //  private ArrayList<messagelogix.com.smartbutton.model.ChatMembers> members;

    private String chatId;

    private static final String LOG_TAG = ChatMembersActivity.class.getSimpleName();

    private ProgressDialog progress;

    private TextView receiverName;
    private TextView receiverEmail;
    private TextView senderName;
    private TextView senderEmail;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_members);
        Preferences.init(this);
        setTitle(R.string.conversation_members);

        receiverName = (TextView) findViewById(R.id.fNameReceiver);
        receiverEmail = (TextView) findViewById(R.id.emailReceiver);

        senderName = (TextView) findViewById(R.id.fNameSender);
        senderEmail = (TextView) findViewById(R.id.emailSender);

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                chatId= "0";

            } else {
                chatId= extras.getString(Config.CONVERSATION_ID);
                Config.setChatId(chatId);

            }
        } else {
            chatId= (String) savedInstanceState.getSerializable(Config.CONVERSATION_ID);
            Config.setChatId(chatId);

        }



     //   messageContainer = (ListView) findViewById(R.id.memberContainer);
        new GetMembers().execute();

    }

    //register your activity onResume()
    @Override
    public void onResume() {
        super.onResume();
    }

    //Must unregister onPause()
    @Override
    protected void onPause() {
        super.onPause();
    }




//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds options to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onPrepareOptionsMenu(Menu menu) {
//
//        menu.clear();
//        getMenuInflater().inflate(R.menu.menu, menu);
//        return super.onPrepareOptionsMenu(menu);
//    }
//
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//
//        switch (item.getItemId()) {
//            case R.id.action_show_info:
//
//                Intent intent = new Intent(this, MainActivity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                startActivity(intent);
//                return true;
//            default:
//                return super.onOptionsItemSelected(item);
//        }
//    }

//    public void displayMessage(messagelogix.com.smartbutton.model.ChatMembers embers){
//        adapter.add(embers);
//        adapter.notifyDataSetChanged();
//
//
//    }


    //Asynchronous tasks
    private class GetMembers extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voidParams) {

            HashMap<String, String> params = new HashMap<>();

            params.put("controller", "GrayBoar");
            params.put("action", "GetMembers");
            params.put("chat_id",chatId);

            return FunctionHelper.apiCaller(context, params);
        }

        @Override
        protected void onPostExecute(String responseData) {

            if (responseData != null) {
                Log.d(LOG_TAG, "json = " + responseData);
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
                      //  adapter = new MemberAdapter (ChatMembers.this, new ArrayList<messagelogix.com.smartbutton.model.ChatMembers>());
                     //   messageContainer.setAdapter(adapter);

                        for (int i = 0; i < jsonarray.length(); i++) {
                            jsonobject = jsonarray.getJSONObject(i);
                            //                         String id = jsonobject.getString("attatchment");

                            String fName = jsonobject.getString("fname");
                            String detail = jsonobject.getString("email");

                            if(i == 0){
                                receiverName.setText(fName);
                                receiverEmail.setText(detail);
                            }
                            else{
                                senderName.setText(fName);
                                senderEmail.setText(detail);
                            }

                           // progress.dismiss();

                        }
                    }
                } catch (JSONException e) {
                    Log.e("Error", e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }

}
