package mobi.nowtechnologies.server.service.o2.impl;

import mobi.nowtechnologies.server.service.O2TariffService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import uk.co.o2.soa.coredata_1.AccountType;
import uk.co.o2.soa.manageorderdata_2.GetOrderList2;
import uk.co.o2.soa.manageorderdata_2.GetOrderList2Response;
import uk.co.o2.soa.managepostpayboltonsdata_2.GetCurrentBoltons;
import uk.co.o2.soa.managepostpayboltonsdata_2.GetCurrentBoltonsResponse;
import uk.co.o2.soa.managepostpaytariffdata_2.GetContract;
import uk.co.o2.soa.managepostpaytariffdata_2.GetContractResponse;
import uk.co.o2.soa.manageprepaytariffdata_2.GetTariff1;
import uk.co.o2.soa.manageprepaytariffdata_2.GetTariff1Response;
import uk.co.o2.soa.subscriberdata_2.GetSubscriberProfile;
import uk.co.o2.soa.subscriberdata_2.GetSubscriberProfileResponse;

import javax.xml.bind.JAXBElement;

/**
 * lach : 17/07/2013 : 11:29
 */
@Component
public class O2TariffServiceImpl implements O2TariffService {

    protected final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    private WebServiceGateway webServiceGateway;
    private String managePostpayTariffEndpoint;
    private String managePrepayTariffEndpoint;
    private String managePostpayBoltonEndpoint;
    private String subscriberEndpoint;
    private String manageOrderEndpoint;

    @Override
     public GetContractResponse getManagePostpayContract(String phoneNumber) {
        GetContract getContract = new GetContract();
        AccountType accountType = new AccountType();
        accountType.setMsisdn(phoneNumber);
        getContract.setCustomerId(accountType);

        uk.co.o2.soa.managepostpaytariffdata_2.ObjectFactory objectFactory = new uk.co.o2.soa.managepostpaytariffdata_2.ObjectFactory();
        JAXBElement<GetContract> wrappedContract =  objectFactory.createGetContract(getContract);

        return webServiceGateway.sendAndReceive(managePostpayTariffEndpoint, wrappedContract);
    }

    @Override
    public GetTariff1Response getManagePrepayTariff(String phoneNumber) {
        GetTariff1 getTariff = new GetTariff1();
        AccountType accountType = new AccountType();
        accountType.setMsisdn(phoneNumber);
        getTariff.setCustomerId(accountType);

        uk.co.o2.soa.manageprepaytariffdata_2.ObjectFactory objectFactory = new uk.co.o2.soa.manageprepaytariffdata_2.ObjectFactory();
        JAXBElement<GetTariff1> wrappedTariff = objectFactory.createGetTariff1(getTariff);

        return webServiceGateway.sendAndReceive(managePrepayTariffEndpoint, wrappedTariff);
    }

    @Override
    public GetCurrentBoltonsResponse getManagePostpayCurrentBoltons(String phoneNumber) {

        GetCurrentBoltons getCurrentBoltons = new GetCurrentBoltons();

        AccountType accountType = new AccountType();
        accountType.setMsisdn(phoneNumber);
        getCurrentBoltons.setCustomerId(accountType);

        uk.co.o2.soa.managepostpayboltonsdata_2.ObjectFactory objectFactory = new uk.co.o2.soa.managepostpayboltonsdata_2.ObjectFactory();
        JAXBElement<GetCurrentBoltons> wrappedBoltons = objectFactory.createGetCurrentBoltons(getCurrentBoltons);

        return webServiceGateway.sendAndReceive(managePostpayBoltonEndpoint, wrappedBoltons);
    }

    
    @Override
    public GetSubscriberProfileResponse getSubscriberProfile(String phoneNumber) {
    	GetSubscriberProfile request= new GetSubscriberProfile();
    	
    	request.setSubscriberID(phoneNumber);

        JAXBElement<GetSubscriberProfile> wrappedRequest = new uk.co.o2.soa.subscriberdata_2.ObjectFactory().createGetSubscriberProfile(request);

        return webServiceGateway.sendAndReceive(subscriberEndpoint, wrappedRequest);
    }
 
    
    @Override
    public GetOrderList2Response getOrderList(String phoneNumber) {

    	GetOrderList2 input = new GetOrderList2();
    	input.setMsisdn(phoneNumber);
    	
        uk.co.o2.soa.manageorderdata_2.ObjectFactory objectFactory = new uk.co.o2.soa.manageorderdata_2.ObjectFactory();
        JAXBElement<GetOrderList2> wrappedInput = objectFactory.createGetOrderList2(input);

        return webServiceGateway.sendAndReceive(manageOrderEndpoint, wrappedInput);
    }
    
    
    public void setWebServiceGateway(WebServiceGateway webServiceGateway) {
        this.webServiceGateway = webServiceGateway;
    }

    public void setManagePostpayTariffEndpoint(String managePostpayTariffEndpoint) {
        this.managePostpayTariffEndpoint = managePostpayTariffEndpoint;
    }

    public void setManagePostpayBoltonEndpoint(String managePostpayBoltonEndpoint) {
        this.managePostpayBoltonEndpoint = managePostpayBoltonEndpoint;
    }

    public void setManagePrepayTariffEndpoint(String managePrepayTariffEndpoint) {
        this.managePrepayTariffEndpoint = managePrepayTariffEndpoint;
    }

	public void setSubscriberEndpoint(String subscriberEndpoint) {
		this.subscriberEndpoint = subscriberEndpoint;
	}

	public void setManageOrderEndpoint(String manageOrderEndpoint) {
		this.manageOrderEndpoint = manageOrderEndpoint;
	}
    
}
