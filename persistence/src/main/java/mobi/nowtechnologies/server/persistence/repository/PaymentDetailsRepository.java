package mobi.nowtechnologies.server.persistence.repository;

import mobi.nowtechnologies.server.persistence.domain.PaymentDetails;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;


@Transactional(readOnly=true)
public interface PaymentDetailsRepository extends JpaRepository<PaymentDetails, Long> {
}