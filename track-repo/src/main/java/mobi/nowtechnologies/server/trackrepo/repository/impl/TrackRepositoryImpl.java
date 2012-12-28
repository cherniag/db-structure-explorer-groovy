package mobi.nowtechnologies.server.trackrepo.repository.impl;

import java.util.List;

import javax.persistence.Query;

import mobi.nowtechnologies.server.trackrepo.SearchTrackCriteria;
import mobi.nowtechnologies.server.trackrepo.domain.Track;
import mobi.nowtechnologies.server.trackrepo.repository.TrackRepositoryCustom;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

/**
 * @author Titov Mykhaylo (titov)
 * @author Alexander Kolpakov (akolpakov)
 * 
 */
public class TrackRepositoryImpl extends BaseJpaRepository implements TrackRepositoryCustom {

	@SuppressWarnings("unchecked")
	@Override
	public Page<Track> find(SearchTrackCriteria searchTrackCreateria, Pageable pagable, boolean withTerritories, boolean withFiles) {
		
		String suffixWithoutFetchQuery = createSuffixQuery(searchTrackCreateria, false, false);
		
		Query countQuery = buildQuery("SELECT count(distinct t) FROM Track t " + suffixWithoutFetchQuery, searchTrackCreateria);
		Long count = (Long) countQuery.getSingleResult();

		Query listQuery = buildQuery("SELECT distinct t.id FROM Track t " + suffixWithoutFetchQuery, searchTrackCreateria);
		listQuery.setFirstResult(pagable.getOffset());
		listQuery.setMaxResults(pagable.getPageSize());
		List<Integer> limitedTrackIdList = (List<Integer>) listQuery.getResultList();
		searchTrackCreateria.setTrackIds(limitedTrackIdList);
		
		String suffixQuery = createSuffixQuery(searchTrackCreateria, withTerritories, withFiles);
		Query limitedListQuery = buildQuery("SELECT distinct t FROM Track t " + suffixQuery, searchTrackCreateria);

		List<Track> limitedTrackList = (List<Track>) limitedListQuery.getResultList();

		PageImpl<Track> page = new PageImpl<Track>(limitedTrackList, pagable, count);
		return page;
	}

	private Query buildQuery(String queryText, SearchTrackCriteria trackCriteria) {
		if (queryText == null)
			throw new NullPointerException("The parameter queryText is null");
		if (trackCriteria == null)
			throw new NullPointerException("The parameter trackCriteria is null");

		Query query = getEntityManager().createQuery(queryText);
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
		if (trackCriteria.getTrackIds() != null)
			query = query.setParameter("limitedTrackIdList", trackCriteria.getTrackIds());

		return query;
	}

	private String createSuffixQuery(SearchTrackCriteria trackCriteria, boolean withTerritories, boolean withFiles) {
		if (trackCriteria == null)
			throw new NullPointerException("The parameter trackCriteria is null");
		
		StringBuilder join = new StringBuilder();

		if (withFiles) {
			join.append(" left join FETCH t.files files");
		}

		if (withTerritories) {
			join.append(" left join FETCH t.territories ter");
		}

		if (!withTerritories && (trackCriteria.getLabel() != null || trackCriteria.getReleaseFrom() != null || trackCriteria.getReleaseTo() != null)) {
			join.append(" left join t.territories ter");
		}

		StringBuilder criteria = new StringBuilder();
		if (trackCriteria.getLabel() != null)
			addCriteria(criteria, " (ter.label like :label or ter.distributor like :label)");
		if (trackCriteria.getReleaseFrom() != null)
			addCriteria(criteria, " ter.startDate >= :releaseFrom");
		if (trackCriteria.getReleaseTo() != null)
			addCriteria(criteria, " ter.startDate <= :releaseTo");
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
		if (trackCriteria.getTrackIds() != null)
			addCriteria(criteria, " t.id in :limitedTrackIdList");

		String suffixQuery = join.toString() + buildWhereCause(" WHERE ", criteria);
		return suffixQuery;
	}

	private void setParamLike(String paramKey, String paramVal, Query query) {
		if (paramVal != null)
			query.setParameter(paramKey, "%" + paramVal.toLowerCase() + "%");
	}
}