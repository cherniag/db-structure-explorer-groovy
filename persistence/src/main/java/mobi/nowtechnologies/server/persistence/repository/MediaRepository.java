package mobi.nowtechnologies.server.persistence.repository;

import mobi.nowtechnologies.server.persistence.domain.Media;

import java.util.Collection;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * @author Titov Mykhaylo (titov)
 * @author Alexander Kolpakov (akolpakov)
 */
public interface MediaRepository extends JpaRepository<Media, Integer> {

    @Query(
        value = "select media from Media media join FETCH media.artist artist join FETCH media.imageFileSmall imageFileSmall where media.title like :searchWords or media.isrc like :searchWords or " +
                "artist.name like :searchWords")
    List<Media> findMedias(@Param("searchWords") String searchWords);

    @Query(value = "select media from Media media " +
                   "join FETCH media.artist artist " +
                   "join FETCH media.imageFileSmall imageFileSmall " +
                   "left join FETCH media.label label " +
                   "where " +
                   "media.audioFile.fileType.i = :type " +
                   "and (media.title like :searchWords or media.isrc like :searchWords or artist.name like :searchWords)")
    List<Media> findMedias(@Param("searchWords") String searchWords, @Param("type") Byte type);

    @Query(value = "select media from Media media where media.isrc = :isrc")
    List<Media> findByIsrc(@Param("isrc") String isrc);

    @Query(value = "select media from Media media where media.isrc in :isrcs")
    List<Media> findByIsrcs(@Param("isrcs") Collection<String> isrcs);

    @Query("select media from ChartDetail chartDetail join chartDetail.media media left join media.artist artist where " +
           "chartDetail.chart.i=:chartId and chartDetail.publishTimeMillis=:publishTimeMillis " +
           "and (media.title like :searchWords escape '^' or media.isrc like :searchWords escape '^' or artist.name like :searchWords escape '^') " +
           "and media.i not in :excludedIds order by media.title")
    List<Media> findMediaByChartAndPublishTimeAndSearchWord(@Param("chartId") int chartId, @Param("publishTimeMillis") long publishTimeMillis, @Param("excludedIds") Collection<Integer> excludedIds,
                                                            @Param("searchWords") String searchWords, Pageable pageable);

    @Query("select media from ChartDetail chartDetail join chartDetail.media media left join media.artist artist where " +
           "chartDetail.chart.i=:chartId and chartDetail.publishTimeMillis=:publishTimeMillis " +
           "and (media.title like :searchWords escape '^' or media.isrc like :searchWords escape '^' or artist.name like :searchWords escape '^') " +
           "order by media.title")
    List<Media> findMediaByChartAndPublishTimeAndSearchWord(@Param("chartId") int chartId, @Param("publishTimeMillis") long publishTimeMillis, @Param("searchWords") String searchWords,
                                                            Pageable pageable);


    @Query("select media from ChartDetail chartDetail " +
           "join chartDetail.media media " +
           "where " +
           "chartDetail.chart.i=:chartId " +
           "and chartDetail.publishTimeMillis=:publishTimeMillis " +
           "and media.i in :mediaIds")
    List<Media> findMediaByChartAndPublishTimeAndMediaIds(@Param("chartId") int chartId, @Param("publishTimeMillis") long publishTimeMillis, @Param("mediaIds") Collection<Integer> mediaIds);

    @Query(value = "select media from Media media where media.trackId = ?1")
    Media findByTrackId(Long trackId);

}
