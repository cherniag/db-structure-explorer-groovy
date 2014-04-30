package mobi.nowtechnologies.server.service;

import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.UserFactory;
import mobi.nowtechnologies.server.persistence.repository.UserRepository;
import mobi.nowtechnologies.server.service.exception.ValidationException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.util.ReflectionTestUtils;

import static mobi.nowtechnologies.server.shared.enums.ActivationStatus.ACTIVATED;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isNull;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
@ContextConfiguration(locations = {"/META-INF/dao-test.xml", "/META-INF/service-test.xml", "/META-INF/shared.xml"})
public class UserPromoServiceTest {

    private UserPromoService userPromoService;

    @Mock
    private ActivationEmailService activationEmailService;

    @Mock
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Test
    public void testApplyInitPromoByEmail() {
        User user = UserFactory.createUser(ACTIVATED);

        when(userRepository.findOne(anyString(), anyString())).thenReturn(user);

        when(userService.applyInitPromo(any(User.class), any(User.class), isNull(String.class), eq(false), eq(true), eq(false))).thenReturn(user);

        userPromoService.applyInitPromoByEmail(user, 1l, "a@gmail.com", "ttt");

        verify(activationEmailService).activate(anyLong(), anyString(), anyString());
        verify(userService).applyInitPromo(any(User.class), any(User.class), isNull(String.class), eq(false), eq(true), eq(false));
        verify(userService).updateUser(user);
    }

    @Test(expected = ValidationException.class)
    public void testApplyInitPromoByEmailActivateError() {
        User user = UserFactory.createUser(ACTIVATED);

        when(userService.applyInitPromo(any(User.class), isNull(String.class), eq(false), eq(true), eq(false))).thenReturn(user);
        doThrow(ValidationException.class).when(activationEmailService).activate(anyLong(), anyString(), anyString());

        userPromoService.applyInitPromoByEmail(user, 1l, "a@gmail.com", "ttt");
    }

    @Before
    public void setUp() {
        userPromoService = new UserPromoServiceImpl();
        ReflectionTestUtils.setField(userPromoService, "activationEmailService", activationEmailService);
        ReflectionTestUtils.setField(userPromoService, "userService", userService);
        ReflectionTestUtils.setField(userPromoService, "userRepository", userRepository);
    }
}
