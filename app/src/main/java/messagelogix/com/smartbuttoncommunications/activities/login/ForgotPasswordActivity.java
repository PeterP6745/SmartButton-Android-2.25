package messagelogix.com.smartbuttoncommunications.activities.login;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import messagelogix.com.smartbuttoncommunications.R;
import messagelogix.com.smartbuttoncommunications.utils.Config;
import messagelogix.com.smartbuttoncommunications.utils.FunctionHelper;
import messagelogix.com.smartbuttoncommunications.utils.LanguageManager;
import messagelogix.com.smartbuttoncommunications.utils.Preferences;

/**
 * Created by Richard on 10/5/2018.
 */
public class ForgotPasswordActivity extends AppCompatActivity {

    EditText etEmail;
    Button buttonForgotPW;

    Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        LanguageManager.setLocale(this,Preferences.getString(Config.LANGUAGE));

        setTitle(R.string.forgot_password_title);
        setContentView(R.layout.forgot_password_activity);

        etEmail = (EditText) findViewById(R.id.forgotPasswordEditText);

        String preTypedEmail = "";
        preTypedEmail = getIntent().getStringExtra("username");

        etEmail.setText(preTypedEmail);

        buttonForgotPW = (Button) findViewById(R.id.sendForgotPassword);
        buttonForgotPW.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                ForgotPasswordTask forgotPWTask = new ForgotPasswordTask(etEmail.getText().toString().trim());
                forgotPWTask.execute();
            }
        });
    }

    public class ForgotPasswordTask extends AsyncTask<String, Void, String> {

        String mUsername;

        private ForgotPasswordTask(String usrName){
            mUsername = usrName;
        }

        @Override
        protected String doInBackground(String... url) {

            HashMap<String, String> postDataParams = new HashMap<>();
            postDataParams.put("controller", "RedBear");
            postDataParams.put("action", "SendUserPassword");
            postDataParams.put("email", mUsername);

            return FunctionHelper.apiCaller(context, postDataParams);
        }

        @Override
        protected void onPostExecute(final String responseData) {

            if (responseData != null) {
                try {
                    JSONObject responseJsonObject = new JSONObject(responseData);
                    boolean success = responseJsonObject.getBoolean(Config.SUCCESS);
                    if (success) {
                        Log.e("FORGOT PW", "response == " + responseData);
                        JSONObject data = responseJsonObject.getJSONObject(Config.DATA);
                        boolean passwordSent = data.getString("return_value").equals("1");
                        if(passwordSent) {
                            Toast.makeText(context, R.string.forgotpass_toast1, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, R.string.forgotpass_toast2, Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        Toast.makeText(context, R.string.forgotpass_error1, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Log.d("Forgot Pw", "No JSON received ! :(");
            }
        }

        @Override
        protected void onCancelled() {}
    }

}
