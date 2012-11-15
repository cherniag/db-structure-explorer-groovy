package mobi.nowtechnologies.server.persistence.repository;

import mobi.nowtechnologies.server.persistence.domain.AbstractFilterWithCtiteria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * @author Titov Mykhaylo (titov)
 *
 */
public interface FilterWithCriteriaRepository  extends JpaRepository<AbstractFilterWithCtiteria, Byte> {

	@Query("select abstractFilterWithCtiteria from AbstractFilterWithCtiteria abstractFilterWithCtiteria where abstractFilterWithCtiteria.name in :names")
	List<AbstractFilterWithCtiteria> findByNames(@Param("names") List<String> names);

}
