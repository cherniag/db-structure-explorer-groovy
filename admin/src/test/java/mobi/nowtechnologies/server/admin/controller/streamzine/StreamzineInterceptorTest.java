package mobi.nowtechnologies.server.admin.controller.streamzine;

import mobi.nowtechnologies.server.admin.validator.CookieUtil;
import mobi.nowtechnologies.server.shared.web.filter.CommunityResolverFilter;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.method.HandlerMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class StreamzineInterceptorTest {
    @Mock
    CookieUtil cookieUtil;
    @Mock
    HttpServletRequest httpServletRequest;
    @Mock
    HttpServletResponse httpServletResponse;
    @Mock
    HandlerMethod handlerMethod;
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testPreHandleDoesNotContain() throws Exception {
        StreamzineInterceptor streamzineInterceptor = new StreamzineInterceptor() {
            @Override
            RequestMapping extractAnnotation(HandlerMethod handlerMethod) {
                RequestMapping request = mock(RequestMapping.class);
                when(request.value()).thenReturn(new String[]{"any-other-value"});
                return request;
            }
        };
        streamzineInterceptor.setAvailableCommunites(new String[]{"hl_uk"});
        streamzineInterceptor.setCookieUtil(cookieUtil);

        // given
        when(handlerMethod.getBean()).thenReturn(mock(StreamzineController.class));
        when(cookieUtil.get(CommunityResolverFilter.DEFAULT_COMMUNITY_COOKIE_NAME)).thenReturn("hl_uk_which_is_different");

        thrown.expect(ResourceNotFoundException.class);
        // when
        streamzineInterceptor.preHandle(httpServletRequest, httpServletResponse, handlerMethod);


    }
}