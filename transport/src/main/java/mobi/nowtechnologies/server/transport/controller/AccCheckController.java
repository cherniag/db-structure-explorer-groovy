package mobi.nowtechnologies.server.transport.controller;

import mobi.nowtechnologies.common.dto.UserRegInfo;
import mobi.nowtechnologies.server.dto.transport.AccountCheckDto;
import mobi.nowtechnologies.server.dto.transport.LockedTrackDto;
import mobi.nowtechnologies.server.dto.transport.SelectedPlaylistDto;
import mobi.nowtechnologies.server.persistence.domain.ChartDetail;
import mobi.nowtechnologies.server.persistence.domain.Response;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.service.ChartService;
import mobi.nowtechnologies.server.service.DeviceUserDataService;
import mobi.nowtechnologies.server.service.UserService;
import org.slf4j.MDC;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static org.apache.commons.lang.StringUtils.isNotBlank;

/**
 * AccCheckConroller
 *
 * @author Titov Mykhaylo (titov)
 * @author Alexander Kollpakov (akolpakov)
 */
@Controller
public class AccCheckController extends CommonController {

    private UserService userService;
    private ChartService chartService;
    private DeviceUserDataService deviceUserDataService;

    public void setDeviceUserDataService(DeviceUserDataService deviceUserDataService) {
        this.deviceUserDataService = deviceUserDataService;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public void setChartService(ChartService chartService) {
        this.chartService = chartService;
    }

    @RequestMapping(method = RequestMethod.POST, value = { "/{community:o2}/{apiVersion:3\\.[0-8]{1,3}}/ACC_CHECK", "*/{community:o2}/{apiVersion:3\\.[0-8]{1,3}}/ACC_CHECK" })
    public ModelAndView accountCheckForO2Client(
            HttpServletRequest httpServletRequest,
            @RequestParam("COMMUNITY_NAME") String communityName,
            @PathVariable("apiVersion") String apiVersion,
            @RequestParam("USER_NAME") String userName,
            @RequestParam("USER_TOKEN") String userToken,
            @RequestParam("TIMESTAMP") String timestamp,
            @RequestParam(required = false, value = "DEVICE_TYPE", defaultValue = UserRegInfo.DeviceType.IOS) String deviceType,
            @RequestParam(required = false, value = "DEVICE_UID") String deviceUID,
            @RequestParam(required = false, value = "PUSH_NOTIFICATION_TOKEN") String pushNotificationToken,
            @RequestParam(required = false, value = "IPHONE_TOKEN") String iphoneToken,
            @RequestParam(required = false, value = "XTIFY_TOKEN") String xtifyToken,
            @RequestParam(required = false, value = "TRANSACTION_RECEIPT") String transactionReceipt,
            @PathVariable("community") String community) throws Exception {

        User user = null;
        Exception ex = null;
        try {
            LOGGER.info("command processing started");

            if (iphoneToken != null)
                pushNotificationToken = iphoneToken;

            if (isValidDeviceUID(deviceUID))
                user = userService.checkCredentials(userName, userToken, timestamp, communityName, deviceUID);
            else
                user = userService.checkCredentials(userName, userToken, timestamp, communityName);

            logAboutSuccessfullAccountCheck();

            final mobi.nowtechnologies.server.shared.dto.AccountCheckDTO accountCheckDTO = userService.proceessAccountCheckCommandForAuthorizedUser(user.getId(),
                    pushNotificationToken, deviceType, transactionReceipt);

            user = userService.getUserWithSelectedCharts(user.getId());
            List<ChartDetail> chartDetails = chartService.getLockedChartItems(communityName, user);

            AccountCheckDto accountCheck = new AccountCheckDto(accountCheckDTO);
            accountCheck.setLockedTracks(LockedTrackDto.fromChartDetailList(chartDetails));
            accountCheck.setPlaylists(SelectedPlaylistDto.fromChartList(user.getSelectedCharts()));

            final Object[] objects = new Object[] { accountCheck };
            precessRememberMeToken(objects);

            ModelAndView mav =  new ModelAndView(view, MODEL_NAME, new Response(objects));

            if (isNotBlank(xtifyToken)) {
                user = deviceUserDataService.saveXtifyToken(xtifyToken, userName, communityName, deviceUID);
            }

            return mav;
        } catch (Exception e) {
            ex = e;
            throw e;
        } finally {
            logProfileData(deviceUID, community, null, null, user, ex);
            LOGGER.info("command processing finished");
        }
    }

    @RequestMapping(method = RequestMethod.POST, value = {
            "/{community:o2}/{apiVersion:3\\.9|4\\.0}/ACC_CHECK",
            "*/{community:o2}/{apiVersion:3\\.9|4\\.0}/ACC_CHECK"
    })
    public ModelAndView accountCheckForO2Client_4d0(
            HttpServletRequest httpServletRequest,
            @RequestParam("COMMUNITY_NAME") String communityName,
            @PathVariable("apiVersion") String apiVersion,
            @RequestParam("USER_NAME") String userName,
            @RequestParam("USER_TOKEN") String userToken,
            @RequestParam("TIMESTAMP") String timestamp,
            @RequestParam(required = false, value = "DEVICE_TYPE", defaultValue = UserRegInfo.DeviceType.IOS) String deviceType,
            @RequestParam(required = false, value = "DEVICE_UID") String deviceUID,
            @RequestParam(required = false, value = "PUSH_NOTIFICATION_TOKEN") String pushNotificationToken,
            @RequestParam(required = false, value = "IPHONE_TOKEN") String iphoneToken,
            @RequestParam(required = false, value = "XTIFY_TOKEN") String xtifyToken,
            @RequestParam(required = false, value = "TRANSACTION_RECEIPT") String transactionReceipt,
            @PathVariable("community") String community) throws Exception {

        return accountCheckForO2Client(httpServletRequest, communityName, apiVersion, userName, userToken, timestamp, deviceType, null, pushNotificationToken, iphoneToken, xtifyToken, transactionReceipt, community);
    }

    @RequestMapping(method = RequestMethod.POST, value = {
            "/{community:o2}/{apiVersion:3\\.9|4\\.0}/ACC_CHECK.json",
            "*/{community:o2}/{apiVersion:3\\.9|4\\.0}/ACC_CHECK.json"
    }, produces = "application/json")
    public @ResponseBody Response accountCheckForO2ClientJson(
            HttpServletRequest httpServletRequest,
            @RequestParam("COMMUNITY_NAME") String communityName,
            @PathVariable("apiVersion") String apiVersion,
            @RequestParam("USER_NAME") String userName,
            @RequestParam("USER_TOKEN") String userToken,
            @RequestParam("TIMESTAMP") String timestamp,
            @RequestParam(required = false, value = "DEVICE_TYPE", defaultValue = UserRegInfo.DeviceType.IOS) String deviceType,
            @RequestParam(required = false, value = "DEVICE_UID") String deviceUID,
            @RequestParam(required = false, value = "PUSH_NOTIFICATION_TOKEN") String pushNotificationToken,
            @RequestParam(required = false, value = "IPHONE_TOKEN") String iphoneToken,
            @RequestParam(required = false, value = "XTIFY_TOKEN") String xtifyToken,
            @RequestParam(required = false, value = "TRANSACTION_RECEIPT") String transactionReceipt,
            @PathVariable("community") String community) throws Exception {

        return (Response)accountCheckForO2Client_4d0(httpServletRequest, communityName, apiVersion, userName, userToken, timestamp, deviceType, deviceUID, pushNotificationToken, iphoneToken, xtifyToken, transactionReceipt, community).getModelMap().get(MODEL_NAME);
    }

    protected boolean isValidDeviceUID(String deviceUID){
        return org.springframework.util.StringUtils.hasText(deviceUID) && !deviceUID.equals("0f607264fc6318a92b9e13c65db7cd3c");
    }

    public static AccountCheckDto getAccountCheckDtoFrom(ModelAndView mav) {
        Response resp = (Response) mav.
                getModelMap().
                get(MODEL_NAME);
        return (AccountCheckDto) resp.getObject()[0];
    }

    @RequestMapping(method = RequestMethod.POST, value = { "/ACC_CHECK", "*/ACC_CHECK" })
    public ModelAndView accountCheckWithXtifyToken(
            HttpServletRequest httpServletRequest,
            @RequestParam("APP_VERSION") String appVersion,
            @RequestParam("COMMUNITY_NAME") String communityName,
            @RequestParam("API_VERSION") String apiVersion,
            @RequestParam("USER_NAME") String userName,
            @RequestParam("USER_TOKEN") String userToken,
            @RequestParam("TIMESTAMP") String timestamp,
            @RequestParam(required = false, value = "DEVICE_TYPE", defaultValue = UserRegInfo.DeviceType.IOS) String deviceType,
            @RequestParam(required = false, value = "DEVICE_UID") String deviceUID,
            @RequestParam(required = false, value = "PUSH_NOTIFICATION_TOKEN") String pushNotificationToken,
            @RequestParam(required = false, value = "IPHONE_TOKEN") String iphoneToken,
            @RequestParam(required = false, value = "XTIFY_TOKEN") String xtifyToken,
            @RequestParam(required = false, value = "TRANSACTION_RECEIPT") String transactionReceipt) throws Exception {

        User user = null;
        Exception ex = null;
        try {
            LOGGER.info("command proccessing started");

            if (iphoneToken != null)
                pushNotificationToken = iphoneToken;

            if (org.springframework.util.StringUtils.hasText(deviceUID))
                user = userService.checkCredentials(userName, userToken, timestamp, communityName, deviceUID);
            else
                user = userService.checkCredentials(userName, userToken, timestamp, communityName);

            logAboutSuccessfullAccountCheck();

            final mobi.nowtechnologies.server.shared.dto.AccountCheckDTO accountCheckDTO = userService.proceessAccountCheckCommandForAuthorizedUser(user.getId(),
                    pushNotificationToken, deviceType, transactionReceipt);

            final Object[] objects = new Object[] { accountCheckDTO };
            precessRememberMeToken(objects);

            ModelAndView mav =  new ModelAndView(view, MODEL_NAME, new Response(objects));

            if (isNotBlank(xtifyToken)) {
                user = deviceUserDataService.saveXtifyToken(xtifyToken, userName, communityName, deviceUID);
            }

            return mav;
        } catch (Exception e) {
            ex = e;
            throw e;
        } finally {
            logProfileData(deviceUID, communityName, null, null, user, ex);
            LOGGER.info("command processing finished");
        }
    }

    private void logAboutSuccessfullAccountCheck() {
        MDC.put("ACC_CHECK", "");
        try {
            LOGGER.info("The login was successful");
        } finally {
            MDC.remove("ACC_CHECK");
        }
    }

}