package mobi.nowtechnologies.server.service.payment.impl;

import mobi.nowtechnologies.server.persistence.dao.PaymentDao;
import mobi.nowtechnologies.server.persistence.domain.PaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.PaymentDetailsType;
import mobi.nowtechnologies.server.persistence.domain.PendingPayment;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.repository.PendingPaymentRepository;
import mobi.nowtechnologies.server.service.PaymentPolicyService;
import mobi.nowtechnologies.server.service.UserService;
import mobi.nowtechnologies.server.service.payment.PaymentSystemService;
import mobi.nowtechnologies.server.service.payment.PendingPaymentService;
import mobi.nowtechnologies.server.shared.dto.PaymentPolicyDto;
import mobi.nowtechnologies.server.shared.enums.PaymentDetailsStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author Titov Mykhaylo (titov)
 * 
 */
public class PendingPaymentServiceImpl implements PendingPaymentService {

	private static final String NONE = "NONE";

	private static final Logger LOGGER = LoggerFactory.getLogger(PendingPaymentServiceImpl.class);

	private Map<String, PaymentSystemService> paymentSystems;

	private UserService userService;
	private PaymentDao paymentDao;
	private PaymentPolicyService paymentPolicyService;
	private PendingPaymentRepository pendingPaymentRepository;

	@Transactional(propagation = Propagation.REQUIRED)
	@Override
	public List<PendingPayment> createPendingPayments() {
		LOGGER.info("Selecting users for pending payments...");
		// Select users we need to make pending payment for
		List<User> usersForPendingPayment = userService.getUsersForPendingPayment();
		LOGGER.info("{} users were selected for pending payment", usersForPendingPayment.size());
		List<PendingPayment> pendingPayments = new LinkedList<PendingPayment>();
		// Going through all users in order to create a pending payment for each one
		for (User user : usersForPendingPayment) {
			if (!user.isInvalidPaymentPolicy()) {
				LOGGER.info("Creating pending payment for user {} with balance {}", user.getId(), user.getSubBalance());
				PendingPayment pendingPayment = createPendingPayment(user, PaymentDetailsType.REGULAR);
				pendingPayments.add(pendingPayment);
				// While creating a pending payment we update last payment status for user to AWAITING
				user.getCurrentPaymentDetails().setLastPaymentStatus(PaymentDetailsStatus.AWAITING);
				userService.updateUser(user);
				LOGGER.info("Pending payment {} was created for user {}", pendingPayment.getInternalTxId(), user.getUserName());
			} else {
				LOGGER.info("Creating pending payment was failed for user {}, because current paymentPolicy of this user is invalid user and needs to unsubscibe him", user.getUserName());
				
				userService.unsubscribeUser(user, "Payment Policy is invalid for user");
			}
		}
		LOGGER.info("{} pending payments were created", pendingPayments.size());
		return pendingPayments;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	@Override
	public List<PendingPayment> createRetryPayments() {
		LOGGER.info("Start creating retry-pending payments...");
		List<User> usersForRetryPayment = userService.getUsersForRetryPayment();
		LOGGER.debug("{} users were selected for retry payment", usersForRetryPayment.size());
		List<PendingPayment> retryPayments = new LinkedList<PendingPayment>();
		for (User user : usersForRetryPayment) {
			PendingPayment pendingPayment = createPendingPayment(user, PaymentDetailsType.RETRY);
			retryPayments.add(pendingPayment);
			PaymentDetails currentPaymentDetails = user.getCurrentPaymentDetails();
			// While creating a pending payment we update last payment status for user to AWAITING
			currentPaymentDetails.setLastPaymentStatus(PaymentDetailsStatus.AWAITING);
			if (currentPaymentDetails.getMadeRetries() == currentPaymentDetails.getRetriesOnError()) {
				currentPaymentDetails.setMadeRetries(1);
			} else {
				currentPaymentDetails.incrementRetries();
			}
			userService.updateUser(user);
		}
		return retryPayments;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public PendingPayment createPendingPayment(User user, PaymentDetailsType type) {
		LOGGER.debug("Start creating pending payment for user {}", user.getUserName());
		PendingPayment pendingPayment = new PendingPayment();
		PaymentDetails currentPaymentDetails = user.getCurrentPaymentDetails();
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
		return paymentDao.savePendingPayment(pendingPayment);
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
}