package mobi.nowtechnologies.server.persistence.social;

import mobi.nowtechnologies.server.persistence.repository.AbstractRepositoryIT;
import mobi.nowtechnologies.server.persistence.social.SocialNetworkInfo;
import mobi.nowtechnologies.server.persistence.social.SocialNetworkInfoRepository;
import mobi.nowtechnologies.server.persistence.social.SocialNetworkType;

import javax.annotation.Resource;

import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;

import org.junit.*;
import static org.junit.Assert.*;

public class SocialNetworkInfoRepositoryIT extends AbstractRepositoryIT {

    @Resource
    private SocialNetworkInfoRepository socialNetworkInfoRepository;

    @Resource
    private JdbcTemplate jdbcTemplate;

    @Test
    public void testMapping() {
        SocialNetworkInfo fbUserInfo = new SocialNetworkInfo(SocialNetworkType.FACEBOOK);
        fbUserInfo.setUserId(106);
        fbUserInfo.setFirstName("AA");
        fbUserInfo.setSocialNetworkId("ID");
        fbUserInfo.setUserName("userName");
        fbUserInfo.setEmail("AA@ukr.net");
        socialNetworkInfoRepository.saveAndFlush(fbUserInfo);
        Number number = jdbcTemplate.queryForObject("select count(*) from social_network_info", Long.class);
        assertEquals(1, number != null ?
                        number.longValue() :
                        0);
    }

    @Test
    public void testFindByEmailOrSocialId() throws Exception {
        SocialNetworkInfo fbUserInfo = new SocialNetworkInfo(SocialNetworkType.FACEBOOK);
        fbUserInfo.setUserId(106);
        fbUserInfo.setFirstName("BB");
        fbUserInfo.setSocialNetworkId("ID0");
        fbUserInfo.setUserName("userName");
        fbUserInfo.setEmail("BB@ukr.net");
        socialNetworkInfoRepository.saveAndFlush(fbUserInfo);

        List<SocialNetworkInfo> foundBySocialId = socialNetworkInfoRepository.findByEmailOrSocialId("ID0", 8, SocialNetworkType.FACEBOOK);
        assertEquals(1, foundBySocialId.size());
        assertEquals("ID0", foundBySocialId.get(0).getSocialNetworkId());


        List<SocialNetworkInfo> foundByEmail = socialNetworkInfoRepository.findByEmailOrSocialId("BB@ukr.net", 8, SocialNetworkType.FACEBOOK);
        assertEquals(1, foundByEmail.size());
        assertEquals("BB@ukr.net", foundBySocialId.get(0).getEmail());

        List<SocialNetworkInfo> foundByWrongCommunity = socialNetworkInfoRepository.findByEmailOrSocialId("BB@ukr.net", -10, SocialNetworkType.FACEBOOK);
        assertTrue(foundByWrongCommunity.isEmpty());
    }

}
