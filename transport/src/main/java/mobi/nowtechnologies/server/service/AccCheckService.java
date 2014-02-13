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


    protected final Logger LOGGER = LoggerFactory.getLogger(this.getClass());


    public AccountCheckDto processAccCheck(User user){

        user = userService.proceessAccountCheckCommandForAuthorizedUser(user.getId());

        Community community = user.getUserGroup().getCommunity();

        List<String> appStoreProductIds = paymentPolicyService.findAppStoreProductIdsByCommunityAndAppStoreProductIdIsNotNull(community);
        mobi.nowtechnologies.server.shared.dto.AccountCheckDTO accountCheckDTO = accountCheckDTOAsm.toAccountCheckDTO(user, null, appStoreProductIds, userService.canActivateVideoTrial(user));

        accountCheckDTO.promotedDevice = deviceService.existsInPromotedList(community, user.getDeviceUID());
        accountCheckDTO.promotedWeeks = (int) Math.floor((user.getNextSubPayment() * 1000L - System.currentTimeMillis()) / 1000 / 60 / 60 / 24 / 7) + 1;

        user = userService.getUserWithSelectedCharts(user.getId());
        List<ChartDetail> chartDetails = chartService.getLockedChartItems(user);

        AccountCheckDto accountCheck = new AccountCheckDto(accountCheckDTO);
        accountCheck.lockedTracks = LockedTrackDto.fromChartDetailList(chartDetails);
        accountCheck.playlists = SelectedPlaylistDto.fromChartList(user.getSelectedCharts());

        return precessRememberMeToken(accountCheck);
    }

    public mobi.nowtechnologies.server.dto.transport.AccountCheckDto precessRememberMeToken(mobi.nowtechnologies.server.dto.transport.AccountCheckDto accountCheckDTO) {
        LOGGER.debug("input parameters mobi.nowtechnologies.server.dto.transport.AccountCheckDTO: [{}]", new Object[]{accountCheckDTO});

        accountCheckDTO.rememberMeToken = getRememberMeToken(accountCheckDTO.userName, accountCheckDTO.userToken);

        LOGGER.debug("Output parameter mobi.nowtechnologies.server.dto.transport.AccountCheckDTO=[{}]", accountCheckDTO);
        return accountCheckDTO;
    }

    public String getRememberMeToken(String userName, String storedToken) {
        LOGGER.debug("input parameters userName, storedToken: [{}], [{}]", new String[] { userName, storedToken});
        notNull(userName , "The parameter userName is null");
        notNull(storedToken , "The parameter storedToken is null");

        String rememberMeToken = nowTechTokenBasedRememberMeServices.getRememberMeToken(userName, storedToken);
        LOGGER.debug("Output parameter rememberMeToken=[{}]", rememberMeToken);
        return rememberMeToken;
    }

}



