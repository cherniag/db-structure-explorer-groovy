package mobi.nowtechnologies.server.transport.controller;

import mobi.nowtechnologies.common.dto.UserRegInfo;
import mobi.nowtechnologies.server.device.domain.DeviceType;
import mobi.nowtechnologies.server.dto.transport.AccountCheckDto;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.repository.UserRepository;
import mobi.nowtechnologies.server.service.DeviceUserDataService;
import mobi.nowtechnologies.server.service.ITunesPaymentDetailsService;
import mobi.nowtechnologies.server.service.UrbanAirshipTokenService;
import mobi.nowtechnologies.server.service.behavior.PaymentTimeService;
import mobi.nowtechnologies.server.service.itunes.ITunesService;
import mobi.nowtechnologies.server.shared.enums.PaymentDetailsStatus;
import mobi.nowtechnologies.server.transport.controller.core.CommonController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static org.apache.commons.lang.StringUtils.isNotBlank;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author Titov Mykhaylo (titov)
 * @author Alexander Kollpakov (akolpakov)
 */
@Controller
public class AccCheckController extends CommonController {

    private final Logger SUCCESS_ACC_CHECK_LOGGER = LoggerFactory.getLogger("SUCCESS_ACC_CHECK_LOGGER");

    @Resource(name = "service.DeviceUserDataService")
    private DeviceUserDataService deviceUserDataService;

    @Resource
    private ITunesService iTunesService;

    @Resource
    private UserRepository userRepository;

    @Resource
    private PaymentTimeService paymentTimeService;

    @Resource(name = "service.UrbanAirshipTokenService")
    private UrbanAirshipTokenService urbanAirshipTokenService;

    @Resource
    private ITunesPaymentDetailsService iTunesPaymentDetailsService;

    @RequestMapping(method = RequestMethod.POST, value = {"**/{community}/{apiVersion:6\\.12}/ACC_CHECK"})
    public ModelAndView accountCheckWithITunesPaymentDetails(@RequestParam("USER_NAME") String userName,
                                                   @RequestParam("USER_TOKEN") String userToken,
                                                   @RequestParam("TIMESTAMP") String timestamp,
                                                   @RequestParam(required = false, value = "DEVICE_TYPE", defaultValue = UserRegInfo.DeviceType.IOS) String deviceType,
                                                   @RequestParam(required = false, value = "DEVICE_UID") String deviceUID,
                                                   @RequestParam(required = false, value = "UA_TOKEN") String uaToken,
                                                   @RequestParam(required = false, value = "XTIFY_TOKEN") String xtifyToken,
                                                   @RequestParam(required = false, value = "TRANSACTION_RECEIPT") String transactionReceipt,
                                                   @RequestParam(required = false, value = "IDFA") String idfa, HttpServletResponse response) throws Exception {
        return accCheckImpl(userName, userToken, timestamp, deviceType, deviceUID, null, uaToken, xtifyToken, transactionReceipt, idfa, true, true, true, response, true);
    }

    @RequestMapping(method = RequestMethod.POST, value = {"**/{community}/{apiVersion:6\\.11}/ACC_CHECK", "**/{community}/{apiVersion:6\\.10}/ACC_CHECK", "**/{community}/{apiVersion:6\\.9}/ACC_CHECK"

    })
    public ModelAndView accountCheckWithUUIDNewApi(@RequestParam("USER_NAME") String userName,
                                                   @RequestParam("USER_TOKEN") String userToken,
                                                   @RequestParam("TIMESTAMP") String timestamp,
                                                   @RequestParam(required = false, value = "DEVICE_TYPE", defaultValue = UserRegInfo.DeviceType.IOS) String deviceType,
                                                   @RequestParam(required = false, value = "DEVICE_UID") String deviceUID,
                                                   @RequestParam(required = false, value = "UA_TOKEN") String uaToken,
                                                   @RequestParam(required = false, value = "XTIFY_TOKEN") String xtifyToken,
                                                   @RequestParam(required = false, value = "TRANSACTION_RECEIPT") String transactionReceipt,
                                                   @RequestParam(required = false, value = "IDFA") String idfa) throws Exception {

        return accCheckImpl(userName, userToken, timestamp, deviceType, deviceUID, null, uaToken, xtifyToken, transactionReceipt, idfa, true, true, true, null, false);
    }


    @RequestMapping(method = RequestMethod.POST, value = {"**/{community}/{apiVersion:6\\.8}/ACC_CHECK"})
    public ModelAndView accountCheckWithOneTimeSubscriptionFlag(@RequestParam("USER_NAME") String userName,
                                                                @RequestParam("USER_TOKEN") String userToken,
                                                                @RequestParam("TIMESTAMP") String timestamp,
                                                                @RequestParam(required = false, value = "DEVICE_TYPE", defaultValue = UserRegInfo.DeviceType.IOS) String deviceType,
                                                                @RequestParam(required = false, value = "DEVICE_UID") String deviceUID,
                                                                @RequestParam(required = false, value = "PUSH_NOTIFICATION_TOKEN") String pushNotificationToken,
                                                                @RequestParam(required = false, value = "IPHONE_TOKEN") String iphoneToken,
                                                                @RequestParam(required = false, value = "XTIFY_TOKEN") String xtifyToken,
                                                                @RequestParam(required = false, value = "TRANSACTION_RECEIPT") String transactionReceipt,
                                                                @RequestParam(required = false, value = "IDFA") String idfa) throws Exception {

        return accCheckImpl(userName, userToken, timestamp, deviceType, deviceUID, pushNotificationToken, iphoneToken, xtifyToken, transactionReceipt, idfa, true, true, true, null, false);
    }

    @RequestMapping(method = RequestMethod.POST, value = {"**/{community}/{apiVersion:6\\.7}/ACC_CHECK", "**/{community}/{apiVersion:6\\.6}/ACC_CHECK"})
    public ModelAndView accountCheckWithUUID(@RequestParam("USER_NAME") String userName,
                                             @RequestParam("USER_TOKEN") String userToken,
                                             @RequestParam("TIMESTAMP") String timestamp,
                                             @RequestParam(required = false, value = "DEVICE_TYPE", defaultValue = UserRegInfo.DeviceType.IOS) String deviceType,
                                             @RequestParam(required = false, value = "DEVICE_UID") String deviceUID,
                                             @RequestParam(required = false, value = "PUSH_NOTIFICATION_TOKEN") String pushNotificationToken,
                                             @RequestParam(required = false, value = "IPHONE_TOKEN") String iphoneToken,
                                             @RequestParam(required = false, value = "XTIFY_TOKEN") String xtifyToken,
                                             @RequestParam(required = false, value = "TRANSACTION_RECEIPT") String transactionReceipt,
                                             @RequestParam(required = false, value = "IDFA") String idfa) throws Exception {

        return accCheckImpl(userName, userToken, timestamp, deviceType, deviceUID, pushNotificationToken, iphoneToken, xtifyToken, transactionReceipt, idfa, true, true, false, null, false);
    }

    @RequestMapping(method = RequestMethod.POST,
                    value = {"**/{community}/{apiVersion:6\\.5}/ACC_CHECK",
                             "**/{community}/{apiVersion:6\\.4}/ACC_CHECK",
                             "**/{community}/{apiVersion:6\\.3}/ACC_CHECK",
                             "**/{community}/{apiVersion:6\\.2}/ACC_CHECK",
                             "**/{community}/{apiVersion:6\\.1}/ACC_CHECK",
                             "**/{community}/{apiVersion:6\\.0}/ACC_CHECK"})
    public ModelAndView accountCheckWithPossibilityOfReactivation(@RequestParam("USER_NAME") String userName,
                                                                  @RequestParam("USER_TOKEN") String userToken,
                                                                  @RequestParam("TIMESTAMP") String timestamp,
                                                                  @RequestParam(required = false, value = "DEVICE_TYPE", defaultValue = UserRegInfo.DeviceType.IOS) String deviceType,
                                                                  @RequestParam(required = false, value = "DEVICE_UID") String deviceUID,
                                                                  @RequestParam(required = false, value = "PUSH_NOTIFICATION_TOKEN") String pushNotificationToken,
                                                                  @RequestParam(required = false, value = "IPHONE_TOKEN") String iphoneToken,
                                                                  @RequestParam(required = false, value = "XTIFY_TOKEN") String xtifyToken,
                                                                  @RequestParam(required = false, value = "TRANSACTION_RECEIPT") String transactionReceipt,
                                                                  @RequestParam(required = false, value = "IDFA") String idfa) throws Exception {

        return accCheckImpl(userName, userToken, timestamp, deviceType, deviceUID, pushNotificationToken, iphoneToken, xtifyToken, transactionReceipt, idfa, true, false, false, null, false);
    }

    @RequestMapping(method = RequestMethod.POST, value = {"**/{community:o2}/{apiVersion:3.9}/ACC_CHECK"})
    public ModelAndView accountCheckForO2Client_v3d9(@RequestParam("USER_NAME") String userName,
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
        User user = userRepository.findByUserNameAndCommunityUrl(userName, community);
        if (user != null && DeviceType.IOS.equals(user.getDeviceType().getName())) {
            user.setDeviceUID(deviceUID);
            userService.updateUser(user);
        }
        ///

        return accCheckImpl(userName, userToken, timestamp, deviceType, deviceUID, pushNotificationToken, iphoneToken, xtifyToken, transactionReceipt, idfa, false, false, false, null, false);
    }

    @RequestMapping(method = RequestMethod.POST, value = {"**/{community}/{apiVersion:3\\.[6-9]|[4-5]{1}\\.[0-9]{1,3}}/ACC_CHECK"})
    public ModelAndView accountCheckForO2Client(@RequestParam("USER_NAME") String userName,
                                                @RequestParam("USER_TOKEN") String userToken,
                                                @RequestParam("TIMESTAMP") String timestamp,
                                                @RequestParam(required = false, value = "DEVICE_TYPE", defaultValue = UserRegInfo.DeviceType.IOS) String deviceType,
                                                @RequestParam(required = false, value = "DEVICE_UID") String deviceUID,
                                                @RequestParam(required = false, value = "PUSH_NOTIFICATION_TOKEN") String pushNotificationToken,
                                                @RequestParam(required = false, value = "IPHONE_TOKEN") String iphoneToken,
                                                @RequestParam(required = false, value = "XTIFY_TOKEN") String xtifyToken,
                                                @RequestParam(required = false, value = "TRANSACTION_RECEIPT") String transactionReceipt,
                                                @RequestParam(required = false, value = "IDFA") String idfa) throws Exception {
        return accCheckImpl(userName, userToken, timestamp, deviceType, deviceUID, pushNotificationToken, iphoneToken, xtifyToken, transactionReceipt, idfa, false, false, false, null, false);
    }

    private ModelAndView accCheckImpl(String userName,
                                      String userToken,
                                      String timestamp,
                                      String deviceType,
                                      String deviceUID,
                                      String pushNotificationToken,
                                      String iphoneToken,
                                      String xtifyToken,
                                      String transactionReceipt,
                                      String idfa,
                                      boolean checkReactivation,
                                      boolean withUuid,
                                      boolean withOneTimePayment,
                                      HttpServletResponse response,
                                      boolean createITunesPaymentDetails) throws Exception {
        LOGGER.info("command processing started");
        User user = null;
        Exception ex = null;
        String community = getCurrentCommunityUri();
        try {

            if (iphoneToken != null) {
                pushNotificationToken = iphoneToken;
            }

            LOGGER.info("input parameters userName [{}],  deviceType [{}], pushToken [{}], transactionReceipt [{}], idfa [{}]", userName, deviceType, pushNotificationToken, transactionReceipt, idfa);
            user = checkUser(userName, userToken, timestamp, deviceUID, checkReactivation);

            SUCCESS_ACC_CHECK_LOGGER.info("The login was successful");

            userService.updateIdfaToken(user, idfa);

            if (isNotBlank(xtifyToken)) {
                try {
                    deviceUserDataService.saveXtifyToken(user, xtifyToken);
                } catch (Exception e) {
                    LOGGER.error(e.getMessage(), e);
                }
            }

            // SRV-628. We are not storing a token if it's a string literal with '(null)' value.
            // Some IOS clients send this value because of bugs in them.
            if (isNotBlank(pushNotificationToken) && !pushNotificationToken.equals("(null)")) {
                urbanAirshipTokenService.saveToken(user, pushNotificationToken);
            }

            processITunesSubscription(user, response, transactionReceipt, createITunesPaymentDetails);

            AccountCheckDto accountCheck = accCheckService.processAccCheck(user, false, withUuid, withOneTimePayment);

            return buildModelAndView(accountCheck);
        } catch (Exception e) {
            ex = e;
            throw e;
        } finally {
            logProfileData(deviceUID, community, null, null, user, ex);
            LOGGER.info("command processing finished");
        }
    }

    private void processITunesSubscription(User user, HttpServletResponse response, String transactionReceipt, boolean createITunesPaymentDetails) {
        try {
            if (createITunesPaymentDetails) {
                if (StringUtils.isNotEmpty(transactionReceipt)) {
                    iTunesPaymentDetailsService.assignAppStoreReceipt(user, transactionReceipt);
                    handleExpires(user, response);
                } else {
                    // temp fix for migration
                    migrateITunesSubscriberToPaymentDetailsModel(user);
                }
            } else {
                iTunesService.processInAppSubscription(user, transactionReceipt);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    private void handleExpires(User user, HttpServletResponse response) {
        if(user.hasActiveITunesPaymentDetails() && user.getCurrentPaymentDetails().getLastPaymentStatus() == PaymentDetailsStatus.NONE && user.isLimited()) {
            LOGGER.info("Handle expires date for iTunes payment for user {}", user.getId());
            Date nextRetryTimeForITunesPayment = paymentTimeService.getNextRetryTimeForITunesPayment(user, new Date());
            if (nextRetryTimeForITunesPayment != null && response != null) {
                response.setDateHeader("Expires", nextRetryTimeForITunesPayment.getTime());
            }
        }
    }

    private void migrateITunesSubscriberToPaymentDetailsModel(User user) {
        boolean hasActiveITunesSubscription = !user.isOnFreeTrial() && user.hasITunesSubscription();
        boolean hasNotEmptyReceipt = StringUtils.isNotEmpty(user.getBase64EncodedAppStoreReceipt());

        if(hasNotEmptyReceipt && hasActiveITunesSubscription && !user.hasActiveITunesPaymentDetails()) {
            iTunesPaymentDetailsService.assignAppStoreReceipt(user, user.getBase64EncodedAppStoreReceipt());
        }
    }
}