package mobi.nowtechnologies.server.service.o2.impl;

import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.UserFactory;
import mobi.nowtechnologies.server.service.UserService;
import mobi.nowtechnologies.server.shared.enums.ActivationStatus;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Arrays;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * User: Alexsandr_Kolpakov
 * Date: 10/21/13
 * Time: 9:37 AM
 */
@RunWith(PowerMockRunner.class)
public class O2UserDetailsUpdaterTest {
    private O2UserDetailsUpdater fixture;

    @Mock
    private UserService userServiceMock;

    @Before
    public void setUp() throws Exception {
        fixture = new O2UserDetailsUpdater();
        fixture.setUserService(userServiceMock);
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
        Mockito.doReturn(Arrays.asList(user1, user2)).when(userServiceMock).findByMobile(data.getPhoneNumber());

        fixture.process(data);

        verify(userServiceMock, times(1)).populateSubscriberData(user1, data);
        verify(userServiceMock, times(1)).populateSubscriberData(user2, data);
        verify(userServiceMock, times(1)).findByMobile(data.getPhoneNumber());
    }
}
