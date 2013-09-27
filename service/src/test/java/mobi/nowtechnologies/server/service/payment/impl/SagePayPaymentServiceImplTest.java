package mobi.nowtechnologies.server.service.payment.impl;

import junit.framework.Assert;
import mobi.nowtechnologies.common.dto.PaymentDetailsDto;
import mobi.nowtechnologies.server.persistence.domain.*;
import mobi.nowtechnologies.server.service.EntityService;
import mobi.nowtechnologies.server.service.exception.ServiceException;
import mobi.nowtechnologies.server.service.payment.http.SagePayHttpService;
import mobi.nowtechnologies.server.service.payment.response.SagePayResponse;
import mobi.nowtechnologies.server.shared.enums.PaymentDetailsStatus;
import mobi.nowtechnologies.server.shared.service.BasicResponse;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.UUID;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;

@Ignore
public class SagePayPaymentServiceImplTest {
	
	private SagePayPaymentServiceImpl service;
	private SagePayHttpService httpService;
	
	private String vpsTxId = "{94E05B24-E1FC-23CB-F2A7-24E595C89F02}";
	private String securityKey = "GXKLI5OUKU";
	private String txAuthNo = "133731";
	private EntityService entityService;
	
	@Before
	public void before() {
		service = new SagePayPaymentServiceImpl();
		service.setRetriesOnError(3);
		httpService = Mockito.mock(SagePayHttpService.class);			
			service.setHttpService(httpService);
		entityService = Mockito.mock(EntityService.class);
			Mockito.when(entityService.updateEntity(Mockito.anyObject())).thenAnswer(new Answer<Object>() {
				@Override public Object answer(InvocationOnMock invocation) throws Throwable {	return invocation.getArguments()[0]; }
			});
			
		service.setEntityService(entityService);
	}
	
	@Test
	public void createPaymentDetails_Successful() {
		
		Mockito.when(httpService.makeDeferRequest(Mockito.any(PaymentDetailsDto.class))).thenReturn(getSuccesfulSagePayResponse());
		
		PaymentDetailsDto paymentDto = getPaymentDto();
		User user = new User();
		PaymentPolicy paymentPolicy = new PaymentPolicy();
		SagePayCreditCardPaymentDetails paymentDetails = service.createPaymentDetails(paymentDto, user, paymentPolicy);
		
		
		Assert.assertEquals(paymentDto.getVendorTxCode(), paymentDetails.getVendorTxCode());
		Assert.assertEquals(vpsTxId, paymentDetails.getVPSTxId());
		Assert.assertEquals(securityKey, paymentDetails.getSecurityKey());
		Assert.assertEquals(txAuthNo, paymentDetails.getTxAuthNo());
		Assert.assertEquals(Boolean.FALSE, paymentDetails.getReleased());
		Assert.assertEquals(0, paymentDetails.getMadeRetries());
		Assert.assertEquals(service.getRetriesOnError(), paymentDetails.getRetriesOnError());
	}
	
	@Test
	public void reCreatePaymentDetails_Successful() {
		Mockito.when(httpService.makeDeferRequest(Mockito.any(PaymentDetailsDto.class))).thenReturn(getSuccesfulSagePayResponse());
		
		PaymentDetailsDto paymentDto = getPaymentDto();
		User user = new User();
		SagePayCreditCardPaymentDetails currentPaymentDetails = new SagePayCreditCardPaymentDetails();
				currentPaymentDetails.setI(12341234L);
				currentPaymentDetails.setLastPaymentStatus(PaymentDetailsStatus.SUCCESSFUL);
				currentPaymentDetails.setSecurityKey("weqwerqwer");
				currentPaymentDetails.setTxAuthNo("wertwert");
				currentPaymentDetails.setVendorTxCode("1234123412");
				currentPaymentDetails.setVPSTxId("12341234");
			user.addPaymentDetails(currentPaymentDetails);
		PaymentPolicy paymentPolicy = new PaymentPolicy();
		SagePayCreditCardPaymentDetails paymentDetails = service.createPaymentDetails(paymentDto, user, paymentPolicy);
		
		Assert.assertNotNull(paymentDetails.getI());
		Assert.assertEquals(paymentDto.getVendorTxCode(), paymentDetails.getVendorTxCode());
		Assert.assertEquals(vpsTxId, paymentDetails.getVPSTxId());
		Assert.assertEquals(securityKey, paymentDetails.getSecurityKey());
		Assert.assertEquals(txAuthNo, paymentDetails.getTxAuthNo());
		Assert.assertEquals(Boolean.FALSE, paymentDetails.getReleased());
		Assert.assertEquals(0, paymentDetails.getMadeRetries());
		Assert.assertEquals(service.getRetriesOnError(), paymentDetails.getRetriesOnError());
	}
	
	@Test(expected=ServiceException.class)
	public void createPaymentDetails_Fail_AwaitingPayment() {
		PaymentDetailsDto paymentDto = getPaymentDto();
		User user = new User();
		SagePayCreditCardPaymentDetails currentPaymentDetails = new SagePayCreditCardPaymentDetails();
		currentPaymentDetails.setI(12341234L);
		currentPaymentDetails.setLastPaymentStatus(PaymentDetailsStatus.AWAITING);
		currentPaymentDetails.setSecurityKey("weqwerqwer");
		currentPaymentDetails.setTxAuthNo("wertwert");
		currentPaymentDetails.setVendorTxCode("1234123412");
		currentPaymentDetails.setVPSTxId("12341234");
			user.addPaymentDetails(currentPaymentDetails);
		PaymentPolicy paymentPolicy = new PaymentPolicy();
		
		service.createPaymentDetails(paymentDto, user, paymentPolicy);
	}
	
	@Test(expected=ServiceException.class)
	public void createPaymentDetails_Fail_InvalidCardType() {
		
		Mockito.when(httpService.makeDeferRequest(Mockito.any(PaymentDetailsDto.class))).thenReturn(getFailSagePayResponse());
		
		PaymentDetailsDto paymentDto = getPaymentDto();
		User user = new User();
		PaymentPolicy paymentPolicy = new PaymentPolicy();
		service.createPaymentDetails(paymentDto, user, paymentPolicy);
	}
	
	@Test(expected=ServiceException.class)
	public void createPaymentDetails_Fail_HttpError() {
		
		Mockito.when(httpService.makeDeferRequest(Mockito.any(PaymentDetailsDto.class))).thenReturn(getHttpFailSagePayResponse());
		
		PaymentDetailsDto paymentDto = getPaymentDto();
		User user = new User();
		PaymentPolicy paymentPolicy = new PaymentPolicy();
		service.createPaymentDetails(paymentDto, user, paymentPolicy);
		
	}
	
	private SagePayResponse getFailSagePayResponse() {
		return new SagePayResponse(new BasicResponse(){
			@Override public String getMessage() { return "VPSProtocol=2.23\nStatus=INVALID\nStatusDetail=4022 : The Card Type selected does not match card number.,"; };
			@Override public int getStatusCode() { return HttpServletResponse.SC_OK; }
		});
	}
	
	private SagePayResponse getHttpFailSagePayResponse() {
		return new SagePayResponse(new BasicResponse(){
			@Override public String getMessage() { return "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\"><html xmlns=\"http://www.w3.org/1999/xhtml\"><head></head><body></body></html>"; };
			@Override public int getStatusCode() { return HttpServletResponse.SC_INTERNAL_SERVER_ERROR; }
		});
	}

	private SagePayResponse getSuccesfulSagePayResponse() {
		return new SagePayResponse(new BasicResponse(){
			@Override public String getMessage() { return "StatusDetail=0000 : The Authorisation was Successful.\nTxAuthNo="+txAuthNo+"\nAVSCV2=SECURITY CODE MATCH ONLY\n3DSecureStatus=NOTCHECKED\nVPSTxId="+vpsTxId+"\nStatus=OK\nAddressResult=NOTMATCHED\nPostCodeResult=MATCHED\nCV2Result=MATCHED\nSecurityKey="+securityKey+"\nVPSProtocol=2.23"; };
			@Override public int getStatusCode() { return HttpServletResponse.SC_OK; }
		});
	}
	
	private PaymentDetailsDto getPaymentDto() {
		PaymentDetailsDto paymentDto = new PaymentDetailsDto();
			paymentDto.setAmount("10");
			paymentDto.setBillingAddress("88");
			paymentDto.setBillingCity("Lugansk");
			paymentDto.setBillingCountry("UA");
			paymentDto.setBillingPostCode("412");
			paymentDto.setCardCv2("123");
			paymentDto.setCardExpirationDate("0113");
			paymentDto.setCardHolderFirstName("Dmitriy");
			paymentDto.setCardHolderLastName("Mayboroda");
			paymentDto.setCardIssueNumber("");
			paymentDto.setCardNumber("5404000000000001");
			paymentDto.setCardStartDate("0110");
			paymentDto.setCardType("MC");
			paymentDto.setCurrency("GBP");
			paymentDto.setVendorTxCode("1234567890"+System.currentTimeMillis());
			paymentDto.setDescription("Making defer request for user");
		return paymentDto;
	}
	
	/**
	 * Testing start payment procedure for SagePay system
	 * We send a release or repeat request to SagePay depends on lastPaymentStatus of the user
	 * If lastPaymentStatus of user equals to {@link PaymentDetailsStatus.NONE} means this user has never payed before
	 * and this is his first payment. In this case we make release request
	 */
	@Test
	public void startPayment_Successful() {
		
		Mockito.when(httpService.makeReleaseRequest(anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), any(BigDecimal.class))).thenReturn(getSagePayPayResponseSuccessful());
		
		PendingPayment pendingPayment = new PendingPayment();
			pendingPayment.setAmount(new BigDecimal(10));
			pendingPayment.setCurrencyISO("GBP");
			pendingPayment.setInternalTxId(UUID.randomUUID().toString());
			pendingPayment.setPaymentSystem(PaymentDetails.SAGEPAY_CREDITCARD_TYPE);
			pendingPayment.setSubweeks(2);
			pendingPayment.setTimestamp(System.currentTimeMillis());
		service.startPayment(pendingPayment);
	}

	private SagePayResponse getSagePayPayResponseSuccessful() {
		return new SagePayResponse(new BasicResponse(){
			@Override public String getMessage() { return "VPSProtocol=2.23\nStatus=OK\nStatusDetail=2004 : The Release was Successful."; };
			@Override public int getStatusCode() { return HttpServletResponse.SC_OK; }
		});
	}
}