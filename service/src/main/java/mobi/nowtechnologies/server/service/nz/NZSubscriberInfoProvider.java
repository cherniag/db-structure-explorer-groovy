package mobi.nowtechnologies.server.service.nz;

/**
 * @author Anton Zemliankin
 */
public interface NZSubscriberInfoProvider {

    NZSubscriberResult getSubscriberResult(String msisdn) throws MsisdnNotFoundException, ProviderNotAvailableException;

}
