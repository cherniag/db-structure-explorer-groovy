package mobi.nowtechnologies.server.service.file.image;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.springframework.util.Assert;


public class ImageInfo {
  private String format;

  private Dimensions dimension;

  public ImageInfo(String format, int width, int height) {
    Assert.hasText(format);
    this.format = format;
    this.dimension = new Dimensions(width, height);
  }

  public String getFormat() {
    return format;
  }

  public Dimensions getDimension() {
    return dimension;
  }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("format", format)
                .append("dimension", dimension)
                .toString();
    }
}
