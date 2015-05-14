package mobi.nowtechnologies.server.service.payment;

import mobi.nowtechnologies.common.util.DateTimeUtils;
import mobi.nowtechnologies.server.TimeService;
import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.payment.ITunesPaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentPolicy;
import mobi.nowtechnologies.server.persistence.domain.payment.PendingPayment;
import mobi.nowtechnologies.server.persistence.domain.payment.Period;
import mobi.nowtechnologies.server.persistence.domain.payment.SubmittedPayment;
import mobi.nowtechnologies.server.persistence.repository.PaymentDetailsRepository;
import mobi.nowtechnologies.server.persistence.repository.PendingPaymentRepository;
import mobi.nowtechnologies.server.service.ITunesPaymentDetailsService;
import mobi.nowtechnologies.server.service.PaymentPolicyService;
import mobi.nowtechnologies.server.service.UserService;
import mobi.nowtechnologies.server.service.event.PaymentEvent;
import mobi.nowtechnologies.server.service.itunes.ITunesResult;
import mobi.nowtechnologies.server.shared.enums.PaymentDetailsStatus;

import static mobi.nowtechnologies.server.persistence.domain.payment.PaymentDetailsType.FIRST;
import static mobi.nowtechnologies.server.persistence.domain.payment.PaymentDetailsType.REGULAR;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
/**
 * Author: Gennadii Cherniaiev Date: 4/15/2015
 */
public class ITunesPaymentSystemServiceHelper implements ApplicationEventPublisherAware{
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private ITunesPaymentDetailsService iTunesPaymentDetailsService;
    private PaymentPolicyService paymentPolicyService;
    private PaymentDetailsRepository paymentDetailsRepository;
    private ApplicationEventPublisher applicationEventPublisher;
    private SubmittedPaymentService submittedPaymentService;
    private PendingPaymentRepository pendingPaymentRepository;
    private UserService userService;
    private TimeService timeService;

    @Transactional
    public void confirmPayment(PendingPayment pendingPayment, ITunesResult result) {
        final User user = pendingPayment.getUser();
        logger.info("Process payment confirmation for user {}, result {}", user.getId(), result);

        checkPaymentPolicy(user, result);

        ITunesPaymentDetails updatedPaymentDetails = user.getCurrentPaymentDetails();
        updatedPaymentDetails.completeSuccessful();
        paymentDetailsRepository.save(updatedPaymentDetails);

        logger.info("Create submitted payment for user {} and transaction {} and publish payment event", user.getId(), pendingPayment.getInternalTxId());
        PaymentPolicy actualPaymentPolicy = updatedPaymentDetails.getPaymentPolicy();
        String actualReceipt = updatedPaymentDetails.getAppStroreReceipt();
        SubmittedPayment submittedPayment = createSuccessfulSubmittedPayment(user, result, actualPaymentPolicy, actualReceipt);
        submittedPaymentService.save(submittedPayment);

        PaymentEvent paymentEvent = new PaymentEvent(submittedPayment);
        applicationEventPublisher.publishEvent(paymentEvent);

        pendingPaymentRepository.delete(pendingPayment);

        /*
		SubmittedPayment submittedPayment = SubmittedPayment.valueOf(pendingPayment);
        PaymentDetails paymentDetails = pendingPayment.getPaymentDetails();
        paymentDetails.setDescriptionError(null);
        paymentDetails.setErrorCode(null);
        paymentDetails.incrementMadeAttemptsAccordingToMadeRetries();
        paymentDetails.setLastPaymentStatus(SUCCESSFUL);
        entityService.updateEntity(paymentDetails);
		applicationEventPublisher.publishEvent(new PaymentEvent(submittedPayment));
        entityService.removeEntity(PendingPayment.class, pendingPayment.getI());
       */
    }

    @Transactional
    public void failAttempt(PendingPayment pendingPayment, String errorDescription) {
        final User user = pendingPayment.getUser();
        logger.info("Fail current payment attempt {} with error {}", pendingPayment, errorDescription);
        ITunesPaymentDetails currentPaymentDetails = user.getCurrentPaymentDetails();

        currentPaymentDetails.completedWithError(errorDescription);
        paymentDetailsRepository.save(currentPaymentDetails);

        logger.info("Create error submitted payment for user {} transaction {}", user.getId(), pendingPayment.getInternalTxId());
        PaymentPolicy storedPaymentPolicy = currentPaymentDetails.getPaymentPolicy();
        SubmittedPayment submittedPayment = createFailedSubmittedPayment(user, storedPaymentPolicy, currentPaymentDetails.getAppStroreReceipt());
        submittedPaymentService.save(submittedPayment);

        if (currentPaymentDetails.shouldBeUnSubscribed()) {
            userService.unsubscribeUser(user, errorDescription);
        }

        pendingPaymentRepository.delete(pendingPayment);

         /*
            PaymentDetails paymentDetails = pendingPayment.getPaymentDetails();
            paymentDetails.setDescriptionError(response.getDescriptionError());
            paymentDetails.setErrorCode(response.getErrorCode());
            paymentDetails.incrementMadeAttemptsAccordingToMadeRetries();
            paymentDetails.setLastPaymentStatus(ERROR);
            entityService.updateEntity(paymentDetails);

            SubmittedPayment submittedPayment = SubmittedPayment.valueOf(pendingPayment);
            submittedPayment.setDescriptionError(response.getDescriptionError());
            submittedPayment.setStatus(ERROR);
            submittedPayment = entityService.updateEntity(submittedPayment);
            paymentEventNotifier.onError(paymentDetails);

            if (paymentDetails.shouldBeUnSubscribed())
                userService.unsubscribeUser(paymentDetails.getOwner(), response.getDescriptionError());
                paymentEventNotifier.onUnsubscribe(paymentDetails.getOwner());
            entityService.removeEntity(PendingPayment.class, pendingPayment.getI());
        */
    }


    @Transactional
    public void stopSubscription(PendingPayment pendingPayment, String descriptionError) {
        final User user = pendingPayment.getUser();
        logger.info("Stop subscription for user {} message {}", user.getId(), descriptionError);

        PaymentDetails currentPaymentDetails = user.getCurrentPaymentDetails();
        currentPaymentDetails.completedWithError(descriptionError);
        paymentDetailsRepository.save(currentPaymentDetails);

        userService.unsubscribeUser(user, descriptionError);

        pendingPaymentRepository.delete(pendingPayment);

         /*
        PaymentDetails paymentDetails = pendingPayment.getPaymentDetails();
        paymentDetails.setDescriptionError(response.getDescriptionError());
        paymentDetails.setErrorCode(response.getErrorCode());
        paymentDetails.incrementMadeAttemptsAccordingToMadeRetries();
        paymentDetails.setLastPaymentStatus(ERROR);
        entityService.updateEntity(paymentDetails);

		SubmittedPayment submittedPayment = SubmittedPayment.valueOf(pendingPayment);
        submittedPayment.setDescriptionError(response.getDescriptionError());
		submittedPayment.setStatus(ERROR);
        sub mittedPayment = entityService.updateEntity(submittedPayment);

        paymentEventNotifier.onError(paymentDetails);
        if (paymentDetails.shouldBeUnSubscribed()) {
            userService.unsubscribeUser(paymentDetails.getOwner(), response.getDescriptionError());
            paymentEventNotifier.onUnsubscribe(paymentDetails.getOwner());
        }
        entityService.removeEntity(PendingPayment.class, pendingPayment.getI());
*/
    }

    private void checkPaymentPolicy(User user, ITunesResult result) {
        ITunesPaymentDetails currentPaymentDetails = user.getCurrentPaymentDetails();
        String actualProductId = result.getProductId();

        PaymentPolicy storedPaymentPolicy = currentPaymentDetails.getPaymentPolicy();
        boolean userResubscribedOnNewPaymentPolicy = !actualProductId.equals(storedPaymentPolicy.getAppStoreProductId());
        if (userResubscribedOnNewPaymentPolicy) {
            logger.info("Stored payment policy product id {} doesn't match actual product id {}", storedPaymentPolicy.getAppStoreProductId(), actualProductId);
            iTunesPaymentDetailsService.createNewPaymentDetails(user, actualProductId, currentPaymentDetails.getAppStroreReceipt());
        }
    }

    private SubmittedPayment createSuccessfulSubmittedPayment(User user, ITunesResult result, PaymentPolicy paymentPolicy, String actualReceipt) {
        SubmittedPayment submittedPayment = new SubmittedPayment();

        fillCommonProperties(submittedPayment, user, actualReceipt, paymentPolicy);

        String originalTransactionId = result.getOriginalTransactionId();
        long expireTime = getExpireTimestamp(user.getCommunity(), result);
        submittedPayment.setNextSubPayment(DateTimeUtils.millisToIntSeconds(expireTime));
        submittedPayment.setExternalTxId(originalTransactionId);
        submittedPayment.setAppStoreOriginalTransactionId(originalTransactionId);
        submittedPayment.setStatus(PaymentDetailsStatus.SUCCESSFUL);

        return submittedPayment;
    }

    private SubmittedPayment createFailedSubmittedPayment(User user, PaymentPolicy paymentPolicy, String actualReceipt) {
        SubmittedPayment submittedPayment = new SubmittedPayment();

        fillCommonProperties(submittedPayment, user, actualReceipt, paymentPolicy);

        submittedPayment.setExternalTxId(user.getAppStoreOriginalTransactionId());
        submittedPayment.setAppStoreOriginalTransactionId(user.getAppStoreOriginalTransactionId());
        submittedPayment.setStatus(PaymentDetailsStatus.ERROR);

        return submittedPayment;
    }

    private void fillCommonProperties(SubmittedPayment submittedPayment, User user, String actualReceipt, PaymentPolicy paymentPolicy) {
        submittedPayment.setUser(user);
        submittedPayment.setTimestamp(timeService.now().getTime());
        submittedPayment.setAmount(paymentPolicy.getSubcost());
        submittedPayment.setCurrencyISO(paymentPolicy.getCurrencyISO());
        submittedPayment.setPaymentSystem(PaymentDetails.ITUNES_SUBSCRIPTION);
        submittedPayment.setBase64EncodedAppStoreReceipt(actualReceipt);
        submittedPayment.setPeriod(paymentPolicy.getPeriod());
        submittedPayment.setPaymentPolicy(paymentPolicy);

        boolean isFirstPayment = user.getLastSuccessfulPaymentTimeMillis() == 0;
        submittedPayment.setType(isFirstPayment ? FIRST : REGULAR);
    }

    private long getExpireTimestamp(Community community, ITunesResult result) {
        Long expireTime = result.getExpireTime();
        boolean inCaseOfOneTimePaymentPolicy = expireTime == null;
        if (inCaseOfOneTimePaymentPolicy) {
            logger.debug("Calculate expire timestamp for result {} and community id {}", result, community.getId());
            PaymentPolicy policy = getPaymentPolicy(community, result.getProductId());
            Period period = policy.getPeriod();
            int purchaseSeconds = DateTimeUtils.millisToIntSeconds(result.getPurchaseTime());
            int nextSubPaymentSeconds = period.toNextSubPaymentSeconds(purchaseSeconds);
            expireTime = DateTimeUtils.secondsToMillis(nextSubPaymentSeconds);
        }
        Assert.isTrue(expireTime > DateTimeUtils.getEpochMillis(), "result [" + result + "] must have expireDate in future");
        return expireTime;
    }

    private PaymentPolicy getPaymentPolicy(Community community, String productId) {
        return paymentPolicyService.findByCommunityAndAppStoreProductId(community, productId);
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    public void setiTunesPaymentDetailsService(ITunesPaymentDetailsService iTunesPaymentDetailsService) {
        this.iTunesPaymentDetailsService = iTunesPaymentDetailsService;
    }

    public void setPaymentPolicyService(PaymentPolicyService paymentPolicyService) {
        this.paymentPolicyService = paymentPolicyService;
    }

    public void setPaymentDetailsRepository(PaymentDetailsRepository paymentDetailsRepository) {
        this.paymentDetailsRepository = paymentDetailsRepository;
    }

    public void setTimeService(TimeService timeService) {
        this.timeService = timeService;
    }

    public void setSubmittedPaymentService(SubmittedPaymentService submittedPaymentService) {
        this.submittedPaymentService = submittedPaymentService;
    }

    public void setPendingPaymentRepository(PendingPaymentRepository pendingPaymentRepository) {
        this.pendingPaymentRepository = pendingPaymentRepository;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }
}
