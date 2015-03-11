package mobi.nowtechnologies.server.service.file.image;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import net.coobird.thumbnailator.Thumbnails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ImageService {

    private Logger logger = LoggerFactory.getLogger(getClass());

    public byte[] resize(byte[] bytes, int width, int height) throws IOException {
        ByteArrayInputStream ios = new ByteArrayInputStream(bytes);
        ByteArrayOutputStream ous = new ByteArrayOutputStream();
        Thumbnails.of(ios).size(width, height).toOutputStream(ous);
        return ous.toByteArray();
    }

    public ImageInfo getImageFormat(byte[] bytes) {
        InputStream inputStream = new ByteArrayInputStream(bytes);
        return getImageFormat(inputStream);
    }

    public ImageInfo getImageFormat(InputStream inputStream) {
        ImageReader reader = null;
        try {
            ImageInputStream imageInputStream = ImageIO.createImageInputStream(inputStream);

            Iterator<ImageReader> iter = ImageIO.getImageReaders(imageInputStream);

            if (!iter.hasNext()) {
                return null;
            }

            reader = iter.next();
            final String format = reader.getFormatName().toLowerCase();

            reader.setInput(imageInputStream);
            int minIndex = reader.getMinIndex();

            final int width = reader.getWidth(minIndex);
            final int height = reader.getHeight(minIndex);

            return new ImageInfo(format, width, height);
        } catch (IOException e) {
            logger.error("Got an error during getting image format", e);
            return null;
        } finally {
            if (reader != null) {
                reader.dispose();
            }
        }
    }
}
