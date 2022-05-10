package messagelogix.com.smartbuttoncommunications.activities.identity;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.File;
import java.util.HashMap;
import java.util.Locale;

import messagelogix.com.smartbuttoncommunications.R;
import messagelogix.com.smartbuttoncommunications.model.Response;
import messagelogix.com.smartbuttoncommunications.utils.Config;
import messagelogix.com.smartbuttoncommunications.utils.FunctionHelper;
import messagelogix.com.smartbuttoncommunications.utils.LanguageManager;
import messagelogix.com.smartbuttoncommunications.utils.Preferences;

/**
 * Created by Richard on 4/13/2018.
 */
public class AddRoomInfoActivity extends AppCompatActivity {

    private Button saveInfoButton;

    private EditText roomEditText;

    private EditText floorEditText;

    private EditText descriptionEditText;

    private Context context = this;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LanguageManager.setLocale(this, Preferences.getString(Config.LANGUAGE));

        setTitle(R.string.add_room_info_title);
        setContentView(R.layout.activity_add_room_information);

        initComponents();
    }

    private void initComponents(){
        //init room field
        roomEditText = (EditText)findViewById(R.id.et_room_number);
        roomEditText.setText(Preferences.getString(Config.USER_ROOM));
        /*roomEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //store in preferences
                Preferences.putString(Config.USER_ROOM, s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });*/



        //init floor field
        floorEditText = (EditText)findViewById(R.id.et_floor_number);
        floorEditText.setText(Preferences.getString(Config.USER_FLOOR));
        /*floorEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //store in preferences
                Preferences.putString(Config.USER_FLOOR, s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });*/


        //init description field
        descriptionEditText = (EditText)findViewById(R.id.et_description);
        descriptionEditText.setText(Preferences.getString(Config.USER_DESCRIPTION));
        /*descriptionEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //store in preferences
                Preferences.putString(Config.USER_DESCRIPTION, s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });*/

        //init save button
        saveInfoButton = (Button)findViewById(R.id.btn_save_room_info);

        saveInfoButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //extract user input
                String roomNumber = roomEditText.getText().toString();
                String floorNumber = floorEditText.getText().toString();
                String description = descriptionEditText.getText().toString();

                //apply to user settings
                Preferences.putString(Config.USER_ROOM, roomNumber);
                Preferences.putString(Config.USER_FLOOR, floorNumber);
                Preferences.putString(Config.USER_DESCRIPTION, description);

                //save in database
                SaveUserInfo saveUserInfoTask = new SaveUserInfo();
                saveUserInfoTask.execute();
            }
        });
    }

    private class SaveUserInfo extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... voidParams) {

            HashMap<String, String> params = new HashMap<>();
//            params.put("controller", "User");
            params.put("controller", "RedBear");
            params.put("action", "SaveIdentity");
            params.put("accountId", Preferences.getString(Config.ACCOUNT_ID));
            params.put("unique_id", Preferences.getString(Config.UNIQUE_ID));
            params.put("contact_id", Preferences.getString(Config.CONTACT_ID));
            params.put("fname", Preferences.getString(Config.USER_FULL_NAME));
            params.put("title", Preferences.getString(Config.USER_TITLE_ID));
            params.put("school", Preferences.getString(Config.USER_BUILDING_ID));
            params.put("cellphone", Preferences.getString(Config.CONTACT_CELLPHONE));
            params.put("email", Preferences.getString(Config.CONTACT_EMAIL));
            params.put("image", new File(Preferences.getString(Config.USER_PROFILE_PICTURE)).getName());
            params.put("app_type", "3");
            params.put("room", Preferences.getString(Config.USER_ROOM));
            params.put("floor", Preferences.getString(Config.USER_FLOOR));
            params.put("description", Preferences.getString(Config.USER_DESCRIPTION));
            return FunctionHelper.apiCaller(context, params);
        }

        @Override
        protected void onPostExecute(String responseData) {

            if (responseData != null) {
                //Log.d(LOG_TAG, "json = " + responseData);
                Response defaults = new Gson().fromJson(responseData, Response.class);
                if (defaults.getSuccess()) {
                    onBackPressed();
                    //Save successful
                    Toast.makeText(getApplicationContext(), context.getResources().getString(R.string.addroom_successmess1), Toast.LENGTH_SHORT).show();
                } else {
                    //error
                    Toast.makeText(getApplicationContext(), context.getResources().getString(R.string.addroom_errormess1), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

}
