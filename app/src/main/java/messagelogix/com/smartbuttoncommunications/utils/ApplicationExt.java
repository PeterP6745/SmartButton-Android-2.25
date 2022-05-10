package messagelogix.com.smartbuttoncommunications.utils;

import android.app.Application;
import android.os.Environment;
import android.util.Log;

import java.io.File;

/**
 * Created by Richard on 6/17/2016.
 */
public class ApplicationExt extends Application {

    // NOTE: the content of this path will be deleted
    //       when the application is uninstalled (Android 2.2 and higher)
    protected File extStorageAppBasePath;

    protected File extStorageAppCachePath;

    private String LOG_TAG = ApplicationExt.class.getSimpleName();

    @Override
    public void onCreate() {

        super.onCreate();
        Log.e(LOG_TAG, "onCreate");
        // Check if the external storage is writeable
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            // Retrieve the base path for the application in the external storage
            File externalStorageDir = Environment.getExternalStorageDirectory();
            if (externalStorageDir != null) {
                // /storage/emulated/0/Android/data/messagelogix.com.smartbutton
                extStorageAppBasePath = new File(externalStorageDir.getAbsolutePath() +
                        File.separator + "Android" + File.separator + "data" +
                        File.separator + getPackageName());
                Log.e(LOG_TAG, "extStorageAppBasePath = " + extStorageAppBasePath);
            }
            if (extStorageAppBasePath != null) {
                // /storage/emulated/0/Android/data/messagelogix.com.smartbutton/cache
                extStorageAppCachePath = new File(extStorageAppBasePath.getAbsolutePath() +
                        File.separator + "cache");
                boolean isCachePathAvailable = true;
                if (!extStorageAppCachePath.exists()) {
                    Log.e(LOG_TAG, "Create the cache path on the external storage");
                    isCachePathAvailable = extStorageAppCachePath.mkdirs();
                }
                if (!isCachePathAvailable) {
                    Log.e(LOG_TAG, "Unable to create the cache path");
                    extStorageAppCachePath = null;
                }
            }
        } else {
            Log.e(LOG_TAG, "Ext Storage is not writable");
        }
    }

    @Override
    public File getCacheDir() {
        // NOTE: this method is used in Android 2.2 and higher
        if (extStorageAppCachePath != null) {
            // Use the external storage for the cache
            return extStorageAppCachePath;
        } else {
            // /data/data/com.devahead.androidwebviewcacheonsd/cache
            return super.getCacheDir();
        }
    }
}
