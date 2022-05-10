package messagelogix.com.smartbuttoncommunications.activities.core;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Spinner;
import android.widget.Toast;

import java.security.KeyPair;
import java.util.Locale;

import messagelogix.com.smartbuttoncommunications.R;
import messagelogix.com.smartbuttoncommunications.activities.login.EmailConfirmActivity;
import messagelogix.com.smartbuttoncommunications.activities.login.LoginActivity;
import messagelogix.com.smartbuttoncommunications.crypto.Crypto;
import messagelogix.com.smartbuttoncommunications.crypto.RSA;
import messagelogix.com.smartbuttoncommunications.utils.Config;
import messagelogix.com.smartbuttoncommunications.utils.LanguageManager;
import messagelogix.com.smartbuttoncommunications.utils.LogUtils;
import messagelogix.com.smartbuttoncommunications.utils.NetworkCheck;
import messagelogix.com.smartbuttoncommunications.utils.Preferences;
import messagelogix.com.smartbuttoncommunications.utils.Strings;

/**
 * Created by Vahid
 * This is the main activity of the app
 */
public class MainActivity extends Activity {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    private final Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        LogUtils.debug("[MainActivity]","onCreate");

//        Bundle extras = getIntent().getExtras();
//        LogUtils.debug("MainActivityExtras", "onCreate - extras: " + extras);

        Preferences.init(this);
        //LanguageManager.setLocale(this,Preferences.getString(Config.LANGUAGE));
        //LanguageManager.setDefaultLocale(this,Preferences.getString(Config.LANGUAGE));

        setContentView(R.layout.activity_main);
        checkNetwork();
        checkRsaKeys();
        //checkLogin();
        //log();
    }

    private void checkNetwork() {
        //Check the network connection
        NetworkCheck networkCheck = new NetworkCheck(context);
        if (!networkCheck.isNetworkConnected()) {
            Toast.makeText(this, getString(R.string.Internet_Connection), Toast.LENGTH_LONG).show();
        }
    }

    private void checkRsaKeys() {
        if (Strings.isNullOrEmpty(Preferences.getString(Preferences.RSA_PUBLIC_KEY)) ||
                Strings.isNullOrEmpty(Preferences.getString(Preferences.RSA_PRIVATE_KEY))) {
            KeyPair keyPair = RSA.generate();
            Crypto.writePublicKeyToPreferences(keyPair);
            Crypto.writePrivateKeyToPreferences(keyPair);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        LogUtils.debug("[MainActivity]","onStart");
        checkLogin();
    }

    @Override
    protected void onResume() {
        super.onResume();
        LogUtils.debug("[MainActivity]","onResume");
        //checkLogin();
    }

    private void checkLogin() {
        if (Preferences.getBoolean(Config.IS_LOGGED_IN)) {
            LogUtils.debug("MainActivity","checkLogin() --> user is LOGGED IN");
            //Check if this is the first login
            LogUtils.debug("MainActivity","checkLogin() --> value of PASSWORD RESET FLAG key is: "+Preferences.getBoolean(Config.PASSWORD_RESET_FLAG));
            if (Preferences.getBoolean(Config.PASSWORD_RESET_FLAG)) {
                LogUtils.debug("MainActivity","checkLogin() --> inside if-block that checks PASSWORD RESET FLAG key");
                Intent intent = new Intent(MainActivity.this, TabBarActivity.class);
                //final Context that = this;
                //startActivity(intent);
                int SPLASH_DISPLAY_LENGTH = 1000;
                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                /* Create an Intent that will start the Menu-Activity. */
                        LogUtils.debug("MainActivity","checkLogin() --> inside postDelayed about to display TabBarActivity");
//                        TabBarActivity tabAct = new TabBarActivity();

                        Intent mainIntent = new Intent(MainActivity.this, TabBarActivity.class);
                        MainActivity.this.startActivity(mainIntent);
                        MainActivity.this.finish();
                        //that.startActivity(mainIntent);
                        finish();
                    }
                }, SPLASH_DISPLAY_LENGTH);
            } else {
                Intent emailConfirmIntent = new Intent(context, EmailConfirmActivity.class);
                startActivity(emailConfirmIntent);
            }
        } else {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /*@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId){
            case R.id.action_logout:
                //logout();
                Log.d("Test","Logout is Called");
                Intent intent = new Intent(this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;
            case R.id.action_change_language:
                Log.d("Test","Before showChangeLangDialog is Called");
                showChangeLangDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }


    }*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void showChangeLangDialog() {
        Log.d("Test","showChangeLangDialog is Working!");
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.activity_show_language_settings, null);
        dialogBuilder.setView(dialogView);

        final Spinner spinner1 = (Spinner) dialogView.findViewById(R.id.spinner1);

        dialogBuilder.setTitle(R.string.select_language);

        dialogBuilder.setPositiveButton(R.string.change, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                int langpos = spinner1.getSelectedItemPosition();
                switch(langpos) {
                    case 0: //English
                        PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("LANG", "en").commit();
                        Preferences.putString("langCode", "en");
                        setLangRecreate("en");
                        return;
                    case 1: //Spanish
                        PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("LANG", "es").commit();
                        Preferences.putString("langCode", "es");
                        //setLangRecreate("es");
                        setLanguageForApp("es");
                        return;
                    /*case 2: //Vietnamese
                        PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("LANG", "vi").commit();
                        Preferences.putString("langCode", "vi");
                        setLangRecreate("vi");
                        return;
                    case 3: //Arabic
                        PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("LANG", "ar").commit();
                        setLangRecreate("ar");
                        return;*/
                    default: //By default set to english
                        PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("LANG", "en").commit();
                        setLangRecreate("en");
                        return;
                }
            }
        });
        dialogBuilder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });
        AlertDialog b = dialogBuilder.create();
        b.show();
    }

    /*public void showChangeLangDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.activity_show_language_settings, null);
        dialogBuilder.setView(dialogView);

        final Spinner spinner1 = (Spinner) dialogView.findViewById(R.id.language_spinner);

        dialogBuilder.setTitle(R.string.select_language);

        dialogBuilder.setPositiveButton("Change", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                int langpos = spinner1.getSelectedItemPosition();
                switch(langpos) {
                    case 0: //English
                        PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("LANG", "en").commit();
                        setLangRecreate("en");
                        return;
                    case 1: //French
                        PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("LANG", "fr").commit();
                        setLangRecreate("fr");
                        return;
                    case 2: //Spanish
                        PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("LANG", "es").commit();
                        //setLangRecreate("es");
                        setLanguageForApp("es");
                        return;
                    case 3: //Chinese
                        PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("LANG", "zh").commit();
                        setLangRecreate("zh");
                        return;
                    default: //By default set to english
                        PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("LANG", "en").commit();
                        setLangRecreate("en");
                        return;
                }
            }
        });
        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });
        AlertDialog b = dialogBuilder.create();
        b.show();
    }*/

    public void setLangRecreate(String langval) {
        Configuration config = getBaseContext().getResources().getConfiguration();
        Locale locale = new Locale(langval);
        Locale.setDefault(locale);
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
        recreate();
    }

    private void setLanguageForApp(String language){
        String languageToLoad  = language; //pass the language code as param
        Locale locale;
        if(languageToLoad.equals("not-set")){
            locale = Locale.getDefault();
        }
        else {
            locale = new Locale(languageToLoad);
        }
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());
        recreate();
    }
}
