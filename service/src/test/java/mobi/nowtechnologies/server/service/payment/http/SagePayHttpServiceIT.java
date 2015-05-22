package mobi.nowtechnologies.server.service.payment.http;

import mobi.nowtechnologies.common.dto.PaymentDetailsDto;
import mobi.nowtechnologies.server.service.payment.request.SagePayRequest;
import mobi.nowtechnologies.server.service.payment.response.SagePayResponse;
import mobi.nowtechnologies.server.support.http.PostService;

import javax.servlet.http.HttpServletResponse;

import java.math.BigDecimal;
import java.util.UUID;

import org.junit.*;
import static org.junit.Assert.*;

@Ignore
public class SagePayHttpServiceIT {

    private SagePayHttpService httpService;

    @Before
    public void before() {
        httpService = new SagePayHttpService();
        httpService.setDeferUrl("https://test.sagepay.com/gateway/service/vspdirect-register.vsp");
        httpService.setReleaseUrl("https://test.sagepay.com/gateway/service/release.vsp");
        httpService.setRepeatUrl("https://test.sagepay.com/gateway/service/repeat.vsp");
        httpService.setPostService(new PostService());
        httpService.setRequest(new SagePayRequest());
    }

    @Test
    public void makePaymentRequest_Successful() {
        SagePayResponse response = httpService.makePaymentRequest(getPaymentDto());

        assertEquals(true, response.isSuccessful());
        assertEquals(SagePayResponse.RESPONSE_ALL_MATCHED_VALUE, response.getAVSCV2());
        assertEquals(SagePayResponse.RESPONSE_MATCHED_VALUE, response.getAddress());
        assertEquals(SagePayResponse.RESPONSE_MATCHED_VALUE, response.getCV2());
        assertEquals(SagePayResponse.RESPONSE_MATCHED_VALUE, response.getPostCode());

        assertNotNull(response.getVPSTxId());
        assertNotNull(response.getSecurityKey());
        assertNotNull(response.getTxAuthNo());
    }

    @Test
    public void makeDeferRequest_Successful() {

        SagePayResponse response = httpService.makeDeferRequest(getPaymentDto());

        Assert.assertEquals(HttpServletResponse.SC_OK, response.getHttpStatus());
        Assert.assertEquals(true, response.isSuccessful());
        Assert.assertEquals(SagePayResponse.RESPONSE_ALL_MATCHED_VALUE, response.getAVSCV2());
        Assert.assertEquals(SagePayResponse.RESPONSE_MATCHED_VALUE, response.getAddress());
        Assert.assertEquals(SagePayResponse.RESPONSE_MATCHED_VALUE, response.getCV2());
        Assert.assertEquals(SagePayResponse.RESPONSE_MATCHED_VALUE, response.getPostCode());

        Assert.assertNotNull(response.getVPSTxId());
        Assert.assertNotNull(response.getSecurityKey());
        Assert.assertNotNull(response.getTxAuthNo());
    }

    @Test
    public void makeDeferRequest_Fail_NoVendorTxCode() {
        PaymentDetailsDto paymentDto = getPaymentDto();
        paymentDto.setVendorTxCode("");

        SagePayResponse response = httpService.makeDeferRequest(paymentDto);

        Assert.assertEquals(HttpServletResponse.SC_OK, response.getHttpStatus());
        Assert.assertEquals(false, response.isSuccessful());
        Assert.assertNotNull(response.getStatusDetail());
    }

    @Test
    public void makeDeferRequest_Fail_NoResponseFromSagePay() {
        httpService.setDeferUrl("https://test.sagepay.com/gateway/service/vspdirect.vsp");

        SagePayResponse response = httpService.makeDeferRequest(getPaymentDto());

        Assert.assertNotSame(HttpServletResponse.SC_OK, response.getHttpStatus());
        Assert.assertEquals(false, response.isSuccessful());
        Assert.assertNotNull(response.getDescriptionError());
    }

    @Test
    public void makeReleaseRequest_Successful() {
        PaymentDetailsDto paymentDto = getPaymentDto();
        SagePayResponse response = httpService.makeDeferRequest(paymentDto);

        Assert.assertEquals(HttpServletResponse.SC_OK, response.getHttpStatus());
        Assert.assertEquals(true, response.isSuccessful());

        response = httpService.makeReleaseRequest(paymentDto.getCurrency(),
                                                  "Making release payment",
                                                  response.getVPSTxId(),
                                                  paymentDto.getVendorTxCode(),
                                                  response.getSecurityKey(),
                                                  response.getTxAuthNo(),
                                                  new BigDecimal("10"));

        Assert.assertEquals(HttpServletResponse.SC_OK, response.getHttpStatus());
        Assert.assertEquals(true, response.isSuccessful());
    }

    @Test
    public void makeReleaseRequest_Fail_WrongVendorTxId() {
        PaymentDetailsDto paymentDto = getPaymentDto();
        SagePayResponse response = httpService.makeDeferRequest(paymentDto);

        Assert.assertEquals(HttpServletResponse.SC_OK, response.getHttpStatus());
        Assert.assertEquals(true, response.isSuccessful());

        response = httpService.makeReleaseRequest(paymentDto.getCurrency(),
                                                  "Making release payment",
                                                  response.getVPSTxId(),
                                                  "123234234",
                                                  response.getSecurityKey(),
                                                  response.getTxAuthNo(),
                                                  new BigDecimal("10"));

        Assert.assertEquals(HttpServletResponse.SC_OK, response.getHttpStatus());
        Assert.assertEquals(false, response.isSuccessful());
    }

    @Test
    public void makeRepeatRequest_Successful() {
        PaymentDetailsDto paymentDto = getPaymentDto();
        SagePayResponse deferResponse = httpService.makeDeferRequest(paymentDto);

        Assert.assertEquals(HttpServletResponse.SC_OK, deferResponse.getHttpStatus());
        Assert.assertEquals(true, deferResponse.isSuccessful());

        SagePayResponse response = httpService.makeReleaseRequest(paymentDto.getCurrency(),
                                                                  "Making release payment",
                                                                  deferResponse.getVPSTxId(),
                                                                  paymentDto.getVendorTxCode(),
                                                                  deferResponse.getSecurityKey(),
                                                                  deferResponse.getTxAuthNo(),
                                                                  new BigDecimal("10"));

        Assert.assertEquals(HttpServletResponse.SC_OK, response.getHttpStatus());
        Assert.assertEquals(true, response.isSuccessful());

        response = httpService.makeRepeatRequest(paymentDto.getCurrency(),
                                                 "Making repeat payment",
                                                 deferResponse.getVPSTxId(),
                                                 paymentDto.getVendorTxCode(),
                                                 deferResponse.getSecurityKey(),
                                                 deferResponse.getTxAuthNo(),
                                                 UUID.randomUUID().toString(),
                                                 new BigDecimal(paymentDto.getAmount()));

        Assert.assertEquals(HttpServletResponse.SC_OK, response.getHttpStatus());
        Assert.assertEquals(true, response.isSuccessful());
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
        paymentDto.setVendorTxCode(UUID.randomUUID().toString());
        paymentDto.setDescription("Making defer request for user");
        return paymentDto;
    }

}