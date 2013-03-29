package mobi.nowtechnologies.server.persistence.repository;

import mobi.nowtechnologies.server.persistence.domain.Drm;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * 
 * @author Alexander Kolpakov (akolpakov)
 *
 */
public interface DrmRepository extends JpaRepository<Drm, Integer> {
	@Query(value = "select distinct d from Drm d where d.user.id = :userId and d.media.id = :mediaId")
	Drm findByUserAndMedia(@Param("userId")Integer userId, @Param("mediaId")Integer mediaId);
}
