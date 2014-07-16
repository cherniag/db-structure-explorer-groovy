package mobi.nowtechnologies.server.persistence.repository;

import mobi.nowtechnologies.server.persistence.domain.streamzine.FilenameAlias;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface FilenameAliasRepository extends JpaRepository<FilenameAlias, Long> {
    @Query("select f from FilenameAlias f where f.domain=?1 order by f.fileName")
    List<FilenameAlias> findAllByDomain(FilenameAlias.Domain domain);

    @Query("select f from FilenameAlias f where f.alias=?1")
    FilenameAlias findByAlias(String alias);
}
