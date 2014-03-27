package mobi.nowtechnologies.server.service;

import java.io.File;

import org.springframework.web.multipart.MultipartFile;

public interface CloudFileService {
	
	String getFilesURL();
	
	boolean uploadFile(MultipartFile file, String fileName);
	
	boolean uploadFile(File file, String fileName, String contentType, String destinationContainer);

	boolean copyFile(String destFileName, String destContainerName, String srcFileName, String srcContainerName);

	boolean login();

}