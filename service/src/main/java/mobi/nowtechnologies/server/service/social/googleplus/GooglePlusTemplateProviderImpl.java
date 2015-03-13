package mobi.nowtechnologies.server.service.social.googleplus;

import org.springframework.social.google.api.impl.GoogleTemplate;

public class GooglePlusTemplateProviderImpl implements GooglePlusTemplateProvider {

    @Override
    public GoogleTemplate provide(String accessToken) {
        return new GoogleTemplate(accessToken);
    }
}
