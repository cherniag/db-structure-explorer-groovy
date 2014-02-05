package mobi.nowtechnologies.server.trackrepo.controller;

import mobi.nowtechnologies.server.trackrepo.factory.AssetFileFactory;
import mobi.nowtechnologies.server.trackrepo.repository.FileRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.junit.Assert.assertEquals;

/**
 * @author Alexander Kolpakov (akolpakov)
 */

public class FileControllerTestIT extends AbstractTrackRepoITTest{
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
		AssetFileFactory assetFileFactory = new AssetFileFactory();
		assetFileFactory.setFileDir(publishDir.getFile());

		fileRepository.save(assetFileFactory.anyAssetFile());
	}
}