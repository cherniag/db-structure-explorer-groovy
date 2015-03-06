package mobi.nowtechnologies.server.service.streamzine;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

import com.google.common.base.Splitter;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

public class MobileApplicationPagesService {

    private static final String VALUES_DELIMITER = ",";
    private File pages;
    private File actions;

    public MobileApplicationPagesService(File pages, File actions) {
        this.pages = pages;
        this.actions = actions;
    }

    public Set<String> getActions(String communityUrl) {
        return getValues(actions, communityUrl);
    }

    public Set<String> getPages(String communityUrl) {
        return getValues(pages, communityUrl);
    }

    private Set<String> getValues(File file, String communityUrl) {
        if (file == null || file.isDirectory() || !file.exists() || communityUrl == null) {
            return Collections.emptySet();
        }

        try {
            Properties properties = PropertiesLoaderUtils.loadProperties(new FileSystemResource(file));
            String value = properties.getProperty(communityUrl);
            if (value == null || value.isEmpty()) {
                return Collections.emptySet();
            }
            return toSet(value);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Set<String> toSet(String value) {
        return new TreeSet<>(Splitter.on(VALUES_DELIMITER).omitEmptyStrings().trimResults().splitToList(value));
    }
}
