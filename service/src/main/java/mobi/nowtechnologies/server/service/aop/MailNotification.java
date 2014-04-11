package mobi.nowtechnologies.server.service.aop;

import java.util.HashMap;
import java.util.Map;

import mobi.nowtechnologies.common.dto.UserRegInfo;
import mobi.nowtechnologies.server.persistence.dao.CommunityDao;
import mobi.nowtechnologies.server.persistence.domain.*;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentDetailsType;
import mobi.nowtechnologies.server.persistence.domain.payment.SubmittedPayment;
import mobi.nowtechnologies.server.service.MailService;
import mobi.nowtechnologies.server.service.UserService;
import mobi.nowtechnologies.server.service.event.PaymentEvent;
import mobi.nowtechnologies.server.shared.message.CommunityResourceBundleMessageSource;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

	/**
	 * Sending email after user made a successful registration
	 * @param joinPoint
	 * @throws Throwable
	 */
	@Around("execution(* mobi.nowtechnologies.server.service.UserService.registerUserWithoutPersonalInfo(*))")
	public Object sendRegistrationNotification(ProceedingJoinPoint joinPoint) throws Throwable {
		Object object = joinPoint.proceed();
		UserRegInfo userRegInfo = (UserRegInfo) joinPoint.getArgs()[0];
		try{
			sendWelcomeEmail(userRegInfo);
		}catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
		return object;
	}

	protected void sendWelcomeEmail(UserRegInfo user) {
		Community community = CommunityDao.getMapAsNames().get(user.getCommunityName());
		String communityUri = community.getRewriteUrlParameter().toLowerCase();
		
		String from = messageSource.getMessage(communityUri, "support.email",null, null);
		String subject = messageSource.getMessage(communityUri, "mail.registration.complete.subject",null, null);
		String body = messageSource.getMessage(communityUri, "mail.registration.complete.body",null, null);
		Map<String, String> model = new HashMap<String, String>();
			model.put("displayName", user.getDisplayName());
			
			model.put("communityName", community.getDisplayName());
			model.put("portalUrl", messageSource.getMessage(communityUri, "mail.portal.url",null, null)+community.getRewriteUrlParameter());
			model.put("supportEmail", messageSource.getMessage(communityUri, "support.email",null, null));
			model.put("supportPhone", messageSource.getMessage(communityUri, "support.phone",null, null));
			
		mailService.sendMail(from, new String[]{user.getEmail()}, subject , body , model);
		LOGGER.info("Welcome email was send to user {} from community {}", user.getEmail(), community.getDisplayName());
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
		
		mailService.sendMail(from, new String[]{user.getUserName()}, subject , body , model);
		LOGGER.info("User {} reset a password", user.getUserName());
	}
	
	protected void sendPaymentEmail(User user, Community community, String subject, String body, String numberOfWeeks, String amountDeducted) {
		String communityUri = community.getRewriteUrlParameter().toLowerCase();
		
		String from = messageSource.getMessage(communityUri, "mail.subscribed.cc.address",null, null);
		
		Map<String, String> model = new HashMap<String, String>();
			model.put("displayName", user.getDisplayName());
			model.put("communityName", community.getDisplayName());
			model.put("numberOfWeeks", numberOfWeeks);
			model.put("amountDeducted", amountDeducted);
			model.put("portalUrl", messageSource.getMessage(communityUri, "mail.portal.url",null, null)+community.getRewriteUrlParameter());
			model.put("supportEmail", messageSource.getMessage(communityUri, "support.email",null, null));
			model.put("supportPhone", messageSource.getMessage(communityUri, "support.phone",null, null));
			
		mailService.sendMail(from, new String[]{user.getUserName()}, subject , body , model);
		
		LOGGER.info("Billing email for SagePay Credit Card was send to user {} from community {}", user.getUserName(), community.getDisplayName());
	}
	
	
	@Around("execution(* mobi.nowtechnologies.server.service.UserService.contactWithUser(..))")
	public void sendContactUsEmail(ProceedingJoinPoint joinPoint) throws Throwable {
		joinPoint.proceed();
		String from = (String) joinPoint.getArgs()[0];
		String name = (String) joinPoint.getArgs()[1];
		String subject = (String) joinPoint.getArgs()[2];
		
		mailService.sendMail(from, new String[]{messageSource.getMessage(null, "support.email",null, null)}, "From User "+name , subject, null);
	}
}