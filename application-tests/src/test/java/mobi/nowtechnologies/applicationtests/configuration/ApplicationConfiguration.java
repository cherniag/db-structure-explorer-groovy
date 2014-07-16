package mobi.nowtechnologies.applicationtests.configuration;

import mobi.nowtechnologies.server.apptests.facebook.AppTestDummyFacebookTokenComposer;
import org.springframework.context.annotation.*;
import org.springframework.web.client.RestTemplate;

@Configuration
@ComponentScan(
        basePackages = {
                "mobi.nowtechnologies.applicationtests.services",
                "mobi.nowtechnologies.applicationtests.features"
        })
@Import(PropertyPlaceholderConfiguration.class)
@ImportResource({"classpath:META-INF/dao.xml"})
public class ApplicationConfiguration {
    @Bean
    public RestTemplate getRestTemplate() {
        return new RestTemplate();
    }

    @Bean
    public AppTestDummyFacebookTokenComposer getDummyFacebookTokenComposer() {
        return new AppTestDummyFacebookTokenComposer();
    }
}
