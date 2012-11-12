package mobi.nowtechnologies.server.service;

import org.springframework.web.multipart.MultipartFile;

public interface CloudFileService {
	
	String getFilesURL();
	
	boolean uploadFile(MultipartFile file, String fileName);

}