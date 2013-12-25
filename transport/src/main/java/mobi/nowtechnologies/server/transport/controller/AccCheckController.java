package mobi.nowtechnologies.server.transport.controller;

import mobi.nowtechnologies.common.dto.UserRegInfo;
import mobi.nowtechnologies.server.dto.transport.AccountCheckDto;
import mobi.nowtechnologies.server.dto.transport.LockedTrackDto;
import mobi.nowtechnologies.server.dto.transport.SelectedPlaylistDto;
import mobi.nowtechnologies.server.persistence.domain.ChartDetail;
import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.DeviceType;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.service.*;
import mobi.nowtechnologies.server.shared.dto.web.ContentOfferDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

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

    private ChartService chartService;
    private DeviceUserDataService deviceUserDataService;

    private UserDeviceDetailsService userDeviceDetailsService;private AccountLogService accountLogService;
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

    public void setChartService(ChartService chartService) {
        this.chartService = chartService;
    }

    @RequestMapping(method = RequestMethod.POST, value = {
            "**/{community}/{apiVersion:3\\.[6-9]|[4-9]{1}\\.[0-9]{1,3}}/ACC_CHECK"
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

        User user = null;
        Exception ex = null;
        String community = getCurrentCommunityUri();
        try {
            LOGGER.info("command processing started");

            if (iphoneToken != null) {
                pushNotificationToken = iphoneToken;
            }

            user = checkUser(userName, userToken, timestamp, deviceUID);
            LOGGER.debug("input parameters userId, pushToken,  deviceType, transactionReceipt: [{}], [{}], [{}], [{}]", new String[]{String.valueOf(user.getId()), pushNotificationToken, deviceType, transactionReceipt});

            logAboutSuccessfullAccountCheck();

            if(idfa != null){
                user = userService.updateTokenDetails(user, idfa);
            }

            if (isNotBlank(xtifyToken)) {
                user = deviceUserDataService.saveXtifyToken(xtifyToken, userName, community, deviceUID);
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

    @RequestMapping(method = RequestMethod.POST, value = {
            "**/{community:o2}/{apiVersion:3.9}/ACC_CHECK"
    })
    public ModelAndView accountCheckForO2Client_v3d9(
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

        return accountCheckForO2Client(userName, userToken, timestamp, deviceType, deviceUID, pushNotificationToken, iphoneToken, xtifyToken, transactionReceipt, idfa);
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

    private void logAboutSuccessfullAccountCheck() {
        SUCCESS_ACC_CHECK_LOGGER.info("The login was successful");
    }
}