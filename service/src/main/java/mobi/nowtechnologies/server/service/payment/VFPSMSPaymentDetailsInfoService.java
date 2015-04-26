/*
 * Copyright 2015 Musicqubed.com. All Rights Reserved.
 */

package mobi.nowtechnologies.server.service.payment;

import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentPolicy;
import mobi.nowtechnologies.server.persistence.domain.payment.VFPSMSPaymentDetails;
import mobi.nowtechnologies.server.persistence.repository.PaymentDetailsRepository;
import mobi.nowtechnologies.server.persistence.repository.UserRepository;
import mobi.nowtechnologies.server.service.PaymentDetailsService;
import mobi.nowtechnologies.server.service.exception.ServiceException;

import javax.annotation.Resource;

import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.transaction.annotation.Transactional;

public class VFPSMSPaymentDetailsInfoService {
    Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    UserRepository userRepository;
    @Resource
    PaymentDetailsRepository paymentDetailsRepository;
    @Resource
    PaymentDetailsService paymentDetailsService;

    private int retriesOnError;

    public void setRetriesOnError(int retriesOnError) {
        this.retriesOnError = retriesOnError;
    }

    @Transactional
    public VFPSMSPaymentDetails createPaymentDetailsInfo(User user, PaymentPolicy paymentPolicy) throws ServiceException {
        Preconditions.checkArgument(paymentPolicy != null);

        paymentDetailsService.deactivateCurrentPaymentDetailsIfOneExist(user, "Commit new payment details");

        VFPSMSPaymentDetails paymentDetails = new VFPSMSPaymentDetails(paymentPolicy, user, retriesOnError);
        user.setCurrentPaymentDetails(paymentDetails);

        paymentDetails = paymentDetailsRepository.save(paymentDetails);
        userRepository.save(user);

        return paymentDetails;
    }
}
