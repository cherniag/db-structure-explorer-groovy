package mobi.nowtechnologies.server.persistence.repository;

import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.SocialNetworkInfo;
import mobi.nowtechnologies.server.shared.dto.OAuthProvider;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SocialNetworkInfoRepository extends JpaRepository<SocialNetworkInfo, Long> {

    SocialNetworkInfo findByUserAndSocialNetwork(User user, OAuthProvider socialNetwork);

    @Modifying
    @Query(value = "delete from SocialNetworkInfo sni where sni.user=?1")
    int deleteByUser(User user);

    @Query("select sni from SocialNetworkInfo sni join sni.user u join u.userGroup ug join ug.community c where (sni.email=:contact or sni.socialNetworkId=:contact) and c=:community and sni.socialNetwork=:socialNetwork")
    public List<SocialNetworkInfo> findByEmailOrSocialId(@Param("contact") String contact, @Param("community") Community community, @Param("socialNetwork") OAuthProvider socialNetwork);
}
