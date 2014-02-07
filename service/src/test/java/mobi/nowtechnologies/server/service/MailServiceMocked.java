package mobi.nowtechnologies.server.service;


import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MailServiceMocked extends MailService {

    @Value("${sms.temporaryFolder}")
    private File temporaryFolder;

    private static final Logger LOGGER = LoggerFactory.getLogger(MailServiceMocked.class);

    @Override
    public void sendMail(String from, String[] to, String subject, String body, Map<String, String> model) {
        String processedSubject = MailTemplateProcessor.processTemplateString(subject, model);
        String processedBody = MailTemplateProcessor.processTemplateString(body, model);

        File file = new File(temporaryFolder, "activationEmail." + System.currentTimeMillis());
        List<String> params = new ArrayList<String>();
        params.add("from: " + from);
        params.add("to: " + to[0]);
        params.add("subject: " + processedSubject);
        params.add("body: " + processedBody);

        try {
            FileUtils.writeLines(file, params);
        } catch (IOException e) {
            LOGGER.error("error", e);
        }
    }
}
