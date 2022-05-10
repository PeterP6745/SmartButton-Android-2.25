package messagelogix.com.smartbuttoncommunications.utils;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.widget.Button;

public class DialConfirmation {

    MarshMallowPermission marshMallowPermission;

    String LOG_TAG = this.getClass().getSimpleName();

    AlertDialog dialog;

    AlertDialog dialogn;

    AlertDialog.Builder builder;

    AlertDialog.Builder buildern;

    private String EULA_PREFIX = "conf_";

    private Activity mActivity;

    public DialConfirmation(Activity context) {

        mActivity = context;
        Preferences.init(context);
        marshMallowPermission = new MarshMallowPermission(mActivity);
        Preferences.init(mActivity);
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

    private void requestDialPermission() {

        ActivityCompat.requestPermissions(mActivity,
                new String[]{Manifest.permission.CALL_PHONE},
                2);
    }

    private void dial() {

        Uri number = Uri.parse("tel:911");
        Intent callIntent = new Intent(Intent.ACTION_CALL, number);
        if (!marshMallowPermission.checkPermissionForCall()) {
            requestDialPermission();
        }
        if (marshMallowPermission.checkPermissionForCall()) {
            //new Track911Task().execute();
            mActivity.startActivity(callIntent);
        }
    }

    public void show() {

        PackageInfo versionInfo = getPackageInfo();
        // the eulaKey changes every time you increment the version number in the AndroidManifest.xml
        final String eulaKey = EULA_PREFIX + versionInfo.versionCode;
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mActivity);
        boolean hasBeenShown = prefs.getBoolean(eulaKey, false);
        if (!hasBeenShown) {
            // Show the Eula
            String title = "Confirmation";
            //Includes the updates as well so users know what changed.
            String message = "Are you sure you want to dial 911?";
            AlertDialog.Builder builder = new AlertDialog.Builder(mActivity)
                    .setTitle(title)
                    .setMessage(message)
                    .setPositiveButton(android.R.string.ok, new Dialog.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            // Mark this version as read. User agrees
                            dial();
                            dialogInterface.dismiss();
                        }
                    })
                    .setNegativeButton(android.R.string.cancel, new Dialog.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            dialog.dismiss();
                        }
                    });
            dialog = builder.create();
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
            Button nbutton = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
            nbutton.setTextColor(Color.RED);
            Button pbutton = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
            pbutton.setTextColor(Color.GREEN);
        }
    }
    //Log 911 call
    //Kick out async task
}