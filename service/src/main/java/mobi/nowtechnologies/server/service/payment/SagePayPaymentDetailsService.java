/*
 * Copyright 2015 Musicqubed.com. All Rights Reserved.
 */

package mobi.nowtechnologies.server.service.payment;

import mobi.nowtechnologies.common.dto.PaymentDetailsDto;
import mobi.nowtechnologies.server.persistence.domain.Promotion;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentPolicy;
import mobi.nowtechnologies.server.persistence.domain.payment.PromotionPaymentPolicy;
import mobi.nowtechnologies.server.persistence.domain.payment.SagePayCreditCardPaymentDetails;
import mobi.nowtechnologies.server.persistence.repository.PaymentDetailsRepository;
import mobi.nowtechnologies.server.persistence.repository.PaymentPolicyRepository;
import mobi.nowtechnologies.server.persistence.repository.PromotionPaymentPolicyRepository;
import mobi.nowtechnologies.server.persistence.repository.UserRepository;
import mobi.nowtechnologies.server.service.PaymentDetailsService;
import mobi.nowtechnologies.server.service.PromotionService;
import mobi.nowtechnologies.server.service.UserNotificationService;
import mobi.nowtechnologies.server.service.exception.ServiceException;
import mobi.nowtechnologies.server.service.payment.http.SagePayHttpService;
import mobi.nowtechnologies.server.service.payment.response.SagePayResponse;
import mobi.nowtechnologies.server.shared.Utils;
import mobi.nowtechnologies.server.shared.dto.web.payment.CreditCardDto;
import static mobi.nowtechnologies.common.dto.UserRegInfo.PaymentType.CREDIT_CARD;
import static mobi.nowtechnologies.server.shared.enums.PaymentDetailsStatus.NONE;

import javax.annotation.Resource;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.transaction.annotation.Transactional;

public class SagePayPaymentDetailsService {
    Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    UserNotificationService userNotificationService;
    @Resource
    PromotionService promotionService;
    @Resource
    UserRepository userRepository;
    @Resource
    PaymentDetailsRepository paymentDetailsRepository;
    @Resource
    PromotionPaymentPolicyRepository promotionPaymentPolicyRepository;
    @Resource
    PaymentPolicyRepository paymentPolicyRepository;
    @Resource
    SagePayPaymentService sagePayPaymentService;
    @Resource
    SagePayHttpService sagePayHttpService;
    @Resource
    PaymentDetailsService paymentDetailsService;

    private int retriesOnError;

    public void setRetriesOnError(int retriesOnError) {
        this.retriesOnError = retriesOnError;
    }

    @Transactional
    public SagePayCreditCardPaymentDetails createPaymentDetails(CreditCardDto dto, int userId) throws ServiceException {
        User user = userRepository.findOne(userId);
        if(user.isLimited()) {
            promotionService.applyPromoToLimitedUser(user);
        }
        PaymentDetailsDto pdto = CreditCardDto.toPaymentDetails(dto);

        return createPaymentDetails(pdto, user);
    }

    SagePayCreditCardPaymentDetails createPaymentDetails(PaymentDetailsDto dto, User user) throws ServiceException {

        PaymentPolicy paymentPolicy = paymentPolicyRepository.findOne(dto.getPaymentPolicyId());
        Promotion promotion = user.getPotentialPromotion();
        PromotionPaymentPolicy promotionPaymentPolicy = null;
        if (null != promotion) {
            promotionPaymentPolicy = promotionPaymentPolicyRepository.findPromotionPaymentPolicy(promotion, paymentPolicy);
        }

        SagePayCreditCardPaymentDetails paymentDetails = null;
        if (null != paymentPolicy) {
            if (dto.getPaymentType().equals(CREDIT_CARD)) {
                dto.setCurrency(paymentPolicy.getCurrencyISO());
                dto.setAmount(paymentPolicy.getSubcost().toString());
                dto.setVendorTxCode(UUID.randomUUID().toString());
                dto.setDescription("Creating payment details for user " + user.getUserName());
                paymentDetails = createPaymentDetails(dto, user, paymentPolicy);
            }

            if (null != paymentDetails) {
                if (null != promotion) {
                    paymentDetails.setPromotionPaymentPolicy(promotionPaymentPolicy);
                    promotionService.incrementUserNumber(promotion);
                }

                paymentDetails = paymentDetailsRepository.save(paymentDetails);
            }
        }

        return paymentDetails;
    }

    private SagePayCreditCardPaymentDetails createPaymentDetails(PaymentDetailsDto paymentDto, User user, PaymentPolicy paymentPolicy) throws ServiceException {
        logger.info("Creating sagepay payment details...");

        SagePayResponse response = sagePayHttpService.makeDeferRequest(paymentDto);
        if (!response.isSagePaySuccessful()) {
            logger.error("Error while trying to get sagepay payment details. (httpStatus: {}, description: {})", response.getHttpStatus(), response.getDescriptionError());
            throw new ServiceException("External error while trying to get sagepay payment details");
        }

        return commitPaymentDetails(response, paymentDto, user, paymentPolicy);
    }

    private SagePayCreditCardPaymentDetails commitPaymentDetails(SagePayResponse response, PaymentDetailsDto paymentDto, User user, PaymentPolicy paymentPolicy)
        throws ServiceException {
        SagePayCreditCardPaymentDetails newPaymentDetails = createPaymentDetailsFromResponse(response);

        newPaymentDetails.setVendorTxCode(paymentDto.getVendorTxCode());
        newPaymentDetails.setCreationTimestampMillis(Utils.getEpochMillis());
        newPaymentDetails.setPaymentPolicy(paymentPolicy);

        newPaymentDetails = (SagePayCreditCardPaymentDetails) commitPaymentDetails1(user, newPaymentDetails);

        logger.info("Credit card payment details was created");
        return newPaymentDetails;
    }

    protected PaymentDetails commitPaymentDetails1(User user, PaymentDetails newPaymentDetails) {

        paymentDetailsService.deactivateCurrentPaymentDetailsIfOneExist(user, "Commit new payment details");

        user.setCurrentPaymentDetails(newPaymentDetails);
        newPaymentDetails.setOwner(user);
        newPaymentDetails.setActivated(true);
        newPaymentDetails.setLastPaymentStatus(NONE);
        newPaymentDetails.setRetriesOnError(retriesOnError);
        newPaymentDetails.resetMadeAttempts();

        return paymentDetailsRepository.save(newPaymentDetails);
    }

    private SagePayCreditCardPaymentDetails createPaymentDetailsFromResponse(SagePayResponse response) {
        SagePayCreditCardPaymentDetails paymentDetails = new SagePayCreditCardPaymentDetails();
        paymentDetails.setReleased(false);
        paymentDetails.setActivated(true);
        paymentDetails.setSecurityKey(response.getSecurityKey());
        paymentDetails.setTxAuthNo(response.getTxAuthNo());
        paymentDetails.setVPSTxId(response.getVPSTxId());
        return paymentDetails;
    }

    private void sendSubscriptionChangedSMS(User user) {
        try {
            userNotificationService.sendSubscriptionChangedSMS(user);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }
}
