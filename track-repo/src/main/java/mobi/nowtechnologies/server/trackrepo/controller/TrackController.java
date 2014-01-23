package mobi.nowtechnologies.server.trackrepo.controller;

import java.util.Collections;
import java.util.Date;
import java.util.concurrent.Callable;

import mobi.nowtechnologies.server.shared.dto.PageListDto;
import mobi.nowtechnologies.server.trackrepo.domain.Territory;
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
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.async.WebAsyncTask;

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
    private Integer executorTimeout;
	
	public void setTrackService(TrackService trackService) {
		this.trackService = trackService;
	}
	
	public void setResourceFileDtoBuilder(ResourceFileDtoBuilder resourceFileDtoBuilder) {
		this.resourceFileDtoBuilder = resourceFileDtoBuilder;
	}

    public void setExecutorTimeout(Integer executorTimeout) {
        this.executorTimeout = executorTimeout;
    }

    @InitBinder({SearchTrackDto.SEARCH_TRACK_DTO, TrackDto.TRACK_DTO})
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
	}

	@RequestMapping(value = "/tracks/{trackId}/encode", method = RequestMethod.POST)
	public @ResponseBody WebAsyncTask<TrackDto> encode(final @PathVariable("trackId")Long trackId,
			final @RequestParam(value="isHighRate", required = false) Boolean isHighRate,final @RequestParam(value="licensed", required = false) Boolean licensed) {

        WebAsyncTask<TrackDto> encodeTask = new WebAsyncTask<TrackDto>(executorTimeout, new Callable<TrackDto>() {
            @Override
            public TrackDto call() throws Exception {
                return new TrackDtoMapper(trackService.encode(trackId, isHighRate, licensed));
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
		LOGGER.info("pull(trackId:{})", trackId);
        WebAsyncTask<TrackDto> pullTask = new WebAsyncTask<TrackDto>(executorTimeout, new Callable<TrackDto>() {
            @Override
            public TrackDto call() throws Exception {
                try {
                	LOGGER.info("Start WebAsyncTask: pullig track with id {}", trackId);
                    Track track = trackService.pull(trackId);
                    TrackDtoMapper trackDto = new TrackDtoMapper(track);

                    if(track.getStatus() == TrackStatus.PUBLISHED){
                        trackDto.setFiles(resourceFileDtoBuilder.build(track));
                        Territory publishTerritory = track.getValidTerritory(Territory.GB_TERRITORY);
                        if (publishTerritory != null) {
                        	LOGGER.info("Change publishDate to Terrytory StartDate: {}", publishTerritory.getStartDate());
                            trackDto.setPublishDate(publishTerritory.getStartDate());
                        }
                    }

                    trackDto.setPublishDate(fixDateJson(trackDto.getPublishDate()));//TODO: date json serialization hot fix. 
                    
                    LOGGER.info("Finish WebAsyncTask: pullig track with id {}", trackId);
                    return trackDto;
                } catch (Exception e) {
                    LOGGER.error("Error while pulling track with ID " + trackId + ": " + e.getMessage(), e);
                    //throw new RuntimeException(e.getMessage());
                    return null;
                }
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
	
	/**
	 * Hot and fast fix for date JSON serialization with Jackson (DateTimeFormat annotation is useless) to proper format: yyyy-MM-dd. 
	 * It works. Don't ask why.
	 * Don't like it? Fell free to implement proper solution!
	 */
	private Date fixDateJson(Date date){
		return new java.sql.Date(date.getTime());
	}
}
