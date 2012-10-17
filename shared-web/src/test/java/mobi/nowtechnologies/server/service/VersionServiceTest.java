package mobi.nowtechnologies.server.service;

import static org.junit.Assert.assertEquals;

import java.io.FileNotFoundException;
import java.io.IOException;

import mobi.nowtechnologies.server.dto.VersionDto;

import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;


public class VersionServiceTest {
	
	private VersionService service;
	
	@Before
	public void before() throws FileNotFoundException, IOException {
		service = new VersionService();
		service.setManifest(new ClassPathResource("/MANIFEST.MF"));
	}
	
	@Test
	public void getVersion() {
		VersionDto versionDto = service.getVersion();
		
		assertEquals("3.3.0-SNAPSHOT", versionDto.getVersion());
		assertEquals("77", versionDto.getBuild());
		assertEquals("4354", versionDto.getRevision());
	}
}