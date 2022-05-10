package messagelogix.com.smartbuttoncommunications.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;

import com.google.gson.Gson;

import javax.crypto.spec.SecretKeySpec;

import messagelogix.com.smartbuttoncommunications.crypto.RSA;

/**
 * Created by Vahid
 * This class is a helper to store data to shared preferences
 * some sensitive values need to be encrypted in method putString()
 * You must add logic to decrypt them when retrieving them back
 * in the method getString()
 */
public class Preferences {

    public static final String SHARED_PREFERENCES = "com.messagelogix.anonymousalerts";

    public static final String RSA_GENERATED = "com.anonymousalerts.RSA_GENERATED";

    public static final String RSA_PUBLIC_KEY = "com.anonymousalerts.RSA_PUBLIC_KEY";

    public static final String RSA_PRIVATE_KEY = "com.anonymousalerts.RSA_PRIVATE_KEY";

    private static final String LOG_TAG = Preferences.class.getSimpleName();

    public static SharedPreferences mPreferences;

    public static void init(Context context) {
        mPreferences = context.getSharedPreferences(SHARED_PREFERENCES, 0);
    }

    public static void setToNull() {
        mPreferences = null;
    }

    public static SharedPreferences getSPInstance(Context context) {

        if(mPreferences == null) {
            init(context);
        }

        return mPreferences;
    }

    //Check existence
    public static boolean isAvailable(String key) {
        
        if (mPreferences.contains(key)) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isAvailableTest(String key) {
        mPreferences = null;
        if (mPreferences.contains(key)) {
            return true;
        } else {
            return false;
        }
    }

    public static void clearProfilePicture() {
        Preferences.remove(Config.USER_PROFILE_PICTURE);
    }

    public static void putObject(String key, Object object) {

        Gson gson = new Gson();
        String json = gson.toJson(object); // myObject - instance of MyObject
        mPreferences.edit().putString(key, json).apply();
    }

    public static Object getObject(String key) {

        Gson gson = new Gson();
        String json = mPreferences.getString(key, "");
        Object obj = gson.fromJson(json, Object.class);
        return obj;
    }

    public static void putSecretKeySpec(SecretKeySpec secretKeySpec) {

        String secretKey = Base64.encodeToString(secretKeySpec.getEncoded(), Base64.DEFAULT);
        mPreferences.edit().putString(SecretKeySpec.class.getName(), secretKey).apply();
    }

    public static SecretKeySpec getSecretKeySpec() {

        String secretKey = mPreferences.getString(SecretKeySpec.class.getName(), "");
        SecretKeySpec key;
        if (!com.google.api.client.repackaged.com.google.common.base.Strings.isNullOrEmpty(secretKey)) {
            byte[] decodedKey = Base64.decode(secretKey, Base64.DEFAULT);
            key = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
        } else {
            key = new Cryptography(Cryptography.AES).initializeAES();
            putSecretKeySpec(key);
        }
        return key;
    }

    public static String getString(String key) {

        String value = mPreferences.getString(key, "");
//        if(key == "language")
//            value = null;

        switch (key) {
            case Config.LANGUAGE: {
                LogUtils.debug("PreferencesLang","before check key is: "+value);
                if (Strings.isNullOrEmpty(value)) {
                    value = "en";
                }
                LogUtils.debug("PreferencesLang","after check key is: "+value);
                break;
            }
            case Config.ACCOUNT_ID:
            case Config.UNIQUE_ID:
            case Config.DISPLAY_NAME:
            case Config.USER_FULL_NAME:
            case Config.USER_TITLE_NAME:
            case Config.USER_BUILDING_NAME:
            case Config.USERNAME:
            case Config.PASSWORD:
                return Strings.isNullOrEmpty(value) ? value : RSA.decryptWithStoredKey(value);
        }

        return value;
    }

    public static void putString(String key, String s) {

        switch (key) {
            case Config.ACCOUNT_ID:
            case Config.UNIQUE_ID:
            case Config.DISPLAY_NAME:
            case Config.USER_FULL_NAME:
            case Config.USER_TITLE_NAME:
            case Config.USER_BUILDING_NAME:
            case Config.USERNAME:
            case Config.PASSWORD:
                if (!Strings.isNullOrEmpty(s)) {
                    s = RSA.encryptWithStoredKey(s);
                }
                break;
        }
        mPreferences.edit().putString(key, s).apply();
    }

    public static boolean getBoolean(String key) {
        if(mPreferences!=null){
        return mPreferences.getBoolean(key, false);}
        return false;
    }

    public static boolean getBoolean(String key, boolean defaultValue) {

        return mPreferences.getBoolean(key, defaultValue);
    }

    public static void putBoolean(String key, boolean bool) {

        mPreferences.edit().putBoolean(key, bool).apply();
    }

    public static void putInteger(String key, Integer integer) {

        mPreferences.edit().putInt(key, integer).apply();
    }

    public static void putLong(String key, long longValue) {

        mPreferences.edit().putLong(key, longValue).apply();
    }

    public static int getInteger(String key) {

        return mPreferences.getInt(key, 0);
    }

    public static int getInteger(String key, int defaultValue) {

        return mPreferences.getInt(key, defaultValue);
    }

    public static long getLong(String key) {

        return mPreferences.getLong(key, 0);
    }

    public static void clear() {

        mPreferences.edit().clear().apply();
    }

    public static void remove(String key) {

        mPreferences.edit().remove(key).apply();
    }
}
