package mobi.nowtechnologies.server.apptests.email;

import mobi.nowtechnologies.server.persistence.apptests.domain.Email;
import mobi.nowtechnologies.server.service.MailService;
import mobi.nowtechnologies.server.service.MailTemplateProcessor;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import java.util.Map;

import org.springframework.transaction.annotation.Transactional;

public class DbMailService extends MailService {

    @PersistenceContext
    private EntityManager entityManager;

    @Resource
    private MailModelSerializer mailModelSerializer;

    @Transactional
    public void sendMessage(String from, String[] to, String subject, String body, Map<String, String> model) {
        String subj = MailTemplateProcessor.processTemplateString(subject, model);
        String text = MailTemplateProcessor.processTemplateString(body, model);

        String serializedModel = mailModelSerializer.serialize(model);
        entityManager.persist(new Email(from, to, subj, text).withModel(serializedModel));
    }

    @Transactional
    public void sendMessage(String from, String to, String subject, String body, Map<String, String> model) {
        sendMessage(from, new String[] {to}, subject, body, model);
    }
}
