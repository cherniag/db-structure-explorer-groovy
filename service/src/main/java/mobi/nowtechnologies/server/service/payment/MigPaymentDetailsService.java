package mobi.nowtechnologies.server.service.payment;

import mobi.nowtechnologies.server.persistence.domain.Operator;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.payment.MigPaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentPolicy;
import mobi.nowtechnologies.server.persistence.repository.PaymentPolicyRepository;
import mobi.nowtechnologies.server.persistence.repository.UserRepository;
import mobi.nowtechnologies.server.service.UserNotificationService;
import mobi.nowtechnologies.server.service.exception.ServiceException;

import javax.annotation.Resource;

import java.io.UnsupportedEncodingException;

import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

public class MigPaymentDetailsService {
    Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    MigPaymentDetailsInfoService migPaymentDetailsInfoService;
    @Resource
    UserNotificationService userNotificationService;
    @Resource
    UserRepository userRepository;
    @Resource
    PaymentPolicyRepository paymentPolicyRepository;
    @Resource
    PinMigService pinMigService;

    //
    // API
    //
    public void commitPaymentDetails(int userId, String verificationPin) throws ServiceException {
        User user = userRepository.findOne(userId);

        logger.info("Verifying pin:{} from mig for user id:{}", verificationPin, user.getId());

        if (StringUtils.hasText(verificationPin) && user.getPin().equals(verificationPin)) {
            MigPaymentDetails migPaymentDetails = migPaymentDetailsInfoService.commitPaymentDetailsInfo(user);

            logger.info("Verification passed. Mig payment details has been created for user id:{}", user.getId());

            sendNotification(user, migPaymentDetails);
        } else {
            logger.info("Incorrect pin for user [{}]", user.getId());
            throw new ServiceException("Incorrect pin");
        }
    }

    @Transactional
    public void createPaymentDetails(int userId, String phone, Integer paymentPolicyId, Operator operator) {
        Preconditions.checkNotNull(operator);

        PaymentPolicy paymentPolicy = paymentPolicyRepository.findOne(paymentPolicyId);

        if(paymentPolicy == null) {
            logger.info("No PaymentPolicy found for id:{}", paymentPolicyId);
            return;
        }

        User user = userRepository.findOne(userId);

        migPaymentDetailsInfoService.createPaymentDetailsInfo(user, operator, phone, paymentPolicy);

        pinMigService.sendPin(userId, phone);
    }

    private void sendNotification(User user, MigPaymentDetails migPaymentDetails) {
        try {
            userNotificationService.sendSubscriptionChangedSMS(user);
        } catch (UnsupportedEncodingException e) {
            logger.error("Can not send subscription SMS for user:{}, payment details:{}", user.getId(), migPaymentDetails.getI());
        }
    }
}
