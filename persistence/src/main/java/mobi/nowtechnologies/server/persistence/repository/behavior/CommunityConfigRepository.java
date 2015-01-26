package mobi.nowtechnologies.server.persistence.repository.behavior;

import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.behavior.CommunityConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CommunityConfigRepository extends JpaRepository<CommunityConfig, Community> {

    @Query("select cfg from CommunityConfig cfg where cfg.community=:community")
    CommunityConfig findByCommunity(@Param("community") Community community);

}
