package mobi.nowtechnologies.server.trackrepo.controller;

import junit.framework.TestCase;
import mobi.nowtechnologies.server.trackrepo.factory.AssetFileFactory;
import mobi.nowtechnologies.server.trackrepo.repository.FileRepository;
import org.junit.Before;
import org.junit.Ignore;
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
 * @author Alexander Kolpakov (akolpakov)
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
		"classpath:META-INF/application-test.xml",
		"file:src/main/webapp/WEB-INF/trackrepo-servlet.xml"})
@TransactionConfiguration(transactionManager = "trackRepo.TransactionManager", defaultRollback = true)
@Transactional
@Ignore
public class FileControllerTestIT extends TestCase {
	@Autowired
	private FileController fixture;
	
	@Autowired
	private FileRepository fileRepository;
	
	@Value("${trackRepo.encode.destination}")
	private Resource publishDir;

	@Test
	public void testFile()
		throws Exception {

		MockHttpServletResponse resp = new MockHttpServletResponse();
		Long id = new Long(1L);

		fixture.file(resp, id);

		assertEquals(200, resp.getStatus());
		assertEquals("image/jpeg", resp.getContentType());
	}

	@Before
	public void setUp() throws Exception {
		super.setUp();
		AssetFileFactory assetFileFactory = new AssetFileFactory();
		assetFileFactory.setFileDir(publishDir.getFile());

		fileRepository.save(assetFileFactory.anyAssetFile());
	}
}