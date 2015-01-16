package mobi.nowtechnologies.server.admin.controller;

import mobi.nowtechnologies.server.persistence.domain.*;
import mobi.nowtechnologies.server.persistence.repository.*;
import mobi.nowtechnologies.server.service.ChartService;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.test.web.servlet.ResultActions;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

import static mobi.nowtechnologies.server.shared.enums.ChartType.BASIC_CHART;
import static mobi.nowtechnologies.server.shared.enums.ChartType.FIFTH_CHART;
import static mobi.nowtechnologies.server.trackrepo.enums.FileType.IMAGE;
import static mobi.nowtechnologies.server.trackrepo.enums.FileType.MOBILE_AUDIO;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// @author Titov Mykhaylo (titov) on 17.11.2014.
public class ChartItemControllerIT extends AbstractAdminITTest{

    static final String URL_DATE_TIME_FORMAT = "yyyy-MM-dd_HH:mm:ss";

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
    @Ignore
    public void shouldReturnDuplicatedMediaAcrossNearestChartsDtos() throws Exception {
        //given
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(URL_DATE_TIME_FORMAT);
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        String requestURI = "/chartsNEW/-1/1970-01-04_00:00:00/";
        String communityUrl = "o2";

        Artist artist = artistRepository.save(new Artist().withName("artist name"));
        MediaFile audioMediaFile = mediaFileRepository.save(new MediaFile().withFileName("file name").withFileType(new FileType().withI(MOBILE_AUDIO.getIdAsByte()).withName(MOBILE_AUDIO.name())));
        MediaFile imageFileSmallMediaFile = mediaFileRepository.save(new MediaFile().withFileName("file name").withFileType(new FileType().withI(IMAGE.getIdAsByte()).withName(IMAGE.name())));
        MediaFile imageFileLargeMediaFile = mediaFileRepository.save(new MediaFile().withFileName("file name").withFileType(new FileType().withI(IMAGE.getIdAsByte()).withName(IMAGE.name())));
        Genre rockGenre = genreRepository.save(new Genre().withName("Rock"));
        Label label = labelRepository.findOne(1L);

        Media media = mediaRepository.save(new Media().withIsrc("isrc").withTitle("title").withArtist(artist).withAudioFile(audioMediaFile).withImageFileSmall(imageFileSmallMediaFile).withImageFileLarge(imageFileLargeMediaFile).withGenre(rockGenre).withLabel(label).withTrackId(666L));

        Community community = communityRepository.findByRewriteUrlParameter(communityUrl);

        Chart chart1 = chartRepository.save(new Chart().withCommunity(community).withName("chart 1").withGenre(rockGenre).withChartType(BASIC_CHART));

        long chart1FirstUpdatePublishTimeMillis = simpleDateFormat.parse("1970-01-02_00:00:00").getTime();
        chartDetailRepository.save(new ChartDetail().withChart(chart1).withPosition(1).withPublishTime(chart1FirstUpdatePublishTimeMillis));
        chartDetailRepository.save(new ChartDetail().withChart(chart1).withPosition(1).withPublishTime(chart1FirstUpdatePublishTimeMillis).withMedia(media));

        long chart1SecondUpdatePublishTimeMillis = simpleDateFormat.parse("1970-01-06_00:00:00").getTime();
        chartDetailRepository.save(new ChartDetail().withChart(chart1).withPosition(1).withPublishTime(chart1SecondUpdatePublishTimeMillis));
        chartDetailRepository.save(new ChartDetail().withChart(chart1).withPosition(1).withPublishTime(chart1SecondUpdatePublishTimeMillis).withMedia(media));

        Chart chart2 = chartRepository.save(new Chart().withCommunity(community).withName("chart 2").withGenre(rockGenre).withChartType(FIFTH_CHART));

        long chart2FirstUpdatePublishTimeMillis = simpleDateFormat.parse("1970-01-01_00:00:00").getTime();
        chartDetailRepository.save(new ChartDetail().withChart(chart2).withPosition(2).withPublishTime(chart2FirstUpdatePublishTimeMillis));
        chartDetailRepository.save(new ChartDetail().withChart(chart2).withPosition(1).withPublishTime(chart2FirstUpdatePublishTimeMillis).withMedia(media));

        long chart2SecondUpdatePublishTimeMillis = simpleDateFormat.parse("1970-01-07_00:00:00").getTime();
        chartDetailRepository.save(new ChartDetail().withChart(chart2).withPosition(2).withPublishTime(chart2SecondUpdatePublishTimeMillis));
        chartDetailRepository.save(new ChartDetail().withChart(chart2).withPosition(1).withPublishTime(chart2SecondUpdatePublishTimeMillis).withMedia(media));

        //when
        ResultActions perform = mockMvc.perform(get(requestURI).headers(getHttpHeaders(true)).cookie(getCommunityCookie(communityUrl)).param("mediaId", "333", media.getI().toString(), "999"));

        //then
        perform.andExpect(status().isOk());
        perform.andExpect(content().string("{\"chartFilesURL\":\"http://c1129449.r49.cf3.rackcdn.com/\",\"duplicatedMediaAcrossNearestChartsDtos\":[{\"chartId\":19,\"chartName\":\"chart 1\",\"publishTimeMillis\":86400000,\"position\":1,\"trackId\":\"isrc_666\"},{\"chartId\":19,\"chartName\":\"chart 1\",\"publishTimeMillis\":432000000,\"position\":1,\"trackId\":\"isrc_666\"},{\"chartId\":20,\"chartName\":\"chart 2\",\"publishTimeMillis\":0,\"position\":1,\"trackId\":\"isrc_666\"},{\"chartId\":20,\"chartName\":\"chart 2\",\"publishTimeMillis\":518400000,\"position\":1,\"trackId\":\"isrc_666\"}]}"));
    }
}
