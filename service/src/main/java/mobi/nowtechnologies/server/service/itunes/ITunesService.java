package mobi.nowtechnologies.server.service.itunes;

import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.payment.*;
import mobi.nowtechnologies.server.persistence.repository.ITunesPaymentLockRepository;
import mobi.nowtechnologies.server.persistence.repository.PaymentPolicyRepository;
import mobi.nowtechnologies.server.service.PaymentPolicyService;
import mobi.nowtechnologies.server.service.UserService;
import mobi.nowtechnologies.server.service.event.PaymentEvent;
import mobi.nowtechnologies.server.service.payment.SubmittedPaymentService;
import mobi.nowtechnologies.server.shared.Utils;
import mobi.nowtechnologies.server.shared.enums.PaymentDetailsStatus;
import mobi.nowtechnologies.server.shared.service.BasicResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.util.Assert;

import java.util.concurrent.TimeUnit;

import static mobi.nowtechnologies.server.persistence.domain.payment.PaymentDetailsType.FIRST;
import static mobi.nowtechnologies.server.persistence.domain.payment.PaymentDetailsType.REGULAR;

/**
 * @author Titov Mykhaylo (titov)
 */
public class ITunesService implements ApplicationEventPublisherAware {

	private final Logger logger = LoggerFactory.getLogger(getClass());

    private ITunesValidator iTunesValidator;
	private PaymentPolicyService paymentPolicyService;
	private UserService userService;
	private ApplicationEventPublisher applicationEventPublisher;
	private SubmittedPaymentService submittedPaymentService;
    private ITunesPaymentLockRepository iTunesPaymentLockRepository;
    private PaymentPolicyRepository paymentPolicyRepository;
    private ITunesReceiptParser iTunesReceiptParser;

	public void processInAppSubscription(int userId, String transactionReceipt) {
		logger.info("Start processing ITunes subscription for user [{}], receipt [{}]", userId, transactionReceipt);

		User user = userService.findById(userId);

        boolean userDoesNotHaveAppReceiptOrNotInLimitedState = user.getBase64EncodedAppStoreReceipt() == null || !user.hasLimitedStatus();

        if (user.hasActivePaymentDetails() || (userDoesNotHaveAppReceiptOrNotInLimitedState && transactionReceipt == null)) {
            return;
        }

        final String actualReceipt = decideAppReceipt(transactionReceipt, user);
        BasicResponse basicResponse = iTunesValidator.validateInITunes(user, actualReceipt);

        if (basicResponse.getStatusCode() != HttpStatus.OK.value()) {
            logger.info("The request of in-app subscription validation returned unexpected basicResponse [{}]", basicResponse);
            return;
        }

        ITunesParseResult parseResult = iTunesReceiptParser.parse(basicResponse.getMessage());
        calcExpireTimestamp(user.getUserGroup().getCommunity(), parseResult);

        if (parseResult.isSuccessful()) {
            logger.info("ITunes confirmed that encoded receipt [{}] is valid by parseResult [{}]", actualReceipt, parseResult);

            try {
                checkForDuplicates(user.getId(), parseResult.getExpireTime());
            } catch (DataIntegrityViolationException e) {
                logger.info("Record with the same next sub payment millis [{}] for user [{}] already exists", parseResult.getExpireTime(), user.getId());
                return;
            }

            SubmittedPayment submittedPayment = createSubmittedPayment(user, actualReceipt, parseResult);
            submittedPaymentService.save(submittedPayment);

            PaymentEvent paymentEvent = new PaymentEvent(submittedPayment);
            applicationEventPublisher.publishEvent(paymentEvent);
            logger.info("Finish processing ITunes subscription");
        } else {
            logger.info("ITunes rejected the encoded receipt [{}] by basicResponse [{}], parseResult: [{}]", actualReceipt, basicResponse, parseResult);
        }
	}

    private void checkForDuplicates(int userId, long nextSubPaymentTimestamp) {
        int nextSubPayment = (int) TimeUnit.MILLISECONDS.toSeconds(nextSubPaymentTimestamp);
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

    private SubmittedPayment createSubmittedPayment(User user, String base64EncodedAppStoreReceipt, ITunesParseResult parseResult) {
        final int expireTime = (int) TimeUnit.MILLISECONDS.toSeconds(parseResult.getExpireTime());

        Assert.isTrue(expireTime > Utils.getEpochSeconds(), "parseResult [" + parseResult + "] must have expireDate in future");

        PaymentPolicy paymentPolicy = paymentPolicyService.findByCommunityAndAppStoreProductId(user.getUserGroup().getCommunity(), parseResult.getProductId());

        SubmittedPayment submittedPayment = new SubmittedPayment();
        submittedPayment.setNextSubPayment(expireTime);
        submittedPayment.setExternalTxId(parseResult.getOriginalTransactionId());
        submittedPayment.setAppStoreOriginalTransactionId(parseResult.getOriginalTransactionId());
        submittedPayment.setStatus(PaymentDetailsStatus.SUCCESSFUL);
        submittedPayment.setUser(user);
        submittedPayment.setTimestamp(Utils.getEpochMillis());
        submittedPayment.setAmount(paymentPolicy.getSubcost());
        submittedPayment.setCurrencyISO(paymentPolicy.getCurrencyISO());
        submittedPayment.setPaymentSystem(PaymentDetails.ITUNES_SUBSCRIPTION);
        submittedPayment.setBase64EncodedAppStoreReceipt(base64EncodedAppStoreReceipt);
        submittedPayment.setPeriod(paymentPolicy.getPeriod());

        boolean isFirstPayment = user.getLastSuccessfulPaymentTimeMillis() == 0;
        submittedPayment.setType(isFirstPayment ? FIRST : REGULAR);
        return submittedPayment;
    }

    private void calcExpireTimestamp(Community community, ITunesParseResult parseResult) {
        if (parseResult.isSuccessful() && parseResult.getExpireTime() == null) {
            logger.debug("Calculate expire timestamp for parse result {} and community id {}", parseResult, community.getId());
            PaymentPolicy policy = paymentPolicyRepository.findByCommunityAndAppStoreProductId(community, parseResult.getProductId());
            Period period = policy.getPeriod();
            int purchaseSeconds = (int) TimeUnit.MILLISECONDS.toSeconds(parseResult.getPurchaseTime());
            int nextSubPaymentSeconds = period.toNextSubPaymentSeconds(purchaseSeconds);
            parseResult.updateExpireTime(TimeUnit.SECONDS.toMillis(nextSubPaymentSeconds));
        }
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

    public void setiTunesPaymentLockRepository(ITunesPaymentLockRepository iTunesPaymentLockRepository) {
        this.iTunesPaymentLockRepository = iTunesPaymentLockRepository;
    }

    public void setiTunesValidator(ITunesValidator iTunesValidator) {
        this.iTunesValidator = iTunesValidator;
    }

    public void setPaymentPolicyRepository(PaymentPolicyRepository paymentPolicyRepository) {
        this.paymentPolicyRepository = paymentPolicyRepository;
    }

    public void setiTunesReceiptParser(ITunesReceiptParser iTunesReceiptParser) {
        this.iTunesReceiptParser = iTunesReceiptParser;
    }
}
