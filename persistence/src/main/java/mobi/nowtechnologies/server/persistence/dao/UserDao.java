package mobi.nowtechnologies.server.persistence.dao;

import static mobi.nowtechnologies.server.shared.AppConstants.NOT_AVAILABLE;
import static mobi.nowtechnologies.server.shared.AppConstants.STATUS_OK;
import static mobi.nowtechnologies.server.shared.AppConstants.STATUS_REGISTERED;
import static mobi.nowtechnologies.server.shared.Utils.getEpochSeconds;

import java.text.MessageFormat;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import mobi.nowtechnologies.common.dto.UserRegInfo.PaymentType;
import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.PayPalPayment;
import mobi.nowtechnologies.server.persistence.domain.Payment;
import mobi.nowtechnologies.server.persistence.domain.PaymentPolicy;
import mobi.nowtechnologies.server.persistence.domain.Promotion;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.UserGroup;
import mobi.nowtechnologies.server.shared.AppConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.orm.jpa.JpaCallback;
import org.springframework.orm.jpa.support.JpaDaoSupport;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * UserDao
 * 
 * @author Maksym Chernolevskyi (maksym)
 * 
 */
public class UserDao extends JpaDaoSupport {
	private static final Logger LOGGER = LoggerFactory.getLogger(UserDao.class);
	
	@SuppressWarnings("unchecked")
	public List<User> getListOfUsersForUpdate() {
		return (List<User>) getJpaTemplate()
				.find("select user from "
					 + User.class.getSimpleName()
					 + " user where user.subBalance = 0"
					 + " and user.paymentEnabled=true"
					 + " and paymentStatus != " + PaymentStatusDao.getAWAITING_PSMS().getId()
					 + " and ( " +
					 		" (user.userStatusId = 10 or user.userStatusId = 11)"
					 + 		" or ( user.userStatusId = 4 and paymentStatus = " + PaymentStatusDao.getAWAITING_PAYMENT().getId() + " )"
					 +" )"
					 + " order by user.userStatusId desc limit 0,1000");
	}

	public Payment getLatestPaymentForUser(int userId) {
		List<?> list = getJpaTemplate().find(
						"select o from "
								+ Payment.class.getSimpleName()
								+ " o where (o.status = '"
								+ STATUS_OK + "' or o.status = '"
								+ STATUS_REGISTERED + "') "
								+ "and ((o.externalTxCode <> '"
								+ NOT_AVAILABLE + "' and o.externalTxCode <> '' "
								+ "and o.externalTxCode is not null) or (o.txType = 7)) "
								+ "and o.userUID = ?1 order by o.timestamp desc limit 0,1",
						userId);
		return list == null || list.size() == 0 ? null : (Payment) list.get(0);
	}

	public Payment getLatestDeferredPaymentForUser(int userId) {
		List<?> list = getJpaTemplate().find(
						"select o from "
								+ Payment.class.getSimpleName()
								+ " o where (o.status = '" 
								+ STATUS_OK + "' or o.status = '" 
								+ STATUS_REGISTERED + "') "
								+ "and o.externalTxCode <> '" 
								+ NOT_AVAILABLE + "' and o.externalTxCode <> '' "
								+ "and o.externalTxCode is not null "
								+ "and o.txType = 8 and o.userUID = ?1 "
								+ "order by o.timestamp desc limit 0,1",
						userId);
		return list == null || list.size() == 0 ? null : (Payment) list.get(0);
	}

	public byte getUserGroupByCommunity(String communityName) {
		if (communityName == null)
			throw new PersistenceException(
					"The parameter communityName is null");
		return (Byte) getJpaTemplate().find(
				"select o.i from UserGroup o where o.communityId = "
						+ "(select oo.id from "+ Community.class.getSimpleName()
						+ " oo where name = ?1)",
				communityName).get(0);
	}
	
	public List<PaymentPolicy> getPaymentPoliciesForPasseadOperatorOrOperatorIs0(
			String communityName, String paymentType, int operator) {
		if (communityName == null)
			throw new PersistenceException(
					"The parameter communityName is null");
		if (paymentType == null)
			throw new PersistenceException("The parameter paymentType is null");
		
		 int communityId = Community.getMapAsNames().get(communityName).getId();
		
		List<PaymentPolicy> paymentPolicies = getJpaTemplate()
				.find(
						"select paymentPolicy from "
								+ PaymentPolicy.class.getSimpleName()
								+ " paymentPolicy where paymentPolicy.communityId = ?1"
								+ " and paymentPolicy.paymentType=?3"
								+ " and (paymentPolicy.operator = ?2 or paymentPolicy.operator = 0)",
						communityId, operator, paymentType);
		return paymentPolicies;
	}

	public PaymentPolicy getPaymentPolicyForUser(
			String communityName, String paymentType, int operator) {
		if (communityName == null)
			throw new PersistenceException(
					"The parameter communityName is null");
		if (paymentType == null)
			throw new PersistenceException(
					"The parameter paymentType is null");
		List<PaymentPolicy> paymentPolicies=getJpaTemplate().find(
				"select o from " + PaymentPolicy.class.getSimpleName()
				+ " o where o.communityId = "
				+ "(select oo.id from " + Community.class.getSimpleName()
				+ " oo where name = ?1) and "
				+ "o.paymentType = ?2 and  "
						+ "((o.operator = ?3 and o.paymentType='"
						+ PaymentType.PREMIUM_USER
						+ "') or  (o.operator = 0 and o.paymentType<>'"
						+ PaymentType.PREMIUM_USER + "'))",
		communityName, paymentType, operator);
		int paymentPoliciesSize = paymentPolicies.size();
		if (paymentPoliciesSize == 0)
			throw new PersistenceException(
					"Could not find payment policy for communityName ["
							+ communityName + "], paymentType [" + paymentType
							+ "], operator [" + operator + "]");
		else if (paymentPoliciesSize > 1) throw new PersistenceException(
				"There are "+paymentPoliciesSize+" payment policies for communityName ["
				+ communityName + "], paymentType [" + paymentType
				+ "], operator [" + operator + "]");
		return paymentPolicies.get(0);
	}

	public String getCommunityNameByUserGroup(byte userGroup) {
		return ((Community) getJpaTemplate().find(
						"select o from "
								+ Community.class.getSimpleName()
								+ " o where o.id = "
								+ "(select oo.community from UserGroup oo where oo.i = ?1)",
						userGroup).get(0)).getName();
	}
	
	public User findByNameAndCommunity(String userName, String communityName) {
		if (userName == null)
			throw new PersistenceException("The parameter userName is null");
		if (communityName == null)
			throw new PersistenceException(
					"The parameter communityName is null");
		List<?> list = getJpaTemplate().find(
						"select user from "
								+ User.class.getSimpleName()
								+ " user where user.userName = ?1 and"
								+ " user.userGroupId=(select userGroup.i from "
								+ UserGroup.class.getSimpleName()
								+ " userGroup where userGroup.communityId=(select community.id from "
								+ Community.class.getSimpleName()
								+ " community where community.name=?2))",
						userName, communityName);
		int size = list.size();
		if (size == 0)
			return null;
		else if (size == 1)
			return (User) list.get(0);
		else
			throw new PersistenceException(
					MessageFormat.format(
							"There are {0} users with userName [{1}] and communityName [{2}]",
							size, userName, communityName));
	}

	public User findByFacebookAndCommunity(String facebookId, String communityName) throws DataAccessException {
		List<?> list = getJpaTemplate().find(
						"select user from "
								+ User.class.getSimpleName()
								+ " user where user.facebookId = ?1 and"
								+ " user.userGroupId=(select userGroup.i from "
								+ UserGroup.class.getSimpleName()
								+ " userGroup where userGroup.communityId=(select community.id from "
								+ Community.class.getSimpleName()
								+ " community where community.name=?2))",
								facebookId, communityName);
		int size = list.size();
		if (size == 0)
			return null;
		else if (size == 1)
			return (User) list.get(0);
		else
			throw new PersistenceException(
					MessageFormat.format(
							"There are {0} facebookIds [{1}] in community [{2}]",
							size, facebookId, communityName));
	}
	
	public boolean userExists(String userName, String communityName) {
		Long userCount = (Long) getJpaTemplate()
				.find(
						"select count(*) from "
								+ User.class.getSimpleName()
								+ " user where user.userName = ?1 and user.userGroupId=(select userGroup.i from "
								+ UserGroup.class.getSimpleName()
								+ " userGroup where userGroup.communityId=(select community.id from "
								+ Community.class.getSimpleName()
								+ " community where community.name=?2))",
						userName, communityName).get(0);
		return userCount != 0;
	}
	
	public boolean facebookserExists(String facebookId, String communityName) {
		Long userCount = (Long) getJpaTemplate()
				.find(
						"select count(*) from "
								+ User.class.getSimpleName()
								+ " user where user.facebookId = ?1 and user.userGroupId=(select userGroup.i from "
								+ UserGroup.class.getSimpleName()
								+ " userGroup where userGroup.communityId=(select community.id from "
								+ Community.class.getSimpleName()
								+ " community where community.name=?2))",
								facebookId, communityName).get(0);
		return userCount != 0;
	}	
	

	public boolean userExists(String userName,
			String storedToken, String communityName) {
		Long userCount = (Long) getJpaTemplate()
				.find(
						"select count(*) from "
								+ User.class.getSimpleName()
								+ " user where user.userName = ?1 and user.token=?2 and"
								+ " user.userGroupId=(select userGroup.i from "
								+ UserGroup.class.getSimpleName()
								+ " userGroup where userGroup.communityId=(select community.id from "
								+ Community.class.getSimpleName()
								+ " community where community.name=?3))",
						userName, storedToken, communityName).get(0);
		return userCount != 0;
	}

	public Promotion getActivePromotion(byte userGroup) {
		List<?> list = getJpaTemplate().find(
				"select o from " + Promotion.class.getSimpleName() +
				" o where (o.numUsers < o.maxUsers or o.maxUsers=0) and o.startDate < ?1 " +
				"and o.endDate > ?1 and o.isActive = 1 and o.userGroup = ?2 and o.type = ?3", 
				getEpochSeconds(), userGroup, Promotion.ADD_SUBBALANCE_PROMOTION);
		return list == null || list.size() == 0 ? null : (Promotion) list.get(0);
	}

	public List<User> getListOfUsersForPaymentRetry() {
		return (List<User>) getJpaTemplate().executeFind(new JpaCallback<List>() {
			@Override
			public List doInJpa(EntityManager entityManager)
					throws javax.persistence.PersistenceException {
						Query query = entityManager
								.createQuery("select user from "
										+ User.class.getSimpleName()
										+ " user, "
										+ PayPalPayment.class.getSimpleName()
										+ " payPalPayment "
										+ " where user.paymentStatus = "
										+ PaymentStatusDao.getAWAITING_PSMS()
												.getId() + " or (user."
										+ User.Fields.id.name()
										+ " = payPalPayment."
										+ Payment.Fields.userUID.name()
										+ " and payPalPayment."
										+ Payment.Fields.status.name() + "='"
										+ AppConstants.STATUS_USER_CONFIRMED
										+ "' ) group by user."
										+ User.Fields.id.name());
                query.setFirstResult(0);
                query.setMaxResults(1000);
                List results = query.getResultList();
                return results;
			}
		});
		
//		return (List<User>) getJpaTemplate().find(
//				"select o from "
//						+ User.class.getSimpleName()
//						+ " o where o.paymentStatus = '" + PaymentStatusDao.getAWAITING_PSMS().getId()
//						+ "' order by o.status desc limit 0,1000");
	}

	@SuppressWarnings("unchecked")
	public List<User> getListOfUsersForWeeklyUpdate() {
		return (List<User>) getJpaTemplate().executeFind(new JpaCallback<List>() {
			@Override
			public List doInJpa(EntityManager entityManager) throws javax.persistence.PersistenceException {
				Query query = entityManager.createNamedQuery(User.NQ_GET_SUBSCRIBED_USERS);
					query.setParameter(1, getEpochSeconds());
	                query.setFirstResult(0);
	                query.setMaxResults(1000);
                List results = query.getResultList();
                return results;
			}
		});
	}

	public List<Integer> getOperatorsAccordingToPaymentPolicies(
			String communityName, String paymentType) {
		 int communityId = Community.getMapAsNames().get(communityName).getId();
			
			List<Integer> operatorIds = getJpaTemplate()
					.find(
							"select paymentPolicy.operatorId from "
									+ PaymentPolicy.class.getSimpleName()
									+ " paymentPolicy where paymentPolicy.communityId = ?1"
									+ " and paymentPolicy.paymentType=?2",
							communityId, paymentType);
		return operatorIds;
	}
	
	public User findUserTree(int userId) {
		LOGGER.debug("input parameters userId: [{}]", userId);
		List<User> users = getJpaTemplate().findByNamedQuery("User.findUserTree", userId);
		User user = null;
		if(users.size()==1) user = users.get(0);
		LOGGER.debug("Output parameter user=[{}]", user);
		return user;
	}
	
	@Transactional(propagation=Propagation.REQUIRED, readOnly=true)
	public User findUserById(int userId) {
		List<User> users = getJpaTemplate().findByNamedQuery(User.NQ_FIND_USER_BY_ID, userId);
		if (null!=users && users.size()>0)
			return users.get(0);
		return null;
	}

	/**
	 * Find user by his User.email and Community.rewriteURLParameter
	 * @param email email field of the User
	 * @param communityUrl rewriteURLParameter filed of the Community entity
	 * @return User or null if there were no users
	 */
	@Transactional(propagation=Propagation.REQUIRED, readOnly=true)
	public User findOne(String email, String communityUrl) {
		@SuppressWarnings("unchecked")
		List<User> users = getJpaTemplate().findByNamedQuery(User.NQ_GET_USER_BY_EMAIL_COMMUNITY_URL, email, communityUrl);
		if (users!=null && users.size()>0)
			return users.get(0);
		return null;
	}

	public boolean checkStoredToken(String deviceUID, byte userGroupId, String storedToken) {
		LOGGER.debug("input parameters deviceUID, userGroupId, storedToken: [{}], [{}], [{}]", new Object[]{deviceUID, userGroupId, storedToken});
		Long count = (Long) getJpaTemplate().findByNamedQuery(User.NQ_GET_USER_COUNT_BY_DEVICE_UID_GROUP_STOREDTOKEN, new Object[]{deviceUID, userGroupId, storedToken}).get(0);
		boolean isValid=(0!=count);
		LOGGER.debug("Output parameter isValid=[{}]", isValid);
		return isValid;
	}

	@SuppressWarnings("unchecked")
	public User findByDeviceUIDAndCommunityRedirectUrl(String deviceUID, String communityRedirectUrl) {
		LOGGER.debug("input parameters deviceUID, communityRedirectUrl: [{}], [{}]", deviceUID, communityRedirectUrl);
		
		User user = null;
		List<User> users = getJpaTemplate().findByNamedQuery(User.NQ_GET_USER_BY_DEVICE_UID_COMMUNITY_REDIRECT_URL, new Object[]{deviceUID, communityRedirectUrl});
		if (users.size() != 0)
			user = users.get(0);
		
		LOGGER.debug("Output parameter user=[{}]", user);
		return user;
	}

}
