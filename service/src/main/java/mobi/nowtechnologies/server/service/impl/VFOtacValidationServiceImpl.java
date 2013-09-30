package mobi.nowtechnologies.server.service.impl;

import mobi.nowtechnologies.server.dto.ProviderUserDetails;
import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.enums.ProviderType;
import mobi.nowtechnologies.server.service.UserService;
import mobi.nowtechnologies.server.service.VFOtacValidationService;
import mobi.nowtechnologies.server.service.exception.ServiceException;
import mobi.nowtechnologies.server.shared.enums.Contract;

import static mobi.nowtechnologies.server.persistence.domain.enums.ProviderType.*;
import static mobi.nowtechnologies.server.shared.enums.Contract.*;

/**
 * User: Titov Mykhaylo (titov)
 * 30.09.13 11:43
 */
public class VFOtacValidationServiceImpl implements VFOtacValidationService {

    private UserService userService;

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public ProviderUserDetails validate(String otac, String phoneNumber, Community community) {
        ProviderUserDetails providerUserDetails = new ProviderUserDetails().withContract(PAYG.name());
        if (userService.isPromotedDevice(phoneNumber) && TEST_OTAC_NON_VF.equals(otac)) {
            providerUserDetails.withOperator(NON_VF.toString());
        }else{
            boolean isOtacValid = userService.isVFNZOtacValid(otac, phoneNumber, community);
            if (!isOtacValid) throw new ServiceException("Otac ["+otac+"] isn't valid for user with mobile ["+phoneNumber+"] and community ["+community.getRewriteUrlParameter()+"]");

            providerUserDetails.withOperator(VF.toString());
        }

        return providerUserDetails;
    }
}
