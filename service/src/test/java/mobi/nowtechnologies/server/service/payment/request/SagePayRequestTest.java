package mobi.nowtechnologies.server.service.payment.request;

import mobi.nowtechnologies.common.dto.PaymentDetailsDto;

import java.math.BigDecimal;
import java.util.List;

import org.apache.http.NameValuePair;

import org.junit.*;

public class SagePayRequestTest {

    @Test
    public void createDeferRequestTest_Successful() {
        PaymentDetailsDto paymentDto = getPaymentDto();
        SagePayRequest request = new SagePayRequest();
        SagePayRequest deferRequest = request.createDeferRequest(paymentDto);

        List<NameValuePair> list = deferRequest.build();

        Assert.assertEquals(SagePayRequest.TxType.DEFERRED.toString(), list.get(0).getValue());
        Assert.assertEquals(paymentDto.getVendorTxCode(), list.get(1).getValue());
        Assert.assertEquals(paymentDto.getAmount(), list.get(2).getValue());
        Assert.assertEquals(paymentDto.getCurrency(), list.get(3).getValue());
        Assert.assertEquals(paymentDto.getDescription(), list.get(4).getValue());
        Assert.assertEquals(paymentDto.getCardHolderFirstName().concat(" ").concat(paymentDto.getCardHolderLastName()), list.get(5).getValue());
        Assert.assertEquals(paymentDto.getCardNumber(), list.get(6).getValue());
        Assert.assertEquals(paymentDto.getCardStartDate(), list.get(7).getValue());
        Assert.assertEquals(paymentDto.getCardExpirationDate(), list.get(8).getValue());
        Assert.assertEquals(paymentDto.getCardIssueNumber(), list.get(9).getValue());
        Assert.assertEquals(paymentDto.getCardCv2(), list.get(10).getValue());
        Assert.assertEquals(paymentDto.getCardType(), list.get(11).getValue());
        Assert.assertEquals(paymentDto.getBillingAddress1(), list.get(12).getValue());
        Assert.assertEquals(paymentDto.getBillingPostCode(), list.get(13).getValue());
        Assert.assertEquals(paymentDto.getBillingCity(), list.get(14).getValue());
        Assert.assertEquals(paymentDto.getBillingCountry(), list.get(15).getValue());
        Assert.assertEquals(paymentDto.getCardHolderLastName(), list.get(16).getValue());
        Assert.assertEquals(paymentDto.getCardHolderFirstName(), list.get(17).getValue());
    }

    @Test
    public void createReleaseRequest_Successful() {
        String currency = "GBP";
        String description = "Release Request";
        String vpsTxId = "123";
        String vendorTx = "123";
        String securityKey = "123";
        String txAuthNo = "123";
        BigDecimal amount = new BigDecimal("123");

        SagePayRequest request = new SagePayRequest();
        SagePayRequest releaseRrequest = request.createReleaseRequest(currency, description, vpsTxId, vendorTx, securityKey, txAuthNo, amount);
        List<NameValuePair> list = releaseRrequest.build();

        Assert.assertEquals(SagePayRequest.TxType.RELEASE.toString(), list.get(0).getValue());
        Assert.assertEquals(currency, list.get(1).getValue());
        Assert.assertEquals(description, list.get(2).getValue());
        Assert.assertEquals(vpsTxId, list.get(3).getValue());
        Assert.assertEquals(vendorTx, list.get(4).getValue());
        Assert.assertEquals(securityKey, list.get(5).getValue());
        Assert.assertEquals(txAuthNo, list.get(6).getValue());
        Assert.assertEquals(amount, new BigDecimal(list.get(7).getValue()));
    }

    @Test
    public void createRepeatRequest_Successful() {
        String currency = "GBP";
        String description = "Release Request";
        String vpsTxId = "123";
        String vendorTx = "123";
        String securityKey = "123";
        String txAuthNo = "123";
        String internalTxId = "1234";
        BigDecimal amount = new BigDecimal("123");

        SagePayRequest request = new SagePayRequest();
        SagePayRequest repeatRequest = request.createRepeatRequest(currency, description, vpsTxId, vendorTx, securityKey, txAuthNo, internalTxId, amount);
        List<NameValuePair> list = repeatRequest.build();

        Assert.assertEquals(SagePayRequest.TxType.REPEAT.toString(), list.get(0).getValue());
        Assert.assertEquals(currency, list.get(1).getValue());
        Assert.assertEquals(description, list.get(2).getValue());
        Assert.assertEquals(vpsTxId, list.get(3).getValue());
        Assert.assertEquals(internalTxId, list.get(4).getValue());
        Assert.assertEquals(vendorTx, list.get(5).getValue());
        Assert.assertEquals(securityKey, list.get(6).getValue());
        Assert.assertEquals(txAuthNo, list.get(7).getValue());
        Assert.assertEquals(amount, new BigDecimal(list.get(8).getValue()));
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
        paymentDto.setVendorTxCode("1234567890" + System.currentTimeMillis());
        paymentDto.setDescription("Making defer request for user");
        return paymentDto;
    }
}