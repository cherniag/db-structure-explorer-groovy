package mobi.nowtechnologies.server.persistence.repository;

import mobi.nowtechnologies.server.persistence.domain.Artist;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * @author Alexander Kolpakov (akolpakov)
 */
public interface ArtistRepository extends JpaRepository<Artist, Integer> {

    @Query(value = "select a from Artist a where a.name = :name")
    Artist getByName(@Param("name") String name);

    @Query(value = "select a from Artist a where a.realName = :realName")
    List<Artist> getByRealName(@Param("realName") String name, Pageable pageable);

    @Query(value = "select a from Artist a where a.realName = :name or a.name = :name")
    List<Artist> getByNames(@Param("name") String name, Pageable pageable);
}
