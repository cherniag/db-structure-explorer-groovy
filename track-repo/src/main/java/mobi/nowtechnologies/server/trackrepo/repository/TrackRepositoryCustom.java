package mobi.nowtechnologies.server.trackrepo.repository;

import mobi.nowtechnologies.server.trackrepo.SearchTrackCriteria;
import mobi.nowtechnologies.server.trackrepo.domain.Track;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * 
 * @author Alexander Kolpakov (akolpakov)
 * 
 */
public interface TrackRepositoryCustom {

	Page<Track> find(SearchTrackCriteria searchTrackCreateria, Pageable pageable, boolean withTerritories, boolean withFiles);
}
