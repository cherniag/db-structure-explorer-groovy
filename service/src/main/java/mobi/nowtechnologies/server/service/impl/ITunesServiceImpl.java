package mobi.nowtechnologies.server.service.impl;

import com.google.gson.Gson;
import mobi.nowtechnologies.server.persistence.dao.UserStatusDao;
import mobi.nowtechnologies.server.persistence.domain.*;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentDetailsType;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentPolicy;
import mobi.nowtechnologies.server.persistence.domain.payment.SubmittedPayment;
import mobi.nowtechnologies.server.service.ITunesService;
import mobi.nowtechnologies.server.service.PaymentPolicyService;
import mobi.nowtechnologies.server.service.UserService;
import mobi.nowtechnologies.server.service.event.PaymentEvent;
import mobi.nowtechnologies.server.service.payment.SubmitedPaymentService;
import mobi.nowtechnologies.server.shared.Utils;
import mobi.nowtechnologies.server.shared.dto.ITunesInAppSubscriptionRequestDto;
import mobi.nowtechnologies.server.shared.dto.ITunesInAppSubscriptionResponseDto;
import mobi.nowtechnologies.server.shared.dto.ITunesInAppSubscriptionResponseDto.Receipt;
import mobi.nowtechnologies.server.shared.enums.PaymentDetailsStatus;
import mobi.nowtechnologies.server.shared.service.BasicResponse;
import mobi.nowtechnologies.server.shared.service.PostService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Titov Mykhaylo (titov)
 * 
 */
public class ITunesServiceImpl implements ITunesService, ApplicationEventPublisherAware {

	private static final Logger LOGGER = LoggerFactory.getLogger(ITunesServiceImpl.class);

	private String iTunesUrl;
	private String password;

	private PostService postService;
	private PaymentPolicyService paymentPolicyService;
	private UserService userService;
	private ApplicationEventPublisher applicationEventPublisher;
	private SubmitedPaymentService submitedPaymentService;
	
	private static Gson gson = new Gson();

	public void setiTunesUrl(String iTunesUrl) {
		this.iTunesUrl = iTunesUrl;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setPostService(PostService postService) {
		this.postService = postService;
	}

	public void setPaymentPolicyService(PaymentPolicyService paymentPolicyService) {
		this.paymentPolicyService = paymentPolicyService;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
		this.applicationEventPublisher = applicationEventPublisher;
	}
	
	public void setSubmitedPaymentService(SubmitedPaymentService submitedPaymentService) {
		this.submitedPaymentService = submitedPaymentService;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public BasicResponse processInAppSubscription(int userId, String transactionReceipt) {
		LOGGER.debug("input parameters userId, transactionReceipt: [{}], [{}]", userId, transactionReceipt);

		final BasicResponse BasicResponse;
		User user = userService.findById(userId);

		if (user != null &&(user.getCurrentPaymentDetails() == null || !user.getCurrentPaymentDetails().isActivated()) && (((user.getBase64EncodedAppStoreReceipt() != null && user.getStatus().getI() == UserStatusDao.getLimitedUserStatus().getI())
				|| transactionReceipt != null))) {

			final String base64EncodedAppStoreReceipt;
			if (user.getBase64EncodedAppStoreReceipt() == null || transactionReceipt != null) {
				base64EncodedAppStoreReceipt = transactionReceipt;
			} else {
				base64EncodedAppStoreReceipt = user.getBase64EncodedAppStoreReceipt();
			}

			ITunesInAppSubscriptionRequestDto iTunesInAppSubscriptionRequestDto = new ITunesInAppSubscriptionRequestDto();
			iTunesInAppSubscriptionRequestDto.setPassword(password);
			iTunesInAppSubscriptionRequestDto.setReceiptData(base64EncodedAppStoreReceipt);

			String body = gson.toJson(iTunesInAppSubscriptionRequestDto);
			
			LOGGER.info("Trying to validate in-app subscription with following params [{}]", iTunesInAppSubscriptionRequestDto);
			BasicResponse = postService.sendHttpPost(iTunesUrl, null, body);

			if (BasicResponse.getStatusCode() == HttpStatus.OK.value()) {
				ITunesInAppSubscriptionResponseDto iTunesInAppSubscriptionResponseDto = gson.fromJson(BasicResponse.getMessage(), ITunesInAppSubscriptionResponseDto.class);

				if (iTunesInAppSubscriptionResponseDto.isSuccess()) {
					LOGGER.info("ITunes confirmed that encoded receipt [{}] is valid by BasicResponse [{}]", base64EncodedAppStoreReceipt, iTunesInAppSubscriptionResponseDto);
					
					Receipt latestReceiptInfo = iTunesInAppSubscriptionResponseDto.getLatestReceiptInfo();
					PaymentPolicy paymentPolicy = paymentPolicyService.findByCommunityAndAppStoreProductId(user.getUserGroup().getCommunity(), latestReceiptInfo.getProductId());
					
					final PaymentDetailsType paymentDetailsType;
					if (user.getLastSuccessfulPaymentTimeMillis() == 0) {
						paymentDetailsType = PaymentDetailsType.FIRST;
					} else {
						paymentDetailsType = PaymentDetailsType.REGULAR;
					}

					SubmittedPayment submittedPayment = new SubmittedPayment();
					submittedPayment.setStatus(PaymentDetailsStatus.SUCCESSFUL);
					submittedPayment.setUser(user);
					submittedPayment.setTimestamp(Utils.getEpochMillis());
					submittedPayment.setAmount(paymentPolicy.getSubcost());
					submittedPayment.setExternalTxId(latestReceiptInfo.getOriginalTransactionId());
					submittedPayment.setType(paymentDetailsType);
					submittedPayment.setCurrencyISO(paymentPolicy.getCurrencyISO());
					submittedPayment.setNextSubPayment(latestReceiptInfo.getExpiresDateSeconds());
					submittedPayment.setAppStoreOriginalTransactionId(latestReceiptInfo.getOriginalTransactionId());
					submittedPayment.setPaymentSystem(PaymentDetails.ITUNES_SUBSCRIPTION);
					submittedPayment.setBase64EncodedAppStoreReceipt(base64EncodedAppStoreReceipt);
					
					submitedPaymentService.save(submittedPayment);

					PaymentEvent paymentEvent = new PaymentEvent(submittedPayment);
					applicationEventPublisher.publishEvent(paymentEvent);
				} else {
					LOGGER.info("ITunes rejected the encoded receipt [{}] by BasicResponse [{}]", base64EncodedAppStoreReceipt, BasicResponse);
				}
			}else{
				LOGGER.info("The request of in-app subscription validation returned unexpected BasicResponse [{}]", BasicResponse);
			}
		} else {
			BasicResponse = null;
		}
		
		LOGGER.debug("Output parameter BasicResponse=[{}]", BasicResponse);
		return BasicResponse;
	}

}
