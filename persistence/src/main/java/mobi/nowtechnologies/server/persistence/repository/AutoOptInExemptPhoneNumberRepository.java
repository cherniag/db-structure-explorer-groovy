package mobi.nowtechnologies.server.persistence.repository;

import mobi.nowtechnologies.server.persistence.domain.AutoOptInExemptPhoneNumber;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AutoOptInExemptPhoneNumberRepository extends JpaRepository<AutoOptInExemptPhoneNumber, Long> {
    AutoOptInExemptPhoneNumber findByUserName(String userName);
}
