package messagelogix.com.smartbuttoncommunications;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.assertion.ViewAssertions;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.runner.AndroidJUnit4;
import android.test.ActivityInstrumentationTestCase2;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import messagelogix.com.smartbuttoncommunications.activities.login.LoginActivity;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

/**
 * Created by Vahid on 3/28/2016.
 */
@RunWith(AndroidJUnit4.class)
public class LoginActivityInstrumentationTest
        extends ActivityInstrumentationTestCase2<LoginActivity> {

    private LoginActivity mActivity;

    public LoginActivityInstrumentationTest() {

        super(LoginActivity.class);
    }

    @Before
    public void setUp() throws Exception {

        super.setUp();
        // Injecting the Instrumentation instance is required
        // for your test to run with AndroidJUnitRunner.
        injectInstrumentation(InstrumentationRegistry.getInstrumentation());
        mActivity = getActivity();
    }

    @Test
    public void testGoodLogin() {

        onView(withId(R.id.account)).perform(clearText());
        onView(withId(R.id.password)).perform(clearText());
        sleepSeconds(2);
        onView(withId(R.id.account)).perform(typeText("124456"));
        hideKeyboard();
        sleepSeconds(2);
        onView(withId(R.id.password)).perform(typeText("qwerty"));
        hideKeyboard();
        sleepSeconds(2);
        onView(withId(R.id.sign_in_button)).perform(click());
        onView(withId(R.id.locate_me_button)).check(matches(isDisplayed()));
        sleepSeconds(2);
    }

    @Test
    public void testBadLogin() {

        onView(withId(R.id.account)).perform(clearText());
        onView(withId(R.id.password)).perform(clearText());
        sleepSeconds(2);
        onView(withId(R.id.account)).perform(typeText("124456"));
        hideKeyboard();
        sleepSeconds(2);
        onView(withId(R.id.password)).perform(typeText("dummy"));
        hideKeyboard();
        sleepSeconds(2);
        onView(withId(R.id.sign_in_button)).perform(click());
        onView(withId(R.id.account)).check(matches(isDisplayed()));
        sleepSeconds(2);
    }

    @Test
    public void testEmptyUsernameLogin() {

        onView(withId(R.id.account)).perform(clearText());
        onView(withId(R.id.password)).perform(clearText());
        sleepSeconds(2);
        onView(withId(R.id.password)).perform(typeText("qwerty"));
        hideKeyboard();
        sleepSeconds(2);
        onView(withId(R.id.sign_in_button)).perform(click());
        onView(ViewMatchers.withId((R.id.account)))
                .check(ViewAssertions.matches(
                        ErrorTextMatchers.withErrorText(Matchers.containsString(mActivity.getResources().getString(R.string.error_username_required)))));
        sleepSeconds(2);
    }

    @Test
    public void testEmptyPasswordLogin() {

        onView(withId(R.id.account)).perform(clearText());
        onView(withId(R.id.password)).perform(clearText());
        sleepSeconds(2);
        onView(withId(R.id.account)).perform(typeText("124456"));
        hideKeyboard();
        sleepSeconds(2);
        onView(withId(R.id.sign_in_button)).perform(click());
        onView(ViewMatchers.withId((R.id.password)))
                .check(ViewAssertions.matches(
                        ErrorTextMatchers.withErrorText(Matchers.containsString(mActivity.getResources().getString(R.string.error_password_required)))));
        sleepSeconds(2);
    }

    public void sleepSeconds(int duration) {

        try {
            Thread.sleep(1000 * duration);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @After
    public void tearDown() throws Exception {

        super.tearDown();
    }

    protected void hideKeyboard() {
        // Check if no view has focus:
        View view = mActivity.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}