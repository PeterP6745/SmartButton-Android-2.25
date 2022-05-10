package messagelogix.com.smartbuttoncommunications.utils;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import messagelogix.com.smartbuttoncommunications.BuildConfig;

/**
 * Created by Vahid
 * This util class is handling the API calls for the app
 */
public class FunctionHelper {

    private static final String LOG_TAG = FunctionHelper.class.getSimpleName();

    public static Context mContext;

    public FunctionHelper(Context context) {

        this.mContext = context;
    }

    public static String apiCaller(Context context, HashMap<String, String> postDataParams) {

        NetworkCheck networkCheck = new NetworkCheck(context);
        if (networkCheck.isNetworkConnected()) {
            postDataParams.put("api_key", Config.API_KEY);
            postDataParams.put("app_id", Config.APP_ID);
            return httpPost(Config.API_URL, postDataParams);
        } else {
            Log.d(LOG_TAG, "Internet is not connected.");
            return null;
        }
    }

    public static String apiCallerTest(Context context, HashMap<String, String> postDataParams) {

        NetworkCheck networkCheck = new NetworkCheck(context);
        if (networkCheck.isNetworkConnected()) {
            postDataParams.put("api_key", Config.API_KEY);
            postDataParams.put("app_id", Config.APP_ID);
            return httpPost(Config.API_URL_TEST, postDataParams);
        } else {
            Log.d(LOG_TAG, "Internet is not connected.");
            return null;
        }
    }

    public static String httpPost(String urlString, HashMap<String, String> postDataParams) {

        URL url;
        String response = "";
        try {
            url = new URL(urlString);
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            LogUtils.debug("calledBackendProcedure",getPostDataString(postDataParams));
            writer.write(getPostDataString(postDataParams));

            writer.flush();
            writer.close();
            os.close();
            int responseCode = conn.getResponseCode();
            LogUtils.debug("ResponseCode","respones code is: "+responseCode);
            if (responseCode == HttpsURLConnection.HTTP_OK) {
                String line;
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((line = br.readLine()) != null) {
                    response += line;
                }
            } else {
                response = "";
                //throw new HttpException(responseCode+"");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (BuildConfig.DEBUG) {
            //Log.d(LOG_TAG, "response = " + response);
        }
        return response;
    }

    public static String getPostDataString(HashMap<String, String> params) {

        StringBuilder result = new StringBuilder();
        try {
            boolean first = true;
            for (Map.Entry<String, String> entry : params.entrySet()) {
                if (first)
                    first = false;
                else
                    result.append("&");
                result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
                result.append("=");
                result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return result.toString();
    }

    public static boolean isNullOrEmpty(String str) {
        return str == null || str.isEmpty() || str.trim().length() == 0;
    }
}
