package mobi.nowtechnologies.server.service.facebook;

import org.springframework.social.facebook.api.impl.FacebookTemplate;

/**
 * Created by oar on 2/7/14.
 */
public class EmptyFacebookTemplateCustomizer implements  FacebookTemplateCustomizer {
    @Override
    public void customize(FacebookTemplate template) {

    }
}
