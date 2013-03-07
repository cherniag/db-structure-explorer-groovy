package mobi.nowtechnologies.server.persistence.repository;

import mobi.nowtechnologies.server.persistence.domain.User;

import mobi.nowtechnologies.server.persistence.domain.enums.SegmentType;
import mobi.nowtechnologies.server.shared.enums.Contract;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

import javax.persistence.QueryHint;

/**
 * @author Titov Mykhaylo (titov)
 *
 */
public interface UserRepository extends PagingAndSortingRepository<User, Integer>{

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
			"join u.status s " +
			"where " +
			"(c.rewriteUrlParameter!='o2' " +
			"and u.subBalance=0 " +
			"and (pd.lastPaymentStatus='NONE' " +
			"or pd.lastPaymentStatus='SUCCESSFUL')) " +
			"or (c.rewriteUrlParameter='o2' " +
			"and (u.provider='o2' " +
			"and u.segment='consumer' " +
			"and u.contract='PAYG' " +
			"and TYPE(pd) = O2PSMSPaymentDetails " +
			"and pd.lastPaymentStatus<>'AWAITING' "+
			"and (s.name='LIMITED' or ((u.nextSubPayment<=?1 and u.lastPaymentTryMillis<=u.nextSubPayment*1000) or  (u.nextSubPayment+86400<=?1 and u.lastPaymentTryMillis<=u.nextSubPayment*1000+86400000 and u.lastPaymentTryMillis>=u.nextSubPayment*1000) or (u.nextSubPayment+172800<=?1 and u.lastPaymentTryMillis<=u.nextSubPayment*1000+172800000)  ))) " +
			"or (u.provider<>'o2' and u.nextSubPayment<=?1+86400 " +
			"and (pd.lastPaymentStatus='NONE' " +
			"or pd.lastPaymentStatus='SUCCESSFUL'))) " +
			"and pd.activated=true " +
			"and u.lastDeviceLogin!=0")
	@QueryHints(value={ @QueryHint(name = "org.hibernate.cacheMode", value = "IGNORE") })
	List<User> getUsersForPendingPayment(int epochSeconds);
	
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

	@Query("select u from User u where u.status=10 and u.nextSubPayment<?1 and lower(u.provider) = ?2 and u.segment in ?3 and u.contract in ?4")
	List<User> findBefore48hExpireUsers(int epochSeconds, String provider, SegmentType segment, Contract contracts);

	@Query("select u from User u " +
			"left join u.currentPaymentDetails pd " +
			"where u.status=10 " +
			"and " +
			"(" +
			"((pd is null or TYPE(pd)<> O2PSMSPaymentDetails) and u.nextSubPayment<?1) " +
			"or " +
			"(u.lastSubscribedPaymentSystem='o2Psms' and (u.nextSubPayment+u.deactivatedO2PSMSGraceCreditMillis/1000)<?1 and (pd is NULL or pd.activated=false))" +
			")")
	List<User> getListOfUsersForWeeklyUpdate(int epochSeconds, Pageable pageable);
}
