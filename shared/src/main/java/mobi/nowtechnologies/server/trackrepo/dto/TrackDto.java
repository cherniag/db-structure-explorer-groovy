package mobi.nowtechnologies.server.trackrepo.dto;

import mobi.nowtechnologies.server.trackrepo.Resolution;
import mobi.nowtechnologies.server.trackrepo.enums.AudioResolution;
import mobi.nowtechnologies.server.trackrepo.enums.FileType;
import mobi.nowtechnologies.server.trackrepo.enums.ReportingType;
import mobi.nowtechnologies.server.trackrepo.enums.TrackStatus;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static org.apache.commons.lang3.builder.ToStringStyle.SHORT_PREFIX_STYLE;

import org.springframework.format.annotation.DateTimeFormat;

/**
 * @author Titov Mykhaylo (titov)
 * @author Alexander Kolpakov (akolpakov)
 */
public class TrackDto {

    public static final String TRACK_DTO_LIST = "TRACK_DTO_LIST";
    public static final String TRACK_DTO = "TRACK_DTO";
    protected static final String URL_DATE_FORMAT = "yyyy-MM-dd";
    private static final Logger LOGGER = LoggerFactory.getLogger(TrackDto.class);
    private Long id;
    private String ingestor;
    private String isrc;
    private String title;
    private String artist;
    private Date ingestionDate;
    private TrackStatus status;
    private String coverFileName;
    private String mediaFileName;
    private FileType mediaType;

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
    private Boolean explicit;
    @DateTimeFormat(pattern = URL_DATE_FORMAT)
    private Date ingestionUpdateDate;
    @DateTimeFormat(pattern = URL_DATE_FORMAT)
    private Date publishDate;
    @DateTimeFormat(pattern = URL_DATE_FORMAT)
    private Date releaseDate;
    private String publishTitle;
    private String publishArtist;
    private String itunesUrl;
    private String amazonUrl;
    private String uniqueTrackId;

    private boolean areArtistUrls;
    private AudioResolution resolution;
    private String territoryCodes;
    private List<ResourceFileDto> files;
    private List<TerritoryDto> territories;
    private ReportingType reportingType;
    private Set<String> negativeTags;

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
        this.explicit = track.explicit;
        this.territories = track.territories;
        this.uniqueTrackId = track.uniqueTrackId;
        this.reportingType = track.reportingType;
        this.negativeTags = track.negativeTags;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getExplicit() {
        return explicit;
    }

    public void setExplicit(Boolean explicit) {
        this.explicit = explicit;
    }

    public String getIngestor() {
        return ingestor;
    }

    public void setIngestor(String ingestor) {
        this.ingestor = ingestor;
    }

    public List<TerritoryDto> getTerritories() {
        return territories;
    }

    public void setTerritories(List<TerritoryDto> territories) {
        this.territories = territories;
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

    public void setPublishDate(Date publishDate) {
        this.publishDate = publishDate;
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

    public List<ResourceFileDto> getFiles() {
        return files;
    }

    public void setFiles(List<ResourceFileDto> files) {
        this.files = files;
    }

    public ResourceFileDto getFile(FileType type, Resolution resolution) {
        if (files != null) {
            for (ResourceFileDto file : files) {
                if (file.getType().equals(type.name()) && file.getResolution().equals(resolution.name())) {
                    return file;
                }
            }
        }
        return null;
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

    public String getTerritoryCodes() {
        return territoryCodes;
    }

    public void setTerritoryCodes(String territoryCodes) {
        this.territoryCodes = territoryCodes;
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

    public boolean getAreArtistUrls() {
        return areArtistUrls;
    }


    public void setAreArtistUrls(boolean areArtistUrls) {
        this.areArtistUrls = areArtistUrls;
    }

    public String getMediaFileName() {
        return mediaFileName;
    }

    public void setMediaFileName(String mediaFileName) {
        this.mediaFileName = mediaFileName;
    }

    public FileType getMediaType() {
        return mediaType;
    }

    public void setMediaType(FileType mediaType) {
        this.mediaType = mediaType;
    }

    public String getUniqueTrackId() {
        return uniqueTrackId;
    }

    public void setUniqueTrackId(String uniqueTrackId) {
        this.uniqueTrackId = uniqueTrackId;
    }

    public ReportingType getReportingType() {
        return reportingType;
    }

    public void setReportingType(ReportingType reportingType) {
        this.reportingType = reportingType;
    }

    public Set<String> getNegativeTags() {
        return negativeTags;
    }

    public void setNegativeTags(Set<String> negativeTags) {
        this.negativeTags = negativeTags;
    }

    public String getFormattedDuration() {

        if (files == null) {
            return "";
        }

        for (ResourceFileDto file : files) {
            if (file.getDuration() != null && file.getDuration() > 0) {
                return "" + (file.getDuration() / 60000) + ":" + String.format("%02d", (file.getDuration() / 1000) % 60);
            }
        }

        return "";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((album == null) ?
                                   0 :
                                   album.hashCode());
        result = prime * result + ((artist == null) ?
                                   0 :
                                   artist.hashCode());
        result = prime * result + ((copyright == null) ?
                                   0 :
                                   copyright.hashCode());
        result = prime * result + ((coverFileName == null) ?
                                   0 :
                                   coverFileName.hashCode());
        result = prime * result + ((files == null) ?
                                   0 :
                                   files.hashCode());
        result = prime * result + ((genre == null) ?
                                   0 :
                                   genre.hashCode());
        result = prime * result + ((id == null) ?
                                   0 :
                                   id.hashCode());
        result = prime * result + ((info == null) ?
                                   0 :
                                   info.hashCode());
        result = prime * result + ((ingestionDate == null) ?
                                   0 :
                                   ingestionDate.hashCode());
        result = prime * result + ((ingestionUpdateDate == null) ?
                                   0 :
                                   ingestionUpdateDate.hashCode());
        result = prime * result + ((ingestor == null) ?
                                   0 :
                                   ingestor.hashCode());
        result = prime * result + ((isrc == null) ?
                                   0 :
                                   isrc.hashCode());
        result = prime * result + ((itunesUrl == null) ?
                                   0 :
                                   itunesUrl.hashCode());
        result = prime * result + ((amazonUrl == null) ?
                                   0 :
                                   amazonUrl.hashCode());
        result = prime * result + (areArtistUrls ?
                                   0 :
                                   1);
        result = prime * result + ((licensed == null) ?
                                   0 :
                                   licensed.hashCode());
        result = prime * result + ((productCode == null) ?
                                   0 :
                                   productCode.hashCode());
        result = prime * result + ((productId == null) ?
                                   0 :
                                   productId.hashCode());
        result = prime * result + ((publishArtist == null) ?
                                   0 :
                                   publishArtist.hashCode());
        result = prime * result + ((publishDate == null) ?
                                   0 :
                                   publishDate.hashCode());
        result = prime * result + ((publishTitle == null) ?
                                   0 :
                                   publishTitle.hashCode());
        result = prime * result + ((resolution == null) ?
                                   0 :
                                   resolution.hashCode());
        result = prime * result + ((status == null) ?
                                   0 :
                                   status.hashCode());
        result = prime * result + ((subTitle == null) ?
                                   0 :
                                   subTitle.hashCode());
        result = prime * result + ((territoryCodes == null) ?
                                   0 :
                                   territoryCodes.hashCode());
        result = prime * result + ((title == null) ?
                                   0 :
                                   title.hashCode());
        result = prime * result + ((year == null) ?
                                   0 :
                                   year.hashCode());
        result = prime * result + ((label == null) ?
                                   0 :
                                   label.hashCode());
        result = prime * result + ((releaseDate == null) ?
                                   0 :
                                   releaseDate.hashCode());
        result = prime * result + ((reportingType == null) ?
                                   0 :
                                   reportingType.hashCode());
        result = prime * result + ((negativeTags == null) ?
                                   0 :
                                   negativeTags.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        TrackDto other = (TrackDto) obj;
        if (album == null) {
            if (other.album != null) {
                return false;
            }
        } else if (!album.equals(other.album)) {
            return false;
        }
        if (artist == null) {
            if (other.artist != null) {
                return false;
            }
        } else if (!artist.equals(other.artist)) {
            return false;
        }
        if (copyright == null) {
            if (other.copyright != null) {
                return false;
            }
        } else if (!copyright.equals(other.copyright)) {
            return false;
        }
        if (coverFileName == null) {
            if (other.coverFileName != null) {
                return false;
            }
        } else if (!coverFileName.equals(other.coverFileName)) {
            return false;
        }
        if (files == null) {
            if (other.files != null) {
                return false;
            }
        } else if (!files.equals(other.files)) {
            return false;
        }
        if (genre == null) {
            if (other.genre != null) {
                return false;
            }
        } else if (!genre.equals(other.genre)) {
            return false;
        }
        if (id == null) {
            if (other.id != null) {
                return false;
            }
        } else if (!id.equals(other.id)) {
            return false;
        }
        if (info == null) {
            if (other.info != null) {
                return false;
            }
        } else if (!info.equals(other.info)) {
            return false;
        }
        if (ingestionDate == null) {
            if (other.ingestionDate != null) {
                return false;
            }
        } else if (!ingestionDate.equals(other.ingestionDate)) {
            return false;
        }
        if (ingestionUpdateDate == null) {
            if (other.ingestionUpdateDate != null) {
                return false;
            }
        } else if (!ingestionUpdateDate.equals(other.ingestionUpdateDate)) {
            return false;
        }
        if (ingestor == null) {
            if (other.ingestor != null) {
                return false;
            }
        } else if (!ingestor.equals(other.ingestor)) {
            return false;
        }
        if (isrc == null) {
            if (other.isrc != null) {
                return false;
            }
        } else if (!isrc.equals(other.isrc)) {
            return false;
        }
        if (itunesUrl == null) {
            if (other.itunesUrl != null) {
                return false;
            }
        } else if (!itunesUrl.equals(other.itunesUrl)) {
            return false;
        }
        if (amazonUrl == null) {
            if (other.amazonUrl != null) {
                return false;
            }
        } else if (!amazonUrl.equals(other.amazonUrl)) {
            return false;
        }
        if (areArtistUrls != other.areArtistUrls) {
            return false;
        }
        if (licensed == null) {
            if (other.licensed != null) {
                return false;
            }
        } else if (!licensed.equals(other.licensed)) {
            return false;
        }
        if (productCode == null) {
            if (other.productCode != null) {
                return false;
            }
        } else if (!productCode.equals(other.productCode)) {
            return false;
        }
        if (productId == null) {
            if (other.productId != null) {
                return false;
            }
        } else if (!productId.equals(other.productId)) {
            return false;
        }
        if (publishArtist == null) {
            if (other.publishArtist != null) {
                return false;
            }
        } else if (!publishArtist.equals(other.publishArtist)) {
            return false;
        }
        if (publishDate == null) {
            if (other.publishDate != null) {
                return false;
            }
        } else if (!publishDate.equals(other.publishDate)) {
            return false;
        }
        if (publishTitle == null) {
            if (other.publishTitle != null) {
                return false;
            }
        } else if (!publishTitle.equals(other.publishTitle)) {
            return false;
        }
        if (resolution != other.resolution) {
            return false;
        }
        if (status != other.status) {
            return false;
        }
        if (subTitle == null) {
            if (other.subTitle != null) {
                return false;
            }
        } else if (!subTitle.equals(other.subTitle)) {
            return false;
        }
        if (territoryCodes == null) {
            if (other.territoryCodes != null) {
                return false;
            }
        } else if (!territoryCodes.equals(other.territoryCodes)) {
            return false;
        }
        if (title == null) {
            if (other.title != null) {
                return false;
            }
        } else if (!title.equals(other.title)) {
            return false;
        }
        if (year == null) {
            if (other.year != null) {
                return false;
            }
        } else if (!year.equals(other.year)) {
            return false;
        }
        if (reportingType == null) {
            if (other.reportingType != null) {
                return false;
            }
        } else if (!reportingType.equals(other.reportingType)) {
            return false;
        }
        if (negativeTags == null) {
            if (other.negativeTags != null) {
                return false;
            }
        } else if (!negativeTags.equals(other.negativeTags)) {
            return false;
        }
        return true;
    }

    public JSONObject toJson() {
        JSONObject result = new JSONObject();
        try {
            result.put("id", id);
            result.put("ingestor", ingestor);
            result.put("isrc", isrc);
            result.put("title", title);
            result.put("artist", artist);
            result.put("ingestionDate", ingestionDate);
            result.put("status", status);
            result.put("coverFileName", coverFileName);
            result.put("mediaFileName", mediaFileName);
            result.put("mediaType", mediaType);
            result.put("label", label);
            result.put("subTitle", subTitle);
            result.put("productId", productId);
            result.put("productCode", productCode);
            result.put("genre", genre);
            result.put("copyright", copyright);
            result.put("year", year);
            result.put("album", album);
            result.put("info", info);
            result.put("licensed", licensed);
            result.put("explicit", explicit);
            result.put("ingestionUpdateDate", ingestionUpdateDate);
            result.put("publishDate", publishDate);
            result.put("releaseDate", releaseDate);
            result.put("publishTitle", publishTitle);
            result.put("publishArtist", publishArtist);
            result.put("itunesUrl", itunesUrl);
            result.put("amazonUrl", amazonUrl);
            result.put("areArtistUrls", areArtistUrls);
            result.put("resolution", resolution);
            result.put("territoryCodes", territoryCodes);
            result.put("files", files);
            result.put("territories", territories);
        } catch (JSONException e) {
            LOGGER.error("Couldn't convert to json", e);
        }
        return result;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("id", id).append("ingestor", ingestor).append("isrc", isrc).append("title", title)
                                                            .append("artist", artist).append("ingestionDate", ingestionDate).append("status", status).append("coverFileName", coverFileName)
                                                            .append("mediaFileName", mediaFileName).append("mediaType", mediaType).append("label", label).append("subTitle", subTitle)
                                                            .append("productId", productId).append("productCode", productCode).append("genre", genre).append("copyright", copyright)
                                                            .append("year", year).append("album", album).append("info", info).append("licensed", licensed).append("explicit", explicit)
                                                            .append("ingestionUpdateDate", ingestionUpdateDate).append("publishDate", publishDate).append("releaseDate", releaseDate)
                                                            .append("publishTitle", publishTitle).append("publishArtist", publishArtist).append("itunesUrl", itunesUrl).append("amazonUrl", amazonUrl)
                                                            .append("uniqueTrackId", uniqueTrackId).append("areArtistUrls", areArtistUrls).append("resolution", resolution)
                                                            .append("territoryCodes", territoryCodes).append("files", files).append("territories", territories).append("reportingType", reportingType)
                                                            .append("negativeTags", negativeTags).toString();
    }

}