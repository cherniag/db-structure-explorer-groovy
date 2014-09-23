package mobi.nowtechnologies.server.apptests.googleplus;

import mobi.nowtechnologies.server.service.social.googleplus.GooglePlusTemplateProvider;
import org.springframework.social.google.api.impl.GoogleTemplate;
import org.springframework.social.google.api.plus.Person;
import org.springframework.social.google.api.plus.PlusOperations;
import org.springframework.social.google.api.plus.impl.PlusTemplate;

import javax.annotation.Resource;

public class AppTestGooglePlusTemplateProvider implements GooglePlusTemplateProvider {
    @Resource
    private AppTestGooglePlusTokenService appTestGooglePlusTokenService;

    @Override
    public GoogleTemplate provide(final String accessToken) {
        return new GoogleTemplate() {
            @Override
            public PlusOperations plusOperations() {
                return new PlusTemplate(getRestTemplate(), false) {
                    @Override
                    public Person getGoogleProfile() {
                        return appTestGooglePlusTokenService.parse(accessToken);
                    }
                };
            }
        };
    }
}
