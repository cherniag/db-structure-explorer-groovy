package mobi.nowtechnologies.server.persistence.repository.social;

import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.social.GooglePlusUserInfo;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

/**
 * Created by oar on 2/7/14.
 */
public interface GooglePlusUserInfoRepository extends BaseSocialRepository<GooglePlusUserInfo> {

    @Modifying
    @Query(value = "delete  from GooglePlusUserInfo googlePlusUserInfo where googlePlusUserInfo.user=?1")
    int deleteByUser(User user);

}
