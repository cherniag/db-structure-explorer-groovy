package mobi.nowtechnologies.server.persistence.repository;

import mobi.nowtechnologies.server.persistence.domain.AccountLog;
import mobi.nowtechnologies.server.shared.enums.TransactionType;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * @author Titov Mykhaylo (titov)
 */
public interface AccountLogRepository extends JpaRepository<AccountLog, Integer> {

    @Query("select accountLog from AccountLog accountLog " +
           "left join FETCH accountLog.submittedPayment submittedPayment " +
           "left join FETCH accountLog.media media " +
           "where accountLog.userId=:userId " +
           "order by accountLog.logTimestamp desc")
    List<AccountLog> findByUserId(@Param("userId") Integer userId);
}
