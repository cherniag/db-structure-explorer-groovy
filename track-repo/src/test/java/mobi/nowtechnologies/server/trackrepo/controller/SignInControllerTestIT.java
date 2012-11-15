package mobi.nowtechnologies.server.trackrepo.controller;

import junit.framework.TestCase;
import org.junit.Ignore;
import org.springframework.mock.web.MockHttpServletRequest;

import java.text.SimpleDateFormat;

/**
 * The class <code>SignInControllerTest</code> contains tests for the class <code>{@link SignInController}</code>.
 *
 * @generatedBy CodePro at 11/13/12 5:09 PM, using the Spring generator
 * @author Alexander Kolpakov (akolpakov)
 * @version $Revision: 1.0 $
 */
@Ignore
public class SignInControllerTestIT extends TestCase {
	/**
	 * Run the Boolean login(HttpServletRequest) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 11/13/12 5:09 PM
	 */
	public void testLogin_1()
		throws Exception {
		SignInController fixture = new SignInController();
		fixture.dateFormat = new SimpleDateFormat();
		fixture.dateTimeFormat = new SimpleDateFormat();
		MockHttpServletRequest request = new MockHttpServletRequest();

		Boolean result = fixture.login(request);

		// add additional test code here
		assertNotNull(result);
		assertEquals("true", result.toString());
		assertEquals(true, result.booleanValue());
		// unverified
	}

	/**
	 * Perform pre-test initialization.
	 *
	 * @throws Exception
	 *         if the initialization fails for some reason
	 *
	 * @see TestCase#setUp()
	 *
	 * @generatedBy CodePro at 11/13/12 5:09 PM
	 */
	protected void setUp()
		throws Exception {
		super.setUp();
		// add additional set up code here
	}

	/**
	 * Perform post-test clean-up.
	 *
	 * @throws Exception
	 *         if the clean-up fails for some reason
	 *
	 * @see TestCase#tearDown()
	 *
	 * @generatedBy CodePro at 11/13/12 5:09 PM
	 */
	protected void tearDown()
		throws Exception {
		super.tearDown();
		// Add additional tear down code here
	}
}