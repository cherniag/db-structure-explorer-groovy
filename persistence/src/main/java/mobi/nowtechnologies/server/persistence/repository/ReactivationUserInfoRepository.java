package mobi.nowtechnologies.server.persistence.repository;

import mobi.nowtechnologies.server.persistence.domain.ReactivationUserInfo;
import mobi.nowtechnologies.server.persistence.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

/**
 * Created by oar on 4/30/2014.
 */
public interface ReactivationUserInfoRepository extends JpaRepository<ReactivationUserInfo, Long> {
    ReactivationUserInfo findByUser(User user);

    @Modifying(clearAutomatically = true)
    @Query("update ReactivationUserInfo r set r.reactivationRequest = false where r.user = ?1")
    int disableReactivationForUser(User user);
}
