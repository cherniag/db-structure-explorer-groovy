/**
 * 
 */
package mobi.nowtechnologies.server.trackrepo.dto;

import mobi.nowtechnologies.server.trackrepo.SearchTrackCriteria;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

import java.util.Date;

/**
 *
 * @author Alexander Kolpakov (akolpakov)
 * @author Mayboroda Dmytro
 *
 */
public class SearchTrackDto implements SearchTrackCriteria{
	public static final String SEARCH_TRACK_DTO = "searchTrackDto";
	
	private String artist;
	private String title;
	private String isrc;
	@DateTimeFormat(iso=ISO.DATE)
	private Date ingestFrom;
	@DateTimeFormat(iso=ISO.DATE)
	private Date ingestTo;
	@DateTimeFormat(iso=ISO.DATE)
	private Date releaseTo;
	@DateTimeFormat(iso=ISO.DATE)
	private Date releaseFrom;
	private String label;
	private String ingestor;
    private String album;
    private String genre;
	
	public SearchTrackDto() {
	}

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

    @Override
	public int hashCode() {
        return hash(artist, ingestFrom, ingestTo, ingestor, isrc, label, releaseFrom, releaseTo, title);
	}

    private <T> int hash(T... params){
        int result = 1;
        for (T t : params){
            result = hashWithParam(t, result);
        }
        return result;
    }

    private <T> int hashWithParam(T t, int result) {
        final int prime = 31;
        result = prime * result + ((t == null) ? 0 : t.hashCode());
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
		SearchTrackDto other = (SearchTrackDto) obj;
		if (artist == null) {
			if (other.artist != null)
				return false;
		} else if (!artist.equals(other.artist))
			return false;
		if (ingestFrom == null) {
			if (other.ingestFrom != null)
				return false;
		} else if (!ingestFrom.equals(other.ingestFrom))
			return false;
		if (ingestTo == null) {
			if (other.ingestTo != null)
				return false;
		} else if (!ingestTo.equals(other.ingestTo))
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
		if (label == null) {
			if (other.label != null)
				return false;
		} else if (!label.equals(other.label))
			return false;
		if (releaseFrom == null) {
			if (other.releaseFrom != null)
				return false;
		} else if (!releaseFrom.equals(other.releaseFrom))
			return false;
		if (releaseTo == null) {
			if (other.releaseTo != null)
				return false;
		} else if (!releaseTo.equals(other.releaseTo))
			return false;
		if (title == null) {
			if (other.title != null)
				return false;
		} else if (!title.equals(other.title))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "SearchTrackDto [album="+album+", artist=" + artist + ", title=" + title + ", isrc=" + isrc + ", ingestFrom=" + ingestFrom + ", ingestTo=" + ingestTo + ", releaseTo=" + releaseTo + ", releaseFrom="
				+ releaseFrom + ", label=" + label + ", ingestor=" + ingestor + "]";
	}
}