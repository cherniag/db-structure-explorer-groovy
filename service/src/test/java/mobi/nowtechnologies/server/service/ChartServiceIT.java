package mobi.nowtechnologies.server.service;

import mobi.nowtechnologies.server.persistence.domain.*;
import mobi.nowtechnologies.server.persistence.repository.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

import static java.util.Arrays.asList;
import static mobi.nowtechnologies.server.shared.enums.ChartType.BASIC_CHART;
import static mobi.nowtechnologies.server.shared.enums.ChartType.FIFTH_CHART;
import static mobi.nowtechnologies.server.trackrepo.enums.FileType.IMAGE;
import static mobi.nowtechnologies.server.trackrepo.enums.FileType.MOBILE_AUDIO;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

// @author Titov Mykhaylo (titov) on 13.11.2014.
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath:/META-INF/service-test.xml", "classpath:/META-INF/dao-test.xml","/META-INF/shared.xml"})
@TransactionConfiguration(transactionManager = "persistence.TransactionManager", defaultRollback = true)
@Transactional
public class ChartServiceIT {

    @Resource(name = "service.ChartService") ChartService chartService;
    @Resource ChartDetailRepository chartDetailRepository;
    @Resource CommunityRepository communityRepository;
    @Resource ChartRepository chartRepository;
    @Resource GenreRepository genreRepository;
    @Resource MediaRepository mediaRepository;
    @Resource ArtistRepository artistRepository;
    @Resource MediaFileRepository mediaFileRepository;
    @Resource LabelRepository labelRepository;

    @Test
    public void shouldReturnDuplicatedMediaChartDetails(){
        //given
        String communityUrl = "g";
        long selectedTimeMillis = 3L;
        int excludedChartId = -1;

        Artist artist = artistRepository.save(new Artist().withName("artist name"));
        MediaFile audioMediaFile = mediaFileRepository.save(new MediaFile().withFileName("file name").withFileType(new FileType().withI(MOBILE_AUDIO.getIdAsByte()).withName(MOBILE_AUDIO.name())));
        MediaFile imageFileSmallMediaFile = mediaFileRepository.save(new MediaFile().withFileName("file name").withFileType(new FileType().withI(IMAGE.getIdAsByte()).withName(IMAGE.name())));
        MediaFile imageFileLargeMediaFile = mediaFileRepository.save(new MediaFile().withFileName("file name").withFileType(new FileType().withI(IMAGE.getIdAsByte()).withName(IMAGE.name())));
        Genre rockGenre = genreRepository.save(new Genre().withName("Rock"));
        Label label = labelRepository.findOne(1L);

        Media media = mediaRepository.save(new Media().withIsrc("isrc").withTitle("title").withArtist(artist).withAudioFile(audioMediaFile).withImageFileSmall(imageFileSmallMediaFile).withImageFileLarge(imageFileLargeMediaFile).withGenre(rockGenre).withLabel(label).withTrackId(666L));

        Community community = communityRepository.save(new Community().withRewriteUrl(communityUrl).withName(communityUrl));

        Chart chart1 = chartRepository.save(new Chart().withCommunity(community).withName("chart 1").withGenre(rockGenre).withChartType(BASIC_CHART));

        long chart1FirstUpdatePublishTimeMillis = 1L;
        chartDetailRepository.save(new ChartDetail().withChart(chart1).withPosition(1).withPublishTime(chart1FirstUpdatePublishTimeMillis));
        ChartDetail chartDetailOfFirstUpdateChart1 = chartDetailRepository.save(new ChartDetail().withChart(chart1).withPosition(1).withPublishTime(chart1FirstUpdatePublishTimeMillis).withMedia(media));

        long chart1SecondUpdatePublishTimeMillis = 5L;
        chartDetailRepository.save(new ChartDetail().withChart(chart1).withPosition(1).withPublishTime(chart1SecondUpdatePublishTimeMillis));
        ChartDetail chartDetailOfSecondUpdateChart1 = chartDetailRepository.save(new ChartDetail().withChart(chart1).withPosition(1).withPublishTime(chart1SecondUpdatePublishTimeMillis).withMedia(media));

        Chart chart2 = chartRepository.save(new Chart().withCommunity(community).withName("chart 2").withGenre(rockGenre).withChartType(FIFTH_CHART));

        long chart2FirstUpdatePublishTimeMillis = 2L;
        chartDetailRepository.save(new ChartDetail().withChart(chart2).withPosition(2).withPublishTime(chart2FirstUpdatePublishTimeMillis));
        ChartDetail chartDetailOfFirstUpdateChart2 = chartDetailRepository.save(new ChartDetail().withChart(chart2).withPosition(1).withPublishTime(chart2FirstUpdatePublishTimeMillis).withMedia(media));

        long chart2SecondUpdatePublishTimeMillis = 6L;
        chartDetailRepository.save(new ChartDetail().withChart(chart2).withPosition(2).withPublishTime(chart2SecondUpdatePublishTimeMillis));
        ChartDetail chartDetailOfSecondUpdateChart2 = chartDetailRepository.save(new ChartDetail().withChart(chart2).withPosition(1).withPublishTime(chart2SecondUpdatePublishTimeMillis).withMedia(media));

        List<Integer> mediaIds = asList(333, media.getI(), 999);

        //when
        List<ChartDetail> duplicatedMediaChartDetails = chartService.getDuplicatedMediaChartDetails(communityUrl, excludedChartId, selectedTimeMillis, mediaIds);

        //then
        assertThat(duplicatedMediaChartDetails.size(), is(4));

        assertThat(duplicatedMediaChartDetails.get(0).getI(), is(chartDetailOfFirstUpdateChart1.getI()));
        assertThat(duplicatedMediaChartDetails.get(1).getI(), is(chartDetailOfSecondUpdateChart1.getI()));
        assertThat(duplicatedMediaChartDetails.get(2).getI(), is(chartDetailOfFirstUpdateChart2.getI()));
        assertThat(duplicatedMediaChartDetails.get(3).getI(), is(chartDetailOfSecondUpdateChart2.getI()));
    }

}
