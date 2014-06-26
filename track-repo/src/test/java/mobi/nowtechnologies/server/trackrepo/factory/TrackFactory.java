/**
 * 
 */
package mobi.nowtechnologies.server.trackrepo.factory;

import mobi.nowtechnologies.server.trackrepo.domain.AssetFile;
import mobi.nowtechnologies.server.trackrepo.domain.Territory;
import mobi.nowtechnologies.server.trackrepo.domain.Track;
import mobi.nowtechnologies.server.trackrepo.enums.TrackStatus;

import java.util.Collections;
import java.util.Date;

/**
 * @author Alexander Kolpakov (akolpakov)
 *
 */
public class TrackFactory {
	
	public static Track anyTrack() {
		Track track = new Track();
		
		track.setId(System.currentTimeMillis());
		track.setTitle("Deth");
		track.setArtist("Slot");
		track.setIngestor("EMI");
		track.setIsrc("ISRC" + System.nanoTime());
        track.setInfo("Cool song");
		track.setIngestionDate(new Date());
		track.setStatus(TrackStatus.NONE);
        track.setMediaType(AssetFile.FileType.PREVIEW);
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

}
