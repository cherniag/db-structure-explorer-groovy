package mobi.nowtechnologies.server.admin.controller;

import mobi.nowtechnologies.server.service.TrackRepoService;
import mobi.nowtechnologies.server.shared.dto.PageListDto;
import mobi.nowtechnologies.server.trackrepo.dto.IngestWizardDataDto;
import mobi.nowtechnologies.server.trackrepo.dto.SearchTrackDto;
import mobi.nowtechnologies.server.trackrepo.dto.TrackDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefaults;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.util.Date;

@Controller
public class TrackRepoController extends AbstractCommonController{
	private static final Logger LOGGER = LoggerFactory.getLogger(TrackRepoController.class);
	public static final String TRACK_REPO_FILES_URL = "trackRepoFilesURL";
	
	private TrackRepoService trackRepoService;
	private String trackRepoFilesURL;
	
	public void setTrackRepoService(TrackRepoService trackRepoService) {
		this.trackRepoService = trackRepoService;
	}

	public void setTrackRepoFilesURL(String trackRepoFilesURL) {
		this.trackRepoFilesURL = trackRepoFilesURL;
	}
	
	@InitBinder({SearchTrackDto.SEARCH_TRACK_DTO, TrackDto.TRACK_DTO})
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
	}

	@RequestMapping(value = "/tracks/list", method = RequestMethod.GET)
	public ModelAndView findTracks(@RequestParam(value = "query", required = false) String query,
                                   @ModelAttribute(SearchTrackDto.SEARCH_TRACK_DTO) SearchTrackDto searchTrackDto,
                                   BindingResult bindingResult,
                                   @PageableDefaults(pageNumber = 0, value = 10) Pageable pageable) {
		LOGGER.debug("input findTracks(query, searchTrackDto): [{}]", new Object[] { searchTrackDto });
		
		ModelAndView modelAndView = new ModelAndView("tracks/tracks");
		if (bindingResult.hasErrors()) {
            modelAndView.getModelMap().put(SearchTrackDto.SEARCH_TRACK_DTO, searchTrackDto);
        } else {
			PageListDto<TrackDto> tracks =  query != null ? trackRepoService.find(query, pageable) : trackRepoService.find(searchTrackDto, pageable);

			modelAndView.addObject(PageListDto.PAGE_LIST_DTO, tracks);
			modelAndView.addObject(TRACK_REPO_FILES_URL, trackRepoFilesURL);
		}

		LOGGER.info("output findTracks(query, searchTrackDto): [{}]", new Object[] { modelAndView });
		return modelAndView ;
	}
	
	@RequestMapping(value = "/tracks/encode", method = RequestMethod.POST)
	public ModelAndView encodeTrack(@ModelAttribute(TrackDto.TRACK_DTO) TrackDto track) {
		LOGGER.debug("input encodeTrack(trackId) ('/tracks/encode') request: [{}]", new Object[] { track });

		track = trackRepoService.encode(track);

        ModelAndView modelAndView = new ModelAndView();
		modelAndView.addObject(TrackDto.TRACK_DTO, track);

		return modelAndView;
	}
	
	@RequestMapping(value = "/tracks/pull", method = RequestMethod.POST)
	public ModelAndView pullTrack(@Valid @ModelAttribute(TrackDto.TRACK_DTO) TrackDto track) {
		LOGGER.debug("input pullTrack(trackId) ('/tracks/pull') request", new Object[] { track });

		track = trackRepoService.pull(track);

		ModelAndView modelAndView = new ModelAndView();
		modelAndView.addObject(TrackDto.TRACK_DTO, track);

		return modelAndView;
	}

	@RequestMapping(value = "/drops", method = RequestMethod.GET)
	public ModelAndView getDrops() {
		LOGGER.debug("input getDrops() request");

		IngestWizardDataDto data = trackRepoService.getDrops();

		ModelAndView modelAndView = new ModelAndView("tracks/drops");
		modelAndView.addObject(IngestWizardDataDto.INGEST_WIZARD_DATA_DTO, data);
		modelAndView.addObject(IngestWizardDataDto.ACTION, "/drops/select");

		return modelAndView;
	}

    @RequestMapping(value = "/drops/select", method = RequestMethod.POST)
    public ModelAndView selectDrops(@Valid @ModelAttribute(IngestWizardDataDto.INGEST_WIZARD_DATA_DTO) IngestWizardDataDto data) {
        LOGGER.debug("input selectDrops(data) request, [{}]", new Object[] { data });

        data = trackRepoService.selectDrops(data);

        ModelAndView modelAndView = new ModelAndView("tracks/drops");
        modelAndView.addObject(IngestWizardDataDto.INGEST_WIZARD_DATA_DTO, data);
        modelAndView.addObject(IngestWizardDataDto.ACTION, "/drops/tracks/select");

        return modelAndView;
    }

    @RequestMapping(value = "/drops/tracks/select", method = RequestMethod.POST)
    public ModelAndView selectTrackDrops(@Valid @ModelAttribute(IngestWizardDataDto.INGEST_WIZARD_DATA_DTO) IngestWizardDataDto data) {
        LOGGER.debug("input selectDrops(data) request, [{}]", new Object[] { data });

        data = trackRepoService.selectTrackDrops(data);

        ModelAndView modelAndView = new ModelAndView("tracks/drops");
        modelAndView.addObject(IngestWizardDataDto.INGEST_WIZARD_DATA_DTO, data);
        modelAndView.addObject(IngestWizardDataDto.ACTION, "/drops/commit");

        return modelAndView;
    }

    @RequestMapping(value = "/drops/commit", method = RequestMethod.POST)
    public ModelAndView commitDrops(@Valid @ModelAttribute(IngestWizardDataDto.INGEST_WIZARD_DATA_DTO) IngestWizardDataDto data) {
        LOGGER.debug("input commitDrops(data) request, [{}]", new Object[] { data });

        trackRepoService.commitDrops(data);

        ModelAndView modelAndView = new ModelAndView("redirect:/tracks/list");

        return modelAndView;
    }
}
