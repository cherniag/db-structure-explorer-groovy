package mobi.nowtechnologies.server.apptests.googleplus;

import mobi.nowtechnologies.server.service.social.googleplus.GooglePlusTemplateProvider;
import org.springframework.social.MissingAuthorizationException;
import org.springframework.social.google.api.impl.GoogleTemplate;
import org.springframework.social.google.api.plus.Person;
import org.springframework.social.google.api.plus.PlusOperations;
import org.springframework.social.google.api.plus.impl.PlusTemplate;
import org.springframework.web.client.RestClientException;

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