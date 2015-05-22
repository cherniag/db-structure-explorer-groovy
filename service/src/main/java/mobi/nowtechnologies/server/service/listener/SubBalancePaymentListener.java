package mobi.nowtechnologies.server.service.listener;

import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentDetailsType;
import mobi.nowtechnologies.server.persistence.domain.payment.SubmittedPayment;
import mobi.nowtechnologies.server.service.PromotionService;
import mobi.nowtechnologies.server.service.UserNotificationService;
import mobi.nowtechnologies.server.service.UserService;
import mobi.nowtechnologies.server.service.event.PaymentEvent;
import static mobi.nowtechnologies.server.persistence.domain.payment.PaymentDetailsType.FIRST;

import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.context.ApplicationListener;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Titov Mykhaylo (titov)
 * @author dmytro
 */
public class SubBalancePaymentListener implements ApplicationListener<PaymentEvent> {

    private static final Logger LOGGER = LoggerFactory.getLogger(SubBalancePaymentListener.class);

    private UserService userService;
    private PromotionService promotionService;
    private UserNotificationService userNotificationService;

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void onApplicationEvent(PaymentEvent event) {
        SubmittedPayment payment = (SubmittedPayment) event.getPayment();

        LOGGER.info("handle SubBalance payment event: [{}]", payment);

        if (payment.getType() != PaymentDetailsType.PAYMENT) {
            User user = payment.getUser();

            LOGGER.info("fetching users for user id: {}", user.getId());
            final List<User> users = fetchUsers(payment, user);
            LOGGER.info("fetched users count: {}", users.size());

            for (User actualUser : users) {
                LOGGER.info("starting process for id: {}", actualUser.getId());

                userService.processPaymentSubBalanceCommand(actualUser, payment);
                if (FIRST.equals(payment.getType())) {
                    LOGGER.info("Applying promotions to user {} after his first successful payment with status {} ", actualUser.getId(), payment.getStatus());
                    userService.applyPromotion(actualUser);
                    promotionService.applyPromotion(actualUser);
                    LOGGER.info("Finished applying promotions to user {} after his first successful payment with status {} ", actualUser.getId(), payment.getStatus());
                }

                PaymentDetails currentActivePaymentDetails = actualUser.getCurrentPaymentDetails();
                if (currentActivePaymentDetails != null && PaymentDetails.MIG_SMS_TYPE.equals(currentActivePaymentDetails.getPaymentType())) {
                    userNotificationService.notifyUserAboutSuccessfulPayment(actualUser);
                }

                userService.populateAmountOfMoneyToUserNotification(actualUser, payment);

                LOGGER.info("end of processing for id: {}", actualUser.getId());
            }
        }
    }

    private List<User> fetchUsers(SubmittedPayment payment, User user) {
        final int nextSubPayment = payment.getNextSubPayment();
        final String appStoreOriginalTransactionId = payment.getAppStoreOriginalTransactionId();

        if (appStoreOriginalTransactionId != null) {
            return userService.findUsersForItunesInAppSubscription(user, nextSubPayment, appStoreOriginalTransactionId);
        } else {
            return Collections.singletonList(user);
        }
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public void setPromotionService(PromotionService promotionService) {
        this.promotionService = promotionService;
    }

    public void setUserNotificationService(UserNotificationService userNotificationService) {
        this.userNotificationService = userNotificationService;
    }

}