package mobi.nowtechnologies.server.service.social.facebook;


import mobi.nowtechnologies.server.persistence.domain.social.FacebookUserInfo;
import mobi.nowtechnologies.server.service.social.core.OAuth2ForbiddenException;

import org.junit.*;
import org.junit.runner.*;
import org.mockito.*;
import org.mockito.runners.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;


@RunWith(MockitoJUnitRunner.class)
public class FacebookServiceTest {

    @Mock
    FacebookUserInfo facebookUserInfo;
    @Mock
    FacebookClient facebookClient;

    @InjectMocks
    FacebookService facebookService = new FacebookService();

    @Test
    public void testGetFacebookUserInfo() throws Exception {
        String accessToken = "accessToken";

        when(facebookUserInfo.getFacebookId()).thenReturn("inputFacebookId");
        when(facebookClient.getProfileUserInfo(accessToken, facebookService.userId)).thenReturn(facebookUserInfo);

        FacebookUserInfo actual = facebookService.getFacebookUserInfo(accessToken, facebookUserInfo.getFacebookId());

        verify(facebookUserInfo, times(2)).getFacebookId();
        verify(facebookClient, times(1)).getProfileUserInfo(accessToken, facebookService.userId);
        verifyNoMoreInteractions(facebookClient, facebookUserInfo);

        assertSame(facebookUserInfo, actual);
    }

    @Test(expected = OAuth2ForbiddenException.class)
    public void testGetFacebook_throwsOAuth2ForbiddenException() throws Exception {
        String inputFacebookId = "inputFacebookId";
        String accessToken = "accessToken";
        when(facebookUserInfo.getFacebookId()).thenReturn("facebookId");
        when(facebookClient.getProfileUserInfo(accessToken, facebookService.userId)).thenReturn(facebookUserInfo);

        try {
            facebookService.getFacebookUserInfo(accessToken, inputFacebookId);
        } catch (OAuth2ForbiddenException e) {
            verify(facebookUserInfo, times(1)).getFacebookId();
            verify(facebookClient, times(1)).getProfileUserInfo(accessToken, facebookService.userId);

            verifyNoMoreInteractions(facebookClient, facebookUserInfo);

            assertSame(FacebookClient.INVALID_FACEBOOK_USER_ID, e);

            throw e;
        }
    }
}