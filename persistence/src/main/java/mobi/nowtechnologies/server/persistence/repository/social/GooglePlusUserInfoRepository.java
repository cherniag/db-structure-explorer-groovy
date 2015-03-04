package mobi.nowtechnologies.server.persistence.repository.social;

import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.social.GooglePlusUserInfo;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Created by oar on 2/7/14.
 */
public interface GooglePlusUserInfoRepository extends BaseSocialRepository<GooglePlusUserInfo> {

    @Query("select g from GooglePlusUserInfo g join g.user u join u.userGroup ug join ug.community c where (g.email=:contact or g.googlePlusId=:contact) and c=:community")
    public List<GooglePlusUserInfo> findByEmailOrSocialId(@Param("contact") String contact, @Param("community") Community community);
}
