package mobi.nowtechnologies.service.dao;

import java.util.List;

import mobi.nowtechnologies.server.persistence.domain.Artist;
import mobi.nowtechnologies.server.persistence.domain.Media;


public class ArtistDAO extends CNBaseDAO {

	private static final long serialVersionUID = -8987811161665939133L;

	public ArtistDAO() {
		targetEntity = Artist.class;
	}

	@Override
	public Class<?> getTargetEntityClass() {
		return targetEntity;
	}
	


  @SuppressWarnings("unchecked")
	public Artist getByname(String isrc) {

			List<Artist> result = (List<Artist>) appManager.createQuery(
					"SELECT p FROM Artist p WHERE p.name = :ident")
					.setParameter("ident", isrc).getResultList();
			if (result != null && result.size() > 0) {
				return result.get(0);
			}
			return null;

	}
  @SuppressWarnings("unchecked")
	public Artist getByRealName(String isrc) {

			List<Artist> result = (List<Artist>) appManager.createQuery(
					"SELECT p FROM Artist p WHERE p.realName = :ident")
					.setParameter("ident", isrc).getResultList();
			if (result != null && result.size() > 0) {
				return result.get(0);
			}
			return null;

	}


}
