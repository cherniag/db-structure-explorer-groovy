package mobi.nowtechnologies.server.service;

import mobi.nowtechnologies.server.device.domain.DeviceType;
import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.Country;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.UserGroup;
import mobi.nowtechnologies.server.persistence.repository.UserGroupRepository;
import mobi.nowtechnologies.server.persistence.repository.UserRepository;
import mobi.nowtechnologies.server.persistence.repository.UserStatusRepository;
import mobi.nowtechnologies.server.shared.dto.web.UserDeviceRegDetailsDto;
import mobi.nowtechnologies.server.shared.enums.ActivationStatus;

import org.junit.*;
import org.mockito.*;
import org.mockito.invocation.*;
import org.mockito.stubbing.*;
import static org.junit.Assert.*;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

public class UserServiceRegistrationTest {

    UserDeviceRegDetailsDto userDeviceRegDetailsDto = mock(UserDeviceRegDetailsDto.class);
    Community community = mock(Community.class);
    UserGroup userGroup = mock(UserGroup.class);
    ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
    @Mock
    private CommunityService communityService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserGroupRepository userGroupRepository;
    @Mock
    private CountryService countryService;
    @Mock
    UserStatusRepository userStatusRepository;
    @InjectMocks
    private UserService userService = new UserService() {
        @Override
        protected DeviceType getDeviceType(String device) {
            DeviceType deviceType = mock(DeviceType.class);
            when(deviceType.getName()).thenReturn(device);
            when(deviceType.getI()).thenReturn((byte) 1);
            return deviceType;
        }

        @Override
        protected UserGroup getUserGroup(Community community) {
            return userGroup;
        }

        @Override
        protected Integer getOperator() {
            return 0;
        }
    };

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        initMocks("communityUrl", "deviceUID", "DEVICE_TYPE");
    }

    @Test
    public void registerNewUserWithoutPromotion() throws Exception {
        when(userRepository.findUserWithUserNameAsPassedDeviceUID("deviceUID".toLowerCase(), community)).thenReturn(null);
        when(countryService.findIdByName("GB")).thenReturn(new Country("GB", "Great Britain"));

        User registerUser = userService.registerUser(userDeviceRegDetailsDto, false, false);

        assertEquals("deviceUID".toLowerCase(), registerUser.getUserName());
        assertEquals("deviceUID".toLowerCase(), registerUser.getDeviceUID());
        assertEquals(userGroup, registerUser.getUserGroup());
        assertEquals(ActivationStatus.REGISTERED, registerUser.getActivationStatus());
        assertEquals("DEVICE_TYPE", registerUser.getDeviceType().getName());
        assertFalse(registerUser.getUuid().isEmpty());
    }

    @Test
    public void reRegisterExistingUserWithoutPromotion() throws Exception {

        User existing = mock(User.class);
        when(userRepository.findUserWithUserNameAsPassedDeviceUID("deviceUID".toLowerCase(), community)).thenReturn(existing);

        userService.registerUser(userDeviceRegDetailsDto, false, false);

        verify(existing, never()).setUserName(anyString());
        verify(existing, never()).setActivationStatus(ActivationStatus.REGISTERED);
        verify(existing, times(1)).setUuid(anyString());
    }

    private void initMocks(String communityUrl, String deviceUID, String deviceType) {
        when(userDeviceRegDetailsDto.getCommunityUri()).thenReturn(communityUrl);
        when(userDeviceRegDetailsDto.getDeviceUID()).thenReturn(deviceUID);
        when(userDeviceRegDetailsDto.getDeviceType()).thenReturn(deviceType);
        when(communityService.getCommunityByUrl("communityUrl")).thenReturn(community);
        when(userRepository.saveAndFlush(userArgumentCaptor.capture())).thenAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                return userArgumentCaptor.getValue();
            }
        });
    }
}