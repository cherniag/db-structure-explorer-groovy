package mobi.nowtechnologies.server.service.aop;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import mobi.nowtechnologies.server.persistence.domain.*;
import mobi.nowtechnologies.server.persistence.domain.enums.SegmentType;
import mobi.nowtechnologies.server.security.NowTechTokenBasedRememberMeServices;
import mobi.nowtechnologies.server.service.UserService;
import mobi.nowtechnologies.server.service.payment.http.MigHttpService;
import mobi.nowtechnologies.server.shared.enums.Contract;
import mobi.nowtechnologies.server.shared.enums.UserStatus;
import mobi.nowtechnologies.server.shared.log.LogUtils;
import mobi.nowtechnologies.server.shared.message.CommunityResourceBundleMessageSource;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
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

	private String rememberMeTokenCookieName;

	private NowTechTokenBasedRememberMeServices rememberMeServices;

	private RestTemplate restTemplate = new RestTemplate();

	public String getRememberMeTokenCookieName() {
		return rememberMeTokenCookieName;
	}

	public void setRememberMeTokenCookieName(String rememberMeTokenCookieName) {
		this.rememberMeTokenCookieName = rememberMeTokenCookieName;
	}

	public void setMigService(MigHttpService migService) {
		this.migService = migService;
	}

	public void setMessageSource(CommunityResourceBundleMessageSource messageSource) {
		this.messageSource = messageSource;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	public void setRememberMeServices(NowTechTokenBasedRememberMeServices rememberMeServices) {
		this.rememberMeServices = rememberMeServices;
	}

	public void setAvailableCommunities(String availableCommunities) {
		if (availableCommunities == null)
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
	
	@Pointcut("execution(* mobi.nowtechnologies.server.service.payment.impl.SagePayPaymentServiceImpl.startPayment(..))")
	protected void startCreditCardPayment() {
	}

	@Pointcut("execution(* mobi.nowtechnologies.server.service.payment.impl.PayPalPaymentServiceImpl.startPayment(..))")
	protected void startPayPalPayment() {
	}

	@Pointcut("execution(* mobi.nowtechnologies.server.service.payment.impl.O2PaymentServiceImpl.startPayment(..))")
	protected void startO2PSMSPayment() {
	}
	
	@Pointcut("execution(* mobi.nowtechnologies.server.service.payment.impl.MigPaymentServiceImpl.startPayment(..))")
	protected void startMigPayment() {
	}
	
	/**
	 * Sending sms after any payment system has spent all retries with failures
	 * 
	 * @param joinPoint
	 * @throws Throwable
	 */
	@Around("startCreditCardPayment()  || startPayPalPayment() || startO2PSMSPayment() || startMigPayment()")
	public Object startPayment(ProceedingJoinPoint joinPoint) throws Throwable {
		try {
			LogUtils.putClassNameMDC(this.getClass());

			Object object = joinPoint.proceed();
			PendingPayment pendingPayment = (PendingPayment) joinPoint.getArgs()[0];
			try {
				sendPaymentFailSMS(pendingPayment);
			} catch (Exception e) {
				LOGGER.error(e.getMessage(), e);
			}
			return object;
		} finally {
			LogUtils.removeClassNameMDC();
		}
	}

	/**
	 * Sending sms after user was set to limited status
	 * 
	 * @param joinPoint
	 * @throws Throwable
	 */
	@Around("execution(* mobi.nowtechnologies.server.service.UserService.saveWeeklyPayment(*))")
	public Object saveWeeklyPayment(ProceedingJoinPoint joinPoint) throws Throwable {
		try {
			LogUtils.putClassNameMDC(this.getClass());

			Object object = joinPoint.proceed();
			User user = (User) joinPoint.getArgs()[0];
			try {
				sendLimitedStatusSMS(user);
			} catch (Exception e) {
				LOGGER.error(e.getMessage(), e);
			}
			return object;
		} finally {
			LogUtils.removeClassNameMDC();
		}
	}

	/**
	 * Sending sms after user unsubscribe
	 * 
	 * @param joinPoint
	 * @throws Throwable
	 */
	@Around("execution(* mobi.nowtechnologies.server.service.UserService.unsubscribeUser(int, mobi.nowtechnologies.server.shared.dto.web.payment.UnsubscribeDto))")
	public Object unsubscribeUser(ProceedingJoinPoint joinPoint) throws Throwable {
		try {
			LogUtils.putClassNameMDC(this.getClass());

			Object object = joinPoint.proceed();
			Integer userId = (Integer) joinPoint.getArgs()[0];
			try {
				User user = userService.findById(userId);
				sendUnsubscribeAfterSMS(user);
			} catch (Exception e) {
				LOGGER.error(e.getMessage(), e);
			}
			return object;
		} finally {
			LogUtils.removeClassNameMDC();
		}
	}
	
	@SuppressWarnings("unchecked")
	@Around("execution(* mobi.nowtechnologies.server.service.UserService.unsubscribeUser(String, String))")
	public Object unsubscribeUserOnStopMessage(ProceedingJoinPoint joinPoint) throws Throwable {
		try {
			LogUtils.putClassNameMDC(this.getClass());

			List<PaymentDetails> paymentDetailsList = (List<PaymentDetails>) joinPoint.proceed();
			Set<User> users = new HashSet<User>();
			for (PaymentDetails paymentDetails : paymentDetailsList) {
				try {
					User user = paymentDetails.getOwner();
					if (!users.contains(user)) {
						sendUnsubscribeAfterSMS(user);
						users.add(user);
					}
				} catch (Exception e) {
					LOGGER.error(e.getMessage(), e);
				}
			}
			return paymentDetailsList;
		} finally {
			LogUtils.removeClassNameMDC();
		}
	}

	/**
	 * Sending sms before 48 h expire subscription
	 * 
	 * @param joinPoint
	 * @throws Throwable
	 */
	@Around("execution(* mobi.nowtechnologies.server.service.UserService.updateLastBefore48SmsMillis(..))")
	public Object updateLastBefore48SmsMillis(ProceedingJoinPoint joinPoint) throws Throwable {
		try {
			LogUtils.putClassNameMDC(this.getClass());
			Object object = joinPoint.proceed();
			Integer userId = (Integer) joinPoint.getArgs()[joinPoint.getArgs().length - 1];
			try {
				User user = userService.findById(userId);
				sendLowBalanceWarning(user);
			} catch (Exception e) {
				LOGGER.error(e.getMessage(), e);
			}
			return object;
		} finally {
			LogUtils.removeClassNameMDC();
		}
	}

	@Pointcut("execution(* mobi.nowtechnologies.server.service.PaymentDetailsService.createCreditCardPamentDetails(..))")
	protected void createdCreditCardPaymentDetails() {
	}

	@Pointcut("execution(* mobi.nowtechnologies.server.service.PaymentDetailsService.commitPayPalPaymentDetails(..))")
	protected void createdPayPalPaymentDetails() {
	}

	@Pointcut("execution(* mobi.nowtechnologies.server.service.PaymentDetailsService.commitMigPaymentDetails(..))")
	protected void createdMigPaymentDetails() {
	}
	
	@Pointcut("execution(* mobi.nowtechnologies.server.service.payment.impl.O2PaymentServiceImpl.commitPaymnetDetails(..))")
	protected void createdO2PsmsPaymentDetails() {
	}

	/**
	 * Sending sms after user was subscribed with some payment details
	 * 
	 * @param joinPoint
	 * @throws Throwable
	 */
	@Around("createdCreditCardPaymentDetails()  || createdPayPalPaymentDetails() || createdMigPaymentDetails()")
	public Object createdPaymentDetails(ProceedingJoinPoint joinPoint) throws Throwable {
		try {
			LogUtils.putClassNameMDC(this.getClass());
			Object object = joinPoint.proceed();
			Integer userId = (Integer) joinPoint.getArgs()[joinPoint.getArgs().length - 1];
			try {
				User user = userService.findById(userId);
				sendUnsubscribePotentialSMS(user);
			} catch (Exception e) {
				LOGGER.error(e.getMessage(), e);
			}
			return object;
		} finally {
			LogUtils.removeClassNameMDC();
		}
	}
	@Around("createdO2PsmsPaymentDetails()")
	public Object createdO2PsmsPaymentDetails(ProceedingJoinPoint joinPoint) throws Throwable {
		try {
			LogUtils.putClassNameMDC(this.getClass());
			Object object = joinPoint.proceed();
			User user = (User) joinPoint.getArgs()[0];
			try {
				sendUnsubscribePotentialSMS(user);
			} catch (Exception e) {
				LOGGER.error(e.getMessage(), e);
			}
			return object;
		} finally {
			LogUtils.removeClassNameMDC();
		}
	}

	protected void sendLimitedStatusSMS(User user) throws UnsupportedEncodingException {
		if (user == null || !user.getStatus().getName().equals(UserStatus.LIMITED.name()) || !user.getPaymentDetailsList().isEmpty())
			return;
		if (rejectDevice(user, "sms.notification.limited.not.for.device.type"))
			return;
		sendSMSWithUrl(user, getMessageCode(user, "sms.limited.status.text"), new String[] { paymentsUrl });
	}

	protected void sendUnsubscribePotentialSMS(User user) throws UnsupportedEncodingException {
		if (user == null || user.getCurrentPaymentDetails() == null)
			return;
		if (rejectDevice(user, "sms.notification.subscribed.not.for.device.type"))
			return;
		sendSMSWithUrl(user, getMessageCode(user, "sms.unsubscribe.potential.text"), new String[] { unsubscribeUrl });
	}

	protected void sendUnsubscribeAfterSMS(User user) throws UnsupportedEncodingException {
		if (user == null || user.getCurrentPaymentDetails() == null)
			return;
		Integer days = Days.daysBetween(new DateTime(System.currentTimeMillis()).toDateMidnight(), new DateTime(user.getNextSubPayment() * 1000L).toDateMidnight()).getDays();
		if (rejectDevice(user, "sms.notification.unsubscribed.not.for.device.type"))
			return;
		sendSMSWithUrl(user, getMessageCode(user, "sms.unsubscribe.after.text"), new String[] { paymentsUrl, days.toString() });
	}

	protected void sendLowBalanceWarning(User user) throws UnsupportedEncodingException {
		if (user == null || user.getCurrentPaymentDetails() == null || user.getContract() != Contract.PAYG || user.getSegment() != SegmentType.CONSUMER)
			return;
		if (rejectDevice(user, "sms.notification.lowBalance.not.for.device.type"))
			return;
		sendSMSWithUrl(user, getMessageCode(user, "sms.lowBalance.text"), null);
	}
	
	protected void sendPaymentFailSMS(PendingPayment pendingPayment) throws UnsupportedEncodingException {
		User user = pendingPayment.getUser();
		PaymentDetails paymentDetails = pendingPayment.getPaymentDetails();
		if (user == null || paymentDetails.getMadeRetries() != paymentDetails.getRetriesOnError())
			return;
		
		int hoursBefore = user.isBeforeExpiration(pendingPayment.getTimestamp(), 0) ? 0 : 24;
		if (rejectDevice(user, "sms.notification.paymentFail.at."+hoursBefore+"h.not.for.device.type"))
			return;
		
		sendSMSWithUrl(user, getMessageCode(user, "sms.paymentFail.at."+hoursBefore+"h.text"), new String[] { paymentsUrl });
	}

	protected void sendSMSWithUrl(User user, String msgCode, String[] msgArgs) throws UnsupportedEncodingException {
		Community community = user.getUserGroup().getCommunity();
		String communityUrl = community.getRewriteUrlParameter();
		if (rejectDevice(user, "sms.notification.not.for.device.type"))
			return;
		if (!availableCommunities.contains(communityUrl))
			return;

		String baseUrl = msgArgs != null ? msgArgs[0] : null;
		if (baseUrl != null) {
			String rememberMeToken = rememberMeServices.getRememberMeToken(user.getUserName(), user.getToken());
			String url = baseUrl + "?community=" + communityUrl + "&" + rememberMeTokenCookieName + "=" + rememberMeToken;

			MultiValueMap<String, Object> request = new LinkedMultiValueMap<String, Object>();
			request.add("url", url);

			try {
				url = restTemplate.postForEntity(tinyUrlService, request, String.class).getBody();
			} catch (Exception e) {
				LOGGER.error("Error get tinyUrl. tinyLink:[{}], error:[{}]", tinyUrlService, e.getMessage());
			}

			msgArgs[0] = url;
		}

		String message = messageSource.getMessage(community.getRewriteUrlParameter(), msgCode, msgArgs, "", null);
		String title = messageSource.getMessage(community.getRewriteUrlParameter(), "sms.title", null, null);

		if (!message.isEmpty())
			migService.makeFreeSMSRequest(user.getMobile(), message, title);
	}

	protected boolean rejectDevice(User user, String code) {
		Community community = user.getUserGroup().getCommunity();  
	  	String communityUrl = community.getRewriteUrlParameter();  
	  	String devices = messageSource.getMessage(communityUrl, code, null, null, null); 
		for (String device : devices.split(",")) {
			if (user.getDeviceTypeIdString().equalsIgnoreCase(device)) {
				LOGGER.warn("SMS will not send for User[{}]. See prop:[{}]", user.getUserName(), code);
				return true;
			}
		}
		return false;
	}

	protected String getMessageCode(User user, String msgCodeBase){
		if(user.getProvider() != null){
			msgCodeBase += ".for."+user.getProvider();
			if(user.getSegment() != null){
				msgCodeBase += "."+user.getSegment();
				if(user.getContract() != null){
					msgCodeBase += "."+user.getContract();
				}
			}
		}
		
		return msgCodeBase;
	}
}
