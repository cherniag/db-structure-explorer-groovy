package mobi.nowtechnologies.server.persistence.repository.behavior;

import mobi.nowtechnologies.server.persistence.domain.UserStatusType;
import mobi.nowtechnologies.server.persistence.domain.behavior.BehaviorConfig;
import mobi.nowtechnologies.server.persistence.domain.behavior.ChartUserStatusBehavior;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ChartUserStatusBehaviorRepository extends JpaRepository<ChartUserStatusBehavior, Long> {

    @Query(
        "select cusb from ChartUserStatusBehavior cusb where cusb.chartId=:chartId and cusb.chartBehavior.behaviorConfig=:behaviorConfig and cusb.userStatusType=:userStatusType order by cusb.chartId")
    ChartUserStatusBehavior findByChartIdBehaviorConfigAndStatus(@Param("chartId") int chartId, @Param("behaviorConfig") BehaviorConfig behaviorConfig,
                                                                 @Param("userStatusType") UserStatusType userStatusType);

    @Query("select cusb from ChartUserStatusBehavior cusb where cusb.chartBehavior.behaviorConfig=:behaviorConfig and cusb.userStatusType in (:userStatusTypes) order by cusb.chartId")
    List<ChartUserStatusBehavior> findByBehaviorConfig(@Param("behaviorConfig") BehaviorConfig behaviorConfig, @Param("userStatusTypes") Collection<UserStatusType> userStatusTypes);
}
