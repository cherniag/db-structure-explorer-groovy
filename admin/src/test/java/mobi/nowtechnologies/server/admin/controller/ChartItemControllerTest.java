package mobi.nowtechnologies.server.admin.controller;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;

import java.text.SimpleDateFormat;
import java.util.*;

import junit.framework.TestCase;
import mobi.nowtechnologies.server.assembler.ChartDetailsAsm;
import mobi.nowtechnologies.server.factory.admin.ChartItemFactory;
import mobi.nowtechnologies.server.persistence.domain.*;
import mobi.nowtechnologies.server.service.ChartDetailService;
import mobi.nowtechnologies.server.service.ChartService;
import mobi.nowtechnologies.server.service.MediaService;
import mobi.nowtechnologies.server.service.exception.ServiceException;
import mobi.nowtechnologies.server.shared.dto.admin.ChartItemDto;
import mobi.nowtechnologies.server.shared.enums.ChartType;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.context.MessageSource;
import org.springframework.test.web.ModelAndViewAssert;
import org.springframework.web.servlet.ModelAndView;

/**
 * The class <code>ChartItemControllerTest</code> contains tests for the class <code>{@link ChartItemController}</code>.
 *
 * @generatedBy CodePro at 9/3/12 5:14 PM, using the Spring generator
 * @author Alexander Kolpakov (akolpakov)
 * @version $Revision: 1.0 $
 */
@RunWith( PowerMockRunner.class )
@PrepareForTest(ChartDetailsAsm.class)
public class ChartItemControllerTest extends TestCase {
	
	private ChartItemController fixture;
	@Mock
	private ChartDetailService chartDetailService;
	@Mock
	private MediaService mediaService;
	@Mock
	private ChartService chartService;
	
	/**
	 * Run the ModelAndView updateChartItems(String,Date,Byte) method test with success expected result.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 9/3/12 5:14 PM
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testUpdateChartItems_Successful()
		throws Exception {
		Date selectedPublishDateTime = new Date();
		Byte chartId = new Byte((byte) 1);
		String chartItemListJSON = ChartItemFactory.anyChartItemListJSON(2, chartId, selectedPublishDateTime);
		
		when(chartDetailService.saveChartItems(any(List.class))).thenReturn(Collections.<ChartDetail>emptyList());

		ModelAndView result = fixture.updateChartItems(chartItemListJSON, selectedPublishDateTime, chartId);

		assertNotNull("ModelAndView should not be null", result);
		ModelAndViewAssert.assertViewName(result, "redirect:/charts/" + chartId);
		
		verify(chartDetailService, times(1)).saveChartItems(any(List.class));
	}
	
	/**
	 * Run the ModelAndView updateChartItems(String,Date,Byte) method test with service exception expected result.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 9/3/12 5:14 PM
	 */
	@SuppressWarnings("unchecked")
	@Test(expected=ServiceException.class)
	public void testUpdateChartItems_ServiceException()
		throws Exception {
		Date selectedPublishDateTime = new Date();
		Byte chartId = new Byte((byte) 1);
		String chartItemListJSON = ChartItemFactory.anyChartItemListJSON(2, chartId, selectedPublishDateTime);
		
		doThrow(ServiceException.getInstance("")).when(chartDetailService).saveChartItems(any(List.class));

		fixture.updateChartItems(chartItemListJSON, selectedPublishDateTime, chartId);
	}
	
	/**
	 * Run the ModelAndView getChartItemsByDate(Date,Byte) method test with success expected result.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 9/3/12 5:14 PM
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testGetChartItemsPage_BasicChart_Successful()
		throws Exception {
		Byte chartId = new Byte((byte) 1);
		Chart chart = new Chart();
		chart.setI(chartId);
		chart.setType(ChartType.BASIC_CHART);
		Date selectedPublishDateTime = new Date();
		String filesUrl = "";
		List<ChartDetail> chartDetails = Collections.singletonList(ChartDetailFactory.createChartDetail());
		List<ChartItemDto> chartItemDtos = ChartItemFactory.getChartItemDtos(2, chartId, selectedPublishDateTime);
		
		fixture.setFilesURL(filesUrl);
		when(chartDetailService.getChartItemsByDate(anyByte(), any(Date.class), anyBoolean())).thenReturn(Collections.<ChartDetail>emptyList());
		when(chartService.getChartById(anyByte())).thenReturn(chart);
		when(chartService.getChartDetails(any(List.class), any(Date.class), anyBoolean())).thenReturn(chartDetails);
		when(ChartDetailsAsm.toChartItemDtos(anyList())).thenReturn(chartItemDtos);
		
		ModelAndView result = fixture.getChartItemsPage(selectedPublishDateTime, chartId, true, null);

		assertNotNull("ModelAndView should not be null", result);
		ModelAndViewAssert.assertViewName(result, "chartItems/chartItems");
		ModelAndViewAssert.assertModelAttributeValue(result, ChartItemDto.CHART_ITEM_DTO_LIST, chartItemDtos);
		ModelAndViewAssert.assertModelAttributeValue(result, "selectedPublishDateTime", selectedPublishDateTime);
		ModelAndViewAssert.assertModelAttributeValue(result, "filesURL", filesUrl);
		
		verify(chartDetailService, times(1)).getChartItemsByDate(anyByte(), any(Date.class), anyBoolean());
		verifyStatic(times(1));
		ChartDetailsAsm.toChartItemDtos(anyList());

	}
	
	/**
	 * Run the ModelAndView getChartItemsByDate(Date,Byte) method test with success expected result.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 9/3/12 5:14 PM
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testGetChartItemsPage_HotChart_Successful()
		throws Exception {
		Byte chartId = new Byte((byte) 1);
		Chart chart = new Chart();
		chart.setI(chartId);
		chart.setType(ChartType.HOT_TRACKS);
		Date selectedPublishDateTime = new Date();
		String filesUrl = "";
		List<ChartDetail> chartDetails = Collections.singletonList(ChartDetailFactory.createChartDetail());
		List<ChartItemDto> chartItemDtos = ChartItemFactory.getChartItemDtos(2, chartId, selectedPublishDateTime);
		
		fixture.setFilesURL(filesUrl);
		when(chartDetailService.getChartItemsByDate(anyByte(), any(Date.class), anyBoolean())).thenReturn(Collections.<ChartDetail>emptyList());
		when(chartService.getChartDetails(any(List.class), any(Date.class), anyBoolean())).thenReturn(chartDetails);
		when(chartService.getChartById(anyByte())).thenReturn(chart);
		when(ChartDetailsAsm.toChartItemDtos(anyList())).thenReturn(chartItemDtos);
		
		ModelAndView result = fixture.getChartItemsPage(selectedPublishDateTime, chartId, true, null);

		assertNotNull("ModelAndView should not be null", result);
		ModelAndViewAssert.assertViewName(result, "chartItems/hotTracks");
		ModelAndViewAssert.assertModelAttributeValue(result, ChartItemDto.CHART_ITEM_DTO_LIST, chartItemDtos);
		ModelAndViewAssert.assertModelAttributeValue(result, "selectedPublishDateTime", selectedPublishDateTime);
		ModelAndViewAssert.assertModelAttributeValue(result, "filesURL", filesUrl);
		
		verify(chartDetailService, times(1)).getChartItemsByDate(anyByte(), any(Date.class), anyBoolean());
		verifyStatic(times(1));
		ChartDetailsAsm.toChartItemDtos(anyList());

	}
	
	/**
	 * Run the ModelAndView getChartItemsByDate(Date,Byte) method test with success expected result.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 9/3/12 5:14 PM
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testGetChartItemsPage_OtherChart_Successful()
		throws Exception {
		Byte chartId = new Byte((byte) 1);
		Chart chart = new Chart();
		chart.setI(chartId);
		chart.setType(ChartType.OTHER_CHART);
		Date selectedPublishDateTime = new Date();
		String filesUrl = "";
		List<ChartDetail> chartDetails = Collections.singletonList(ChartDetailFactory.createChartDetail());
		when(chartService.getChartDetails(any(List.class), any(Date.class), anyBoolean())).thenReturn(chartDetails);
		List<ChartItemDto> chartItemDtos = ChartItemFactory.getChartItemDtos(2, chartId, selectedPublishDateTime);
		
		fixture.setFilesURL(filesUrl);
		when(chartDetailService.getChartItemsByDate(anyByte(), any(Date.class), anyBoolean())).thenReturn(Collections.<ChartDetail>emptyList());
		when(chartService.getChartById(anyByte())).thenReturn(chart);
		when(ChartDetailsAsm.toChartItemDtos(anyList())).thenReturn(chartItemDtos);
		
		ModelAndView result = fixture.getChartItemsPage(selectedPublishDateTime, chartId, true, null);

		assertNotNull("ModelAndView should not be null", result);
		ModelAndViewAssert.assertViewName(result, "chartItems/hotTracks");
		ModelAndViewAssert.assertModelAttributeValue(result, ChartItemDto.CHART_ITEM_DTO_LIST, chartItemDtos);
		ModelAndViewAssert.assertModelAttributeValue(result, "selectedPublishDateTime", selectedPublishDateTime);
		ModelAndViewAssert.assertModelAttributeValue(result, "filesURL", filesUrl);
		
		verify(chartDetailService, times(1)).getChartItemsByDate(anyByte(), any(Date.class), anyBoolean());
		verifyStatic(times(1));
		ChartDetailsAsm.toChartItemDtos(anyList());

	}
	
	/**
	 * Run the ModelAndView getChartItemsByDate(Date,Byte) method test with service exception expected result.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 9/3/12 5:14 PM
	 */
	@Test(expected=ServiceException.class)
	public void testGetChartItemsPage_ServiceException()
		throws Exception {
		Date selectedPublishDateTime = new Date();
		Byte chartId = new Byte((byte) 1);
		List<ChartDetail> chartDetails = Collections.singletonList(ChartDetailFactory.createChartDetail());
		
		when(chartService.getChartDetails(any(List.class), any(Date.class), anyBoolean())).thenReturn(chartDetails);
		doThrow(ServiceException.getInstance("")).when(chartDetailService).getChartItemsByDate(anyByte(), any(Date.class), anyBoolean());

		fixture.getChartItemsPage(selectedPublishDateTime, chartId, true, null);
	}
	
	/**
	 * Run the ModelAndView getMediaList(String,Date,Byte) method test with success expected result.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 9/3/12 5:14 PM
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testGetMediaList_Successful()
		throws Exception {
		Date selectedPublishDateTime = new Date();
		Byte chartId = new Byte((byte) 1);
		String searchWords = "some words";
		List<ChartItemDto> chartItemDtos = ChartItemFactory.getChartItemDtos(2, chartId, selectedPublishDateTime);
		
		when(mediaService.getMedias(anyString())).thenReturn(Collections.<Media>emptyList());
		when(ChartDetailsAsm.toChartItemDtosFromMedia(any(Date.class), anyByte(), anyList())).thenReturn(chartItemDtos);

		ModelAndView result = fixture.getMediaList(searchWords, selectedPublishDateTime, chartId);

		assertNotNull("ModelAndView should not be null", result);
		ModelAndViewAssert.assertViewName(result, null);
		ModelAndViewAssert.assertModelAttributeValue(result, ChartItemDto.CHART_ITEM_DTO_LIST, chartItemDtos);
		
		verify(mediaService, times(1)).getMedias(anyString());
		verifyStatic(times(1));
		ChartDetailsAsm.toChartItemDtosFromMedia(any(Date.class), anyByte(), anyList());
	}
	
	/**
	 * Run the ModelAndView getMediaList(String,Date,Byte) method test with service exception expected result.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 9/3/12 5:14 PM
	 */
	@Test(expected=ServiceException.class)
	public void testGetMediaList_ServiceException()
		throws Exception {
		String searchWords = "some words";
		Date selectedPublishDateTime = new Date();
		Byte chartId = new Byte((byte) 1);
		
		doThrow(ServiceException.getInstance("")).when(mediaService).getMedias(anyString());

		fixture.getMediaList(searchWords, selectedPublishDateTime, chartId);
	}

	/**
	 * Perform pre-test initialization.
	 *
	 * @throws Exception
	 *         if the initialization fails for some reason
	 *
	 * @see TestCase#setUp()
	 *
	 * @generatedBy CodePro at 9/3/12 5:14 PM
	 */
	@Before
	public void setUp()
		throws Exception {
		super.setUp();
		
		mockStatic( ChartDetailsAsm.class );

		Map<String, String> viewByChartType = new HashMap<String, String>();
		viewByChartType.put("BASIC_CHART", "chartItems/chartItems");
		viewByChartType.put("HOT_TRACKS", "chartItems/hotTracks");
		viewByChartType.put("OTHER_CHART", "chartItems/hotTracks");
		
		fixture = new ChartItemController();
		fixture.setViewByChartType(viewByChartType);
		fixture.setMediaService(mediaService);
		fixture.setChartService(chartService);
		fixture.setChartDetailService(chartDetailService);
		fixture.setFilesURL("");
		fixture.dateTimeFormat = new SimpleDateFormat();
		fixture.dateFormat = new SimpleDateFormat();
		fixture.messageSource = mock(MessageSource.class);
	}

	/**
	 * Perform post-test clean-up.
	 *
	 * @throws Exception
	 *         if the clean-up fails for some reason
	 *
	 * @see TestCase#tearDown()
	 *
	 * @generatedBy CodePro at 9/3/12 5:14 PM
	 */
	@After
	public void tearDown()
		throws Exception {
		super.tearDown();
	}
}
