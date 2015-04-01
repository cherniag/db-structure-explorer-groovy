package mobi.nowtechnologies.server.service.social.googleplus;

import mobi.nowtechnologies.server.persistence.social.SocialNetworkInfo;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.springframework.util.StringUtils.isEmpty;

public class GooglePlusService {

    @Resource
    GooglePlusClient googlePlusClient;
    private Logger log = LoggerFactory.getLogger(GooglePlusService.class);

    public SocialNetworkInfo getGooglePlusUserInfo(String accessToken, String inputGooglePlusId) {
        SocialNetworkInfo googlePlusProfileInfo = googlePlusClient.getProfileUserInfo(accessToken);
        if (!googlePlusProfileInfo.getSocialNetworkId().equals(inputGooglePlusId)) {
            log.warn("inputGooglePlusId should match id on GooglePlus!");
            throw GooglePlusClient.INVALID_GOOGLE_PLUS_USER_ID;
        }
        if (isEmpty(googlePlusProfileInfo.getEmail())) {
            log.warn("GooglePlus profile should have email!");
            throw GooglePlusClient.EMPTY_GOOGLE_PLUS_EMAIL;
        }

        return googlePlusProfileInfo;
    }

}
