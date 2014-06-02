package mobi.nowtechnologies.server.service;

import com.google.common.collect.Lists;
import com.rackspacecloud.client.cloudfiles.FilesObject;
import mobi.nowtechnologies.server.dto.ImageDTO;
import mobi.nowtechnologies.server.service.file.image.CloudFileMetadataService;
import mobi.nowtechnologies.server.service.file.image.ImageCloudFileMetadata;
import org.apache.http.HttpException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;

import static org.springframework.util.CollectionUtils.isEmpty;

/**
 * Created by oar on 2/25/14.
 */
public class CloudFileImagesService {
    private static final int DEFAULT_SIZE = 10000;

    private CloudFileService cloudFileService;

    private CloudFileMetadataService cloudFileMetadataService;

    private Logger logger = LoggerFactory.getLogger(getClass());

    public Collection<ImageDTO> findByPrefix(String prefix) {
        Assert.hasText(prefix);
        Collection<FilesObject> files = cloudFileService.findFilesStartWith(prefix.toLowerCase(), DEFAULT_SIZE);
        if (!isEmpty(files)) {
            Collection<ImageDTO> result = Lists.newArrayList();
            for (FilesObject filesObject : files) {
                result.add(convertToDTO(filesObject));
            }
            return result;
        }
        return Collections.emptyList();
    }

    public ImageDTO uploadImageWithGivenName(MultipartFile file, String nameInCloud) {
        return doUploadImage(file, nameInCloud);
    }

    public ImageDTO uploadImage(MultipartFile file) {
        String originalFileName = file.getOriginalFilename();
        String fileNameInCloud = originalFileName.toLowerCase();
        return doUploadImage(file, fileNameInCloud);
    }

    public void deleteImage(String fileName) {
        cloudFileService.deleteFile(fileName.toLowerCase());
    }

    public void setCloudFileService(CloudFileService cloudFileService) {
        this.cloudFileService = cloudFileService;
    }

    public void setCloudFileMetadataService(CloudFileMetadataService cloudFileMetadataService) {
        this.cloudFileMetadataService = cloudFileMetadataService;
    }

    private ImageDTO doUploadImage(MultipartFile file, String fileNameInCloud) {
        ImageCloudFileMetadata imageCloudFileMetadata = null;
        try {
            imageCloudFileMetadata = cloudFileMetadataService.forImage(file.getBytes(), file.getOriginalFilename());

            boolean resultUpload = cloudFileService.uploadFile(file, fileNameInCloud, imageCloudFileMetadata.toMap());

            logger.info("File with name {} is uploaded: {}", fileNameInCloud, resultUpload);

            if (resultUpload) {
                return convertToDTO(imageCloudFileMetadata);
            }
            return null;
        } catch (IOException e) {
            logger.error("Got the problem during getting image info for: " + file, e);
            throw new RuntimeException(e);
        }
    }


    private ImageDTO convertToDTO(ImageCloudFileMetadata imageCloudFileMetadata) {
        ImageDTO result = new ImageDTO();
        result.setFileName(imageCloudFileMetadata.getFileName());
        result.setHeight(imageCloudFileMetadata.getHeight());
        result.setWidth(imageCloudFileMetadata.getWidth());
        result.setUrl(cloudFileService.getFilesURL() + imageCloudFileMetadata.getFileName());
        return result;
    }


    private ImageDTO convertToDTO(FilesObject filesObject) {
        try {
            ImageCloudFileMetadata imageCloudFileMetadata = ImageCloudFileMetadata.fromFilesObjectMetaData(filesObject.getMetaData());
            return convertToDTO(imageCloudFileMetadata);
        } catch (HttpException e) {
            logger.error("Got the problem during extracting cloud file metadata for: " + filesObject, e);
            throw new RuntimeException(e);
        } catch (IOException e) {
            logger.error("Got the problem during extracting cloud file metadata for: " + filesObject, e);
            throw new RuntimeException(e);
        }
    }
}
