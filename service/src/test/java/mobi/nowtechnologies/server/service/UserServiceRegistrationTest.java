package mobi.nowtechnologies.server.service;

import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.DeviceType;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.UserGroup;
import mobi.nowtechnologies.server.persistence.repository.UserGroupRepository;
import mobi.nowtechnologies.server.persistence.repository.UserRepository;
import mobi.nowtechnologies.server.shared.dto.web.UserDeviceRegDetailsDto;
import mobi.nowtechnologies.server.shared.enums.ActivationStatus;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceRegistrationTest {

    @Mock
    private CommunityService communityService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserGroupRepository userGroupRepository;

    @Mock
    private CountryService countryService;

    @InjectMocks
    private UserService userService = new UserService(){
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

    UserDeviceRegDetailsDto userDeviceRegDetailsDto = mock(UserDeviceRegDetailsDto.class);
    Community community = mock(Community.class);
    UserGroup userGroup = mock(UserGroup.class);
    ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);


    @Test
    public void registerNewUserWithoutPromotion() throws Exception {
        initMocks("communityUrl", "deviceUID", "DEVICE_TYPE");
        when(userRepository.findUserWithUserNameAsPassedDeviceUID("deviceUID".toLowerCase(), community)).thenReturn(null);

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
        initMocks("communityUrl", "deviceUID", "DEVICE_TYPE");
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
        when(userRepository.save(userArgumentCaptor.capture())).thenAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                return userArgumentCaptor.getValue();
            }
        });
    }
}