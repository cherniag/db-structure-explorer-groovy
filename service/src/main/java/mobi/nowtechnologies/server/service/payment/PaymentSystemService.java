package mobi.nowtechnologies.server.service.payment;

import mobi.nowtechnologies.server.persistence.domain.PendingPayment;
import mobi.nowtechnologies.server.persistence.domain.payment.SubmittedPayment;
import mobi.nowtechnologies.server.service.payment.response.PaymentSystemResponse;

public interface PaymentSystemService {
	
	public void startPayment(PendingPayment pendingPayment) throws Exception;
	
	public SubmittedPayment commitPayment(PendingPayment pendingPayment, PaymentSystemResponse response) throws Exception;
	
	public int getRetriesOnError();
	
	public long getExpireMillis();
	
	public PaymentSystemResponse getExpiredResponse();
}