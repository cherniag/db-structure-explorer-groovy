package mobi.nowtechnologies.applicationtests.configuration;

import mobi.nowtechnologies.applicationtests.services.util.LoggingResponseErrorHandler;
import mobi.nowtechnologies.server.apptests.email.MailModelSerializer;
import mobi.nowtechnologies.server.apptests.provider.o2.O2PhoneExtensionsService;
import mobi.nowtechnologies.server.social.service.facebook.impl.mock.AppTestFacebookTokenService;
import mobi.nowtechnologies.server.social.service.googleplus.impl.mock.AppTestGooglePlusTokenService;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import java.util.Properties;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Charsets;
import org.apache.http.pool.ConnPoolControl;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.xml.Jaxb2RootElementHttpMessageConverter;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.client.RestTemplate;

@Configuration
@ComponentScan(
    basePackages = {"mobi.nowtechnologies.applicationtests.services", "mobi.nowtechnologies.applicationtests.features"})
@Import(PropertyPlaceholderConfiguration.class)
@ImportResource({"classpath:META-INF/dao.xml", "classpath:context/services.xml"
        /*"classpath:META-INF/service-application-tests.xml"*/})
@EnableTransactionManagement(proxyTargetClass = true)
public class ApplicationConfiguration {

    @Bean(name = "mno.RestTemplate")
    public RestTemplate getRestTemplate() {
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
        // just let the developer to debug
        requestFactory.setReadTimeout(120 * 1000);
        ConnPoolControl conManager = (ConnPoolControl) requestFactory.getHttpClient().getConnectionManager();
        conManager.setDefaultMaxPerRoute(20);

        // requestFactory.setOutputStreaming(false);
        RestTemplate restTemplate = new RestTemplate(requestFactory);

        //needed fo xml handling
        restTemplate.getMessageConverters().add(new Jaxb2RootElementHttpMessageConverter());

        //needed to parse non 2xx request body
        restTemplate.setErrorHandler(new LoggingResponseErrorHandler());
        return restTemplate;
    }

    @Bean
    public AppTestFacebookTokenService appTestFacebookTokenService() {
        return new AppTestFacebookTokenService();
    }

    @Bean
    public AppTestGooglePlusTokenService appTestGooglePlusTokenService() {
        return new AppTestGooglePlusTokenService();
    }

    @Bean
    public MailModelSerializer getMailModelSerializer() {
        return new MailModelSerializer();
    }

    @Bean
    public O2PhoneExtensionsService getPhoneExtensionsService() {
        return new O2PhoneExtensionsService();
    }

    @Bean(name = "applicationTestsEntityManager")
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(@Value("${jdbc.url}") String url, @Value("${jdbc.username}") String username, @Value("${jdbc.password}") String password) {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource(url, username, password));
        em.setPackagesToScan(
            // phone generator
            "mobi.nowtechnologies.applicationtests",
            // fat_email
            "mobi.nowtechnologies.server.persistence.apptests",
            // two mno tables
            "mobi.nowtechnologies.server.mno.api.impl.domain",
            // all the tables form persistence artifact
            "mobi.nowtechnologies.server.social.domain",
            "mobi.nowtechnologies.server.device.domain",
            "mobi.nowtechnologies.server.versioncheck.domain",
            "mobi.nowtechnologies.server.persistence.domain");
        em.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        em.setJpaProperties(additionalProperties());
        return em;
    }

    @Bean(name = "applicationTestsTransactionManager")
    public PlatformTransactionManager transactionManager(@Qualifier("applicationTestsEntityManager") EntityManagerFactory emf) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(emf);
        return transactionManager;
    }

    @Bean
    public PersistenceExceptionTranslationPostProcessor exceptionTranslation() {
        return new PersistenceExceptionTranslationPostProcessor();
    }

    @Bean(name = "webReloadableResourceBundleMessageSource")
    public MessageSource webReloadableResourceBundleMessageSource() {
        ReloadableResourceBundleMessageSource source = new ReloadableResourceBundleMessageSource();
        source.setBasename("classpath:web/application_tests/i18n/messages");
        source.setCacheSeconds(-1);
        source.setDefaultEncoding(Charsets.UTF_8.name());
        return source;
    }

    @Bean
    public ObjectMapper jacksonObjectMapper() {
        return new ObjectMapper();
    }

    private DataSource dataSource(String url, String username, String password) {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("com.mysql.jdbc.Driver");
        dataSource.setUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        return dataSource;
    }

    Properties additionalProperties() {
        Properties properties = new Properties();
        properties.setProperty("hibernate.hbm2ddl.auto", "validate");
        properties.setProperty("hibernate.show_sql", "false");
        properties.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQL5Dialect");
        return properties;
    }
}
