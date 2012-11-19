package mobi.nowtechnologies.server.persistence.repository;

import mobi.nowtechnologies.server.persistence.domain.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * @author Alexander Kolpakov (akolpakov)
 * 
 */
public interface ItemRepository extends JpaRepository<Item, Integer> {
	
	@Query("select distinct item from Item item where item.title like ? order by item.title asc")
	List<Item> findByTitle(String title);
	
	@Query("select item from Item item where item.i in :ids")
	List<Item> findByIds(@Param("ids") List<Integer> ids);
}