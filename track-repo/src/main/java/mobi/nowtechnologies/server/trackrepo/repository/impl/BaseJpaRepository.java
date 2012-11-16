package mobi.nowtechnologies.server.trackrepo.repository.impl;

import javax.persistence.*;

public class BaseJpaRepository {
	@PersistenceContext
	private EntityManager entityManager;
	
	public EntityManager getEntityManager() {
		return entityManager;
	}

	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	protected void addCriteria(StringBuilder cause, String criteria){
		cause.append(cause.length() == 0 ? "" : " and ");
		cause.append(criteria);
	}
	
	protected String buildWhereCause(String name, StringBuilder cause){
		return cause.length() == 0 ? "" : name+cause.toString();
	}
}
