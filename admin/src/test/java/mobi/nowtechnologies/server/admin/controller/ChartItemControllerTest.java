package mobi.nowtechnologies.server.admin.controller;

import mobi.nowtechnologies.server.admin.asm.ChartAsm;
import mobi.nowtechnologies.server.assembler.ChartDetailsAsm;
import mobi.nowtechnologies.server.factory.admin.ChartItemFactory;
import mobi.nowtechnologies.server.persistence.domain.Chart;
import mobi.nowtechnologies.server.persistence.domain.ChartDetail;
import mobi.nowtechnologies.server.persistence.domain.ChartDetailFactory;
import mobi.nowtechnologies.server.persistence.domain.Media;
import mobi.nowtechnologies.server.persistence.repository.ChartRepository;
import mobi.nowtechnologies.server.service.ChartDetailService;
import mobi.nowtechnologies.server.service.ChartService;
import mobi.nowtechnologies.server.service.MediaService;
import mobi.nowtechnologies.server.service.exception.ServiceException;
import mobi.nowtechnologies.server.shared.dto.admin.ChartItemDto;
import mobi.nowtechnologies.server.shared.enums.ChartType;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.context.MessageSource;
import org.springframework.web.servlet.ModelAndView;

import org.junit.*;
import org.junit.runner.*;
import org.mockito.*;
import org.springframework.test.web.ModelAndViewAssert;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;

@RunWith(PowerMockRunner.class)
@PrepareForTest(ChartDetailsAsm.class)
public class ChartItemControllerTest {

    private ChartItemController fixture;
    @Mock
    private ChartDetailService chartDetailService;
    @Mock
    private MediaService mediaService;
    @Mock
    private ChartService chartService;
    @Mock
    private ChartAsm chartAsm;
    @Mock
    private ChartRepository chartRepository;

    @Before
    public void setUp() throws Exception {

        mockStatic(ChartDetailsAsm.class);

        Map<ChartType, String> viewByChartType = new HashMap<ChartType, String>();
        viewByChartType.put(ChartType.BASIC_CHART, "chartItems/chartItems");
        viewByChartType.put(ChartType.HOT_TRACKS, "chartItems/hotTracks");
        viewByChartType.put(ChartType.OTHER_CHART, "chartItems/hotTracks");

        fixture = new ChartItemController();
        fixture.setViewByChartType(viewByChartType);
        fixture.setMediaService(mediaService);
        fixture.setChartService(chartService);
        fixture.setChartDetailService(chartDetailService);
        fixture.setFilesURL("");
        fixture.setChartAsm(chartAsm);
        fixture.setChartRepository(chartRepository);
        //fixture.dateFormat = new SimpleDateFormat();
        fixture.messageSource = mock(MessageSource.class);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testUpdateChartItems_Successful() throws Exception {
        Date selectedPublishDateTime = new Date();
        Integer chartId = 1;
        List<ChartItemDto> items = ChartItemFactory.getChartItemDtos(2, chartId, selectedPublishDateTime);

        when(chartDetailService.saveChartItems(any(List.class))).thenReturn(Collections.<ChartDetail>emptyList());

        ModelAndView result = fixture.updateChartItems(items, selectedPublishDateTime, chartId);

        assertNotNull("ModelAndView should not be null", result);
        ModelAndViewAssert.assertViewName(result, "redirect:/charts/" + chartId);

        verify(chartDetailService, times(1)).saveChartItems(any(List.class));
    }

    @SuppressWarnings("unchecked")
    @Test(expected = ServiceException.class)
    public void testUpdateChartItems_ServiceException() throws Exception {
        Date selectedPublishDateTime = new Date();
        Integer chartId = 1;
        List<ChartItemDto> items = ChartItemFactory.getChartItemDtos(2, chartId, selectedPublishDateTime);

        doThrow(ServiceException.getInstance("")).when(chartDetailService).saveChartItems(any(List.class));

        fixture.updateChartItems(items, selectedPublishDateTime, chartId);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testGetChartItemsPage_BasicChart_Successful() throws Exception {
        Integer chartId = 1;
        Chart chart = new Chart();
        chart.setI(chartId);
        chart.setType(ChartType.BASIC_CHART);
        Date selectedPublishDateTime = new Date();
        String filesUrl = "";
        List<ChartDetail> chartDetails = Collections.singletonList(ChartDetailFactory.createChartDetail());
        List<ChartItemDto> chartItemDtos = ChartItemFactory.getChartItemDtos(2, chartId, selectedPublishDateTime);

        fixture.setFilesURL(filesUrl);
        when(chartDetailService.getChartItemsByDate(anyInt(), any(Date.class), anyBoolean())).thenReturn(Collections.<ChartDetail>emptyList());
        when(chartRepository.findOne(anyInt())).thenReturn(chart);
        when(chartService.getChartDetails(any(List.class), any(Date.class), anyBoolean())).thenReturn(chartDetails);
        when(ChartDetailsAsm.toChartItemDtos(anyList())).thenReturn(chartItemDtos);

        ModelAndView result = fixture.getChartItemsPage(selectedPublishDateTime, chartId, true, null);

        assertNotNull("ModelAndView should not be null", result);
        ModelAndViewAssert.assertViewName(result, "chartItems/chartItems");
        ModelAndViewAssert.assertModelAttributeValue(result, ChartItemDto.CHART_ITEM_DTO_LIST, chartItemDtos);
        ModelAndViewAssert.assertModelAttributeValue(result, "selectedPublishDateTime", selectedPublishDateTime);
        ModelAndViewAssert.assertModelAttributeValue(result, "filesURL", filesUrl);

        verify(chartDetailService, times(1)).getChartItemsByDate(anyInt(), any(Date.class), anyBoolean());
        verifyStatic(times(1));
        ChartDetailsAsm.toChartItemDtos(anyList());

    }

    @SuppressWarnings("unchecked")
    @Test
    public void testGetChartItemsPage_HotChart_Successful() throws Exception {
        Integer chartId = 1;
        Chart chart = new Chart();
        chart.setI(chartId);
        chart.setType(ChartType.HOT_TRACKS);
        Date selectedPublishDateTime = new Date();
        String filesUrl = "";
        List<ChartDetail> chartDetails = Collections.singletonList(ChartDetailFactory.createChartDetail());
        List<ChartItemDto> chartItemDtos = ChartItemFactory.getChartItemDtos(2, chartId, selectedPublishDateTime);

        fixture.setFilesURL(filesUrl);
        when(chartDetailService.getChartItemsByDate(anyInt(), any(Date.class), anyBoolean())).thenReturn(Collections.<ChartDetail>emptyList());
        when(chartService.getChartDetails(any(List.class), any(Date.class), anyBoolean())).thenReturn(chartDetails);
        when(chartRepository.findOne(anyInt())).thenReturn(chart);
        when(ChartDetailsAsm.toChartItemDtos(anyList())).thenReturn(chartItemDtos);

        ModelAndView result = fixture.getChartItemsPage(selectedPublishDateTime, chartId, true, null);

        assertNotNull("ModelAndView should not be null", result);
        ModelAndViewAssert.assertViewName(result, "chartItems/hotTracks");
        ModelAndViewAssert.assertModelAttributeValue(result, ChartItemDto.CHART_ITEM_DTO_LIST, chartItemDtos);
        ModelAndViewAssert.assertModelAttributeValue(result, "selectedPublishDateTime", selectedPublishDateTime);
        ModelAndViewAssert.assertModelAttributeValue(result, "filesURL", filesUrl);

        verify(chartDetailService, times(1)).getChartItemsByDate(anyInt(), any(Date.class), anyBoolean());
        verifyStatic(times(1));
        ChartDetailsAsm.toChartItemDtos(anyList());

    }

    @SuppressWarnings("unchecked")
    @Test
    public void testGetChartItemsPage_OtherChart_Successful() throws Exception {
        Integer chartId = 1;
        Chart chart = new Chart();
        chart.setI(chartId);
        chart.setType(ChartType.OTHER_CHART);
        Date selectedPublishDateTime = new Date();
        String filesUrl = "";
        List<ChartDetail> chartDetails = Collections.singletonList(ChartDetailFactory.createChartDetail());
        when(chartService.getChartDetails(any(List.class), any(Date.class), anyBoolean())).thenReturn(chartDetails);
        List<ChartItemDto> chartItemDtos = ChartItemFactory.getChartItemDtos(2, chartId, selectedPublishDateTime);

        fixture.setFilesURL(filesUrl);
        when(chartDetailService.getChartItemsByDate(anyInt(), any(Date.class), anyBoolean())).thenReturn(Collections.<ChartDetail>emptyList());
        when(chartRepository.findOne(anyInt())).thenReturn(chart);
        when(ChartDetailsAsm.toChartItemDtos(anyList())).thenReturn(chartItemDtos);

        ModelAndView result = fixture.getChartItemsPage(selectedPublishDateTime, chartId, true, null);

        assertNotNull("ModelAndView should not be null", result);
        ModelAndViewAssert.assertViewName(result, "chartItems/hotTracks");
        ModelAndViewAssert.assertModelAttributeValue(result, ChartItemDto.CHART_ITEM_DTO_LIST, chartItemDtos);
        ModelAndViewAssert.assertModelAttributeValue(result, "selectedPublishDateTime", selectedPublishDateTime);
        ModelAndViewAssert.assertModelAttributeValue(result, "filesURL", filesUrl);

        verify(chartDetailService, times(1)).getChartItemsByDate(anyInt(), any(Date.class), anyBoolean());
        verifyStatic(times(1));
        ChartDetailsAsm.toChartItemDtos(anyList());

    }

    @Test(expected = ServiceException.class)
    public void testGetChartItemsPage_ServiceException() throws Exception {
        Date selectedPublishDateTime = new Date();
        Integer chartId = 1;
        List<ChartDetail> chartDetails = Collections.singletonList(ChartDetailFactory.createChartDetail());

        when(chartService.getChartDetails(any(List.class), any(Date.class), anyBoolean())).thenReturn(chartDetails);
        doThrow(ServiceException.getInstance("")).when(chartDetailService).getChartItemsByDate(anyInt(), any(Date.class), anyBoolean());

        fixture.getChartItemsPage(selectedPublishDateTime, chartId, true, null);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testGetMediaList_Successful() throws Exception {
        Date selectedPublishDateTime = new Date();
        Integer chartId = 1;
        String searchWords = "some words";
        List<ChartItemDto> chartItemDtos = ChartItemFactory.getChartItemDtos(2, chartId, selectedPublishDateTime);

        when(mediaService.getMedias(anyString())).thenReturn(Collections.<Media>emptyList());
        when(ChartDetailsAsm.toChartItemDtosFromMedia(any(Date.class), anyInt(), anyList())).thenReturn(chartItemDtos);

        ModelAndView result = fixture.getMediaList(searchWords, selectedPublishDateTime, chartId, "media");

        assertNotNull("ModelAndView should not be null", result);
        ModelAndViewAssert.assertViewName(result, null);
        ModelAndViewAssert.assertModelAttributeValue(result, ChartItemDto.CHART_ITEM_DTO_LIST, chartItemDtos);

        verify(mediaService, times(1)).getMusic(anyString());
        verifyStatic(times(1));
        ChartDetailsAsm.toChartItemDtosFromMedia(any(Date.class), anyInt(), anyList());
    }

    @Test(expected = ServiceException.class)
    public void testGetMediaList_ServiceException() throws Exception {
        String searchWords = "some words";
        Date selectedPublishDateTime = new Date();
        Integer chartId = 1;

        doThrow(ServiceException.getInstance("")).when(mediaService).getMusic(anyString());

        fixture.getMediaList(searchWords, selectedPublishDateTime, chartId, "media");
    }

}
