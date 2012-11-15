package mobi.nowtechnologies.server.persistence.dao;

import mobi.nowtechnologies.server.persistence.domain.DrmType;

import java.util.List;

public class DrmTypeDao {
	public static final String PLAYS="PLAYS"; 
	public static final String TIME="TIME";
	public static final String PURCHASED="PURCHASED";
	
	private static DrmType PLAYS_DRM_TYPE;
	private static DrmType TIME_DRM_TYPE;
	private static DrmType PURCHASED_DRM_TYPE;
	
	private static void setEntityDao(EntityDao entityDao) {
		List<DrmType> drmTypes = entityDao.findAll(DrmType.class);
		
		for (DrmType drmType : drmTypes) {
			if (drmType.getName().equals(PLAYS))
				PLAYS_DRM_TYPE = drmType;
			else if (drmType.getName().equals(TIME))
				TIME_DRM_TYPE = drmType;
			else if (drmType.getName().equals(PURCHASED))
				PURCHASED_DRM_TYPE = drmType;
		}
		
		if (PLAYS_DRM_TYPE == null)
			throw new PersistenceException("The parameter PLAYS_DRM_TYPE is null");
		if (PURCHASED_DRM_TYPE == null)
			throw new PersistenceException("The parameter PURCHASED_DRM_TYPE is null");
		if (TIME_DRM_TYPE == null)
			throw new PersistenceException("The parameter TIME_DRM_TYPE is null");
	}

	public static DrmType getPLAYS_DRM_TYPE() {
		return PLAYS_DRM_TYPE;
	}

	public static DrmType getTIME_DRM_TYPE() {
		return TIME_DRM_TYPE;
	}

	public static DrmType getPURCHASED_DRM_TYPE() {
		return PURCHASED_DRM_TYPE;
	}

}
