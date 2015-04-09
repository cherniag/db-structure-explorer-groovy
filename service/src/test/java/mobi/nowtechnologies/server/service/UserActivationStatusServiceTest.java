package mobi.nowtechnologies.server.service;

import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.UserFactory;
import mobi.nowtechnologies.server.persistence.domain.UserStatus;
import mobi.nowtechnologies.server.service.exception.ActivationStatusException;
import mobi.nowtechnologies.server.shared.enums.ActivationStatus;
import static mobi.nowtechnologies.server.shared.enums.ActivationStatus.ACTIVATED;
import static mobi.nowtechnologies.server.shared.enums.ActivationStatus.ENTERED_NUMBER;
import static mobi.nowtechnologies.server.shared.enums.ActivationStatus.REGISTERED;

import org.junit.*;
import org.mockito.*;
import static org.mockito.Mockito.*;

public class UserActivationStatusServiceTest {
    UserActivationStatusService userActivationStatusService = new UserActivationStatusService();

    @Test(expected = ActivationStatusException.class)
    public void shouldThrowCredentialExeption_OnCheckActivationStatus_BecauseUserIsNotRegistered() {
        User user = Mockito.mock(User.class);
        when(user.getActivationStatus()).thenReturn(ACTIVATED);
        when(user.hasAllDetails()).thenReturn(false);
        when(user.getUserName()).thenReturn("dfsdfasffasfafsdfsdf");
        when(user.getDeviceUID()).thenReturn("dfsdfasffasfafsdfsdf");

        userActivationStatusService.checkActivationStatus(user);

        verify(user, times(1)).getActivationStatus();
        verify(user, times(1)).hasAllDetails();
    }

    @Test(expected = ActivationStatusException.class)
    public void shouldThrowCredentialExeption_OnCheckActivationStatus_BecauseUserIsNotPhoneNumber() {
        User user = Mockito.mock(User.class);
        when(user.getActivationStatus()).thenReturn(ACTIVATED);
        when(user.hasAllDetails()).thenReturn(true);
        when(user.getUserName()).thenReturn("dfsdfasffasfafsdfsdf");
        when(user.getMobile()).thenReturn("+4400000000000");

        userActivationStatusService.checkActivationStatus(user);

        verify(user, times(1)).getActivationStatus();
        verify(user, times(1)).hasAllDetails();
        verify(user, times(1)).getUserName();
        verify(user, times(1)).getMobile();
    }

    @Test(expected = ActivationStatusException.class)
    public void shouldThrowCredentialExeption_OnCheckActivationStatus_BecauseUserIsNotActivated() {
        User user = Mockito.mock(User.class);
        when(user.getActivationStatus()).thenReturn(ENTERED_NUMBER);
        when(user.hasAllDetails()).thenReturn(true);
        when(user.getUserName()).thenReturn("+4400000000000");
        when(user.getMobile()).thenReturn("+4400000000000");

        userActivationStatusService.checkActivationStatus(user);

        verify(user, times(1)).getActivationStatus();
        verify(user, times(1)).hasAllDetails();
        verify(user, times(1)).getUserName();
        verify(user, times(1)).getMobile();
    }

    @Test
    public void shouldReturn_OnCheckActivationStatus_BecauseUserIsActivated() {
        User user = Mockito.mock(User.class);
        when(user.getActivationStatus()).thenReturn(ACTIVATED);
        when(user.hasAllDetails()).thenReturn(true);
        when(user.isActivatedUserName()).thenReturn(true);
        when(user.getUserName()).thenReturn("+4400000000000");
        when(user.getMobile()).thenReturn("+4400000000000");

        userActivationStatusService.checkActivationStatus(user);

        verify(user, times(1)).getActivationStatus();
        verify(user, times(1)).hasAllDetails();
        verify(user, times(0)).getUserName();
        verify(user, times(0)).getMobile();
    }

    @Test
    public void shouldReturn_OnCheckActivationStatus_BecauseUserIsEnteredPhoneNumber() {
        User user = Mockito.mock(User.class);
        when(user.getActivationStatus()).thenReturn(ENTERED_NUMBER);
        when(user.hasAllDetails()).thenReturn(true);
        when(user.isTempUserName()).thenReturn(true);
        when(user.isLimited()).thenReturn(true);
        when(user.hasPhoneNumber()).thenReturn(true);
        when(user.getUserName()).thenReturn("afdfsdfsdfsdfsdfsdfsd");
        when(user.getMobile()).thenReturn("+4400000000000");

        userActivationStatusService.checkActivationStatus(user);

        verify(user, times(1)).getActivationStatus();
        verify(user, times(1)).hasPhoneNumber();
        verify(user, times(1)).isTempUserName();
        verify(user, times(0)).hasAllDetails();
        verify(user, times(0)).getUserName();
        verify(user, times(0)).getMobile();
    }

    @Test
    public void shouldReturn_OnCheckActivationStatus_BecauseUserIsRegistered() {
        User user = Mockito.mock(User.class);
        when(user.getActivationStatus()).thenReturn(REGISTERED);
        when(user.isTempUserName()).thenReturn(true);
        when(user.isLimited()).thenReturn(true);
        when(user.hasPhoneNumber()).thenReturn(false);
        when(user.getUserName()).thenReturn("afdfsdfsdfsdfsdfsdfsd");
        when(user.getDeviceUID()).thenReturn("afdfsdfsdfsdfsdfsdfsd");
        when(user.hasAllDetails()).thenReturn(false);

        userActivationStatusService.checkActivationStatus(user);

        verify(user, times(1)).getActivationStatus();
        verify(user, times(1)).hasAllDetails();
        verify(user, times(1)).isLimited();
        verify(user, times(1)).hasPhoneNumber();
        verify(user, times(1)).isTempUserName();
        verify(user, times(0)).getUserName();
        verify(user, times(0)).getMobile();
    }

    @Test
    public void checkActivationStatusRegisteredShouldPassWhenDifferentCase() {
        User user = UserFactory.createUser(ActivationStatus.ACTIVATED);
        user.setDeviceUID("DEVICE_UID");
        user.setUserName("device_uid");
        user.setMobile("");
        user.setActivationStatus(REGISTERED);
        user.setStatus(new UserStatus(UserStatus.LIMITED));
        userActivationStatusService.checkActivationStatus(user, REGISTERED);
    }

    @Test
    public void checkActivationStatusEnteredNumberShouldPassWhenDifferentCase() {
        User user = UserFactory.createUser(ActivationStatus.ACTIVATED);
        user.setDeviceUID("DEVICE_UID");
        user.setUserName("device_uid");
        user.setStatus(new UserStatus(UserStatus.LIMITED));
        user.setActivationStatus(ENTERED_NUMBER);
        userActivationStatusService.checkActivationStatus(user, ENTERED_NUMBER);
    }

    @Test(expected = ActivationStatusException.class)
    public void checkActivationStatusRegisteredShouldNotPass() {
        User user = UserFactory.createUser(ActivationStatus.ACTIVATED);
        user.setDeviceUID("device_uid");
        user.setUserName("other");
        user.setStatus(new UserStatus(UserStatus.LIMITED));
        user.setActivationStatus(REGISTERED);
        userActivationStatusService.checkActivationStatus(user, REGISTERED);
    }

    @Test(expected = ActivationStatusException.class)
    public void checkActivationStatusEnteredNumberShouldNotPass() {
        User user = UserFactory.createUser(ActivationStatus.ACTIVATED);
        user.setDeviceUID("other");
        user.setUserName("device_uid");
        user.setStatus(new UserStatus(UserStatus.LIMITED));
        user.setActivationStatus(ENTERED_NUMBER);
        userActivationStatusService.checkActivationStatus(user, ENTERED_NUMBER);
    }

}