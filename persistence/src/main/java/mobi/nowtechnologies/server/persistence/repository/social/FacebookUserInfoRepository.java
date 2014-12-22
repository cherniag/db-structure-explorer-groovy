package mobi.nowtechnologies.server.persistence.repository.social;

import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.social.FacebookUserInfo;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by oar on 2/7/14.
 */
public interface FacebookUserInfoRepository extends BaseSocialRepository<FacebookUserInfo> {
    @Query("select f from FacebookUserInfo f join f.user u join u.userGroup ug join ug.community c where (f.email=:contact or f.facebookId=:contact) and c=:community")
    public List<FacebookUserInfo> findByEmailOrSocialId(@Param("contact") String contact, @Param("community") Community community);
}
