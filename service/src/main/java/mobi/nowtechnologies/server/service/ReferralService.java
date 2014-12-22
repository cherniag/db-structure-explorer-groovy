package mobi.nowtechnologies.server.service;

import com.google.common.collect.Lists;
import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.referral.Referral;
import mobi.nowtechnologies.server.persistence.domain.referral.ReferralState;
import mobi.nowtechnologies.server.persistence.domain.social.SocialInfo;
import mobi.nowtechnologies.server.persistence.repository.CommunityRepository;
import mobi.nowtechnologies.server.persistence.repository.ReferralRepository;
import mobi.nowtechnologies.server.persistence.repository.social.FacebookUserInfoRepository;
import mobi.nowtechnologies.server.persistence.repository.social.GooglePlusUserInfoRepository;
import mobi.nowtechnologies.server.shared.enums.ProviderType;
import mobi.nowtechnologies.server.shared.message.CommunityResourceBundleMessageSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
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

    @Resource(name = "serviceMessageSource")
    private CommunityResourceBundleMessageSource messageSource;

    private String requiredPropertyName;

    public void setRequiredPropertyName(String requiredPropertyName) {
        this.requiredPropertyName = requiredPropertyName;
    }

    @Resource
    private CommunityRepository communityRepository;
    
    //
    //
    // API
    public int getRequiredReferralsCount(String community) {
        return messageSource.readInt(community, requiredPropertyName, 5, null);
    }

    @Transactional(readOnly = true)
    public int getActivatedReferralsCount(User user) {
        return referralRepository.getCountByCommunityIdUserIdAndStates(user.getCommunityId(), user.getId(), Arrays.asList(ReferralState.ACTIVATED));
    }

    @Transactional
    public void refer(List<Referral> referrals) {
        logger.info("Trying to save referrals: [{}]", referrals);
        for (Referral referral : referrals) {
            Community community = communityRepository.findOne(referral.getCommunityId());
            if(exists(referral, community)){
                logger.info("Skipped to save referral", referral);
            } else {
                referralRepository.saveAndFlush(referral);
            }
        }
    }

    public void acknowledge(User user, String email) {
        logger.info("Acknowledge referrals for user id: [{]] and email: [{1}]" + user.getId(), email);

        referralRepository.updateReferrals(Lists.newArrayList(email), user.getUserGroup().getCommunity().getId(), ReferralState.ACTIVATED, ReferralState.PENDING);
    }

    public void acknowledge(User user, SocialInfo socialInfo) {
        List<String> contacts = getContacts(socialInfo);

        logger.info("Acknowledge referrals for user id: [{]] and contacts: [{1}]" + user.getId(), contacts);

        referralRepository.updateReferrals(contacts, user.getUserGroup().getCommunity().getId(), ReferralState.ACTIVATED, ReferralState.PENDING);
    }

    //
    // Internals
    //
    private boolean exists(Referral referral, Community community) {
        Referral existing = referralRepository.findByContactAndUserId(referral.getContact(), referral.getUserId());
        if(existing != null) {
            return true;
        }

        final ProviderType providerType = referral.getProviderType();
        final String contact = referral.getContact();

        if(providerType == ProviderType.EMAIL) {
            User byContact = userService.findByName(contact);
            return byContact != null && byContact.getCommunityId().equals(community.getId());
        }
        if(providerType == ProviderType.FACEBOOK) {
            return !facebookUserInfoRepository.findByEmailOrSocialId(contact, community).isEmpty();
        }
        if(providerType == ProviderType.GOOGLE_PLUS) {
            return !googlePlusUserInfoRepository.findByEmailOrSocialId(contact, community).isEmpty();
        }
        throw new IllegalArgumentException("Not supported type: " + providerType + " to find by " + contact);
    }

    private ArrayList<String> getContacts(SocialInfo socialInfo) {
        return Lists.newArrayList(socialInfo.getSocialId(), socialInfo.getEmail());
    }
}
