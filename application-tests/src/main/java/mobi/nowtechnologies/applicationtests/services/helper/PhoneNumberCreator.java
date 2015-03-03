package mobi.nowtechnologies.applicationtests.services.helper;

import mobi.nowtechnologies.applicationtests.services.device.domain.CurrentPhone;
import mobi.nowtechnologies.server.apptests.provider.o2.PhoneExtensionsService;
import mobi.nowtechnologies.server.shared.enums.Contract;
import mobi.nowtechnologies.server.shared.enums.ContractChannel;
import mobi.nowtechnologies.server.shared.enums.ProviderType;
import mobi.nowtechnologies.server.shared.enums.SegmentType;
import mobi.nowtechnologies.server.shared.enums.Tariff;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Author: Gennadii Cherniaiev Date: 7/3/2014
 */
@Component
public class PhoneNumberCreator {

    @PersistenceContext(name = "applicationTestsEntityManager", unitName = "applicationTestsEntityManager")
    private EntityManager applicationTestsEntityManager;

    @Resource
    private PhoneExtensionsService phoneExtensionsService;

    @Transactional("applicationTestsTransactionManager")
    public String createValidPhoneNumber(ProviderType providerType, SegmentType segmentType, Contract contract, Tariff tariff, ContractChannel contractChannel) {
        CurrentPhone currentPhone = new CurrentPhone();
        applicationTestsEntityManager.persist(currentPhone);
        Integer phoneTypePrefix = phoneExtensionsService.getPhoneNumberSuffix(providerType, segmentType, contract, tariff, contractChannel);
        return currentPhone.getO2Phone(phoneTypePrefix);
    }

    @Transactional("applicationTestsTransactionManager")
    public String createAnyValidPhoneNumber() {
        CurrentPhone currentPhone = new CurrentPhone();
        applicationTestsEntityManager.persist(currentPhone);
        return currentPhone.getAnyPhone();
    }
}
