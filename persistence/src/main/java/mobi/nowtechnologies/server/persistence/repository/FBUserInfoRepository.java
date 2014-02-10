package mobi.nowtechnologies.server.persistence.repository;

import mobi.nowtechnologies.server.persistence.domain.social.FBUserInfo;
import mobi.nowtechnologies.server.persistence.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

/**
 * Created by oar on 2/7/14.
 */
public interface FBUserInfoRepository extends JpaRepository<FBUserInfo, Long> {

    @Query(value="select fbUserInfo from FBUserInfo fbUserInfo where fbUserInfo.user=?1")
    FBUserInfo findForUser(User user);

    @Modifying
    @Query(value="delete  from FBUserInfo fbUserInfo where fbUserInfo.user=?1")
    int deleteForUser(User user);
}
