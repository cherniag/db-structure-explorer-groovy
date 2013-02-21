package mobi.nowtechnologies.server.service.listener;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import mobi.nowtechnologies.server.persistence.domain.PaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.PaymentDetailsType;
import mobi.nowtechnologies.server.persistence.domain.SubmittedPayment;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.service.PromotionService;
import mobi.nowtechnologies.server.service.UserNotificationService;
import mobi.nowtechnologies.server.service.UserService;
import mobi.nowtechnologies.server.service.event.PaymentEvent;
import mobi.nowtechnologies.server.service.payment.http.MigHttpService;
import mobi.nowtechnologies.server.shared.message.CommunityResourceBundleMessageSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Titov Mykhaylo (titov)
 * @author dmytro
 * 
 */
public class SubBalancePaymentListener implements ApplicationListener<PaymentEvent> {

	private static final Logger LOGGER = LoggerFactory.getLogger(SubBalancePaymentListener.class);

	private UserService userService;
	private PromotionService promotionService;
	private MigHttpService migHttpService;
	private CommunityResourceBundleMessageSource messageSource;
	private UserNotificationService userNotificationService;

	@Transactional(propagation = Propagation.REQUIRED)
	@Override
	public void onApplicationEvent(PaymentEvent event) {
		SubmittedPayment payment = (SubmittedPayment) event.getPayment();

		if (payment.getType() != PaymentDetailsType.PAYMENT) {
					
			LOGGER.info("handle SubBalance payment event: [{}]", payment);
			User user = payment.getUser();
			int subweeks = payment.getSubweeks();
			final String appStoreOriginalTransactionId = payment.getAppStoreOriginalTransactionId();
			final int nextSubPayment = payment.getNextSubPayment();
		
			final List<User> users;
			if (appStoreOriginalTransactionId != null) {
				users = userService.findUsersForItunesInAppSubscription(user, nextSubPayment, appStoreOriginalTransactionId);
			}else{
				users = Collections.singletonList(user);
			}
			
			for (User actualUser : users) {
	
				userService.processPaymentSubBalanceCommand(actualUser, subweeks, payment);
	
				if (payment.getType() == PaymentDetailsType.FIRST) {
					LOGGER
							.info("Applying promotions to user {} after his first successful payment with status {} ", actualUser.getId(), payment
									.getStatus());
					userService.applyPromotion(actualUser);
					promotionService.applyPromotion(actualUser);
				}
	
				PaymentDetails currentActivePaymentDetails = actualUser.getCurrentPaymentDetails();
				if (currentActivePaymentDetails != null && PaymentDetails.MIG_SMS_TYPE.equals(currentActivePaymentDetails.getPaymentType())) {
					userNotificationService.notifyUserAboutSuccesfullPayment(actualUser);
				}
	
				userService.populateAmountOfMoneyToUserNotification(actualUser, payment);
			}
		}
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	public void setPromotionService(PromotionService promotionService) {
		this.promotionService = promotionService;
	}

	public void setMigHttpService(MigHttpService migHttpService) {
		this.migHttpService = migHttpService;
	}

	public void setMessageSource(CommunityResourceBundleMessageSource messageSource) {
		this.messageSource = messageSource;
	}
	
	public void setUserNotificationService(UserNotificationService userNotificationService) {
		this.userNotificationService = userNotificationService;
	}
 
}