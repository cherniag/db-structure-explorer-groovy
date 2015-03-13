package mobi.nowtechnologies.server.persistence.repository.behavior;

import mobi.nowtechnologies.server.persistence.domain.behavior.BehaviorConfig;
import mobi.nowtechnologies.server.persistence.domain.behavior.BehaviorConfigType;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BehaviorConfigRepository extends JpaRepository<BehaviorConfig, Long> {

    @Query("select cfg from BehaviorConfig cfg where cfg.communityId=:communityId and cfg.type=:type")
    BehaviorConfig findByCommunityIdAndBehaviorConfigType(@Param("communityId") int communityId, @Param("type") BehaviorConfigType behaviorConfigType);

    @Query("select distinct(cfg.type) from BehaviorConfig cfg where cfg.communityId=:communityId")
    List<BehaviorConfigType> findBehaviorConfigTypesForCommunity(@Param("communityId") int communityId);
}
