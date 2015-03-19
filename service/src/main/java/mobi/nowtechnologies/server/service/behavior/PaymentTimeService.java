package mobi.nowtechnologies.server.service.behavior;

import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.payment.PendingPayment;
import mobi.nowtechnologies.server.persistence.domain.payment.SubmittedPayment;
import mobi.nowtechnologies.server.persistence.repository.PendingPaymentRepository;
import mobi.nowtechnologies.server.service.payment.SubmittedPaymentService;
import mobi.nowtechnologies.server.shared.enums.PaymentDetailsStatus;

import javax.annotation.Resource;

import java.util.Date;
import java.util.List;
import java.util.NavigableSet;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Preconditions;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PaymentTimeService {
    private static final long MILLIS_IN_MINUTE = TimeUnit.MINUTES.toMillis(1);
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private NavigableSet<Integer> timeRange = new TreeSet<>();

    @Resource
    PendingPaymentRepository pendingPaymentRepository;
    @Resource
    SubmittedPaymentService submittedPaymentService;

    public Date getNextRetryTime(User user, Date serverTime) {
        logger.info("Start calculating retry time for user {}, time {}", user.getId(), serverTime.getTime());
        PaymentDetails paymentDetails = user.getCurrentPaymentDetails();

        Preconditions.checkArgument(paymentDetails.isActivated());
        Preconditions.checkArgument(paymentDetails.getLastPaymentStatus() == PaymentDetailsStatus.AWAITING
                                    || paymentDetails.getLastPaymentStatus() == PaymentDetailsStatus.ERROR);

        if(paymentDetails.getLastPaymentStatus() == PaymentDetailsStatus.AWAITING) {
            return nextRequestTimeFromPendingPayment(user, serverTime);
        } else {
            return nextRequestTimeFromSubmittedPayment(user, serverTime);
        }
    }

    private Date nextRequestTimeFromPendingPayment(User user, Date serverTime) {
        List<PendingPayment> pendingPayments = pendingPaymentRepository.findByUserId(user.getId());
        if(pendingPayments.size() != 1) {
            logger.warn("Found {} pending payments for user {}", pendingPayments.size(), user.getId());
            return null;
        }
        long createTimestamp = pendingPayments.get(0).getTimestamp();
        return nextRequestTime(createTimestamp, serverTime);
    }

    private Date nextRequestTimeFromSubmittedPayment(User user, Date serverTime) {
        SubmittedPayment latest = submittedPaymentService.getLatest(user);
        if(latest == null) {
            logger.error("No SubmittedPayment was found for user {}", user.getId());
            return null;
        }
        long latestTimestamp = latest.getTimestamp();
        return nextRequestTime(latestTimestamp, serverTime);
    }

    private Date nextRequestTime(long lastActionTimestamp, Date serverTime) {
        int minutesSpent = (int) ((serverTime.getTime() - lastActionTimestamp) / MILLIS_IN_MINUTE);
        Integer timeShift = timeRange.higher(minutesSpent);
        logger.info("serverTime={}, lastActionTimestamp = {}, minutesSpent={}, timeShift={}", serverTime.getTime(), lastActionTimestamp, minutesSpent, timeShift);
        if(timeShift == null) {
            return null;
        }
        return DateUtils.addMinutes(serverTime, timeShift);
    }

    public void setTimeRange(int[] timeRange) {
        Preconditions.checkArgument(timeRange.length > 0, "No time range");

        for (int i : timeRange) {
            this.timeRange.add(i);
        }
    }
}
