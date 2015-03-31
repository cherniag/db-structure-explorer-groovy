package mobi.nowtechnologies.server.service.social.googleplus.impl.mock;

import mobi.nowtechnologies.server.service.social.googleplus.GooglePlusTemplateProvider;

import javax.annotation.Resource;

import org.springframework.social.google.api.impl.GoogleTemplate;
import org.springframework.social.google.api.plus.Person;
import org.springframework.social.google.api.plus.PlusOperations;
import org.springframework.social.google.api.plus.impl.PlusTemplate;
import org.springframework.web.client.RestClientException;

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
                        Person person = appTestGooglePlusTokenService.parse(accessToken);
                        if (person instanceof FailureGooglePlusPerson) {
                            throw new RestClientException("provider id");
                        }
                        return person;
                    }
                };
            }
        };
    }
}
