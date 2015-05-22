
package mobi.nowtechnologies.server.persistence.repository;

import mobi.nowtechnologies.server.persistence.domain.MediaLogType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MediaLogTypeRepository extends JpaRepository<MediaLogType, Integer> {
    @Query(value = "select m from MediaLogType m where m.name = :name")
    MediaLogType findByName(@Param("name") String name);
}
