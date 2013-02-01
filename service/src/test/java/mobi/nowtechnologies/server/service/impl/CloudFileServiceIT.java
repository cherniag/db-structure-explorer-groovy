package mobi.nowtechnologies.server.service.impl;

import java.io.*;

import mobi.nowtechnologies.server.service.CloudFileService;
import mobi.nowtechnologies.server.service.exception.ExternalServiceException;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/META-INF/shared.xml", "/META-INF/dao-test.xml", "/META-INF/service-test.xml" })
@TransactionConfiguration(transactionManager = "persistence.TransactionManager", defaultRollback = true)
@Transactional
public class CloudFileServiceIT {
	private final String DEFAULT_FILE_NAME = "testData/image/USJAY1100032S_fileResolution.jpg";

	@Autowired
	private CloudFileService cloudFileService;

	@Test
	public void testCopyFile_Success() throws Exception {
		// Preparations for test
		String destFileName = "destFileName";
		String destContainerName = "test-storage";
		String srcFileName = "srcFileName";
		String srcContainerName = "test-storage";
				
		cloudFileService.uploadFile(createTestFile(), srcFileName);

		// Invocation of test method
		boolean copied = cloudFileService.copyFile(destFileName, destContainerName, srcFileName, srcContainerName);

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
		cloudFileService.copyFile(destFileName, destContainerName, srcFileName, srcContainerName);
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
		cloudFileService.copyFile(destFileName, destContainerName, srcFileName, srcContainerName);
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
		cloudFileService.copyFile(destFileName, destContainerName, srcFileName, srcContainerName);
	}
	
	private MultipartFile createTestFile() throws IOException{
		InputStream srcFile = getClass().getClassLoader().getResourceAsStream(DEFAULT_FILE_NAME);
		FileItem fileItem = new DiskFileItemFactory().createItem("srcFileName", "application/octet-stream", true, "srcFileName");
		IOUtils.copy(srcFile, fileItem.getOutputStream());
		MultipartFile file = new CommonsMultipartFile(fileItem); 
		
		return file;
	}
}