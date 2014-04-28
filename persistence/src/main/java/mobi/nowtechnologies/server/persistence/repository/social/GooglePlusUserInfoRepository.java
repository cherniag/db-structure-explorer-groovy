package mobi.nowtechnologies.server.persistence.repository.social;

import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.social.GooglePlusUserInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

/**
 * Created by oar on 2/7/14.
 */
public interface GooglePlusUserInfoRepository extends JpaRepository<GooglePlusUserInfo, Long> {

    @Query(value = "select googlePlusUserInfo from GooglePlusUserInfo googlePlusUserInfo where googlePlusUserInfo.user=?1")
    GooglePlusUserInfo findForUser(User user);

    @Modifying
    @Query(value = "delete  from GooglePlusUserInfo googlePlusUserInfo where googlePlusUserInfo.user=?1")
    int deleteForUser(User user);

    GooglePlusUserInfo findByEmail(String email);
}
