package mobi.nowtechnologies.server.trackrepo.repository.impl;

import mobi.nowtechnologies.server.trackrepo.SearchTrackCriteria;
import mobi.nowtechnologies.server.trackrepo.domain.Track;
import mobi.nowtechnologies.server.trackrepo.repository.TrackRepositoryCustom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import javax.persistence.Query;
import java.util.Iterator;
import java.util.List;

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
		
		Query countQuery = buildQuery("SELECT t.id FROM Track t " + suffixWithoutFetchQuery, searchTrackCreateria);
        countQuery.setFirstResult(pagable.getOffset());
        countQuery.setMaxResults(pagable.getPageSize() * 5 + 1);
        List<Integer> ids = countQuery.getResultList();
        Long count = new Long(ids.size() + pagable.getOffset());

		String suffixQuery = createSuffixQuery(searchTrackCreateria, withTerritories, withFiles);
		Query listQuery = buildQuery("SELECT t FROM Track t " + suffixQuery, searchTrackCreateria);
        listQuery.setFirstResult(pagable.getOffset());
        listQuery.setMaxResults(pagable.getPageSize());

		List<Track> limitedTrackList = (List<Track>) listQuery.getResultList();
        if(withFiles || withTerritories){
            Iterator<Track> i = limitedTrackList.iterator();
            int j = 0;
            while(i.hasNext() && j < pagable.getPageSize()){
                Track track = i.next();
                if(withTerritories)
                    track.getTerritories().size();
                if(withFiles)
                    track.getFiles().size();

                j++;
            }
        }

        PageImpl <Track> page = new PageImpl<Track>(limitedTrackList, pagable, count);
		return page;
	}

	private Query buildQuery(String queryText, SearchTrackCriteria trackCriteria) {
		if (queryText == null)
			throw new NullPointerException("The parameter queryText is null");
		if (trackCriteria == null)
			throw new NullPointerException("The parameter trackCriteria is null");

		Query query = getEntityManager().createQuery(queryText);

        if (trackCriteria.getTrackIds() != null && trackCriteria.getTrackIds().size() > 0) {
            query = query.setParameter("id", trackCriteria.getTrackIds().get(0));
        }
        else{
            setParamLike("genre", trackCriteria.getGenre(), query);
            setParamLike("album", trackCriteria.getAlbum(), query);
            setParamLike("artist", trackCriteria.getArtist(), query);
            setParamLike("title", trackCriteria.getTitle(), query);
            setParamLike("label", trackCriteria.getLabel(), query);
            setParamLike("ingestor", trackCriteria.getIngestor(), query);

            setParam("isrc", trackCriteria.getIsrc(), query);
            setParam("from", trackCriteria.getIngestFrom(), query);
            setParam("to", trackCriteria.getIngestTo(), query);
            setParam("releaseFrom", trackCriteria.getReleaseFrom(), query);
            setParam("releaseTo", trackCriteria.getReleaseTo(), query);
        }

		return query;
	}

	private String createSuffixQuery(SearchTrackCriteria trackCriteria, boolean withTerritories, boolean withFiles) {
		if (trackCriteria == null)
			throw new NullPointerException("The parameter trackCriteria is null");
		
		StringBuilder join = new StringBuilder();

		if (!withTerritories && (trackCriteria.getLabel() != null || trackCriteria.getReleaseFrom() != null || trackCriteria.getReleaseTo() != null)) {
			join.append(" left join t.territories ter");
		}

		StringBuilder criteria = new StringBuilder();
        if (trackCriteria.getTrackIds() != null && trackCriteria.getTrackIds().size() > 0)
            addCriteria(criteria, " t.id = :id");
        else{
            if (trackCriteria.getLabel() != null && !trackCriteria.getLabel().isEmpty())
                addCriteria(criteria, " (ter.label like :label or ter.distributor like :label)");
            if (trackCriteria.getReleaseFrom() != null)
                addCriteria(criteria, " ter.startDate >= :releaseFrom");
            if (trackCriteria.getReleaseTo() != null)
                addCriteria(criteria, " ter.startDate <= :releaseTo");
            if (trackCriteria.getGenre() != null && !trackCriteria.getGenre().isEmpty())
                addCriteria(criteria, " lower(t.genre) like :genre");
            if (trackCriteria.getAlbum() != null && !trackCriteria.getAlbum().isEmpty())
                addCriteria(criteria, " lower(t.album) like :album");
            if (trackCriteria.getArtist() != null && !trackCriteria.getArtist().isEmpty())
                addCriteria(criteria, " lower(t.artist) like :artist");
            if (trackCriteria.getTitle() != null && !trackCriteria.getTitle().isEmpty())
                addCriteria(criteria, " lower(t.title) like :title");
            if (trackCriteria.getIsrc() != null && !trackCriteria.getIsrc().isEmpty())
                addCriteria(criteria, " t.isrc = :isrc");
            if (trackCriteria.getIngestFrom() != null)
                addCriteria(criteria, " t.ingestionDate >= :from");
            if (trackCriteria.getIngestTo() != null)
                addCriteria(criteria, " t.ingestionDate <= :to");
            if (trackCriteria.getIngestor() != null && !trackCriteria.getIngestor().isEmpty())
                addCriteria(criteria, " lower(t.ingestor) like :ingestor");
        }

		String suffixQuery = join.toString() + buildWhereCause(" WHERE ", criteria);
		return suffixQuery;
	}

	private void setParamLike(String paramKey, String paramVal, Query query) {
		if (paramVal != null && !paramVal.isEmpty())
			query.setParameter(paramKey, "%" + paramVal.toLowerCase() + "%");
	}

    private void setParam(String paramKey, String paramVal, Query query) {
		if (paramVal != null && !paramVal.isEmpty())
			query.setParameter(paramKey, paramVal);
	}

    private void setParam(String paramKey, Object paramVal, Query query) {
        if (paramVal != null)
            query.setParameter(paramKey, paramVal);
    }
}