package mobi.nowtechnologies.server.support.http;

import javax.annotation.PostConstruct;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.apache.http.NameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;

/**
 * Created by oar on 12/20/13.
 */
public class PostsSaverPostService extends PostService {

    private Logger logger = LoggerFactory.getLogger(getClass());
    @Value("${sms.temporaryFolder}")
    private File smsTemporaryFolder;
    private ArrayBlockingQueue<Integer> queue;

    public Monitor getMonitor() {
        if (queue != null) {
            throw new IllegalStateException("already monitoring...");
        }

        queue = new ArrayBlockingQueue<Integer>(1);

        return new Monitor() {
            @Override
            public void waitToComplete(final long timeout) throws Exception {
                try {
                    queue.poll(timeout, TimeUnit.MILLISECONDS);
                } finally {
                    queue = null;
                }
            }
        };
    }

    public BasicResponse sendHttpPost(String url, List<NameValuePair> nameValuePairs, String body) {
        try {
            try {


                saveDataToFile(url, nameValuePairs, body);
            } catch (IOException e) {
                logger.error("Error save to file", e);
                throw new RuntimeException(e);
            }
            BasicResponse response = new BasicResponse();
            response.setMessage("OK");
            response.setStatusCode(200);
            return response;
        } finally {
            if (queue != null) {
                queue.add(0);
            }
        }
    }

    private void saveDataToFile(String url, List<NameValuePair> nameValuePairs, String body) throws IOException {
        File smsFile = new File(smsTemporaryFolder, "smsInTime." + System.currentTimeMillis());
        List<String> lines = new ArrayList<String>();
        lines.add("URL: " + url);
        lines.add("Body: " + body);
        for (NameValuePair parameter : nameValuePairs) {
            lines.add("Parameter: " + parameter.getName() + ".." + "Value: " + parameter.getValue());
        }
        FileUtils.writeLines(smsFile, lines);
    }

    @PostConstruct
    private void initData() throws IOException {
        if (smsTemporaryFolder.exists()) {
            FileUtils.cleanDirectory(smsTemporaryFolder);
        } else {
            smsTemporaryFolder.mkdirs();
        }
    }

    public interface Monitor {

        void waitToComplete(long timeout) throws Exception;
    }

}
