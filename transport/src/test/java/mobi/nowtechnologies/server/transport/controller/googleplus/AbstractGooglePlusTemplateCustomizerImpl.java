package mobi.nowtechnologies.server.transport.controller.googleplus;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import mobi.nowtechnologies.server.service.social.core.AbstractOAuth2ApiBindingCustomizer;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.social.google.api.impl.GoogleTemplate;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

public abstract class AbstractGooglePlusTemplateCustomizerImpl implements AbstractOAuth2ApiBindingCustomizer<GoogleTemplate> {
    private String goolePlusToken;

    public AbstractGooglePlusTemplateCustomizerImpl(String fbToken) {
        this.goolePlusToken = fbToken;
    }

    @Override
    public final void customize(GoogleTemplate template) {
        RestTemplate mock = template.getRestTemplate();
        MockRestServiceServer mockServer = MockRestServiceServer.createServer(mock);
        String responseForGPlusInfo = renderGooglePlusResponse(prepareGooglePlusBody());
        if (!StringUtils.isEmpty(responseForGPlusInfo)) {
            mockServer.expect(requestTo("https://www.googleapis.com/plus/v1/people/me"))
                    .andExpect(method(HttpMethod.GET)).
                    andExpect(header("Authorization", "Bearer " + goolePlusToken)).
                    andRespond(withStatus(getRespondStatus()).body(responseForGPlusInfo).contentType(MediaType.APPLICATION_JSON));
        }
    }

   private String getFileContent(String fileName){

       try {
           File file = new ClassPathResource(fileName).getFile();
           return Files.toString(file, Charsets.UTF_8);
       } catch (IOException e) {
           throw new RuntimeException(e);
       }
   }

    private String prepareGooglePlusBody() {
        return getFileContent(provideResourceNameForGooglePlusResponse());
    }


    protected String renderGooglePlusResponse(String body) {
        return body;
    }

    protected HttpStatus getRespondStatus() {
        return HttpStatus.OK;
    }

        protected abstract String provideResourceNameForGooglePlusResponse();
}
