package mobi.nowtechnologies.applicationtests.configuration;

import mobi.nowtechnologies.server.apptests.email.MailModelSerializer;
import mobi.nowtechnologies.server.apptests.facebook.AppTestDummyFacebookTokenComposer;
import mobi.nowtechnologies.server.apptests.provider.o2.PhoneExtensionsService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.client.RestTemplate;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@ComponentScan(
        basePackages = {
                "mobi.nowtechnologies.applicationtests.services",
                "mobi.nowtechnologies.applicationtests.features"
        })
@Import(PropertyPlaceholderConfiguration.class)
@ImportResource({"classpath:META-INF/dao.xml"})
@EnableTransactionManagement(proxyTargetClass = true)
public class ApplicationConfiguration {
    @Bean
    public RestTemplate getRestTemplate() {
        return new RestTemplate();
    }

    @Bean
    public AppTestDummyFacebookTokenComposer getDummyFacebookTokenComposer() {
        return new AppTestDummyFacebookTokenComposer();
    }

    @Bean
    public MailModelSerializer getMailModelSerializer() {
        return new MailModelSerializer();
    }

    @Bean
    public PhoneExtensionsService getPhoneExtensionsService() {
        return new PhoneExtensionsService();
    }

    @Bean(name = "applicationTestsEntityManager")
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(@Value("${jdbc.url}") String url,
                                                                       @Value("${jdbc.username}") String username,
                                                                       @Value("${jdbc.password}") String password) {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource(url, username, password));
        em.setPackagesToScan(
                "mobi.nowtechnologies.applicationtests",
                "mobi.nowtechnologies.server.persistence.domain.apptests"
        );
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
        properties.setProperty("hibernate.hbm2ddl.auto", "update");
        properties.setProperty("hibernate.show_sql", "true");
        properties.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQL5Dialect");
        return properties;
    }
}
