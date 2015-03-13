package mobi.nowtechnologies.server.job;

import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.CommunityFactory;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.UserFactory;
import mobi.nowtechnologies.server.persistence.domain.UserGroup;
import mobi.nowtechnologies.server.service.UserService;
import mobi.nowtechnologies.server.shared.enums.ActivationStatus;

import java.util.Collections;

import org.springframework.data.domain.Pageable;

import org.junit.*;
import org.junit.runner.*;
import org.mockito.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.*;

import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
public class Before48hExpirePSMSPaymentJobTest {

    @Mock
    private UserService mockUserService;

    private Before48hPSMSPaymentJob fixture;

    @Test
    public void testExecute_Successful() throws Exception {
        Community community = CommunityFactory.createCommunity();
        community.setRewriteUrlParameter("o2");

        UserGroup userGroup = new UserGroup();
        userGroup.setCommunity(community);

        User user = UserFactory.createUser(ActivationStatus.ACTIVATED);
        user.setUserGroup(userGroup);

        when(mockUserService.findBefore48hExpireUsers(anyInt(), any(Pageable.class))).thenReturn(Collections.singletonList(user));

        fixture.executeInternal(null);

        verify(mockUserService, times(1)).findBefore48hExpireUsers(anyInt(), any(Pageable.class));
    }

    @Before
    public void setUp() throws Exception {
        fixture = new Before48hPSMSPaymentJob();
        Before48hPSMSPaymentJob.userService = mockUserService;
    }
}