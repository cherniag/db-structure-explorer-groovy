package mobi.nowtechnologies.server.service.impl;

import mobi.nowtechnologies.server.persistence.dao.UserStatusDao;
import mobi.nowtechnologies.server.persistence.domain.*;
import mobi.nowtechnologies.server.persistence.domain.payment.*;
import mobi.nowtechnologies.server.service.PaymentPolicyService;
import mobi.nowtechnologies.server.service.UserService;
import mobi.nowtechnologies.server.service.event.PaymentEvent;
import mobi.nowtechnologies.server.service.payment.SubmitedPaymentService;
import mobi.nowtechnologies.server.shared.Utils;
import mobi.nowtechnologies.server.shared.enums.PaymentDetailsStatus;
import mobi.nowtechnologies.server.shared.message.CommunityResourceBundleMessageSource;
import mobi.nowtechnologies.server.shared.service.BasicResponse;
import mobi.nowtechnologies.server.shared.service.PostService;
import org.apache.http.NameValuePair;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.context.ApplicationEventPublisher;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.Assert.*;

/**
 * @author Titov Mykhaylo (titov)
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({SubmittedPayment.class, Utils.class, PaymentEvent.class, UserStatusDao.class})
public class ITunesServiceImplTest {

    private CommunityResourceBundleMessageSource communityResourceBundleMessageSourceMock;

    private final class PaymentEventAnswer implements Answer<Void> {
		private final long expiresDate;
		private final long paymentTimestamp;
		private final User user;
		private final String base64EncodedAppStoreReceipt;
		private final String appStoreOriginalTransactionId;
		private final String currencyISO;
		private final String originalTransactionId;
		private final PaymentDetailsType paymentType;
		private final BigDecimal paymentPolicySubCost;

		private PaymentEventAnswer(long expiresDate, long paymentTimestamp, User user, String base64EncodedAppStoreReceipt, String appStoreOriginalTransactionId, String currencyISO,
				String originalTransactionId, PaymentDetailsType paymentType, BigDecimal paymentPolicySubCost) {
			this.expiresDate = expiresDate;
			this.paymentTimestamp = paymentTimestamp;
			this.user = user;
			this.base64EncodedAppStoreReceipt = base64EncodedAppStoreReceipt;
			this.appStoreOriginalTransactionId = appStoreOriginalTransactionId;
			this.currencyISO = currencyISO;
			this.originalTransactionId = originalTransactionId;
			this.paymentType = paymentType;
			this.paymentPolicySubCost = paymentPolicySubCost;
		}

		@Override
		public Void answer(InvocationOnMock invocation) throws Throwable {
			PaymentEvent passedPaymentEvent = (PaymentEvent)invocation.getArguments()[0]; 
			
			SubmittedPayment passedSubmittedPayment = (SubmittedPayment) passedPaymentEvent.getPayment();
			
			validatePayment(base64EncodedAppStoreReceipt, originalTransactionId, expiresDate, appStoreOriginalTransactionId, user, paymentPolicySubCost, currencyISO, paymentTimestamp, paymentType, passedSubmittedPayment);
			
			return null;
		}
	}

	private final class SubmitedPaymentServiceAnswer implements Answer<SubmittedPayment> {
		private final String base64EncodedAppStoreReceipt;
		private final long paymentTimestamp;
		private final String currencyISO;
		private final String appStoreOriginalTransactionId;
		private final PaymentDetailsType paymentType;
		private final BigDecimal paymentPolicySubCost;
		private final User user;
		private final String originalTransactionId;
		private final long expiresDate;

		private SubmitedPaymentServiceAnswer(String base64EncodedAppStoreReceipt, long paymentTimestamp, String currencyISO, String appStoreOriginalTransactionId, PaymentDetailsType paymentType,
				BigDecimal paymentPolicySubCost, User user, String originalTransactionId, long expiresDate) {
			this.base64EncodedAppStoreReceipt = base64EncodedAppStoreReceipt;
			this.paymentTimestamp = paymentTimestamp;
			this.currencyISO = currencyISO;
			this.appStoreOriginalTransactionId = appStoreOriginalTransactionId;
			this.paymentType = paymentType;
			this.paymentPolicySubCost = paymentPolicySubCost;
			this.user = user;
			this.originalTransactionId = originalTransactionId;
			this.expiresDate = expiresDate;
		}

		@Override
		public SubmittedPayment answer(InvocationOnMock invocation) throws Throwable {
			SubmittedPayment passedSubmittedPayment = (SubmittedPayment)invocation.getArguments()[0]; 
			
			validatePayment(base64EncodedAppStoreReceipt, originalTransactionId, expiresDate, appStoreOriginalTransactionId, user, paymentPolicySubCost, currencyISO, paymentTimestamp, paymentType, passedSubmittedPayment);
			
			return passedSubmittedPayment;
		}
	}

	private ITunesServiceImpl fixtureITunesServiceImpl;
	private UserService mockUserService;
	private ApplicationEventPublisher mockApplicationEventPublisher;
	private PaymentPolicyService mockPaymentPolicyService;
	private PostService mockPostService;
	private SubmitedPaymentService mockSubmitedPaymentService;
	
	final String iTunesUrl = "https://sandbox.itunes.apple.com/verifyReceipt";
	final String password = "564e6871a69b424eb3197750d3a60bf7";

	@Test
	public void testITunesServiceImpl_Success()
		throws Exception {
		ITunesServiceImpl result = new ITunesServiceImpl();
		assertNotNull(result);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testProcessInAppSubscription_WithoutPaymentDetails_Success()
		throws Exception {
		final int userId = 1;
		final String base64EncodedAppStoreReceipt = "ewoJInNpZ25hdHVyZSIgPSAiQW5nNDZNTjRLdjNiV2QrZkNTeE8xS1ZRa3p3RnpNNmh4S3FqMENKZ2xrdlBRMkRRMUpoTE5DRzhSbnZNRlFvMXVBU2RrQjAzemtob29wRnlqYm0zVXduSHBjSmE3RGJ1aVlpa0hSNFhud1V0cnBjOHZIa2JORkN4b3NyYXQ1cEZQL3RYaERpRHZHdHdESW12aWZFVDhPYkRsU1VaSnNNeFYyTkR3ajJ1TEJhZ0FBQURWekNDQTFNd2dnSTdvQU1DQVFJQ0NHVVVrVTNaV0FTMU1BMEdDU3FHU0liM0RRRUJCUVVBTUg4eEN6QUpCZ05WQkFZVEFsVlRNUk13RVFZRFZRUUtEQXBCY0hCc1pTQkpibU11TVNZd0pBWURWUVFMREIxQmNIQnNaU0JEWlhKMGFXWnBZMkYwYVc5dUlFRjFkR2h2Y21sMGVURXpNREVHQTFVRUF3d3FRWEJ3YkdVZ2FWUjFibVZ6SUZOMGIzSmxJRU5sY25ScFptbGpZWFJwYjI0Z1FYVjBhRzl5YVhSNU1CNFhEVEE1TURZeE5USXlNRFUxTmxvWERURTBNRFl4TkRJeU1EVTFObG93WkRFak1DRUdBMVVFQXd3YVVIVnlZMmhoYzJWU1pXTmxhWEIwUTJWeWRHbG1hV05oZEdVeEd6QVpCZ05WQkFzTUVrRndjR3hsSUdsVWRXNWxjeUJUZEc5eVpURVRNQkVHQTFVRUNnd0tRWEJ3YkdVZ1NXNWpMakVMTUFrR0ExVUVCaE1DVlZNd2daOHdEUVlKS29aSWh2Y05BUUVCQlFBRGdZMEFNSUdKQW9HQkFNclJqRjJjdDRJclNkaVRDaGFJMGc4cHd2L2NtSHM4cC9Sd1YvcnQvOTFYS1ZoTmw0WElCaW1LalFRTmZnSHNEczZ5anUrK0RyS0pFN3VLc3BoTWRkS1lmRkU1ckdYc0FkQkVqQndSSXhleFRldngzSExFRkdBdDFtb0t4NTA5ZGh4dGlJZERnSnYyWWFWczQ5QjB1SnZOZHk2U01xTk5MSHNETHpEUzlvWkhBZ01CQUFHamNqQndNQXdHQTFVZEV3RUIvd1FDTUFBd0h3WURWUjBqQkJnd0ZvQVVOaDNvNHAyQzBnRVl0VEpyRHRkREM1RllRem93RGdZRFZSMFBBUUgvQkFRREFnZUFNQjBHQTFVZERnUVdCQlNwZzRQeUdVakZQaEpYQ0JUTXphTittVjhrOVRBUUJnb3Foa2lHOTJOa0JnVUJCQUlGQURBTkJna3Foa2lHOXcwQkFRVUZBQU9DQVFFQUVhU2JQanRtTjRDL0lCM1FFcEszMlJ4YWNDRFhkVlhBZVZSZVM1RmFaeGMrdDg4cFFQOTNCaUF4dmRXLzNlVFNNR1k1RmJlQVlMM2V0cVA1Z204d3JGb2pYMGlreVZSU3RRKy9BUTBLRWp0cUIwN2tMczlRVWU4Y3pSOFVHZmRNMUV1bVYvVWd2RGQ0TndOWXhMUU1nNFdUUWZna1FRVnk4R1had1ZIZ2JFL1VDNlk3MDUzcEdYQms1MU5QTTN3b3hoZDNnU1JMdlhqK2xvSHNTdGNURXFlOXBCRHBtRzUrc2s0dHcrR0szR01lRU41LytlMVFUOW5wL0tsMW5qK2FCdzdDMHhzeTBiRm5hQWQxY1NTNnhkb3J5L0NVdk02Z3RLc21uT09kcVRlc2JwMGJzOHNuNldxczBDOWRnY3hSSHVPTVoydG04bnBMVW03YXJnT1N6UT09IjsKCSJwdXJjaGFzZS1pbmZvIiA9ICJld29KSW05eWFXZHBibUZzTFhCMWNtTm9ZWE5sTFdSaGRHVXRjSE4wSWlBOUlDSXlNREV6TFRBeUxURXlJREE0T2pBM09qVTRJRUZ0WlhKcFkyRXZURzl6WDBGdVoyVnNaWE1pT3dvSkluQjFjbU5vWVhObExXUmhkR1V0YlhNaUlEMGdJakV6TmpBMk9EVXlOemd5T0RVaU93b0pJblZ1YVhGMVpTMXBaR1Z1ZEdsbWFXVnlJaUE5SUNJeVl6UmtNbVprWXpJNU1HWm1NR00zWlRrMk9ESXlOREJsTkdZMU1tUXdPVEV6TldNME1qVTRJanNLQ1NKdmNtbG5hVzVoYkMxMGNtRnVjMkZqZEdsdmJpMXBaQ0lnUFNBaU1UQXdNREF3TURBMk5EYzBOek01TWlJN0Nna2laWGh3YVhKbGN5MWtZWFJsSWlBOUlDSXhNell3TmpnMU5EVTRNamcxSWpzS0NTSjBjbUZ1YzJGamRHbHZiaTFwWkNJZ1BTQWlNVEF3TURBd01EQTJORGMwTnpNNU1pSTdDZ2tpYjNKcFoybHVZV3d0Y0hWeVkyaGhjMlV0WkdGMFpTMXRjeUlnUFNBaU1UTTJNRFk0TlRJM09EWTJPU0k3Q2draWQyVmlMVzl5WkdWeUxXeHBibVV0YVhSbGJTMXBaQ0lnUFNBaU1UQXdNREF3TURBeU5qWXpORGd4T0NJN0Nna2lZblp5Y3lJZ1BTQWlNUzR3SWpzS0NTSjFibWx4ZFdVdGRtVnVaRzl5TFdsa1pXNTBhV1pwWlhJaUlEMGdJa0kzUkRWQlFqUTVMVVpHUkRjdE5ERTBSUzA1TURZNExVSTFNalEzUWtJd05VUXdOQ0k3Q2draVpYaHdhWEpsY3kxa1lYUmxMV1p2Y20xaGRIUmxaQzF3YzNRaUlEMGdJakl3TVRNdE1ESXRNVElnTURnNk1UQTZOVGdnUVcxbGNtbGpZUzlNYjNOZlFXNW5aV3hsY3lJN0Nna2lhWFJsYlMxcFpDSWdQU0FpTmpBeU5qWTFPVEUwSWpzS0NTSmxlSEJwY21WekxXUmhkR1V0Wm05eWJXRjBkR1ZrSWlBOUlDSXlNREV6TFRBeUxURXlJREUyT2pFd09qVTRJRVYwWXk5SFRWUWlPd29KSW5CeWIyUjFZM1F0YVdRaUlEMGdJbU52YlM1dGRYTnBZM0YxWW1Wa0xtOHlJanNLQ1NKd2RYSmphR0Z6WlMxa1lYUmxJaUE5SUNJeU1ERXpMVEF5TFRFeUlERTJPakEzT2pVNElFVjBZeTlIVFZRaU93b0pJbTl5YVdkcGJtRnNMWEIxY21Ob1lYTmxMV1JoZEdVaUlEMGdJakl3TVRNdE1ESXRNVElnTVRZNk1EYzZOVGdnUlhSakwwZE5WQ0k3Q2draVltbGtJaUE5SUNKamIyMHViWFZ6YVdOeGRXSmxaQzV2TWlJN0Nna2ljSFZ5WTJoaGMyVXRaR0YwWlMxd2MzUWlJRDBnSWpJd01UTXRNREl0TVRJZ01EZzZNRGM2TlRnZ1FXMWxjbWxqWVM5TWIzTmZRVzVuWld4bGN5STdDZ2tpY1hWaGJuUnBkSGtpSUQwZ0lqRWlPd3A5IjsKCSJlbnZpcm9ubWVudCIgPSAiU2FuZGJveCI7CgkicG9kIiA9ICIxMDAiOwoJInNpZ25pbmctc3RhdHVzIiA9ICIwIjsKfQ==";
		final String transactionReceipt = base64EncodedAppStoreReceipt.replaceAll("=", "\\\\u003d");
		final String originalTransactionId = "1000000064861007";
		final long expiresDate = 1360756242000L;
		final String appStoreOriginalTransactionId = "1000000064861007";
		
		UserStatus limitedUserStatus = UserStatusFactory.createUserStatus(mobi.nowtechnologies.server.shared.enums.UserStatus.LIMITED);

		Community community = CommunityFactory.createCommunity();

		UserGroup userGroup = UserGroupFactory.createUserGroup();
		userGroup.setCommunity(community);
		
		final User user = UserFactory.createUser();
		user.setId(userId);
		user.setCurrentPaymentDetails(null);
		user.setBase64EncodedAppStoreReceipt(null);
		user.setStatus(limitedUserStatus);
		user.setUserGroup(userGroup);
		user.setLastSuccessfulPaymentTimeMillis(0L);
		
		String body = "{\"receipt-data\":\""+transactionReceipt+"\",\"password\":\""+password+"\"}";
		
		final String appStoreProductId = "com.musicqubed.o2.autorenew.test";

        BasicResponse expectedResponse = new BasicResponse();
		expectedResponse.setStatusCode(200);
		expectedResponse.setMessage("{ \"receipt\" : { \"original_purchase_date_pst\" : \"2013-02-13 03:41:43 America/Los_Angeles\", \"unique_identifier\" : \"80d70017aae1547196bc92c02c3f83cc5f9e4cc6\", \"original_transaction_id\" : \""+originalTransactionId+"\", \"expires_date\" : \""+expiresDate+"\", \"transaction_id\" : \""+appStoreOriginalTransactionId+"\", \"quantity\" : \"1\", \"product_id\" : \""+appStoreProductId+"\", \"original_purchase_date_ms\" : \"1360755703334\", \"bid\" : \"com.musicqubed.o2\", \"web_order_line_item_id\" : \"1000000026638439\", \"bvrs\" : \"1.0\", \"expires_date_formatted\" : \"2013-02-13 11:44:42 Etc/GMT\", \"purchase_date\" : \"2013-02-13 11:41:42 Etc/GMT\", \"purchase_date_ms\" : \"1360755702795\", \"expires_date_formatted_pst\" : \"2013-02-13 03:44:42 America/Los_Angeles\", \"purchase_date_pst\" : \"2013-02-13 03:41:42 America/Los_Angeles\", \"original_purchase_date\" : \"2013-02-13 11:41:43 Etc/GMT\", \"item_id\" : \"602725828\" }, \"latest_receipt_info\" : { \"original_purchase_date_pst\" : \"2013-02-13 03:41:43 America/Los_Angeles\", \"unique_identifier\" : \"80d70017aae1547196bc92c02c3f83cc5f9e4cc6\", \"original_transaction_id\" : \""+originalTransactionId+"\", \"expires_date\" : \""+expiresDate+"\", \"transaction_id\" : \""+appStoreOriginalTransactionId+"\", \"quantity\" : \"1\", \"product_id\" : \""+appStoreProductId+"\", \"original_purchase_date_ms\" : \"1360755703000\", \"bid\" : \"com.musicqubed.o2\", \"web_order_line_item_id\" : \"1000000026638446\", \"bvrs\" : \"1.0\", \"expires_date_formatted\" : \"2013-02-13 11:50:42 Etc/GMT\", \"purchase_date\" : \"2013-02-13 11:47:42 Etc/GMT\", \"purchase_date_ms\" : \"1360756062000\", \"expires_date_formatted_pst\" : \"2013-02-13 03:50:42 America/Los_Angeles\", \"purchase_date_pst\" : \"2013-02-13 03:47:42 America/Los_Angeles\", \"original_purchase_date\" : \"2013-02-13 11:41:43 Etc/GMT\", \"item_id\" : \"602725828\" }, \"status\" : 0, \"latest_receipt\" : \""+transactionReceipt+"\" }");

		final BigDecimal paymentPolicySubCost = BigDecimal.TEN;
		final String currencyISO = "GBP";
		
		final PaymentPolicy paymentPolicy = PaymentPolicyFactory.createPaymentPolicy();
		paymentPolicy.setSubcost(paymentPolicySubCost);
		paymentPolicy.setCurrencyISO(currencyISO);
		
		final long paymentTimestamp = Long.MAX_VALUE;
		final PaymentDetailsType paymentType = PaymentDetailsType.FIRST;

		Mockito.when(mockUserService.findById(userId)).thenReturn(user);
		Mockito.when(mockPostService.sendHttpPost(Mockito.eq(iTunesUrl), (List<NameValuePair>)Mockito.isNull(), Mockito.eq(body))).thenReturn(expectedResponse);
		Mockito.when(mockPaymentPolicyService.findByCommunityAndAppStoreProductId(community, appStoreProductId)).thenReturn(paymentPolicy); 

		PowerMockito.mockStatic(Utils.class);
		PowerMockito.when(Utils.getEpochMillis()).thenReturn(paymentTimestamp);
		
		Mockito.when(mockSubmitedPaymentService.save(Mockito.any(SubmittedPayment.class))).thenAnswer(new SubmitedPaymentServiceAnswer(base64EncodedAppStoreReceipt, paymentTimestamp, currencyISO, appStoreOriginalTransactionId, paymentType, paymentPolicySubCost, user, originalTransactionId,
				expiresDate));
		
		Mockito.doAnswer(new PaymentEventAnswer(expiresDate, paymentTimestamp, user, base64EncodedAppStoreReceipt, appStoreOriginalTransactionId, currencyISO, originalTransactionId, paymentType, paymentPolicySubCost)).when(mockApplicationEventPublisher).publishEvent(Mockito.any(PaymentEvent.class));

        BasicResponse actualResponse = fixtureITunesServiceImpl.processInAppSubscription(userId, base64EncodedAppStoreReceipt);
		
		assertNotNull(actualResponse);
		assertEquals(expectedResponse, actualResponse);
		
		Mockito.verify(mockUserService, Mockito.times(1)).findById(userId);
		Mockito.verify(mockPostService, Mockito.times(1)).sendHttpPost(Mockito.eq(iTunesUrl), (List<NameValuePair>)Mockito.isNull(), Mockito.eq(body));
		Mockito.verify(mockPaymentPolicyService, Mockito.times(1)).findByCommunityAndAppStoreProductId(community, appStoreProductId);
		Mockito.verify(mockSubmitedPaymentService, Mockito.times(1)).save(Mockito.any(SubmittedPayment.class));
		Mockito.verify(mockApplicationEventPublisher, Mockito.times(1)).publishEvent(Mockito.any(PaymentEvent.class));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testProcessInAppSubscription_WithoutPaymentDetailsInSubscribedStatusAndtTansactionReceiptAndBase64EncodedAppStoreReceiptAreNotNull_Success()
		throws Exception {
		final int userId = 1;
		final String base64EncodedAppStoreReceipt = "ewoJInNpZ25hdHVyZSIgPSAiQW5nNDZNTjRLdjNiV2QrZkNTeE8xS1ZRa3p3RnpNNmh4S3FqMENKZ2xrdlBRMkRRMUpoTE5DRzhSbnZNRlFvMXVBU2RrQjAzemtob29wRnlqYm0zVXduSHBjSmE3RGJ1aVlpa0hSNFhud1V0cnBjOHZIa2JORkN4b3NyYXQ1cEZQL3RYaERpRHZHdHdESW12aWZFVDhPYkRsU1VaSnNNeFYyTkR3ajJ1TEJhZ0FBQURWekNDQTFNd2dnSTdvQU1DQVFJQ0NHVVVrVTNaV0FTMU1BMEdDU3FHU0liM0RRRUJCUVVBTUg4eEN6QUpCZ05WQkFZVEFsVlRNUk13RVFZRFZRUUtEQXBCY0hCc1pTQkpibU11TVNZd0pBWURWUVFMREIxQmNIQnNaU0JEWlhKMGFXWnBZMkYwYVc5dUlFRjFkR2h2Y21sMGVURXpNREVHQTFVRUF3d3FRWEJ3YkdVZ2FWUjFibVZ6SUZOMGIzSmxJRU5sY25ScFptbGpZWFJwYjI0Z1FYVjBhRzl5YVhSNU1CNFhEVEE1TURZeE5USXlNRFUxTmxvWERURTBNRFl4TkRJeU1EVTFObG93WkRFak1DRUdBMVVFQXd3YVVIVnlZMmhoYzJWU1pXTmxhWEIwUTJWeWRHbG1hV05oZEdVeEd6QVpCZ05WQkFzTUVrRndjR3hsSUdsVWRXNWxjeUJUZEc5eVpURVRNQkVHQTFVRUNnd0tRWEJ3YkdVZ1NXNWpMakVMTUFrR0ExVUVCaE1DVlZNd2daOHdEUVlKS29aSWh2Y05BUUVCQlFBRGdZMEFNSUdKQW9HQkFNclJqRjJjdDRJclNkaVRDaGFJMGc4cHd2L2NtSHM4cC9Sd1YvcnQvOTFYS1ZoTmw0WElCaW1LalFRTmZnSHNEczZ5anUrK0RyS0pFN3VLc3BoTWRkS1lmRkU1ckdYc0FkQkVqQndSSXhleFRldngzSExFRkdBdDFtb0t4NTA5ZGh4dGlJZERnSnYyWWFWczQ5QjB1SnZOZHk2U01xTk5MSHNETHpEUzlvWkhBZ01CQUFHamNqQndNQXdHQTFVZEV3RUIvd1FDTUFBd0h3WURWUjBqQkJnd0ZvQVVOaDNvNHAyQzBnRVl0VEpyRHRkREM1RllRem93RGdZRFZSMFBBUUgvQkFRREFnZUFNQjBHQTFVZERnUVdCQlNwZzRQeUdVakZQaEpYQ0JUTXphTittVjhrOVRBUUJnb3Foa2lHOTJOa0JnVUJCQUlGQURBTkJna3Foa2lHOXcwQkFRVUZBQU9DQVFFQUVhU2JQanRtTjRDL0lCM1FFcEszMlJ4YWNDRFhkVlhBZVZSZVM1RmFaeGMrdDg4cFFQOTNCaUF4dmRXLzNlVFNNR1k1RmJlQVlMM2V0cVA1Z204d3JGb2pYMGlreVZSU3RRKy9BUTBLRWp0cUIwN2tMczlRVWU4Y3pSOFVHZmRNMUV1bVYvVWd2RGQ0TndOWXhMUU1nNFdUUWZna1FRVnk4R1had1ZIZ2JFL1VDNlk3MDUzcEdYQms1MU5QTTN3b3hoZDNnU1JMdlhqK2xvSHNTdGNURXFlOXBCRHBtRzUrc2s0dHcrR0szR01lRU41LytlMVFUOW5wL0tsMW5qK2FCdzdDMHhzeTBiRm5hQWQxY1NTNnhkb3J5L0NVdk02Z3RLc21uT09kcVRlc2JwMGJzOHNuNldxczBDOWRnY3hSSHVPTVoydG04bnBMVW03YXJnT1N6UT09IjsKCSJwdXJjaGFzZS1pbmZvIiA9ICJld29KSW05eWFXZHBibUZzTFhCMWNtTm9ZWE5sTFdSaGRHVXRjSE4wSWlBOUlDSXlNREV6TFRBeUxURXlJREE0T2pBM09qVTRJRUZ0WlhKcFkyRXZURzl6WDBGdVoyVnNaWE1pT3dvSkluQjFjbU5vWVhObExXUmhkR1V0YlhNaUlEMGdJakV6TmpBMk9EVXlOemd5T0RVaU93b0pJblZ1YVhGMVpTMXBaR1Z1ZEdsbWFXVnlJaUE5SUNJeVl6UmtNbVprWXpJNU1HWm1NR00zWlRrMk9ESXlOREJsTkdZMU1tUXdPVEV6TldNME1qVTRJanNLQ1NKdmNtbG5hVzVoYkMxMGNtRnVjMkZqZEdsdmJpMXBaQ0lnUFNBaU1UQXdNREF3TURBMk5EYzBOek01TWlJN0Nna2laWGh3YVhKbGN5MWtZWFJsSWlBOUlDSXhNell3TmpnMU5EVTRNamcxSWpzS0NTSjBjbUZ1YzJGamRHbHZiaTFwWkNJZ1BTQWlNVEF3TURBd01EQTJORGMwTnpNNU1pSTdDZ2tpYjNKcFoybHVZV3d0Y0hWeVkyaGhjMlV0WkdGMFpTMXRjeUlnUFNBaU1UTTJNRFk0TlRJM09EWTJPU0k3Q2draWQyVmlMVzl5WkdWeUxXeHBibVV0YVhSbGJTMXBaQ0lnUFNBaU1UQXdNREF3TURBeU5qWXpORGd4T0NJN0Nna2lZblp5Y3lJZ1BTQWlNUzR3SWpzS0NTSjFibWx4ZFdVdGRtVnVaRzl5TFdsa1pXNTBhV1pwWlhJaUlEMGdJa0kzUkRWQlFqUTVMVVpHUkRjdE5ERTBSUzA1TURZNExVSTFNalEzUWtJd05VUXdOQ0k3Q2draVpYaHdhWEpsY3kxa1lYUmxMV1p2Y20xaGRIUmxaQzF3YzNRaUlEMGdJakl3TVRNdE1ESXRNVElnTURnNk1UQTZOVGdnUVcxbGNtbGpZUzlNYjNOZlFXNW5aV3hsY3lJN0Nna2lhWFJsYlMxcFpDSWdQU0FpTmpBeU5qWTFPVEUwSWpzS0NTSmxlSEJwY21WekxXUmhkR1V0Wm05eWJXRjBkR1ZrSWlBOUlDSXlNREV6TFRBeUxURXlJREUyT2pFd09qVTRJRVYwWXk5SFRWUWlPd29KSW5CeWIyUjFZM1F0YVdRaUlEMGdJbU52YlM1dGRYTnBZM0YxWW1Wa0xtOHlJanNLQ1NKd2RYSmphR0Z6WlMxa1lYUmxJaUE5SUNJeU1ERXpMVEF5TFRFeUlERTJPakEzT2pVNElFVjBZeTlIVFZRaU93b0pJbTl5YVdkcGJtRnNMWEIxY21Ob1lYTmxMV1JoZEdVaUlEMGdJakl3TVRNdE1ESXRNVElnTVRZNk1EYzZOVGdnUlhSakwwZE5WQ0k3Q2draVltbGtJaUE5SUNKamIyMHViWFZ6YVdOeGRXSmxaQzV2TWlJN0Nna2ljSFZ5WTJoaGMyVXRaR0YwWlMxd2MzUWlJRDBnSWpJd01UTXRNREl0TVRJZ01EZzZNRGM2TlRnZ1FXMWxjbWxqWVM5TWIzTmZRVzVuWld4bGN5STdDZ2tpY1hWaGJuUnBkSGtpSUQwZ0lqRWlPd3A5IjsKCSJlbnZpcm9ubWVudCIgPSAiU2FuZGJveCI7CgkicG9kIiA9ICIxMDAiOwoJInNpZ25pbmctc3RhdHVzIiA9ICIwIjsKfQ==";
		final String transactionReceipt = base64EncodedAppStoreReceipt.replaceAll("=", "\\\\u003d");
		final String originalTransactionId = "1000000064861007";
		final long expiresDate = 1360756242000L;
		final String appStoreOriginalTransactionId = "1000000064861007";
		
		UserStatus subscribedUserStatus = UserStatusFactory.createUserStatus(mobi.nowtechnologies.server.shared.enums.UserStatus.SUBSCRIBED);
		UserStatus limitedUserStatus = UserStatusFactory.createUserStatus(mobi.nowtechnologies.server.shared.enums.UserStatus.LIMITED);

		Community community = CommunityFactory.createCommunity();

		UserGroup userGroup = UserGroupFactory.createUserGroup().withId(1);
		userGroup.setCommunity(community);
		
		final User user = UserFactory.createUser();
		user.setId(userId);
		user.setCurrentPaymentDetails(null);
		user.setBase64EncodedAppStoreReceipt("g"+transactionReceipt);
		user.setStatus(subscribedUserStatus);
		user.setUserGroup(userGroup);
		user.setLastSuccessfulPaymentTimeMillis(0L);
		
		String body = "{\"receipt-data\":\""+transactionReceipt+"\",\"password\":\""+password+"\"}";
		
		final String appStoreProductId = "com.musicqubed.o2.autorenew.test";

        BasicResponse expectedResponse = new BasicResponse();
		expectedResponse.setStatusCode(200);
		expectedResponse.setMessage("{ \"receipt\" : { \"original_purchase_date_pst\" : \"2013-02-13 03:41:43 America/Los_Angeles\", \"unique_identifier\" : \"80d70017aae1547196bc92c02c3f83cc5f9e4cc6\", \"original_transaction_id\" : \""+originalTransactionId+"\", \"expires_date\" : \""+expiresDate+"\", \"transaction_id\" : \""+appStoreOriginalTransactionId+"\", \"quantity\" : \"1\", \"product_id\" : \""+appStoreProductId+"\", \"original_purchase_date_ms\" : \"1360755703334\", \"bid\" : \"com.musicqubed.o2\", \"web_order_line_item_id\" : \"1000000026638439\", \"bvrs\" : \"1.0\", \"expires_date_formatted\" : \"2013-02-13 11:44:42 Etc/GMT\", \"purchase_date\" : \"2013-02-13 11:41:42 Etc/GMT\", \"purchase_date_ms\" : \"1360755702795\", \"expires_date_formatted_pst\" : \"2013-02-13 03:44:42 America/Los_Angeles\", \"purchase_date_pst\" : \"2013-02-13 03:41:42 America/Los_Angeles\", \"original_purchase_date\" : \"2013-02-13 11:41:43 Etc/GMT\", \"item_id\" : \"602725828\" }, \"latest_receipt_info\" : { \"original_purchase_date_pst\" : \"2013-02-13 03:41:43 America/Los_Angeles\", \"unique_identifier\" : \"80d70017aae1547196bc92c02c3f83cc5f9e4cc6\", \"original_transaction_id\" : \""+originalTransactionId+"\", \"expires_date\" : \""+expiresDate+"\", \"transaction_id\" : \""+appStoreOriginalTransactionId+"\", \"quantity\" : \"1\", \"product_id\" : \""+appStoreProductId+"\", \"original_purchase_date_ms\" : \"1360755703000\", \"bid\" : \"com.musicqubed.o2\", \"web_order_line_item_id\" : \"1000000026638446\", \"bvrs\" : \"1.0\", \"expires_date_formatted\" : \"2013-02-13 11:50:42 Etc/GMT\", \"purchase_date\" : \"2013-02-13 11:47:42 Etc/GMT\", \"purchase_date_ms\" : \"1360756062000\", \"expires_date_formatted_pst\" : \"2013-02-13 03:50:42 America/Los_Angeles\", \"purchase_date_pst\" : \"2013-02-13 03:47:42 America/Los_Angeles\", \"original_purchase_date\" : \"2013-02-13 11:41:43 Etc/GMT\", \"item_id\" : \"602725828\" }, \"status\" : 0, \"latest_receipt\" : \""+transactionReceipt+"\" }");

		final BigDecimal paymentPolicySubCost = BigDecimal.TEN;
		final String currencyISO = "GBP";
		
		final PaymentPolicy paymentPolicy = PaymentPolicyFactory.createPaymentPolicy();
		paymentPolicy.setSubcost(paymentPolicySubCost);
		paymentPolicy.setCurrencyISO(currencyISO);
		
		final long paymentTimestamp = Long.MAX_VALUE;
		final PaymentDetailsType paymentType = PaymentDetailsType.FIRST;
		
		PowerMockito.mockStatic(UserStatusDao.class);
		PowerMockito.when(UserStatusDao.getLimitedUserStatus()).thenReturn(limitedUserStatus);

		Mockito.when(mockUserService.findById(userId)).thenReturn(user);
		Mockito.when(mockPostService.sendHttpPost(Mockito.eq(iTunesUrl), (List<NameValuePair>)Mockito.isNull(), Mockito.eq(body))).thenReturn(expectedResponse);
		Mockito.when(mockPaymentPolicyService.findByCommunityAndAppStoreProductId(community, appStoreProductId)).thenReturn(paymentPolicy); 

		PowerMockito.mockStatic(Utils.class);
		PowerMockito.when(Utils.getEpochMillis()).thenReturn(paymentTimestamp);
		
		Mockito.when(mockSubmitedPaymentService.save(Mockito.any(SubmittedPayment.class))).thenAnswer(new SubmitedPaymentServiceAnswer(base64EncodedAppStoreReceipt, paymentTimestamp, currencyISO, appStoreOriginalTransactionId, paymentType, paymentPolicySubCost, user, originalTransactionId,
				expiresDate));
		
		Mockito.doAnswer(new PaymentEventAnswer(expiresDate, paymentTimestamp, user, base64EncodedAppStoreReceipt, appStoreOriginalTransactionId, currencyISO, originalTransactionId, paymentType, paymentPolicySubCost)).when(mockApplicationEventPublisher).publishEvent(Mockito.any(PaymentEvent.class));

        BasicResponse actualResponse = fixtureITunesServiceImpl.processInAppSubscription(userId, base64EncodedAppStoreReceipt);
		
		assertNotNull(actualResponse);
		assertEquals(expectedResponse, actualResponse);
		
		Mockito.verify(mockUserService, Mockito.times(1)).findById(userId);
		Mockito.verify(mockPostService, Mockito.times(1)).sendHttpPost(Mockito.eq(iTunesUrl), (List<NameValuePair>)Mockito.isNull(), Mockito.eq(body));
		Mockito.verify(mockPaymentPolicyService, Mockito.times(1)).findByCommunityAndAppStoreProductId(community, appStoreProductId);
		Mockito.verify(mockSubmitedPaymentService, Mockito.times(1)).save(Mockito.any(SubmittedPayment.class));
		Mockito.verify(mockApplicationEventPublisher, Mockito.times(1)).publishEvent(Mockito.any(PaymentEvent.class));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testProcessInAppSubscription_WithDeactivatedPaymentDetails_Success()
		throws Exception {
		final int userId = 1;
		final String base64EncodedAppStoreReceipt = "ewoJInNpZ25hdHVyZSIgPSAiQW5nNDZNTjRLdjNiV2QrZkNTeE8xS1ZRa3p3RnpNNmh4S3FqMENKZ2xrdlBRMkRRMUpoTE5DRzhSbnZNRlFvMXVBU2RrQjAzemtob29wRnlqYm0zVXduSHBjSmE3RGJ1aVlpa0hSNFhud1V0cnBjOHZIa2JORkN4b3NyYXQ1cEZQL3RYaERpRHZHdHdESW12aWZFVDhPYkRsU1VaSnNNeFYyTkR3ajJ1TEJhZ0FBQURWekNDQTFNd2dnSTdvQU1DQVFJQ0NHVVVrVTNaV0FTMU1BMEdDU3FHU0liM0RRRUJCUVVBTUg4eEN6QUpCZ05WQkFZVEFsVlRNUk13RVFZRFZRUUtEQXBCY0hCc1pTQkpibU11TVNZd0pBWURWUVFMREIxQmNIQnNaU0JEWlhKMGFXWnBZMkYwYVc5dUlFRjFkR2h2Y21sMGVURXpNREVHQTFVRUF3d3FRWEJ3YkdVZ2FWUjFibVZ6SUZOMGIzSmxJRU5sY25ScFptbGpZWFJwYjI0Z1FYVjBhRzl5YVhSNU1CNFhEVEE1TURZeE5USXlNRFUxTmxvWERURTBNRFl4TkRJeU1EVTFObG93WkRFak1DRUdBMVVFQXd3YVVIVnlZMmhoYzJWU1pXTmxhWEIwUTJWeWRHbG1hV05oZEdVeEd6QVpCZ05WQkFzTUVrRndjR3hsSUdsVWRXNWxjeUJUZEc5eVpURVRNQkVHQTFVRUNnd0tRWEJ3YkdVZ1NXNWpMakVMTUFrR0ExVUVCaE1DVlZNd2daOHdEUVlKS29aSWh2Y05BUUVCQlFBRGdZMEFNSUdKQW9HQkFNclJqRjJjdDRJclNkaVRDaGFJMGc4cHd2L2NtSHM4cC9Sd1YvcnQvOTFYS1ZoTmw0WElCaW1LalFRTmZnSHNEczZ5anUrK0RyS0pFN3VLc3BoTWRkS1lmRkU1ckdYc0FkQkVqQndSSXhleFRldngzSExFRkdBdDFtb0t4NTA5ZGh4dGlJZERnSnYyWWFWczQ5QjB1SnZOZHk2U01xTk5MSHNETHpEUzlvWkhBZ01CQUFHamNqQndNQXdHQTFVZEV3RUIvd1FDTUFBd0h3WURWUjBqQkJnd0ZvQVVOaDNvNHAyQzBnRVl0VEpyRHRkREM1RllRem93RGdZRFZSMFBBUUgvQkFRREFnZUFNQjBHQTFVZERnUVdCQlNwZzRQeUdVakZQaEpYQ0JUTXphTittVjhrOVRBUUJnb3Foa2lHOTJOa0JnVUJCQUlGQURBTkJna3Foa2lHOXcwQkFRVUZBQU9DQVFFQUVhU2JQanRtTjRDL0lCM1FFcEszMlJ4YWNDRFhkVlhBZVZSZVM1RmFaeGMrdDg4cFFQOTNCaUF4dmRXLzNlVFNNR1k1RmJlQVlMM2V0cVA1Z204d3JGb2pYMGlreVZSU3RRKy9BUTBLRWp0cUIwN2tMczlRVWU4Y3pSOFVHZmRNMUV1bVYvVWd2RGQ0TndOWXhMUU1nNFdUUWZna1FRVnk4R1had1ZIZ2JFL1VDNlk3MDUzcEdYQms1MU5QTTN3b3hoZDNnU1JMdlhqK2xvSHNTdGNURXFlOXBCRHBtRzUrc2s0dHcrR0szR01lRU41LytlMVFUOW5wL0tsMW5qK2FCdzdDMHhzeTBiRm5hQWQxY1NTNnhkb3J5L0NVdk02Z3RLc21uT09kcVRlc2JwMGJzOHNuNldxczBDOWRnY3hSSHVPTVoydG04bnBMVW03YXJnT1N6UT09IjsKCSJwdXJjaGFzZS1pbmZvIiA9ICJld29KSW05eWFXZHBibUZzTFhCMWNtTm9ZWE5sTFdSaGRHVXRjSE4wSWlBOUlDSXlNREV6TFRBeUxURXlJREE0T2pBM09qVTRJRUZ0WlhKcFkyRXZURzl6WDBGdVoyVnNaWE1pT3dvSkluQjFjbU5vWVhObExXUmhkR1V0YlhNaUlEMGdJakV6TmpBMk9EVXlOemd5T0RVaU93b0pJblZ1YVhGMVpTMXBaR1Z1ZEdsbWFXVnlJaUE5SUNJeVl6UmtNbVprWXpJNU1HWm1NR00zWlRrMk9ESXlOREJsTkdZMU1tUXdPVEV6TldNME1qVTRJanNLQ1NKdmNtbG5hVzVoYkMxMGNtRnVjMkZqZEdsdmJpMXBaQ0lnUFNBaU1UQXdNREF3TURBMk5EYzBOek01TWlJN0Nna2laWGh3YVhKbGN5MWtZWFJsSWlBOUlDSXhNell3TmpnMU5EVTRNamcxSWpzS0NTSjBjbUZ1YzJGamRHbHZiaTFwWkNJZ1BTQWlNVEF3TURBd01EQTJORGMwTnpNNU1pSTdDZ2tpYjNKcFoybHVZV3d0Y0hWeVkyaGhjMlV0WkdGMFpTMXRjeUlnUFNBaU1UTTJNRFk0TlRJM09EWTJPU0k3Q2draWQyVmlMVzl5WkdWeUxXeHBibVV0YVhSbGJTMXBaQ0lnUFNBaU1UQXdNREF3TURBeU5qWXpORGd4T0NJN0Nna2lZblp5Y3lJZ1BTQWlNUzR3SWpzS0NTSjFibWx4ZFdVdGRtVnVaRzl5TFdsa1pXNTBhV1pwWlhJaUlEMGdJa0kzUkRWQlFqUTVMVVpHUkRjdE5ERTBSUzA1TURZNExVSTFNalEzUWtJd05VUXdOQ0k3Q2draVpYaHdhWEpsY3kxa1lYUmxMV1p2Y20xaGRIUmxaQzF3YzNRaUlEMGdJakl3TVRNdE1ESXRNVElnTURnNk1UQTZOVGdnUVcxbGNtbGpZUzlNYjNOZlFXNW5aV3hsY3lJN0Nna2lhWFJsYlMxcFpDSWdQU0FpTmpBeU5qWTFPVEUwSWpzS0NTSmxlSEJwY21WekxXUmhkR1V0Wm05eWJXRjBkR1ZrSWlBOUlDSXlNREV6TFRBeUxURXlJREUyT2pFd09qVTRJRVYwWXk5SFRWUWlPd29KSW5CeWIyUjFZM1F0YVdRaUlEMGdJbU52YlM1dGRYTnBZM0YxWW1Wa0xtOHlJanNLQ1NKd2RYSmphR0Z6WlMxa1lYUmxJaUE5SUNJeU1ERXpMVEF5TFRFeUlERTJPakEzT2pVNElFVjBZeTlIVFZRaU93b0pJbTl5YVdkcGJtRnNMWEIxY21Ob1lYTmxMV1JoZEdVaUlEMGdJakl3TVRNdE1ESXRNVElnTVRZNk1EYzZOVGdnUlhSakwwZE5WQ0k3Q2draVltbGtJaUE5SUNKamIyMHViWFZ6YVdOeGRXSmxaQzV2TWlJN0Nna2ljSFZ5WTJoaGMyVXRaR0YwWlMxd2MzUWlJRDBnSWpJd01UTXRNREl0TVRJZ01EZzZNRGM2TlRnZ1FXMWxjbWxqWVM5TWIzTmZRVzVuWld4bGN5STdDZ2tpY1hWaGJuUnBkSGtpSUQwZ0lqRWlPd3A5IjsKCSJlbnZpcm9ubWVudCIgPSAiU2FuZGJveCI7CgkicG9kIiA9ICIxMDAiOwoJInNpZ25pbmctc3RhdHVzIiA9ICIwIjsKfQ==";
		final String transactionReceipt = base64EncodedAppStoreReceipt.replaceAll("=", "\\\\u003d");
		final String originalTransactionId = "1000000064861007";
		final long expiresDate = 1360756242000L;
		final String appStoreOriginalTransactionId = "1000000064861007";
		
		UserStatus limitedUserStatus = UserStatusFactory.createUserStatus(mobi.nowtechnologies.server.shared.enums.UserStatus.LIMITED);

		Community community = CommunityFactory.createCommunity();

		UserGroup userGroup = UserGroupFactory.createUserGroup();
		userGroup.setCommunity(community);
		
		final SagePayCreditCardPaymentDetails currentPaymentDetails = new SagePayCreditCardPaymentDetails();
		currentPaymentDetails.setActivated(true);

		final User user = UserFactory.createUser();
		user.setId(userId);
		user.setCurrentPaymentDetails(currentPaymentDetails);
		user.setBase64EncodedAppStoreReceipt(null);
		user.setStatus(limitedUserStatus);
		user.setUserGroup(userGroup);
		user.setLastSuccessfulPaymentTimeMillis(0L);
		
		String body = "{\"receipt-data\":\""+transactionReceipt+"\",\"password\":\""+password+"\"}";
		
		final String appStoreProductId = "com.musicqubed.o2.autorenew.test";

        BasicResponse expectedResponse = new BasicResponse();
		expectedResponse.setStatusCode(200);
		expectedResponse.setMessage("{ \"receipt\" : { \"original_purchase_date_pst\" : \"2013-02-13 03:41:43 America/Los_Angeles\", \"unique_identifier\" : \"80d70017aae1547196bc92c02c3f83cc5f9e4cc6\", \"original_transaction_id\" : \""+originalTransactionId+"\", \"expires_date\" : \""+expiresDate+"\", \"transaction_id\" : \""+appStoreOriginalTransactionId+"\", \"quantity\" : \"1\", \"product_id\" : \""+appStoreProductId+"\", \"original_purchase_date_ms\" : \"1360755703334\", \"bid\" : \"com.musicqubed.o2\", \"web_order_line_item_id\" : \"1000000026638439\", \"bvrs\" : \"1.0\", \"expires_date_formatted\" : \"2013-02-13 11:44:42 Etc/GMT\", \"purchase_date\" : \"2013-02-13 11:41:42 Etc/GMT\", \"purchase_date_ms\" : \"1360755702795\", \"expires_date_formatted_pst\" : \"2013-02-13 03:44:42 America/Los_Angeles\", \"purchase_date_pst\" : \"2013-02-13 03:41:42 America/Los_Angeles\", \"original_purchase_date\" : \"2013-02-13 11:41:43 Etc/GMT\", \"item_id\" : \"602725828\" }, \"latest_receipt_info\" : { \"original_purchase_date_pst\" : \"2013-02-13 03:41:43 America/Los_Angeles\", \"unique_identifier\" : \"80d70017aae1547196bc92c02c3f83cc5f9e4cc6\", \"original_transaction_id\" : \""+originalTransactionId+"\", \"expires_date\" : \""+expiresDate+"\", \"transaction_id\" : \""+appStoreOriginalTransactionId+"\", \"quantity\" : \"1\", \"product_id\" : \""+appStoreProductId+"\", \"original_purchase_date_ms\" : \"1360755703000\", \"bid\" : \"com.musicqubed.o2\", \"web_order_line_item_id\" : \"1000000026638446\", \"bvrs\" : \"1.0\", \"expires_date_formatted\" : \"2013-02-13 11:50:42 Etc/GMT\", \"purchase_date\" : \"2013-02-13 11:47:42 Etc/GMT\", \"purchase_date_ms\" : \"1360756062000\", \"expires_date_formatted_pst\" : \"2013-02-13 03:50:42 America/Los_Angeles\", \"purchase_date_pst\" : \"2013-02-13 03:47:42 America/Los_Angeles\", \"original_purchase_date\" : \"2013-02-13 11:41:43 Etc/GMT\", \"item_id\" : \"602725828\" }, \"status\" : 0, \"latest_receipt\" : \""+transactionReceipt+"\" }");

		final BigDecimal paymentPolicySubCost = BigDecimal.TEN;
		final String currencyISO = "GBP";
		
		final PaymentPolicy paymentPolicy = PaymentPolicyFactory.createPaymentPolicy();
		paymentPolicy.setSubcost(paymentPolicySubCost);
		paymentPolicy.setCurrencyISO(currencyISO);
		
		final long paymentTimestamp = Long.MAX_VALUE;
		final PaymentDetailsType paymentType = PaymentDetailsType.FIRST;

		Mockito.when(mockUserService.findById(userId)).thenReturn(user);
		Mockito.when(mockPostService.sendHttpPost(Mockito.eq(iTunesUrl), (List<NameValuePair>)Mockito.isNull(), Mockito.eq(body))).thenReturn(expectedResponse);
		Mockito.when(mockPaymentPolicyService.findByCommunityAndAppStoreProductId(community, appStoreProductId)).thenReturn(paymentPolicy); 

		PowerMockito.mockStatic(Utils.class);
		PowerMockito.when(Utils.getEpochMillis()).thenReturn(paymentTimestamp);
		
		Mockito.when(mockSubmitedPaymentService.save(Mockito.any(SubmittedPayment.class))).thenAnswer(new SubmitedPaymentServiceAnswer(base64EncodedAppStoreReceipt, paymentTimestamp, currencyISO, appStoreOriginalTransactionId, paymentType, paymentPolicySubCost, user, originalTransactionId,
				expiresDate));
		
		Mockito.doAnswer(new PaymentEventAnswer(expiresDate, paymentTimestamp, user, base64EncodedAppStoreReceipt, appStoreOriginalTransactionId, currencyISO, originalTransactionId, paymentType, paymentPolicySubCost)).when(mockApplicationEventPublisher).publishEvent(Mockito.any(PaymentEvent.class));

		BasicResponse actualResponse = fixtureITunesServiceImpl.processInAppSubscription(userId, base64EncodedAppStoreReceipt);
		
		assertNull(actualResponse);
		
		Mockito.verify(mockUserService, Mockito.times(1)).findById(userId);
		Mockito.verify(mockPostService, Mockito.times(0)).sendHttpPost(Mockito.eq(iTunesUrl), (List<NameValuePair>)Mockito.isNull(), Mockito.eq(body));
		Mockito.verify(mockPaymentPolicyService, Mockito.times(0)).findByCommunityAndAppStoreProductId(community, appStoreProductId);
		Mockito.verify(mockSubmitedPaymentService, Mockito.times(0)).save(Mockito.any(SubmittedPayment.class));
		Mockito.verify(mockApplicationEventPublisher, Mockito.times(0)).publishEvent(Mockito.any(PaymentEvent.class));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testProcessInAppSubscription_WithoutPaymentDetailsWhenUserInLIMITEDAndHasBase64EncodedAppStoreReceipt_Success()
		throws Exception {
		final int userId = 1;
		final String base64EncodedAppStoreReceipt = "ewoJInNpZ25hdHVyZSIgPSAiQW5nNDZNTjRLdjNiV2QrZkNTeE8xS1ZRa3p3RnpNNmh4S3FqMENKZ2xrdlBRMkRRMUpoTE5DRzhSbnZNRlFvMXVBU2RrQjAzemtob29wRnlqYm0zVXduSHBjSmE3RGJ1aVlpa0hSNFhud1V0cnBjOHZIa2JORkN4b3NyYXQ1cEZQL3RYaERpRHZHdHdESW12aWZFVDhPYkRsU1VaSnNNeFYyTkR3ajJ1TEJhZ0FBQURWekNDQTFNd2dnSTdvQU1DQVFJQ0NHVVVrVTNaV0FTMU1BMEdDU3FHU0liM0RRRUJCUVVBTUg4eEN6QUpCZ05WQkFZVEFsVlRNUk13RVFZRFZRUUtEQXBCY0hCc1pTQkpibU11TVNZd0pBWURWUVFMREIxQmNIQnNaU0JEWlhKMGFXWnBZMkYwYVc5dUlFRjFkR2h2Y21sMGVURXpNREVHQTFVRUF3d3FRWEJ3YkdVZ2FWUjFibVZ6SUZOMGIzSmxJRU5sY25ScFptbGpZWFJwYjI0Z1FYVjBhRzl5YVhSNU1CNFhEVEE1TURZeE5USXlNRFUxTmxvWERURTBNRFl4TkRJeU1EVTFObG93WkRFak1DRUdBMVVFQXd3YVVIVnlZMmhoYzJWU1pXTmxhWEIwUTJWeWRHbG1hV05oZEdVeEd6QVpCZ05WQkFzTUVrRndjR3hsSUdsVWRXNWxjeUJUZEc5eVpURVRNQkVHQTFVRUNnd0tRWEJ3YkdVZ1NXNWpMakVMTUFrR0ExVUVCaE1DVlZNd2daOHdEUVlKS29aSWh2Y05BUUVCQlFBRGdZMEFNSUdKQW9HQkFNclJqRjJjdDRJclNkaVRDaGFJMGc4cHd2L2NtSHM4cC9Sd1YvcnQvOTFYS1ZoTmw0WElCaW1LalFRTmZnSHNEczZ5anUrK0RyS0pFN3VLc3BoTWRkS1lmRkU1ckdYc0FkQkVqQndSSXhleFRldngzSExFRkdBdDFtb0t4NTA5ZGh4dGlJZERnSnYyWWFWczQ5QjB1SnZOZHk2U01xTk5MSHNETHpEUzlvWkhBZ01CQUFHamNqQndNQXdHQTFVZEV3RUIvd1FDTUFBd0h3WURWUjBqQkJnd0ZvQVVOaDNvNHAyQzBnRVl0VEpyRHRkREM1RllRem93RGdZRFZSMFBBUUgvQkFRREFnZUFNQjBHQTFVZERnUVdCQlNwZzRQeUdVakZQaEpYQ0JUTXphTittVjhrOVRBUUJnb3Foa2lHOTJOa0JnVUJCQUlGQURBTkJna3Foa2lHOXcwQkFRVUZBQU9DQVFFQUVhU2JQanRtTjRDL0lCM1FFcEszMlJ4YWNDRFhkVlhBZVZSZVM1RmFaeGMrdDg4cFFQOTNCaUF4dmRXLzNlVFNNR1k1RmJlQVlMM2V0cVA1Z204d3JGb2pYMGlreVZSU3RRKy9BUTBLRWp0cUIwN2tMczlRVWU4Y3pSOFVHZmRNMUV1bVYvVWd2RGQ0TndOWXhMUU1nNFdUUWZna1FRVnk4R1had1ZIZ2JFL1VDNlk3MDUzcEdYQms1MU5QTTN3b3hoZDNnU1JMdlhqK2xvSHNTdGNURXFlOXBCRHBtRzUrc2s0dHcrR0szR01lRU41LytlMVFUOW5wL0tsMW5qK2FCdzdDMHhzeTBiRm5hQWQxY1NTNnhkb3J5L0NVdk02Z3RLc21uT09kcVRlc2JwMGJzOHNuNldxczBDOWRnY3hSSHVPTVoydG04bnBMVW03YXJnT1N6UT09IjsKCSJwdXJjaGFzZS1pbmZvIiA9ICJld29KSW05eWFXZHBibUZzTFhCMWNtTm9ZWE5sTFdSaGRHVXRjSE4wSWlBOUlDSXlNREV6TFRBeUxURXlJREE0T2pBM09qVTRJRUZ0WlhKcFkyRXZURzl6WDBGdVoyVnNaWE1pT3dvSkluQjFjbU5vWVhObExXUmhkR1V0YlhNaUlEMGdJakV6TmpBMk9EVXlOemd5T0RVaU93b0pJblZ1YVhGMVpTMXBaR1Z1ZEdsbWFXVnlJaUE5SUNJeVl6UmtNbVprWXpJNU1HWm1NR00zWlRrMk9ESXlOREJsTkdZMU1tUXdPVEV6TldNME1qVTRJanNLQ1NKdmNtbG5hVzVoYkMxMGNtRnVjMkZqZEdsdmJpMXBaQ0lnUFNBaU1UQXdNREF3TURBMk5EYzBOek01TWlJN0Nna2laWGh3YVhKbGN5MWtZWFJsSWlBOUlDSXhNell3TmpnMU5EVTRNamcxSWpzS0NTSjBjbUZ1YzJGamRHbHZiaTFwWkNJZ1BTQWlNVEF3TURBd01EQTJORGMwTnpNNU1pSTdDZ2tpYjNKcFoybHVZV3d0Y0hWeVkyaGhjMlV0WkdGMFpTMXRjeUlnUFNBaU1UTTJNRFk0TlRJM09EWTJPU0k3Q2draWQyVmlMVzl5WkdWeUxXeHBibVV0YVhSbGJTMXBaQ0lnUFNBaU1UQXdNREF3TURBeU5qWXpORGd4T0NJN0Nna2lZblp5Y3lJZ1BTQWlNUzR3SWpzS0NTSjFibWx4ZFdVdGRtVnVaRzl5TFdsa1pXNTBhV1pwWlhJaUlEMGdJa0kzUkRWQlFqUTVMVVpHUkRjdE5ERTBSUzA1TURZNExVSTFNalEzUWtJd05VUXdOQ0k3Q2draVpYaHdhWEpsY3kxa1lYUmxMV1p2Y20xaGRIUmxaQzF3YzNRaUlEMGdJakl3TVRNdE1ESXRNVElnTURnNk1UQTZOVGdnUVcxbGNtbGpZUzlNYjNOZlFXNW5aV3hsY3lJN0Nna2lhWFJsYlMxcFpDSWdQU0FpTmpBeU5qWTFPVEUwSWpzS0NTSmxlSEJwY21WekxXUmhkR1V0Wm05eWJXRjBkR1ZrSWlBOUlDSXlNREV6TFRBeUxURXlJREUyT2pFd09qVTRJRVYwWXk5SFRWUWlPd29KSW5CeWIyUjFZM1F0YVdRaUlEMGdJbU52YlM1dGRYTnBZM0YxWW1Wa0xtOHlJanNLQ1NKd2RYSmphR0Z6WlMxa1lYUmxJaUE5SUNJeU1ERXpMVEF5TFRFeUlERTJPakEzT2pVNElFVjBZeTlIVFZRaU93b0pJbTl5YVdkcGJtRnNMWEIxY21Ob1lYTmxMV1JoZEdVaUlEMGdJakl3TVRNdE1ESXRNVElnTVRZNk1EYzZOVGdnUlhSakwwZE5WQ0k3Q2draVltbGtJaUE5SUNKamIyMHViWFZ6YVdOeGRXSmxaQzV2TWlJN0Nna2ljSFZ5WTJoaGMyVXRaR0YwWlMxd2MzUWlJRDBnSWpJd01UTXRNREl0TVRJZ01EZzZNRGM2TlRnZ1FXMWxjbWxqWVM5TWIzTmZRVzVuWld4bGN5STdDZ2tpY1hWaGJuUnBkSGtpSUQwZ0lqRWlPd3A5IjsKCSJlbnZpcm9ubWVudCIgPSAiU2FuZGJveCI7CgkicG9kIiA9ICIxMDAiOwoJInNpZ25pbmctc3RhdHVzIiA9ICIwIjsKfQ==";
		final String transactionReceipt = base64EncodedAppStoreReceipt.replaceAll("=", "\\\\u003d");
		final String originalTransactionId = "1000000064861007";
		final long expiresDate = 1360756242000L;
		final String appStoreOriginalTransactionId = "1000000064861007";
		
		UserStatus limitedUserStatus = UserStatusFactory.createUserStatus(mobi.nowtechnologies.server.shared.enums.UserStatus.LIMITED);

		Community community = CommunityFactory.createCommunity();

		UserGroup userGroup = UserGroupFactory.createUserGroup();
		userGroup.setCommunity(community);
		
		final User user = UserFactory.createUser();
		user.setId(userId);
		user.setCurrentPaymentDetails(null);
		user.setBase64EncodedAppStoreReceipt(base64EncodedAppStoreReceipt);
		user.setStatus(limitedUserStatus);
		user.setUserGroup(userGroup);
		user.setLastSuccessfulPaymentTimeMillis(0L);
		
		String body = "{\"receipt-data\":\""+transactionReceipt+"\",\"password\":\""+password+"\"}";
		
		final String appStoreProductId = "com.musicqubed.o2.autorenew.test";

        BasicResponse expectedResponse = new BasicResponse();
		expectedResponse.setStatusCode(200);
		expectedResponse.setMessage("{ \"receipt\" : { \"original_purchase_date_pst\" : \"2013-02-13 03:41:43 America/Los_Angeles\", \"unique_identifier\" : \"80d70017aae1547196bc92c02c3f83cc5f9e4cc6\", \"original_transaction_id\" : \""+originalTransactionId+"\", \"expires_date\" : \""+expiresDate+"\", \"transaction_id\" : \""+appStoreOriginalTransactionId+"\", \"quantity\" : \"1\", \"product_id\" : \""+appStoreProductId+"\", \"original_purchase_date_ms\" : \"1360755703334\", \"bid\" : \"com.musicqubed.o2\", \"web_order_line_item_id\" : \"1000000026638439\", \"bvrs\" : \"1.0\", \"expires_date_formatted\" : \"2013-02-13 11:44:42 Etc/GMT\", \"purchase_date\" : \"2013-02-13 11:41:42 Etc/GMT\", \"purchase_date_ms\" : \"1360755702795\", \"expires_date_formatted_pst\" : \"2013-02-13 03:44:42 America/Los_Angeles\", \"purchase_date_pst\" : \"2013-02-13 03:41:42 America/Los_Angeles\", \"original_purchase_date\" : \"2013-02-13 11:41:43 Etc/GMT\", \"item_id\" : \"602725828\" }, \"latest_receipt_info\" : { \"original_purchase_date_pst\" : \"2013-02-13 03:41:43 America/Los_Angeles\", \"unique_identifier\" : \"80d70017aae1547196bc92c02c3f83cc5f9e4cc6\", \"original_transaction_id\" : \""+originalTransactionId+"\", \"expires_date\" : \""+expiresDate+"\", \"transaction_id\" : \""+appStoreOriginalTransactionId+"\", \"quantity\" : \"1\", \"product_id\" : \""+appStoreProductId+"\", \"original_purchase_date_ms\" : \"1360755703000\", \"bid\" : \"com.musicqubed.o2\", \"web_order_line_item_id\" : \"1000000026638446\", \"bvrs\" : \"1.0\", \"expires_date_formatted\" : \"2013-02-13 11:50:42 Etc/GMT\", \"purchase_date\" : \"2013-02-13 11:47:42 Etc/GMT\", \"purchase_date_ms\" : \"1360756062000\", \"expires_date_formatted_pst\" : \"2013-02-13 03:50:42 America/Los_Angeles\", \"purchase_date_pst\" : \"2013-02-13 03:47:42 America/Los_Angeles\", \"original_purchase_date\" : \"2013-02-13 11:41:43 Etc/GMT\", \"item_id\" : \"602725828\" }, \"status\" : 0, \"latest_receipt\" : \""+transactionReceipt+"\" }");

		final BigDecimal paymentPolicySubCost = BigDecimal.TEN;
		final String currencyISO = "GBP";
		
		final PaymentPolicy paymentPolicy = PaymentPolicyFactory.createPaymentPolicy();
		paymentPolicy.setSubcost(paymentPolicySubCost);
		paymentPolicy.setCurrencyISO(currencyISO);
		
		final long paymentTimestamp = Long.MAX_VALUE;
		final PaymentDetailsType paymentType = PaymentDetailsType.FIRST;

		PowerMockito.mockStatic(UserStatusDao.class);
		PowerMockito.when(UserStatusDao.getLimitedUserStatus()).thenReturn(limitedUserStatus);
		Mockito.when(mockUserService.findById(userId)).thenReturn(user);
		Mockito.when(mockPostService.sendHttpPost(Mockito.eq(iTunesUrl), (List<NameValuePair>)Mockito.isNull(), Mockito.eq(body))).thenReturn(expectedResponse);
		Mockito.when(mockPaymentPolicyService.findByCommunityAndAppStoreProductId(community, appStoreProductId)).thenReturn(paymentPolicy); 

		PowerMockito.mockStatic(Utils.class);
		PowerMockito.when(Utils.getEpochMillis()).thenReturn(paymentTimestamp);
		
		Mockito.when(mockSubmitedPaymentService.save(Mockito.any(SubmittedPayment.class))).thenAnswer(new SubmitedPaymentServiceAnswer(base64EncodedAppStoreReceipt, paymentTimestamp, currencyISO, appStoreOriginalTransactionId, paymentType, paymentPolicySubCost, user, originalTransactionId,
				expiresDate));
		
		Mockito.doAnswer(new PaymentEventAnswer(expiresDate, paymentTimestamp, user, base64EncodedAppStoreReceipt, appStoreOriginalTransactionId, currencyISO, originalTransactionId, paymentType, paymentPolicySubCost)).when(mockApplicationEventPublisher).publishEvent(Mockito.any(PaymentEvent.class));

        BasicResponse actualResponse = fixtureITunesServiceImpl.processInAppSubscription(userId, base64EncodedAppStoreReceipt);
		
		assertNotNull(actualResponse);
		assertEquals(expectedResponse, actualResponse);
		
		Mockito.verify(mockUserService, Mockito.times(1)).findById(userId);
		Mockito.verify(mockPostService, Mockito.times(1)).sendHttpPost(Mockito.eq(iTunesUrl), (List<NameValuePair>)Mockito.isNull(), Mockito.eq(body));
		Mockito.verify(mockPaymentPolicyService, Mockito.times(1)).findByCommunityAndAppStoreProductId(community, appStoreProductId);
		Mockito.verify(mockSubmitedPaymentService, Mockito.times(1)).save(Mockito.any(SubmittedPayment.class));
		Mockito.verify(mockApplicationEventPublisher, Mockito.times(1)).publishEvent(Mockito.any(PaymentEvent.class));
	}
	
	private static void validatePayment(final String base64EncodedAppStoreReceipt, final String originalTransactionId, final long expiresDate, final String appStoreOriginalTransactionId, final User user,
			final BigDecimal paymentPolicySubCost, final String currencyISO, final long paymentTimestamp, final PaymentDetailsType paymentType, SubmittedPayment passedSubmittedPayment) {
	
		assertEquals(PaymentDetailsStatus.SUCCESSFUL, passedSubmittedPayment.getStatus());
		assertEquals(user, passedSubmittedPayment.getUser());
		assertEquals(paymentTimestamp, passedSubmittedPayment.getTimestamp());
		assertEquals(paymentPolicySubCost, passedSubmittedPayment.getAmount());
		assertEquals(originalTransactionId, passedSubmittedPayment.getExternalTxId());
		assertEquals(paymentType, passedSubmittedPayment.getType());
		assertEquals(currencyISO, passedSubmittedPayment.getCurrencyISO());
		assertEquals((int)(expiresDate/1000), passedSubmittedPayment.getNextSubPayment());
		assertEquals(appStoreOriginalTransactionId, passedSubmittedPayment.getAppStoreOriginalTransactionId());
		assertEquals(PaymentDetails.ITUNES_SUBSCRIPTION, passedSubmittedPayment.getPaymentSystem());
		assertEquals(base64EncodedAppStoreReceipt, passedSubmittedPayment.getBase64EncodedAppStoreReceipt());
	}

	@Before
	public void setUp()
		throws Exception {
		fixtureITunesServiceImpl = new ITunesServiceImpl();
		
		mockUserService = Mockito.mock(UserService.class);
		mockApplicationEventPublisher = Mockito.mock(ApplicationEventPublisher.class);
		mockPaymentPolicyService = Mockito.mock(PaymentPolicyService.class);
		mockPostService = Mockito.mock(PostService.class);
		mockSubmitedPaymentService = Mockito.mock(SubmitedPaymentService.class);
        communityResourceBundleMessageSourceMock = Mockito.mock(CommunityResourceBundleMessageSource.class);
		
		fixtureITunesServiceImpl.setUserService(mockUserService);
		fixtureITunesServiceImpl.setApplicationEventPublisher(mockApplicationEventPublisher);
		fixtureITunesServiceImpl.setPaymentPolicyService(mockPaymentPolicyService);
		fixtureITunesServiceImpl.setPostService(mockPostService);
		fixtureITunesServiceImpl.setSubmitedPaymentService(mockSubmitedPaymentService);
        fixtureITunesServiceImpl.setCommunityResourceBundleMessageSource(communityResourceBundleMessageSourceMock);

		Mockito.doReturn(iTunesUrl).when(communityResourceBundleMessageSourceMock).getMessage("nowtop40", "apple.inApp.iTunesUrl", null, null);
        Mockito.doReturn(password).when(communityResourceBundleMessageSourceMock).getMessage("nowtop40", "apple.inApp.password", null, null);
	}
}