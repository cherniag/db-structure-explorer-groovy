package mobi.nowtechnologies.service.dao;

import mobi.nowtechnologies.domain.AbstractEntity;
import mobi.nowtechnologies.server.persistence.domain.CNAbstractEntity;
import mobi.nowtechnologies.service.AppManager;
import mobi.nowtechnologies.service.IAppManager;

import java.io.Serializable;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public abstract class CNBaseDAO implements Serializable {


	private static final long serialVersionUID = 1L;

	protected static final Log LOG = LogFactory.getLog(CNBaseDAO.class);

	protected Class targetEntity;

	protected IAppManager appManager;

	public abstract Class<?> getTargetEntityClass();

	public void setAppManager(IAppManager appManager) {
		this.appManager = appManager;
	}

	public Object findById(byte id) {
		Object ret = null;
		try {
			ret = appManager.find(getTargetEntityClass(), id);
			LOG.debug("found " + ret);

		} catch (Exception e) {
			LOG.warn("Error in BaseDAO: " + e.getLocalizedMessage());
		}
		return ret;
	}

	public List<?> listAll() {
  
		return appManager.createQuery("SELECT a from " + getTargetEntityClass().getSimpleName() + " a")
				.getResultList();
	}

	public Long getCount() {
		return (Long) appManager.createQuery(
				"SELECT COUNT(o) FROM " + getTargetEntityClass().getSimpleName() + " o").getSingleResult();
	}

	public void persist(CNAbstractEntity obj) {
		appManager.beginTransaction();
		if (obj.getI() == null) {
			appManager.persist(obj);
		} else {
			appManager.merge(obj);
		}
		appManager.commitTransaction();
	}

	public void delete(Long id) {
		appManager.beginTransaction();
		Object obj = appManager.find(getTargetEntityClass(), id);
		appManager.remove(obj);
		appManager.commitTransaction();

	}
}
