package mobi.nowtechnologies.server.persistence.repository;

import java.util.List;

import mobi.nowtechnologies.server.persistence.domain.MigPaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.PaymentDetails;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;


/**
 * @author Titov Mykhaylo (titov)
 *
 */
public interface PaymentDetailsRepository extends JpaRepository<PaymentDetails, Long> {

	@Query(value="select pd from PaymentDetails pd where pd.migPhoneNumber=CONCAT(?1, '.', ?2) or (UPPER(?1)='O2' and ?2=TRIM('+' from pd.phoneNumber))")
	List<PaymentDetails> findPaymentDetails(String operatorName, String phoneNumber);
	

}