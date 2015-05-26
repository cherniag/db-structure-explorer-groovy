package mobi.nowtechnologies.server.persistence.repository;

import mobi.nowtechnologies.server.persistence.domain.Community;

import javax.annotation.Resource;

import java.util.Arrays;
import java.util.List;

import org.junit.*;
import static org.junit.Assert.*;

import static org.hamcrest.core.Is.is;

// @author Oleg Artomov on 9/9/2014.
public class CommunityRepositoryIT extends AbstractRepositoryIT {
    @Resource
    CommunityRepository communityRepository;

    List<String> liveCommunitiesInInitScript = Arrays.asList("o2", "vf_nz", "hl_uk", "mtv1", "mtvnz");

    @Test
    public void testFindByRewriteUrlParameter() {
        Community community = communityRepository.findByRewriteUrlParameter("mtv1");
        assertNotNull(community);
    }

    @Test
    public void shouldFindLiveCommunities() {
        //when
        List<Community> communities = communityRepository.findByLive(true);

        //then
        assertThat(communities.size(), is(5));

        for (Community community : communities) {
            assertTrue(liveCommunitiesInInitScript.contains(community.getRewriteUrlParameter()));
        }
    }

    @Test
    public void shouldFindDeadCommunities() {
        //when
        List<Community> communities = communityRepository.findByLive(false);

        for (Community community : communities) {
            assertFalse(liveCommunitiesInInitScript.contains(community.getRewriteUrlParameter()));
        }
    }
}
