package mobi.nowtechnologies.server.service.facebook;

import com.google.common.annotations.VisibleForTesting;
import mobi.nowtechnologies.server.persistence.domain.FBDetails;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.repository.FBDetailsRepository;
import org.springframework.social.facebook.api.FacebookProfile;
import org.springframework.social.facebook.api.impl.FacebookTemplate;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

public class FacebookService {

    @Resource
    private FBDetailsRepository fbDetailsRepository;

    private FacebookTemplateCustomizer templateCustomizer = new EmptyFacebookTemplateCustomizer();

    @VisibleForTesting
    public void setTemplateCustomizer(FacebookTemplateCustomizer templateCustomizer) {
        this.templateCustomizer = templateCustomizer;
    }

    @Transactional
    public void saveFacebookInfoForUser(User user, FacebookProfile profile) {
        fbDetailsRepository.deleteForUser(user);
        FBDetails details = new FBDetails();
        details.setEmail(profile.getEmail());
        details.setFirstName(profile.getFirstName());
        details.setSurname(profile.getLastName());
        details.setFacebookId(profile.getId());
        details.setUserName(profile.getUsername());
        details.setProfileUrl("https://graph.facebook.com/" + profile.getUsername() + "/picture");
        details.setUser(user);
        fbDetailsRepository.save(details);
    }

    public FacebookProfile getAndValidateFacebookProfile(String facebookAccessToken, String facebookId) {
        FacebookTemplate facebookTemplate = new FacebookTemplate(facebookAccessToken);
        templateCustomizer.customize(facebookTemplate);
        FacebookProfile facebookProfile = facebookTemplate.userOperations().getUserProfile();
        if (!facebookProfile.getId().equals(facebookId)) {
            throw new RuntimeException("Facebook id is not equal to passed id from client");
        }
        return facebookProfile;
    }


}
