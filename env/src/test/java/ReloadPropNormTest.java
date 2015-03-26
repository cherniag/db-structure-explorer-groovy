/*
 * Copyright 2015 Musicqubed.com. All Rights Reserved.
 */

import mobi.nowtechnologies.server.shared.message.CommunityResourceBundleMessageSourceImpl;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.PropertiesConfigurationLayout;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.io.FileSystemResourceLoader;

import org.junit.*;

// @author Titov Mykhaylo (titov) on 23.03.2015.
@Ignore
public class ReloadPropNormTest {

    Logger logger = LoggerFactory.getLogger(ReloadPropNormTest.class);

    static String[] communities = {"o2", "vf_nz", "demo", "mtvnz", "hl_uk", "mtv1", "demo", "demo2", "demo3", "demo4", "demo5", "demo6"};

    static FileSystemResourceLoader fileSystemResourceLoader = new FileSystemResourceLoader();

    static String[] propPaths = {
        "classpath:env/autotest/conf/services",
        "classpath:env/cherry/conf/services",
        "classpath:env/cucumber/conf/services",
        "classpath:env/kiwi/conf/services",
        "classpath:env/lime/conf/services",
        "classpath:env/orange/conf/services",
        "classpath:env/rage/conf/services",
        "classpath:env/staging/conf/services",
        "classpath:env/potato/conf/services",
        "classpath:env/prod_db1/conf/services",
        "classpath:env/prod_db2/conf/services",
        "classpath:env/prod_jadmin/conf/services",
        "classpath:env/prod_trackrepo/conf/services",
    };

    static String[][] globalAndEnvGlobalProps = {
        {"classpath:service.properties",  "service.properties"},
    };

    Comparator<Map.Entry<String, List<String>>> comparator = new Comparator<Map.Entry<String, List<String>>>() {
        @Override
        public int compare(Map.Entry<String, List<String>> o1, Map.Entry<String, List<String>> o2) {
            if (o1.getValue().size() < o2.getValue().size()) {
                return 1;
            }else if (o1.getValue().size() == o2.getValue().size()){
                return 0;
            }else {
                return -1;
            }
        }
    };

    class CustomReloadableResourceBundleMessageSource extends ReloadableResourceBundleMessageSource {
        Properties getAllProperties() {
            return getMergedProperties(Locale.getDefault()).getProperties();
        }
    }

    @Test
    public void test() throws Exception {
        StandardPBEStringEncryptor stringEncryptor = new StandardPBEStringEncryptor();
        stringEncryptor.setPassword("gggg");

        for (String propPath : propPaths) {
            CustomReloadableResourceBundleMessageSource customReloadableResourceBundleMessageSource = new CustomReloadableResourceBundleMessageSource();
            customReloadableResourceBundleMessageSource.setBasenames(globalAndEnvGlobalProps[0][0], propPath);

            CommunityResourceBundleMessageSourceImpl communityResourceBundleMessageSource = new CommunityResourceBundleMessageSourceImpl();
            communityResourceBundleMessageSource.setReloadableResourceBundleMessageSource(customReloadableResourceBundleMessageSource);
            communityResourceBundleMessageSource.setStringEncryptor(stringEncryptor);

            Properties allProperties = customReloadableResourceBundleMessageSource.getAllProperties();

            Map<String, PropertiesConfigurationLayout> communityToPropertiesConfigurationLayoutMap = new HashMap<>();

            for (String propName : allProperties.stringPropertyNames()) {
                Map<String, List<String>> messageToCommunitiesMap = new HashMap<>();
                for (String community : communities) {

                    String filePath = propPath.replace("classpath:", "env/src/main/resources/") + "_" + community + ".properties";
                    if (new File(filePath).exists()){
                        PropertiesConfiguration propertiesConfiguration = new PropertiesConfiguration();
                        PropertiesConfigurationLayout propertiesConfigurationLayout = new PropertiesConfigurationLayout(propertiesConfiguration);
                        propertiesConfigurationLayout.load(new FileReader(filePath));
                        propertiesConfiguration.setPath(filePath);
                        communityToPropertiesConfigurationLayoutMap.put(community, propertiesConfigurationLayout);
                    }

                    String message = communityResourceBundleMessageSource.getMessage(community, propName, null, null, null);
                    List<String> communities = messageToCommunitiesMap.get(message);
                    if (communities == null) {
                        communities = new ArrayList<>();
                        messageToCommunitiesMap.put(message, communities);
                    }
                    communities.add(community);
                }

                List<Map.Entry<String, List<String>>> entries = new ArrayList<>(messageToCommunitiesMap.entrySet());
                Collections.sort(entries, comparator);

                Map.Entry<String, List<String>> messageToCommunitiesMapEntry = entries.get(0);

                PropertiesConfigurationLayout commonPropertiesConfigurationLayout = new PropertiesConfigurationLayout(new PropertiesConfiguration());
                commonPropertiesConfigurationLayout.load(new FileReader(propPath.replace("classpath:", "env/src/main/resources/") + ".properties"));

                if (!entries.isEmpty() && messageToCommunitiesMapEntry.getValue().size() >= propPaths.length / 2) {
                    String propValue = messageToCommunitiesMapEntry.getKey();
                    if (propValue != null) {
                        commonPropertiesConfigurationLayout.getConfiguration().setProperty(propName, propValue);

                        for (String community : communities) {
                            PropertiesConfigurationLayout propertiesConfigurationLayout = communityToPropertiesConfigurationLayoutMap.get(community);
                            if (propertiesConfigurationLayout == null){
                                continue;
                            }
                            String message = communityResourceBundleMessageSource.getMessage(community, propName, null, null, null);

                            Object oldProperty = propertiesConfigurationLayout.getConfiguration().getProperty(propName);
                            if (oldProperty!=null &&  oldProperty.equals(message)) {
                                propertiesConfigurationLayout.getConfiguration().clearProperty(propName);
                            } else {
                                propertiesConfigurationLayout.getConfiguration().setProperty(propName, message);
                            }
                        }
                    }
                }

                commonPropertiesConfigurationLayout.save(new FileWriter("env/src/main/resources/n/" + propPath.replaceFirst(".*?/", "/") + ".properties"));

                for (String community : communities) {
                    PropertiesConfigurationLayout propertiesConfigurationLayout = communityToPropertiesConfigurationLayoutMap.get(community);
                    if (propertiesConfigurationLayout == null){
                        continue;
                    }
                    String path = propertiesConfigurationLayout.getConfiguration().getPath();

                    propertiesConfigurationLayout.save(new FileWriter(path.replaceFirst("resources\\\\env", "resources\\\\n")));
                }

            }
        }
    }
}
