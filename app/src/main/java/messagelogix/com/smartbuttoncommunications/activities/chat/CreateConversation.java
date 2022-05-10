package messagelogix.com.smartbuttoncommunications.activities.chat;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.text.Html;
import android.widget.Button;

import messagelogix.com.smartbuttoncommunications.utils.Preferences;

/**
 * Created by Vahid
 * This is the activity that creates a new conversation
 */
public class CreateConversation {

    String LOG_TAG = this.getClass().getSimpleName();

    AlertDialog dialog;
//
//    AlertDialog dialogn;
//
//    AlertDialog.Builder builder;
//
//    AlertDialog.Builder buildern;
//
//    private String EULA_PREFIX = "eula_";

    private Activity mActivity;

    public CreateConversation(Activity context) {

        mActivity = context;
        Preferences.init(context);
    }
//
//    private PackageInfo getPackageInfo() {
//
//        PackageInfo pi = null;
//        try {
//            pi = mActivity.getPackageManager().getPackageInfo(mActivity.getPackageName(), PackageManager.GET_ACTIVITIES);
//        } catch (PackageManager.NameNotFoundException e) {
//            e.printStackTrace();
//        }
//        return pi;
//    }

    public void show() {
//        PackageInfo versionInfo = getPackageInfo();
//        // the eulaKey changes every time you increment the version number in the AndroidManifest.xml
//        final String eulaKey = EULA_PREFIX + versionInfo.versionCode;
//        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mActivity);
//        boolean hasBeenShown = prefs.getBoolean(eulaKey, false);
//        if (hasBeenShown == false) {
        // Show the Eula
//            String title = mActivity.getString(R.string.app_name) + " v" + versionInfo.versionName;
        String title = "Create conversation";
        //Includes the updates as well so users know what changed.
        String message = "This is the fname";
//            Log.d(LOG_TAG, "Policy Content: " + Preferences.getString(Config.CONTACT_POLICY_CONTENT));
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(Html.fromHtml("Create"), new Dialog.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // Mark this version as read. User agrees
//                            new UpdatePolicyTask().execute();
                        //Send the info to database
                        dialogInterface.dismiss();
                    }
                })
                .setNegativeButton(android.R.string.cancel, new Dialog.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();
                        // Close the activity as they have declined the EULA. User denies
//                            AlertDialog.Builder buildern = new AlertDialog.Builder(mActivity)
//                                    .setTitle("Are you sure?")
//                                    .setPositiveButton("Yes", new Dialog.OnClickListener() {
//
//                                        @Override
//                                        public void onClick(DialogInterface dialogInterface, int i) {
//
//                                            Preferences.putBoolean(Config.IS_LOGGED_IN, false);
//                                            Intent intent = new Intent(mActivity, MainActivity.class);
//                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                                            mActivity.startActivity(intent);
//                                        }
//                                    })
//                                    .setNegativeButton("No", new Dialog.OnClickListener() {
//
//                                        @Override
//                                        public void onClick(DialogInterface dialog, int which) {
//
//                                            new CreateConversation(mActivity).show();
//                                        }
//                                    });
//                            dialogn = buildern.create();
//                            dialogn.setCanceledOnTouchOutside(false);
//                            dialogn.show();
//                            Button nbutton = dialogn.getButton(DialogInterface.BUTTON_NEGATIVE);
//                            nbutton.setTextColor(Color.RED);
//                            Button pbutton = dialogn.getButton(DialogInterface.BUTTON_POSITIVE);
//                            pbutton.setTextColor(Color.GREEN);
                    }
                });
        dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        Button nbutton = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        nbutton.setTextColor(Color.RED);
        Button pbutton = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
        pbutton.setTextColor(Color.GREEN);
//        }
    }
//    public class UpdatePolicyTask extends AsyncTask<String, Void, String> {
//
//        @Override
//        protected String doInBackground(String... url) {
//
//            HashMap<String, String> postDataParams = new HashMap<>();
//            postDataParams.put("controller", "User");
//            postDataParams.put("action", "AgreeTerms");
//            postDataParams.put("contactId", Preferences.getString(Config.CONTACT_ID));
//            return FunctionHelper.apiCaller(mActivity, postDataParams);
//        }
//
//        @Override
//        protected void onPostExecute(final String responseData) {
//
//            if (responseData != null) {
//                try {
//                    JSONObject responseJsonObject = new JSONObject(responseData);
//                    boolean success = responseJsonObject.getBoolean(Config.SUCCESS);
//                    if (success) {
//                        //Update successful
//                        Preferences.putString(Config.CONTACT_POLICY, "1");
//                        Log.d(LOG_TAG, "Policy update was successful!");
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            } else {
//                Log.d(LOG_TAG, "No JSON received ! :(");
//            }
//        }
//    }
}