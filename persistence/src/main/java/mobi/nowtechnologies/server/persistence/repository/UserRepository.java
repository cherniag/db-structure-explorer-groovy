package mobi.nowtechnologies.server.persistence.repository;

import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.UserGroup;

import javax.persistence.QueryHint;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;

/**
 * @author Titov Mykhaylo (titov)
 */
public interface UserRepository extends JpaRepository<User, Integer> {

    @Query(value = "select user from User user " +
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

    @Query(value = "select user from User user " +
                   "join user.userGroup userGroup " +
                   "join userGroup.community community " +
                   "join user.currentPaymentDetails currentPaymentDetails " +
                   "where " +
                   "TYPE(currentPaymentDetails) = MigPaymentDetails " +
                   "and currentPaymentDetails.activated=true " +
                   "and UPPER (community.rewriteUrlParameter)=UPPER(?1) " +
                   "and user.lastSuccesfullPaymentSmsSendingTimestampMillis<>0 " +
                   "and (user.amountOfMoneyToUserNotification >= ?2 " +
                   "or (?3 - user.lastSuccesfullPaymentSmsSendingTimestampMillis)>= ?4)")
    List<User> findActivePsmsUsers(String communityURL, BigDecimal amountOfMoneyToUserNotification, long currentTimeMillis, long deltaSuccesfullPaymentSmsSendingTimestampMillis);

    @Modifying
    @Query(value = "update User user " +
                   "set " +
                   "user.amountOfMoneyToUserNotification=:amountOfMoneyToUserNotification " +
                   ", user.lastSuccesfullPaymentSmsSendingTimestampMillis=:lastSuccessfulPaymentSmsSendingTimestampMillis " +
                   "where " +
                   "user.id=:id")
    int updateFields(@Param("amountOfMoneyToUserNotification") BigDecimal amountOfMoneyToUserNotification,
                     @Param("lastSuccessfulPaymentSmsSendingTimestampMillis") long lastSuccessfulPaymentSmsSendingTimestampMillis, @Param("id") int id);

    @Modifying
    @Query(value = "update User user " +
                   "set " +
                   "user.token=:token " +
                   "where " +
                   "user.id=:id")
    int updateFields(@Param("token") String token, @Param("id") int id);

    @Modifying
    @Query(value = "update User user " +
                   "set " +
                   "user.lastSuccesfullPaymentSmsSendingTimestampMillis=:lastSuccessfulPaymentSmsSendingTimestampMillis " +
                   "where " +
                   "user.id=:id")
    int updateFields(@Param("lastSuccessfulPaymentSmsSendingTimestampMillis") long lastSuccessfulPaymentSmsSendingTimestampMillis, @Param("id") int id);

    @Query("select u from User u " +
           "join u.currentPaymentDetails pd " +
           "join u.userGroup ug " +
           "join ug.community c " +
           "join pd.paymentPolicy pp " +
           "where " +
           "u.subBalance=0 " +
           "and ((u.nextSubPayment<=?1)" +
           "or (u.nextSubPayment<=(?1+pp.advancedPaymentSeconds) and u.nextSubPayment>u.freeTrialExpiredMillis/1000)) " +
           "and pd.lastPaymentStatus in ('NONE', 'SUCCESSFUL') " +
           "and pd.activated=true " +
           "and u.lastDeviceLogin!=0")
    @QueryHints(@QueryHint(name = "org.hibernate.cacheMode", value = "IGNORE"))
    Page<User> findUsersForPendingPayment(int epochSeconds, Pageable pageable);

    @Query("select u from User u " +
           "join u.currentPaymentDetails pd " +
           "join u.userGroup ug " +
           "join ug.community c " +
           "join pd.paymentPolicy pp " +
           "where pd.retriesOnError>0 " +
           "and (pd.lastPaymentStatus='ERROR' or pd.lastPaymentStatus='EXTERNAL_ERROR') " + "and (" +
           " (u.nextSubPayment<=?1 and pd.madeAttempts=0 and pp.advancedPaymentSeconds=0)" +
           "   or (" +
           "         (pp.advancedPaymentSeconds>0 and pd.madeAttempts=0) " +
           "         or (u.nextSubPayment<=?1 and pd.madeAttempts=1 and pp.advancedPaymentSeconds>0) " +
           "         or ((u.nextSubPayment+pp.afterNextSubPaymentSeconds)<=?1 and pp.afterNextSubPaymentSeconds>0)" +
           "       )" +
           ") " + "and pd.activated=true " + "and u.lastDeviceLogin!=0")
    @QueryHints(@QueryHint(name = "org.hibernate.cacheMode", value = "IGNORE"))
    Page<User> findUsersForRetryPayment(int epochSeconds, Pageable pageable);

    @Query(value = "select u from User u " +
                   "join u.userGroup ug " +
                   "join ug.community c " +
                   "where " +
                   "u<>?1 " +
                   "and u.nextSubPayment<>?2 " +
                   "and u.appStoreOriginalTransactionId=?3 " +
                   "and u.currentPaymentDetails is NULL " +
                   "and c.rewriteUrlParameter = 'o2' " +
                   "and u.provider<>'O2' ")
    List<User> findUsersForItunesInAppSubscription(User user, int nextSubPayment, String appStoreOriginalTransactionId);

    @Query("select u from User u " +
           "join u.currentPaymentDetails pd " +
           "where " +
           "TYPE(pd)=O2PSMSPaymentDetails " +
           "and pd.activated=true " +
           "and u.status=10 " +
           "and u.nextSubPayment<=?1+172800 " +
           "and u.nextSubPayment>?1 " +
           "and u.lastBefore48SmsMillis/1000+172800<u.nextSubPayment " +
           "and u.segment='CONSUMER' " +
           "and u.contract='PAYG'")
    List<User> findBefore48hExpireUsers(int epochSeconds, Pageable pageable);

    @Query("select u from User u " +
           "where u.status=10 " +
           "and u.nextSubPayment<?1")
    List<User> findListOfUsersForWeeklyUpdate(int epochSeconds, Pageable pageable);

    @Modifying
    @Query(value = "update User user " +
                   "set " +
                   "user.lastBefore48SmsMillis=:lastBefore48SmsMillis " +
                   "where " +
                   "user.id=:id")
    int updateLastBefore48SmsMillis(@Param("lastBefore48SmsMillis") long lastBefore48SmsMillis, @Param("id") int id);

    // TODO rewrite uses jpql to avoid log tables and native queries using
    @Query(nativeQuery = true, value = "select u.i " +
                                       " from tb_users u " +
                                       " where u.activation_status = 'ACTIVATED' " +
                                       "      and u.userGroup = ?2 " +
                                       "      and not exists (select log.user_id from user_logs log " +
                                       " where log.user_id = u.i " +
                                       "      and log.user_id is not null " +
                                       "      and log.type = 'UPDATE_O2_USER' " +
                                       "      and log.last_update >  ?1)")
    List<Integer> findUsersForUpdate(long timeMillis, int userGroupId);

    @Modifying
    @Query(value = "update User user " +
                   "set " +
                   "user.idfa=:idfa " +
                   "where " +
                   "user.id=:id")
    int updateTokenDetails(@Param("id") int userId, @Param("idfa") String idfa);

    @Query(value = "select count(u) from User u " + "join u.userGroup ug " + "join ug.community c " + "where " + "u.pin=?1 " + "and u.mobile=?2 " + "and c=?3")
    long findByOtacMobileAndCommunity(String otac, String phoneNumber, Community community);

    @Query(value = "select u from User u " +
                   "join u.userGroup ug " +
                   "join ug.community c " +
                   "where " +
                   "u.userName = ?1 " +
                   "and u.deviceUID = ?1 " +
                   "and c = ?2")
    User findUserWithUserNameAsPassedDeviceUID(String deviceUID, Community community);

    @Query(value = "select u from User u " +
                   "join u.userGroup ug " +
                   "join ug.community c " +
                   "where " +
                   "u.deviceUID = ?1 " +
                   "and c = ?2")
    User findByDeviceUIDAndCommunity(String deviceUID, Community community);

    @Modifying
    @Query(value = "update User u " +
                   "set u.deviceUID=CONCAT(u.deviceUID,'_disabled_at_', CURRENT_TIMESTAMP()) " +
                   "where " +
                   "u.deviceUID = ?1 " +
                   "and u.userGroup=?2 ")
    int updateUserAccountWithSameDeviceAndDisableIt(String deviceUID, UserGroup userGroup);

    @Query(value = "select user from User user " +
                   "join FETCH user.deviceType deviceType " +
                   "join FETCH user.userGroup userGroup " +
                   "join FETCH userGroup.chart chart " +
                   "join FETCH userGroup.community community " +
                   "join FETCH community.appVersion appVersion " +
                   "join FETCH user.status status " +
                   "where " + "user.id=?1")
    User findUserTree(int userId);

    @Query(value = "select user from User user " + "join user.userGroup userGroup " + "join userGroup.community community " + "where " + "user.userName=?1 " + "and community=?2 " + "and user.id<>?3")
    User findByUserNameAndCommunityAndOtherThanPassedId(String userName, Community community, int userId);

    @Modifying
    @Query(value = "delete User u " +
                   "where " +
                   "u.id = ?1 ")
    int deleteUser(int userId);

    @Query(value = "select u from User u " + "where u.mobile = ?1 and u.deviceUID not like '%_disabled_at_%' and u.deviceUID not like '%_wipe'")
    List<User> findByMobile(String phoneNumber);

    @Query(value = "select user from User user join user.userGroup userGroup " +
                   "join userGroup.community community where " +
                   "user.userName like concat('%',:searchWord,'%') escape '^' and community.rewriteUrlParameter = :rewriteUrlParameter and user.activationStatus='ACTIVATED' order by user.userName ")
    List<User> findByUserNameAndCommunity(@Param("searchWord") String searchWord, @Param("rewriteUrlParameter") String rewriteUrlParameter, Pageable pageable);

    @Query(value = "select user from User user join user.userGroup userGroup " +
                   "join userGroup.community community where " +
                   "user.userName like concat('%',:searchWord,'%') escape '^' and community.rewriteUrlParameter = :rewriteUrlParameter and user.activationStatus='ACTIVATED' " +
                   "and user.userName not in (:excludedUserNames) order by user.userName ")
    List<User> findByUserNameAndCommunity(@Param("searchWord") String searchWord, @Param("rewriteUrlParameter") String rewriteUrlParameter, @Param("excludedUserNames") List<String> excludedUserNames,
                                          Pageable pageable);

    @Query(value = "select user from User user join user.userGroup userGroup  " +
                   "join userGroup.community community where" +
                   " user.userName in (:userNames) and community.rewriteUrlParameter = :communityRewriteUrl")
    List<User> findByUserNamesAndCommunity(@Param("userNames") List<String> userNames, @Param("communityRewriteUrl") String communityRewriteUrl);

    @Query(value = "select user from User user join user.userGroup userGroup  " +
                   "join userGroup.community community where" +
                   " user.userName = :userName and community.rewriteUrlParameter = :communityRewriteUrl")
    User findByUserNameAndCommunityUrl(@Param("userName") String userName, @Param("communityRewriteUrl") String communityRewriteUrl);
}
