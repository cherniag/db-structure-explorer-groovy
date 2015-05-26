package mobi.nowtechnologies.server.service;

import mobi.nowtechnologies.server.persistence.domain.Chart;
import mobi.nowtechnologies.server.persistence.domain.Media;
import mobi.nowtechnologies.server.persistence.repository.ChartDetailRepository;
import mobi.nowtechnologies.server.persistence.repository.ChartRepository;
import mobi.nowtechnologies.server.persistence.repository.MediaRepository;
import mobi.nowtechnologies.server.service.exception.ServiceException;
import mobi.nowtechnologies.server.trackrepo.enums.FileType;

import javax.annotation.Resource;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Alexander Kolpakov (akolpakov)
 * @author Titov Mykhaylo (titov)
 */
public class MediaService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MediaService.class);
    private static final PageRequest PAGE_REQUEST_50 = new PageRequest(0, 50);

    @Resource
    private MediaRepository mediaRepository;

    @Resource
    private ChartRepository chartRepository;

    @Resource
    private ChartDetailRepository chartDetailRepository;

    public Media findByIsrc(String mediaIsrc) {
        if (mediaIsrc == null) {
            throw new ServiceException("The parameter mediaIsrc is null");
        }

        List<Media> medias = mediaRepository.findByIsrc(mediaIsrc);

        return Iterables.getFirst(medias, null);
    }

    @Transactional(readOnly = true)
    public List<Media> getMedias(String searchWords) {
        return mediaRepository.findMedias("%" + searchWords + "%");
    }

    @Transactional(readOnly = true)
    public List<Media> getVideo(String searchWords) {
        return mediaRepository.findMedias("%" + searchWords + "%", FileType.VIDEO.getIdAsByte());
    }

    @Transactional(readOnly = true)
    public List<Media> getMusic(String searchWords) {
        return mediaRepository.findMedias("%" + searchWords + "%", FileType.MOBILE_AUDIO.getIdAsByte());
    }

    @Transactional(readOnly = true)
    public Set<Media> getMediasForAvailableCommunityCharts(String communityRewriteUrl, long timeMillis, String searchWord, Collection<Integer> excludedIds) {
        LOGGER.debug("input parameters communityRewriteUrl [{}] timeMillis [{}] searchWord [{}] excludedIds [{}]", communityRewriteUrl, timeMillis, searchWord, excludedIds);
        Set<Media> medias = Sets.newHashSet();

        List<Chart> charts = chartRepository.findByCommunityURL(communityRewriteUrl);
        for (Chart chart : charts) {
            Long latestPublishDate = chartDetailRepository.findNearestLatestPublishDate(timeMillis, chart.getI());
            if (latestPublishDate != null) {
                medias.addAll(findMedias(searchWord, excludedIds, chart, latestPublishDate));
            }
        }
        LOGGER.debug("Output parameter mediaList [{}]", medias);
        return medias;
    }

    @Transactional(readOnly = true)
    public Set<Media> getMediasByChartAndPublishTimeAndMediaIds(String communityRewriteUrl, long timeMillis, Collection<Integer> ids) {
        LOGGER.debug("input parameters communityRewriteUrl [{}] timeMillis [{}] ids [{}]", communityRewriteUrl, timeMillis, ids);
        Set<Media> medias = Sets.newHashSet();
        List<Chart> charts = chartRepository.findByCommunityURL(communityRewriteUrl);
        for (Chart chart : charts) {
            Long latestPublishDate = chartDetailRepository.findNearestLatestPublishDate(timeMillis, chart.getI());
            if (latestPublishDate != null) {
                medias.addAll(mediaRepository.findMediaByChartAndPublishTimeAndMediaIds(chart.getI(), latestPublishDate, ids));
            }
        }
        LOGGER.debug("Output parameter mediaList [{}]", medias);
        return medias;
    }

    private List<Media> findMedias(String searchWord, Collection<Integer> excludedIds, Chart chart, Long latestPublishDate) {
        final String searchWordsLike = "%" + searchWord + "%";

        if (excludedIds != null && !excludedIds.isEmpty()) {
            return mediaRepository.findMediaByChartAndPublishTimeAndSearchWord(chart.getI(), latestPublishDate, excludedIds, searchWordsLike, PAGE_REQUEST_50);
        } else {
            return mediaRepository.findMediaByChartAndPublishTimeAndSearchWord(chart.getI(), latestPublishDate, searchWordsLike, PAGE_REQUEST_50);
        }
    }

}
