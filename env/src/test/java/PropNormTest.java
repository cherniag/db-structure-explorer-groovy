/*
 * Copyright 2015 Musicqubed.com. All Rights Reserved.
 */

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.PropertiesConfigurationLayout;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.core.io.FileSystemResourceLoader;
import org.springframework.core.io.Resource;

import org.junit.*;

// @author Titov Mykhaylo (titov) on 23.03.2015.
public class PropNormTest {

    Logger logger = LoggerFactory.getLogger(PropNormTest.class);

    static FileSystemResourceLoader fileSystemResourceLoader = new FileSystemResourceLoader();

    static String[] propPaths = {
        "classpath:env/autotest/conf/application.properties",
        "classpath:env/cherry/conf/application.properties",
        "classpath:env/cucumber/conf/application.properties",
        "classpath:env/potato/conf/application.properties",
        "classpath:env/kiwi/conf/application.properties",
        "classpath:env/lime/conf/application.properties",
        "classpath:env/orange/conf/application.properties",
        "classpath:env/rage/conf/application.properties",
        "classpath:env/staging/conf/application.properties",
        "classpath:env/prod_db1/conf/application.properties",
        "classpath:env/prod_db2/conf/application.properties",
        "classpath:env/prod_jadmin/conf/application.properties",
        "classpath:env/prod_trackrepo/conf/application.properties"
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

    @Test
    public void test() throws Exception {
        Map<String, PropertiesConfigurationLayout> propertiesConfigurationLayoutMap = new HashMap<>();
        Map<String, Properties> propPathToPropertiesMap = new HashMap<>();
        Set<String> allPropKeys = new HashSet<>();

        for (String propPath : propPaths) {
            AppPropertyResourceConfigurer propertyPlaceholderConfigurer = new AppPropertyResourceConfigurer();
            Resource propResource = getResource(propPath);
            propertyPlaceholderConfigurer.setLocations(new Resource[] {getResource("classpath:application.properties"), getResource("classpath:env/gen-app.properties"), propResource});

            Properties properties = propertyPlaceholderConfigurer.mergeProperties();
            propPathToPropertiesMap.put(propPath, properties);
            allPropKeys.addAll(properties.stringPropertyNames());

            PropertiesConfigurationLayout propertiesConfigurationLayout = new PropertiesConfigurationLayout(new PropertiesConfiguration());
            propertiesConfigurationLayout.load(new FileReader(propResource.getFile()));
            propertiesConfigurationLayoutMap.put(propPath, propertiesConfigurationLayout);
        }

        Properties commonProperties = new Properties();
        for (String key : allPropKeys) {
            Map<String, List<String>> propValueToPropsPathMap = new HashMap<>();
            for (Map.Entry<String, Properties> propPathToPropertiesEntry : propPathToPropertiesMap.entrySet()) {
                Properties properties = propPathToPropertiesEntry.getValue();
                String value = properties.getProperty(key);
                List<String> propPaths = propValueToPropsPathMap.get(value);
                if(propPaths == null){
                    propPaths = new ArrayList<>();
                    propValueToPropsPathMap.put(value, propPaths);
                }
                propPaths.add(propPathToPropertiesEntry.getKey());
            }

            List<Map.Entry<String, List<String>>> entries = new ArrayList<>(propValueToPropsPathMap.entrySet());
            Collections.sort(entries, comparator);

            Map.Entry<String, List<String>> propValuePropPathsEntry = entries.get(0);
            if(!entries.isEmpty() &&  propValuePropPathsEntry.getValue().size() >= propPaths.length/2){
                String propValue = propValuePropPathsEntry.getKey();
                if (propValue != null) {
                    if (propValue.startsWith("ENC(") && propValuePropPathsEntry.getKey().contains("prod")){
                        continue;
                    }
                    commonProperties.setProperty(key, propValue);


                    for (Map.Entry<String, Properties> propPathToPropertiesEntry : propPathToPropertiesMap.entrySet()) {
                        Properties propPathToPropertiesEntryValue = propPathToPropertiesEntry.getValue();
                        PropertiesConfigurationLayout propertiesConfigurationLayout = propertiesConfigurationLayoutMap.get(propPathToPropertiesEntry.getKey());
                        if (propValuePropPathsEntry.getValue().contains(propPathToPropertiesEntry.getKey())) {
                            propPathToPropertiesEntryValue.remove(key);
                            propertiesConfigurationLayout.getConfiguration().clearProperty(key);
                        }else {
                            propertiesConfigurationLayout.getConfiguration().setProperty(key, propPathToPropertiesEntryValue.getProperty(key));
                        }
                    }
                }
            }
        }

        FileUtils.copyDirectory(new File("env/src/main/resources/env"), new File("env/src/main/resources/n"));

        Properties classpathProperties = new Properties();
        classpathProperties.load(getResource("classpath:application.properties").getInputStream());

        PrintWriter printWriter = new PrintWriter("env/src/main/resources/n/application.properties");
        for (Object key : commonProperties.keySet()) {
            Object val = commonProperties.get(key);
            Object classpathVal = classpathProperties.get(key);
            if(classpathVal == null || !classpathVal.equals(val)){
                printWriter.println(key + "=" + val);
            }
        }
        printWriter.close();

        for (Map.Entry<String, PropertiesConfigurationLayout> stringPropertiesConfigurationLayoutEntry : propertiesConfigurationLayoutMap.entrySet()) {
            PropertiesConfigurationLayout propertiesConfigurationLayout = stringPropertiesConfigurationLayoutEntry.getValue();
            propertiesConfigurationLayout.save(new FileWriter("env/src/main/resources/n/" + stringPropertiesConfigurationLayoutEntry.getKey().replaceFirst(".*?/", "/")));
        }

    }

    static Resource getResource(String filePath) {return fileSystemResourceLoader.getResource(filePath);}

    static class AppPropertyResourceConfigurer extends PropertyPlaceholderConfigurer {
        @Override
        public Properties mergeProperties() throws IOException {
            return super.mergeProperties();
        }
    }
}
