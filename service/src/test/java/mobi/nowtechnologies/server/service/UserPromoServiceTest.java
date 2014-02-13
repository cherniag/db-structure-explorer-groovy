package mobi.nowtechnologies.server.service;

import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.UserFactory;
import mobi.nowtechnologies.server.service.exception.ValidationException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
@ContextConfiguration(locations = {"/META-INF/dao-test.xml", "/META-INF/service-test.xml", "/META-INF/shared.xml"})
@TransactionConfiguration(transactionManager = "persistence.TransactionManager", defaultRollback = true)
@Transactional
public class UserPromoServiceTest {

    private UserPromoService userPromoService;

    @Mock
    private ActivationEmailService activationEmailService;

    @Mock
    private UserService userService;

    @Test
    public void testApplyInitPromoByEmail() {
        User user = UserFactory.createUser();

        when(userService.applyInitPromo(any(User.class), isNull(String.class), eq(false), eq(true))).thenReturn(user);

        userPromoService.applyInitPromoByEmail(user, 1l, "a@gmail.com", "ttt");

        verify(activationEmailService).activate(anyLong(), anyString(), anyString());
        verify(userService).applyInitPromo(any(User.class), isNull(String.class), eq(false), eq(true));
        verify(userService).updateUser(user);
    }

    @Test(expected = ValidationException.class)
    public void testApplyInitPromoByEmailActivateError() {
        User user = UserFactory.createUser();

        when(userService.applyInitPromo(any(User.class), isNull(String.class), eq(false), eq(true))).thenReturn(user);
        doThrow(ValidationException.class).when(activationEmailService).activate(anyLong(), anyString(), anyString());

        userPromoService.applyInitPromoByEmail(user, 1l, "a@gmail.com", "ttt");
    }

    @Before
    public void setUp() {
        userPromoService = new UserPromoServiceImpl();
        ReflectionTestUtils.setField(userPromoService, "activationEmailService", activationEmailService);
        ReflectionTestUtils.setField(userPromoService, "userService", userService);
    }
}
