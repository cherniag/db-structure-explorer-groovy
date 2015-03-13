package mobi.nowtechnologies.server.persistence.apptests.repository;

import mobi.nowtechnologies.server.persistence.apptests.domain.JobTriggerRequest;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * Author: Gennadii Cherniaiev Date: 8/29/2014
 */
public interface JobTriggerRequestRepository extends JpaRepository<JobTriggerRequest, Long> {

    @Query("select r from JobTriggerRequest r where r.executeTimestamp < ?1")
    List<JobTriggerRequest> findBefore(long timestamp);

}
