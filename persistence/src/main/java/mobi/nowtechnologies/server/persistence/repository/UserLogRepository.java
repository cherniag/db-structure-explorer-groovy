package mobi.nowtechnologies.server.persistence.repository;

import mobi.nowtechnologies.server.persistence.domain.UserLog;
import mobi.nowtechnologies.server.persistence.domain.enums.UserLogType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserLogRepository extends JpaRepository<UserLog, Integer> {

    @Query(value = "select userLog from UserLog userLog " +
            " where userLog.user.id = ?1 and userLog.userLogType = ?2" +
            " group by userLog.user " +
            " having min(userLog.logTimeMillis) = userLog.logTimeMillis")
    UserLog findByUser(int id, UserLogType userLogType);

    @Query(value = "select userLog from UserLog userLog " +
    		" where userLog.phoneNumber = ?1 and userLog.userLogType = ?2" +
    		" group by userLog.phoneNumber " +
    		" having min(userLog.logTimeMillis) = userLog.logTimeMillis")
    UserLog findByPhoneNumber(String phoneNumber, UserLogType userLogType);
    
    //day in millis date = millis_date/1000/60/60/24
    @Query(value = "select count(userLog) from UserLog userLog " +
    		" where userLog.phoneNumber = ?1 and abs(userLog.logTimeMillis/86400000 - ?3) < 1 and userLog.userLogType = ?2")
    Long countByPhoneNumberAndDay(String phoneNumber, UserLogType userLogType, long dayOfDate);
}
