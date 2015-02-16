package mobi.nowtechnologies.server.persistence.repository;

import mobi.nowtechnologies.server.persistence.domain.PinCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;

/**
 * @author Anton Zemliankin
 */
public interface PinCodeRepository extends JpaRepository<PinCode, Integer> {

    @Query(value = "select p from PinCode p " +
            " where p.userId = ?1" +
            " and p.entered = 0 " +
            " and p.creationTime > ?2 " +
            " order by p.creationTime desc")
    List<PinCode> findPinCodesByUserAndCreationTime(Integer userId, Date creationTime);

    @Query(value="select count(p) from PinCode p " +
            " where p.userId = ?1" +
            " and p.creationTime > ?2 ")
    int countUserPinCodes(Integer userId, Date creationTime);
}
