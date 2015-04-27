/*
 * Copyright 2015 Musicqubed.com. All Rights Reserved.
 */

package mobi.nowtechnologies.server.service.payment;

import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.payment.O2PSMSPaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentPolicy;
import mobi.nowtechnologies.server.persistence.repository.PaymentDetailsRepository;
import mobi.nowtechnologies.server.persistence.repository.PaymentPolicyRepository;
import mobi.nowtechnologies.server.service.UserNotificationService;
import mobi.nowtechnologies.server.service.exception.ServiceException;
import mobi.nowtechnologies.server.shared.enums.Contract;
import static mobi.nowtechnologies.server.shared.ObjectUtils.isNull;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class O2PSMSPaymentDetailsService {
    Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    UserNotificationService userNotificationService;
    @Resource
    O2PSMSPaymentDetailsInfoService o2PSMSPaymentDetailsInfoService;
    @Resource
    PaymentDetailsRepository paymentDetailsRepository;
    @Resource
    PaymentPolicyRepository paymentPolicyRepository;

    //
    // API
    //
    public O2PSMSPaymentDetails createPaymentDetails(User user, PaymentPolicy paymentPolicy) throws ServiceException {
        logger.info("Start creation psms payment details for user [{}] and paymentPolicyId [{}]", new Object[] {user.getId(), paymentPolicy.getId()});

        O2PSMSPaymentDetails paymentDetails = o2PSMSPaymentDetailsInfoService.createPaymentDetailsInfo(user, paymentPolicy);

        logger.info("Done commitment of psms payment details [{}] for user [{}]", new Object[] {paymentDetails, user.getId()});

        sendSubscriptionChangedSMS(user);

        return paymentDetails;
    }

    public O2PSMSPaymentDetails createPaymentDetails(User user) throws ServiceException {
        PaymentPolicy paymentPolicy = findDefaultO2PsmsPaymentPolicy(user);
        return createPaymentDetails(user, paymentPolicy);
    }

    //
    // Internals
    //
    private PaymentPolicy findDefaultO2PsmsPaymentPolicy(User user) {
        Contract contract = user.getContract();
        if (isNull(contract)) {
            contract = Contract.PAYM;
        }
        Community community = user.getUserGroup().getCommunity();

        return paymentPolicyRepository.findDefaultO2PsmsPaymentPolicy(community, user.getProvider(), user.getSegment(), contract, user.getTariff());
    }

    private void sendSubscriptionChangedSMS(User user) {
        try {
            userNotificationService.sendSubscriptionChangedSMS(user);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }
}
