package mobi.nowtechnologies.server.admin.controller;

import mobi.nowtechnologies.server.dto.CommunityDto;
import mobi.nowtechnologies.server.dto.asm.CommunityDtoAsm;
import mobi.nowtechnologies.server.service.CommunityService;
import mobi.nowtechnologies.server.service.TrackRepoService;
import mobi.nowtechnologies.server.shared.dto.PageListDto;
import mobi.nowtechnologies.server.admin.util.EnumEditor;
import mobi.nowtechnologies.server.trackrepo.dto.IngestWizardDataDto;
import mobi.nowtechnologies.server.trackrepo.dto.SearchTrackDto;
import mobi.nowtechnologies.server.trackrepo.dto.TrackDto;
import mobi.nowtechnologies.server.trackrepo.dto.TrackReportingOptionsDto;
import mobi.nowtechnologies.server.trackrepo.enums.AudioResolution;
import mobi.nowtechnologies.server.trackrepo.enums.ReportingType;
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
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.WebAsyncTask;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.Callable;

import static mobi.nowtechnologies.server.shared.dto.PageListDto.PAGE_LIST_DTO;
import static mobi.nowtechnologies.server.trackrepo.dto.IngestWizardDataDto.ACTION;
import static mobi.nowtechnologies.server.trackrepo.dto.IngestWizardDataDto.INGEST_WIZARD_DATA_DTO;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.web.bind.annotation.RequestMethod.*;
import static mobi.nowtechnologies.server.trackrepo.dto.SearchTrackDto.SEARCH_TRACK_DTO;
import static mobi.nowtechnologies.server.trackrepo.dto.TrackDto.TRACK_DTO;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
public class TrackRepoController extends AbstractCommonController{
	private static final Logger LOGGER = LoggerFactory.getLogger(TrackRepoController.class);

	public static final String TRACK_REPO_FILES_URL = "trackRepoFilesURL";
    private static final int DEFAULT_EXECUTOR_TIMEOUT = 60000;

    @Resource(name = "service.communityService") private CommunityService communityService;
    @Resource private CommunityDtoAsm communityDtoAsm;
    private TrackRepoService trackRepoService;

    private String trackRepoUrl;
	private String trackRepoFilesURL;
    private Integer executorTimeout = DEFAULT_EXECUTOR_TIMEOUT;

	public void setTrackRepoService(TrackRepoService trackRepoService) {
		this.trackRepoService = trackRepoService;
	}

    public void setTrackRepoUrl(String trackRepoUrl) {
        this.trackRepoUrl = trackRepoUrl;
    }

    public void setTrackRepoFilesURL(String trackRepoFilesURL) {
		this.trackRepoFilesURL = trackRepoFilesURL;
	}

    public void setExecutorTimeout(Integer executorTimeout) {
        this.executorTimeout = executorTimeout;
    }

    @InitBinder({SEARCH_TRACK_DTO, TRACK_DTO})
    public void initBinder(WebDataBinder binder) {
        binder.setAutoGrowCollectionLimit(Integer.MAX_VALUE);
		binder.registerCustomEditor(Date.class, new CustomDateEditor(new SimpleDateFormat(URL_DATE_FORMAT), true));
        binder.registerCustomEditor(ReportingType.class, new EnumEditor(ReportingType.class));
    }

    @InitBinder({INGEST_WIZARD_DATA_DTO})
    public void initBinderForIngestWizardDataDto(WebDataBinder binder) {
        binder.setAutoGrowCollectionLimit(Integer.MAX_VALUE);
    }

    @ModelAttribute("liveCommunities")
    public List<CommunityDto> getLiveCommunities(){
        return communityDtoAsm.toCommunityDtos(communityService.getLiveCommunities());
    }

	@RequestMapping(value = "/tracks/list", method = GET)
	public ModelAndView findTracks(@RequestParam(value = "query", required = false) String query,
                                   @ModelAttribute(SEARCH_TRACK_DTO) SearchTrackDto searchTrackDto,
                                   BindingResult bindingResult,
                                   @PageableDefaults(pageNumber = 0, value = 10) Pageable pageable) {
		LOGGER.debug("input findTracks(query, searchTrackDto): [{}]", new Object[] { searchTrackDto });

		ModelAndView modelAndView = new ModelAndView("tracks/tracks");
		if (bindingResult.hasErrors()) {
            modelAndView.getModelMap().put(SEARCH_TRACK_DTO, searchTrackDto);
        } else {
			PageListDto<TrackDto> tracks =  query != null ? trackRepoService.find(query, pageable) : trackRepoService.find(searchTrackDto, pageable);

			modelAndView.addObject(PAGE_LIST_DTO, tracks);
            modelAndView.addObject("mediaType", searchTrackDto.getMediaType());
			modelAndView.addObject(TRACK_REPO_FILES_URL, trackRepoFilesURL);
		}

        LOGGER.debug("output findTracks(query, searchTrackDto): [{}]", new Object[]{modelAndView});
        return modelAndView;
    }
	
	@RequestMapping(value = "/tracks/encode", method = POST)
    public
    @ResponseBody
    WebAsyncTask<TrackDto> encodeTrack(final @ModelAttribute(TRACK_DTO) TrackDto track) {
        LOGGER.debug("input encodeTrack(trackId) ('/tracks/encode') request: [{}]", new Object[]{track});

        WebAsyncTask<TrackDto> encodeTask = new WebAsyncTask<TrackDto>(executorTimeout, new Callable<TrackDto>() {
            @Override
            public TrackDto call() throws Exception {
                LOGGER.info("Start WebAsyncTask: encoding track with id {}", track.getId());
                TrackDto result = trackRepoService.encode(track);
                LOGGER.info("Finish WebAsyncTask: encoding track with id {}", track.getId());
                return result;
            }
        });
        encodeTask.onTimeout(new Callable<TrackDto>() {
            @Override
            public TrackDto call() throws Exception {
                LOGGER.warn("On encodeTrack timeout for: {}", track.getId());
                SearchTrackDto criteria = new SearchTrackDto();
                criteria.setTrackIds(Collections.singletonList(track.getId().intValue()));

                return trackRepoService.find(criteria, new PageRequest(0, 10)).getList().get(0);
            }
        });

		return encodeTask;
	}

    @RequestMapping(value = "/tracks/encode2", method = POST)
      public @ResponseBody Callable<String> encodeTrack2(@RequestParam Map<String, String> params) {

        final List<TrackDto> tracks = mapParamsToTracks(params);

        return new Callable<String>() {
            @Override
            public String call() throws Exception {
                LOGGER.debug("Start encode2 : {}", tracks);
                Map<String, List<TrackDto>> rez = trackRepoService.encodeTracks(tracks);

                List<TrackDto> fails = rez.get("fail");
                List<TrackDto> successes = rez.get("success");
                //FIXME: remove serialization to JSON. It is performed automatically with @ResponseBody
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
	
	@RequestMapping(value = "/tracks/pull", method = POST)
    public
    @ResponseBody
    WebAsyncTask<TrackDto> pullTrack(final @Valid @ModelAttribute(TRACK_DTO) TrackDto track) {
        LOGGER.debug("input pullTrack(trackId) ('/tracks/pull') request", new Object[]{track});

        WebAsyncTask<TrackDto> pullTask = new WebAsyncTask<TrackDto>(executorTimeout, new Callable<TrackDto>() {
            @Override
            public TrackDto call() throws Exception {
            	try{
            		LOGGER.info("Start WebAsyncTask: pulling track with id {}", track.getId());
            		TrackDto ret = trackRepoService.pull(track);
            		LOGGER.info("Finish WebAsyncTask: pulling track with id {}", track.getId());
            		return ret;
            	}catch(Exception e){
            		LOGGER.error("Error while pulling track with ID " + track.getId() + ": " + e.getMessage(), e);
            		throw e;
            	}
            }
        });
        pullTask.onTimeout(new Callable<TrackDto>() {
            @Override
            public TrackDto call() throws Exception {
                LOGGER.warn("On pullTrack timeout for: {}", track.getId());
                SearchTrackDto criteria = new SearchTrackDto();
                criteria.setTrackIds(Collections.singletonList(track.getId().intValue()));

                return trackRepoService.find(criteria, new PageRequest(0, 10)).getList().get(0);
            }
        });

		return pullTask;
	}

	@RequestMapping(value = "/drops", method = GET)
	public ModelAndView getDrops(@RequestParam(value="ingestors", required=false) String[] ingestors) {
		LOGGER.debug("input getDrops({}) request", Arrays.toString(ingestors));

		IngestWizardDataDto data = trackRepoService.getDrops(ingestors);

		ModelAndView modelAndView = new ModelAndView("tracks/drops");
		modelAndView.addObject(INGEST_WIZARD_DATA_DTO, data);
		modelAndView.addObject(ACTION, "/drops/select");
        LOGGER.debug("output getDrops({})", data);
        return modelAndView;
    }

    @RequestMapping(value = "/drops/select", method = POST)
    public ModelAndView selectDrops(@Valid @ModelAttribute(INGEST_WIZARD_DATA_DTO) IngestWizardDataDto data) {
        LOGGER.debug("input selectDrops(data) request, [{}]", new Object[] { data });

        data = trackRepoService.selectDrops(data);

        ModelAndView modelAndView = new ModelAndView("tracks/drops");
        modelAndView.addObject(INGEST_WIZARD_DATA_DTO, data);
        modelAndView.addObject(ACTION, "/drops/tracks/select");

        return modelAndView;
    }

    @RequestMapping(value = "/drops/tracks/select", method = POST)
    public ModelAndView selectTrackDrops(@Valid @ModelAttribute(INGEST_WIZARD_DATA_DTO) IngestWizardDataDto data) {
        LOGGER.debug("input selectDrops(data) request, [{}]", new Object[]{data});

        data = trackRepoService.selectTrackDrops(data);

        ModelAndView modelAndView = new ModelAndView("tracks/drops");
        modelAndView.addObject(INGEST_WIZARD_DATA_DTO, data);
        modelAndView.addObject(ACTION, "/drops/commit");

        return modelAndView;
    }

    @RequestMapping(value = "/drops/commit", method = POST)
    public ModelAndView commitDrops(@Valid @ModelAttribute(INGEST_WIZARD_DATA_DTO) IngestWizardDataDto data) {
        LOGGER.debug("input commitDrops(data) request, [{}]", new Object[]{data});

        trackRepoService.commitDrops(data);

        ModelAndView modelAndView = new ModelAndView("redirect:/tracks/list");

        return modelAndView;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(BAD_REQUEST)
    public ModelAndView handleValidationException(MethodArgumentNotValidException methodArgumentNotValidException) {
        LOGGER.trace("Bad request", methodArgumentNotValidException);
        ModelAndView modelAndView = new ModelAndView("");
        modelAndView.addObject("error", methodArgumentNotValidException.getBindingResult());
        return modelAndView;
    }

    @RequestMapping(value = "/reportingOptions", method = PUT)
    public void assignReportingOptions(@Valid @RequestBody TrackReportingOptionsDto trackReportingOptionsDto) {
        LOGGER.debug("assign Reporting Options, [{}]", trackReportingOptionsDto);
        trackRepoService.assignReportingOptions(trackReportingOptionsDto);
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
