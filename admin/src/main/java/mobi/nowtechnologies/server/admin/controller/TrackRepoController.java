package mobi.nowtechnologies.server.admin.controller;

import mobi.nowtechnologies.server.service.TrackRepoService;
import mobi.nowtechnologies.server.shared.dto.PageListDto;
import mobi.nowtechnologies.server.trackrepo.dto.IngestWizardDataDto;
import mobi.nowtechnologies.server.trackrepo.dto.SearchTrackDto;
import mobi.nowtechnologies.server.trackrepo.dto.TrackDto;
import mobi.nowtechnologies.server.trackrepo.enums.AudioResolution;
import org.json.JSONArray;
import org.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefaults;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.async.WebAsyncTask;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.util.ArrayList;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

@Controller
public class TrackRepoController extends AbstractCommonController{
	private static final Logger LOGGER = LoggerFactory.getLogger(TrackRepoController.class);
	public static final String TRACK_REPO_FILES_URL = "trackRepoFilesURL";
    private static final int DEFAULT_EXECUTOR_TIMEOUT = 60000;
	
	private TrackRepoService trackRepoService;
	private String trackRepoFilesURL;
    private Integer executorTimeout = DEFAULT_EXECUTOR_TIMEOUT;
	
	public void setTrackRepoService(TrackRepoService trackRepoService) {
		this.trackRepoService = trackRepoService;
	}

	public void setTrackRepoFilesURL(String trackRepoFilesURL) {
		this.trackRepoFilesURL = trackRepoFilesURL;
	}

    public void setExecutorTimeout(Integer executorTimeout) {
        this.executorTimeout = executorTimeout;
    }

    @InitBinder({SearchTrackDto.SEARCH_TRACK_DTO, TrackDto.TRACK_DTO})
	public void initBinder(WebDataBinder binder) {
        binder.setAutoGrowCollectionLimit(Integer.MAX_VALUE);
		binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
	}

    @InitBinder({IngestWizardDataDto.INGEST_WIZARD_DATA_DTO})
    public void initBinderForIngestWizardDataDto(WebDataBinder binder) {
        binder.setAutoGrowCollectionLimit(Integer.MAX_VALUE);
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
	public @ResponseBody WebAsyncTask<TrackDto> encodeTrack(final @ModelAttribute(TrackDto.TRACK_DTO) TrackDto track) {
		LOGGER.debug("input encodeTrack(trackId) ('/tracks/encode') request: [{}]", new Object[] { track });

        WebAsyncTask<TrackDto> encodeTask = new WebAsyncTask<TrackDto>(executorTimeout, new Callable<TrackDto>() {
            @Override
            public TrackDto call() throws Exception {
                TrackDto result = trackRepoService.encode(track);
                return result;
            }
        });
        encodeTask.onTimeout(new Callable<TrackDto>() {
            @Override
            public TrackDto call() throws Exception {
                SearchTrackDto criteria = new SearchTrackDto();
                criteria.setTrackIds(Collections.singletonList(track.getId().intValue()));

                return trackRepoService.find(criteria, new PageRequest(0, 10)).getList().get(0);
            }
        });

		return encodeTask;
	}

    @RequestMapping(value = "/tracks/encode2", method = RequestMethod.POST)
      public @ResponseBody Callable<String> encodeTrack2(@RequestParam Map<String, String> params) {

        final List<TrackDto> tracks = mapParamsToTracks(params);

        return new Callable<String>() {
            @Override
            public String call() throws Exception {
                Map<String, List<TrackDto>> rez = trackRepoService.encodeTracks(tracks);

                List<TrackDto> fails = rez.get("fail");
                List<TrackDto> successes = rez.get("success");
                JSONObject result = new JSONObject();
                JSONArray jsonArray = new JSONArray();

                if  ((fails != null) && (fails.size() != 0)) {

                    for (TrackDto fail : fails) {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("id", fail.getId());
                        jsonObject.put("isrc", fail.getIsrc());
                        jsonArray.put(jsonObject);
                    }
                }
                result.put("fail", jsonArray);

                jsonArray = new JSONArray();
                if ((successes != null) && (successes.size() != 0)) {

                    for (TrackDto success : successes) {
                        JSONObject jsonObject = success.toJson();
                        jsonArray.put(jsonObject);
                    }
                }
                result.put("success", jsonArray);

                return result.toString();
            }
        };
    }
	
	@RequestMapping(value = "/tracks/pull", method = RequestMethod.POST)
	public @ResponseBody WebAsyncTask<TrackDto> pullTrack(final @Valid @ModelAttribute(TrackDto.TRACK_DTO) TrackDto track) {
		LOGGER.debug("input pullTrack(trackId) ('/tracks/pull') request", new Object[] { track });

        WebAsyncTask<TrackDto> pullTask = new WebAsyncTask<TrackDto>(executorTimeout, new Callable<TrackDto>() {
            @Override
            public TrackDto call() throws Exception {
                return trackRepoService.pull(track);
            }
        });
        pullTask.onTimeout(new Callable<TrackDto>() {
            @Override
            public TrackDto call() throws Exception {
                SearchTrackDto criteria = new SearchTrackDto();
                criteria.setTrackIds(Collections.singletonList(track.getId().intValue()));

                return trackRepoService.find(criteria, new PageRequest(0, 10)).getList().get(0);
            }
        });

		return pullTask;
	}

	@RequestMapping(value = "/drops", method = RequestMethod.GET)
	public ModelAndView getDrops(@RequestParam(value="ingestors", required=false) String[] ingestors) {
		LOGGER.debug("input getDrops({}) request", Arrays.toString(ingestors));

		IngestWizardDataDto data = trackRepoService.getDrops(ingestors);

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

    private List<TrackDto> mapParamsToTracks(final Map<String, String> map){
        if (map.size() == 0)
            return null;

        List<TrackDto> tracks = new ArrayList<TrackDto>();

        for (int i = 0; i < map.size() / 4; i++){
            TrackDto trackDto = new TrackDto();
            String id = map.get("TRACK_DTO[" + i + "][id]");
            String isrc = map.get("TRACK_DTO[" + i + "][isrc]");
            String resolution = map.get("TRACK_DTO[" + i + "][resolution]");
            String license = map.get("TRACK_DTO[" + i + "][license]");

            if (license.equals("on"))
                trackDto.setLicensed(true);

            if (resolution.equals(AudioResolution.RATE_96.name()))
                trackDto.setResolution(AudioResolution.RATE_96);
            else
                trackDto.setResolution(AudioResolution.RATE_48);

            trackDto.setId(Long.parseLong(id));
            trackDto.setIsrc(isrc);
            tracks.add(trackDto);
        }
        return tracks;
    }
}
