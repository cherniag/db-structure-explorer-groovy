package mobi.nowtechnologies.server.persistence.repository;

import mobi.nowtechnologies.server.persistence.domain.Drm;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

// @author Alexander Kolpakov (akolpakov)
public interface DrmRepository extends JpaRepository<Drm, Integer> {

    @Query(value = "select distinct d from Drm d where d.userId = :userId and d.mediaId = :mediaId")
    Drm findByUserAndMedia(@Param("userId") Integer userId, @Param("mediaId") Integer mediaId);

    @Modifying
    @Query(value = "update Drm drm " +
                   "set drm.timestamp = :timestamp " +
                   "where drm.userId = :userId " +
                   "and drm.mediaId = :mediaId " +
                   "and drm.timestamp = 0")
    int updateByUserAndMedia(@Param("userId") Integer userId, @Param("mediaId") Integer mediaId, @Param("timestamp") Integer timestamp);
}
