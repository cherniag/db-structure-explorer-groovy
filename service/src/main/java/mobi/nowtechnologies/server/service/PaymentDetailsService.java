package mobi.nowtechnologies.server.service;

import mobi.nowtechnologies.common.dto.PaymentDetailsDto;
import mobi.nowtechnologies.server.persistence.domain.Promotion;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentPolicy;
import mobi.nowtechnologies.server.persistence.domain.payment.PromotionPaymentPolicy;
import mobi.nowtechnologies.server.persistence.domain.payment.SagePayCreditCardPaymentDetails;
import mobi.nowtechnologies.server.persistence.repository.PaymentDetailsRepository;
import mobi.nowtechnologies.server.persistence.repository.PaymentPolicyRepository;
import mobi.nowtechnologies.server.persistence.repository.PromotionPaymentPolicyRepository;
import mobi.nowtechnologies.server.persistence.repository.UserRepository;
import mobi.nowtechnologies.server.service.exception.CanNotDeactivatePaymentDetailsException;
import mobi.nowtechnologies.server.service.exception.ServiceException;
import mobi.nowtechnologies.server.service.payment.SagePayPaymentService;
import mobi.nowtechnologies.server.shared.dto.web.payment.CreditCardDto;
import mobi.nowtechnologies.server.shared.enums.PaymentDetailsStatus;
import static mobi.nowtechnologies.common.dto.UserRegInfo.PaymentType.CREDIT_CARD;
import static mobi.nowtechnologies.server.shared.ObjectUtils.isNotNull;

import javax.annotation.Resource;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static org.apache.commons.lang.Validate.notNull;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Titov Mykhaylo (titov)
 * @author Alexander Kolpakov (akolpakov)
 */
public class PaymentDetailsService {
    private static final Logger LOGGER = LoggerFactory.getLogger(PaymentDetailsService.class);

    private SagePayPaymentService sagePayPaymentService;
    private PromotionService promotionService;
    private UserService userService;

    @Resource
    UserRepository userRepository;
    @Resource
    PaymentDetailsRepository paymentDetailsRepository;
    @Resource
    PromotionPaymentPolicyRepository promotionPaymentPolicyRepository;
    @Resource
    PaymentPolicyRepository paymentPolicyRepository;

    PaymentDetails createPaymentDetails(PaymentDetailsDto dto, User user) throws ServiceException {

        PaymentPolicy paymentPolicy = paymentPolicyRepository.findOne(dto.getPaymentPolicyId());
        Promotion promotion = user.getPotentialPromotion();
        PromotionPaymentPolicy promotionPaymentPolicy = null;
        if (null != promotion) {
            promotionPaymentPolicy = promotionPaymentPolicyRepository.findPromotionPaymentPolicy(promotion, paymentPolicy);
        }

        PaymentDetails paymentDetails = null;
        if (null != paymentPolicy) {
            if (dto.getPaymentType().equals(CREDIT_CARD)) {
                dto.setCurrency(paymentPolicy.getCurrencyISO());
                dto.setAmount(paymentPolicy.getSubcost().toString());
                dto.setVendorTxCode(UUID.randomUUID().toString());
                dto.setDescription("Creating payment details for user " + user.getUserName());
                paymentDetails = sagePayPaymentService.createPaymentDetails(dto, user, paymentPolicy);
            }

            if (null != paymentDetails) {
                if (null != promotion) {
                    paymentDetails.setPromotionPaymentPolicy(promotionPaymentPolicy);
                    promotionService.incrementUserNumber(promotion);
                }

                paymentDetails = paymentDetailsRepository.save(paymentDetails);
            }
        }

        return paymentDetails;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public SagePayCreditCardPaymentDetails createCreditCardPaymentDetails(CreditCardDto dto, int userId) throws ServiceException {
        User user = userRepository.findOne(userId);
        if(user.isLimited()) {
            promotionService.applyPromoToLimitedUser(user);
        }
        PaymentDetailsDto pdto = CreditCardDto.toPaymentDetails(dto);

        return (SagePayCreditCardPaymentDetails) createPaymentDetails(pdto, user);
    }

    @Transactional(readOnly = true)
    public PaymentDetails getPendingPaymentDetails(int userId) {
        List<PaymentDetails> detailsList = paymentDetailsRepository.findPaymentDetailsByOwnerIdAndLastPaymentStatus(userId, PaymentDetailsStatus.PENDING);
        if(detailsList == null || detailsList.isEmpty()){
            return null;
        }
        return detailsList.get(0);
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = CanNotDeactivatePaymentDetailsException.class)
    public User deactivateCurrentPaymentDetailsIfOneExist(User user, String reason) {
        LOGGER.info("Deactivate current payment details for user {} reason {}", user.shortInfo(), reason);

        notNull(user, "The parameter user is null");
        user = userService.setToZeroSmsAccordingToLawAttributes(user);

        PaymentDetails currentPaymentDetails = user.getCurrentPaymentDetails();

        if (isNotNull(currentPaymentDetails)) {
            boolean inPending = currentPaymentDetails.getLastPaymentStatus() == PaymentDetailsStatus.AWAITING;
            if (inPending) {
                throw new CanNotDeactivatePaymentDetailsException();
            }
            currentPaymentDetails.disable(reason, new Date());
            paymentDetailsRepository.save(currentPaymentDetails);
            user = userRepository.save(user);
        }

        LOGGER.info("Current payment details were deactivated for user {}", user.shortInfo());
        return user;
    }

    public void setSagePayPaymentService(SagePayPaymentService sagePayPaymentService) {
        this.sagePayPaymentService = sagePayPaymentService;
    }

    public void setPromotionService(PromotionService promotionService) {
        this.promotionService = promotionService;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }
}
