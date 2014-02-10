package mobi.nowtechnologies.server.persistence.repository;

import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.UserGroup;
import org.junit.Test;

import javax.annotation.Resource;

import static junit.framework.Assert.assertNotNull;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * User: Titov Mykhaylo (titov)
 * 17.10.13 10:30
 */

public class UserGroupRepositoryIT  extends AbstractRepositoryIT{

    @Resource(name = "userGroupRepository")
    private UserGroupRepository userGroupRepository;

    @Resource(name = "communityRepository")
    private CommunityRepository communityRepository;

    @Test
    public void shouldFindByCommunityRewriteUrl(){
        //given
        Community community = communityRepository.save(new Community().withRewriteUrl("g").withName("g"));
        UserGroup userGroup = userGroupRepository.save(new UserGroup().withCommunity(community));

        //when
        UserGroup actualUserGroup = userGroupRepository.findByCommunityRewriteUrl(community.getRewriteUrlParameter());

        //then
        assertNotNull(actualUserGroup);
        assertThat(actualUserGroup.getId(), is(userGroup.getId()));
    }
}
