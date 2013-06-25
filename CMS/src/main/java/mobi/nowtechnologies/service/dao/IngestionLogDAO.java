package mobi.nowtechnologies.service.dao;

import java.util.List;

import javax.persistence.Query;

import mobi.nowtechnologies.domain.Track;



public class IngestionLogDAO extends BaseDAO  {

	private static final long serialVersionUID = -8987811161665939133L;

	public IngestionLogDAO() {
		targetEntity = Track.class;
	}

	@Override
	public Class<?> getTargetEntityClass() {
		return targetEntity;
	}
	
	public List<?> getLastLogs() {
		 
		String queryStr = "SELECT distinct l FROM IngestionLog l order by ingestionDate desc limit 50";
		Query query = appManager.createQuery(queryStr).setMaxResults(50);
	
				
		List result= query.getResultList();
		System.out.println("Found " + result.size()+" entries");
		return result;
	}




}
