package mobi.nowtechnologies.server.security.impl.spring.method.annotation;

import mobi.nowtechnologies.server.security.bind.annotation.AuthenticatedUser;

import org.springframework.core.MethodParameter;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.RequestAttributes;

import org.junit.*;
import org.junit.runner.*;
import org.mockito.*;
import org.mockito.runners.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class AuthenticatedUserMethodArgumentResolverTest {

    private static final Class AUTHENTICATED_USER_CLASS = AuthenticatedUser.class;

    @Mock
    private MethodParameter methodParameter;

    @InjectMocks
    private AuthenticatedUserMethodArgumentResolver argumentResolver;

    @Test
    public void testCreateNamedValueInfo() throws Exception {
        AuthenticatedUser mock = mock(AuthenticatedUser.class);

        when(methodParameter.getParameterAnnotation(AUTHENTICATED_USER_CLASS)).thenReturn(mock);

        assertNotNull(argumentResolver.createNamedValueInfo(methodParameter));

        verify(methodParameter, times(1)).getParameterAnnotation(AUTHENTICATED_USER_CLASS);
    }

    @Test
    public void testResolveName() throws Exception {
        NativeWebRequest request = mock(NativeWebRequest.class);
        String name = "name";
        Object expected = this;

        when(request.getAttribute(name, RequestAttributes.SCOPE_REQUEST)).thenReturn(expected);

        Object actual = argumentResolver.resolveName(name, methodParameter, request);
        assertSame(expected, actual);

        verify(request, times(1)).getAttribute(name, RequestAttributes.SCOPE_REQUEST);
        verifyNoMoreInteractions(methodParameter, request);
    }

    @Test(expected = MissingServletRequestParameterException.class)
    public void testHandleMissingValue() throws Exception {
        try {
            // noinspection unchecked
            when(methodParameter.getParameterType()).thenReturn(AUTHENTICATED_USER_CLASS);
            argumentResolver.handleMissingValue("missing", methodParameter);
        } catch (MissingServletRequestParameterException e) {
            assertEquals(AuthenticatedUser.class.getSimpleName(), e.getParameterType());
            assertEquals("missing", e.getParameterName());

            verify(methodParameter, times(1)).getParameterType();
            verifyNoMoreInteractions(methodParameter);

            throw e;
        }
    }

    @Test
    public void testSupportsParameter() throws Exception {
        testSupportsParameter(true);
        testSupportsParameter(false);
    }

    private void testSupportsParameter(boolean expected) throws Exception {
        AuthenticatedUserMethodArgumentResolver resolver2spy = new AuthenticatedUserMethodArgumentResolver();
        resolver2spy.setParameterType(AUTHENTICATED_USER_CLASS);

        AuthenticatedUserMethodArgumentResolver resolver = spy(resolver2spy);

        MethodParameter methodParameter = mock(MethodParameter.class);

        when(methodParameter.hasParameterAnnotation(AUTHENTICATED_USER_CLASS)).thenReturn(expected);
        // noinspection unchecked
        when(methodParameter.getParameterType()).thenReturn(AUTHENTICATED_USER_CLASS);

        assertTrue(expected == resolver2spy.supportsParameter(methodParameter));

        verify(methodParameter, times(1)).hasParameterAnnotation(AUTHENTICATED_USER_CLASS);
        verify(methodParameter, times(expected ?
                                      1 :
                                      0)).getParameterType();

        verifyNoMoreInteractions(resolver, methodParameter);
    }
}