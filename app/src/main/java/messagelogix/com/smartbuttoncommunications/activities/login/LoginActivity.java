package messagelogix.com.smartbuttoncommunications.activities.login;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Locale;

import messagelogix.com.smartbuttoncommunications.BuildConfig;
import messagelogix.com.smartbuttoncommunications.R;
import messagelogix.com.smartbuttoncommunications.activities.core.TabBarActivity;
import messagelogix.com.smartbuttoncommunications.utils.Config;
import messagelogix.com.smartbuttoncommunications.utils.FunctionHelper;
import messagelogix.com.smartbuttoncommunications.utils.LanguageManager;
import messagelogix.com.smartbuttoncommunications.utils.LogUtils;
import messagelogix.com.smartbuttoncommunications.utils.MarshMallowPermission;
import messagelogix.com.smartbuttoncommunications.utils.NetworkCheck;
import messagelogix.com.smartbuttoncommunications.utils.Preferences;
import messagelogix.com.smartbuttoncommunications.utils.Strings;

/**
 * Created by Vahid
 * Activity which displays a login screen to the user, offering registration as
 * well. This is where the user login.
 */
public class LoginActivity extends AppCompatActivity {

    private static final String LOG_TAG = LoginActivity.class.getSimpleName();

    final Context context = this;

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    // Values for email and password at the time of the login attempt.
    private String mUsername;

    private String mPassword;

    // UI references.
    private EditText mAccountView;

    private EditText mPasswordView;

    private View mLoginFormView;

    private View mLoginStatusView;

    private TextView mLoginStatusMessageView;

    private MarshMallowPermission marshMallowPermission = new MarshMallowPermission(this);

    private CheckBox mRememberMeCheckBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        LanguageManager.setLocale(this, Preferences.getString(Config.LANGUAGE));

        setContentView(R.layout.activity_login);
        Preferences.init(context);

        mRememberMeCheckBox = (CheckBox) findViewById(R.id.rememberMeCheckBox);
        mRememberMeCheckBox.setChecked(Preferences.getBoolean(Config.REMEMBER_ME));

        Button signInButton = (Button) findViewById(R.id.sign_in_button);
        //Check the network connection
        NetworkCheck networkCheck = new NetworkCheck(context);
        if (!networkCheck.isNetworkConnected()) {
            Toast.makeText(this, getString(R.string.No_Internet), Toast.LENGTH_LONG).show();
            //Hide the spinners in offline mode
            assert signInButton != null;
            //signInButton.setEnabled(false);
        }

        if (!marshMallowPermission.checkPermissionForNetworkState() ||
                !marshMallowPermission.checkPermissionForWifiState()) {
            marshMallowPermission.requestPermissionForNetwork();
        }
        TextView logoTextView = (TextView) findViewById(R.id.smartButtonTextView);
        assert logoTextView != null;
        logoTextView.setText(Html.fromHtml(getString(R.string.smart_button_html)));
        mAccountView = (EditText) findViewById(R.id.account);
        mAccountView.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        if (mAccountView != null) {
            mAccountView.setText(mUsername);
        }
        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView textView, int id,
                                          KeyEvent keyEvent) {

                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        if(Preferences.getBoolean(Config.REMEMBER_ME)){
            if (!Strings.isNullOrEmpty(Preferences.getString(Config.USERNAME))) {
                mAccountView.setText(Preferences.getString(Config.USERNAME));
            }
            if (!Strings.isNullOrEmpty(Preferences.getString(Config.PASSWORD))) {
                mPasswordView.setText(Preferences.getString(Config.PASSWORD));
            }
        }

        mLoginFormView = findViewById(R.id.login_form);
        mLoginStatusView = findViewById(R.id.login_status);
        mLoginStatusMessageView = (TextView) findViewById(R.id.login_status_message);
        assert signInButton != null;
        signInButton.setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {

                        attemptLogin();
                    }
                });
        setTouchListenerForKeyboardDismissal();

        //Forgot Password Button
        Button forgotPasswordButton = (Button) findViewById(R.id.loginForgotPasswordBtn);
        assert forgotPasswordButton != null;
        forgotPasswordButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent forgotPWIntent = new Intent(context, ForgotPasswordActivity.class);
                forgotPWIntent.putExtra("username", mAccountView.getText().toString());
                startActivity(forgotPWIntent);
            }
        });


        new IsSecuredTask().execute();
    }

    public void setTouchListenerForKeyboardDismissal() {

        LinearLayout layout = (LinearLayout) findViewById(R.id.step1);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        super.onCreateOptionsMenu(menu);
        //getMenuInflater().inflate(R.menu.login, menu);
        return true;
    }

    /**DISABLE THE BACK BUTTON TO AVOID UNWANTED BEHAVIOR ON THIS ACTIVITY
     * Simply override the onBackPressed function
     **/
    @Override
    public void onBackPressed() {

    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    public void attemptLogin() {

        if (mAuthTask != null) {
            return;
        }
        // Reset errors.
        mAccountView.setError(null);
        mPasswordView.setError(null);
        // Store values at the time of the login attempt.
        mUsername = mAccountView.getText().toString();
        mPassword = mPasswordView.getText().toString();
        boolean cancel = false;
        View focusView = null;
        // Check for a valid password.
        if (TextUtils.isEmpty(mPassword)) {
            mPasswordView.setError(getString(R.string.error_password_required));
            focusView = mPasswordView;
            cancel = true;
        }
        // Check for a valid email address.
        if (TextUtils.isEmpty(mUsername)) {
            mAccountView.setError(getString(R.string.error_username_required));
            focusView = mAccountView;
            cancel = true;
        } else if (mUsername.length() == 0) {
            mAccountView.setError(getString(R.string.error_invalid_account));
            focusView = mAccountView;
            cancel = true;
        }
        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            mLoginStatusMessageView.setText(R.string.login_progress_signing_in);
            showProgress(true);
            mAuthTask = new UserLoginTask();
            mAuthTask.execute();
        }
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(
                    android.R.integer.config_shortAnimTime);
            mLoginStatusView.setVisibility(View.VISIBLE);
            mLoginStatusView.animate().setDuration(shortAnimTime)
                    .alpha(show ? 1 : 0)
                    .setListener(new AnimatorListenerAdapter() {

                        @Override
                        public void onAnimationEnd(Animator animation) {

                            mLoginStatusView.setVisibility(show ? View.VISIBLE
                                    : View.GONE);
                        }
                    });
            mLoginFormView.setVisibility(View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime)
                    .alpha(show ? 0 : 1)
                    .setListener(new AnimatorListenerAdapter() {

                        @Override
                        public void onAnimationEnd(Animator animation) {

                            mLoginFormView.setVisibility(show ? View.GONE
                                    : View.VISIBLE);
                        }
                    });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class IsSecuredTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... url) {

            HashMap<String, String> postDataParams = new HashMap<>();
            postDataParams.put("controller", "RedBear");
            postDataParams.put("action", "IsSecure");
            Log.d(LOG_TAG, "Try to see if the connection is secured ...");
//            return FunctionHelper.apiCaller(context, postDataParams);
            Log.d(LOG_TAG, "Result: " + FunctionHelper.apiCaller(context, postDataParams));
            return FunctionHelper.apiCaller(context, postDataParams);
        }

        @Override
        protected void onPostExecute(final String responseData) {

            mAuthTask = null;
            showProgress(false);
            if (responseData != null) {
                try {
                    JSONObject responseJsonObject = new JSONObject(responseData);
                    boolean success = responseJsonObject.getBoolean(Config.SUCCESS);
                    if (success) {
                        Log.e(LOG_TAG, "response == isSecured " + responseData);
                    } else {
                        Log.d(LOG_TAG, "failure :(");
                        mAccountView
                                .setError(getString(R.string.error_invalid_account));
                        mPasswordView
                                .setError(getString(R.string.error_incorrect_password));
                        mPasswordView.requestFocus();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Log.d(LOG_TAG, "No JSON received ! :(");
            }
        }

        @Override
        protected void onCancelled() {

            mAuthTask = null;
            showProgress(false);
        }
    }

    public class UserLoginTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... url) {

            HashMap<String, String> postDataParams = new HashMap<>();
            postDataParams.put("controller", "RedBear");
            postDataParams.put("action", "AuthenticateSmartButton");
            postDataParams.put("username", mUsername);
            postDataParams.put("password", mPassword);
            Log.d(LOG_TAG, "login: " + mUsername + " " + mPassword);
//            return FunctionHelper.apiCaller(context, postDataParams);
            return FunctionHelper.apiCaller(context, postDataParams);
        }

        @Override
        protected void onPostExecute(final String responseData) {

            mAuthTask = null;
            showProgress(false);
            if (responseData != null) {
                try {
                    JSONObject responseJsonObject = new JSONObject(responseData);
                    boolean success = responseJsonObject.getBoolean(Config.SUCCESS);
                    if (success) {
                        mAccountView.getText().clear();
                        mPasswordView.getText().clear();
                        Log.e("getUserLoginProfileInfo", "response == " + responseData);
                        JSONObject data = responseJsonObject.getJSONObject(Config.DATA);
                        String accountId = data.getString(Config.ACCOUNT_ID);
                        Preferences.putInteger(Config.LOCATOR_TIMEOUT, data.getInt(Config.LOCATOR_TIMEOUT));
                        Preferences.putString(Config.LOGIN_TYPE, data.getString(Config.LOGIN_TYPE));
                        Preferences.putString(Config.ACCOUNT_ID, accountId);
                        Preferences.putString(Config.CONTACT_EMAIL, data.getString(Config.CONTACT_EMAIL));
                        Preferences.putString(Config.CONTACT_ID, data.getString(Config.CONTACT_ID));
                        Preferences.putInteger(Config.LAST_VERSION_CODE, BuildConfig.VERSION_CODE);
                        Preferences.putBoolean(Config.TEXT_IS_ENABLED, data.getInt(Config.TEXT_IS_ENABLED) == 1);
                        Preferences.putBoolean(Config.FORCE_UPDATE, data.getInt(Config.FORCE_UPDATE) == 1);
                        Preferences.putBoolean(Config.TWO_WAY_TOGGLE, data.getInt(Config.TWO_WAY_TOGGLE) == 1);
                        Preferences.putBoolean(Config.NO_LOC_TOGGLE, false);//data.getInt(Config.NO_LOC_TOGGLE) == 1);
                        Preferences.putBoolean(Config.IS_LOGGED_IN, true);
                        Preferences.putBoolean(Config.ADD_DEFAULT_CONTACTS, data.getInt(Config.ADD_DEFAULT_CONTACTS) == 1);
                        Preferences.putString(Config.USERNAME, mUsername);
                        Preferences.putString(Config.PASSWORD, mPassword);
                        Preferences.putBoolean(Config.REMEMBER_ME, mRememberMeCheckBox.isChecked());

                        Preferences.putBoolean(Config.ROOM_TOGGLE, data.getInt(Config.ROOM_TOGGLE) == 1);
                        Preferences.putBoolean(Config.ALT_LAUNCH, data.getInt(Config.ALT_LAUNCH) == 1);

                        Preferences.putBoolean(Config.HAS_DESKTOPS, data.getInt(Config.HAS_DESKTOPS) == 1);
                        Preferences.putBoolean(Config.SHOULD_ALERT_RSS, data.getInt(Config.SHOULD_ALERT_RSS) == 1);
                        Preferences.putBoolean(Config.IS_SUPER_USER, data.getInt(Config.IS_SUPER_USER) == 1);
                        Preferences.putBoolean(Config.DEC_ONLY, data.getInt(Config.DEC_ONLY) == 1);
                        Preferences.putBoolean(Config.PUSH_TO_APP_ONLY, data.getInt(Config.PUSH_TO_APP_ONLY) == 1);

                        LogUtils.debug("MasterPanicAlert", "LoginActivity - toggles - dec_only: "+Preferences.getBoolean(Config.DEC_ONLY)+"\n sb_desktop_rssfeed: "+Preferences.getBoolean(Config.SHOULD_ALERT_RSS));

                        Preferences.putBoolean(Config.HAS_TRANSLATIONS, data.getInt(Config.HAS_TRANSLATIONS) == 1);
                        Preferences.putBoolean(Config.COVIDSCREENING_ACCOUNT, data.getInt(Config.COVIDSCREENING_ACCOUNT) == 1);
                        Preferences.putBoolean(Config.QUESTIONNAIRE_ACCOUNT, data.getInt(Config.QUESTIONNAIRE_ACCOUNT) == 1);
//                        LogUtils.debug("[Toggles]","Value of toggle directly from backend --> "+data.getInt(Config.QUESTIONNAIRE_ACCOUNT));
//                        LogUtils.debug("[Toggles]","QUESTIONNAIRE ACCOUNT --> "+Preferences.getBoolean(Config.QUESTIONNAIRE_ACCOUNT));

                        Preferences.putBoolean(Config.GEOFENCE_TOGGLE, data.getInt(Config.GEOFENCE_TOGGLE) == 1);
                        Preferences.putInteger(Config.SB_PRESS_LENGTH, data.getInt(Config.SB_PRESS_LENGTH));
                        //Preferences.putBoolean(Config.GEOFENCE_TOGGLE, true);

                        Log.e("[Toggles]", "Has Desktops: " + Preferences.getBoolean(Config.HAS_DESKTOPS));
                        Log.e("[Toggles]", "Super User: " + Preferences.getBoolean(Config.IS_SUPER_USER));
                        Log.e("[Toggles]", "Should Alert RSS: " + Preferences.getBoolean(Config.SHOULD_ALERT_RSS));
                        Log.e("[Toggles]", "Dec Only: " + Preferences.getBoolean(Config.DEC_ONLY));
                        Log.e("[Toggles]", "Push to app only: " + Preferences.getBoolean(Config.PUSH_TO_APP_ONLY));
                        Log.e("[Toggles]", "Geofence Feature: " + Preferences.getBoolean(Config.GEOFENCE_TOGGLE));
                        if(Preferences.getBoolean(Config.GEOFENCE_TOGGLE)){
                            //Initiate the IN_ACTIVE_ZONE bool
                            Log.e("GEOFENCE", "Initiating the IN_ACTIVE_ZONE toggle to FALSE\nSystem will monitor if the user enters the GEOFENCE ACTIVE ZONE and update this toggle when the user enters the ACTIVE ZONE");
                            Preferences.putBoolean(Config.GEOFENCE_IN_ACTIVE_ZONE, false);
                        }
                        //Email confirmation if it is the first time that they login
//                        if (mPassword.equals(Config.DEFAULT_PASSWORD)) {
//                            Preferences.putBoolean(Config.PASSWORD_RESET_FLAG, false);
//                            Intent emailConfirmIntent = new Intent(context, EmailConfirmActivity.class);
//                            startActivity(emailConfirmIntent);
//                        } else {
                            Preferences.putBoolean(Config.PASSWORD_RESET_FLAG, true);

                            Intent intent = new Intent(context, TabBarActivity.class);
                            startActivity(intent);
                       //}
                    } else {
                        mAccountView
                                .setError(getString(R.string.error_invalid_account));
                        mPasswordView
                                .setError(getString(R.string.error_incorrect_password));
                        mPasswordView.requestFocus();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Log.d(LOG_TAG, "No JSON received ! :(");
            }
        }

        @Override
        protected void onCancelled() {

            mAuthTask = null;
            showProgress(false);
        }
    }
}

