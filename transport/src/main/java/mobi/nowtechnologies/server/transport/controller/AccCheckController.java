package mobi.nowtechnologies.server.transport.controller;

import mobi.nowtechnologies.common.dto.UserRegInfo;
import mobi.nowtechnologies.server.dto.transport.AccountCheckDto;
import mobi.nowtechnologies.server.dto.transport.LockedTrackDto;
import mobi.nowtechnologies.server.dto.transport.SelectedPlaylistDto;
import mobi.nowtechnologies.server.persistence.domain.*;
import mobi.nowtechnologies.server.service.*;
import mobi.nowtechnologies.server.shared.dto.AccountCheckDTO;
import mobi.nowtechnologies.server.shared.dto.web.ContentOfferDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static mobi.nowtechnologies.server.assembler.UserAsm.toAccountCheckDTO;
import static mobi.nowtechnologies.server.shared.enums.TransactionType.OFFER_PURCHASE;
import static org.apache.commons.lang.StringUtils.isNotBlank;

/**
 * @author Titov Mykhaylo (titov)
 * @author Alexander Kollpakov (akolpakov)
 */
@Controller
public class AccCheckController extends CommonController {

    private final Logger SUCCESS_ACC_CHECK_LOGGER = LoggerFactory.getLogger("SUCCESS_ACC_CHECK_LOGGER");

    private UserService userService;
    private ChartService chartService;
    private DeviceUserDataService deviceUserDataService;

    private UserDeviceDetailsService userDeviceDetailsService;
    private AccountLogService accountLogService;
    private OfferService offerService;
    private ITunesService iTunesService;
    private DeviceService deviceService;
    private PaymentPolicyService paymentPolicyService;

    public void setAccountLogService(AccountLogService accountLogService) {
        this.accountLogService = accountLogService;
    }

    public void setOfferService(OfferService offerService) {
        this.offerService = offerService;
    }

    public void setiTunesService(ITunesService iTunesService) {
        this.iTunesService = iTunesService;
    }

    public void setDeviceService(DeviceService deviceService) {
        this.deviceService = deviceService;
    }

    public void setPaymentPolicyService(PaymentPolicyService paymentPolicyService) {
        this.paymentPolicyService = paymentPolicyService;
    }

    public void setUserDeviceDetailsService(UserDeviceDetailsService userDeviceDetailsService) {
        this.userDeviceDetailsService = userDeviceDetailsService;
    }

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
            @RequestParam(required = false, value = "IDFA") String idfa,
            @PathVariable("community") String community) throws Exception {

        User user = null;
        Exception ex = null;
        try {
            LOGGER.info("command processing started");

            if (iphoneToken != null) {
                pushNotificationToken = iphoneToken;
            }

            if (isValidDeviceUID(deviceUID)) {
                user = userService.checkCredentials(userName, userToken, timestamp, communityName, deviceUID);
            }
            else {
                user = userService.checkCredentials(userName, userToken, timestamp, communityName);
            }
            LOGGER.debug("input parameters userId, pushToken,  deviceType, transactionReceipt: [{}], [{}], [{}], [{}]", new String[]{String.valueOf(user.getId()), pushNotificationToken, deviceType, transactionReceipt});

            logAboutSuccessfullAccountCheck();

            if(idfa != null){
                user = userService.updateTokenDetails(user, idfa);
            }

            if (isNotBlank(xtifyToken)) {
                user = deviceUserDataService.saveXtifyToken(xtifyToken, userName, communityName, deviceUID);
            }

            if (deviceType != null && pushNotificationToken != null)
                userDeviceDetailsService.mergeUserDeviceDetails(user, pushNotificationToken, deviceType);

            try {
                iTunesService.processInAppSubscription(user.getId(), transactionReceipt);
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }

            AccountCheckDto accountCheck = processAccCheck(user);

            return buildModelAndView(accountCheck);
        } catch (Exception e) {
            ex = e;
            throw e;
        } finally {
            logProfileData(deviceUID, community, null, null, user, ex);
            LOGGER.info("command processing finished");
        }
    }

    public AccountCheckDto processAccCheck(User user){
        user = userService.proceessAccountCheckCommandForAuthorizedUser(user.getId());

        Community community = user.getUserGroup().getCommunity();

        List<String> appStoreProductIds = paymentPolicyService.findAppStoreProductIdsByCommunityAndAppStoreProductIdIsNotNull(community);
        mobi.nowtechnologies.server.shared.dto.AccountCheckDTO accountCheckDTO = toAccountCheckDTO(user, null, appStoreProductIds, userService.canActivateVideoTrial(user));

        accountCheckDTO.promotedDevice = deviceService.existsInPromotedList(community, user.getDeviceUID());
        accountCheckDTO.promotedWeeks = (int) Math.floor((user.getNextSubPayment() * 1000L - System.currentTimeMillis()) / 1000 / 60 / 60 / 24 / 7) + 1;


        user = userService.getUserWithSelectedCharts(user.getId());
        List<ChartDetail> chartDetails = chartService.getLockedChartItems(user);

        AccountCheckDto accountCheck = new AccountCheckDto(accountCheckDTO);
        accountCheck.lockedTracks = LockedTrackDto.fromChartDetailList(chartDetails);
        accountCheck.playlists = SelectedPlaylistDto.fromChartList(user.getSelectedCharts());

        return precessRememberMeToken(accountCheck);
    }

    public AccountCheckDto processAccCheckBeforeO2Releases(User user){
        user = userService.proceessAccountCheckCommandForAuthorizedUser(user.getId());

        Community community = user.getUserGroup().getCommunity();

        List<String> appStoreProductIds = paymentPolicyService.findAppStoreProductIdsByCommunityAndAppStoreProductIdIsNotNull(community);
        mobi.nowtechnologies.server.shared.dto.AccountCheckDTO accountCheckDTO = toAccountCheckDTO(user, null, appStoreProductIds, userService.canActivateVideoTrial(user));

        accountCheckDTO.promotedDevice = deviceService.existsInPromotedList(community, user.getDeviceUID());
        accountCheckDTO.promotedWeeks = (int) Math.floor((user.getNextSubPayment() * 1000L - System.currentTimeMillis()) / 1000 / 60 / 60 / 24 / 7) + 1;

        List<Integer> relatedMediaUIDsByLogTypeList = accountLogService.getRelatedMediaUIDsByLogType(user.getId(), OFFER_PURCHASE);

        accountCheckDTO.hasOffers = false;
        if (relatedMediaUIDsByLogTypeList.isEmpty()) {
            List<ContentOfferDto> contentOfferDtos = offerService.getContentOfferDtos(user.getId());
            if (contentOfferDtos != null && contentOfferDtos.size() > 0)
                accountCheckDTO.hasOffers = true;
        }

        return precessRememberMeToken(new AccountCheckDto(accountCheckDTO));
    }

    @RequestMapping(method = RequestMethod.POST, value = {
            "*/{community:o2}/{apiVersion:4\\.1}/ACC_CHECK",
            "*/{community:o2}/{apiVersion:4\\.1}/ACC_CHECK.json",
            "*/{community:o2}/{apiVersion:4\\.2}/ACC_CHECK",
            "*/{community:o2}/{apiVersion:4\\.2}/ACC_CHECK.json",
            "/{community:o2}/{apiVersion:4\\.2}/ACC_CHECK",
            "/{community:o2}/{apiVersion:4\\.2}/ACC_CHECK.json"
    })
    public ModelAndView accountCheckForO2ClientAcceptHeaderSupport(
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
            @RequestParam(required = false, value = "IDFA") String idfa,
            @PathVariable("community") String community) throws Exception {
        apiVersionThreadLocal.set(apiVersion);

        ModelAndView modelAndView = accountCheckForO2Client_4d0(httpServletRequest, communityName, apiVersion, userName, userToken, timestamp, deviceType, deviceUID, pushNotificationToken, iphoneToken, xtifyToken, transactionReceipt, idfa, community);
        modelAndView.setViewName(defaultViewName);

        return modelAndView;
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
            @RequestParam(required = false, value = "IDFA") String idfa,
            @PathVariable("community") String community) throws Exception {

        // hack for IOS7 users that needs to remove it soon
        User user = userService.findByNameAndCommunity(userName, community);
        if(user != null && DeviceType.IOS.equals(user.getDeviceType().getName())){
            user.setDeviceUID(deviceUID);
            userService.updateUser(user);
       }
       ///

        return accountCheckForO2Client(httpServletRequest, communityName, apiVersion, userName, userToken, timestamp, deviceType, deviceUID, pushNotificationToken, iphoneToken, xtifyToken, transactionReceipt, idfa, community);
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
            @RequestParam(required = false, value = "IDFA") String idfa,
            @PathVariable("community") String community) throws Exception {

        return (Response)accountCheckForO2Client_4d0(httpServletRequest, communityName, apiVersion, userName, userToken, timestamp, deviceType, deviceUID, pushNotificationToken, iphoneToken, xtifyToken, transactionReceipt, idfa, community).getModelMap().get(MODEL_NAME);
    }

    @RequestMapping(method = RequestMethod.POST, value = {
            "*/{community:.*}/{apiVersion:5\\.[0-9]{1,3}}/{command:ACC_CHECK(?:\\.json){0,1}}",
            "/{community:.*}/{apiVersion:5\\.[0-9]{1,3}}/{command:ACC_CHECK(?:\\.json){0,1}}",
    })
    public ModelAndView accountCheckWithAcceptHeaderSupporting(
            HttpServletRequest httpServletRequest,
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
            @RequestParam(required = false, value = "IDFA") String idfa,
            @PathVariable("community") String community) throws Exception {
        apiVersionThreadLocal.set(apiVersion);

        ModelAndView modelAndView = accountCheckForO2Client_4d0(httpServletRequest, community, apiVersion, userName, userToken, timestamp, deviceType, deviceUID, pushNotificationToken, iphoneToken, xtifyToken, transactionReceipt, idfa, community);
        modelAndView.setViewName(defaultViewName);

        return modelAndView;
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

            user = userService.assignPotentialPromotion(user);

            if (deviceType != null && pushNotificationToken != null)
                userDeviceDetailsService.mergeUserDeviceDetails(user, pushNotificationToken, deviceType);

            try {
                iTunesService.processInAppSubscription(user.getId(), transactionReceipt);
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }

            AccountCheckDTO accountCheckDTO = processAccCheckBeforeO2Releases(user);

            final Object[] objects = new Object[] { accountCheckDTO };

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
        SUCCESS_ACC_CHECK_LOGGER.info("The login was successful");
    }

}