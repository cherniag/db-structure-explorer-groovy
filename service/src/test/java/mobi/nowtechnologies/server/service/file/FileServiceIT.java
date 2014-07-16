package mobi.nowtechnologies.server.service.file;

import com.google.common.io.Files;
import mobi.nowtechnologies.server.service.file.file.FileInfo;
import mobi.nowtechnologies.server.service.file.file.FileService;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.*;

public class FileServiceIT {
    private FileService fileService = new FileService();

    private File structureDir;
    private File testDir;

    private File nestedDir;

    @Before
    public void setUp() throws Exception {
        structureDir = new ClassPathResource("file/structure").getFile();
        testDir = new ClassPathResource("file/test").getFile();

        nestedDir = new File(testDir, "nested");
        nestedDir.mkdir();
    }

    @After
    public void tearDown() throws Exception {
        FileUtils.deleteDirectory(nestedDir);
    }

    @Test
    public void testCreateRoot() throws Exception {
        fileService.setRootDir(nestedDir);
        fileService.afterPropertiesSet();

        assertEquals(0, nestedDir.list().length);

        final String testName = "test";
        fileService.createRoot(testName);
        assertEquals(1, nestedDir.listFiles().length);
        assertEquals(testName, nestedDir.listFiles()[0].getName());
    }

    @Test
    public void testCreate() throws Exception {
        fileService.setRootDir(nestedDir);
        fileService.afterPropertiesSet();

        final String firstRoot = "firstRoot";
        fileService.createRoot(firstRoot);

        final String testDirName = "testDirName";
        File newRootFile = new File(nestedDir, firstRoot);
        fileService.create(newRootFile.getPath(), testDirName);

        assertEquals(1, newRootFile.listFiles().length);
        assertEquals(testDirName, newRootFile.listFiles()[0].getName());
    }

    @Test
    public void testDelete() throws Exception {
        fileService.setRootDir(nestedDir);
        fileService.afterPropertiesSet();

        final String firstRoot = "firstRoot";
        fileService.createRoot(firstRoot);

        final String testDirName = "testDirName";
        File newRootFile = new File(nestedDir, firstRoot);
        fileService.create(newRootFile.getPath(), testDirName);

        File file = new File(newRootFile, testDirName);
        assertTrue(file.exists());
        fileService.remove(file.getPath());
        assertFalse(file.exists());
    }

    @Test
    @Ignore
    public void testStructure() throws Exception {
        fileService.setRootDir(structureDir);
        fileService.afterPropertiesSet();

        List<FileInfo> rootDirContent = fileService.getRootDirContent();

        assertEquals(1, rootDirContent.size());

        FileInfo file1 = rootDirContent.get(0);
        assertEquals("file1", file1.getName());

        assertEquals(2, file1.getChildren().size());

        Iterator<FileInfo> iterator = file1.getChildren().iterator();
        FileInfo firstChild = iterator.next();
        FileInfo secondChild = iterator.next();

        assertTrue(Arrays.asList(firstChild.getName(), secondChild.getName()).contains("file10"));
        assertTrue(Arrays.asList(firstChild.getName(), secondChild.getName()).contains("file11"));

        assertEquals("file020.txt", firstChild.getChildren().iterator().next().getChildren().iterator().next().getName());
        assertEquals("file101.txt", secondChild.getChildren().iterator().next().getName());
    }

    @Test
    public void testUpload() throws Exception {
        fileService.setRootDir(nestedDir);
        fileService.afterPropertiesSet();

        final String firstRoot = "firstRoot";
        fileService.createRoot(firstRoot);

        final String testDirName = "testDirName";
        File newRootFile = new File(nestedDir, firstRoot);
        fileService.create(newRootFile.getPath(), testDirName);

        File file = new File(newRootFile, testDirName);
        assertTrue(file.exists());

        // prepare file to upload:
        File uploadedFile = new File(nestedDir, "uploaded.txt");
        Files.touch(uploadedFile);

        fileService.upload(file.getPath(), uploadedFile);
        assertEquals(1, file.listFiles().length);
        assertEquals(uploadedFile.getName(), file.listFiles()[0].getName());
    }
}
