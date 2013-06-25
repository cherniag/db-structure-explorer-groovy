package mobi.nowtechnologies.service;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.FlushModeType;
import javax.persistence.LockModeType;
import javax.persistence.Persistence;
import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public abstract class IAppManager {



	private EntityManagerFactory cnEntityMgrfactory ;



	public abstract void beginTransaction() ;

	public abstract void commitTransaction() ;

	public abstract void rollbackTransaction() ;

	public abstract void closeAll() ;

	public abstract void closeCommunication() ;

	public abstract void clear() ;

	public abstract boolean contains(Object arg0) ;

	public abstract Query createNamedQuery(String arg0) ;

	public abstract Query createNativeQuery(String arg0, Class arg1) ;

	public abstract Query createNativeQuery(String arg0, String arg1) ;

	public abstract Query createNativeQuery(String arg0) ;

	public abstract Query createQuery(String arg0) ;

	public abstract <T> T find(Class<T> arg0, Object arg1) ;

	public abstract void flush() ;

	public abstract Object getDelegate() ;

	public abstract FlushModeType getFlushMode() ;

	public abstract <T> T getReference(Class<T> arg0, Object arg1) ;

	public abstract EntityTransaction getTransaction() ;

	public abstract boolean isOpen() ;

	public abstract void joinTransaction() ;

	public abstract void lock(Object arg0, LockModeType arg1) ;
	public abstract <T> T merge(T arg0) ;

	public abstract void persist(Object arg0) ;

	public abstract void refresh(Object arg0) ;

	public abstract void remove(Object arg0) ;

	public abstract void setFlushMode(FlushModeType arg0) ;

	public abstract void initCommunication() ;

	protected abstract EntityManager getEntityManager() ;

	protected abstract EntityTransaction getEntityTransaction() ;
}
