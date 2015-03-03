package mobi.nowtechnologies.server.service.payment;

import mobi.nowtechnologies.server.persistence.domain.payment.PendingPayment;

import java.util.List;

public interface PendingPaymentService {

    /**
     * Method search for users that needs to have a payment transaction and creates a pending payment for each of found. Also method should change users lastPaymentStatus to AWAITING
     *
     * @return A list of created pending payments ready to be processed
     */
    List<PendingPayment> createPendingPayments();

    /**
     * Method search for users that has lastPaymentStatus=(ERROR or EXTERNAL_ERROR) and creates a pending payment for each of found. Also method should increase madeRetries for user and change users
     * lastPaymentStatus to AWAITING
     *
     * @return A list of created pending payments ready to be processed
     */
    List<PendingPayment> createRetryPayments();

    /**
     * Method returns a list of all pending payments It should be used only once when server is starting up in order to push all pending payments into executor
     *
     * @return
     */
    List<PendingPayment> getExpiredPendingPayments();

    List<PendingPayment> getPendingPayments(int userId);

}