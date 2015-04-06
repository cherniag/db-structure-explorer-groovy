package mobi.nowtechnologies.server.social.service.facebook;


import mobi.nowtechnologies.server.social.domain.SocialNetworkInfo;
import mobi.nowtechnologies.server.social.service.OAuth2ForbiddenException;
import mobi.nowtechnologies.server.social.service.facebook.impl.mock.AppTestFacebookOperationsAdaptor;

import org.junit.*;
import org.junit.runner.*;
import org.mockito.*;
import org.mockito.runners.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;


@RunWith(MockitoJUnitRunner.class)
public class FacebookServiceTest {

    @Mock
    SocialNetworkInfo facebookUserInfo;
    @Mock
    FacebookClient facebookClient;

    @InjectMocks
    FacebookService facebookService = new FacebookService();

    @Test
    public void testGetFacebookUserInfo() throws Exception {
        String accessToken = "accessToken";

        when(facebookUserInfo.getSocialNetworkId()).thenReturn("inputFacebookId");
        when(facebookClient.getProfileUserInfo(accessToken, facebookService.userId)).thenReturn(facebookUserInfo);
        when(facebookClient.getProfileImage(accessToken, facebookService.userProfileImageUrlId)).thenReturn(new FacebookProfileImage(AppTestFacebookOperationsAdaptor.TEST_PROFILE_IMAGE_URL, false));

        SocialNetworkInfo actual = facebookService.getFacebookUserInfo(accessToken, facebookUserInfo.getSocialNetworkId());

        verify(facebookUserInfo, times(2)).getSocialNetworkId();
        verify(facebookClient, times(1)).getProfileUserInfo(accessToken, facebookService.userId);
        verify(facebookClient, times(1)).getProfileImage(accessToken, facebookService.userProfileImageUrlId);
        verify(facebookUserInfo, times(1)).setProfileImageUrl(AppTestFacebookOperationsAdaptor.TEST_PROFILE_IMAGE_URL);
        verify(facebookUserInfo, times(1)).setProfileImageSilhouette(false);
        verifyNoMoreInteractions(facebookClient, facebookUserInfo);

        assertSame(facebookUserInfo, actual);
    }

    @Test(expected = OAuth2ForbiddenException.class)
    public void testGetFacebook_throwsOAuth2ForbiddenException() throws Exception {
        String inputFacebookId = "inputFacebookId";
        String accessToken = "accessToken";
        when(facebookUserInfo.getSocialNetworkId()).thenReturn("facebookId");
        when(facebookClient.getProfileUserInfo(accessToken, facebookService.userId)).thenReturn(facebookUserInfo);

        try {
            facebookService.getFacebookUserInfo(accessToken, inputFacebookId);
        } catch (OAuth2ForbiddenException e) {
            verify(facebookUserInfo, times(1)).getSocialNetworkId();
            verify(facebookClient, times(1)).getProfileUserInfo(accessToken, facebookService.userId);

            verifyNoMoreInteractions(facebookClient, facebookUserInfo);

            assertSame(FacebookClient.INVALID_FACEBOOK_USER_ID, e);

            throw e;
        }
    }
}