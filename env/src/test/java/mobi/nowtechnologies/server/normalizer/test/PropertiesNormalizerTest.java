package mobi.nowtechnologies.server.normalizer.test;

import mobi.nowtechnologies.server.normalizer.PropertiesNormalizer;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertTrue;

/**
 * @author Anton Zemliankin
 */
public class PropertiesNormalizerTest {

    private static final String ROOT = PropertiesNormalizerTest.class.getClassLoader().getResource(".").getFile();
    private static final String GOLDEN_PROPERTIES_FOLDER = "golden_properties";
    private static final String NORMALIZED_PROPERTIES_FOLDER = "normalized_properties";

    @After
    public void cleanup() throws IOException {
        FileUtils.deleteDirectory(new File(ROOT + NORMALIZED_PROPERTIES_FOLDER));
    }

    @Test
    public void testBundle() throws Exception {
        System.setProperty("normalizer.root.folder", ROOT + File.separator + "original_properties");
        System.setProperty("normalizer.properties.folder", "community_1/conf");
        System.setProperty("normalizer.property.name", "bundle.properties");

        PropertiesNormalizer.main(null);

        assertTrue(compareFiles("community_1/conf/bundle.properties"));
        assertTrue(compareFiles("community_1/conf/bundle_1.properties"));
        assertTrue(compareFiles("community_1/conf/bundle_2.properties"));
        assertTrue(compareFiles("community_1/conf/bundle_3.properties"));

        removeFile("community_1/conf/bundle.properties");
        removeFile("community_1/conf/bundle_1.properties");
        removeFile("community_1/conf/bundle_2.properties");
        removeFile("community_1/conf/bundle_3.properties");
    }

    @Test
    public void testRawProperties() throws Exception {
        System.setProperty("normalizer.root.folder", ROOT + File.separator + "original_properties");
        System.setProperty("normalizer.properties.folder", "*/conf");
        System.setProperty("normalizer.property.name", "app.properties");
        System.setProperty("normalizer.prod.environments", "community_1,community_2");

        PropertiesNormalizer.main(null);

        assertTrue(compareFiles("community_1/conf/app.properties"));
        assertTrue(compareFiles("community_2/conf/app.properties"));
        assertTrue(compareFiles("community_3/conf/app.properties"));
        assertTrue(compareFiles("community_4/conf/app.properties"));
        assertTrue(compareFiles("COMMON/conf/app.properties"));

        removeFile("community_1/conf/app.properties");
        removeFile("community_2/conf/app.properties");
        removeFile("community_3/conf/app.properties");
        removeFile("community_4/conf/app.properties");
        removeFile("COMMON/conf/app.properties");
    }

    private boolean compareFiles(String file) throws IOException {
        File file1 = new File(ROOT + NORMALIZED_PROPERTIES_FOLDER + File.separator + file);
        File file2 = new File(ROOT + GOLDEN_PROPERTIES_FOLDER + File.separator + file);
        return FileUtils.contentEquals(file1, file2);
    }

    private void removeFile(String fileStr) {
        File file = new File(ROOT + NORMALIZED_PROPERTIES_FOLDER + File.separator + fileStr);
        file.delete();
    }

}
