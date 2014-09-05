package mobi.nowtechnologies.server.persistence.repository;

import mobi.nowtechnologies.server.persistence.domain.streamzine.FilenameAlias;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FilenameAliasRepository extends JpaRepository<FilenameAlias, Long> {
}
