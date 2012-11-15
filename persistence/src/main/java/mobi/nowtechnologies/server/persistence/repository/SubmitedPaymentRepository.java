package mobi.nowtechnologies.server.persistence.repository;

import java.util.List;

import mobi.nowtechnologies.server.persistence.domain.SubmittedPayment;
import mobi.nowtechnologies.server.shared.enums.PaymentDetailsStatus;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * @author Titov Mykhaylo (titov)
 *
 */
public interface SubmitedPaymentRepository extends PaymentRepository<SubmittedPayment>{

	@Query(value="select submittedPayment from SubmittedPayment submittedPayment where submittedPayment.userId in :userIds and submittedPayment.status in :paymentDetailsStatuses order by submittedPayment.timestamp desc")
	List<SubmittedPayment> findByUserIdAndPaymentStatus(@Param("userIds") List<Integer> userIds, @Param("paymentDetailsStatuses") List<PaymentDetailsStatus> paymentDetailsStatuses);
}
