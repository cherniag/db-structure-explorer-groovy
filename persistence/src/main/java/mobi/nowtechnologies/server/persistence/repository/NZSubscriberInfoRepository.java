package mobi.nowtechnologies.server.persistence.repository;

import mobi.nowtechnologies.server.persistence.domain.NZSubscriberInfo;
import mobi.nowtechnologies.server.persistence.domain.PinCode;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author Anton Zemliankin
 */
public interface NZSubscriberInfoRepository extends JpaRepository<NZSubscriberInfo, Integer> {

    NZSubscriberInfo findTopByMsisdn(String msisdn);

}
