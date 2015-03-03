package mobi.nowtechnologies.server.persistence.repository;

import mobi.nowtechnologies.server.persistence.domain.streamzine.badge.Resolution;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ResolutionRepository extends JpaRepository<Resolution, Long> {

    @Query(value = "select r from Resolution r order by r.deviceType, r.width, r.height desc")
    List<Resolution> findAllSorted();

    @Query(value = "select r from Resolution r where r.deviceType=:deviceType and r.width=:width and r.height=:height")
    Resolution find(@Param("deviceType") String deviceType, @Param("width") int width, @Param("height") int height);
}
