package mobi.nowtechnologies.server.service.payment;

import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.payment.SubmittedPayment;
import mobi.nowtechnologies.server.shared.enums.PaymentDetailsStatus;

import java.util.List;

/**
 * @author Titov Mykhaylo (titov)
 *
 */
public interface SubmittedPaymentService {
	
	List<SubmittedPayment> findByUserIdAndPaymentStatus(List<Integer> userIds, List<PaymentDetailsStatus> paymentDetailsStatuses);
	
	SubmittedPayment save(SubmittedPayment submittedPayment);

	SubmittedPayment getLatest(User user);
}