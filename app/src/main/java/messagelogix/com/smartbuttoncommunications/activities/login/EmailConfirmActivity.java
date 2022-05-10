package messagelogix.com.smartbuttoncommunications.activities.login;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import messagelogix.com.smartbuttoncommunications.R;
import messagelogix.com.smartbuttoncommunications.model.SuccessModel;
import messagelogix.com.smartbuttoncommunications.utils.Config;
import messagelogix.com.smartbuttoncommunications.utils.FunctionHelper;
import messagelogix.com.smartbuttoncommunications.utils.LanguageManager;
import messagelogix.com.smartbuttoncommunications.utils.LogUtils;
import messagelogix.com.smartbuttoncommunications.utils.Preferences;
import messagelogix.com.smartbuttoncommunications.utils.Validation;

/**
 * Created by Vahid
 * This activity is for confirming the email address of the user
 */
public class EmailConfirmActivity extends AppCompatActivity {

    private static final String LOG_TAG = EmailConfirmActivity.class.getSimpleName();

    //Context
    Context context = this;

    //Controls
    //Edit Texts
    EditText emailEditText;

    EditText emailConfirmEditText;

    //Buttons
    Button nextButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        //Init preferences
        Preferences.init(this);
        LanguageManager.setLocale(this, Preferences.getString(Config.LANGUAGE));
        setTitle(R.string.title_activity_email_confirm);
        LogUtils.debug("onboardingtag","inside onCreate email confirmation page");
        setContentView(R.layout.activity_email_confirm);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //Get the inputs
        emailEditText = (EditText) findViewById(R.id.et_email);
        emailConfirmEditText = (EditText) findViewById(R.id.et_email_confirmation);
        //Get the buttton
        nextButton = (Button) findViewById(R.id.btn_email_confirm);
        //Set the listener
        nextButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                nextButtonClick();
            }
        });
        new GetUserInfo().execute();
        //Set keyboard dismissal
        hideKeyboard();
        setTouchListenerForKeyboardDismissal();
    }

    public void setTouchListenerForKeyboardDismissal() {

        RelativeLayout layout = (RelativeLayout) findViewById(R.id.Step1Layout);
        if (layout != null) {
            layout.setOnTouchListener(new View.OnTouchListener() {

                @Override
                public boolean onTouch(View view, MotionEvent motion) {
                    hideKeyboard();
                    return false;
                }
            });
        }
    }

    protected void hideKeyboard() {
        // Check if no view has focus:
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void nextButtonClick() {
        if (nextValidate(emailEditText.getText().toString().toLowerCase(), emailConfirmEditText.getText().toString().toLowerCase())) {
            new EmailConfirmationTask().execute();
        }
    }

    private boolean nextValidate(String email, String emailConfirm) {
        //Validate
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(emailConfirm)) {
            Toast.makeText(this, this.getResources().getString(R.string.emailconfirm_toastmess1), Toast.LENGTH_LONG).show();
            return false;
        }
        if (!Validation.isValidEmail(email)) {
            Toast.makeText(this, this.getResources().getString(R.string.emailconfirm_toastmess2), Toast.LENGTH_LONG).show();
            return false;
        }
        if (!email.equals(emailConfirm)) {
            Toast.makeText(this, this.getResources().getString(R.string.emailconfirm_toastmess3), Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    public class GetUserInfo extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... url) {

            HashMap<String, String> params = new HashMap<>();
//            params.put("controller", "User");
            params.put("controller", "RedBear");
            params.put("action", "GetUserInfo");
            params.put("contact_id", Preferences.getString(Config.CONTACT_ID));
            return FunctionHelper.apiCaller(context, params);
        }

        @Override
        protected void onPostExecute(final String responseData) {

            if (responseData != null) {
                try {
                    JSONObject responseJsonObject = new JSONObject(responseData);
                    boolean success = responseJsonObject.getBoolean(Config.SUCCESS);
                    if (success) {
                        JSONObject data = responseJsonObject.getJSONObject(Config.DATA);
                        Preferences.putString(Config.CONTACT_TYPE, data.getString(Config.CONTACT_TYPE));
                        Preferences.putString(Config.CONTACT_ID, data.getString(Config.CONTACT_ID));
                        Preferences.putString(Config.USER_FULL_NAME, data.getString(Config.CONTACT_FIRST_NAME));
                        Preferences.putString(Config.CONTACT_TITLE, data.getString(Config.CONTACT_TITLE));
                        Preferences.putString(Config.CONTACT_EMAIL, data.getString(Config.EMAIL));
                        Preferences.putString(Config.CONTACT_CELLPHONE, data.getString(Config.CONTACT_CELLPHONE));
                        Preferences.putString(Config.CONTACT_CARRIER, data.getString(Config.CONTACT_CARRIER));
                        Preferences.putString(Config.CONTACT_CARRIER_ID, data.getString(Config.CONTACT_CARRIER_ID));
                        Preferences.putString(Config.CONTACT_USER_NAME, data.getString(Config.CONTACT_USER_NAME));
                        Preferences.putString(Config.CONTACT_PRIVILEGE, data.getString(Config.CONTACT_PRIVILEGE));
                        Preferences.putString(Config.CONTACT_SCHOOLS, data.getString(Config.CONTACT_SCHOOLS));
                        Preferences.putString(Config.CONTACT_POLICY, data.getString(Config.CONTACT_POLICY));
                        //Set the initial email
                        if (Preferences.isAvailable(Config.CONTACT_EMAIL)) {
                            emailEditText.setText(Preferences.getString(Config.CONTACT_EMAIL));
                            Log.d(LOG_TAG, "Email confirm: " + Preferences.getString(Config.CONTACT_EMAIL));
                        }
                    } else {
                        Log.d(LOG_TAG, "GetUserInfo Account invalid");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Log.d(LOG_TAG, "GetUserInfo No JSON received ! :(");
            }
        }
    }

    //Update email task
    private class EmailConfirmationTask extends AsyncTask<Void, Void, String> {

        private ProgressDialog pDialog;

        private String emailAddress;

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
            // show progress
            pDialog = new ProgressDialog(context);
            pDialog.setMessage(context.getResources().getString(R.string.generic_progressdialog_mess2));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
            this.emailAddress = emailEditText.getText().toString();
        }

        @Override
        protected String doInBackground(Void... voidParams) {

            HashMap<String, String> params = new HashMap<>();
//            params.put("controller", "User");
            params.put("controller", "RedBear");
            params.put("action", "UpdateEmail");
            params.put("contact_id", Preferences.getString(Config.CONTACT_ID));
            params.put("email", this.emailAddress);
            return FunctionHelper.apiCaller(context, params);
        }

        @Override
        protected void onPostExecute(String result) {

            if (result != null) {
                SuccessModel successModel = new Gson().fromJson(result, SuccessModel.class);
                if (successModel.getSuccess()) {
                    //Update successful
                    Preferences.putString(Config.CONFIRMATION_EMAIL, emailEditText.getText().toString());
                    Preferences.putString(Config.CONTACT_EMAIL, emailEditText.getText().toString());
                    Intent passwordResetIntent = new Intent(context, PasswordResetActivity.class);
                    startActivity(passwordResetIntent);
                }
            }
            pDialog.cancel();
        }
    }

    @Override
    public void onBackPressed() {}
}
