/*
 * Copyright 2015 Musicqubed.com. All Rights Reserved.
 */

package mobi.nowtechnologies.server.service.payment;

import mobi.nowtechnologies.server.persistence.domain.Promotion;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentPolicy;
import mobi.nowtechnologies.server.persistence.domain.payment.PromotionPaymentPolicy;
import mobi.nowtechnologies.server.persistence.domain.payment.SagePayCreditCardPaymentDetails;
import mobi.nowtechnologies.server.persistence.repository.PaymentDetailsRepository;
import mobi.nowtechnologies.server.persistence.repository.PromotionPaymentPolicyRepository;
import mobi.nowtechnologies.server.persistence.repository.UserRepository;
import mobi.nowtechnologies.server.service.PaymentDetailsService;
import mobi.nowtechnologies.server.service.PromotionService;
import mobi.nowtechnologies.server.service.exception.ServiceException;
import mobi.nowtechnologies.server.service.payment.response.SagePayResponse;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.transaction.annotation.Transactional;

public class SagePayPaymentDetailsInfoService {
    Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    PromotionService promotionService;
    @Resource
    UserRepository userRepository;
    @Resource
    PaymentDetailsRepository paymentDetailsRepository;
    @Resource
    PromotionPaymentPolicyRepository promotionPaymentPolicyRepository;
    @Resource
    PaymentDetailsService paymentDetailsService;

    private int retriesOnError;

    public void setRetriesOnError(int retriesOnError) {
        this.retriesOnError = retriesOnError;
    }

    @Transactional
    public SagePayCreditCardPaymentDetails createPaymentDetailsInfo(User user, PaymentPolicy paymentPolicy, SagePayResponse response, String vendorTxCode) throws ServiceException {
        paymentDetailsService.deactivateCurrentPaymentDetailsIfOneExist(user, "Commit new payment details");

        SagePayCreditCardPaymentDetails paymentDetails = new SagePayCreditCardPaymentDetails(response, user, paymentPolicy, retriesOnError, vendorTxCode);
        user.setCurrentPaymentDetails(paymentDetails);

        if(user.isLimited()) {
            logger.info("Applying promo for user: {}", user.getId());
            promotionService.applyPromoToLimitedUser(user);
        }

        Promotion promotion = user.getPotentialPromotion();
        if (null != promotion) {
            logger.info("Applying potential promo: {} for user: {}", promotion.getI(), user.getId());
            PromotionPaymentPolicy promotionPaymentPolicy = promotionPaymentPolicyRepository.findPromotionPaymentPolicy(promotion, paymentPolicy);
            paymentDetails.setPromotionPaymentPolicy(promotionPaymentPolicy);
            promotionService.incrementUserNumber(promotion);
        }

        paymentDetails = paymentDetailsRepository.save(paymentDetails);
        userRepository.save(user);

        return paymentDetails;
    }
}
