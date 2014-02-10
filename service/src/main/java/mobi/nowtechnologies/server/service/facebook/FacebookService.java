package mobi.nowtechnologies.server.service.facebook;

import com.google.common.annotations.VisibleForTesting;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.social.FBUserInfo;
import mobi.nowtechnologies.server.persistence.repository.FBUserInfoRepository;
import mobi.nowtechnologies.server.persistence.repository.UserRepository;
import mobi.nowtechnologies.server.shared.enums.ProviderType;
import org.springframework.social.facebook.api.FacebookProfile;
import org.springframework.social.facebook.api.impl.FacebookTemplate;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

public class FacebookService {

    @Resource
    private FBUserInfoRepository fbDetailsRepository;

    @Resource
    private UserRepository userRepository;

    private FacebookTemplateCustomizer templateCustomizer = new EmptyFacebookTemplateCustomizer();

    @VisibleForTesting
    public void setTemplateCustomizer(FacebookTemplateCustomizer templateCustomizer) {
        this.templateCustomizer = templateCustomizer;
    }

    @Transactional
    public void saveFacebookInfoForUser(User user, FacebookProfile profile) {
        fbDetailsRepository.deleteForUser(user);
        User refreshedUser = userRepository.findOne(user.getId());
        FBUserInfo details = buildUserDetailsFromProfile(refreshedUser, profile);
        assignProviderInfo(refreshedUser, profile);
        userRepository.save(refreshedUser);
        fbDetailsRepository.save(details);
    }

    private FBUserInfo buildUserDetailsFromProfile(User user, FacebookProfile profile) {
        FBUserInfo details = new FBUserInfo();
        details.setEmail(profile.getEmail());
        details.setFirstName(profile.getFirstName());
        details.setSurname(profile.getLastName());
        details.setFacebookId(profile.getId());
        details.setUserName(profile.getUsername());
        details.setProfileUrl("https://graph.facebook.com/" + profile.getUsername() + "/picture");
        details.setUser(user);
        return details;
    }

    private void assignProviderInfo(User user, FacebookProfile profile) {
        user.setUserName(profile.getEmail());
        user.setMobile(profile.getEmail());
        user.setProvider(ProviderType.FACEBOOK);
    }

    public FacebookProfile getAndValidateFacebookProfile(String facebookAccessToken, String inputFacebookId) {
        FacebookTemplate facebookTemplate = new FacebookTemplate(facebookAccessToken);
        templateCustomizer.customize(facebookTemplate);
        FacebookProfile facebookProfile = facebookTemplate.userOperations().getUserProfile();
        if (!facebookProfile.getId().equals(inputFacebookId)) {
            throw new RuntimeException("Facebook id is not equal to passed id from client");
        }
        return facebookProfile;
    }


}