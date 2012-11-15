package mobi.nowtechnologies.server.persistence.repository;

import mobi.nowtechnologies.server.persistence.domain.AbstractPayment;
import mobi.nowtechnologies.server.persistence.domain.PaymentDetailsType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

/**
 * @author Titov Mykhaylo (titov)
 * 
 */
@NoRepositoryBean
public interface PaymentRepository<T extends AbstractPayment> extends JpaRepository<T, Long> {

	T findByTypeAndUserIdOrderByTimestampDesc(PaymentDetailsType paymentDetailsType, int userId);

}
