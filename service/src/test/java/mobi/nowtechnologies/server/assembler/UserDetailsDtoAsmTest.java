package mobi.nowtechnologies.server.assembler;

import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.shared.enums.ProviderType;
import mobi.nowtechnologies.server.social.domain.GenderType;
import mobi.nowtechnologies.server.social.domain.SocialNetworkInfo;
import mobi.nowtechnologies.server.social.domain.SocialNetworkInfoRepository;
import mobi.nowtechnologies.server.social.domain.SocialNetworkType;
import mobi.nowtechnologies.server.social.dto.SocialInfoType;
import mobi.nowtechnologies.server.social.dto.UserDetailsDto;
import mobi.nowtechnologies.server.social.dto.facebook.FacebookUserDetailsDto;
import mobi.nowtechnologies.server.social.dto.googleplus.GooglePlusUserDetailsDto;
import mobi.nowtechnologies.server.user.rules.RuleServiceSupport;

import java.text.SimpleDateFormat;

import org.springframework.social.facebook.api.GraphApi;

import org.junit.*;
import org.junit.runner.*;
import org.mockito.*;
import org.mockito.runners.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class UserDetailsDtoAsmTest {

    @Mock
    private RuleServiceSupport ruleServiceSupport;
    @Mock
    private SocialNetworkInfoRepository socialNetworkInfoRepository;

    @InjectMocks
    private UserDetailsDtoAsm userDetailsDtoAsm;

    @Test
    public void checkConvertForGPUser() throws Exception {
        User user = mock(User.class);
        when(user.getProvider()).thenReturn(ProviderType.GOOGLE_PLUS);

        SocialNetworkInfo googlePlusUserInfo = mock(SocialNetworkInfo.class);
        when(googlePlusUserInfo.getEmail()).thenReturn("a@bc.com");
        when(googlePlusUserInfo.getUserName()).thenReturn("Display Name");
        when(googlePlusUserInfo.getProfileImageUrl()).thenReturn("http://gp.com/1/1.jpg");
        when(googlePlusUserInfo.getSocialNetworkId()).thenReturn("000001");
        when(googlePlusUserInfo.getFirstName()).thenReturn("Name");
        when(googlePlusUserInfo.getLastName()).thenReturn("Surname");
        when(googlePlusUserInfo.getGenderType()).thenReturn(GenderType.FEMALE);
        when(googlePlusUserInfo.getCity()).thenReturn("London");
        when(googlePlusUserInfo.getBirthday()).thenReturn(new SimpleDateFormat("MM/dd/yyyy").parse("07/12/1980"));

        when(socialNetworkInfoRepository.findByUserIdAndSocialNetworkType(user.getId(), SocialNetworkType.GOOGLE)).thenReturn(googlePlusUserInfo);


        // invoke
        UserDetailsDto dto = userDetailsDtoAsm.toUserDetailsDto(user);

        //check user details
        GooglePlusUserDetailsDto dtoUserDetails = (GooglePlusUserDetailsDto) dto;
        assertEquals("a@bc.com", dtoUserDetails.getEmail());
        assertEquals("Display Name", dtoUserDetails.getUserName());
        assertEquals(googlePlusUserInfo.getProfileImageUrl(), dtoUserDetails.getProfileUrl());
        assertEquals("000001", dtoUserDetails.getGooglePlusId());
        assertEquals("Name", dtoUserDetails.getFirstName());
        assertEquals("Surname", dtoUserDetails.getSurname());
        assertEquals(GenderType.FEMALE.name(), dtoUserDetails.getGender());
        assertEquals("London", dtoUserDetails.getLocation());
        assertEquals("07/12/1980", dtoUserDetails.getBirthDay());
        assertEquals(SocialInfoType.GooglePlus, dtoUserDetails.getSocialInfoType());
    }

    @Test
    public void checkConvertForFBUser() throws Exception {
        User user = mock(User.class);
        when(user.getProvider()).thenReturn(ProviderType.FACEBOOK);

        SocialNetworkInfo facebookUserInfo = mock(SocialNetworkInfo.class);
        when(facebookUserInfo.getUserName()).thenReturn("UserName");
        when(facebookUserInfo.getFirstName()).thenReturn("Name");
        when(facebookUserInfo.getLastName()).thenReturn("Surname");
        when(facebookUserInfo.getEmail()).thenReturn("a@bc.com");
        when(facebookUserInfo.getSocialNetworkId()).thenReturn("000001");
        when(facebookUserInfo.getCity()).thenReturn("London");
        when(facebookUserInfo.getGenderType()).thenReturn(GenderType.FEMALE);
        when(facebookUserInfo.getBirthday()).thenReturn(new SimpleDateFormat("MM/dd/yyyy").parse("07/12/1980"));

        when(socialNetworkInfoRepository.findByUserIdAndSocialNetworkType(user.getId(), SocialNetworkType.FACEBOOK)).thenReturn(facebookUserInfo);


        // invoke
        UserDetailsDto dto = userDetailsDtoAsm.toUserDetailsDto(user);

        //check user details
        FacebookUserDetailsDto dtoUserDetails = (FacebookUserDetailsDto) dto;
        assertEquals("UserName", dtoUserDetails.getUserName());
        assertEquals("Name", dtoUserDetails.getFirstName());
        assertEquals("Surname", dtoUserDetails.getSurname());
        assertEquals("a@bc.com", dtoUserDetails.getEmail());
        assertEquals(String.format("%s%s/picture?type=large", GraphApi.GRAPH_API_URL, "000001"), dtoUserDetails.getProfileUrl());
        assertEquals("000001", dtoUserDetails.getFacebookId());
        assertEquals("London", dtoUserDetails.getLocation());
        assertEquals(GenderType.FEMALE.name(), dtoUserDetails.getGender());
        assertEquals("07/12/1980", dtoUserDetails.getBirthDay());
        assertEquals(SocialInfoType.Facebook, dtoUserDetails.getSocialInfoType());
    }
}