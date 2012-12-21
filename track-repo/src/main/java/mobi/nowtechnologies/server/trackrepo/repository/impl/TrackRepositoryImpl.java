package mobi.nowtechnologies.server.trackrepo.repository.impl;

import mobi.nowtechnologies.server.trackrepo.SearchTrackCriteria;
import mobi.nowtechnologies.server.trackrepo.domain.Track;
import mobi.nowtechnologies.server.trackrepo.repository.TrackRepositoryCustom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import javax.persistence.Query;
import java.util.Collections;
import java.util.List;

/**
 * 
 * @author Alexander Kolpakov (akolpakov)
 * 
 */
public class TrackRepositoryImpl extends BaseJpaRepository implements TrackRepositoryCustom {

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public Page<Track> find(SearchTrackCriteria searchTrackCreateria, Pageable page, boolean withTerritories, boolean withFiles) {
		Query listQuery = buildQuery("SELECT distinct t FROM Track t ", searchTrackCreateria);

		if (listQuery != null) {
			listQuery.setFirstResult(page.getOffset());
			listQuery.setMaxResults(page.getPageSize());

			Query countQuery = buildQuery("SELECT count(distinct t) FROM Track t ", searchTrackCreateria);

			List<Track> result = (List<Track>) listQuery.getResultList();
			Long count = (Long) countQuery.getSingleResult();

			if (withTerritories || withFiles) {
				for (Track track : result) {
					if (withTerritories)
						track.getTerritories().size();
					if (withFiles)
						track.getFiles().size();
				}
			}

			return new PageImpl<Track>(result, page, count);
		}

		return new PageImpl<Track>(Collections.<Track> emptyList(), page, 0L);
	}

	private Query buildQuery(String baseQuery, SearchTrackCriteria trackCriteria) {
		StringBuilder join = new StringBuilder();
		if (trackCriteria.getLabel() != null || trackCriteria.getReleaseFrom() != null || trackCriteria.getReleaseTo() != null) {
			join.append(" left join t.territories as ter");

			StringBuilder criteria = new StringBuilder();
			if (trackCriteria.getLabel() != null)
				addCriteria(criteria, " (ter.label like :label or ter.distributor like :label)");
			if (trackCriteria.getReleaseFrom() != null)
				addCriteria(criteria, " ter.startDate >= :releaseFrom");
			if (trackCriteria.getReleaseTo() != null)
				addCriteria(criteria, " ter.startDate <= :releaseTo");

			join.append(buildWhereCause(" WITH ", criteria));
		}

		StringBuilder criteria = new StringBuilder();
		if (trackCriteria.getGenre() != null)
			addCriteria(criteria, " lower(t.genre) like :genre");
		if (trackCriteria.getAlbum() != null)
			addCriteria(criteria, " lower(t.album) like :album");
		if (trackCriteria.getArtist() != null)
			addCriteria(criteria, " lower(t.artist) like :artist");
		if (trackCriteria.getTitle() != null)
			addCriteria(criteria, " lower(t.title) like :title");
		if (trackCriteria.getIsrc() != null)
			addCriteria(criteria, " lower(t.isrc) like :isrc");
		if (trackCriteria.getIngestFrom() != null)
			addCriteria(criteria, " t.ingestionDate >= :from");
		if (trackCriteria.getIngestTo() != null)
			addCriteria(criteria, " t.ingestionDate <= :to");
		if (trackCriteria.getIngestor() != null)
			addCriteria(criteria, " lower(t.ingestor) like :ingestor");

		if (criteria.length() != 0 || (join.length() != 0 && join.indexOf("WITH") > 0)) {
			Query query = getEntityManager().createQuery(baseQuery + join.toString() + buildWhereCause(" WHERE ", criteria));
			setParamLike("genre", trackCriteria.getGenre(), query);
			setParamLike("album", trackCriteria.getAlbum(), query);
			setParamLike("artist", trackCriteria.getArtist(), query);
			setParamLike("title", trackCriteria.getTitle(), query);
			setParamLike("isrc", trackCriteria.getIsrc(), query);
			setParamLike("label", trackCriteria.getLabel(), query);
			setParamLike("ingestor", trackCriteria.getIngestor(), query);

			if (trackCriteria.getIngestFrom() != null)
				query = query.setParameter("from", trackCriteria.getIngestFrom());
			if (trackCriteria.getIngestTo() != null)
				query = query.setParameter("to", trackCriteria.getIngestTo());
			if (trackCriteria.getReleaseFrom() != null)
				query = query.setParameter("releaseFrom", trackCriteria.getReleaseFrom());
			if (trackCriteria.getReleaseTo() != null)
				query = query.setParameter("releaseTo", trackCriteria.getReleaseTo());

			return query;
		}

		return null;
	}

	private void setParamLike(String paramKey, String paramVal, Query query) {
		if (paramVal != null)
			query.setParameter(paramKey, "%" + paramVal.toLowerCase() + "%");
	}
}