package mobi.nowtechnologies.server.transport.controller;

import mobi.nowtechnologies.server.log4j.InMemoryEventAppender;
import mobi.nowtechnologies.server.persistence.domain.Artist;
import mobi.nowtechnologies.server.persistence.domain.Chart;
import mobi.nowtechnologies.server.persistence.domain.ChartDetail;
import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.FileType;
import mobi.nowtechnologies.server.persistence.domain.Genre;
import mobi.nowtechnologies.server.persistence.domain.Label;
import mobi.nowtechnologies.server.persistence.domain.Media;
import mobi.nowtechnologies.server.persistence.domain.MediaFile;
import mobi.nowtechnologies.server.persistence.repository.ArtistRepository;
import mobi.nowtechnologies.server.persistence.repository.ChartDetailRepository;
import mobi.nowtechnologies.server.persistence.repository.ChartRepository;
import mobi.nowtechnologies.server.persistence.repository.CommunityRepository;
import mobi.nowtechnologies.server.persistence.repository.GenreRepository;
import mobi.nowtechnologies.server.persistence.repository.LabelRepository;
import mobi.nowtechnologies.server.persistence.repository.MediaFileRepository;
import mobi.nowtechnologies.server.persistence.repository.MediaRepository;
import mobi.nowtechnologies.server.service.chart.ChartDetailsConverter;
import mobi.nowtechnologies.server.shared.Utils;
import static mobi.nowtechnologies.server.shared.enums.ChgPosition.DOWN;
import static mobi.nowtechnologies.server.trackrepo.enums.FileType.IMAGE;
import static mobi.nowtechnologies.server.trackrepo.enums.FileType.MOBILE_AUDIO;

import javax.annotation.Resource;

import org.apache.log4j.Logger;

import org.springframework.transaction.annotation.Transactional;

import org.junit.*;
import org.springframework.test.web.servlet.ResultActions;
import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
            new Media().withIsrc(isrc).withTitle("title").withArtist(artist).withAudioFile(audioMediaFile).withImageFileSmall(imageFileSmallMediaFile).withImageFileLarge(imageFileLargeMediaFile)
                       .withGenre(rockGenre).withLabel(label).withTrackId(trackId));

        Community community = communityRepository.findByRewriteUrlParameter(communityUrl);
        Chart chart = chartRepository.save(new Chart().withName("chart name").withGenre(rockGenre).withCommunity(community));

        chartDetailRepository.save(new ChartDetail().withMedia(media).withChart(chart).withPrevPosition(Byte.MAX_VALUE).withChgPosition(DOWN).withPosition(18));

        //when
        ResultActions resultActions = mockMvc.perform(
            post("/" + communityUrl + "/" + apiVersion + "/GET_CHART.json").param("USER_NAME", userName).param("USER_TOKEN", userToken).param("TIMESTAMP", timestamp).param("DEVICE_UID", deviceUID));

        //then
        resultActions.andExpect(status().isOk()).andDo(print()).andExpect(jsonPath("$.response.data[1].chart.tracks[(@.length-1)].media").value(isrc + "_" + trackId));
        assertEquals(0, inMemoryEventAppender.countOfWarnForLogger(ChartDetailsConverter.class));
    }


}
