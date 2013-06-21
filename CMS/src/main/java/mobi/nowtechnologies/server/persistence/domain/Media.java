package mobi.nowtechnologies.server.persistence.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import mobi.nowtechnologies.domain.AssetFile;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

/**
 * The persistent class for the tb_media database table.
 * 
 */
@Entity
@Table(name = "tb_media")
public class Media extends CNAbstractEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public static enum Fields{
		isrc, i;
	}


	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "artist")
	@Fetch(FetchMode.JOIN)
	private Artist artist;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "audioFile")
	@Fetch(FetchMode.JOIN)
	private MediaFile audioFile;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "genre")
	@Fetch(FetchMode.JOIN)
	private Genre genre;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "headerFile")
	@Fetch(FetchMode.JOIN)
	private MediaFile headerFile;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "imageFIleLarge")
	@Fetch(FetchMode.JOIN)
	private MediaFile imageFIleLarge;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "imageFileSmall")
	@Fetch(FetchMode.JOIN)
	private MediaFile imageFileSmall;

	@OneToMany(cascade={CascadeType.ALL})
    @JoinColumn(name="TrackId")
	protected Set<ResourceFile> Files; 

	
//	@OneToOne(fetch = FetchType.EAGER,targetEntity=Drm.class,mappedBy="media")
//	@Fetch(FetchMode.JOIN)
	@Transient
	private Drm drm;

	@Column(name="info",columnDefinition="text")
	@Lob()
	private String info;

	@Column(name="isrc",columnDefinition="char(15)")
	private String isrc;

	private byte label;

	@Column(name="title",columnDefinition="char(50)")
	private String title;
	
	@Column(precision=5, scale=2)
	private BigDecimal price;
	
	@Column(name="price_currency",columnDefinition="char(4)")
	private String price_currency;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "imgFileResolution")
	@Fetch(FetchMode.JOIN)
	private MediaFile imgFileResolution;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "purchasedFile")
	@Fetch(FetchMode.JOIN)
	private MediaFile purchasedFile;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "audioPreviewFile")
	@Fetch(FetchMode.JOIN)
	private MediaFile audioPreviewFile;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "headerPreviewFile")
	private MediaFile headerPreviewFile;
	
	private String iTunesUrl;
	
    private int publishDate;


	public Media() {
	}

	public Artist getArtist() {
		return this.artist;
	}

	public void setArtist(Artist artist) {
		this.artist = artist;
	}

	public MediaFile getAudioFile() {
		return this.audioFile;
	}

	public void setAudioFile(MediaFile audioFile) {
		this.audioFile = audioFile;
	}

	public Genre getGenre() {
		return this.genre;
	}

	public void setGenre(Genre genre) {
		this.genre = genre;
	}

	public MediaFile getHeaderFile() {
		return this.headerFile;
	}

	public void setHeaderFile(MediaFile headerFile) {
		this.headerFile = headerFile;
	}

	public MediaFile getImageFIleLarge() {
		return this.imageFIleLarge;
	}

	public void setImageFIleLarge(MediaFile imageFIleLarge) {
		this.imageFIleLarge = imageFIleLarge;
	}

	public MediaFile getImageFileSmall() {
		return this.imageFileSmall;
	}

	public void setImageFileSmall(MediaFile imageFileSmall) {
		this.imageFileSmall = imageFileSmall;
	}

	public String getInfo() {
		return this.info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public String getIsrc() {
		return this.isrc;
	}

	public void setIsrc(String isrc) {
		this.isrc = isrc;
	}

	public byte getLabel() {
		return this.label;
	}

	public void setLabel(byte label) {
		this.label = label;
	}

	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return
	 */
	public String getArtistName() {
		return artist.getName();
	}

	/**
	 * @return
	 */
	public String getGenreName() {
		return genre.getName();
	}

	/**
	 * @return
	 */
	public Integer getAudioSize() {
		return audioFile.getSize();
	}

	/**
	 * @return
	 */
	public Integer getImageLargeSize() {
		return imageFIleLarge.getSize();
	}

	/**
	 * @return
	 */
	public int getImageSmallSize() {
		return imageFileSmall.getSize();
	}

	public Drm getDrm() {
		return drm;
	}

	public void setDrm(Drm drm) {
		this.drm = drm;
	}

	public String getDrmTypeName() {
		if (null == drm)
			return null;
		return this.drm.getDrmTypeName();
	}

	public Byte getDrmValue() {
		if (null == drm)
			return null;
		return this.drm.getDrmValue();
	}

	/**
	 * @return
	 */
	public int getHeaderSize() {
		return headerFile.getSize();
	}
	
	public BigDecimal getPrice(){
		return price;
	}
	
	public void setPrice(BigDecimal aPrice){
		price=aPrice;
	}
	
	public String getPrice_currency() {
		return price_currency;
	}

	public void setPrice_currency(String aPriceCurrency) {
		price_currency = aPriceCurrency;
	}

	public MediaFile getImgFileResolution() {
		return imgFileResolution;
	}
	
	public void setImgFileResolution(MediaFile aImageFileResolution) {
		this.imgFileResolution = aImageFileResolution;
	}

	public void setPurchasedFile(MediaFile purchasedFile) {
		this.purchasedFile = purchasedFile;
	}
	
	public MediaFile getPurchasedFile() {
		return purchasedFile;
	}

	public MediaFile getAudioPreviewFile() {
		return audioPreviewFile;
	}

	public void setAudioPreviewFile(MediaFile audioPreviewFile) {
		this.audioPreviewFile = audioPreviewFile;
	}

	public String getiTunesUrl() {
		return iTunesUrl;
	}

	public void setiTunesUrl(String iTunesUrl) {
		this.iTunesUrl = iTunesUrl;
	}

	public MediaFile getHeaderPreviewFile() {
		return headerPreviewFile;
	}

	public void setHeaderPreviewFile(MediaFile headerPreviewFile) {
		this.headerPreviewFile = headerPreviewFile;
	}

	public Set<ResourceFile> getFiles() {
		return Files;
	}

	public void setFiles(Set<ResourceFile> files) {
		Files = files;
	}

	public int getPublishDate() {
		return publishDate;
	}

	public void setPublishDate(int publishDate) {
		this.publishDate = publishDate;
	}

	
}