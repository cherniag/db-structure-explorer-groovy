/*
 * Copyright 2015 Musicqubed.com. All Rights Reserved.
 */

package mobi.nowtechnologies.server.service.itunes.payment.impl;

import mobi.nowtechnologies.common.util.DateTimeUtils;
import mobi.nowtechnologies.server.TimeService;
import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.enums.PaymentPolicyType;
import mobi.nowtechnologies.server.persistence.domain.payment.*;
import mobi.nowtechnologies.server.persistence.repository.ITunesPaymentLockRepository;
import mobi.nowtechnologies.server.service.PaymentPolicyService;
import mobi.nowtechnologies.server.service.event.PaymentEvent;
import mobi.nowtechnologies.server.service.itunes.ITunesResult;
import mobi.nowtechnologies.server.service.itunes.payment.ITunesPaymentService;
import mobi.nowtechnologies.server.service.payment.SubmittedPaymentService;
import mobi.nowtechnologies.server.shared.enums.PaymentDetailsStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import static mobi.nowtechnologies.server.persistence.domain.payment.PaymentDetailsType.FIRST;
import static mobi.nowtechnologies.server.persistence.domain.payment.PaymentDetailsType.REGULAR;

/**
 * Created by zam on 1/16/2015.
 */
public class ITunesPaymentServiceImpl implements ApplicationEventPublisherAware, ITunesPaymentService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private PaymentPolicyService paymentPolicyService;
    private ITunesPaymentLockRepository iTunesPaymentLockRepository;
    private TimeService timeService;
    private SubmittedPaymentService submittedPaymentService;

    private ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Override
    public boolean hasOneTimeSubscription(User user) {
        SubmittedPayment latest = submittedPaymentService.getLatest(user);
        if (latest != null) {
            PaymentPolicy paymentPolicy = latest.getPaymentPolicy();
            if (paymentPolicy != null) {
                return PaymentPolicyType.ONETIME.equals(paymentPolicy.getPaymentPolicyType());
            }
        }
        return false;
    }

    @Transactional
    @Override
    public void createSubmittedPayment(User user, String appStoreReceipt, ITunesResult result, ITunesPaymentService iTunesPaymentService) {
        Community community = user.getCommunity();

        long expireTime = getExpireTimestamp(community, result);
        try {
            iTunesPaymentService.checkForDuplicates(user.getId(), expireTime);
        } catch (DataIntegrityViolationException e) {
            logger.info("Record with the same next sub payment millis [{}] for user [{}] already exists", expireTime, user.getId());
            return;
        }

        PaymentPolicy paymentPolicy = paymentPolicyService.findByCommunityAndAppStoreProductId(community, result.getProductId());

        String originalTransactionId = result.getOriginalTransactionId();

        SubmittedPayment submittedPayment = new SubmittedPayment();
        submittedPayment.setNextSubPayment(DateTimeUtils.millisToIntSeconds(expireTime));
        submittedPayment.setExternalTxId(originalTransactionId);
        submittedPayment.setAppStoreOriginalTransactionId(originalTransactionId);
        submittedPayment.setStatus(PaymentDetailsStatus.SUCCESSFUL);
        submittedPayment.setUser(user);
        submittedPayment.setTimestamp(timeService.now().getTime());
        submittedPayment.setAmount(paymentPolicy.getSubcost());
        submittedPayment.setCurrencyISO(paymentPolicy.getCurrencyISO());
        submittedPayment.setPaymentSystem(PaymentDetails.ITUNES_SUBSCRIPTION);
        submittedPayment.setBase64EncodedAppStoreReceipt(appStoreReceipt);
        submittedPayment.setPeriod(paymentPolicy.getPeriod());
        submittedPayment.setPaymentPolicy(paymentPolicy);

        boolean isFirstPayment = user.getLastSuccessfulPaymentTimeMillis() == 0;
        submittedPayment.setType(isFirstPayment ? FIRST : REGULAR);

        submittedPayment = submittedPaymentService.save(submittedPayment);

        PaymentEvent paymentEvent = new PaymentEvent(submittedPayment);
        applicationEventPublisher.publishEvent(paymentEvent);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public void checkForDuplicates(int userId, long nextSubPaymentTimestamp) {
        int nextSubPayment = DateTimeUtils.millisToIntSeconds(nextSubPaymentTimestamp);
        ITunesPaymentLock iTunesPaymentLock = new ITunesPaymentLock(userId, nextSubPayment);
        iTunesPaymentLockRepository.saveAndFlush(iTunesPaymentLock);

    }

    private long getExpireTimestamp(Community community, ITunesResult result) {
        Long time = result.getExpireTime();
        if (time == null) {
            logger.debug("Calculate expire timestamp for result {} and community id {}", result, community.getId());
            PaymentPolicy policy = paymentPolicyService.findByCommunityAndAppStoreProductId(community, result.getProductId());
            Period period = policy.getPeriod();
            int purchaseSeconds = DateTimeUtils.millisToIntSeconds(result.getPurchaseTime());
            int nextSubPaymentSeconds = period.toNextSubPaymentSeconds(purchaseSeconds);
            time = DateTimeUtils.secondsToMillis(nextSubPaymentSeconds);
        }
        long expireTime = time;
        Assert.isTrue(expireTime > DateTimeUtils.getEpochMillis(), "result [" + result + "] must have expireDate in future");

        return expireTime;
    }

    public void setPaymentPolicyService(PaymentPolicyService paymentPolicyService) {
        this.paymentPolicyService = paymentPolicyService;
    }

    public void setSubmittedPaymentService(SubmittedPaymentService submittedPaymentService) {
        this.submittedPaymentService = submittedPaymentService;
    }

    public void setiTunesPaymentLockRepository(ITunesPaymentLockRepository iTunesPaymentLockRepository) {
        this.iTunesPaymentLockRepository = iTunesPaymentLockRepository;
    }

    public void setTimeService(TimeService timeService) {
        this.timeService = timeService;
    }
}
