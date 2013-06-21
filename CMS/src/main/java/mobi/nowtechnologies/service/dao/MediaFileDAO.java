package mobi.nowtechnologies.service.dao;

import java.util.List;

import mobi.nowtechnologies.server.persistence.domain.Artist;
import mobi.nowtechnologies.server.persistence.domain.Media;
import mobi.nowtechnologies.server.persistence.domain.MediaFile;


public class MediaFileDAO extends CNBaseDAO {

	private static final long serialVersionUID = -8987811161665939133L;

	public MediaFileDAO() {
		targetEntity = MediaFile.class;
	}

	@Override
	public Class<?> getTargetEntityClass() {
		return targetEntity;
	}
	


  @SuppressWarnings("unchecked")
	public MediaFile getByName(String isrc) {

			List<MediaFile> result = (List<MediaFile>) appManager.createQuery(
					"SELECT p FROM MediaFile p WHERE p.filename = :ident")
					.setParameter("ident", isrc).getResultList();
			if (result != null && result.size() > 0) {
				return result.get(0);
			}
			return null;

	}
  
  public MediaFile createFile(String name, byte type) {
	  MediaFile file = getByName(name);
	  if (file == null){
		  file = new MediaFile();
		  file.setFilename(name);
		  file.setFileType(type);
		  persist(file);
	  }
	  return file;
  }


}
