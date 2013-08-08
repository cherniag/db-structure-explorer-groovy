package mobi.nowtechnologies.server.persistence.repository;

import java.math.BigDecimal;
import java.util.List;

import javax.persistence.QueryHint;

import mobi.nowtechnologies.server.persistence.domain.User;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

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
	
	@Query(value = "select u from User u "+
			"join u.currentPaymentDetails pd "+
			"join u.userGroup ug "+
			"join ug.community c "+
			"where "+
			"((c.rewriteUrlParameter!='o2' and u.subBalance=0 and u.nextSubPayment<=?1) " +
			"or (c.rewriteUrlParameter='o2' and u.nextSubPayment<=(?1+86400) and ( u.provider<>'o2' or (u.provider='o2' and ((u.segment='CONSUMER' and TYPE(pd) = O2PSMSPaymentDetails) or u.segment='BUSINESS') )  ) )) "+
			"and (pd.lastPaymentStatus='NONE' or  pd.lastPaymentStatus='SUCCESSFUL') "+
			"and pd.activated=true "+
			"and u.lastDeviceLogin!=0")
	@QueryHints(value={ @QueryHint(name = "org.hibernate.cacheMode", value = "IGNORE") })
	List<User> getUsersForPendingPayment(int epochSeconds);
	
    @Query(value="select u from User u "
    		+ "join u.currentPaymentDetails pd "
    		+ "join u.userGroup ug "
			+ "join ug.community c "
    		+ "where "
    		+ "(pd.lastPaymentStatus='ERROR' or pd.lastPaymentStatus='EXTERNAL_ERROR') "
    		+ "and (pd.madeRetries!=pd.retriesOnError or u.nextSubPayment<=?1) "
    		+ "and pd.activated=true "
    		+ "and u.lastDeviceLogin!=0")
    @QueryHints(value={ @QueryHint(name = "org.hibernate.cacheMode", value = "IGNORE") })
	List<User> getUsersForRetryPayment(int epochSeconds);
	
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

	@Query("select u from User u " +
			"join u.currentPaymentDetails pd " +
			"where " +
			"TYPE(pd)=O2PSMSPaymentDetails " +
			"and pd.activated=true "+
			"and u.status=10 " +
			"and u.nextSubPayment<=?1+172800 " +
			"and u.nextSubPayment>?1 " +
			"and u.lastBefore48SmsMillis/1000+172800<u.nextSubPayment"
			)
	List<User> findBefore48hExpireUsers(int epochSeconds, Pageable pageable);

	@Query("select u from User u " +
			"where u.status=10 " +
			"and u.nextSubPayment<?1")
	List<User> getListOfUsersForWeeklyUpdate(int epochSeconds, Pageable pageable);

	@Modifying
	@Query(value="update User user " +
			"set " +
			"user.lastBefore48SmsMillis=:lastBefore48SmsMillis " +
			"where " +
			"user.id=:id")
	int updateLastBefore48SmsMillis(@Param("lastBefore48SmsMillis") long lastBefore48SmsMillis, @Param("id") int id);

    @Query(nativeQuery = true, value = "select u.i " +
            " from tb_users u " +
            " where u.activation_status = 'ACTIVATED' " +
            "      and u.userGroup = 10 " +
            "      and not exists (select log.user_id from user_logs log " +
            " where log.user_id = u.i " +
            "      and log.user_id is not null " +
            "      and log.type = 'UPDATE_O2_USER' " +
            "      and log.last_update >  ?1)")
    List<Integer> getUsersForUpdate(long after);

    @Query(value = "select u from User u " +
            " join u.userGroup ug " +
            " join ug.community c " +
            "  where c.rewriteUrlParameter = ?2" +
            " and u.userName = ?1 ")
    User findOne(String userName, String communityUrl);
}
