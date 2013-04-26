package mobi.nowtechnologies.server.persistence.repository;

import mobi.nowtechnologies.server.persistence.domain.UserLog;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserLogRepository extends JpaRepository<UserLog, Integer> {

    @Query(value = "select userLog from UserLog userLog " +
            " where userLog.userId = ?1 " +
            " group by userLog.userId " +
            " having min(userLog.last_update) = userLog.last_update")
    UserLog findByUser(int id);
    
    //day in millis date = millis_date/1000/60/60/24
    @Query(value = "select count(userLog) from UserLog userLog " +
    		" where userLog.phoneNumber = ?1 and userLog.last_update/86400000 = ?3 and userLog.description = ?2")
    Long countByPhoneNumberAndDay(String phoneNumber, String description, long dayOfDate);
}
