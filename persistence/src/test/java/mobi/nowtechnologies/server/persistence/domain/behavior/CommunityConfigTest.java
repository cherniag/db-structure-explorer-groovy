package mobi.nowtechnologies.server.persistence.domain.behavior;

import org.junit.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class CommunityConfigTest {

    @Test
    public void testRequiresBehaviorConfigChange() throws Exception {
        CommunityConfig communityConfigDefFree = createCommunityConfig(BehaviorConfigType.DEFAULT);
        assertTrue(communityConfigDefFree.requiresBehaviorConfigChange(BehaviorConfigType.FREEMIUM));

        CommunityConfig communityConfigFreeDef = createCommunityConfig(BehaviorConfigType.FREEMIUM);
        assertTrue(communityConfigFreeDef.requiresBehaviorConfigChange(BehaviorConfigType.DEFAULT));

        CommunityConfig communityConfigDefDef = createCommunityConfig(BehaviorConfigType.DEFAULT);
        assertFalse(communityConfigDefDef.requiresBehaviorConfigChange(BehaviorConfigType.DEFAULT));

        CommunityConfig communityConfigFreeFree = createCommunityConfig(BehaviorConfigType.FREEMIUM);
        assertFalse(communityConfigFreeFree.requiresBehaviorConfigChange(BehaviorConfigType.FREEMIUM));
    }

    private CommunityConfig createCommunityConfig(BehaviorConfigType configType) {
        BehaviorConfig behaveConfig = mock(BehaviorConfig.class);
        when(behaveConfig.getType()).thenReturn(configType);

        CommunityConfig communityConfig = new CommunityConfig();
        communityConfig.setBehaviorConfig(behaveConfig);
        return communityConfig;
    }
}