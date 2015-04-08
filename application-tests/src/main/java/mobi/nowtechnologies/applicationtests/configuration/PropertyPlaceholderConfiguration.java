package mobi.nowtechnologies.applicationtests.configuration;

import mobi.nowtechnologies.server.shared.message.CommunityResourceBundleMessageSource;
import mobi.nowtechnologies.server.shared.message.CommunityResourceBundleMessageSourceImpl;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

@Configuration
@PropertySource({"classpath:env.properties", "classpath:application-tests/persistence.properties", "classpath:db.properties", "classpath:common.properties"})
public class PropertyPlaceholderConfiguration {

    @Bean
    public static CommunityResourceBundleMessageSource communityResourceBundleMessageSource() {

        ReloadableResourceBundleMessageSource reloadableResourceBundleMessageSource = new ReloadableResourceBundleMessageSource();

        reloadableResourceBundleMessageSource.setBasename("classpath:services");
        reloadableResourceBundleMessageSource.setDefaultEncoding("utf-8");
        reloadableResourceBundleMessageSource.setUseCodeAsDefaultMessage(true);

        CommunityResourceBundleMessageSourceImpl source = new CommunityResourceBundleMessageSourceImpl();
        source.setReloadableResourceBundleMessageSource(reloadableResourceBundleMessageSource);
        return source;
    }

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer = new PropertySourcesPlaceholderConfigurer();
        propertySourcesPlaceholderConfigurer.setIgnoreUnresolvablePlaceholders(true);
        return propertySourcesPlaceholderConfigurer;
    }
}
