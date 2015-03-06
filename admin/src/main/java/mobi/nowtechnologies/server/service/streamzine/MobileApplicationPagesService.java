package mobi.nowtechnologies.server.service.streamzine;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

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

        try (FileReader fileReader = new FileReader(file)) {
            Properties properties = new Properties();
            properties.load(fileReader);
            String value = properties.getProperty(communityUrl);
            if (value == null || value.isEmpty()) {
                return Collections.emptySet();
            }
            return toSet(value);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Set<String> toSet(String value) {
        List<String> strings = Arrays.asList(value.split(VALUES_DELIMITER));
        return new TreeSet<>(strings);
    }
}
