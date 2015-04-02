package mobi.nowtechnologies.server.service.aop;

import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentDetails;
import mobi.nowtechnologies.server.persistence.repository.UserRepository;
import mobi.nowtechnologies.server.service.UserNotificationService;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Aspect
public class SMSNotification {

    private static final Logger LOGGER = LoggerFactory.getLogger(SMSNotification.class);

    private UserNotificationService userNotificationService;

    private UserRepository userRepository;

    public void setUserNotificationService(UserNotificationService userNotificationService) {
        this.userNotificationService = userNotificationService;
    }

    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Sending sms after user was set to limited status
     */
    @Around("execution(* mobi.nowtechnologies.server.service.UserService.saveWeeklyPayment(*))")
    public Object saveWeeklyPayment(ProceedingJoinPoint joinPoint) throws Throwable {
        Object object = joinPoint.proceed();
        User user = (User) joinPoint.getArgs()[0];
        try {
            userNotificationService.sendSmsOnFreeTrialExpired(user);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return object;
    }

    /**
     * Sending sms after user unsubscribe
     */
    @Around("execution(* mobi.nowtechnologies.server.service.UserService.unsubscribeUser(int, mobi.nowtechnologies.server.shared.dto.web.payment.UnsubscribeDto))")
    public Object unsubscribeUser(ProceedingJoinPoint joinPoint) throws Throwable {
        Object object = joinPoint.proceed();
        Integer userId = (Integer) joinPoint.getArgs()[0];
        try {
            User user = userRepository.findOne(userId);
            userNotificationService.sendUnsubscribeAfterSMS(user);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return object;
    }

    @SuppressWarnings("unchecked")
    @Around("execution(* mobi.nowtechnologies.server.service.UserService.unsubscribeUser(String, String))")
    public Object unsubscribeUserOnStopMessage(ProceedingJoinPoint joinPoint) throws Throwable {
        List<PaymentDetails> paymentDetailsList = (List<PaymentDetails>) joinPoint.proceed();
        Set<User> users = new HashSet<User>();
        for (PaymentDetails paymentDetails : paymentDetailsList) {
            try {
                User user = paymentDetails.getOwner();
                if (!users.contains(user)) {
                    userNotificationService.sendUnsubscribeAfterSMS(user);
                    users.add(user);
                }
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
        return paymentDetailsList;
    }

    /**
     * Sending sms before 48 h expire subscription
     */
    @Around("execution(* mobi.nowtechnologies.server.service.UserService.updateLastBefore48SmsMillis(..))")
    public Object updateLastBefore48SmsMillis(ProceedingJoinPoint joinPoint) throws Throwable {
        Object object = joinPoint.proceed();
        Integer userId = (Integer) joinPoint.getArgs()[joinPoint.getArgs().length - 1];
        try {
            User user = userRepository.findOne(userId);
            userNotificationService.sendLowBalanceWarning(user);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return object;
    }

    @Pointcut("execution(* mobi.nowtechnologies.server.service.PaymentDetailsService.createCreditCardPaymentDetails(..))")
    protected void createdCreditCardPaymentDetails() {
    }

    @Pointcut("execution(* mobi.nowtechnologies.server.service.PaymentDetailsService.commitPayPalPaymentDetails(..))")
    protected void createdPayPalPaymentDetails() {
    }

    @Pointcut("execution(* mobi.nowtechnologies.server.service.PaymentDetailsService.commitMigPaymentDetails(..))")
    protected void createdMigPaymentDetails() {
    }

    /**
     * Sending sms after user was subscribed with some payment details
     */
    @Around("createdCreditCardPaymentDetails()  || createdPayPalPaymentDetails() || createdMigPaymentDetails()")
    public Object createdPaymentDetails(ProceedingJoinPoint joinPoint) throws Throwable {
        Object object = joinPoint.proceed();
        Integer userId = (Integer) joinPoint.getArgs()[joinPoint.getArgs().length - 1];
        try {
            User user = userRepository.findOne(userId);
            userNotificationService.sendSubscriptionChangedSMS(user);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return object;
    }

    @Around("execution(* mobi.nowtechnologies.server.service.UserServiceNotification.sendSmsFor4GDowngradeForSubscribed(*))")
    public Object sendSmsFor4GDowngradeForSubscribed(ProceedingJoinPoint joinPoint) throws Throwable {
        Object object = joinPoint.proceed();
        User user = (User) joinPoint.getArgs()[0];
        try {
            userNotificationService.send4GDowngradeSMS(user, UserNotificationService.DOWNGRADE_FROM_4G_SUBSCRIBED);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return object;
    }

    @Around("execution(* mobi.nowtechnologies.server.service.UserServiceNotification.sendSmsFor4GDowngradeForFreeTrial(*))")
    public Object sendSmsFor4GDowngradeForFreeTrial(ProceedingJoinPoint joinPoint) throws Throwable {
        Object object = joinPoint.proceed();
        User user = (User) joinPoint.getArgs()[0];
        try {
            userNotificationService.send4GDowngradeSMS(user, UserNotificationService.DOWNGRADE_FROM_4G_FREETRIAL);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return object;
    }
}
