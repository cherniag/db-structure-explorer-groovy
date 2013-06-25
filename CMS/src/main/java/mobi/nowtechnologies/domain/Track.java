package mobi.nowtechnologies.domain;

import java.util.Date;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


@SuppressWarnings("serial")
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class Track extends AbstractEntity {

	
	@Basic(optional=false)
	protected String Ingestor;
	@Basic(optional=false)
	protected String ISRC;
	@Basic(optional=false)
	protected String Title;
	@Basic(optional=true)
	protected String SubTitle;
	@Basic(optional=false)
	protected String Artist;
	@Basic(optional=true)
	protected String ProductId;
	@Basic(optional=true)
	protected String ProductCode;
	@Basic(optional=true)
	protected String Genre;
	@Basic(optional=true)
	@Column(length=1024)
	protected String Copyright;
	@Basic(optional=true)
	protected String Year;	
	@Basic(optional=true)
	protected String Album;	
	@Basic(optional=true)
	protected String Info;	
	@Basic(optional=true)
	protected Boolean Licensed;	

	
    @Column(columnDefinition="LONGBLOB")
	@Lob()
	protected byte[] Xml;
	@Temporal(TemporalType.DATE)
	@Basic(optional=false)
	protected Date IngestionDate;	
	@Temporal(TemporalType.DATE)
	@Basic(optional=true)
	protected Date IngestionUpdateDate;	
	@Temporal(TemporalType.DATE)
	@Basic(optional=true)
	protected Date PublishDate;

	@OneToMany(cascade={CascadeType.ALL})
    @JoinColumn(name="TrackId") 
	protected Set<Territory> Territories; 

	@OneToMany(cascade={CascadeType.ALL})
    @JoinColumn(name="TrackId")
	protected Set<AssetFile> Files; 

	public String getISRC() {
		return ISRC;
	}

	public void setISRC(String iSRC) {
		ISRC = iSRC;
	}

	public String getTitle() {
		return Title;
	}

	public void setTitle(String title) {
		Title = title;
	}

	public String getArtist() {
		return Artist;
	}

	public void setArtist(String artist) {
		Artist = artist;
	}

	public String getGenre() {
		return Genre;
	}

	public void setGenre(String genre) {
		Genre = genre;
	}

	public Date getIngestionDate() {
		return IngestionDate;
	}

	public void setIngestionDate(Date ingestionDate) {
		IngestionDate = ingestionDate;
	}

	public Date getIngestionUpdateDate() {
		return IngestionUpdateDate;
	}

	public void setIngestionUpdateDate(Date ingestionUpdateDate) {
		IngestionUpdateDate = ingestionUpdateDate;
	}

	public Set<Territory> getTerritories() {
		return Territories;
	}

	public void setTerritories(Set<Territory> territories) {
		this.Territories = territories;
	}

	public Set<AssetFile> getFiles() {
		return Files;
	}

	public void setFiles(Set<AssetFile> files) {
		Files = files;
	}

	public byte[] getXml() {
		return Xml;
	}

	public void setXml(byte[] xml) {
		Xml = xml;
	}

	public Date getPublishDate() {
		return PublishDate;
	}

	public void setPublishDate(Date publishDate) {
		PublishDate = publishDate;
	}

	public String getProductId() {
		return ProductId;
	}

	public void setProductId(String productId) {
		ProductId = productId;
	}

	public String getCopyright() {
		return Copyright;
	}

	public void setCopyright(String copyright) {
		Copyright = copyright;
	}

	public String getYear() {
		return Year;
	}

	public void setYear(String year) {
		Year = year;
	}

	public String getIngestor() {
		return Ingestor;
	}

	public void setIngestor(String ingestor) {
		Ingestor = ingestor;
	}

	public String getProductCode() {
		return ProductCode;
	}

	public void setProductCode(String productCode) {
		ProductCode = productCode;
	}

	public String getAlbum() {
		return Album;
	}

	public void setAlbum(String album) {
		Album = album;
	}

	public String getInfo() {
		return Info;
	}

	public void setInfo(String info) {
		Info = info;
	}

	public String getSubTitle() {
		return SubTitle;
	}

	public void setSubTitle(String subTitle) {
		SubTitle = subTitle;
	}

	public Boolean getLicensed() {
		return Licensed;
	}

	public void setLicensed(Boolean licensed) {
		Licensed = licensed;
	}
	


}
