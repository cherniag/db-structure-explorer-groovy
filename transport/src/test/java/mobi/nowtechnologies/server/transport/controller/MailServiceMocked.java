package mobi.nowtechnologies.server.transport.controller;


import mobi.nowtechnologies.server.service.MailService;
import mobi.nowtechnologies.server.service.MailTemplateProcessor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.common.base.Joiner;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;

public class MailServiceMocked extends MailService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MailServiceMocked.class);
    @Value("${sms.temporaryFolder}")
    private File temporaryFolder;

    @Override
    public void sendMessage(String from, String[] to, String subject, String body, Map<String, String> model) {
        String processedSubject = MailTemplateProcessor.processTemplateString(subject, model);
        String processedBody = MailTemplateProcessor.processTemplateString(body, model);

        File file = new File(temporaryFolder, "activationEmail-" + Thread.currentThread().getName() + "." + System.currentTimeMillis());
        List<String> params = new ArrayList<String>();
        params.add("from: " + from);
        params.add("to: " + Joiner.on(',').join(to));
        params.add("subject: " + processedSubject);
        params.add("body: " + processedBody);

        try {
            LOGGER.info("Writing to: " + file);
            FileUtils.writeLines(file, params);
        }
        catch (IOException e) {
            LOGGER.error("error", e);
        }
    }
}
