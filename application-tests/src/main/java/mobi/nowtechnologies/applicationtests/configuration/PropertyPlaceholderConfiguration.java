package mobi.nowtechnologies.applicationtests.configuration;

import mobi.nowtechnologies.server.shared.message.CommunityResourceBundleMessageSource;
import mobi.nowtechnologies.server.shared.message.CommunityResourceBundleMessageSourceImpl;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

@Configuration
@PropertySource({"classpath:env.properties", "classpath:application-tests/persistence.properties", "classpath:db.properties", "classpath:common.properties"})
public class PropertyPlaceholderConfiguration {

    @Bean
    public static CommunityResourceBundleMessageSource communityResourceBundleMessageSource() {
        CommunityResourceBundleMessageSourceImpl source = new CommunityResourceBundleMessageSourceImpl();
        source.setBasename("classpath:services");
        source.setDefaultEncoding("utf-8");
        source.setUseCodeAsDefaultMessage(true);
        return source;
    }

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer = new PropertySourcesPlaceholderConfigurer();
        propertySourcesPlaceholderConfigurer.setIgnoreUnresolvablePlaceholders(true);
        return propertySourcesPlaceholderConfigurer;
    }
}
