package mobi.nowtechnologies.server.service.itunes;

import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.payment.ITunesPaymentLock;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentPolicy;
import mobi.nowtechnologies.server.persistence.domain.payment.SubmittedPayment;
import mobi.nowtechnologies.server.persistence.repository.ITunesPaymentLockRepository;
import mobi.nowtechnologies.server.service.PaymentPolicyService;
import mobi.nowtechnologies.server.service.UserService;
import mobi.nowtechnologies.server.service.event.PaymentEvent;
import mobi.nowtechnologies.server.service.payment.SubmittedPaymentService;
import mobi.nowtechnologies.server.shared.Utils;
import mobi.nowtechnologies.server.shared.dto.ITunesInAppSubscriptionResponseDto;
import mobi.nowtechnologies.server.shared.dto.ITunesInAppSubscriptionResponseDto.Receipt;
import mobi.nowtechnologies.server.shared.enums.PaymentDetailsStatus;
import mobi.nowtechnologies.server.shared.message.CommunityResourceBundleMessageSource;
import mobi.nowtechnologies.server.shared.service.BasicResponse;
import mobi.nowtechnologies.server.shared.service.PostService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;

import static mobi.nowtechnologies.server.persistence.domain.payment.PaymentDetailsType.FIRST;
import static mobi.nowtechnologies.server.persistence.domain.payment.PaymentDetailsType.REGULAR;

/**
 * @author Titov Mykhaylo (titov)
 */
public class ITunesServiceImpl implements ITunesService, ApplicationEventPublisherAware {

	private final Logger logger = LoggerFactory.getLogger(getClass());
    private CommunityResourceBundleMessageSource messageSource;
	private PostService postService;
	private PaymentPolicyService paymentPolicyService;
	private UserService userService;
	private ApplicationEventPublisher applicationEventPublisher;
	private SubmittedPaymentService submittedPaymentService;
    private ITunesPaymentLockRepository iTunesPaymentLockRepository;
    private ITunesDtoConverter iTunesDtoConverter;

    @Override
	public BasicResponse processInAppSubscription(int userId, String transactionReceipt) {
		logger.debug("input parameters userId, transactionReceipt: [{}], [{}]", userId, transactionReceipt);

		User user = userService.findById(userId);

        boolean userDoesNotHaveAppReceiptOrNotInLimitedState = user.getBase64EncodedAppStoreReceipt() == null || !user.hasLimitedStatus();

        if (user.hasActivePaymentDetails() || (userDoesNotHaveAppReceiptOrNotInLimitedState && transactionReceipt == null)) {
            return null;
        }

        final String actualReceipt = decideAppReceipt(transactionReceipt, user);
        BasicResponse basicResponse = validateInITunes(user, actualReceipt);

        if (basicResponse.getStatusCode() != HttpStatus.OK.value()) {
            logger.info("The request of in-app subscription validation returned unexpected basicResponse [{}]", basicResponse);
            return basicResponse;
        }

        ITunesInAppSubscriptionResponseDto responseDto = iTunesDtoConverter.convertToResponseDTO(basicResponse);

        if (responseDto.isSuccess()) {
            logger.info("ITunes confirmed that encoded receipt [{}] is valid by basicResponse [{}]", actualReceipt, responseDto);

            Receipt latestReceiptInfo = responseDto.getLatestReceiptInfo();
            int nextSubPayment = latestReceiptInfo.getExpiresDateSeconds();
            try {
                checkForDuplicates(user.getId(), nextSubPayment);

                SubmittedPayment submittedPayment = createSubmittedPayment(user, actualReceipt, latestReceiptInfo);
                submittedPaymentService.save(submittedPayment);

                PaymentEvent paymentEvent = new PaymentEvent(submittedPayment);
                applicationEventPublisher.publishEvent(paymentEvent);
            } catch (DataIntegrityViolationException e) {
                logger.warn("Record with the same next sub payment [{}] for user [{}] already exists", nextSubPayment, user.getId());
                user.setBase64EncodedAppStoreReceipt(actualReceipt);
                userService.updateUser(user);
            }
        } else {
            logger.info("ITunes rejected the encoded receipt [{}] by basicResponse [{}]", actualReceipt, basicResponse);
        }


        logger.debug("Output parameter basicResponse=[{}]", basicResponse);
		return basicResponse;
	}

    private BasicResponse validateInITunes(User user, String appStoreReceipt){
        String iTunesUrl = messageSource.getMessage(user.getCommunityRewriteUrl(), "apple.inApp.iTunesUrl", null, null);
        String password = messageSource.getMessage(user.getCommunityRewriteUrl(), "apple.inApp.password", null, null);

        String body = iTunesDtoConverter.convertToRequestBody(appStoreReceipt, password);

        logger.info("Trying to validate in-app subscription with following params [{}]", body);
        return postService.sendHttpPost(iTunesUrl, body);
    }

    private void checkForDuplicates(int userId, int nextSubPayment) {
        ITunesPaymentLock iTunesPaymentLock = new ITunesPaymentLock(userId, nextSubPayment);
        iTunesPaymentLockRepository.saveAndFlush(iTunesPaymentLock);
    }

    private String decideAppReceipt(String transactionReceipt, User user) {
        if (user.getBase64EncodedAppStoreReceipt() == null || transactionReceipt != null) {
            return transactionReceipt;
        } else {
            return user.getBase64EncodedAppStoreReceipt();
        }
    }

    private SubmittedPayment createSubmittedPayment(User user, String base64EncodedAppStoreReceipt, Receipt latestReceiptInfo) {
        PaymentPolicy paymentPolicy = paymentPolicyService.findByCommunityAndAppStoreProductId(user.getUserGroup().getCommunity(), latestReceiptInfo.getProductId());

        SubmittedPayment submittedPayment = new SubmittedPayment();
        submittedPayment.setStatus(PaymentDetailsStatus.SUCCESSFUL);
        submittedPayment.setUser(user);
        submittedPayment.setTimestamp(Utils.getEpochMillis());
        submittedPayment.setAmount(paymentPolicy.getSubcost());
        submittedPayment.setExternalTxId(latestReceiptInfo.getOriginalTransactionId());
        submittedPayment.setCurrencyISO(paymentPolicy.getCurrencyISO());
        submittedPayment.setNextSubPayment(latestReceiptInfo.getExpiresDateSeconds());
        submittedPayment.setAppStoreOriginalTransactionId(latestReceiptInfo.getOriginalTransactionId());
        submittedPayment.setPaymentSystem(PaymentDetails.ITUNES_SUBSCRIPTION);
        submittedPayment.setBase64EncodedAppStoreReceipt(base64EncodedAppStoreReceipt);
        submittedPayment.setPeriod(paymentPolicy.getPeriod());

        boolean isFirstPayment = user.getLastSuccessfulPaymentTimeMillis() == 0;
        submittedPayment.setType(isFirstPayment ? FIRST : REGULAR);
        return submittedPayment;
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

    public void setSubmittedPaymentService(SubmittedPaymentService submittedPaymentService) {
        this.submittedPaymentService = submittedPaymentService;
    }

    public void setMessageSource(CommunityResourceBundleMessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public void setiTunesPaymentLockRepository(ITunesPaymentLockRepository iTunesPaymentLockRepository) {
        this.iTunesPaymentLockRepository = iTunesPaymentLockRepository;
    }

    public void setiTunesDtoConverter(ITunesDtoConverter iTunesDtoConverter) {
        this.iTunesDtoConverter = iTunesDtoConverter;
    }
}
