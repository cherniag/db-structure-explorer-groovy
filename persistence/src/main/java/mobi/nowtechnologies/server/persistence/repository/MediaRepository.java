package mobi.nowtechnologies.server.persistence.repository;

import mobi.nowtechnologies.server.persistence.domain.Media;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

/**
 * @author Titov Mykhaylo (titov)
 * @author Alexander Kolpakov (akolpakov)
 */
public interface MediaRepository extends JpaRepository<Media, Integer> {

	@Query(value = "select media from Media media join FETCH media.artist artist join FETCH media.imageFileSmall imageFileSmall where media.title like :searchWords or media.isrc like :searchWords or artist.name like :searchWords")
	List<Media> getMedias(@Param("searchWords") String searchWords);

    @Query(value = "select media from Media media " +
            "join FETCH media.artist artist " +
            "join FETCH media.imageFileSmall imageFileSmall " +
            "join FETCH media.label label " +
            "where " +
            "media.audioFile.fileType.i = :type " +
            "and (media.title like :searchWords or media.isrc like :searchWords or artist.name like :searchWords)")
    List<Media> getMedias(@Param("searchWords") String searchWords, @Param("type")Byte type);

	@Query(value = "select media from Media media where media.isrc = :isrc")
	Media getByIsrc(@Param("isrc")String isrc);

	@Query(value = "select media from Media media where media.isrc in :isrcs")
	List<Media> findByIsrcs(@Param("isrcs")Collection<String> isrcs);

    @Query(value = "select media from Media media where media.trackId = ?1")
    Media findByTrackId(Long trackId);
}
