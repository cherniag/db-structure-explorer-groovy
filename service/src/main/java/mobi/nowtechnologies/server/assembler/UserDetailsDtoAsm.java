package mobi.nowtechnologies.server.assembler;

import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.SocialNetworkInfo;
import mobi.nowtechnologies.server.persistence.repository.SocialNetworkInfoRepository;
import mobi.nowtechnologies.server.shared.dto.OAuthProvider;
import mobi.nowtechnologies.server.shared.dto.social.FacebookUserDetailsDto;
import mobi.nowtechnologies.server.shared.dto.social.GooglePlusUserDetailsDto;
import mobi.nowtechnologies.server.shared.dto.social.UserDetailsDto;
import mobi.nowtechnologies.server.shared.enums.ProviderType;

import javax.annotation.Resource;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Author: Gennadii Cherniaiev Date: 10/28/2014
 */
public class UserDetailsDtoAsm {

    @Resource
    private SocialNetworkInfoRepository socialNetworkInfoRepository;

    public UserDetailsDto toUserDetailsDto(User user) {
        if (ProviderType.FACEBOOK == user.getProvider()) {
            SocialNetworkInfo facebookUserInfo = socialNetworkInfoRepository.findByUserAndSocialNetwork(user, OAuthProvider.FACEBOOK);
            if (facebookUserInfo != null) {
                return convertFacebookInfoToDetails(facebookUserInfo);
            }
        }

        if (ProviderType.GOOGLE_PLUS == user.getProvider()) {
            SocialNetworkInfo googlePlusUserInfo = socialNetworkInfoRepository.findByUserAndSocialNetwork(user, OAuthProvider.GOOGLE);
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
        result.setGender(googlePlusUserInfo.getGender());
        result.setLocation(googlePlusUserInfo.getLocation());
        result.setBirthDay(convertBirthday(googlePlusUserInfo.getBirthday()));
        return result;
    }

    private FacebookUserDetailsDto convertFacebookInfoToDetails(SocialNetworkInfo details) {
        FacebookUserDetailsDto result = new FacebookUserDetailsDto();
        result.setUserName(details.getUserName());
        result.setFirstName(details.getFirstName());
        result.setSurname(details.getLastName());
        result.setEmail(details.getEmail());
        result.setProfileUrl(details.getProfileUrl());
        result.setFacebookId(details.getSocialNetworkId());
        result.setLocation(details.getLocation());
        result.setGender(details.getGender());
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
