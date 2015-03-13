package mobi.nowtechnologies.server.security.impl;

import mobi.nowtechnologies.server.interceptor.PathVariableResolver;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.security.bind.annotation.AuthenticatedUser;
import mobi.nowtechnologies.server.service.UserService;

import javax.servlet.http.HttpServletRequest;

import org.junit.*;
import org.junit.runner.*;
import org.mockito.*;
import org.mockito.runners.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class AuthenticationServiceImplTest {

    @Mock
    UserService userService;
    @Mock
    PathVariableResolver pathVariableResolver;

    @InjectMocks
    AuthenticationServiceImpl authenticationService;

    @Test
    public void testSuccess() throws Exception {
        User user = new User();
        HttpServletRequest request = createRequest();

        when(pathVariableResolver.resolveCommunityUri(request)).thenReturn("communityUri");
        when(userService.authenticate("communityUri", AuthenticatedUser.USER_NAME, AuthenticatedUser.USER_TOKEN, AuthenticatedUser.TIMESTAMP, AuthenticatedUser.DEVICE_UID)).thenReturn(user);

        Object principal = authenticationService.authenticate(request);
        assertSame(user, principal);

        verify(pathVariableResolver, times(1)).resolveCommunityUri(request);
        verify(userService, times(1)).authenticate("communityUri", AuthenticatedUser.USER_NAME, AuthenticatedUser.USER_TOKEN, AuthenticatedUser.TIMESTAMP, AuthenticatedUser.DEVICE_UID);
        verify(request, times(1)).getParameter(AuthenticatedUser.USER_NAME);
        verify(request, times(1)).getParameter(AuthenticatedUser.USER_TOKEN);
        verify(request, times(1)).getParameter(AuthenticatedUser.TIMESTAMP);
        verify(request, times(1)).getParameter(AuthenticatedUser.DEVICE_UID);

        verifyNoMoreInteractions(pathVariableResolver, userService, request);
    }

    private HttpServletRequest createRequest() {
        HttpServletRequest mock = mock(HttpServletRequest.class);

        when(mock.getParameter(AuthenticatedUser.USER_NAME)).thenReturn(AuthenticatedUser.USER_NAME);
        when(mock.getParameter(AuthenticatedUser.USER_TOKEN)).thenReturn(AuthenticatedUser.USER_TOKEN);
        when(mock.getParameter(AuthenticatedUser.TIMESTAMP)).thenReturn(AuthenticatedUser.TIMESTAMP);
        when(mock.getParameter(AuthenticatedUser.DEVICE_UID)).thenReturn(AuthenticatedUser.DEVICE_UID);

        return mock;
    }
}