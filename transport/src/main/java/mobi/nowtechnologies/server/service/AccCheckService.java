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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import java.util.List;

import static org.apache.commons.lang.Validate.notNull;

/**
 * Created by oar on 2/13/14.
 */
public class AccCheckService {

    @Resource(name = "service.UserService")
    private UserService userService;

    @Resource
    private PaymentPolicyRepository paymentPolicyService;

    @Resource
    private AccountCheckDTOAsm accountCheckDTOAsm;

    @Resource
    private DeviceService deviceService;

    @Resource
    private ChartService chartService;

    @Resource
    private NowTechTokenBasedRememberMeServices nowTechTokenBasedRememberMeServices;


    private static final Logger LOGGER = LoggerFactory.getLogger(AccCheckService.class);


    public AccountCheckDto processAccCheck(MergeResult mergeResult, boolean withUserDetails) {
        return processAccCheck(mergeResult.getResultOfOperation(), withUserDetails, !mergeResult.isMergeDone(), false);
    }

    public AccountCheckDto processAccCheck(User user, boolean withUserDetails, boolean withUuid) {
        return processAccCheck(user, withUserDetails, null, withUuid);
    }

    private AccountCheckDto processAccCheck(User user, boolean withUserDetails, Boolean firstActivation, boolean withUuid) {
        if (firstActivation != null){
            LOGGER.info("First activation: {}", firstActivation);
        }
        user = userService.proceessAccountCheckCommandForAuthorizedUser(user.getId());

        Community community = user.getUserGroup().getCommunity();

        List<String> appStoreProductIds = paymentPolicyService.findAppStoreProductIdsByCommunityAndAppStoreProductIdIsNotNull(community);
        Boolean canActivateVideoTrial = userService.canActivateVideoTrial(user);
        AccountCheckDTO accountCheckDTO = accountCheckDTOAsm.toAccountCheckDTO(user, null, appStoreProductIds, canActivateVideoTrial, withUserDetails, firstActivation, withUuid);

        accountCheckDTO.promotedDevice = deviceService.existsInPromotedList(community, user.getDeviceUID());
        accountCheckDTO.promotedWeeks = (int) Math.floor((user.getNextSubPayment() * 1000L - System.currentTimeMillis()) / 1000 / 60 / 60 / 24 / 7) + 1;

        user = userService.getUserWithSelectedCharts(user.getId());
        List<ChartDetail> chartDetails = chartService.getLockedChartItems(user);

        AccountCheckDto accountCheck = new AccountCheckDto(accountCheckDTO);
        accountCheck.lockedTracks = LockedTrackDto.fromChartDetailList(chartDetails);
        accountCheck.playlists = SelectedPlaylistDto.fromChartList(user.getSelectedCharts());

        return precessRememberMeToken(accountCheck);
    }

    private AccountCheckDto precessRememberMeToken(AccountCheckDto accountCheckDTO) {
        LOGGER.debug("input parameters: [{}]", new Object[]{accountCheckDTO});

        accountCheckDTO.rememberMeToken = getRememberMeToken(accountCheckDTO.userName, accountCheckDTO.userToken);

        LOGGER.debug("Output parameter [{}]", accountCheckDTO);
        return accountCheckDTO;
    }

    private String getRememberMeToken(String userName, String storedToken) {
        LOGGER.debug("input parameters userName, storedToken: [{}], [{}]", new String[]{userName, storedToken});
        notNull(userName, "The parameter userName is null");
        notNull(storedToken, "The parameter storedToken is null");

        String rememberMeToken = nowTechTokenBasedRememberMeServices.getRememberMeToken(userName, storedToken);
        LOGGER.debug("Output parameter rememberMeToken=[{}]", rememberMeToken);
        return rememberMeToken;
    }

}



