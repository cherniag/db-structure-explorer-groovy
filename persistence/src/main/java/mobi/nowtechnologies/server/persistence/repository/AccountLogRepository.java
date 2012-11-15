package mobi.nowtechnologies.server.persistence.repository;

import mobi.nowtechnologies.server.persistence.domain.AccountLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * @author Titov Mykhaylo (titov)
 *
 */
public interface AccountLogRepository extends JpaRepository<AccountLog, Integer> {

	@Query("select accountLog from AccountLog accountLog" +
			" left join FETCH accountLog.submittedPayment submittedPayment" +
			" left join FETCH accountLog.media media" +
			" where" +
			" accountLog.userId=?1" +
			" order by accountLog.logTimestamp desc")
	public List<AccountLog> findByUserId(Integer userId);
}
