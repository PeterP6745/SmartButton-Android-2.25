package messagelogix.com.smartbuttoncommunications.utils;

import android.text.TextUtils;

/**
 * Created by Vahid
 * This is the util class for validation
 */
public class Validation {

    public static boolean isValidEmail(CharSequence target) {

        if (TextUtils.isEmpty(target)) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

    public static boolean isConfirmed(String original, String confirmation) {

        if (TextUtils.isEmpty(original) || TextUtils.isEmpty(confirmation)) {
            return false;
        } else if (original.equals(confirmation)) {
            return true;
        } else {
            return false;
        }
    }
}
