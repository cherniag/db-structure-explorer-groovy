package mobi.nowtechnologies.server.service;

import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.referral.Referral;
import mobi.nowtechnologies.server.persistence.domain.referral.ReferralState;
import mobi.nowtechnologies.server.persistence.domain.referral.UserReferralsSnapshot;
import mobi.nowtechnologies.server.persistence.domain.social.SocialInfo;
import mobi.nowtechnologies.server.persistence.repository.CommunityRepository;
import mobi.nowtechnologies.server.persistence.repository.ReferralRepository;
import mobi.nowtechnologies.server.persistence.repository.UserReferralsSnapshotRepository;
import mobi.nowtechnologies.server.persistence.repository.social.FacebookUserInfoRepository;
import mobi.nowtechnologies.server.persistence.repository.social.GooglePlusUserInfoRepository;
import mobi.nowtechnologies.server.shared.enums.ProviderType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Author: Gennadii Cherniaiev
 * Date: 11/21/2014
 */
public class ReferralService {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    private ReferralRepository referralRepository;

    @Resource(name = "service.UserService")
    private UserService userService;

    @Resource
    private GooglePlusUserInfoRepository googlePlusUserInfoRepository;

    @Resource
    private FacebookUserInfoRepository facebookUserInfoRepository;

    @Resource
    private UserReferralsSnapshotRepository userReferralsSnapshotRepository;

    @Resource
    private CommunityRepository communityRepository;

    @Transactional
    public void refer(List<Referral> referrals) {
        logger.info("Trying to save referrals: [{}]", referrals);
        for (Referral referral : referrals) {
            Community community = communityRepository.findOne(referral.getCommunityId());
            if (exists(referral, community)) {
                logger.info("Skipped to save referral", referral);
            } else {
                referralRepository.saveAndFlush(referral);
            }
        }
    }

    @Transactional
    public void acknowledge(User user, String email) {
        doAck(user, Arrays.asList(email));
    }

    @Transactional
    public void acknowledge(User user, SocialInfo socialInfo) {
        doAck(user, getContacts(socialInfo));
    }

    private void doAck(User promoUser, List<String> contacts) {
        logger.info("Acknowledge referrals for user id: [{]] and contacts: [{}]", promoUser.getId(), contacts);

        final Integer communityId = promoUser.getUserGroup().getCommunity().getId();

        referralRepository.updateReferrals(contacts, communityId, ReferralState.ACTIVATED, ReferralState.PENDING);
        List<Integer> referralUserIds = referralRepository.findReferralUserIdsByContacts(communityId, contacts);

        List<UserReferralsSnapshot> snapshots = userReferralsSnapshotRepository.findAll(referralUserIds);
        for (UserReferralsSnapshot snapshot : snapshots) {
            int referredAndConfirmedCount = referralRepository.getCountByCommunityIdUserIdAndStates(communityId, snapshot.getUserId(), ReferralState.ACTIVATED);

            logger.info("trying to update matchesData in snapshotId={} for communityId={}, userId={} with currentReferrals={}",
                    snapshot.getUserId(), communityId, snapshot.getUserId(), referredAndConfirmedCount);

            snapshot.updateMatchesData(referredAndConfirmedCount, new Date());
        }
    }

    //
    // Internals
    //
    private boolean exists(Referral referral, Community community) {
        Referral existing = referralRepository.findByContactAndUserId(referral.getContact(), referral.getUserId());
        if (existing != null) {
            return true;
        }

        final ProviderType providerType = referral.getProviderType();
        final String contact = referral.getContact();

        if (providerType == ProviderType.EMAIL) {
            User byContact = userService.findByName(contact);
            return byContact != null && byContact.getCommunityId().equals(community.getId());
        }
        if (providerType == ProviderType.FACEBOOK) {
            return !facebookUserInfoRepository.findByEmailOrSocialId(contact, community).isEmpty();
        }
        if (providerType == ProviderType.GOOGLE_PLUS) {
            return !googlePlusUserInfoRepository.findByEmailOrSocialId(contact, community).isEmpty();
        }
        throw new IllegalArgumentException("Not supported type: " + providerType + " to find by " + contact);
    }

    private List<String> getContacts(SocialInfo socialInfo) {
        return Arrays.asList(socialInfo.getSocialId(), socialInfo.getEmail());
    }
}