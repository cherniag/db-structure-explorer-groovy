package mobi.nowtechnologies.server.service.o2.impl;

import mobi.nowtechnologies.server.dto.ProviderUserDetails;
import mobi.nowtechnologies.server.service.o2.O2Service;
import mobi.nowtechnologies.server.service.o2.O2TariffService;
import mobi.nowtechnologies.server.service.aop.ProfileLoggingAspect;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import uk.co.o2.soa.manageorderdata_2.GetOrderList2Response;
import uk.co.o2.soa.managepostpayboltonsdata_2.GetCurrentBoltonsResponse;
import uk.co.o2.soa.managepostpaytariffdata_2.GetContractResponse;
import uk.co.o2.soa.manageprepaytariffdata_2.GetTariff1Response;
import uk.co.o2.soa.subscriberdata_2.GetSubscriberProfileResponse;

import com.google.common.base.CharMatcher;

import javax.xml.transform.dom.DOMSource;

@Component
public class O2ServiceImpl implements O2Service {

	private final Logger LOGGER = LoggerFactory.getLogger(O2ServiceImpl.class);

	@Autowired
	private O2TariffService o2TariffService;
    private RestTemplate restTemplate;
	private O2WebServiceResultsProcessor resultsProcessor = new O2WebServiceResultsProcessor();

	@Override
	public O2SubscriberData getSubscriberData(String originalPhoneNumber) {
		long beforeExecutionTimeNano = System.nanoTime();
		Throwable error = null;
		O2SubscriberData data = null;
		try {
            data = getSubscriberDataInternal(originalPhoneNumber);
			return data;
		} catch (Exception ex) {
			LOGGER.error("Can't get subscriber data " + originalPhoneNumber, ex);
			throw new RuntimeException(ex);
		} finally {
			ProfileLoggingAspect.logThirdPartyRequest(beforeExecutionTimeNano, error, originalPhoneNumber, data,
					"getSubscriberProfile");
		}
	}

    @Override
    public String validatePhoneNumber(String url, String validatedPhoneNumber) {
        MultiValueMap<String, Object> request = new LinkedMultiValueMap<String, Object>();
        request.add("phone_number", validatedPhoneNumber);
        DOMSource response = restTemplate.postForObject(url, request, DOMSource.class);
        return response.getNode().getFirstChild().getFirstChild().getFirstChild().getNodeValue();
    }

    @Override
    public ProviderUserDetails getProviderUserDetails(String serverO2Url, String token) {
        MultiValueMap<String, Object> request = new LinkedMultiValueMap<String, Object>();
        request.add("otac_auth_code", token);
        DOMSource response = restTemplate.postForObject(serverO2Url, request, DOMSource.class);
        return new ProviderUserDetails()
                .withOperator(response.getNode().getFirstChild().getFirstChild().getFirstChild().getNodeValue())
                .withContract(response.getNode().getFirstChild().getFirstChild().getNextSibling()
                        .getFirstChild().getNodeValue());
    }

    private O2SubscriberData getSubscriberDataInternal(String originalPhoneNumber) {
		LOGGER.info("getSubscriberData " + originalPhoneNumber);
		String digitOnlyPhoneNumber = getDigits(originalPhoneNumber);

		O2SubscriberData data = createSubscriberData(digitOnlyPhoneNumber);
		LOGGER.info("phone:{}, business:{}, contract:{}, provider O2:{}", digitOnlyPhoneNumber,
				data.isBusinessOrConsumerSegment(), data.isContractPostPay(), data.isProviderO2());

		if (data.isProviderO2() && data.isConsumerSegment()) {
			if (data.isContractPostPay()) {
				data.setTariff4G(isPostPay4G(digitOnlyPhoneNumber));
				if (data.isTariff4G()) {
					data.setDirectOrIndirect4GChannel(isPostPayDirectChannel(digitOnlyPhoneNumber));
				}
			} else {
				prePayPopulate4G(digitOnlyPhoneNumber, data);
			}
		}
		LOGGER.info("getSubscriberData completed for {} result-{}", originalPhoneNumber, data);
		return data;
	}

	/** @return string that have only digits from the given string */
	public static String getDigits(String source) {
		return CharMatcher.DIGIT.retainFrom(source);
	}

	private O2SubscriberData createSubscriberData(String digitOnlyPhoneNumber) {
		GetSubscriberProfileResponse subscriberProfile = o2TariffService.getSubscriberProfile(digitOnlyPhoneNumber);
		O2SubscriberData o2subscriberData = resultsProcessor.getSubscriberData(subscriberProfile);
		return o2subscriberData;
	}

	private boolean isPostPay4G(String digitOnlyPhoneNumber) {
		GetContractResponse postPayContract = o2TariffService.getManagePostpayContract(digitOnlyPhoneNumber);

		boolean subscribedTo4G = false;
		if (resultsProcessor.isPostPayContract4G(postPayContract)) {
			subscribedTo4G = true;
		} else {
			GetCurrentBoltonsResponse boltons = o2TariffService.getManagePostpayCurrentBoltons(digitOnlyPhoneNumber);
			if (resultsProcessor.isPostPay4GBoltonPresent(boltons)) {
				subscribedTo4G = true;
			}
		}
		return subscribedTo4G;
	}

	private boolean isPostPayDirectChannel(String digitOnlyPhoneNumber) {
		GetOrderList2Response orderList = o2TariffService.getOrderList(digitOnlyPhoneNumber);
		return resultsProcessor.isPostpayDirectPartner(orderList, digitOnlyPhoneNumber);
	}

	private void prePayPopulate4G(String digitOnlyPhoneNumber, O2SubscriberData data) {
		GetTariff1Response prepayTariff = o2TariffService.getManagePrepayTariff(digitOnlyPhoneNumber);
		resultsProcessor.populatePrepay4G(prepayTariff, data);
	}

	public void setO2TariffService(O2TariffService o2TariffService) {
		this.o2TariffService = o2TariffService;
	}

    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
}
