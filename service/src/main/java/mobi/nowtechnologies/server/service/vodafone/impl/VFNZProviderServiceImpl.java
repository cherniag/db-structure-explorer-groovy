package mobi.nowtechnologies.server.service.vodafone.impl;

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
 * Created with IntelliJ IDEA.
 * User: Alexsandr_Kolpakov
 * Date: 10/1/13
 * Time: 6:06 PM
 * To change this template use File | Settings | File Templates.
 */
public class VFNZProviderServiceImpl implements VFNZProviderService {
    protected final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    private NZCellNumberValidator phoneValidator = new NZCellNumberValidator();
    private VFNZSMSGatewayService gatewayService;
    private String providerNumber;

    @Override
    public PhoneNumberValidationData validatePhoneNumber(String phoneNumber) throws InvalidPhoneNumberException {
        LOGGER.info("VALIDATE_PHONE_NUMBER for[{}] url[{}]", phoneNumber);

        PhoneNumberValidationData result = null;
        try {
            String normalizedPhoneNumber = phoneValidator.validate(phoneNumber);

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
            throw new InvalidPhoneNumberException();
        } finally {
            LOGGER.info("NZ VALIDATE_PHONE_NUMBER finished for[{}] with [{}]", new Object[]{phoneNumber, result});
        }
    }

    @Override
    public void getSubscriberData(String phoneNumber, Processor<VFNZSubscriberData> processor) {
        LOGGER.info("NZ GET_SUBSCRIBER_DATA for[{}]", phoneNumber);

        gatewayService.send(providerNumber, phoneNumber, "GET_PROVIDER", processor);

        LOGGER.info("NZ GET_SUBSCRIBER_DATA finished for[{}] with [{}]", new Object[]{phoneNumber});
    }

    public void setPhoneValidator(NZCellNumberValidator phoneValidator) {
        this.phoneValidator = phoneValidator;
    }

    public void setGatewayService(VFNZSMSGatewayService gatewayService) {
        this.gatewayService = gatewayService;
    }

    public void setProviderNumber(String providerNumber) {
        this.providerNumber = providerNumber;
    }
}
