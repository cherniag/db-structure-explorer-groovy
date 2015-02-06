package mobi.nowtechnologies.server.service.vodafone.impl;

import mobi.nowtechnologies.server.persistence.domain.NZSubscriberInfo;
import mobi.nowtechnologies.server.service.nz.NZSubscriberInfoService;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Anton Zemliankin
 */
public class NZSubscriberInfoServiceMock implements NZSubscriberInfoService {

    private static final String PAY_INDICATOR = "payIndicator";
    private static final String PROVIDER_NAME = "providerName";
    private static final String BILLING_ACCOUNT_NAME = "billingAccountName";
    private static final String BILLING_ACCOUNT_NUMBER = "billingAccountNumber";
    private static final String DEFAULT_DATA = "6421111111";

    private static final Map<String, Map<String, String>> testData = new HashMap<String, Map<String, String>>(){{
        put(DEFAULT_DATA, new HashMap<String, String>(){{
            put(PAY_INDICATOR, "Prepay");
            put(PROVIDER_NAME, "Vodafone");
            put(BILLING_ACCOUNT_NAME, "Simplepostpay_CCRoam");
            put(BILLING_ACCOUNT_NUMBER, "300001121");
        }});
    }};

    @Override
    public NZSubscriberInfo getSubscriberInfo(int userId, String msisdn) {
        Map<String, String> data = testData.containsKey(msisdn) ? testData.get(msisdn) : testData.get(DEFAULT_DATA);

        NZSubscriberInfo nzSubscriberInfo = new NZSubscriberInfo();
        nzSubscriberInfo.setUserId(userId);
        nzSubscriberInfo.setMsisdn(msisdn);
        nzSubscriberInfo.setPayIndicator(data.get(PAY_INDICATOR));
        nzSubscriberInfo.setProviderName(data.get(PROVIDER_NAME));
        nzSubscriberInfo.setBillingAccountName(data.get(BILLING_ACCOUNT_NAME));
        nzSubscriberInfo.setBillingAccountNumber(data.get(BILLING_ACCOUNT_NUMBER));
        nzSubscriberInfo.setActive(false);

        return nzSubscriberInfo;
    }


}
