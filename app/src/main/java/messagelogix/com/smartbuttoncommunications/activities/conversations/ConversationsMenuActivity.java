package messagelogix.com.smartbuttoncommunications.activities.conversations;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import java.util.Stack;

import messagelogix.com.smartbuttoncommunications.R;
import messagelogix.com.smartbuttoncommunications.utils.Config;
import messagelogix.com.smartbuttoncommunications.utils.LanguageManager;
import messagelogix.com.smartbuttoncommunications.utils.LogUtils;
import messagelogix.com.smartbuttoncommunications.utils.Preferences;
import messagelogix.com.smartbuttoncommunications.utils.TitleTracker;

/**
 * Created by Richard on 8/23/2018.
 */
public class ConversationsMenuActivity extends AppCompatActivity implements TitleTracker {

    Stack<String> titleStack = new Stack<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LogUtils.debug("RebuildSignalR","ConversationsMenuActivity - onCreate()");

        Preferences.init(this);
        LanguageManager.setLocale(this, Preferences.getString(Config.LANGUAGE));

        setTitle(R.string.conversations_title);
        addToTitleStack(getString(R.string.conversations_title));

        setContentView(R.layout.activity_conversations_menu);

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        // Replace the contents of the container with the new fragment
        // or ft.add(R.id.your_placeholder, new FooFragment());
        // Complete the changes added above
        ft.replace(R.id.conversationsmenu_activity_framelayout, new ConversationsMenu()).commit();
    }

    @Override
    protected void onStart() {
        super.onStart();
        LogUtils.debug("RebuildSignalR","ConversationsMenuActivity - onStart()");
//        FragmentTransaction ft = getFragmentManager().beginTransaction();
//        // Replace the contents of the container with the new fragment
//        // or ft.add(R.id.your_placeholder, new FooFragment());
//        // Complete the changes added above
//        ft.replace(R.id.conversationsmenu_activity_framelayout, new ConversationsMenu()).commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        LogUtils.debug("RebuildSignalR","ConversationsMenuActivity - onResume()");
    }

    @Override
    protected void onPause() {
        super.onPause();
        LogUtils.debug("RebuildSignalR","ConversationsMenuActivity - onPause()");
    }

    @Override
    protected void onStop() {
        super.onStop();
        LogUtils.debug("RebuildSignalR","ConversationsMenuActivity - onStop()");
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
            LogUtils.debug("backPressed","ConvoMenuActivity - fragment is visible, so closing fragment");
        } else {
            LogUtils.debug("backPressed", "ConvoMenuActivity - back button pressed - onBackPressed() method overridden, so does nothing");
        }
    }
}
