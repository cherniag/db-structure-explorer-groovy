package mobi.nowtechnologies.server.service.aop;

import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.service.MailService;
import mobi.nowtechnologies.server.service.UserService;
import mobi.nowtechnologies.server.shared.message.CommunityResourceBundleMessageSource;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

@Aspect
public class MailNotification {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(MailNotification.class);
	
	private MailService mailService;

	private CommunityResourceBundleMessageSource messageSource;
		
	private UserService userService;
	
	public void setMailService(MailService mailService) {
		this.mailService = mailService;
	}
	
	public void setMessageSource(CommunityResourceBundleMessageSource messageSource) {
		this.messageSource = messageSource;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}
	
	@Around("execution(* mobi.nowtechnologies.server.service.UserService.resetPassword(..))")
	public void sendResetPasswordNotification(ProceedingJoinPoint joinPoint) throws Throwable {
		joinPoint.proceed();
		User user = (User) joinPoint.getArgs()[0];
		String newPassword = (String) joinPoint.getArgs()[1];
		sendResetPasswordEmail(user, newPassword);
	}

	protected void sendResetPasswordEmail(User user, String newPassword) {
		String communityUri = user.getUserGroup().getCommunity().getRewriteUrlParameter().toLowerCase();
		
		String from = messageSource.getMessage(communityUri, "mail.rest.password.address",null, null);
		String subject = messageSource.getMessage(communityUri, "mail.rest.password.subject",null, null);
		String body = messageSource.getMessage(communityUri, "mail.rest.password.body",null, null);
		
		Map<String, String> model = new HashMap<String, String>();
			model.put("displayName", user.getDisplayName());
			model.put("email", user.getUserName());
			model.put("password", newPassword);
		
		mailService.sendMessage(from, new String[]{user.getUserName()}, subject, body, model);
		LOGGER.info("User {} reset a password", user.getUserName());
	}
	
	
	@Around("execution(* mobi.nowtechnologies.server.service.UserService.contactWithUser(..))")
	public void sendContactUsEmail(ProceedingJoinPoint joinPoint) throws Throwable {
		joinPoint.proceed();
		String from = (String) joinPoint.getArgs()[0];
		String name = (String) joinPoint.getArgs()[1];
		String subject = (String) joinPoint.getArgs()[2];
		
		mailService.sendMessage(from, new String[]{messageSource.getMessage(null, "support.email", null, null)}, "From User " + name, subject, null);
	}
}