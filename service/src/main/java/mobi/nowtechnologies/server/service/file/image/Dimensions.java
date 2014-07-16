package mobi.nowtechnologies.server.service.file.image;

import org.apache.commons.lang.builder.ToStringBuilder;


public class Dimensions {
    private int width;

    private int height;

    public Dimensions(int width, int height) {
        setWidth(width);
        setHeight(height);
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("width", width)
                .append("height", height)
                .toString();
    }
}
