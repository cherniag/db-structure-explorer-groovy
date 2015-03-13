package mobi.nowtechnologies.server.persistence.repository;

import mobi.nowtechnologies.server.persistence.domain.referral.UserReferralsSnapshot;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Created by zam on 12/17/2014.
 */
public interface UserReferralsSnapshotRepository extends JpaRepository<UserReferralsSnapshot, Integer> {

    @Query("select snapshot from UserReferralsSnapshot snapshot where snapshot.userId=:userId")
    UserReferralsSnapshot findByUserId(@Param("userId") int userId);
}
