package mobi.nowtechnologies.server.service;

import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.UserFactory;
import mobi.nowtechnologies.server.persistence.repository.UserRepository;
import mobi.nowtechnologies.server.service.exception.ValidationException;
import static mobi.nowtechnologies.server.shared.enums.ActivationStatus.ACTIVATED;

import org.junit.*;
import org.junit.runner.*;
import org.mockito.*;
import org.mockito.runners.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isNull;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class UserPromoServiceTest {

    @InjectMocks
    private UserPromoService userPromoService;

    @Mock
    private ActivationEmailService activationEmailService;
    @Mock
    private UserService userService;
    @Mock
    private ReferralService referralService;
    @Mock
    private UserRepository userRepository;

    @Test
    public void testApplyInitPromoByEmail() {
        User user = UserFactory.createUser(ACTIVATED);

        when(userRepository.findByUserNameAndCommunityUrl(anyString(), anyString())).thenReturn(user);

        MergeResult mergeResult = OperationResultFactory.createOperationResult(false, user);
        when(userService.applyInitPromo(any(User.class), any(User.class), isNull(String.class), eq(false), eq(true), eq(false))).thenReturn(mergeResult);

        userPromoService.applyInitPromoByEmail(user, 1l, "a@gmail.com", "ttt");

        verify(activationEmailService).activate(anyLong(), anyString(), anyString());
        verify(userService).applyInitPromo(any(User.class), any(User.class), isNull(String.class), eq(false), eq(true), eq(false));
        verify(userService).updateUser(user);
    }

    @Test(expected = ValidationException.class)
    public void testApplyInitPromoByEmailActivateError() {
        User user = UserFactory.createUser(ACTIVATED);

        MergeResult mergeResult = OperationResultFactory.createOperationResult(false, user);
        when(userService.applyInitPromo(any(User.class), isNull(String.class), eq(false), eq(true), eq(false))).thenReturn(mergeResult);
        doThrow(ValidationException.class).when(activationEmailService).activate(anyLong(), anyString(), anyString());

        userPromoService.applyInitPromoByEmail(user, 1l, "a@gmail.com", "ttt");
    }
}
