package mobi.nowtechnologies.server.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import mobi.nowtechnologies.server.persistence.dao.EntityDao;
import mobi.nowtechnologies.server.persistence.dao.PersistenceException;
import mobi.nowtechnologies.server.service.exception.ServiceException;

/**
 * EntityService
 * 
 * @author Maksym Chernolevskyi (maksym)
 * 
 */
@Deprecated
public class EntityService {
	private EntityDao entityDao;

	public void setEntityDao(EntityDao entityDao) {
		this.entityDao = entityDao;
	}

	public <T> T findById(Class<T> entityClass, Object id) {
		if (id == null)
			throw new ServiceException("The parameter id is null");
		if (entityClass == null)
			throw new ServiceException("The parameter entityClass is null");
		return entityDao.findById(entityClass, id);
	}
	
	@Transactional(propagation = Propagation.REQUIRED)
	public <T> T updateEntity(T entity) {
		if (entity == null)
			throw new ServiceException("The parameter entity is null");
		return entityDao.updateEntity(entity);
	}

	public void saveEntity(Object entity) {
		if (entity == null)
			throw new ServiceException("The parameter entity is null");
		entityDao.saveEntity(entity);
	}

	public void removeEntity(Class<?> entityClass, Object entityId) {
		if (entityClass == null || entityId == null)
			throw new PersistenceException("The parameter entityClass or entityId is null");
		entityDao.removeEntity(entityClass, entityId);
	}
	
	public void removeEntity(Object entity) {
		if (entity == null)
			throw new PersistenceException("The parameter entity is null");
		entityDao.removeEntity(entity);
	}

	public <T> T findByProperties(Class<T> entityClass, Map<String, Object> fieldNameValueMap) {
		if (entityClass == null)
			throw new ServiceException("The parameter entityClass is null");
		if (fieldNameValueMap == null)
			throw new ServiceException("The parameter fieldNameValueMap is null");
		return entityDao.findByProperties(entityClass, fieldNameValueMap);
	}

	public <T> List<T> findListByProperties(Class<T> entityClass, Map<String, Object> fieldNameValueMap) {
		if (entityClass == null)
			throw new ServiceException("The parameter entityClass is null");
		if (fieldNameValueMap == null)
			throw new ServiceException("The parameter fieldNameValueMap is null");
		return entityDao.findListByProperties(entityClass, fieldNameValueMap);
	}

	public <T> List<T> findListByProperty(Class<T> entityClass, String fieldName, Object fieldValue) {
		if (entityClass == null)
			throw new ServiceException("The parameter entityClass is null");
		if (fieldName == null)
			throw new ServiceException("The parameter fieldName is null");
		if (fieldName.isEmpty())
			throw new ServiceException("The parameter fieldName is empty");

		return entityDao.findListByProperty(entityClass, fieldName, fieldValue);
	}

	public <T> List<T> findListByProperty(Class<T> entityClass, String fieldName, Object[] values) {
		if (entityClass == null)
			throw new ServiceException("The parameter entityClass is null");
		if (fieldName == null)
			throw new ServiceException("The parameter fieldName is null");
		if (fieldName.isEmpty())
			throw new ServiceException("The parameter fieldName is empty");
		if (values == null)
			throw new ServiceException("The parameter values is null");
		if (values.length == 0)
			return new ArrayList<T>();
		return entityDao.findListByProperty(entityClass, fieldName, values);
	}

	public <T> T findByProperty(Class<T> entityClass, String fieldName, Object fieldValue) {
		if (fieldName == null)
			throw new ServiceException("The parameter fieldName is null");
		if (fieldName.isEmpty())
			throw new ServiceException("The parameter fieldName is empty");
		if (entityClass == null)
			throw new ServiceException("The parameter entityClass is null");
		return entityDao.findByProperty(entityClass, fieldName, fieldValue);
	}
}
