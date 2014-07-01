package mobi.nowtechnologies.server.trackrepo.domain;

import mobi.nowtechnologies.server.trackrepo.enums.AudioResolution;
import mobi.nowtechnologies.server.trackrepo.enums.TrackStatus;

import javax.persistence.*;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

import static mobi.nowtechnologies.common.util.TrackUtils.buildUniqueTrackId;

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
    @Basic(optional=false)
    @Enumerated(EnumType.STRING)
    protected AssetFile.FileType mediaType;
	
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
    @Basic(optional=true)
	@Column(name="explicit")
	protected Boolean explicit;
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

	@OneToMany(cascade={CascadeType.ALL}, fetch=FetchType.LAZY)
    @JoinColumn(name="TrackId") 
	@Column(name="Territories")
	protected Set<Territory> territories; 

	@OneToMany(cascade={CascadeType.ALL}, fetch=FetchType.LAZY)
    @JoinColumn(name="TrackId")
	@Column(name="Files")
	protected Set<AssetFile> files;
	
	@Basic(optional=true)
	@Enumerated(EnumType.STRING)
	protected AudioResolution resolution; 
	protected String itunesUrl;
	protected String amazonUrl;

    @Basic(optional=true)
    @Column(name="territoryCodes", length=1024)
    protected String territoryCodes;

    @Basic(optional=true)
    @Column(name="label")
    protected String label;

    @Temporal(TemporalType.DATE)
    @Basic(optional=true)
    @Column(name="releaseDate")
    protected Date releaseDate;

    @OneToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="mediaFile")
    protected AssetFile mediaFile;

    @OneToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="coverFile")
    protected AssetFile coverFile;

    @Column(name = "coverFile", insertable = false, updatable = false)
    private Long coverFileId;

    @Column(name = "mediaFile", insertable = false, updatable = false)
    private Long mediaFileId;

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

	public String getAmazonUrl() {
		return amazonUrl;
	}

	public void setAmazonUrl(String amazonUrl) {
		this.amazonUrl = amazonUrl;
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

    public AssetFile getFile(AssetFile.FileType type) {
        if(files != null)
        {
            for (AssetFile file : files) {
                if (file.getType() == type) {
                    return file;
                }
            }
        }

        return null;
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
	
	public Territory getValidTerritory(String country) {
		Set<Territory> territories = this.getTerritories();
		if (territories != null && territories.size() > 0) {
			Territory countryTerritory = null;
			Territory worldwide = null;

			Iterator<Territory> it = territories.iterator();
			while (it.hasNext()) {
				Territory territory = it.next();
				if (territory.isDeleted())
					continue;
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

        this.mediaFileId = mediaFile != null ? mediaFile.getId() : null;
    }

    public AssetFile getCoverFile() {
        return coverFile;
    }

    public void setCoverFile(AssetFile coverFile) {
        this.coverFile = coverFile;

        this.coverFileId = coverFile != null ? coverFile.getId() : null;
    }

    public String getUniqueTrackId(){
        return buildUniqueTrackId(isrc, id);
    }

    public Long getCoverFileId() {
        return coverFileId;
    }

    public Long getMediaFileId() {
        return mediaFileId;
    }

    public AssetFile.FileType getMediaType() {
        return mediaType;
    }

    public void setMediaType(AssetFile.FileType mediaType) {
        this.mediaType = mediaType;
    }

    @Override
    public String toString() {
        return "Track{" +
                "ingestor='" + ingestor + '\'' +
                ", isrc='" + isrc + '\'' +
                ", title='" + title + '\'' +
                ", artist='" + artist + '\'' +
                ", ingestionDate=" + ingestionDate +
                ", status=" + status +
                ", subTitle='" + subTitle + '\'' +
                ", productId='" + productId + '\'' +
                ", productCode='" + productCode + '\'' +
                ", genre='" + genre + '\'' +
                ", copyright='" + copyright + '\'' +
                ", year='" + year + '\'' +
                ", album='" + album + '\'' +
                ", info='" + info + '\'' +
                ", licensed=" + licensed +
                ", explicit=" + explicit +
                ", ingestionUpdateDate=" + ingestionUpdateDate +
                ", publishDate=" + publishDate +
//                ", xml=" + Arrays.toString(xml) +
                ", resolution=" + resolution +
                ", itunesUrl='" + itunesUrl + '\'' +
                ", territoryCodes='" + territoryCodes + '\'' +
                ", label='" + label + '\'' +
                ", startDate=" + releaseDate +
                ", mediaType=" + mediaType +
                "} " + super.toString();
    }
}