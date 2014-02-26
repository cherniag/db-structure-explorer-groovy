package mobi.nowtechnologies.server.persistence.repository;

import mobi.nowtechnologies.server.persistence.domain.social.FacebookUserInfo;
import mobi.nowtechnologies.server.persistence.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

/**
 * Created by oar on 2/7/14.
 */
public interface FacebookUserInfoRepository extends JpaRepository<FacebookUserInfo, Long> {

    @Query(value="select facebookUserInfo from FacebookUserInfo facebookUserInfo where facebookUserInfo.user=?1")
    FacebookUserInfo findForUser(User user);

    @Modifying
    @Query(value="delete  from FacebookUserInfo facebookUserInfo where facebookUserInfo.user=?1")
    int deleteForUser(User user);

    FacebookUserInfo findByEmail(String email);
}
