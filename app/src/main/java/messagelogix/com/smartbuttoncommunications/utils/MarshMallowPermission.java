package messagelogix.com.smartbuttoncommunications.utils;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import messagelogix.com.smartbuttoncommunications.R;

/**
 * Created by Vahid
 * This is the util class for handling the Marshmallow permission requests and checks
 */
public class MarshMallowPermission {

    public static final int RECORD_PERMISSION_REQUEST_CODE = 100;

    public static final int EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE = 200;

    public static final int CAMERA_PERMISSION_REQUEST_CODE = 300;

    public static final int COARSE_LOCATION_REQUEST_CODE = 400;

    public static final int FINE_LOCATION_REQUEST_CODE = 500;

    public static final int LOCATION_REQUEST_CODE = 600;

    Activity activity;

    public MarshMallowPermission(Activity activity) {

        this.activity = activity;
    }

    public boolean checkPermissionForRecord() {

        if (Build.VERSION.SDK_INT < 23) {
            return true;
        }
        int result = ContextCompat.checkSelfPermission(activity, Manifest.permission.RECORD_AUDIO);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    public boolean checkPermissionForAccessFine() {

        if (Build.VERSION.SDK_INT < 23) {
            return true;
        }
        int result = ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    public boolean checkPermissionForAccessCoarse() {

        if (Build.VERSION.SDK_INT < 23) {
            return true;
        }
        int result = ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    public boolean checkPermissionForExternalStorage() {

        if (Build.VERSION.SDK_INT < 23) {
            return true;
        }
        int result = ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    public boolean checkPermissionForCamera() {

        if (Build.VERSION.SDK_INT < 23) {
            return true;
        }
        int result = ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    public boolean checkPermissionForWifiState() {

        if (Build.VERSION.SDK_INT < 23) {
            return true;
        }
        int result = ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_WIFI_STATE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    public boolean checkPermissionForNetworkState() {

        if (Build.VERSION.SDK_INT < 23) {
            return true;
        }
        int result = ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_NETWORK_STATE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    public void requestPermissionForNetwork() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_WIFI_STATE) ||
                ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_NETWORK_STATE)) {
            Toast.makeText(activity, R.string.mm_permissions_warning8, Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_WIFI_STATE
                    , Manifest.permission.ACCESS_NETWORK_STATE
            }, EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE);
        }
    }

    public boolean checkPermissionForCoarseLocation() {

        if (Build.VERSION.SDK_INT < 23) {
            return true;
        }
        int result = ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    public boolean checkPermissionForFineLocation() {

        if (Build.VERSION.SDK_INT < 23) {
            return true;
        }
        int result = ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    public boolean checkPermissionForCall() {

        if (Build.VERSION.SDK_INT < 23) {
            return true;
        }
        int result = ContextCompat.checkSelfPermission(activity, Manifest.permission.CALL_PHONE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    public boolean checkPermissionForContact() {

        if (Build.VERSION.SDK_INT < 23) {
            return true;
        }
        int result = ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_CONTACTS);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    public boolean checkPermissionForInternet() {

        if (Build.VERSION.SDK_INT < 23) {
            return true;
        }
        int result = ContextCompat.checkSelfPermission(activity, Manifest.permission.INTERNET);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    public void requestPermissionForCall() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.RECORD_AUDIO)) {
            Toast.makeText(activity, R.string.mm_permissions_warning7, Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.RECORD_AUDIO}, RECORD_PERMISSION_REQUEST_CODE);
        }
    }

    public void requestPermissionForRecord() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.RECORD_AUDIO)) {
            Toast.makeText(activity, R.string.mm_permissions_warning6, Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.RECORD_AUDIO}, RECORD_PERMISSION_REQUEST_CODE);
        }
    }

    public void requestPermissionForExternalStorage() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Toast.makeText(activity, R.string.mm_permissions_warning5, Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE);
        }
    }

    public void requestPermissionForCamera() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.CAMERA)) {
            Toast.makeText(activity, R.string.mm_permissions_warning4, Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
        }
    }

    public void requestPermissionForCoarseLocation() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_COARSE_LOCATION)) {
            Toast.makeText(activity, R.string.mm_permissions_warning3, Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, COARSE_LOCATION_REQUEST_CODE);
        }
    }

    public void requestPermissionForFineLocation() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_FINE_LOCATION)) {
            Toast.makeText(activity, R.string.mm_permissions_warning2, Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, FINE_LOCATION_REQUEST_CODE);
        }
    }

    public void requestPermissionForLocation() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_FINE_LOCATION) ||
                ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_COARSE_LOCATION)) {
            Toast.makeText(activity, R.string.mm_permissions_warning2, Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_REQUEST_CODE);
        }
    }
//    public void requestPermissionForHome() {
//
//        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_FINE_LOCATION) ||
//                ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_COARSE_LOCATION) ||
//                ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.CALL_PHONE) ||
//                ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.INTERNET)) {
//            Toast.makeText(activity, "Location and phone call permission are required.", Toast.LENGTH_LONG).show();
//        } else {
//            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION
//                    , Manifest.permission.ACCESS_COARSE_LOCATION
//                    , Manifest.permission.CALL_PHONE}
//                    , LOCATION_REQUEST_CODE)
//            ;
//        }
//    }

    public void requestPermissionsForApp() {
        LogUtils.debug("[TabBarActivity]","inside MarshaMallowPermission class --> requestPermissionsForApp()");

        if (
                ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_FINE_LOCATION) ||
                        ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_COARSE_LOCATION) ||
                        ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.CALL_PHONE) ||
                        ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.INTERNET) ||
                        ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) ||
                        ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.CAMERA) ||
                        ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.READ_CONTACTS)) {
            Toast.makeText(activity, R.string.mm_permissions_warning1, Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(activity, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION
                    , Manifest.permission.ACCESS_COARSE_LOCATION
                    , Manifest.permission.CALL_PHONE
                    , Manifest.permission.INTERNET
                    , Manifest.permission.WRITE_EXTERNAL_STORAGE
                    , Manifest.permission.CAMERA
                    , Manifest.permission.READ_CONTACTS
            }, EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE);
        }
    }

    public void requestPermissionsForAppNoLoc() {

        if (            ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.CALL_PHONE) ||
                        ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.INTERNET) ||
                        ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) ||
                        ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.CAMERA) ||
                        ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.READ_CONTACTS)) {
            Toast.makeText(activity, R.string.mm_permissions_warning1, Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(activity, new String[]{
                    Manifest.permission.CALL_PHONE
                    , Manifest.permission.INTERNET
                    , Manifest.permission.WRITE_EXTERNAL_STORAGE
                    , Manifest.permission.CAMERA
                    , Manifest.permission.READ_CONTACTS
            }, EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE)
            ;
        }
    }

}