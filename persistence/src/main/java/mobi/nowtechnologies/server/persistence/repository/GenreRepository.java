package mobi.nowtechnologies.server.persistence.repository;

import mobi.nowtechnologies.server.persistence.domain.Genre;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * @author Alexander Kolpakov (akolpakov)
 */
public interface GenreRepository extends JpaRepository<Genre, Integer> {

    @Query(value = "select g from Genre g where g.name = :name")
    Genre findByName(@Param("name") String name);
}
