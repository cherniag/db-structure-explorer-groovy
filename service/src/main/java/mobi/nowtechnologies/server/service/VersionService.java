package mobi.nowtechnologies.server.service;

import mobi.nowtechnologies.server.dto.VersionDto;

import java.io.IOException;
import java.io.InputStream;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import org.springframework.core.io.Resource;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

public class VersionService {
    public static final String IMPLEMENTATION_BUILD = "Implementation-Build";
    public static final String IMPLEMENTATION_VERSION = "Implementation-Version";
    public static final String BRANCH_ATTRIBUTE = "Branch";

    VersionDto version;

    public VersionDto getVersion() {
        return version;
    }

    public void setManifest(Resource manifest) throws IOException {
        try (InputStream inputStream = manifest.getInputStream()) {
            Assert.isTrue(manifest.exists());

            version = readVersion(new Manifest(inputStream));
        }
    }

    private VersionDto readVersion(Manifest mf) {
        Attributes attributes = mf.getMainAttributes();

        VersionDto dto = new VersionDto();
        dto.setVersion(attributes.getValue(IMPLEMENTATION_VERSION));
        String buildNumber = attributes.getValue(IMPLEMENTATION_BUILD);
        if (StringUtils.hasText(buildNumber)) {
            String[] vers = buildNumber.split("-", 2);
            dto.setBuild(vers[1]);
            dto.setRevision(vers[0]);
        }
        dto.setBranchName(attributes.getValue(BRANCH_ATTRIBUTE));
        return dto;
    }

}