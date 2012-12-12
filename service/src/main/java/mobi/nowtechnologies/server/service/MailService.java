package mobi.nowtechnologies.server.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import javax.mail.Session;
import java.util.Date;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * MailService
 * 
 * @author Maksym Chernolevskyi (maksym)
 * 
 */
public class MailService {
	
	private static final Logger logger = LoggerFactory.getLogger(MailService.class);
	
	private MailSender mailSender;
	
	private Pattern tokenPatter;
			
	public MailService() {
		tokenPatter = Pattern.compile("%([^%]+)%");
	}

	public void setMailSender(MailSender mailSender) {
		this.mailSender = mailSender;
	}

	public void setMailSession(Session mailSession) {	
		JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
			mailSender.setSession(mailSession);
		this.mailSender = mailSender;
	}

	public void sendMail(String from, String[] to, String subject, String body, Map<String, String> model) {
		try {
			SimpleMailMessage message = new SimpleMailMessage();
				message.setFrom(from);
				message.setTo(to);
				message.setSubject(processTemplateString(subject, model));
				message.setText(processTemplateString(body, model));
				message.setSentDate(new Date());
			mailSender.send(message);
		} catch (Exception e) {
			String msg = e.getMessage();
			msg = msg+(e.getCause() != null ? ": "+e.getCause().getMessage() : "");
			JavaMailSenderImpl sender = (JavaMailSenderImpl)mailSender;
			logger.error("Error while sending email(" + sender.getHost() + ":" + sender.getPort() + "@" + sender.getUsername() + ". " + msg, e);
		}
	}
	
	/**
	 * Method searches for sequences framed by %-sign and replaces
	 * them with values from model map. 
	 * @param templateString - a string with framed sequences
	 * @param model - a map with key - values
	 * @return processed string
	 */
	protected String processTemplateString(String templateString, Map<String, String> model) {
		StringBuilder output = new StringBuilder();
		Matcher matcher = tokenPatter.matcher(templateString);
		
		int cursor = 0;
		while(matcher.find()) {
			int tokenStart = matcher.start();
            int tokenEnd = matcher.end();
            int keyStart = matcher.start(1);
            int keyEnd = matcher.end(1);

            output.append(templateString.substring(cursor, tokenStart));

            String token = templateString.substring(tokenStart, tokenEnd);
            String key = templateString.substring(keyStart, keyEnd);

            if (model.containsKey(key)) {
                String value = model.get(key);
                output.append(value);
            } else {
                output.append(token);
            }

            cursor = tokenEnd;
        }
        output.append(templateString.substring(cursor));

        return output.toString();
	}
}