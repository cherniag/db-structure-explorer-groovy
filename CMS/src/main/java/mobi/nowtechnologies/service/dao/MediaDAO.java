package mobi.nowtechnologies.service.dao;

import java.util.List;

import mobi.nowtechnologies.server.persistence.domain.Media;


public class MediaDAO extends CNBaseDAO {

	private static final long serialVersionUID = -8987811161665939133L;

	public MediaDAO() {
		targetEntity = Media.class;
	}

	@Override
	public Class<?> getTargetEntityClass() {
		return targetEntity;
	}
	


  @SuppressWarnings("unchecked")
	public Media getByISRC(String isrc) {

			List<Media> result = (List<Media>) appManager.createQuery(
					"SELECT p FROM Media p WHERE p.isrc = :ident")
					.setParameter("ident", isrc).getResultList();
			if (result != null && result.size() > 0) {
				return result.get(0);
			}
			return null;

	}


}
