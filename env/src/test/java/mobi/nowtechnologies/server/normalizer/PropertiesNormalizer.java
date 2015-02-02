package mobi.nowtechnologies.server.normalizer;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.regex.Matcher;

/**
 * @author Anton Zemliankin
 */

public class PropertiesNormalizer {

    private static final String DEFAULT_ORIGINAL_PROPERTIES_FOLDER = "env";

    private PropertiesProcessor propertiesProcessor;

    public PropertiesNormalizer(String rootFolder, boolean overrideOriginal) {
        propertiesProcessor = new PropertiesProcessor(new BundlesResolver(rootFolder, overrideOriginal));
    }

    public static void main(String[] args) throws IOException {
        String rootFolder = System.getProperty("normalizer.root.folder", getDefaultRoot());
        String propertiesFolder = System.getProperty("normalizer.properties.folder");
        String mainPropertyName = System.getProperty("normalizer.property.name");
        boolean overrideOriginal = Boolean.getBoolean("normalizer.override.original.properties");

        if (rootFolder == null || propertiesFolder == null || mainPropertyName == null) {
            System.out.println("-Dnormalizer.root.folder, -Dnormalizer.properties.folder, -Dnormalizer.property.name was not properly specified.");
            System.out.println("Example for bundle: -Dnormalizer.root.folder=\"D:/Workspace/Server/env\" -Dnormalizer.properties.folder=\"cherry/conf\" -Dnormalizer.property.name=\"services.properties\"");
            System.out.println("Example for row properties: -Dnormalizer.root.folder=\"D:/Workspace/Server/env\" -Dnormalizer.properties.folder=\"*/conf\" -Dnormalizer.property.name=\"application.properties\" -Dnormalizer.prod.environments=\"prod_db1,prod_db2,prod_jadmin,prod_trackrepo\"");
            return;
        } else {
            propertiesFolder = propertiesFolder.replaceAll("\\\\|/", Matcher.quoteReplacement(File.separator));
        }

        PropertiesNormalizer normalizer = new PropertiesNormalizer(rootFolder, overrideOriginal);

        if (propertiesFolder != null && mainPropertyName != null) {

            if (propertiesFolder.startsWith("*")) {
                String prodEnvironments = System.getProperty("normalizer.prod.environments");
                if (prodEnvironments == null) {
                    System.out.println("-Dnormalizer.prod.environments was not properly specified.");
                    return;
                }
                normalizer.normalizeRowProperties(propertiesFolder, mainPropertyName, prodEnvironments.split(","));
            } else {
                normalizer.normalizeBundle(propertiesFolder, mainPropertyName);
            }
        } else {
            normalizer.normalizeAll();
        }

    }

    private static String getDefaultRoot() {
        URL classesRoot = PropertiesNormalizer.class.getClassLoader().getResource(".");
        if(classesRoot != null){
            return new File(new File(classesRoot.getFile()).getParent() + File.separator + "classes" + File.separator + DEFAULT_ORIGINAL_PROPERTIES_FOLDER).getPath();
        }
        return null;
    }



    private void normalizeRowProperties(String propertiesFolder, String mainPropertyName, String[] prodEnvironments) throws IOException {
        System.out.println("=========================NORMALIZING ROW PROPERTIES " + propertiesFolder + "/" + mainPropertyName + "=========================\n");

        //1. normalize only prod environments properties
        propertiesProcessor.loadProdRowProperties(propertiesFolder, mainPropertyName, prodEnvironments);
        Map<String, String> extractedProperties = propertiesProcessor.extractCommonValuesToDefaultProperty();
        propertiesProcessor.filterCommonProperty(extractedProperties);

        //2. replace prod common values with test common values
        propertiesProcessor.loadTestRowProperties(propertiesFolder, mainPropertyName, prodEnvironments);
        propertiesProcessor.extractCommonValuesFromTestEnvironments(prodEnvironments);

        propertiesProcessor.synchronizeBundles();
        propertiesProcessor.printWarnings();
        System.out.println("=========================================================================================\n\n");
    }

    public void normalizeBundle(String propertiesFolder, String mainPropertyName) throws IOException {
        System.out.println("=========================NORMALIZING BUNDLE " + propertiesFolder + "/" + mainPropertyName + "=========================\n");

        propertiesProcessor.loadBundle(propertiesFolder, mainPropertyName);
        propertiesProcessor.extractCommonValuesToDefaultProperty();
        propertiesProcessor.removeCommonValuesFromSlaveProperties();

        propertiesProcessor.synchronizeBundles();
        System.out.println("=========================================================================================\n\n");
    }

    private void normalizeAll() {
        throw new UnsupportedOperationException("Normalizing all communities bundles not implemented yet.");
    }

}
