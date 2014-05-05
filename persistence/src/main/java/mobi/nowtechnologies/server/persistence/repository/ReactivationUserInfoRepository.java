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

    @Query("select r.reactivationRequest as result from ReactivationUserInfo r where r.user = ?1 and r.reactivationRequest = true")
    Boolean isUserShouldBeReactivated(User user);


    ReactivationUserInfo findByUser(User user);

    @Modifying
    @Query("update ReactivationUserInfo r set r.reactivationRequest = false where r.user = ?1")
    int disableReactivationForUser(User user);
}
