package mobi.nowtechnologies.server.transport.controller.facebook;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import mobi.nowtechnologies.server.service.social.core.AbstractOAuth2ApiBindingCustomizer;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.social.facebook.api.impl.FacebookTemplate;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

public abstract class AbstractFacebookTemplateCustomizerImpl implements AbstractOAuth2ApiBindingCustomizer<FacebookTemplate> {
    private String fbToken;

    public AbstractFacebookTemplateCustomizerImpl(String fbToken) {
        this.fbToken = fbToken;
    }

    @Override
    public final void customize(FacebookTemplate template) {
        RestTemplate mock = template.getRestTemplate();
        MockRestServiceServer mockServer = MockRestServiceServer.createServer(mock);
        String response = render(prepareBody());

        mockServer.expect(requestTo("https://graph.facebook.com/me"))
                .andExpect(method(HttpMethod.GET)).
                andExpect(header("Authorization", "OAuth " + fbToken)).
                andRespond(withStatus(getRespondStatus()).body(response).contentType(MediaType.APPLICATION_JSON));
    }

    private String prepareBody() {
        try {
            File file = new ClassPathResource(provideResourceName()).getFile();
            return Files.toString(file, Charsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected String render(String body) {
        return body;
    }

    protected HttpStatus getRespondStatus() {
        return HttpStatus.OK;
    }

    protected abstract String provideResourceName();
}
