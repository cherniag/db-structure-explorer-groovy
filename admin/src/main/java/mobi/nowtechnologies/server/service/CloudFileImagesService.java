package mobi.nowtechnologies.server.service;

import com.google.common.collect.Lists;
import com.rackspacecloud.client.cloudfiles.FilesObject;
import mobi.nowtechnologies.server.dto.ImageDTO;
import mobi.nowtechnologies.server.service.file.image.ImageInfo;
import mobi.nowtechnologies.server.service.file.image.ImageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.util.CollectionUtils.isEmpty;

/**
 * Created by oar on 2/25/14.
 */
public class CloudFileImagesService {
    private static final String FILE_NAME_CONSTANT = "fileName";
    private static final String IMAGE_WIDTH = "Imagewidth";
    private static final String IMAGE_HEIGHT = "Imageheight";

    private static final int DEFAULT_SIZE = 10000;

    private CloudFileService cloudFileService;

    private ImageService imageService;

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

    public void setImageService(ImageService imageService) {
        this.imageService = imageService;
    }

    private ImageDTO doUploadImage(MultipartFile file, String fileNameInCloud) {
        Map<String, String> metadata = buildMetadataForFile(file);
        boolean resultUpload = cloudFileService.uploadFile(file, fileNameInCloud, metadata);
        logger.info("File with name {} is uploaded: {}", fileNameInCloud, resultUpload);
        if (resultUpload) {
            return convertToDTO(metadata);
        }
        return null;
    }


    private ImageDTO convertToDTO(Map<String, String> metadata) {
        ImageDTO result = new ImageDTO();
        result.setFileName(metadata.get(FILE_NAME_CONSTANT));
        result.setHeight(Integer.valueOf(metadata.get(IMAGE_HEIGHT)));
        result.setWidth(Integer.valueOf(metadata.get(IMAGE_WIDTH)));
        result.setUrl(buildImageUrl(result.getFileName().toLowerCase()));
        return result;
    }


    private ImageDTO convertToDTO(FilesObject filesObject) {
        try {
            return convertToDTO(filesObject.getMetaData().getMetaData());
        } catch (Exception e) {
            logger.error("ERROR", e);
        }
        return null;
    }


    private String buildImageUrl(String filesObjectName) {
        return cloudFileService.getFilesURL() + filesObjectName;
    }

    private Map<String, String> buildMetadataForFile(MultipartFile file) {
        Map<String, String> resultMap = new HashMap<String, String>();
        try {
            ImageInfo imageInfo = imageService.getImageFormat(file.getBytes());
            if (imageInfo != null) {
                resultMap.put(IMAGE_WIDTH, String.valueOf(imageInfo.getDimension().getWidth()));
                resultMap.put(IMAGE_HEIGHT, String.valueOf(imageInfo.getDimension().getHeight()));
            }
        } catch (IOException e) {
            logger.error("Error during extracting image info", e);
        }
        resultMap.put(FILE_NAME_CONSTANT, file.getOriginalFilename());
        return resultMap;
    }
}
