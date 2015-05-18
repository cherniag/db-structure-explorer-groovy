package mobi.nowtechnologies.server.social.service.facebook.impl;

import mobi.nowtechnologies.server.social.domain.GenderType;
import mobi.nowtechnologies.server.social.domain.SocialNetworkInfo;
import mobi.nowtechnologies.server.social.service.OAuth2ForbiddenException;
import mobi.nowtechnologies.server.social.service.facebook.FacebookClient;

import org.apache.commons.lang3.time.DateUtils;

import org.springframework.social.MissingAuthorizationException;
import org.springframework.social.facebook.api.AgeRange;
import org.springframework.social.facebook.api.Reference;
import org.springframework.social.facebook.api.User;

import org.junit.*;
import org.junit.runner.*;
import org.mockito.*;
import org.mockito.runners.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;


@RunWith(MockitoJUnitRunner.class)
public class FacebookClientImplTest {

    @Mock
    User facebookProfile;
    @Mock
    FacebookOperationsAdaptor facebookOperationsAdaptor;
    @InjectMocks
    FacebookClientImpl facebookClient = new FacebookClientImpl();

    @Before
    public void setUp() throws Exception {
        when(facebookProfile.getEmail()).thenReturn("email@dot.com");
        when(facebookProfile.getId()).thenReturn("101");
        when(facebookProfile.getBirthday()).thenReturn("24/06/1985");
        when(facebookProfile.getGender()).thenReturn(GenderType.MALE.getKey());
        when(facebookProfile.getLocation()).thenReturn(new Reference("1", "Kiev, Ukraine"));
        when(facebookProfile.getFirstName()).thenReturn("firstName");
        when(facebookProfile.getLastName()).thenReturn("lastName");
    }

    @Test
    public void testGetProfileUserInfo() throws Exception {
        when(facebookOperationsAdaptor.getFacebookProfile("", "")).thenReturn(facebookProfile);
        when(facebookProfile.getAgeRange()).thenReturn(AgeRange.UNKNOWN);

        SocialNetworkInfo convert = facebookClient.getProfileUserInfo("", "");

        verify(facebookOperationsAdaptor, times(1)).getFacebookProfile("", "");

        assertEquals("101", convert.getSocialNetworkId());
        assertEquals("email@dot.com", convert.getEmail());
        assertEquals("firstName", convert.getFirstName());
        assertEquals("lastName", convert.getLastName());
        assertEquals(GenderType.MALE, convert.getGenderType());
        assertEquals("Kiev", convert.getCity());
        assertEquals("Ukraine", convert.getCountry());
        assertEquals(DateUtils.parseDate("24/06/1985", FacebookClient.DATE_FORMAT), convert.getBirthday());

        assertEquals(convert.getAgeRangeMin(), AgeRange.UNKNOWN.getMin());
        assertEquals(convert.getAgeRangeMax(), AgeRange.UNKNOWN.getMax());
    }

    @Test(expected = OAuth2ForbiddenException.class)
    public void testGetProfileUserInfo_handlesSocialException() throws Exception {
        when(facebookOperationsAdaptor.getFacebookProfile("asd", "dsa")).thenThrow(new MissingAuthorizationException(""));

        try {
            facebookClient.getProfileUserInfo("asd", "dsa");
        } catch (OAuth2ForbiddenException e) {
            verify(facebookOperationsAdaptor, times(1)).getFacebookProfile("asd", "dsa");
            assertSame(FacebookClient.INVALID_FACEBOOK_TOKEN_EXCEPTION, e);
            throw e;
        }
    }

    @Test
    public void testGetProfileUserInfo_WithNullEmail() throws Exception {
        when(facebookOperationsAdaptor.getFacebookProfile("", "")).thenReturn(facebookProfile);
        when(facebookProfile.getAgeRange()).thenReturn(AgeRange.AGE_13_17);
        when(facebookProfile.getEmail()).thenReturn("");
        when(facebookProfile.getId()).thenReturn("some.user");

        SocialNetworkInfo convert = facebookClient.getProfileUserInfo("", "");

        verify(facebookOperationsAdaptor, times(1)).getFacebookProfile("", "");

        assertEquals("some.user@facebook.com", convert.getEmail());
        assertEquals(convert.getAgeRangeMin(), AgeRange.AGE_13_17.getMin());
        assertEquals(convert.getAgeRangeMax(), AgeRange.AGE_13_17.getMax());
    }
}