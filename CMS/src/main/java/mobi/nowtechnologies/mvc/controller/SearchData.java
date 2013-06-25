package mobi.nowtechnologies.mvc.controller;

import java.util.Date;

public class SearchData {
	
	public String ISRC;
	public String artist;
	public String title;
	public Date ingestFrom;
	public Date ingestTo;
	public String label;
	public String ingestor;
	
	public SearchData() {
		ISRC="";
		artist="";
		title="";
		ingestFrom = null;
		ingestTo = null;
		label = null;
		ingestor = null;
	}

	
	public String getISRC() {
		return ISRC;
	}
	public void setISRC(String iSRC) {
		ISRC = iSRC;
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
	
	
	

}
