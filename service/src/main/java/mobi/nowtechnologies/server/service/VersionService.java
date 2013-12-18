package mobi.nowtechnologies.server.service;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import mobi.nowtechnologies.server.dto.VersionDto;
import org.apache.commons.io.input.ReaderInputStream;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

public class VersionService {
    public static final String IMPLEMENTATION_BUILD = "Implementation-Build";
    public static final String IMPLEMENTATION_VERSION = "Implementation-Version";
    public static final String BRANCH_ATTRIBUTE = "Branch";

    private Attributes attributes;

    public VersionDto getVersion() {
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

    protected void init() {
        Assert.notNull(attributes);
    }

    public void setManifest(Resource manifest) throws FileNotFoundException, IOException {
        String text = Files.toString(manifest.getFile(), Charsets.UTF_8);

        Manifest mf = new Manifest();
        mf.read(new ReaderInputStream(new StringReader(text)));
        attributes = mf.getMainAttributes();

    }

}