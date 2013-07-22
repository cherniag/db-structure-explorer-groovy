package mobi.nowtechnologies.server.trackrepo.dto;

import mobi.nowtechnologies.server.trackrepo.enums.DropTrackType;

/**
 * Created with IntelliJ IDEA.
 * User: sanya
 * Date: 7/15/13
 * Time: 2:52 PM
 * To change this template use File | Settings | File Templates.
 */
public class DropTrackDto {
    private String productCode;
    private String title;
    private String artist;
    private String isrc;
    private Boolean exists;
    private DropTrackType type;
    private Boolean ingest;

    public Boolean getIngest() {
        return ingest;
    }

    public void setIngest(Boolean ingest) {
        this.ingest = ingest;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getIsrc() {
        return isrc;
    }

    public void setIsrc(String isrc) {
        this.isrc = isrc;
    }

    public Boolean getExists() {
        return exists;
    }

    public void setExists(Boolean exists) {
        this.exists = exists;
    }

    public DropTrackType getType() {
        return type;
    }

    public void setType(DropTrackType type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "DropTrackDto{" +
                "productCode='" + productCode + '\'' +
                ", title='" + title + '\'' +
                ", artist='" + artist + '\'' +
                ", isrc='" + isrc + '\'' +
                ", exists=" + exists +
                ", type=" + type +
                ", ingest=" + ingest +
                "} " + super.toString();
    }
}
