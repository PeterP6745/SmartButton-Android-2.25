package messagelogix.com.smartbuttoncommunications;

import android.app.Activity;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import messagelogix.com.smartbuttoncommunications.activities.core.MainActivity;

/**
 * Created by Program on 3/28/2016.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class MainActivityInstrumentationTest {

    @Rule
    public ActivityTestRule mActivityRule = new ActivityTestRule<>(
            MainActivity.class);

    @Test
    public void useActivityInTest() {

        Activity activity = mActivityRule.getActivity();
    }
}

