package mobi.nowtechnologies.server.service.vodafone.impl;

import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.service.DeviceService;
import mobi.nowtechnologies.server.service.data.PhoneNumberValidationData;
import mobi.nowtechnologies.server.service.exception.InvalidPhoneNumberException;
import mobi.nowtechnologies.server.service.exception.ServiceException;
import mobi.nowtechnologies.server.service.validator.NZCellNumberValidator;
import mobi.nowtechnologies.server.service.vodafone.VFNZProviderService;
import mobi.nowtechnologies.server.shared.Processor;
import mobi.nowtechnologies.server.shared.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * User: Alexsandr_Kolpakov
 * Date: 10/1/13
 * Time: 6:06 PM
 */
public class VFNZProviderServiceImpl implements VFNZProviderService {
    protected final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    private NZCellNumberValidator phoneValidator = new NZCellNumberValidator();
    private VFNZSMSGatewayServiceImpl gatewayService;
    private DeviceService deviceService;
    protected String providerNumber;
    private Community vfnzCommunity = new Community().withRewriteUrl("vf_nz");

    @Override
    public PhoneNumberValidationData validatePhoneNumber(String phoneNumber) throws InvalidPhoneNumberException {
        LOGGER.info("VALIDATE_PHONE_NUMBER for[{}] url[{}]", phoneNumber);

        PhoneNumberValidationData result = null;
        try {
            String normalizedPhoneNumber = phoneNumber;
            if(!deviceService.isPromotedDevicePhone(vfnzCommunity, phoneNumber, null)){
                normalizedPhoneNumber = phoneValidator.validate(phoneNumber);
            }

            if(normalizedPhoneNumber == null){
                throw new ServiceException("Invalid phone number");
            }

            String pin = Utils.generateRandomPIN().toString();

            result = new PhoneNumberValidationData()
                    .withPhoneNumber(normalizedPhoneNumber)
                    .withPin(pin);

            return result;
        } catch (Exception e) {
            LOGGER.error("NZ VALIDATE_PHONE_NUMBER Error for[{}] error[{}]", phoneNumber, e.getMessage());
            throw new InvalidPhoneNumberException(phoneNumber);
        } finally {
            LOGGER.info("NZ VALIDATE_PHONE_NUMBER finished for[{}] with [{}]", new Object[]{phoneNumber, result});
        }
    }

    @Override
    public void getSubscriberData(String phoneNumber,final Processor<VFNZSubscriberData> processor) {
        LOGGER.info("NZ GET_SUBSCRIBER_DATA for[{}]", phoneNumber);

        processor.process(new VFNZSubscriberData().withPhoneNumber(phoneNumber));

        gatewayService.send(phoneNumber, "GET_PROVIDER", providerNumber);

        LOGGER.info("NZ GET_SUBSCRIBER_DATA finished for[{}]", new Object[]{phoneNumber});
    }

    public void setPhoneValidator(NZCellNumberValidator phoneValidator) {
        this.phoneValidator = phoneValidator;
    }

    public void setGatewayService(VFNZSMSGatewayServiceImpl gatewayService) {
        this.gatewayService = gatewayService;
    }

    public void setProviderNumber(String providerNumber) {
        this.providerNumber = providerNumber;
    }

    public void setDeviceService(DeviceService deviceService) {
        this.deviceService = deviceService;
    }
}
