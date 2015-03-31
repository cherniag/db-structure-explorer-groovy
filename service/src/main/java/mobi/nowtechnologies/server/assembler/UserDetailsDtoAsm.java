package mobi.nowtechnologies.server.assembler;

import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.social.SocialNetworkInfo;
import mobi.nowtechnologies.server.persistence.social.SocialNetworkInfoRepository;
import mobi.nowtechnologies.server.persistence.social.SocialNetworkType;
import mobi.nowtechnologies.server.shared.dto.social.FacebookUserDetailsDto;
import mobi.nowtechnologies.server.shared.dto.social.GooglePlusUserDetailsDto;
import mobi.nowtechnologies.server.shared.dto.social.UserDetailsDto;
import mobi.nowtechnologies.server.shared.enums.ProviderType;

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
        result.setGender(googlePlusUserInfo.getGenderType().name());
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
        result.setGender(details.getGenderType().name());
        result.setBirthDay(convertBirthday(details.getBirthday()));
        return result;
    }

    private String convertBirthday(Date birthday) {
        if (birthday != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
            return dateFormat.format(birthday);
        }
        return null;
    }
}
