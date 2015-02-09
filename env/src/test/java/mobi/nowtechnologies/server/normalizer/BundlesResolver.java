package mobi.nowtechnologies.server.normalizer;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringEscapeUtils;

import java.io.*;
import java.util.*;

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
        if(overrideOriginal){
            this.normalizedPropertiesFolder = originalPropertiesFolder;
        }else{
            this.normalizedPropertiesFolder = new File(originalPropertiesFolder).getParent() + File.separator + NORMALIZED_FOLDER_NAME;
        }
    }

    public Map<String, Properties> readBundle(String propertiesFolderName, String mainPropertyName) throws IOException {
        Map<String, Properties> bundles = new HashMap<String, Properties>();

        File parentDir = getParentDirectory(propertiesFolderName);
        String[] bundleFilesNames = searchBundleFileNames(parentDir, mainPropertyName);

        storeProperty(bundles, parentDir, propertiesFolderName, mainPropertyName);

        for(String bundleFileName : bundleFilesNames){
            storeProperty(bundles, parentDir, propertiesFolderName, bundleFileName);
        }

        return bundles;
    }

    public Map<String, Properties> readRowProdProperties(final String propertiesFolder, String propertyName, String[] prodEnvironments) throws IOException {
        final Map<String, Properties> bundles = new HashMap<String, Properties>();
        File originalCommunitiesRoot = new File(originalPropertiesFolder);

        storeProperty(
                bundles,
                new File(originalCommunitiesRoot.getPath() + File.separator + propertiesFolder.replace("*", prodEnvironments[0])),
                propertiesFolder.replace("*", COMMON_PROPERTIES_FOLDER),
                propertyName
        );

        for(String communitiesDir : prodEnvironments){
            storeProperty(bundles, new File(originalCommunitiesRoot.getPath() + File.separator + propertiesFolder.replace("*", communitiesDir)), propertiesFolder.replace("*", communitiesDir), propertyName);
        }
        return bundles;
    }

    public Map<String, Properties> readRowTestProperties(final String propertiesFolder, String propertyName, final String[] prodEnvironments) throws IOException {
        final Map<String, Properties> bundles = new HashMap<String, Properties>();
        File originalCommunitiesRoot = new File(originalPropertiesFolder);

        String[] communitiesDirs = originalCommunitiesRoot.list(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return !name.equalsIgnoreCase(COMMON_PROPERTIES_FOLDER) && new File(dir, name).isDirectory() && Arrays.binarySearch(prodEnvironments, name)<0;
            }
        });

        for(String communitiesDir : communitiesDirs){
            storeProperty(bundles, new File(originalCommunitiesRoot.getPath() + File.separator + propertiesFolder.replace("*", communitiesDir)), propertiesFolder.replace("*", communitiesDir), propertyName);
        }
        return bundles;
    }

    private void storeProperty(Map<String, Properties> bundles, File parentDir, String propertiesFolderName, String propertyName) throws IOException {
        //add bundle to bundles map
        Properties prop = new Properties();
        prop.load(new FileInputStream(new File(parentDir, propertyName)));
        bundles.put(propertiesFolderName + File.separator + propertyName, prop);

        //copy bundle to normalized folder and remove duplicates
        File newPropertiesFileDirectory = new File(normalizedPropertiesFolder + File.separator + propertiesFolderName);
        if(!normalizedPropertiesFolder.equalsIgnoreCase(originalPropertiesFolder) || propertiesFolderName.startsWith(COMMON_PROPERTIES_FOLDER)){
            FileUtils.copyFileToDirectory(new File(parentDir, propertyName), newPropertiesFileDirectory);
        }
        removeDuplicates(new File(newPropertiesFileDirectory, propertyName));
    }

    private void removeDuplicates(File newPropertiesFile) throws IOException {
        Map<String, Integer> existedProperties = new HashMap<String, Integer>();
        Set<Integer> rowsToDelete = new HashSet<Integer>();

        BufferedReader reader = new BufferedReader(new FileReader(newPropertiesFile));
        String currentLine;
        int row = 0;
        while((currentLine = reader.readLine()) != null) {
            if(!currentLine.trim().startsWith("#") && currentLine.contains("=")){
                String propertyKey = currentLine.substring(0, currentLine.indexOf("=")).trim();

                if(existedProperties.containsKey(propertyKey)){
                    rowsToDelete.add(existedProperties.get(propertyKey));
                }
                existedProperties.put(propertyKey, row);
            }
            row++;
        }
        IOUtils.closeQuietly(reader);
        reader = null; System.gc(); //java bug http://bugs.java.com/bugdatabase/view_bug.do?bug_id=4715154

        File tempFile = new File(newPropertiesFile.getPath() + ".tmp");

        reader = new BufferedReader(new FileReader(newPropertiesFile));
        BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));
        row = 0;

        while((currentLine = reader.readLine()) != null) {
            if(!rowsToDelete.contains(row)){
                writer.write(currentLine + LINE_SEPARATOR);
            } else{
                System.out.println("Duplicate '" + currentLine + "' was found in " + newPropertiesFile.getPath());
            }
            row++;
        }
        IOUtils.closeQuietly(writer);
        IOUtils.closeQuietly(reader);
        writer = null; reader = null; System.gc(); //java bug http://bugs.java.com/bugdatabase/view_bug.do?bug_id=4715154

        if(newPropertiesFile.delete() & tempFile.renameTo(newPropertiesFile)){
            System.out.println(newPropertiesFile.getPath() + " has been cleaned for duplicates.");
        }else{
            throw new IllegalStateException("ERROR cleaning duplicates from " + newPropertiesFile.getPath());
        }
    }

    private File getParentDirectory(String propertiesFolderName){
        File parentDir = new File(originalPropertiesFolder + File.separator + propertiesFolderName);

        if(!parentDir.exists()){
            throw new IllegalStateException("Folder does not exist: " + parentDir.getPath());
        }

        return parentDir;
    }

    private String[] searchBundleFileNames(File parentDir, String mainPropertyName){
        final String mainExtension = mainPropertyName.substring(mainPropertyName.lastIndexOf(EXTENSION_SEPARATOR) + 1);
        final String mainName = mainPropertyName.substring(0, mainPropertyName.length() - mainExtension.length() - 1);

        return parentDir.list(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.startsWith(mainName + BUNDLE_NAME_SEPARATOR) && name.endsWith(EXTENSION_SEPARATOR + mainExtension) && new File(dir, name).isFile();
            }
        });
    }

    public String getCommonPropertiesFolder(String folderMask){
        return folderMask.replace("*", COMMON_PROPERTIES_FOLDER);
    }

    public boolean synchronizeBundle(String propertyPath, Properties properties) throws IOException {
        File inputFile = new File(normalizedPropertiesFolder + File.separator + propertyPath);
        File tempFile = new File(normalizedPropertiesFolder + File.separator + propertyPath + ".tmp");

        BufferedReader reader = new BufferedReader(new FileReader(inputFile));
        BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));

        String currentLine;

        while((currentLine = reader.readLine()) != null) {
            if(!currentLine.trim().startsWith("#") && currentLine.contains("=")){
                String propertyKey = currentLine.substring(0, currentLine.indexOf("=")).trim();
                String propertyValue = currentLine.substring(currentLine.indexOf("=")+1).trim();

                if(!properties.containsKey(propertyKey)){ //remove
                    System.out.println("Remove property " + propertyKey + " from " + propertyPath);
                    //do nothing
                } else {

                    String newValue = StringEscapeUtils.escapeJava(properties.getProperty(propertyKey).trim());
                    if(propertyValue.equals(newValue)){ //no changes
                        writer.write(currentLine + LINE_SEPARATOR);
                    } else {
                        writer.write(propertyKey + "=" + newValue + LINE_SEPARATOR);
                        System.out.println("Update property " + propertyKey + " in " + propertyPath + ". Old value = [" + propertyValue + "]. New value = [" + newValue + "].");
                    }
                }

            }else{
                writer.write(currentLine + LINE_SEPARATOR); //write comments or empty lines
            }
        }
        IOUtils.closeQuietly(writer);
        IOUtils.closeQuietly(reader);
        writer = null; reader = null; System.gc(); //java bug http://bugs.java.com/bugdatabase/view_bug.do?bug_id=4715154
        return inputFile.delete() & tempFile.renameTo(inputFile);
    }
}


