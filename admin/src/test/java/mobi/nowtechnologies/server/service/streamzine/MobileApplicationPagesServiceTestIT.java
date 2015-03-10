package mobi.nowtechnologies.server.service.streamzine;

import mobi.nowtechnologies.server.admin.controller.AbstractAdminITTest;

import javax.annotation.Resource;

import java.util.Set;

import org.junit.*;
import static org.junit.Assert.*;

public class MobileApplicationPagesServiceTestIT extends AbstractAdminITTest {

    @Resource
    MobileApplicationPagesService mobileApplicationPagesService;

    @Test
    public void getActionsForCommunity() throws Exception {
        final String specifiedCommunityUrl = "mtv1";

        Set<String> actions = mobileApplicationPagesService.getActions(specifiedCommunityUrl);

        assertEquals(3, actions.size());
        assertTrue(actions.contains("refer_a_friend"));
        assertTrue(actions.contains("openbrowser"));
        assertTrue(actions.contains("openadds"));
    }

    @Test
    public void getActionsForNotExistingCommunity() throws Exception {
        final String notExistingCommunityUrl = "i am not exist";

        Set<String> actions = mobileApplicationPagesService.getActions(notExistingCommunityUrl);

        assertEquals(0, actions.size());
    }

    @Test
    public void getPagesForCommunity() throws Exception {
        final String specifiedCommunityUrl = "mtv1";

        Set<String> pages = mobileApplicationPagesService.getPages(specifiedCommunityUrl);

        assertEquals(8, pages.size());
        assertTrue(pages.contains("playlists"));
        assertTrue(pages.contains("tracks"));
    }

    @Test
    public void getPagesForNotExistingCommunity() throws Exception {
        final String notExistingCommunityUrl = "i am not exist";

        Set<String> pages = mobileApplicationPagesService.getPages(notExistingCommunityUrl);

        assertEquals(0, pages.size());
    }

}