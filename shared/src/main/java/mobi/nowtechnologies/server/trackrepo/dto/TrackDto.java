package mobi.nowtechnologies.server.trackrepo.dto;

import mobi.nowtechnologies.server.trackrepo.Resolution;
import mobi.nowtechnologies.server.trackrepo.enums.AudioResolution;
import mobi.nowtechnologies.server.trackrepo.enums.FileType;
import mobi.nowtechnologies.server.trackrepo.enums.TrackStatus;

import java.util.Date;
import java.util.List;

import org.hibernate.validator.constraints.NotEmpty;

/**
 * @author Titov Mykhaylo (titov)
 * @author Alexander Kolpakov (akolpakov)
 * 
 */
public class TrackDto {
	public static final String TRACK_DTO_LIST = "TRACK_DTO_LIST";
	public static final String TRACK_DTO = "TRACK_DTO";

	// ------------------basic properties-----------------------//
	private Long id;
	private String ingestor;
	private String isrc;
	private String title;
	private String artist;
	private Date ingestionDate;
	private TrackStatus status;
	private String coverFileName;

	// ------------------additional properties------------------//
	private String label;
	private String subTitle;
	private String productId;
	private String productCode;
	private String genre;
	private String copyright;
	private String year;
	private String album;
	private String info;
	private Boolean licensed;
	private Date ingestionUpdateDate;
	private Date publishDate;
	private Date releaseDate;
	private String publishTitle;
	private String publishArtist;
	private String itunesUrl;
	private String amazonUrl;
	private AudioResolution resolution;
	private String territories;
	private List<ResourceFileDto> files;

	public TrackDto() {

	}



	public TrackDto(TrackDto track) {
		this.id = track.id;
		this.ingestor = track.ingestor;
		this.isrc = track.isrc;
		this.title = track.title;
		this.artist = track.artist;
		this.ingestionDate = track.ingestionDate;
		this.status = track.status;
		this.coverFileName = track.coverFileName;
		this.subTitle = track.subTitle;
		this.productId = track.productId;
		this.productCode = track.productCode;
		this.genre = track.genre;
		this.copyright = track.copyright;
		this.year = track.year;
		this.album = track.album;
		this.info = track.info;
		this.licensed = track.licensed;
		this.ingestionUpdateDate = track.ingestionUpdateDate;
		this.publishDate = track.publishDate;
		this.itunesUrl = track.itunesUrl;
		this.files = track.files;
		this.resolution = track.resolution;
		this.publishTitle = track.publishTitle;
		this.publishArtist = track.publishArtist;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getIngestor() {
		return ingestor;
	}

	public void setIngestor(String ingestor) {
		this.ingestor = ingestor;
	}

	public String getCoverFileName() {
		return coverFileName;
	}

	public void setCoverFileName(String coverFileName) {
		this.coverFileName = coverFileName;
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

	public String getArtist() {
		return artist;
	}

	public void setArtist(String artist) {
		this.artist = artist;
	}

	public Date getIngestionDate() {
		return ingestionDate;
	}

	public void setIngestionDate(Date ingestionDate) {
		this.ingestionDate = ingestionDate;
	}

	public TrackStatus getStatus() {
		return status;
	}

	public void setStatus(TrackStatus status) {
		this.status = status;
	}

	public String getSubTitle() {
		return subTitle;
	}

	public void setSubTitle(String subTitle) {
		this.subTitle = subTitle;
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

	public Date getIngestionUpdateDate() {
		return ingestionUpdateDate;
	}

	public void setIngestionUpdateDate(Date ingestionUpdateDate) {
		this.ingestionUpdateDate = ingestionUpdateDate;
	}

	public Date getPublishDate() {
		return publishDate;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public Date getReleaseDate() {
		return releaseDate;
	}

	public void setReleaseDate(Date releaseDate) {
		this.releaseDate = releaseDate;
	}

	public void setPublishDate(Date publishDate) {
		this.publishDate = publishDate;
	}

	public List<ResourceFileDto> getFiles() {
		return files;
	}

	public ResourceFileDto getFile(FileType type, Resolution resolution)
	{
		if (files != null) {
			for (ResourceFileDto file : files) {
				if (file.getType().equals(type.name()) && file.getResolution().equals(resolution.name()))
					return file;
			}
		}

		return null;
	}

	public void setFiles(List<ResourceFileDto> files) {
		this.files = files;
	}

	public String getAmazonUrl() {
		return amazonUrl;
	}

	public void setAmazonUrl(String amazonUrl) {
		this.amazonUrl = amazonUrl;
	}

	public String getItunesUrl() {
		return itunesUrl;
	}

	public void setItunesUrl(String itunesUrl) {
		this.itunesUrl = itunesUrl;
	}

	public String getTerritories() {
		return territories;
	}

	public void setTerritories(String territories) {
		this.territories = territories;
	}

	public AudioResolution getResolution() {
		return resolution;
	}

	public void setResolution(AudioResolution resolution) {
		this.resolution = resolution;
	}

	public String getPublishTitle() {
		return publishTitle;
	}

	public void setPublishTitle(String publishTitle) {
		this.publishTitle = publishTitle;
	}

	public String getPublishArtist() {
		return publishArtist;
	}

	public void setPublishArtist(String publishArtist) {
		this.publishArtist = publishArtist;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((album == null) ? 0 : album.hashCode());
		result = prime * result + ((artist == null) ? 0 : artist.hashCode());
		result = prime * result + ((copyright == null) ? 0 : copyright.hashCode());
		result = prime * result + ((coverFileName == null) ? 0 : coverFileName.hashCode());
		result = prime * result + ((files == null) ? 0 : files.hashCode());
		result = prime * result + ((genre == null) ? 0 : genre.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((info == null) ? 0 : info.hashCode());
		result = prime * result + ((ingestionDate == null) ? 0 : ingestionDate.hashCode());
		result = prime * result + ((ingestionUpdateDate == null) ? 0 : ingestionUpdateDate.hashCode());
		result = prime * result + ((ingestor == null) ? 0 : ingestor.hashCode());
		result = prime * result + ((isrc == null) ? 0 : isrc.hashCode());
		result = prime * result + ((itunesUrl == null) ? 0 : itunesUrl.hashCode());
		result = prime * result + ((amazonUrl == null) ? 0 : amazonUrl.hashCode());
		result = prime * result + ((licensed == null) ? 0 : licensed.hashCode());
		result = prime * result + ((productCode == null) ? 0 : productCode.hashCode());
		result = prime * result + ((productId == null) ? 0 : productId.hashCode());
		result = prime * result + ((publishArtist == null) ? 0 : publishArtist.hashCode());
		result = prime * result + ((publishDate == null) ? 0 : publishDate.hashCode());
		result = prime * result + ((publishTitle == null) ? 0 : publishTitle.hashCode());
		result = prime * result + ((resolution == null) ? 0 : resolution.hashCode());
		result = prime * result + ((status == null) ? 0 : status.hashCode());
		result = prime * result + ((subTitle == null) ? 0 : subTitle.hashCode());
		result = prime * result + ((territories == null) ? 0 : territories.hashCode());
		result = prime * result + ((title == null) ? 0 : title.hashCode());
		result = prime * result + ((year == null) ? 0 : year.hashCode());
		result = prime * result + ((label == null) ? 0 :label.hashCode());
		result = prime * result + ((releaseDate == null) ? 0 : releaseDate.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TrackDto other = (TrackDto) obj;
		if (album == null) {
			if (other.album != null)
				return false;
		} else if (!album.equals(other.album))
			return false;
		if (artist == null) {
			if (other.artist != null)
				return false;
		} else if (!artist.equals(other.artist))
			return false;
		if (copyright == null) {
			if (other.copyright != null)
				return false;
		} else if (!copyright.equals(other.copyright))
			return false;
		if (coverFileName == null) {
			if (other.coverFileName != null)
				return false;
		} else if (!coverFileName.equals(other.coverFileName))
			return false;
		if (files == null) {
			if (other.files != null)
				return false;
		} else if (!files.equals(other.files))
			return false;
		if (genre == null) {
			if (other.genre != null)
				return false;
		} else if (!genre.equals(other.genre))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (info == null) {
			if (other.info != null)
				return false;
		} else if (!info.equals(other.info))
			return false;
		if (ingestionDate == null) {
			if (other.ingestionDate != null)
				return false;
		} else if (!ingestionDate.equals(other.ingestionDate))
			return false;
		if (ingestionUpdateDate == null) {
			if (other.ingestionUpdateDate != null)
				return false;
		} else if (!ingestionUpdateDate.equals(other.ingestionUpdateDate))
			return false;
		if (ingestor == null) {
			if (other.ingestor != null)
				return false;
		} else if (!ingestor.equals(other.ingestor))
			return false;
		if (isrc == null) {
			if (other.isrc != null)
				return false;
		} else if (!isrc.equals(other.isrc))
			return false;
		if (itunesUrl == null) {
			if (other.itunesUrl != null)
				return false;
		} else if (!itunesUrl.equals(other.itunesUrl))
			return false;
		if (amazonUrl == null) {
			if (other.amazonUrl != null)
				return false;
		} else if (!amazonUrl.equals(other.amazonUrl))
			return false;
		if (licensed == null) {
			if (other.licensed != null)
				return false;
		} else if (!licensed.equals(other.licensed))
			return false;
		if (productCode == null) {
			if (other.productCode != null)
				return false;
		} else if (!productCode.equals(other.productCode))
			return false;
		if (productId == null) {
			if (other.productId != null)
				return false;
		} else if (!productId.equals(other.productId))
			return false;
		if (publishArtist == null) {
			if (other.publishArtist != null)
				return false;
		} else if (!publishArtist.equals(other.publishArtist))
			return false;
		if (publishDate == null) {
			if (other.publishDate != null)
				return false;
		} else if (!publishDate.equals(other.publishDate))
			return false;
		if (publishTitle == null) {
			if (other.publishTitle != null)
				return false;
		} else if (!publishTitle.equals(other.publishTitle))
			return false;
		if (resolution != other.resolution)
			return false;
		if (status != other.status)
			return false;
		if (subTitle == null) {
			if (other.subTitle != null)
				return false;
		} else if (!subTitle.equals(other.subTitle))
			return false;
		if (territories == null) {
			if (other.territories != null)
				return false;
		} else if (!territories.equals(other.territories))
			return false;
		if (title == null) {
			if (other.title != null)
				return false;
		} else if (!title.equals(other.title))
			return false;
		if (year == null) {
			if (other.year != null)
				return false;
		} else if (!year.equals(other.year))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "TrackDto [id=" + id + ", ingestor=" + ingestor + ", isrc=" + isrc + ", title=" + title + ", artist=" + artist + ", ingestionDate=" + ingestionDate + ", status=" + status
				+ ", coverFileName=" + coverFileName + ", label=" + label + ", subTitle=" + subTitle + ", productId=" + productId + ", productCode=" + productCode + ", genre=" + genre
				+ ", copyright=" + copyright + ", year=" + year + ", album=" + album + ", info=" + info + ", licensed=" + licensed + ", ingestionUpdateDate=" + ingestionUpdateDate + ", publishDate="
				+ publishDate + ", releaseDate=" + releaseDate + ", publishTitle=" + publishTitle + ", publishArtist=" + publishArtist + ", itunesUrl=" + itunesUrl + ", amazonUrl=" + amazonUrl
				+ ", resolution=" + resolution + ", territories=" + territories + ", files=" + files + "]";
	}
}