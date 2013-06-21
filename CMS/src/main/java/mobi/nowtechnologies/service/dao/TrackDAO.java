package mobi.nowtechnologies.service.dao;

import mobi.nowtechnologies.domain.Track;

import java.util.Date;
import java.util.List;

import javax.persistence.Query;



public class TrackDAO extends BaseDAO implements ITrackDAO {

	private static final long serialVersionUID = -8987811161665939133L;

	public TrackDAO() {
		targetEntity = Track.class;
	}

	@Override
	public Class<?> getTargetEntityClass() {
		return targetEntity;
	}
	
	public List<?> search(String artist, String title, String isrc, Date ingestFrom, Date ingestTo, String label, String ingestor) {
		 
		String queryStr = "SELECT distinct t FROM Track t ";
		if (label != null) {
			queryStr += "left join t.Territories as ter";
		}


		queryStr += " WHERE lower(t.Artist) like :artist and lower(t.Title) like :title and lower(t.ISRC) like :isrc";
		if (ingestFrom != null) {
			queryStr += "  and IngestionDate >= :from";
		}
		if (ingestTo != null) {
			queryStr += "  and IngestionDate <= :to";
		}
		if (ingestor != null) {
			queryStr += " and t.Ingestor like :ingestor";
		}
		if (label != null) {
			queryStr += " and (ter.Label like :label or ter.Distributor like :label)";
		}
		Query query = appManager.createQuery(queryStr)
				.setParameter("artist", "%"+artist.toLowerCase()+"%")
				.setParameter("title", "%"+title.toLowerCase()+"%")
				.setParameter("isrc", "%"+isrc.toLowerCase()+"%");
		if (label != null) {
			query.setParameter("label", "%"+label.toLowerCase()+"%");
			
		}
		if (ingestor != null) {
			query.setParameter("ingestor", "%"+ingestor.toLowerCase()+"%");
			
		}
		if (ingestFrom != null) {
			query = query.setParameter("from", ingestFrom);
		}
		if (ingestTo != null) {
			query = query.setParameter("to", ingestTo);
		}
	
				
		List result= query.getResultList();
		System.out.println("Found " + result.size()+" entries");
		return result;
	}


  @SuppressWarnings("unchecked")
	public List<Track> listByArtist(String artist) {

			return (List<Track>) appManager.createQuery(
					"SELECT p FROM Track p WHERE p.Artist = :ident")
					.setParameter("ident", artist).getResultList();

	}
  @SuppressWarnings("unchecked")
	public Track getByISRC(String isrc) {

			List<Track> result = (List<Track>) appManager.createQuery(
					"SELECT p FROM Track p WHERE p.ISRC = :ident")
					.setParameter("ident", isrc).getResultList();
			if (result != null && result.size() > 0) {
				return result.get(0);
			}
			return null;

	}
  @SuppressWarnings("unchecked")
	public Track getByKey(String isrc, String productCode, String ingestor) {

			List<Track> result = (List<Track>) appManager.createQuery(
					"SELECT p FROM Track p WHERE p.ISRC = :isrc and p.ProductCode = :code and p.Ingestor = :ingestor")
					.setParameter("isrc", isrc)
					.setParameter("code", productCode)
					.setParameter("ingestor", ingestor)
					.getResultList();
			if (result != null && result.size() > 0) {
				return result.get(0);
			}
			return null;

	}
  @SuppressWarnings("unchecked")
	public Track getByShortKey(String isrc, String productCode, String ingestor) {

			List<Track> result = (List<Track>) appManager.createQuery(
					"SELECT p FROM Track p WHERE p.ISRC = :isrc and p.Ingestor = :ingestor")
					.setParameter("isrc", isrc)
					.setParameter("ingestor", ingestor)
					.getResultList();
			if (result != null && result.size() > 0) {
				return result.get(0);
			}
			return null;

	}

  @SuppressWarnings("unchecked")
	public Track getByProductCode(String productCode) {

			List<Track> result = (List<Track>) appManager.createQuery(
					"SELECT p FROM Track p WHERE p.ProductCode = :ident")
					.setParameter("ident", productCode).getResultList();
			if (result != null && result.size() > 0) {
				return result.get(0);
			}
			return null;

	}


}
