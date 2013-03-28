package mobi.nowtechnologies.server.persistence.repository;

import java.util.List;

import mobi.nowtechnologies.server.persistence.domain.PaymentDetails;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


/**
 * @author Titov Mykhaylo (titov)
 *
 */
public interface PaymentDetailsRepository extends JpaRepository<PaymentDetails, Long> {

	@Query(value="select pd from PaymentDetails pd where pd.activated=true and (pd.migPhoneNumber=CONCAT(?1, '.', ?2) or (UPPER(?1)='O2' and ?2=TRIM('+' from pd.phoneNumber)))")
	List<PaymentDetails> findActivatedPaymentDetails(String operatorName, String phoneNumber);
	

}