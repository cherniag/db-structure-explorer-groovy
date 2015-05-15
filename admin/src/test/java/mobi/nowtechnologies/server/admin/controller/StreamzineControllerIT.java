package mobi.nowtechnologies.server.admin.controller;

import mobi.nowtechnologies.server.dto.streamzine.OrdinalBlockDto;
import mobi.nowtechnologies.server.dto.streamzine.UpdateIncomingDto;
import mobi.nowtechnologies.server.persistence.domain.Chart;
import mobi.nowtechnologies.server.persistence.domain.ChartDetail;
import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.Media;
import mobi.nowtechnologies.server.persistence.domain.streamzine.Update;
import mobi.nowtechnologies.server.persistence.domain.streamzine.types.ContentType;
import mobi.nowtechnologies.server.persistence.domain.streamzine.types.sub.LinkLocationType;
import mobi.nowtechnologies.server.persistence.domain.streamzine.types.sub.MusicType;
import mobi.nowtechnologies.server.persistence.domain.streamzine.types.sub.NewsType;
import mobi.nowtechnologies.server.persistence.domain.streamzine.visual.ShapeType;
import mobi.nowtechnologies.server.persistence.repository.ChartDetailRepository;
import mobi.nowtechnologies.server.persistence.repository.CommunityRepository;
import mobi.nowtechnologies.server.service.streamzine.StreamzineUpdateService;
import static mobi.nowtechnologies.server.persistence.domain.streamzine.PlayerType.MINI_PLAYER_ONLY;
import static mobi.nowtechnologies.server.persistence.domain.streamzine.types.ContentType.MUSIC;
import static mobi.nowtechnologies.server.persistence.domain.streamzine.types.sub.MusicType.MANUAL_COMPILATION;
import static mobi.nowtechnologies.server.persistence.domain.streamzine.visual.ShapeType.WIDE;

import java.util.Date;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;

import org.junit.*;
import org.springframework.test.util.ReflectionTestUtils;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Author: Gennadii Cherniaiev Date: 4/4/2014
 */
public class StreamzineControllerIT extends AbstractAdminITTest {


    @Value("${cloudFile.filesURL}")
    private String cloudFileUrl;

    @Autowired
    private ChartDetailRepository chartDetailRepository;

    @Autowired
    private StreamzineUpdateService streamzineUpdateService;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private CommunityRepository communityRepository;

    @Test
    public void testGetChartList() throws Exception {
        long publishTime = System.currentTimeMillis() + 100 * 1000;

        String communityUrl = "hl_uk";
        Community community = findHlUkCommunity();
        Update update = streamzineUpdateService.create(new Date(publishTime), community);

        //chart update before SZUpdate with 2 tracks
        createAndSaveChartDetail(publishTime - 100 * 1000, null, (byte) 1, "cover1.jpg", "title1", 10);
        createAndSaveChartDetail(publishTime - 100 * 1000, 49, (byte) 2, null, null, 10);
        createAndSaveChartDetail(publishTime - 100 * 1000, 50, (byte) 3, null, null, 10);
        //chart update for SZUpdate with 3 tracks
        createAndSaveChartDetail(publishTime - 50 * 1000, null, (byte) 1, "cover2.jpg", "title2", 10);
        createAndSaveChartDetail(publishTime - 50 * 1000, 49, (byte) 2, null, null, 10);
        createAndSaveChartDetail(publishTime - 50 * 1000, 50, (byte) 3, null, null, 10);
        createAndSaveChartDetail(publishTime - 50 * 1000, 51, (byte) 4, null, null, 10);
        //chart update after SZUpdate with 1 track
        createAndSaveChartDetail(publishTime + 50 * 1000, null, (byte) 1, "cover3.jpg", "title3", 10);
        createAndSaveChartDetail(publishTime + 50 * 1000, 49, (byte) 2, null, null, 10);

        mockMvc.perform(get("/streamzine/chart/list")
                            //.accept(MediaType.APPLICATION_JSON)
                            .headers(getHttpHeaders(true)).cookie(getCommunityCookie(communityUrl)).param("id", String.valueOf(update.getId()))).andExpect(status().isOk());
    }

    @Test
    public void checkSavingManualCompilationDto() throws Exception {
        long publishTime = System.currentTimeMillis() + 100 * 1000;
        String communityUrl = "hl_uk";

        Community community = findHlUkCommunity();
        Update update = streamzineUpdateService.create(new Date(publishTime), community);
        UpdateIncomingDto dto = getUpdateIncomingDto(update, MUSIC, WIDE, MANUAL_COMPILATION.name(), "70");

        //chart update before nearest - not valid
        createAndSaveChartDetail(publishTime - 100 * 1000, null, 11);
        createAndSaveChartDetail(publishTime - 100 * 1000, 69, 11);
        //chart update - nearest - valid
        createAndSaveChartDetail(publishTime - 50 * 1000, null, 11);
        createAndSaveChartDetail(publishTime - 50 * 1000, 70, 11); //"US-UM7-11-00062"
        //chart update after publish date - not valid
        createAndSaveChartDetail(publishTime + 50 * 1000, null, 11);
        createAndSaveChartDetail(publishTime + 50 * 1000, 71, 11);

        mockMvc.perform(
            post("/streamzine/update").accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON).headers(getHttpHeaders(true)).content(objectMapper.writeValueAsString(dto))
                                      .cookie(getCommunityCookie(communityUrl))).andExpect(status().isOk());
    }


    @Test
    public void testUpdateStreamzineWithIncludedBlockWithInvalidUrlForExternalAdd() throws Exception {
        long publishTime = System.currentTimeMillis() + 100 * 1000;
        String communityUrl = "hl_uk";
        Community community = findHlUkCommunity();
        Update update = streamzineUpdateService.create(new Date(publishTime), community);
        UpdateIncomingDto dto = new UpdateIncomingDto();
        ReflectionTestUtils.setField(dto, "id", update.getId());

        OrdinalBlockDto block = new OrdinalBlockDto();
        block.setShapeType(WIDE);
        block.setContentType(ContentType.PROMOTIONAL);
        block.setKey(LinkLocationType.EXTERNAL_AD.toString());
        block.setIncluded(true);
        dto.getBlocks().add(block);
        mockMvc
            .perform(post("/streamzine/update").accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON).headers(getHttpHeaders(true)).content(objectMapper.writeValueAsString(dto)).
                cookie(getCommunityCookie(communityUrl))).andDo(print()).andExpect(status().isBadRequest()).andExpect(jsonPath("$[3].message").value("Value does not look like URL: "));
    }


    @Test
    public void testUpdateStreamzineWithExcludedBlock() throws Exception {
        long publishTime = System.currentTimeMillis() + 100 * 1000;
        String communityUrl = "hl_uk";
        Community community = findHlUkCommunity();
        Update update = streamzineUpdateService.create(new Date(publishTime), community);
        UpdateIncomingDto dto = new UpdateIncomingDto();
        dto.setTimestamp(publishTime);
        ReflectionTestUtils.setField(dto, "id", update.getId());
        for (ShapeType currentType : new ShapeType[] {WIDE, ShapeType.NARROW}) {
            checkSave(communityUrl, dto, currentType, ContentType.NEWS, NewsType.LIST.name());
            checkSave(communityUrl, dto, currentType, ContentType.NEWS, NewsType.STORY.name());
            checkSave(communityUrl, dto, currentType, ContentType.PROMOTIONAL, LinkLocationType.INTERNAL_AD.name());
            checkSave(communityUrl, dto, currentType, ContentType.PROMOTIONAL, LinkLocationType.EXTERNAL_AD.name());
            checkSave(communityUrl, dto, currentType, MUSIC, MusicType.PLAYLIST.name());
            checkSave(communityUrl, dto, currentType, MUSIC, MusicType.TRACK.name());
            checkSave(communityUrl, dto, currentType, MUSIC, MANUAL_COMPILATION.name());
        }
    }


    private void checkSave(String communityUrl, UpdateIncomingDto dto, ShapeType currentType, ContentType contentType, String key) throws Exception {
        OrdinalBlockDto ordinalBlockDto = new OrdinalBlockDto();
        ordinalBlockDto.setShapeType(currentType);
        ordinalBlockDto.setContentType(contentType);
        ordinalBlockDto.setKey(key);
        ordinalBlockDto.setIncluded(false);
        if (key.equals(MusicType.PLAYLIST.name()) || key.equals(MusicType.TRACK.name())) {
            ordinalBlockDto.setValue("#" + MINI_PLAYER_ONLY);
        }
        dto.getBlocks().clear();
        dto.getBlocks().add(ordinalBlockDto);
        if (currentType.equals(ShapeType.NARROW)) {
            dto.getBlocks().add(ordinalBlockDto);
        }
        mockMvc
            .perform(post("/streamzine/update").accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON).headers(getHttpHeaders(true)).content(objectMapper.writeValueAsString(dto)).
                cookie(getCommunityCookie(communityUrl))).andExpect(status().isOk());
        chartDetailRepository.flush();
        mockMvc.perform(get("/streamzine/edit/" + dto.getId()).headers(getHttpHeaders(true)).cookie(getCommunityCookie(communityUrl))).andExpect(status().isOk());
        chartDetailRepository.flush();

    }

    private UpdateIncomingDto getUpdateIncomingDto(Update update, ContentType contentType, ShapeType shapeType, String key, String value) {
        UpdateIncomingDto dto = new UpdateIncomingDto();
        ReflectionTestUtils.setField(dto, "id", update.getId());
        dto.setTimestamp(update.getDate().getTime());
        OrdinalBlockDto block = new OrdinalBlockDto();
        block.setShapeType(shapeType);
        block.setContentType(contentType);
        block.setKey(key);
        block.setValue(value);
        block.setIncluded(true);
        block.setTitle("Title");
        block.setSubTitle("SubTitle");
        block.setCoverUrl("http://red.jpg");
        dto.getBlocks().add(block);
        return dto;
    }

    private void createAndSaveChartDetail(long publishTimeMillis, Integer mediaId, int chartId) {
        createAndSaveChartDetail(publishTimeMillis, mediaId, (byte) 1, "image", "title", chartId);
    }

    private void createAndSaveChartDetail(long publishTimeMillis, Integer mediaId, byte position, String imageFileName, String title, int chartId) {
        ChartDetail chartDetail = new ChartDetail();
        chartDetail.setInfo("position is " + position);
        Chart chart = new Chart();
        chart.setI(chartId);
        chartDetail.setChart(chart);
        chartDetail.setPublishTimeMillis(publishTimeMillis);
        chartDetail.setPosition(position);
        chartDetail.setImageFileName(imageFileName);
        chartDetail.setTitle(title);
        if (mediaId != null) {
            Media media = new Media();
            media.setI(mediaId);
            chartDetail.setMedia(media);
        }
        chartDetailRepository.save(chartDetail);
    }

    private Community findHlUkCommunity() {
        return communityRepository.findByRewriteUrlParameter("hl_uk");
    }
}
