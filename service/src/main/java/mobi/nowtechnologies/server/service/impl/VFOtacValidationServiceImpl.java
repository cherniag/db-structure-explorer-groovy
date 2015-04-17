package mobi.nowtechnologies.server.service.impl;

import mobi.nowtechnologies.server.dto.ProviderUserDetails;
import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.repository.UserRepository;
import mobi.nowtechnologies.server.service.DevicePromotionsService;
import mobi.nowtechnologies.server.service.VFOtacValidationService;
import mobi.nowtechnologies.server.service.exception.ServiceException;
import static mobi.nowtechnologies.server.shared.enums.ProviderType.NON_VF;
import static mobi.nowtechnologies.server.shared.enums.ProviderType.VF;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * User: Titov Mykhaylo (titov) 30.09.13 11:43
 */
public class VFOtacValidationServiceImpl implements VFOtacValidationService {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    private DevicePromotionsService deviceService;
    @Resource
    private UserRepository userRepository;

    @Override
    public ProviderUserDetails validate(String otac, String phoneNumber, Community community) {
        ProviderUserDetails providerUserDetails = new ProviderUserDetails();

        boolean promotedDevice = deviceService.isPromotedDevicePhone(community, phoneNumber, null);

        if (promotedDevice && TEST_OTAC_NON_VF.equals(otac)) {
            logger.info("The phone number [{}] is promoted and pin [{}] is stub so the operator will be NON_VF", phoneNumber, otac);

            providerUserDetails.withOperator(NON_VF.getKey());
        } else if (promotedDevice && TEST_OTAC_VF.equals(otac)) {
            logger.info("The phone number [{}] is promoted and pin [{}] is stub so the operator will be VF", phoneNumber, otac);

            providerUserDetails.withOperator(VF.getKey());
        } else {
            boolean isOtacValid = userRepository.findByOtacMobileAndCommunity(otac, phoneNumber, community) != 0L;
            if (!isOtacValid) {
                throw new ServiceException("Otac [" + otac + "] isn't valid for user with mobile [" + phoneNumber + "] and community [" + community.getRewriteUrlParameter() + "]");
            }
            logger.info("The phone number [{}] is NOT promoted and pin [{}] is valid so the operator will be not changed", phoneNumber, otac);
        }

        logger.debug("Output param [{}]", providerUserDetails);
        return providerUserDetails;
    }
}
