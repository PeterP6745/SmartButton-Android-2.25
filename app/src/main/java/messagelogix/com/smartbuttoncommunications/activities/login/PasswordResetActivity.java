package messagelogix.com.smartbuttoncommunications.activities.login;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.HashMap;

import messagelogix.com.smartbuttoncommunications.R;
import messagelogix.com.smartbuttoncommunications.activities.core.TabBarActivity;
import messagelogix.com.smartbuttoncommunications.model.SuccessModel;
import messagelogix.com.smartbuttoncommunications.utils.Config;
import messagelogix.com.smartbuttoncommunications.utils.FunctionHelper;
import messagelogix.com.smartbuttoncommunications.utils.Preferences;

/**
 * Created by Vahid
 * This is the activity for resetting the password for the fist time. The initial password would be "changeme".
 */
public class PasswordResetActivity extends AppCompatActivity {

    private static final String LOG_TAG = EmailConfirmActivity.class.getSimpleName();

    //Context
    Context context = this;

    //Controls
    //Edit Texts
    EditText passwordEditText;

    EditText passwordConfirmEditText;

    //Buttons
    Button resetButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setTitle(R.string.title_activity_password_reset);
        setContentView(R.layout.activity_password_reset);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //Init preferences
        Preferences.init(this);
        //Get the inputs
        passwordEditText = (EditText) findViewById(R.id.et_password);
        passwordConfirmEditText = (EditText) findViewById(R.id.et_password_confirmation);
        //Get the button
        resetButton = (Button) findViewById(R.id.btn_password_reset);
        //Set the listener
        resetButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                nextButtonClick();
            }
        });
        //Set keyboard dismissal
        hideKeyboard();
        setTouchListenerForKeyboardDismissal();
    }

    public void setTouchListenerForKeyboardDismissal() {

        RelativeLayout layout = (RelativeLayout) findViewById(R.id.Step1Layout);
        assert layout != null;
        layout.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View view, MotionEvent motion) {

                hideKeyboard();
                return false;
            }
        });
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

        if (nextValidate(passwordEditText.getText().toString(), passwordConfirmEditText.getText().toString())) {
            new PasswordResetTask().execute();
        }
    }

    private boolean nextValidate(String password, String passowrdConfirm) {
        //Validate
        if (TextUtils.isEmpty(password) || TextUtils.isEmpty(passowrdConfirm)) {
            Toast.makeText(this, this.getResources().getString(R.string.passreset_toastmess1), Toast.LENGTH_LONG).show();
            return false;
        }
        if (!password.equals(passowrdConfirm)) {
            Toast.makeText(this, this.getResources().getString(R.string.passreset_toastmess2), Toast.LENGTH_LONG).show();
            return false;
        }
        if (password.equals("changeme")) {
            Toast.makeText(this, this.getResources().getString(R.string.passreset_toastmess3), Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    //Update password task
    private class PasswordResetTask extends AsyncTask<Void, Void, String> {

        private ProgressDialog pDialog;

        private String password;

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
            // show progress
            pDialog = new ProgressDialog(context);
            pDialog.setMessage(context.getResources().getString(R.string.generic_progressdialog_mess2));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
            this.password = passwordEditText.getText().toString();
        }

        @Override
        protected String doInBackground(Void... voidParams) {

            HashMap<String, String> params = new HashMap<>();
//            params.put("controller", "User");
            params.put("controller", "RedBear");
            params.put("action", "UpdatePassword");
            params.put("contact_id", Preferences.getString(Config.CONTACT_ID));
            params.put("password", this.password);
            return FunctionHelper.apiCaller(context, params);
        }

        @Override
        protected void onPostExecute(String result) {

            if (result != null) {
                SuccessModel successModel = new Gson().fromJson(result, SuccessModel.class);
                if (successModel.getSuccess()) {
                    Preferences.putString(Config.PASSWORD, this.password);
                    Preferences.putBoolean(Config.PASSWORD_RESET_FLAG, true);
                    Intent homeIntent = new Intent(context, TabBarActivity.class);
                    startActivity(homeIntent);
                }
            }
            pDialog.cancel();
        }
    }
}
