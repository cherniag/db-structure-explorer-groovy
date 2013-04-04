package mobi.nowtechnologies.server.persistence.repository;

import mobi.nowtechnologies.server.persistence.domain.UserLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserLogRepository extends JpaRepository<UserLog, Integer> {

    @Query(value = "select userLog from UserLog userLog " +
            " where userLog.userId = ?1 " +
            " group by userLog.userId " +
            " having min(userLog.last_update) = userLog.last_update")
    UserLog findByUser(int id);
}
