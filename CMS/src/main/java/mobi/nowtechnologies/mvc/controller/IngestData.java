package mobi.nowtechnologies.mvc.controller;

import java.util.List;
import java.util.Map;

import mobi.nowtechnologies.ingestors.DropTrack;

public class IngestData {
	
	public class Track {
		public String ISRC;
		public String title;
		public String artist;
		public boolean exists;
		public boolean ingest = true;
		public DropTrack.Type type;
		public String productCode;

		
		public String getProductCode() {
			return productCode;
		}
		public void setProductCode(String productId) {
			this.productCode = productId;
		}
		public DropTrack.Type getType() {
			return type;
		}
		public void setType(DropTrack.Type type) {
			this.type = type;
		}
		public String getISRC() {
			return ISRC;
		}
		public void setISRC(String iSRC) {
			ISRC = iSRC;
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
		public boolean isExists() {
			return exists;
		}
		public void setExists(boolean exists) {
			this.exists = exists;
		}
		public boolean isIngest() {
			return ingest;
		}
		public void setIngest(boolean ingest) {
			this.ingest = ingest;
		}
		
	}
	Map<String, DropTrack> tracks;
	List<Track> data;
	Object drop;
	public Map<String, DropTrack> getTracks() {
		return tracks;
	}
	public void setTracks(Map<String, DropTrack> tracks) {
		this.tracks = tracks;
	}
	public List<Track> getData() {
		return data;
	}
	public void setData(List<Track> data) {
		this.data = data;
	}
	public Object getDrop() {
		return drop;
	}
	public void setDrop(Object drop) {
		this.drop = drop;
	}

}
