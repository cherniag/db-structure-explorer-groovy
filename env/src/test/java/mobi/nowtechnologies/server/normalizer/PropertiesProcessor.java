package mobi.nowtechnologies.server.normalizer;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * @author Anton Zemliankin
 */

public class PropertiesProcessor {

    private BundlesResolver bundlesResolver;
    private Map<String, Properties> bundle;
    private String mainPropertyPath;

    public PropertiesProcessor(BundlesResolver bundlesResolver){
        this.bundlesResolver = bundlesResolver;
    }

    public void extractCommonValuesToDefaultProperty() throws IOException {
        System.out.println("...................Start extracting common values to default property....................");

        Set<String> propertiesIntersection = null;

        //find common properties in bundle
        for(String propertyName : bundle.keySet()){
            if(!propertyName.equalsIgnoreCase(mainPropertyPath)){
                Set<String> propertyValues = bundle.get(propertyName).stringPropertyNames();
                if(propertiesIntersection == null){
                    propertiesIntersection = propertyValues;
                } else {
                    propertiesIntersection.retainAll(propertyValues);
                }
            }
        }

        //find equal properties in common properties
        if(propertiesIntersection != null) {
            for (String propertyKey : propertiesIntersection) {
                boolean allEquals = true;
                String value = null;

                for (String propertyName : bundle.keySet()) {
                    if (!propertyName.equalsIgnoreCase(mainPropertyPath)) {

                        String bundlePropertyValue = bundle.get(propertyName).getProperty(propertyKey);

                        if(bundlePropertyValue.startsWith("ENC(") && bundlePropertyValue.endsWith(")")){ //do not transfer encrypted properties
                            allEquals = false;
                            break;
                        }

                        if (value == null) {
                            value = bundlePropertyValue;
                        } else if (!value.equals(bundlePropertyValue)) {
                            allEquals = false;
                            break;
                        }

                    }
                }

                if (allEquals) {
                    extractCommonValueToDefaultProperty(propertyKey, value);
                }
            }
        }

        System.out.println(".........................................................................................\n");
    }

    private void extractCommonValueToDefaultProperty(String propertyKey, String propertyValue) throws IOException {
        for (String propertyName : bundle.keySet()) {
            if (propertyName.equalsIgnoreCase(mainPropertyPath)) {
                if(bundle.get(mainPropertyPath).containsKey(propertyKey) && !propertyValue.equals(bundle.get(mainPropertyPath).getProperty(propertyKey))){
                    System.out.println("Rewrite default property " + propertyKey + ". Old value [" + bundle.get(mainPropertyPath).getProperty(propertyKey) + "]. New value [" + propertyValue + "]");
                }
                bundle.get(mainPropertyPath).setProperty(propertyKey, propertyValue);
            } else {
                bundle.get(propertyName).remove(propertyKey);
                System.out.println("Property " + propertyKey + " has been removed from " + propertyName);
            }
        }
    }

    public void removeCommonValuesFromSlaveProperties() throws IOException {
        System.out.println("......................Start removing common values from properties.......................");

        Properties mainProperties = bundle.get(mainPropertyPath);

        for (String propertyName : bundle.keySet()) {
            if (!propertyName.equalsIgnoreCase(mainPropertyPath)) {
                for(String propertyKey : bundle.get(propertyName).stringPropertyNames()){
                    if(mainProperties.containsKey(propertyKey) && bundle.get(propertyName).getProperty(propertyKey).equals(mainProperties.getProperty(propertyKey))){
                        bundle.get(propertyName).remove(propertyKey);
                        System.out.println("Property " + propertyKey + " has been removed from " + propertyName);
                    }
                }
            }
        }

        System.out.println(".........................................................................................\n");
    }

    public void loadBundle(String propertiesFolder, String mainPropertyName) throws IOException {
        mainPropertyPath = propertiesFolder + File.separator + mainPropertyName;
        bundle = bundlesResolver.readBundle(propertiesFolder, mainPropertyName);
    }

    public void loadProdRowProperties(String propertiesFolder, String mainPropertyName, String[] prodEnvironments) throws IOException {
        mainPropertyPath = bundlesResolver.getCommonPropertiesFolder(propertiesFolder) + File.separator + mainPropertyName;
        bundle = bundlesResolver.readRowProdProperties(propertiesFolder, mainPropertyName, prodEnvironments);
    }

    public void loadTestRowProperties(String propertiesFolder, String mainPropertyName, String[] prodEnvironments) throws IOException {
        mainPropertyPath = bundlesResolver.getCommonPropertiesFolder(propertiesFolder) + File.separator + mainPropertyName;
        bundle.putAll(bundlesResolver.readRowTestProperties(propertiesFolder, mainPropertyName, prodEnvironments));
    }

    public void extractCommonValuesFromTestEnvironments(String[] prodEnvironments) throws IOException {
        System.out.println("..................Start extracting common values from test environments..................");

        for(String commonPropertyKey : bundle.get(mainPropertyPath).stringPropertyNames()){
            String prodValue = bundle.get(mainPropertyPath).getProperty(commonPropertyKey);
            String testValue = getTestEnvironmentsSameValue(commonPropertyKey, prodEnvironments);

            if(testValue != null){
                bundle.get(mainPropertyPath).setProperty(commonPropertyKey, testValue);
                System.out.println("Property " + commonPropertyKey + " has been updated in " + mainPropertyPath);

                for(String propertyPath : bundle.keySet()){
                    if(!propertyPath.equalsIgnoreCase(mainPropertyPath)) {
                        if (isProdPath(propertyPath, prodEnvironments) && !prodValue.equals(testValue)) {
                            bundle.get(propertyPath).setProperty(commonPropertyKey, prodValue);
                            System.out.println("Property " + commonPropertyKey + " has been returned back to " + propertyPath);
                        } else {
                            bundle.get(propertyPath).remove(commonPropertyKey);
                            System.out.println("Property " + commonPropertyKey + " has been removed from " + propertyPath);
                        }
                    }
                }

            }
        }

        System.out.println(".........................................................................................\n");
    }

    private boolean isProdPath(String propertyPath, String[] prodEnvironments) {
        for(String prodEnvironment : prodEnvironments){
            if(propertyPath.startsWith(prodEnvironment + File.separator))
                return true;
        }
        return false;
    }

    private String getTestEnvironmentsSameValue(String propertyKey, String[] prodEnvironments) {
        String value = null;
        int testEnvironmentsCount = 0;
        int testEnvironmentsHasProperty = 0;

        for(String propertyPath : bundle.keySet()){
            if(!isProdPath(propertyPath, prodEnvironments) && bundle.get(propertyPath).containsKey(propertyKey) && !propertyPath.equalsIgnoreCase(mainPropertyPath)){
                testEnvironmentsCount++;
                String bundlePropertyValue = bundle.get(propertyPath).getProperty(propertyKey);

                if((bundlePropertyValue.startsWith("ENC(") && bundlePropertyValue.endsWith(")"))){ //do not transfer encrypted properties
                    value = null;
                    break;
                }

                if (value == null) {
                    value = bundlePropertyValue;
                    testEnvironmentsHasProperty++;
                } else if (!value.equals(bundlePropertyValue)) {
                    value = null;
                    break;
                }else{
                    testEnvironmentsHasProperty++;
                }
            }
        }

        return testEnvironmentsCount==testEnvironmentsHasProperty ? value : null;
    }

    public void synchronizeBundles() throws IOException {
        System.out.println("...........................Start synchronizing property files...........................");

        for(String propertyPath : bundle.keySet()){
            if(bundlesResolver.synchronizeBundle(propertyPath, bundle.get(propertyPath))){
                System.out.println("Property file " + propertyPath + " has been successfully synchronized.");
            }else{
                throw new IllegalStateException("ERROR: Failed to synchronize " + propertyPath);
            }
        }

        System.out.println(".........................................................................................\n");
    }

    public void printWarnings() {
        Set<String> propertiesIntersection = null;
        Set<String> warningProperties = new HashSet<String>();

        //find common properties in bundle
        for(String propertyName : bundle.keySet()){
            if(!propertyName.equalsIgnoreCase(mainPropertyPath)){
                Set<String> propertyValues = bundle.get(propertyName).stringPropertyNames();
                warningProperties.addAll(propertyValues);
                if(propertiesIntersection == null){
                    propertiesIntersection = propertyValues;
                } else {
                    propertiesIntersection.retainAll(propertyValues);
                }
            }
        }

        if(propertiesIntersection != null){
            warningProperties.removeAll(propertiesIntersection);
        }

        System.out.println("....................................... WARNINGS: .......................................");
            for(String propertyKey : warningProperties){
                for(String propertyName : bundle.keySet()){
                    if(!propertyName.equalsIgnoreCase(mainPropertyPath)){
                        if(!bundle.get(propertyName).containsKey(propertyKey)){
                            System.out.println("Property " + propertyKey + " does not exist in " + propertyName);
                        }
                    }
                }
            }
        System.out.println(".........................................................................................\n");
    }

    public void mergeCommonPropertiesWith(String mergePropertiesWithFilePath) throws IOException {
        bundlesResolver.mergeProperties(mergePropertiesWithFilePath, mainPropertyPath);
    }
}
