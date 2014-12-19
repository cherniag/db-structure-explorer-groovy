package mobi.nowtechnologies.server.trackrepo.dto;

import mobi.nowtechnologies.server.shared.enums.FileType;
import mobi.nowtechnologies.server.trackrepo.SearchTrackCriteria;
import mobi.nowtechnologies.server.trackrepo.enums.ReportingType;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

import java.util.Date;
import java.util.List;

import static org.apache.commons.lang3.builder.ToStringStyle.SHORT_PREFIX_STYLE;
import static org.springframework.format.annotation.DateTimeFormat.ISO.DATE;

/**
 * @author Alexander Kolpakov (akolpakov)
 * @author Mayboroda Dmytro
 */
public class SearchTrackDto implements SearchTrackCriteria{
	public static final String SEARCH_TRACK_DTO = "searchTrackDto";
	
	private String artist;
	private String title;
	private String isrc;
	@DateTimeFormat(iso = DATE) private Date ingestFrom;
	@DateTimeFormat(iso = DATE) private Date ingestTo;
	@DateTimeFormat(iso = DATE) private Date releaseTo;
	@DateTimeFormat(iso = DATE) private Date releaseFrom;
	private String label;
	private String ingestor;
    private String album;
    private String genre;
    private boolean withTerritories;
    private boolean withFiles;
    private String territory;
    private List<Integer> trackIds;
    private String mediaType;
	private ReportingType reportingType;

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getArtist() {
		return artist;
	}

	public void setArtist(String artist) {
		this.artist = artist;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getIsrc() {
		return isrc;
	}

	public void setIsrc(String isrc) {
		this.isrc = isrc;
	}

	public Date getIngestFrom() {
		return ingestFrom;
	}

	public void setIngestFrom(Date ingestFrom) {
		this.ingestFrom = ingestFrom;
	}

	public Date getIngestTo() {
		return ingestTo;
	}

	public void setIngestTo(Date ingestTo) {
		this.ingestTo = ingestTo;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getIngestor() {
		return ingestor;
	}

	public void setIngestor(String ingestor) {
		this.ingestor = ingestor;
	}

	public Date getReleaseTo() {
		return releaseTo;
	}

	public void setReleaseTo(Date releaseTo) {
		this.releaseTo = releaseTo;
	}

	public Date getReleaseFrom() {
		return releaseFrom;
	}

	public void setReleaseFrom(Date releaseFrom) {
		this.releaseFrom = releaseFrom;
	}

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }
    
    public void setTrackIds(List<Integer> trackIds) {
		this.trackIds = trackIds;
	}

    public List<Integer> getTrackIds() {
		return trackIds;
	}

    public boolean isWithTerritories() {
        return withTerritories;
    }

    public void setWithTerritories(boolean withTerritories) {
        this.withTerritories = withTerritories;
    }

    public boolean isWithFiles() {
        return withFiles;
    }

    public void setWithFiles(boolean withFiles) {
        this.withFiles = withFiles;
    }

    public String getTerritory() {
		return territory;
	}

	public void setTerritory(String territory) {
		this.territory = territory;
	}

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

	@Override
	public ReportingType getReportingType() {
		return reportingType;
	}

	public void setReportingType(ReportingType reportingType) {
		this.reportingType = reportingType;
	}

    @Override
	public String toString() {
		return new ToStringBuilder(this, SHORT_PREFIX_STYLE)
				.append("isrc", isrc)
				.append("artist", artist)
				.append("album", album)
				.append("genre", genre)
				.append("title", title)
				.append("ingestor", ingestor)
				.append("label", label)
				.append("ingestFrom", ingestFrom)
				.append("ingestTo", ingestTo)
				.append("releaseFrom", releaseFrom)
				.append("releaseTo", releaseTo)
				.append("withTerritories", withTerritories)
				.append("withFiles", withFiles)
				.append("territory", territory)
				.append("trackIds", trackIds)
				.append("mediaType", mediaType)
				.append("reportingType", reportingType)
				.toString();
	}
}