package messagelogix.com.smartbuttoncommunications;

import org.junit.Test;

import messagelogix.com.smartbuttoncommunications.utils.FunctionHelper;

import static org.junit.Assert.assertTrue;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class FunctionHelperUnitTest {

    @Test
    public void string_isEmpty() throws Exception {

        assertTrue("Function failed to check for empty", FunctionHelper.isNullOrEmpty(""));
    }

    @Test
    public void string_isNull() throws Exception {

        assertTrue("Function failed to check for null", FunctionHelper.isNullOrEmpty(null));
    }

    @Test
    public void trimmedString_isEmpty() throws Exception {

        assertTrue("Function failed to check for empty", FunctionHelper.isNullOrEmpty("    "));
    }
}