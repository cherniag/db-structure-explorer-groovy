package mobi.nowtechnologies.server.persistence.domain.streamzine;

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

    public String getInfo() {
        return width + "x" + height;
    }

    @Override
    public String toString() {
        return getInfo();
    }
}
