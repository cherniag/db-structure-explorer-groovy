package mobi.nowtechnologies.server.service.impl;

import mobi.nowtechnologies.server.service.CloudFileService;
import mobi.nowtechnologies.server.service.exception.ExternalServiceException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.io.IOUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import org.junit.*;
import org.junit.runner.*;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/META-INF/shared.xml", "/META-INF/service-test.xml", "/META-INF/dao-test.xml"})
@TransactionConfiguration(transactionManager = "persistence.TransactionManager", defaultRollback = true)
@Transactional
@Ignore
public class CloudFileServiceIT {

    @Autowired
    private CloudFileService cloudFileService;

    @Value("classpath:testData\\image\\USJAY1100032S_fileResolution.jpg")
    private File file;

    @Test
    public void testCopyFile_Success() throws Exception {
        // Preparations for test
        String destFileName = "destFileName";
        String destContainerName = "test-storage";
        String srcFileName = "11122233344455.jpg";
        String srcContainerName = "test-storage";

        cloudFileService.uploadFile(createTestFile(), srcFileName);

        // Invocation of test method
        boolean copied = cloudFileService.copyFile(srcContainerName, srcFileName, destContainerName, "11122233344455566.jpg");

        // Asserts
        Assert.assertTrue(copied);
    }

    @Test(expected = ExternalServiceException.class)
    public void testCopyFile_NotExistSrcContainer_Failure() throws Exception {
        // Preparations for test
        String destFileName = "destFileName";
        String destContainerName = "test-storage";
        String srcFileName = "srcFileName";
        String srcContainerName = "test-storage_F";

        cloudFileService.uploadFile(createTestFile(), srcFileName);

        // Invocation of test method
        cloudFileService.copyFile(srcContainerName, srcFileName, destContainerName, destFileName);
    }

    @Test(expected = ExternalServiceException.class)
    public void testCopyFile_NotExistSrcFile_Failure() throws Exception {
        // Preparations for test
        String destFileName = "destFileName";
        String destContainerName = "test-storage";
        String srcFileName = "srcFileName_F";
        String srcContainerName = "test-storage";

        cloudFileService.uploadFile(createTestFile(), "srcFileName");

        // Invocation of test method
        cloudFileService.copyFile(srcContainerName, srcFileName, destContainerName, destFileName);
    }

    @Test(expected = ExternalServiceException.class)
    public void testCopyFile_NotExistDestContainer_Failure() throws Exception {
        // Preparations for test
        String destFileName = "destFileName";
        String destContainerName = "test-storage_F";
        String srcFileName = "srcFileName";
        String srcContainerName = "test-storage";

        cloudFileService.uploadFile(createTestFile(), srcFileName);

        // Invocation of test method
        cloudFileService.copyFile(srcContainerName, srcFileName, destContainerName, destFileName);
    }

    private MultipartFile createTestFile() throws IOException {
        InputStream srcFile = new FileInputStream(file);
        FileItem fileItem = new DiskFileItemFactory().createItem("srcFileName", "image/jpeg", true, "11122233344455.jpg");
        IOUtils.copy(srcFile, fileItem.getOutputStream());
        MultipartFile file = new CommonsMultipartFile(fileItem);

        return file;
    }
}