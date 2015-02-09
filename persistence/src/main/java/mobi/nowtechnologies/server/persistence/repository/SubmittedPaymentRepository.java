package mobi.nowtechnologies.server.persistence.repository;

import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.payment.SubmittedPayment;
import mobi.nowtechnologies.server.shared.enums.PaymentDetailsStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * @author Titov Mykhaylo (titov)
 *
 */
public interface SubmittedPaymentRepository extends PaymentRepository<SubmittedPayment>{

	@Query(value="select submittedPayment from SubmittedPayment submittedPayment where submittedPayment.userId in :userIds and submittedPayment.status in :paymentDetailsStatuses order by submittedPayment.timestamp desc")
	List<SubmittedPayment> findByUserIdAndPaymentStatus(@Param("userIds") List<Integer> userIds, @Param("paymentDetailsStatuses") List<PaymentDetailsStatus> paymentDetailsStatuses);

	@Query(value="select submittedPayment from SubmittedPayment submittedPayment where submittedPayment.user=:user order by submittedPayment.timestamp desc")
	List<SubmittedPayment> findTopByUser(@Param("user") User user, Pageable top);
}
