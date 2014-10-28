package mobi.nowtechnologies.server.persistence.repository;

import mobi.nowtechnologies.server.persistence.domain.Label;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * @author Titov Mykhaylo (titov) on 25.06.2014.
 */
public interface LabelRepository extends JpaRepository<Label, Long> {

    @Query(value = "select a from Label a where a.name = ?1")
    Label findByName(String name);
}
