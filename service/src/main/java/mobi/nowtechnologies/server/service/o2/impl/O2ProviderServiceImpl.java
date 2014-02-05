package mobi.nowtechnologies.server.service.o2.impl;

import mobi.nowtechnologies.server.dto.ProviderUserDetails;
import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.UserLog;
import mobi.nowtechnologies.server.persistence.domain.enums.UserLogStatus;
import mobi.nowtechnologies.server.persistence.domain.enums.UserLogType;
import mobi.nowtechnologies.server.persistence.repository.UserLogRepository;
import mobi.nowtechnologies.server.service.CommunityService;
import mobi.nowtechnologies.server.service.DeviceService;
import mobi.nowtechnologies.server.service.UserService;
import mobi.nowtechnologies.server.service.data.PhoneNumberValidationData;
import mobi.nowtechnologies.server.service.exception.ExternalServiceException;
import mobi.nowtechnologies.server.service.exception.InvalidPhoneNumberException;
import mobi.nowtechnologies.server.service.exception.LimitPhoneNumberValidationException;
import mobi.nowtechnologies.server.service.o2.O2Service;
import mobi.nowtechnologies.server.service.payment.response.O2Response;
import mobi.nowtechnologies.server.shared.Processor;
import mobi.nowtechnologies.server.shared.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.DOMException;
import uk.co.o2.soa.chargecustomerdata.BillSubscriber;
import uk.co.o2.soa.chargecustomerservice.BillSubscriberFault;
import uk.co.o2.soa.coredata.SOAFaultType;
import uk.co.o2.soa.subscriberdata.GetSubscriberProfile;
import uk.co.o2.soa.subscriberdata.GetSubscriberProfileResponse;

import javax.xml.transform.dom.DOMSource;
import java.math.BigDecimal;
import java.math.BigInteger;

import static mobi.nowtechnologies.server.persistence.domain.Community.O2_COMMUNITY_REWRITE_URL;
import static mobi.nowtechnologies.server.shared.ObjectUtils.isNotNull;
import static mobi.nowtechnologies.server.shared.enums.Contract.PAYM;
import static mobi.nowtechnologies.server.shared.enums.ProviderType.O2;


public class O2ProviderServiceImpl implements O2ProviderService {
	private static final BigDecimal MULTIPLICAND_100 = new BigDecimal("100");
	private static final String VALIDATE_PHONE_NUMBER_DESC = "validate_phonenumber";

	protected final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

	public final static String VALIDATE_PHONE_REQ = "/user/carrier/o2/authorise/";
	public final static String GET_USER_DETAILS_REQ = "/user/carrier/o2/details/";

	private String subscriberEndpoint;
	private String chargeCustomerEndpoint;
	private String sendMessageEndpoint;

	private String serverO2Url;

	private String promotedServerO2Url;

	private RestTemplate restTemplate;

	private String redeemServerO2Url;

	private String redeemPromotedServerO2Url;

	private CommunityService communityService;

	private DeviceService deviceService;

	private WebServiceGateway webServiceGateway;

	private UserLogRepository userLogRepository;
	
	private Integer limitValidatePhoneNumber;

    private UserService userService;

    private O2Service o2Service;

	public void init() {
		restTemplate = new RestTemplate();
	}

    public void setO2Service(O2Service o2Service) {
        this.o2Service = o2Service;
    }

    public void setLimitValidatePhoneNumber(Integer limitValidatePhoneNumber) {
		this.limitValidatePhoneNumber = limitValidatePhoneNumber;
	}

	public void setSubscriberEndpoint(String subscriberEndpoint) {
		this.subscriberEndpoint = subscriberEndpoint;
	}

	public void setChargeCustomerEndpoint(String chargeCustomerEndpoint) {
		this.chargeCustomerEndpoint = chargeCustomerEndpoint;
	}

	public void setSendMessageEndpoint(String sendMessageEndpoint) {
		this.sendMessageEndpoint = sendMessageEndpoint;
	}

	public void setUserLogRepository(UserLogRepository userLogRepository) {
		this.userLogRepository = userLogRepository;
	}

	public void setServerO2Url(String serverO2Url) {
		this.serverO2Url = serverO2Url;
	}

	public void setPromotedServerO2Url(String promotedServerO2Url) {
		this.promotedServerO2Url = promotedServerO2Url;
	}

	public void setWebServiceGateway(WebServiceGateway webServiceGateway) {
		this.webServiceGateway = webServiceGateway;
	}

	public void setRestTemplate(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Override
	public String getServerO2Url(String phoneNumber) {
		Community o2Community = communityService.getCommunityByName(O2_COMMUNITY_REWRITE_URL);

		String serverO2Url = (isPromoted(phoneNumber, o2Community) || isOtacPromoted(phoneNumber, o2Community))
				? this.promotedServerO2Url
				: this.serverO2Url;

		return serverO2Url;
	}

	@Override
	public String getRedeemServerO2Url(String phoneNumber) {
		Community o2Community = communityService.getCommunityByUrl(O2_COMMUNITY_REWRITE_URL);

		String redeemServerO2Url = (isPromoted(phoneNumber, o2Community) || isOtacPromoted(phoneNumber, o2Community))
				? this.redeemPromotedServerO2Url
				: this.redeemServerO2Url;

		return redeemServerO2Url;
	}

	private boolean isOtacPromoted(String phoneNumber, Community o2Community) {
		return deviceService.isOtacPromotedDevicePhone(o2Community, phoneNumber, null);
	}
	
	private boolean isPromoted(String phoneNumber, Community o2Community) {
		return deviceService.isPromotedDevicePhone(o2Community, phoneNumber, null);
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
	public PhoneNumberValidationData validatePhoneNumber(String phoneNumber) {
		String serverO2Url = getServerO2Url(phoneNumber);
		String url = serverO2Url + VALIDATE_PHONE_REQ;

		MultiValueMap<String, Object> request = new LinkedMultiValueMap<String, Object>();
		request.add("phone_number", phoneNumber);

		String result = handleValidatePhoneNumber(phoneNumber, url, request);

		return new PhoneNumberValidationData().withPhoneNumber(result);
	}

    @Override
    public void getSubscriberData(String phoneNumber, Processor<O2SubscriberData> processor) {
        O2SubscriberData data = o2Service.getSubscriberData(phoneNumber);
        data.setPhoneNumber(phoneNumber);

        processor.process(data);
    }

    private String handleValidatePhoneNumber(String phoneNumber, String url, MultiValueMap<String, Object> request) {
		LOGGER.info("VALIDATE_PHONE_NUMBER for[{}] url[{}]", phoneNumber, url);
		
		Long curDay = new Long(Utils.getEpochDays());
		String phoneNumberCode = phoneNumber.replaceAll("\\s", "");
		phoneNumberCode = phoneNumberCode.length() >= 10 ? phoneNumberCode.substring(phoneNumberCode.length()-10) : phoneNumberCode;
		Long countPerDay = userLogRepository.countByPhoneNumberAndDay(phoneNumberCode, UserLogType.VALIDATE_PHONE_NUMBER, curDay);
		UserLog userLog = null;
		if(countPerDay >= limitValidatePhoneNumber){
			LOGGER.error("VALIDATE_PHONE_NUMBER limit phone_number calls is exceeded for[{}] url[{}]", phoneNumber, url);
			throw new LimitPhoneNumberValidationException();
		}else{
			userLog = userLogRepository.findByPhoneNumber(phoneNumberCode, UserLogType.VALIDATE_PHONE_NUMBER);
			userLog = userLog != null && curDay.intValue() - Utils.toEpochDays(userLog.getLastUpdateMillis()) > 0 ? userLog : null;
		}
		
		try {
			DOMSource response = restTemplate.postForObject(url, request, DOMSource.class);
			String result = response.getNode().getFirstChild().getFirstChild().getFirstChild().getNodeValue();
			
			userLogRepository.save(new UserLog(userLog, phoneNumberCode, UserLogStatus.SUCCESS, UserLogType.VALIDATE_PHONE_NUMBER, VALIDATE_PHONE_NUMBER_DESC));
			
			return result;
		} catch (RestClientException e) {
			userLogRepository.save(new UserLog(userLog, phoneNumberCode, UserLogStatus.O2_FAIL, UserLogType.VALIDATE_PHONE_NUMBER, VALIDATE_PHONE_NUMBER_DESC));
			LOGGER.error("VALIDATE_PHONE_NUMBER error_msg[{}] for[{}] url[{}]", e.getMessage(), phoneNumber, url);
			throw new InvalidPhoneNumberException();
		} catch (DOMException e) {
			userLogRepository.save(new UserLog(userLog, phoneNumberCode, UserLogStatus.FAIL, UserLogType.VALIDATE_PHONE_NUMBER, VALIDATE_PHONE_NUMBER_DESC));
			LOGGER.error("VALIDATE_PHONE_NUMBER error_msg[{}] for[{}] url[{}]", e.getMessage(), phoneNumber, url);
			throw new InvalidPhoneNumberException();
		} catch (Exception e) {
			userLogRepository.save(new UserLog(userLog, phoneNumberCode, UserLogStatus.FAIL, UserLogType.VALIDATE_PHONE_NUMBER, VALIDATE_PHONE_NUMBER_DESC));
			LOGGER.error("VALIDATE_PHONE_NUMBER Error for[{}] error[{}]", phoneNumber, e.getMessage());
			throw new InvalidPhoneNumberException();
		} finally {
			LOGGER.info("VALIDATE_PHONE_NUMBER finished for[{}]", phoneNumber);
		}
	}

	@Override
	public ProviderUserDetails getUserDetails(String token, String phoneNumber, Community community) {
        if (userService.isPromotedDevice(phoneNumber, community)) return new ProviderUserDetails().withContract(PAYM.name()).withOperator("o2");

		String serverO2Url = getServerO2Url(phoneNumber);

		MultiValueMap<String, Object> request = new LinkedMultiValueMap<String, Object>();
		request.add("otac_auth_code", token);
		try {
			DOMSource response = restTemplate.postForObject(serverO2Url + GET_USER_DETAILS_REQ, request, DOMSource.class);
			return new ProviderUserDetails().withOperator(response.getNode().getFirstChild().getFirstChild().getFirstChild().getNodeValue()).withContract(response.getNode().getFirstChild().getFirstChild().getNextSibling()
					.getFirstChild().getNodeValue());
		} catch (Exception e) {
			LOGGER.error("Error of the number validation [{}]: [{}]", phoneNumber, e.getMessage());
			throw new ExternalServiceException("602", "O2 server cannot be reached");
		}
	}

	@Override
	public boolean isO2User(ProviderUserDetails userDetails) {
		if (isNotNull(userDetails) && O2.getKey().equals(userDetails.operator)) {
			return true;
		}
		return false;
	}

	@Override
	public boolean sendFreeSms(String phoneNumber, String message) {
		GetSubscriberProfile getSubscriberProfile = new GetSubscriberProfile();
		getSubscriberProfile.setSubscriberID("447702059016");

		GetSubscriberProfileResponse profileResponse = webServiceGateway.sendAndReceive(subscriberEndpoint, getSubscriberProfile);

		return profileResponse != null && profileResponse.getSubscriberProfile() != null;
	}

	@Override
	public O2Response makePremiumSMSRequest(final int userId, String internalTxId, BigDecimal subCost, final String o2PhoneNumber, String message, String contentCategory, String contentType,
			String contentDescription, String subMerchantId, boolean smsNotify) {
		LOGGER.debug(
				"input parameters userId, internalTxId, subCost, o2PhoneNumber, message, contentCategory, contentType, contentDescription, subMerchantId, smsNotify: [{}], [{}], [{}], [{}], [{}], [{}], [{}], [{}], [{}], [{}]",
				userId, internalTxId, subCost, o2PhoneNumber, message, contentCategory, contentType, contentDescription, subMerchantId, smsNotify);

		final BigInteger subCostPences = subCost.multiply(MULTIPLICAND_100).toBigInteger();

		BillSubscriber billSubscriber = new BillSubscriber();

		final String formattedO2PhoneNumber = o2PhoneNumber.replace("+", "");

		billSubscriber.setMsisdn(formattedO2PhoneNumber);
		billSubscriber.setSubMerchantId(subMerchantId);
		billSubscriber.setPriceGross(subCostPences);
		billSubscriber.setPriceNet(MULTIPLICAND_100.toBigInteger());
		billSubscriber.setDebitCredit("DEBIT");
		billSubscriber.setContentCategory(contentCategory);
		billSubscriber.setContentType(contentType);
		billSubscriber.setContentDescription(contentDescription);
		billSubscriber.setApplicationReference("");
		billSubscriber.setSmsNotify(smsNotify);
		billSubscriber.setSmsMessage(message);
		billSubscriber.setPromotionCode("");

		LOGGER.info("Sent request to O2 with pending payment with internalTxId: [{}]", internalTxId);

		Object response = null;
		try {
			response = webServiceGateway.sendAndReceive(chargeCustomerEndpoint, billSubscriber);
		} catch (SoapFaultException e) {
			response = new BillSubscriberFault(e.getMessage(), (SOAFaultType) e.getSoapFaultObject());
		}

		O2Response o2Response = O2Response.valueOf(response);

		LOGGER.debug("Output parameter o2Response=[{}]", o2Response);
		return o2Response;
	}
}
