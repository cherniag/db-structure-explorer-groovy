package mobi.nowtechnologies.server.track_repo.repository;

import java.util.List;

import mobi.nowtechnologies.server.shared.dto.admin.SearchTrackDto;
import mobi.nowtechnologies.server.track_repo.domain.Track;

import org.springframework.data.domain.Pageable;

/**
 * 
 * @author Alexander Kolpakov (akolpakov)
 * 
 */
public interface TrackRepositoryCustom {

	List<Track> find(SearchTrackDto searchTrackDto, Pageable pageable);
	
	long count(SearchTrackDto searchTrackDto);
}
