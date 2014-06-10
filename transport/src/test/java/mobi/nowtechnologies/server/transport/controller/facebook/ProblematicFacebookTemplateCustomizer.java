package mobi.nowtechnologies.server.transport.controller.facebook;

import org.springframework.http.HttpStatus;

public class ProblematicFacebookTemplateCustomizer extends AbstractFacebookTemplateCustomizerImpl {
    public ProblematicFacebookTemplateCustomizer(String fbToken) {
        super(fbToken);
    }

    @Override
    protected String provideResourceName() {
        return "facebook/failure.json";
    }

    @Override
    protected HttpStatus getRespondStatus() {
        return HttpStatus.BAD_REQUEST;
    }
}
