package mobi.nowtechnologies.server.persistence.repository;

import mobi.nowtechnologies.server.persistence.domain.Chart;
import mobi.nowtechnologies.server.persistence.domain.ChartDetail;
import mobi.nowtechnologies.server.persistence.domain.Community;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

/**
 * @author Titov Mykhaylo (titov)
 * 
 */
public interface ChartDetailRepository extends JpaRepository<ChartDetail, Integer> {

	@Query("select chartDetail.publishTimeMillis from ChartDetail chartDetail where chartDetail.chart.i=?1 group by chartDetail.publishTimeMillis")
	List<Long> getAllPublishTimeMillis(Integer chartId);

	@Query("select chartDetail from ChartDetail chartDetail join FETCH chartDetail.media media join FETCH media.artist artist join FETCH media.imageFileSmall imageFileSmall where chartDetail.chart.i=:chartId and chartDetail.publishTimeMillis=:publishTimeMillis order by chartDetail.position asc")
	List<ChartDetail> getActualChartItems(@Param("chartId") Integer chartId, @Param("publishTimeMillis") long publishTimeMillis);

	@Query("select chartDetail from ChartDetail chartDetail where chartDetail.chart.i=:chartId and chartDetail.publishTimeMillis=:publishTimeMillis order by chartDetail.position asc")
	List<ChartDetail> getAllActualChartDetails(@Param("chartId") Integer chartId, @Param("publishTimeMillis") long publishTimeMillis);

	@Query("select chartDetail from ChartDetail chartDetail join FETCH chartDetail.media media join FETCH media.artist artist join FETCH media.imageFileSmall imageFileSmall where chartDetail.chart.i=?1 and chartDetail.publishTimeMillis=?2 order by chartDetail.position asc")
	List<ChartDetail> getChartItemsByDate(Integer chartId, long publishTimeMillis);
	
	@Query("select chartDetail.media.isrc from ChartDetail chartDetail where chartDetail.chart.i=?1 and chartDetail.publishTimeMillis=?2 and chartDetail.locked = true")
	List<String> getLockedChartItemISRCByDate(Integer chartId, long publishTimeMillis);
	
	@Query("select chartDetail.i from ChartDetail chartDetail where chartDetail.chart.i=?1 and chartDetail.publishTimeMillis=?2 and chartDetail.position>?3 order by chartDetail.position asc")
	List<Integer> getIdsByDateAndPosition(Integer chartId, long publishTimeMillis, byte afterPosition);

	@Query("select count(chartDetail) from ChartDetail chartDetail where chartDetail.chart.i=?1 and chartDetail.publishTimeMillis=?2")
	long getCount(Integer chartId, long choosedPublishTimeMillis);

	@Query("select max(chartDetail.publishTimeMillis) from ChartDetail chartDetail where chartDetail.chart.i=?2 and chartDetail.publishTimeMillis<=?1")
	Long findNearestLatestPublishDate(long choosedPublishTimeMillis, Integer chartId);

	@Query("select chartDetail from ChartDetail chartDetail where chartDetail.chart.i=:chartId and chartDetail.publishTimeMillis=:publishTimeMillis order by chartDetail.position asc")
	List<ChartDetail> findByChartAndPublishTimeMillis(@Param("chartId") Integer chartId, @Param("publishTimeMillis") Long nearestLatestPublishTimeMillis);

	@Query("select max(chartDetail.position) from ChartDetail chartDetail where chartDetail.chart=?1 and chartDetail.publishTimeMillis=?2")
	Byte findMaxPosition(Chart chart, long publishTimeMillis);

	@Query("select chartDetail from ChartDetail chartDetail where chartDetail.i in :ids")
	List<ChartDetail> getByIds(@Param("ids") Set<Integer> ids);

	@Query("select chartDetail.channel from ChartDetail chartDetail where chartDetail.channel is not null group by chartDetail.channel")
	List<String> getAllChannels();

	@Query("select chartDetail from ChartDetail chartDetail join FETCH chartDetail.media media join FETCH media.artist artist join FETCH media.imageFileSmall imageFileSmall where chartDetail.i=?1")
	ChartDetail findById(Integer chartItemId);

	@Query("select chartDetail from ChartDetail chartDetail join FETCH chartDetail.chart chart join FETCH chartDetail.media media join FETCH chart.genre genre1 join FETCH media.artist artist join FETCH media.genre genre2 left join FETCH media.headerFile headerFile join FETCH media.audioFile audioFile join FETCH media.imageFIleLarge imageFileLarge join FETCH media.imageFileSmall imageFileSmall where chart.i=:chartId and chartDetail.publishTimeMillis=:publishTimeMillis order by chartDetail.position")
	List<ChartDetail> findChartDetailTreeForDrmUpdateByChartAndPublishTimeMillis(@Param("chartId") Integer chartId,
			@Param("publishTimeMillis") Long nearestLatestPublishTimeMillis);
	
	@Query("select chartDetail from ChartDetail chartDetail join FETCH chartDetail.chart chart join FETCH chartDetail.media media join FETCH chart.genre genre1 join FETCH media.artist artist join FETCH media.genre genre2 left join FETCH media.headerFile headerFile join FETCH media.audioFile audioFile join FETCH media.imageFIleLarge imageFileLarge join FETCH media.imageFileSmall imageFileSmall where chart.i=:chartId and chartDetail.publishTimeMillis=:publishTimeMillis and (chartDetail.locked is null or chartDetail.locked != true) order by chartDetail.position")
	List<ChartDetail> findNotLockedChartDetailTreeForDrmUpdateByChartAndPublishTimeMillis(@Param("chartId") Integer chartId,
			@Param("publishTimeMillis") Long nearestLatestPublishTimeMillis);
	
	@Query("select chartDetail from ChartDetail chartDetail where chartDetail.media is null and chartDetail.chart.i=:chartId and chartDetail.publishTimeMillis=:publishTimeMillis")
	ChartDetail findChartWithDetailsByChartAndPublishTimeMillis(@Param("chartId") Integer chartId,
			@Param("publishTimeMillis") Long nearestLatestPublishTimeMillis);

	@Query("select count(chartDetail) from ChartDetail chartDetail where chartDetail.media is not null and chartDetail.chart.i=:chartId and chartDetail.publishTimeMillis=:publishTimeMillis")
	Long countChartDetailTreeByChartAndPublishTimeMillis(@Param("chartId") Integer chartId,
			@Param("publishTimeMillis") Long nearestLatestPublishTimeMillis);
	
	@Query("select max(chartDetail.publishTimeMillis) from ChartDetail chartDetail where chartDetail.media is null and chartDetail.chart.i=?2 and chartDetail.publishTimeMillis<=?1")
	Long findNearestLatestChartPublishDate(long choosedPublishTimeMillis, Integer chartId);
	
	@Modifying
	@Query(value="update ChartDetail chartDetail " +
			"set " +
			"chartDetail.publishTimeMillis=:newPublishTimeMillis " +
			"where " +
			"chartDetail.publishTimeMillis=:oldPublishTimeMillis " +
			"and chartDetail.chart.i=:chartId")
	int updateChartItems(@Param("newPublishTimeMillis") long newPublishTimeMillis, @Param("oldPublishTimeMillis") long oldPublishTimeMillis, @Param("chartId") Integer chartId);

	@Query(value="select max(chartDetail.publishTimeMillis) "
			+ "from ChartDetail chartDetail "
			+ "join chartDetail.chart chart "
			+ "join chart.communities communities "
			+ "where "
			+ "communities=?2 "
			+ "and chartDetail.publishTimeMillis<=?1")
	Long findNearestLatestPublishDate(long choosedPublishTimeMillis, Community community);
}
