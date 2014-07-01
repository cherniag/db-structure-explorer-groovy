package mobi.nowtechnologies.server.persistence.repository;

import mobi.nowtechnologies.server.persistence.domain.Label;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author Titov Mykhaylo (titov) on 25.06.2014.
 */
public interface LabelRepository extends JpaRepository<Label, Byte> {
}
