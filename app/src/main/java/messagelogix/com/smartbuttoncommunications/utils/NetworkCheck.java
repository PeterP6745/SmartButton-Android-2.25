package messagelogix.com.smartbuttoncommunications.utils;

import android.content.Context;
import android.net.ConnectivityManager;

import java.net.InetAddress;

/**
 * Created by Vahid
 * This is the util class for checking the internet and network connectivity
 */
public class NetworkCheck {

    private Context context;

    public NetworkCheck(Context context) {

        this.context = context;
    }

    public boolean isNetworkConnected() {

        ConnectivityManager cm = (ConnectivityManager) this.context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

    public boolean isInternetAvailable() {

        try {
            InetAddress ipAddr = InetAddress.getByName("google.com"); //You can replace it with your name
            if (ipAddr.equals("")) {
                return false;
            } else {
                return true;
            }
        } catch (Exception e) {
            return false;
        }
    }
}
