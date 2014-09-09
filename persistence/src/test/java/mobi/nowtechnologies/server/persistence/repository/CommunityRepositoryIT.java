package mobi.nowtechnologies.server.persistence.repository;

import mobi.nowtechnologies.server.persistence.domain.Community;
import org.junit.Test;

import javax.annotation.Resource;

import static org.junit.Assert.assertNotNull;

/**
 * Created by Oleg Artomov on 9/9/2014.
 */
public class CommunityRepositoryIT extends AbstractRepositoryIT {

    @Resource
    private CommunityRepository communityRepository;


    @Test
    public void testFindByRewriteUrlParameter() {
        Community community = communityRepository.findByRewriteUrlParameter("mtv1");
        assertNotNull(community);
    }
}
