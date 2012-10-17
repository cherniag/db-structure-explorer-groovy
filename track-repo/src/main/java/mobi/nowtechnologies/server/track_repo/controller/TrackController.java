package mobi.nowtechnologies.server.track_repo.controller;

import java.util.Date;

import mobi.nowtechnologies.server.shared.dto.PageListDto;
import mobi.nowtechnologies.server.shared.dto.TrackDto;
import mobi.nowtechnologies.server.shared.dto.admin.SearchTrackDto;
import mobi.nowtechnologies.server.track_repo.service.TrackService;

import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefaults;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

/**
 * 
 * @author Alexander Kolpakov (akolpakov)
 *
 */
@Controller
public class TrackController extends AbstractCommonController{

	private TrackService trackService;
	
	public void setTrackService(TrackService trackService) {
		this.trackService = trackService;
	}
	
	@InitBinder({SearchTrackDto.SEARCH_TRACK_DTO})
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
	}

	@RequestMapping(value = "/tracks/{trackId}/encode", method = RequestMethod.POST)
	public @ResponseBody TrackDto encode(@PathVariable("trackId")Long trackId, 
			@RequestParam(value="isHighRate", required = false) Boolean isHighRate, @RequestParam(value="licensed", required = false) Boolean licensed) {

		return trackService.encode(trackId, isHighRate, licensed);
	}
	
	@RequestMapping(value = "/tracks", method = RequestMethod.GET)
	public @ResponseBody PageListDto<TrackDto> find(@RequestParam(value="query", required = false) String query, @ModelAttribute(SearchTrackDto.SEARCH_TRACK_DTO) SearchTrackDto searchTrackDto
			, @PageableDefaults(pageNumber = 0, value = 10) Pageable page) {

		return query != null ? trackService.find(query, page) : trackService.find(searchTrackDto, page);
	}
	
	@RequestMapping(value = "/tracks/{trackId}/pull", method = RequestMethod.GET)
	public @ResponseBody TrackDto pull(@PathVariable("trackId")Long trackId) {
		return trackService.pull(trackId);
	}
}
