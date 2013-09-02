package mobi.nowtechnologies.server.trackrepo.ingest;

import java.io.File;
import java.util.Map;

/**
 * User: Titov Mykhaylo (titov)
 * 02.09.13 15:34
 */
public interface Parser {

    public Map<String, DropTrack> loadXml(File file);
}
