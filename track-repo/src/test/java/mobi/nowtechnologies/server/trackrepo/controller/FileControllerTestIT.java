package mobi.nowtechnologies.server.trackrepo.controller;

import junit.framework.TestCase;
import mobi.nowtechnologies.server.trackrepo.factory.AssetFileFactory;
import mobi.nowtechnologies.server.trackrepo.repository.FileRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

/**
 * The class <code>FileControllerTest</code> contains tests for the class <code>{@link FileController}</code>.
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
public class FileControllerTestIT extends TestCase {
	@Autowired
	private FileController fixture;
	
	@Autowired
	private FileRepository fileRepository;
	
	@Value("${trackRepo.encode.destination}")
	private Resource publishDir;

	/**
	 * Run the void file(HttpServletResponse,Long) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 11/13/12 5:09 PM
	 */
	@Test
	public void testFile()
		throws Exception {

		MockHttpServletResponse resp = new MockHttpServletResponse();
		Long id = new Long(1L);

		fixture.file(resp, id);

		assertEquals(200, resp.getStatus());
		assertEquals("image/jpeg", resp.getContentType());
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
	@Before
	public void setUp() throws Exception {
		super.setUp();
		AssetFileFactory assetFileFactory = new AssetFileFactory();
		assetFileFactory.setFileDir(publishDir.getFile());

		fileRepository.save(assetFileFactory.anyAssetFile());
	}
}