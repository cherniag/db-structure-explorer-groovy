package mobi.nowtechnologies.server.service;

import mobi.nowtechnologies.server.assembler.AccountCheckDTOAsm;
import mobi.nowtechnologies.server.dto.transport.AccountCheckDto;
import mobi.nowtechnologies.server.dto.transport.LockedTrackDto;
import mobi.nowtechnologies.server.dto.transport.SelectedPlaylistDto;
import mobi.nowtechnologies.server.persistence.domain.ChartDetail;
import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.repository.PaymentPolicyRepository;
import mobi.nowtechnologies.server.security.NowTechTokenBasedRememberMeServices;
import mobi.nowtechnologies.server.shared.dto.AccountCheckDTO;

import javax.annotation.Resource;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AccCheckService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AccCheckService.class);
    @Resource(name = "service.UserService")
    private UserService userService;
    @Resource
    private PaymentPolicyRepository paymentPolicyService;
    @Resource
    private AccountCheckDTOAsm accountCheckDTOAsm;
    @Resource
    private DevicePromotionsService deviceService;
    @Resource
    private ChartService chartService;
    @Resource(name = "nowTechBasedRememberMeServices")
    private NowTechTokenBasedRememberMeServices nowTechTokenBasedRememberMeServices;

    public AccountCheckDto processAccCheck(MergeResult mergeResult, boolean withUserDetails, boolean withOneTimePayment) {
        return processAccCheck(mergeResult.getResultOfOperation(), withUserDetails, !mergeResult.isMergeDone(), false, withOneTimePayment);
    }

    public AccountCheckDto processAccCheck(User user, boolean withUserDetails, boolean withUuid, boolean withOneTimePayment) {
        return processAccCheck(user, withUserDetails, null, withUuid, withOneTimePayment);
    }

    private AccountCheckDto processAccCheck(User user, boolean withUserDetails, Boolean firstActivation, boolean withUuid, boolean withOneTimePayment) {
        if (firstActivation != null) {
            LOGGER.info("First activation: {}", firstActivation);
        }
        user = userService.processAccountCheckCommandForAuthorizedUser(user.getId());

        Community community = user.getUserGroup().getCommunity();

        List<String> appStoreProductIds = paymentPolicyService.findAppStoreProductIdsByCommunityAndAppStoreProductIdIsNotNull(community);
        Boolean canActivateVideoTrial = userService.canActivateVideoTrial(user);
        AccountCheckDTO accountCheckDTO = accountCheckDTOAsm.toAccountCheckDTO(user, null, appStoreProductIds, canActivateVideoTrial, withUserDetails, firstActivation, withUuid, withOneTimePayment);

        accountCheckDTO.promotedDevice = deviceService.existsInPromotedList(community, user.getDeviceUID());
        accountCheckDTO.promotedWeeks = (int) Math.floor((user.getNextSubPayment() * 1000L - System.currentTimeMillis()) / 1000 / 60 / 60 / 24 / 7) + 1;

        user = userService.getUserWithSelectedCharts(user.getId());
        List<ChartDetail> chartDetails = chartService.getLockedChartItems(user);

        AccountCheckDto accountCheck = new AccountCheckDto(accountCheckDTO);
        accountCheck.lockedTracks = LockedTrackDto.fromChartDetailList(chartDetails);
        accountCheck.playlists = SelectedPlaylistDto.fromChartList(user.getSelectedCharts());
        accountCheck.rememberMeToken = nowTechTokenBasedRememberMeServices.getRememberMeToken(accountCheckDTO.userName, accountCheckDTO.userToken);

        LOGGER.debug("For user id: {} acc check dto: {}", user.getId(), accountCheck);

        return accountCheck;
    }
}



