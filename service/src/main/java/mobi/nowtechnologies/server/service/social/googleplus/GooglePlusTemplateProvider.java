package mobi.nowtechnologies.server.service.social.googleplus;

import org.springframework.social.google.api.impl.GoogleTemplate;

public interface GooglePlusTemplateProvider {

    GoogleTemplate provide(String accessToken);
}
