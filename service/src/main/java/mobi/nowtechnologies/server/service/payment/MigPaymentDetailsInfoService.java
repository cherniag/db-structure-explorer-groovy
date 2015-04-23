package mobi.nowtechnologies.server.service.payment;

import mobi.nowtechnologies.server.persistence.domain.Operator;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.payment.MigPaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentPolicy;
import mobi.nowtechnologies.server.persistence.domain.payment.PromotionPaymentPolicy;
import mobi.nowtechnologies.server.persistence.repository.PaymentDetailsRepository;
import mobi.nowtechnologies.server.persistence.repository.PaymentPolicyRepository;
import mobi.nowtechnologies.server.persistence.repository.PromotionPaymentPolicyRepository;
import mobi.nowtechnologies.server.persistence.repository.UserRepository;
import mobi.nowtechnologies.server.service.PaymentDetailsService;
import mobi.nowtechnologies.server.service.PromotionService;
import mobi.nowtechnologies.server.service.UserNotificationService;
import mobi.nowtechnologies.server.service.exception.ServiceException;
import mobi.nowtechnologies.server.shared.enums.PaymentDetailsStatus;
import static mobi.nowtechnologies.server.shared.enums.PaymentDetailsStatus.NONE;

import javax.annotation.Resource;

import java.util.List;

import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.transaction.annotation.Transactional;

public class MigPaymentDetailsInfoService {
    Logger logger = LoggerFactory.getLogger(getClass());
    @Resource
    PaymentDetailsService paymentDetailsService;
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
    PromotionService promotionService;

    private int retriesOnError;

    public void setRetriesOnError(int retriesOnError) {
        logger.info("Retries On Error: {}", retriesOnError);

        this.retriesOnError = retriesOnError;
    }

    @Transactional
    public void createPaymentDetailsInfo(User user, Operator operator, String phone, PaymentPolicy paymentPolicy) {
        Preconditions.checkNotNull(operator);
        Preconditions.checkNotNull(paymentPolicy);

        PaymentDetails pendingPaymentDetails = findLastPendingPaymentDetails(user);
        if (null != pendingPaymentDetails) {
            pendingPaymentDetails.setLastPaymentStatus(PaymentDetailsStatus.ERROR);
            pendingPaymentDetails.setDescriptionError("Was not verified and replaced by another payment details");
        }

        logger.info("Starting creating MIG payment details...");

        String phoneNumber = getMigPhoneNumber(phone, operator);
        logger.info("phoneNumber: {}", phoneNumber);
        PaymentDetails paymentDetails = new MigPaymentDetails(phoneNumber, user, paymentPolicy, retriesOnError);

        if (null != user.getPotentialPromotion()) {
            PromotionPaymentPolicy promotionPaymentPolicy = promotionPaymentPolicyRepository.findPromotionPaymentPolicy(user.getPotentialPromotion(), paymentPolicy);
            paymentDetails.setPromotionPaymentPolicy(promotionPaymentPolicy);
            promotionService.incrementUserNumber(user.getPotentialPromotion());
        }

        paymentDetailsRepository.save(paymentDetails);
    }


    @Transactional
    public MigPaymentDetails commitPaymentDetailsInfo(User user) throws ServiceException {
        paymentDetailsService.deactivateCurrentPaymentDetailsIfOneExist(user, "Commit new payment details");

        MigPaymentDetails paymentDetails = findLastPendingPaymentDetails(user);
        paymentDetails.setOwner(user);
        paymentDetails.setActivated(true);
        paymentDetails.setLastPaymentStatus(NONE);
        paymentDetails.setRetriesOnError(retriesOnError);
        paymentDetails.resetMadeAttempts();

        paymentDetailsRepository.save(paymentDetails);

        user.setPin("");
        user.setCurrentPaymentDetails(paymentDetails);
        userRepository.save(user);

        return paymentDetails;
    }

    //
    // Internals
    //
    private String getMigPhoneNumber(String phone, Operator operator) {
        String mobile = convertPhoneNumberFromGreatBritainToInternationalFormat(phone);
        return operator.getMigName() + "." + mobile;
    }

    private String convertPhoneNumberFromGreatBritainToInternationalFormat(String mobile) {
        if (!mobile.startsWith("0044")) {
            return mobile.replaceFirst("0", "0044");
        }
        return mobile;
    }

    private MigPaymentDetails findLastPendingPaymentDetails(User user) {
        List<PaymentDetails> detailsList = paymentDetailsRepository.findPaymentDetailsByOwnerIdAndLastPaymentStatus(user.getId(), PaymentDetailsStatus.PENDING);
        if(detailsList == null || detailsList.isEmpty()){
            return null;
        }
        return (MigPaymentDetails)detailsList.get(0);
    }
}
