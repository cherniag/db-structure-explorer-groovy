package mobi.nowtechnologies.server.security.impl.spring.handler;

import mobi.nowtechnologies.server.security.AuthenticationService;
import mobi.nowtechnologies.server.security.bind.annotation.AuthenticatedUser;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.MissingServletRequestParameterException;

import org.junit.*;
import org.junit.runner.*;
import org.mockito.*;
import org.mockito.runners.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class AuthenticatingInterceptorTest {

    @Mock
    private AuthenticationService<HttpServletRequest, Object> authenticationService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private Object handler;

    @InjectMocks
    private AuthenticatingInterceptor authenticatingInterceptor;

    @Test(expected = MissingServletRequestParameterException.class)
    public void testPreHandle_MissingParamException() throws Exception {
        when(request.getParameter(AuthenticatedUser.USER_NAME)).thenReturn(AuthenticatedUser.USER_NAME);
        when(request.getParameter(AuthenticatedUser.USER_TOKEN)).thenReturn(null);

        try {
            authenticatingInterceptor.preHandle(request, response, handler);
        }
        catch (MissingServletRequestParameterException e) {
            verify(request, times(1)).getParameter(AuthenticatedUser.USER_NAME);
            verify(request, times(1)).getParameter(AuthenticatedUser.USER_TOKEN);

            verifyNoMoreInteractions(authenticationService, request, response, handler);

            throw e;
        }
    }

    @Test(expected = MissingServletRequestParameterException.class)
    public void testPreHandle_BlackParamException() throws Exception {
        when(request.getParameter(AuthenticatedUser.USER_NAME)).thenReturn("   ");

        try {
            authenticatingInterceptor.preHandle(request, response, handler);
        }
        catch (MissingServletRequestParameterException e) {
            verify(request, times(1)).getParameter(AuthenticatedUser.USER_NAME);

            verifyNoMoreInteractions(authenticationService, request, response, handler);

            throw e;
        }
    }

    @Test
    public void testPreHandle_Success() throws Exception {
        Object expected = this;

        when(request.getParameter(AuthenticatedUser.USER_NAME)).thenReturn(AuthenticatedUser.USER_NAME);
        when(request.getParameter(AuthenticatedUser.USER_TOKEN)).thenReturn(AuthenticatedUser.USER_TOKEN);
        when(request.getParameter(AuthenticatedUser.TIMESTAMP)).thenReturn(AuthenticatedUser.TIMESTAMP);
        when(authenticationService.authenticate(request)).thenReturn(expected);
        doNothing().when(request).setAttribute(AuthenticatedUser.AUTHENTICATED_USER_REQUEST_ATTRIBUTE, expected);

        authenticatingInterceptor.preHandle(request, response, handler);

        verify(request, times(1)).getParameter(AuthenticatedUser.USER_NAME);
        verify(request, times(1)).getParameter(AuthenticatedUser.USER_TOKEN);
        verify(request, times(1)).getParameter(AuthenticatedUser.TIMESTAMP);
        verify(authenticationService, times(1)).authenticate(request);
        verify(request, times(1)).setAttribute(AuthenticatedUser.AUTHENTICATED_USER_REQUEST_ATTRIBUTE, expected);
        verifyNoMoreInteractions(authenticationService, request, response, handler);
    }


    @Test
    public void testAfterCompletion() throws Exception {
        Exception ex = mock(Exception.class);

        doNothing().when(request).removeAttribute(AuthenticatedUser.AUTHENTICATED_USER_REQUEST_ATTRIBUTE);

        authenticatingInterceptor.afterCompletion(request, response, handler, ex);

        verify(request, times(1)).removeAttribute(AuthenticatedUser.AUTHENTICATED_USER_REQUEST_ATTRIBUTE);
        verifyNoMoreInteractions(authenticationService, request, response, handler, ex);
    }
}