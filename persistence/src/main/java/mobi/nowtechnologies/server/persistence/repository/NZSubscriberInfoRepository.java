package mobi.nowtechnologies.server.persistence.repository;

import mobi.nowtechnologies.server.persistence.domain.NZSubscriberInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * @author Anton Zemliankin
 */
public interface NZSubscriberInfoRepository extends JpaRepository<NZSubscriberInfo, Integer> {

    @Query(value = "select si from NZSubscriberInfo si where si.msisdn = ?1")
    NZSubscriberInfo findSubscriberInfoByMsisdn(String msisdn);

    @Query(value = "select si from NZSubscriberInfo si where si.userId = ?1")
    NZSubscriberInfo findSubscriberInfoByUserId(int userId);
}
