package mobi.nowtechnologies.server.service.o2.impl;

import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.UserFactory;
import mobi.nowtechnologies.server.persistence.repository.UserRepository;
import mobi.nowtechnologies.server.service.UserService;
import mobi.nowtechnologies.server.shared.enums.ActivationStatus;

import java.util.Arrays;

import org.junit.*;
import org.mockito.*;
import static org.mockito.Mockito.*;

/**
 * User: Alexsandr_Kolpakov Date: 10/21/13 Time: 9:37 AM
 */
public class O2UserDetailsUpdaterTest {

    private O2UserDetailsUpdater fixture = new O2UserDetailsUpdater();

    @Mock
    private UserService userServiceMock;

    @Mock
    private UserRepository userRepository;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        fixture.setUserService(userServiceMock);
        fixture.setUserRepository(userRepository);
    }

    @Test
    public void testProcess_Success() throws Exception {
        User user1 = UserFactory.createUser(ActivationStatus.ACTIVATED);
        User user2 = UserFactory.createUser(ActivationStatus.ACTIVATED);

        O2SubscriberData data = new O2SubscriberData();
        data.setPhoneNumber("+642111111111");
        user1.setMobile(data.getPhoneNumber());
        user2.setMobile(data.getPhoneNumber());

        Mockito.doNothing().when(userServiceMock).populateSubscriberData(user1, data);
        Mockito.doNothing().when(userServiceMock).populateSubscriberData(user2, data);
        Mockito.doReturn(Arrays.asList(user1, user2)).when(userRepository).findByMobile(data.getPhoneNumber());

        fixture.process(data);

        verify(userServiceMock, times(1)).populateSubscriberData(user1, data);
        verify(userServiceMock, times(1)).populateSubscriberData(user2, data);
        verify(userRepository, times(1)).findByMobile(data.getPhoneNumber());
    }
}
