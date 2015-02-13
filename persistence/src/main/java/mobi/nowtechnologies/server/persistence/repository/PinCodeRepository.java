package mobi.nowtechnologies.server.persistence.repository;

import mobi.nowtechnologies.server.persistence.domain.PinCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

/**
 * @author Anton Zemliankin
 */
public interface PinCodeRepository extends JpaRepository<PinCode, Integer> {

    /**
     * Returns the latest not expired pin code by user
     * @param userId
     * @param creationTime
     * @return
     */
    PinCode findTopByUserIdAndEnteredFalseAndCreationTimeGreaterThanOrderByCreationTimeDesc(Integer userId, Date creationTime);


    /**
     * Returns count of user pin codes starting from specified time
     * @param userId
     * @param creationTime
     * @return
     */
    Integer countByUserIdAndCreationTimeGreaterThan(Integer userId, Date creationTime);
}
