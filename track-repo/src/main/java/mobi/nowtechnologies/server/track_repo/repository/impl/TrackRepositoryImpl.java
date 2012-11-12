package mobi.nowtechnologies.server.track_repo.repository.impl;

import java.util.Collections;
import java.util.List;

import javax.persistence.Query;

import mobi.nowtechnologies.server.shared.dto.admin.SearchTrackDto;
import mobi.nowtechnologies.server.track_repo.domain.Track;
import mobi.nowtechnologies.server.track_repo.repository.TrackRepositoryCustom;

import org.springframework.data.domain.Pageable;

/**
 * 
 * @author Alexander Kolpakov (akolpakov)
 * 
 */
public class TrackRepositoryImpl extends BaseJpaRepository implements TrackRepositoryCustom {

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public List<Track> find(SearchTrackDto searchTrackDto, Pageable page) {		
		Query query = buildQuery("SELECT distinct t FROM Track t ", searchTrackDto);
		
		if (query != null) {
			query.setFirstResult(page.getOffset());
			query.setMaxResults(page.getPageSize());
			
			List result = query.getResultList();

			return result;
		}

		return Collections.emptyList();
	}

	@Override
	public long count(SearchTrackDto searchTrackDto) {
		Query query = buildQuery("SELECT count(distinct t) FROM Track t ", searchTrackDto);
		
		if (query != null) {			
			Long result = (Long)query.getSingleResult();

			return result;
		}

		return 0;
	}
	
	private Query buildQuery(String baseQuery, SearchTrackDto searchTrackDto){
		StringBuilder join = new StringBuilder();
		if (searchTrackDto.getLabel() != null || searchTrackDto.getReleaseFrom() != null || searchTrackDto.getReleaseTo() != null) {
			join.append("join t.territories as ter");
			
			StringBuilder criteria = new StringBuilder();
			if(searchTrackDto.getLabel() != null)
				addCriteria(criteria, " (ter.label like :label or ter.distributor like :label)");
			if (searchTrackDto.getReleaseFrom() != null)
				addCriteria(criteria, " ter.startDate >= :releaseFrom");
			if (searchTrackDto.getReleaseTo() != null)
				addCriteria(criteria, " ter.startDate <= :releaseTo");
			
			join.append(buildWhereCause(" WITH ", criteria));
		}

		StringBuilder criteria = new StringBuilder();
		if (searchTrackDto.getArtist() != null)
			addCriteria(criteria, " lower(t.artist) like :artist");
		if (searchTrackDto.getTitle() != null)
			addCriteria(criteria, " lower(t.title) like :title");
		if (searchTrackDto.getIsrc() != null)
			addCriteria(criteria, " lower(t.isrc) like :isrc");
		if (searchTrackDto.getIngestFrom() != null)
			addCriteria(criteria, " t.ingestionDate >= :from");
		if (searchTrackDto.getIngestTo() != null)
			addCriteria(criteria, " t.ingestionDate <= :to");
		if (searchTrackDto.getIngestor() != null)
			addCriteria(criteria, " lower(t.ingestor) like :ingestor");

		if (criteria.length() != 0 || join.length() != 0) {
			Query query = getEntityManager().createQuery(baseQuery+join.toString()+buildWhereCause(" WHERE ", criteria));
			if (searchTrackDto.getArtist() != null)
				query.setParameter("artist", "%" + searchTrackDto.getArtist().toLowerCase() + "%");
			if (searchTrackDto.getTitle() != null)
				query.setParameter("title", "%" + searchTrackDto.getTitle().toLowerCase() + "%");
			if (searchTrackDto.getIsrc() != null)
				query.setParameter("isrc", "%" + searchTrackDto.getIsrc().toLowerCase() + "%");
			if (searchTrackDto.getLabel() != null)
				query.setParameter("label", "%" + searchTrackDto.getLabel().toLowerCase() + "%");
			if (searchTrackDto.getIngestor() != null)
				query.setParameter("ingestor", "%" + searchTrackDto.getIngestor().toLowerCase() + "%");
			if (searchTrackDto.getIngestFrom() != null)
				query = query.setParameter("from", searchTrackDto.getIngestFrom());
			if (searchTrackDto.getIngestTo() != null)
				query = query.setParameter("to", searchTrackDto.getIngestTo());
			if (searchTrackDto.getReleaseFrom() != null)
				query = query.setParameter("releaseFrom", searchTrackDto.getReleaseFrom());
			if (searchTrackDto.getReleaseTo() != null)
				query = query.setParameter("releaseTo", searchTrackDto.getReleaseTo());
			
			return query;
		}	
		
		return null;
	}
}