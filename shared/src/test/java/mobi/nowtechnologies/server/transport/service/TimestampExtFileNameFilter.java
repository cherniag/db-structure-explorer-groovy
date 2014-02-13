package mobi.nowtechnologies.server.transport.service;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.math.NumberUtils;

import java.io.File;
import java.io.FilenameFilter;

/**
* Created by oar on 12/23/13.
*/
public class TimestampExtFileNameFilter implements FilenameFilter {
    private long cutoff;

    public TimestampExtFileNameFilter(long cutoff) {
        this.cutoff = cutoff;
    }

    @Override
    public boolean accept(File dir, String name) {
        String ext = FilenameUtils.getExtension(name);

        long extLongValue = NumberUtils.toLong(ext, 0);

        return cutoff < extLongValue;
    }
}
