package mobi.nowtechnologies.server.transport.controller.googleplus;

import org.springframework.http.HttpStatus;

public class ProblematicGooglePlusTemplateCustomizer extends AbstractGooglePlusTemplateCustomizerImpl {
    public ProblematicGooglePlusTemplateCustomizer(String accessToken) {
        super(accessToken);
    }

    @Override
    protected String provideResourceName() {
        return "googleplus/failure.json";
    }

    @Override
    protected HttpStatus getRespondStatus() {
        return HttpStatus.BAD_REQUEST;
    }
}
