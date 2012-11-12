package mobi.nowtechnologies.server.track_repo.service;

import java.rmi.ServerException;

import mobi.nowtechnologies.server.shared.dto.PageListDto;
import mobi.nowtechnologies.server.shared.dto.TrackDto;
import mobi.nowtechnologies.server.shared.dto.admin.SearchTrackDto;

import org.springframework.data.domain.Pageable;


/**
 * 
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
	TrackDto encode(Long trackId, Boolean isHighRate, Boolean licensed);
	
	/**
	 * This method pull full data about track including urls and hashes of encoded resource files.
	 * 
	 * @param   trackId   track ID of track which needs to pull.
     *
     * @return  full data about track including urls and hashes of encoded resource files.
	 */
	TrackDto pull(Long trackId);
	
	/**
	 * This method finds matched tracks by given query and returns a list of tracks.
	 * 
	 * @param   query  pattern of some property value in track.
	 * 
	 * @return  short data about track including only basic properties.
	 */
	PageListDto<TrackDto> find(String query, Pageable page);
	
	/**
	 * This method finds matched tracks by given track fields and returns a list of tracks.
	 * 
	 * @param   artist  pattern artist value in track.
	 * @param   title  pattern title value in track.
	 * @param   isrc  pattern isrc value in track.
	 * @param   label  pattern label or destributor of some track territory.
	 * @param   ingestor  pattern ingestor value in track.
	 * @param   ingestFrom date from track was ingested.
	 * @param   ingestTo  date to track was ingested.
	 * 
	 * @return  short data about track including only basic properties.
	 */
	PageListDto<TrackDto> find(SearchTrackDto searchTrackDto, Pageable page);
}
