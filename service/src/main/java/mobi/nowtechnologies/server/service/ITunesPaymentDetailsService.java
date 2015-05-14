package mobi.nowtechnologies.server.service;

import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.payment.ITunesPaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentPolicy;
import mobi.nowtechnologies.server.persistence.repository.PaymentDetailsRepository;

import com.google.common.base.Preconditions;
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
    private PaymentPolicyService paymentPolicyService;
    private UserService userService;
    private int retriesOnError;

    @Transactional
    public void createNewOrUpdatePaymentDetails(User user, String appStoreReceipt, String productId) {
        Preconditions.checkArgument(StringUtils.isNotEmpty(appStoreReceipt));
        Preconditions.checkArgument(StringUtils.isNotEmpty(productId));

        logger.info("Assign app store receipt [{}] to user [{}]", appStoreReceipt, user.getId());

        if(!user.hasActiveITunesPaymentDetails() || !productId.equals(user.getCurrentPaymentDetails().getPaymentPolicy().getAppStoreProductId())) {
            logger.info("Another product id [{}] or user does not have payment details", productId);
            createNewPaymentDetails(user, appStoreReceipt, productId);
        } else {
            ITunesPaymentDetails currentDetails = user.getCurrentPaymentDetails();
            if(!appStoreReceipt.equals(currentDetails.getAppStroreReceipt())) {
                logger.info("Update payment details [{}] with new receipt", currentDetails.getI());
                ITunesPaymentDetails updated = (ITunesPaymentDetails) paymentDetailsRepository.findOne(currentDetails.getI());
                updated.updateAppStroreReceipt(appStoreReceipt);
                paymentDetailsRepository.save(updated);
            }
        }
    }

    @Transactional
    public void createNewPaymentDetails(User user, String appStoreReceipt, String productId) {
        Preconditions.checkArgument(StringUtils.isNotEmpty(appStoreReceipt));
        Preconditions.checkArgument(StringUtils.isNotEmpty(productId));

        logger.info("Create new payment details for user {} and productId {}", user.getId(), productId);

        paymentDetailsService.deactivateCurrentPaymentDetailsIfOneExist(user, "Commit new payment details");

        if (user.isOnFreeTrial()) {
            userService.skipFreeTrial(user);
        }

        PaymentPolicy paymentPolicy = paymentPolicyService.findByCommunityAndAppStoreProductId(user.getCommunity(), productId);
        Preconditions.checkNotNull(paymentPolicy);
        logger.info("Payment policy is [{}] for [{}]", paymentPolicy.getId(), productId);

        ITunesPaymentDetails iTunesPaymentDetails = new ITunesPaymentDetails(user, paymentPolicy, appStoreReceipt, retriesOnError);
        paymentDetailsRepository.save(iTunesPaymentDetails);

        user.setCurrentPaymentDetails(iTunesPaymentDetails);
        userService.updateUser(user);
        logger.info("ITunes payment details {} were created for user {}", iTunesPaymentDetails.getI(), user.getId());
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

    public void setPaymentPolicyService(PaymentPolicyService paymentPolicyService) {
        this.paymentPolicyService = paymentPolicyService;
    }

    public void setRetriesOnError(int retriesOnError) {
        this.retriesOnError = retriesOnError;
    }
}
