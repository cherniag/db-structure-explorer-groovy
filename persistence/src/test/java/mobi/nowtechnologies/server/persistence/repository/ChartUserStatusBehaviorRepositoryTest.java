package mobi.nowtechnologies.server.persistence.repository;

import com.google.common.collect.Lists;
import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.UserStatusType;
import mobi.nowtechnologies.server.persistence.domain.behavior.BehaviorConfig;
import mobi.nowtechnologies.server.persistence.domain.behavior.BehaviorConfigType;
import mobi.nowtechnologies.server.persistence.domain.behavior.ChartUserStatusBehavior;
import mobi.nowtechnologies.server.persistence.repository.behavior.BehaviorConfigRepository;
import mobi.nowtechnologies.server.persistence.repository.behavior.ChartUserStatusBehaviorRepository;
import org.junit.Test;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.util.List;

public class ChartUserStatusBehaviorRepositoryTest extends AbstractRepositoryIT {
    @Resource
    ChartUserStatusBehaviorRepository chartUserStatusBehaviorRepository;
    @Resource
    BehaviorConfigRepository behaviorConfigRepository;
    @Resource
    CommunityRepository communityRepository;

    @Test
    public void testFindByBehaviorConfig() throws Exception {
        Community community = communityRepository.findByRewriteUrlParameter("hl_uk");
        BehaviorConfig behaviorConfig = behaviorConfigRepository.findByCommunityIdAndBehaviorConfigType(community.getId(), BehaviorConfigType.FREEMIUM);
        List<ChartUserStatusBehavior> chartUserStatusBehaviors = chartUserStatusBehaviorRepository.findByBehaviorConfig(behaviorConfig, Lists.newArrayList(UserStatusType.FREE_TRIAL));

        for (ChartUserStatusBehavior chartUserStatusBehavior : chartUserStatusBehaviors) {
            Assert.isTrue(chartUserStatusBehavior.getUserStatusType() != UserStatusType.LIMITED);
            Assert.isTrue(chartUserStatusBehavior.getUserStatusType() != UserStatusType.SUBSCRIBED);
        }
    }
}