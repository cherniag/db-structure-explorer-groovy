package mobi.nowtechnologies.server.transport.controller;

import com.google.common.base.CharMatcher;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.service.ChartService;
import mobi.nowtechnologies.server.service.ThrottlingService;
import mobi.nowtechnologies.server.shared.dto.*;
import mobi.nowtechnologies.server.shared.enums.ActivationStatus;
import mobi.nowtechnologies.server.shared.enums.ChartType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
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

    private ChartService chartService;
    private ThrottlingService throttlingService;
    public void setChartService(ChartService chartService) {
        this.chartService = chartService;
    }

    public void setThrottlingService(ThrottlingService throttlingService) {
        this.throttlingService = throttlingService;
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
            LOGGER.info("command proccessing started");
            throttlingService.throttling(request, userName, deviceUID, community);

            user = checkUser(userName, userToken, timestamp, deviceUID, ActivationStatus.ACTIVATED);

            ChartDto chartDto = chartService.processGetChartCommand(user, community, true, false);
            chartDto = convertToOldVersion(chartDto, apiVersion);

            AccountCheckDTO accountCheck = accCheckService.processAccCheck(user, false);

            return buildModelAndView(accountCheck, chartDto);
        } catch (Exception e) {
            ex = e;
            throw e;
        } finally {
            logProfileData(deviceUID, community, null, null, user, ex);
            LOGGER.info("command processing finished");
        }
    }

    @RequestMapping(method = RequestMethod.POST, value = {"" +
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
            LOGGER.info("command proccessing started");
            throttlingService.throttling(request, userName, deviceUID, community);

            user = checkUser(userName, userToken, timestamp, deviceUID, ActivationStatus.ACTIVATED);

            ChartDto chartDto = chartService.processGetChartCommand(user, community, false, false);
            chartDto = convertToOldVersion(chartDto, apiVersion);

            AccountCheckDTO accountCheck = accCheckService.processAccCheck(user, false);

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
            LOGGER.info("command proccessing started");
            throttlingService.throttling(request, userName, deviceUID, community);

            user = checkUser(userName, userToken, timestamp, deviceUID, ActivationStatus.ACTIVATED);

            ChartDto chartDto = chartService.processGetChartCommand(user, community, false, true);
            chartDto = convertToOldVersion(chartDto, apiVersion);

            AccountCheckDTO accountCheck = accCheckService.processAccCheck(user, false);

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
            "**/{community}/{apiVersion:[4-9]{1}\\.[0-9]{1,3}}/GET_CHART"
    })
    public ModelAndView getChart_O2_v4d0(
            HttpServletRequest request,
            @RequestParam("USER_NAME") String userName,
            @RequestParam("USER_TOKEN") String userToken,
            @RequestParam("TIMESTAMP") String timestamp,
            @RequestParam(required = false, value = "DEVICE_UID") String deviceUID
    ) throws Exception {
        User user = null;
        Exception ex = null;
        String community = getCurrentCommunityUri();
        String apiVersion = getCurrentApiVersion();
        try {
            LOGGER.info("command proccessing started");
            throttlingService.throttling(request, userName, deviceUID, community);

            user = checkUser(userName, userToken, timestamp, deviceUID, ActivationStatus.ACTIVATED);

            ChartDto chartDto = chartService.processGetChartCommand(user, community, false, true);

            AccountCheckDTO accountCheck = accCheckService.processAccCheck(user, false);

            return buildModelAndView(accountCheck, chartDto);
        } catch (Exception e) {
            ex = e;
            throw e;
        } finally {
            logProfileData(deviceUID, community, null, null, user, ex);
            LOGGER.info("command processing finished");
        }
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
