package mobi.nowtechnologies.server.service.o2;


import uk.co.o2.soa.manageorderdata_2.GetOrderList2Response;
import uk.co.o2.soa.managepostpayboltonsdata_2.GetCurrentBoltonsResponse;
import uk.co.o2.soa.managepostpaytariffdata_2.GetContractResponse;
import uk.co.o2.soa.manageprepaytariffdata_2.GetTariff1Response;
import uk.co.o2.soa.subscriberdata_2.GetSubscriberProfileResponse;

public interface O2TariffService {

    GetContractResponse getManagePostpayContract(String phoneNumber);

    GetCurrentBoltonsResponse getManagePostpayCurrentBoltons(String phoneNumber);

    GetTariff1Response getManagePrepayTariff(String phoneNumber);

    GetSubscriberProfileResponse getSubscriberProfile(String phoneNumber);

    GetOrderList2Response getOrderList(String phoneNumber);
}
