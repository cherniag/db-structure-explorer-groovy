package mobi.nowtechnologies.server.service.payment;

import mobi.nowtechnologies.server.persistence.domain.payment.PendingPayment;
import mobi.nowtechnologies.server.persistence.domain.payment.SubmittedPayment;
import mobi.nowtechnologies.server.service.payment.response.PaymentSystemResponse;

public interface PaymentSystemService {

    void startPayment(PendingPayment pendingPayment) throws Exception;

    SubmittedPayment commitPayment(PendingPayment pendingPayment, PaymentSystemResponse response);

    long getExpireMillis();

    PaymentSystemResponse getExpiredResponse();
}