package mobi.nowtechnologies.server.trackrepo.controller;

import junit.framework.TestCase;
import mobi.nowtechnologies.server.shared.Utils;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;

/**
 * The class <code>FileControllerTest</code> contains tests for the class <code>{@link FileController}</code>.
 *
 * @generatedBy CodePro at 11/13/12 5:09 PM, using the Spring generator
 * @author Alexander Kolpakov (akolpakov)
 * @version $Revision: 1.0 $
 */
@Ignore
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
		"file:src/main/webapp/WEB-INF/application.xml",
		"file:src/main/webapp/WEB-INF/track-repo-servlet.xml"})
@TransactionConfiguration(transactionManager = "trackRepo.TransactionManager", defaultRollback = true)
@Transactional
@PrepareForTest(Utils.class)
public class FileControllerTestIT extends TestCase {
	private FileController fixture;

	/**
	 * Run the void file(HttpServletResponse,Long) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 11/13/12 5:09 PM
	 */
	public void testFile()
		throws Exception {

		MockHttpServletResponse resp = new MockHttpServletResponse();
		Long id = new Long(1L);

		fixture.file(resp, id);

		assertEquals(200, resp.getStatus());
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
		
		fixture = new FileController();
		fixture.dateTimeFormat = new SimpleDateFormat();
		fixture.dateFormat = new SimpleDateFormat();
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
	}
}