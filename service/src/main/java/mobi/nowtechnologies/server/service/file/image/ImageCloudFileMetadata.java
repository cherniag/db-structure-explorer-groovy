package mobi.nowtechnologies.server.service.file.image;

import com.rackspacecloud.client.cloudfiles.FilesObjectMetaData;

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

    public static ImageCloudFileMetadata fromFilesObjectMetaData(FilesObjectMetaData metaData) {
        final Map<String, String> meta = metaData.getMetaData();
        ImageCloudFileMetadata data  = new ImageCloudFileMetadata();
        data.width = Integer.parseInt(meta.get(IMAGE_WIDTH));
        data.height = Integer.parseInt(meta.get(IMAGE_HEIGHT));
        data.fileName = meta.get(FILE_NAME);
        return data;
    }

    public static ImageCloudFileMetadata fromImageInfo(ImageInfo imageInfo, String givenName) {
        ImageCloudFileMetadata data  = new ImageCloudFileMetadata();
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
