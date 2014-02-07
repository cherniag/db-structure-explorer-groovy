package mobi.nowtechnologies.server.persistence.repository;

import mobi.nowtechnologies.server.persistence.domain.FBDetails;
import mobi.nowtechnologies.server.persistence.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

/**
 * Created by oar on 2/7/14.
 */
public interface FBDetailsRepository extends JpaRepository<FBDetails, Long> {

    @Query(value="select fbDetails from FBDetails fbDetails where fbDetails.user=?1")
    FBDetails findForUser(User user);

    @Modifying
    @Query(value="delete  from FBDetails fbDetails where fbDetails.user=?1")
    int deleteForUser(User user);
}
