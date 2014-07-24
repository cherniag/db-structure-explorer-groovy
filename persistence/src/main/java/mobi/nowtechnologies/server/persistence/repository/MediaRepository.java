package mobi.nowtechnologies.server.persistence.repository;

import mobi.nowtechnologies.server.persistence.domain.Media;
import org.springframework.data.domain.Pageable;
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

    @Query(value = "select media from Media media join FETCH media.artist artist join FETCH media.imageFileSmall imageFileSmall where media.audioFile.fileType.i = :type and (media.title like :searchWords or media.isrc like :searchWords or artist.name like :searchWords)")
    List<Media> getMedias(@Param("searchWords") String searchWords, @Param("type")Byte type);

	@Query(value = "select media from Media media where media.isrc = :isrc")
	Media getByIsrc(@Param("isrc")String isrc);

	@Query(value = "select media from Media media where media.isrc in :isrcs")
	List<Media> findByIsrcs(@Param("isrcs")Collection<String> isrcs);

    @Query("select media from ChartDetail chartDetail join chartDetail.media media left join media.artist artist where " +
            "chartDetail.chart.i=:chartId and chartDetail.publishTimeMillis=:publishTimeMillis " +
            "and (media.title like :searchWords escape '^' or media.isrc like :searchWords escape '^' or artist.name like :searchWords escape '^') " +
            "and media.isrc not in :excludedIsrcs order by media.title")
    List<Media> findMediaByChartAndPublishTimeAndSearchWord(@Param("chartId") int chartId,
                                                            @Param("publishTimeMillis") long publishTimeMillis,
                                                            @Param("excludedIsrcs") Collection<String> excludedIsrcs,
                                                            @Param("searchWords") String searchWords,
                                                            Pageable pageable);

    @Query("select media from ChartDetail chartDetail join chartDetail.media media left join media.artist artist where " +
            "chartDetail.chart.i=:chartId and chartDetail.publishTimeMillis=:publishTimeMillis " +
            "and (media.title like :searchWords escape '^' or media.isrc like :searchWords escape '^' or artist.name like :searchWords escape '^') " +
            "order by media.title")
    List<Media> findMediaByChartAndPublishTimeAndSearchWord(@Param("chartId") int chartId,
                                                            @Param("publishTimeMillis") long publishTimeMillis,
                                                            @Param("searchWords") String searchWords,
                                                            Pageable pageable);


    @Query("select media from ChartDetail chartDetail join chartDetail.media media where chartDetail.chart.i=:chartId and chartDetail.publishTimeMillis=:publishTimeMillis " +
            "and media.isrc in :mediaIsrcs")
    List<Media> findMediaByChartAndPublishTimeAndMediaIsrcs(@Param("chartId") int chartId,
                                                            @Param("publishTimeMillis") long publishTimeMillis,
                                                            @Param("mediaIsrcs") Collection<String> mediaIsrcs);

    @Query(value = "select media from Media media where media.isrc=?1")
    List<Media> findByIsrc(String mediaIsrc);
}
