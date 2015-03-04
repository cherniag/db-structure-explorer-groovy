package mobi.nowtechnologies.server.trackrepo.utils;

import mobi.nowtechnologies.server.service.CloudFileService;
import mobi.nowtechnologies.server.trackrepo.domain.Track;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UploadToCloudFileManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(UploadToCloudFileManager.class);
    private String dataContainerName;
    private String privateContainerName;
    private CloudFileService cloudService;
    private Map<String, String> fileExtensionToContentTypeMap;
    private String defaultContentType;

    public void uploadFilesToCloud(Track track, List<String> filesToPrivate, List<String> filesToData) {
        LOGGER.info("Uploading files to private container : [{}], to data container : [{}] for track {}", filesToPrivate, filesToData, track.getUniqueTrackId());
        for (String fileName : filesToPrivate) {

            File file = new File(fileName);
            if (file.exists()) {
                String contentType = getContentTypeByExtensioin(fileName);
                cloudService.uploadFile(file, track.getId() + "_" + file.getName(), contentType, privateContainerName);
            }
            else {
                LOGGER.warn("File {} doesn't exist", file);
            }
        }

        for (String fileName : filesToData) {

            File file = new File(fileName);
            if (file.exists()) {
                String contentType = getContentTypeByExtensioin(fileName);
                cloudService.uploadFile(file, file.getName(), contentType, dataContainerName);
            }
            else {
                LOGGER.warn("File {} doesn't exist", file);
            }
        }
        LOGGER.info("Uploading done");
    }

    private String getContentTypeByExtensioin(String fileName) {

        for (Map.Entry<String, String> entry : fileExtensionToContentTypeMap.entrySet()) {
            if (fileName.toLowerCase().endsWith(entry.getKey().toLowerCase())) {
                return entry.getValue();
            }
        }

        return defaultContentType;
    }

    public void setDataContainerName(String dataContainerName) {
        this.dataContainerName = dataContainerName;
    }

    public void setPrivateContainerName(String privateContainerName) {
        this.privateContainerName = privateContainerName;
    }

    public void setCloudService(CloudFileService cloudService) {
        this.cloudService = cloudService;
    }

    public void setFileExtensionToContentTypeMap(Map<String, String> fileExtensionToContentTypeMap) {
        this.fileExtensionToContentTypeMap = fileExtensionToContentTypeMap;
    }

    public void setDefaultContentType(String defaultContentType) {
        this.defaultContentType = defaultContentType;
    }
}
