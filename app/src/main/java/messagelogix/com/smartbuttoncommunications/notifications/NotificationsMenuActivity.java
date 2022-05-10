package messagelogix.com.smartbuttoncommunications.notifications;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import java.util.Stack;

import messagelogix.com.smartbuttoncommunications.R;
import messagelogix.com.smartbuttoncommunications.utils.TitleTracker;
import messagelogix.com.smartbuttoncommunications.utils.Config;
import messagelogix.com.smartbuttoncommunications.utils.LanguageManager;
import messagelogix.com.smartbuttoncommunications.utils.LogUtils;
import messagelogix.com.smartbuttoncommunications.utils.Preferences;

/**
 * Created by Richard on 8/23/2018.
 */
public class NotificationsMenuActivity extends AppCompatActivity implements TitleTracker {

    Stack<String> titleStack = new Stack<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Preferences.init(this);
        LanguageManager.setLocale(this, Preferences.getString(Config.LANGUAGE));

        setTitle(R.string.notifications_title);
        addToTitleStack(getString(R.string.notifications_title));

        setContentView(R.layout.activity_notifications_menu);

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        // Replace the contents of the container with the new fragment
        // or ft.add(R.id.your_placeholder, new FooFragment());
        // Complete the changes added above
        ft.replace(R.id.notificationsmenu_framelayout, new NotificationsMenu()).commit();
    }

    @Override
    public void addToTitleStack(String newTitle) {
        titleStack.push(newTitle);
    }

    public void setMenuBarTitle() {
        if(titleStack.size() > 1) {
            titleStack.pop();
            String nextTitle = titleStack.peek();
            setTitle(nextTitle);
        }
    }

    @Override
    public void onBackPressed() {
        if(getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().popBackStack();
            setMenuBarTitle();
            LogUtils.debug("backPressed","NotifMenuActivity - notiflist fragment is visible, so closing fragment");
        } else {
            LogUtils.debug("backPressed", "NotifMenuActivity - back button pressed - method overriden, so does nothing");
        }
    }
}
