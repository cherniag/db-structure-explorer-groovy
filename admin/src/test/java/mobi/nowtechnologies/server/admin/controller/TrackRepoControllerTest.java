/**
 * 
 */
package mobi.nowtechnologies.server.admin.controller;

import mobi.nowtechnologies.server.factory.TrackDtoFactory;
import mobi.nowtechnologies.server.service.TrackRepoService;
import mobi.nowtechnologies.server.service.exception.ExternalServiceException;
import mobi.nowtechnologies.server.service.exception.ServiceException;
import mobi.nowtechnologies.server.shared.dto.PageListDto;
import mobi.nowtechnologies.server.trackrepo.dto.SearchTrackDto;
import mobi.nowtechnologies.server.trackrepo.dto.TrackDto;
import org.junit.Before;
import org.junit.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.web.context.request.async.WebAsyncTask;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

// @author Mayboroda Dmytro
public class TrackRepoControllerTest {
	
	private TrackRepoController trackRepositoryController;
	private TrackRepoService trackRepoService;
	
	@Before
	public void before() {
		trackRepoService = mock(TrackRepoService.class);
		trackRepositoryController = new TrackRepoController();
		trackRepositoryController.setTrackRepoService(trackRepoService);
		trackRepositoryController.setTrackRepoFilesURL("/track/repo/url");
	}
	
	@Test
	public void searchTrackWithCriteria() {
		SearchTrackDto searchTrackDto = new SearchTrackDto();
		searchTrackDto.setArtist("SWEDISH");
		searchTrackDto.setTitle("Save the world");
		PageListDto<TrackDto> trackDtos = TrackDtoFactory.getTrackPage(3);
		when(trackRepoService.find(any(SearchTrackDto.class), any(Pageable.class))).thenReturn(trackDtos);
		
		Pageable page = new PageRequest(0, 30);
		ModelAndView modelAndView = trackRepositoryController.findTracks(null, searchTrackDto, new BeanPropertyBindingResult(searchTrackDto, SearchTrackDto.SEARCH_TRACK_DTO), page);
		
		assertNotNull(modelAndView);
		Map<String, Object> model = modelAndView.getModel();
		assertEquals(trackDtos, model.get(PageListDto.PAGE_LIST_DTO));
		assertNotNull(model.get(TrackRepoController.TRACK_REPO_FILES_URL));
		assertEquals("tracks/tracks", modelAndView.getViewName());
	}
	
	@Test
	public void searchTrackWithoutCriteria() {
		PageListDto<TrackDto> emptyTrackDtos = TrackDtoFactory.getEmptyTrackPage();
		when(trackRepoService.find(any(SearchTrackDto.class), any(Pageable.class))).thenReturn(emptyTrackDtos);
		SearchTrackDto searchTrackDto = new SearchTrackDto();
		
		Pageable page = new PageRequest(0, 30);
		ModelAndView modelAndView = trackRepositoryController.findTracks(null ,searchTrackDto, new BeanPropertyBindingResult(searchTrackDto, SearchTrackDto.SEARCH_TRACK_DTO), page);
		
		assertNotNull(modelAndView);
		Map<String, Object> model = modelAndView.getModel();
		assertEquals(emptyTrackDtos, model.get(PageListDto.PAGE_LIST_DTO));
		assertNotNull(model.get(TrackRepoController.TRACK_REPO_FILES_URL));
		assertEquals("tracks/tracks", modelAndView.getViewName());
	}
	
	@Test
	public void testEncodeTrack_Successfull() throws Exception {
		TrackDto resultTrackDto = TrackDtoFactory.anyTrackDto();
		TrackDto configTrackDto = new TrackDto(resultTrackDto);
		
		when(trackRepoService.encode(any(TrackDto.class))).thenReturn(resultTrackDto);
		
		WebAsyncTask<TrackDto> task = trackRepositoryController.encodeTrack(configTrackDto);
		
		assertNotNull(task);
		assertEquals(resultTrackDto, (TrackDto)task.getCallable().call());
	}
	
	@Test(expected=ExternalServiceException.class)
	public void testEncodeTrack_Exception() throws Exception {
		TrackDto resultTrackDto = TrackDtoFactory.anyTrackDto();
		TrackDto configTrackDto = new TrackDto(resultTrackDto);
		
		when(trackRepoService.encode(any(TrackDto.class))).thenThrow(new ExternalServiceException("tracks.encode.error", "Couldn't encode track"));

        WebAsyncTask<TrackDto> task = trackRepositoryController.encodeTrack(configTrackDto);
        task.getCallable().call();
	}
	
	@Test
	public void testPullTrack_Successfull() throws Exception {
		TrackDto resultTrackDto = TrackDtoFactory.anyTrackDto();
		TrackDto configTrackDto = new TrackDto(resultTrackDto);
		
		when(trackRepoService.pull(any(TrackDto.class))).thenReturn(resultTrackDto);

        WebAsyncTask<TrackDto> task = trackRepositoryController.pullTrack(configTrackDto);
		
		assertNotNull(task);
		assertEquals(resultTrackDto, (TrackDto)task.getCallable().call());
	}
	
	@Test(expected=ServiceException.class)
	public void testPullTrack_Exception() throws Exception {
		TrackDto resultTrackDto = TrackDtoFactory.anyTrackDto();
		TrackDto configTrackDto = new TrackDto(resultTrackDto);
		
		when(trackRepoService.pull(any(TrackDto.class))).thenThrow(new ServiceException("tracks.pull.error", "Couldn't pull track"));

        WebAsyncTask<TrackDto> task = trackRepositoryController.pullTrack(configTrackDto);
        task.getCallable().call();
	}

	@Test
	public void shouldReturnTrackRepoUrlWhenTrackRepoUrl() {
		//given
		trackRepositoryController.setTrackRepoUrl("http://localhost/track-repo");

		//when
		String trackRepoUrl = trackRepositoryController.getTrackRepoReportingOptionsUrl();

		//then
		assertThat(trackRepoUrl, is("http://localhost/track-repo/reportingOptions"));
	}

	@Test
	public void shouldReturnTrackRepoUrlWhenTrackRepoUrlHasSlashEnding() {
		//given
		trackRepositoryController.setTrackRepoUrl("http://localhost/track-repo/");

		//when
		String trackRepoUrl = trackRepositoryController.getTrackRepoReportingOptionsUrl();

		//then
		assertThat(trackRepoUrl, is("http://localhost/track-repo/reportingOptions"));
	}
}