package mobi.nowtechnologies.server.service.payment;

import mobi.nowtechnologies.server.persistence.domain.Operator;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentPolicy;
import mobi.nowtechnologies.server.persistence.repository.OperatorRepository;
import mobi.nowtechnologies.server.persistence.repository.PaymentDetailsRepository;
import mobi.nowtechnologies.server.persistence.repository.PaymentPolicyRepository;
import mobi.nowtechnologies.server.persistence.repository.PromotionPaymentPolicyRepository;
import mobi.nowtechnologies.server.persistence.repository.UserRepository;
import mobi.nowtechnologies.server.service.PromotionService;
import mobi.nowtechnologies.server.service.UserNotificationService;
import mobi.nowtechnologies.server.service.exception.ServiceException;
import mobi.nowtechnologies.server.service.payment.http.MigHttpService;
import mobi.nowtechnologies.server.service.payment.response.MigResponse;
import mobi.nowtechnologies.server.shared.Utils;
import mobi.nowtechnologies.server.shared.message.CommunityResourceBundleMessageSource;

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
    PaymentDetailsRepository paymentDetailsRepository;
    @Resource
    PromotionPaymentPolicyRepository promotionPaymentPolicyRepository;
    @Resource
    PaymentPolicyRepository paymentPolicyRepository;
    @Resource
    OperatorRepository operatorRepository;
    @Resource
    PromotionService promotionService;
    @Resource
    MigHttpService httpService;
    @Resource(name = "serviceMessageSource")
    CommunityResourceBundleMessageSource messageSource;

    //
    // API
    //
    public void commitPaymentDetails(int userId, String verificationPin) throws ServiceException {
        User user = userRepository.findOne(userId);

        logger.info("Verifying pin:{} from mig for user id:{}", verificationPin, user.getId());

        if (StringUtils.hasText(verificationPin) && user.getPin().equals(verificationPin)) {
            migPaymentDetailsInfoService.commitPaymentDetailsInfo(user);

            logger.info("Verification passed. Mig payment details has been created for user id:{}", user.getId());

            sendNotification(user);
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

        String pin = generatePin();
        user.setPin(pin);
        userRepository.save(user);

        sendPin(user, pin, phone);
    }

    private String generatePin() {
        return Utils.generateRandom4DigitsPIN();
    }

    private void sendPin(User user, String pin, String phoneNumber) {
        String communityUrl = user.getUserGroup().getCommunity().getRewriteUrlParameter().toLowerCase();
        String message = messageSource.getMessage(communityUrl, "sms.freeMsg", new Object[] {pin}, null);
        MigResponse response = httpService.makeFreeSMSRequest(phoneNumber, message);
        if (response.isSuccessful()) {
            logger.info("Free sms with pin code was sent");
        } else {
            logger.error("Problem while sending free sms. Error: {}", response.getDescriptionError());
        }
    }

    private void sendNotification(User user) {
        try {
            userNotificationService.sendSubscriptionChangedSMS(user);
        } catch (UnsupportedEncodingException e) {
            logger.error("Can not send subscription SMS for user: {}", user.getId());
            throw new RuntimeException(e);
        }
    }
}
