/**
 * 
 */
package mobi.nowtechnologies.server.client.trackrepo;

import mobi.nowtechnologies.server.shared.dto.PageListDto;
import mobi.nowtechnologies.server.shared.dto.TrackDto;
import mobi.nowtechnologies.server.shared.dto.admin.SearchTrackDto;

import org.springframework.data.domain.Pageable;

/**
 * 
 * @author Alexander Kolpakov (akolpakov)
 * @author Mayboroda Dmytro
 *
 */
public interface TrackRepositoryClient {
	
	/**
	 * Checks if client may connect to track repository with provided credentials
	 * @return true if client is logged in false otherwise
	 */
	public boolean isLoggedIn();
	
	/**
	 * Searches for tracks in tack repository by criteria
	 * @param criteria
	 * @param page pagination parameters
	 * @return
	 */
	public PageListDto<TrackDto> search(String criteria, Pageable page);
	
	/**
	 * 
	 * @param id
	 * @return
	 * @throws Exception 
	 */
	public TrackDto pullTrack(Long id) throws Exception;
	
	/**
	 * Encode track
	 * @param id track id
	 * @param licensed 
	 * @param isHighRate 
	 * @return TrackDto object if http status code is OK or CREATED and null if NO_CONTENT  
	 * @throws Exception if some exception occurred or wrong response code
	 */
	public TrackDto encodeTrack(Long id, Boolean isHighRate, Boolean licensed) throws Exception;

	/**
	 * Searches for tracks in tack repository by criteria
	 * @param criteria
	 * @param page pagination parameters
	 * @return
	 */
	public PageListDto<TrackDto> search(SearchTrackDto criteria, Pageable page);
	
}