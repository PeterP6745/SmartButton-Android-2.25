package messagelogix.com.smartbuttoncommunications.utils;

import android.util.Log;

import messagelogix.com.smartbuttoncommunications.BuildConfig;

public class LogUtils {

    public static void debug(final String tag, String message) {
        if(BuildConfig.DEBUG) {
            Log.d(tag, message);
        }
    }
}
