package mobi.nowtechnologies.server.service;

import mobi.nowtechnologies.server.persistence.domain.DeviceType;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.repository.UserRepository;
import mobi.nowtechnologies.server.service.exception.ServiceException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceMergeTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private AccountLogService accountLogService;

    @Mock
    private UserDeviceDetailsService userDeviceDetailsService;

    @Mock
    private DeviceUserDataService deviceUserDataService;

    @Mock
    private AppsFlyerDataService appsFlyerDataService;

    @InjectMocks
    private UserService userService;

    private ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);

    @Rule
    public final ExpectedException thrown = ExpectedException.none();


    @Before
    public void setUp() throws Exception {
        when(userRepository.save(userArgumentCaptor.capture())).thenAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                return userArgumentCaptor.getValue();
            }
        });
    }

    @Test
    public void mergeUsers() throws Exception {
        User oldUser = mock(User.class);
        User tempUser = mock(User.class);
        when(tempUser.getId()).thenReturn(987);
        when(tempUser.getDeviceUID()).thenReturn("deviceUID");
        DeviceType deviceType = mock(DeviceType.class);
        when(tempUser.getDeviceType()).thenReturn(deviceType);
        when(tempUser.getDeviceModel()).thenReturn("DEVICE_MODEL");
        when(tempUser.getIpAddress()).thenReturn("10.10.20.30");
        when(tempUser.getUuid()).thenReturn("1234-5678-9000");

        userService.mergeUser(oldUser, tempUser);

        verify(userDeviceDetailsService).removeUserDeviceDetails(tempUser);
        verify(deviceUserDataService).removeDeviceUserData(oldUser);
        verify(deviceUserDataService).removeDeviceUserData(tempUser);
        verify(userRepository).deleteUser(987);
        verify(accountLogService).logAccountMergeEvent(oldUser, tempUser);
        verify(appsFlyerDataService).mergeAppsFlyerData(tempUser, oldUser);

        verify(oldUser).setDeviceUID("deviceUID");
        verify(oldUser).setDeviceType(deviceType);
        verify(oldUser).setDeviceModel("DEVICE_MODEL");
        verify(oldUser).setIpAddress("10.10.20.30");
        verify(oldUser).setUuid("1234-5678-9000");
    }

    @Test
    public void mergeWhenSeveralTempUserRecordsWereRemoved() throws Exception {
        User oldUser = mock(User.class);
        User tempUser = mock(User.class);
        final int tempUserId = 987;
        when(tempUser.getId()).thenReturn(tempUserId);
        when(userRepository.deleteUser(tempUserId)).thenReturn(5);

        thrown.expect(ServiceException.class);
        userService.mergeUser(oldUser, tempUser);
    }
}