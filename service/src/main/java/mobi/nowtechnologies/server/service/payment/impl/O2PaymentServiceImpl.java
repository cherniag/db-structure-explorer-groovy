package mobi.nowtechnologies.server.service.payment.impl;

import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.O2PSMSPaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.PaymentPolicy;
import mobi.nowtechnologies.server.persistence.domain.PendingPayment;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.service.DataToDoRefundService;
import mobi.nowtechnologies.server.service.O2ClientService;
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

    @Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void startPayment(PendingPayment pendingPayment) throws Exception {
		LOGGER.debug("input parameters pendingPayment: [{}]", pendingPayment);
		final User user = pendingPayment.getUser();
		final O2PSMSPaymentDetails currentPaymentDetails = (O2PSMSPaymentDetails) user.getCurrentPaymentDetails();
		final PaymentPolicy paymentPolicy = currentPaymentDetails.getPaymentPolicy();
		Community community = user.getUserGroup().getCommunity();
		
		Boolean smsNotify = Boolean.valueOf(messageSource.getMessage(community.getRewriteUrlParameter().toLowerCase(), "sms.o2_psms.send",
				null, null));
		
		String message = messageSource.getMessage(community.getRewriteUrlParameter().toLowerCase(), "sms.o2_psms",
				new Object[] {community.getDisplayName(), pendingPayment.getAmount(), pendingPayment.getSubweeks(), paymentPolicy.getShortCode() }, null);
		
		String internalTxId = Utils.getBigRandomInt().toString();
		O2Response response = o2ClientService.makePremiumSMSRequest(user.getId(), internalTxId, pendingPayment.getAmount(), currentPaymentDetails.getPhoneNumber(), message,
				paymentPolicy.getContentCategory(), paymentPolicy.getContentType(), paymentPolicy.getContentDescription(), paymentPolicy.getSubMerchantId(), smsNotify);
		
		pendingPayment.setInternalTxId(internalTxId);
		
		final String externalTxId = response.getExternalTxId();
		if (externalTxId!=null){
			pendingPayment.setExternalTxId(externalTxId);
		}else{
			pendingPayment.setExternalTxId("");
		}
			entityService.updateEntity(pendingPayment);
		LOGGER.info("Sent request to O2 with pending payment [{}]. [{}]", pendingPayment.getI(), response);
		if (!response.isSuccessful()) {
			LOGGER.error("External exception while making payment transaction with O2 for user with id: [{}] ", user.getId());
		}
		
		commitPayment(pendingPayment, response);
	}
	
	
	public boolean mustTheAttemptsOfPaymentContinue(User user) {
		LOGGER.debug("input parameters user: [{}]", user);
		
		boolean mustTheAttemptsOfPaymentContinue = false;
		if (user.isSubscribedStatus() && user.getLastPaymentTryInCycleSeconds() < (user.getNextSubPayment() + 0)) {
			mustTheAttemptsOfPaymentContinue = true;
		}
		LOGGER.debug("Output parameter mustTheAttemptsOfPaymentContinue=[{}]", mustTheAttemptsOfPaymentContinue);
		return mustTheAttemptsOfPaymentContinue;
	}

	@Override
	public O2PSMSPaymentDetails commitPaymentDetails(User user, PaymentPolicy paymentPolicy) throws ServiceException {
		LOGGER.info("Commiting o2Psms payment details for user {} ...", user.getUserName());
		
		O2PSMSPaymentDetails details = new O2PSMSPaymentDetails();
		details.setLastPaymentStatus(PaymentDetailsStatus.NONE);
		details.setPaymentPolicy(paymentPolicy);
		details.setMadeRetries(0);
        details.setPhoneNumber(user.getMobile());
		details.setRetriesOnError(getRetriesOnError());
		details.setCreationTimestampMillis(Utils.getEpochMillis());
		details.setActivated(true);
		
		paymentDetailsService.deactivateCurrentPaymentDetailsIfOneExist(user, "Commit new payment details");

		user.setCurrentPaymentDetails(details);
		details.setOwner(user);

		details = (O2PSMSPaymentDetails) getPaymentDetailsRepository().save(details);
		
		LOGGER.info("Done creation of o2Psms payment details for user {}", user.getUserName());
		
		return details;
    }

}
