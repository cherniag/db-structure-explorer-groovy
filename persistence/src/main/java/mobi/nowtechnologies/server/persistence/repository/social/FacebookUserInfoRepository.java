package mobi.nowtechnologies.server.persistence.repository.social;

import mobi.nowtechnologies.server.persistence.domain.social.FacebookUserInfo;
import mobi.nowtechnologies.server.persistence.domain.User;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

/**
 * Created by oar on 2/7/14.
 */
public interface FacebookUserInfoRepository extends BaseSocialRepository<FacebookUserInfo> {

    @Modifying
    @Query(value="delete  from FacebookUserInfo facebookUserInfo where facebookUserInfo.user=?1")
    int deleteByUser(User user);

}
