package mobi.nowtechnologies.server.trackrepo.controller;

import java.util.Date;

import mobi.nowtechnologies.server.shared.dto.PageListDto;
import mobi.nowtechnologies.server.trackrepo.domain.Territory;
import mobi.nowtechnologies.server.trackrepo.domain.Track;
import mobi.nowtechnologies.server.trackrepo.dto.SearchTrackDto;
import mobi.nowtechnologies.server.trackrepo.dto.TrackDto;
import mobi.nowtechnologies.server.trackrepo.dto.TrackDtoExt;
import mobi.nowtechnologies.server.trackrepo.dto.builder.ResourceFileDtoBuilder;
import mobi.nowtechnologies.server.trackrepo.enums.TrackStatus;
import mobi.nowtechnologies.server.trackrepo.service.TrackService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
	private static final Logger LOGGER = LoggerFactory.getLogger(TrackController.class);

	private TrackService trackService;
	private ResourceFileDtoBuilder resourceFileDtoBuilder;
	
	public void setTrackService(TrackService trackService) {
		this.trackService = trackService;
	}
	
	public void setResourceFileDtoBuilder(ResourceFileDtoBuilder resourceFileDtoBuilder) {
		this.resourceFileDtoBuilder = resourceFileDtoBuilder;
	}

	@InitBinder({SearchTrackDto.SEARCH_TRACK_DTO})
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
	}

	@RequestMapping(value = "/tracks/{trackId}/encode", method = RequestMethod.POST)
	public @ResponseBody TrackDto encode(@PathVariable("trackId")Long trackId, 
			@RequestParam(value="isHighRate", required = false) Boolean isHighRate, @RequestParam(value="licensed", required = false) Boolean licensed) {

		Track track = trackService.encode(trackId, isHighRate, licensed);
		
		return new TrackDtoExt(track);
	}
	
	@RequestMapping(value = "/tracks", method = RequestMethod.GET)
	public @ResponseBody PageListDto<? extends TrackDto> find(@RequestParam(value="query", required = false) String query, @ModelAttribute(SearchTrackDto.SEARCH_TRACK_DTO) SearchTrackDto searchTrackDto
			, @PageableDefaults(pageNumber = 0, value = 10) Pageable page) {

		return TrackDtoExt.toPage(query != null ? trackService.find(query, page) : trackService.find(searchTrackDto, page));
	}
	
	@RequestMapping(value = "/tracks/{trackId}/pull", method = RequestMethod.GET)
	public @ResponseBody TrackDto pull(@PathVariable("trackId")Long trackId) {
		try {
			Track track = trackService.pull(trackId);
			TrackDtoExt trackDto = new TrackDtoExt(track);
			
			if(track.getStatus() == TrackStatus.ENCODED){
				trackDto.setFiles(resourceFileDtoBuilder.build(track.getIsrc()));
				Territory publishTerritory = track.getValidTerritory(Territory.GB_TERRITORY);
				if (publishTerritory != null) {
					trackDto.setPublishDate(publishTerritory.getStartDate());
				}
			}
			
			return trackDto;
		} catch (Exception e) {
			LOGGER.error("Cannot pull encoded track.", e);
			throw new RuntimeException("Cannot pull encoded track.");
		}
	}
}
