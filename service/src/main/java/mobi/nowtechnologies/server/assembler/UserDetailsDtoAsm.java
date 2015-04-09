package mobi.nowtechnologies.server.assembler;

import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.shared.enums.ProviderType;
import mobi.nowtechnologies.server.social.domain.SocialNetworkInfo;
import mobi.nowtechnologies.server.social.domain.SocialNetworkInfoRepository;
import mobi.nowtechnologies.server.social.domain.SocialNetworkType;
import mobi.nowtechnologies.server.social.dto.UserDetailsDto;
import mobi.nowtechnologies.server.social.dto.facebook.FacebookUserDetailsDto;
import mobi.nowtechnologies.server.social.dto.googleplus.GooglePlusUserDetailsDto;

import javax.annotation.Resource;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.social.facebook.api.GraphApi;

/**
 * Author: Gennadii Cherniaiev Date: 10/28/2014
 */
public class UserDetailsDtoAsm {

    @Resource
    private SocialNetworkInfoRepository socialNetworkInfoRepository;

    public UserDetailsDto toUserDetailsDto(User user) {
        if (ProviderType.FACEBOOK == user.getProvider()) {
            SocialNetworkInfo facebookUserInfo = socialNetworkInfoRepository.findByUserIdAndSocialNetworkType(user.getId(), SocialNetworkType.FACEBOOK);
            if (facebookUserInfo != null) {
                return convertFacebookInfoToDetails(facebookUserInfo);
            }
        }

        if (ProviderType.GOOGLE_PLUS == user.getProvider()) {
            SocialNetworkInfo googlePlusUserInfo = socialNetworkInfoRepository.findByUserIdAndSocialNetworkType(user.getId(), SocialNetworkType.GOOGLE);
            if (googlePlusUserInfo != null) {
                return convertGooglePlusInfoToDetails(googlePlusUserInfo);
            }
        }
        return null;
    }

    private GooglePlusUserDetailsDto convertGooglePlusInfoToDetails(SocialNetworkInfo googlePlusUserInfo) {
        GooglePlusUserDetailsDto result = new GooglePlusUserDetailsDto();
        result.setEmail(googlePlusUserInfo.getEmail());
        result.setUserName(googlePlusUserInfo.getUserName());
        result.setProfileUrl(googlePlusUserInfo.getProfileImageUrl());
        result.setGooglePlusId(googlePlusUserInfo.getSocialNetworkId());
        result.setFirstName(googlePlusUserInfo.getFirstName());
        result.setSurname(googlePlusUserInfo.getLastName());
        result.setGender(getGenderType(googlePlusUserInfo));
        result.setLocation(googlePlusUserInfo.getCity());
        result.setBirthDay(convertBirthday(googlePlusUserInfo.getBirthday()));
        return result;
    }

    private FacebookUserDetailsDto convertFacebookInfoToDetails(SocialNetworkInfo details) {
        FacebookUserDetailsDto result = new FacebookUserDetailsDto();
        result.setUserName(details.getUserName());
        result.setFirstName(details.getFirstName());
        result.setSurname(details.getLastName());
        result.setEmail(details.getEmail());
        result.setProfileUrl(String.format("%s%s/picture?type=large", GraphApi.GRAPH_API_URL, details.getSocialNetworkId()));
        result.setFacebookId(details.getSocialNetworkId());
        result.setLocation(details.getCity());
        result.setGender(getGenderType(details));
        result.setBirthDay(convertBirthday(details.getBirthday()));
        return result;
    }

    private String getGenderType(SocialNetworkInfo socialNetworkInfo) {
        return socialNetworkInfo.getGenderType() != null ? socialNetworkInfo.getGenderType().name() : null;
    }

    private String convertBirthday(Date birthday) {
        if (birthday != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
            return dateFormat.format(birthday);
        }
        return null;
    }
}
