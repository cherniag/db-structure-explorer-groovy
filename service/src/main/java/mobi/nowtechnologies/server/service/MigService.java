package mobi.nowtechnologies.server.service;

import mobi.nowtechnologies.server.persistence.domain.Operator;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.service.exception.ServiceException;
import mobi.nowtechnologies.server.shared.service.PostService;
import mobi.nowtechnologies.server.shared.service.PostService.Response;
import mobi.nowtechnologies.server.shared.util.URLValidation;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;

import java.util.ArrayList;
import java.util.List;

import static mobi.nowtechnologies.server.shared.AppConstants.OADC_FREE;
import static mobi.nowtechnologies.server.shared.Utils.getBigRandomInt;

/**
 * MigService
 * 
 * @author Maksym Chernolevskyi (maksym)
 * 
 */
@Deprecated
public class MigService {
	public static final String _0 = "0";

	public static final String _0044 = "0044";

	private static final Logger LOGGER = LoggerFactory.getLogger(MigService.class);

	private String migOtaUrl;
	private String urlFreeSms;
	private String urlPremiumSms;
	private int timeToLive;

	private enum PARAMETER_OUT {
		OADC, OADCTYPE, NUMBERS, BODY, MESSAGEID, TIMETOLIVE;
	}

	private static final String OADCTYPE_PREMIUM = _0;
	private static final String OADCTYPE_FREE = "2";

	private PostService postService;

	private MessageSource messageSource;

	public void init() throws Exception {
		if (migOtaUrl == null)
			throw new Exception("The parameter mig.otaUrl is null");
		if (urlFreeSms == null)
			throw new Exception("The parameter mig.freeSMSURL is null");
		if (urlPremiumSms == null)
			throw new Exception("The parameter mig.premiumSMSUR is null");
		if (!URLValidation.validate(migOtaUrl))
			throw new Exception("The parameter mig.otaUrl is not valid url");
		if (!URLValidation.validate(urlFreeSms))
			throw new Exception("The parameter mig.freeSMSURL is not valid url");
		if (!URLValidation.validate(urlPremiumSms))
			throw new Exception("The parameter urlPremiumSms is not valid url");
	}

	public void setPostService(PostService postService) {
		this.postService = postService;
	}

	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}

	public String getUrlFreeSms() {
		return urlFreeSms;
	}

	public void setUrlFreeSms(String urlFreeSms) {
		this.urlFreeSms = urlFreeSms;
	}

	public String getUrlPremiumSms() {
		return urlPremiumSms;
	}

	public void setUrlPremiumSms(String urlPremiumSms) {
		this.urlPremiumSms = urlPremiumSms;
	}

	public int getTimeToLive() {
		return timeToLive;
	}

	public void setTimeToLive(int timeToLive) {
		this.timeToLive = timeToLive;
	}

	public String getMigOtaUrl() {
		return migOtaUrl;
	}

	public void setMigOtaUrl(String migOtaUrl) {
		this.migOtaUrl = migOtaUrl;
	}

	public Response sendFreeSms(String messageId, int operator, String mobile, String message) {
		String mobileNumber = convertPhoneNumberFromGreatBritainToInternationalFormat(mobile);

		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair(PARAMETER_OUT.OADC.toString(), OADC_FREE));
		nameValuePairs.add(new BasicNameValuePair(PARAMETER_OUT.OADCTYPE.toString(), OADCTYPE_FREE));
		nameValuePairs.add(new BasicNameValuePair(PARAMETER_OUT.MESSAGEID.toString(), messageId));
		nameValuePairs.add(new BasicNameValuePair(PARAMETER_OUT.NUMBERS.toString(), getNumbers(operator, mobileNumber)));
		nameValuePairs.add(new BasicNameValuePair(PARAMETER_OUT.BODY.toString(), message));
		Response reply = processTransaction(urlFreeSms, nameValuePairs);
		LOGGER.info("MIG response is {}", reply.toString());
		return reply;
	}

	private static String convertPhoneNumberFromGreatBritainToInternationalFormat(String mobile) {
		if (mobile == null)
			throw new ServiceException("The parameter mobile is null");
		return mobile.replaceFirst(_0, _0044);
	}

	public static String convertPhoneNumberFromInternationalToGreatBritainFormat(String mobile) {
		if (mobile == null)
			throw new ServiceException("The parameter mobile is null");
		return mobile.replaceFirst(_0044, _0);
	}

	public Response sendPremiumSms(String messageId, int operator, String phoneNumber, String message, String shortCode) {
		if (shortCode == null)
			throw new NullPointerException("The parameter shortCode is null");
		String mobile = convertPhoneNumberFromGreatBritainToInternationalFormat(phoneNumber);

		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair(PARAMETER_OUT.OADC.toString(), shortCode));
		nameValuePairs.add(new BasicNameValuePair(PARAMETER_OUT.OADCTYPE.toString(), OADCTYPE_PREMIUM));
		nameValuePairs.add(new BasicNameValuePair(PARAMETER_OUT.MESSAGEID.toString(), messageId));
		nameValuePairs.add(new BasicNameValuePair(PARAMETER_OUT.NUMBERS.toString(), getNumbers(operator, mobile)));
		nameValuePairs.add(new BasicNameValuePair(PARAMETER_OUT.BODY.toString(), message));
		nameValuePairs.add(new BasicNameValuePair(PARAMETER_OUT.TIMETOLIVE.toString(), String.valueOf(timeToLive)));
		Response reply = processTransaction(urlPremiumSms, nameValuePairs);
		LOGGER.info("MIG response is [{}]", reply.toString());
		return reply;
	}

	private String getNumbers(int operator, String mobile) {
		return Operator.getMapAsIds().get(operator).getMigName() + "." + mobile;
	}

	private Response processTransaction(String url, List<NameValuePair> nameValuePairs) {
		LOGGER.info("mig params [{}]", nameValuePairs);
		return postService.sendHttpPost(url, nameValuePairs, null);
	}

	public void sendSMSWithOTALink(User user) {
		if (user == null)
			throw new ServiceException("The parameter user is null");
		String[] args = { migOtaUrl + "&CODE=" + user.getCode() };
		sendFreeSms("" + getBigRandomInt(), user.getOperator(), user.getMobile(), messageSource.getMessage("sms.otalink.text", args, null));
	}
}
