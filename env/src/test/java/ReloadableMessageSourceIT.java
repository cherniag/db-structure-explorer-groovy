import mobi.nowtechnologies.server.shared.message.CommunityResourceBundleMessageSourceImpl;

import java.util.Arrays;
import java.util.Locale;
import java.util.Properties;
import static java.util.Locale.ENGLISH;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;

import org.junit.*;
import org.junit.experimental.theories.*;
import org.junit.runner.*;
import static org.junit.Assert.*;

import static org.hamcrest.core.Is.is;

// @author Titov Mykhaylo (titov) on 06.01.2015.
//To run this test in idea IDE please run following command: maven -Dtest=ReloadableMessageSourceIT test -DfailIfNoTests=false
@RunWith(Theories.class)
@Ignore
public class ReloadableMessageSourceIT {

    @DataPoints
    public static String[][] baseNames = {
        {"classpath:services","classpath:n/services", "classpath:props/autotest/conf/services", "classpath:env/autotest/conf/services"},
        {"classpath:services","classpath:n/services", "classpath:props/cherry/conf/services", "classpath:env/cherry/conf/services"},
        {"classpath:services","classpath:n/services", "classpath:props/cucumber/conf/services", "classpath:env/cucumber/conf/services"},
        {"classpath:services","classpath:n/services", "classpath:props/kiwi/conf/services", "classpath:env/kiwi/conf/services"},
        {"classpath:services","classpath:n/services", "classpath:props/lime/conf/services", "classpath:env/lime/conf/services"},
        {"classpath:services","classpath:n/services", "classpath:props/orange/conf/services", "classpath:env/orange/conf/services"},
        {"classpath:services","classpath:n/services", "classpath:props/rage/conf/services", "classpath:env/rage/conf/services"},
        {"classpath:services","classpath:n/services", "classpath:props/staging/conf/services", "classpath:env/staging/conf/services"},
        {"classpath:services","classpath:n/services", "classpath:props/potato/conf/services", "classpath:env/potato/conf/services"},
        {"classpath:services","classpath:n/services", "classpath:props/prod_db1/conf/services", "classpath:env/prod_db1/conf/services"},
        {"classpath:services","classpath:n/services", "classpath:props/prod_db2/conf/services", "classpath:env/prod_db2/conf/services"},
        {"classpath:services","classpath:n/services", "classpath:props/prod_jadmin/conf/services", "classpath:env/prod_jadmin/conf/services"},
        {"classpath:services","classpath:n/services", "classpath:props/prod_trackrepo/conf/services", "classpath:env/prod_trackrepo/conf/services"},
    };

    @DataPoints
    public static String[] communities = {"o2", "vf_nz", "demo", "mtvnz", "hl_uk", "mtv1", "demo", "demo2", "demo3", "demo4", "demo5", "demo6"};
    @DataPoints
    public static Locale[] locales = {null, ENGLISH};
    private static Logger logger = LoggerFactory.getLogger(ReloadableMessageSourceIT.class);
    ResourceLoader resourceLoader = new DefaultResourceLoader();

    @Theory
    //To run this test in idea IDE please run following command: maven -Dtest=ReloadableMessageSourceIT test -DfailIfNoTests=false
    public void shouldConfirmNoDiffAsIs(String[] baseNames, String community, Locale locale) throws Exception {
        //given
        CustomReloadableResourceBundleMessageSource oldCustomReloadableResourceBundleMessageSource = new CustomReloadableResourceBundleMessageSource();
        oldCustomReloadableResourceBundleMessageSource.setBasenames(baseNames[2], baseNames[0]);

        CommunityResourceBundleMessageSourceImpl oldCommunityResourceBundleMessageSource = new CommunityResourceBundleMessageSourceImpl();
        oldCommunityResourceBundleMessageSource.setReloadableResourceBundleMessageSource(oldCustomReloadableResourceBundleMessageSource);

        CustomReloadableResourceBundleMessageSource newCustomReloadableResourceBundleMessageSource = new CustomReloadableResourceBundleMessageSource();
        newCustomReloadableResourceBundleMessageSource.setBasenames(baseNames[3], baseNames[1], baseNames[0]);

        StandardPBEStringEncryptor stringEncryptor = new StandardPBEStringEncryptor();
        stringEncryptor.setPassword("gggg");

        CommunityResourceBundleMessageSourceImpl newCommunityResourceBundleMessageSource = new CommunityResourceBundleMessageSourceImpl();
        newCommunityResourceBundleMessageSource.setReloadableResourceBundleMessageSource(newCustomReloadableResourceBundleMessageSource);
        newCommunityResourceBundleMessageSource.setStringEncryptor(stringEncryptor);

        Properties allOldLocalProperties = oldCustomReloadableResourceBundleMessageSource.getAllProperties(locale);

        for (String oldPropertyName : allOldLocalProperties.stringPropertyNames()) {
            //when
            String oldMessage = oldCommunityResourceBundleMessageSource.getMessage(community, oldPropertyName, null, null, locale).trim();
            String newMessage = newCommunityResourceBundleMessageSource.getMessage(community, oldPropertyName, null, null, locale).trim();

            //then
            try {
                assertEquals("oldPropertyName=" + oldPropertyName + " for " + Arrays.toString(baseNames), oldMessage, newMessage);
            } catch (Throwable e) {
                logger.error(String.format("%s, %s, %s, %s, %s", Arrays.toString(baseNames), oldPropertyName, oldMessage, newMessage, community));
                throw e;
            }
        }

        assertThat(resourceLoader.getResource(baseNames[0] + ".properties").exists(), is(true));
    }

    class CustomReloadableResourceBundleMessageSource extends ReloadableResourceBundleMessageSource {

        Properties getAllProperties(Locale locale) {
            return getMergedProperties(locale == null ?
                                       Locale.getDefault() :
                                       locale).getProperties();
        }
    }
}
