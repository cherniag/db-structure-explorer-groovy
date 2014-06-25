package mobi.nowtechnologies.server.transport.controller;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.jayway.jsonpath.JsonPath;
import mobi.nowtechnologies.server.persistence.domain.*;
import mobi.nowtechnologies.server.persistence.domain.FileType;
import mobi.nowtechnologies.server.persistence.repository.*;
import mobi.nowtechnologies.server.shared.Utils;
import mobi.nowtechnologies.server.shared.enums.ChgPosition;
import mobi.nowtechnologies.server.trackrepo.enums.*;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.stereotype.Repository;
import org.springframework.test.web.servlet.ResultActions;

import javax.annotation.Resource;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static com.google.common.base.Charsets.UTF_8;
import static mobi.nowtechnologies.server.shared.enums.ChgPosition.DOWN;
import static mobi.nowtechnologies.server.trackrepo.enums.FileType.IMAGE;
import static mobi.nowtechnologies.server.trackrepo.enums.FileType.MOBILE_AUDIO;
import static org.skyscreamer.jsonassert.JSONAssert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Titov Mykhaylo (titov) on 25.06.2014.
 */
public class GetChartControllerIT extends AbstractControllerTestIT {

    @Resource ArtistRepository artistRepository;
    @Resource MediaFileRepository mediaFileRepository;
    @Resource MediaRepository mediaRepository;
    @Resource ChartRepository chartRepository;
    @Resource ChartDetailRepository chartDetailRepository;
    @Resource GenreRepository genreRepository;
    @Resource CommunityRepository communityRepository;
    @Resource LabelRepository labelRepository;
    private String rememberMeToken;

    @Test
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
        Label label = labelRepository.findOne((byte)1);

        Media media = mediaRepository.save(new Media().withIsrc("isrc").withTitle("title").withArtist(artist).withAudioFile(audioMediaFile).withImageFileSmall(imageFileSmallMediaFile).withImageFileLarge(imageFileLargeMediaFile).withGenre(rockGenre).withLabel(label).withTrackId(666L));

        Community community = communityRepository.findByRewriteUrlParameter(communityUrl);
        Chart chart = chartRepository.save(new Chart().withName("chart name").withGenre(rockGenre).withCommunity(community));

        ChartDetail chartDetail = chartDetailRepository.save(new ChartDetail().withMedia(media).withChart(chart).withPrevPosition(Byte.MAX_VALUE).withChgPosition(DOWN));

        //when
        ResultActions resultActions = mockMvc.perform(
                post("/" + communityUrl + "/" + apiVersion + "/GET_CHART.json")
                        .param("USER_NAME", userName)
                        .param("USER_TOKEN", userToken)
                        .param("TIMESTAMP", timestamp)
                        .param("DEVICE_UID", deviceUID)
        );

        //then
        resultActions.andExpect(status().isOk());

        MockHttpServletResponse aHttpServletResponse = resultActions.andReturn().getResponse();
        String resultJson = aHttpServletResponse.getContentAsString();
        rememberMeToken = JsonPath.read(resultJson, "$.response.data[?(@.user)][0].user.rememberMeToken");

        String expectedJson = getExpectedJson(chartDetail);
        assertEquals(expectedJson, resultJson, false);

    }

    private String getExpectedJson(ChartDetail chartDetail) throws Exception {
        String jsonFormat = getFileContent("chart/chart.json");

        String json = jsonFormat.replace("{rememberMeToken}", rememberMeToken);
        json = json.replace("{media}", chartDetail.getMedia().getIsrcTrackId());
        json = json.replace("{title}", chartDetail.getMedia().getTitle());
        json = json.replace("{artist}", chartDetail.getMedia().getArtistName());
        json = json.replace("{genre1}", chartDetail.getMedia().getGenreName());
        json = json.replace("{genre2}", chartDetail.getChart().getGenreName());
        json = json.replace("{changePosition}", chartDetail.getChgPosition().getLabel());

        return json;
    }

    private String getFileContent(String fileName) throws IOException {
            File file = new ClassPathResource(fileName).getFile();
            return Files.toString(file, UTF_8);
    }
}
