package mobi.nowtechnologies.server.persistence.repository;

import mobi.nowtechnologies.server.persistence.domain.AppsFlyerData;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Author: Gennadii Cherniaiev Date: 11/10/2014
 */

public interface AppsFlyerDataRepository extends JpaRepository<AppsFlyerData, Long> {

    @Query("select afd from AppsFlyerData afd where afd.userId = :userId")
    AppsFlyerData findDataByUserId(@Param("userId") int userId);
}
