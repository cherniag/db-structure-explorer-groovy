package mobi.nowtechnologies.applicationtests.services.helper;

import com.google.common.base.Preconditions;
import mobi.nowtechnologies.applicationtests.services.device.domain.CurrentPhone;
import mobi.nowtechnologies.server.apptests.NZSubscriberInfoGatewayMock;
import mobi.nowtechnologies.server.apptests.provider.o2.O2PhoneExtensionsService;
import mobi.nowtechnologies.server.shared.enums.*;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Author: Gennadii Cherniaiev Date: 7/3/2014
 */
@Component
public class PhoneNumberCreator {
    @PersistenceContext(name = "applicationTestsEntityManager", unitName = "applicationTestsEntityManager")
    private EntityManager applicationTestsEntityManager;

    @Resource
    private O2PhoneExtensionsService o2PhoneExtensionsService;

    @Transactional("applicationTestsTransactionManager")
    public String createO2ValidPhoneNumber(ProviderType providerType, SegmentType segmentType, Contract contract, Tariff tariff, ContractChannel contractChannel){
        final int phoneTypePrefix = o2PhoneExtensionsService.getPhoneNumberSuffix(providerType, segmentType, contract, tariff, contractChannel);

        return doCreatePhone("+447%02d%07d", phoneTypePrefix);
    }

    @Transactional("applicationTestsTransactionManager")
    public String createNZValidPhoneNumber() {
        final int validPhoneTypePrefix = 1;

        Preconditions.checkState(validPhoneTypePrefix != NZSubscriberInfoGatewayMock.notAvailablePrefix);
        Preconditions.checkState(validPhoneTypePrefix != NZSubscriberInfoGatewayMock.doesNotBelong);

        return doCreatePhone("+64%01d%07d", validPhoneTypePrefix);
    }

    @Transactional("applicationTestsTransactionManager")
    public String createNZNotFoundPhoneNumber() {
        return doCreatePhone("+00%01d%07d", 0);
    }
    @Transactional("applicationTestsTransactionManager")
    public String createNZDoesNotBelong() {
        return doCreatePhone("+00%01d%07d", NZSubscriberInfoGatewayMock.doesNotBelong);
    }
    @Transactional("applicationTestsTransactionManager")
    public String createNZNotAvailablePhoneNumber() {
        return doCreatePhone("+64%01d%07d", NZSubscriberInfoGatewayMock.notAvailablePrefix);
    }

    private String doCreatePhone(String pattern, int phonePrefix) {
        CurrentPhone currentPhone = new CurrentPhone();
        applicationTestsEntityManager.persist(currentPhone);

        long phoneSuffix = currentPhone.getPhoneSuffix();

        return String.format(pattern, phonePrefix, phoneSuffix);
    }

}
