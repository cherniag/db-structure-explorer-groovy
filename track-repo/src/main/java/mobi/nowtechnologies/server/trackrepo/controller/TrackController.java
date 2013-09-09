package mobi.nowtechnologies.server.trackrepo.controller;

import mobi.nowtechnologies.server.shared.dto.PageListDto;
import mobi.nowtechnologies.server.trackrepo.domain.Track;
import mobi.nowtechnologies.server.trackrepo.dto.SearchTrackDto;
import mobi.nowtechnologies.server.trackrepo.dto.TrackDto;
import mobi.nowtechnologies.server.trackrepo.dto.TrackDtoMapper;
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
import org.springframework.web.context.request.async.WebAsyncTask;

import java.util.Collections;
import java.util.Date;
import java.util.concurrent.Callable;

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

	@InitBinder({SearchTrackDto.SEARCH_TRACK_DTO, TrackDto.TRACK_DTO})
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
	}

	@RequestMapping(value = "/tracks/{trackId}/encode", method = RequestMethod.POST)
	public @ResponseBody WebAsyncTask<TrackDto> encode(final @PathVariable("trackId")Long trackId,
			final @RequestParam(value="isHighRate", required = false) Boolean isHighRate,final @RequestParam(value="licensed", required = false) Boolean licensed) {

        WebAsyncTask<TrackDto> encodeTask = new WebAsyncTask<TrackDto>(new Callable<TrackDto>() {
            @Override
            public TrackDto call() throws Exception {
               // return new TrackDtoMapper(trackService.encode(trackId, isHighRate, licensed));
                Thread.sleep(4000);

                Track track = new Track();
                track.setStatus(TrackStatus.ENCODED);

                return new TrackDtoMapper(track);
            }
        });
        encodeTask.onTimeout(new Callable<TrackDto>() {
            @Override
            public TrackDto call() throws Exception {
                SearchTrackDto criteria = new SearchTrackDto();
                criteria.setTrackIds(Collections.singletonList(trackId.intValue()));

                return new TrackDtoMapper(trackService.find(criteria, null).getContent().get(0));
            }
        });

		return encodeTask;
	}
	
	@RequestMapping(value = "/tracks", method = RequestMethod.GET)
	public @ResponseBody PageListDto<? extends TrackDto> find(@RequestParam(value="query", required = false) String query, @ModelAttribute(SearchTrackDto.SEARCH_TRACK_DTO) SearchTrackDto searchTrackDto
			, @PageableDefaults(pageNumber = 0, value = 10) Pageable page) {

		return TrackDtoMapper.toPage(query != null ? trackService.find(query, page) : trackService.find(searchTrackDto, page));
	}
	
	@RequestMapping(value = "/tracks/{trackId}/pull", method = RequestMethod.GET)
	public @ResponseBody WebAsyncTask<TrackDto> pull(final @PathVariable("trackId")Long trackId) {
        WebAsyncTask<TrackDto> pullTask = new WebAsyncTask<TrackDto>(new Callable<TrackDto>() {
            @Override
            public TrackDto call() throws Exception {
//                try {
//                    Track track = trackService.pull(trackId);
//                    TrackDtoMapper trackDto = new TrackDtoMapper(track);
//
//                    if(track.getStatus() == TrackStatus.PUBLISHED){
//                        trackDto.setFiles(resourceFileDtoBuilder.build(track));
//                        Territory publishTerritory = track.getValidTerritory(Territory.GB_TERRITORY);
//                        if (publishTerritory != null) {
//                            trackDto.setPublishDate(publishTerritory.getStartDate());
//                        }
//                    }
//
//                    return trackDto;
//                } catch (Exception e) {
//                    LOGGER.error("Cannot pull encoded track.", e);
//                    throw new RuntimeException(e.getMessage());
//                }
                Thread.sleep(4000);

                Track track = new Track();
                track.setStatus(TrackStatus.PUBLISHED);
                track.setPublishDate(new Date());

                return new TrackDtoMapper(track);
            }
        });
        pullTask.onTimeout(new Callable<TrackDto>() {
            @Override
            public TrackDto call() throws Exception {
                SearchTrackDto criteria = new SearchTrackDto();
                criteria.setTrackIds(Collections.singletonList(trackId.intValue()));

                return new TrackDtoMapper(trackService.find(criteria, null).getContent().get(0));
            }
        });

        return pullTask;
	}
}
