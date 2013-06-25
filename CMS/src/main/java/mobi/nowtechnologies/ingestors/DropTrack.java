package mobi.nowtechnologies.ingestors;

import java.util.ArrayList;
import java.util.List;

public class DropTrack {
	public enum Type {
		INSERT, UPDATE, DELETE
	};
	
	public DropTrack() {
		licensed = true; // by default
	}

	public String xml;
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
	public List<DropAssetFile> files = new ArrayList<DropAssetFile>();
	public List<DropTerritory> territories = new ArrayList<DropTerritory>();
	public String productId;
	public String getXml() {
		return xml;
	}
	public void setXml(String xml) {
		this.xml = xml;
	}
	public Type getType() {
		return type;
	}
	public void setType(Type type) {
		this.type = type;
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
	public String getSubTitle() {
		return subTitle;
	}
	public void setSubTitle(String subTitle) {
		this.subTitle = subTitle;
	}
	public String getArtist() {
		return artist;
	}
	public void setArtist(String artist) {
		this.artist = artist;
	}
	public String getGenre() {
		return genre;
	}
	public void setGenre(String genre) {
		this.genre = genre;
	}
	public String getCopyright() {
		return copyright;
	}
	public void setCopyright(String copyright) {
		this.copyright = copyright;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public String getIsrc() {
		return isrc;
	}
	public void setIsrc(String isrc) {
		this.isrc = isrc;
	}
	public String getYear() {
		return year;
	}
	public void setYear(String year) {
		this.year = year;
	}
	public String getPhysicalProductId() {
		return physicalProductId;
	}
	public void setPhysicalProductId(String physicalProductId) {
		this.physicalProductId = physicalProductId;
	}
	public String getAlbum() {
		return album;
	}
	public void setAlbum(String album) {
		this.album = album;
	}
	public String getInfo() {
		return info;
	}
	public void setInfo(String info) {
		this.info = info;
	}
	public boolean isLicensed() {
		return licensed;
	}
	public void setLicensed(boolean licensed) {
		this.licensed = licensed;
	}
	public List<DropAssetFile> getFiles() {
		return files;
	}
	public void setFiles(List<DropAssetFile> files) {
		this.files = files;
	}
	public List<DropTerritory> getTerritories() {
		return territories;
	}
	public void setTerritories(List<DropTerritory> territories) {
		this.territories = territories;
	}
	public String getProductId() {
		return productId;
	}
	public void setProductId(String productId) {
		this.productId = productId;
	}
	public boolean isExists() {
		return exists;
	}
	public void setExists(boolean exists) {
		this.exists = exists;
	}

}
