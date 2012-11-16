package mobi.nowtechnologies.server.service.payment;

import java.util.List;

import mobi.nowtechnologies.server.persistence.domain.SubmittedPayment;
import mobi.nowtechnologies.server.shared.enums.PaymentDetailsStatus;

/**
 * @author Titov Mykhaylo (titov)
 *
 */
public interface SubmitedPaymentService {
	
	List<SubmittedPayment> findByUserIdAndPaymentStatus(List<Integer> userIds, List<PaymentDetailsStatus> paymentDetailsStatuses);

}
