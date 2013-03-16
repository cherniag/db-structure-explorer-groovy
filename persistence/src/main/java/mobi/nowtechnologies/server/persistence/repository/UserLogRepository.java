package mobi.nowtechnologies.server.persistence.repository;

import mobi.nowtechnologies.server.persistence.domain.UserLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserLogRepository extends JpaRepository<UserLog, Integer> {

    @Query(value = "select userLog.userId from UserLog userLog  where userLog.last_update > ?1")
    List<Integer> findUpdatedUsers(long lastUpdate);
}