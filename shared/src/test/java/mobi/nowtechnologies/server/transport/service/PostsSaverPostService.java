package mobi.nowtechnologies.server.transport.service;

import mobi.nowtechnologies.server.shared.service.BasicResponse;
import mobi.nowtechnologies.server.shared.service.PostService;
import org.apache.commons.io.FileUtils;
import org.apache.http.NameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by oar on 12/20/13.
 */
public class PostsSaverPostService extends PostService {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Value("${sms.temporaryFolder}")
    private File smsTemporaryFolder;



    public BasicResponse sendHttpPost(String url, List<NameValuePair> nameValuePairs, String body) {
        try {
            saveDataToFile(url, nameValuePairs, body);
        } catch (IOException e) {
            logger.error("Error save to file", e);
        }
        BasicResponse response = new BasicResponse();
        response.setMessage("OK");
        response.setStatusCode(200);
        return response;
    }

    private void saveDataToFile(String url, List<NameValuePair> nameValuePairs, String body) throws IOException {
        File smsFile = new File(smsTemporaryFolder, "smsInTime_" + System.currentTimeMillis());
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

}
