package mobi.nowtechnologies.server.service.behavior;

import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.behavior.BehaviorConfig;
import mobi.nowtechnologies.server.persistence.domain.behavior.CommunityConfig;
import mobi.nowtechnologies.server.persistence.domain.referral.ReferralState;
import mobi.nowtechnologies.server.persistence.domain.referral.UserReferralsSnapshot;
import mobi.nowtechnologies.server.persistence.repository.ReferralRepository;
import mobi.nowtechnologies.server.persistence.repository.UserReferralsSnapshotRepository;
import mobi.nowtechnologies.server.persistence.repository.behavior.CommunityConfigRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

public class BehaviorInfoService {

    private Logger logger = LoggerFactory.getLogger(BehaviorInfoService.class);

    @Resource
    UserReferralsSnapshotRepository userReferralsSnapshotRepository;
    @Resource
    CommunityConfigRepository communityConfigRepository;
    @Resource
    ReferralRepository referralRepository;

    @Transactional(readOnly = true)
    public UserReferralsSnapshot getUserReferralsSnapshot(User user) {
        BehaviorConfig behaviorConfig = findConfig(user);

        boolean hasNoFreemium = behaviorConfig.getType().isDefault();
        if (hasNoFreemium) {
            logger.info("Has no Freemium enabled for user id {}", user.getId());
            return null;
        }

        int requiredReferrals = behaviorConfig.getRequiredReferrals();
        if (requiredReferrals <= 0) {
            logger.info("Has no positive referrals count for user id {}", user.getId());
            return null;
        }

        UserReferralsSnapshot existing = userReferralsSnapshotRepository.findOne(user.getId());
        if (existing == null) {
            UserReferralsSnapshot newOne = new UserReferralsSnapshot(user.getId(), requiredReferrals, behaviorConfig.getReferralsDuration());
            int referredAndConfirmedCount = referralRepository.getCountByCommunityIdUserIdAndStates(behaviorConfig.getCommunityId(), user.getId(), ReferralState.ACTIVATED);
            newOne.updateMatchesData(referredAndConfirmedCount);

            existing = userReferralsSnapshotRepository.saveAndFlush(newOne);
            logger.info("New user referrals snapshot {} created for user id {}", existing, user.getId());
        }

        return existing;
    }

    private BehaviorConfig findConfig(User user) {
        Community community = user.getUserGroup().getCommunity();
        CommunityConfig communityConfig = communityConfigRepository.findByCommunity(community);
        return communityConfig.getBehaviorConfig();
    }
}
