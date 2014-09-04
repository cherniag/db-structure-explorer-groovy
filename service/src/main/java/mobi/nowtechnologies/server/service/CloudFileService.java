package mobi.nowtechnologies.server.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

public interface CloudFileService {
	
	String getFilesURL();
	
	boolean uploadFile(MultipartFile file, String fileName, Map metadata);

	void uploadFromStream(InputStream stream, String fileName, Map metadata);

    void downloadToStream(OutputStream stream, String fileName);

	boolean uploadFile(MultipartFile file, String fileName);
	
	boolean uploadFile(File file, String fileName, String contentType, String destinationContainer);

	boolean copyFile(String destFileName, String destContainerName, String srcFileName, String srcContainerName);

    void deleteFile(String fileName);

}