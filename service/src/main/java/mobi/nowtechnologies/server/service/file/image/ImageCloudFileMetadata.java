package mobi.nowtechnologies.server.service.file.image;

import java.util.HashMap;
import java.util.Map;

public class ImageCloudFileMetadata {

    private static final String FILE_NAME = "fileName";
    private static final String IMAGE_WIDTH = "Imagewidth";
    private static final String IMAGE_HEIGHT = "Imageheight";

    private int width;
    private int height;
    private String fileName;

    ImageCloudFileMetadata() {

    }

    public static ImageCloudFileMetadata fromImageInfo(ImageInfo imageInfo, String givenName) {
        ImageCloudFileMetadata data = new ImageCloudFileMetadata();
        data.width = imageInfo.getDimension().getWidth();
        data.height = imageInfo.getDimension().getHeight();
        data.fileName = givenName;
        return data;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public String getFileName() {
        return fileName;
    }

    public Map<String, String> toMap() {
        Map<String, String> mapView = new HashMap<String, String>();
        mapView.put(IMAGE_WIDTH, String.valueOf(width));
        mapView.put(IMAGE_HEIGHT, String.valueOf(height));
        mapView.put(FILE_NAME, fileName);
        return mapView;
    }
}
