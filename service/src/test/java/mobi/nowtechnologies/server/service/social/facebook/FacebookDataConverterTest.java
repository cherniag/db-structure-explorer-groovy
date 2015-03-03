package mobi.nowtechnologies.server.service.social.facebook;

import mobi.nowtechnologies.server.persistence.domain.social.FacebookUserInfo;
import mobi.nowtechnologies.server.shared.enums.Gender;

import org.apache.commons.lang3.time.DateUtils;

import org.springframework.social.facebook.api.FacebookProfile;
import org.springframework.social.facebook.api.Reference;

import org.junit.*;
import org.junit.runner.*;
import org.mockito.*;
import org.mockito.runners.*;
import static org.mockito.Mockito.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

@RunWith(MockitoJUnitRunner.class)
public class FacebookDataConverterTest {

    private FacebookDataConverter facebookDataConverter = new FacebookDataConverter();
    @Mock
    private FacebookProfile facebookProfile;

    @Before
    public void setUp() throws Exception {
        when(facebookProfile.getId()).thenReturn("101");
        when(facebookProfile.getUsername()).thenReturn("userName");
        when(facebookProfile.getEmail()).thenReturn("email@dot.com");
        when(facebookProfile.getFirstName()).thenReturn("firstName");
        when(facebookProfile.getLastName()).thenReturn("lastName");
        when(facebookProfile.getGender()).thenReturn("male");
        when(facebookProfile.getBirthday()).thenReturn("06/24/1985");
        when(facebookProfile.getLocation()).thenReturn(new Reference("1", "Kiev, Ukraine"));
    }

    @Test
    public void convertFacebookProfile() throws Exception {
        FacebookUserInfo convert = facebookDataConverter.convert(facebookProfile);

        assertThat(convert.getFacebookId(), is("101"));
        assertThat(convert.getUserName(), is("userName"));
        assertThat(convert.getProfileUrl(), is("https://graph.facebook.com/userName/picture?type=large"));
        assertThat(convert.getEmail(), is("email@dot.com"));
        assertThat(convert.getFirstName(), is("firstName"));
        assertThat(convert.getSurname(), is("lastName"));
        assertThat(convert.getGender(), is(Gender.MALE));
        assertThat(convert.getCity(), is("Kiev"));
        assertThat(convert.getCountry(), is("Ukraine"));
        assertThat(convert.getBirthday(), is(DateUtils.parseDate("24/06/1985", "dd/MM/yyyy")));
    }

    @Test
    public void convertFacebookProfileWithNullUserName() throws Exception {
        when(facebookProfile.getUsername()).thenReturn(null);

        FacebookUserInfo convert = facebookDataConverter.convert(facebookProfile);

        assertThat(convert.getFacebookId(), is("101"));
        assertThat(convert.getUserName(), is("101"));
        assertThat(convert.getEmail(), is("email@dot.com"));
        assertThat(convert.getFirstName(), is("firstName"));
        assertThat(convert.getProfileUrl(), is("https://graph.facebook.com/101/picture?type=large"));
        assertThat(convert.getSurname(), is("lastName"));
        assertThat(convert.getGender(), is(Gender.MALE));
        assertThat(convert.getCity(), is("Kiev"));
        assertThat(convert.getCountry(), is("Ukraine"));
        assertThat(convert.getBirthday(), is(DateUtils.parseDate("24/06/1985", "dd/MM/yyyy")));
    }

    @Test
    public void convertFacebookProfileWithNullEmail() throws Exception {
        when(facebookProfile.getEmail()).thenReturn("");
        when(facebookProfile.getUsername()).thenReturn(null);
        when(facebookProfile.getId()).thenReturn("some.user");

        FacebookUserInfo convert = facebookDataConverter.convert(facebookProfile);

        assertThat(convert.getEmail(), is("some.user@facebook.com"));

    }
}