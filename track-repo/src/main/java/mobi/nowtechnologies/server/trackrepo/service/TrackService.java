package mobi.nowtechnologies.server.trackrepo.service;

import mobi.nowtechnologies.server.trackrepo.SearchTrackCriteria;
import mobi.nowtechnologies.server.trackrepo.domain.Track;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.rmi.ServerException;


/**
 * @author Alexander Kolpakov (akolpakov)
 * This interface contains all methods to work with Tracks in Track Repository. 
 */
public interface TrackService {
	
	/**
	 * This method encodes given track resource files, packs them to zip filee.
	 * 
	 * @param  trackId    track ID of track which needs to encode.
     *
     * @param  isHighRate whether encoded track will be high rate.
     * 
     * @param  licensed whether encoded track will be licensed.
     *
     * @throws ServerException if process is not completed.
	 */
	Track encode(Long trackId, Boolean isHighRate, Boolean licensed);
	
	/**
	 * This method pull full data about track including urls and hashes of encoded resource files.
	 * 
	 * @param   trackId   track ID of track which needs to pull.
     *
     * @return  full data about track including urls and hashes of encoded resource files.
	 */
	Track pull(Long trackId);
	
	/**
	 * This method finds matched tracks by given query and returns a list of tracks.
	 * 
	 * @param   query  pattern of some property value in track.
	 * 
	 * @return  short data about track including only basic properties.
	 */
	Page<Track> find(String query, Pageable page);
	
	/**
	 * This method finds matched tracks by given track fields and returns a list of tracks.
	 *
	 * @return  short data about track including only basic properties.
	 */
	Page<Track> find(SearchTrackCriteria searchTrackCriteria, Pageable page);
}
