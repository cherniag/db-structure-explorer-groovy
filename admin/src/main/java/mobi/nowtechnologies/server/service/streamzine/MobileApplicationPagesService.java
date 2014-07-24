package mobi.nowtechnologies.server.service.streamzine;

import com.google.common.base.Charsets;
import com.google.common.collect.Sets;
import com.google.common.io.Files;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class MobileApplicationPagesService {
    private File pages;
    private File actions;

    public MobileApplicationPagesService(File pages, File actions) {
        this.pages = pages;
        this.actions = actions;
    }

    public Set<String> getActions() {
        return getValues(actions);
    }

    public Set<String> getPages() {
        return getValues(pages);
    }

    private Set<String> getValues(File file) {
        if(file == null || file.isDirectory() || !file.exists()) {
            return Collections.emptySet();
        }

        try {
            List<String> lines = Files.readLines(file, Charsets.UTF_8);
            return Sets.newTreeSet(lines);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
