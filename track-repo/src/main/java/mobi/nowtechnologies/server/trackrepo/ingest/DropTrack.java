package mobi.nowtechnologies.server.trackrepo.ingest;

import mobi.nowtechnologies.server.trackrepo.domain.AssetFile;
import mobi.nowtechnologies.server.trackrepo.enums.ReportingType;
import static mobi.nowtechnologies.server.trackrepo.enums.ReportingType.REPORTED_BY_TAGS;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;

public class DropTrack {

    public String xml;

    ;
    public Type type;
    public String productCode;
    public String title;
    public String subTitle;
    public String artist;
    public String genre;
    public String copyright;
    public String label;
    public String isrc;
    public String year;
    public String physicalProductId;
    public String album;
    public String info;
    public boolean licensed;
    public boolean exists;
    public boolean explicit;
    public List<DropAssetFile> files = new ArrayList<DropAssetFile>();
    public List<DropTerritory> territories = new ArrayList<DropTerritory>();
    public String productId;
    public ReportingType reportingType = REPORTED_BY_TAGS;

    public DropTrack() {
        licensed = true; // by default
    }

    public String getXml() {
        return xml;
    }

    public void setXml(String xml) {
        this.xml = xml;
    }

    public DropTrack addXml(String xml) {
        this.xml = xml;
        return this;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public DropTrack addType(Type type) {
        this.type = type;
        return this;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public DropTrack addProductCode(String productCode) {
        this.productCode = productCode;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public DropTrack addTitle(String title) {
        this.title = title;
        return this;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public DropTrack addSubTitle(String subTitle) {
        this.subTitle = subTitle;
        return this;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public boolean isExplicit() {
        return explicit;
    }

    public void setExplicit(boolean explicit) {
        this.explicit = explicit;
    }

    public DropTrack addExplicit(boolean explicit) {
        this.explicit = explicit;
        return this;
    }

    public DropTrack addArtist(String artist) {
        this.artist = artist;
        return this;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public DropTrack addGenre(String genre) {
        this.genre = genre;
        return this;
    }

    public String getCopyright() {
        return copyright;
    }

    public void setCopyright(String copyright) {
        this.copyright = copyright;
    }

    public DropTrack addCopyright(String copyright) {
        this.copyright = copyright;
        return this;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public DropTrack addLabel(String label) {
        this.label = label;
        return this;
    }

    public String getIsrc() {
        return isrc;
    }

    public void setIsrc(String isrc) {
        this.isrc = isrc;
    }

    public DropTrack addIsrc(String isrc) {
        this.isrc = isrc;
        return this;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public DropTrack addYear(String year) {
        this.year = year;
        return this;
    }

    public String getPhysicalProductId() {
        return physicalProductId;
    }

    public void setPhysicalProductId(String physicalProductId) {
        this.physicalProductId = physicalProductId;
    }

    public DropTrack addPhysicalProductId(String physicalProductId) {
        this.physicalProductId = physicalProductId;
        return this;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public DropTrack addAlbum(String album) {
        this.album = album;
        return this;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public DropTrack addInfo(String info) {
        this.info = info;
        return this;
    }

    public boolean isLicensed() {
        return licensed;
    }

    public void setLicensed(boolean licensed) {
        this.licensed = licensed;
    }

    public DropTrack addLicensed(boolean licensed) {
        this.licensed = licensed;
        return this;
    }

    public List<DropAssetFile> getFiles() {
        return files;
    }

    public void setFiles(List<DropAssetFile> files) {
        this.files = files;
    }

    public boolean hasAnyMediaResources() {
        if (files != null) {
            for (DropAssetFile file : files) {
                if (file.type == AssetFile.FileType.VIDEO || file.type == AssetFile.FileType.DOWNLOAD) {
                    return true;
                }
            }
        }

        return false;
    }

    public DropTrack addFiles(List<DropAssetFile> files) {
        this.files = files;
        return this;
    }

    public List<DropTerritory> getTerritories() {
        return territories;
    }

    public void setTerritories(List<DropTerritory> territories) {
        this.territories = territories;
    }

    public DropTrack addTerritories(List<DropTerritory> territories) {
        this.territories = territories;
        return this;
    }

    public DropTrack addTerritories(DropTerritory... territory) {
        for (DropTerritory t : territory) {
            this.territories.add(t);
        }
        return this;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public DropTrack addProductId(String productId) {
        this.productId = productId;
        return this;
    }

    public boolean isExists() {
        return exists;
    }

    public void setExists(boolean exists) {
        this.exists = exists;
    }

    public DropTrack addExists(boolean exists) {
        this.exists = exists;
        return this;
    }

    public ReportingType getReportingType() {
        return reportingType;
    }

    public void setReportingType(ReportingType reportingType) {
        this.reportingType = reportingType;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("xml", xml).append("type", type).append("productCode", productCode).append("title", title).append("subTitle", subTitle).append("artist", artist)
                                        .append("genre", genre).append("copyright", copyright).append("label", label).append("isrc", isrc).append("year", year)
                                        .append("physicalProductId", physicalProductId).append("album", album).append("info", info).append("licensed", licensed).append("exists", exists)
                                        .append("explicit", explicit).append("productId", productId).append("reportingType", reportingType).toString();
    }

    public enum Type {
        INSERT, UPDATE, DELETE
    }
}
