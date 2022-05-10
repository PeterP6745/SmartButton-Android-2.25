package messagelogix.com.smartbuttoncommunications.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;

import java.util.Locale;
import messagelogix.com.smartbuttoncommunications.R;

public class LanguageManager {
    public static void setLocale(Activity activity, String languageCode) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);
        Resources resources = activity.getResources();
        Configuration config = resources.getConfiguration();
        config.locale=locale;
        resources.updateConfiguration(config, resources.getDisplayMetrics());
    }

    public static void setDefaultLocale(Context context, String langCode) {
        LogUtils.debug("DefaultLanguageManager","setDefaultLocale() - langCode is --> "+langCode);
        Locale newLocale = new Locale(langCode);
        LogUtils.debug("LanguageManagerDefault","setDefaultLocale() - newLocale before update --> "+newLocale);
        Locale.setDefault(newLocale);
        LogUtils.debug("DefaultLanguageManager","setDefaultLocale() - newLocale after update --> "+newLocale);
        Configuration config = new Configuration();
        LogUtils.debug("DefaultLanguageManager","setDefaultLocale() - config.locale before update is --> "+config.locale);
        config.locale = newLocale;
        LogUtils.debug("DefaultLanguageManager","setDefaultLocale() - config.locale after update is --> "+config.locale);
        Resources res = context.getResources();
        res.updateConfiguration(config, res.getDisplayMetrics());
    }
}
