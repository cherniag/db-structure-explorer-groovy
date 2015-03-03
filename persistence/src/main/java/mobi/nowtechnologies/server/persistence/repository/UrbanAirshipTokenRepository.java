package mobi.nowtechnologies.server.persistence.repository;

import mobi.nowtechnologies.server.persistence.domain.UrbanAirshipToken;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Created by enes on 1/26/15.
 */
public interface UrbanAirshipTokenRepository extends JpaRepository<UrbanAirshipToken, Long> {

    @Query("select uat from UrbanAirshipToken uat where uat.user.id = :userId")
    UrbanAirshipToken findDataByUserId(@Param("userId") int userId);
}
