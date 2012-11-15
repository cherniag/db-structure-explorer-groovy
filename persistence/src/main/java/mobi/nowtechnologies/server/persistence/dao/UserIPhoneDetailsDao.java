package mobi.nowtechnologies.server.persistence.dao;

import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.UserDeviceDetails;
import mobi.nowtechnologies.server.persistence.domain.UserIPhoneDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.jpa.JpaCallback;
import org.springframework.orm.jpa.support.JpaDaoSupport;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;
import java.util.List;

/**
 * @author Titov Mykhaylo (titov)
 * 
 */
public class UserIPhoneDetailsDao extends JpaDaoSupport {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(UserIPhoneDetailsDao.class);

	@SuppressWarnings("unchecked")
	public List<UserIPhoneDetails> getUserIPhoneDetailsListForPushNotification(Community community) {
		if (community == null)
			throw new PersistenceException("The parameter community is null");
		LOGGER.debug("input parameters community: [{}]", community);
		
		List<UserIPhoneDetails> userIPhoneDetailsList = getJpaTemplate()
				.findByNamedQuery(
						UserIPhoneDetails.NQ_GET_USER_IPHONE_DETAILS_LIST_FOR_PUSH_NOTIFICATION, community.getId());
		LOGGER.debug("Output parameter userIPhoneDetailsList=[{}]",
				userIPhoneDetailsList);
		return userIPhoneDetailsList;
	}
	
	public int updateUserIPhoneDetailsForPushNotification(final Community community) {
		if (community == null)
			throw new PersistenceException("The parameter community is null");
		LOGGER.debug("input parameters community: [{}]", community);

		Integer updatedRowsCount = getJpaTemplate().execute(new JpaCallback<Integer>() {

			@Override
			public Integer doInJpa(EntityManager em) throws javax.persistence.PersistenceException {
				LOGGER.debug("input parameters em: [{}]", em );
				EntityTransaction entityTransaction = em.getTransaction();
				try{
					entityTransaction.begin();
					Query query = em.createNamedQuery(UserIPhoneDetails.NQ_UPDATE_USER_IPHONE_DETAILS_LIST_FOR_PUSH_NOTIFICATION);
					query.setParameter(1, community.getId());
					
					Integer updatedRowsCount = query.executeUpdate();
					entityTransaction.commit();
					LOGGER.debug("Output parameter updatedRowsCount=[{}]", updatedRowsCount);
					return updatedRowsCount;
				}catch (PersistenceException e) {
					LOGGER.error(e.getMessage(), e);
					entityTransaction.rollback();
					throw e;
				}
			}

		});
		
		LOGGER.debug("Output parameter updatedRowsCount=[{}]", updatedRowsCount);
		return updatedRowsCount;
	}
	
	
	@SuppressWarnings("unchecked")
	public UserDeviceDetails getUserDeviceDetails(int userId){
		LOGGER.debug("input parameters userId: [{}]", userId);
		
		UserDeviceDetails userDeviceDetails = null;
		List<UserDeviceDetails> userDeviceDetailsList = getJpaTemplate().findByNamedQuery(UserIPhoneDetails.NQ_GET_USER_IPHONE_DETAILS_BY_USER_ID, userId);
		if (!userDeviceDetailsList.isEmpty()) userDeviceDetails = userDeviceDetailsList.get(0);
		
		LOGGER.debug("Output parameter userDeviceDetails=[{}]", userDeviceDetails);
		return userDeviceDetails;
	}

}
