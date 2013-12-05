package mobi.nowtechnologies.server.transport.controller;

import com.google.common.base.CharMatcher;
import mobi.nowtechnologies.server.persistence.domain.Response;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.service.ChartService;
import mobi.nowtechnologies.server.service.ThrottlingService;
import mobi.nowtechnologies.server.service.UserService;
import mobi.nowtechnologies.server.shared.Utils;
import mobi.nowtechnologies.server.shared.dto.*;
import mobi.nowtechnologies.server.shared.enums.ChartType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static java.util.Arrays.asList;
import static mobi.nowtechnologies.server.shared.enums.ChartType.*;

/**
 * GetChartController
 *
 * @author Titov Mykhaylo (titov)
 * @author Alexander Kollpakov (akolpakov)
 */
@Controller
public class GetChartController extends CommonController {

    private UserService userService;
    private ChartService chartService;
    private ThrottlingService throttlingService;
    private AccCheckController accCheckController;

    public void setAccCheckController(AccCheckController accCheckController) {
        this.accCheckController = accCheckController;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public void setChartService(ChartService chartService) {
        this.chartService = chartService;
    }

    public void setThrottlingService(ThrottlingService throttlingService) {
        this.throttlingService = throttlingService;
    }

    @RequestMapping(method = RequestMethod.POST, value = {"/GET_CHART", "/{apiVersion:3\\.4}/GET_CHART", "/{apiVersion:3\\.4\\.0}/GET_CHART", "*/GET_CHART", "*/{apiVersion:3\\.4}/GET_CHART",
            "*/{apiVersion:3\\.4\\.0}/GET_CHART"})
    public ModelAndView getChart(
            HttpServletRequest request,
            @RequestParam("APP_VERSION") String appVersion,
            @RequestParam("COMMUNITY_NAME") String communityName,
            @RequestParam("API_VERSION") String apiVersion,
            @RequestParam("USER_NAME") String userName,
            @RequestParam("USER_TOKEN") String userToken,
            @RequestParam("TIMESTAMP") String timestamp) throws Exception {
        User user = null;
        Exception ex = null;
        try {
            LOGGER.info("command proccessing started");
            if (userName == null)
                throw new NullPointerException("The parameter userName is null");
            if (communityName == null)
                throw new NullPointerException("The parameter communityName is null");
            if (null == appVersion)
                throw new NullPointerException("The argument aAppVersion is null");
            if (null == apiVersion)
                throw new NullPointerException("The argument aApiVersion is null");
            if (null == userToken)
                throw new NullPointerException("The argument aUserToken is null");
            if (null == timestamp)
                throw new NullPointerException("The argument aTimestamp is null");

            String ip = Utils.getIpFromRequest(request);
            user = userService.checkCredentials(userName, userToken,
                    timestamp, communityName);

            ChartDto chartDto = chartService.processGetChartCommand(user, communityName, true, false);

            AccountCheckDTO accountCheck = accCheckController.processAccCheckBeforeO2Releases(user);

            chartDto = convertToOldVersion(chartDto, apiVersion);

            Object[] objects = new Object[]{accountCheck, chartDto};


            ChartDetailDto[] chartDetailDtos = chartDto.getChartDetailDtos();
            for (ChartDetailDto chartDetailDto : chartDetailDtos) {
                if (chartDetailDto instanceof BonusChartDetailDto) {
                    BonusChartDetailDto bonusChartDetailDto = (BonusChartDetailDto) chartDetailDto;

                    final String songTitle = bonusChartDetailDto.getTitle();
                    final String artistName = bonusChartDetailDto.getArtist();
                    final String channel = bonusChartDetailDto.getChannel().replaceAll("_", " ");

                    bonusChartDetailDto.setArtist(artistName + "-" + songTitle);
                    bonusChartDetailDto.setTitle(channel);
                }
            }

            return buildModelAndView(accountCheck, chartDto);
        } catch (Exception e) {
            ex = e;
            throw e;
        } finally {
            logProfileData(null, communityName, null, null, user, ex);
            LOGGER.info("command processing finished");
        }
    }

    @RequestMapping(method = RequestMethod.POST, value = {"/{apiVersion:[3-9]{1,2}\\.[4-5][0-9]{0,2}\\.[1-9]{1,3}}/GET_CHART", "/{apiVersion:[3-9]{1,2}\\.[4-5][0-9]{0,2}}/GET_CHART",
            "*/{apiVersion:[3-9]{1,2}\\.[4-5][0-9]{0,2}\\.[1-9]{1,3}}/GET_CHART", "*/{apiVersion:[3-9]{1,2}\\.[4-5][0-9]{0,2}}/GET_CHART"})
    public ModelAndView getChart_WithChanelSplintered(
            HttpServletRequest request,
            @RequestParam("APP_VERSION") String appVersion,
            @RequestParam("COMMUNITY_NAME") String communityName,
            @RequestParam("API_VERSION") String apiVersion,
            @RequestParam("USER_NAME") String userName,
            @RequestParam("USER_TOKEN") String userToken,
            @RequestParam("TIMESTAMP") String timestamp) throws Exception {
        User user = null;
        Exception ex = null;
        try {
            LOGGER.info("command proccessing started");
            if (userName == null)
                throw new NullPointerException("The parameter userName is null");
            if (communityName == null)
                throw new NullPointerException("The parameter communityName is null");
            if (null == appVersion)
                throw new NullPointerException("The argument aAppVersion is null");
            if (null == apiVersion)
                throw new NullPointerException("The argument aApiVersion is null");
            if (null == userToken)
                throw new NullPointerException("The argument aUserToken is null");
            if (null == timestamp)
                throw new NullPointerException("The argument aTimestamp is null");

            user = userService.checkCredentials(userName, userToken,
                    timestamp, communityName);

            ChartDto chartDto = chartService.processGetChartCommand(user, communityName, true, false);
            chartDto = convertToOldVersion(chartDto, apiVersion);

            AccountCheckDTO accountCheck = accCheckController.processAccCheckBeforeO2Releases(user);

            return buildModelAndView(accountCheck,chartDto);
        } catch (Exception e) {
            ex = e;
            throw e;
        } finally {
            logProfileData(null, communityName, null, null, user, ex);
            LOGGER.info("command processing finished");
        }
    }

    @RequestMapping(method = RequestMethod.POST, value = {"/{community:o2}/3.6/GET_CHART", "*/{community:o2}/3.6/GET_CHART"})
    public ModelAndView getChart_O2(
            HttpServletRequest request,
            @RequestParam("APP_VERSION") String appVersion,
            @RequestParam("COMMUNITY_NAME") String communityName,
            @RequestParam("API_VERSION") String apiVersion,
            @RequestParam("USER_NAME") String userName,
            @RequestParam("USER_TOKEN") String userToken,
            @RequestParam("TIMESTAMP") String timestamp,
            @RequestParam(required = false, value = "DEVICE_UID") String deviceUID,
            @PathVariable("community") String community) throws Exception {
        User user = null;
        Exception ex = null;
        try {
            LOGGER.info("command proccessing started");
            throttlingService.throttling(request, userName, deviceUID, community);

            user = userService.checkCredentials(userName, userToken, timestamp, community, deviceUID);

            ChartDto chartDto = chartService.processGetChartCommand(user, community, true, false);
            chartDto = convertToOldVersion(chartDto, apiVersion);

            AccountCheckDTO accountCheck = accCheckController.processAccCheck(user);

            return buildModelAndView(accountCheck, chartDto);
        } catch (Exception e) {
            ex = e;
            throw e;
        } finally {
            logProfileData(deviceUID, community, null, null, user, ex);
            LOGGER.info("command processing finished");
        }
    }

    @RequestMapping(method = RequestMethod.POST, value = {"/{community:o2}/{apiVersion:3.7}/GET_CHART", "*/{community:o2}/{apiVersion:3.7}/GET_CHART"})
    public ModelAndView getChart_O2_v3d7(
            HttpServletRequest request,
            @RequestParam("APP_VERSION") String appVersion,
            @RequestParam("COMMUNITY_NAME") String communityName,
            @PathVariable("apiVersion") String apiVersion,
            @RequestParam("USER_NAME") String userName,
            @RequestParam("USER_TOKEN") String userToken,
            @RequestParam("TIMESTAMP") String timestamp,
            @RequestParam(required = false, value = "DEVICE_UID") String deviceUID,
            @PathVariable("community") String community) throws Exception {
        User user = null;
        Exception ex = null;
        try {
            LOGGER.info("command proccessing started");
            throttlingService.throttling(request, userName, deviceUID, community);

            user = userService.checkCredentials(userName, userToken, timestamp, community, deviceUID);

            ChartDto chartDto = chartService.processGetChartCommand(user, community, false, false);
            chartDto = convertToOldVersion(chartDto, apiVersion);

            AccountCheckDTO accountCheck = accCheckController.processAccCheck(user);

            return buildModelAndView(accountCheck, chartDto);
        } catch (Exception e) {
            ex = e;
            throw e;
        } finally {
            logProfileData(deviceUID, community, null, null, user, ex);
            LOGGER.info("command processing finished");
        }
    }

    @RequestMapping(method = RequestMethod.POST, value = {"/{community:o2}/{apiVersion:3\\.[8-9]{1,3}}/GET_CHART", "*/{community:o2}/{apiVersion:3\\.[8-9]{1,3}}/GET_CHART"})
    public ModelAndView getChart_O2_v3d8(
            HttpServletRequest request,
            @RequestParam("APP_VERSION") String appVersion,
            @RequestParam("COMMUNITY_NAME") String communityName,
            @PathVariable("apiVersion") String apiVersion,
            @RequestParam("USER_NAME") String userName,
            @RequestParam("USER_TOKEN") String userToken,
            @RequestParam("TIMESTAMP") String timestamp,
            @RequestParam(required = false, value = "DEVICE_UID") String deviceUID,
            @PathVariable("community") String community) throws Exception {
        User user = null;
        Exception ex = null;
        try {
            LOGGER.info("command proccessing started");
            throttlingService.throttling(request, userName, deviceUID, community);

            user = userService.checkCredentials(userName, userToken, timestamp, community, deviceUID);

            ChartDto chartDto = chartService.processGetChartCommand(user, community, false, true);
            chartDto = convertToOldVersion(chartDto, apiVersion);

            AccountCheckDTO accountCheck = accCheckController.processAccCheck(user);

            return buildModelAndView(accountCheck, chartDto);
        } catch (Exception e) {
            ex = e;
            throw e;
        } finally {
            logProfileData(deviceUID, community, null, null, user, ex);
            LOGGER.info("command processing finished");
        }
    }

    @RequestMapping(method = RequestMethod.POST, value = {"/{community:o2}/{apiVersion:[4-9]{1,2}\\.[0-9]{1,3}}/GET_CHART", "*/{community:o2}/{apiVersion:[4-9]{1,2}\\.[0-9]{1,3}}/GET_CHART"})
    public ModelAndView getChart_O2_v4d0(
            HttpServletRequest request,
            @RequestParam("USER_NAME") String userName,
            @RequestParam("USER_TOKEN") String userToken,
            @RequestParam("TIMESTAMP") String timestamp,
            @PathVariable("community") String community,
            @PathVariable("apiVersion") String apiVersion,
            @RequestParam(required = false, value = "DEVICE_UID") String deviceUID
    ) throws Exception {
        User user = null;
        Exception ex = null;
        try {
            LOGGER.info("command proccessing started");
            throttlingService.throttling(request, userName, deviceUID, community);

            user = userService.checkCredentials(userName, userToken, timestamp, community, deviceUID);

            ChartDto chartDto = chartService.processGetChartCommand(user, community, false, true);

            AccountCheckDTO accountCheck = accCheckController.processAccCheck(user);

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
            "*/{community:o2}/{apiVersion:4\\.0}/GET_CHART.json"
    }, produces = "application/json")
    public
    @ResponseBody
    Response getChart_O2_v4d0Json(
            HttpServletRequest request,
            @RequestParam("USER_NAME") String userName,
            @RequestParam("USER_TOKEN") String userToken,
            @RequestParam("TIMESTAMP") String timestamp,
            @PathVariable("community") String community,
            @PathVariable("apiVersion") String apiVersion,
            @RequestParam(required = false, value = "DEVICE_UID") String deviceUID
    ) throws Exception {
        return (Response) getChart_O2_v4d0(request, userName, userToken, timestamp, community, apiVersion, deviceUID).getModelMap().get(MODEL_NAME);
    }

    @RequestMapping(method = RequestMethod.POST, value = {
            "*/{community:.*}/{apiVersion:5\\.0}/GET_CHART",
            "*/{community:.*}/{apiVersion:5\\.0}/GET_CHART.json"
    })
    public ModelAndView getChartWithAcceptHeaderSupporting(
            HttpServletRequest request,
            @RequestParam("USER_NAME") String userName,
            @RequestParam("USER_TOKEN") String userToken,
            @RequestParam("TIMESTAMP") String timestamp,
            @PathVariable("community") String community,
            @PathVariable("apiVersion") String apiVersion,
            @RequestParam(required = false, value = "DEVICE_UID") String deviceUID
    ) throws Exception {
        apiVersionThreadLocal.set(apiVersion);

        ModelAndView modelAndView = getChart_O2_v4d0(request, userName, userToken, timestamp, community, apiVersion, deviceUID);
        modelAndView.setViewName(defaultViewName);
        return modelAndView;
    }

    @RequestMapping(method = RequestMethod.POST, value = {
            "*/{community:o2}/{apiVersion:4\\.1}/GET_CHART",
            "*/{community:o2}/{apiVersion:4\\.1}/GET_CHART.json",
            "*/{community:o2}/{apiVersion:4\\.2}/GET_CHART",
            "*/{community:o2}/{apiVersion:4\\.2}/GET_CHART.json"
    })
    public ModelAndView getChart_O2_AcceptHeaderSupport(
            HttpServletRequest request,
            @RequestParam("USER_NAME") String userName,
            @RequestParam("USER_TOKEN") String userToken,
            @RequestParam("TIMESTAMP") String timestamp,
            @PathVariable("community") String community,
            @PathVariable("apiVersion") String apiVersion,
            @RequestParam(required = false, value = "DEVICE_UID") String deviceUID
    ) throws Exception {
        apiVersionThreadLocal.set(apiVersion);

        ModelAndView modelAndView = getChart_O2_v4d0(request, userName, userToken, timestamp, community, apiVersion, deviceUID);
        modelAndView.setViewName(defaultViewName);
        return modelAndView;
    }

    public ChartDto convertToOldVersion(ChartDto chartDto, String version) {
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
