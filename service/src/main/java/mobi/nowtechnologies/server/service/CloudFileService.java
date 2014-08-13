package mobi.nowtechnologies.server.service;

import com.rackspacecloud.client.cloudfiles.FilesNotFoundException;
import com.rackspacecloud.client.cloudfiles.FilesObject;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.InputStream;
import java.util.Collection;
import java.util.Map;

public interface CloudFileService {
	
	String getFilesURL();
	
	boolean uploadFile(MultipartFile file, String fileName, Map metadata);

	boolean uploadFile(MultipartFile file, String fileName);
	
	boolean uploadFile(File file, String fileName, String contentType, String destinationContainer);

	boolean copyFile(String destFileName, String destContainerName, String srcFileName, String srcContainerName);

    Collection<FilesObject> findFilesStartWith(String prefix, int limit);

    void deleteByPrefix(String prefix);

    void deleteFile(String fileName);

    InputStream getInputStream(String fileName) throws FilesNotFoundException;

}