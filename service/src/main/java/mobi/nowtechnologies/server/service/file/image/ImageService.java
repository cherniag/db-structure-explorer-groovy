package mobi.nowtechnologies.server.service.file.image;

import com.google.common.io.Files;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Iterator;

public class ImageService {
    private Logger logger = LoggerFactory.getLogger(getClass());

    public byte[] resize(InputStream stream, int newWidth, int newHeight) {
        ImageInfo imageFormat = getImageFormat(stream);

        Assert.notNull(imageFormat);

        try {
            BufferedImage image = ImageIO.read(stream);
            BufferedImage scaled = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = scaled.createGraphics();
            g.setComposite(AlphaComposite.Src);
            g.drawImage(image, 0, 0, newWidth, newHeight, null);
            g.dispose();

            ByteArrayOutputStream outputBytesStream = new ByteArrayOutputStream();
            OutputStream out = new BufferedOutputStream(outputBytesStream);
            ImageIO.write(scaled, imageFormat.getFormat(), out);
            out.flush();
            return outputBytesStream.toByteArray();
        } catch (IOException e) {
            logger.error("Got an error during cropping image format", e);
            throw new RuntimeException(e);
        }
    }

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
        InputStream inputStream = new ByteArrayInputStream(bytes);
        return getImageFormat(inputStream);
    }

    public ImageInfo getImageFormat(InputStream inputStream) {
        ImageReader reader = null;
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

    public static void main(String[] args) throws IOException {
        File inputImage = new File("d:\\News_pic\\News_pic\\71224.jpg");
        byte[] bytes = Files.toByteArray(inputImage);

        byte[] resized = new ImageService().resize(new ByteArrayInputStream(bytes), 50, 50);

        Files.write(resized, new File("d:/ttt009090.jpeg"));
    }
}
