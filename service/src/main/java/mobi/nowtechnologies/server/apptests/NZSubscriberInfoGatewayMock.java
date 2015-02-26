package mobi.nowtechnologies.server.apptests;

import mobi.nowtechnologies.server.service.nz.MsisdnNotFoundException;
import mobi.nowtechnologies.server.service.nz.NZSubscriberInfoProvider;
import mobi.nowtechnologies.server.service.nz.NZSubscriberResult;
import mobi.nowtechnologies.server.service.nz.ProviderNotAvailableException;

/**
 * @author Anton Zemliankin
 */
public class NZSubscriberInfoGatewayMock implements NZSubscriberInfoProvider {
    public static int notAvailablePrefix = 6;
    public static int doesNotBelong = 9;

    @Override
    public NZSubscriberResult getSubscriberResult(String msisdn) throws MsisdnNotFoundException, ProviderNotAvailableException {
        final String vodafoneMsisdnPrefix = "64";

        if(!msisdn.startsWith(vodafoneMsisdnPrefix)) {
            throw new MsisdnNotFoundException("MSISDN NOT FOUND IN INFRANET");
        }

        final String notFoundPrefix = vodafoneMsisdnPrefix + NZSubscriberInfoGatewayMock.doesNotBelong;
        if(msisdn.startsWith(notFoundPrefix)) {
            return new NZSubscriberResult("Prepay", "Unknown Operator", "300001121", "Simplepostpay_CCRoam");
        }

        final String notAvailablePrefix = vodafoneMsisdnPrefix + NZSubscriberInfoGatewayMock.notAvailablePrefix;
        if (msisdn.startsWith(notAvailablePrefix)) {
            throw new ProviderNotAvailableException("Test Reason.");
        }

        return new NZSubscriberResult("Prepay", "Vodafone", "300001121", "Simplepostpay_CCRoam");
    }

}
