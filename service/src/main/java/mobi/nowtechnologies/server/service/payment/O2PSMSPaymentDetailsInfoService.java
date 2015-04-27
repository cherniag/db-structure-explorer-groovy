/*
 * Copyright 2015 Musicqubed.com. All Rights Reserved.
 */

package mobi.nowtechnologies.server.service.payment;

import mobi.nowtechnologies.server.persistence.domain.Promotion;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.payment.O2PSMSPaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentPolicy;
import mobi.nowtechnologies.server.persistence.domain.payment.PromotionPaymentPolicy;
import mobi.nowtechnologies.server.persistence.repository.PaymentDetailsRepository;
import mobi.nowtechnologies.server.persistence.repository.PromotionPaymentPolicyRepository;
import mobi.nowtechnologies.server.persistence.repository.UserRepository;
import mobi.nowtechnologies.server.service.PaymentDetailsService;
import mobi.nowtechnologies.server.service.PromotionService;
import mobi.nowtechnologies.server.service.exception.ServiceException;

import javax.annotation.Resource;

import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.transaction.annotation.Transactional;

public class O2PSMSPaymentDetailsInfoService {
    Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    UserRepository userRepository;
    @Resource
    PaymentDetailsRepository paymentDetailsRepository;
    @Resource
    PromotionPaymentPolicyRepository promotionPaymentPolicyRepository;
    @Resource
    PaymentDetailsService paymentDetailsService;
    @Resource
    PromotionService promotionService;

    private int retriesOnError;

    public void setRetriesOnError(int retriesOnError) {
        this.retriesOnError = retriesOnError;
    }

    @Transactional
    public O2PSMSPaymentDetails createPaymentDetailsInfo(User user, PaymentPolicy paymentPolicy) throws ServiceException {
        Preconditions.checkArgument(paymentPolicy != null);

        paymentDetailsService.deactivateCurrentPaymentDetailsIfOneExist(user, "Commit new payment details");

        O2PSMSPaymentDetails paymentDetails = new O2PSMSPaymentDetails(paymentPolicy, user, retriesOnError);
        user.setCurrentPaymentDetails(paymentDetails);

        Promotion promotion = user.getPotentialPromotion();
        if (null != promotion) {
            PromotionPaymentPolicy promotionPaymentPolicy = promotionPaymentPolicyRepository.findPromotionPaymentPolicy(promotion, paymentPolicy);
            paymentDetails.setPromotionPaymentPolicy(promotionPaymentPolicy);
            promotionService.incrementUserNumber(promotion);
        }

        paymentDetails = paymentDetailsRepository.save(paymentDetails);
        userRepository.save(user);

        return paymentDetails;
    }
}
