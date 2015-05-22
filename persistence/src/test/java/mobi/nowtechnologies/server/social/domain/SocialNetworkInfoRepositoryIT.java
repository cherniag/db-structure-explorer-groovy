package mobi.nowtechnologies.server.social.domain;

import mobi.nowtechnologies.server.persistence.repository.AbstractRepositoryIT;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import java.util.List;

import org.junit.*;
import static org.junit.Assert.*;

public class SocialNetworkInfoRepositoryIT extends AbstractRepositoryIT {

    @Resource
    private SocialNetworkInfoRepository socialNetworkInfoRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    public void testMapping() {
        SocialNetworkInfo fbUserInfo = new SocialNetworkInfo(SocialNetworkType.FACEBOOK);
        fbUserInfo.setUserId(106);
        fbUserInfo.setFirstName("AA");
        fbUserInfo.setSocialNetworkId("ID");
        fbUserInfo.setUserName("userName");
        fbUserInfo.setEmail("AA@ukr.net");
        socialNetworkInfoRepository.saveAndFlush(fbUserInfo);
        Long count = entityManager.createQuery("select count(sni) from SocialNetworkInfo sni", Long.class).getSingleResult();
        assertEquals(1, count.longValue());
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
