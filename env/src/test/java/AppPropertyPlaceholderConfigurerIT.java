import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.core.io.FileSystemResourceLoader;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.Properties;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

// @author Titov Mykhaylo (titov) on 06.01.2015.
@RunWith(Theories.class)
public class AppPropertyPlaceholderConfigurerIT {

    @DataPoints
    public static String[][] filePaths = {
            {"classpath:application.properties", "classpath:props/autotest/conf/application.properties", "classpath:env/autotest/conf/application.properties"},
            {"classpath:application.properties", "classpath:props/cherry/conf/application.properties", "classpath:env/cherry/conf/application.properties"},
            {"classpath:application.properties", "classpath:props/cucumber/conf/application.properties", "classpath:env/cucumber/conf/application.properties"},
            {"classpath:application.properties", "classpath:props/kiwi/conf/application.properties", "classpath:env/kiwi/conf/application.properties"},
            {"classpath:application.properties", "classpath:props/lime/conf/application.properties", "classpath:env/lime/conf/application.properties"},
            {"classpath:application.properties", "classpath:props/orange/conf/application.properties", "classpath:env/orange/conf/application.properties"},
            {"classpath:application.properties", "classpath:props/rage/conf/application.properties", "classpath:env/rage/conf/application.properties"},
            {"classpath:application.properties", "classpath:props/staging/conf/application.properties", "classpath:env/staging/conf/application.properties"},

            {"classpath:application.properties", "classpath:props/prod_db1/conf/application.properties", "classpath:env/prod_db1/conf/application.properties"},
            {"classpath:application.properties", "classpath:props/prod_db2/conf/application.properties", "classpath:env/prod_db2/conf/application.properties"},
            {"classpath:application.properties", "classpath:props/prod_jadmin/conf/application.properties", "classpath:env/prod_jadmin/conf/application.properties"},
            {"classpath:application.properties", "classpath:props/prod_trackrepo/conf/application.properties", "classpath:env/prod_trackrepo/conf/application.properties"},

            {"classpath:trackrepo-application.properties", "classpath:props/autotest/conf/trackrepo-application.properties", "classpath:env/autotest/conf/trackrepo-application.properties"},
            {"classpath:trackrepo-application.properties", "classpath:props/cherry/conf/trackrepo-application.properties", "classpath:env/cherry/conf/trackrepo-application.properties"},
            {"classpath:trackrepo-application.properties", "classpath:props/cucumber/conf/trackrepo-application.properties", "classpath:env/cucumber/conf/trackrepo-application.properties"},
            {"classpath:trackrepo-application.properties", "classpath:props/kiwi/conf/trackrepo-application.properties", "classpath:env/kiwi/conf/trackrepo-application.properties"},
            {"classpath:trackrepo-application.properties", "classpath:props/lime/conf/trackrepo-application.properties", "classpath:env/lime/conf/trackrepo-application.properties"},
            {"classpath:trackrepo-application.properties", "classpath:props/orange/conf/trackrepo-application.properties", "classpath:env/orange/conf/trackrepo-application.properties"},
            {"classpath:trackrepo-application.properties", "classpath:props/rage/conf/trackrepo-application.properties", "classpath:env/rage/conf/trackrepo-application.properties"},
            {"classpath:trackrepo-application.properties", "classpath:props/staging/conf/trackrepo-application.properties", "classpath:env/staging/conf/trackrepo-application.properties"},

            {"classpath:trackrepo-application.properties", "classpath:props/prod_jadmin/conf/trackrepo-application.properties", "classpath:env/prod_jadmin/conf/trackrepo-application.properties"},
            {"classpath:trackrepo-application.properties", "classpath:props/prod_trackrepo/conf/trackrepo-application.properties", "classpath:env/prod_trackrepo/conf/trackrepo-application.properties"},
    };

    class AppPropertyResourceConfigurer extends PropertyPlaceholderConfigurer{
        @Override
        public Properties mergeProperties() throws IOException {
            return super.mergeProperties();
        }
    }

    @Theory
    public void shouldConfirmNoDiffAsIs(String[] filePaths) throws Exception {
        //given
        AppPropertyResourceConfigurer oldAppPropertyPlaceholderConfigurer = new AppPropertyResourceConfigurer();
        AppPropertyResourceConfigurer newAppPropertyPlaceholderConfigurer = new AppPropertyResourceConfigurer();

        FileSystemResourceLoader fileSystemResourceLoader = new FileSystemResourceLoader();

        oldAppPropertyPlaceholderConfigurer.setLocations(new Resource[]{fileSystemResourceLoader.getResource(filePaths[0]), fileSystemResourceLoader.getResource(filePaths[1])});
        newAppPropertyPlaceholderConfigurer.setLocations(new Resource[]{fileSystemResourceLoader.getResource(filePaths[0]), fileSystemResourceLoader.getResource(filePaths[2])});

        //when
        Properties oldProperties = oldAppPropertyPlaceholderConfigurer.mergeProperties();
        Properties newProperties = newAppPropertyPlaceholderConfigurer.mergeProperties();

        //then
        assertThat(newProperties.size(), is(oldProperties.size()));

        for (String oldPropertyKey : oldProperties.stringPropertyNames()) {
            assertThat(newProperties.getProperty(oldPropertyKey).trim(), is(oldProperties.getProperty(oldPropertyKey).trim()));
        }
    }
}


//Replace with this version when turning on properties normalizer script on maven build

//import org.junit.experimental.theories.DataPoints;
//import org.junit.experimental.theories.Theories;
//import org.junit.experimental.theories.Theory;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
//import org.springframework.core.io.FileSystemResourceLoader;
//import org.springframework.core.io.Resource;
//
//import java.io.IOException;
//import java.util.Properties;
//
//import static org.hamcrest.core.Is.is;
//import static org.junit.Assert.assertThat;
//
//// @author Titov Mykhaylo (titov) on 06.01.2015.
//@RunWith(Theories.class)
//public class AppPropertyPlaceholderConfigurerIT {
//
//    private FileSystemResourceLoader fileSystemResourceLoader = new FileSystemResourceLoader();
//
//    private static final String APP_COMMON_PROPERTIES = "classpath:application.properties";
//    private static final String TRACKREPO_COMMON_PROPERTIES = "classpath:trackrepo-application.properties";
//    private static final String ENV_COMMON_PROPERTIES = "classpath:env/COMMON/conf/application.properties";
//
//    @DataPoints
//    public static String[][] filePaths = {
//            {APP_COMMON_PROPERTIES, ENV_COMMON_PROPERTIES, "classpath:props/autotest/conf/application.properties",       "classpath:env/autotest/conf/application.properties"},
//            {APP_COMMON_PROPERTIES, ENV_COMMON_PROPERTIES, "classpath:props/cherry/conf/application.properties",         "classpath:env/cherry/conf/application.properties"},
//            {APP_COMMON_PROPERTIES, ENV_COMMON_PROPERTIES, "classpath:props/cucumber/conf/application.properties",       "classpath:env/cucumber/conf/application.properties"},
//            {APP_COMMON_PROPERTIES, ENV_COMMON_PROPERTIES, "classpath:props/kiwi/conf/application.properties",           "classpath:env/kiwi/conf/application.properties"},
//            {APP_COMMON_PROPERTIES, ENV_COMMON_PROPERTIES, "classpath:props/lime/conf/application.properties",           "classpath:env/lime/conf/application.properties"},
//            {APP_COMMON_PROPERTIES, ENV_COMMON_PROPERTIES, "classpath:props/orange/conf/application.properties",         "classpath:env/orange/conf/application.properties"},
//            {APP_COMMON_PROPERTIES, ENV_COMMON_PROPERTIES, "classpath:props/rage/conf/application.properties",           "classpath:env/rage/conf/application.properties"},
//            {APP_COMMON_PROPERTIES, ENV_COMMON_PROPERTIES, "classpath:props/staging/conf/application.properties",        "classpath:env/staging/conf/application.properties"},
//
//            {APP_COMMON_PROPERTIES, ENV_COMMON_PROPERTIES, "classpath:props/prod_db1/conf/application.properties",       "classpath:env/prod_db1/conf/application.properties"},
//            {APP_COMMON_PROPERTIES, ENV_COMMON_PROPERTIES, "classpath:props/prod_db2/conf/application.properties",       "classpath:env/prod_db2/conf/application.properties"},
//            {APP_COMMON_PROPERTIES, ENV_COMMON_PROPERTIES, "classpath:props/prod_jadmin/conf/application.properties",    "classpath:env/prod_jadmin/conf/application.properties"},
//            {APP_COMMON_PROPERTIES, ENV_COMMON_PROPERTIES, "classpath:props/prod_trackrepo/conf/application.properties", "classpath:env/prod_trackrepo/conf/application.properties"},
//
//            {TRACKREPO_COMMON_PROPERTIES, "classpath:props/autotest/conf/trackrepo-application.properties",       "classpath:env/autotest/conf/trackrepo-application.properties"},
//            {TRACKREPO_COMMON_PROPERTIES, "classpath:props/cherry/conf/trackrepo-application.properties",         "classpath:env/cherry/conf/trackrepo-application.properties"},
//            {TRACKREPO_COMMON_PROPERTIES, "classpath:props/cucumber/conf/trackrepo-application.properties",       "classpath:env/cucumber/conf/trackrepo-application.properties"},
//            {TRACKREPO_COMMON_PROPERTIES, "classpath:props/kiwi/conf/trackrepo-application.properties",           "classpath:env/kiwi/conf/trackrepo-application.properties"},
//            {TRACKREPO_COMMON_PROPERTIES, "classpath:props/lime/conf/trackrepo-application.properties",           "classpath:env/lime/conf/trackrepo-application.properties"},
//            {TRACKREPO_COMMON_PROPERTIES, "classpath:props/orange/conf/trackrepo-application.properties",         "classpath:env/orange/conf/trackrepo-application.properties"},
//            {TRACKREPO_COMMON_PROPERTIES, "classpath:props/rage/conf/trackrepo-application.properties",           "classpath:env/rage/conf/trackrepo-application.properties"},
//            {TRACKREPO_COMMON_PROPERTIES, "classpath:props/staging/conf/trackrepo-application.properties",        "classpath:env/staging/conf/trackrepo-application.properties"},
//
//            {TRACKREPO_COMMON_PROPERTIES, "classpath:props/prod_jadmin/conf/trackrepo-application.properties",    "classpath:env/prod_jadmin/conf/trackrepo-application.properties"},
//            {TRACKREPO_COMMON_PROPERTIES, "classpath:props/prod_trackrepo/conf/trackrepo-application.properties", "classpath:env/prod_trackrepo/conf/trackrepo-application.properties"},
//    };
//
//    class AppPropertyResourceConfigurer extends PropertyPlaceholderConfigurer{
//        @Override
//        public Properties mergeProperties() throws IOException {
//            return super.mergeProperties();
//        }
//    }
//
//    @Theory
//    public void shouldConfirmNoDiffAsIs(String[] filePaths) throws Exception {
//        //given
//        AppPropertyResourceConfigurer oldAppPropertyPlaceholderConfigurer = new AppPropertyResourceConfigurer();
//        AppPropertyResourceConfigurer newAppPropertyPlaceholderConfigurer = new AppPropertyResourceConfigurer();
//
//        oldAppPropertyPlaceholderConfigurer.setLocations(getOldAppPropertyLocations(filePaths));
//        newAppPropertyPlaceholderConfigurer.setLocations(getNewAppPropertyLocations(filePaths));
//
//        //when
//        Properties oldProperties = oldAppPropertyPlaceholderConfigurer.mergeProperties();
//        Properties newProperties = newAppPropertyPlaceholderConfigurer.mergeProperties();
//
//        //then
//        assertThat(newProperties.size(), is(oldProperties.size()));
//
//        for (String oldPropertyKey : oldProperties.stringPropertyNames()) {
//            assertThat(newProperties.getProperty(oldPropertyKey).trim(), is(oldProperties.getProperty(oldPropertyKey).trim()));
//        }
//    }
//
//    private Resource[] getNewAppPropertyLocations(String[] filePaths) {
//        if(filePaths.length == 3){
//            return new Resource[]{fileSystemResourceLoader.getResource(filePaths[0]), fileSystemResourceLoader.getResource(filePaths[2])};
//        }else if(filePaths.length == 4){
//            return new Resource[]{fileSystemResourceLoader.getResource(filePaths[0]), fileSystemResourceLoader.getResource(filePaths[1]), fileSystemResourceLoader.getResource(filePaths[3])};
//        }
//        throw new IllegalStateException("Wrong input properties paths.");
//    }
//
//    private Resource[] getOldAppPropertyLocations(String[] filePaths) {
//        if(filePaths.length == 3){
//            return new Resource[]{fileSystemResourceLoader.getResource(filePaths[0]), fileSystemResourceLoader.getResource(filePaths[1])};
//        }else if(filePaths.length == 4){
//            return new Resource[]{fileSystemResourceLoader.getResource(filePaths[0]), fileSystemResourceLoader.getResource(filePaths[1]), fileSystemResourceLoader.getResource(filePaths[2])};
//        }
//        throw new IllegalStateException("Wrong input properties paths.");
//    }
//}
