package mobi.nowtechnologies.server.service.payment.impl;

import mobi.nowtechnologies.server.dto.payment.PaymentPolicyDto;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.enums.PaymentPolicyType;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentDetailsType;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentPolicy;
import mobi.nowtechnologies.server.persistence.domain.payment.PendingPayment;
import mobi.nowtechnologies.server.persistence.domain.payment.Period;
import mobi.nowtechnologies.server.persistence.repository.PendingPaymentRepository;
import mobi.nowtechnologies.server.persistence.repository.UserRepository;
import mobi.nowtechnologies.server.service.PaymentPolicyService;
import mobi.nowtechnologies.server.service.UserService;
import mobi.nowtechnologies.server.service.payment.PaymentSystemService;
import mobi.nowtechnologies.server.service.payment.PendingPaymentService;
import mobi.nowtechnologies.server.shared.dto.web.payment.UnsubscribeDto;
import static mobi.nowtechnologies.server.persistence.domain.payment.PaymentDetailsType.REGULAR;
import static mobi.nowtechnologies.server.persistence.domain.payment.PaymentDetailsType.RETRY;
import static mobi.nowtechnologies.server.shared.Utils.getEpochSeconds;
import static mobi.nowtechnologies.server.shared.enums.PaymentDetailsStatus.AWAITING;
import static mobi.nowtechnologies.server.shared.enums.PaymentDetailsStatus.SUCCESSFUL;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Titov Mykhaylo (titov)
 */
public class PendingPaymentServiceImpl implements PendingPaymentService {

    private static final String NONE = "NONE";

    private static final Logger LOGGER = LoggerFactory.getLogger(PendingPaymentServiceImpl.class);

    private Map<String, PaymentSystemService> paymentSystems;

    private UserService userService;
    private UserRepository userRepository;
    private PaymentPolicyService paymentPolicyService;
    private PendingPaymentRepository pendingPaymentRepository;
    private int maxCount;

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public List<PendingPayment> createPendingPayments() {
        LOGGER.info("Selecting users for pending payments...");
        final Page<User> usersPage = userService.getUsersForPendingPayment(maxCount);

        LOGGER.info("{} users were selected for pending payment", usersPage.getNumberOfElements());
        if (usersPage.hasNextPage()) {
            LOGGER.warn("Pending Payments throughput is not enough to process this amount: {}, max count: {} ", usersPage.getTotalElements(), maxCount);
        }

        List<PendingPayment> pendingPayments = new LinkedList<PendingPayment>();
        for (User user : usersPage.getContent()) {
            if (shouldNotPayIfOneTimePaymentPolicy(user)) {
                LOGGER.info("Unsubscribe user {} because of one time payment policy {}", user.getId(), user.getCurrentPaymentDetails().getPaymentPolicy().getId());
                userService.unsubscribeUser(user, "One time payment policy");
            } else if (user.isInvalidPaymentPolicy()) {
                LOGGER.info("Creating pending payment was failed for user {}, because current paymentPolicy of this user is invalid user and needs to unsubscribe him", user.getUserName());
                userService.unsubscribeUser(user.getId(), new UnsubscribeDto().withReason("Payment Policy is invalid for user"));
            } else {
                LOGGER.info("Creating pending payment for user {} with balance {}", user.getId(), user.getSubBalance());
                PendingPayment pendingPayment = createPendingPayment(user, REGULAR);
                pendingPayment = pendingPaymentRepository.save(pendingPayment);
                pendingPayments.add(pendingPayment);

                PaymentDetails currentPaymentDetails = user.getCurrentPaymentDetails();
                currentPaymentDetails.setLastPaymentStatus(AWAITING);
                currentPaymentDetails.resetMadeAttemptsForFirstPayment();
                user = userService.updateUser(user);
                LOGGER.info("Pending payment {} was created for user {}", pendingPayment.getInternalTxId(), user.getUserName());
            }
        }

        LOGGER.info("{} pending payments were created", pendingPayments.size());
        return pendingPayments;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public List<PendingPayment> createRetryPayments() {
        LOGGER.info("Start creating retry payments...");

        int epochSeconds = getEpochSeconds();
        PageRequest nextSubPayment = new PageRequest(0, maxCount, Sort.Direction.ASC, "nextSubPayment");
        final Page<User> usersPage = userRepository.findUsersForRetryPayment(epochSeconds, nextSubPayment);

        LOGGER.info("{} users were selected for retry payment", usersPage.getNumberOfElements());
        if (usersPage.hasNextPage()) {
            LOGGER.warn("Retry Payments throughput is not enough to process this amount: {}, max count: {} ", usersPage.getTotalElements(), maxCount);
        }

        List<PendingPayment> retryPayments = new LinkedList<PendingPayment>();
        for (User user : usersPage.getContent()) {
            PendingPayment pendingPayment = createPendingPayment(user, RETRY);
            pendingPayment = pendingPaymentRepository.save(pendingPayment);
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
        return pendingPaymentRepository.findExpiredPayments(new Date().getTime());
    }

    private boolean shouldNotPayIfOneTimePaymentPolicy(User user) {
        PaymentDetails details = user.getCurrentPaymentDetails();
        PaymentPolicy policy = details.getPaymentPolicy();
        return details.getLastPaymentStatus() == SUCCESSFUL && policy.getPaymentPolicyType() == PaymentPolicyType.ONETIME;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
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
        PaymentPolicyDto paymentPolicyDto = paymentPolicyService.getPaymentPolicy(currentPaymentDetails);
        pendingPayment.setPaymentDetails(currentPaymentDetails);
        pendingPayment.setAmount(paymentPolicyDto.getSubcost());
        pendingPayment.setCurrencyISO(paymentPolicyDto.getCurrencyISO());
        pendingPayment.setPaymentSystem(currentPaymentDetails.getPaymentType());
        pendingPayment.setPeriod(new Period().withDuration(paymentPolicyDto.getDuration()).withDurationUnit(paymentPolicyDto.getDurationUnit()));
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
}