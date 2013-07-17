package mobi.nowtechnologies.server.service;


import uk.co.o2.soa.managepostpayboltonsdata_2.GetCurrentBoltonsResponse;
import uk.co.o2.soa.managepostpaytariffdata_2.GetContractResponse;
import uk.co.o2.soa.manageprepaytariffdata_2.GetTariff1Response;

public interface O2TariffService {

    GetContractResponse getManagePostpayContract(String phoneNumber);

    GetCurrentBoltonsResponse getManagePostpayCurrentBoltons(String phoneNumber);

    GetTariff1Response getManagePrepayTariff(String phoneNumber);

}
