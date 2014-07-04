package mobi.nowtechnologies.server.service.social.facebook;

import org.springframework.social.facebook.api.impl.FacebookTemplate;

public class FacebookTemplateProviderImpl implements FacebookTemplateProvider {
    @Override
    public FacebookTemplate provide(String facebookAccessToken) {
        return new FacebookTemplate(facebookAccessToken);
    }
}
