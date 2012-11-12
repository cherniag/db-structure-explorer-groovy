package mobi.nowtechnologies.server.track_repo.repository.impl;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;

public class BaseJpaRepository {
	private static final ThreadLocal<EntityManager> threadLocalEntityManager = new ThreadLocal<EntityManager>();
	
	private static final ThreadLocal<EntityTransaction> threadLocalEntityTransaction = new ThreadLocal<EntityTransaction>();

	private EntityManagerFactory entityManagerFactory;

	public void setEntityManagerFactory(EntityManagerFactory entityManagerFactory) {
		this.entityManagerFactory = entityManagerFactory;
	}

	protected EntityManager getEntityManager() {
		EntityManager ret = threadLocalEntityManager.get();
		if (ret == null) {

			ret = entityManagerFactory.createEntityManager();
			threadLocalEntityManager.set(ret);
		}
		return ret;
	}
	
	protected EntityTransaction getEntityTransaction() {
		EntityTransaction et = threadLocalEntityTransaction.get();
		if (et == null) {
			et = getEntityManager().getTransaction();
			threadLocalEntityTransaction.set(et);
		}
		return et;
	}
	
	protected void addCriteria(StringBuilder cause, String criteria){
		cause.append(cause.length() == 0 ? "" : " and ");
		cause.append(criteria);
	}
	
	protected String buildWhereCause(String name, StringBuilder cause){
		return cause.length() == 0 ? "" : name+cause.toString();
	}
}
