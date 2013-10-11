package mobi.nowtechnologies.server.persistence.dao;

import mobi.nowtechnologies.common.dto.UserRegInfo.PaymentType;
import mobi.nowtechnologies.server.persistence.domain.*;
import mobi.nowtechnologies.server.shared.AppConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.orm.jpa.JpaCallback;
import org.springframework.orm.jpa.support.JpaDaoSupport;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.text.MessageFormat;
import java.util.List;

import static mobi.nowtechnologies.server.shared.AppConstants.*;
import static mobi.nowtechnologies.server.shared.Utils.getEpochSeconds;

/**
 * UserDao
 * @author Maksym Chernolevskyi (maksym)
 * @deprecated should be replaced on {@link mobi.nowtechnologies.server.persistence.repository.UserRepository}
 */
@Deprecated
public class UserDao extends JpaDaoSupport {
	private static final Logger LOGGER = LoggerFactory.getLogger(UserDao.class);

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

	public Promotion getActivePromotion(UserGroup userGroup) {
		List<?> list = getJpaTemplate().find(
				"select o from " + Promotion.class.getSimpleName() +
				" o where (o.numUsers < o.maxUsers or o.maxUsers=0) and o.startDate < ?1 " +
				"and o.endDate > ?1 and o.isActive = 1 and o.userGroup = ?2 and o.type = ?3", 
				getEpochSeconds(), userGroup, Promotion.ADD_SUBBALANCE_PROMOTION);
		return list == null || list.size() == 0 ? null : (Promotion) list.get(0);
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
