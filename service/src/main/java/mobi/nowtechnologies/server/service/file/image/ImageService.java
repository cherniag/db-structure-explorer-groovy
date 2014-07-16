package mobi.nowtechnologies.server.service.file.image;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Iterator;

public class ImageService {
    private Logger logger = LoggerFactory.getLogger(getClass());

    public byte[] crop(byte[] bytes, int x, int y, int width, int height) {
        ImageInfo imageFormat = getImageFormat(bytes);

        Assert.notNull(imageFormat);

        try {
            BufferedImage image = ImageIO.read(new ByteArrayInputStream(bytes));
            BufferedImage subimage = image.getSubimage(x, y, width, height);

            ByteArrayOutputStream outputBytesStream = new ByteArrayOutputStream();
            OutputStream out = new BufferedOutputStream(outputBytesStream);
            ImageIO.write(subimage, imageFormat.getFormat(), out);
            out.flush();
            return outputBytesStream.toByteArray();
        } catch (IOException e) {
            logger.error("Got an error during cropping image format", e);
            throw new RuntimeException(e);
        }
    }

    public ImageInfo getImageFormat(byte[] bytes) {
        ImageReader reader = null;
        InputStream inputStream = new ByteArrayInputStream(bytes);
        try {
            ImageInputStream imageInputStream = ImageIO.createImageInputStream(inputStream);

            Iterator<ImageReader> iter = ImageIO.getImageReaders(imageInputStream);

            if(!iter.hasNext()) {
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
/*
    public static void main(String[] args) throws IOException {
        File inputImage = new File("d:/heli.jpeg");
        byte[] bytes = Files.toByteArray(inputImage);

        byte[] crop = new ImageService().crop(bytes, 0, 0, 550, 550);

        Files.write(crop, new File("d:/heli_cropped.jpeg"));
    }*/
}
