package mobi.nowtechnologies.server.service.facebook;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.social.FacebookUserInfo;
import mobi.nowtechnologies.server.persistence.repository.FacebookUserInfoRepository;
import mobi.nowtechnologies.server.persistence.repository.UserRepository;
import mobi.nowtechnologies.server.service.facebook.exception.FacebookForbiddenException;
import mobi.nowtechnologies.server.service.facebook.exception.FacebookSocialException;
import mobi.nowtechnologies.server.shared.CollectionUtils;
import mobi.nowtechnologies.server.shared.enums.ProviderType;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.social.SocialException;
import org.springframework.social.facebook.api.FacebookProfile;
import org.springframework.social.facebook.api.GraphApi;
import org.springframework.social.facebook.api.Reference;
import org.springframework.social.facebook.api.impl.FacebookTemplate;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

public class FacebookService {

    @Resource
    private FacebookUserInfoRepository facebookUserInfoRepository;

    @Resource
    private UserRepository userRepository;

    private FacebookTemplateCustomizer templateCustomizer = new EmptyFacebookTemplateCustomizer();

    private Logger logger = LoggerFactory.getLogger(getClass());

    @VisibleForTesting
    public void setTemplateCustomizer(FacebookTemplateCustomizer templateCustomizer) {
        this.templateCustomizer = templateCustomizer;
    }

    @Transactional
    public void saveFacebookInfoForUser(User inputUser, FacebookProfile profile) {
        facebookUserInfoRepository.deleteForUser(inputUser);
        User refreshedUser = userRepository.findOne(inputUser.getId());
        FacebookUserInfo details = buildUserDetailsFromProfile(refreshedUser, profile);
        assignProviderInfo(refreshedUser, profile);
        userRepository.save(refreshedUser);
        facebookUserInfoRepository.save(details);
    }

    private FacebookUserInfo buildUserDetailsFromProfile(User user, FacebookProfile profile) {
        FacebookUserInfo details = new FacebookUserInfo();
        details.setEmail(profile.getEmail());
        details.setFirstName(profile.getFirstName());
        details.setSurname(profile.getLastName());
        details.setFacebookId(profile.getId());
        details.setUserName(profile.getUsername());
        details.setProfileUrl(GraphApi.GRAPH_API_URL + profile.getUsername() + "/picture");
        details.setUser(user);
        Reference loc = profile.getLocation();
        if (loc != null) {
            String cityWithCountry = loc.getName();
            if (!StringUtils.isEmpty(cityWithCountry)) {
                Iterable<String> resultOfSplit = Splitter.on(',').omitEmptyStrings().trimResults().split(cityWithCountry);
                List<String> result = Lists.newArrayList(resultOfSplit);
                details.setCity(CollectionUtils.get(result, 0, null));
                details.setCountry(CollectionUtils.get(result, 1, null));
            }
        }
        return details;
    }

    private void assignProviderInfo(User user, FacebookProfile profile) {
        user.setUserName(profile.getEmail());
        user.setProvider(ProviderType.FACEBOOK);
    }

    public FacebookProfile getAndValidateFacebookProfile(String facebookAccessToken, String inputFacebookId) {
        try {
            FacebookTemplate facebookTemplate = new FacebookTemplate(facebookAccessToken);
            templateCustomizer.customize(facebookTemplate);
            FacebookProfile facebookProfile = facebookTemplate.userOperations().getUserProfile();
            if (!facebookProfile.getId().equals(inputFacebookId)) {
                throw new FacebookForbiddenException("invalid user facebook id");
            }
            return facebookProfile;
        } catch (SocialException se) {
            logger.error("ERROR", se);
            throw new FacebookSocialException("invalid authorization token", se);
        }
    }


}
