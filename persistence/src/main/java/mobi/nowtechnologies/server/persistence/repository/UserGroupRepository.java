package mobi.nowtechnologies.server.persistence.repository;

import mobi.nowtechnologies.server.persistence.domain.UserGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * User: Titov Mykhaylo (titov)
 * 17.10.13 10:20
 */
public interface UserGroupRepository extends JpaRepository<UserGroup, Byte> {

    @Query(value = "select uG from UserGroup uG " +
            "join uG.community c " +
            "where " +
            "c.rewriteUrlParameter = ?1")
    UserGroup findByCommunityRewriteUrl(String communityRewriteUrl);
}