package mobi.nowtechnologies.server.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import mobi.nowtechnologies.server.persistence.dao.MediaDao;
import mobi.nowtechnologies.server.persistence.domain.Chart;
import mobi.nowtechnologies.server.persistence.domain.Media;
import mobi.nowtechnologies.server.persistence.domain.MediaLogType;
import mobi.nowtechnologies.server.persistence.repository.ChartDetailRepository;
import mobi.nowtechnologies.server.persistence.repository.ChartRepository;
import mobi.nowtechnologies.server.persistence.repository.MediaRepository;
import mobi.nowtechnologies.server.service.exception.ServiceException;
import mobi.nowtechnologies.server.trackrepo.enums.FileType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Alexander Kolpakov (akolpakov)
 * @author Titov Mykhaylo (titov)
 * 
 */
public class MediaService {
	private static final Logger LOGGER = LoggerFactory.getLogger(MediaService.class);
    private static final PageRequest PAGE_REQUEST_50 = new PageRequest(0, 50);

	private MediaLogService mediaLogService;
	private MediaLogTypeService mediaLogTypeService;
	private MediaDao mediaDao;
	private EntityService entityService;
	private MediaRepository mediaRepository;
    private ChartRepository chartRepository;
    private ChartDetailRepository chartDetailRepository;

	public void setMediaLogService(MediaLogService mediaLogService) {
		this.mediaLogService = mediaLogService;
	}
	public void setMediaLogTypeService(MediaLogTypeService aMediaLogTypeService) {
		this.mediaLogTypeService = aMediaLogTypeService;
	}

	public void setMediaDao(MediaDao aMediaDao) {
		this.mediaDao = aMediaDao;
	}

	public void setEntityService(EntityService entityService) {
		this.entityService = entityService;
	}
	
	public void setMediaRepository(MediaRepository mediaRepository) {
		this.mediaRepository = mediaRepository;
	}

    public void setChartRepository(ChartRepository chartRepository) {
        this.chartRepository = chartRepository;
    }

    public void setChartDetailRepository(ChartDetailRepository chartDetailRepository) {
        this.chartDetailRepository = chartDetailRepository;
    }

    @Transactional(propagation=Propagation.REQUIRED)
	public void logMediaEvent(int userId, Media media, String mediaLogType) {
		final Map<String,MediaLogType> MEDIA_LOG_TYPES = mediaLogTypeService.getMediaLogTypes();
		mediaLogService.logMediaEvent(userId, media, (byte) MEDIA_LOG_TYPES.get(mediaLogType).getI());
	}

	public Media findByIsrc(String mediaIsrc) {
		if (mediaIsrc == null)
			throw new ServiceException("The parameter mediaIsrc is null");
		return entityService.findByProperty(Media.class, Media.Fields.isrc.toString(), mediaIsrc);
	}
	
	public void conditionalUpdateByUserAndMedia(int userId, int mediaId) {
		mediaDao.conditionalUpdateByUserAndMedia(userId, mediaId);
	}
	
	@Transactional(readOnly = true)
	public List<Media> getMedias(String searchWords) {
        return mediaRepository.getMedias("%"+searchWords+"%");
	}

    @Transactional(readOnly = true)
    public List<Media> getVideo(String searchWords) {
        return mediaRepository.getMedias("%"+searchWords+"%", FileType.VIDEO.getIdAsByte());
    }

    @Transactional(readOnly = true)
    public List<Media> getMusic(String searchWords) {
        return mediaRepository.getMedias("%"+searchWords+"%", FileType.MOBILE_AUDIO.getIdAsByte());
    }
	
	@Transactional(readOnly = true)
	public Media findById(Integer id) {
		LOGGER.debug("input parameters id: [{}]", id);
		
		Media media = mediaRepository.findOne(id);
		
		LOGGER.info("Output parameter media=[{}]", media);
		return media;
	}

    @Transactional(readOnly = true)
    public List<Media> getMediasForAvailableCommunityCharts(String communityRewriteUrl, long timeMillis, String searchWord, Collection<String> excludedIsrcs){
        LOGGER.debug("input parameters communityRewriteUrl [{}] timeMillis [{}] searchWord [{}] excludedIsrcs [{}]", communityRewriteUrl, timeMillis, searchWord, excludedIsrcs);
        List<Media> medias = Lists.newArrayList();

        List<Chart> charts = chartRepository.getByCommunityURL(communityRewriteUrl);
        for (Chart chart : charts) {
            Long latestPublishDate = chartDetailRepository.findNearestLatestPublishDate(timeMillis, chart.getI());
            if (latestPublishDate != null){
                medias.addAll(findMedias(searchWord, excludedIsrcs, chart, latestPublishDate));
            }
        }
        LOGGER.debug("Output parameter mediaList [{}]", medias);
        return medias;
    }

    @Transactional(readOnly = true)
    public Set<Media> getMediasByChartAndPublishTimeAndMediaIsrcs(String communityRewriteUrl, long timeMillis, Collection<String> isrcs){
        LOGGER.debug("input parameters communityRewriteUrl [{}] timeMillis [{}] isrcs [{}]", communityRewriteUrl, timeMillis, isrcs);
        Set<Media> medias = Sets.newHashSet();
        List<Chart> charts = chartRepository.getByCommunityURL(communityRewriteUrl);
        for (Chart chart : charts) {
            Long latestPublishDate = chartDetailRepository.findNearestLatestPublishDate(timeMillis, chart.getI());
            if (latestPublishDate != null){
                medias.addAll(
                        mediaRepository.findMediaByChartAndPublishTimeAndMediaIsrcs(chart.getI(), latestPublishDate, isrcs)
                );
            }
        }
        LOGGER.debug("Output parameter mediaList [{}]", medias);
        return medias;
    }

    private List<Media> findMedias(String searchWord, Collection<String> excludedIsrcs, Chart chart, Long latestPublishDate) {
        final String searchWordsLike = "%" + searchWord + "%";

        if(excludedIsrcs!=null && !excludedIsrcs.isEmpty()){
            return mediaRepository.findMediaByChartAndPublishTimeAndSearchWord(chart.getI(), latestPublishDate, excludedIsrcs, searchWordsLike, PAGE_REQUEST_50);
        }else{
            return mediaRepository.findMediaByChartAndPublishTimeAndSearchWord(chart.getI(), latestPublishDate, searchWordsLike, PAGE_REQUEST_50);
        }
    }

}
