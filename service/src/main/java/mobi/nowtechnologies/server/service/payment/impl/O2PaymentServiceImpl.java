package mobi.nowtechnologies.server.service.payment.impl;

import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.O2PSMSPaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.PaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.PaymentPolicy;
import mobi.nowtechnologies.server.persistence.domain.PendingPayment;
import mobi.nowtechnologies.server.persistence.domain.SubmittedPayment;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.service.O2ClientService;
import mobi.nowtechnologies.server.service.UserService;
import mobi.nowtechnologies.server.service.exception.ServiceException;
import mobi.nowtechnologies.server.service.payment.AbstractPaymentSystemService;
import mobi.nowtechnologies.server.service.payment.O2PaymentService;
import mobi.nowtechnologies.server.service.payment.response.O2Response;
import mobi.nowtechnologies.server.service.payment.response.PaymentSystemResponse;
import mobi.nowtechnologies.server.shared.Utils;
import mobi.nowtechnologies.server.shared.enums.PaymentDetailsStatus;
import mobi.nowtechnologies.server.shared.message.CommunityResourceBundleMessageSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Titov Mykhaylo (titov)
 *
 */
public class O2PaymentServiceImpl extends AbstractPaymentSystemService implements O2PaymentService{
	
	private static final Logger LOGGER = LoggerFactory.getLogger(O2PaymentServiceImpl.class);
	
	private O2ClientService o2ClientService;
	private CommunityResourceBundleMessageSource messageSource;
	private UserService userService;
	
	public void setO2ClientService(O2ClientService o2ClientService) {
		this.o2ClientService = o2ClientService;
	}
	
	public void setMessageSource(CommunityResourceBundleMessageSource messageSource) {
		this.messageSource = messageSource;
	}

	@Override
	public PaymentSystemResponse getExpiredResponse() {
		return O2Response.failO2Response("O2 pending payment has been expired");
	}
	
	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void startPayment(PendingPayment pendingPayment) throws Exception {
		LOGGER.debug("input parameters pendingPayment: [{}]", pendingPayment);
		final User user = pendingPayment.getUser();
		final O2PSMSPaymentDetails currentPaymentDetails = (O2PSMSPaymentDetails) user.getCurrentPaymentDetails();
		final PaymentPolicy paymentPolicy = currentPaymentDetails.getPaymentPolicy();
		Community community = user.getUserGroup().getCommunity();
		
		String message = messageSource.getMessage(community.getRewriteUrlParameter().toLowerCase(), "sms.o2_psms",
				new Object[] {community.getDisplayName(), pendingPayment.getAmount(), pendingPayment.getSubweeks(), paymentPolicy.getShortCode() }, null);
		
		String internalTxId = Utils.getBigRandomInt().toString();
		O2Response response = o2ClientService.makePremiumSMSRequest(user.getId(), internalTxId, pendingPayment.getAmount(), currentPaymentDetails.getPhoneNumber(), message,
				paymentPolicy.getContentCategory(), paymentPolicy.getContentType(), paymentPolicy.getContentDescription(), paymentPolicy.getSubMerchantId());
		
		pendingPayment.setInternalTxId(internalTxId);
		pendingPayment.setExternalTxId(response.getExternalTxId());
			entityService.updateEntity(pendingPayment);
		LOGGER.info("Sent request to O2 with pending payment [{}]. [{}]", pendingPayment.getI(), response);
		if (!response.isSuccessful()) {
			LOGGER.error("External exception while making payment transaction with O2 for user with id: [{}] ", user.getId());
		}
		
		commitPayment(pendingPayment, response);
	}
	
	
	public boolean mustTheAttemptsOfPaymentContinue(User user) {
		LOGGER.debug("input parameters user: [{}]", user);
		
		int graceDurationSeconds = user.getGraceDurationSeconds();
		
		boolean mustTheAttemptsOfPaymentContinue = false;
		if (user.isSubscribedStatus() && user.getLastPaymentTryInCycleSeconds() < (user.getNextSubPayment() + graceDurationSeconds)) {
			mustTheAttemptsOfPaymentContinue = true;
		}
		LOGGER.debug("Output parameter mustTheAttemptsOfPaymentContinue=[{}]", mustTheAttemptsOfPaymentContinue);
		return mustTheAttemptsOfPaymentContinue;
	}

	@Override
	public O2PSMSPaymentDetails commitPaymnetDetails(User user, PaymentPolicy paymentPolicy) throws ServiceException {
		// TODO Auto-generated method stub
		LOGGER.info("Commiting o2Psms payment details for user {} ...", user.getUserName());
		
		O2PSMSPaymentDetails o2PSMSPaymentDetails = new O2PSMSPaymentDetails();
		o2PSMSPaymentDetails.setLastPaymentStatus(PaymentDetailsStatus.NONE);
		o2PSMSPaymentDetails.setPaymentPolicy(paymentPolicy);
		o2PSMSPaymentDetails.setMadeRetries(0);
		o2PSMSPaymentDetails.setRetriesOnError(getRetriesOnError());
		o2PSMSPaymentDetails.setCreationTimestampMillis(Utils.getEpochMillis());
		//o2PSMSPaymentDetails.setActivated(activated);
		
		paymentDetailsService.deactivateCurrentPaymentDetailsIfOneExist(user, "Commit new payment details");
		
		user.setCurrentPaymentDetails(o2PSMSPaymentDetails);
		o2PSMSPaymentDetails.setOwner(user);

		o2PSMSPaymentDetails = (O2PSMSPaymentDetails) getPaymentDetailsRepository().save(o2PSMSPaymentDetails);
		
		LOGGER.info("Done creation of o2Psms payment details for user {}", user.getUserName());
		
		return o2PSMSPaymentDetails;
	}

	@Override
	public O2PSMSPaymentDetails createPaymentDetails(String phoneNumber, User user, PaymentPolicy paymentPolicy) throws ServiceException {
		LOGGER.info("Creating o2Psms payment details...");
		
		O2PSMSPaymentDetails o2PSMSPaymentDetails = commitPaymnetDetails(user, paymentPolicy);
		return o2PSMSPaymentDetails;
	}

}
