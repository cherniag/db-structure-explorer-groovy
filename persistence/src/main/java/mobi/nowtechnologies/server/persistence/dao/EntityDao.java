package mobi.nowtechnologies.server.persistence.dao;

import mobi.nowtechnologies.server.persistence.domain.Community;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.dao.DataAccessException;
import org.springframework.orm.jpa.support.JpaDaoSupport;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Maksym Chernolevskyi (maksym)
 */
public class EntityDao extends JpaDaoSupport {

    private static final Logger LOGGER = LoggerFactory.getLogger(EntityDao.class);
    private static final String _AND_ = " and ";
    private static final String _O_WHERE_ = " o where ";
    private static final String _OR_ = " or ";

    public <T> T findById(Class<T> entityClass, Object id) {
        if (id == null) {
            throw new NullPointerException("The parameter id is null");
        }
        if (entityClass == null) {
            throw new NullPointerException("The parameter entityClass is null");
        }
        try {
            return getJpaTemplate().find(entityClass, id);
        } catch (DataAccessException dae) {
            LOGGER.error(dae.getMessage(), dae);
            throw new PersistenceException(dae);
        }
    }

    public <T> List<T> findAll(Class<T> entityClass) {
        return getJpaTemplate().find("select o from " + entityClass.getSimpleName() + " o");
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public <T> T updateEntity(T entity) {
        LOGGER.debug("input parameters entity: [{}]", entity);
        try {
            if (entity == null) {
                throw new PersistenceException("The parameter entity is null");
            }
            T merge = getJpaTemplate().merge(entity);
            LOGGER.debug("Output parameter [{}]", merge);
            return merge;
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new PersistenceException("Couldn't update entity");
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Object saveEntity(Object entity) {
        LOGGER.debug("input parameters entity: [{}]", entity);
        try {
            if (entity == null) {
                throw new PersistenceException("The parameter entity is null");
            }
            getJpaTemplate().persist(entity);
            getJpaTemplate().flush();
            LOGGER.info("[{}] entity [{}] inserted in db", entity.getClass(), entity);
            return entity;
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new PersistenceException("Couldn't save entity");
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void removeEntity(Class<?> entityClass, Object entityId) {
        try {
            if (entityClass == null || entityId == null) {
                throw new PersistenceException("The parameter entityClass or entityId is null");
            }
            Object objectToRemove = getJpaTemplate().find(entityClass, entityId);
            getJpaTemplate().remove(objectToRemove);
            getJpaTemplate().flush();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new PersistenceException("Couldn't remove entity");
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void removeEntity(Object entity) {
        try {
            if (entity == null) {
                throw new PersistenceException("The parameter entity is null");
            }
            getJpaTemplate().remove(entity);
            getJpaTemplate().flush();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new PersistenceException("Couldn't remove entity");
        }
    }

    public <T> List<T> findListByProperty(Class<T> entityClass, String fieldName, Object fieldValue) {
        if (entityClass == null) {
            throw new PersistenceException("The parameter entityClass is null");
        }
        if (fieldName == null) {
            throw new PersistenceException("The parameter fieldName is null");
        }
        if (fieldName.isEmpty()) {
            throw new PersistenceException("The parameter fieldName is empty");
        }

        Map<String, Object> fieldNameValueMap = new HashMap<String, Object>();
        fieldNameValueMap.put(fieldName, fieldValue);
        return findByPropertiesInternal(entityClass, fieldNameValueMap);
    }

    public <T> T findByProperty(Class<T> entityClass, String fieldName, Object fieldValue) {
        if (entityClass == null) {
            throw new PersistenceException("The parameter entityClass is null");
        }
        if (fieldName == null) {
            throw new PersistenceException("The parameter fieldName is null");
        }
        if (fieldName.isEmpty()) {
            throw new PersistenceException("The parameter fieldName is empty");
        }
        Map<String, Object> fieldNameValueMap = new HashMap<String, Object>();
        fieldNameValueMap.put(fieldName, fieldValue);
        List<T> list = findByPropertiesInternal(entityClass, fieldNameValueMap);
        return list == null || list.size() == 0 ?
               null :
               list.get(0);
    }

    // OR condtions
    public <T> List<T> findListByProperty(Class<T> entityClass, String fieldName, Object[] values) {
        try {
            if (null == values) {
                throw new NullPointerException("The parameter aMediaIds is null");
            }
            if (0 == values.length) {
                throw new IllegalArgumentException("The length of aMediaIds array parameter is 0");
            }
            if (entityClass == null) {
                throw new PersistenceException("The parameter entityClass is null");
            }
            if (fieldName == null) {
                throw new PersistenceException("The parameter fieldName is null");
            }
            if (fieldName.isEmpty()) {
                throw new PersistenceException("The parameter fieldName is empty");
            }
            if (values == null) {
                throw new PersistenceException("The parameter values is null");
            }

            StringBuilder query = new StringBuilder("select o from " + entityClass.getSimpleName() + _O_WHERE_);

            for (Object object : values) {
                String condition = " o." + fieldName;
                if (object == null) {
                    condition = condition + " is NULL ";
                } else if (object instanceof String) {
                    condition = condition + " = '" + String.valueOf(object) + "'";
                } else {
                    condition = condition + " = " + String.valueOf(object);
                }
                if (query.length() == query.lastIndexOf(_O_WHERE_) + _O_WHERE_.length()) {
                    query.append(condition);
                } else {
                    query.append(_OR_ + condition);
                }
            }
            return getJpaTemplate().find(query.toString());
        } catch (DataAccessException dae) {
            LOGGER.error(dae.getMessage(), dae);
            throw new PersistenceException();
        } catch (NumberFormatException nfe) {
            LOGGER.error(nfe.getMessage(), nfe);
            throw new PersistenceException();
        }
    }


    /**
     * and conditions
     */
    public <T> List<T> findListByProperties(Class<T> entityClass, Map<String, Object> fieldNameValueMap) {
        if (entityClass == null) {
            throw new PersistenceException("The parameter entityClass is null");
        }
        if (fieldNameValueMap == null) {
            throw new PersistenceException("The parameter fieldNameValueMap is null");
        }
        return findByPropertiesInternal(entityClass, fieldNameValueMap);
    }


    /**
     * and conditions
     */
    public <T> T findByProperties(Class<T> entityClass, Map<String, Object> fieldNameValueMap) {
        if (entityClass == null) {
            throw new PersistenceException("The parameter entityClass is null");
        }
        if (fieldNameValueMap == null) {
            throw new PersistenceException("The parameter fieldNameValueMap is null");
        }
        List<T> list = findByPropertiesInternal(entityClass, fieldNameValueMap);
        return list == null || list.size() == 0 ?
               null :
               list.get(0);
    }

    /**
     * and conditions
     */
    @SuppressWarnings("unchecked")
    private <T> List<T> findByPropertiesInternal(Class<T> entityClass, Map<String, Object> fieldNameValueMap) {
        if (null == entityClass) {
            throw new PersistenceException("The argument entityClass is null");
        }
        if (null == fieldNameValueMap) {
            throw new PersistenceException("The argument fieldNameValueMap is null");
        }
        if (fieldNameValueMap.size() == 0) {
            throw new PersistenceException("The fieldNameValueMap argument has 0 elements");
        }

        StringBuilder query = new StringBuilder("select o from " + entityClass.getSimpleName() + _O_WHERE_);
        Set<Entry<String, Object>> fieldNameValuesSet = fieldNameValueMap.entrySet();
        for (Entry<String, Object> fieldNameValueSetEntry : fieldNameValuesSet) {
            String condition = fieldNameValueSetEntry.getValue() instanceof String ?
                               fieldNameValueSetEntry.getKey() + " = '" + fieldNameValueSetEntry.getValue() + "'" :
                               fieldNameValueSetEntry.getKey() + " = " + fieldNameValueSetEntry.getValue();
            if (query.length() == query.lastIndexOf(_O_WHERE_) + _O_WHERE_.length()) {
                query.append(condition);
            } else {
                query.append(_AND_ + condition);
            }
        }
        return getJpaTemplate().find(query.toString());
    }

}
