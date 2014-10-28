package mobi.nowtechnologies.server.transport.controller;

import mobi.nowtechnologies.server.log4j.InMemoryEventAppender;
import mobi.nowtechnologies.server.persistence.domain.*;
import mobi.nowtechnologies.server.persistence.repository.*;
import mobi.nowtechnologies.server.shared.Utils;
import mobi.nowtechnologies.server.utils.ChartDetailsConverter;
import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;

import static com.google.common.net.HttpHeaders.LAST_MODIFIED;
import static junit.framework.Assert.assertEquals;
import static mobi.nowtechnologies.server.shared.enums.ChgPosition.DOWN;
import static mobi.nowtechnologies.server.trackrepo.enums.FileType.IMAGE;
import static mobi.nowtechnologies.server.trackrepo.enums.FileType.MOBILE_AUDIO;
import static org.springframework.test.web.servlet.request.ExtMockMvcRequestBuilders.extGet;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * @author Titov Mykhaylo (titov) on 25.06.2014.
 */
public class GetChartControllerIT extends AbstractControllerTestIT {

    @Resource
    private ArtistRepository artistRepository;
    @Resource
    private MediaFileRepository mediaFileRepository;
    @Resource
    private MediaRepository mediaRepository;
    @Resource
    private ChartRepository chartRepository;
    @Resource
    private ChartDetailRepository chartDetailRepository;
    @Resource
    private GenreRepository genreRepository;
    @Resource
    private CommunityRepository communityRepository;
    @Resource
    private LabelRepository labelRepository;

    private InMemoryEventAppender inMemoryEventAppender = new InMemoryEventAppender();

    @After
    public void onComplete() {
        Logger.getRootLogger().removeAppender(inMemoryEventAppender);
    }

    @Before
    public void onStart() throws Exception {
        Logger.getRootLogger().addAppender(inMemoryEventAppender);
    }

    @Test
    @Transactional
    public void shouldReturnMediaAsIsrc_TrackId() throws Exception {
        //given
        String userName = "+447111111114";
        String deviceUID = "b88106713409e92622461a876abcd74b";
        String apiVersion = "5.1";
        String communityUrl = "o2";
        String timestamp = "2011_12_26_07_04_23";
        String storedToken = "f701af8d07e5c95d3f5cf3bd9a62344d";
        String userToken = Utils.createTimestampToken(storedToken, timestamp);

        Genre rockGenre = genreRepository.save(new Genre().withName("Rock"));
        Artist artist = artistRepository.save(new Artist().withName("artist name"));
        MediaFile audioMediaFile = mediaFileRepository.save(new MediaFile().withFileName("file name").withFileType(new FileType().withI(MOBILE_AUDIO.getIdAsByte()).withName(MOBILE_AUDIO.name())));
        MediaFile imageFileSmallMediaFile = mediaFileRepository.save(new MediaFile().withFileName("file name").withFileType(new FileType().withI(IMAGE.getIdAsByte()).withName(IMAGE.name())));
        MediaFile imageFileLargeMediaFile = mediaFileRepository.save(new MediaFile().withFileName("file name").withFileType(new FileType().withI(IMAGE.getIdAsByte()).withName(IMAGE.name())));
        Label label = labelRepository.findOne(1L);

        final String isrc = "isrc";
        final long trackId = 666L;
        Media media = mediaRepository.save(
                new Media().withIsrc(isrc).withTitle("title").withArtist(artist).withAudioFile(audioMediaFile).withImageFileSmall(imageFileSmallMediaFile).withImageFileLarge(imageFileLargeMediaFile).withGenre(rockGenre).withLabel(label).withTrackId(trackId)
        );

        Community community = communityRepository.findByRewriteUrlParameter(communityUrl);
        Chart chart = chartRepository.save(new Chart().withName("chart name").withGenre(rockGenre).withCommunity(community));

        chartDetailRepository.save(
                new ChartDetail().withMedia(media).withChart(chart).withPrevPosition(Byte.MAX_VALUE).withChgPosition(DOWN).withPosition(18)
        );

        //when
        ResultActions resultActions = mockMvc.perform(
                post("/" + communityUrl + "/" + apiVersion + "/GET_CHART.json")
                        .param("USER_NAME", userName)
                        .param("USER_TOKEN", userToken)
                        .param("TIMESTAMP", timestamp)
                        .param("DEVICE_UID", deviceUID)
        );

        //then
        resultActions
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.response.data[1].chart.tracks[(@.length-1)].media").value(isrc + "_" + trackId));
        assertEquals(0, inMemoryEventAppender.countOfWarnForLogger(ChartDetailsConverter.class));
    }



    @Test
    @Transactional
    public void testGetChartForAPI63() throws Exception {
        //given
        String userName = "+447111111114";
        String deviceUID = "b88106713409e92622461a876abcd74b";
        String apiVersion = "6.3";
        String communityUrl = "o2";
        String timestamp = "2011_12_26_07_04_23";
        String storedToken = "f701af8d07e5c95d3f5cf3bd9a62344d";
        String userToken = Utils.createTimestampToken(storedToken, timestamp);

        Genre rockGenre = genreRepository.save(new Genre().withName("Rock"));
        Artist artist = artistRepository.save(new Artist().withName("artist name"));
        MediaFile audioMediaFile = mediaFileRepository.save(new MediaFile().withFileName("file name").withFileType(new FileType().withI(MOBILE_AUDIO.getIdAsByte()).withName(MOBILE_AUDIO.name())));
        MediaFile imageFileSmallMediaFile = mediaFileRepository.save(new MediaFile().withFileName("file name").withFileType(new FileType().withI(IMAGE.getIdAsByte()).withName(IMAGE.name())));
        MediaFile imageFileLargeMediaFile = mediaFileRepository.save(new MediaFile().withFileName("file name").withFileType(new FileType().withI(IMAGE.getIdAsByte()).withName(IMAGE.name())));
        Label label = labelRepository.findOne(1L);

        final String isrc = "isrc";
        final long trackId = 666L;
        Media media = mediaRepository.save(
                new Media().withIsrc(isrc).withTitle("title").withArtist(artist).withAudioFile(audioMediaFile).withImageFileSmall(imageFileSmallMediaFile).withImageFileLarge(imageFileLargeMediaFile).withGenre(rockGenre).withLabel(label).withTrackId(trackId)
        );

        Community community = communityRepository.findByRewriteUrlParameter(communityUrl);
        Chart chart = chartRepository.save(new Chart().withName("chart name").withGenre(rockGenre).withCommunity(community));
        Date currentDate = new Date();
        Long publishTime = DateUtils.addDays(currentDate, -1).getTime();
        chartDetailRepository.save(
                new ChartDetail().withMedia(media)
                        .withChart(chart)
                        .withPrevPosition(Byte.MAX_VALUE)
                        .withChgPosition(DOWN)
                        .withPosition(18)
                        .withPublishTime(publishTime));
        mockMvc.perform(
                extGet("/" + communityUrl + "/" + apiVersion + "/GET_CHART.json")
                        .param("USER_NAME", userName)
                        .param("USER_TOKEN", userToken)
                        .param("TIMESTAMP", timestamp)
                        .param("DEVICE_UID", deviceUID)
         ).andExpect(header().longValue(LAST_MODIFIED, publishTime))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.response.data[1].chart.tracks[(@.length-1)].media").value(isrc + "_" + trackId));
        Date dateInFuture = DateUtils.addDays(currentDate, 1);
        mockMvc.perform(
                extGet("/" + communityUrl + "/" + apiVersion + "/GET_CHART.json")
                        .param("USER_NAME", userName)
                        .param("USER_TOKEN", userToken)
                        .param("TIMESTAMP", timestamp)
                        .param("DEVICE_UID", deviceUID)
                        .headers(getHttpHeadersWithIfModifiedSince(dateInFuture))
        ).andExpect(header().longValue(LAST_MODIFIED, publishTime))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.response.data[1].chart.tracks[(@.length-1)].media").value(isrc + "_" + trackId));

        mockMvc.perform(
                extGet("/" + communityUrl + "/" + apiVersion + "/GET_CHART.json")
                        .param("USER_NAME", userName)
                        .param("USER_TOKEN", userToken)
                        .param("TIMESTAMP", timestamp)
                        .param("DEVICE_UID", deviceUID)
                        .headers(getHttpHeadersWithIfModifiedSince("INVALID DATE"))
        ).andExpect(header().longValue(LAST_MODIFIED, publishTime))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.response.data[1].chart.tracks[(@.length-1)].media").value(isrc + "_" + trackId));
        mockMvc.perform(
                extGet("/" + communityUrl + "/" + apiVersion + "/GET_CHART.json")
                        .param("USER_NAME", userName)
                        .param("USER_TOKEN", userToken)
                        .param("TIMESTAMP", timestamp)
                        .param("DEVICE_UID", deviceUID)
                        .headers(getHttpHeadersWithIfModifiedSince(publishTime))
        ).andExpect(status().isNotModified()).andExpect(content().string(""));

    }

}
