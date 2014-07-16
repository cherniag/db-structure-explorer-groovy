package mobi.nowtechnologies.server.service.file.image;

public class CloudFileMetadataService {

    private ImageService imageService;

    public ImageCloudFileMetadata forImage(byte[] imageBytes, String givenName) {
        ImageInfo imageInfo = imageService.getImageFormat(imageBytes);

        return ImageCloudFileMetadata.fromImageInfo(imageInfo, givenName);
    }

    public void setImageService(ImageService imageService) {
        this.imageService = imageService;
    }
}
