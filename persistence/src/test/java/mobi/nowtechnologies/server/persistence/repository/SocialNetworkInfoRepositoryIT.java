package mobi.nowtechnologies.server.persistence.repository;

import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.CommunityFactory;
import mobi.nowtechnologies.server.social.domain.SocialNetworkInfo;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.shared.dto.OAuthProvider;
import mobi.nowtechnologies.server.social.SocialNetworkInfoRepository;

import javax.annotation.Resource;

import java.util.List;

import com.google.common.collect.Iterables;

import org.springframework.jdbc.core.JdbcTemplate;

import org.junit.*;
import static org.junit.Assert.*;

public class SocialNetworkInfoRepositoryIT extends AbstractRepositoryIT {

    @Resource(name = "userRepository")
    private UserRepository userRepository;

    @Resource
    private SocialNetworkInfoRepository socialNetworkInfoRepository;

    @Resource
    private JdbcTemplate jdbcTemplate;

    @Resource
    private CommunityRepository communityRepository;

    @Test
    public void testMapping() {
        SocialNetworkInfo fbUserInfo = new SocialNetworkInfo(OAuthProvider.FACEBOOK);
        fbUserInfo.setUserId(findUser().getId());
        fbUserInfo.setFirstName("AA");
        fbUserInfo.setSocialNetworkId("ID");
        fbUserInfo.setUserName("userName");
        fbUserInfo.setEmail("AA@ukr.net");
        socialNetworkInfoRepository.saveAndFlush(fbUserInfo);
        assertEquals(1, jdbcTemplate.queryForInt("select count(*) from social_network_info"));
    }

    @Test
    public void testFindByEmailOrSocialId() throws Exception {
        final User user = findUser();

        SocialNetworkInfo fbUserInfo = new SocialNetworkInfo(OAuthProvider.FACEBOOK);
        fbUserInfo.setUserId(user.getId());
        fbUserInfo.setFirstName("BB");
        fbUserInfo.setSocialNetworkId("ID0");
        fbUserInfo.setUserName("userName");
        fbUserInfo.setEmail("BB@ukr.net");
        socialNetworkInfoRepository.saveAndFlush(fbUserInfo);

        List<SocialNetworkInfo> foundBySocialId = socialNetworkInfoRepository.findByEmailOrSocialId("ID0", user.getUserGroup().getCommunity(), OAuthProvider.FACEBOOK);
        assertEquals(1, foundBySocialId.size());
        assertEquals("ID0", foundBySocialId.get(0).getSocialNetworkId());


        List<SocialNetworkInfo> foundByEmail = socialNetworkInfoRepository.findByEmailOrSocialId("BB@ukr.net", user.getUserGroup().getCommunity(), OAuthProvider.FACEBOOK);
        assertEquals(1, foundByEmail.size());
        assertEquals("BB@ukr.net", foundBySocialId.get(0).getEmail());

        Community unknownCommunity = communityRepository.saveAndFlush(CommunityFactory.createCommunity("unknown"));
        List<SocialNetworkInfo> foundByWrongCommunity = socialNetworkInfoRepository.findByEmailOrSocialId("BB@ukr.net", unknownCommunity, OAuthProvider.FACEBOOK);
        assertTrue(foundByWrongCommunity.isEmpty());
    }

    private User findUser() {
        String phoneNumber = "+64279000456";
        List<User> list = userRepository.findByMobile(phoneNumber);
        return Iterables.getFirst(list, null);
    }

}
