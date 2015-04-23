/*
 * Copyright 2015 Musicqubed.com. All Rights Reserved.
 */

package mobi.nowtechnologies.server.service.payment;

import mobi.nowtechnologies.server.persistence.domain.Promotion;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.payment.PayPalPaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentPolicy;
import mobi.nowtechnologies.server.persistence.repository.PaymentDetailsRepository;
import mobi.nowtechnologies.server.persistence.repository.UserRepository;
import mobi.nowtechnologies.server.service.PaymentDetailsService;
import mobi.nowtechnologies.server.service.PromotionService;
import mobi.nowtechnologies.server.service.payment.response.PayPalResponse;
import static mobi.nowtechnologies.server.persistence.domain.PromoCode.PROMO_CODE_FOR_FREE_TRIAL_BEFORE_SUBSCRIBE;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.transaction.annotation.Transactional;

public class PayPalPaymentDetailsInfoService {
    Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    PaymentDetailsService paymentDetailsService;
    @Resource
    UserRepository userRepository;
    @Resource
    PaymentDetailsRepository paymentDetailsRepository;
    @Resource
    PromotionService promotionService;

    private int retriesOnError;
    public void setRetriesOnError(int retriesOnError) {
        this.retriesOnError = retriesOnError;
    }

    @Transactional
    public PayPalPaymentDetails commitPaymentDetails(User user, PaymentPolicy paymentPolicy, PayPalResponse response) {
        applyPromoToLimitedUsers(user);

        paymentDetailsService.deactivateCurrentPaymentDetailsIfOneExist(user, "Commit new payment details");

        PayPalPaymentDetails newPaymentDetails = new PayPalPaymentDetails(user, paymentPolicy, response.getBillingAgreement(), response.getToken(), response.getPayerId(), retriesOnError);
        newPaymentDetails = paymentDetailsRepository.save(newPaymentDetails);
        user.setCurrentPaymentDetails(newPaymentDetails);
        userRepository.save(user);

        return newPaymentDetails;
    }

    private void applyPromoToLimitedUsers(User user) {
        if (user.isLimited()) {

            Promotion twoWeeksTrial = promotionService.getActivePromotion(user.getUserGroup(), PROMO_CODE_FOR_FREE_TRIAL_BEFORE_SUBSCRIBE);
            long now = System.currentTimeMillis();
            int dbSecs = (int) (now / 1000); // in db we keep time in seconds not milliseconds
            if (twoWeeksTrial != null && twoWeeksTrial.getStartDate() < dbSecs && dbSecs < twoWeeksTrial.getEndDate()) {
                promotionService.applyPromotionByPromoCode(user, twoWeeksTrial);
            }
        }
    }
}
