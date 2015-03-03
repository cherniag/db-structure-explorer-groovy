package mobi.nowtechnologies.server.httpinvoker;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.base.Charsets;
import com.google.common.io.Files;

import org.springframework.core.io.Resource;

public class UrlsProducer {

    public Map<String, String> produceUrls(Resource resource, DataToUrlStrategy strategy) throws IOException {
        List<String> lines = Files.readLines(resource.getFile(), Charsets.UTF_8);

        Map<String, String> urls = new HashMap<String, String>();

        for (String line : lines) {
            String trim = line.trim();

            if (trim.isEmpty()) {
                continue;
            }

            urls.put(line, strategy.createUrl(trim));
        }

        return urls;
    }
}
