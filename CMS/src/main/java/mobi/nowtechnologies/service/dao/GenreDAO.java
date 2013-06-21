package mobi.nowtechnologies.service.dao;

import java.util.List;

import mobi.nowtechnologies.server.persistence.domain.Artist;
import mobi.nowtechnologies.server.persistence.domain.Genre;
import mobi.nowtechnologies.server.persistence.domain.Media;


public class GenreDAO extends CNBaseDAO {

	private static final long serialVersionUID = -8987811161665939133L;

	public GenreDAO() {
		targetEntity = Genre.class;
	}

	@Override
	public Class<?> getTargetEntityClass() {
		return targetEntity;
	}
	
	  @SuppressWarnings("unchecked")
		public Genre getByname(String name) {

				List<Genre> result = (List<Genre>) appManager.createQuery(
						"SELECT p FROM Genre p WHERE p.name = :ident")
						.setParameter("ident", name).getResultList();
				if (result != null && result.size() > 0) {
					return result.get(0);
				}
				return null;

		}





}
