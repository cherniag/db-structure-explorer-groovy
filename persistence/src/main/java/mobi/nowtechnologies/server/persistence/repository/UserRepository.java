package mobi.nowtechnologies.server.persistence.repository;

import mobi.nowtechnologies.server.persistence.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

import javax.persistence.QueryHint;

/**
 * @author Titov Mykhaylo (titov)
 *
 */
public interface UserRepository extends JpaRepository<User, Integer>{

	@Query(value="select user from User user " +
			"join user.userGroup userGroup " +
			"join userGroup.community community " +
			"join user.deviceType deviceType " +
			"where " +
			"UPPER (community.rewriteUrlParameter)=UPPER(?1) " +
			"and (user.displayName like ?2 " +
			"or user.userName like ?2 " +
			//"or user.subBalance=?2 " + doesn't work
			"or user.status=?2 " +
			"or deviceType.name like ?2 " +
			"or userGroup.name like ?2 " +
			"or user.ipAddress like ?2 " +
			"or user.facebookId like ?2 " +
			"or user.pin like ?2 " +
			"or user.deviceUID like ?2 " +
			"or user.deviceModel like ?2 " +
			")")
	List<User> findUser(String communityURL, String searchWords);
	
	@Query(value="select user from User user " +
			"join user.userGroup userGroup " +
			"join userGroup.community community " +
			"join user.currentPaymentDetails currentPaymentDetails " +
			"where " +
			"TYPE(currentPaymentDetails) = MigPaymentDetails "+
			"and currentPaymentDetails.activated=true "+
			"and UPPER (community.rewriteUrlParameter)=UPPER(?1) " +
			"and user.lastSuccesfullPaymentSmsSendingTimestampMillis<>0 " +
			"and (user.amountOfMoneyToUserNotification >= ?2 " +
			"or (?3 - user.lastSuccesfullPaymentSmsSendingTimestampMillis)>= ?4)")
	List<User> findActivePsmsUsers(String communityURL, BigDecimal amountOfMoneyToUserNotification, long currentTimeMillis, long deltaSuccesfullPaymentSmsSendingTimestampMillis);
	
	@Modifying
	@Query(value="update User user " +
			"set " +
			"user.amountOfMoneyToUserNotification=:amountOfMoneyToUserNotification " +
			", user.lastSuccesfullPaymentSmsSendingTimestampMillis=:lastSuccesfullPaymentSmsSendingTimestampMillis " +
			"where " +
			"user.id=:id")
	int updateFields(@Param("amountOfMoneyToUserNotification") BigDecimal amountOfMoneyToUserNotification, @Param("lastSuccesfullPaymentSmsSendingTimestampMillis") long lastSuccesfullPaymentSmsSendingTimestampMillis, @Param("id") int id);
	
	@Modifying
	@Query(value="update User user " +
			"set " +
			"user.token=:token " +
			"where " +
			"user.id=:id")
	int updateFields(@Param("token") String token, @Param("id") int id);
	
	@Modifying
	@Query(value="update User user " +
			"set " +
			"user.lastSuccesfullPaymentSmsSendingTimestampMillis=:lastSuccesfullPaymentSmsSendingTimestampMillis " +
			"where " +
			"user.id=:id")
	int updateFields(@Param("lastSuccesfullPaymentSmsSendingTimestampMillis") long lastSuccesfullPaymentSmsSendingTimestampMillis, @Param("id") int id);

	
	@Query(value="select u from User u " +
			"join u.currentPaymentDetails pd " +
			"join u.userGroup ug " +
			"join ug.community c " +
			"where " +
			"((c.rewriteUrlParameter not like'o2' " +
			"and u.subBalance=0) " +
			"or (c.rewriteUrlParameter like 'o2' " +
			"and u.provider<>'o2' " +
			"and (u.nextSubPayment<=?1+86400))) " +
			"and (pd.lastPaymentStatus='NONE' " +
			"or  pd.lastPaymentStatus='SUCCESSFUL') " +
			"and pd.activated=true " +
			"and u.lastDeviceLogin!=0")
	@QueryHints(value={ @QueryHint(name = "org.hibernate.cacheMode", value = "IGNORE") })
	List<User> getUsersForPendingPayment(int epochSeconds);
	
//	@Query(value="select u from User u " +
//			"join u.currentPaymentDetails as pd " +
//			"where u.subBalance=0 " +
//			"and (pd.lastPaymentStatus='NONE' " +
//			"or  pd.lastPaymentStatus='SUCCESSFUL') " +
//			"and pd.activated=true and " +
//			"u.lastDeviceLogin!=0")
//	@QueryHints(value={ @QueryHint(name = "org.hibernate.cacheMode", value = "IGNORE") })
//	List<User> getUsersForPendingPayment();
	
	@Query(value="select u from User u " +
			"join u.userGroup ug " +
			"join ug.community c " +
			"where " +
			"u<>?1 "+
			"and u.nextSubPayment<>?2 " +
			"and u.appStoreOriginalTransactionId=?3 " +
			"and u.currentPaymentDetails is NULL " +
			"and c.rewriteUrlParameter = 'o2' " +
			"and u.provider<>'o2' ")
	List<User> findUsersForItunesInAppSubscription(User user, int nextSubPayment, String appStoreOriginalTransactionId);
}
