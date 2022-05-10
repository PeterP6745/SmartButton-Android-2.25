package messagelogix.com.smartbuttoncommunications.notifications;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import messagelogix.com.smartbuttoncommunications.R;
import messagelogix.com.smartbuttoncommunications.utils.Config;
import messagelogix.com.smartbuttoncommunications.utils.LanguageManager;
import messagelogix.com.smartbuttoncommunications.utils.LogUtils;
import messagelogix.com.smartbuttoncommunications.utils.Preferences;

public class NotificationsListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Preferences.init(this);
        LanguageManager.setLocale(this, Preferences.getString(Config.LANGUAGE));

        setTitle(R.string.notifications_title);
        setContentView(R.layout.activity_notificationslist);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        LogUtils.debug("backPressed","NotificationsListActivity - pressing back button");
    }
}
