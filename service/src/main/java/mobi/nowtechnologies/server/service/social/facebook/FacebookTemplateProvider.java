package mobi.nowtechnologies.server.service.social.facebook;

import org.springframework.social.facebook.api.impl.FacebookTemplate;

public interface FacebookTemplateProvider {
    FacebookTemplate provide(String facebookAccessToken);
}
