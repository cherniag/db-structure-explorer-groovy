package mobi.nowtechnologies.server.service.social.googleplus;

import org.springframework.social.oauth2.AbstractOAuth2ApiBinding;

/**
 * Created by oar on 4/28/2014.
 */
public interface AbstractOAuth2ApiBindingCustomizer<T extends AbstractOAuth2ApiBinding> {

    void customize(T template);
}
