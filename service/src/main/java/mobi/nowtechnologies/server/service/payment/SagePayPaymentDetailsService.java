/*
 * Copyright 2015 Musicqubed.com. All Rights Reserved.
 */

package mobi.nowtechnologies.server.service.payment;

import mobi.nowtechnologies.common.dto.PaymentDetailsDto;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentPolicy;
import mobi.nowtechnologies.server.persistence.domain.payment.SagePayCreditCardPaymentDetails;
import mobi.nowtechnologies.server.service.UserNotificationService;
import mobi.nowtechnologies.server.service.exception.ServiceException;
import mobi.nowtechnologies.server.service.payment.http.SagePayHttpService;
import mobi.nowtechnologies.server.service.payment.response.SagePayResponse;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SagePayPaymentDetailsService {
    Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    UserNotificationService userNotificationService;
    @Resource
    SagePayHttpService sagePayHttpService;
    @Resource
    SagePayPaymentDetailsInfoService sagePayPaymentDetailsInfoService;

    public SagePayCreditCardPaymentDetails createPaymentDetails(PaymentDetailsDto pdto, User user, PaymentPolicy paymentPolicy) throws ServiceException {
        logger.info("Creating Credit card payment details for user: {}", user.getId());

        SagePayResponse response = sagePayHttpService.makeDeferRequest(pdto);
        if (!response.isSuccessful()) {
            logger.error("Error while trying to get sagepay payment details. (httpStatus: {}, description: {})", response.getHttpStatus(), response.getDescriptionError());
            throw new ServiceException("External error while trying to get sagepay payment details");
        }

        SagePayCreditCardPaymentDetails paymentDetails = sagePayPaymentDetailsInfoService.createPaymentDetailsInfo(user, paymentPolicy, response, pdto.getVendorTxCode());

        logger.info("Credit card payment details was created: {} for user: {}", paymentDetails.getI(), user.getId());

        sendSubscriptionChangedSMS(user);

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
