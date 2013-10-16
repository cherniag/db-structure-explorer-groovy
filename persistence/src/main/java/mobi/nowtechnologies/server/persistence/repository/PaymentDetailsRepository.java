package mobi.nowtechnologies.server.persistence.repository;

import mobi.nowtechnologies.server.persistence.domain.payment.PaymentDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


/**
 * @author Titov Mykhaylo (titov)
 *
 */
public interface PaymentDetailsRepository extends JpaRepository<PaymentDetails, Long> {

	@Query(value="select pd from PaymentDetails pd where pd.activated=true and (pd.migPhoneNumber=CONCAT(?1, '.', ?2) or (TYPE(pd) like CONCAT(LOWER(?1),'%') and ?2=TRIM('+' from pd.phoneNumber)))")
	List<PaymentDetails> findActivatedPaymentDetails(String operatorName, String phoneNumber);
}