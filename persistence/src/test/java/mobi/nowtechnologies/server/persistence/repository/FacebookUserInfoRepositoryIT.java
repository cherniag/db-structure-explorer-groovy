package mobi.nowtechnologies.server.persistence.repository;

import com.google.common.collect.Iterables;
import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.CommunityFactory;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.social.FacebookUserInfo;
import mobi.nowtechnologies.server.persistence.repository.social.FacebookUserInfoRepository;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by oar on 2/10/14.
 */
public class FacebookUserInfoRepositoryIT extends AbstractRepositoryIT {
    @Resource(name = "userRepository")
    private UserRepository userRepository;

    @Resource
    private FacebookUserInfoRepository facebookUserInfoRepository;

    @Resource(name = "persistence.DataSource")
    private DataSource dataSource;

    @Resource
    private CommunityRepository communityRepository;

    @Test
    public void testMapping() {
        FacebookUserInfo fbUserInfo = new FacebookUserInfo();
        fbUserInfo.setUser(findUser());
        fbUserInfo.setFirstName("AA");
        fbUserInfo.setFacebookId("ID");
        fbUserInfo.setUserName("userName");
        fbUserInfo.setEmail("AA@ukr.net");
        facebookUserInfoRepository.saveAndFlush(fbUserInfo);
        JdbcTemplate template = new JdbcTemplate(dataSource);
        assertEquals(1, template.queryForInt("select count(*) from social_info"));
        assertEquals(1, template.queryForInt("select count(*) from facebook_user_info"));
    }

    @Test
    public void testFindByEmailOrSocialId() throws Exception {
        final User user = findUser();

        FacebookUserInfo fbUserInfo = new FacebookUserInfo();
        fbUserInfo.setUser(user);
        fbUserInfo.setFirstName("BB");
        fbUserInfo.setFacebookId("ID0");
        fbUserInfo.setUserName("userName");
        fbUserInfo.setEmail("BB@ukr.net");
        facebookUserInfoRepository.saveAndFlush(fbUserInfo);

        List<FacebookUserInfo> foundBySocialId = facebookUserInfoRepository.findByEmailOrSocialId("ID0", user.getUserGroup().getCommunity());
        assertEquals(1, foundBySocialId.size());
        assertEquals("ID0", foundBySocialId.get(0).getSocialId());


        List<FacebookUserInfo> foundByEmail = facebookUserInfoRepository.findByEmailOrSocialId("BB@ukr.net", user.getUserGroup().getCommunity());
        assertEquals(1, foundByEmail.size());
        assertEquals("BB@ukr.net", foundBySocialId.get(0).getEmail());

        Community unknownCommunity = communityRepository.saveAndFlush(CommunityFactory.createCommunity("unknown"));
        List<FacebookUserInfo> foundByWrongCommunity = facebookUserInfoRepository.findByEmailOrSocialId("BB@ukr.net", unknownCommunity);
        assertTrue(foundByWrongCommunity.isEmpty());
    }

    private User findUser() {
        String phoneNumber = "+64279000456";
        List<User> list = userRepository.findByMobile(phoneNumber);
        return Iterables.getFirst(list, null);
    }

}
