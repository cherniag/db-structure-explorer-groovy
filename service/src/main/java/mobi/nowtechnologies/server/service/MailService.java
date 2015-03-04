package mobi.nowtechnologies.server.service;

import javax.mail.Session;

import java.util.Date;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;

/**
 * MailService
 *
 * @author Maksym Chernolevskyi (maksym)
 */
public class MailService {

    private static final Logger logger = LoggerFactory.getLogger(MailService.class);

    private MailSender mailSender;

    public void setMailSender(MailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void setMailSession(Session mailSession) {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setSession(mailSession);
        this.mailSender = mailSender;
    }

    public void sendMessage(String from, String[] to, String subject, String body, Map<String, String> model) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(from);
            message.setTo(to);
            message.setSubject(MailTemplateProcessor.processTemplateString(subject, model));
            message.setText(MailTemplateProcessor.processTemplateString(body, model));
            message.setSentDate(new Date());
            mailSender.send(message);
        }
        catch (Exception e) {
            String msg = e.getMessage();
            msg = msg + (e.getCause() != null ?
                         ": " + e.getCause().getMessage() :
                         "");
            JavaMailSenderImpl sender = (JavaMailSenderImpl) mailSender;
            logger.error("Error while sending email(" + sender.getHost() + ":" + sender.getPort() + "@" + sender.getUsername() + ". " + msg, e);
        }
    }
}