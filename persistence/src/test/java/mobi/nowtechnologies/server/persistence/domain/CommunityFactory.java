package mobi.nowtechnologies.server.persistence.domain;

import static org.mockito.Mockito.*;

/**
 * @author Titov Mykhaylo (titov)
 */
public class CommunityFactory {

    public static Community createCommunity() {
        Community community = new Community();
        community.setId(1);
        community.setName("name");
        community.setDisplayName("displayName");
        community.setRewriteUrlParameter("nowtop40");
        return community;
    }

    public static Community createCommunity(String url) {
        Community community = new Community();
        community.setName("name");
        community.setDisplayName("displayName");
        community.setRewriteUrlParameter(url);
        return community;
    }

    public static Community createCommunityMock(int id, String rewriteUrl) {
        Community community = mock(Community.class);
        when(community.getId()).thenReturn(id);
        when(community.getRewriteUrlParameter()).thenReturn(rewriteUrl);
        return community;
    }
}