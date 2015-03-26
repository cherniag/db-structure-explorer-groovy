package mobi.nowtechnologies.server.web.asm;

import mobi.nowtechnologies.server.persistence.repository.UserRepository;
import mobi.nowtechnologies.server.service.UserService;
import mobi.nowtechnologies.server.shared.dto.web.UserRegDetailsDto;
import mobi.nowtechnologies.server.shared.web.filter.CommunityResolverFilter;

import javax.servlet.http.Cookie;

import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import org.junit.*;
import org.mockito.*;
import org.springframework.mock.web.MockHttpServletRequest;
import static org.junit.Assert.*;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

public class UserRegDetailsDtoValidatorTest {

    @Mock
    private UserService userService;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserRegDetailsDtoValidator validator;

    private UserRegDetailsDto dto = new UserRegDetailsDto();

    private Errors errors = new BeanPropertyBindingResult(dto, "");

    @Before
    public void before() {
        MockitoAnnotations.initMocks(this);

        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        request.setCookies(createCookieWithDefaultCommunityValue());

        dto.setPassword("TEST");
        dto.setEmail("test@gmail.com");
    }

    @Test
    public void testCountryIsNotSupported() {
        when(userService.isCommunitySupportByIp(anyString(), anyString(), anyString())).thenReturn(false);
        validator.customValidate(dto, errors);
        assertTrue(errors.hasErrors());
        assertEquals("We don't support your country", errors.getFieldError("ipAddress").getDefaultMessage());
    }

    @Test
    public void testCountryIsSupported() {
        when(userService.isCommunitySupportByIp(anyString(), anyString(), anyString())).thenReturn(true);
        validator.customValidate(dto, errors);
        assertNull(errors.getFieldError("ipAddress"));
    }

    private Cookie createCookieWithDefaultCommunityValue() {
        Cookie cookie = mock(Cookie.class);
        when(cookie.getName()).thenReturn(CommunityResolverFilter.DEFAULT_COMMUNITY_COOKIE_NAME);
        when(cookie.getValue()).thenReturn("SomeDefaultCommunity");
        return cookie;
    }
}
