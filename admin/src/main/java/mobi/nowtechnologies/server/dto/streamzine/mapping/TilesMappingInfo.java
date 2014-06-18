package mobi.nowtechnologies.server.dto.streamzine.mapping;

public class TilesMappingInfo {
    private String shapeType;
    private boolean title;
    private boolean subTitle;

    public String getShapeType() {
        return shapeType;
    }

    public void setShapeType(String shapeType) {
        this.shapeType = shapeType;
    }

    public boolean isTitle() {
        return title;
    }

    public void setTitle(boolean title) {
        this.title = title;
    }

    public boolean isSubTitle() {
        return subTitle;
    }

    public void setSubTitle(boolean subTitle) {
        this.subTitle = subTitle;
    }
}
