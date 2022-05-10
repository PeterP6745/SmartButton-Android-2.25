package messagelogix.com.smartbuttoncommunications.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.widget.Button;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import messagelogix.com.smartbuttoncommunications.R;
import messagelogix.com.smartbuttoncommunications.activities.core.MainActivity;
import messagelogix.com.smartbuttoncommunications.activities.login.EmailConfirmActivity;

/**
 * Created by Vahid
 * This is the util class that we use for showing the disclaimer page
 */
public class EULA {

    String LOG_TAG = this.getClass().getSimpleName();

    AlertDialog dialog;

    AlertDialog dialogn;

    AlertDialog.Builder builder;

    AlertDialog.Builder buildern;

    private String EULA_PREFIX = "eula_";

    private Activity mActivity;

    public EULA(Activity context) {

        mActivity = context;
        Preferences.init(context);
    }

    private PackageInfo getPackageInfo() {

        PackageInfo pi = null;
        try {
            pi = mActivity.getPackageManager().getPackageInfo(mActivity.getPackageName(), PackageManager.GET_ACTIVITIES);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return pi;
    }

    public void show() {

        PackageInfo versionInfo = getPackageInfo();
        // the eulaKey changes every time you increment the version number in the AndroidManifest.xml
        final String eulaKey = EULA_PREFIX + versionInfo.versionCode;
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mActivity);
        boolean hasBeenShown = prefs.getBoolean(eulaKey, false);
        if(hasBeenShown == false) {
            // Show the Eula
            String title = mActivity.getString(R.string.app_name) + " v" + versionInfo.versionName;
            //Includes the updates as well so users know what changed.
            Spanned message = Html.fromHtml(Preferences.getString(Config.CONTACT_POLICY_CONTENT));
            Log.d(LOG_TAG, "Policy Content: " + Preferences.getString(Config.CONTACT_POLICY_CONTENT));
            AlertDialog.Builder builder = new AlertDialog.Builder(mActivity)
                    .setTitle(title)
                    .setMessage(message)
                    .setPositiveButton(Html.fromHtml(mActivity.getResources().getString(R.string.generic_buttontext_accept1)), new Dialog.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            // Mark this version as read. User agrees
                            new UpdatePolicyTask().execute();
                            dialogInterface.dismiss();
                            //getRemainingProfileInfo();
                            if(Preferences.getString(Config.PASSWORD).equals(Config.DEFAULT_PASSWORD)){
                                LogUtils.debug("OnboardingTag","showing email/password reset screens");
                                Preferences.putBoolean(Config.PASSWORD_RESET_FLAG, false);
                                Intent emailConfirmIntent = new Intent(mActivity, EmailConfirmActivity.class);
                                mActivity.startActivity(emailConfirmIntent);
                            }
                        }
                    })
                    .setNegativeButton(android.R.string.cancel, new Dialog.OnClickListener() {

                        @Override
                        public void onClick(final DialogInterface dialog, int which) {
                            // Close the activity as they have declined the EULA. User denies
                            AlertDialog.Builder buildern = new AlertDialog.Builder(mActivity)
                                    .setTitle("Are you sure?")
                                    .setPositiveButton("Yes", new Dialog.OnClickListener() {

                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            Preferences.putBoolean(Config.IS_LOGGED_IN, false);
                                            Intent intent = new Intent(mActivity, MainActivity.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            mActivity.startActivity(intent);
                                        }
                                    })
                                    .setNegativeButton("No", new Dialog.OnClickListener() {

                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                            new EULA(mActivity).show();
                                        }
                                    });
                            dialogn = buildern.create();
                            dialogn.setCanceledOnTouchOutside(false);
                            dialogn.show();
                            Button nbutton = dialogn.getButton(DialogInterface.BUTTON_NEGATIVE);
                            nbutton.setTextColor(Color.RED);
                            Button pbutton = dialogn.getButton(DialogInterface.BUTTON_POSITIVE);
                            pbutton.setTextColor(Color.GREEN);
                        }
                    });
            dialog = builder.create();
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
            Button nbutton = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
            nbutton.setTextColor(Color.RED);
            Button pbutton = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
            pbutton.setTextColor(Color.GREEN);
        }
    }

//    public void dismiss() {
//        if (dialog !=null && dialog.isShowing() ){
//            dialog.cancel();
//        }
//    }

    public class UpdatePolicyTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... url) {

            HashMap<String, String> postDataParams = new HashMap<>();
            postDataParams.put("controller", "RedBear");
            postDataParams.put("action", "AgreeTerms");
            postDataParams.put("contactId", Preferences.getString(Config.CONTACT_ID));
            return FunctionHelper.apiCaller(mActivity, postDataParams);
        }

        @Override
        protected void onPostExecute(final String responseData) {

            if (responseData != null) {
                try {
                    JSONObject responseJsonObject = new JSONObject(responseData);
                    boolean success = responseJsonObject.getBoolean(Config.SUCCESS);
                    if (success) {
                        //Update successful
                        Preferences.putString(Config.CONTACT_POLICY, "1");
                        Log.d(LOG_TAG, "Policy update was successful!");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Log.d(LOG_TAG, "No JSON received ! :(");
            }
        }
    }
}