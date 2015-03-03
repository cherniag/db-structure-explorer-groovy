package mobi.nowtechnologies.server.service;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

import com.rackspacecloud.client.cloudfiles.FilesNotFoundException;

import org.springframework.web.multipart.MultipartFile;

public interface CloudFileService {

    String getFilesURL();

    boolean uploadFile(MultipartFile file, String fileName, Map metadata);

    void uploadFromStream(InputStream stream, String fileName, Map metadata);

    void downloadToStream(OutputStream stream, String fileName);

    boolean uploadFile(MultipartFile file, String fileName);

    boolean uploadFile(File file, String fileName, String contentType, String destinationContainer);

    boolean copyFile(String srcContainerName, String srcFileName, String targetContainerName, String targetFileName);

    void deleteFile(String fileName);

    InputStream getInputStream(String destinationContainer, String fileName) throws FilesNotFoundException;

    boolean fileExists(String destinationContainer, String fileName);

}