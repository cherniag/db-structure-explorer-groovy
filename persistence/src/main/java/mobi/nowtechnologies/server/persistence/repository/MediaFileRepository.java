package mobi.nowtechnologies.server.persistence.repository;

import mobi.nowtechnologies.server.persistence.domain.MediaFile;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * @author Alexander Kolpakov (akolpakov)
 */
public interface MediaFileRepository extends JpaRepository<MediaFile, Integer> {

    @Query(value = "select file from MediaFile file where file.filename = :name")
    MediaFile findByName(@Param("name") String name);
}
