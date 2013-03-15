package mobi.nowtechnologies.server.service.impl;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;

import javax.xml.namespace.QName;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;

import mobi.nowtechnologies.server.dto.O2UserDetails;
import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.service.CommunityService;
import mobi.nowtechnologies.server.service.DeviceService;
import mobi.nowtechnologies.server.service.O2ClientService;
import mobi.nowtechnologies.server.service.exception.ExternalServiceException;
import mobi.nowtechnologies.server.service.exception.InvalidPhoneNumberException;
import mobi.nowtechnologies.server.service.o2.impl.WebServiceGateway;
import mobi.nowtechnologies.server.service.payment.response.O2Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.client.core.WebServiceMessageCallback;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.soap.SoapHeader;
import org.springframework.ws.soap.SoapHeaderElement;
import org.springframework.ws.soap.SoapMessage;

import uk.co.o2.soa.chargecustomerdata.BillSubscriber;
import uk.co.o2.soa.subscriberdata_2.GetSubscriberProfile;

public class O2ClientServiceImpl implements O2ClientService {
	private static final BigDecimal MULTIPLICAND_100 = new BigDecimal("100");

	protected final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

	public final static String VALIDATE_PHONE_REQ = "/user/carrier/o2/authorise/";
	public final static String GET_USER_DETAILS_REQ = "/user/carrier/o2/details/";

	private String serverO2Url;

	private String promotedServerO2Url;

	private RestTemplate restTemplate;

	private String redeemServerO2Url;

	private String redeemPromotedServerO2Url;

	private CommunityService communityService;

	private DeviceService deviceService;

	private WebServiceTemplate webServiceTemplate;

	private WebServiceGateway webServiceGateway;

	public void init() {
		restTemplate = new RestTemplate();
	}

	public void setServerO2Url(String serverO2Url) {
		this.serverO2Url = serverO2Url;
	}

	public void setPromotedServerO2Url(String promotedServerO2Url) {
		this.promotedServerO2Url = promotedServerO2Url;
	}

	public void setWebServiceTemplate(WebServiceTemplate webServiceTemplate) {
		this.webServiceTemplate = webServiceTemplate;
	}

	public void setWebServiceGateway(WebServiceGateway webServiceGateway) {
		this.webServiceGateway = webServiceGateway;
	}

	@Override
	public String getServerO2Url(String phoneNumber) {
		Community o2Community = communityService.getCommunityByName("o2");

		String serverO2Url = deviceService.isPromotedDevicePhone(o2Community, phoneNumber)
				? this.promotedServerO2Url
				: this.serverO2Url;

		return serverO2Url;
	}

	@Override
	public String getRedeemServerO2Url(String phoneNumber) {
		Community o2Community = communityService.getCommunityByName("o2");

		String redeemServerO2Url = deviceService.isPromotedDevicePhone(o2Community, phoneNumber)
				? this.redeemPromotedServerO2Url
				: this.redeemServerO2Url;

		return redeemServerO2Url;
	}

	public void setRedeemServerO2Url(String redeemServerO2Url) {
		this.redeemServerO2Url = redeemServerO2Url;
	}

	public void setCommunityService(CommunityService communityService) {
		this.communityService = communityService;
	}

	public void setDeviceService(DeviceService deviceService) {
		this.deviceService = deviceService;
	}

	public void setRedeemPromotedServerO2Url(String redeemPromotedServerO2Url) {
		this.redeemPromotedServerO2Url = redeemPromotedServerO2Url;
	}

	@Override
	public String validatePhoneNumber(String phoneNumber) {
		String serverO2Url = getServerO2Url(phoneNumber);

		MultiValueMap<String, Object> request = new LinkedMultiValueMap<String, Object>();
		request.add("phone_number", phoneNumber);

		try {
			DOMSource response = restTemplate.postForObject(serverO2Url + VALIDATE_PHONE_REQ, request, DOMSource.class);
			return response.getNode().getFirstChild().getFirstChild().getFirstChild().getNodeValue();
		} catch (Exception e) {
			LOGGER.error("Error of the number validation", e);
			throw new InvalidPhoneNumberException();
		}
	}

	@Override
	public O2UserDetails getUserDetails(String token, String phoneNumber) {
		String serverO2Url = getServerO2Url(phoneNumber);

		MultiValueMap<String, Object> request = new LinkedMultiValueMap<String, Object>();
		request.add("otac_auth_code", token);
		try {
			DOMSource response = restTemplate.postForObject(serverO2Url + GET_USER_DETAILS_REQ, request, DOMSource.class);
			return new O2UserDetails(response.getNode().getFirstChild().getFirstChild().getFirstChild().getNodeValue(), response.getNode().getFirstChild().getFirstChild().getNextSibling()
					.getFirstChild().getNodeValue());
		} catch (Exception e) {
			LOGGER.error("Error of the number validation", e);
			throw new ExternalServiceException("602", "O2 server cannot be reached");
		}
	}

	@Override
	public boolean isO2User(O2UserDetails userDetails) {
		if (userDetails != null && "o2".equals(userDetails.getOperator())) {
			return true;
		}
		return false;
	}

	@Override
	public boolean sendFreeSms(String phoneNumber, String message) {
		GetSubscriberProfile getSubscriberProfile = new GetSubscriberProfile();
		getSubscriberProfile.setSubscriberID("447702059016");

		// WebServiceMessageCallback webServiceMessageCallback = new WebServiceMessageCallback() {
		//
		// @Override
		// public void doWithMessage(WebServiceMessage message) throws IOException, TransformerException {
		// SoapMessage soapMessage = (SoapMessage) message;
		// SoapHeader soapHeader = soapMessage.getSoapHeader();
		//
		// QName soaConsumerTransactionIDQName = new QName("http://soa.o2.co.uk/coredata_1", "SOAConsumerTransactionID", "cor");
		// SoapHeaderElement soaConsumerTransactionIDHeaderElement = soapHeader.addHeaderElement(soaConsumerTransactionIDQName);
		// soaConsumerTransactionIDHeaderElement.setText(o2PhoneNumber + ":" + userId);
		// }
		// };
		//
		// LOGGER.info("Sent request to O2 with pending payment with internalTxId: [{}]", internalTxId);
		// Object objectResponse = webServiceTemplate.marshalSendAndReceive(billSubscriber, webServiceMessageCallback);
		Object objectResponse = webServiceGateway.sendAndReceive("https://sdpapi.ref.o2.co.uk/services/Subscriber_2_0", getSubscriberProfile);

		O2Response o2Response = O2Response.valueOf(objectResponse);
		return false;
	}

	@Override
	public O2Response makePremiumSMSRequest(final int userId, String internalTxId, BigDecimal subCost, final String o2PhoneNumber, String message) {
		LOGGER.debug("input parameters userId, internalTxId, subCost, o2PhoneNumber, message: [{}], [{}], [{}], [{}], [{}]", userId, internalTxId, subCost, o2PhoneNumber, message);

		final BigInteger subCostPences = subCost.multiply(MULTIPLICAND_100).toBigInteger();

		BillSubscriber billSubscriber = new BillSubscriber();

		billSubscriber.setMsisdn(o2PhoneNumber);
		billSubscriber.setSubMerchantId(null);
		billSubscriber.setPriceGross(subCostPences);
		billSubscriber.setPriceNet(null);
		billSubscriber.setDebitCredit("DEBIT");
		billSubscriber.setContentCategory(null);
		billSubscriber.setContentType(null);
		billSubscriber.setContentDescription(null);
		billSubscriber.setApplicationReference(internalTxId);
		billSubscriber.setSmsNotify(true);
		billSubscriber.setSmsMessage(message);
		billSubscriber.setPromotionCode(null);

		WebServiceMessageCallback webServiceMessageCallback = new WebServiceMessageCallback() {

			@Override
			public void doWithMessage(WebServiceMessage message) throws IOException, TransformerException {
				SoapMessage soapMessage = (SoapMessage) message;
				SoapHeader soapHeader = soapMessage.getSoapHeader();

				QName soaConsumerTransactionIDQName = new QName("http://soa.o2.co.uk/coredata_1", "SOAConsumerTransactionID", "cor");
				SoapHeaderElement soaConsumerTransactionIDHeaderElement = soapHeader.addHeaderElement(soaConsumerTransactionIDQName);
				soaConsumerTransactionIDHeaderElement.setText(o2PhoneNumber + ":" + userId);
			}
		};

		LOGGER.info("Sent request to O2 with pending payment with internalTxId: [{}]", internalTxId);
		Object objectResponse = webServiceTemplate.marshalSendAndReceive(billSubscriber, webServiceMessageCallback);

		O2Response o2Response = O2Response.valueOf(objectResponse);

		LOGGER.debug("Output parameter o2Response=[{}]", o2Response);
		return o2Response;
	}
}
