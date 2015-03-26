package mobi.nowtechnologies.server.social;

import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.shared.dto.OAuthProvider;
import mobi.nowtechnologies.server.social.domain.SocialNetworkInfo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SocialNetworkInfoRepository extends JpaRepository<SocialNetworkInfo, Long> {

    SocialNetworkInfo findByUserIdAndSocialNetworkType(Integer userId, OAuthProvider socialNetworkType);

    @Query(value = "select sni from SocialNetworkInfo sni where sni.userId=?1 order by sni.socialNetworkType")
    List<SocialNetworkInfo> findByUserId(Integer userId);

    @Modifying
    @Query(value = "delete from SocialNetworkInfo sni where sni.userId=?1")
    int deleteByUserId(Integer userId);

    @Query("select sni from SocialNetworkInfo sni, User u join u.userGroup ug join ug.community c where u.id = sni.userId and (sni.email=:contact or sni.socialNetworkId=:contact) and c=:community and sni.socialNetworkType=:socialNetworkType")
    List<SocialNetworkInfo> findByEmailOrSocialId(@Param("contact") String contact, @Param("community") Community community, @Param("socialNetworkType") OAuthProvider socialNetworkType);
}
