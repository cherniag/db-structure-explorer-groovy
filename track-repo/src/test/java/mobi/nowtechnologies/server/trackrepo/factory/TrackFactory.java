/**
 * 
 */
package mobi.nowtechnologies.server.trackrepo.factory;

import mobi.nowtechnologies.server.trackrepo.domain.Territory;
import mobi.nowtechnologies.server.trackrepo.domain.Track;
import mobi.nowtechnologies.server.trackrepo.enums.TrackStatus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * @author Alexander Kolpakov (akolpakov)
 *
 */
public class TrackFactory {
	
	public static Track anyTrack() {
		Track track = new Track();
		
		track.setId(new Long((int)(Math.random()*100)));
		track.setTitle("Deth");
		track.setArtist("Slot");
		track.setIngestor("EMI");
		track.setIsrc("APPCAST");
        track.setInfo("Cool song");
		track.setIngestionDate(new Date());
		track.setStatus(TrackStatus.NONE);

        Territory territory = new Territory();
        territory.setCode("Worldwide");
        territory.setLabel("MANUAL");
        territory.setPublisher("MANUAL");
        territory.setDistributor("MANUAL");
        territory.setReportingId(track.getIsrc());
        territory.setStartDate(new Date());
        track.setTerritories(Collections.singleton(territory));
		
		return track;
	}
	
	public static List<Track> anyTracks(int amount) {
		List<Track> items = new ArrayList<Track>();
		for (int i=0; i<amount; i++)
			items.add(anyTrack());
		return items;
	}
	
	public static List<Track> cloneTracks(Track item, int amount) {
		List<Track> items = new ArrayList<Track>();
		for (int i=0; i<amount; i++)
			items.add(item);
		return items;
	}
	
	public static List<Track> getEmptyTracks() {
		return new ArrayList<Track>();
	}
}
