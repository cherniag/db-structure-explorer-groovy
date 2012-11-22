package mobi.nowtechnologies.server.trackrepo;

import java.util.Date;

public interface SearchTrackCriteria {
	String getArtist();
	
	String getTitle();
	
	String getIsrc();
	
	Date getIngestFrom();
	
	Date getIngestTo();
	
	String getLabel();
	
	String getIngestor();
	
	Date getReleaseTo();

	Date getReleaseFrom();

    String getAlbum();

    String getGenre();
}
