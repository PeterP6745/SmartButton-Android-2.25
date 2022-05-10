package messagelogix.com.smartbuttoncommunications;

import android.test.ActivityInstrumentationTestCase2;

/**
 * Created by Program on 3/28/2016.
 */
public class SmartButtonActivityTests extends ActivityInstrumentationTestCase2<HomeActivity> {

    public SmartButtonActivityTests() {

        super(HomeActivity.class);
    }

    public void testSimple() {

        assertTrue(true);
    }
//    /**
//     * Test to make sure that spinner values are persisted across activity restarts.
//     *
//     * <p>Launches the main activity, sets a spinner value, closes the activity, then relaunches
//     * that activity. Checks to make sure that the spinner values match what we set them to.
//     */
//    // BEGIN_INCLUDE (test_name)
//    public void testSpinnerValuePersistedBetweenLaunches() {
//        // END_INCLUDE (test_name)
//        final int TEST_SPINNER_POSITION_1 = 1;
//
//        // BEGIN_INCLUDE (launch_activity)
//        // Launch the activity
//        Activity activity = getActivity();
//        // END_INCLUDE (launch_activity)
//
//        // BEGIN_INCLUDE (write_to_ui)
//        // Set spinner to test position 1
//        final Spinner spinner1 = (Spinner) activity.findViewById(R.id.message_spinner);
//        activity.runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                // Attempts to manipulate the UI must be performed on a UI thread.
//                // Calling this outside runOnUiThread() will cause an exception.
//                //
//                // You could also use @UiThreadTest, but activity lifecycle methods
//                // cannot be called if this annotation is used.
//                spinner1.requestFocus();
//                spinner1.setSelection(TEST_SPINNER_POSITION_1);
//            }
//        });
//        // END_INCLUDE (write_to_ui)
//
//        // BEGIN_INCLUDE (relaunch_activity)
//        // Close the activity
//        activity.finish();
//        setActivity(null);  // Required to force creation of a new activity
//
//        // Relaunch the activity
//        activity = this.getActivity();
//        // END_INCLUDE (relaunch_activity)
//
//        // BEGIN_INCLUDE (check_results)
//        // Verify that the spinner was saved at position 1
//        final Spinner spinner2 = (Spinner) activity.findViewById(R.id.message_spinner);
//        int currentPosition = spinner2.getSelectedItemPosition();
//        assertEquals(TEST_SPINNER_POSITION_1, currentPosition);
//        // END_INCLUDE (check_results)
//
//        // Since this is a stateful test, we need to make sure that the activity isn't simply
//        // echoing a previously-stored value that (coincidentally) matches position 1
//        final int TEST_SPINNER_POSITION_2 = 2;
//
//        // Set spinner to test position 2
//        activity.runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                spinner2.requestFocus();
//                spinner2.setSelection(TEST_SPINNER_POSITION_2);
//            }
//        });
//
//        // Close the activity
//        activity.finish();
//        setActivity(null);
//
//        // Relaunch the activity
//        activity = this.getActivity();
//
//        // Verify that the spinner was saved at position 2
//        final Spinner spinner3 = (Spinner) activity.findViewById(R.id.message_spinner);
//        currentPosition = spinner3.getSelectedItemPosition();
//        assertEquals(TEST_SPINNER_POSITION_2, currentPosition);
//    }
}
