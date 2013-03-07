package mobi.nowtechnologies.server.service.payment.impl;

import mobi.nowtechnologies.server.persistence.dao.UserStatusDao;
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
		
		String message = messageSource.getMessage(community.getRewriteUrlParameter().toLowerCase(), "sms.psms",
				new Object[] {community.getDisplayName(), paymentPolicy.getSubcost(), paymentPolicy.getSubweeks(), paymentPolicy.getShortCode() }, null);
		
		String internalTxId = Utils.getBigRandomInt().toString();
		O2Response response = o2ClientService.makePremiumSMSRequest(internalTxId, paymentPolicy.getShortCode(), currentPaymentDetails.getPhoneNumber(), message);
		
		pendingPayment.setInternalTxId(internalTxId);
		//pendingPayment.setExternalTxId(response.getExternalTxId());
			entityService.updateEntity(pendingPayment);
		LOGGER.info("Sent request to O2 with pending payment {}. {}", pendingPayment.getI(), response);
		if (!response.isSuccessful()) {
			LOGGER.error("External exception while making payment transaction with O2 for user {} ", user.getId());
		}
		
		commitPayment(pendingPayment, response);
	}
	
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public SubmittedPayment commitPayment(PendingPayment pendingPayment, PaymentSystemResponse response) throws ServiceException {
		LOGGER.debug("input parameters pendingPayment, response: [{}], [{}]", pendingPayment, response);

		final User user = pendingPayment.getUser();	
		user.setLastPaymentTryMillis(Utils.getEpochMillis());
		
		SubmittedPayment submittedPayment = super.commitPayment(pendingPayment, response);
		
		final PaymentDetails paymentDetails = pendingPayment.getPaymentDetails();
		final boolean isUserInLimitedStatus = user.getStatus().getName().equals(UserStatusDao.LIMITED);
		
		if (!response.isSuccessful()){
			if(!isUserInLimitedStatus && userService.mustTheAttemptsOfPaymentContinue(user)){
				paymentDetails.setActivated(true);
			}else{
				final String reson;
				if (isUserInLimitedStatus){
					reson = "The payment attempt was unsuccesed for user in LIMITED status";
				}else{
					reson = "Grace period expired";
				}
				userService.unsubscribeUser(user, reson);				
			}
		}
		
		
		
		LOGGER.debug("Output parameter submittedPayment=[{}]", submittedPayment);
		return submittedPayment;
	}

	@Override
	public O2PSMSPaymentDetails commitPaymnetDetails(User user) throws ServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public O2PSMSPaymentDetails createPaymentDetails(String phoneNumber, User user, PaymentPolicy paymentPolicy) throws ServiceException {
		// TODO Auto-generated method stub
		return null;
	}

}
