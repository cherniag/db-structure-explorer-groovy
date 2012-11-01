package mobi.nowtechnologies.server.track_repo.repository;

import java.util.List;

import mobi.nowtechnologies.server.track_repo.domain.Track;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * 
 * @author Alexander Kolpakov (akolpakov)
 *
 */
public interface TrackRepository extends JpaRepository<Track, Long>, TrackRepositoryCustom {
			
	@Query("SELECT t FROM Track t WHERE t.artist = :artist")
	List<Track> listByArtist(@Param("artist")String artist);
	
	@Query("SELECT t FROM Track t WHERE t.isrc = :isrc")
	Track findByISRC(@Param("isrc")String isrc);
	
	@Query("SELECT t FROM Track t WHERE t.isrc = :isrc and t.productCode = :code and t.ingestor = :ingestor")
	Track findByKey(@Param("isrc")String isrc, @Param("code")String productCode, @Param("ingestor")String ingestor);
	
	@Query("SELECT t FROM Track t WHERE t.isrc = :isrc and t.ingestor = :ingestor")
	Track findByShortKey(@Param("isrc")String isrc, @Param("ingestor")String ingestor);

	@Query("SELECT t FROM Track t WHERE t.productCode = :code")
	Track findByProductCode(@Param("code")String productCode);
	
	@Query("SELECT t FROM Track t left join t.territories as ter WHERE t.isrc like :query or t.title like :query or t.artist like :query or t.ingestor like :query or ter.label like :query or ter.distributor like :query")
	List<Track> find(@Param("query")String query, Pageable pageable);
	
	@Query("SELECT count(t) FROM Track t left join t.territories as ter WHERE t.isrc like :query or t.title like :query or t.artist like :query or t.ingestor like :query or ter.label like :query or ter.distributor like :query")
	long count(@Param("query")String query);
	
	@Query("SELECT t FROM Track t left join fetch t.territories as ter left join fetch t.files as f WHERE t.id = :id")
	Track findOneWithCollections(@Param("id")Long id);
}