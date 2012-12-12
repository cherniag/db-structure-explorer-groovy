package mobi.nowtechnologies.server.trackrepo.controller;

import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

/**
 * The class <code>SignInControllerTest</code> contains tests for the class <code>{@link SignInController}</code>.
 *
 * @generatedBy CodePro at 11/13/12 5:09 PM, using the Spring generator
 * @author Alexander Kolpakov (akolpakov)
 * @version $Revision: 1.0 $
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
		"classpath:META-INF/application-test.xml",
		"file:src/main/webapp/WEB-INF/trackrepo-servlet.xml"})
@TransactionConfiguration(transactionManager = "trackRepo.TransactionManager", defaultRollback = true)
@Transactional
public class SignInControllerTestIT extends TestCase {
	@Autowired
	private SignInController fixture;
	
	/**
	 * Run the Boolean login(HttpServletRequest) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 11/13/12 5:09 PM
	 */
	@Test
	public void testLogin() throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest();

		Boolean result = fixture.login(request);

		assertNotNull(result);
		assertEquals(true, result.booleanValue());
	}
}