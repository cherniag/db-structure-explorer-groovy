/**
 *
 */

package mobi.nowtechnologies.server.security;


import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.rememberme.AbstractRememberMeServices;

import org.junit.*;
import org.springframework.mock.web.MockHttpServletRequest;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
/**
 * @author Mayboroda Dmytro
 */
public class NowTechTokenBasedRememberMeServicesTest {

    private NowTechTokenBasedRememberMeServices service;
    private String key = "123";
    private UserDetailsService userDetailsService;

    @Before
    public void before() {
        userDetailsService = mock(UserDetailsService.class);
        service = new NowTechTokenBasedRememberMeServices(key, userDetailsService);
    }

    @Test
    public void encodeUsername_WithColons() {
        String userName = "mac_00:12:34:c2:ee:42";
        String expected = userName.replaceAll(":", "|");
        String encodedUserName = service.getEncodedUserName(userName);
        assertEquals(expected, encodedUserName);
    }

    @Test
    public void encodeUsername_WithoutColons() {
        String userName = "thisis@mail.com";
        String encodedUserName = service.getEncodedUserName(userName);
        assertEquals(userName, encodedUserName);
    }

    @Test
    public void givenValidRememberMeTokenOnlyInURL_whenExtractRememberMeCookie_ReturnTokenFromUrl() {
        String rememberMeToken = "1234";
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addParameter(AbstractRememberMeServices.SPRING_SECURITY_REMEMBER_ME_COOKIE_KEY, rememberMeToken);

        NowTechTokenBasedRememberMeServices rememberMeServices = new NowTechTokenBasedRememberMeServices(key, mock(UserDetailsService.class));
        String actualToken = rememberMeServices.extractRememberMeCookie(request);

        assertEquals(rememberMeToken, actualToken);
    }
}