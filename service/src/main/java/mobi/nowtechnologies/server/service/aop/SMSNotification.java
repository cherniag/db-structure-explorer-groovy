package mobi.nowtechnologies.server.service.aop;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.service.UserService;
import mobi.nowtechnologies.server.service.payment.http.MigHttpService;
import mobi.nowtechnologies.server.shared.enums.UserStatus;
import mobi.nowtechnologies.server.shared.log.LogUtils;
import mobi.nowtechnologies.server.shared.message.CommunityResourceBundleMessageSource;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;

@Aspect
public class SMSNotification {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(SMSNotification.class);
	
	private MigHttpService migService;

	private CommunityResourceBundleMessageSource messageSource;
		
	private UserService userService;
	
	private List<String> availableCommunities = Collections.emptyList();
	
	private String paymentsUrl;
	
	private String unsubscribeUrl;
	
	private String tinyUrlService;
	
	private RestTemplate restTemplate = new RestTemplate();
	
	public void setMigService(MigHttpService migService) {
		this.migService = migService;
	}

	public void setMessageSource(CommunityResourceBundleMessageSource messageSource) {
		this.messageSource = messageSource;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	public void setAvailableCommunities(String availableCommunities) {
		if(availableCommunities == null)
			return;
			
		String delims = "[ ,]+";
		this.availableCommunities = Arrays.asList(availableCommunities.split(delims));
	}

	public void setPaymentsUrl(String paymentsUrl) {
		this.paymentsUrl = paymentsUrl;
	}

	public void setUnsubscribeUrl(String unsubscribeUrl) {
		this.unsubscribeUrl = unsubscribeUrl;
	}

	public void setTinyUrlService(String tinyUrlService) {
		this.tinyUrlService = tinyUrlService;
	}

	/**
	 * Sending sms after user was set to limited status
	 * @param joinPoint
	 * @throws Throwable
	 */
	@Around("execution(* mobi.nowtechnologies.server.service.UserService.saveWeeklyPayment(*))")
	public Object saveWeeklyPayment(ProceedingJoinPoint joinPoint) throws Throwable {
		try{
			LogUtils.putClassNameMDC(this.getClass());
			
			Object object = joinPoint.proceed();
			User user = (User) joinPoint.getArgs()[0];
			try{
				if (user.getPaymentDetailsList().isEmpty())
					sendLimitedStatusSMS(user);
			}catch (Exception e) {
				LOGGER.error(e.getMessage(), e);
			}
			return object;
		}finally{
			LogUtils.removeClassNameMDC();
		}
	}
	
	@Pointcut("execution(* mobi.nowtechnologies.server.service.PaymentDetailsService.createCreditCardPamentDetails(..))")
	protected void createdCreditCardPaymentDetails() {}
	
	@Pointcut("execution(* mobi.nowtechnologies.server.service.PaymentDetailsService.commitPayPalPaymentDetails(..))")
	protected void createdPayPalPaymentDetails() {}

	@Pointcut("execution(* mobi.nowtechnologies.server.service.PaymentDetailsService.commitMigPaymentDetails(..))")
	protected void createdMigPaymentDetails() {}
	
	/**
	 * Sending sms after user was subscribed with some payment details
	 * @param joinPoint
	 * @throws Throwable
	 */
	@Around("createdCreditCardPaymentDetails()  || createdPayPalPaymentDetails() || createdMigPaymentDetails()")
	public Object createdPaymentDetails(ProceedingJoinPoint joinPoint) throws Throwable {
		try{
			LogUtils.putClassNameMDC(this.getClass());
			Object object = joinPoint.proceed();
			Integer userId = (Integer) joinPoint.getArgs()[joinPoint.getArgs().length-1];
			try{
				User user = userService.findById(userId);
				sendUnsubscribePotentialSMS(user);
			}catch (Exception e) {
				LOGGER.error(e.getMessage(), e);
			}
			return object;
		}finally{
			LogUtils.removeClassNameMDC();
		}
	}
	
	protected void sendLimitedStatusSMS(User user) {
		if(user == null || !user.getStatus().getName().equals(UserStatus.LIMITED.name()))
			return;
			
		sendSMSWithUrl(user, "sms.limited.status.text.for."+user.getProvider()+"."+user.getContract(), paymentsUrl);
	}
	
	protected void sendUnsubscribePotentialSMS(User user) {
		if(user == null || user.getCurrentPaymentDetails() == null)
			return;
				
		sendSMSWithUrl(user, "sms.unsubscribe.potential.text.for."+user.getProvider()+"."+user.getContract(), unsubscribeUrl);
	}
	
	protected void sendSMSWithUrl(User user, String msgCode, String baseUrl){
		Community community = user.getUserGroup().getCommunity();
		String communityUrl = community.getRewriteUrlParameter();
		if(!availableCommunities.contains(communityUrl))
			return;
		
		String url =  baseUrl + "?rememberMeToken=" + user.getToken()+"&community="+communityUrl;
		try{
			url = restTemplate.getForObject(tinyUrlService, String.class, url);			
		}catch(Exception e){
			LOGGER.error("Error get tinyUrl.");
		}
		
		String[] args = {url};
		String message = messageSource.getMessage(community.getRewriteUrlParameter(), msgCode, args, null);
		
		migService.makeFreeSMSRequest(user.getMobile(), message);
	}
}