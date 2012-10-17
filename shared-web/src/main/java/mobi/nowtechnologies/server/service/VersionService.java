package mobi.nowtechnologies.server.service;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import org.springframework.core.io.Resource;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import mobi.nowtechnologies.server.dto.VersionDto;

public class VersionService {
	public static final String IMPLEMENTATION_BUILD = "Implementation-Build";
	public static final String IMPLEMENTATION_VERSION = "Implementation-Version";
	
	private Attributes attributes;
	
	public VersionDto getVersion() {
		VersionDto dto = new VersionDto();
			dto.setVersion(attributes.getValue(IMPLEMENTATION_VERSION));
			String buildNumber = attributes.getValue(IMPLEMENTATION_BUILD);
			if (StringUtils.hasText(buildNumber)) {
				String[] vers = buildNumber.split("-",2);
				dto.setBuild(vers[1]);
				dto.setRevision(vers[0]);
			}
		return dto;
	}
	
	protected void init() {
		Assert.notNull(attributes);
	}
	
	public void setManifest(Resource manifest) throws FileNotFoundException, IOException {
		Manifest mf = new Manifest();
		mf.read(new FileInputStream(manifest.getFile()));
		attributes = mf.getMainAttributes();
	}
	
}