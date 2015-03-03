package mobi.nowtechnologies.server.assembler;

import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.social.FacebookUserInfo;
import mobi.nowtechnologies.server.persistence.domain.social.GooglePlusUserInfo;
import mobi.nowtechnologies.server.persistence.repository.social.FacebookUserInfoRepository;
import mobi.nowtechnologies.server.persistence.repository.social.GooglePlusUserInfoRepository;
import mobi.nowtechnologies.server.shared.dto.social.FacebookUserDetailsDto;
import mobi.nowtechnologies.server.shared.dto.social.GooglePlusUserDetailsDto;
import mobi.nowtechnologies.server.shared.dto.social.SocialInfoType;
import mobi.nowtechnologies.server.shared.dto.social.UserDetailsDto;
import mobi.nowtechnologies.server.shared.enums.Gender;
import mobi.nowtechnologies.server.shared.enums.ProviderType;
import mobi.nowtechnologies.server.user.rules.RuleServiceSupport;

import java.text.SimpleDateFormat;

import org.junit.*;
import org.junit.runner.*;
import org.mockito.*;
import org.mockito.runners.*;
import static org.mockito.Mockito.*;

import static junit.framework.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class UserDetailsDtoAsmTest {

    @Mock
    private RuleServiceSupport ruleServiceSupport;
    @Mock
    private GooglePlusUserInfoRepository googlePlusUserInfoRepository;
    @Mock
    private FacebookUserInfoRepository facebookUserInfoRepository;

    @InjectMocks
    private UserDetailsDtoAsm userDetailsDtoAsm;

    @Test
    public void checkConvertForGPUser() throws Exception {
        User user = mock(User.class);
        when(user.getProvider()).thenReturn(ProviderType.GOOGLE_PLUS);

        GooglePlusUserInfo googlePlusUserInfo = mock(GooglePlusUserInfo.class);
        when(googlePlusUserInfo.getEmail()).thenReturn("a@bc.com");
        when(googlePlusUserInfo.getDisplayName()).thenReturn("Display Name");
        when(googlePlusUserInfo.getPicture()).thenReturn("http://gp.com/1/1.jpg");
        when(googlePlusUserInfo.getGooglePlusId()).thenReturn("000001");
        when(googlePlusUserInfo.getGivenName()).thenReturn("Name");
        when(googlePlusUserInfo.getFamilyName()).thenReturn("Surname");
        when(googlePlusUserInfo.getGender()).thenReturn(Gender.FEMALE);
        when(googlePlusUserInfo.getLocation()).thenReturn("London");
        when(googlePlusUserInfo.getBirthday()).thenReturn(new SimpleDateFormat("MM/dd/yyyy").parse("07/12/1980"));

        when(googlePlusUserInfoRepository.findByUser(user)).thenReturn(googlePlusUserInfo);


        // invoke
        UserDetailsDto dto = userDetailsDtoAsm.toUserDetailsDto(user);

        //check user details
        GooglePlusUserDetailsDto dtoUserDetails = (GooglePlusUserDetailsDto) dto;
        assertEquals("a@bc.com", dtoUserDetails.getEmail());
        assertEquals("Display Name", dtoUserDetails.getUserName());
        assertEquals("http://gp.com/1/1.jpg", dtoUserDetails.getProfileUrl());
        assertEquals("000001", dtoUserDetails.getGooglePlusId());
        assertEquals("Name", dtoUserDetails.getFirstName());
        assertEquals("Surname", dtoUserDetails.getSurname());
        assertEquals(Gender.FEMALE, dtoUserDetails.getGender());
        assertEquals("London", dtoUserDetails.getLocation());
        assertEquals("07/12/1980", dtoUserDetails.getBirthDay());
        assertEquals(SocialInfoType.GooglePlus, dtoUserDetails.getSocialInfoType());
    }

    @Test
    public void checkConvertForFBUser() throws Exception {
        User user = mock(User.class);
        when(user.getProvider()).thenReturn(ProviderType.FACEBOOK);

        FacebookUserInfo facebookUserInfo = mock(FacebookUserInfo.class);
        when(facebookUserInfo.getUserName()).thenReturn("UserName");
        when(facebookUserInfo.getFirstName()).thenReturn("Name");
        when(facebookUserInfo.getSurname()).thenReturn("Surname");
        when(facebookUserInfo.getEmail()).thenReturn("a@bc.com");
        when(facebookUserInfo.getProfileUrl()).thenReturn("http://fb.com/1/1.jpg");
        when(facebookUserInfo.getFacebookId()).thenReturn("000001");
        when(facebookUserInfo.getCity()).thenReturn("London");
        when(facebookUserInfo.getGender()).thenReturn(Gender.FEMALE);
        when(facebookUserInfo.getBirthday()).thenReturn(new SimpleDateFormat("MM/dd/yyyy").parse("07/12/1980"));

        when(facebookUserInfoRepository.findByUser(user)).thenReturn(facebookUserInfo);


        // invoke
        UserDetailsDto dto = userDetailsDtoAsm.toUserDetailsDto(user);

        //check user details
        FacebookUserDetailsDto dtoUserDetails = (FacebookUserDetailsDto) dto;
        assertEquals("UserName", dtoUserDetails.getUserName());
        assertEquals("Name", dtoUserDetails.getFirstName());
        assertEquals("Surname", dtoUserDetails.getSurname());
        assertEquals("a@bc.com", dtoUserDetails.getEmail());
        assertEquals("http://fb.com/1/1.jpg", dtoUserDetails.getProfileUrl());
        assertEquals("000001", dtoUserDetails.getFacebookId());
        assertEquals("London", dtoUserDetails.getLocation());
        assertEquals(Gender.FEMALE, dtoUserDetails.getGender());
        assertEquals("07/12/1980", dtoUserDetails.getBirthDay());
        assertEquals(SocialInfoType.Facebook, dtoUserDetails.getSocialInfoType());
    }
}