package mobi.nowtechnologies.server.service.impl;

import mobi.nowtechnologies.server.dto.ProviderUserDetails;
import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.enums.ProviderType;
import mobi.nowtechnologies.server.service.UserService;
import mobi.nowtechnologies.server.service.VFOtacValidationService;
import mobi.nowtechnologies.server.service.exception.ServiceException;
import mobi.nowtechnologies.server.shared.enums.Contract;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static mobi.nowtechnologies.server.persistence.domain.enums.ProviderType.*;
import static mobi.nowtechnologies.server.shared.enums.Contract.*;

/**
 * User: Titov Mykhaylo (titov)
 * 30.09.13 11:43
 */
public class VFOtacValidationServiceImpl implements VFOtacValidationService {
    private static Logger LOGGER = LoggerFactory.getLogger(VFOtacValidationServiceImpl.class);

    private UserService userService;

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public ProviderUserDetails validate(String otac, String phoneNumber, Community community) {
        ProviderUserDetails providerUserDetails = new ProviderUserDetails();

        boolean promotedDevice = userService.isPromotedDevice(phoneNumber, community);

        if (promotedDevice && TEST_OTAC_NON_VF.equals(otac)) {
            LOGGER.info("The phone number [{}] is promoted and pin [{}] is stub so the operator will be NON_VF", phoneNumber, otac);

            providerUserDetails.withOperator(NON_VF.toString());
        }else if (promotedDevice && TEST_OTAC_VF.equals(otac)) {
            LOGGER.info("The phone number [{}] is promoted and pin [{}] is stub so the operator will be VF", phoneNumber, otac);

            providerUserDetails.withOperator(VF.toString());
        }else{
            boolean isOtacValid = userService.isVFNZOtacValid(otac, phoneNumber, community);
            if (!isOtacValid) throw new ServiceException("Otac ["+otac+"] isn't valid for user with mobile ["+phoneNumber+"] and community ["+community.getRewriteUrlParameter()+"]");
            LOGGER.info("The phone number [{}] is NOT promoted and pin [{}] is valid so the operator will be not changed", phoneNumber, otac);
        }

        LOGGER.debug("Output param [{}]", providerUserDetails);
        return providerUserDetails;
    }
}
