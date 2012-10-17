package mobi.nowtechnologies.server.service.payment.request;

import java.math.BigDecimal;

import mobi.nowtechnologies.common.dto.PaymentDetailsDto;


// TODO Write validate() method to make validation of the request params before sending them to sage pay server
public class SagePayRequest extends AbstractPaymentRequest<SagePayRequest> {	
	
	public static final String VPS_PROTOCOL = "2.23";
	public static final String VENDOR = "chartsnowmobili";
	
	public static enum SageRequestParam implements PaymentRequestParam {
		TxType,
		Amount,
		Currency,
		Description,
		CardHolder,
		CardNumber,
		StartDate,
		ExpiryDate,
		IssueNumber,
		CV2,
		CardType,
		BillingAddress1,
		BillingPostCode,
		BillingCity,
		BillingCountry,
		BillingSurname,
		BillingFirstnames,
		VendorTxCode,
		VPSTxId,
		SecurityKey,
		TxAuthNo,
		ReleaseAmount,
		RelatedVPSTxId,
		RelatedSecurityKey,
		RelatedVendorTxCode,
		RelatedTxAuthNo,
		VPSProtocol,
		Vendor
	}
	
	public static enum TxType {
		PAYMENT,
		REFUND,
		REPEAT,
		AUTHENTICATE,
		AUTHORISE,
		CANCEL,
		RELEASE,
		DEFERRED;
	}
	
	protected SagePayRequest registrationTransactionRequest(PaymentDetailsDto paymentDto, String type) {
		SagePayRequest request = new SagePayRequest()
				.addParam(SageRequestParam.TxType, type)
				.addParam(SageRequestParam.VendorTxCode, paymentDto.getVendorTxCode())
				.addParam(SageRequestParam.Amount, paymentDto.getAmount())
				.addParam(SageRequestParam.Currency, paymentDto.getCurrency())
				.addParam(SageRequestParam.Description, paymentDto.getDescription())
				.addParam(SageRequestParam.CardHolder, paymentDto.getCardHolderFirstName().concat(" ").concat(paymentDto.getCardHolderLastName()))
				.addParam(SageRequestParam.CardNumber, paymentDto.getCardNumber())
				.addParam(SageRequestParam.StartDate, paymentDto.getCardStartDate())
				.addParam(SageRequestParam.ExpiryDate, paymentDto.getCardExpirationDate())
				.addParam(SageRequestParam.IssueNumber, paymentDto.getCardIssueNumber())
				.addParam(SageRequestParam.CV2, paymentDto.getCardCv2())
				.addParam(SageRequestParam.CardType, paymentDto.getCardType())
				.addParam(SageRequestParam.BillingAddress1, paymentDto.getBillingAddress1())
				.addParam(SageRequestParam.BillingPostCode, paymentDto.getBillingPostCode())
				.addParam(SageRequestParam.BillingCity, paymentDto.getBillingCity())
				.addParam(SageRequestParam.BillingCountry, paymentDto.getBillingCountry())
				.addParam(SageRequestParam.BillingSurname, paymentDto.getCardHolderLastName())
				.addParam(SageRequestParam.BillingFirstnames, paymentDto.getCardHolderFirstName())
				.addParam(SageRequestParam.Vendor, VENDOR)
				.addParam(SageRequestParam.VPSProtocol, VPS_PROTOCOL);
		return request;
	}
	
	public SagePayRequest createDeferRequest(PaymentDetailsDto paymentDto) {
		return registrationTransactionRequest(paymentDto, TxType.DEFERRED.toString());
	}
	
	public SagePayRequest createPaymentRequest(PaymentDetailsDto paymentDto) {
		return registrationTransactionRequest(paymentDto, TxType.PAYMENT.toString());
	}
	
	public SagePayRequest createReleaseRequest(String currencyCode, String description, String vpsTxId, String vendorTx, String securityKey, String txAuthNo, BigDecimal releaseAmount) {
		return new SagePayRequest()
				.addParam(SageRequestParam.TxType, TxType.RELEASE.toString())
				.addParam(SageRequestParam.Currency, currencyCode)
				.addParam(SageRequestParam.Description, description)
				.addParam(SageRequestParam.VPSTxId, vpsTxId)
				.addParam(SageRequestParam.VendorTxCode, vendorTx)
				.addParam(SageRequestParam.SecurityKey, securityKey)
				.addParam(SageRequestParam.TxAuthNo, txAuthNo)
				.addParam(SageRequestParam.ReleaseAmount, releaseAmount.toString())
				.addParam(SageRequestParam.Vendor, VENDOR)
				.addParam(SageRequestParam.VPSProtocol, VPS_PROTOCOL);
	}

	public SagePayRequest createRepeatRequest(String currencyCode, String description, String vpsTxId, String vendorTx, String securityKey, String txAuthNo, String internalTxId, BigDecimal amount) {
		return new SagePayRequest()
			.addParam(SageRequestParam.TxType, TxType.REPEAT.toString())
			.addParam(SageRequestParam.Currency, currencyCode)
			.addParam(SageRequestParam.Description, description)
			.addParam(SageRequestParam.RelatedVPSTxId, vpsTxId)
			.addParam(SageRequestParam.VendorTxCode, internalTxId)
			.addParam(SageRequestParam.RelatedVendorTxCode, vendorTx)
			.addParam(SageRequestParam.RelatedSecurityKey, securityKey)
			.addParam(SageRequestParam.RelatedTxAuthNo, txAuthNo)
			.addParam(SageRequestParam.Amount, amount.toString())
			.addParam(SageRequestParam.Vendor, VENDOR)
			.addParam(SageRequestParam.VPSProtocol, VPS_PROTOCOL);
	}
}