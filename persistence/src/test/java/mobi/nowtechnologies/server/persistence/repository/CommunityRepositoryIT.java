package mobi.nowtechnologies.server.persistence.repository;

import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.repository.behavior.CommunityConfigRepository;

import javax.annotation.Resource;

import java.util.List;

import org.junit.*;
import static org.junit.Assert.*;

import static org.hamcrest.core.Is.is;

// @author Oleg Artomov on 9/9/2014.
public class CommunityRepositoryIT extends AbstractRepositoryIT {

    @Resource
    OfferRepository offerRepository;
    @Resource
    PaymentPolicyRepository paymentPolicyRepository;
    @Resource
    MessageRepository messageRepository;
    @Resource
    DrmRepository drmRepository;
    @Resource
    PaymentDetailsRepository paymentDetailsRepository;
    @Resource
    UserRepository userRepository;
    @Resource
    PromoCodeRepository promoCodeRepository;
    @Resource
    PromotionRepository promotionRepository;
    @Resource
    UserGroupRepository userGroupRepository;
    @Resource
    ChartDetailRepository chartDetailRepository;
    @Resource
    ChartRepository chartRepository;
    @Resource
    PromotedDeviceRepository promotedDeviceRepository;
    @Resource
    DrmPolicyRepository drmPolicyRepository;
    @Resource
    CommunityRepository communityRepository;
    @Resource
    CommunityConfigRepository communityConfigRepository;

    @Test
    public void testFindByRewriteUrlParameter() {
        Community community = communityRepository.findByRewriteUrlParameter("mtv1");
        assertNotNull(community);
    }

    @Test
    public void shouldFindLiveCommunities() {
        //given
        boolean isLive = true;

        paymentPolicyRepository.deleteAll();
        messageRepository.deleteAll();
        chartDetailRepository.deleteAll();
        drmRepository.deleteAll();
        offerRepository.deleteAll();
        paymentDetailsRepository.deleteAll();
        userRepository.deleteAll();
        promoCodeRepository.deleteAll();
        promotionRepository.deleteAll();
        userGroupRepository.deleteAll();
        chartRepository.deleteAll();
        promotedDeviceRepository.deleteAll();
        drmPolicyRepository.deleteAll();
        communityConfigRepository.deleteAll();
        communityRepository.deleteAll();

        Community community1 = communityRepository.save(new Community().withRewriteUrl("1").withName("1").withLive(true));
        Community community2 = communityRepository.save(new Community().withRewriteUrl("2").withName("2").withLive(true));
        communityRepository.save(new Community().withRewriteUrl("3").withName("3").withLive(false));

        //when
        List<Community> communities = communityRepository.findByLive(isLive);

        //then
        assertThat(communities.size(), is(2));
        assertThat(communities.get(0).getId(), is(community1.getId()));
        assertThat(communities.get(1).getId(), is(community2.getId()));
    }

    @Test
    public void shouldFindDeadCommunities() {
        //given
        boolean isLive = false;

        paymentPolicyRepository.deleteAll();
        messageRepository.deleteAll();
        chartDetailRepository.deleteAll();
        drmRepository.deleteAll();
        offerRepository.deleteAll();
        paymentDetailsRepository.deleteAll();
        userRepository.deleteAll();
        promoCodeRepository.deleteAll();
        promotionRepository.deleteAll();
        userGroupRepository.deleteAll();
        chartRepository.deleteAll();
        promotedDeviceRepository.deleteAll();
        drmPolicyRepository.deleteAll();
        communityConfigRepository.deleteAll();
        communityRepository.deleteAll();

        Community community1 = communityRepository.save(new Community().withRewriteUrl("1").withName("1").withLive(false));
        Community community2 = communityRepository.save(new Community().withRewriteUrl("2").withName("2").withLive(false));
        communityRepository.save(new Community().withRewriteUrl("3").withName("3").withLive(true));

        //when
        List<Community> communities = communityRepository.findByLive(isLive);

        //then
        assertThat(communities.size(), is(2));
        assertThat(communities.get(0).getId(), is(community1.getId()));
        assertThat(communities.get(1).getId(), is(community2.getId()));
    }
}
