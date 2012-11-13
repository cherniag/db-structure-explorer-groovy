package mobi.nowtechnologies.server.trackrepo.repository.impl;

import java.util.Collections;
import java.util.List;

import javax.persistence.Query;

import mobi.nowtechnologies.server.trackrepo.SearchTrackCriteria;
import mobi.nowtechnologies.server.trackrepo.domain.Track;
import mobi.nowtechnologies.server.trackrepo.repository.TrackRepositoryCustom;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

/**
 * 
 * @author Alexander Kolpakov (akolpakov)
 * 
 */
public class TrackRepositoryImpl extends BaseJpaRepository implements TrackRepositoryCustom {

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public Page<Track> find(SearchTrackCriteria searchTrackCreateria, Pageable page) {		
		Query listQuery = buildQuery("SELECT distinct t FROM Track t ", searchTrackCreateria);
		
		if (listQuery != null) {
			listQuery.setFirstResult(page.getOffset());
			listQuery.setMaxResults(page.getPageSize());
			
			Query countQuery = buildQuery("SELECT count(distinct t) FROM Track t ", searchTrackCreateria);
			
			List result = listQuery.getResultList();
			Long count = (Long)countQuery.getSingleResult();

			return new PageImpl<Track>(result, page, count);
		}

		return new PageImpl<Track>(Collections.<Track>emptyList(), page, 0L);
	}
	
	private Query buildQuery(String baseQuery, SearchTrackCriteria searchTrackCreateria){
		StringBuilder join = new StringBuilder();
		if (searchTrackCreateria.getLabel() != null || searchTrackCreateria.getReleaseFrom() != null || searchTrackCreateria.getReleaseTo() != null) {
			join.append("join t.territories as ter");
			
			StringBuilder criteria = new StringBuilder();
			if(searchTrackCreateria.getLabel() != null)
				addCriteria(criteria, " (ter.label like :label or ter.distributor like :label)");
			if (searchTrackCreateria.getReleaseFrom() != null)
				addCriteria(criteria, " ter.startDate >= :releaseFrom");
			if (searchTrackCreateria.getReleaseTo() != null)
				addCriteria(criteria, " ter.startDate <= :releaseTo");
			
			join.append(buildWhereCause(" WITH ", criteria));
		}

		StringBuilder criteria = new StringBuilder();
		if (searchTrackCreateria.getArtist() != null)
			addCriteria(criteria, " lower(t.artist) like :artist");
		if (searchTrackCreateria.getTitle() != null)
			addCriteria(criteria, " lower(t.title) like :title");
		if (searchTrackCreateria.getIsrc() != null)
			addCriteria(criteria, " lower(t.isrc) like :isrc");
		if (searchTrackCreateria.getIngestFrom() != null)
			addCriteria(criteria, " t.ingestionDate >= :from");
		if (searchTrackCreateria.getIngestTo() != null)
			addCriteria(criteria, " t.ingestionDate <= :to");
		if (searchTrackCreateria.getIngestor() != null)
			addCriteria(criteria, " lower(t.ingestor) like :ingestor");

		if (criteria.length() != 0 || join.length() != 0) {
			Query query = getEntityManager().createQuery(baseQuery+join.toString()+buildWhereCause(" WHERE ", criteria));
			if (searchTrackCreateria.getArtist() != null)
				query.setParameter("artist", "%" + searchTrackCreateria.getArtist().toLowerCase() + "%");
			if (searchTrackCreateria.getTitle() != null)
				query.setParameter("title", "%" + searchTrackCreateria.getTitle().toLowerCase() + "%");
			if (searchTrackCreateria.getIsrc() != null)
				query.setParameter("isrc", "%" + searchTrackCreateria.getIsrc().toLowerCase() + "%");
			if (searchTrackCreateria.getLabel() != null)
				query.setParameter("label", "%" + searchTrackCreateria.getLabel().toLowerCase() + "%");
			if (searchTrackCreateria.getIngestor() != null)
				query.setParameter("ingestor", "%" + searchTrackCreateria.getIngestor().toLowerCase() + "%");
			if (searchTrackCreateria.getIngestFrom() != null)
				query = query.setParameter("from", searchTrackCreateria.getIngestFrom());
			if (searchTrackCreateria.getIngestTo() != null)
				query = query.setParameter("to", searchTrackCreateria.getIngestTo());
			if (searchTrackCreateria.getReleaseFrom() != null)
				query = query.setParameter("releaseFrom", searchTrackCreateria.getReleaseFrom());
			if (searchTrackCreateria.getReleaseTo() != null)
				query = query.setParameter("releaseTo", searchTrackCreateria.getReleaseTo());
			
			return query;
		}	
		
		return null;
	}
}