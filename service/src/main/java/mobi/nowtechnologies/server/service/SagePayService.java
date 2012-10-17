package mobi.nowtechnologies.server.service;

import static mobi.nowtechnologies.server.shared.AppConstants.NOT_AVAILABLE;
import static mobi.nowtechnologies.server.shared.AppConstants.STATUS_OK;
import static mobi.nowtechnologies.server.shared.Utils.getEpochSeconds;

import java.io.IOException;
import java.io.StringReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.servlet.http.HttpServletResponse;

import mobi.nowtechnologies.common.dto.UserRegInfo;
import mobi.nowtechnologies.common.dto.UserRegInfo.PaymentType;
import mobi.nowtechnologies.server.persistence.dao.PaymentDao.TxType;
import mobi.nowtechnologies.server.persistence.dao.PaymentStatusDao;
import mobi.nowtechnologies.server.persistence.domain.CreditCardPayment;
import mobi.nowtechnologies.server.persistence.domain.Payment;
import mobi.nowtechnologies.server.persistence.domain.PremiumUserPayment;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.service.exception.SagePayException;
import mobi.nowtechnologies.server.service.exception.ServiceException;
import mobi.nowtechnologies.server.shared.AppConstants;
import mobi.nowtechnologies.server.shared.service.PostService;
import mobi.nowtechnologies.server.shared.service.PostService.Response;
import mobi.nowtechnologies.server.shared.util.URLValidation;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

/**
 * SagePayService
 * 
 * @author Maksym Chernolevskyi (maksym)
 * 
 */
@Deprecated
public class SagePayService {
	private static final String PAYMENT_SYSTEM_ERROR_MESSAGE = "Payment system error: ";

	private static final Logger LOGGER = LoggerFactory.getLogger(SagePayService.class);

	private static final String VSPPROTOCOL = "2.23";
	private static final String VENDOR = "chartsnowmobili";

	private String urlDeferred;
	private String urlRelease;
	private String urlRepeat;

	private UserService userService;

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	private enum PARAMETER_OUT {
		VPSProtocol, TxType, Vendor, VendorTxCode, Amount, Currency, // GBP
		Description, CardHolder, CardNumber, StartDate, // optional MMYY
		ExpiryDate, // MMYY
		IssueNumber, // optional
		CV2, CardType, // VISA, MC, DELTA, MAESTRO, UKE, AMEX, DC, JCB, LASER,
						// PAYPAL
		BillingSurname, BillingFirstnames, BillingAddress1, BillingAddress2, // optional
		BillingCity, BillingPostCode, BillingCountry, // GB
		BillingState, // optional
		BillingPhone, // optional
		DeliverySurname, DeliveryFirstnames, DeliveryAddress1, DeliveryAddress2, // optional
		DeliveryCity, DeliveryPostCode, DeliveryCountry, DeliveryState, // optional
		DeliveryPhone, // optional
		PayPalCallbackURL, // optional
		CustomerEMail, // optional
		Basket, // optional
		GiftAidPayment, // optional
		ApplyAVSCV2, // optional
		ClientIPAddress, // optional
		Apply3DSecure, // optional
		AccountType, // optional
		BillingAgreement, // optional,

		RelatedVPSTxId, RelatedVendorTxCode, RelatedSecurityKey,

		VPSTxId, SecurityKey, TxAuthNo, ReleaseAmount,

		RelatedTxAuthNo
	}

	public enum SagePayMode {
		test, live
	}

	private static final PostService POST_SERVICE = new PostService();

	public void init() throws Exception {
		if (urlDeferred == null)
			throw new NullPointerException("The parameter sagepay.deferUrl is null");
		if (urlRelease == null)
			throw new NullPointerException("The parameter sagepay.releaseUrl is null");
		if (urlRepeat == null)
			throw new NullPointerException("The parameter sagepay.repeatUrl is null");

		if (!URLValidation.validate(urlDeferred))
			throw new Exception("The parameter sagepay.deferUrl is not valid url");
		if (!URLValidation.validate(urlRelease))
			throw new Exception("The parameter sagepay.releaseUrl is not valid url");
		if (!URLValidation.validate(urlRepeat))
			throw new Exception("The parameter sagepay.repeatUrl is not valid url");
	}

	public void setUrlDeferred(String urlDeferred) {
		this.urlDeferred = urlDeferred;
	}

	public void setUrlRelease(String urlRelease) {
		this.urlRelease = urlRelease;
	}

	public void setUrlRepeat(String urlRepeat) {
		this.urlRepeat = urlRepeat;
	}

	public Payment makeDeferredPayment(UserRegInfo userRegInfo, BigDecimal amount, byte subweeks, String currency, String description, String vendorTxCode) {
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair(PARAMETER_OUT.VendorTxCode.toString(), vendorTxCode));

		nameValuePairs.add(new BasicNameValuePair(PARAMETER_OUT.TxType.toString(), TxType.DEFERRED.toString()));

		nameValuePairs.add(new BasicNameValuePair(PARAMETER_OUT.Amount.toString(), amount.toString()));
		nameValuePairs.add(new BasicNameValuePair(PARAMETER_OUT.Currency.toString(), currency));
		nameValuePairs.add(new BasicNameValuePair(PARAMETER_OUT.Description.toString(), description));

		nameValuePairs.add(new BasicNameValuePair(PARAMETER_OUT.CardHolder.toString(), userRegInfo.getCardHolderFirstName().toUpperCase() + " "
				+ userRegInfo.getCardHolderLastName().toUpperCase()));
		nameValuePairs.add(new BasicNameValuePair(PARAMETER_OUT.CardNumber.toString(), userRegInfo.getCardNumber()));
		nameValuePairs.add(new BasicNameValuePair(PARAMETER_OUT.StartDate.toString(), toMMYY(userRegInfo.getCardStartMonth(), userRegInfo.getCardStartYear())));
		nameValuePairs.add(new BasicNameValuePair(PARAMETER_OUT.ExpiryDate.toString(), toMMYY(userRegInfo.getCardExpirationMonth(),
				userRegInfo.getCardExpirationYear())));
		nameValuePairs.add(new BasicNameValuePair(PARAMETER_OUT.IssueNumber.toString(), userRegInfo.getCardIssueNumber()));
		nameValuePairs.add(new BasicNameValuePair(PARAMETER_OUT.CV2.toString(), userRegInfo.getCardCv2()));
		nameValuePairs.add(new BasicNameValuePair(PARAMETER_OUT.CardType.toString(), userRegInfo.getCardType()));
		nameValuePairs.add(new BasicNameValuePair(PARAMETER_OUT.BillingAddress1.toString(), userRegInfo.getCardBillingAddress()));
		nameValuePairs.add(new BasicNameValuePair(PARAMETER_OUT.BillingPostCode.toString(), userRegInfo.getCardBillingPostCode()));
		nameValuePairs.add(new BasicNameValuePair(PARAMETER_OUT.BillingCity.toString(), userRegInfo.getCardBillingCity()));
		nameValuePairs.add(new BasicNameValuePair(PARAMETER_OUT.BillingCountry.toString(), userRegInfo.getCardBillingCountry()));
		nameValuePairs.add(new BasicNameValuePair(PARAMETER_OUT.BillingSurname.toString(), userRegInfo.getCardHolderLastName()));
		nameValuePairs.add(new BasicNameValuePair(PARAMETER_OUT.BillingFirstnames.toString(), userRegInfo.getCardHolderFirstName()));

		Response reply = processTransaction(urlDeferred, nameValuePairs, TxType.DEFERRED.getCode());
		LOGGER.info("Sage pay system response is [{}]", reply.toString());
		Properties properties = new Properties();
		try {
			properties.load(new StringReader(reply.getMessage()));
		} catch (IOException e) {
			LOGGER.error("can't parse sage pay reply");
			throw new ServiceException("can't parse sage pay reply", e);
		}
		return processDeferredReply(properties, vendorTxCode, amount, subweeks, description, userRegInfo.getPaymentType(), reply.getStatusCode());
	}

	private String toMMYY(Integer month, Integer year) {
		if (month == null || year == null)
			return null;
		if (month <= 0 || month > 12)
			throw new ServiceException("month <= 0 or month > 12");
		if (year < 0 || year > 2099)
			throw new ServiceException("year < 0 or year > 2099");
		return (month < 10 ? "0" : "") + month + "" + (year % 100 < 10 ? "0" : "") + year % 100;
	}

	private Payment processDeferredReply(Properties properties, String vendorTxCode, BigDecimal amount, byte subweeks, String description, String paymentType,
			int httpStatusCode) {
		Payment payment;
		if (paymentType.equals(PaymentType.CREDIT_CARD)) {
			payment = new CreditCardPayment();
		} else if (paymentType.equals(PaymentType.PREMIUM_USER)) {
			payment = new PremiumUserPayment();
		} else
			throw new ServiceException("Unknow payment type: [" + paymentType + "]");

		String status = properties.getProperty("Status");
		checkSuccessfullReply(0, properties, vendorTxCode, amount, subweeks, status, TxType.DEFERRED.getCode(), paymentType, httpStatusCode);
		LOGGER.info("sage pay deferred (initial transaction) payment succeeded");
		payment.setExternalTxCode(properties.getProperty("VPSTxId"));
		payment.setExternalSecurityKey(properties.getProperty("SecurityKey"));
		payment.setExternalAuthCode(properties.getProperty("TxAuthNo"));
		payment.setInternalTxCode(vendorTxCode);
		payment.setStatus(status);
		payment.setStatusDetail(properties.getProperty("StatusDetail"));
		payment.setTimestamp(getEpochSeconds());
		payment.setDescription(description);

		payment.setRelatedPayment(0L);
		payment.setTxType(TxType.DEFERRED.getCode());
		payment.setAmount(amount.floatValue());
		payment.setSubweeks(subweeks);
		return payment;
	}

	public Payment release(User user, Payment deferredPayment, BigDecimal amount, byte subweeks, String currency, String description, String vendorTxCode) {
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair(PARAMETER_OUT.TxType.toString(), TxType.RELEASE.toString()));
		nameValuePairs.add(new BasicNameValuePair(PARAMETER_OUT.Currency.toString(), currency));
		nameValuePairs.add(new BasicNameValuePair(PARAMETER_OUT.Description.toString(), description));

		nameValuePairs.add(new BasicNameValuePair(PARAMETER_OUT.VendorTxCode.toString(), vendorTxCode));
		nameValuePairs.add(new BasicNameValuePair(PARAMETER_OUT.VPSTxId.toString(), deferredPayment.getExternalTxCode()));
		nameValuePairs.add(new BasicNameValuePair(PARAMETER_OUT.SecurityKey.toString(), deferredPayment.getExternalSecurityKey()));
		nameValuePairs.add(new BasicNameValuePair(PARAMETER_OUT.TxAuthNo.toString(), deferredPayment.getExternalAuthCode()));
		nameValuePairs.add(new BasicNameValuePair(PARAMETER_OUT.ReleaseAmount.toString(), amount.toString()));

		Response reply = processTransaction(urlRelease, nameValuePairs, TxType.RELEASE.getCode());
		LOGGER.info("Sage pay system response is [{}]", reply.toString());

		int statusCode = reply.getStatusCode();
		if (statusCode != HttpServletResponse.SC_OK) {
			user.setPaymentEnabled(false);
			userService.updateUser(user);
			throw new ServiceException("Sage pay error [" + reply.toString() + "]");
		}

		Properties properties = new Properties();
		try {
			properties.load(new StringReader(reply.getMessage()));
		} catch (IOException e) {
			LOGGER.error("can't parse sage pay reply");
			user.setPaymentEnabled(false);
			userService.updateUser(user);
			throw new ServiceException("can't parse sage pay reply", e);
		}
		return processReleaseReply(user.getId(), properties, vendorTxCode, amount, subweeks, deferredPayment.getI(), description, user.getPaymentType(),
				statusCode);
	}

	private Payment processReleaseReply(int userId, Properties properties, String vendorTxCode, BigDecimal amount, byte subweeks, long deferredPaymentId,
			String description, String paymentType, int httpStatusCode) {
		Payment payment;
		if (paymentType.equals(PaymentType.CREDIT_CARD)) {
			payment = new CreditCardPayment();
		} else if (paymentType.equals(PaymentType.PREMIUM_USER)) {
			payment = new PremiumUserPayment();
		} else
			throw new ServiceException("Unknow payment type: [" + paymentType + "]");

		String status = properties.getProperty("Status");
		checkSuccessfullReply(userId, properties, vendorTxCode, amount, subweeks, status, TxType.AUTHORISE.getCode(), paymentType, httpStatusCode);
		LOGGER.info("sage pay release payment for user [{}] succeeded", userId);

		payment.setExternalTxCode(NOT_AVAILABLE);
		payment.setExternalSecurityKey(NOT_AVAILABLE);
		payment.setExternalAuthCode(NOT_AVAILABLE);
		payment.setInternalTxCode(vendorTxCode);

		if (status != null)
			payment.setStatus(status);
		else
			payment.setStatus(PaymentStatusDao.getNULL().getName());

		payment.setStatusDetail(properties.getProperty("StatusDetail"));
		payment.setTimestamp(getEpochSeconds());
		payment.setUserUID(userId);
		payment.setDescription(description);
		payment.setRelatedPayment(deferredPaymentId);
		payment.setTxType(TxType.RELEASE.getCode());
		payment.setAmount(amount.floatValue());
		payment.setSubweeks(subweeks);
		return payment;
	}

	public Payment repeat(User user, Payment deferredPayment, BigDecimal amount, byte subweeks, String currency, String description, String vendorTxCode) {
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair(PARAMETER_OUT.VendorTxCode.toString(), vendorTxCode));

		nameValuePairs.add(new BasicNameValuePair(PARAMETER_OUT.TxType.toString(), TxType.REPEAT.toString()));

		nameValuePairs.add(new BasicNameValuePair(PARAMETER_OUT.Amount.toString(), amount.toString()));
		nameValuePairs.add(new BasicNameValuePair(PARAMETER_OUT.Currency.toString(), currency));
		nameValuePairs.add(new BasicNameValuePair(PARAMETER_OUT.Description.toString(), description));

		nameValuePairs.add(new BasicNameValuePair(PARAMETER_OUT.RelatedVPSTxId.toString(), deferredPayment.getExternalTxCode()));
		nameValuePairs.add(new BasicNameValuePair(PARAMETER_OUT.RelatedVendorTxCode.toString(), deferredPayment.getInternalTxCode()));
		nameValuePairs.add(new BasicNameValuePair(PARAMETER_OUT.RelatedSecurityKey.toString(), deferredPayment.getExternalSecurityKey()));
		nameValuePairs.add(new BasicNameValuePair(PARAMETER_OUT.RelatedTxAuthNo.toString(), deferredPayment.getExternalAuthCode()));

		Response reply = processTransaction(urlRepeat, nameValuePairs, TxType.REPEAT.getCode());
		LOGGER.info("Sage pay system response is [{}]", reply.toString());

		if (reply.getStatusCode() != HttpServletResponse.SC_OK) {
			user.setPaymentEnabled(false);
			userService.updateUser(user);
			throw new ServiceException("Sage pay error [" + reply.toString() + "]");
		}

		Properties properties = new Properties();
		try {
			properties.load(new StringReader(reply.getMessage()));
		} catch (IOException e) {
			LOGGER.error("can't parse sage pay reply");
			user.setPaymentEnabled(false);
			userService.updateUser(user);
			throw new ServiceException("can't parse sage pay reply", e);
		}
		return processRepeatReply(user.getId(), properties, vendorTxCode, amount, subweeks, deferredPayment.getI(), description, user.getPaymentType(),
				reply.getStatusCode());
	}

	private Payment processRepeatReply(int userId, Properties properties, String vendorTxCode, BigDecimal amount, byte subweeks, long deferredPaymentId,
			String description, String paymentType, int httpStatusCode) {
		Payment payment;
		if (paymentType.equals(PaymentType.CREDIT_CARD)) {
			payment = new CreditCardPayment();
		} else if (paymentType.equals(PaymentType.PREMIUM_USER)) {
			payment = new PremiumUserPayment();
		} else
			throw new ServiceException("Unknow payment type: [" + paymentType + "]");

		String status = properties.getProperty("Status");
		checkSuccessfullReply(userId, properties, vendorTxCode, amount, subweeks, status, TxType.REPEAT.getCode(), paymentType, httpStatusCode);
		LOGGER.info("sage pay repeat payment for user [{}] succeeded", userId);

		payment.setExternalTxCode(properties.getProperty("VPSTxId"));
		payment.setExternalSecurityKey(properties.getProperty("SecurityKey"));
		payment.setInternalTxCode(vendorTxCode);
		payment.setExternalAuthCode(properties.getProperty("TxAuthNo"));
		payment.setStatus(status);
		payment.setStatusDetail(properties.getProperty("StatusDetail"));
		payment.setTimestamp(getEpochSeconds());
		payment.setUserUID(userId);
		payment.setDescription(description);
		payment.setRelatedPayment(deferredPaymentId);
		payment.setTxType(TxType.REPEAT.getCode());
		payment.setAmount(amount.floatValue());
		payment.setSubweeks(subweeks);
		return payment;
	}

	private void checkSuccessfullReply(int userId, Properties properties, String vendorTxCode, BigDecimal amount, byte subweeks, String status, int txTypeCode,
			String paymentType, int httpStatusCode) {
		if (httpStatusCode != HttpStatus.OK.value() || !STATUS_OK.equals(status)) {
			String description;
			if (httpStatusCode != HttpStatus.OK.value()) {
				description = "Sage pay returned http status code: [" + httpStatusCode + "]";
			} else
				description = properties.getProperty("StatusDetail");

			Payment failedPayment = getFailedPayment(userId, status, description, txTypeCode, vendorTxCode, amount, subweeks, paymentType);

			SagePayException sagePayException = new SagePayException(PAYMENT_SYSTEM_ERROR_MESSAGE + properties.getProperty("StatusDetail"), failedPayment);
			sagePayException.setHttpStatusCode(httpStatusCode);
			throw sagePayException;
		}
	}

	private Payment getFailedPayment(int userId, String status, String statusDetail, int txType, String vendorTxCode, BigDecimal amount, byte subweeks,
			String paymentType) {
		Payment payment;
		if (paymentType.equals(PaymentType.CREDIT_CARD)) {
			payment = new CreditCardPayment();
		} else if (paymentType.equals(PaymentType.PREMIUM_USER)) {
			payment = new PremiumUserPayment();
		} else
			throw new ServiceException("Unknow payment type: [" + paymentType + "]");

		payment.setExternalTxCode(NOT_AVAILABLE);
		payment.setExternalSecurityKey(NOT_AVAILABLE);
		payment.setInternalTxCode(vendorTxCode);
		payment.setExternalAuthCode(NOT_AVAILABLE);

		if (status == null)
			status = AppConstants.STATUS_FAIL;
		payment.setStatus(status);

		if (statusDetail == null)
			statusDetail = "";
		payment.setStatusDetail(statusDetail);
		payment.setTimestamp(getEpochSeconds());
		payment.setTxType(TxType.PAYMENT.getCode());
		payment.setUserUID(userId);
		payment.setDescription(NOT_AVAILABLE);
		payment.setRelatedPayment(0L);
		payment.setTxType(TxType.REPEAT.getCode());
		payment.setAmount(amount.floatValue());
		payment.setSubweeks(subweeks);
		return payment;
	}

	private Response processTransaction(String url, List<NameValuePair> nameValuePairs, int txType) {
		nameValuePairs.add(new BasicNameValuePair(PARAMETER_OUT.VPSProtocol.toString(), VSPPROTOCOL));
		nameValuePairs.add(new BasicNameValuePair(PARAMETER_OUT.Vendor.toString(), VENDOR));
		if (!(TxType.DEFERRED.getCode() == txType))
			LOGGER.info("sage pay params [{}]", nameValuePairs);
		return POST_SERVICE.sendHttpPost(url, nameValuePairs, null);
	}

}
