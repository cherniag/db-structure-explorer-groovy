package mobi.nowtechnologies.server.service;

import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.payment.ITunesPaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentPolicy;
import mobi.nowtechnologies.server.persistence.repository.PaymentDetailsRepository;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.transaction.annotation.Transactional;

/**
 * Author: Gennadii Cherniaiev Date: 4/14/2015
 */
public class ITunesPaymentDetailsService {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private PaymentDetailsRepository paymentDetailsRepository;
    private PaymentDetailsService paymentDetailsService;
    private UserService userService;
    private int retriesOnError;

    @Transactional
    public void assignAppStoreReceipt(User user, String appStoreReceipt) {
        logger.info("Assign app store receipt [{}] to user {}", appStoreReceipt, user.getId());
        if(user.hasActiveITunesPaymentDetails()) {
            ITunesPaymentDetails currentDetails = user.getCurrentPaymentDetails();
            if(!appStoreReceipt.equals(currentDetails.getAppStroreReceipt())) {
                logger.info("Update payment details {} with new receipt", currentDetails.getI());
                currentDetails.updateAppStroreReceipt(appStoreReceipt);
                paymentDetailsRepository.save(currentDetails);
            }
        } else {
            createPaymentDetails(user, appStoreReceipt);
        }
    }

    @Transactional
    public void createPaymentDetails(User user, String token) {
        logger.info("Create new payment details for user {}", user.getId());
        paymentDetailsService.deactivateCurrentPaymentDetailsIfOneExist(user, "Commit new payment details");
        if(user.isOnFreeTrial()) {
            userService.skipFreeTrial(user);
        }
        //PaymentPolicy is null till first payment
        ITunesPaymentDetails iTunesPaymentDetails = new ITunesPaymentDetails(user, token, retriesOnError);

        assignPaymentDetailsToUser(user, iTunesPaymentDetails);
    }

    @Transactional
    public void createPaymentDetails(User user, PaymentPolicy paymentPolicy, String token) {
        logger.info("Create new payment details for user {} and payment policy {}", user.getId(), paymentPolicy.getId());
        paymentDetailsService.deactivateCurrentPaymentDetailsIfOneExist(user, "Commit new payment details");

        ITunesPaymentDetails iTunesPaymentDetails = new ITunesPaymentDetails(user, token, retriesOnError);
        iTunesPaymentDetails.setPaymentPolicy(paymentPolicy);

        assignPaymentDetailsToUser(user, iTunesPaymentDetails);
    }

    private void assignPaymentDetailsToUser(User user, ITunesPaymentDetails iTunesPaymentDetails) {
        logger.info("Assign ITunesPaymentDetails to user {}", user.getId());
        paymentDetailsRepository.save(iTunesPaymentDetails);
        user.setCurrentPaymentDetails(iTunesPaymentDetails);
        userService.updateUser(user);
    }

    public void setPaymentDetailsRepository(PaymentDetailsRepository paymentDetailsRepository) {
        this.paymentDetailsRepository = paymentDetailsRepository;
    }

    public void setPaymentDetailsService(PaymentDetailsService paymentDetailsService) {
        this.paymentDetailsService = paymentDetailsService;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public void setRetriesOnError(int retriesOnError) {
        this.retriesOnError = retriesOnError;
    }
}
