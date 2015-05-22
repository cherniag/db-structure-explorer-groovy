package mobi.nowtechnologies.server.apptests.provider.o2;

import mobi.nowtechnologies.server.dto.ProviderUserDetails;
import mobi.nowtechnologies.server.service.o2.O2Service;
import mobi.nowtechnologies.server.service.o2.impl.O2SubscriberData;
import mobi.nowtechnologies.server.shared.enums.Contract;
import mobi.nowtechnologies.server.shared.enums.ContractChannel;
import mobi.nowtechnologies.server.shared.enums.ProviderType;
import mobi.nowtechnologies.server.shared.enums.SegmentType;
import mobi.nowtechnologies.server.shared.enums.Tariff;

/**
 * Author: Gennadii Cherniaiev Date: 7/3/2014
 */
public class O2ServiceStub implements O2Service {

    private O2PhoneExtensionsService o2PhoneExtensionsService;

    @Override
    public O2SubscriberData getSubscriberData(String phoneNumber) {
        O2PhoneExtensionsService.PhoneDataKey phoneDataKey = getPhoneDataKey(phoneNumber);

        O2SubscriberData o2SubscriberData = new O2SubscriberData();
        o2SubscriberData.setProviderO2(phoneDataKey.providerType == ProviderType.O2);
        o2SubscriberData.setBusinessOrConsumerSegment(phoneDataKey.segmentType == SegmentType.BUSINESS);
        o2SubscriberData.setContractPostPayOrPrePay(phoneDataKey.contract == Contract.PAYM);
        o2SubscriberData.setTariff4G(phoneDataKey.tariff == Tariff._4G);
        o2SubscriberData.setDirectOrIndirect4GChannel(phoneDataKey.contractChannel == ContractChannel.DIRECT);
        return o2SubscriberData;
    }

    @Override
    public String validatePhoneNumber(String url, String phoneNumber) {
        O2PhoneExtensionsService.PhoneDataKey phoneDataKey = getPhoneDataKey(phoneNumber);
        if(phoneDataKey.exception){
            throw new RuntimeException("Expected exception for phone: "+ phoneNumber);
        }
        return phoneNumber;
    }

    private O2PhoneExtensionsService.PhoneDataKey getPhoneDataKey(String phoneNumber) {
        String suffix = phoneNumber.substring(4, 6);
        return o2PhoneExtensionsService.getDataBySuffix(Integer.valueOf(suffix));
    }

    @Override
    public ProviderUserDetails getProviderUserDetails(String serverO2Url, String token) {
        String[] parts = token.split("#");
        ProviderUserDetails providerUserDetails = new ProviderUserDetails().withContract(parts[0]).withOperator(parts[1]);
        return providerUserDetails;
    }

    public void setO2PhoneExtensionsService(O2PhoneExtensionsService o2PhoneExtensionsService) {
        this.o2PhoneExtensionsService = o2PhoneExtensionsService;
    }
}
