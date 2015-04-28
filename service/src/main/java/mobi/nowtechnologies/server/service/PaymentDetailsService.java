package mobi.nowtechnologies.server.service;

import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentDetails;
import mobi.nowtechnologies.server.persistence.repository.PaymentDetailsRepository;
import mobi.nowtechnologies.server.persistence.repository.UserRepository;
import mobi.nowtechnologies.server.service.exception.CanNotDeactivatePaymentDetailsException;
import mobi.nowtechnologies.server.shared.enums.PaymentDetailsStatus;
import static mobi.nowtechnologies.server.shared.ObjectUtils.isNotNull;

import javax.annotation.Resource;

import java.util.Date;
import java.util.List;

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

    private UserService userService;

    @Resource
    UserRepository userRepository;
    @Resource
    PaymentDetailsRepository paymentDetailsRepository;

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

    public void setUserService(UserService userService) {
        this.userService = userService;
    }
}
