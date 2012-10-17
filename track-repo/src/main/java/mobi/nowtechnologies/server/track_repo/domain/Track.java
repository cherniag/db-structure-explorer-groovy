package mobi.nowtechnologies.server.track_repo.domain;

import java.util.Date;
import java.util.Set;

import javax.persistence.*;

import mobi.nowtechnologies.server.shared.dto.AudioResolution;
import mobi.nowtechnologies.server.shared.dto.TrackStatus;

/**
 * 
 * @author Alexander Kolpakov (akolpakov)
 *
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class Track extends AbstractEntity {
	//------------basic properties----------------//
	@Basic(optional=false)
	@Column(name="Ingestor")
	protected String ingestor;
	@Basic(optional=false)
	@Column(name="ISRC")
	protected String isrc;
	@Basic(optional=false)
	@Column(name="Title")
	protected String title;
	@Basic(optional=false)
	@Column(name="Artist")
	protected String artist;	
	@Temporal(TemporalType.DATE)
	@Basic(optional=false)
	@Column(name="IngestionDate")
	protected Date ingestionDate;
	@Basic(optional=false)
	@Enumerated(EnumType.STRING)
	protected TrackStatus status;
	
	//-------------optional properties---------------//
	@Basic(optional=true)
	@Column(name="SubTitle")
	protected String subTitle;
	@Basic(optional=true)
	@Column(name="ProductId")
	protected String productId;
	@Basic(optional=true)
	@Column(name="ProductCode")
	protected String productCode;
	@Basic(optional=true)
	@Column(name="Genre")
	protected String genre;
	@Basic(optional=true)
	@Column(name="Copyright", length=1024)
	protected String copyright;
	@Basic(optional=true)
	@Column(name="Year")
	protected String year;	
	@Basic(optional=true)
	@Column(name="Album")
	protected String album;	
	@Basic(optional=true)
	@Column(name="Info")
	protected String info;	
	@Basic(optional=true)
	@Column(name="Licensed")
	protected Boolean licensed;	
	@Temporal(TemporalType.DATE)
	@Basic(optional=true)
	@Column(name="IngestionUpdateDate")
	protected Date ingestionUpdateDate;	
	@Temporal(TemporalType.DATE)
	@Basic(optional=true)
	@Column(name="PublishDate")
	protected Date publishDate;
	
    @Column(name="Xml", columnDefinition="LONGBLOB")
	@Lob()
	protected byte[] xml;	

	@OneToMany(cascade={CascadeType.ALL})
    @JoinColumn(name="TrackId") 
	@Column(name="Territories")
	protected Set<Territory> territories; 

	@OneToMany(cascade={CascadeType.ALL})
    @JoinColumn(name="TrackId")
	@Column(name="Files")
	protected Set<AssetFile> files;
	
	@Basic(optional=true)
	@Enumerated(EnumType.STRING)
	protected AudioResolution resolution; 
	protected String itunesUrl; 
	
	public Track()
	{
		status = TrackStatus.NONE;
		resolution = AudioResolution.RATE_ORIGINAL;
	}

	public String getIngestor() {
		return ingestor;
	}

	public void setIngestor(String ingestor) {
		this.ingestor = ingestor;
	}

	public String getIsrc() {
		return isrc;
	}

	public void setIsrc(String isrc) {
		this.isrc = isrc;
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

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public String getProductCode() {
		return productCode;
	}

	public void setProductCode(String productCode) {
		this.productCode = productCode;
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

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
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

	public Boolean getLicensed() {
		return licensed;
	}

	public void setLicensed(Boolean licensed) {
		this.licensed = licensed;
	}

	public byte[] getXml() {
		return xml;
	}

	public void setXml(byte[] xml) {
		this.xml = xml;
	}

	public Date getIngestionDate() {
		return ingestionDate;
	}

	public void setIngestionDate(Date ingestionDate) {
		this.ingestionDate = ingestionDate;
	}

	public Date getIngestionUpdateDate() {
		return ingestionUpdateDate;
	}

	public void setIngestionUpdateDate(Date ingestionUpdateDate) {
		this.ingestionUpdateDate = ingestionUpdateDate;
	}

	public Date getPublishDate() {
		return publishDate;
	}

	public void setPublishDate(Date publishDate) {
		this.publishDate = publishDate;
	}

	public Set<Territory> getTerritories() {
		return territories;
	}

	public void setTerritories(Set<Territory> territories) {
		this.territories = territories;
	}

	public Set<AssetFile> getFiles() {
		return files;
	}

	public void setFiles(Set<AssetFile> files) {
		this.files = files;
	}
	
	public TrackStatus getStatus() {
		return status;
	}

	public void setStatus(TrackStatus status) {
		this.status = status;
	}

	public AudioResolution getResolution() {
		return resolution;
	}

	public void setResolution(AudioResolution resolution) {
		this.resolution = resolution;
	}

	public String getItunesUrl() {
		return itunesUrl;
	}

	public void setItunesUrl(String itunesUrl) {
		this.itunesUrl = itunesUrl;
	}

	public String getFileName(AssetFile.FileType type) {
		if(files != null)
		{
			for (AssetFile file : files) {
				if (file.getType() == type) {
					return file.getPath();
				}
			}
		}
		
		return "";
	}
	
	public Long getFileId(AssetFile.FileType type) {
		if(files != null)
		{
			for (AssetFile file : files) {
				if (file.getType() == type) {
					return file.getId();
				}
			}
		}
		
		return null;
	}
	
	@Override
	public String toString() {
		return "Track [ingestor=" + ingestor  + ", isrc=" + isrc  + ", title=" + title  + ", subTitle=" + subTitle 
				 + ", artist=" + artist  + ", productId=" + productId  + ", productCode=" + productCode  + ", genre=" + genre 
				 + ", copyright=" + copyright  + ", year=" + year + ", album=" + album  + ", info=" + info+ ", licenced=" + licensed
				 + ", xml=" + xml + ", ingestionDate=" + ingestionDate + ", ingestionUpdateDate=" + ingestionUpdateDate + ", publishDate=" + publishDate + ", territories=" + territories
				 + ", files=" + files + ", status=" + status + ", resolution=" + resolution + ", itunesUrl=" + itunesUrl
				 + super.toString()+ "]";
	}
}