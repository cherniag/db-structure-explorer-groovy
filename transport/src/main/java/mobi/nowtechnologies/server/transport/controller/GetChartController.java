package mobi.nowtechnologies.server.transport.controller;

import com.google.common.base.CharMatcher;
import mobi.nowtechnologies.server.editor.ResolutionParameterEditor;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.streamzine.badge.Resolution;
import mobi.nowtechnologies.server.service.ChartService;
import mobi.nowtechnologies.server.service.ThrottlingService;
import mobi.nowtechnologies.server.shared.dto.*;
import mobi.nowtechnologies.server.shared.enums.ActivationStatus;
import mobi.nowtechnologies.server.shared.enums.ChartType;
import mobi.nowtechnologies.server.shared.web.spring.modifiedsince.IfModifiedSinceHeader;
import mobi.nowtechnologies.server.transport.controller.core.CommonController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static java.util.Arrays.asList;
import static mobi.nowtechnologies.server.shared.enums.ChartType.*;
import static mobi.nowtechnologies.server.shared.web.spring.modifiedsince.IfModifiedDefaultValue.ZERO;
import static mobi.nowtechnologies.server.shared.web.spring.modifiedsince.IfModifiedUtils.checkNotModified;

/**
 * GetChartController
 *
 * @author Titov Mykhaylo (titov)
 * @author Alexander Kollpakov (akolpakov)
 */
@Controller
public class GetChartController extends CommonController {

    @Resource
    private ChartService chartService;

    @Resource
    private ThrottlingService throttlingService;

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(Resolution.class, new ResolutionParameterEditor());
    }

    @RequestMapping(method = RequestMethod.GET, value = {
            "**/{community}/{apiVersion:6\\.7}/GET_CHART"
    })
    public ModelAndView getChartV67(
            HttpServletRequest request,
            @RequestParam("USER_NAME") String userName,
            @RequestParam("USER_TOKEN") String userToken,
            @RequestParam("TIMESTAMP") String timestamp,
            @RequestParam(required = false, value = "DEVICE_UID") String deviceUID,
            @RequestParam("WIDTHXHEIGHT") Resolution resolution,
            @IfModifiedSinceHeader(defaultValue = ZERO) Long modifiedSince,
            HttpServletResponse response
    ) throws Exception {
        return getChart(request, userName, userToken, timestamp, deviceUID, modifiedSince, resolution, response, true, ActivationStatus.ACTIVATED);
    }

    @RequestMapping(method = RequestMethod.GET, value = {
            "**/{community}/{apiVersion:6\\.6}/GET_CHART",
            "**/{community}/{apiVersion:6\\.5}/GET_CHART",
            "**/{community}/{apiVersion:6\\.4}/GET_CHART"
    })
    public ModelAndView getChartV6(
            HttpServletRequest request,
            @RequestParam("USER_NAME") String userName,
            @RequestParam("USER_TOKEN") String userToken,
            @RequestParam("TIMESTAMP") String timestamp,
            @RequestParam(required = false, value = "DEVICE_UID") String deviceUID,
            @RequestParam("WIDTHXHEIGHT") Resolution resolution,
            @IfModifiedSinceHeader(defaultValue = ZERO) Long modifiedSince,
            HttpServletResponse response
    ) throws Exception {
        return getChart(request, userName, userToken, timestamp, deviceUID, modifiedSince, resolution, response, false, ActivationStatus.ACTIVATED);
    }


    @RequestMapping(method = RequestMethod.GET, value = {
            "**/{community}/{apiVersion:6\\.3}/GET_CHART"
    })
    public ModelAndView getChartV63(
            HttpServletRequest request,
            @RequestParam("USER_NAME") String userName,
            @RequestParam("USER_TOKEN") String userToken,
            @RequestParam("TIMESTAMP") String timestamp,
            @RequestParam(required = false, value = "DEVICE_UID") String deviceUID,
            @IfModifiedSinceHeader(defaultValue = ZERO) Long modifiedSince,
            HttpServletResponse response
    ) throws Exception {
        return getChart(request, userName, userToken, timestamp, deviceUID, modifiedSince, null, response, false, ActivationStatus.ACTIVATED);
    }


    @RequestMapping(method = RequestMethod.POST, value = {
            "**/{community}/{apiVersion:6\\.2}/GET_CHART",
            "**/{community}/{apiVersion:6\\.1}/GET_CHART",
            "**/{community}/{apiVersion:6\\.0}/GET_CHART",
            "**/{community}/{apiVersion:5\\.[0-4]{1,3}}/GET_CHART",
            "**/{community}/{apiVersion:4\\.[0-9]{1,3}}/GET_CHART"
    })
    public ModelAndView getChart_O2_v4d0(
            HttpServletRequest request,
            @RequestParam("USER_NAME") String userName,
            @RequestParam("USER_TOKEN") String userToken,
            @RequestParam("TIMESTAMP") String timestamp,
            @RequestParam(required = false, value = "DEVICE_UID") String deviceUID
    ) throws Exception {
        return getChart(request, userName, userToken, timestamp, deviceUID, null, null, null, false, ActivationStatus.ACTIVATED);
    }


    @RequestMapping(method = RequestMethod.POST, value = {
            "**/{community}/5.5/GET_CHART",
            "**/{community}/5.5.0/GET_CHART"
    })
    public ModelAndView getChart_v5(
            HttpServletRequest request,
            @RequestParam("USER_NAME") String userName,
            @RequestParam("USER_TOKEN") String userToken,
            @RequestParam("TIMESTAMP") String timestamp,
            @RequestParam(required = false, value = "DEVICE_UID") String deviceUID
    ) throws Exception {
        return getChart(request, userName, userToken, timestamp, deviceUID,
                null, null, null, false, ActivationStatus.REGISTERED, ActivationStatus.ACTIVATED);
    }

    @RequestMapping(method = RequestMethod.POST, value = {
            "**/{community:o2}/{apiVersion:3\\.[8-9]{1,3}}/GET_CHART"
    })
    public ModelAndView getChart_O2_v3d8(
            HttpServletRequest request,
            @RequestParam("USER_NAME") String userName,
            @RequestParam("USER_TOKEN") String userToken,
            @RequestParam("TIMESTAMP") String timestamp,
            @RequestParam(required = false, value = "DEVICE_UID") String deviceUID) throws Exception {
        User user = null;
        Exception ex = null;
        String community = getCurrentCommunityUri();
        String apiVersion = getCurrentApiVersion();
        try {
            LOGGER.info("command processing started");
            throttlingService.throttling(request, userName, deviceUID, community);

            user = checkUser(userName, userToken, timestamp, deviceUID, false, ActivationStatus.ACTIVATED);

            ContentDtoResult<ChartDto> contentDtoResult = chartService.processGetChartCommand(user, false, true, null, null, false);
            ChartDto chartDto = contentDtoResult.getContent();
            chartDto = convertToOldVersion(chartDto, apiVersion);

            AccountCheckDTO accountCheck = accCheckService.processAccCheck(user, false, false);

            return buildModelAndView(accountCheck, chartDto);
        } catch (Exception e) {
            ex = e;
            throw e;
        } finally {
            logProfileData(deviceUID, community, null, null, user, ex);
            LOGGER.info("command processing finished");
        }
    }


    @RequestMapping(method = RequestMethod.POST, value = {
            "**/{community:o2}/{apiVersion:3.7}/GET_CHART"
    })
    public ModelAndView getChart_O2_v3d7(
            HttpServletRequest request,
            @RequestParam("USER_NAME") String userName,
            @RequestParam("USER_TOKEN") String userToken,
            @RequestParam("TIMESTAMP") String timestamp,
            @RequestParam(required = false, value = "DEVICE_UID") String deviceUID) throws Exception {
        User user = null;
        Exception ex = null;
        String community = getCurrentCommunityUri();
        String apiVersion = getCurrentApiVersion();
        try {
            LOGGER.info("command processing started");
            throttlingService.throttling(request, userName, deviceUID, community);

            user = checkUser(userName, userToken, timestamp, deviceUID, false, ActivationStatus.ACTIVATED);

            ContentDtoResult<ChartDto> chartDtoResult = chartService.processGetChartCommand(user, false, false, null, null, false);
            ChartDto chartDto = chartDtoResult.getContent();
            chartDto = convertToOldVersion(chartDto, apiVersion);

            AccountCheckDTO accountCheck = accCheckService.processAccCheck(user, false, false);

            return buildModelAndView(accountCheck, chartDto);
        } catch (Exception e) {
            ex = e;
            throw e;
        } finally {
            logProfileData(deviceUID, community, null, null, user, ex);
            LOGGER.info("command processing finished");
        }
    }


    @RequestMapping(method = RequestMethod.POST, value = {
            "**/{community:o2}/3.6/GET_CHART"
    })
    public ModelAndView getChart_O2(
            HttpServletRequest request,
            @RequestParam("API_VERSION") String apiVersion,
            @RequestParam("USER_NAME") String userName,
            @RequestParam("USER_TOKEN") String userToken,
            @RequestParam("TIMESTAMP") String timestamp,
            @RequestParam(required = false, value = "DEVICE_UID") String deviceUID) throws Exception {
        User user = null;
        Exception ex = null;
        String community = getCurrentCommunityUri();
        try {
            LOGGER.info("command processing started");
            throttlingService.throttling(request, userName, deviceUID, community);

            user = checkUser(userName, userToken, timestamp, deviceUID, false, ActivationStatus.ACTIVATED);

            ContentDtoResult<ChartDto> chartDtoResult = chartService.processGetChartCommand(user, true, false, null, null, false);

            ChartDto chartDto = chartDtoResult.getContent();

            chartDto = convertToOldVersion(chartDto, apiVersion);

            AccountCheckDTO accountCheck = accCheckService.processAccCheck(user, false, false);

            return buildModelAndView(accountCheck, chartDto);
        } catch (Exception e) {
            ex = e;
            throw e;
        } finally {
            logProfileData(deviceUID, community, null, null, user, ex);
            LOGGER.info("command processing finished");
        }
    }


    private ModelAndView getChart(HttpServletRequest request,
                                  String userName,
                                  String userToken,
                                  String timestamp,
                                  String deviceUID,
                                  Long lastDateOfUpdateChartOnClient, Resolution resolution, HttpServletResponse response, boolean isPlayListLockedSupported, ActivationStatus... activationStatuses) throws Exception {
        User user = null;
        Exception ex = null;
        String community = getCurrentCommunityUri();
        try {
            LOGGER.info("command processing started");
            throttlingService.throttling(request, userName, deviceUID, community);
            user = checkUser(userName, userToken, timestamp, deviceUID, false, activationStatuses);

            boolean checkCaching = ((response != null) && (lastDateOfUpdateChartOnClient != null));

            if(resolution != null) {
                resolution.withDeviceType(user.getDeviceType().getName());
            }
            ContentDtoResult<ChartDto> chartResult = chartService.processGetChartCommand(user, false, true, resolution, lastDateOfUpdateChartOnClient, isPlayListLockedSupported);

            if (checkCaching) {
                Long lastUpdateTime = chartResult.getLastUpdatedTime();
                if (checkNotModified(lastUpdateTime, request, response)) {
                    return null;
                }
            }

            AccountCheckDTO accountCheck = accCheckService.processAccCheck(user, false, false);

            return buildModelAndView(accountCheck, chartResult.getContent());
        } catch (Exception e) {
            ex = e;
            throw e;
        } finally {
            logProfileData(deviceUID, community, null, null, user, ex);
            LOGGER.info("command processing finished");
        }
    }

    private ChartDto convertToOldVersion(ChartDto chartDto, String version) {
        version = CharMatcher.DIGIT.retainFrom(version).substring(0, 2);
        int intVersion = new Integer(version);

        Set<ChartType> removeChartTypes = new HashSet<ChartType>(intVersion < 38 ? asList(FOURTH_CHART, FIFTH_CHART, VIDEO_CHART) : asList(VIDEO_CHART));
        PlaylistDto[] playlistDtos = chartDto.getPlaylistDtos();
        Set<Integer> removedPlaylistIds = new HashSet<Integer>();
        Map<Integer, PlaylistDto> playlistMap = new HashMap<Integer, PlaylistDto>();
        for (int i = 0; i < playlistDtos.length; i++) {
            ChartType chartType = playlistDtos[i].getType();
            if (removeChartTypes.contains(chartType)) {
                removedPlaylistIds.add(playlistDtos[i].getId());
                playlistDtos[i] = null;
            } else {
                playlistMap.put(playlistDtos[i].getId(), playlistDtos[i]);
            }
        }

        ChartDetailDto[] tracks = chartDto.getChartDetailDtos();
        Map<ChartType, Integer> positionMap = new HashMap<ChartType, Integer>();
        for (int i = 0; i < tracks.length; i++) {
            if (removedPlaylistIds.contains(tracks[i].getPlaylistId()))
                tracks[i] = null;
            else if (tracks[i].getChannel() != null && intVersion < 37)
                tracks[i] = new BonusChartDetailDto(tracks[i]);

            if (intVersion < 38 && tracks[i] != null) {
                PlaylistDto playlistDto = playlistMap.get(tracks[i].getPlaylistId());
                Integer position = positionMap.get(playlistDto.getType());
                position = position != null ? position : 1;

                tracks[i].setPosition(position.byteValue());

                positionMap.put(playlistDto.getType(), position + 1);
            }
        }

        return chartDto;
    }
}
