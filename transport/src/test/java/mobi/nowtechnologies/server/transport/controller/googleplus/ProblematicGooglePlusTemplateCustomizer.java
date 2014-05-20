package mobi.nowtechnologies.server.transport.controller.googleplus;

import org.springframework.http.HttpStatus;

public class ProblematicGooglePlusTemplateCustomizer extends AbstractGooglePlusTemplateCustomizerImpl {
    public ProblematicGooglePlusTemplateCustomizer(String accessToken) {
        super(accessToken);
    }

    @Override
    protected String provideResourceNameForEmailResponse() {
        return "googleplus/failure.json";
    }

    @Override
    protected String provideResourceNameForGooglePlusResponse() {
        return "googleplus/failureGooglePlus.json";
    }

    @Override
    protected HttpStatus getRespondStatus() {
        return HttpStatus.BAD_REQUEST;
    }
}
