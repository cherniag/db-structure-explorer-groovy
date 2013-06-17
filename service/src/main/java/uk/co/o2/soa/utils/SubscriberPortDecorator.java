package uk.co.o2.soa.utils;

import com.google.common.base.CharMatcher;
import com.google.common.base.Strings;

import uk.co.o2.soa.coredata.PaymentCategoryType;
import uk.co.o2.soa.coredata.SegmentType;
import uk.co.o2.soa.subscriberdata.*;
import uk.co.o2.soa.subscriberservice.*;

import javax.xml.ws.Binding;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Holder;
import javax.xml.ws.handler.Handler;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mobi.nowtechnologies.server.service.aop.ProfileLoggingAspect;
import mobi.nowtechnologies.server.shared.log.LogUtils;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class SubscriberPortDecorator implements SubscriberPort {

	private static final Logger LOGGER = LoggerFactory.getLogger(SubscriberPortDecorator.class);

	private SubscriberPort port;

	public SubscriberPortDecorator(SubscriberPort port) {
		this.port = port;
	}

	public SubscriberPortDecorator(SubscriberService service, String endpoint, String username, String password) {
		this.port = service.getSubscriberPort();
		this.setHandler(new SOAPLoggingHandler());
		this.setEndpoint(endpoint);
		this.setHandler(new SecurityHandler(username, password));
	}

	@Override
	public SubscriberProfileType getSubscriberProfile(String subscriberID) throws GetSubscriberProfileFault {
		Throwable throwable = null;
		SubscriberProfileType subscriberProfileType = null;
		long beforeExecutionTimeNano = System.nanoTime();
		String retainFrom = null;
		try {
			retainFrom = CharMatcher.DIGIT.retainFrom(subscriberID);
			subscriberProfileType = port.getSubscriberProfile(retainFrom);
			return subscriberProfileType;
		} catch (GetSubscriberProfileFault gSPF) {
			throwable = gSPF;
			throw gSPF;
		} catch (RuntimeException re) {
			throwable = re;
			throw re;
		} finally {
			try {
				if (ProfileLoggingAspect.THIRD_PARTY_REQUESTS_PROFILE_LOGGER.isDebugEnabled()) {
					String errorMessage = null;
					String result = "success";
					if (throwable != null) {
						errorMessage = throwable.getMessage();
						result = "fail";
					}
					long executionDurationMillis = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - beforeExecutionTimeNano);

					LogUtils.set3rdParyRequestProfileMDC(executionDurationMillis, errorMessage, result, "http://soa.o2.co.uk/subscriberdata_2", null, retainFrom, subscriberProfileType);

					ProfileLoggingAspect.THIRD_PARTY_REQUESTS_PROFILE_LOGGER.debug("THIRD_PARTY_REQUESTS_PROFILE_LOGGER values in the MDC");
				}
			} catch (Exception e) {
				LOGGER.error(e.getMessage(), e);
			}
		}
	}

	@Override
	public SegmentType getSubscriberSegment(String subscriberID) throws GetSubscriberSegmentFault {
		return null;
	}

	@Override
	public PaymentCategoryType getSubscriberPaymentCategory(String subscriberID) throws GetSubscriberPaymentCategoryFault {
		return null;
	}

	@Override
	public String getSubscriberOperator(String subscriberID) throws GetSubscriberOperatorFault {
		return null;
	}

	@Override
	public String getSubscriberChannel(String subscriberID) throws GetSubscriberChannelFault {
		return null;
	}

	@Override
	public BillingProfileType getBillingSystem(String msisdn) throws GetBillingSystemFault {
		return port.getBillingSystem(msisdn);
	}

	@Override
	public void getSubscriberAndBillingSystem(String msisdn, Holder<uk.co.o2.soa.coredata.SubscriberProfileType> subscriberProfile, Holder<BillingProfileType> billingProfile)
			throws GetSubscriberAndBillingSystemFault {

	}

	@Override
	public void getBillProfile(String msisdn, Holder<String> accountNumber, Holder<BillingProfileType> billingSystem, Holder<MsisdnList> msisdnList) throws GetBillProfileFault {

	}

	@Override
	public List<String> getChargingCategory(String msisdn, ProductListType productList) throws GetChargingCategoryFault {
		return null;
	}

	@Override
	public ServiceProviderDetailsType getSPIDDetails1(String serviceProviderId) throws GetSPIDDetails1Fault {
		return null;
	}

	public void setEndpoint(String endpoint) {
		BindingProvider provider = (BindingProvider) port;
		Map<String, Object> context = provider.getRequestContext();
		context.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, endpoint);
	}

	public void setHandler(SOAPHandler<SOAPMessageContext> handler) {
		BindingProvider provider = (BindingProvider) port;
		Binding binding = provider.getBinding();
		List<Handler> handlerChain = binding.getHandlerChain();
		handlerChain.add(handler);
		binding.setHandlerChain(handlerChain);
	}
}
