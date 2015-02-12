import mobi.nowtechnologies.server.shared.message.CommunityResourceBundleMessageSourceImpl;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;

import java.util.Arrays;
import java.util.Locale;
import java.util.Properties;

import static java.util.Locale.ENGLISH;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

// @author Titov Mykhaylo (titov) on 06.01.2015.
//To run this test in idea IDE please run following command: maven -Dtest=ReloadableMessageSourceIT test -DfailIfNoTests=false
@RunWith(Theories.class)
public class ReloadableMessageSourceIT {
    Logger logger = LoggerFactory.getLogger(this.getClass());

    @DataPoints
    public static String[][] baseNames = {
            {"classpath:services", "classpath:props/autotest/conf/services", "classpath:env/autotest/conf/services"},
            {"classpath:services", "classpath:props/cherry/conf/services", "classpath:env/cherry/conf/services"},
            {"classpath:services", "classpath:props/cucumber/conf/services", "classpath:env/cucumber/conf/services"},
            {"classpath:services", "classpath:props/kiwi/conf/services", "classpath:env/kiwi/conf/services"},
            {"classpath:services", "classpath:props/lime/conf/services", "classpath:env/lime/conf/services"},
            {"classpath:services", "classpath:props/orange/conf/services", "classpath:env/orange/conf/services"},
            {"classpath:services", "classpath:props/rage/conf/services", "classpath:env/rage/conf/services"},
            {"classpath:services", "classpath:props/staging/conf/services", "classpath:env/staging/conf/services"},
            {"classpath:services", "classpath:props/potato/conf/services", "classpath:env/potato/conf/services"},

            {"classpath:services", "classpath:props/prod_db1/conf/services", "classpath:env/prod_db1/conf/services"},
            {"classpath:services", "classpath:props/prod_db2/conf/services", "classpath:env/prod_db2/conf/services"},
            {"classpath:services", "classpath:props/prod_jadmin/conf/services", "classpath:env/prod_jadmin/conf/services"},
            {"classpath:services", "classpath:props/prod_trackrepo/conf/services", "classpath:env/prod_trackrepo/conf/services"},

            //admin
            {"classpath:admin/src/main/webapp/i18n/messages", "classpath:props/autotest/conf/i18n/admin/messages", "classpath:env/autotest/conf/i18n/admin/messages"},
            {"classpath:admin/src/main/webapp/i18n/messages", "classpath:props/cherry/conf/i18n/admin/messages", "classpath:env/cherry/conf/i18n/admin/messages"},
            {"classpath:admin/src/main/webapp/i18n/messages", "classpath:props/cucumber/conf/i18n/admin/messages", "classpath:env/cucumber/conf/i18n/admin/messages"},
            {"classpath:admin/src/main/webapp/i18n/messages", "classpath:props/kiwi/conf/i18n/admin/messages", "classpath:env/kiwi/conf/i18n/admin/messages"},
            {"classpath:admin/src/main/webapp/i18n/messages", "classpath:props/lime/conf/i18n/admin/messages", "classpath:env/lime/conf/i18n/admin/messages"},
            {"classpath:admin/src/main/webapp/i18n/messages", "classpath:props/orange/conf/i18n/admin/messages", "classpath:env/orange/conf/i18n/admin/messages"},
            {"classpath:admin/src/main/webapp/i18n/messages", "classpath:props/rage/conf/i18n/admin/messages", "classpath:env/rage/conf/i18n/admin/messages"},
            {"classpath:admin/src/main/webapp/i18n/messages", "classpath:props/staging/conf/i18n/admin/messages", "classpath:env/staging/conf/i18n/admin/messages"},
            {"classpath:admin/src/main/webapp/i18n/messages", "classpath:props/potato/conf/i18n/admin/messages", "classpath:env/potato/conf/i18n/admin/messages"},
            {"classpath:admin/src/main/webapp/i18n/messages", "classpath:props/prod_db1/conf/i18n/admin/messages", "classpath:env/prod_db1/conf/i18n/admin/messages"},
            {"classpath:admin/src/main/webapp/i18n/messages", "classpath:props/prod_db2/conf/i18n/admin/messages", "classpath:env/prod_db2/conf/i18n/admin/messages"},
            {"classpath:admin/src/main/webapp/i18n/messages", "classpath:props/prod_jadmin/conf/i18n/admin/messages", "classpath:env/prod_jadmin/conf/i18n/admin/messages"},
            {"classpath:admin/src/main/webapp/i18n/messages", "classpath:props/prod_trackrepo/conf/i18n/admin/messages", "classpath:env/prod_trackrepo/conf/i18n/admin/messages"},

            //web
            {"classpath:web/src/main/webapp/i18n/messages", "classpath:props/autotest/conf/i18n/web/messages", "classpath:env/autotest/conf/i18n/web/messages"},
            {"classpath:web/src/main/webapp/i18n/messages", "classpath:props/cherry/conf/i18n/web/messages", "classpath:env/cherry/conf/i18n/web/messages"},
            {"classpath:web/src/main/webapp/i18n/messages", "classpath:props/cucumber/conf/i18n/web/messages", "classpath:env/cucumber/conf/i18n/web/messages"},
            {"classpath:web/src/main/webapp/i18n/messages", "classpath:props/kiwi/conf/i18n/web/messages", "classpath:env/kiwi/conf/i18n/web/messages"},
            {"classpath:web/src/main/webapp/i18n/messages", "classpath:props/lime/conf/i18n/web/messages", "classpath:env/lime/conf/i18n/web/messages"},
            {"classpath:web/src/main/webapp/i18n/messages", "classpath:props/orange/conf/i18n/web/messages", "classpath:env/orange/conf/i18n/web/messages"},
            {"classpath:web/src/main/webapp/i18n/messages", "classpath:props/rage/conf/i18n/web/messages", "classpath:env/rage/conf/i18n/web/messages"},
            {"classpath:web/src/main/webapp/i18n/messages", "classpath:props/staging/conf/i18n/web/messages", "classpath:env/staging/conf/i18n/web/messages"},
            {"classpath:web/src/main/webapp/i18n/messages", "classpath:props/potato/conf/i18n/web/messages", "classpath:env/potato/conf/i18n/web/messages"},
            {"classpath:web/src/main/webapp/i18n/messages", "classpath:props/prod_db1/conf/i18n/web/messages", "classpath:env/prod_db1/conf/i18n/web/messages"},
            {"classpath:web/src/main/webapp/i18n/messages", "classpath:props/prod_db2/conf/i18n/web/messages", "classpath:env/prod_db2/conf/i18n/web/messages"},
            {"classpath:web/src/main/webapp/i18n/messages", "classpath:props/prod_jadmin/conf/i18n/web/messages", "classpath:env/prod_jadmin/conf/i18n/web/messages"},
            {"classpath:web/src/main/webapp/i18n/messages", "classpath:props/prod_trackrepo/conf/i18n/web/messages", "classpath:env/prod_trackrepo/conf/i18n/web/messages"}
    };

    @DataPoints public static String[] communities = {"o2", "vf_nz", "demo", "mtvnz", "hl_uk", "mtv1", "demo", "demo2", "demo3", "demo4", "demo5", "demo6"};
    @DataPoints public static Locale[] locales = {ENGLISH};
    ResourceLoader resourceLoader = new DefaultResourceLoader();

    class CustomReloadableResourceBundleMessageSource extends ReloadableResourceBundleMessageSource {
        Properties getAllProperties(Locale locale) {
            return getMergedProperties(locale).getProperties();
        }
    }

    @Theory
    //To run this test in idea IDE please run following command: maven -Dtest=ReloadableMessageSourceIT test -DfailIfNoTests=false
    public void shouldConfirmNoDiffAsIs(String[] baseNames, String community, Locale locale) throws Exception {
        //given
        CustomReloadableResourceBundleMessageSource oldCustomReloadableResourceBundleMessageSource = new CustomReloadableResourceBundleMessageSource();
        oldCustomReloadableResourceBundleMessageSource.setBasenames(baseNames[1], baseNames[0]);

        CommunityResourceBundleMessageSourceImpl oldCommunityResourceBundleMessageSource = new CommunityResourceBundleMessageSourceImpl();
        oldCommunityResourceBundleMessageSource.setReloadableResourceBundleMessageSource(oldCustomReloadableResourceBundleMessageSource);

        CustomReloadableResourceBundleMessageSource newCustomReloadableResourceBundleMessageSource = new CustomReloadableResourceBundleMessageSource();
        newCustomReloadableResourceBundleMessageSource.setBasenames(baseNames[2], baseNames[0]);

        StandardPBEStringEncryptor stringEncryptor = new StandardPBEStringEncryptor();
        stringEncryptor.setPassword("gggg");

        CommunityResourceBundleMessageSourceImpl newCommunityResourceBundleMessageSource = new CommunityResourceBundleMessageSourceImpl();
        newCommunityResourceBundleMessageSource.setReloadableResourceBundleMessageSource(newCustomReloadableResourceBundleMessageSource);
        newCommunityResourceBundleMessageSource.setStringEncryptor(stringEncryptor);

        Properties allOldLocalProperties = oldCustomReloadableResourceBundleMessageSource.getAllProperties(locale);

        for (String oldPropertyName : allOldLocalProperties.stringPropertyNames()) {
            //when
            String oldMessage = oldCommunityResourceBundleMessageSource.getMessage(community, oldPropertyName, null, null, locale);
            String newMessage = newCommunityResourceBundleMessageSource.getMessage(community, oldPropertyName, null, null, locale);

            //then
            assertThat("oldPropertyName=" + oldPropertyName + " for " + Arrays.toString(baseNames), newMessage, is(oldMessage));
        }

        assertThat(resourceLoader.getResource(baseNames[0]+".properties").exists(), is(true));
    }
}
