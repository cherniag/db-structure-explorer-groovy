package mobi.nowtechnologies.server.transport.controller;

import mobi.nowtechnologies.common.dto.UserRegInfo;
import mobi.nowtechnologies.server.persistence.domain.DeviceType;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.service.DeviceUserDataService;
import mobi.nowtechnologies.server.service.ITunesService;
import mobi.nowtechnologies.server.service.UserDeviceDetailsService;
import mobi.nowtechnologies.server.transport.controller.core.CommonController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;

import static org.apache.commons.lang.StringUtils.isNotBlank;

/**
 * @author Titov Mykhaylo (titov)
 * @author Alexander Kollpakov (akolpakov)
 */
@Controller
public class AccCheckController extends CommonController {

    private final Logger SUCCESS_ACC_CHECK_LOGGER = LoggerFactory.getLogger("SUCCESS_ACC_CHECK_LOGGER");

    @Resource(name = "service.DeviceUserDataService")
    private DeviceUserDataService deviceUserDataService;

    @Resource(name = "service.UserDeviceDetailsService")
    private UserDeviceDetailsService userDeviceDetailsService;

    @Resource
    private ITunesService iTunesService;

    @RequestMapping(method = RequestMethod.POST, value = {
            "**/{community}/{apiVersion:3\\.[6-9]|[4-5]{1}\\.[0-9]{1,3}}/ACC_CHECK"
    })
    public ModelAndView accountCheckForO2Client(
            @RequestParam("USER_NAME") String userName,
            @RequestParam("USER_TOKEN") String userToken,
            @RequestParam("TIMESTAMP") String timestamp,
            @RequestParam(required = false, value = "DEVICE_TYPE", defaultValue = UserRegInfo.DeviceType.IOS) String deviceType,
            @RequestParam(required = false, value = "DEVICE_UID") String deviceUID,
            @RequestParam(required = false, value = "PUSH_NOTIFICATION_TOKEN") String pushNotificationToken,
            @RequestParam(required = false, value = "IPHONE_TOKEN") String iphoneToken,
            @RequestParam(required = false, value = "XTIFY_TOKEN") String xtifyToken,
            @RequestParam(required = false, value = "TRANSACTION_RECEIPT") String transactionReceipt,
            @RequestParam(required = false, value = "IDFA") String idfa
    ) throws Exception {

        return accCheckImpl(userName, userToken, timestamp, deviceType, deviceUID, pushNotificationToken, iphoneToken, xtifyToken, transactionReceipt, idfa, false);
    }

    private ModelAndView accCheckImpl(String userName, String userToken, String timestamp, String deviceType, String deviceUID, String pushNotificationToken, String iphoneToken, String xtifyToken, String transactionReceipt, String idfa, boolean checkReactivation) throws Exception {
        User user = null;
        Exception ex = null;
        String community = getCurrentCommunityUri();
        try {
            LOGGER.info("command processing started");

            if (iphoneToken != null) {
                pushNotificationToken = iphoneToken;
            }

            user = checkUser(userName, userToken, timestamp, deviceUID, checkReactivation);
            LOGGER.debug("input parameters userId, pushToken,  deviceType, transactionReceipt: [{}], [{}], [{}], [{}]", new String[]{String.valueOf(user.getId()), pushNotificationToken, deviceType, transactionReceipt});

            logAboutSuccessfullAccountCheck();

            if(idfa != null){
                user = userService.updateTokenDetails(user, idfa);
            }

            if (isNotBlank(xtifyToken)) {
                try {
                    deviceUserDataService.saveXtifyToken(user, xtifyToken);
                } catch (Exception e) {
                    LOGGER.error(e.getMessage(), e);
                }
            }

            if (deviceType != null && pushNotificationToken != null)
                userDeviceDetailsService.mergeUserDeviceDetails(user, pushNotificationToken, deviceType);

            try {
                iTunesService.processInAppSubscription(user.getId(), transactionReceipt);
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }

            mobi.nowtechnologies.server.dto.transport.AccountCheckDto accountCheck = accCheckService.processAccCheck(user, false);

            return buildModelAndView(accountCheck);
        } catch (Exception e) {
            ex = e;
            throw e;
        } finally {
            logProfileData(deviceUID, community, null, null, user, ex);
            LOGGER.info("command processing finished");
        }
    }

    @RequestMapping(method = RequestMethod.POST, value = {
            "**/{community:o2}/{apiVersion:3.9}/ACC_CHECK"
    })
    public ModelAndView accountCheckForO2Client_v3d9(
            @RequestParam("USER_NAME") String userName,
            @RequestParam("USER_TOKEN") String userToken,
            @RequestParam("TIMESTAMP") String timestamp,
            @RequestParam(required = false, value = "DEVICE_TYPE", defaultValue = UserRegInfo.DeviceType.IOS) String deviceType,
            @RequestParam(required = false, value = "DEVICE_UID") String deviceUID,
            @RequestParam(required = false, value = "PUSH_NOTIFICATION_TOKEN") String pushNotificationToken,
            @RequestParam(required = false, value = "IPHONE_TOKEN") String iphoneToken,
            @RequestParam(required = false, value = "XTIFY_TOKEN") String xtifyToken,
            @RequestParam(required = false, value = "TRANSACTION_RECEIPT") String transactionReceipt,
            @RequestParam(required = false, value = "IDFA") String idfa,
            @PathVariable("community") String community) throws Exception {

        // hack for IOS7 users that needs to remove it soon
        User user = userService.findByNameAndCommunity(userName, community);
        if(user != null && DeviceType.IOS.equals(user.getDeviceType().getName())){
            user.setDeviceUID(deviceUID);
            userService.updateUser(user);
        }
        ///

        return accCheckImpl(userName, userToken, timestamp, deviceType, deviceUID, pushNotificationToken, iphoneToken, xtifyToken, transactionReceipt, idfa, false);
    }



    @RequestMapping(method = RequestMethod.POST, value = {
            "**/{community}/{apiVersion:6\\.0}/ACC_CHECK",
            "**/{community}/{apiVersion:6\\.1}/ACC_CHECK",
            "**/{community}/{apiVersion:6\\.2}/ACC_CHECK",
            "**/{community}/{apiVersion:6\\.3}/ACC_CHECK",
            "**/{community}/{apiVersion:6\\.4}/ACC_CHECK"
    })
    public ModelAndView accountCheckWithPossibilityOfReactivation(
            @RequestParam("USER_NAME") String userName,
            @RequestParam("USER_TOKEN") String userToken,
            @RequestParam("TIMESTAMP") String timestamp,
            @RequestParam(required = false, value = "DEVICE_TYPE", defaultValue = UserRegInfo.DeviceType.IOS) String deviceType,
            @RequestParam(required = false, value = "DEVICE_UID") String deviceUID,
            @RequestParam(required = false, value = "PUSH_NOTIFICATION_TOKEN") String pushNotificationToken,
            @RequestParam(required = false, value = "IPHONE_TOKEN") String iphoneToken,
            @RequestParam(required = false, value = "XTIFY_TOKEN") String xtifyToken,
            @RequestParam(required = false, value = "TRANSACTION_RECEIPT") String transactionReceipt,
            @RequestParam(required = false, value = "IDFA") String idfa) throws Exception {

        return accCheckImpl(userName, userToken, timestamp, deviceType, deviceUID, pushNotificationToken, iphoneToken, xtifyToken, transactionReceipt, idfa, true);
    }


    private void logAboutSuccessfullAccountCheck() {
        SUCCESS_ACC_CHECK_LOGGER.info("The login was successful");
    }
}