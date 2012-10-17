package mobi.nowtechnologies.server.persistence.repository;

import java.util.List;

import mobi.nowtechnologies.server.persistence.domain.PendingPayment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * @author Titov Mykhaylo (titov)
 *
 */
public interface PendingPaymentRepository extends JpaRepository<PendingPayment, Long> {
	
	@Query(value="select pendingPayment from PendingPayment pendingPayment" +
			" left join FETCH pendingPayment.user user" +
			" where" +
			" pendingPayment.userId=?1")
	List<PendingPayment> findByUserId(int userId);

}