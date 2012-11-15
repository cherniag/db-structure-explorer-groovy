/**
 * 
 */
package mobi.nowtechnologies.server.service;

import mobi.nowtechnologies.server.shared.dto.PageListDto;
import mobi.nowtechnologies.server.trackrepo.dto.SearchTrackDto;
import mobi.nowtechnologies.server.trackrepo.dto.TrackDto;

import org.springframework.data.domain.Pageable;

/**
 * @author Mayboroda Dmytro
 *
 */
public interface TrackRepoService {
	
	/**
	 * Returns a list of tracks from track repository by search criteria
	 * @param criteria
	 * @param pageanation parameters
	 * @return matched track list
	 */
	public PageListDto<TrackDto> find(String criteria, Pageable page);
	
	/**
	 * Returns a list of tracks from track repository by track proprties
	 * @param search track properties
	 * @param pageanation parameters
	 * @return matched track list
	 */
	public PageListDto<TrackDto> find(SearchTrackDto criteria, Pageable page);
	
	/**
	 * Retrieves a track info from track repository by id and store it to tb_media and tb_files
	 * @param id id of the track in track repository
	 * @return track that was published in database
	 */
	public TrackDto pull(TrackDto track);
	
	/**
	 * Starts encoding process on track repository for track with specified id
	 * @param id of the  track in track repository
	 * @return track representing a track that is in encoding process right now
	 */
	public TrackDto encode(TrackDto track);
}