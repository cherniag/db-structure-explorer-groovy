/**
 * 
 */
package mobi.nowtechnologies.server.trackrepo.factory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import mobi.nowtechnologies.server.trackrepo.domain.Track;
import mobi.nowtechnologies.server.trackrepo.enums.TrackStatus;

/**
 * @author Alexander Kolpakov (akolpakov)
 *
 */
public class TrackFactory {
	
	public Track anyTrack() {
		Track track = new Track();
		
		track.setTitle("Deth");
		track.setArtist("Slot");
		track.setIngestor("EMI");
		track.setIsrc("APPCAST");
		track.setIngestionDate(new Date());
		track.setStatus(TrackStatus.NONE);
		
		return track;
	}
	
	public List<Track> anyTracks(int amount) {
		List<Track> items = new ArrayList<Track>();
		for (int i=0; i<amount; i++)
			items.add(anyTrack());
		return items;
	}
	
	public List<Track> cloneTracks(Track item, int amount) {
		List<Track> items = new ArrayList<Track>();
		for (int i=0; i<amount; i++)
			items.add(item);
		return items;
	}
	
	public static List<Track> getEmptyTracks() {
		return new ArrayList<Track>();
	}
}
