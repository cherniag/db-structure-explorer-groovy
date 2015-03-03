package mobi.nowtechnologies.server.assembler;

import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.social.FacebookUserInfo;
import mobi.nowtechnologies.server.persistence.domain.social.GooglePlusUserInfo;
import mobi.nowtechnologies.server.persistence.repository.social.FacebookUserInfoRepository;
import mobi.nowtechnologies.server.persistence.repository.social.GooglePlusUserInfoRepository;
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
    private FacebookUserInfoRepository facebookUserInfoRepository;

    @Resource
    private GooglePlusUserInfoRepository googlePlusUserInfoRepository;

    public UserDetailsDto toUserDetailsDto(User user) {
        if (ProviderType.FACEBOOK == user.getProvider()) {
            FacebookUserInfo facebookUserInfo = facebookUserInfoRepository.findByUser(user);
            if (facebookUserInfo != null) {
                return convertFacebookInfoToDetails(facebookUserInfo);
            }
        }

        if (ProviderType.GOOGLE_PLUS == user.getProvider()) {
            GooglePlusUserInfo googlePlusUserInfo = googlePlusUserInfoRepository.findByUser(user);
            if (googlePlusUserInfo != null) {
                return convertGooglePlusInfoToDetails(googlePlusUserInfo);
            }
        }
        return null;
    }

    private UserDetailsDto convertGooglePlusInfoToDetails(GooglePlusUserInfo googlePlusUserInfo) {
        GooglePlusUserDetailsDto result = new GooglePlusUserDetailsDto();
        result.setEmail(googlePlusUserInfo.getEmail());
        result.setUserName(googlePlusUserInfo.getDisplayName());
        result.setProfileUrl(googlePlusUserInfo.getPicture());
        result.setGooglePlusId(googlePlusUserInfo.getGooglePlusId());
        result.setFirstName(googlePlusUserInfo.getGivenName());
        result.setSurname(googlePlusUserInfo.getFamilyName());
        result.setGender(googlePlusUserInfo.getGender());
        result.setLocation(googlePlusUserInfo.getLocation());
        result.setBirthDay(convertBirthday(googlePlusUserInfo.getBirthday()));
        return result;
    }

    private FacebookUserDetailsDto convertFacebookInfoToDetails(FacebookUserInfo details) {
        FacebookUserDetailsDto result = new FacebookUserDetailsDto();
        result.setUserName(details.getUserName());
        result.setFirstName(details.getFirstName());
        result.setSurname(details.getSurname());
        result.setEmail(details.getEmail());
        result.setProfileUrl(details.getProfileUrl());
        result.setFacebookId(details.getFacebookId());
        result.setLocation(details.getCity());
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
