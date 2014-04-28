package mobi.nowtechnologies.server.service.social.core;

import org.springframework.social.oauth2.AbstractOAuth2ApiBinding;

/**
 * Created by oar on 2/7/14.
 */
public class EmptyOAuth2ApiBindingCustomizer implements AbstractOAuth2ApiBindingCustomizer {
    @Override
    public void customize(AbstractOAuth2ApiBinding template) {

    }
}
