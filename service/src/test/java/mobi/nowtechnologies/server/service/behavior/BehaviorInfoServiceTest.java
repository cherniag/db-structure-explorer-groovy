package mobi.nowtechnologies.server.service.behavior;

import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.Duration;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.UserGroup;
import mobi.nowtechnologies.server.persistence.domain.behavior.BehaviorConfig;
import mobi.nowtechnologies.server.persistence.domain.behavior.BehaviorConfigType;
import mobi.nowtechnologies.server.persistence.domain.behavior.CommunityConfig;
import mobi.nowtechnologies.server.persistence.domain.referral.ReferralState;
import mobi.nowtechnologies.server.persistence.repository.ReferralRepository;
import mobi.nowtechnologies.server.persistence.repository.UserReferralsSnapshotRepository;
import mobi.nowtechnologies.server.persistence.repository.behavior.CommunityConfigRepository;

import org.junit.*;
import org.mockito.*;
import static org.mockito.Mockito.*;

public class BehaviorInfoServiceTest {

    @Mock
    UserReferralsSnapshotRepository userReferralsSnapshotRepository;
    @Mock
    CommunityConfigRepository communityConfigRepository;
    @Mock
    ReferralRepository referralRepository;
    @InjectMocks
    BehaviorInfoService behaviorInfoService;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetUserReferralsSnapshotWhenFreemiumButNoRequiredReferralsCount() throws Exception {
        // given
        final int userId = 1;
        final int communityId = 2;
        final int activated = 2;
        User user = createUser("user", userId, communityId);

        BehaviorConfig freemiumBehaviorConfig = mock(BehaviorConfig.class);
        when(freemiumBehaviorConfig.getType()).thenReturn(BehaviorConfigType.FREEMIUM);
        when(freemiumBehaviorConfig.getCommunityId()).thenReturn(communityId);
        CommunityConfig communityConfig = mock(CommunityConfig.class);
        when(communityConfig.getBehaviorConfig()).thenReturn(freemiumBehaviorConfig);
        when(freemiumBehaviorConfig.getRequiredReferrals()).thenReturn(5);
        when(freemiumBehaviorConfig.getReferralsDuration()).thenReturn(mock(Duration.class));

        when(communityConfigRepository.findByCommunity(user.getUserGroup().getCommunity())).thenReturn(communityConfig);
        when(referralRepository.countByCommunityIdUserIdAndStates(communityId, userId, ReferralState.ACTIVATED)).thenReturn(activated);

        // when
        behaviorInfoService.getUserReferralsSnapshot(user, freemiumBehaviorConfig);

        // then
        verify(referralRepository, timeout(1)).countByCommunityIdUserIdAndStates(communityId, userId, ReferralState.ACTIVATED);
        verify(userReferralsSnapshotRepository).findOne(anyInt());
    }

    private User createUser(String name, int id, int communityId) {
        Community community = mock(Community.class);
        when(community.getId()).thenReturn(communityId);

        UserGroup userGroup = mock(UserGroup.class);
        when(userGroup.getCommunity()).thenReturn(community);

        User user = mock(User.class);
        when(user.getUserGroup()).thenReturn(userGroup);
        when(user.getId()).thenReturn(id);
        when(user.getUserName()).thenReturn(name);
        return user;
    }
}