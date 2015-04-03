package mobi.nowtechnologies.applicationtests.services.helper;

import mobi.nowtechnologies.applicationtests.services.device.domain.CurrentPhone;
import mobi.nowtechnologies.server.apptests.provider.o2.O2PhoneExtensionsService;
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
    private O2PhoneExtensionsService o2PhoneExtensionsService;

    @Transactional("applicationTestsTransactionManager")
    public String createO2ValidPhoneNumber(ProviderType providerType, SegmentType segmentType, Contract contract, Tariff tariff, ContractChannel contractChannel){
        final int phoneTypePrefix = o2PhoneExtensionsService.getPhoneNumberSuffix(providerType, segmentType, contract, tariff, contractChannel);

        return doCreatePhone("+447%02d%07d", phoneTypePrefix);
    }

    private String doCreatePhone(String pattern, int phonePrefix) {
        CurrentPhone currentPhone = new CurrentPhone();
        applicationTestsEntityManager.persist(currentPhone);

        long phoneSuffix = currentPhone.getPhoneSuffix();

        return String.format(pattern, phonePrefix, phoneSuffix);
    }

}
