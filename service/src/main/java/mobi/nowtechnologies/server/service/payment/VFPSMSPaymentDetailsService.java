/*
 * Copyright 2015 Musicqubed.com. All Rights Reserved.
 */

package mobi.nowtechnologies.server.service.payment;

import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentPolicy;
import mobi.nowtechnologies.server.persistence.domain.payment.VFPSMSPaymentDetails;
import mobi.nowtechnologies.server.service.UserNotificationService;
import mobi.nowtechnologies.server.service.exception.ServiceException;

import javax.annotation.Resource;

import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VFPSMSPaymentDetailsService {
    Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    UserNotificationService userNotificationService;
    @Resource
    VFPSMSPaymentDetailsInfoService vfpsmsPaymentDetailsInfoService;

    public VFPSMSPaymentDetails createPaymentDetails(User user, PaymentPolicy paymentPolicy) throws ServiceException {
        Preconditions.checkArgument(paymentPolicy != null);

        logger.info("Start creation psms payment details for user [{}] and paymentPolicyId [{}]", new Object[] {user.getId(), paymentPolicy.getId()});

        VFPSMSPaymentDetails paymentDetails = vfpsmsPaymentDetailsInfoService.createPaymentDetailsInfo(user, paymentPolicy);

        logger.info("Done commitment of psms payment details [{}] for user [{}]", new Object[] {paymentDetails, user.getId()});

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
