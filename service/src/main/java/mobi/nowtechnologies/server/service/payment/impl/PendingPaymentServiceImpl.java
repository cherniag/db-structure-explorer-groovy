package mobi.nowtechnologies.server.service.payment.impl;

import mobi.nowtechnologies.common.ListDataResult;
import mobi.nowtechnologies.server.persistence.dao.PaymentDao;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentDetailsType;
import mobi.nowtechnologies.server.persistence.domain.payment.PendingPayment;
import mobi.nowtechnologies.server.persistence.repository.PendingPaymentRepository;
import mobi.nowtechnologies.server.service.PaymentPolicyService;
import mobi.nowtechnologies.server.service.UserService;
import mobi.nowtechnologies.server.service.payment.PaymentSystemService;
import mobi.nowtechnologies.server.service.payment.PendingPaymentService;
import mobi.nowtechnologies.server.shared.dto.PaymentPolicyDto;
import mobi.nowtechnologies.server.shared.dto.web.payment.UnsubscribeDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static mobi.nowtechnologies.server.persistence.domain.payment.PaymentDetailsType.REGULAR;
import static mobi.nowtechnologies.server.persistence.domain.payment.PaymentDetailsType.RETRY;
import static mobi.nowtechnologies.server.shared.enums.PaymentDetailsStatus.AWAITING;

/**
 * @author Titov Mykhaylo (titov)
 */
public class PendingPaymentServiceImpl implements PendingPaymentService {

	private static final String NONE = "NONE";

	private static final Logger LOGGER = LoggerFactory.getLogger(PendingPaymentServiceImpl.class);

	private Map<String, PaymentSystemService> paymentSystems;

	private UserService userService;
	private PaymentDao paymentDao;
	private PaymentPolicyService paymentPolicyService;
	private PendingPaymentRepository pendingPaymentRepository;
    private int maxCount;

    @Transactional(propagation = Propagation.REQUIRED)
	@Override
	public List<PendingPayment> createPendingPayments() {
		LOGGER.info("Selecting users for pending payments...");
        final ListDataResult<User> users = userService.getUsersForPendingPayment(maxCount);

        LOGGER.info("{} users were selected for pending payment", users.getSize());
        if(users.totalExceedsDataSize()) {
            LOGGER.warn("Pending Payments throughput is not enough to process this amount: {}, max count: {} ", users.getTotal(), maxCount);
        }

        // unsubscribing users with invalid payment policy
        for (User user : collectUsersWithInvalidPaymentPolicy(users)) {
            LOGGER.info("Creating pending payment was failed for user {}, because current paymentPolicy of this user is invalid user and needs to unsubscribe him", user.getUserName());
            userService.unsubscribeUser(user.getId(), new UnsubscribeDto().withReason("Payment Policy is invalid for user"));
        }

        // processing users with valid payment policy
        List<PendingPayment> pendingPayments = new LinkedList<PendingPayment>();
		for (User user : collectUsersWithValidPaymentPolicy(users)) {
            LOGGER.info("Creating pending payment for user {} with balance {}", user.getId(), user.getSubBalance());
            PendingPayment pendingPayment = createPendingPayment(user, REGULAR);
            pendingPayment = paymentDao.savePendingPayment(pendingPayment);
            pendingPayments.add(pendingPayment);

            PaymentDetails currentPaymentDetails = user.getCurrentPaymentDetails();
            currentPaymentDetails.setLastPaymentStatus(AWAITING);
            currentPaymentDetails.resetMadeAttemptsForFirstPayment();
            user = userService.updateUser(user);
            LOGGER.info("Pending payment {} was created for user {}", pendingPayment.getInternalTxId(), user.getUserName());
		}

		LOGGER.info("{} pending payments were created", pendingPayments.size());
		return pendingPayments;
	}

    @Transactional(propagation = Propagation.REQUIRED)
	@Override
	public List<PendingPayment> createRetryPayments() {
		LOGGER.info("Start creating retry payments...");
        final ListDataResult<User> users = userService.getUsersForRetryPayment(maxCount);

		LOGGER.info("{} users were selected for retry payment", users.getSize());
        if(users.totalExceedsDataSize()) {
            LOGGER.warn("Retry Payments throughput is not enough to process this amount: {}, max count: {} ", users.getTotal(), maxCount);
        }

		List<PendingPayment> retryPayments = new LinkedList<PendingPayment>();
		for (User user : users.getData()) {
			PendingPayment pendingPayment = createPendingPayment(user, RETRY);
            pendingPayment = paymentDao.savePendingPayment(pendingPayment);
			retryPayments.add(pendingPayment);
			PaymentDetails currentPaymentDetails = user.getCurrentPaymentDetails();
			currentPaymentDetails.setLastPaymentStatus(AWAITING);
            userService.updateUser(user);
		}
        LOGGER.info("{} retry payments were created", retryPayments.size());
		return retryPayments;
	}

	@Override
	@Transactional(readOnly = true)
	public List<PendingPayment> getPendingPayments(int userId) {
		LOGGER.debug("input parameters userId: [{}]", userId);
		List<PendingPayment> pendingPayments = pendingPaymentRepository.findByUserId(userId);
		LOGGER.info("Output parameter pendingPayments=[{}]", pendingPayments);
		return pendingPayments;
	}

	@Transactional(readOnly = true, propagation = Propagation.REQUIRED)
	@Override
	public List<PendingPayment> getExpiredPendingPayments() {
		return paymentDao.getExpiredPendingPayments();
	}

	public void setPaymentDao(PaymentDao paymentDao) {
		this.paymentDao = paymentDao;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	public void setPaymentSystems(Map<String, PaymentSystemService> paymentSystems) {
		this.paymentSystems = paymentSystems;
	}

	public void setPaymentPolicyService(PaymentPolicyService paymentPolicyService) {
		this.paymentPolicyService = paymentPolicyService;
	}

	public void setPendingPaymentRepository(PendingPaymentRepository pendingPaymentRepository) {
		this.pendingPaymentRepository = pendingPaymentRepository;
	}

    public void setMaxCount(int maxCount) {
        this.maxCount = maxCount;
    }

    private PendingPayment createPendingPayment(User user, PaymentDetailsType type) {
        LOGGER.debug("Start creating pending payment for user {}", user.getUserName());
        PaymentDetails currentPaymentDetails = user.getCurrentPaymentDetails();

        PendingPayment pendingPayment = new PendingPayment();
        PaymentPolicyDto paymentPolicy = paymentPolicyService.getPaymentPolicy(currentPaymentDetails);
        pendingPayment.setPaymentDetails(currentPaymentDetails);
        pendingPayment.setAmount(paymentPolicy.getSubcost());
        pendingPayment.setCurrencyISO(paymentPolicy.getCurrencyISO());
        pendingPayment.setPaymentSystem(currentPaymentDetails.getPaymentType());
        pendingPayment.setSubweeks(paymentPolicy.getSubweeks());
        pendingPayment.setUser(user);
        pendingPayment.setExternalTxId(NONE);
        long currentTimeMillis = System.currentTimeMillis();
        pendingPayment.setTimestamp(currentTimeMillis);
        pendingPayment.setExpireTimeMillis(currentTimeMillis + paymentSystems.get(currentPaymentDetails.getPaymentType()).getExpireMillis());

        if (0 == user.getLastSuccessfulPaymentTimeMillis() && pendingPayment.getType() != PaymentDetailsType.PAYMENT) {
            pendingPayment.setType(PaymentDetailsType.FIRST);
        } else {
            pendingPayment.setType(type);
        }
        return pendingPayment;
    }

    private List<User> collectUsersWithValidPaymentPolicy(ListDataResult<User> users) {
        List<User> result = new ArrayList<User>();
        for (User user : users.getData()) {
            if (!user.isInvalidPaymentPolicy()) {
                result.add(user);
            }
        }
        return result;
    }

    private List<User> collectUsersWithInvalidPaymentPolicy(ListDataResult<User> users) {
        List<User> result = new ArrayList<User>();
        for (User user : users.getData()) {
            if (user.isInvalidPaymentPolicy()) {
                result.add(user);
            }
        }
        return result;
    }
}