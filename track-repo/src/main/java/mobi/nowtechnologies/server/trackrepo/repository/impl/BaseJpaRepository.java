package mobi.nowtechnologies.server.trackrepo.repository.impl;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

public class BaseJpaRepository {
	@PersistenceContext
	private EntityManager entityManager;
	
	public EntityManager getEntityManager() {
		return entityManager;
	}

	protected void addCriteria(StringBuilder cause, String criteria){
		cause.append(cause.length() == 0 ? "" : " and ");
		cause.append(criteria);
	}
	
	protected String buildWhereCause(String name, StringBuilder cause){
		return cause.length() == 0 ? "" : name+cause.toString();
	}
}
