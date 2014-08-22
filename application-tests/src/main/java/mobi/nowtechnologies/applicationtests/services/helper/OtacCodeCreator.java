package mobi.nowtechnologies.applicationtests.services.helper;

import mobi.nowtechnologies.server.shared.dto.AccountCheckDTO;
import mobi.nowtechnologies.server.shared.enums.Contract;
import mobi.nowtechnologies.server.shared.enums.ProviderType;
import org.springframework.stereotype.Component;

/**
 * Author: Gennadii Cherniaiev
 * Date: 7/8/2014
 */
@Component
public class OtacCodeCreator {

    public String generateValidOtac(AccountCheckDTO accountCheck) {
        return generateValidOtac(accountCheck.contract, ProviderType.valueOfKey(accountCheck.provider));
    }

    private String generateValidOtac(Contract contract, ProviderType providerType){
        return contract.name() + "#" + providerType.getKey();
    }
}
