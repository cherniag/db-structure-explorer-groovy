package mobi.nowtechnologies.server.trackrepo.domain;

import mobi.nowtechnologies.server.trackrepo.domain.AssetFile.FileType;
import mobi.nowtechnologies.server.trackrepo.enums.AudioResolution;
import mobi.nowtechnologies.server.trackrepo.enums.ReportingType;
import mobi.nowtechnologies.server.trackrepo.enums.TrackStatus;
import static mobi.nowtechnologies.common.util.TrackIdGenerator.buildUniqueTrackId;
import static mobi.nowtechnologies.server.shared.ObjectUtils.isNotNull;
import static mobi.nowtechnologies.server.trackrepo.enums.AudioResolution.RATE_ORIGINAL;
import static mobi.nowtechnologies.server.trackrepo.enums.ReportingType.REPORTED_BY_TAGS;
import static mobi.nowtechnologies.server.trackrepo.enums.TrackStatus.NONE;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.Inheritance;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import static javax.persistence.CascadeType.ALL;
import static javax.persistence.EnumType.STRING;
import static javax.persistence.FetchType.EAGER;
import static javax.persistence.InheritanceType.JOINED;
import static javax.persistence.TemporalType.DATE;

import java.util.Date;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.lang3.builder.ToStringBuilder;

// @author Alexander Kolpakov (akolpakov)
@Entity
@Inheritance(strategy = JOINED)
public class Track extends AbstractEntity {

    @Column(name = "Ingestor", nullable = false)
    protected String ingestor;

    @Column(name = "ISRC", nullable = false)
    protected String isrc;

    @Column(name = "Title", nullable = false)
    protected String title;

    @Column(name = "Artist", nullable = false)
    protected String artist;

    @Temporal(DATE)
    @Column(name = "IngestionDate", nullable = false)
    protected Date ingestionDate;

    @Enumerated(STRING)
    @Column(nullable = false)
    protected TrackStatus status;

    @Enumerated(STRING)
    @Column(nullable = false)
    protected FileType mediaType;

    @Column(name = "SubTitle")
    protected String subTitle;

    @Column(name = "ProductId")
    protected String productId;

    @Column(name = "ProductCode")
    protected String productCode;

    @Column(name = "Genre")
    protected String genre;

    @Column(name = "Copyright", length = 1024)
    protected String copyright;

    @Column(name = "Year")
    protected String year;

    @Column(name = "Album")
    protected String album;

    @Column(name = "Info")
    protected String info;

    @Column(name = "Licensed")
    protected Boolean licensed;

    @Column(name = "explicit")
    protected Boolean explicit;

    @Temporal(DATE)
    @Column(name = "IngestionUpdateDate")
    protected Date ingestionUpdateDate;

    @Temporal(DATE)
    @Column(name = "PublishDate")
    protected Date publishDate;

    @Column(name = "Xml", columnDefinition = "LONGBLOB")
    @Lob
    protected byte[] xml;

    @OneToMany(cascade = ALL)
    @JoinColumn(name = "TrackId")
    @Column(name = "Territories")
    protected Set<Territory> territories;

    @OneToMany(cascade = ALL)
    @JoinColumn(name = "TrackId")
    @Column(name = "Files")
    protected Set<AssetFile> files;

    @Enumerated(STRING)
    protected AudioResolution resolution;

    protected String itunesUrl;

    protected String amazonUrl;

    @Column(name = "territoryCodes", length = 1024)
    protected String territoryCodes;

    @Column(name = "label")
    protected String label;

    @Temporal(DATE)
    @Column(name = "releaseDate")
    protected Date releaseDate;

    @OneToOne
    @JoinColumn(name = "mediaFile")
    protected AssetFile mediaFile;

    @OneToOne
    @JoinColumn(name = "coverFile")
    protected AssetFile coverFile;

    @Column(name = "coverFile", insertable = false, updatable = false)
    private Long coverFileId;

    @Column(name = "mediaFile", insertable = false, updatable = false)
    private Long mediaFileId;

    @Enumerated(STRING)
    @Column(nullable = false)
    private ReportingType reportingType = REPORTED_BY_TAGS;

    @OneToMany(mappedBy = "track", fetch = EAGER, cascade = ALL, orphanRemoval = true)
    private Set<NegativeTag> negativeTags;

    public Track() {
        status = NONE;
        resolution = RATE_ORIGINAL;
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

    public String getAmazonUrl() {
        return amazonUrl;
    }

    public void setAmazonUrl(String amazonUrl) {
        this.amazonUrl = amazonUrl;
    }

    public String getFileName(FileType type) {
        if (files != null) {
            for (AssetFile file : files) {
                if (file.getType() == type) {
                    return file.getPath();
                }
            }
        }
        return "";
    }

    public AssetFile getFile(FileType type) {
        if (files != null) {
            for (AssetFile file : files) {
                if (file.getType() == type) {
                    return file;
                }
            }
        }
        return null;
    }

    public Territory getValidTerritory(String country) {
        Set<Territory> territories = this.getTerritories();
        if (territories != null && territories.size() > 0) {
            Territory countryTerritory = null;
            Territory worldwide = null;

            Iterator<Territory> it = territories.iterator();
            while (it.hasNext()) {
                Territory territory = it.next();
                if (territory.isDeleted()) {
                    continue;
                }
                if (country.equalsIgnoreCase(territory.getCode())) {
                    countryTerritory = territory;
                }
                if (Territory.WWW_TERRITORY.equalsIgnoreCase(territory.getCode())) {
                    worldwide = territory;
                }
            }
            if (countryTerritory != null) {
                return countryTerritory;
            }
            if (worldwide != null) {
                return worldwide;
            }
        }
        return null;
    }

    public Boolean getExplicit() {
        return explicit;
    }

    public void setExplicit(Boolean explicit) {
        this.explicit = explicit;
    }

    public String getTerritoryCodes() {
        return territoryCodes;
    }

    public void setTerritoryCodes(String territoryCodes) {
        this.territoryCodes = territoryCodes;
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

    public void setReleaseDate(Date startDate) {
        this.releaseDate = startDate;
    }

    public AssetFile getMediaFile() {
        return mediaFile;
    }

    public void setMediaFile(AssetFile mediaFile) {
        this.mediaFile = mediaFile;

        this.mediaFileId = mediaFile != null ?
                           mediaFile.getId() :
                           null;
    }

    public AssetFile getCoverFile() {
        return coverFile;
    }

    public void setCoverFile(AssetFile coverFile) {
        this.coverFile = coverFile;

        this.coverFileId = coverFile != null ?
                           coverFile.getId() :
                           null;
    }

    public String getUniqueTrackId() {
        return buildUniqueTrackId(isrc, id);
    }

    public Long getCoverFileId() {
        return coverFileId;
    }

    public Long getMediaFileId() {
        return mediaFileId;
    }

    public FileType getMediaType() {
        return mediaType;
    }

    public void setMediaType(FileType mediaType) {
        this.mediaType = mediaType;
    }

    public ReportingType getReportingType() {
        return reportingType;
    }

    public void setReportingType(ReportingType reportingType) {
        this.reportingType = reportingType;
    }

    public Set<NegativeTag> getNegativeTags() {
        return negativeTags;
    }

    public void setNegativeTags(Set<NegativeTag> negativeTags) {
        this.negativeTags = negativeTags;
    }

    public Track withIngestor(String ingestor) {
        this.ingestor = ingestor;
        return this;
    }

    public Track withIsrc(String isrc) {
        this.isrc = isrc;
        return this;
    }

    public Track withTitle(String title) {
        this.title = title;
        return this;
    }

    public Track withArtist(String artist) {
        this.artist = artist;
        return this;
    }

    public Track withIngestionDate(Date ingestionDate) {
        this.ingestionDate = ingestionDate;
        return this;
    }

    public Track withMediaType(FileType mediaType) {
        this.mediaType = mediaType;
        return this;
    }

    public Track withNegativeTags(Set<NegativeTag> negativeTags) {
        this.negativeTags = negativeTags;
        if (isNotNull(negativeTags)) {
            for (NegativeTag negativeTag : negativeTags) {
                negativeTag.setTrack(this);
            }
        }
        return this;
    }

    public Track assignNegativeTags(Set<String> negativeTagSet) {
        negativeTags.clear();

        for (String negativeTagString : negativeTagSet) {
            NegativeTag negativeTag = new NegativeTag();
            negativeTag.setTag(negativeTagString);
            negativeTag.setTrack(this);

            negativeTags.add(negativeTag);
        }
        return this;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).appendSuper(super.toString()).append("ingestor", ingestor).append("isrc", isrc).append("title", title).append("artist", artist)
                                        .append("ingestionDate", ingestionDate).append("status", status).append("mediaType", mediaType).append("subTitle", subTitle).append("productId", productId)
                                        .append("productCode", productCode).append("genre", genre).append("copyright", copyright).append("year", year).append("album", album).append("info", info)
                                        .append("licensed", licensed).append("explicit", explicit).append("ingestionUpdateDate", ingestionUpdateDate).append("publishDate", publishDate)
                                        .append("resolution", resolution).append("itunesUrl", itunesUrl).append("amazonUrl", amazonUrl).append("territoryCodes", territoryCodes).append("label", label)
                                        .append("releaseDate", releaseDate).append("coverFileId", coverFileId).append("mediaFileId", mediaFileId).append("reportingType", reportingType).toString();
    }
}