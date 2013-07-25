package mobi.nowtechnologies.server.service.o2.impl;

import mobi.nowtechnologies.server.service.O2Service;
import mobi.nowtechnologies.server.service.O2TariffService;
import mobi.nowtechnologies.server.service.aop.ProfileLoggingAspect;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import uk.co.o2.soa.manageorderdata_2.GetOrderList2Response;
import uk.co.o2.soa.managepostpayboltonsdata_2.GetCurrentBoltonsResponse;
import uk.co.o2.soa.managepostpaytariffdata_2.GetContractResponse;
import uk.co.o2.soa.manageprepaytariffdata_2.GetTariff1Response;
import uk.co.o2.soa.subscriberdata_2.GetSubscriberProfileResponse;

import com.google.common.base.CharMatcher;

@Component
public class O2ServiceImpl implements O2Service {

	private final Logger LOGGER = LoggerFactory.getLogger(O2ServiceImpl.class);

	// O2 is not ready yet to process those calls
	private static final boolean CALL_GET_ORDER_LIST = false;
	private static final boolean CALL_PREPAY_4G = false;

	@Autowired
	private O2TariffService o2TariffService;

	private O2WebServiceResultsProcessor resultsProcessor = new O2WebServiceResultsProcessor();

	@Override
	public O2SubscriberData getSubscriberData(String originalPhoneNumber) {
		long beforeExecutionTimeNano = System.nanoTime();
		Throwable error = null;
		O2SubscriberData data = null;
		try {

			return getSubscriberDataInternal(originalPhoneNumber);

		} catch (Exception ex) {
			LOGGER.error("Can't get subscriber data " + originalPhoneNumber, ex);
			throw new RuntimeException(ex);
		} finally {
			ProfileLoggingAspect.logThirdPartyRequest(beforeExecutionTimeNano, error, originalPhoneNumber, data,
					"getSubscriberProfile");
		}
	}

	private O2SubscriberData getSubscriberDataInternal(String originalPhoneNumber) {
		LOGGER.info("getSubscriberData " + originalPhoneNumber + " CALL_PREPAY_4G=" + CALL_PREPAY_4G
				+ " CALL_GET_ORDER_LIST=" + CALL_GET_ORDER_LIST);

		String digitOnlyPhoneNumber = getDigits(originalPhoneNumber);

		O2SubscriberData data = createSubscriberData(digitOnlyPhoneNumber);
		LOGGER.info("busness: {}, contract:{}, provider O2:{}", data.isBusinessOrConsumerSegment(),
				data.isContractPostPay(), data.isProviderO2());

		if (data.isProviderO2() && data.isConsumerSegment()) {
			if (data.isContractPostPay()) {

				data.setTariff4G(isPostPay4G(digitOnlyPhoneNumber));
				if (data.isTariff4G()) {
					if (CALL_GET_ORDER_LIST) {
						data.setDirectOrIndirect4GChannel(isPostPayDirectChannel(digitOnlyPhoneNumber));
					}
				}
			} else {
				if (CALL_PREPAY_4G) {
					prePayPopulate4G(digitOnlyPhoneNumber, data);
				}
			}
		}
		LOGGER.info("getSubscriberData completed {} result-{}", originalPhoneNumber, data);
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
}
