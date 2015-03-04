package mobi.nowtechnologies.server.normalizer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringEscapeUtils;

/**
 * @author Anton Zemliankin
 */

public class BundlesResolver {

    private static final String NORMALIZED_FOLDER_NAME = "normalized_properties";
    private static final String COMMON_PROPERTIES_FOLDER = "COMMON";
    private static final String EXTENSION_SEPARATOR = ".";
    private static final String BUNDLE_NAME_SEPARATOR = "_";
    private static final String LINE_SEPARATOR = System.getProperty("line.separator");

    private String originalPropertiesFolder;
    private String normalizedPropertiesFolder;


    public BundlesResolver(String originalPropertiesFolder, boolean overrideOriginal) {
        this.originalPropertiesFolder = originalPropertiesFolder;
        if (overrideOriginal) {
            this.normalizedPropertiesFolder = originalPropertiesFolder;
        }
        else {
            this.normalizedPropertiesFolder = new File(originalPropertiesFolder).getParent() + File.separator + NORMALIZED_FOLDER_NAME;
        }
    }


    /**
     * Reads bundles and stores it in map
     *
     * @param propertiesFolderName - folder that contains properties file
     * @param mainPropertyName     - properties name
     * @return - map of loaded bundles
     * @throws IOException
     */
    public Map<String, Properties> readBundle(String propertiesFolderName, String mainPropertyName) throws IOException {
        Map<String, Properties> bundles = new HashMap<String, Properties>();

        File parentDir = getParentDirectory(propertiesFolderName);
        String[] bundleFilesNames = searchBundleFileNames(parentDir, mainPropertyName);

        storeAndCopyProperty(bundles, parentDir, propertiesFolderName, mainPropertyName);

        for (String bundleFileName : bundleFilesNames) {
            storeAndCopyProperty(bundles, parentDir, propertiesFolderName, bundleFileName);
        }

        return bundles;
    }


    /**
     * Reads row properties from PROD environments and stores it in map
     *
     * @param propertiesFolderMask - folder wildcard that contains properties file
     * @param propertyName         - properties name
     * @param prodEnvironments     - list of PROD environments
     * @return - map of loaded bundles
     * @throws IOException
     */
    public Map<String, Properties> readRowProdProperties(String propertiesFolderMask, String propertyName, String[] prodEnvironments) throws IOException {
        final Map<String, Properties> bundles = new HashMap<String, Properties>();
        File originalPropertiesRoot = new File(originalPropertiesFolder);

        bundles.put(getCommonPropertiesFolder(propertiesFolderMask) + File.separator + propertyName, new Properties());
        copyBundle(new File(originalPropertiesRoot.getPath() + File.separator + propertiesFolderMask.replace("*", prodEnvironments[0]) + File.separator + propertyName),
                   getCommonPropertiesFolder(propertiesFolderMask), propertyName);

        for (String environmentDirName : prodEnvironments) {
            storeAndCopyProperty(bundles, new File(originalPropertiesRoot.getPath() + File.separator + propertiesFolderMask.replace("*", environmentDirName)),
                                 propertiesFolderMask.replace("*", environmentDirName), propertyName);
        }
        return bundles;
    }


    /**
     * Reads row properties from TEST environments and stores it in map
     *
     * @param propertiesFolder - folder wildcard that contains properties file
     * @param propertyName     - properties name
     * @param prodEnvironments - list of PROD environments
     * @return - map of loaded bundles
     * @throws IOException
     */
    public Map<String, Properties> readRowTestProperties(final String propertiesFolder, String propertyName, final String[] prodEnvironments) throws IOException {
        final Map<String, Properties> bundles = new HashMap<String, Properties>();
        File originalPropertiesRoot = new File(originalPropertiesFolder);

        String[] propertiesDirs = originalPropertiesRoot.list(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return !name.equalsIgnoreCase(COMMON_PROPERTIES_FOLDER) && new File(dir, name).isDirectory() && Arrays.binarySearch(prodEnvironments, name) < 0;
            }
        });

        for (String propertiesDir : propertiesDirs) {
            storeAndCopyProperty(bundles, new File(originalPropertiesRoot.getPath() + File.separator + propertiesFolder.replace("*", propertiesDir)), propertiesFolder.replace("*", propertiesDir),
                                 propertyName);
        }
        return bundles;
    }


    /**
     * Stores properties in bundles map and copies property file
     *
     * @param bundles              - map of all loaded bundles
     * @param propertyDir          - initial properties file directory
     * @param propertiesFolderName - folder that contains properties file
     * @param propertyName         - property file name
     * @throws IOException
     */
    private void storeAndCopyProperty(Map<String, Properties> bundles, File propertyDir, String propertiesFolderName, String propertyName) throws IOException {
        File propertyFile = new File(propertyDir, propertyName);
        if (propertyFile.exists()) {
            storeProperty(bundles, propertyFile, propertiesFolderName + File.separator + propertyName);
            copyBundle(propertyFile, propertiesFolderName, propertyName);
        }
    }


    /**
     * Adds bundle to bundles map
     *
     * @param bundles      - map of all loaded bundles
     * @param propertyFile - initial properties file to read property from
     * @param propertyKey  - map key for properties
     * @throws IOException
     */
    private void storeProperty(Map<String, Properties> bundles, File propertyFile, String propertyKey) throws IOException {
        Properties prop = new Properties();
        prop.load(new FileInputStream(propertyFile));
        bundles.put(propertyKey, prop);
    }


    /**
     * Copy bundle to normalized folder and remove duplicates
     *
     * @param propertyFile         - initial properties file
     * @param propertiesFolderName - folder that contains properties file
     * @param propertyName         - property file name
     * @throws IOException
     */
    private void copyBundle(File propertyFile, String propertiesFolderName, String propertyName) throws IOException {
        File newPropertiesFileDirectory = new File(normalizedPropertiesFolder + File.separator + propertiesFolderName);
        if (!normalizedPropertiesFolder.equalsIgnoreCase(originalPropertiesFolder) || propertiesFolderName.startsWith(COMMON_PROPERTIES_FOLDER)) {
            FileUtils.copyFileToDirectory(propertyFile, newPropertiesFileDirectory);
        }
        removeDuplicates(new File(newPropertiesFileDirectory, propertyName));
    }


    /**
     * Removes duplicate properties from property file
     *
     * @param newPropertiesFile - properties file
     * @throws IOException
     */
    private void removeDuplicates(File newPropertiesFile) throws IOException {
        Map<String, Integer> existedProperties = new HashMap<String, Integer>();
        Set<Integer> rowsToDelete = new HashSet<Integer>();

        BufferedReader reader = new BufferedReader(new FileReader(newPropertiesFile));
        String currentLine;
        int row = 0;
        while ((currentLine = reader.readLine()) != null) {
            if (!currentLine.trim().startsWith("#") && currentLine.contains("=")) {
                String propertyKey = currentLine.substring(0, currentLine.indexOf("=")).trim();

                if (existedProperties.containsKey(propertyKey)) {
                    rowsToDelete.add(existedProperties.get(propertyKey));
                }
                existedProperties.put(propertyKey, row);
            }
            row++;
        }
        IOUtils.closeQuietly(reader);
        reader = null;
        System.gc(); //java bug http://bugs.java.com/bugdatabase/view_bug.do?bug_id=4715154

        File tempFile = new File(newPropertiesFile.getPath() + ".tmp");

        reader = new BufferedReader(new FileReader(newPropertiesFile));
        BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));
        row = 0;

        while ((currentLine = reader.readLine()) != null) {
            if (!rowsToDelete.contains(row)) {
                writer.write(currentLine + LINE_SEPARATOR);
            }
            else {
                System.out.println("Duplicate '" + currentLine + "' was found in " + newPropertiesFile.getPath());
            }
            row++;
        }
        IOUtils.closeQuietly(writer);
        IOUtils.closeQuietly(reader);
        writer = null;
        reader = null;
        System.gc(); //java bug http://bugs.java.com/bugdatabase/view_bug.do?bug_id=4715154

        if (newPropertiesFile.delete() & tempFile.renameTo(newPropertiesFile)) {
            System.out.println(newPropertiesFile.getPath() + " has been cleaned for duplicates.");
        }
        else {
            throw new IllegalStateException("ERROR cleaning duplicates from " + newPropertiesFile.getPath());
        }
    }


    /**
     * Search all bundle properties files in directory
     *
     * @param parentDir        - directory to search in
     * @param mainPropertyName - main property file name
     * @return - array of properties files
     */
    private String[] searchBundleFileNames(File parentDir, String mainPropertyName) {
        final String mainExtension = mainPropertyName.substring(mainPropertyName.lastIndexOf(EXTENSION_SEPARATOR) + 1);
        final String mainName = mainPropertyName.substring(0, mainPropertyName.length() - mainExtension.length() - 1);

        return parentDir.list(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.startsWith(mainName + BUNDLE_NAME_SEPARATOR) && name.endsWith(EXTENSION_SEPARATOR + mainExtension) && new File(dir, name).isFile();
            }
        });
    }


    /**
     * Synchronizes properties file with Properties container
     *
     * @param propertyPath - path to properties file
     * @param properties   - properties container
     * @return - successful result
     * @throws IOException
     */
    public boolean synchronizeBundle(String propertyPath, Properties properties) throws IOException {
        File inputFile = new File(normalizedPropertiesFolder + File.separator + propertyPath);
        File tempFile = new File(normalizedPropertiesFolder + File.separator + propertyPath + ".tmp");

        BufferedReader reader = new BufferedReader(new FileReader(inputFile));
        BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));

        String currentLine;

        while ((currentLine = reader.readLine()) != null) {
            if (!currentLine.trim().startsWith("#") && currentLine.contains("=")) {
                String propertyKey = currentLine.substring(0, currentLine.indexOf("=")).trim();
                String propertyValue = currentLine.substring(currentLine.indexOf("=") + 1).trim();

                if (!properties.containsKey(propertyKey)) { //remove
                    System.out.println("Remove property " + propertyKey + " from " + propertyPath);
                    //do nothing
                }
                else {

                    String newValue = StringEscapeUtils.escapeJava(properties.getProperty(propertyKey).trim());
                    if (propertyValue.equals(newValue)) { //no changes
                        writer.write(currentLine + LINE_SEPARATOR);
                    }
                    else {
                        writer.write(propertyKey + "=" + newValue + LINE_SEPARATOR);
                        System.out.println("Update property " + propertyKey + " in " + propertyPath + ". Old value = [" + propertyValue + "]. New value = [" + newValue + "].");
                    }
                }

            }
            else {
                writer.write(currentLine + LINE_SEPARATOR); //write comments or empty lines
            }
        }
        IOUtils.closeQuietly(writer);
        IOUtils.closeQuietly(reader);
        writer = null;
        reader = null;
        System.gc(); //java bug http://bugs.java.com/bugdatabase/view_bug.do?bug_id=4715154
        return inputFile.delete() & tempFile.renameTo(inputFile);
    }


    /**
     * Merges default properties with generated
     *
     * @param mergePropertiesWithFilePath - default properties absolute path
     * @param mainPropertyPath            - generated properties relative path
     * @return - successful result
     * @throws IOException
     */
    public boolean mergeProperties(String mergePropertiesWithFilePath, String mainPropertyPath) throws IOException {
        //Common + generated common
        File mergeWithFile = new File(mergePropertiesWithFilePath);
        File tempFile = new File(normalizedPropertiesFolder + File.separator + mainPropertyPath + ".tmp");
        File commonProperties = new File(normalizedPropertiesFolder + File.separator + mainPropertyPath);

        FileUtils.copyFile(mergeWithFile, tempFile);

        BufferedReader reader = new BufferedReader(new FileReader(commonProperties));
        String currentLine;

        while ((currentLine = reader.readLine()) != null) {
            FileUtils.writeStringToFile(tempFile, currentLine + LINE_SEPARATOR, true);
        }

        IOUtils.closeQuietly(reader);
        reader = null;
        System.gc(); //java bug http://bugs.java.com/bugdatabase/view_bug.do?bug_id=4715154

        removeDuplicates(tempFile);

        return commonProperties.delete() & tempFile.renameTo(commonProperties);
    }


    public String getCommonPropertiesFolder(String folderMask) {
        return folderMask.replace("*", COMMON_PROPERTIES_FOLDER);
    }


    private File getParentDirectory(String propertiesFolderName) {
        File parentDir = new File(originalPropertiesFolder + File.separator + propertiesFolderName);

        if (!parentDir.exists()) {
            throw new IllegalStateException("Folder does not exist: " + parentDir.getPath());
        }

        return parentDir;
    }
}


